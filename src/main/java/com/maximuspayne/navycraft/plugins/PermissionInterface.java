package com.maximuspayne.navycraft.plugins;


import com.maximuspayne.navycraft.CraftType;
import com.maximuspayne.navycraft.NavyCraft;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

/**
 * Permissions support file to interface Nijikokun's Permissions plugin to MoveCraft
*/

public class PermissionInterface {
	//public static PermissionInfo Permissions = null;

	public static void setupPermissions() {
		PluginManager pm = NavyCraft.instance.getServer().getPluginManager();
//test.
		if(pm != null) {
			pm.addPermission(new Permission("seacraft.periscope.use"));
			pm.addPermission(new Permission("seacraft.aa-gun.use"));
			pm.addPermission(new Permission("seacraft.periscope.create"));
			pm.addPermission(new Permission("seacraft.aa-gun.create"));
			
			for (CraftType type : CraftType.craftTypes) 
			{
				pm.addPermission(new Permission("seacraft." + type.name + ".release"));
				pm.addPermission(new Permission("seacraft." + type.name + ".info"));
				pm.addPermission(new Permission("seacraft." + type.name + ".takeover"));
				pm.addPermission(new Permission("seacraft." + type.name + ".start"));
				pm.addPermission(new Permission("seacraft." + type.name + ".create"));
				pm.addPermission(new Permission("seacraft." + type.name + ".sink"));
				pm.addPermission(new Permission("seacraft." + type.name + ".remove"));
			}
		}
	}
	
	/*public static boolean inGroup(Player player, String group) {
		if(Permissions == null) {
			System.out.println("Movecraft: WARNING! A command attempted to check against a group, " + 
				"but no group handling plugin was found!");
			//return true;
		}

		player.sendMessage("Only users in group " + group + " may use that.");
		return false;
	}

	@SuppressWarnings("deprecation")
	public static boolean CheckGroupPermission(String world, Player player, String group) {
		MoveCraft.instance.DebugMessage("Checking if " + player.getName() + " is in group " + group, 4);
		
		if(Permissions == null) {
			System.out.println("Movecraft: WARNING! A command attempted to check against a group, " + 
				"but no group handling plugin was found!");
			//return true;
		}
		//else if(group.equalsIgnoreCase(Permissions.getGroup(player.getName())))
		else if(group.equalsIgnoreCase(Permissions.getGroup(world, player.getName())))
			return true;
		if(group.equalsIgnoreCase(player.getName()))
			return true;

		player.sendMessage("Your group does not have permission for that.");
		return false;
	}*/
	
	public static boolean CheckPermission(Player player, String command) {		
		command = command.replace(" ", ".");
		NavyCraft.instance.DebugMessage("Checking if " + player.getName() + " can " + command, 3);
		
		
		    if( player.hasPermission(command) || player.isOp() ) 
		    {
		    	NavyCraft.instance.DebugMessage("Player has permissions: " + command, 3);
		    	NavyCraft.instance.DebugMessage("Player isop: " + 
		    			player.isOp(), 3);
		    	return true;
		    } else 
		    {
				player.sendMessage("You do not have permission to perform " + command);
				return false;
		    }
	}
	
	public static boolean CheckQuietPermission(Player player, String command) {		
		command = command.replace(" ", ".");
		NavyCraft.instance.DebugMessage("Checking if " + player.getName() + " can " + command, 3);
		
		
		    if( player.hasPermission(command) || player.isOp() ) 
		    {
		    	NavyCraft.instance.DebugMessage("Player has permissions: " + command, 3);
		    	NavyCraft.instance.DebugMessage("Player isop: " + 
		    			player.isOp(), 3);
		    	return true;
		    } else 
		    {
				//player.sendMessage("You do not have permission to perform " + command);
				return false;
		    }
	}
}
