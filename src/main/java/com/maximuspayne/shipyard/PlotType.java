package com.maximuspayne.shipyard;

import com.maximuspayne.navycraft.ConfigManager;

public class PlotType {
	public int bfr;
	public int sizeX;
	public int sizeY;
	public int sizeZ;
	public int originX;
	public int originY;
	public int originZ;
	public int lengthZ;
	public int lengthX;
	public boolean doFix;
	public boolean dontSelect;
	public String name;
	
	public PlotType(String n)
	{
		name = n;
		sizeX = ConfigManager.getsyConfig().getInt("Types." + n + ".SZX");
		sizeY = ConfigManager.getsyConfig().getInt("Types." + n + ".SZY");
		sizeZ = ConfigManager.getsyConfig().getInt("Types." + n + ".SZZ");
		originX= ConfigManager.getsyConfig().getInt("Types." + n + ".OX");
		originY = ConfigManager.getsyConfig().getInt("Types." + n + ".OY");
		originZ = ConfigManager.getsyConfig().getInt("Types." + n + ".OZ");
		lengthX = ConfigManager.getsyConfig().getInt("Types." + n + ".LX");
		lengthZ = ConfigManager.getsyConfig().getInt("Types." + n + ".LZ");
		bfr = ConfigManager.getsyConfig().getInt("Types." + n + ".BFR");
		doFix = ConfigManager.getsyConfig().getBoolean("Types." + n + ".doFix");
		dontSelect = ConfigManager.getsyConfig().getBoolean("Types." + n + ".dontSelect");
	}
	
	public static void initialize() {
		Shipyard.plots.clear();
		for(String num : ConfigManager.getsyConfig().getConfigurationSection("Types").getKeys(false)){
			PlotType Plot = new PlotType(num);
			Shipyard.plots.add(Plot);
			System.out.println(Plot.name + " added to registry.");
		}
	}
}
