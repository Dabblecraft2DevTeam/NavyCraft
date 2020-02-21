package com.maximuspayne.navycraft.commands.navycraft;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;

public class loglevel {

	public static void call(Player player, String[] split) {
		if( !PermissionInterface.CheckPerm(player, "navycraft.loglevel") )
			return;
		try {
			Integer.parseInt(split[2]);
			NavyCraft.instance.getConfig().set("LogLevel", split[2]);
			player.sendMessage(ChatColor.GREEN + "LogLevel set to: " + ChatColor.GOLD + split[2]);
		} catch (Exception ex) {
			player.sendMessage(ChatColor.RED + "Invalid loglevel.");
		}
		return;
	}

}
