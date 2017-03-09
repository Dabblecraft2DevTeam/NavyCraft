package com.maximuspayne.navycraft;



import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.Plugin;

public class MoveCraft_EntityListener implements Listener {
    private static Plugin plugin;


    public MoveCraft_EntityListener(Plugin p) {
    	plugin = p;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) 
    {
    	Entity ent = event.getEntity();
    	if( (ent != null && ent instanceof TNTPrimed) )
    	{
    		if( event.getLocation() != null )
    		{
    			if( NavyCraft.shotTNTList.containsKey(ent.getUniqueId()) )
    			{
    				
    				if( !structureUpdate(event.getLocation(), NavyCraft.shotTNTList.get(ent.getUniqueId())) )
    					if( !structureUpdate(event.getLocation().getBlock().getRelative(4,4,4).getLocation(), NavyCraft.shotTNTList.get(ent.getUniqueId())) )
    						if( !structureUpdate(event.getLocation().getBlock().getRelative(-4,-4,-4).getLocation(), NavyCraft.shotTNTList.get(ent.getUniqueId())) )
    							if( !structureUpdate(event.getLocation().getBlock().getRelative(2,-1,-2).getLocation(), NavyCraft.shotTNTList.get(ent.getUniqueId())) )
    								structureUpdate(event.getLocation().getBlock().getRelative(-2,1,2).getLocation(), NavyCraft.shotTNTList.get(ent.getUniqueId()));
    				NavyCraft.shotTNTList.remove(ent.getUniqueId());
    			}
    			else
    				structureUpdate(event.getLocation(), null);
    		}
    	}
 
    }
    
    
    public boolean structureUpdate(Location loc, Player causer)
    {
    	Craft testcraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		if( testcraft != null )
		{
			CraftMover cm = new CraftMover(testcraft, plugin);
			cm.structureUpdate(causer,false);
			return true;
		}
		return false;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
    /*    if (event.getSpawnReason() == SpawnReason.EGG) {
            event.setCancelled(true);
        if (event.getSpawnReason() == SpawnReason.EGG) {
            event.setCancelled(false);*/
    	if( event.getEntity().getWorld().getName().equalsIgnoreCase("warworld1") )
    	{
    		/*if( event.getEntity().getWorld().getBiome(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ()) == Biome.OCEAN 
    				|| event.getEntity().getWorld().getBiome(event.getEntity().getLocation().getBlockX(), event.getEntity().getLocation().getBlockZ()) == Biome.FROZEN_OCEAN )
    		{
    			event.setCancelled(true);
    		}*/
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityTarget(EntityTargetEvent event)
    {
    	if( event.getEntity() instanceof Skeleton && NavyCraft.aaSkelesList.contains((Skeleton)event.getEntity()) )
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

    		/*Craft craft = Craft.getPlayerCraft(player);
    		
    		if( craft != null && craft.onScope == player )
    		{
    			event.setCancelled(true);
    			return;
    		}*/
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
    		
    		if( player.getWorld().getName().equalsIgnoreCase("warworld2") )
    		{
    			if ( event instanceof EntityDamageByEntityEvent ) 
    			{
    				EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent)event;
    				if( event2.getDamager() instanceof Player )
    				{
    					Player p = (Player)event2.getDamager();
    					
    					if( NavyCraft.redPlayers.contains(player.getName()) && NavyCraft.redPlayers.contains(p.getName()))
    	    			{
    						p.sendMessage("That player is on your team.");
    						event.setCancelled(true);
    						return;
    	    			}else if( NavyCraft.bluePlayers.contains(player.getName()) && NavyCraft.bluePlayers.contains(p.getName()))
    	    			{
    						p.sendMessage("That player is on your team.");
    						event.setCancelled(true);
    						return;
    	    			}
    				}
    			}
    			
    		}
    	}
    	
    	if ( event instanceof EntityDamageByEntityEvent ) 
    	{
    		//event.getEntity().getServer().getPlayer("Maximuspayne").sendMessage("entity dmg by entity");

    		Entity attacker = ((EntityDamageByEntityEvent) event).getDamager();
    		if( attacker instanceof Egg )
    		{
    			if( NavyCraft.explosiveEggsList.contains((Egg)attacker) )
    			{
    				//event.getEntity().getServer().getPlayer("Maximuspayne").sendMessage("dmg by egg");	
    				event.setDamage(5);
    			}
    		}
    	}

    }
    
}
