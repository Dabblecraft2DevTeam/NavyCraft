package com.maximuspayne.navycraft.commands.rank;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class view {

	public static PermissionsEx pex;
	
	@SuppressWarnings("deprecation")
	public static void call(Player player, String[] split) {
		if (!PermissionInterface.CheckPerm(player, "navycraft.rview") && !player.isOp()) {
			player.sendMessage(ChatColor.RED + "You do not have permission to view players ranks.");
			return;
		}

		if (split.length < 3) {
			player.sendMessage(ChatColor.GOLD + "Usage - /rank view <player>");
			player.sendMessage(ChatColor.GOLD + "Example - /rank view Solmex");
			return;
		}
		String p = split[2];
		{
			int exp = 0;
			int exp1 = 0;
			String worldName = null;
			
			NavyCraft_FileListener.loadExperience(p);
			
			pex = (PermissionsEx)NavyCraft.instance.getServer().getPluginManager().getPlugin("PermissionsEx");
			
			int rankExp=0;
			for(String s:PermissionsEx.getUser(p).getPermissions(worldName)) {
				if( s.contains("navycraft") ) {
					if( s.contains("exp") ) {
						String[] split2 = s.split("\\.");
						try {
							rankExp = Integer.parseInt(split2[2]);	
						} catch (Exception ex) {
							System.out.println("Invalid perm-" + s);
						}
					}
				}
			}
			
			List<String> groupNames = PermissionsEx.getUser(p).getParentIdentifiers("navycraft");
			for( String s : groupNames ) {
				if( PermissionsEx.getPermissionManager().getGroup(s).getRankLadder().equalsIgnoreCase("navycraft") ) {
					if (NavyCraft.playerExp.containsKey(p)) {
						exp = NavyCraft.playerExp.get(p);
					}
					player.sendMessage(ChatColor.GRAY + p + "'s rank is " + ChatColor.WHITE + s.toUpperCase()
							+ ChatColor.GRAY + " and has " + ChatColor.WHITE + exp + "/" + rankExp
							+ ChatColor.GRAY + " rank points.");
					return;
		   } else { 
			   exp1 = NavyCraft.playerExp.get(p);
				String[] groupName = PermissionsEx.getUser(p).getGroupsNames();
				for( String g : groupName ) {
				player.sendMessage(ChatColor.GRAY + p + "'s rank is " + ChatColor.WHITE + g.toUpperCase()
				+ ChatColor.GRAY + " and has " + ChatColor.WHITE + exp1
				+ ChatColor.GRAY + " rank points.");
		return;
		       }
			}
		}
		}
	}

}
