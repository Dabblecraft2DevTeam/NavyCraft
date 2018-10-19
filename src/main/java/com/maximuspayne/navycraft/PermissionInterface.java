package com.maximuspayne.navycraft;


import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.craft.CraftType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;

/**
 * Permissions support file to interface Nijikokun's Permissions plugin to NavyCraft
*/

public class PermissionInterface {
	public static NavyCraft plugin;
	//public static PermissionInfo Permissions = null;

	public static void setupPermissions(NavyCraft p) {
		plugin = p;
		PluginManager pm = NavyCraft.instance.getServer().getPluginManager();
		if(pm != null) {
			//basic permissions
			pm.addPermission(new Permission("navycraft.basic"));
			pm.addPermission(new Permission("navycraft.signcreate"));
			pm.addPermission(new Permission("navycraft.periscope.use"));
			pm.addPermission(new Permission("navycraft.aa-gun.use"));
			pm.addPermission(new Permission("navycraft.flak-gun.use"));
			pm.addPermission(new Permission("navycraft.ciws.use"));
			pm.addPermission(new Permission("navycraft.searchlight.use"));
			pm.addPermission(new Permission("navycraft.volume.engine"));
			pm.addPermission(new Permission("navycraft.volume.weapon"));
			pm.addPermission(new Permission("navycraft.volume.other"));
			pm.addPermission(new Permission("navycraft.volume.all"));
			pm.addPermission(new Permission("navycraft.expview"));
			pm.addPermission(new Permission("navycraft.dropchance"));
			
			//admin permissions
			pm.addPermission(new Permission("navycraft.admin"));
			pm.addPermission(new Permission("navycraft.pbes"));
			pm.addPermission(new Permission("navycraft.bbes"));
			pm.addPermission(new Permission("navycraft.admincraft"));
			pm.addPermission(new Permission("navycraft.adminsigncreate"));
			pm.addPermission(new Permission("navycraft.craftitems"));
			pm.addPermission(new Permission("navycraft.list"));
			pm.addPermission(new Permission("navycraft.reload"));
			pm.addPermission(new Permission("navycraft.debug"));
			pm.addPermission(new Permission("navycraft.loglevel"));
			pm.addPermission(new Permission("navycraft.cleanup"));
			pm.addPermission(new Permission("navycraft.weapons"));
			pm.addPermission(new Permission("navycraft.cannons"));
			pm.addPermission(new Permission("navycraft.destroyships"));
			pm.addPermission(new Permission("navycraft.removeships"));
			pm.addPermission(new Permission("navycraft.tpship"));
			pm.addPermission(new Permission("navycraft.addsign"));
			pm.addPermission(new Permission("navycraft.aunclaim"));
			pm.addPermission(new Permission("navycraft.explode"));
			pm.addPermission(new Permission("navycraft.explodesigns"));
			pm.addPermission(new Permission("navycraft.expset"));
			pm.addPermission(new Permission("navycraft.expadd"));
			pm.addPermission(new Permission("navycraft.expremove"));
			pm.addPermission(new Permission("navycraft.admindrive"));
			pm.addPermission(new Permission("navycraft.takeover"));
			pm.addPermission(new Permission("navycraft.remove"));
			pm.addPermission(new Permission("navycraft.destroy"));
			pm.addPermission(new Permission("navycraft.buoy"));
			pm.addPermission(new Permission("navycraft.free"));
			pm.addPermission(new Permission("navycraft.select"));
			pm.addPermission(new Permission("navycraft.reward"));
			
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
	
	public static void removePermissions(NavyCraft p) {
		plugin = p;
		PluginManager pm = NavyCraft.instance.getServer().getPluginManager();
		if(pm != null) {
			//basic permissions
			pm.removePermission(new Permission("navycraft.basic"));
			pm.removePermission(new Permission("navycraft.signcreate"));
			pm.removePermission(new Permission("navycraft.periscope.use"));
			pm.removePermission(new Permission("navycraft.aa-gun.use"));
			pm.removePermission(new Permission("navycraft.flak-gun.use"));
			pm.removePermission(new Permission("navycraft.ciws.use"));
			pm.removePermission(new Permission("navycraft.searchlight.use"));
			pm.removePermission(new Permission("navycraft.volume.engine"));
			pm.removePermission(new Permission("navycraft.volume.weapon"));
			pm.removePermission(new Permission("navycraft.volume.other"));
			pm.removePermission(new Permission("navycraft.volume.all"));
			pm.removePermission(new Permission("navycraft.expview"));
			pm.removePermission(new Permission("navycraft.dropchance"));
			//admin permissions
			pm.removePermission(new Permission("navycraft.admin"));
			pm.removePermission(new Permission("navycraft.pbes"));
			pm.removePermission(new Permission("navycraft.bbes"));
			pm.removePermission(new Permission("navycraft.admincraft"));
			pm.removePermission(new Permission("navycraft.adminsigncreate"));
			pm.removePermission(new Permission("navycraft.craftitems"));
			pm.removePermission(new Permission("navycraft.list"));
			pm.removePermission(new Permission("navycraft.reload"));
			pm.removePermission(new Permission("navycraft.debug"));
			pm.removePermission(new Permission("navycraft.loglevel"));
			pm.removePermission(new Permission("navycraft.cleanup"));
			pm.removePermission(new Permission("navycraft.weapons"));
			pm.removePermission(new Permission("navycraft.cannons"));
			pm.removePermission(new Permission("navycraft.destroyships"));
			pm.removePermission(new Permission("navycraft.removeships"));
			pm.removePermission(new Permission("navycraft.tpship"));
			pm.removePermission(new Permission("navycraft.addsign"));
			pm.removePermission(new Permission("navycraft.aunclaim"));
			pm.removePermission(new Permission("navycraft.explode"));
			pm.removePermission(new Permission("navycraft.explodesigns"));
			pm.removePermission(new Permission("navycraft.expset"));
			pm.removePermission(new Permission("navycraft.expadd"));
			pm.removePermission(new Permission("navycraft.expremove"));
			pm.removePermission(new Permission("navycraft.admindrive"));
			pm.removePermission(new Permission("navycraft.takeover"));
			pm.removePermission(new Permission("navycraft.remove"));
			pm.removePermission(new Permission("navycraft.destroy"));
			pm.removePermission(new Permission("navycraft.buoy"));
			pm.removePermission(new Permission("navycraft.free"));
			pm.removePermission(new Permission("navycraft.select"));
			pm.removePermission(new Permission("navycraft.reward"));
			
			for (CraftType type : CraftType.craftTypes) 
			{
				pm.removePermission(new Permission("navycraft." + type.name + ".release"));
				pm.removePermission(new Permission("navycraft." + type.name + ".info"));
				pm.removePermission(new Permission("navycraft." + type.name + ".takeover"));
				pm.removePermission(new Permission("navycraft." + type.name + ".start"));
				pm.removePermission(new Permission("navycraft." + type.name + ".create"));
				pm.removePermission(new Permission("navycraft." + type.name + ".sink"));
				pm.removePermission(new Permission("navycraft." + type.name + ".remove"));
			}
		}
	}
	
	public static boolean CheckPerm(Player player, String command) {		
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
				player.sendMessage(ChatColor.RED + "You do not have permission to perform " + ChatColor.YELLOW + command);
				return false;
		    }
	}
	
	public static boolean CheckQuietPerm(Player player, String command) {		
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