package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class load {

	public static WorldGuardPlugin wgp;
	
	public static void call(Player player, String[] split) {
		if (split.length == 5) {
			int tpId = -1;
			try {
				tpId = Integer.parseInt(split[4]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
				return;
			}

			String nameString = split[3];
			
			if (tpId > -1) {
				NavyCraft_FileListener.loadSignData();
				NavyCraft_BlockListener.loadRewards(player.getName());

				Sign foundSign = null;
				foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

				if (foundSign != null) {
					wgp = (WorldGuardPlugin) NavyCraft.instance.getServer().getPluginManager()
							.getPlugin("WorldGuard");
					if (wgp != null) {
						RegionManager regionManager = wgp.getRegionManager(foundSign.getWorld());
						int x = foundSign.getX();
						int y = foundSign.getY();
						int z = foundSign.getZ();
						World world = foundSign.getWorld();
						String regionName = "--" + player.getUniqueId() + "-" +  NavyCraft_FileListener.getSign(x, y, z, world);

						ProtectedRegion region = regionManager.getRegion(regionName);
						
						Sign sign2 = (Sign) foundSign.getBlock().getRelative(BlockFace.DOWN, 1).getRelative(Utils.getBlockFace(foundSign.getBlock()), -1).getState();
						
						String name = player.getName() + "-" +  sign2.getLine(3).trim().toUpperCase() + "-" + nameString;
						Location loc = new Location(world, region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
						if (Utils.pasteSchem(name, loc)) {
						player.sendMessage(ChatColor.GREEN + "Plot loaded " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "ID: " + ChatColor.GOLD + tpId + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "Name: "+ ChatColor.GOLD + name + ".schematic" + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + ".");
						} else {
							player.sendMessage(ChatColor.RED + "Plot name doesn't exist in database!");
						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
				}

			} else {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
			}
		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard schem load <name> <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "saves your plot in a schematic" );
		}
	}

}
