package com.maximuspayne.navycraft.commands.volume;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;

public class other {

	public static void call(Player player, String[] split) {
		if (!PermissionInterface.CheckPerm(player, "navycraft.volume.other") && !player.isOp()) {
			player.sendMessage(ChatColor.RED + "You do not have permission to set other volume.");
			return;
		}
			if (split.length == 3) {
				float inValue = 1.0f;
				try {
					inValue = Float.parseFloat(split[2]);
					if ((inValue >= 0) && (inValue <= 100.0f)) {
						NavyCraft.playerOtherVolumes.put(player, inValue);
						player.sendMessage(ChatColor.GOLD + "Volume set for other - " + ChatColor.GREEN + inValue + "%");
					} else {
						player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
					}
				} catch (NumberFormatException e) {
					player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
				}
			} else {
				player.sendMessage(ChatColor.YELLOW + "Change other volume with /volume other <%> with % from 0 to 100");
			}
			return;
		}

}
