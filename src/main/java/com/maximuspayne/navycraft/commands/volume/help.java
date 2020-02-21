package com.maximuspayne.navycraft.commands.volume;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;

public class help {

	public static void call(Player player, String[] split) {
		player.sendMessage(ChatColor.GOLD + "Volume v" + ChatColor.GREEN + NavyCraft.version + ChatColor.GOLD + " commands :");
		player.sendMessage(ChatColor.AQUA + "/volume - status message");
		player.sendMessage(ChatColor.AQUA + "/volume <type> <volume> - sets volume for type");
		player.sendMessage(ChatColor.YELLOW + "Types: engine, weapon, other, all");
		return;
	}

	
	
}
