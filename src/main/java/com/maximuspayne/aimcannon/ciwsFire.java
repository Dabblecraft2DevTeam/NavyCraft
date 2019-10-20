package com.maximuspayne.aimcannon;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.craft.CraftMover;
@SuppressWarnings("deprecation")
public class ciwsFire extends BukkitRunnable
{
   private Player player;

   public ciwsFire(Player p)
   {
       player = p;
   }

   @Override
   public void run()
   {
		Egg newEgg = player.launchProjectile(Egg.class);
		newEgg.setVelocity(newEgg.getVelocity().multiply(2.0f));
		NavyCraft.explosiveEggsList.add(new explosiveEgg(newEgg, -1.5));
		player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
		CraftMover.playWeaponSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5.0f,1.70f);	
   }

public static void fireCIWS(Player player)
   {
	new Thread() {
	    @Override
	    public void run()
	    {
	    	
		setPriority(Thread.MIN_PRIORITY);
			try
			{
				while(NavyCraft.ciwsFiringList.contains(player))
				{
					int radius = 50;
					long radiusSquared = radius * radius;
					Location newLoc = player.getLocation();
					boolean change = false;
					
					for (Weapon torp: AimCannon.weapons)
					{
						if (!torp.dead) {
							Location torpLoc = torp.warhead.getLocation();
							if (torpLoc.getWorld() != player.getWorld())
							{
								continue;
							}

							long delta = (long)torpLoc.distanceSquared(player.getLocation());
							if (delta < radiusSquared)
							{
								double dX = torpLoc.getX() - player.getLocation().getX();
								double dY = torpLoc.getY() - player.getLocation().getY();
								double dZ = torpLoc.getZ() - player.getLocation().getZ();
								
								Vector playerLookDirection = new Vector(dX + 0.5, dY - 0.5, dZ + 0.5);
								
								change = true;
								newLoc.setDirection(playerLookDirection.normalize());
								break;
							}
						} else {
							change = false;
						}
					}
					if (change)
					player.teleport(newLoc);
					
					newLoc = player.getLocation();
					change = false;
					
			        NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, new ciwsFire(player));
					sleep(50);
				}
				
			} catch (InterruptedException e) 
			{
			}
	    }
	}.start();
  }
}