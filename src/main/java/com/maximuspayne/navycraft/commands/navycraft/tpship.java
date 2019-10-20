package com.maximuspayne.navycraft.commands.navycraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.craft.Craft;

public class tpship {

	public static void call(Player player, String[] split) {
		if (!PermissionInterface.CheckPerm(player, "navycraft.tpship"))
			return;
		int shipNum = -1;
		if (split.length == 3) {
			try {
				shipNum = Integer.parseInt(split[2]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "Invalid ID Number");
				e.printStackTrace();
			}
		}
		if (shipNum != -1) {
			for (Craft c : Craft.craftList) {
				if (shipNum == c.craftID) {
					player.teleport(new Location(c.world, c.getLocation().getX(), c.maxY, c.getLocation().getZ()));
					return;
				}
			}
			player.sendMessage(ChatColor.RED + "ID Number not found");
		} else {
			player.sendMessage(ChatColor.RED + "Invalid ID Number");
		}
		return;
	}

}
