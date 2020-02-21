package com.maximuspayne.navycraft.commands.rank;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Default {

	public static PermissionsEx pex;
	
	@SuppressWarnings("deprecation")
	public static void call(Player player, String[] split) {
		int exp = 0;
		int exp1 = 0;
		String worldName = player.getWorld().getName();
		
		NavyCraft_FileListener.loadExperience(player.getName());
		
		pex = (PermissionsEx)NavyCraft.instance.getServer().getPluginManager().getPlugin("PermissionsEx");
		
		int rankExp=0;
		for(String s:PermissionsEx.getUser(player).getPermissions(worldName)) {
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
		List<String> groupNames = PermissionsEx.getUser(player).getParentIdentifiers("navycraft");
		for( String s : groupNames ) {
			if( PermissionsEx.getPermissionManager().getGroup(s).getRankLadder().equalsIgnoreCase("navycraft") ) {
				if (NavyCraft.playerExp.containsKey(player.getName())) {
					exp = NavyCraft.playerExp.get(player.getName());
				}
				player.sendMessage(ChatColor.GRAY + "Your rank is " + ChatColor.WHITE + s.toUpperCase()
						+ ChatColor.GRAY + " and you have " + ChatColor.WHITE + exp + "/" + rankExp
						+ ChatColor.GRAY + " rank points.");
				if( exp >= rankExp )
				{
							NavyCraft_BlockListener.checkRankWorld(player, exp, player.getWorld());
				}
				return;
			   } else { 
					if (NavyCraft.playerExp.containsKey(player.getName())) {
					exp1 = NavyCraft.playerExp.get(player.getName());
					}
					String[] groupName = PermissionsEx.getUser(player).getGroupsNames();
					for( String g : groupName ) {
					player.sendMessage(ChatColor.GRAY + "Your rank is " + ChatColor.WHITE + g.toUpperCase()
					+ ChatColor.GRAY + " and you have " + ChatColor.WHITE + exp1
					+ ChatColor.GRAY + " rank points.");
				return;
			}
	   }
	}
	}

}
