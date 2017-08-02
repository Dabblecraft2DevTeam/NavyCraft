package com.maximuspayne.navycraft.plugins;


import com.maximuspayne.navycraft.CraftType;
import com.maximuspayne.navycraft.NavyCraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Permissions support file to interface Nijikokun's Permissions plugin to MoveCraft
*/

public class PermissionInterface {
	public static NavyCraft plugin;
	//public static PermissionInfo Permissions = null;

	public static void setupPermissions(NavyCraft p) {
		plugin = p;
		PluginManager pm = NavyCraft.instance.getServer().getPluginManager();
//test.
		if(pm != null) {
			pm.addPermission(new Permission("navycraft.periscope.use"));
			pm.addPermission(new Permission("navycraft.aa-gun.use"));
			pm.addPermission(new Permission("navycraft.periscope.create"));
			pm.addPermission(new Permission("navycraft.aa-gun.create"));
			
			for (CraftType type : CraftType.craftTypes) 
			{
				pm.addPermission(new Permission("navycraft." + type.name + ".release"));
				pm.addPermission(new Permission("navycraft." + type.name + ".info"));
				pm.addPermission(new Permission("navycraft." + type.name + ".takeover"));
				pm.addPermission(new Permission("navycraft." + type.name + ".start"));
				pm.addPermission(new Permission("navycraft." + type.name + ".create"));
				pm.addPermission(new Permission("navycraft." + type.name + ".sink"));
				pm.addPermission(new Permission("navycraft." + type.name + ".remove"));
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
	
	public static boolean CheckEnabledWorld(Location loc) {
		if(plugin.ConfigSetting("EnabledWorlds") != "null") {
			String[] worlds = NavyCraft.instance.ConfigSetting("EnabledWorlds").split(",");
			for(int i = 0; i < worlds.length; i++) {
				if( loc.getWorld().getName().equalsIgnoreCase(worlds[i]) )
				{
					return true;
				}
					
			}
		}
		return false;
	}
	
	public static boolean CheckBattleWorld(Location loc) {
		if(plugin.ConfigSetting("BattleWorlds") != "null") {
			String[] worlds = NavyCraft.instance.ConfigSetting("BattleWorlds").split(",");
			for(int i = 0; i < worlds.length; i++) {
				if( loc.getWorld().getName().equalsIgnoreCase(worlds[i]) )
					return true;
			}
		}
		return false;
	}
}
