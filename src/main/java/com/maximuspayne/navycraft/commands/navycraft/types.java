package com.maximuspayne.navycraft.commands.navycraft;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.craft.CraftType;

public class types {

	public static void call(Player player, String[] split) {
		if( !PermissionInterface.CheckPerm(player, "navycraft.basic") )
			return;
		for (CraftType craftType : CraftType.craftTypes) {
			if (craftType.canUse(player)) {
				player.sendMessage(ChatColor.GREEN + craftType.name + ChatColor.YELLOW + craftType.minBlocks
						+ "-" + craftType.maxBlocks + " blocks" + " doesCruise : " + craftType.doesCruise);
			}
		}
	}

}
