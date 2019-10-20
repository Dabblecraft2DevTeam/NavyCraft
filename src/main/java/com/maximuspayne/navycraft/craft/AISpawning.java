package com.maximuspayne.navycraft.craft;

import com.maximuspayne.navycraft.ConfigManager;
import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;


public class AISpawning extends BukkitRunnable {

    public String schemName = "";
    public AISpawning(String schematicName) {
        schemName = schematicName.toUpperCase();
    }
    @Override
    public void run() {
        HashMap<String, Location> targetLocations = null;
        if (ConfigManager.routeData.getConfigurationSection("Spawning") != null) {
            targetLocations = new HashMap<>();
            List<String> list = new ArrayList<String>(ConfigManager.routeData.getConfigurationSection("Spawning").getKeys(false));
            for (String schems : list) {
                String i = schems.toUpperCase();
                if (!i.equalsIgnoreCase("schematicName")) {
                    World world = NavyCraft.instance.getServer().getWorld(ConfigManager.routeData.getString("Spawning." + i + ".world"));
                    int tx = ConfigManager.routeData.getInt("Spawning." + i + ".x");
                    int ty = ConfigManager.routeData.getInt("Spawning." + i + ".y");
                    int tz = ConfigManager.routeData.getInt("Spawning." + i + ".z");
                    Location targetLocation = new Location(world, tx, ty, tz);
                    targetLocations.put(i, targetLocation);
                }
            }
        }
            Utils.pasteSchem(schemName, targetLocations.get(schemName));
            int tx = ConfigManager.routeData.getInt("Spawning." + schemName + ".signx");
            int ty = ConfigManager.routeData.getInt("Spawning." + schemName + ".signy");
            int tz = ConfigManager.routeData.getInt("Spawning." + schemName + ".signz");
            Location signLocation = new Location(targetLocations.get(schemName).getWorld(), tx, ty, tz);
            Block signBlock = signLocation.getBlock();
            if (signBlock != null && (signBlock.getType() == Material.WALL_SIGN || signBlock.getType() == Material.SIGN || signBlock.getType() == Material.SIGN_POST)) {
                Sign sign = (Sign) signBlock.getState();
                float dr = 0;

                switch (signBlock.getData()) {
                    case (byte) 0x2:// n
                        dr = 180;
                        break;
                    case (byte) 0x3:// s
                        dr = 0;
                        break;
                    case (byte) 0x4:// w
                        dr = 90;
                        break;
                    case (byte) 0x5:// e
                        dr = 270;
                        break;
                }
                int direction = signBlock.getData();

                // get the block the sign is attached to
                tx = tx + (direction == 4 ? 1 : (direction == 5 ? -1 : 0));
                tz = tz + (direction == 2 ? 1 : (direction == 3 ? -1 : 0));

                CraftType craftType = CraftType.getCraftType(sign.getLine(0));
                String name = sign.getLine(1);

                if (name != null && craftType != null) {
                    Craft theCraft = NavyCraft.instance.createCraft(null, craftType, tx, ty, tz, name, dr, signBlock);
                    new Thread() {
                        @Override
                        public void run()
                        {

                            setPriority(Thread.MIN_PRIORITY);
                            try
                            {
                                int sleep = (int) Math.round(Math.random() * (60000 - 30000)) + 30000;
                                sleep(sleep);
                                NavyCraft.instance.getServer().broadcastMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "****" + ChatColor.BOLD
                                        + "" + ChatColor.AQUA + " Merchant Announcement " + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "****");
                                NavyCraft.instance.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "ATTENTION!" + ChatColor.YELLOW + " A Merchant Ship has been spotted!");
                                NavyCraft.instance.getServer().broadcastMessage(ChatColor.YELLOW + "Rumors say this vessel is traveling the " + ChatColor.GOLD + name + ChatColor.YELLOW +  " route!");
                            } catch (InterruptedException | ConcurrentModificationException e)
                            {
                            }
                        }
                    }.start();
                }
        }
    }
}