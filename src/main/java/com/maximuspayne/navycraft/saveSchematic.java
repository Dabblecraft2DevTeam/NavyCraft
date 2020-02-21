package com.maximuspayne.navycraft;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;


public class saveSchematic extends BukkitRunnable
{
    String name;
    String nameString;
    ProtectedRegion region;
    World world;

    public saveSchematic(String n, String ns, ProtectedRegion r, World w) {
        name = n;
        nameString = ns;
        region = r;
        world = w;
    }
    @Override
    public void run()
    {
        Utils.saveSchem(name, nameString, region, world);
    }

}
