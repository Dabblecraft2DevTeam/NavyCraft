package com.maximuspayne.navycraft.commands.navycraft;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandNavyCraftBase {

	public static void call(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String[] split = event.getMessage().split(" ");
		for (int i = 0; i < split.length; i++) {
			 split[i] = split[i].toLowerCase();
			}
		if (split.length > 1) {
		switch (split[1]) {
		case "types":
			types.call(player, split);
			break;
		case "list":
			list.call(player, split);
			break;
		case "reload":
			reload.call(player, split);
			break;
		case "debug":
			debug.call(player, split);
			break;
		case "loglevel":
			loglevel.call(player, split);
			break;
		case "cleanup":
			cleanup.call(player, split);
			break;
		case "weapons":
			weapons.call(player, split);
			break;
		case "cannons":
			cannons.call(player, split);
			break;
		case "destroyships":
			destroyships.call(player, split);
			break;
		case "removeships":
			removeships.call(player, split);
			break;
		case "tp":
			tpship.call(player, split);
			break;
		case "tpship":
			tpship.call(player, split);
			break;
		case "help":
			help.call(player, split);
			break;
		case "hidden":
			hidden.call(player, split);
			break;
		case "autotask":
			auto.call(player, split);
			break;
		case "listtask":
			listtask.call(player, split);
			break;
		case "deltask":
			deltask.call(player, split);
			break;
		case "starttasks":
			startTasks.call(player, split);
			break;
            case "kick":
                kick.call(player, split);
                break;
			/*
		case "divideexpplayers":
			Utils.divideExp();
			break;
		case "resetallplayers":
			Utils.resetAll();
			break;
		case "deleteallfilescontaining":
			deleteFiles.call(player, split);
			break;
			*/
	    default:
	        player.sendMessage("Unknown command. Type " + split[0] + " help for help.");
		}
		
	} else {
	 help.call(player, split);
	}

}
	
	
}
