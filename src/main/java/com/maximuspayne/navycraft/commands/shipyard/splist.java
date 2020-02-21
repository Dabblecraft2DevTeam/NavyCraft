package com.maximuspayne.navycraft.commands.shipyard;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;

public class splist {

	public static void call(Player player, String[] split) {
		if (split.length == 4) {
			String p = split[3];
			NavyCraft_FileListener.loadSignData();
			NavyCraft_BlockListener.loadRewards(p);
			player.sendMessage(ChatColor.AQUA + p + "'s" + " Shipyard Schematics:");
			player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "NAME" + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TYPE");
            File dir = new File(NavyCraft.instance.getDataFolder(), "/schematics/");
				for (File f : dir.listFiles()) {
					String[] splits = f.getName().split("-");
					if (splits[0].equalsIgnoreCase(p)) {
					player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + f.getName() + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + splits[1]);
				}
			}
			return;
		} else {
			player.sendMessage(ChatColor.YELLOW + "/shipyard schem plist <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "list the given player's schematics");
			return;
		}
	}

}
