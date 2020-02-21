package com.maximuspayne.navycraft.commands.debug;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class DevCommands {

	public static void call(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String[] split = event.getMessage().split(" ");
		split[0] = split[0].substring(1);
		for (int i = 0; i < split.length; i++) {
			 split[i] = split[i].toLowerCase();
			}
		switch (split[0]) {
		case "isdatablock":
			isDataBlock.call(player, split);
			break;
		case "iscomplexblock":
			isComplexBlock.call(player, split);
			break;
		case "diamondit":
			diamondit.call(player, split);
			break;
		case "craftvars":
			craftvars.call(player, split);
			break;
		case "getrotation":
			getRotation.call(player, split);
			break;
		case "finddatablocks":
			findDataBlocks.call(player, split);
			break;
		case "findcomplexblocks":
			findComplexBlocks.call(player, split);
			break;
		case "explode":
			explode.call(player, split);
			break;
		case "explodesigns":
			explodesigns.call(player, split);
			break;
		}
		
	}
	
	
}
