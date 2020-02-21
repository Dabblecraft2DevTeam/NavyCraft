package com.maximuspayne.navycraft.commands.navycraft;

import java.math.BigDecimal;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.maximuspayne.navycraft.NavyCraft;

import net.ess3.api.MaxMoneyException;

@SuppressWarnings("deprecation")
public class undo {

	public static void call (Player player, String split[]) {
		if (NavyCraft.playerLastBoughtSign.containsKey(player)) {
			if ((NavyCraft.playerLastBoughtSign.get(player).getTypeId() == 68)
					|| (NavyCraft.playerLastBoughtSign.get(player).getTypeId() == 63)) {
				Sign sign = (Sign) NavyCraft.playerLastBoughtSign.get(player).getState();
				String signString0 = sign.getLine(0).trim().toLowerCase();
				signString0 = signString0.replaceAll(ChatColor.BLUE.toString(), "");
				String signString1 = sign.getLine(1).trim().toLowerCase();
				signString1 = signString1.replaceAll(ChatColor.BLUE.toString(), "");
				String signString2 = sign.getLine(2).trim().toLowerCase();
				signString2 = signString2.replaceAll(ChatColor.BLUE.toString(), "");
				if (signString0.equalsIgnoreCase(NavyCraft.playerLastBoughtSignString0.get(player))
						&& signString1
								.equalsIgnoreCase(NavyCraft.playerLastBoughtSignString1.get(player))
						&& signString2
								.equalsIgnoreCase(NavyCraft.playerLastBoughtSignString2.get(player))) {
					NavyCraft.playerLastBoughtSign.get(player).setTypeId(0);
					Essentials ess;
					ess = (Essentials) NavyCraft.instance.getServer().getPluginManager().getPlugin("Essentials");
					if (ess == null) {
						player.sendMessage(ChatColor.RED + "Essentials Economy error");
						return;
					}
					player.sendMessage(ChatColor.RED + "Undoing sign and refunding player.");
					try {
						ess.getUser(player)
								.giveMoney(new BigDecimal(NavyCraft.playerLastBoughtCost.get(player)));
					} catch (MaxMoneyException e) {
						
						e.printStackTrace();
					}
					NavyCraft.playerLastBoughtSign.remove(player);
					NavyCraft.playerLastBoughtCost.remove(player);
					NavyCraft.playerLastBoughtSignString0.remove(player);
					NavyCraft.playerLastBoughtSignString1.remove(player);
					NavyCraft.playerLastBoughtSignString2.remove(player);
				} else {
					player.sendMessage(ChatColor.RED + "Incorrect sign detected.");
				}

			} else {
				player.sendMessage(ChatColor.RED + "No sign detected to undo.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "Nothing to undo.");
		}
		return;
	}
}
