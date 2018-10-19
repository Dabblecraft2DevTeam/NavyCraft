package com.maximuspayne.navycraft;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
	
	private NavyCraft plugin = NavyCraft.getPlugin(NavyCraft.class);
	
	//files and configuration
	File shipyarddata = new File(plugin.getDataFolder(),File.separator + "shipyarddata");
	public static FileConfiguration syConfig;
	public static File syCFile;
	public static FileConfiguration syData;
	public static File syDFile;
	
	public void setupsyConfig() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		syCFile = new File(shipyarddata + File.separator , "config.yml");
		syConfig = YamlConfiguration.loadConfiguration(syCFile);
		
		if (!syCFile.exists()) {
			try {
				syConfig.set("Types.SHIP1.SZX", 13);
				syConfig.set("Types.SHIP1.SZY", 28);
				syConfig.set("Types.SHIP1.SZZ", 28);
				syConfig.set("Types.SHIP1.OX", 0);
				syConfig.set("Types.SHIP1.OY", -8);
				syConfig.set("Types.SHIP1.OZ", 0);
				syConfig.set("Types.SHIP1.BFR", 28);
				syConfig.set("Types.SHIP1.doFix", false);
				
				syConfig.set("Types.SHIP2.SZX", 9);
				syConfig.set("Types.SHIP2.SZY", 28);
				syConfig.set("Types.SHIP2.SZZ", 43);
				syConfig.set("Types.SHIP2.OX", 0);
				syConfig.set("Types.SHIP2.OY", -8);
				syConfig.set("Types.SHIP2.OZ", 0);
				syConfig.set("Types.SHIP2.BFR", 43);
				syConfig.set("Types.SHIP2.doFix", false);
				
				syConfig.set("Types.SHIP3.SZX", 11);
				syConfig.set("Types.SHIP3.SZY", 28);
				syConfig.set("Types.SHIP3.SZZ", 70);
				syConfig.set("Types.SHIP3.OX", 0);
				syConfig.set("Types.SHIP3.OY", -8);
				syConfig.set("Types.SHIP3.OZ", 0);
				syConfig.set("Types.SHIP3.BFR", 70);
				syConfig.set("Types.SHIP3.doFix", false);
				
				syConfig.set("Types.SHIP4.SZX", 17);
				syConfig.set("Types.SHIP4.SZY", 28);
				syConfig.set("Types.SHIP4.SZZ", 55);
				syConfig.set("Types.SHIP4.OX", 0);
				syConfig.set("Types.SHIP4.OY", -8);
				syConfig.set("Types.SHIP4.OZ", 0);
				syConfig.set("Types.SHIP4.BFR", 55);
				syConfig.set("Types.SHIP4.doFix", false);
				
				syConfig.set("Types.SHIP5.SZX", 17);
				syConfig.set("Types.SHIP5.SZY", 28);
				syConfig.set("Types.SHIP5.SZZ", 98);
				syConfig.set("Types.SHIP5.OX", 0);
				syConfig.set("Types.SHIP5.OY", -8);
				syConfig.set("Types.SHIP5.OZ", 0);
				syConfig.set("Types.SHIP5.BFR", 98);
				syConfig.set("Types.SHIP5.doFix", false);
				
				syConfig.set("Types.HANGAR1.SZX", 17);
				syConfig.set("Types.HANGAR1.SZY", 7);
				syConfig.set("Types.HANGAR1.SZZ", 19);
				syConfig.set("Types.HANGAR1.OX", 0);
				syConfig.set("Types.HANGAR1.OY", -1);
				syConfig.set("Types.HANGAR1.OZ", -18);
				syConfig.set("Types.HANGAR1.BFR", 17);
				syConfig.set("Types.HANGAR1.doFix", true);
				
				syConfig.set("Types.HANGAR2.SZX", 25);
				syConfig.set("Types.HANGAR2.SZY", 7);
				syConfig.set("Types.HANGAR2.SZZ", 32);
				syConfig.set("Types.HANGAR2.OX", 0);
				syConfig.set("Types.HANGAR2.OY", -1);
				syConfig.set("Types.HANGAR2.OZ", -31);
				syConfig.set("Types.HANGAR2.BFR", 25);
				syConfig.set("Types.HANGAR2.doFix", true);
				
				syConfig.set("Types.TANK1.SZX", 12);
				syConfig.set("Types.TANK1.SZY", 7);
				syConfig.set("Types.TANK1.SZZ", 19);
				syConfig.set("Types.TANK1.OX", 0);
				syConfig.set("Types.TANK1.OY", -1);
				syConfig.set("Types.TANK1.OZ", -18);
				syConfig.set("Types.TANK1.BFR", 12);
				syConfig.set("Types.TANK1.doFix", true);

				syConfig.set("Types.TANK2.SZX", 27);
				syConfig.set("Types.TANK2.SZY", 9);
				syConfig.set("Types.TANK2.SZZ", 33);
				syConfig.set("Types.TANK2.OX", 0);
				syConfig.set("Types.TANK2.OY", -1);
				syConfig.set("Types.TANK2.OZ", -32);
				syConfig.set("Types.TANK2.BFR", 27);
				syConfig.set("Types.TANK2.doFix", true);
				
				syConfig.set("Types.MAP1.SZX", 100);
				syConfig.set("Types.MAP1.SZY", 255);
				syConfig.set("Types.MAP1.SZZ", 100);
				syConfig.set("Types.MAP1.OX", 0);
				syConfig.set("Types.MAP1.OY", -63);
				syConfig.set("Types.MAP1.OZ", -99);
				syConfig.set("Types.MAP1.BFR", 100);
				syConfig.set("Types.MAP1.dontSelect", true);
				
				syConfig.set("Types.MAP2.SZX", 150);
				syConfig.set("Types.MAP2.SZY", 255);
				syConfig.set("Types.MAP2.SZZ", 150);
				syConfig.set("Types.MAP2.OX", 0);
				syConfig.set("Types.MAP2.OY", -63);
				syConfig.set("Types.MAP2.OZ", -149);
				syConfig.set("Types.MAP2.BFR", 150);
				syConfig.set("Types.MAP2.dontSelect", true);
				
				syConfig.set("Types.MAP3.SZX", 200);
				syConfig.set("Types.MAP3.SZY", 255);
				syConfig.set("Types.MAP3.SZZ", 200);
				syConfig.set("Types.MAP3.OX", 0);
				syConfig.set("Types.MAP3.OY", -63);
				syConfig.set("Types.MAP3.OZ", -199);
				syConfig.set("Types.MAP3.BFR", 200);
				syConfig.set("Types.MAP3.dontSelect", true);
				
				syConfig.set("Types.MAP4.SZX", 250);
				syConfig.set("Types.MAP4.SZY", 255);
				syConfig.set("Types.MAP4.SZZ", 250);
				syConfig.set("Types.MAP4.OX", 0);
				syConfig.set("Types.MAP4.OY", -63);
				syConfig.set("Types.MAP4.OZ", -249);
				syConfig.set("Types.MAP4.BFR", 250);
				syConfig.set("Types.MAP4.dontSelect", true);
				
				syConfig.set("Types.MAP5.SZX", 500);
				syConfig.set("Types.MAP5.SZY", 255);
				syConfig.set("Types.MAP5.SZZ", 250);
				syConfig.set("Types.MAP5.OX", 0);
				syConfig.set("Types.MAP5.OY", -63);
				syConfig.set("Types.MAP5.OZ", -499);
				syConfig.set("Types.MAP5.BFR", 500);
				syConfig.set("Types.MAP5.dontSelect", true);
				
				syConfig.save(syCFile);
			} catch(IOException e) {
				System.out.println("Could not create the shipyard config.yml file!");
			}
		}
	}
	
	public static FileConfiguration getsyConfig () {
		return syConfig;
	}
	
	public static void savesyConfig() {
		try {
			syConfig.save(syCFile);
		} catch (IOException e) {
			System.out.println("Could not save shipyard config.yml file");
		}
	}
	public static void reloadsyConfig() {
		syConfig = YamlConfiguration.loadConfiguration(syCFile);
	}
	
	
	
	public void setupsyData() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		syDFile = new File(shipyarddata + File.separator , "signs.yml");
		syData = YamlConfiguration.loadConfiguration(syDFile);
		
		if (!syDFile.exists()) {
			try {
				syData.createSection("Signs");
				syData.save(syDFile);
			} catch(IOException e) {
				System.out.println("Could not create the shipyard signs.yml file!");
			}
		}
	}
	
	public static FileConfiguration getsyData () {
		return syData;
	}
	
	public static void savesyData() {
		try {
			syData.save(syDFile);
		} catch (IOException e) {
			System.out.println("Could not save shipyard signs.yml file");
			e.printStackTrace();
		}
	}
	public static void reloadsyData() {
		syData = YamlConfiguration.loadConfiguration(syDFile);
	}

}
