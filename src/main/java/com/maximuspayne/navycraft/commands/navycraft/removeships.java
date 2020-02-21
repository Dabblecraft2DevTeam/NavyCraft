package com.maximuspayne.navycraft.commands.navycraft;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.craft.Craft;

public class removeships {

	public static void call(Player player, String[] split) {
		if( !PermissionInterface.CheckPerm(player, "navycraft.removeships") )
			return;
		for (Craft c : Craft.craftList) {
			c.doRemove = true;
		}
		player.sendMessage(ChatColor.GREEN + "All vehicles removed");
		return;
	}

}
