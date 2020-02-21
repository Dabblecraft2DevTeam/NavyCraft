package com.maximuspayne.navycraft.commands.rank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;

public class help {

	public static void call(Player player, String[] split) {
		player.sendMessage(ChatColor.GOLD + "Rank v" + ChatColor.GREEN + NavyCraft.version + ChatColor.GOLD + " commands :");
		player.sendMessage(ChatColor.AQUA + "/rank - view your rank");
		player.sendMessage(ChatColor.AQUA + "/rank view <player> - view players exp");
		player.sendMessage(ChatColor.AQUA + "/rank list - list ranks in the plugin");
		if (PermissionInterface.CheckQuietPerm(player, "navycraft.admin") || player.isOp()) {
		player.sendMessage(ChatColor.RED + "Rank Admin v" + ChatColor.GREEN + NavyCraft.version + ChatColor.RED + "commands :");
		player.sendMessage(ChatColor.BLUE + "/rank set <player> <exp> - set a players exp");
		player.sendMessage(ChatColor.BLUE + "/rank add <player> <exp> - give exp to a player");
		player.sendMessage(ChatColor.BLUE + "/rank remove <player> <exp> - remove exp from a player");
		}
	}

}
