package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.maximuspayne.shipyard.Plot;

public class list {

	public static void call(Player player, String[] split) {
		NavyCraft_FileListener.loadSignData();
		NavyCraft_BlockListener.loadRewards(player.getName());
		String UUID = Utils.getUUIDfromPlayer(player.getName());
		player.sendMessage(ChatColor.AQUA + "Your Shipyard Plots:");
		player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "ID" + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TYPE");

		if (NavyCraft.playerSigns.containsKey(UUID)) {
			for (Plot p : NavyCraft.playerSigns.get(UUID)) {
				player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(p.sign) + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + p.name);
			}
		}
	}

}
