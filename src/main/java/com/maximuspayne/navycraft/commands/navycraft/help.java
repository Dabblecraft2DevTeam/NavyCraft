package com.maximuspayne.navycraft.commands.navycraft;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class help {

	public static void call(Player player, String[] split) {
		if (PermissionInterface.CheckPerm(player, "navycraft.basic")) {
			player.sendMessage(ChatColor.GOLD + "NavyCraft v" + ChatColor.GREEN + NavyCraft.version
					+ ChatColor.GOLD + " commands :");
			player.sendMessage(ChatColor.AQUA + split[0] + " types " + " : " + ChatColor.WHITE
					+ "list the types of craft available");
			player.sendMessage(ChatColor.AQUA + "/[craft type] " + " : " + ChatColor.WHITE
					+ "commands specific to the craft type try /ship help");
			player.sendMessage(ChatColor.AQUA + "/volume" + " : " + ChatColor.WHITE + "volume help");
			player.sendMessage(ChatColor.AQUA + "/rank" + " : " + ChatColor.WHITE + "rank status message");
			player.sendMessage(
					ChatColor.AQUA + split[0] + " undo " + " : " + ChatColor.WHITE + "undo a sign you paid for");
		}

		if (PermissionInterface.CheckQuietPerm(player, "navycraft.admin")) {
			player.sendMessage(ChatColor.RED + "NavyCraft Admin v" + ChatColor.GREEN + NavyCraft.version
					+ ChatColor.RED + " commands :");
			player.sendMessage(ChatColor.BLUE + split[0] + " list : " + ChatColor.WHITE
					+ "list all craft");
			player.sendMessage(
					ChatColor.BLUE + split[0] + " reload : " + ChatColor.WHITE + "reload config files");
			player.sendMessage(
					ChatColor.BLUE + split[0] + " config : " + ChatColor.WHITE + "display config settings");
			player.sendMessage(ChatColor.BLUE + split[0] + " cleanup : " + ChatColor.WHITE
					+ "enables cleanup tools, use lighter, gold pickaxe, and shears");
			player.sendMessage(ChatColor.BLUE + split[0] + " destroyships : " + ChatColor.WHITE
					+ "destroys all active ships");
			player.sendMessage(ChatColor.BLUE + split[0] + " removeships : " + ChatColor.WHITE
					+ "deactivates all active ships");
			player.sendMessage(ChatColor.BLUE + split[0] + " tpship id # : " + ChatColor.WHITE
					+ "teleport to ship ID #");
			player.sendMessage(ChatColor.BLUE + split[0] + " autotask initialwait repeatwait : " + ChatColor.WHITE
					+ "start auto merchant spawning task");
			player.sendMessage(ChatColor.BLUE + split[0] + " listtask : " + ChatColor.WHITE
					+ "list auto merchant spawning tasks");
			player.sendMessage(ChatColor.BLUE + split[0] + " deltask : " + ChatColor.WHITE
					+ "remove auto merchant spawning task");
		}
	}

}
