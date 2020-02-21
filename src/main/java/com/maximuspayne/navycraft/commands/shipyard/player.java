package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.maximuspayne.shipyard.Plot;
import com.maximuspayne.shipyard.PlotType;
import com.maximuspayne.shipyard.Reward;
import com.maximuspayne.shipyard.Shipyard;

public class player {

	public static void call(Player player, String[] split) {
		if (split.length == 3) {
			String p = split[2];
			String UUID = Utils.getUUIDfromPlayer(p);
			NavyCraft_FileListener.loadSignData();
			NavyCraft_BlockListener.loadRewards(p);
			if (UUID != null) {
			player.sendMessage(ChatColor.AQUA + p + "'s Shipyard Plots:");
		for (PlotType pt : Shipyard.getPlots()) {
			int numPlots = 0;
			int numRewPlots = 0;
			if (NavyCraft.playerSigns.containsKey(UUID)) {
				for (Plot p1 : NavyCraft.playerSigns.get(UUID)) {
					if (p1.name.equalsIgnoreCase(pt.name))
						numPlots++;
				}
			}
			if (NavyCraft.playerRewards.containsKey(UUID)) {
				for (Reward r : NavyCraft.playerRewards.get(UUID)) {
					if (r.name.equalsIgnoreCase(pt.name)) {
						numRewPlots = r.amount;
					}
				}
			}
		if (numPlots > 0 || numRewPlots > 0) {
					player.sendMessage(ChatColor.GOLD + pt.name + ChatColor.DARK_GRAY + " [" +  ChatColor.GREEN + numPlots + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/"
							+ ChatColor.DARK_GRAY + "[" + ChatColor.RED + numRewPlots + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
		}
		}
		} else {
			player.sendMessage(ChatColor.RED + p + "has never joined the server!");
		}
		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard player <playerName>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "View a players shipyard status");
		}
	}

}
