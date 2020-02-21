package com.maximuspayne.navycraft.commands.navycraft;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.craft.Craft;

public class list {

	public static void call(Player player, String[] split) {
		if( !PermissionInterface.CheckPerm(player, "navycraft.list") )
			return;
		if (Craft.craftList.isEmpty()) {
			player.sendMessage(ChatColor.RED + "No player controlled craft");
			// return true;
		}

		for (Craft craft : Craft.craftList) {

			player.sendMessage(ChatColor.YELLOW + "" + craft.craftID + " - " + craft.name + " commanded by " + craft.captainName + ": "
					+ craft.blockCount + " blocks");
		}
	}

}
