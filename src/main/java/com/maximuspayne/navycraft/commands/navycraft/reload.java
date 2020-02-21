package com.maximuspayne.navycraft.commands.navycraft;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;

public class reload {

	public static void call(Player player, String[] split) {
		if( !PermissionInterface.CheckPerm(player, "navycraft.reload") )
			return;
		NavyCraft.instance.loadProperties();
		player.sendMessage(ChatColor.GREEN + "NavyCraft configuration reloaded");
		return;
	}

}
