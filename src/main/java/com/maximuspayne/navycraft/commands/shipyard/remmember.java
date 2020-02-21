package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;

public class remmember {

	public static WorldGuardPlugin wgp;
	
	public static void call(Player player, String[] split) {
		if (split.length == 4) {
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
						int x = foundSign.getX();
						int y = foundSign.getY();
						int z = foundSign.getZ();
						World world = foundSign.getWorld();
						String regionName = "--" + player.getUniqueId() + "-" +  NavyCraft_FileListener.getSign(x, y, z, world);

						String playerInName = split[3];

						if (!regionManager.getRegion(regionName).getMembers().contains(Utils.getUUIDfromPlayer(playerInName))) {
							player.sendMessage(ChatColor.RED + "Member not found.");
							return;
						}

						regionManager.getRegion(regionName).getMembers().removePlayer(playerInName);
						
						try {
							regionManager.save();
						} catch (StorageException e) {
							e.printStackTrace();
						}

						player.sendMessage(ChatColor.GREEN + "Player removed.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
				}

			} else {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
			}
		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard remmember <id> <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "removes player permission to that plot");
		}
	}

}
