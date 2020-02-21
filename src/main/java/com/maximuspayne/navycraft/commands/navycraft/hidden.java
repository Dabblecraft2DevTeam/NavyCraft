package com.maximuspayne.navycraft.commands.navycraft;

import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;

import net.md_5.bungee.api.ChatColor;

public class hidden {

	public static void call(Player player, String[] split) {
		if (NavyCraft.disableHiddenChats.contains(player.getName())) {
			NavyCraft.disableHiddenChats.remove(player.getName());
			player.sendMessage(ChatColor.GREEN + "You will now see hidden chat messages.");
		} else {
			NavyCraft.disableHiddenChats.add(player.getName());

			player.sendMessage(ChatColor.RED + "You will no longer view hidden chat messages.");
		}
		return;
	}

}
