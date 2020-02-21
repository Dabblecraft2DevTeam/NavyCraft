package com.maximuspayne.navycraft.commands.craft;

import org.bukkit.entity.Player;
import com.maximuspayne.navycraft.commands.navycraft.cannons;
import com.maximuspayne.navycraft.commands.navycraft.cleanup;
import com.maximuspayne.navycraft.commands.navycraft.debug;
import com.maximuspayne.navycraft.commands.navycraft.destroyships;
import com.maximuspayne.navycraft.commands.navycraft.help;
import com.maximuspayne.navycraft.commands.navycraft.list;
import com.maximuspayne.navycraft.commands.navycraft.loglevel;
import com.maximuspayne.navycraft.commands.navycraft.reload;
import com.maximuspayne.navycraft.commands.navycraft.removeships;
import com.maximuspayne.navycraft.commands.navycraft.tpship;
import com.maximuspayne.navycraft.commands.navycraft.weapons;
import com.maximuspayne.navycraft.craft.Craft;
import com.maximuspayne.navycraft.craft.CraftType;

public class CommandCraftBase {
	
	public boolean call(CraftType craftType, Player player, String[] split) {
		for (int i = 0; i < split.length; i++) {
			 split[i] = split[i].toLowerCase();
			}
		Craft craft = Craft.getPlayerCraft(player);
		if (split.length > 1) {
		switch (split[1]) {
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
	    default:
	        player.sendMessage("Unknown command. Type /" + split[0] + " help for help.");
		}
		
	} else {
	 Default.call(craft, player, split);
	}
		return false;
	}
}
