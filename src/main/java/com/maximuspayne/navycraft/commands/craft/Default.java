package com.maximuspayne.navycraft.commands.craft;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.craft.Craft;

public class Default {

	public static void call(Craft craft, Player player, String[] split) {
		if (craft != null) {
			player.sendMessage(ChatColor.GOLD + "Vehicle Status");
			player.sendMessage(ChatColor.GOLD + "Type : " + ChatColor.WHITE + craft.name);
			if (craft.customName != null) {
				player.sendMessage(ChatColor.GOLD + "Name : " + ChatColor.WHITE + craft.customName);
			} else {
				player.sendMessage(ChatColor.GOLD + "Name : " + ChatColor.WHITE + craft.name);
			}
			player.sendMessage(ChatColor.GOLD + "Captain : " + ChatColor.DARK_AQUA + craft.captainName);
			player.sendMessage(ChatColor.GOLD + "Crew : " + ChatColor.BLUE + craft.crewNames.size());
			player.sendMessage(ChatColor.GOLD + "Size : " + ChatColor.WHITE + craft.blockCount + " blocks");
			player.sendMessage(ChatColor.GOLD + "Weight (current) : " + ChatColor.WHITE + craft.weightCurrent + " tons");
			player.sendMessage(ChatColor.GOLD + "Weight (start) : " + ChatColor.WHITE + craft.weightStart + " tons");
			player.sendMessage(ChatColor.GOLD + "Displacement : " + ChatColor.WHITE + craft.displacement + " tons ("
					+ craft.blockDisplacement + " block," + craft.airDisplacement + " air)");
			player.sendMessage(ChatColor.GOLD + "Health : " + ChatColor.WHITE
					+ (int) (((float) craft.blockCount * 100) / craft.blockCountStart) + "%");
			player.sendMessage(ChatColor.GOLD + "Engines : " + ChatColor.WHITE + craft.engineIDLocs.size() + " of "
					+ craft.engineIDIsOn.size());
		} else {
			player.sendMessage(ChatColor.GOLD + "You have no active vehicle.");
		}
	}

}
