package com.maximuspayne.navycraft.craft;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;

/*
 * NavyCraft plugin by Maximuspayne
 *
 * You are free to modify it for your own server
 * or use part of the code for your own plugins.
 */
public class CraftType {
	
	public static FileConfiguration CraftConfig;
	public static File CraftFile;
	
	
	public String name = "";
	public String driveCommand = "drive";

	public int minBlocks = 9;
	public int maxBlocks = 500;
	public int maxSpeed = 4;
	public int discount = 0;
	
	public boolean adminBuild=false;

	public int digBlockId = 0;		//the type of block needed to make the vehicle able to drill through terrain
	public double digBlockPercent = 0;
	public int digBlockDurability = 0;
	
	public int HelmControllerItem = 0;

	public boolean canFly = false;
	public boolean canNavigate = false;
	public boolean canDive = false;
	public boolean canDig = false;
	public boolean obeysGravity = false;
	public boolean isTerrestrial = false;
	
	
	public boolean doesCruise = false;
	public boolean canZamboni = false;
	public int maxEngineSpeed = 4;
	public int maxForwardGear = 2;
	public int maxReverseGear = -2;
	public int turnRadius = 4;
	public int maxSurfaceSpeed = 4;
	public int maxSubmergedSpeed = 3;

	public short[] structureBlocks = null; // blocks that can make the structure of the craft
	public short[] extendedBlocks = null;		//structureblocks only for this craft type 
	public short[] restrictedBlocks = null;	//structureblocks to be exlcuded from this craft type 
	public short[] forbiddenBlocks = null;		//blocks that are not allowed whatsoever on this craft

	public static ArrayList<CraftType> craftTypes = new ArrayList<CraftType>();
	
	public boolean listenItem = true;
	public boolean listenAnimation, listenMovement = false;

	public CraftType(String name) {
		this.name = name;
		
		String[] bob = NavyCraft.instance.getConfig().getString("StructureBlocks").split(",");
		short[] juan = new short[bob.length + 1];
		for(int i = 0; i < bob.length; i++)
			juan[i] = Short.parseShort(bob[i]);
		structureBlocks = juan;
		
		if(NavyCraft.instance.getConfig().getString("ForbiddenBlocks") != "null") {
			bob = NavyCraft.instance.getConfig().getString("ForbiddenBlocks").split(",");
			juan = new short[bob.length];
			for(int i = 0; i < bob.length; i++) {
				try {
					juan[i] = Short.parseShort(bob[i]);
				}
				catch (Exception ex){
				}
			}
			if(juan != null && juan.length > 0 && juan[0] != 0)
				forbiddenBlocks = juan;
		}
	}

	public static CraftType getCraftType(String name) {

		for (CraftType type : craftTypes) {
			if (type.name.equalsIgnoreCase(name))
				return type;
		}

		return null;
	}

	public String getCommand() {
		return "/" + name.toLowerCase();
	}

	public Boolean canUse(Player player){
		if(PermissionInterface.CheckPerm(player, "navycraft." + name.toLowerCase()))
			return true;
		else
			return false;
	}
	// set the attributes of the craft type
	private static void setAttribute(CraftType craftType, String attribute,
			String value) {

		if (attribute.equalsIgnoreCase("driveCommand"))
			craftType.driveCommand = value;
		else if (attribute.equalsIgnoreCase("minBlocks"))
			craftType.minBlocks = Integer.parseInt(value);
		else if (attribute.equalsIgnoreCase("maxBlocks"))
			craftType.maxBlocks = Integer.parseInt(value);
		else if (attribute.equalsIgnoreCase("maxSpeed"))
			craftType.maxSpeed = Integer.parseInt(value);
		else if (attribute.equalsIgnoreCase("discount"))
			craftType.discount = Integer.parseInt(value);
		else if (attribute.equalsIgnoreCase("adminBuild"))
			craftType.adminBuild = Boolean.parseBoolean(value);
		else if (attribute.equalsIgnoreCase("digBlockId"))
			craftType.digBlockId = Integer.parseInt(value);
		else if (attribute.equalsIgnoreCase("digBlockDurability"))
			craftType.digBlockDurability = Integer.parseInt(value);
		else if (attribute.equalsIgnoreCase("canNavigate"))
			craftType.canNavigate = Boolean.parseBoolean(value);
		else if (attribute.equalsIgnoreCase("isTerrestrial"))
			craftType.isTerrestrial = Boolean.parseBoolean(value);
		else if (attribute.equalsIgnoreCase("canFly"))
			craftType.canFly = Boolean.parseBoolean(value);
		else if (attribute.equalsIgnoreCase("canDive"))
			craftType.canDive = Boolean.parseBoolean(value);
		else if (attribute.equalsIgnoreCase("canDig"))
			craftType.canDig = Boolean.parseBoolean(value);
		else if (attribute.equalsIgnoreCase("canZamboni"))
			craftType.canZamboni = Boolean.parseBoolean(value);
		else if (attribute.equalsIgnoreCase("obeysGravity"))
			craftType.obeysGravity = Boolean.parseBoolean(value);
		
		
		else if (attribute.equalsIgnoreCase("doesCruise"))
			craftType.doesCruise = Boolean.parseBoolean(value);
		
		else if (attribute.equalsIgnoreCase("maxEngineSpeed"))
		{
			craftType.maxEngineSpeed = Integer.parseInt(value);
			craftType.maxSurfaceSpeed = Integer.parseInt(value);
		}
		else if (attribute.equalsIgnoreCase("maxForwardGear"))
			craftType.maxForwardGear = Integer.parseInt(value);
		else if (attribute.equalsIgnoreCase("maxReverseGear"))
			craftType.maxReverseGear = Integer.parseInt(value);
		else if (attribute.equalsIgnoreCase("turnRadius"))
			craftType.turnRadius = Integer.parseInt(value);
		else if (attribute.equalsIgnoreCase("maxSubmergedSpeed"))
			craftType.maxSubmergedSpeed = Integer.parseInt(value);	
		// else if(attribute.equalsIgnoreCase("iceBreaker"))
		// craftType.iceBreaker = Boolean.parseBoolean(value);
		else if (attribute.equalsIgnoreCase("HelmControllerItem"))
			craftType.HelmControllerItem = Integer.parseInt(value);
		else if (attribute.equalsIgnoreCase("listenItem"))
			craftType.listenItem = Boolean.parseBoolean(value);
		else if (attribute.equalsIgnoreCase("listenAnimation"))
			craftType.listenAnimation = Boolean.parseBoolean(value);
		else if (attribute.equalsIgnoreCase("listenMovement")){
			craftType.listenMovement = Boolean.parseBoolean(value);	
		}
		else if (attribute.equalsIgnoreCase("structureBlocks")) {
			String[] split = value.split(",");
			craftType.structureBlocks = new short[split.length];
			int i = 0;
			for (String blockId : split) {
				craftType.structureBlocks[i] = Short.parseShort(blockId);
				i++;
			}
		} else if (attribute.equalsIgnoreCase("restrictedBlocks")) {
			if(craftType.structureBlocks == null)
				return;
			
			ArrayList<Short> restrictedBlocks = new ArrayList<Short>();
			ArrayList<Short> newStructureBlocks = new ArrayList<Short>();

			String[] split = value.split(",");
			
			for(String s : split){
				try
				{
					restrictedBlocks.add(Short.parseShort(s));
				}
				catch (NumberFormatException ex) {
					System.out.println("Tried to remove invalid block ID " + s + 
							" from structureblocks of craft type " + craftType.name);
				}
			}
			for(Short i: craftType.structureBlocks)
				if(!restrictedBlocks.contains(i))
					newStructureBlocks.add(i);
			
			Short nsb[] = new Short[newStructureBlocks.size()];
			//craftType.structureBlocks = newStructureBlocks.toArray(short[]);
			newStructureBlocks.toArray(nsb);
			//I give up.
			//craftType.structureBlocks = nsb;
			
		} else if (attribute.equalsIgnoreCase("extendedBlocks")) {
			if(craftType.structureBlocks == null)
				return;
			
			String[] split = value.split(",");
			short[] newStructureBlocks = new short[craftType.structureBlocks.length + split.length];
			
			for(int i = 0; i < craftType.structureBlocks.length; i++) {
				newStructureBlocks[i] = craftType.structureBlocks[i];
			}
			
			int i = 0;
			for(String s : split) {
				try
				{
					newStructureBlocks[craftType.structureBlocks.length + i] = Short.parseShort(s);
				}
				catch (NumberFormatException ex) {
					System.out.println("Tried to add invalid block ID " + s + 
							" to structureblocks of craft type " + craftType.name);					
				}				
			}
			craftType.structureBlocks = newStructureBlocks;
		} else if (attribute.equalsIgnoreCase("forbiddenBlocks")) {			
			String[] split = value.split(",");
			craftType.forbiddenBlocks = new short[split.length];
			for (int i = 0; i < split.length; i++) {
				craftType.forbiddenBlocks[i] = Short.parseShort(split[i]);
			}			
		}
	}

	
	public static void setupCraftConfig() {
		if (!NavyCraft.instance.getDataFolder().exists()) {
			NavyCraft.instance.getDataFolder().mkdir();
		}
		CraftFile = new File(NavyCraft.instance.getDataFolder() + File.separator , "types.yml");
		CraftConfig = YamlConfiguration.loadConfiguration(CraftFile);
		
		if (!CraftFile.exists()) {
			try {
				CraftConfig.createSection("Types");
				//Ship
				CraftConfig.set("Types.ship.structureBlocks", "4,5,14,15,16,17,19,20,21,22,23,24,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,164,165,166,167,168,169,170,172,173,174,179,183,184,185,186,187,188,189,190,191,192,251,0");
				CraftConfig.set("Types.ship.forbiddenBlocks", "29,33,416");
				CraftConfig.set("Types.ship.driveCommand", "sail");
				CraftConfig.set("Types.ship.canNavigate", "true");
				CraftConfig.set("Types.ship.minBlocks", "20");
				CraftConfig.set("Types.ship.maxBlocks", "50000");
				CraftConfig.set("Types.ship.maxSpeed", "6");
				CraftConfig.set("Types.ship.doesCruise", "true");
				CraftConfig.set("Types.ship.maxEngineSpeed", "8");
				CraftConfig.set("Types.ship.maxForwardGear", "2");
				CraftConfig.set("Types.ship.maxReverseGear", "-2");
				//Freeship
				CraftConfig.set("Types.freeship.structureBlocks", "4,5,14,15,16,17,19,20,21,22,23,24,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,164,165,166,167,168,169,170,172,173,174,179,183,184,185,186,187,188,189,190,191,192,251,0");
				CraftConfig.set("Types.freeship.forbiddenBlocks", "29,33,416");
				CraftConfig.set("Types.freeship.driveCommand", "sail");
				CraftConfig.set("Types.freeship.canNavigate", "true");
				CraftConfig.set("Types.freeship.minBlocks", "20");
				CraftConfig.set("Types.freeship.maxBlocks", "50000");
				CraftConfig.set("Types.freeship.maxSpeed", "6");
				CraftConfig.set("Types.freeship.doesCruise", "true");
				CraftConfig.set("Types.freeship.maxEngineSpeed", "8");
				CraftConfig.set("Types.freeship.maxForwardGear", "2");
				CraftConfig.set("Types.freeship.maxReverseGear", "-2");
				CraftConfig.set("Types.freeship.discount", "100");
				CraftConfig.set("Types.freeship.adminBuild", "true");
				//Submarine
				CraftConfig.set("Types.submarine.structureBlocks", "4,5,14,15,16,17,19,20,21,22,23,24,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,164,165,166,167,168,169,170,172,173,174,179,183,184,185,186,187,188,189,190,191,192,251,0");
				CraftConfig.set("Types.submarine.forbiddenBlocks", "29,33,416");
				CraftConfig.set("Types.submarine.driveCommand", "dive");
				CraftConfig.set("Types.submarine.canDive", "true");
				CraftConfig.set("Types.submarine.minBlocks", "20");
				CraftConfig.set("Types.submarine.maxBlocks", "50000");
				CraftConfig.set("Types.submarine.maxSpeed", "3");
				CraftConfig.set("Types.submarine.doesCruise", "true");
				CraftConfig.set("Types.submarine.maxEngineSpeed", "6");
				CraftConfig.set("Types.submarine.maxSubmergedSpeed", "3");
				CraftConfig.set("Types.submarine.maxForwardGear", "2");
				CraftConfig.set("Types.submarine.maxReverseGear", "-2");
				//Aircraft
				CraftConfig.set("Types.aircraft.structureBlocks", "4,5,14,15,16,17,20,21,22,23,24,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,164,165,166,167,168,169,170,172,173,174,179,183,184,185,186,187,188,189,190,191,192,0");
				CraftConfig.set("Types.aircraft.forbiddenBlocks", "29,33,251,416");
				CraftConfig.set("Types.aircraft.driveCommand", "pilot");
				CraftConfig.set("Types.aircraft.canFly", "true");
				CraftConfig.set("Types.aircraft.minBlocks", "20");
				CraftConfig.set("Types.aircraft.maxBlocks", "20000");
				CraftConfig.set("Types.aircraft.maxSpeed", "20");
				CraftConfig.set("Types.aircraft.doesCruise", "true");
				CraftConfig.set("Types.aircraft.maxEngineSpeed", "8");
				CraftConfig.set("Types.aircraft.maxForwardGear", "3");
				CraftConfig.set("Types.aircraft.maxReverseGear", "-2");
				
				CraftConfig.set("Types.helicopter.structureBlocks", "4,5,14,15,16,17,20,21,22,23,24,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,164,165,166,167,168,169,170,172,173,174,183,184,185,186,187,188,189,190,191,192,0");
				CraftConfig.set("Types.helicopter.forbiddenBlocks", "29,33,251,416");
				CraftConfig.set("Types.helicopter.driveCommand", "pilot");
				CraftConfig.set("Types.helicopter.canFly", "true");
				CraftConfig.set("Types.helicopter.minBlocks", "20");
				CraftConfig.set("Types.helicopter.maxBlocks", "20000");
				CraftConfig.set("Types.helicopter.maxSpeed", "5");
				CraftConfig.set("Types.helicopter.doesCruise", "false");
				CraftConfig.set("Types.helicopter.maxForwardGear", "3");
				CraftConfig.set("Types.helicopter.maxReverseGear", "-2");
				
				CraftConfig.set("Types.tank.structureBlocks", "4,5,14,15,16,17,20,21,22,23,24,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,164,165,166,167,168,169,170,172,173,174,179,183,184,185,186,187,188,189,190,191,192,251,0");
				CraftConfig.set("Types.tank.forbiddenBlocks", "29,33,416");
				CraftConfig.set("Types.tank.driveCommand", "drive");
				CraftConfig.set("Types.tank.canNavigate", "false");
				CraftConfig.set("Types.tank.isTerrestrial", "true");
				CraftConfig.set("Types.tank.obeysGravity", "true");
				CraftConfig.set("Types.tank.minBlocks", "20");
				CraftConfig.set("Types.tank.maxBlocks", "10000");
				CraftConfig.set("Types.tank.maxSpeed", "3");
				CraftConfig.set("Types.tank.doesCruise", "true");
				CraftConfig.set("Types.tank.maxEngineSpeed", "5");
				CraftConfig.set("Types.tank.maxForwardGear", "3");
				CraftConfig.set("Types.tank.maxReverseGear", "-2");

				CraftConfig.save(CraftFile);
			} catch(IOException e) {
				System.out.println("Could not create the types.yml file!");
			}
		}
		loadTypes();
	}
	
	public static FileConfiguration getCraftConfig () {
		return CraftConfig;
	}
	
	public static void saveCraftConfig() {
		try {
			CraftConfig.save(CraftFile);
		} catch (IOException e) {
			System.out.println("Could not save types.yml file");
		}
	}
	public static void reloadCraftConfig() {
		CraftConfig = YamlConfiguration.loadConfiguration(CraftFile);
	}

	public static void loadTypes() {
		List<String> list = new ArrayList<String>(CraftConfig.getConfigurationSection("Types").getKeys(false));
		int size = list.size();
		craftTypes.clear();

		if (size == 0) return;
		for (String name : list) {
			System.out.println("Found Type: " + name);
				CraftType craftType = new CraftType(name);
				
				craftType.HelmControllerItem = Integer.parseInt(NavyCraft.instance.getConfig().getString("HelmID"));

					List<String> info = new ArrayList<String>(CraftConfig.getConfigurationSection("Types." + name).getKeys(false));
					int s = info.size();

					if (s == 0) return;
					
					for (String attribute : info) {
							setAttribute(craftType, attribute, CraftConfig.getString("Types." + name + "." + attribute));
					}

				craftTypes.add(craftType);
		}
	}
}
