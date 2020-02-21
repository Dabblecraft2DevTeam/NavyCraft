package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;

@SuppressWarnings("deprecation")
public class unclaim {

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
					Block foundBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(),
							foundSign.getY() - 1, foundSign.getZ() + 1);
					if (foundBlock2.getTypeId() != 68) {
						foundBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1,
								foundSign.getY() - 1, foundSign.getZ());
					}
					if (foundBlock2.getTypeId() == 68) {
						Sign foundSign2 = (Sign) foundBlock2.getState();
						
						wgp = (WorldGuardPlugin) NavyCraft.instance.getServer().getPluginManager()
								.getPlugin("WorldGuard");
						if (wgp != null) {
							RegionManager regionManager = wgp.getRegionManager(foundSign.getWorld());
							int x1 = foundSign.getX();
							int y1 = foundSign.getY();
							int z1 = foundSign.getZ();
							World world = foundSign.getWorld();
							String regionName = "--" + player.getUniqueId() + "-" +  NavyCraft_FileListener.getSign(x1, y1, z1, world);
							int startX = 0;
							int endX = 0;
							int startZ = 0;
							int endZ = 0;
							int startY = 0;
							int endY = 0;
						try {
							startX = regionManager.getRegion(regionName).getMinimumPoint().getBlockX();
							endX = regionManager.getRegion(regionName).getMaximumPoint().getBlockX();
							startZ = regionManager.getRegion(regionName).getMinimumPoint().getBlockZ();
							endZ = regionManager.getRegion(regionName).getMaximumPoint().getBlockZ();
							startY = regionManager.getRegion(regionName).getMinimumPoint().getBlockY();
							endY = regionManager.getRegion(regionName).getMaximumPoint().getBlockY();
							} catch(Exception e) {
							player.sendMessage(ChatColor.DARK_RED + "Your plots region is defined incorrectly, contact an admin!");
							return;
							}
							
							for (int x = startX; x <= endX; x++) {
								for (int z = startZ; z <= endZ; z++) {
									for (int y = startY; y <= endY; y++) {
										foundSign.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
									}
								}
							}
							regionManager.removeRegion(regionName);
							NavyCraft_FileListener.updateSign(null, foundSign2.getLine(3), foundSign.getX(), foundSign.getY(),foundSign.getZ(), foundSign.getWorld(), null, false);
							foundSign.setLine(0, "*Claim*");
							foundSign.setLine(1, "");
							foundSign.setLine(2, "");
							foundSign.setLine(3, "");
							foundSign.update();
							foundSign2.setLine(0, "Open");
							foundSign2.setLine(1, "1");
							foundSign2.setLine(2, "0");
							foundSign2.setLine(3, foundSign2.getLine(3).toUpperCase());
							foundSign2.update();
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());
							try {
								regionManager.save();
							} catch (StorageException e) {
								e.printStackTrace();
							}
							player.sendMessage(ChatColor.GREEN + "Plot Unclaimed.");
						}
					} else {
						player.sendMessage(
								ChatColor.RED + "Error: There may be a problem with your plot signs.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW
							+ "/shipyard list" + ChatColor.RED + " to see IDs");
				}
				
			} else {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
			}
		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard unclaim <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "destroys all blocks within the plot and unclaims it");
		}
	}

}
