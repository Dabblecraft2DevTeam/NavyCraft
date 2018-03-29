package com.maximuspayne.navycraft;
import org.bukkit.Location;

//I don't know why this needs its own class...
public class Pump {
	public Location loc;
	public int counter=0;
	public int limit=20;
	public boolean updated=false;

	public Pump(Location pumpLocIn)
	{
		loc = pumpLocIn;
	}
}
