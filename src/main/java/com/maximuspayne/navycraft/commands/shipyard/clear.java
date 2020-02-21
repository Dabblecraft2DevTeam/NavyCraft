package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class clear {
	
	public static WorldGuardPlugin wgp;
	
	public static void call(Player player, String[] split) {
		if (split.length == 3) {
			int tpId = -1;
			try {
				tpId = Integer.parseInt(split[2]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
				return;
			}

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
						int x1 = foundSign.getX();
						int y1 = foundSign.getY();
						int z1 = foundSign.getZ();
						World world = foundSign.getWorld();
						String regionName = "--" + player.getUniqueId() + "-" +  NavyCraft_FileListener.getSign(x1, y1, z1, world);

						int startX = regionManager.getRegion(regionName).getMinimumPoint().getBlockX();
						int endX = regionManager.getRegion(regionName).getMaximumPoint().getBlockX();
						int startZ = regionManager.getRegion(regionName).getMinimumPoint().getBlockZ();
						int endZ = regionManager.getRegion(regionName).getMaximumPoint().getBlockZ();
						int startY = regionManager.getRegion(regionName).getMinimumPoint().getBlockY();
						int endY = regionManager.getRegion(regionName).getMaximumPoint().getBlockY();

						for (int x = startX; x <= endX; x++) {
							for (int z = startZ; z <= endZ; z++) {
								for (int y = startY; y <= endY; y++) {
									foundSign.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
								}
							}
						}

						player.sendMessage(ChatColor.GREEN + "Plot Cleared.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
				}

			} else {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
			}
		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard clear <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "destroys all blocks within the plot" );
		}
	}

}
