package com.maximuspayne.navycraft.commands.navycraft;

import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;

import net.md_5.bungee.api.ChatColor;

public class debug {

	public static void call(Player player, String[] split) {
		if( !PermissionInterface.CheckPerm(player, "navycraft.debug") )
			return;
		NavyCraft.instance.ToggleDebug();
		player.sendMessage(ChatColor.GREEN + "Debug mode set to: " + ChatColor.GOLD + NavyCraft.instance.DebugMode);
		return;
	}

}
