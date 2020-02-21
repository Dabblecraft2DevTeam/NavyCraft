package com.maximuspayne.navycraft.commands.rank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;

public class remove {

	public static void call(Player player, String[] split) {
		if (!PermissionInterface.CheckPerm(player, "navycraft.rremove") && !player.isOp()) {
			player.sendMessage(ChatColor.RED + "You do not have permission to remove exp.");
			return;
		}

		if (split.length < 4) {
			player.sendMessage(ChatColor.GOLD + "Usage - /rank remove <player> <exp>");
			player.sendMessage(ChatColor.GOLD + "Example - /rank remove Solmex 100");
			return;
		}
		int newExp = Math.abs(Integer.parseInt(split[3]));
		String p = split[2];
		if (NavyCraft.playerExp.containsKey(p)) {
			newExp = NavyCraft.playerExp.get(p) - newExp;
			NavyCraft.playerExp.put(p, newExp);
		} else {
			NavyCraft.playerExp.put(p, newExp);
		}
		NavyCraft_FileListener.saveExperience(p);
		Utils.showRank(player, p);
	}

}
