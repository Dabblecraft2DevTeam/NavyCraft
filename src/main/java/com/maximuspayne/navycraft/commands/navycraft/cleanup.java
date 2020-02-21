package com.maximuspayne.navycraft.commands.navycraft;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;

public class cleanup {

	public static void call(Player player, String[] split) {
		if( !PermissionInterface.CheckPerm(player, "navycraft.cleanup") )
			return;
		if (NavyCraft.cleanupPlayers.contains(player.getName())) {
			NavyCraft.cleanupPlayers.remove(player.getName());
			player.sendMessage(ChatColor.GOLD + "Exiting cleanup mode.");
		} else {


			NavyCraft.cleanupPlayers.add(player.getName());
			player.sendMessage(ChatColor.GREEN + "Entering cleanup mode.");
		}
		return;
	}

}
