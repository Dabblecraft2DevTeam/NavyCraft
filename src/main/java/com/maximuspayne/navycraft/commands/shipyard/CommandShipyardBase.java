package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.maximuspayne.navycraft.commands.navycraft.undo;

public class CommandShipyardBase {
	public static void call(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		String[] split = event.getMessage().split(" ");
		for (int i = 0; i < split.length; i++) {
			 split[i] = split[i].toLowerCase();
			}
		if (split.length > 1) {
		switch (split[1]) {
		case "addmember":
			addmember.call(p, split);
			break;
		case "addsign":
			addsign.call(p, split);
			break;
		case "aunclaim":
			aunclaim.call(p, split);
			break;
		case "clear":
			clear.call(p, split);
			break;
		case "help":
			help.call(p, split);
			break;
		case "info":
			info.call(p, split);
			break;
		case "list":
			list.call(p, split);
			break;
		case "load":
			load.call(p, split);
			break;
		case "open":
			open.call(p, split);
			break;
		case "player":
			player.call(p, split);
			break;
		case "plist":
			plist.call(p, split);
			break;
		case "private":
			Private.call(p, split);
			break;
		case "ptp":
			ptp.call(p, split);
			break;
		case "public":
			Public.call(p, split);
			break;
		case "remmember":
			remmember.call(p, split);
			break;
		case "renumber":
			renumber.call(p, split);
			break;
		case "save":
			save.call(p, split);
			break;
		case "saveall":
			saveall.call(p, split);
			break;
		case "slist":
			slist.call(p, split);
			break;
		case "splist":
			splist.call(p, split);
			break;
		case "rename":
			rename.call(p, split);
			break;
		case "tp":
			tp.call(p, split);
			break;
		case "unclaim":
			unclaim.call(p, split);
			break;
		case "undo":
			undo.call(p, split);
	    default:
	        p.sendMessage("Unknown command. Type /" + split[0] + " help for help.");
		}
		
	} else {
	 Default.call(p, split);
	}

}
}
