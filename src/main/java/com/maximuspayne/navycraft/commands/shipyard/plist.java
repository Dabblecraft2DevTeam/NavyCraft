package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.maximuspayne.shipyard.Plot;

public class plist {

	public static void call(Player player, String[] split) {
		if (split.length == 3) {
			String p = split[2];
			NavyCraft_FileListener.loadSignData();
			NavyCraft_BlockListener.loadRewards(p);
			String UUID = Utils.getUUIDfromPlayer(p);
			if (UUID != null) {
			player.sendMessage(ChatColor.AQUA + p + "'s" + " Shipyard Plots:");
			player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "ID" + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TYPE");
			
			if (NavyCraft.playerSigns.containsKey(UUID)) {
				for (Plot p1 : NavyCraft.playerSigns.get(UUID)) {
					player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(p1.sign) + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + p1.name);
				}
			}
			return;	
		} else {
			player.sendMessage(ChatColor.RED + p + "has never joined the server!");
			return;
		}
		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard plist <playerName>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "List the given player's plots");
			return;
		}
	}

}
