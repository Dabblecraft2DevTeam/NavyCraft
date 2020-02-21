package com.maximuspayne.navycraft.commands.craft;

import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.craft.Craft;
import com.maximuspayne.navycraft.craft.CraftType;

public class driveCommand {

	public static void call(CraftType craftType, Player player, String[] split) {
		String name = craftType.name;
		if ((split.length > 2) && (split[2] != null)) {
			name = split[2];
		}

		// try to detect and create the craft
		// use the block the player is standing on
		Craft checkCraft = Craft.getCraft(player.getLocation().getBlockX(), player.getLocation().getBlockY(),
				player.getLocation().getBlockZ());
		if (checkCraft != null) {

		} else {
			NavyCraft.instance.createCraft(player, craftType, (int) Math.floor(player.getLocation().getX()),(int) Math.floor(player.getLocation().getY() - 1),(int) Math.floor(player.getLocation().getZ()), name, player.getLocation().getYaw(), null);
		}
		return;
	}
}
