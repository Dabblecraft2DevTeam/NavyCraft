package com.maximuspayne.navycraft.commands.debug;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;

public class explodesigns {

	public static void call(Player player, String[] split) {
		if (PermissionInterface.CheckPerm(player, "navycraft.explodesigns")) {
			if (split.length == 2) {
				float inValue = 1.0f;
				try {
					inValue = Float.parseFloat(split[1]);
					if ((inValue >= 1) && (inValue <= 100.0f)) {
						NavyCraft.explosion((int)inValue, player.getLocation().getBlock(),true);
						player.sendMessage(ChatColor.GOLD + "Boom Level" + ChatColor.DARK_GRAY + "- " + ChatColor.GREEN + inValue);
					} else {
						player.sendMessage(ChatColor.RED + "Invalid explosion level, use a number from 1 to 100");
					}
				} catch (NumberFormatException e) {
					player.sendMessage(ChatColor.RED + "Invalid explosion level, use a number from 1 to 100");
				}
			} else {
				player.sendMessage(ChatColor.YELLOW + "/explode ###  number from 1-100");
			}
		}else{
			player.sendMessage(ChatColor.RED + "You do not have permission to use that.");
		}
		return;
	}

}
