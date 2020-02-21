package com.maximuspayne.navycraft.commands.shipyard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.ConfigManager;
import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

@SuppressWarnings("deprecation")
public class saveall {
	
	public static WorldGuardPlugin wgp;

	public static void call(Player player, String[] split) {
		if (!PermissionInterface.CheckPerm(player, "navycraft.saveall") && !player.isOp()) {
			player.sendMessage(ChatColor.RED + "You do not have permission to save all plots.");
			return;
		}
		new Thread(){
			
			@Override
				public void run() {
			NavyCraft_FileListener.loadSignData();
			NavyCraft_BlockListener.loadRewards(player.getName());
			List<String> list = new ArrayList<String>(ConfigManager.syData.getConfigurationSection("Signs").getKeys(false));
			int size = list.size();
			NavyCraft.playerSigns.clear();
			if (size == 0) return;
			for (String num : list) {
				if (ConfigManager.syData.getString("Signs." + num + ".isClaimed").equalsIgnoreCase("true")) {
					String type = ConfigManager.syData.getString("Signs." + num + ".type");
					int x = ConfigManager.syData.getInt("Signs." + num + ".x");
					int y = ConfigManager.syData.getInt("Signs." + num + ".y");
					int z = ConfigManager.syData.getInt("Signs." + num + ".z");
					World world = NavyCraft.instance.getServer().getWorld(ConfigManager.syData.getString("Signs." + num + "." + "world"));
					String nameString = String.valueOf(ConfigManager.syData.getInt("Signs." + num + ".id"));
					wgp = (WorldGuardPlugin) NavyCraft.instance.getServer().getPluginManager().getPlugin("WorldGuard");
					Block selectSignBlock = world.getBlockAt(x, y, z);
					if (selectSignBlock.getTypeId() == 63) {
					Sign selectSign = (Sign) selectSignBlock.getState();
				if (wgp != null) {
					try {
					RegionManager regionManager = wgp.getRegionManager(world);
					String regionName = "--" + Utils.getUUIDfromPlayer(selectSign.getLine(1)) + "-" + num;
					ProtectedRegion region = regionManager.getRegion(regionName);
					
					String name = selectSign.getLine(1) + "-" +  type;

					player.sendMessage(ChatColor.GREEN + "Plot Saved as " + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD  + name + "-" + nameString + ".schematic" + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + ".");
					Utils.saveSchem(name, nameString, region, world);
					} catch (NullPointerException e) {
						System.out.println("Couldnt Save");
						continue;
					}
				}
			}
		}
	}
}
}.start(); //, 20L);
	System.out.println("Saving Finished.");
	}

}
