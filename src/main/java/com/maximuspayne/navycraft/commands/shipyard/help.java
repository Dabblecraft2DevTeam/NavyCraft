package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;

public class help {

	public static void call(Player player, String[] split) {
		player.sendMessage(ChatColor.GOLD + "Shipyard v" + ChatColor.GREEN + NavyCraft.version + ChatColor.GOLD + " commands :");
		player.sendMessage(ChatColor.AQUA + "/shipyard - Status message");
		player.sendMessage(ChatColor.AQUA + "/shipyard list - List your current plots");
		player.sendMessage(ChatColor.AQUA + "/shipyard info <id> - Information about the given plot");
		player.sendMessage(ChatColor.AQUA + "/shipyard open <plot type> - Teleport to an unclaimed plot");
		player.sendMessage(ChatColor.AQUA + "/shipyard unclaim <id> - Clears and unclaims a plot");
		player.sendMessage(ChatColor.AQUA + "/shipyard tp <id> - Teleport to the plot ID number");
		player.sendMessage(ChatColor.AQUA + "/shipyard addmember <id> <player> - Gives player permission to that plot");
		player.sendMessage(ChatColor.AQUA + "/shipyard remmember <id> <player> - Removes player permission to that plot");
		player.sendMessage(ChatColor.AQUA + "/shipyard clear <id> - Destroys all blocks within the plot");
		player.sendMessage(ChatColor.AQUA + "/shipyard rename <id> <custom name> - Renames the plot");
		player.sendMessage(ChatColor.AQUA + "/shipyard public <id> - Allows any player to select your vehicle");
		player.sendMessage(ChatColor.AQUA + "/shipyard private <id> - Allows only you and your members to select your vehicle");
		player.sendMessage(ChatColor.AQUA + "/shipyard plist <player> - List the given player's plots");
		player.sendMessage(ChatColor.AQUA + "/shipyard ptp <player> <id> - Teleport to the player's plot ID");
		player.sendMessage(ChatColor.AQUA + "/shipyard renumber <id> <newid> - Renumbers the given plot ID to new ID");
		player.sendMessage(ChatColor.AQUA + "/shipyard schem list - List saved vehicles");
		player.sendMessage(ChatColor.AQUA + "/shipyard schem plist - List the given player's saved vehicles");
		player.sendMessage(ChatColor.AQUA + "/shipyard schem load <name> <id> - Load a saved vehicle into a plot");
		player.sendMessage(ChatColor.AQUA + "/shipyard schem save <id> <name> - Saves a vehicle in a plot to a schematic");
		if (PermissionInterface.CheckQuietPerm(player, "navycraft.admin") || player.isOp()) {
			player.sendMessage(ChatColor.RED + "Shipyard Admin v" + ChatColor.GREEN + NavyCraft.version + ChatColor.RED + " commands :");
			player.sendMessage(ChatColor.BLUE + "/shipyard player <player> - View a players plot status");
			player.sendMessage(ChatColor.BLUE + "/shipyard reward <player> <type> <reason> - Rewards the specified plot type to the player");
		}
	}

}
