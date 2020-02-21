package com.maximuspayne.navycraft.listeners;


import com.maximuspayne.aimcannon.explosiveEgg;
import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Periscope;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.craft.Craft;
import com.maximuspayne.navycraft.craft.CraftMover;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class NavyCraft_EntityListener implements Listener {
    private static Plugin plugin;


    public NavyCraft_EntityListener(Plugin p) {
    	plugin = p;
    }

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockFall(EntityChangeBlockEvent event) {
		if ((event.getEntityType() == EntityType.FALLING_BLOCK)) {
			if (!NavyCraft.fallingBlocksList.contains(event.getEntity()))
				NavyCraft.fallingBlocksList.add((FallingBlock) event.getEntity());
		}

	}

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) 
    {
    	Entity ent = event.getEntity();

		if (event.isCancelled()) {
			return;
		}
		if (event.blockList().isEmpty()) {
			return;
		}
		event.setYield(0F);
		double x = 0;
		double y = 0;
		double z = 0;
		Location eLoc = event.getLocation();
		World w = eLoc.getWorld();
		for (int i = 0; i < event.blockList().size(); i++) {
			Block b = event.blockList().get(i);
			Location bLoc = b.getLocation();
			x = bLoc.getX() - eLoc.getX();
			y = bLoc.getY() - eLoc.getY() + .5;
			z = bLoc.getZ() - eLoc.getZ();
			FallingBlock fb = w.spawnFallingBlock(bLoc, b.getType(), b.getData());
			fb.setDropItem(false);
			fb.setVelocity(new Vector(x, y, z));
			if (!NavyCraft.fallingBlocksList.contains(fb))
				NavyCraft.fallingBlocksList.add(fb);
		}

    	if( (ent != null && ent instanceof TNTPrimed) )
    	{
    		if( event.getLocation() != null )
    		{
    			if( NavyCraft.shotTNTList.containsKey(ent.getUniqueId()) )
    			{
    				for (Block theBlock : event.blockList()) {
						int fuseDelay = 5;
						if (Craft.blockHardness(theBlock.getTypeId()) == -1) {
							theBlock.setType(Material.AIR);
							TNTPrimed tnt = (TNTPrimed) theBlock.getWorld().spawnEntity(new Location(theBlock.getWorld(), theBlock.getX(), theBlock.getY(), theBlock.getZ()), EntityType.PRIMED_TNT);
							tnt.setFuseTicks(fuseDelay);
							fuseDelay = fuseDelay + 2;
						} else if (Craft.blockHardness(theBlock.getTypeId()) == -2) {
							theBlock.setType(Material.AIR);
							TNTPrimed tnt = (TNTPrimed) theBlock.getWorld().spawnEntity(new Location(theBlock.getWorld(), theBlock.getX(), theBlock.getY(), theBlock.getZ()), EntityType.PRIMED_TNT);
							tnt.setFuseTicks(fuseDelay);
							tnt.setYield(tnt.getYield() * 0.5f);
							fuseDelay = fuseDelay + 2;
						}
					}
    				Craft checkCraft;
    				checkCraft = structureUpdate(event.getLocation(), NavyCraft.shotTNTList.get(ent.getUniqueId()));
    				if( checkCraft == null ) {
    					checkCraft = structureUpdate(event.getLocation().getBlock().getRelative(4,4,4).getLocation(), NavyCraft.shotTNTList.get(ent.getUniqueId()));
    					if( checkCraft == null ) {
    						checkCraft = structureUpdate(event.getLocation().getBlock().getRelative(-4,-4,-4).getLocation(), NavyCraft.shotTNTList.get(ent.getUniqueId()));
    						if( checkCraft == null ) {
    							checkCraft = structureUpdate(event.getLocation().getBlock().getRelative(2,-2,-2).getLocation(), NavyCraft.shotTNTList.get(ent.getUniqueId()));
    							if( checkCraft == null ) {
    								checkCraft = structureUpdate(event.getLocation().getBlock().getRelative(-2,2,2).getLocation(), NavyCraft.shotTNTList.get(ent.getUniqueId()));
    							}
    						}
    					}
    				}
    				NavyCraft.shotTNTList.remove(ent.getUniqueId());
    			}
    			else
    				structureUpdate(event.getLocation(), null);
    		}
    	}
 
    }
    
    
    public Craft structureUpdate(Location loc, Player causer)
    {
    	Craft testcraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if( testcraft != null )
		{
			CraftMover cm = new CraftMover(testcraft, plugin);
			cm.structureUpdate(causer,false);
			return testcraft;
		}
		return null;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {

    	if( Utils.CheckEnabledWorld(event.getEntity().getLocation()) )
    	{

    	}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityTarget(EntityTargetEvent event)
    {
		if (event.getEntity() instanceof Skeleton && (NavyCraft.aaSkelesList.contains(event.getEntity()) || NavyCraft.boforSkelesList.contains(event.getEntity()) || NavyCraft.ciwsSkelesList.contains(event.getEntity())))
    	{
    		if( event.getTarget() instanceof Player )
    		{
    			Player target = (Player)event.getTarget();
    			Craft skeleCraft = Craft.getCraft(target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ());
    			if( skeleCraft != null && !skeleCraft.crewNames.isEmpty() && skeleCraft.crewNames.contains(target.getName()) )
    			{
    				event.setCancelled(true);
    			}
    		}
    	}
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event)
    {
    	
    	//cancel if on periscope
    	if( event.getEntity() instanceof Player )
    	{
    		Player player = (Player)event.getEntity();


    		for( Periscope p: NavyCraft.allPeriscopes )
    		{
    			if( p.user == player )
    			{
    				event.setCancelled(true);
        			return;
    			}
    		}
    		
    		if( event.getCause() == DamageCause.SUFFOCATION )
    		{
    			Craft c = Craft.getCraft(player.getLocation().getBlockX(),player.getLocation().getBlockY(), player.getLocation().getBlockZ());
    			if( c != null )
    			{
    				event.setCancelled(true);
        			return;
    			}
    				
    		}
    		
    	}
    	
    	if ( event instanceof EntityDamageByEntityEvent ) 
    	{


    		Entity attacker = ((EntityDamageByEntityEvent) event).getDamager();
    		if( attacker instanceof Egg )
    		{
    			Egg egg = (Egg)attacker;
    			explosiveEgg exegg = null;
    			for (explosiveEgg e : NavyCraft.explosiveEggsList) {
    				if (e.egg.equals(egg)) {
    					exegg = e;
    				}
    			}
    			if (exegg != null) {
    				event.setDamage(Math.ceil(Math.abs(exegg.luck * 10)));
    		}
    	}
    }

}

	@EventHandler(priority = EventPriority.HIGH)
    public void vehicleSpawnEvent(VehicleCreateEvent e) {
    	if (e.getVehicle().getType() == EntityType.BOAT) {
    		System.out.println("bruh");
    		e.getVehicle().remove();
			e.setCancelled(true);
		}
    	return;
	}
}
