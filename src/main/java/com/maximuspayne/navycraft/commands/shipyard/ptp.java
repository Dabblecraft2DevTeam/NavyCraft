package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;

public class ptp {

	public static void call(Player player, String[] split) {
		if (split.length == 4) {
			String p = split[2];
			int tpId = -1;
			try {
				tpId = Integer.parseInt(split[3]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
				return;
			}
			if (tpId > -1) {
				NavyCraft_FileListener.loadSignData();
				NavyCraft_BlockListener.loadRewards(p);

				Sign foundSign = null;
				foundSign = NavyCraft_BlockListener.findSign(p, tpId);

				if (foundSign != null) {
					player.teleport(foundSign.getLocation().add(0.5, 0.5, 0.5));
				} else {
					player.sendMessage(ChatColor.RED + "ID not found, Use:" + ChatColor.YELLOW + "/shipyard plist " + p + ChatColor.RED +  "to see IDs");
				}

			} else {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
			}
		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard ptp <playerName> <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "teleport to a player's plot id");
		}
	}

}
