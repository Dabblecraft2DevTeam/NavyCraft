package com.maximuspayne.navycraft.commands.debug;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.craft.Craft;

public class explode {

	public static void call(Player player, String[] split) {
		if (PermissionInterface.CheckPerm(player, "navycraft.explode")) {
			if (split.length == 2) {
				float inValue = 1.0f;
				try {
					inValue = Float.parseFloat(split[1]);
					if ((inValue >= 1) && (inValue <= 100.0f)) {
						NavyCraft.explosion((int)inValue, player.getLocation().getBlock(),false);
						Craft checkCraft=null;
						checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation(), player);
						if( checkCraft == null ) {
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(7,7,7).getLocation(), player);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(-7,-7,-7).getLocation(), player);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(3,-2,-3).getLocation(), player);
									if( checkCraft == null ) {
										checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(-3,2,3).getLocation(), player);
									}
								}
							}
						}
						
						if( checkCraft == null )
							player.sendMessage(ChatColor.GOLD + "Boom Level" + ChatColor.DARK_GRAY + "- " + ChatColor.GREEN + inValue);
						else
							player.sendMessage(ChatColor.GOLD + "Boom level " + ChatColor.DARK_GRAY + "- " + ChatColor.GREEN + inValue + ChatColor.GOLD + " done on " + ChatColor.GREEN + checkCraft.name);
					} else {
						player.sendMessage(ChatColor.RED + "Invalid explosion level, use a number from 1 to 100");
					}
				} catch (NumberFormatException e) {
					player.sendMessage(ChatColor.RED + "Invalid explosion level, use a number from 1 to 100");
				}
			} else {
				player.sendMessage(ChatColor.YELLOW + "/explode ###  number from 1-100");
			}
		}else {
			player.sendMessage(ChatColor.RED + "You do not have permission to use that.");
		}
		return;
	}

}
