package com.maximuspayne.aimcannon;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.craft.CraftMover;
@SuppressWarnings("deprecation")
public class boforFire extends BukkitRunnable
{
   private Player player;

   public boforFire(Player p)
   {
       player = p;
   }

   @Override
   public void run()
   {
		Egg newEgg = player.launchProjectile(Egg.class);
		newEgg.setVelocity(newEgg.getVelocity().multiply(2.5f));
		NavyCraft.explosiveEggsList.add(new explosiveEgg(newEgg, 1.8));
		player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
		CraftMover.playWeaponSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5.0f,1.70f);	
   }

public static void fireBofor(Player player)
   {
	new Thread() {
	    @Override
	    public void run()
	    {
	    	
		setPriority(Thread.MIN_PRIORITY);
			try
			{
				while(NavyCraft.boforFiringList.contains(player))
				{
			        NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, new boforFire(player));
					sleep(200);
				}
				
			} catch (InterruptedException e) 
			{
			}
	    }
	}.start();
  }
}