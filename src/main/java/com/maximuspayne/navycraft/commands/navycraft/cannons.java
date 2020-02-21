package com.maximuspayne.navycraft.commands.navycraft;

import com.maximuspayne.aimcannon.AimCannon;
import com.maximuspayne.aimcannon.OneCannon;
import com.maximuspayne.navycraft.PermissionInterface;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class cannons {

	public static void call(Player player, String[] split) {
		if( !PermissionInterface.CheckPerm(player, "navycraft.cannons") )
			return;
		player.sendMessage(ChatColor.GREEN + "Registered Cannons:");
		for (OneCannon c : AimCannon.cannons) {
			String addString = "";
				if (c.ownerCraft != null)
					addString += c.ownerCraft.name + " id:" + c.ownerCraft.craftID;
				player.sendMessage(ChatColor.GOLD + "cannon: " + ChatColor.YELLOW + c.cannonType + " " + addString);
		}
		return;
	}

}
