package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.maximuspayne.shipyard.Plot;

@SuppressWarnings("deprecation")
public class renumber {

	public static void call(Player player, String[] split) {
		if (split.length > 3) {
			String UUID = Utils.getUUIDfromPlayer(player.getName());
			int tpId = -1;
			try {
				tpId = Integer.parseInt(split[2]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
				return;
			}

			int newId;
			try {
				newId = Integer.parseInt(split[3]);
			} catch (NumberFormatException e) {
				player.sendMessage(ChatColor.RED + "Invalid New ID");
				return;
			}

			if (tpId > -1 && newId > -1) {
				NavyCraft_FileListener.loadSignData();
				NavyCraft_BlockListener.loadRewards(player.getName());

				Sign foundSign = null;
				foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);
				if (foundSign != null) {
				Block block = foundSign.getBlock();
				if (NavyCraft.playerSigns.containsKey(UUID)) {
					for (Plot p : NavyCraft.playerSigns.get(UUID)) {
						if (newId == NavyCraft.playerSignIndex.get(p.sign)) {
							player.sendMessage(ChatColor.RED + "ID already exists in database!");
							return;
						}
					}
				}
				BlockFace bf = null;
				if (block != null) {
				// bf2 = null;
				switch (block.getData()) {
					case (byte) 0x8:// n
						bf = BlockFace.SOUTH;
						// bf2 = BlockFace.NORTH;
						break;
					case (byte) 0x0:// s
						bf = BlockFace.NORTH;
						// bf2 = BlockFace.SOUTH;
						break;
					case (byte) 0x4:// w
						bf = BlockFace.EAST;
						// bf2 = BlockFace.WEST;
						break;
					case (byte) 0xC:// e
						bf = BlockFace.WEST;
						// bf2 = BlockFace.EAST;
						break;
					default:
						break;
				}

				if (bf == null) {
					player.sendMessage(ChatColor.DARK_RED + "Sign Error: Check Direction?");
					return;
				}
			}
				Sign sign2 = (Sign) block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getState();
					sign2.setLine(2, String.valueOf(newId));
					sign2.update();
					NavyCraft_FileListener.updateSign(UUID, sign2.getLine(3), foundSign.getX(), foundSign.getY(), foundSign.getZ(), foundSign.getWorld(), newId, true);
					player.sendMessage(ChatColor.GREEN + "Plot renumbered.");
					NavyCraft_FileListener.loadSignData();
				} else {
					player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
				}

			} else {
				player.sendMessage(ChatColor.RED + "Invalid Plot ID");
			}
		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard renumber <old id> <new id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "renames the plot");
		}
	}

}
