package com.maximuspayne.aimcannon;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.craft.Craft;




public class AimCannon{
	public static List<OneCannon> cannons = new ArrayList<OneCannon>();
	public static List<Weapon> weapons = new ArrayList<Weapon>();
	
	public static List<OneCannon> getCannons() {
		return cannons;
	}

	public static List<Weapon> getWeapons() {
		return weapons;
	}
}
