package com.maximuspayne.navycraft;

import com.maximuspayne.navycraft.craft.Craft;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
public class Utils {

	public static PermissionsEx pex;
	public static WorldEditPlugin wep;
	
	public static void saveSchem(String schematicName, String customName, ProtectedRegion region, org.bukkit.World world){
        try {
        	if (region != null) {
				wep = (WorldEditPlugin) NavyCraft.instance.getServer().getPluginManager().getPlugin("WorldEdit");
				if (wep == null) {
					return;
				}
				File dir = new File(wep.getConfig().getString("saving.dir"));
				File file = new File(dir, schematicName + "-!" + customName + ".schematic");
				if (!dir.exists())
					dir.mkdirs();
				for (File f : dir.listFiles()) {
					if (f.getName().contains(schematicName + customName)) {
						file = f;
					}
				}

				World weWorld = new BukkitWorld(world);

				Vector min = region.getMinimumPoint();
				Vector max = region.getMaximumPoint();

				EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1);
				CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
				clipboard.copy(editSession);
				SchematicFormat.MCEDIT.save(clipboard, file);
			}
       } catch (IOException | DataException ex) {
           ex.printStackTrace();
       }
    }

	public static double randomDouble(double min, double max) {
		return Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min);
	}

    public static boolean pasteSchem(String schematicName, Location pasteLoc) {
        try {
			wep = (WorldEditPlugin) NavyCraft.instance.getServer().getPluginManager().getPlugin("WorldEdit");
			if (wep == null) {
				return false;
			}
			File dir = new File(wep.getConfig().getString("saving.dir"), schematicName + ".schematic");
			System.out.println(dir);
            if (!dir.exists()) {
            	return false;
            }
            EditSession editSession = new EditSession(new BukkitWorld(pasteLoc.getWorld()), 999999999);
            editSession.enableQueue();

            SchematicFormat schematic = SchematicFormat.getFormat(dir);
            CuboidClipboard clipboard = schematic.load(dir);

            clipboard.paste(editSession, BukkitUtil.toVector(pasteLoc), false);
            editSession.flushQueue();
            return true;
        } catch (DataException | IOException ex) {
        	System.out.println("aaaa");
            return false;
        } catch (MaxChangedBlocksException ex) {
			System.out.println("aaaaaaaaaaa");
            return false;
        }
    }
	
	public static String getUUIDfromPlayer(String player) {
		if (player != null) {
		String UUID = NavyCraft.instance.getServer().getOfflinePlayer(player).getUniqueId().toString();
		return UUID;
		} else {
		return null;
		}
	}
    public static String getNamefromUUID(String uuid) {
    	if (uuid != null) {
    		String name = NavyCraft.instance.getServer().getOfflinePlayer(uuid).getPlayer().getName();
    	    return name;
    	} else {
    		return null;
    	}
    }
	public static boolean CheckEnabledWorld(Location loc) {
		if(!NavyCraft.instance.getConfig().getString("EnabledWorlds").equalsIgnoreCase("null")) {
			String[] worlds = NavyCraft.instance.getConfig().getString("EnabledWorlds").split(",");
			for(int i = 0; i < worlds.length; i++) {
				if( loc.getWorld().getName().equalsIgnoreCase(worlds[i]) )
				{
					return true;
				}
					
			}
			return false;
		}
		return true;
	}
	
	public static boolean CheckTestWorld(Location loc) {
		if(!NavyCraft.instance.getConfig().getString("TestWorlds").equalsIgnoreCase("null")) {
			String[] worlds = NavyCraft.instance.getConfig().getString("TestWorlds").split(",");
			for(int i = 0; i < worlds.length; i++) {
				if( loc.getWorld().getName().equalsIgnoreCase(worlds[i]) )
				{
					return true;
				}
					
			}
			return false;
		}
		return true;
	}
	
	public static boolean CheckBattleWorld(Location loc) {
		if(!NavyCraft.instance.getConfig().getString("BattleWorld").equalsIgnoreCase("null")) {
			String[] worlds = NavyCraft.instance.getConfig().getString("BattleWorld").split(",");
			for(int i = 0; i < worlds.length; i++) {
				if( loc.getWorld().getName().equalsIgnoreCase(worlds[i]) )
				{
					return true;
				}
					
			}
			return false;
		}
		return true;
	}
	
	public static BlockFace getBlockFace(Block block) {
		BlockFace bf;
		bf = null;
		// bf2 = null;
		switch (block.getData()) {
			case (byte) 0x8:// n
				bf = BlockFace.SOUTH;
				// bf2 = BlockFace.NORTH;
				break;
			case (byte) 0x0:// s
				bf = BlockFace.NORTH;
				// bf2 = BlockFace.SOUTH;
				break;
			case (byte) 0x4:// w
				bf = BlockFace.EAST;
				// bf2 = BlockFace.WEST;
				break;
			case (byte) 0xC:// e
				bf = BlockFace.WEST;
				// bf2 = BlockFace.EAST;
				break;
			default:
				break;
		}
		return bf;
	}

	public static BlockFace directionFromCardinal(int targetBearing) {
		BlockFace bf = BlockFace.NORTH;
		if (targetBearing > 45 && targetBearing <= 135)
			bf = BlockFace.EAST;
		if (targetBearing > 135 && targetBearing <= 225)
			bf = BlockFace.SOUTH;
		if (targetBearing > 225 && targetBearing <= 315)
			bf = BlockFace.WEST;
		if (targetBearing > 315 || targetBearing <= 45)
			bf = BlockFace.NORTH;
		return bf;
	}

	public static int directionFromCardinal(BlockFace bf) {
		int targetBearing;
		targetBearing = 0;
		// bf2 = null;
		if (bf == BlockFace.EAST)
			targetBearing = 90;
		if (bf == BlockFace.SOUTH)
			targetBearing = 180;
		if (bf == BlockFace.WEST)
			targetBearing = 270;
		if (bf == BlockFace.NORTH)
			targetBearing = 0;
		return targetBearing;
	}
	
	public static void showRank(Player player, String p) {
		int exp = 0;
		int exp1 = 0;
		String worldName = null;
		
		NavyCraft_FileListener.loadExperience(p);
		
		pex = (PermissionsEx)NavyCraft.instance.getServer().getPluginManager().getPlugin("PermissionsEx");
		
		int rankExp=0;
		for(String s:PermissionsEx.getUser(p).getPermissions(worldName)) {
			if( s.contains("navycraft") ) {
				if( s.contains("exp") ) {
					String[] split = s.split("\\.");
					try {
						rankExp = Integer.parseInt(split[2]);	
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

	public static int calculateBearing(Craft craft, Location loc) {
		int bearing = 0;
		float xDist = loc.getBlockX() - (craft.minX + (craft.sizeX / 2.0f));
		float zDist = loc.getBlockZ() - (craft.minZ + (craft.sizeZ / 2.0f));
		float dist = (float) Math.sqrt((xDist * xDist) + (zDist * zDist));

			double trueBearing = 0;
			if ((xDist >= 0) && (zDist < 0)) {
				double bear = Math.atan(xDist / (-zDist));
				if (bear < 0.26) {
					trueBearing = 0;
				} else if (bear < .70) {
					trueBearing = 30;
				} else if (bear < .87) {
					trueBearing = 45;
				} else if (bear < 1.31) {
					trueBearing = 60;
				} else if (bear < 1.57) {
					trueBearing = 90;
				}
			} else if ((xDist < 0) && (zDist < 0)) {
				double bear = Math.atan(xDist / zDist);
				if (bear < 0.26) {
					trueBearing = 0;
				} else if (bear < .70) {
					trueBearing = 330;
				} else if (bear < .87) {
					trueBearing = 315;
				} else if (bear < 1.31) {
					trueBearing = 300;
				} else if (bear < 1.57) {
					trueBearing = 270;
				}
			} else if ((xDist >= 0) && (zDist > 0)) {
				double bear = Math.atan(xDist / zDist);
				if (bear < 0.26) {
					trueBearing = 180;
				} else if (bear < .70) {
					trueBearing = 150;
				} else if (bear < .87) {
					trueBearing = 135;
				} else if (bear < 1.31) {
					trueBearing = 120;
				} else if (bear < 1.57) {
					trueBearing = 90;
				}
			} else if ((xDist < 0) && (zDist > 0)) {
				double bear = Math.atan((-xDist) / zDist);
				if (bear < 0.26) {
					trueBearing = 180;
				} else if (bear < .70) {
					trueBearing = 210;
				} else if (bear < .87) {
					trueBearing = 225;
				} else if (bear < 1.31) {
					trueBearing = 240;
				} else if (bear < 1.57) {
					trueBearing = 270;
				}
			} else if ((zDist == 0) && (xDist < 0)) {
				trueBearing = 270;
			} else if ((zDist == 0) && (xDist > 0)) {
				trueBearing = 90;
			} else {
				trueBearing = 0;
			}



			int relBearing = (int) trueBearing - craft.rotation;
			if (relBearing < 0) {
				relBearing = relBearing + 360;
			}

			bearing = relBearing;
		return bearing;
	}

	
	
	public static boolean isAlpha(String s) {
		char[] charArr = s.toCharArray();

		for(char c : charArr) {
			if(!Character.isLetter(c) && !Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}
	public static int getSmallest(Object[] a){
		int temp;
		for (int i = 0; i < a.length; i++)
		{
			for (int j = i + 1; j < a.length; j++)
			{
				if (Integer.valueOf(a[i].toString()) > Integer.valueOf(a[j].toString()))
				{
					temp = Integer.valueOf(a[i].toString());
					a[i] = a[j];
					a[j] = temp;
				}
			}
		}
		return Integer.valueOf(a[0].toString());
	}
	/*
	public static void resetAll() {
		new Thread() {
			@Override
			public void run()
			{

				setPriority(Thread.MIN_PRIORITY);
				try
				{
		pex = (PermissionsEx)NavyCraft.instance.getServer().getPluginManager().getPlugin("PermissionsEx");
		for (PermissionUser pu : pex.getPermissionsManager().getUsers()) {
			PermissionGroup permissionGroup = pu.getRankLadderGroup("navycraft");
			pu.removeGroup(permissionGroup);
			pu.addGroup("Ensign-");
			System.out.println(pu.getName());
			pu.save();
			sleep(100);
		}
				} catch (InterruptedException e)
				{
				}
			}
		}.start();
	}
	public static void divideExp() {
		new Thread() {
			@Override
			public void run()
			{

				setPriority(Thread.MIN_PRIORITY);
				try
				{
		pex = (PermissionsEx)NavyCraft.instance.getServer().getPluginManager().getPlugin("PermissionsEx");
		for (PermissionUser pu : pex.getPermissionsManager().getUsers()) {
			String p = pu.getName();
			System.out.println("Dividing exp for: " + p);
			NavyCraft_FileListener.loadExperience(p);
			int newExp = NavyCraft.playerExp.get(p);
			System.out.println("Old EXP: " + newExp);
				if (newExp > 300000)
					newExp = Math.round(newExp/2);
				NavyCraft.playerExp.put(p, newExp);
				NavyCraft_FileListener.saveExperience(p);
				PermissionGroup permissionGroup = pu.getRankLadderGroup("navycraft");
				pu.removeGroup(permissionGroup);
			System.out.println("New EXP: " + newExp);
			sleep(250);
		}
				} catch (InterruptedException e)
				{
				}
			}
		}.start();
	}
	*/
}


