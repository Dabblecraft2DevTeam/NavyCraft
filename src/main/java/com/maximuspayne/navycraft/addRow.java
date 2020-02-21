package com.maximuspayne.navycraft;

import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.maximuspayne.shipyard.PlotType;
import com.maximuspayne.shipyard.Shipyard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("deprecation")
public class addRow extends BukkitRunnable
{
    private Player p;
    private Block block;
    private static WorldGuardPlugin wgp;
    private NavyCraft plugin;
    public static Block currentBlock;

    public addRow(Player p1, Block b)
    {
        p = p1;
        block = b;
        plugin = NavyCraft.instance;
    }

    @Override
    public void run()
    {
            BlockFace bf;
            bf = null;
            Sign sign = (Sign) block.getState();
            // bf2 = null;
            switch (block.getData()) {
                case (byte) 0x8:// n
                    bf = BlockFace.SOUTH;
                    // bf2 = BlockFace.NORTH;
                    break;
                case (byte) 0x0:// s
                    bf = BlockFace.NORTH;
                    // bf2 = BlockFace.SOUTH;
                    break;
                case (byte) 0x4:// w
                    bf = BlockFace.EAST;
                    // bf2 = BlockFace.WEST;
                    break;
                case (byte) 0xC:// e
                    bf = BlockFace.WEST;
                    // bf2 = BlockFace.EAST;
                    break;
                default:
                    break;
            }

            if (bf == null) {
                p.sendMessage(ChatColor.DARK_RED + "Sign Error: Check Direction?");
                return;
            }

            if (block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getTypeId() == 68) {
                Sign sign2 = (Sign) block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getState();
                String lotStr = sign2.getLine(3).trim().toLowerCase();
                lotStr = lotStr.replaceAll(ChatColor.BLUE.toString(), "");



                NavyCraft_FileListener.loadSignData();

                if (sign.getLine(0).equalsIgnoreCase("*select*")) {
                String UUID = Utils.getUUIDfromPlayer(sign.getLine(1));
                OfflinePlayer player = NavyCraft.instance.getServer().getOfflinePlayer(UUID);
                Location loc = null;
                int sizeX = 0, sizeY = 0, sizeZ = 0, originX = 0, originY = 0, originZ = 0;
                String name = null;
                for (PlotType pt : Shipyard.getPlots()) {
                    if (pt.name.equalsIgnoreCase(lotStr)) {
                        name = pt.name;
                        sizeX = pt.sizeX;
                        sizeY = pt.sizeY;
                        sizeZ = pt.sizeZ;
                        originX = pt.originX;
                        originY = pt.originY;
                        originZ = pt.originZ;
                        loc = block.getRelative(bf, pt.bfr).getLocation();
                    }
                }

                if (name == null || sizeX == 0 || sizeY == 0 || sizeZ == 0 || loc == null) {
                    NavyCraft.instance.DebugMessage(name, 3);
                    NavyCraft.instance.DebugMessage(String.valueOf(sizeX), 3);
                    NavyCraft.instance.DebugMessage(String.valueOf(sizeY), 3);
                    NavyCraft.instance.DebugMessage(String.valueOf(sizeZ), 3);
                    NavyCraft.instance.DebugMessage(loc.toString(), 3);
                    p.sendMessage(ChatColor.DARK_RED + "Sign Error: Invalid Lot");
                    return;
                }

                originX = loc.getBlockX() + originX;
                originY = loc.getBlockY() + originY;
                originZ = loc.getBlockZ() + originZ;


                    wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
                    if (wgp != null) {
                        RegionManager regionManager = wgp.getRegionManager(loc.getWorld());

                        // ApplicableRegionSet set = regionManager.getApplicableRegions(loc);

                        sign.setLine(0, "*Select*");
                        sign.update();

                        sign2.setLine(0, "Private");
                        sign2.setLine(1, "1");
                        sign2.setLine(2, "" + (NavyCraft_BlockListener.maxId(UUID) + 1));
                        sign2.setLine(3, lotStr.toUpperCase());
                        sign2.update();

                        System.out.println(ChatColor.GREEN + "SIGN AT " + sign.getX() + " " + sign.getY() + " " + sign.getZ() + " ADDED WITH OWNER " + player.getName());

                        int x = sign.getX();
                        int y = sign.getY();
                        int z = sign.getZ();
                        org.bukkit.World world = sign.getWorld();
                        NavyCraft_FileListener.updateSign(UUID, lotStr, x, y, z, world, NavyCraft_BlockListener.maxId(UUID) + 1, true);
                        String regionName = "--" + UUID + "-" + NavyCraft_FileListener.getSign(x, y, z, world);

                        regionManager.addRegion(new com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion(regionName, new com.sk89q.worldedit.BlockVector(originX, originY, originZ), new com.sk89q.worldedit.BlockVector((originX + sizeX) - 1, (originY + sizeY) - 1, (originZ + sizeZ) - 1)));
                        DefaultDomain owners = new DefaultDomain();
                        owners.addPlayer(java.util.UUID.fromString(UUID));
                        regionManager.getRegion(regionName).setOwners(owners);

                        try {
                            regionManager.save();
                        } catch (StorageException e) {
                            e.printStackTrace();
                        }
                    } else {
                        p.sendMessage("World Guard error");
                    }
                    NavyCraft_FileListener.loadSignData();
                    int LX = ConfigManager.getsyConfig().getInt("Types." + lotStr.toUpperCase() + ".LX");
                    int LZ = ConfigManager.getsyConfig().getInt("Types." + lotStr.toUpperCase() + ".LZ");
                    currentBlock = new Location(currentBlock.getWorld(), currentBlock.getX() + LX, currentBlock.getY(), currentBlock.getZ() + LZ).getBlock();
                } else {
                    int x = sign.getX();
                    int y = sign.getY();
                    int z = sign.getZ();
                    org.bukkit.World world = sign.getWorld();
                    NavyCraft_FileListener.checkSign(x, y, z, world);
                    NavyCraft_FileListener.loadSignData();
                    int LX = ConfigManager.getsyConfig().getInt("Types." + lotStr.toUpperCase() + ".LX");
                    int LZ = ConfigManager.getsyConfig().getInt("Types." + lotStr.toUpperCase() + ".LZ");
                    currentBlock = new Location(currentBlock.getWorld(), currentBlock.getX() + LX, currentBlock.getY(), currentBlock.getZ() + LZ).getBlock();

                }

            } else {
                p.sendMessage(ChatColor.DARK_RED + "Sign error: Check Second Sign?");
                return;
            }

    }

    public static void addRow(Player player, final Block b)
    {
        new Thread() {
            @Override
            public void run()
            {

                setPriority(Thread.MIN_PRIORITY);
                try
                {
                    currentBlock = b;
                    while (currentBlock.getType() == Material.SIGN_POST) {
                        NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, new addRow(player, currentBlock));
                        sleep(1000);
                    }

                } catch (InterruptedException e)
                {
                }
            }
        }.start();
    }
}