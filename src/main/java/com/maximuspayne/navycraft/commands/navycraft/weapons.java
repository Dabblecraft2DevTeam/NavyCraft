package com.maximuspayne.navycraft.commands.navycraft;

import org.bukkit.entity.Player;

import com.maximuspayne.aimcannon.AimCannon;
import com.maximuspayne.aimcannon.Weapon;
import com.maximuspayne.navycraft.PermissionInterface;

import net.md_5.bungee.api.ChatColor;

public class weapons {

	public static void call(Player player, String[] split) {
		if( !PermissionInterface.CheckPerm(player, "navycraft.weapons") )
			return;
		player.sendMessage(ChatColor.GREEN + "Registered Weapons:");
		for (Weapon w : AimCannon.weapons) {
				player.sendMessage(ChatColor.GOLD + "weapon: " + ChatColor.YELLOW + w.weaponType);
		}
		return;
	}

}
