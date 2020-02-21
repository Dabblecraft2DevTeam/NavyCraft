package com.maximuspayne.navycraft.commands.volume;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;

public class engine {

	public static void call(Player player, String[] split) {
		if (!PermissionInterface.CheckPerm(player, "navycraft.volume.engine") && !player.isOp()) {
			player.sendMessage(ChatColor.RED + "You do not have permission to set engine volume.");
			return;
		}
			if (split.length == 3) {
				float inValue = 1.0f;
				try {
					inValue = Float.parseFloat(split[2]);
					if ((inValue >= 0) && (inValue <= 100.0f)) {
						NavyCraft.playerEngineVolumes.put(player, inValue);
						player.sendMessage(ChatColor.GOLD + "Volume set for engines - " + ChatColor.GREEN + inValue + "%");
					} else {
						player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
					}
				} catch (NumberFormatException e) {
					player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
				}
			} else {
				player.sendMessage(ChatColor.YELLOW + "Change engine volume with /volume engine <%> with % from 0 to 100");
			}
			return;
	}

}
