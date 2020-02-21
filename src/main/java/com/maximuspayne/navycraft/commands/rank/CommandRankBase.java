package com.maximuspayne.navycraft.commands.rank;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandRankBase {
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
		case "list":
			list.call(player, split);
			break;
		case "view":
			view.call(player, split);
			break;
		case "set":
			set.call(player, split);
			break;
		case "add":
			add.call(player, split);
			break;
		case "remove":
			remove.call(player, split);
			break;
	    default:
	        player.sendMessage("Unknown command. Type /" + split[0] + " help for help.");
		}
		
	} else {
	 Default.call(player, split);
	}

}
}
