package com.maximuspayne.aimcannon;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Utils;

import net.md_5.bungee.api.ChatColor;

public class AimCannonPlayerListener implements Listener {
    public static AimCannon plugin;
    
    public static void onPlayerInteract(PlayerInteractEvent event) {
    	
    	if (event.getHand() != EquipmentSlot.HAND)
    		return;
    	
    ///////stone button
	if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK ) {
	    if (event.getClickedBlock().getType() == Material.STONE_BUTTON) {
		Block b = null;
		if (event.getClickedBlock().getRelative(BlockFace.NORTH_EAST).getType() == Material.DISPENSER) {
		    b = event.getClickedBlock().getRelative(BlockFace.NORTH_EAST);
		}
		if (event.getClickedBlock().getRelative(BlockFace.NORTH_WEST).getType() == Material.DISPENSER) {
		    b = event.getClickedBlock().getRelative(BlockFace.NORTH_WEST);
		}
		if (event.getClickedBlock().getRelative(BlockFace.SOUTH_EAST).getType() == Material.DISPENSER) {
		    b = event.getClickedBlock().getRelative(BlockFace.SOUTH_EAST);
		}
		if (event.getClickedBlock().getRelative(BlockFace.SOUTH_WEST).getType() == Material.DISPENSER) {
		    b = event.getClickedBlock().getRelative(BlockFace.SOUTH_WEST);
		}
		
		if (b != null)
		{
		    for (OneCannon onec : AimCannon.getCannons()) 
		    {
				if (onec.isThisCannon(b.getLocation(), false, false)) 
				{
					if( onec.cannonType == 2 )
					{
						if( event.getAction() == Action.LEFT_CLICK_BLOCK )
							onec.fireCannonButton(event.getPlayer(), true);
						else
							onec.fireCannonButton(event.getPlayer(), false);
					}else if( onec.cannonType == 4 || onec.cannonType == 5 || onec.cannonType == 9 || onec.cannonType == 10)
					{
						if( event.getAction() == Action.LEFT_CLICK_BLOCK )
							onec.fireDCButton(event.getPlayer(), true);
						else
							onec.fireDCButton(event.getPlayer(), false);
					}
					else if(event.getAction() == Action.LEFT_CLICK_BLOCK)
					{
					    if (onec.isCharged() ) {
						onec.Action(event.getPlayer());
					    } else {
						event.getPlayer().sendMessage("Load Cannon first.. (left click Dispenser)");
					    }
					}else
					{
						onec.setDelay(event.getPlayer());
					}
				}
			}
		   ////else not gun, maybe torpedo or missile?
		}else
		{
			if( event.getClickedBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN).getType() == Material.DISPENSER ) 
			{
				b = event.getClickedBlock().getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN);
			}else if( event.getClickedBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN).getType() == Material.DISPENSER ) 
			{
				b = event.getClickedBlock().getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN);
			}else if( event.getClickedBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN).getType() == Material.DISPENSER ) 
			{
				b = event.getClickedBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN);
			}else if( event.getClickedBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).getType() == Material.DISPENSER ) 
			{
				b = event.getClickedBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN);
			}else if( event.getClickedBlock().getRelative(BlockFace.NORTH).getType() == Material.DISPENSER ) 
			{
				b = event.getClickedBlock().getRelative(BlockFace.NORTH);
			}else if( event.getClickedBlock().getRelative(BlockFace.SOUTH).getType() == Material.DISPENSER ) 
			{
				b = event.getClickedBlock().getRelative(BlockFace.SOUTH);
			}else if( event.getClickedBlock().getRelative(BlockFace.EAST).getType() == Material.DISPENSER ) 
			{
				b = event.getClickedBlock().getRelative(BlockFace.EAST);
			}else if( event.getClickedBlock().getRelative(BlockFace.WEST).getType() == Material.DISPENSER ) 
			{
				b = event.getClickedBlock().getRelative(BlockFace.WEST);
			}
			
			if( b != null )
			{
				for (OneCannon onec : AimCannon.getCannons()) 
				{
					if (onec.isThisCannon(b.getLocation(), false, false) && ( onec.cannonType == 3 || onec.cannonType == 7 || onec.cannonType == 8))
					{
						if( event.getAction() == Action.LEFT_CLICK_BLOCK )
							onec.fireTorpedoButton(event.getPlayer());
						else
							onec.setTorpedoMode(event.getPlayer());
					} else if (onec.isThisCannon(b.getLocation(), false, false) && (onec.cannonType == 11 || onec.cannonType == 12 )) {
							if( event.getAction() == Action.LEFT_CLICK_BLOCK )
							onec.fireMissileButton(event.getPlayer(), false);
							else
							onec.setMissileMode(event.getPlayer());
					}
			}
		} else {
			if( event.getClickedBlock().getRelative(BlockFace.NORTH).getType() == Material.DROPPER ) 
		{
			b = event.getClickedBlock().getRelative(BlockFace.NORTH);
		}else if( event.getClickedBlock().getRelative(BlockFace.SOUTH).getType() == Material.DROPPER ) 
		{
			b = event.getClickedBlock().getRelative(BlockFace.SOUTH);
		}else if( event.getClickedBlock().getRelative(BlockFace.EAST).getType() == Material.DROPPER ) 
		{
			b = event.getClickedBlock().getRelative(BlockFace.EAST);
		}else if( event.getClickedBlock().getRelative(BlockFace.WEST).getType() == Material.DROPPER ) 
		{
			b = event.getClickedBlock().getRelative(BlockFace.WEST);
		}
			if( b != null )
			{
				for (OneCannon onec : AimCannon.getCannons()) 
				{
				if (onec.isThisCannon(b.getLocation(), false, true) && (onec.cannonType == 13 || onec.cannonType == 14 )) {
					if( event.getAction() == Action.LEFT_CLICK_BLOCK )
					onec.fireMissileButton(event.getPlayer(), true);
					else
					onec.setMissileRange(event.getPlayer());
				}
			}
		}
	}
}
	}
	    //////Levers
	    if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK ) {
		    if (event.getClickedBlock().getType() == Material.LEVER) {
			Block b = null;
			
			////cannon levers
			if (event.getClickedBlock().getRelative(BlockFace.NORTH_EAST).getType() == Material.DISPENSER) {
			    b = event.getClickedBlock().getRelative(BlockFace.NORTH_EAST);
			}
			if (event.getClickedBlock().getRelative(BlockFace.NORTH_WEST).getType() == Material.DISPENSER) {
			    b = event.getClickedBlock().getRelative(BlockFace.NORTH_WEST);
			}
			if (event.getClickedBlock().getRelative(BlockFace.SOUTH_EAST).getType() == Material.DISPENSER) {
			    b = event.getClickedBlock().getRelative(BlockFace.SOUTH_EAST);
			}
			if (event.getClickedBlock().getRelative(BlockFace.SOUTH_WEST).getType() == Material.DISPENSER) {
			    b = event.getClickedBlock().getRelative(BlockFace.SOUTH_WEST);
			}
			
			//if no gun lever, then check torpedo levers
			if( b == null )
			{
				////torpedo levers
				//left load lever
				//north
				int torpedoAction = 0;  //1 left load lever, 2 right load lever, 3 outer door lever, 4 right click button,5 left click button, 6 left inner door lever, 7 right inner door lever
				if (event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH,2).getType() == Material.DISPENSER) 
				{
					b = event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH,2);
					torpedoAction = 1;
				}//south
				if (event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH,2).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH,2);
					torpedoAction = 1;
				}//east
				if (event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST,2).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST,2);
					torpedoAction = 1;
				}//west
				if (event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST,2).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST,2);
					torpedoAction = 1;
				}
				//right load lever
				//north
				if (event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH,2).getType() == Material.DISPENSER) 
				{
					b = event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH,2);
					torpedoAction = 2;
				}//south
				if (event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH,2).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH,2);
					torpedoAction = 2;
				}//east
				if (event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST,2).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST,2);
					torpedoAction = 2;
				}//west
				if (event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST,2).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST,2);
					torpedoAction = 2;
				}
				
				
				//left inner door lever
				//north
				if (event.getClickedBlock().getRelative(BlockFace.EAST,2).getRelative(BlockFace.NORTH,3).getType() == Material.DISPENSER) 
				{
					b = event.getClickedBlock().getRelative(BlockFace.EAST,2).getRelative(BlockFace.NORTH,3);
					torpedoAction = 6;
				}//south
				if (event.getClickedBlock().getRelative(BlockFace.WEST,2).getRelative(BlockFace.SOUTH,3).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.WEST,2).getRelative(BlockFace.SOUTH,3);
					torpedoAction = 6;
				}//east
				if (event.getClickedBlock().getRelative(BlockFace.SOUTH,2).getRelative(BlockFace.EAST,3).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.SOUTH,2).getRelative(BlockFace.EAST,3);
					torpedoAction = 6;
				}//west
				if (event.getClickedBlock().getRelative(BlockFace.NORTH,2).getRelative(BlockFace.WEST,3).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.NORTH,2).getRelative(BlockFace.WEST,3);
					torpedoAction = 6;
				}
				//right inner door lever
				//north
				if (event.getClickedBlock().getRelative(BlockFace.WEST,2).getRelative(BlockFace.NORTH,3).getType() == Material.DISPENSER) 
				{
					b = event.getClickedBlock().getRelative(BlockFace.WEST,2).getRelative(BlockFace.NORTH,3);
					torpedoAction = 7;
				}//south
				if (event.getClickedBlock().getRelative(BlockFace.EAST,2).getRelative(BlockFace.SOUTH,3).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.EAST,2).getRelative(BlockFace.SOUTH,3);
					torpedoAction = 7;
				}//east
				if (event.getClickedBlock().getRelative(BlockFace.NORTH,2).getRelative(BlockFace.EAST,3).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.NORTH,2).getRelative(BlockFace.EAST,3);
					torpedoAction = 7;
				}//west
				if (event.getClickedBlock().getRelative(BlockFace.SOUTH,2).getRelative(BlockFace.WEST,3).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.SOUTH,2).getRelative(BlockFace.WEST,3);
					torpedoAction = 7;
				}
				
				
				//outer door lever
				//north
				if (event.getClickedBlock().getRelative(BlockFace.NORTH,1).getType() == Material.DISPENSER) 
				{
					b = event.getClickedBlock().getRelative(BlockFace.NORTH,1);
					torpedoAction = 3;
				}//south
				if (event.getClickedBlock().getRelative(BlockFace.SOUTH,1).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.SOUTH,1);
					torpedoAction = 3;
				}//east
				if (event.getClickedBlock().getRelative(BlockFace.EAST,1).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.EAST,1);
					torpedoAction = 3;
				}//west
				if (event.getClickedBlock().getRelative(BlockFace.WEST,1).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.WEST,1);
					torpedoAction = 3;
				}
				
				//outer missile door lever
				//north
				if (event.getClickedBlock().getRelative(BlockFace.NORTH,1).getRelative(BlockFace.DOWN, 2).getType() == Material.DROPPER) 
				{
					b = event.getClickedBlock().getRelative(BlockFace.NORTH,1).getRelative(BlockFace.DOWN, 2);
					torpedoAction = 10;
				}//south
				if (event.getClickedBlock().getRelative(BlockFace.SOUTH,1).getRelative(BlockFace.DOWN, 2).getType() == Material.DROPPER) {
					b = event.getClickedBlock().getRelative(BlockFace.SOUTH,1).getRelative(BlockFace.DOWN, 2);
					torpedoAction = 10;
				}//east
				if (event.getClickedBlock().getRelative(BlockFace.EAST,1).getRelative(BlockFace.DOWN, 2).getType() == Material.DROPPER) {
					b = event.getClickedBlock().getRelative(BlockFace.EAST,1).getRelative(BlockFace.DOWN, 2);
					torpedoAction = 10;
				}//west
				if (event.getClickedBlock().getRelative(BlockFace.WEST,1).getRelative(BlockFace.DOWN, 2).getType() == Material.DROPPER) {
					b = event.getClickedBlock().getRelative(BlockFace.WEST,1).getRelative(BlockFace.DOWN, 2);
					torpedoAction = 10;
				}
				
				//rotate lever
				//north
				if (event.getClickedBlock().getRelative(BlockFace.NORTH,1).getRelative(BlockFace.DOWN,1).getType() == Material.DISPENSER) 
				{
					b = event.getClickedBlock().getRelative(BlockFace.NORTH,1).getRelative(BlockFace.DOWN,1);
					torpedoAction = 11;
				}//south
				if (event.getClickedBlock().getRelative(BlockFace.SOUTH,1).getRelative(BlockFace.DOWN,1).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.SOUTH,1).getRelative(BlockFace.DOWN,1);
					torpedoAction = 11;
				}//east
				if (event.getClickedBlock().getRelative(BlockFace.EAST,1).getRelative(BlockFace.DOWN,1).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.EAST,1).getRelative(BlockFace.DOWN,1);
					torpedoAction = 11;
				}//west
				if (event.getClickedBlock().getRelative(BlockFace.WEST,1).getRelative(BlockFace.DOWN,1).getType() == Material.DISPENSER) {
					b = event.getClickedBlock().getRelative(BlockFace.WEST,1).getRelative(BlockFace.DOWN,1);
					torpedoAction = 11;
				}
				
				if( b != null )
				{
				for (OneCannon onec : AimCannon.getCannons()) {
					if (onec.isThisCannon(b.getLocation(), false, true) || onec.isThisCannon(b.getLocation(), false, false)) {
						//Do torpedo action
						if( torpedoAction > 0 )
						{
							if( torpedoAction == 1 )
							{
								onec.loadTorpedoLever(true, event.getPlayer());
							}else if( torpedoAction == 2 )
							{
								onec.loadTorpedoLever(false, event.getPlayer());
							}else if( torpedoAction == 3 )
							{
								onec.openTorpedoDoors(event.getPlayer(), false, false);
							}else if( torpedoAction == 6 )
							{
								onec.openTorpedoDoors(event.getPlayer(), true, true);
							}else if( torpedoAction == 7 )
							{
								onec.openTorpedoDoors(event.getPlayer(), true, false);
							}else if( torpedoAction == 10 )
							{
								onec.openMissileDoorsV(event.getPlayer());
							}else if( torpedoAction == 11 )
							{
							    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
								    onec.turnCannon(true,event.getPlayer()); 
								}  else  {
								    onec.turnCannon(false,event.getPlayer());
								}
							    event.getPlayer().sendMessage("Cannon turned..");
							}
						}
					}
				}
				}
				
			}else { //b != null
				if (Utils.CheckEnabledWorld(event.getPlayer().getLocation())) {
			    for (OneCannon onec : AimCannon.getCannons()) {
				if (onec.isThisCannon(b.getLocation(), false, false)) {
				    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					    onec.turnCannon(true,event.getPlayer()); 
					}  else  {
					    onec.turnCannon(false,event.getPlayer());
					}
				    event.getPlayer().sendMessage("Cannon turned..");
				    event.setCancelled(true);
				    return;
				}
			    }
			} else {
				event.getPlayer().sendMessage(ChatColor.RED + "Can't turn cannons outside of the main world!");
			}
		    }
		    }
	    }

	    ///Dispenser
	    if (event.getClickedBlock().getType() == Material.DISPENSER || event.getClickedBlock().getType() == Material.DROPPER) {
		for (OneCannon onec : AimCannon.getCannons()) 
		{
		    if (onec.isThisCannon(event.getClickedBlock().getLocation(), false, false) || onec.isThisCannon(event.getClickedBlock().getLocation(), false, true)) {
		    	if (Utils.CheckEnabledWorld(event.getClickedBlock().getLocation()) && !NavyCraft.checkSafeDockRegion(event.getClickedBlock().getLocation())) {
		    	if( event.getAction() == Action.LEFT_CLICK_BLOCK )
		    		onec.Charge(event.getPlayer(), true);
				else
					onec.Charge(event.getPlayer(), false);
			    
			    return;
		    } else {
		    	event.getPlayer().sendMessage(ChatColor.RED + "You can't load cannons inside safedock!");
		    	return;
		    }
		}
	}
		
		// new Cannon
		OneCannon oc = new OneCannon(event.getClickedBlock().getLocation(), NavyCraft.instance);
		if ((oc.isValidCannon(event.getClickedBlock(), false) && event.getClickedBlock().getType() == Material.DISPENSER) || (oc.isValidCannon(event.getClickedBlock(), true) && event.getClickedBlock().getType() == Material.DROPPER)) {
			if (Utils.CheckEnabledWorld(event.getClickedBlock().getLocation()) && !NavyCraft.checkSafeDockRegion(event.getClickedBlock().getLocation())) {
			if( event.getAction() == Action.LEFT_CLICK_BLOCK )
				oc.Charge(event.getPlayer(), true);
			else
				oc.Charge(event.getPlayer(), false);
		    AimCannon.cannons.add(oc);
		    } else {
		    	event.getPlayer().sendMessage(ChatColor.RED + "You can't load cannons inside safedock!");
		    	return;
		    }
		}
	    }
	    
	    
	}
    }
    

	@SuppressWarnings("deprecation")
	public static void onBlockDispense(BlockDispenseEvent event) {
    	if( event.getBlock() != null && (event.getBlock().getTypeId() == 23 ||  event.getBlock().getTypeId() == 158))
    	{
	    	for (OneCannon onec : AimCannon.getCannons()) 
			{
			    if (onec.isThisCannon(event.getBlock().getLocation(), true, false)) 
			    {
			    	event.setCancelled(true);
			    	return;
			    }
			}
    	}
    }
}
