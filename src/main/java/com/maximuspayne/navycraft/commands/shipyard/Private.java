package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;

@SuppressWarnings("deprecation")
public class Private {

	public static void call(Player player, String[] split) {
		if (split.length == 3) {
			int tpId = -1;
			try {
				tpId = Integer.parseInt(split[2]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
				return;
			}

			if (tpId > -1) {
				NavyCraft_FileListener.loadSignData();
				NavyCraft_BlockListener.loadRewards(player.getName());

				Sign foundSign = null;
				foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

				if (foundSign != null) {
					Block selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(),
							foundSign.getY() - 1, foundSign.getZ() + 1);
					if (selectSignBlock2.getTypeId() != 68) {
						selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1,
								foundSign.getY() - 1, foundSign.getZ());
					}
					if (selectSignBlock2.getTypeId() == 68) {
						Sign selectSign2 = (Sign) selectSignBlock2.getState();
						selectSign2.setLine(0, "Private");
						selectSign2.update();
						player.sendMessage(ChatColor.GREEN + "Plot set to PRIVATE" + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "Only you and your plot members can SELECT it.");
					} else {
						player.sendMessage(ChatColor.DARK_RED + "Error: There may be a problem with your plot signs.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
				}

			} else {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
			}
		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard private <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "allows any player to select your vehicle");
		}
	}

}
