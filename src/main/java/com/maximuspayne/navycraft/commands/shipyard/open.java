package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.maximuspayne.shipyard.PlotType;
import com.maximuspayne.shipyard.Shipyard;

public class open {

	public static void call(Player player, String[] split) {
		if (split.length == 3) {
			String typeString = split[2];

			Block tpBlock = null;
		for (PlotType pt :Shipyard.getPlots()) {
			if (typeString.equalsIgnoreCase(pt.name)) {
				tpBlock = NavyCraft_FileListener.findSignOpen(pt.name);
				break;
			}
		}

			if (tpBlock != null) {
				player.teleport(tpBlock.getLocation().add(0.5, 0.5, 0.5));
			} else {
				player.sendMessage(ChatColor.RED + "No open plots found!");
			}

		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard open <plot type>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD +  "teleport to an unclaimed plot");
		}
	}

}
