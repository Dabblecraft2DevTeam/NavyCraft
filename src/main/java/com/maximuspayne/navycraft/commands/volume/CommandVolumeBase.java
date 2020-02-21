package com.maximuspayne.navycraft.commands.volume;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandVolumeBase {
	public static void call(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String[] split = event.getMessage().split(" ");
		for (int i = 0; i < split.length; i++) {
			 split[i] = split[i].toLowerCase();
			}
		if (split.length > 1) {
		switch (split[1]) {
		case "help":
			help.call(player, split);
			break;
		case "engine":
			engine.call(player, split);
			break;
		case "weapon":
			weapon.call(player, split);
			break;
		case "other":
			other.call(player, split);
			break;
		case "all":
			engine.call(player, split);
			weapon.call(player, split);
			other.call(player, split);
			break;
	    default:
	        player.sendMessage("Unknown command. Type \"/" + split[0] + "\" help for help.");
		}
		
	} else {
	 help.call(player, split);
	}

}
}
