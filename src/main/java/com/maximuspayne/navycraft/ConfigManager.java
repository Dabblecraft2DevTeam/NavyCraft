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
	public static FileConfiguration costConfig;
	public static File costFile;
	public static FileConfiguration syData;
	public static File syDFile;
	public static FileConfiguration baseData;
	public static File baseFile;
	public static FileConfiguration routeData;
	public static File routeFile;
	
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
				syConfig.set("Types.SHIP1.LX", 18);
				syConfig.set("Types.SHIP1.LZ", 0);
				syConfig.set("Types.SHIP1.doFix", false);
				
				syConfig.set("Types.SHIP2.SZX", 9);
				syConfig.set("Types.SHIP2.SZY", 28);
				syConfig.set("Types.SHIP2.SZZ", 43);
				syConfig.set("Types.SHIP2.OX", 0);
				syConfig.set("Types.SHIP2.OY", -8);
				syConfig.set("Types.SHIP2.OZ", 0);
				syConfig.set("Types.SHIP2.BFR", 43);
				syConfig.set("Types.SHIP2.LX", 14);
				syConfig.set("Types.SHIP2.LZ", 0);
				syConfig.set("Types.SHIP2.doFix", false);
				
				syConfig.set("Types.SHIP3.SZX", 11);
				syConfig.set("Types.SHIP3.SZY", 28);
				syConfig.set("Types.SHIP3.SZZ", 70);
				syConfig.set("Types.SHIP3.OX", 0);
				syConfig.set("Types.SHIP3.OY", -8);
				syConfig.set("Types.SHIP3.OZ", 0);
				syConfig.set("Types.SHIP3.BFR", 70);
				syConfig.set("Types.SHIP3.LX", 16);
				syConfig.set("Types.SHIP3.LZ", 0);
				syConfig.set("Types.SHIP3.doFix", false);
				
				syConfig.set("Types.SHIP4.SZX", 17);
				syConfig.set("Types.SHIP4.SZY", 28);
				syConfig.set("Types.SHIP4.SZZ", 55);
				syConfig.set("Types.SHIP4.OX", 0);
				syConfig.set("Types.SHIP4.OY", -8);
				syConfig.set("Types.SHIP4.OZ", 0);
				syConfig.set("Types.SHIP4.BFR", 55);
				syConfig.set("Types.SHIP4.LX", 22);
				syConfig.set("Types.SHIP4.LZ", 0);
				syConfig.set("Types.SHIP4.doFix", false);
				
				syConfig.set("Types.SHIP5.SZX", 17);
				syConfig.set("Types.SHIP5.SZY", 28);
				syConfig.set("Types.SHIP5.SZZ", 98);
				syConfig.set("Types.SHIP5.OX", 0);
				syConfig.set("Types.SHIP5.OY", -8);
				syConfig.set("Types.SHIP5.OZ", 0);
				syConfig.set("Types.SHIP5.BFR", 98);
				syConfig.set("Types.SHIP5.LX", 22);
				syConfig.set("Types.SHIP5.LZ", 0);
				syConfig.set("Types.SHIP5.doFix", false);
				
				syConfig.set("Types.HANGAR1.SZX", 17);
				syConfig.set("Types.HANGAR1.SZY", 7);
				syConfig.set("Types.HANGAR1.SZZ", 19);
				syConfig.set("Types.HANGAR1.OX", 0);
				syConfig.set("Types.HANGAR1.OY", -1);
				syConfig.set("Types.HANGAR1.OZ", -18);
				syConfig.set("Types.HANGAR1.BFR", 17);
				syConfig.set("Types.HANGAR1.LX", -22);
				syConfig.set("Types.HANGAR1.LZ", 0);
				syConfig.set("Types.HANGAR1.doFix", true);
				
				syConfig.set("Types.HANGAR2.SZX", 25);
				syConfig.set("Types.HANGAR2.SZY", 7);
				syConfig.set("Types.HANGAR2.SZZ", 32);
				syConfig.set("Types.HANGAR2.OX", 0);
				syConfig.set("Types.HANGAR2.OY", -1);
				syConfig.set("Types.HANGAR2.OZ", -31);
				syConfig.set("Types.HANGAR2.BFR", 25);
				syConfig.set("Types.HANGAR2.LX", -29);
				syConfig.set("Types.HANGAR2.LZ", 0);
				syConfig.set("Types.HANGAR2.doFix", true);
				
				syConfig.set("Types.TANK1.SZX", 12);
				syConfig.set("Types.TANK1.SZY", 7);
				syConfig.set("Types.TANK1.SZZ", 19);
				syConfig.set("Types.TANK1.OX", 0);
				syConfig.set("Types.TANK1.OY", -1);
				syConfig.set("Types.TANK1.OZ", -18);
				syConfig.set("Types.TANK1.BFR", 12);
				syConfig.set("Types.TANK1.LX", -17);
				syConfig.set("Types.TANK1.LZ", 0);
				syConfig.set("Types.TANK1.doFix", true);

				syConfig.set("Types.TANK2.SZX", 27);
				syConfig.set("Types.TANK2.SZY", 9);
				syConfig.set("Types.TANK2.SZZ", 33);
				syConfig.set("Types.TANK2.OX", 0);
				syConfig.set("Types.TANK2.OY", -1);
				syConfig.set("Types.TANK2.OZ", -32);
				syConfig.set("Types.TANK2.BFR", 27);
				syConfig.set("Types.TANK2.LX", 0);
				syConfig.set("Types.TANK2.LZ", 0);
				syConfig.set("Types.TANK2.doFix", true);
				
				syConfig.set("Types.MAP1.SZX", 100);
				syConfig.set("Types.MAP1.SZY", 255);
				syConfig.set("Types.MAP1.SZZ", 100);
				syConfig.set("Types.MAP1.OX", 0);
				syConfig.set("Types.MAP1.OY", -63);
				syConfig.set("Types.MAP1.OZ", -99);
				syConfig.set("Types.MAP1.BFR", 100);
				syConfig.set("Types.MAP1.LX", 0);
				syConfig.set("Types.MAP1.LZ", 0);
				syConfig.set("Types.MAP1.dontSelect", true);
				
				syConfig.set("Types.MAP2.SZX", 150);
				syConfig.set("Types.MAP2.SZY", 255);
				syConfig.set("Types.MAP2.SZZ", 150);
				syConfig.set("Types.MAP2.OX", 0);
				syConfig.set("Types.MAP2.OY", -63);
				syConfig.set("Types.MAP2.OZ", -149);
				syConfig.set("Types.MAP2.BFR", 150);
				syConfig.set("Types.MAP2.LX", 0);
				syConfig.set("Types.MAP2.LZ", 0);
				syConfig.set("Types.MAP2.dontSelect", true);
				
				syConfig.set("Types.MAP3.SZX", 200);
				syConfig.set("Types.MAP3.SZY", 255);
				syConfig.set("Types.MAP3.SZZ", 200);
				syConfig.set("Types.MAP3.OX", 0);
				syConfig.set("Types.MAP3.OY", -63);
				syConfig.set("Types.MAP3.OZ", -199);
				syConfig.set("Types.MAP3.BFR", 200);
				syConfig.set("Types.MAP3.LX", 0);
				syConfig.set("Types.MAP3.LZ", 0);
				syConfig.set("Types.MAP3.dontSelect", true);
				
				syConfig.set("Types.MAP4.SZX", 250);
				syConfig.set("Types.MAP4.SZY", 255);
				syConfig.set("Types.MAP4.SZZ", 250);
				syConfig.set("Types.MAP4.OX", 0);
				syConfig.set("Types.MAP4.OY", -63);
				syConfig.set("Types.MAP4.OZ", -249);
				syConfig.set("Types.MAP4.BFR", 250);
				syConfig.set("Types.MAP4.LX", 0);
				syConfig.set("Types.MAP4.LZ", 0);
				syConfig.set("Types.MAP4.dontSelect", true);
				
				syConfig.set("Types.MAP5.SZX", 500);
				syConfig.set("Types.MAP5.SZY", 255);
				syConfig.set("Types.MAP5.SZZ", 250);
				syConfig.set("Types.MAP5.OX", 0);
				syConfig.set("Types.MAP5.OY", -63);
				syConfig.set("Types.MAP5.OZ", -499);
				syConfig.set("Types.MAP5.BFR", 500);
				syConfig.set("Types.MAP5.LX", 0);
				syConfig.set("Types.MAP5.LZ", 0);
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
	
	
	
	public void setupBaseData() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		baseFile = new File(shipyarddata + File.separator , "bases.yml");
		baseData = YamlConfiguration.loadConfiguration(baseFile);

		if (!baseFile.exists()) {
			try {
				baseData.createSection("Bases");
				baseData.save(baseFile);
			} catch(IOException e) {
				System.out.println("Could not create the faction bases.yml file!");
			}
		}
	}

	public static FileConfiguration getBaseData () {
		return baseData;
	}

	public static void saveBaseData() {
		try {
			baseData.save(baseFile);
		} catch (IOException e) {
			System.out.println("Could not save faction bases.yml file");
			e.printStackTrace();
		}
	}
	public static void reloadBaseData() {
		baseData = YamlConfiguration.loadConfiguration(baseFile);
	}

	public void setupRouteData() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		routeFile = new File(shipyarddata + File.separator , "routes.yml");
		routeData = YamlConfiguration.loadConfiguration(routeFile);

		if (!routeFile.exists()) {
			try {
				routeData.createSection("Routes");
				routeData.set("Routes.DDAIT.1.x", 18708);
				routeData.set("Routes.DDAIT.1.y", 64);
				routeData.set("Routes.DDAIT.1.z", 10442);
				routeData.save(routeFile);
			} catch(IOException e) {
				System.out.println("Could not create the routes.yml file!");
			}
		}
	}

	public static FileConfiguration getRouteData () {
		return routeData;
	}

	public static void saveRouteData() {
		try {
			routeData.save(routeFile);
		} catch (IOException e) {
			System.out.println("Could not save routes.yml file");
			e.printStackTrace();
		}
	}
	public static void reloadRouteData() {
		routeData = YamlConfiguration.loadConfiguration(routeFile);
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

	public void setupcostData() {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		costFile = new File(plugin.getDataFolder() + File.separator , "cost.yml");
		costConfig = YamlConfiguration.loadConfiguration(costFile);
		
		if (!costFile.exists()) {
			try {
				costConfig.createSection("Weapons");
				costConfig.set("Weapons.1barrel", 100);
				costConfig.set("Weapons.2barrel", 150);
				costConfig.set("Weapons.3barrel", 650);
				costConfig.set("Weapons.mk1torps", 150);
				costConfig.set("Weapons.mk2torps", 350);
				costConfig.set("Weapons.mk3torps", 650);
				costConfig.set("Weapons.mk1dc", 350);
				costConfig.set("Weapons.mk2dc", 650);
				costConfig.set("Weapons.mk1bomb", 150);
				costConfig.set("Weapons.mk2bomb", 350);
				costConfig.set("Weapons.mk1missiles", 450);
				costConfig.set("Weapons.mk2missiles", 650);
				costConfig.set("Weapons.mk1vmissiles", 350);
				costConfig.set("Weapons.mk2vmissiles", 550);
				
				costConfig.createSection("Signs");
				costConfig.set("Signs.helm", 50);
				costConfig.set("Signs.nav", 50);
				costConfig.set("Signs.periscope", 100);
				costConfig.set("Signs.aa-gun", 100);
				costConfig.set("Signs.bofors", 450);
				costConfig.set("Signs.ciws", 650);
				costConfig.set("Signs.radar", 200);
				costConfig.set("Signs.turbo", 500);
				costConfig.set("Signs.radio", 50);
				costConfig.set("Signs.detector", 50);
				costConfig.set("Signs.sonar", 250);
				costConfig.set("Signs.hydrophone", 100);
				costConfig.set("Signs.subdrive", 50);
				costConfig.set("Signs.tdc", 400);
				costConfig.set("Signs.firecontrol", 500);
				costConfig.set("Signs.passivesonar", 650);
				costConfig.set("Signs.activesonar", 650);
				costConfig.set("Signs.hfsonar", 650);
				
				costConfig.createSection("Engines");
				costConfig.set("Engines.diesel1", 100);
				costConfig.set("Engines.motor1", 150);
				costConfig.set("Engines.diesel2", 250);
				costConfig.set("Engines.boiler1", 250);
				costConfig.set("Engines.diesel3", 500);
				costConfig.set("Engines.gasoline1", 50);
				costConfig.set("Engines.boiler2", 600);
				costConfig.set("Engines.boiler3", 850);
				costConfig.set("Engines.gasoline2", 100);
				costConfig.set("Engines.nuclear", 1000);
				costConfig.set("Engines.airplane1", 50);
				costConfig.set("Engines.airplane2", 80);
				costConfig.set("Engines.airplane3", 120);
				costConfig.set("Engines.airplane4", 160);
				costConfig.set("Engines.airplane7", 500);
				costConfig.set("Engines.airplane5", 400);
				costConfig.set("Engines.airplane6", 500);
				costConfig.set("Engines.airplane8", 850);
				costConfig.set("Engines.tank1", 50);
				costConfig.set("Engines.tank2", 250);
				costConfig.save(costFile);
			} catch(IOException e) {
				System.out.println("Could not create the cost.yml file!");
			}
		}
	}
	
	public static FileConfiguration getcostData () {
		return costConfig;
	}
	
	public static void savecostConfig() {
		try {
			costConfig.save(costFile);
		} catch (IOException e) {
			System.out.println("Could not save cost.yml file");
			e.printStackTrace();
		}
	}
	public static void reloadcostConfig() {
		costConfig = YamlConfiguration.loadConfiguration(costFile);
	}
	
}
