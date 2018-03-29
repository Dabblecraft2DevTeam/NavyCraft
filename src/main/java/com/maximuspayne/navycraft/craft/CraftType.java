package com.maximuspayne.navycraft.craft;

import java.io.*;
import java.util.ArrayList;

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

	private static void loadDefaultCraftTypes() {
		// if the default craft types are not loaded (first execution), then
		// load them
		if (CraftType.getCraftType("boat") == null)
			craftTypes.add(CraftType.getDefaultCraftType("boat"));
		if (CraftType.getCraftType("ship") == null)
			craftTypes.add(CraftType.getDefaultCraftType("ship"));
		if (CraftType.getCraftType("freeship") == null)
			craftTypes.add(CraftType.getDefaultCraftType("freeship"));
		if (CraftType.getCraftType("halfship") == null)
			craftTypes.add(CraftType.getDefaultCraftType("halfship"));
		if (CraftType.getCraftType("aircraft") == null)
			craftTypes.add(CraftType.getDefaultCraftType("aircraft"));
		if (CraftType.getCraftType("airship") == null)
			craftTypes.add(CraftType.getDefaultCraftType("airship"));
		if (CraftType.getCraftType("submarine") == null)
			craftTypes.add(CraftType.getDefaultCraftType("submarine"));
		if (CraftType.getCraftType("tank") == null)
			craftTypes.add(CraftType.getDefaultCraftType("tank"));
	}

	private static CraftType getDefaultCraftType(String name) {

		CraftType craftType = new CraftType(name);
		
		if (name.equalsIgnoreCase("ship")) {
			setAttribute(
					craftType,
					"structureBlocks",
					"4,5,14,15,16,17,19,20,21,22,23,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,"
					+ "57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,"
					+ "92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,"
					+ "133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,"
					+ "164,165,166,167,168,169,170,172,173,174,183,184,185,186,187,188,189,190,191,192,0");
			
			craftType.driveCommand = "sail";
			craftType.canNavigate = true;
			craftType.minBlocks = 50;
			craftType.maxBlocks = 18000;
			craftType.maxSpeed = 6;
			craftType.doesCruise = true;
			craftType.maxEngineSpeed = 8;
			craftType.maxForwardGear = 2;
			
		} else if (name.equalsIgnoreCase("freeship")) {
			setAttribute(
					craftType,
					"structureBlocks",
					"4,5,14,15,16,17,19,20,21,22,23,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,"
					+ "57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,"
					+ "92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,"
					+ "133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,"
					+ "164,165,166,167,168,169,170,172,173,174,183,184,185,186,187,188,189,190,191,192,0");
			
			craftType.driveCommand = "sail";
			craftType.canNavigate = true;
			craftType.minBlocks = 50;
			craftType.maxBlocks = 3000;
			craftType.maxSpeed = 6;
			craftType.doesCruise = true;
			craftType.maxEngineSpeed = 8;
			craftType.maxForwardGear = 3;
			craftType.discount = 100;
			craftType.adminBuild = true;
			
		} else if (name.equalsIgnoreCase("aircraft")) {
			setAttribute(
					craftType,
					"structureBlocks",
					"4,5,14,15,16,17,20,21,22,23,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,"
					+ "57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,"
					+ "92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,"
					+ "133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,160,162,163,"
					+ "164,165,166,167,168,169,170,172,173,174,183,184,185,186,187,188,189,190,191,192,0");
			craftType.driveCommand = "pilot";
			craftType.canFly = true;
			craftType.minBlocks = 20;
			craftType.maxBlocks = 18000;
			craftType.maxSpeed = 20;
			craftType.doesCruise = true;
			craftType.maxEngineSpeed = 8;
			craftType.maxForwardGear = 3;
			
		} else if (name.equalsIgnoreCase("helicopter")) {
			setAttribute(
					craftType,
					"structureBlocks",
					"4,5,14,15,16,17,20,21,22,23,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,"
					+ "57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,"
					+ "92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,"
					+ "133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,160,162,163,"
					+ "164,165,166,167,168,169,170,172,173,174,183,184,185,186,187,188,189,190,191,192,0");
			craftType.driveCommand = "pilot";
			craftType.canFly = true;
			craftType.minBlocks = 9;
			craftType.maxBlocks = 18000;
			craftType.maxSpeed = 5;
			craftType.doesCruise = false;
			craftType.maxForwardGear = 3;
			
		} else if (name.equalsIgnoreCase("submarine")) {
			setAttribute(
					craftType,
					"structureBlocks",
					"4,5,14,15,16,17,19,20,21,22,23,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,"
					+ "57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,"
					+ "92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,"
					+ "133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,"
					+ "164,165,166,167,168,169,170,172,173,174,183,184,185,186,187,188,189,190,191,192,0");
			
			craftType.driveCommand = "dive";
			craftType.canDive = true;
			craftType.minBlocks = 20;
			craftType.maxBlocks = 18000;
			craftType.maxSpeed = 3;
			craftType.doesCruise = true;
			craftType.maxEngineSpeed = 6;
			craftType.maxSubmergedSpeed = 3;
			craftType.maxForwardGear = 2;
		} else if (name.equalsIgnoreCase("tank")) {
		setAttribute(
				craftType,
				"structureBlocks",
				"4,5,14,15,16,17,20,21,22,23,25,26,27,28,30,35,41,42,43,44,45,46,47,48,49,50,51,53,54,55,56,"
				+ "57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,79,80,81,82,84,85,86,87,88,89,91,"
				+ "92,93,94,95,96,98,101,102,106,107,108,109,112,113,114,118,121,123,124,125,126,129,131,132,"
				+ "133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,155,156,157,158,159,160,162,163,"
				+ "164,165,166,167,168,169,170,172,173,174,183,184,185,186,187,188,189,190,191,192,0");
		
		craftType.driveCommand = "drive";
		craftType.canNavigate = false;
		craftType.isTerrestrial = true;
		craftType.obeysGravity = true;
		craftType.minBlocks = 10;
		craftType.maxBlocks = 2000;
		craftType.maxSpeed = 3;
		craftType.doesCruise = true;
		craftType.maxEngineSpeed = 4;
		craftType.maxForwardGear = 3;
		
	}

		return craftType;
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

	public static void saveType(File dir, CraftType craftType, boolean force) {		
		File craftFile = new File(dir + File.separator
				+ craftType.name + ".txt");

		if (!craftFile.exists()) {
			try {
				craftFile.createNewFile();
			} catch (IOException ex) {
				return;
			}
		} else
			// we don't overwrite existing files
			return;

		try {
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(craftFile));

			writeAttribute(writer, "driveCommand", craftType.driveCommand,
					force);
			writeAttribute(writer, "minBlocks", craftType.minBlocks, true);
			writeAttribute(writer, "maxBlocks", craftType.maxBlocks, force);

			// list of blocks that make the structure of the craft
			if (craftType.structureBlocks != null) {
				String line = "structureBlocks=";
				for (short blockId : craftType.structureBlocks) {

					line += blockId + ",";
				}

				writer.write(line.substring(0, line.length() - 1));
				writer.newLine();
			}
			
			writeAttribute(writer, "maxSpeed", craftType.maxSpeed, force);
			writeAttribute(writer, "discount", craftType.discount, force);
			writeAttribute(writer, "adminBuild", craftType.adminBuild, force);
			writeAttribute(writer, "digBlockId", craftType.digBlockId, force);
			writeAttribute(writer, "digBlockDurability", craftType.digBlockDurability, force);
			writeAttribute(writer, "canNavigate", craftType.canNavigate, force);
			writeAttribute(writer, "isTerrestrial", craftType.isTerrestrial, force);
			writeAttribute(writer, "canFly", craftType.canFly, force);
			writeAttribute(writer, "canDive", craftType.canDive, force);
			writeAttribute(writer, "canDig", craftType.canDig, force);
			writeAttribute(writer, "obeysGravity", craftType.obeysGravity, force);
			// writeAttribute(writer, "iceBreaker", craftType.iceBreaker);
			writeAttribute(writer, "doesCruise", craftType.doesCruise, force);
			writeAttribute(writer, "maxEngineSpeed", craftType.maxEngineSpeed, force);
			writeAttribute(writer, "maxSubmergedSpeed", craftType.maxSubmergedSpeed, force);
			writeAttribute(writer, "maxForwardGear", craftType.maxForwardGear, force);
			writeAttribute(writer, "maxReverseGear", craftType.maxReverseGear, force);

			writer.close();

		} catch (IOException ex) {
		}
	}

	public static void saveTypes(File dir) {		
		for (CraftType craftType : craftTypes) {
			saveType(dir, craftType, false);
		}

		// the template is just a file that shows all parameters
		saveType(dir, getDefaultCraftType("template"), true);

	}

	private static void writeAttribute(BufferedWriter writer, String attribute,
			String value, boolean force) throws IOException {
		if ((value == null || value.trim().equals("")) && !force)
			return;
		writer.write(attribute + "=" + value);
		writer.newLine();
	}

	private static void writeAttribute(BufferedWriter writer, String attribute,
			int value, boolean force) throws IOException {
		if (value == 0 && !force)
			return;
		writer.write(attribute + "=" + value);
		writer.newLine();
	}

	private static void writeAttribute(BufferedWriter writer, String attribute,
			boolean value, boolean force) throws IOException {
		if (!value && !force)
			return;
		writer.write(attribute + "=" + value);
		writer.newLine();
	}

	public static void loadTypes(File dir) {
		File[] craftTypesList = dir.listFiles();
		craftTypes.clear();

		for (File craftFile : craftTypesList) {

			if (craftFile.isFile() && craftFile.getName().endsWith(".txt")) {

				String craftName = craftFile.getName().split("\\.")[0];

				// skip the template file
				if (craftName.equalsIgnoreCase("template"))
					continue;

				CraftType craftType = new CraftType(craftName);
				
				craftType.HelmControllerItem = Integer.parseInt(NavyCraft.instance.getConfig().getString("HelmID"));

				try {
					BufferedReader reader = new BufferedReader(new FileReader(
							craftFile));

					String line;
					while ((line = reader.readLine()) != null) {

						String[] split;
						split = line.split("=");

						if (split.length >= 2)
							setAttribute(craftType, split[0], split[1]);
					}

					reader.close();

				} catch (IOException ex) {
			
					System.out.println("Warning, craft type " + craftType.name + " has an invalid engine block ID. " + 
							"Please use a block which has a facing direction (default is furnace, ID 61).");
				}

				craftTypes.add(craftType);
			}
		}

		if(NavyCraft.instance.getConfig().getString("WriteDefaultCraft").equalsIgnoreCase("true"))
			loadDefaultCraftTypes();
	}
}
