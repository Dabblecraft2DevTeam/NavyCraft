package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;

public class rename {

	public static void call(Player player, String[] split) {
		if (split.length > 3) {
			int tpId = -1;
			try {
				tpId = Integer.parseInt(split[2]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
				return;
			}

			String nameString;
			nameString = "";
			for (int i = 3; i < split.length; i++) {
				nameString += split[i] + " ";
			}

			if (tpId > -1) {
				NavyCraft_FileListener.loadSignData();
				NavyCraft_BlockListener.loadRewards(player.getName());

				Sign foundSign = null;
				foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

				if (foundSign != null) {
					foundSign.setLine(3, nameString);
					foundSign.update();
					player.sendMessage(ChatColor.GREEN + "Plot renamed.");
				} else {
					player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
				}

			} else {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
			}
		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard rename <id> <custom name>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "renames the plot");
		}
	}

}
