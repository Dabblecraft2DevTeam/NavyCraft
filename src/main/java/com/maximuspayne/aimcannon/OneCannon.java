package com.maximuspayne.aimcannon;

import com.earth2me.essentials.Essentials;
import com.maximuspayne.navycraft.ConfigManager;
import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Periscope;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.blocks.BlocksInfo;
import com.maximuspayne.navycraft.craft.Craft;
import com.maximuspayne.navycraft.craft.CraftMover;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.util.*;

@SuppressWarnings("deprecation")
public class OneCannon{
	public WorldGuardPlugin wgp;
	public Craft ownerCraft;
	NavyCraft nc;
    BlockFace direction;
    boolean isCannon;
    double timeout;
    int last = -1;
    int charged;
    boolean ignite;
    int cannonLength;
    Vector olook;
    public HashMap<Craft, Integer> targetCraft = new HashMap<>();
    public int cannonType; //0 single, 1 double, 2 fireball (crap), 3 torpedo mk2, 4 depth charge, 5 depth charger II, 6 triple cannon, 7 torp mk3, 8 torp mk1, 9 bombs mk1, 10 bombs mk2, 11 missile mk1, 12 missile mk2, 13 vmissile mk1, 14 vmissile mk2, 15 flares, 20 nuke
    int delay;
    int mode = 0;
    int range;
    int torpedoMode;
    int missileMode;
    boolean explode = false;
	boolean remove = false;
    int depth;
    boolean leftLoading, rightLoading;
    public Location loc;
    TNTPrimed tntp;
    TNTPrimed tntp2;
    TNTPrimed tntp3;
    int turnCount = 0;
    int ammunition=-1;
    int initAmmo = 0;
    int cannonTurnCounter=0;
    double tnt1X=0;
    double tnt1Z=0;
    double tnt2X=0;
    double tnt2Z=0;
    double tnt3X=0;
    double tnt3Z=0;
    
    
    volatile boolean stopFall0 = false; //center depth charge
    volatile boolean stopFallM1 = false; // -1 direction dc
    volatile boolean stopFall1 = false;// +1 direction dc
    volatile boolean stopFall2 = false; //single depth charge
    
    
    public OneCannon(Location inloc, NavyCraft inplugin) {
		delay = 1000;
		timeout = 0;
		charged = 0;
		range = 10;
		torpedoMode = 0;
		depth = 0;
		leftLoading = false;
		rightLoading = false;
		loc = inloc;
		nc = inplugin;
		wgp = (WorldGuardPlugin) nc.getServer().getPluginManager().getPlugin("WorldGuard");
		ownerCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

    public void setLocation(Location inloc)
    {
    	loc = inloc;
    	ownerCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    public boolean isThisCannon(Location inloc, boolean skipValidation) {
	if (inloc.getBlockX() == loc.getBlockX()
		&& inloc.getBlockY() == loc.getBlockY()
		&& inloc.getBlockZ() == loc.getBlockZ()) {
		if( skipValidation || isValidCannon(loc.getBlock())) {
			NavyCraft.instance.DebugMessage("Was a cannon", 3);
			return true;
		} else {
			NavyCraft.instance.DebugMessage("Wasn't a cannon", 3);
			return false;
		}
	} else {
		NavyCraft.instance.DebugMessage("Wasn't a cannon", 3);
	    return false;
	}
    }

    public int getCannonLength() {
	return cannonLength;
    }

    public boolean isCharged() {
	return charged > 0;
    }

    public boolean isTimeout() {
		return timeout + 4000 < new Date().getTime();

    }

    public boolean isIgnite() {
	return ignite;
    }

    public void Ignite(final Player p) {

    	if (isIgnite()) {
    		if (p != null)
            p.sendMessage(ChatColor.RED + "You have to wait for the Cannon to cool down!");
            return;
        }
    	
	    Block b;

	    if( cannonType == 6 )
	    	b = loc.getBlock().getRelative(BlockFace.UP).getRelative(direction);
	    else
	    	b = loc.getBlock().getRelative(BlockFace.UP);
	    
	    if( cannonType == 0 )
	    {
		    
		    if (b.getType() == Material.AIR) 
		    {

		    	tntp = (TNTPrimed)b.getWorld().spawnEntity(new Location(b.getWorld(), b.getX()+0.5, b.getY()+0.5, b.getZ()+0.5), EntityType.PRIMED_TNT);
				NavyCraft.shotTNTList.put(tntp.getUniqueId(), p);
				ignite = true;
		    }
	    }else if( cannonType == 1 )
	    {
	    	if (b.getType() == Material.AIR) 
		    {
	    		final Block c;
	    		final Block d;
		    	if( direction == BlockFace.NORTH )
		    	{
				    c = b.getRelative(BlockFace.WEST);
				    d = b.getRelative(BlockFace.EAST);
		    	}else if( direction == BlockFace.SOUTH )
		    	{
				    c = b.getRelative(BlockFace.WEST);
				    d = b.getRelative(BlockFace.EAST);
		    	}else if( direction == BlockFace.WEST )
		    	{
				    c = b.getRelative(BlockFace.SOUTH);
				    d = b.getRelative(BlockFace.NORTH);
		    	}else //east
		    	{
				    c = b.getRelative(BlockFace.NORTH);
				    d = b.getRelative(BlockFace.SOUTH);
		    	}

		    		tntp = (TNTPrimed)c.getWorld().spawnEntity(new Location(c.getWorld(), c.getX()+0.5, c.getY()+0.5, c.getZ()+0.5), EntityType.PRIMED_TNT);

		    		tntp2 = (TNTPrimed)d.getWorld().spawnEntity(new Location(d.getWorld(), d.getX()+0.5, d.getY()+0.5, d.getZ()+0.5), EntityType.PRIMED_TNT);
		    		NavyCraft.shotTNTList.put(tntp.getUniqueId(), p);
					NavyCraft.shotTNTList.put(tntp2.getUniqueId(), p);
					ignite = true;
			   }
	    
	    }else if( cannonType == 6 )
	    {

	    	if (b.getType() == Material.AIR) 
		    {
	    		final Block c;
	    		final Block d;
		    	if( direction == BlockFace.NORTH )
		    	{
				    c = b.getRelative(BlockFace.WEST,2);
				    d = b.getRelative(BlockFace.EAST,2);
		    	}else if( direction == BlockFace.SOUTH )
		    	{
				    c = b.getRelative(BlockFace.WEST,2);
				    d = b.getRelative(BlockFace.EAST,2);
		    	}else if( direction == BlockFace.WEST )
		    	{
				    c = b.getRelative(BlockFace.SOUTH,2);
				    d = b.getRelative(BlockFace.NORTH,2);
		    	}else //east
		    	{
				    c = b.getRelative(BlockFace.NORTH,2);
				    d = b.getRelative(BlockFace.SOUTH,2);
		    	}
			    
		    		tntp = (TNTPrimed)c.getWorld().spawnEntity(new Location(c.getWorld(), c.getX()+0.5, c.getY()+0.5, c.getZ()+0.5), EntityType.PRIMED_TNT);
		    		tntp2 = (TNTPrimed)d.getWorld().spawnEntity(new Location(d.getWorld(), d.getX()+0.5, d.getY()+0.5, d.getZ()+0.5), EntityType.PRIMED_TNT);

		    		tntp3 = (TNTPrimed)b.getWorld().spawnEntity(new Location(b.getWorld(), b.getX()+0.5, b.getY()+0.5, b.getZ()+0.5), EntityType.PRIMED_TNT);
		    		
		    		NavyCraft.shotTNTList.put(tntp.getUniqueId(), p);
		    		NavyCraft.shotTNTList.put(tntp2.getUniqueId(), p);
		    		NavyCraft.shotTNTList.put(tntp3.getUniqueId(), p);
					ignite = true;
			   }
	    
	    }
	    if( charged == 1 && delay == 2000 )
	    	delay = 1500;

	    if( ignite )
	    {
	    	fireThreadNew(delay, p);
			if (p != null)
	    	p.sendMessage(ChatColor.RED + "3 - Ready!");
	    }
    }
    
    public void fireThreadNew(int delay, final Player p)
    {
    	new Thread() {
    		public void run() {
    			try {
    				explode = false;
					remove = false;
					last = -1;
    				setPriority(Thread.MIN_PRIORITY);
					sleep(delay);
					fire1(p);
					sleep(1000);
					fire2(p);
					sleep(500);
					fire3(p);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}.start();
    }
    
    public void fire1(final Player p) {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
	  //  @Override
	    public void run()
	    {
			if (p != null) p.sendMessage(ChatColor.YELLOW + "2 - Aim!");
	    }
	    });
    }
    
    public void fire2(final Player p) {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
	  //  @Override
	    public void run()
	    {
			if (p != null)
	    	p.sendMessage(ChatColor.GREEN + "1 - Fire!!!");
	    }
	    });
    }
    
    public void fire3(final Player p) {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
	  //  @Override
	    public void run()
	    {
	    	Vector look;
	    	if (olook != null)
	    		look = olook;
	    	else
		    look = p.getLocation().getDirection();
		    if (direction == BlockFace.WEST) {
			if (look.getX() > -0.5)
			    look.setX(-0.5);
			if (look.getY() < 0.05)
				look.setY(0.05);
			if (look.getZ() > 0.5)
			    look.setZ(0.5);
			if (look.getZ() < -0.5)
			    look.setZ(-0.5);
		    }
		    if (direction == BlockFace.NORTH) {
			if (look.getZ() > -0.5)
			    look.setZ(-0.5);
			if (look.getY() < 0.05)
				look.setY(0.05);
			if (look.getX() > 0.5)
			    look.setX(0.5);
			if (look.getX() < -0.5)
			    look.setX(-0.5);
		    }
		    if (direction == BlockFace.EAST) {
			if (look.getX() < 0.5)
			    look.setX(0.5);
			if (look.getY() < 0.05)
				look.setY(0.05);
			if (look.getZ() > 0.5)
			    look.setZ(0.5);
			if (look.getZ() < -0.5)
			    look.setZ(-0.5);
		    }
		    if (direction == BlockFace.SOUTH) {
			if (look.getZ() < 0.5)
			    look.setZ(0.5);
			if (look.getY() < 0.05)
				look.setY(0.05);
			if (look.getX() > 0.5)
			    look.setX(0.5);
			if (look.getX() < -0.5)
			    look.setX(-0.5);
		    }
		    fireShell(look.multiply((float)(2*charged)), p);
	    	ignite = false;
	    }
	    });
    }
    	
    public void fireShell(Vector look, Player p)
    {
		tntp.setVelocity(look);
		if (cannonType == 1) {

			tntp2.setVelocity(look);
		}
		if (cannonType == 6) {
			tntp2.setVelocity(look);
			tntp3.setVelocity(look);
		}
		charged = 0;

		if (direction != null) {
		Block b;
		b = loc.getBlock().getRelative(BlockFace.UP);
		b.getWorld().createExplosion(loc.getBlock().getRelative(direction, 4).getLocation(), 0);

		if (cannonType == 6) {
			if (direction == BlockFace.EAST || direction == BlockFace.WEST) {
				b.getWorld().createExplosion(loc.getBlock().getRelative(direction, 4).getRelative(BlockFace.NORTH, 2).getLocation(), 0);
				b.getWorld().createExplosion(loc.getBlock().getRelative(direction, 4).getRelative(BlockFace.SOUTH, 2).getLocation(), 0);
			} else {
				b.getWorld().createExplosion(loc.getBlock().getRelative(direction, 4).getRelative(BlockFace.EAST, 2).getLocation(), 0);
				b.getWorld().createExplosion(loc.getBlock().getRelative(direction, 4).getRelative(BlockFace.WEST, 2).getLocation(), 0);
			}
		}
	}
	    
	    new Thread() {

			@Override
			public void run() {

				setPriority(Thread.MIN_PRIORITY);

				// taskNum = -1;
				try {
					while (!tntp.isDead()) {
						if (remove) {
							tntp.remove();
							if (cannonType == 1)
								tntp2.remove();
							if (cannonType == 6) {
								tntp2.remove();
								tntp3.remove();
							}
							last = -1;
							remove = false;
						}
						if (explode) {
							tntp.setFuseTicks(0);
							if (cannonType == 1)
								tntp2.setFuseTicks(0);
							if (cannonType == 6) {
								tntp2.setFuseTicks(0);
								tntp3.setFuseTicks(0);
							}
							last = -1;
							remove = explode;
						}
						int distance = (int) loc.distance(tntp.getLocation());
						if ((tntp.getLocation().getBlock().getType() == Material.WATER || tntp.getLocation().getBlock().getType() == Material.STATIONARY_WATER)) {
							remove = true;
						}
						if (cannonType == 1) {
							if ((tntp2.getLocation().getBlock().getType() == Material.WATER || tntp2.getLocation().getBlock().getType() == Material.STATIONARY_WATER)) {
								remove = true;
						}
						}
						if (cannonType == 6) {
							if ((tntp2.getLocation().getBlock().getType() == Material.WATER || tntp2.getLocation().getBlock().getType() == Material.STATIONARY_WATER)) {
								remove = true;
							}
							if ((tntp3.getLocation().getBlock().getType() == Material.WATER || tntp3.getLocation().getBlock().getType() == Material.STATIONARY_WATER)) {
								remove = true;
							}
						}
						if (mode == 1) {
						if ((last == distance || last == distance-1 || last == distance+1) && distance > 10)
							explode = true;
						last = distance;
						}
						sleep(200);
						
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start(); // , 20L);
	    }
    
    public void Fire(final Player p) {
    	
    	setTimeout();
    	// Fire the TNT at player View Direction
    	new Thread() {
    	    @Override
    	    public void run() {
    		setPriority(Thread.MIN_PRIORITY);
    		try {

    		    sleep(1000);
    		    sleep(500);

    		    Vector look;

    		    if (olook != null)
    		    	look = olook;
    		    else
    		    look = p.getLocation().getDirection();

    		    if (direction == BlockFace.WEST) {
    			if (look.getX() > -0.5)
    			    look.setX(-0.5);
    			if (look.getY() < 0.05)
    				look.setY(0.05);
    			if (look.getZ() > 0.5)
    			    look.setZ(0.5);
    			if (look.getZ() < -0.5)
    			    look.setZ(-0.5);
    		    }
    		    if (direction == BlockFace.NORTH) {
    			if (look.getZ() > -0.5)
    			    look.setZ(-0.5);
    			if (look.getY() < 0.05)
    					look.setY(0.05);
    			if (look.getX() > 0.5)
    			    look.setX(0.5);
    			if (look.getX() < -0.5)
    			    look.setX(-0.5);
    		    }
    		    if (direction == BlockFace.EAST) {
    			if (look.getX() < 0.5)
    			    look.setX(0.5);
    			if (look.getY() < 0.05)
    				look.setY(0.05);
    			if (look.getZ() > 0.5)
    			    look.setZ(0.5);
    			if (look.getZ() < -0.5)
    			    look.setZ(-0.5);
    		    }
    		    if (direction == BlockFace.SOUTH) {
    			if (look.getZ() < 0.5)
    			    look.setZ(0.5);
    			if (look.getY() < 0.05)
    				look.setY(0.05);
    			if (look.getX() > 0.5)
    			    look.setX(0.5);
    			if (look.getX() < -0.5)
    			    look.setX(-0.5);
    		    }
    		    
    		    	
    		    
    		    fireUpdate(look.multiply((float)(2*charged)), p);
    	    	ignite = false;
    		    
    		} catch (InterruptedException e) {
    		}
    	    }
    	}.start();
        }
    public void fireUpdate(final Vector look, final Player p) { //unused
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){

	    public void run()
	    {

	    	
	    	tntp.setVelocity(look);
	    	if( cannonType == 1 )
		    {
	    		
		    	tntp2.setVelocity(look);
		    }
		    if( cannonType == 6 )
		    {
		    	tntp2.setVelocity(look);
		    	
		    	tntp3.setVelocity(look);
		    }
		    charged = 0;
		    
		    final Block b;
		    b = loc.getBlock().getRelative(BlockFace.UP);
		    b.getWorld().createExplosion(loc.getBlock().getRelative(direction,4).getLocation(), 0);
		    
		    if( cannonType == 6 )
		    {
		    	if( direction == BlockFace.EAST || direction == BlockFace.WEST )
		    	{
		    		b.getWorld().createExplosion(loc.getBlock().getRelative(direction,4).getRelative(BlockFace.NORTH,2).getLocation(), 0);
		    		b.getWorld().createExplosion(loc.getBlock().getRelative(direction,4).getRelative(BlockFace.SOUTH,2).getLocation(), 0);
		    	}else
		    	{
		    		b.getWorld().createExplosion(loc.getBlock().getRelative(direction,4).getRelative(BlockFace.EAST,2).getLocation(), 0);
		    		b.getWorld().createExplosion(loc.getBlock().getRelative(direction,4).getRelative(BlockFace.WEST,2).getLocation(), 0);
		    	}
		    }
	    	
	    }
    	}
    	
	);
    }
    
    public void setDelay(Player p)
    {
    	if( mode == 1)
    	{
    		delay = 0;
    		mode = 0;
    		p.sendMessage(ChatColor.RED + "Long Flight Fuse");
    	}else if( delay == 0 )
    	{
    		delay = 1000;
    		mode = 0;
    		p.sendMessage(ChatColor.GOLD + "Medium Flight Fuse");

    	}else if( delay == 1000 )
    	{
    		delay = 2000;
    		mode = 0;
    		p.sendMessage(ChatColor.YELLOW + "Short Flight Fuse");
    	}else if( delay >= 2000 && mode == 0)
    	{
    		delay = 0;
    		mode = 1;
    		p.sendMessage(ChatColor.BLUE + "Impact Flight Fuse " + ChatColor.DARK_RED + "(EXPERIMENTAL)");
		}
    }

    public void Action(Player p) {
	if (isCharged()) {
	    if (isTimeout()) {
		if (isIgnite()) {
		    Fire(p);
		} else {
		    Ignite(p);
		}
	    
	    } else {
	    	if (p != null)
		p.sendMessage(ChatColor.RED + "You have to wait for the Cannon to cool down!");
	    }
	}

    }
    
    public void fireCannonButton(Player p, boolean leftClick)
    {
    	if( charged > 0 )
    	{
    		fireCannon(p, loc.getBlock());
    		charged = 0;
    		
    	}
    }
    
    public void fireCannon(final Player p, final Block b)
    {
		new Thread(){
			
    	@Override
			public void run() {
    		
    		setPriority(Thread.MIN_PRIORITY);
				//taskNum = -1;
    			try{
    				sleep(500);
    				p.sendMessage(ChatColor.RED + "3 - Ready!");
    			    sleep(500);
    			    p.sendMessage(ChatColor.YELLOW + "2 - Aim!  ");
    			    sleep(500);
    			    p.sendMessage(ChatColor.GREEN + "1 - Fire!!!");
    			    sleep(500);
    			    Vector look;
    			    look = p.getLocation().getDirection();
    			    look.setY(0);
    			    look.normalize();
    			    if (direction == BlockFace.NORTH) {
    				if (look.getX() > -0.5)
    				    look.setX(-0.5);

    				if (look.getZ() > 0.5)
    				    look.setZ(0.5);
    				if (look.getZ() < -0.5)
    				    look.setZ(-0.5);
    			    }
    			    if (direction == BlockFace.EAST) {
    				if (look.getZ() > -0.5)
    				    look.setZ(-0.5);

    				if (look.getX() > 0.5)
    				    look.setX(0.5);
    				if (look.getX() < -0.5)
    				    look.setX(-0.5);
    			    }
    			    if (direction == BlockFace.SOUTH) {
    				if (look.getX() < 0.5)
    				    look.setX(0.5);

    				if (look.getZ() > 0.5)
    				    look.setZ(0.5);
    				if (look.getZ() < -0.5)
    				    look.setZ(-0.5);
    			    }
    			    if (direction == BlockFace.WEST) {
    				if (look.getZ() < 0.5)
    				    look.setZ(0.5);

    				if (look.getX() > 0.5)
    				    look.setX(0.5);
    				if (look.getX() < -0.5)
    				    look.setX(-0.5);
    			    }
    			    
    			    

    			    
    			    double x2,y2,z2,x1,y1,z1;
    			    double arcHeight;
    			    int gunHeight = b.getY() - 64;
    			    arcHeight = gunHeight+2;
    			    
    			    double termA, termB, termC;
    			    termA = (-4*arcHeight + 2*gunHeight/(range*range));
    			    termB = (-gunHeight/range + 4*arcHeight*range - 2*gunHeight/range);
    			    termC = gunHeight;
    			    //p.sendMessage("termA=")
    			    
    			    for(int i=0; i<=range; i++)
    			    {
    			    	x2 = look.getX() * i;
    			    	z2 = look.getZ() * i;
    			    	y2 = (termA*i*i + termB*i + termC) - gunHeight;
    			    	
        			    if( i > 0 )
        			    {
        			    	x1 = look.getX() * (i-1);
        			    	z1 = look.getZ() * (i-1);
        			    	y1 = (termA*(i-1)*(i-1) + termB*(i-1) + termC) - gunHeight;

        			    }else
        			    {
        			    	x1=0;
        			    	z1=0;
        			    	y1=0;
        			    }
    			    	
        			   
        			    fireCannonUpdate(b.getRelative(direction,cannonLength+1), x2, y2, z2, x1, y1, z1, i);
    			    	sleep(100);
    			    }
    			    
				}catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
    	}.start(); 
    }
    
    public void fireCannonUpdate(final Block b, final double x2, final double y2, final double z2, final double x1, final double y1, final double z1, final int i)
    {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
 

		public void run()
	    {
	    	
	    	Block c = b.getRelative((int)x1, (int)y1, (int)z1);
	    	if( c.getTypeId() == 42 || i == 0 )
	    	{
	    		if( i > 0 )
	    		{
		    		if( c.getY() >= 64 )
			    		c.setType(Material.AIR);
			    	else	
			    		c.setType(Material.WATER);
	    		}
	    		
		    	Block d = b.getRelative((int)x2, (int)y2, (int)z2);
		    	if( d.getY() > 60 )
		    		d.setTypeId(42);
	    	}
	    	
	    }
    	
    	});
    }

    
	
	public void colorTorpedoes()
    {
    	//color wool
		Block a,b,c,d;
		if( direction == BlockFace.NORTH )
		{
    		a = loc.getBlock().getRelative(BlockFace.WEST,2);
    		b = loc.getBlock().getRelative(BlockFace.EAST,2);
    		c = loc.getBlock().getRelative(BlockFace.WEST,1);
    		d = loc.getBlock().getRelative(BlockFace.EAST,1);
		}
    	else if( direction == BlockFace.SOUTH )
    	{
    		a = loc.getBlock().getRelative(BlockFace.EAST,2);
    		b = loc.getBlock().getRelative(BlockFace.WEST,2);
    		c = loc.getBlock().getRelative(BlockFace.EAST,1);
    		d = loc.getBlock().getRelative(BlockFace.WEST,1);
    	}
    	else if( direction == BlockFace.EAST )
    	{
    		a = loc.getBlock().getRelative(BlockFace.NORTH,2);
    		b = loc.getBlock().getRelative(BlockFace.SOUTH,2);
    		c = loc.getBlock().getRelative(BlockFace.NORTH,1);
    		d = loc.getBlock().getRelative(BlockFace.SOUTH,1);
    	}
    	else //if( direction == BlockFace.WEST )
    	{
    		a = loc.getBlock().getRelative(BlockFace.SOUTH,2);
    		b = loc.getBlock().getRelative(BlockFace.NORTH,2);
    		c = loc.getBlock().getRelative(BlockFace.SOUTH,1);
    		d = loc.getBlock().getRelative(BlockFace.NORTH,1);
    	}
    	a = a.getRelative(direction,-5);
    	b = b.getRelative(direction,-5);
    	c = c.getRelative(direction,4);
    	d = d.getRelative(direction,4);
    	
    	Byte wool1;
    	Byte wool2;
    	Byte wool3;
    	Byte wool4;
		wool1 = 0xE;
		wool2 = 0x8;
		wool3 = 0x8;
		wool4 = 0x7;
		
		
		if( cannonType == 7 )
		{
    		wool1 = 0x3;
			wool2 = 0xD;
			wool3 = 0xD;
			wool4 = 0x7;
		}else if( cannonType == 8 )
		{
			wool1 = 0x8;
			wool2 = 0x0;
			wool3 = 0x0;
			wool4 = 0x7;
		} else if (cannonType == 12) {
		wool1 = 0xE;
		wool2 = 0x0;
		wool3 = 0x0;
		wool4 = 0x7;
		
    	} else if( cannonType == 11 )
		{
    		wool1 = 0xB;
			wool2 = 0x0;
			wool3 = 0x0;
			wool4 = 0x7;
		}else if (cannonType == 16) {
    		wool1 = 0x5;
			wool2 = 0x0;
			wool3 = 0x0;
			wool4 = 0x7;
		}
		

		
		
		if( c.getTypeId() == 35 )
    		c.setTypeIdAndData(35, wool1, false);
    	if( c.getRelative(direction,-1).getTypeId() == 35 )
    		c.getRelative(direction,-1).setTypeIdAndData(35, wool2, false);
    	if( c.getRelative(direction,-2).getTypeId() == 35 )
    		c.getRelative(direction,-2).setTypeIdAndData(35, wool3, false);
    	if( c.getRelative(direction,-3).getTypeId() == 35 )
    		c.getRelative(direction,-3).setTypeIdAndData(35, wool4, false);
    	
    	if( d.getTypeId() == 35 )
    		d.setTypeIdAndData(35, wool1, false);
    	if( d.getRelative(direction,-1).getTypeId() == 35 )
    		d.getRelative(direction,-1).setTypeIdAndData(35, wool2, false);
    	if( d.getRelative(direction,-2).getTypeId() == 35 )
    		d.getRelative(direction,-2).setTypeIdAndData(35, wool3, false);
    	if( d.getRelative(direction,-3).getTypeId() == 35 )
    		d.getRelative(direction,-3).setTypeIdAndData(35, wool4, false);
		
    	for( int i=0; i<4; i++ )
    	{	
    		
        	if( a.getTypeId() == 35 )
        		a.setTypeIdAndData(35, wool1, false);
        	if( a.getRelative(direction,-1).getTypeId() == 35 )
        		a.getRelative(direction,-1).setTypeIdAndData(35, wool2, false);
        	if( a.getRelative(direction,-2).getTypeId() == 35 )
        		a.getRelative(direction,-2).setTypeIdAndData(35, wool3, false);
        	if( a.getRelative(direction,-3).getTypeId() == 35 )
        		a.getRelative(direction,-3).setTypeIdAndData(35, wool4, false);
        	
        	if( b.getTypeId() == 35 )
        		b.setTypeIdAndData(35, wool1, false);
        	if( b.getRelative(direction,-1).getTypeId() == 35 )
        		b.getRelative(direction,-1).setTypeIdAndData(35, wool2, false);
        	if( b.getRelative(direction,-2).getTypeId() == 35 )
        		b.getRelative(direction,-2).setTypeIdAndData(35, wool3, false);
        	if( b.getRelative(direction,-3).getTypeId() == 35 )
        		b.getRelative(direction,-3).setTypeIdAndData(35, wool4, false);
    		
    		
        	
        	if( i == 0 || i == 2 )
        	{
        		a = a.getRelative(direction,-4);
        		b = b.getRelative(direction,-4);
        	}else if( i == 1 )
        	{
        		a = a.getRelative(direction,4).getRelative(BlockFace.DOWN);
        		b = b.getRelative(direction,4).getRelative(BlockFace.DOWN);
        	}	
    	}
    }
	
	public void colorMissiles()
    {
    	//color wool
		Block a;
		a = loc.getBlock().getRelative(direction,1);
    	
    	Byte wool1;
    	Byte wool2;
    	Byte wool3;
    	Byte wool4;
		wool1 = 0xE;
		wool2 = 0x0;
		wool3 = 0x0;
		wool4 = 0x7;
		
		if( cannonType == 13 )
		{
    		wool1 = 0xB;
			wool2 = 0x0;
			wool3 = 0x0;
			wool4 = 0x7;
		} else if (cannonType == 17) {
    		wool1 = 0x5;
			wool2 = 0x0;
			wool3 = 0x0;
			wool4 = 0x7;
		}
		
		if( a.getRelative(BlockFace.UP,1).getTypeId() == 35 )
    		a.getRelative(BlockFace.UP,1).setTypeIdAndData(35, wool4, false);
    	if( a.getRelative(BlockFace.UP,2).getTypeId() == 35 )
    		a.getRelative(BlockFace.UP,2).setTypeIdAndData(35, wool3, false);
    	if( a.getRelative(BlockFace.UP,3).getTypeId() == 35 )
    		a.getRelative(BlockFace.UP,3).setTypeIdAndData(35, wool2, false);
    	if( a.getRelative(BlockFace.UP,4).getTypeId() == 35 )
    		a.getRelative(BlockFace.UP,4).setTypeIdAndData(35, wool1, false);
    }
	

    
	public boolean Charge(Player p, boolean leftClick) 
    {
		Inventory inventory = null;
		if (loc.getBlock().getType() == Material.DISPENSER) {
    	Dispenser dispenser = (Dispenser) loc.getBlock().getState();
    	inventory = dispenser.getInventory();
		} else if (loc.getBlock().getType() == Material.DROPPER) {
		Dropper dropper = (Dropper) loc.getBlock().getState();
    	inventory = dropper.getInventory();
		}
	    if (inventory != null) {
	    	if( inventory.getItem(4) == null || inventory.getItem(4).getTypeId() != 388 )
	    	{
	    		Essentials ess;
				ess = (Essentials) nc.getServer().getPluginManager().getPlugin("Essentials");
				if( ess == null )
				{
					if (p != null)
					p.sendMessage("Essentials Economy error");
					return false;
				}
				
				int cost=0;
			    if( cannonType == 0 ) //single barrel-
			    	cost=ConfigManager.getcostData().getInt("Weapons.1barrel");
			    else if( cannonType == 1 )//double barrel-
			    	cost=ConfigManager.getcostData().getInt("Weapons.2barrel");
			    else if( cannonType == 3 )//torpedo mk 2-
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk2torps");
			    else if( cannonType == 4 )//depth charge-
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk1dc");
			    else if( cannonType == 5 )//depth charge mk2-
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk2dc");
			    else if( cannonType == 6 )//triple barrel-
			    	cost=ConfigManager.getcostData().getInt("Weapons.3barrel");
			    else if( cannonType == 7 )//torpedo mk 3-
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk3torps");
			    else if( cannonType == 8 )//torpedo mk 1-
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk1torps");
			    else if( cannonType == 9 )//bombs mk1-
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk1bomb");
			    else if( cannonType == 10 )//bombs mk2-
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk2bomb");
			    else if( cannonType == 11 )//missiles mk1
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk1missiles");
			    else if( cannonType == 12 )//missiles mk2
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk2missiles");
			    else if( cannonType == 12 )//missiles mk3
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk3missiles");
			    else if( cannonType == 13 )//vertical missiles mk1
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk1vmissiles");
			    else if( cannonType == 14 )//vertical missiles mk2
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk2vmissiles");
			    else if( cannonType == 17 )//vertical missiles mk3
			    	cost=ConfigManager.getcostData().getInt("Weapons.mk3vmissiles");

			    if (p != null && Utils.CheckTestWorld(p.getLocation())) {
			    	cost = 0;
			    }
			    
				if(p == null || (p != null && Utils.CheckEnabledWorld(p.getLocation())) )
				{
					if(p == null || (p != null && ess.getUser(p).canAfford(new BigDecimal(cost))) )
					{
						if (p != null)
						p.sendMessage(ChatColor.GREEN + "Weapon purchased.");
						
						inventory.setItem(4, new ItemStack( 388, 1));
						if (p!= null)
						ess.getUser(p).takeMoney(new BigDecimal(cost));
					}else
					{
						if (p != null)
						p.sendMessage(ChatColor.RED + "You cannot afford this weapon.");
						AimCannon.cannons.remove(this);
						return false;
					}
				}else
				{
					inventory.setItem(4, new ItemStack( 388, 1));
				}
	    	}
	    	
	    	if (Utils.CheckTestWorld(loc)) {
	    		ammunition = initAmmo;
	    	}
	    	
	    	//color wool for torpedoes
			if( !leftClick && (cannonType == 3 || cannonType == 7 || cannonType == 8) )
			{
				if (charged == 1) {
		    		if(depth > 5 )
		    			depth = depth - 5;
		    		else
		    			depth = 0;
					if (p != null)
		    		p.sendMessage(ChatColor.GREEN + "Torpedo System Active: Depth set to " + ChatColor.YELLOW + depth + ChatColor.YELLOW + " meters.");
		    		colorTorpedoes();
		    		return true;
				} else {
	        		colorTorpedoes();
	        		return false;
				}
			} else if( !leftClick && (cannonType == 11 || cannonType == 12 || cannonType == 13 || cannonType == 14 || cannonType == 16 || cannonType == 17) )
	    		{
				if (charged == 1) {
		    		if(depth > 0 )
		    			depth = depth - 5;
		    		else
		    			depth = 0;
					if (p != null)
		    		p.sendMessage(ChatColor.GREEN + "Missile System Active: Y axis set to " + ChatColor.YELLOW + depth + ChatColor.YELLOW + ".");
		    		if (cannonType == 13 || cannonType == 14 || cannonType == 17) {
		    			colorMissiles();
		    			return true;
		    		} else {
		    		colorTorpedoes();
		    		return true;
		    		}
				} else {
		    		if (cannonType == 13 || cannonType == 14 || cannonType == 17) {
		    			colorMissiles();
		    			return false;
		    		} else {
		    		colorTorpedoes();
		    		return false;
		    		}
				}
			}else if( charged < 4 )
	    	{
		    	if( cannonType == 0 )
		    	{
		    		
				    if( charged == 0 )
				    {
				    	if( ammunition > 0 )
				    	{
				    		ammunition = ammunition - 1;
				    	}else
				    	{
							Craft theCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			    			Block block;
			    			if (theCraft != null) {
			    				for (int x = 0; x < theCraft.sizeX; x++) {
			    					for (int z = 0; z < theCraft.sizeZ; z++) {
			    						for (int y = 0; y < theCraft.sizeY; y++) {

			    							if( theCraft.matrix == null )
			    								return false;
			    
			    							block = theCraft.world.getBlockAt(theCraft.minX + x, theCraft.minY + y, theCraft.minZ + z);
			    							if (block.getType() == Material.EMERALD_ORE && ammunition < 1) {
			    								reload(p);
			    								setBlock(0, block, theCraft);
			    								break;
			    							}
			    						}
			    					}
			    				}
			    			}
			    			if (ammunition < 1) {
			    				if (p != null)
				    			p.sendMessage( ChatColor.RED + "Cannon out of ammo!");
				    			return false;
			    			}
				    	}
				    	charged=1;
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Cannon Loaded! " + ChatColor.YELLOW + ammunition + ChatColor.GREEN + " shots remaining. Cannon Power X" + ChatColor.YELLOW + charged);
				    }else{
				    	charged++;
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Cannon Power X" + ChatColor.YELLOW + charged);
				    }
			    	
				    return true;
				    
		    	} else if( cannonType == 1 )
		    	{
		    		if( charged == 0 )
				    {
				    	if( ammunition > 0 )
				    	{
				    		ammunition = ammunition - 1;
				    	}else
				    	{
			    			Craft theCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			    			Block block;
			    			if (theCraft != null) {
			    				for (int x = 0; x < theCraft.sizeX; x++) {
			    					for (int z = 0; z < theCraft.sizeZ; z++) {
			    						for (int y = 0; y < theCraft.sizeY; y++) {

			    							if( theCraft.matrix == null )
			    								return false;
			    
			    							block = theCraft.world.getBlockAt(theCraft.minX + x, theCraft.minY + y, theCraft.minZ + z);
			    							if (block.getType() == Material.EMERALD_ORE && ammunition < 1) {
			    								reload(p);
			    								setBlock(0, block, theCraft);
			    								break;
			    							}
			    						}
			    					}
			    				}
			    			}
			    			if (ammunition < 1) {
								if (p != null)
				    		p.sendMessage( ChatColor.RED + "Cannon out of ammo!");
				    		return false;
			    			}
				    	}
				    	charged=1;
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Cannon Loaded! " + ChatColor.YELLOW + ammunition + ChatColor.GREEN + " shots remaining. Cannon Power X" + ChatColor.YELLOW + charged);
				    }else{
				    	charged++;
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Cannon Power X" + ChatColor.YELLOW + charged);
				    }
			    	
				    return true;
		    	}else if( cannonType == 6 )
		    	{
		    		if( charged == 0 )
				    {
				    	if( ammunition > 0 )
				    	{
				    		ammunition = ammunition - 1;
				    	}else
				    	{
							Craft theCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			    			Block block;
			    			if (theCraft != null) {
			    				for (int x = 0; x < theCraft.sizeX; x++) {
			    					for (int z = 0; z < theCraft.sizeZ; z++) {
			    						for (int y = 0; y < theCraft.sizeY; y++) {

			    							if( theCraft.matrix == null )
			    								return false;
			    
			    							block = theCraft.world.getBlockAt(theCraft.minX + x, theCraft.minY + y, theCraft.minZ + z);
			    							if (block.getType() == Material.EMERALD_ORE && ammunition < 1) {
			    								reload(p);
			    								setBlock(0, block, theCraft);
			    								break;
			    							}
			    						}
			    					}
			    				}
			    			}
			    			if (ammunition < 1) {
								if (p != null)
				    		p.sendMessage( ChatColor.RED + "Cannon out of ammo!");
				    		return false;
			    			}
				    	}
				    	charged=1;
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Cannon Loaded! " + ChatColor.YELLOW + ammunition + ChatColor.GREEN + " shots remaining. Cannon Power X" + ChatColor.YELLOW + charged);
				    }else{
				    	charged++;
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Cannon Power X" + ChatColor.YELLOW + charged);
				    }
			    	
				    return true;
				}else if( cannonType == 3 || cannonType == 7 || cannonType == 8 )
		    	{
		    		charged=1;
		    		if( depth < 5 )
		    			depth++;
		    		else if( depth == 5 )
		    			depth = 10;
		    		else if( depth >= 10 && depth < 50 )
		    			depth = depth + 5;
		    		else
		    			depth = 0;
					if (p != null)
		    		p.sendMessage(ChatColor.GREEN + "Torpedo System Active: Depth set to " + ChatColor.YELLOW + depth + ChatColor.YELLOW + " meters.");
					colorTorpedoes();
		   		
		    		return true;
		    	}else if( cannonType == 11 || cannonType == 12 || cannonType == 13 || cannonType == 14 || cannonType == 16 || cannonType == 17)
		    	{
		    		charged=1;
		    		if(depth < 240 )
		    			depth = depth + 5;
		    		else
		    			depth = 0;
					if (p != null)
		    		p.sendMessage(ChatColor.GREEN + "Missile System Active: Y axis set to " + ChatColor.YELLOW + depth + ChatColor.YELLOW + ".");
		    		if (cannonType == 13 || cannonType == 14 || cannonType == 17) {
		    		colorMissiles();
		    		} else {
		    		colorTorpedoes();
		    		}
		   		
		    		
		    		return true;
		    	}
		    	else if( cannonType == 4 )
		    	{
		    		if( charged == 0 )
				    {
						Craft theCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		    			if (theCraft != null) {
		    				if (theCraft.type.canFly || theCraft.type.isTerrestrial) {
								if (p != null)
		    					p.sendMessage(ChatColor.RED + "You can't use Depth Charges on an aircraft!");
		    					return false;
		    				}
		    			}
				    	if( ammunition > 0 && ammunition <= 10)
				    	{
				    		ammunition = ammunition - 1;
				    	}else
				    	{
			    			Block block;
			    			if (theCraft != null) {
			    				for (int x = 0; x < theCraft.sizeX; x++) {
			    					for (int z = 0; z < theCraft.sizeZ; z++) {
			    						for (int y = 0; y < theCraft.sizeY; y++) {

			    							if( theCraft.matrix == null )
			    								return false;
			    
			    							block = theCraft.world.getBlockAt(theCraft.minX + x, theCraft.minY + y, theCraft.minZ + z);
			    							if (block.getType() == Material.EMERALD_ORE && ammunition < 1) {
			    								reload(p);
			    								setBlock(0, block, theCraft);
			    								break;
			    							}
			    						}
			    					}
			    				}
			    			}
			    			if (ammunition < 1) {
								if (p != null)
				    		p.sendMessage( ChatColor.RED + "Out of depth charges!");
				    		return false;
			    			}
				    	}
				    	charged=1;
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Depth charge dropper loaded! " + ChatColor.YELLOW + ammunition + ChatColor.GREEN + " depth charges left.");
				    }else{
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Depth charge dropper already loaded! " + ChatColor.YELLOW + ammunition + ChatColor.GREEN + " depth charges left.");
				    }
			    	
				    return true;
		    	}else if( cannonType == 5 )
		    	{
		    		if( charged == 0 )
				    {
						Craft theCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		    			if (theCraft != null) {
		    				if (theCraft.type.canFly || theCraft.type.isTerrestrial) {
								if (p != null)
		    					p.sendMessage(ChatColor.RED + "You can't use Depth Charges on an aircraft!");
		    					return false;
		    				}
		    			}
				    	if( ammunition > 0 && ammunition <= 15)
				    	{
				    		ammunition = ammunition - 1;
				    	}else
				    	{
			    			Block block;
			    			if (theCraft != null) {
			    				for (int x = 0; x < theCraft.sizeX; x++) {
			    					for (int z = 0; z < theCraft.sizeZ; z++) {
			    						for (int y = 0; y < theCraft.sizeY; y++) {

			    							if( theCraft.matrix == null )
			    								return false;
			    
			    							block = theCraft.world.getBlockAt(theCraft.minX + x, theCraft.minY + y, theCraft.minZ + z);
			    							if (block.getType() == Material.EMERALD_ORE && ammunition < 1) {
			    								reload(p);
			    								setBlock(0, block, theCraft);
			    								break;
			    							}
			    						}
			    					}
			    				}
			    			}
			    			if (ammunition < 1) {
								if (p != null)
				    		p.sendMessage( ChatColor.RED + "Out of depth charges!");
				    		return false;
			    			}
				    	}
			    	charged=1;
						if (p != null)
			    	p.sendMessage(ChatColor.GREEN + "Depth charge launcher loaded! " + ChatColor.YELLOW + ammunition + ChatColor.GREEN + " depth charges left.");
			    }else{
						if (p != null)
			    	p.sendMessage(ChatColor.GREEN + "Depth charge launcher already loaded! " + ChatColor.YELLOW + ammunition + ChatColor.GREEN + " depth charges left.");
			    }
			    	
				    return true;
		    		
		    	}else if( cannonType == 9 )
		    	{
		    		if( charged == 0 )
				    {
				    	if( ammunition > 0 && ammunition <= 2)
				    	{
				    		ammunition = ammunition - 1;
				    	}else
				    	{
							Craft theCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			    			Block block;
			    			if (theCraft != null) {
			    				for (int x = 0; x < theCraft.sizeX; x++) {
			    					for (int z = 0; z < theCraft.sizeZ; z++) {
			    						for (int y = 0; y < theCraft.sizeY; y++) {

			    							if( theCraft.matrix == null )
			    								return false;
			    
			    							block = theCraft.world.getBlockAt(theCraft.minX + x, theCraft.minY + y, theCraft.minZ + z);
			    							if (block.getType() == Material.EMERALD_ORE && ammunition < 1) {
			    								reload(p);
			    								setBlock(0, block, theCraft);
			    								break;
			    							}
			    						}
			    					}
			    				}
			    			}
			    			if (ammunition < 1) {
				    		p.sendMessage( ChatColor.RED + "Out of bombs!");
				    		return false;
			    			}
				    	}
				    	charged=1;
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Bomb dropper loaded! " + ChatColor.YELLOW + ammunition + ChatColor.GREEN + " bombs left.");
				    }else{
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Bomb dropper already loaded! " + ChatColor.YELLOW + ammunition + ChatColor.GREEN + " bombs left.");
				    }
			    	
				    return true;
		    		
		    	}else if( cannonType == 10 )
		    	{
		    		if( charged == 0 )
				    {
				    	if( ammunition > 0 && ammunition <= 6)
				    	{
				    		ammunition = ammunition - 1;
				    	}else
				    	{
							Craft theCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			    			Block block;
			    			if (theCraft != null) {
			    				for (int x = 0; x < theCraft.sizeX; x++) {
			    					for (int z = 0; z < theCraft.sizeZ; z++) {
			    						for (int y = 0; y < theCraft.sizeY; y++) {

			    							if( theCraft.matrix == null )
			    								return false;
			    
			    							block = theCraft.world.getBlockAt(theCraft.minX + x, theCraft.minY + y, theCraft.minZ + z);
			    							if (block.getType() == Material.EMERALD_ORE && ammunition < 1) {
			    								reload(p);
			    								setBlock(0, block, theCraft);
			    								break;
			    							}
			    						}
			    					}
			    				}
			    			}
			    			if (ammunition < 1) {
								if (p != null)
				    		p.sendMessage( ChatColor.RED + "Out of bombs!");
				    		return false;
			    			}
				    	}
				    	charged=1;
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Bomb launcher loaded! " + ChatColor.YELLOW + ammunition + ChatColor.GREEN + " bombs left.");
				    }else{
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Bomb launcher already loaded! " + ChatColor.YELLOW + ammunition + ChatColor.GREEN + " bombs left.");
				    }
			    	
				    return true;
		    		
		    	}else if( cannonType == 15 )
		    	{
		    		if( charged == 0 )
				    {
				    	if( ammunition > 0 && ammunition <= 10)
				    	{
				    		ammunition = ammunition - 1;
				    	}else
				    	{
							Craft theCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			    			Block block;
			    			if (theCraft != null) {
			    				for (int x = 0; x < theCraft.sizeX; x++) {
			    					for (int z = 0; z < theCraft.sizeZ; z++) {
			    						for (int y = 0; y < theCraft.sizeY; y++) {

			    							if( theCraft.matrix == null )
			    								return false;

			    							block = theCraft.world.getBlockAt(theCraft.minX + x, theCraft.minY + y, theCraft.minZ + z);
			    							if (block.getType() == Material.EMERALD_ORE && ammunition < 1) {
			    								reload(p);
			    								setBlock(0, block, theCraft);
			    								break;
			    							}
			    						}
			    					}
			    				}
			    			}
			    			if (ammunition < 1) {
				    		p.sendMessage( ChatColor.RED + "Out of flares!");
				    		return false;
			    			}
				    	}
				    	charged=1;
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Flare launcher loaded! " + ChatColor.RED + ammunition + ChatColor.GREEN + " flares left.");
				    }else{
						if (p != null)
				    	p.sendMessage(ChatColor.GREEN + "Flare launcher already loaded! " + ChatColor.RED + ammunition + ChatColor.GREEN + " flares left.");
				    }

				    return true;

		    	}else if( cannonType == 20 )
				{
					if( charged == 0 )
					{
						if( ammunition > 0 && ammunition <= 10)
						{
							ammunition = ammunition - 1;
						}else
						{
							if (ammunition < 1) {
								if (p != null)
								p.sendMessage( ChatColor.RED + "Out of Nukes!");
								return false;
							}
						}
						charged=1;
						if (p != null)
						p.sendMessage(ChatColor.GREEN + "Nuke launcher loaded!");
					}else{
						if (p != null)
						p.sendMessage(ChatColor.GREEN + "Nuke launcher already loaded!");
					}

					return true;

				}else
		    	{
		    		charged = 1;
		    		return true;
		    	}
	    	}else
	    	{
	    		charged=1;
	    		if (p != null)
	    		p.sendMessage(ChatColor.GREEN + "Cannon Power X" + ChatColor.YELLOW + charged);
	    		return false;
	    	}
		
	    } else {
	    	NavyCraft.instance.DebugMessage("Inventory was null", 3);
	    	return false;
	    }
}
    private void setTimeout() {
	timeout = new Date().getTime();
    }

    public void loadTubeV(Player p) {
		if( !checkTubeLoadedV()) {
			Block b1 = loc.getBlock().getRelative(direction,1).getRelative(BlockFace.UP);
			Block b2 = loc.getBlock().getRelative(direction,1).getRelative(BlockFace.UP,2);
			Block b3 = loc.getBlock().getRelative(direction,1).getRelative(BlockFace.UP,3);
			Block b4 = loc.getBlock().getRelative(direction,1).getRelative(BlockFace.UP,4);
			b1.setType(Material.WOOL);
			b2.setType(Material.WOOL);
			b3.setType(Material.WOOL);
			b4.setType(Material.WOOL);
		}
	}

	public void loadTube(boolean left, Player p) {
    	if (!checkTubeLoaded(left)) {
				Block b;
				b = getDirectionFromRelative(loc.getBlock(), direction, left).getRelative(direction, -5);

				if( direction == BlockFace.NORTH && left )
					b = b.getRelative(BlockFace.WEST);
				else if( direction == BlockFace.NORTH && !left )
					b = b.getRelative(BlockFace.EAST);
				else if( direction == BlockFace.SOUTH && left )
					b = b.getRelative(BlockFace.EAST);
				else if( direction == BlockFace.SOUTH && !left )
					b = b.getRelative(BlockFace.WEST);
				else if( direction == BlockFace.EAST && left )
					b = b.getRelative(BlockFace.NORTH);
				else if( direction == BlockFace.EAST && !left )
					b = b.getRelative(BlockFace.SOUTH);
				else if( direction == BlockFace.WEST && left )
					b = b.getRelative(BlockFace.SOUTH);
				else //if( direction == BlockFace.WEST && !left )
					b = b.getRelative(BlockFace.NORTH);

				b.setType(Material.WOOL);
				b.getRelative(direction, -1).setType(Material.WOOL);
				b.getRelative(direction, -2).setType(Material.WOOL);
				b.getRelative(direction, -3).setType(Material.WOOL);
		}
	}
    
    public void loadTorpedoLever(boolean left, Player p)
    {
    	if( !checkTubeLoaded(left) )
    	{
    		if( checkOuterDoorClosed() )
    		{
    			if( !checkInnerDoorClosed(left) )
    			{
    				if( loadTorpedo(left) )
    				{
    					p.sendMessage(ChatColor.GREEN + "Tube Loading!");
    				}else
    				{
    					p.sendMessage(ChatColor.RED + "No torpedoes remaining for this tube!");
    				}
    			}else
    			{
    				p.sendMessage(ChatColor.YELLOW + "Open inner door before loading.");
    			}
    		}else
    		{
    			p.sendMessage(ChatColor.YELLOW + "Close outer doors first.");	
    		}
    	}else
    	{
    		p.sendMessage(ChatColor.YELLOW + "Tube already loaded.");
    	}
    }
    
    
	public boolean loadTorpedo(boolean left)
    {
    	Block b;
    	b = getDirectionFromRelative(loc.getBlock(), direction, left).getRelative(direction, -5);
    	
    	if( direction == BlockFace.NORTH && left )
    		b = b.getRelative(BlockFace.WEST);
    	else if( direction == BlockFace.NORTH && !left )
    		b = b.getRelative(BlockFace.EAST);
    	else if( direction == BlockFace.SOUTH && left )
    		b = b.getRelative(BlockFace.EAST);
    	else if( direction == BlockFace.SOUTH && !left )
    		b = b.getRelative(BlockFace.WEST);
    	else if( direction == BlockFace.EAST && left )
    		b = b.getRelative(BlockFace.NORTH);
    	else if( direction == BlockFace.EAST && !left )
    		b = b.getRelative(BlockFace.SOUTH);
    	else if( direction == BlockFace.WEST && left )
    		b = b.getRelative(BlockFace.SOUTH);
    	else //if( direction == BlockFace.WEST && !left )
    		b = b.getRelative(BlockFace.NORTH);
    	
    	for( int i=0; i<4; i++)
    	{
    		if( b.getTypeId() == 35 )
    			if( b.getRelative(direction,-1).getTypeId() == 35 )
        			if( b.getRelative(direction,-2).getTypeId() == 35 )
        				if( b.getRelative(direction,-3).getTypeId() == 35 )
        				{
        					if( i > 1 )
        						loadingTorp(left, b, direction, true);
        					else
        						loadingTorp(left, b, direction, false);
        					
        					if( left )
        						leftLoading = true;
        					else
        						rightLoading = true;
        					
        					Craft testCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        					if( testCraft != null )
        					{
        						testCraft.waitTorpLoading++;
        					}
        					return true;
        				}
    		//move back four spaces unless going from 2 to 3, then carriage return it
    		if( i == 1 )
    			b = b.getRelative(direction,4).getRelative(BlockFace.DOWN);
    		else
    			b = b.getRelative(direction,-4);
    	}
    	return false;
    	
    }
    
    public void loadingTorp(final boolean left, final Block b, final BlockFace torpHeading, final boolean lift) {
    	new Thread() {
    	    @Override
    	    public void run()
    	    {
    	    	
    		setPriority(Thread.MIN_PRIORITY);
    			try
    			{ 
    				
					sleep(1000);
					for( int i=0; i<16; i++ )
					{
						loadingTorpUpdate(left, b, torpHeading, lift, i);
						sleep(1000);
					}
					
    			} catch (InterruptedException e) 
    			{
    			}
    	    }
    	}.start();
        }
    
    
    public void loadingTorpUpdate(final boolean left, final Block b, final BlockFace torpHeading, final boolean lift, final int i)
    {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
 

		public void run()
	    {
	    	Block warhead = b;
	    	Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
			
	    	if( (left && !leftLoading) || (!left && !rightLoading) )
	    		return;
	    	
	    	
	    	if( i == 0 && lift )
	    	{
	    		warhead.setTypeId(0);
				warhead.getRelative(torpHeading,-1).setTypeId(0);
				warhead.getRelative(torpHeading,-2).setTypeId(0);
				warhead.getRelative(torpHeading,-3).setTypeId(0);
				
				if( testCraft != null )
			    {
			    	testCraft.addBlock(warhead, true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-1), true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-2), true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-3), true);
			    }
				
				warhead = warhead.getRelative(BlockFace.UP);
				if( cannonType == 7 )
				{
					warhead.setTypeIdAndData(35, (byte) 0x3, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0xD, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0xD, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}else if( cannonType == 8 )
				{
					warhead.setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0x0, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0x0, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}else
				{
					warhead.setTypeIdAndData(35, (byte) 0xE, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}
	    		
	    	}else if( i == 0 )
	    	{
	    		warhead.setTypeId(0);
				warhead.getRelative(torpHeading,-1).setTypeId(0);
				warhead.getRelative(torpHeading,-2).setTypeId(0);
				warhead.getRelative(torpHeading,-3).setTypeId(0);
				
				if( testCraft != null )
			    {
			    	testCraft.addBlock(warhead, true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-1), true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-2), true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-3), true);
			    }
				
	    		if( torpHeading == BlockFace.NORTH )
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.EAST);
					else
						warhead = warhead.getRelative(BlockFace.WEST);
				}else if( torpHeading == BlockFace.SOUTH )
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.WEST);
					else
						warhead = warhead.getRelative(BlockFace.EAST);
				}else if( torpHeading == BlockFace.EAST )
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.SOUTH);
					else
						warhead = warhead.getRelative(BlockFace.NORTH);
				}else
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.NORTH);
					else
						warhead = warhead.getRelative(BlockFace.SOUTH);
				}
	    		
	    		if( cannonType == 7 )
				{
					warhead.setTypeIdAndData(35, (byte) 0x3, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0xD, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0xD, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}else if( cannonType == 8 )
				{
		    		warhead.setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0x0, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0x0, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}else
				{
		    		warhead.setTypeIdAndData(35, (byte) 0xE, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}
	    	}else if( i == 1 && lift )
	    	{
	    		warhead = warhead.getRelative(BlockFace.UP);
	    		warhead.setTypeId(0);
				warhead.getRelative(torpHeading,-1).setTypeId(0);
				warhead.getRelative(torpHeading,-2).setTypeId(0);
				warhead.getRelative(torpHeading,-3).setTypeId(0);
				
				if( testCraft != null )
			    {
			    	testCraft.addBlock(warhead, true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-1), true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-2), true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-3), true);
			    }
				
	    		if( torpHeading == BlockFace.NORTH )
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.EAST);
					else
						warhead = warhead.getRelative(BlockFace.WEST);
				}else if( torpHeading == BlockFace.SOUTH )
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.WEST);
					else
						warhead = warhead.getRelative(BlockFace.EAST);
				}else if( torpHeading == BlockFace.EAST )
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.SOUTH);
					else
						warhead = warhead.getRelative(BlockFace.NORTH);
				}else
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.NORTH);
					else
						warhead = warhead.getRelative(BlockFace.SOUTH);
				}
	    		
	    		if( cannonType == 7 )
				{
					warhead.setTypeIdAndData(35, (byte) 0x3, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0xD, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0xD, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}else if( cannonType == 8 )
				{
					warhead.setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0x0, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0x0, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}else
				{
					warhead.setTypeIdAndData(35, (byte) 0xE, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}
	    	}else
	    	{
	    		int j=i;
	    		if( lift )
	    		{
	    			warhead = warhead.getRelative(BlockFace.UP);
	    			j = j - 1;
	    		}
	    		if( torpHeading == BlockFace.NORTH )
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.EAST);
					else
						warhead = warhead.getRelative(BlockFace.WEST);
				}else if( torpHeading == BlockFace.SOUTH )
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.WEST);
					else
						warhead = warhead.getRelative(BlockFace.EAST);
				}else if( torpHeading == BlockFace.EAST )
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.SOUTH);
					else
						warhead = warhead.getRelative(BlockFace.NORTH);
				}else
				{
					if( left )
						warhead = warhead.getRelative(BlockFace.NORTH);
					else
						warhead = warhead.getRelative(BlockFace.SOUTH);
				}
	    		
	    		
	    		for( int k=1; k<=j; k++ )
	    		{
	    			if( warhead.getRelative(torpHeading, k).getType() == Material.CLAY )
	    			{
	    				if( left )
	    					leftLoading = false;
	    				else
	    					rightLoading = false;
	    				
	    				if( testCraft != null)
    					{
    						testCraft.waitTorpLoading--;
    					}
	    				return;
	    			}
	    		}
	    		
	    		warhead = warhead.getRelative(torpHeading, j);
	    		Block oldWarhead = warhead.getRelative(torpHeading, -1);
    			oldWarhead.setTypeId(0);
    			oldWarhead.getRelative(torpHeading,-1).setTypeId(0);
    			oldWarhead.getRelative(torpHeading,-2).setTypeId(0);
    			oldWarhead.getRelative(torpHeading,-3).setTypeId(0);
    			
    			if( testCraft != null )
			    {
			    	testCraft.addBlock(oldWarhead, true);
			    	testCraft.addBlock(oldWarhead.getRelative(torpHeading,-1), true);
			    	testCraft.addBlock(oldWarhead.getRelative(torpHeading,-2), true);
			    	testCraft.addBlock(oldWarhead.getRelative(torpHeading,-3), true);
			    }
    			
    			
    			if( cannonType == 7 )
				{
					warhead.setTypeIdAndData(35, (byte) 0x3, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0xD, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0xD, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}else if( cannonType == 8 )
				{
					warhead.setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0x0, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0x0, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}else
				{
					warhead.setTypeIdAndData(35, (byte) 0xE, false);
					warhead.getRelative(torpHeading,-1).setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-2).setTypeIdAndData(35, (byte) 0x8, false);
					warhead.getRelative(torpHeading,-3).setTypeIdAndData(35, (byte) 0x7, false);
				}
    			
				if( testCraft != null )
			    {
			    	testCraft.addBlock(warhead, true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-1), true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-2), true);
			    	testCraft.addBlock(warhead.getRelative(torpHeading,-3), true);
			    }
	    	}
	    	if( i== 15 )
	    	{
	    		leftLoading = false;
				rightLoading = false;
				if( testCraft != null )
				{
					testCraft.waitTorpLoading--;
				}
	    	}
	    }
    	
    	});
    }

    
	public void openTorpedoDoors(Player p, boolean inner, boolean leftInner)
    {
    	if(p != null && checkProtectedRegion(p, p.getLocation()) )
    	{
    		p.sendMessage(ChatColor.RED + "You are in a protected region");
    		return;
    	}
    	
    	Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
    	if( !inner )
    	{
	    	Block a,b,c;
	    	a = loc.getBlock().getRelative(direction, 5);
	    	if( direction == BlockFace.NORTH || direction == BlockFace.SOUTH )
	    	{
	    		b = a.getRelative(BlockFace.EAST);
	    		c = a.getRelative(BlockFace.WEST);
	    	}else
	    	{
	    		b = a.getRelative(BlockFace.NORTH);
	    		c = a.getRelative(BlockFace.SOUTH);
	    	}
	    	
	    	
	    	if( checkOuterDoorClosed() )
	    	{
	    		if( checkInnerDoorClosed(true) )
	    		{
	    			if( checkInnerDoorClosed(false) )
	    			{
	    				b.setType(Material.AIR);
	    	    		c.setType(Material.AIR);
						if (p != null)
	    	    		p.sendMessage(ChatColor.GREEN + "Opening Outer Tube Doors!");
	    			}else
	    			{
						if (p != null)
	    				p.sendMessage(ChatColor.YELLOW + "Close BOTH inner doors before opening outer doors.");
	    			}
	    		}else
	    		{
					if (p != null)
	    			p.sendMessage(ChatColor.YELLOW + "Close BOTH inner doors before opening outer doors.");
	    		}
	    	}else
	    	{
	    		b.setType(Material.CLAY);
	    		c.setType(Material.CLAY);
				if (testCraft != null)
			    {
			    	testCraft.addBlock(b, true);
			    	testCraft.addBlock(c, true);
			    }
				if (p != null)
	    		p.sendMessage(ChatColor.GREEN + "Closing Outer Tube Doors!");
	    	}
    	}else ///inner doors
    	{
	    	Block a,b,c;
	    	a = loc.getBlock();
	    	if( direction == BlockFace.NORTH )
	    	{
	    		c = a.getRelative(BlockFace.EAST);
	    		b = a.getRelative(BlockFace.WEST);
	    	}else if( direction == BlockFace.SOUTH )
	    	{
	    		b = a.getRelative(BlockFace.EAST);
	    		c = a.getRelative(BlockFace.WEST);
	    	}else if( direction == BlockFace.EAST )
	    	{
	    		b = a.getRelative(BlockFace.NORTH);
	    		c = a.getRelative(BlockFace.SOUTH);
	    	}else
	    	{
	    		c = a.getRelative(BlockFace.NORTH);
	    		b = a.getRelative(BlockFace.SOUTH);
	    	}
	    	
	    	
	    	if( checkInnerDoorClosed(leftInner) )
	    	{
	    		if( checkOuterDoorClosed() )
	    		{
	    			
	    	    	
	    	    	if( leftInner )
	    	    	{
	    	    		b.setType(Material.AIR);
	    	    		if( b.getRelative(direction).getTypeId() >= 8 && b.getRelative(direction).getTypeId() <= 11)
	    	    			b.getRelative(direction).setType(Material.AIR);
	    	    		if( b.getRelative(direction,2).getTypeId() >= 8 && b.getRelative(direction,2).getTypeId() <= 11)
	    	    			b.getRelative(direction,2).setType(Material.AIR);
	    	    		if( b.getRelative(direction,3).getTypeId() >= 8 && b.getRelative(direction,3).getTypeId() <= 11)
	    	    			b.getRelative(direction,3).setType(Material.AIR);
	    	    		if( b.getRelative(direction,4).getTypeId() >= 8 && b.getRelative(direction,4).getTypeId() <= 11)
	    	    			b.getRelative(direction,4).setType(Material.AIR);
						if (p != null)
	    	    		p.sendMessage(ChatColor.GREEN + "Opening Left Inner Tube Door!");
	    	    	}
	    	    	else
	    	    	{
	    	    		c.setType(Material.AIR);
	    	    		if( c.getRelative(direction).getTypeId() >= 8 && c.getRelative(direction).getTypeId() <= 11)
	    	    			c.getRelative(direction).setType(Material.AIR);
	    	    		if( c.getRelative(direction,2).getTypeId() >= 8 && c.getRelative(direction,2).getTypeId() <= 11)
	    	    			c.getRelative(direction,2).setType(Material.AIR);
	    	    		if( c.getRelative(direction,3).getTypeId() >= 8 && c.getRelative(direction,3).getTypeId() <= 11)
	    	    			c.getRelative(direction,3).setType(Material.AIR);
	    	    		if( c.getRelative(direction,4).getTypeId() >= 8 && c.getRelative(direction,4).getTypeId() <= 11)
	    	    			c.getRelative(direction,4).setType(Material.AIR);
						if (p != null)
	    	    		p.sendMessage(ChatColor.GREEN + "Opening Right Inner Tube Door!");
	    	    	}
	    		}else
	    		{
					if (p != null)
	    			p.sendMessage(ChatColor.YELLOW + "Close the OUTER doors before opening inner doors.");
	    		}
	    	}else
	    	{
	    		if( leftInner )
	    		{
	    			b.setType(Material.CLAY);
	    			if( testCraft != null )
				    {
				    	testCraft.addBlock(b, true);
				    }
					if (p != null)
	    			p.sendMessage(ChatColor.GREEN + "Closing Left Inner Tube Door!");
	    		}
	    		else
	    		{
	    			c.setType(Material.CLAY);
	    			if( testCraft != null )
				    {
				    	testCraft.addBlock(c, true);
				    }
					if (p != null)
	    			p.sendMessage(ChatColor.GREEN + "Closing Right Inner Tube Door!");
	    		}
	    	}
    	}
    }
    
    public void setTorpedoMode(Player p)
    {
    	torpedoMode++;
    	torpedoMode = torpedoMode%3;
    	switch( torpedoMode )
    	{
    	case 0:
    		p.sendMessage(ChatColor.GREEN + "Firing Mode : " + ChatColor.YELLOW + "Left Tube");
    		break;
    	case 1:
    		p.sendMessage(ChatColor.GREEN + "Firing Mode : " + ChatColor.YELLOW + "Right Tube");
    		break;
    	case 2:
    		p.sendMessage(ChatColor.GREEN + "Firing Mode : " + ChatColor.YELLOW + "Both");
    		break;
    	}
    }
    
    public void fireTorpedoButton(Player p)
    {
    	if(p != null && checkProtectedRegion(p, p.getLocation()) )
    	{
    		p.sendMessage(ChatColor.RED + "You are in a protected region");
    		return;
    	}
    	
    	if( torpedoMode == 0 )
    	{
    		if( checkTubeLoaded(true) )
    		{
    			if( checkInnerDoorClosed(true) && !checkOuterDoorClosed() )
    			{
    				fireLeft(p);
    			}else
    			{
    				if (p != null)
    				p.sendMessage(ChatColor.YELLOW + "Left Tube: Open Outer Doors and Close Left Inner Door");
    			}
    		}else
    		{
				if (p != null)
    			p.sendMessage(ChatColor.RED + "Left Tube: Tube Not Loaded");
    		}
    		
    	}else if( torpedoMode == 1)
    	{
    		if( checkTubeLoaded(false) )
    		{
    			if( checkInnerDoorClosed(false) && !checkOuterDoorClosed() )
    			{
    				fireRight(p);
    			}else
    			{
					if (p != null)
    				p.sendMessage(ChatColor.YELLOW + "Right Tube: Open Outer Doors and Close Right Inner Door");
    			}
    			
    		}else
    		{
				if (p != null)
    			p.sendMessage(ChatColor.RED + "Right Tube: Tube Not Loaded");
    		}
    		
    	}else
    	{
    		if( checkTubeLoaded(true) && checkTubeLoaded(false) )
    		{
    			if( checkInnerDoorClosed(true) && checkInnerDoorClosed(false) && !checkOuterDoorClosed() )
    			{
    				fireBoth(p);
    			}else
    			{
					if (p != null)
    				p.sendMessage(ChatColor.YELLOW + "Both Tubes: Open Outer Doors and Close Both Inner Doors");
    			}
    			
    		}else
    		{
				if (p != null)
    			p.sendMessage(ChatColor.RED + "Both Tubes: Both Tubes Not Loaded");
    		}
    	}
    }
    
 
    
	public void fireTorpedoMk1(final Player p, final Block b, final BlockFace torpHeading, final int torpDepth, final int delayShoot, final boolean left){

    	final Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		final Weapon torp = new Weapon(b, torpHeading, torpDepth, 0);
		AimCannon.weapons.add(torp);
		
		if( torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getTypeId() == 68 )
		{
			Sign sign = (Sign) torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getState();
			String signLine0 =  sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
			int tubeNum=0;
			if( signLine0.equalsIgnoreCase("Tube") )
			{
				String tubeString = sign.getLine(1).trim().toLowerCase();
				tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
				if( !tubeString.isEmpty() )
				{
					try{
						tubeNum = Integer.parseInt(tubeString);
					}catch (NumberFormatException nfe)
					{
						tubeNum=0;
					}
				}
			}
			torp.tubeNum=tubeNum;
		}
		
		torp.setDepth = torpDepth;
		
		if( testCraft != null )
		{
			for( String s : testCraft.crewNames )
			{
				Player pl = nc.getServer().getPlayer(s);
				if( pl != null )
				{
					if( torp.tubeNum == 0 )
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube Fired!");
					else
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube " + torp.tubeNum + " Fired!");
				}
			}
		}
		
    	
    	
		new Thread(){
			
    	@Override
			public void run() {
    		
    		setPriority(Thread.MIN_PRIORITY);
				//taskNum = -1;
    			try{
    				sleep(delayShoot);
    				
    				
    				
    				
    				for( int i=0; i<150; i++ )
    				{
						fireTorpedoUpdateMk1(p, torp, i, testCraft, left);
						sleep(200);
					} 
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
    	}.start(); //, 20L);
    }
    
    public void fireTorpedoUpdateMk1(final Player p, final Weapon torp, final int i, final Craft firingCraft, final boolean left) {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
    	//new Thread() {
	  //  @Override
		public void run()
	    {

	    	if( !torp.dead )
	    	{
		    	if( torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -1).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -2).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -3).getTypeId() == 35 )
		    	{
		    		CraftMover.playWeaponSound(torp.warhead.getLocation(), Sound.ENTITY_PLAYER_BREATH, 2.0f, 0.8f);
		    		
					if( i > 15 )
					{
						if( torp.warhead.getY() > 62 )
			    		{
			    			torp.warhead.setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
			    		}else
			    		{
			    			torp.warhead.setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
			    		}
						
						if( checkProtectedRegion(p, torp.warhead.getLocation()) )
						{
							p.sendMessage(ChatColor.RED + "No torpedoes allowed in dock area!");
							torp.destroyWeapon();
							torp.dead = true;
							return;
						}
						
						
						
						
						//new position
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						int depthDifference = torp.setDepth - (63 - torp.warhead.getY());
						if( depthDifference > 0 )
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.DOWN);
						}else if( depthDifference < 0)
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.UP);
						}
						
						if( torp.turnProgress > -1 )
						{
							
							if( torp.turnProgress == 10 )
							{
								if( torp.hdg == BlockFace.NORTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.WEST;
									else
										torp.hdg = BlockFace.EAST;
								}else if( torp.hdg == BlockFace.SOUTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.EAST;
									else
										torp.hdg = BlockFace.WEST;
								}else if( torp.hdg == BlockFace.EAST )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.NORTH;
									else
										torp.hdg = BlockFace.SOUTH;
								}else
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.SOUTH;
									else
										torp.hdg = BlockFace.NORTH;
								}
								torp.rudder = -torp.rudder;
							}
							
							if( torp.turnProgress == 20 )
							{
								if( torp.doubleTurn )
								{
									torp.turnProgress = 0;
									torp.rudder = -torp.rudder;
									torp.doubleTurn = false;
								}else
								{
									torp.turnProgress = -1;
									torp.rudder = torp.rudderSetting;
								}
							}else
								torp.turnProgress += 1;
						}
						
						if( torp.rudder != 0 )
						{
							int dirMod  = Math.abs(torp.rudder);
							if( i % dirMod == 0 )
							{
								if( torp.rudder < 0 )
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, true);
								}else
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, false);
								}
							}
						}
						
						
						
						//check new position
						if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.STATIONARY_WATER || torp.warhead.getType() == Material.LAVA || torp.warhead.getType() == Material.STATIONARY_LAVA || torp.warhead.getType() == Material.AIR )
						{
		    				if( i == 149 )
							{
								if( torp.warhead.getY() > 62 )
								{
									torp.warhead.setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
								}else
								{
									torp.warhead.setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
								}
								p.sendMessage(ChatColor.RED + "Torpedo expired.");
								return;
							}
		    				
		    				torp.warhead.setTypeIdAndData(35, (byte) 0x8, false);
		    				torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x0, false);
		    				torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x0, false);
		    				torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
						}else if( torp.active ) ///detonate!
						{
							if( checkProtectedRegion(p, torp.warhead.getLocation()) )
							{
								p.sendMessage(ChatColor.RED + "No torpedo explosions in dock area.");
								return;
							}
							
							
							
							
							
							torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
							NavyCraft.explosion(7,  torp.warhead, false);
							torp.dead = true;
							torp.destroyWeapon();
	
							Craft checkCraft=null;
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
									if( checkCraft == null ) {
										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
										if( checkCraft == null ) {
											checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
										}
									}
								}
							}
							
							if( checkCraft == null )
								p.sendMessage(ChatColor.GREEN + "Torpedo hit unknown object!");
							else
								p.sendMessage(ChatColor.GREEN + "Torpedo hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
										
						
						}else
						{
							torp.dead = true;
							torp.destroyWeapon();
							p.sendMessage(ChatColor.RED + "Torpedo Dud (Too close).");
						}
						
						
					}
					else/// i <= 15
					{
						if( torp.warhead.getY() > 62 || i < 5 )
						{
							torp.warhead.setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
							
							if( i == 4  )
							{
								if( firingCraft != null )
								{
									firingCraft.addBlock(torp.warhead, true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -1), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -2), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -3), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -4), true);
	
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -5), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -6), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -7), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -8), true);
								}
							}
						}else
						{
							torp.warhead.setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
						}
						
	
						//Move torp
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						
						if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.AIR || torp.warhead.getType() == Material.STATIONARY_WATER )
						{
							torp.warhead.setTypeIdAndData(35, (byte) 0x8, false);
			    			torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x0, false);
			    			torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x0, false);
			    			torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
						}else
						{
							if( firingCraft != null )
							{
								firingCraft.waitTorpLoading--;
								if( left )
									leftLoading = false;
								else
									rightLoading = false;
								
								if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
									openTorpedoDoors(p, false, false);
							}else
							{
								if( left )
									leftLoading = false;
								else
									rightLoading = false;
								
								if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
									openTorpedoDoors(p, false, false);
							}
							p.sendMessage(ChatColor.RED + "Dud Torpedo! Too close.");
							torp.dead = true;
							torp.destroyWeapon();

						}
						
						
					}
					
					
		    		
		    	}else //else torp blocks missing, detonate
		    	{
		    		if( checkProtectedRegion(p, torp.warhead.getLocation()) )
					{
						p.sendMessage(ChatColor.RED + "No torpedo explosions in dock area.");
						return;
					}
					
		    		if( !torp.active )
		    		{
		    			p.sendMessage(ChatColor.RED + "Dud Torpedo! Too close.");
						torp.dead = true;
						torp.destroyWeapon();
						if( firingCraft != null )
						{
							firingCraft.waitTorpLoading--;
							if( left )
								leftLoading = false;
							else
								rightLoading = false;
							
							if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
								openTorpedoDoors(p, false, false);
						}else
						{
							if( left )
								leftLoading = false;
							else
								rightLoading = false;
							
							if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
								openTorpedoDoors(p, false, false);
						}
						return;
		    		}
					
					
					torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
					NavyCraft.explosion(7,  torp.warhead, false);
					torp.dead = true;
					torp.destroyWeapon();
					
					Craft checkCraft=null;
					checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
					if( checkCraft == null ) {
						checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
						if( checkCraft == null ) {
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
								}
							}
						}
					}
					
					if( checkCraft == null )
						p.sendMessage(ChatColor.RED + "Torpedo detonated prematurely!");
					else
						p.sendMessage(ChatColor.GREEN + "Torpedo hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
					
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}else
					{
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}
		    	}
		    	if( i == 15 )
				{
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}else
					{
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}
					torp.active = true;
				}
	    	}

	    }
    	}
    	
	);

    }
    
    

    
	public void fireTorpedoMk2(final Player p, final Block b, final BlockFace torpHeading, final int torpDepth, final int delayShoot, final boolean left){
    	//final int taskNum;
    	//int taskNum = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
    	final Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		final Weapon torp = new Weapon(b, torpHeading, torpDepth, 0);
		AimCannon.weapons.add(torp);
		
		if( torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getTypeId() == 68 )
		{
			Sign sign = (Sign) torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getState();
			String signLine0 =  sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
			int tubeNum=0;
			if( signLine0.equalsIgnoreCase("Tube") )
			{
				String tubeString = sign.getLine(1).trim().toLowerCase();
				tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
				if( !tubeString.isEmpty() )
				{
					try{
						tubeNum = Integer.parseInt(tubeString);
					}catch (NumberFormatException nfe)
					{
						tubeNum=0;
					}
				}
			}
			torp.tubeNum=tubeNum;
		}
		
		if( testCraft != null && testCraft.tubeMk1FiringDisplay > -1 )
		{
			if( testCraft.tubeMk1FiringDepth > -1)
				torp.setDepth = testCraft.tubeMk1FiringDepth;
			else
				torp.setDepth = torpDepth;
			Player onScopePlayer=null;
			for( Periscope per: testCraft.periscopes )
			{
				if( per.user != null )
				{
					onScopePlayer = per.user;
					break;
				}
			}
			

			float rotation=0;
			int torpRotation=0;
			if( direction == BlockFace.SOUTH )
				torpRotation=180;
			else if( direction == BlockFace.WEST )
				torpRotation=270;
			else if( direction == BlockFace.EAST )
				torpRotation=90;
			else if( direction == BlockFace.NORTH )
				torpRotation=0;
			
			if( onScopePlayer != null && testCraft.tubeMk1FiringMode == -1 ) {
				rotation = (float) Math.PI * onScopePlayer.getLocation().getYaw() / 180f;
				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);
				transp.add(Material.STATIONARY_WATER);
				transp.add(Material.WATER);
				transp.add(Material.LAVA);
				transp.add(Material.STATIONARY_LAVA);
				transp.add(Material.WOOL);
				int depth = 63 - (onScopePlayer.getTargetBlock(transp, 1000).getY());
				if (depth < 0) depth = 0;
				torp.setDepth = depth;
			} else if( testCraft.lastPeriscopeYaw != -9999 && testCraft.tubeMk1FiringMode == -1 ) {
				rotation = (float) Math.PI * testCraft.lastPeriscopeYaw / 180f;
				int depth = 63 - (testCraft.lastPeriscopeBlock.getY());
				if (depth < 0) depth = 0;
				torp.setDepth = depth;
			} else
			{
				rotation = (float) Math.PI * (torpRotation+180f) / 180f;
				
			}
			
			if( left )
				rotation -= testCraft.tubeMk1FiringSpread*Math.PI/180f;
			else
				rotation += testCraft.tubeMk1FiringSpread*Math.PI/180f;
			
			float nx = -(float) Math.sin(rotation);
			float nz = (float) Math.cos(rotation);
			
		////north
			
			//p.sendMessage("torpRotation=" + torpRotation + " rotation=" + rotation);
			
					
			if( torpRotation%360 == 0 )
			{
				if( nx > 0.5 )
				{
					torp.rudder = 1;
					torp.turnProgress = 0;
					if(  Math.abs(nz) > .07 )
					{
						torp.rudderSetting = (int)(1.0f / nz);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}
				else if( nx < -0.5 )
				{
					torp.rudder = -1;
					torp.turnProgress = 0;
					if( Math.abs(nz) > .07 )
					{
						torp.rudderSetting = -(int)(1.0f / nz);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}else if( nz < 0 )
				{
					if(  Math.abs(nx) > .07 )
					{
						torp.rudder = (int)(1.0f / nx);
						if( torp.rudder > 10 )
							torp.rudder = 10;
						else if( torp.rudder < -10 )
							torp.rudder = -10;
						torp.rudderSetting = torp.rudder;
					}
				}else
				{
					if( nx < 0 )
					{
						torp.doubleTurn = true;
						torp.rudder = -1;
						torp.turnProgress = 0;
					}else if( nx > 0 )
					{
						torp.doubleTurn = true;
						torp.rudder = 1;
						torp.turnProgress = 0;
					}
					if(  Math.abs(nx) > .07 )
					{
						torp.rudderSetting = (int)(1.0f / -nx);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}
				
			
			//////south
			}else if( torpRotation%360 == 180 )
			{
				
				if( nx > 0.5 )
				{
					torp.rudder = -1;
					torp.turnProgress = 0;
					if(  Math.abs(nz) > .07 )
					{
						torp.rudderSetting = (int)(1.0f / nz);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}
				else if( nx < -0.5 )
				{
					torp.rudder = 1;
					torp.turnProgress = 0;
					if(  Math.abs(nz) > .07 )
					{
						torp.rudderSetting = (int)(1.0f / -nz);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}else if( nz > 0 )
				{
					if(  Math.abs(nx) > .07 )
					{
						torp.rudder = (int)(1.0f / -nx);
						if( torp.rudder > 10 )
							torp.rudder = 10;
						else if( torp.rudder < -10 )
							torp.rudder = -10;
						torp.rudderSetting = torp.rudder;
					}
				}else
				{
					if( nx < 0 )
					{
						torp.doubleTurn = true;
						torp.rudder = 1;
						torp.turnProgress = 0;
					}else if( nx > 0 )
					{
						torp.doubleTurn = true;
						torp.rudder = -1;
						torp.turnProgress = 0;
					}
					if(  Math.abs(nx) > .07 )
					{
						torp.rudderSetting = (int)(1.0f / nx);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}
			//////east
			}else if( torpRotation%360 == 90 )
			{
				
				if( nz > 0.5 )
				{
					torp.rudder = 1;
					torp.turnProgress = 0;
					if(  Math.abs(nx) > .07 )
					{
						torp.rudderSetting = (int)(1.0f / -nx);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}
				else if( nz < -0.5 )
				{
					torp.rudder = -1;
					torp.turnProgress = 0;
					if(  Math.abs(nx) > .07 )
					{
						torp.rudderSetting = (int)(1.0f / nx);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}else if( nx > 0 )
				{
					if(  Math.abs(nz) > .07 )
					{
						torp.rudder = (int)(1.0f / nz);
						if( torp.rudder > 10 )
							torp.rudder = 10;
						else if( torp.rudder < -10 )
							torp.rudder = -10;
						torp.rudderSetting = torp.rudder;
					}
				}else
				{
					if( nz < 0 )
					{
						torp.doubleTurn = true;
						torp.rudder = -1;
						torp.turnProgress = 0;
					}else if( nz > 0 )
					{
						torp.doubleTurn = true;
						torp.rudder = 1;
						torp.turnProgress = 0;
					}
					if(  Math.abs(nz) > .07 )
					{
						torp.rudderSetting = (int)(1.0f / -nz);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}
			//////////////west
			}else if( torpRotation%360 == 270 )
			{
				if( nz > 0.5 )
				{
					torp.rudder = -1;
					torp.turnProgress = 0;
					if(  Math.abs(nx) > .07 )
					{
						torp.rudderSetting = (int)(1.0f / -nx);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}
				else if( nz < -0.5 )
				{
					torp.rudder = 1;
					torp.turnProgress = 0;
					if(  Math.abs(nx) > .07 )
					{
						torp.rudderSetting = (int)(1.0f / nx);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}else if( nx < 0 )
				{
					if(  Math.abs(nz) > .07 )
					{
						torp.rudder = (int)(1.0f / -nz);
						if( torp.rudder > 10 )
							torp.rudder = 10;
						else if( torp.rudder < -10 )
							torp.rudder = -10;
						torp.rudderSetting = torp.rudder;
					}
				}else
				{
					if( nz < 0 )
					{
						torp.doubleTurn = true;
						torp.rudder = 1;
						torp.turnProgress = 0;
					}else if( nz > 0 )
					{
						torp.doubleTurn = true;
						torp.rudder = -1;
						torp.turnProgress = 0;
					}
					if(  Math.abs(nz) > .07 )
					{
						torp.rudderSetting = (int)(1.0f / nz);
						if( torp.rudderSetting > 10 )
							torp.rudderSetting = 10;
						else if( torp.rudderSetting < -10 )
							torp.rudderSetting = -10;
					}
				}
			}
			
			
			for( String s : testCraft.crewNames )
			{
				Player pl = nc.getServer().getPlayer(s);
				if( pl != null )
				{
					if( torp.tubeNum == 0 )
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube Fired!");
					else
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube " + torp.tubeNum + " Fired!");
				}
			}
		}else
		{

				torp.setDepth = torpDepth;
		}
    	
    	
    	
		new Thread(){
			
    	@Override
			public void run() {
    		
    		setPriority(Thread.MIN_PRIORITY);
				//taskNum = -1;
    			try{
    				sleep(delayShoot);
    				
    				
    				
    				
    				for( int i=0; i<250; i++ )
    				{
						fireTorpedoUpdateMk2(p, torp, i, testCraft, left);
						sleep(160);
					} 
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
    	}.start(); //, 20L);
    }
    
    public void fireTorpedoUpdateMk2(final Player p, final Weapon torp, final int i, final Craft firingCraft, final boolean left) {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
    	//new Thread() {
	  //  @Override
		public void run()
	    {
	    	//getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
	    //	}
		//setPriority(Thread.MIN_PRIORITY);
			//try
			//{ 
	    	if( !torp.dead )
	    	{
		    	if( torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -1).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -2).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -3).getTypeId() == 35 )
		    	{
		    		CraftMover.playWeaponSound(torp.warhead.getLocation(), Sound.ENTITY_PLAYER_BREATH, 2.0f, 0.8f);
					if( i > 15 )
					{
						if( torp.warhead.getY() > 62 )
			    		{
			    			torp.warhead.setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
			    		}else
			    		{
			    			torp.warhead.setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
			    		}
						
						if( checkProtectedRegion(p, torp.warhead.getLocation()) )
						{
							p.sendMessage(ChatColor.RED + "No torpedoes allowed in dock area!");
							torp.destroyWeapon();
							torp.dead = true;
							return;
						}
						
						
						
						
						//new position
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						int depthDifference = torp.setDepth - (63 - torp.warhead.getY());
						if( depthDifference > 0 )
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.DOWN);
						}else if( depthDifference < 0)
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.UP);
						}
						
						if( torp.turnProgress > -1 )
						{
							
							if( torp.turnProgress == 10 )
							{
								if( torp.hdg == BlockFace.NORTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.WEST;
									else
										torp.hdg = BlockFace.EAST;
								}else if( torp.hdg == BlockFace.SOUTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.EAST;
									else
										torp.hdg = BlockFace.WEST;
								}else if( torp.hdg == BlockFace.EAST )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.NORTH;
									else
										torp.hdg = BlockFace.SOUTH;
								}else
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.SOUTH;
									else
										torp.hdg = BlockFace.NORTH;
								}
								torp.rudder = -torp.rudder;
							}
							
							if( torp.turnProgress == 20 )
							{
								if( torp.doubleTurn )
								{
									torp.turnProgress = 0;
									torp.rudder = -torp.rudder;
									torp.doubleTurn = false;
								}else
								{
									torp.turnProgress = -1;
									torp.rudder = torp.rudderSetting;
								}
							}else
								torp.turnProgress += 1;
						}
						
						if( torp.rudder != 0 )
						{
							int dirMod  = Math.abs(torp.rudder);
							if( i % dirMod == 0 )
							{
								if( torp.rudder < 0 )
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, true);
								}else
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, false);
								}
							}
						}
						
						
						
						//check new position
						if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.STATIONARY_WATER || torp.warhead.getType() == Material.LAVA || torp.warhead.getType() == Material.STATIONARY_LAVA || torp.warhead.getType() == Material.AIR )
						{
		    				if( i == 249 )
							{
								if( torp.warhead.getY() > 62 )
								{
									torp.warhead.setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
								}else
								{
									torp.warhead.setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
								}
								p.sendMessage(ChatColor.RED + "Torpedo expired.");
								return;
							}
		    				
		    				torp.warhead.setTypeIdAndData(35, (byte) 0xE, false);
		    				torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x8, false);
		    				torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x8, false);
		    				torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
						}else if( torp.active ) ///detonate!
						{
							if( checkProtectedRegion(p, torp.warhead.getLocation()) )
							{
								p.sendMessage(ChatColor.RED + "No torpedo explosions in dock area.");
								torp.dead = true;
								torp.destroyWeapon();
								return;
							}
	
							
							torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
							NavyCraft.explosion(8,  torp.warhead, false);
							torp.dead = true;
							torp.destroyWeapon();
							
							Craft checkCraft=null;
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
									if( checkCraft == null ) {
										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
										if( checkCraft == null ) {
											checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
										}
									}
								}
							}
							
							if( checkCraft == null )
								p.sendMessage(ChatColor.YELLOW + "Torpedo hit unknown object!");
							else
								p.sendMessage(ChatColor.GREEN + "Torpedo hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
							
						}else
						{
							p.sendMessage(ChatColor.RED + "Torpedo Dud (Too close).");
							torp.dead = true;
							torp.destroyWeapon();
						}
						
						
					}
					else/// i <= 15
					{
						if( torp.warhead.getY() > 62 || i < 5 )
						{
							torp.warhead.setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
							
							if( i == 4  )
							{
								if( firingCraft != null )
								{
									firingCraft.addBlock(torp.warhead, true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -1), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -2), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -3), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -4), true);
	
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -5), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -6), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -7), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -8), true);
								}
							}
						}else
						{
							torp.warhead.setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
						}
						
						
						
						
						//Move torp
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						
						if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.AIR || torp.warhead.getType() == Material.STATIONARY_WATER )
						{
							torp.warhead.setTypeIdAndData(35, (byte) 0xE, false);
			    			torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x8, false);
			    			torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x8, false);
			    			torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
						}else
						{
							if( firingCraft != null )
							{
								firingCraft.waitTorpLoading--;
								if( left )
									leftLoading = false;
								else
									rightLoading = false;
								
								if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
									openTorpedoDoors(p, false, false);
							}else
							{
								if( left )
									leftLoading = false;
								else
									rightLoading = false;
								
								if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
									openTorpedoDoors(p, false, false);
							}
							torp.dead = true;
							torp.destroyWeapon();
							p.sendMessage(ChatColor.RED + "Dud Torpedo! Too close.");
						}
	
					}
					
					
		    		
		    	}else //torp blocks missing, detonate
		    	{
		    		if( checkProtectedRegion(p, torp.warhead.getLocation()) )
					{
						p.sendMessage(ChatColor.RED + "No torpedo explosions in dock area.");
						return;
					}
					
		    		if( !torp.active )
		    		{
		    			p.sendMessage(ChatColor.RED + "Dud Torpedo! Too close.");
						torp.dead = true;
						torp.destroyWeapon();
						if( firingCraft != null )
						{
							firingCraft.waitTorpLoading--;
							if( left )
								leftLoading = false;
							else
								rightLoading = false;
							
							if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
								openTorpedoDoors(p, false, false);
						}else
						{
							if( left )
								leftLoading = false;
							else
								rightLoading = false;
							
							if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
								openTorpedoDoors(p, false, false);
						}
						return;
		    		}
					
					
					torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
					NavyCraft.explosion(8,  torp.warhead, false);
					torp.dead = true;
					torp.destroyWeapon();
					
					Craft checkCraft=null;
					checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
					if( checkCraft == null ) {
						checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
						if( checkCraft == null ) {
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
								}
							}
						}
					}
					
					if( checkCraft == null )
						p.sendMessage(ChatColor.RED + "Torpedo detonated prematurely!");
					else
						p.sendMessage(ChatColor.GREEN + "Torpedo hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
					
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}else
					{
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}
		    	}
		    	
		    	if( i == 15 )
				{
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}else
					{
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}
					torp.active = true;
				}
	    	}

	    }
    	}
    	
	);

    }
    
    
    
	public void fireTorpedoMk3(final Player p, final Block b, final BlockFace torpHeading, final int torpDepth, final int delayShoot, final boolean left){
    	//final int taskNum;
    	//int taskNum = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
    	final Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		final Weapon torp = new Weapon(b, torpHeading, torpDepth);
		AimCannon.weapons.add(torp);
		
		if( torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getTypeId() == 68 )
		{
			Sign sign = (Sign) torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getState();
			String signLine0 =  sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
			int tubeNum = -2;
			if( left )
				tubeNum = -1;
			if( signLine0.equalsIgnoreCase("Tube") )
			{
				String tubeString = sign.getLine(1).trim().toLowerCase();
				tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
				if( !tubeString.isEmpty() )
				{
					try{
						tubeNum = Integer.parseInt(tubeString);
					}catch (NumberFormatException nfe)
					{

					}
				}
			}
			torp.tubeNum=tubeNum;
		}
		
		if( testCraft != null && torp.tubeNum > 0 && testCraft.tubeFiringMode.containsKey(torp.tubeNum) )
		{
			torp.setDepth = testCraft.tubeFiringDepth.get(torp.tubeNum);
			Player onScopePlayer=null;
			for( Periscope per: testCraft.periscopes )
			{
				if( per.user != null )
				{
					onScopePlayer = per.user;
					break;
				}
			}

			float rotation=0;
			if( onScopePlayer != null && testCraft.tubeFiringMode.get(torp.tubeNum) == -1 ) //firing periscope mode, player on scope
			{
				rotation = (float) Math.PI * onScopePlayer.getLocation().getYaw() / 180f;
				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);
				transp.add(Material.STATIONARY_WATER);
				transp.add(Material.WATER);
				transp.add(Material.LAVA);
				transp.add(Material.STATIONARY_LAVA);
				transp.add(Material.WOOL);
				int depth = 63 - (onScopePlayer.getTargetBlock(transp, 1000).getY());
				if (depth < 0) depth = 0;
				torp.setDepth = depth;
				torp.isGuided = true;
			}else if( testCraft.lastPeriscopeYaw != -9999 && testCraft.tubeFiringMode.get(torp.tubeNum) == -1 ) //firing periscope mode, last used periscope yaw
			{
				rotation = (float) Math.PI * testCraft.lastPeriscopeYaw / 180f;
				int depth = 63 - (testCraft.lastPeriscopeBlock.getY());
				if (depth < 0) depth = 0;
				torp.setDepth = depth;
				torp.isGuided = true;
			}else if( testCraft.tubeFiringMode.get(torp.tubeNum) >= 0 )  //firing at target
			{
				int targetID = testCraft.fireControlTargets.get(torp.tubeNum);
				Craft targetCraft = testCraft.sonarTargetIDs2.get(targetID);
				float xDist = targetCraft.getLocation().getBlockX() - loc.getBlockX();
				float zDist = targetCraft.getLocation().getBlockZ() - loc.getBlockZ();
				int depth = 63 - (targetCraft.minY + targetCraft.sizeY/3);
				if (depth < 0) depth = 0;
				torp.setDepth = depth;
				//rotation = torp.calculateRelBearing(xDist, zDist);
				System.out.println(torp.calculateRelBearing(xDist, zDist)-180f);
				rotation = (float) Math.PI * (torp.calculateRelBearing(xDist, zDist)-180f) / 180f;
			}
			else //firing straight mode
			{
				rotation = (float) Math.PI * (torp.weaponRotation()+180f) / 180f;
				
			}
			
			torp.calculateHeading(rotation);
			testCraft.tubeFiringMode.put(torp.tubeNum, -3);
			testCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
		}
		
		if( testCraft != null )
		{
			for( String s : testCraft.crewNames )
			{
				Player pl = nc.getServer().getPlayer(s);
				if( pl != null )
				{
					if( torp.tubeNum == 0 )
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube Fired! - Depth: " + torp.setDepth);
					else
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube " + torp.tubeNum + " Fired! - Depth: " + torp.setDepth);
				}
			}
		}
    	
    	
    	
		new Thread(){
			
    	@Override
			public void run() {
    		
    		setPriority(Thread.MIN_PRIORITY);
				//taskNum = -1;
    			try{
    				sleep(delayShoot);
    				
    				
    				
    				
    				for( int i=0; i<500; i++ )
    				{
						fireTorpedoUpdateMk3(p, torp, i, testCraft, left);
						sleep(160);
					} 
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
    	}.start(); //, 20L);
    }
    
    public void fireTorpedoUpdateMk3(final Player p, final Weapon torp, final int i, final Craft firingCraft, final boolean left) {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
    	//new Thread() {
	  //  @Override
		public void run()
	    {

	    	if( !torp.dead )
	    	{
		    	if( torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -1).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -2).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -3).getTypeId() == 35 )
		    	{
		    		torp.warhead.getWorld().playSound(torp.warhead.getLocation(), Sound.ENTITY_PLAYER_BREATH, 2.0f, 0.8f);
					if( i > 15 )
					{
						if( torp.warhead.getY() > 62 )
			    		{
			    			torp.warhead.setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
			    		}else
			    		{
			    			torp.warhead.setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
			    		}
						
						if( checkProtectedRegion(p, torp.warhead.getLocation()) )
						{
							if (p != null)
							p.sendMessage(ChatColor.RED + "No torpedoes allowed in dock area!");
							if (firingCraft != null && firingCraft.tubeFiringMode.get(torp.tubeNum) == -3 ) {
							firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
							firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
							firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
							firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
							firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
							firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
							firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
							}
							torp.destroyWeapon();
							torp.dead = true;
							return;
						}
						
						//check sub update
						if( firingCraft != null && torp.tubeNum > 0 && firingCraft.tubeFiringMode.containsKey(torp.tubeNum) )
						{
							if( firingCraft.tubeFiringHeading.get(torp.tubeNum) != torp.torpSetHeading )
							{
								torp.torpSetHeading = firingCraft.tubeFiringHeading.get(torp.tubeNum);
							}
							if( firingCraft.tubeFiringArmed.get(torp.tubeNum) != torp.active )
							{
								torp.active = firingCraft.tubeFiringArmed.get(torp.tubeNum);
							}
							if( firingCraft.tubeFiringAuto.get(torp.tubeNum) != torp.auto )
							{
								torp.auto = firingCraft.tubeFiringAuto.get(torp.tubeNum);
							}
							
							if( !firingCraft.tubeFiringArmed.get(torp.tubeNum) && (i == firingCraft.tubeFiringArm.get(torp.tubeNum) || (i==11 && firingCraft.tubeFiringArm.get(torp.tubeNum)==10)) )
							{
								firingCraft.tubeFiringArmed.put(torp.tubeNum,true);
								torp.active = true;
							}
						}
						
						
						//new position
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						int depthDifference = torp.setDepth - (63 - torp.warhead.getY());
						if( depthDifference > 0 )
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.DOWN);
						}else if( depthDifference < 0)
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.UP);
						}
						
						if( torp.turnProgress > -1 )
						{
							
							if( torp.turnProgress == 10 )
							{
								if( torp.hdg == BlockFace.NORTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.WEST;
									else
										torp.hdg = BlockFace.EAST;
								}else if( torp.hdg == BlockFace.SOUTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.EAST;
									else
										torp.hdg = BlockFace.WEST;
								}else if( torp.hdg == BlockFace.EAST )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.NORTH;
									else
										torp.hdg = BlockFace.SOUTH;
								}else
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.SOUTH;
									else
										torp.hdg = BlockFace.NORTH;
								}
								torp.rudder = -torp.rudder;
							}
							
							if( torp.turnProgress == 20 )
							{
								if( torp.doubleTurn )
								{
									torp.turnProgress = 0;
									torp.rudder = -torp.rudder;
									torp.doubleTurn = false;
								}else
								{
									torp.turnProgress = -1;
									torp.rudder = torp.rudderSetting;
								}
							}else
								torp.turnProgress += 1;
						}
						
						if( torp.rudder != 0 )
						{
							int dirMod  = Math.abs(torp.rudder);
							if( i % dirMod == 0 )
							{
								if( torp.rudder < 0 )
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, true);
								}else
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, false);
								}
							}
						}
						
						
						
						//check new position
						if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.STATIONARY_WATER || torp.warhead.getType() == Material.LAVA || torp.warhead.getType() == Material.STATIONARY_LAVA || torp.warhead.getType() == Material.AIR )
						{
		    				if( i == 499 )
							{
								if( torp.warhead.getY() > 62 )
								{
									torp.warhead.setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
								}else
								{
									torp.warhead.setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
								}

								if (p != null)
								p.sendMessage(ChatColor.RED + "Torpedo expired.");

								if( firingCraft != null && torp.tubeNum > 0 ) {
									firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
									firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
									firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
									firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
									firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
									firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
									firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
								}
								torp.destroyWeapon();
								return;
							}
		    				
		    				torp.warhead.setTypeIdAndData(35, (byte) 0x3, false);
		    				torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0xD, false);
		    				torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0xD, false);
		    				torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
		    				
		    				if( i >= 40 && i%torp.pingDelay == 0 && torp.turnProgress == -1 && firingCraft != null )
		    					torp.doPingTrack(firingCraft);
		    				
		    				if (firingCraft != null) {
		    				Block targetBlock = null;
		    				Player onScopePlayer=null;
		    				for( Periscope per: firingCraft.periscopes )
		    				{
		    					if( per.user != null )
		    					{
		    						onScopePlayer = per.user;
		    						break;
		    					}
		    				}
		    				
		    				if( onScopePlayer != null && torp.isGuided ) {
		    					Set<Material> transp = new HashSet<>();
		    					transp.add(Material.AIR);
		    					transp.add(Material.STATIONARY_WATER);
		    					transp.add(Material.WATER);
								transp.add(Material.LAVA);
								transp.add(Material.STATIONARY_LAVA);
								transp.add(Material.WOOL);
		    					targetBlock = onScopePlayer.getTargetBlock(transp, 1000);
		    				} else if( firingCraft.lastPeriscopeBlock != null && torp.isGuided )
		    					targetBlock = firingCraft.lastPeriscopeBlock;

		    				if( i >= 40 && i%torp.pingDelay == 0 && torp.turnProgress == -1 && targetBlock != null )
		    				torp.doBlockTrack(targetBlock);
		    			}
		    				
						}else if( torp.active ) ///detonate!
						{
							if( checkProtectedRegion(p, torp.warhead.getLocation()) )
							{
								if (p != null)
								p.sendMessage(ChatColor.RED + "No torpedo explosions in dock area.");

								torp.destroyWeapon();
								torp.dead=true;
								if( firingCraft != null )
								{
									firingCraft.waitTorpLoading--;
									if( left )
										leftLoading = false;
									else
										rightLoading = false;
									
									if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
										openTorpedoDoors(p, false, false);
								}
					    		if( firingCraft != null && torp.tubeNum > 0 && firingCraft.tubeFiringMode.get(torp.tubeNum) == -3 )
					    		{
						    		firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
									firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
									firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
									firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
									firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
									firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
									firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
									//CraftMover cm = new CraftMover( firingCraft, plugin);
									//cm.structureUpdate(null);
					    		}
								return;
							}

							torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
							NavyCraft.explosion(10,  torp.warhead, false);
							torp.dead=true;
							torp.destroyWeapon();
							
							
							Craft checkCraft=null;
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
									if( checkCraft == null ) {
										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
										if( checkCraft == null ) {
											checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
										}
									}
								}
							}
							if (p != null) {
								if (checkCraft == null)
									p.sendMessage(ChatColor.GREEN + "Torpedo hit unknown object!");
								else
									p.sendMessage(ChatColor.GREEN + "Torpedo hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
							}

							if( firingCraft != null && torp.tubeNum > 0 ) {
								firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
								firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
								firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
								firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
								firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
								firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
								firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
							}
							//CraftMover cm = new CraftMover( firingCraft, plugin);
							//cm.structureUpdate(null);
						}else
						{
							if (p != null)
							p.sendMessage(ChatColor.RED + "Torpedo Dud (Inactive).");

							if( firingCraft != null && torp.tubeNum > 0 ) {
								firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
								firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
								firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
								firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
								firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
								firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
								firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
							}
							torp.destroyWeapon();
							//CraftMover cm = new CraftMover( firingCraft, plugin);
							//cm.structureUpdate(null);
						}
						
						
					}
					else/// i <= 15
					{
						if( torp.warhead.getY() > 62 || i < 5 )
						{
							torp.warhead.setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
							
							if( i == 4  )
							{
								if( firingCraft != null )
								{
									firingCraft.addBlock(torp.warhead, true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -1), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -2), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -3), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -4), true);
	
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -5), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -6), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -7), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -8), true);
								}
							}
						}else
						{
							torp.warhead.setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
						}
						
						//Move torp
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						
						if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.AIR || torp.warhead.getType() == Material.STATIONARY_WATER )
						{
							torp.warhead.setTypeIdAndData(35, (byte) 0x3, false);
			    			torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0xD, false);
			    			torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0xD, false);
			    			torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
						}else
						{
							if (p != null)
							p.sendMessage(ChatColor.RED + "Dud Torpedo! Too close!");

							torp.dead=true;
							torp.destroyWeapon();
							if( firingCraft != null && torp.tubeNum > 0 ) {
								firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
								firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
								firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
								firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
								firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
								firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
								firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
								
								firingCraft.waitTorpLoading--;
								if( left )
									leftLoading = false;
								else
									rightLoading = false;
								
								if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
									openTorpedoDoors(p, false, false);
								
								
							}
						}
					}
					
					
		    		
		    	}else
		    	{
		    		if( checkProtectedRegion(p, torp.warhead.getLocation()) )
					{
						if (p != null)
						p.sendMessage(ChatColor.RED + "No torpedo explosions in dock area.");
						return;
					}
					
		    		if( !torp.active )
		    		{
						if (p != null)
		    			p.sendMessage(ChatColor.RED + "Dud Torpedo! Too close!");
						torp.dead = true;
						if( firingCraft != null )
						{
							firingCraft.waitTorpLoading--;
							if( left )
								leftLoading = false;
							else
								rightLoading = false;
							
							if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
								openTorpedoDoors(p, false, false);
						}else
						{
							if( left )
								leftLoading = false;
							else
								rightLoading = false;
							
							if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
								openTorpedoDoors(p, false, false);
						}
			    		if( firingCraft != null && torp.tubeNum > 0 && firingCraft.tubeFiringMode.get(torp.tubeNum) == -3 )
			    		{
				    		firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
							firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
							firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
							firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
							firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
							firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
							firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
							//CraftMover cm = new CraftMover( firingCraft, plugin);
							//cm.structureUpdate(null);
			    		}
						return;
		    		}

					torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
					NavyCraft.explosion(10,  torp.warhead, false);
					torp.dead = true;
					
					Craft checkCraft=null;
					checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
					if( checkCraft == null ) {
						checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
						if( checkCraft == null ) {
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
								}
							}
						}
					}
					if (p != null) {
						if (checkCraft == null)
							p.sendMessage(ChatColor.RED + "Torpedo detonated prematurely!");
						else
							p.sendMessage(ChatColor.GREEN + "Torpedo hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
					}
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}else
					{
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}
		    		if( firingCraft != null && torp.tubeNum > 0 && firingCraft.tubeFiringMode.get(torp.tubeNum) == -3 )
		    		{
			    		firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
						firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
						firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
						firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
						firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
						firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
						firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
						//CraftMover cm = new CraftMover( firingCraft, plugin);
						//cm.structureUpdate(null);
		    		}
		    	}
		    	
		    	if( i == 15 )
				{
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}else
					{
						if( left )
							leftLoading = false;
						else
							rightLoading = false;
						
						if( !leftLoading && !rightLoading && !checkOuterDoorClosed() )
							openTorpedoDoors(p, false, false);
					}
					torp.active = true;
				}
	
		    }
	    	}
    	}
    	
	);

    }
    
    public void fireLeft(Player p)
    {
    	Block b;
    	b = getDirectionFromRelative(loc.getBlock(), direction, true).getRelative(direction,4);
    	Craft testCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    	leftLoading = true;
		if( testCraft != null )
		{
			testCraft.waitTorpLoading++;
		}
		if( cannonType == 7 )
			fireTorpedoMk3(p, b, direction, depth, 0, true);
		else if( cannonType == 8 )
			//fireTorpedo2(p, b, direction, depth, 500, getTubeBlockFace(true), 0);
			fireTorpedoMk1(p, b, direction, depth, 0, true);
		else if ( cannonType == 3)
			fireTorpedoMk2(p, b, direction, depth, 0, true);
		else if (cannonType == 11)
			fireMissileMk1(p, b, direction, depth, 0, 0, true, false);
		else if (cannonType == 12)
			fireMissileMk2(p, b, direction, depth, 0, 0, true, false);
		else
			fireMissileMk3(p, b, direction, depth, 0, 0, true, false);
    }
    
    public void fireRight(Player p)
    {
    	Block b;
    	b = getDirectionFromRelative(loc.getBlock(), direction, false).getRelative(direction,4);
    	Craft testCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    	rightLoading = true;
		if( testCraft != null )
		{
			testCraft.waitTorpLoading++;
		}
		if( cannonType == 7 )
			fireTorpedoMk3(p, b, direction, depth, 0, false);
		else if( cannonType == 8 )
			fireTorpedoMk1(p, b, direction, depth, 0, false);
		else if ( cannonType == 3 )
			//fireTorpedo(p, b, direction, depth, 500, getTubeBlockFace(false), 0);
			fireTorpedoMk2(p, b, direction, depth, 0, false);
		else if (cannonType == 11)
			fireMissileMk1(p, b, direction, depth, 0, 0, false, false);
		else if (cannonType == 12)
			fireMissileMk2(p, b, direction, depth, 0, 0, false, false);
		else
			fireMissileMk3(p, b, direction, depth, 0, 0, false, false);
    }
    
    public void fireBoth(Player p)
    {
    	Block b;
    	Craft testCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    	rightLoading = true;
    	leftLoading = true;
		if( testCraft != null )
		{
			testCraft.waitTorpLoading+=2;
		}
    	
    	if( cannonType == 7 )
    	{
    		b = getDirectionFromRelative(loc.getBlock(), direction, true).getRelative(direction,4);
    		fireTorpedoMk3(p, b, direction, depth, 0, true);
    		b = getDirectionFromRelative(loc.getBlock(), direction, false).getRelative(direction,4);
    		fireTorpedoMk3(p, b, direction, depth, 2000, false);
    	}else if( cannonType == 8 )
    	{
    		b = getDirectionFromRelative(loc.getBlock(), direction, true).getRelative(direction,4);
    		fireTorpedoMk1(p, b, direction, depth, 0, true);
    		b = getDirectionFromRelative(loc.getBlock(), direction, false).getRelative(direction,4);
    		fireTorpedoMk1(p, b, direction, depth, 2000, false);
    	}else if (cannonType == 3)
    	{
    		b = getDirectionFromRelative(loc.getBlock(), direction, true).getRelative(direction,4);
    		fireTorpedoMk2(p, b, direction, depth, 0,true);
    		b = getDirectionFromRelative(loc.getBlock(), direction, false).getRelative(direction,4);
    		fireTorpedoMk2(p, b, direction, depth, 2000,false);
    	}else if (cannonType == 11)
    	{
    		b = getDirectionFromRelative(loc.getBlock(), direction, true).getRelative(direction,4);
    		fireMissileMk1(p, b, direction, depth, 0, 0,true,false);
    		b = getDirectionFromRelative(loc.getBlock(), direction, false).getRelative(direction,4);
    		fireMissileMk1(p, b, direction, depth, 2000, 0,false,false);
    	}else if (cannonType == 12)
    	{
    		b = getDirectionFromRelative(loc.getBlock(), direction, true).getRelative(direction,4);
    		fireMissileMk2(p, b, direction, depth, 0, 0,true,false);
    		b = getDirectionFromRelative(loc.getBlock(), direction, false).getRelative(direction,4);
    		fireMissileMk2(p, b, direction, depth, 2000, 0,false,false);
    	}else
    	{
    		b = getDirectionFromRelative(loc.getBlock(), direction, true).getRelative(direction,4);
    		fireMissileMk3(p, b, direction, depth, 0, 0,true,false);
    		b = getDirectionFromRelative(loc.getBlock(), direction, false).getRelative(direction,4);
    		fireMissileMk3(p, b, direction, depth, 2000, 0,false,false);
    	}
    	
    	
    	
    	//p.sendMessage("Tube 1 and 2 Fired!");	
    }
    
    public void fireVertical(Player p)
    {
    	Block b;
    	b = loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.UP,4);
    	Craft testCraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    	leftLoading = true;
    	rightLoading = true;
		if( testCraft != null )
		{
			testCraft.waitTorpLoading++;
		}
		if (cannonType == 13)
			fireMissileMk1(p, b, direction, depth, 0, range, true, true);
		else if (cannonType == 14)
			fireMissileMk2(p, b, direction, depth, 0, range, true, true);
		else
			fireMissileMk3(p, b, direction, depth, 0, range, true, true);
    }
    
    public Block getDirectionFromRelative(Block blockIn, BlockFace dir, boolean left)
    {
    	Block b;
    	if( dir == BlockFace.NORTH )	
    	{
    		if( left )
    			b = blockIn.getRelative(BlockFace.WEST);
    		else
    			b = blockIn.getRelative(BlockFace.EAST);
    	}else if( dir == BlockFace.SOUTH )
    	{
    		if( left )
    			b = blockIn.getRelative(BlockFace.EAST);
    		else
    			b = blockIn.getRelative(BlockFace.WEST);
    	}else if( dir == BlockFace.EAST )
    	{
    		if( left )
    			b = blockIn.getRelative(BlockFace.NORTH);
    		else
    			b = blockIn.getRelative(BlockFace.SOUTH);
    	}else if( direction == BlockFace.WEST )
    	{
    		if( left )
    			b = blockIn.getRelative(BlockFace.SOUTH);
    		else
    			b = blockIn.getRelative(BlockFace.NORTH);
    	} else //if( direction == BlockFace.WEST )
    	{
    		if( left )
    			b = blockIn.getRelative(BlockFace.SOUTH);
    		else
    			b = blockIn.getRelative(BlockFace.NORTH);
    	}
    	return b;
    }
    
    public BlockFace getTubeBlockFace(boolean left)
    {
    	BlockFace bf;
    	if( direction == BlockFace.NORTH )	
    	{
    		if( left )
    			bf = BlockFace.WEST;
    		else
    			bf = BlockFace.EAST;
    	}else if( direction == BlockFace.SOUTH )
    	{
    		if( left )
    			bf = BlockFace.EAST;
    		else
    			bf = BlockFace.WEST;
    	}else if( direction == BlockFace.EAST )
    	{
    		if( left )
    			bf = BlockFace.NORTH;
    		else
    			bf = BlockFace.SOUTH;
    	}else //if( direction == BlockFace.WEST )
    	{
    		if( left )
    			bf = BlockFace.SOUTH;
    		else
    			bf = BlockFace.NORTH;
    	}
    	return bf;
    }
    
    public boolean checkInnerDoorClosed(boolean left)
    {
    	Block b;
    	b = getDirectionFromRelative(loc.getBlock(), direction, left);
		return b.getType() == Material.CLAY;
    }
    
    public boolean checkOuterDoorClosed()
    {
    	Block b;
    	b = loc.getBlock().getRelative(direction, 5);
    	if( b.getRelative(BlockFace.EAST).getType() == Material.CLAY && b.getRelative(BlockFace.WEST).getType() == Material.CLAY )
    	{
    		return true;
    	}else
			return b.getRelative(BlockFace.NORTH).getType() == Material.CLAY && b.getRelative(BlockFace.SOUTH).getType() == Material.CLAY;
    }
    
    
	public boolean checkTubeLoaded(boolean left)
    {
    	Block b;
    	b = getDirectionFromRelative(loc.getBlock(), direction, left);
    	
    	if( b.getRelative(direction).getTypeId() == 35 )
    		if( b.getRelative(direction,2).getTypeId() == 35 )
    			if( b.getRelative(direction,3).getTypeId() == 35 )
    				if( b.getRelative(direction,4).getTypeId() == 35 )
    					return true;
    	
    	if( left )
    	{
			return leftLoading;
    	}else
    	{
			return rightLoading;
    	}
    	
    }
	
	public boolean checkTubeLoadedV()
    {
    	Block b;
    	b = loc.getBlock().getRelative(direction, 1);
    	
    	if( b.getRelative(BlockFace.UP).getTypeId() == 35 )
    		if( b.getRelative(BlockFace.UP,2).getTypeId() == 35 )
    			if( b.getRelative(BlockFace.UP,3).getTypeId() == 35 )
					return b.getRelative(BlockFace.UP, 4).getTypeId() == 35;
    	return false;
    }
	
    public boolean checkOuterDoorClosedV()
    {
    	Block b;
    	b = loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.UP, 5);
		return b.getType() == Material.CLAY;
    }
	
	public void openMissileDoors(Player p)
    {
    	if(p != null && checkProtectedRegion(p, p.getLocation()) )
    	{
    		p.sendMessage(ChatColor.RED + "You are in a protected region");
    		return;
    	}
    	
    	Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
	    	Block a,b,c;
	    	a = loc.getBlock().getRelative(direction, 5);
	    	if( direction == BlockFace.NORTH || direction == BlockFace.SOUTH )
	    	{
	    		b = a.getRelative(BlockFace.EAST);
	    		c = a.getRelative(BlockFace.WEST);
	    	}else
	    	{
	    		b = a.getRelative(BlockFace.NORTH);
	    		c = a.getRelative(BlockFace.SOUTH);
	    	}
	    	
	    	
	    	if( checkOuterDoorClosed() )
	    	{
	    				b.setType(Material.AIR);
	    	    		c.setType(Material.AIR);
						if (p != null)
	    	    		p.sendMessage(ChatColor.GREEN + "Opening Outer Tube Doors!");
	    	}else
	    	{
	    		b.setType(Material.CLAY);
	    		c.setType(Material.CLAY);
				if (testCraft != null)
			    {
			    	testCraft.addBlock(b, true);
			    	testCraft.addBlock(c, true);
			    }
				if (p != null)
	    		p.sendMessage(ChatColor.GREEN + "Closing Outer Tube Doors!");
	    	}
    	}
    
	public void openMissileDoorsV(Player p)
    {
    	if(p != null && checkProtectedRegion(p, p.getLocation()) )
    	{
    		p.sendMessage(ChatColor.RED + "You are in a protected region");
    		return;
    	}
    	
    	Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
    	Block a = loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.UP, 5);
	    	
	    	if( checkOuterDoorClosedV() )
	    	{
	    				a.setType(Material.AIR);
						if (p != null)
	    	    		p.sendMessage(ChatColor.GREEN + "Opening Outer Tube Doors!");
	    	}else
	    	{
	    		a.setType(Material.CLAY);
				if (testCraft != null)
			    {
			    	testCraft.addBlock(a, true);
			    }
				if (p != null)
	    		p.sendMessage(ChatColor.GREEN + "Closing Outer Tube Doors!");
	    	}
    	}
	
    public void setMissileMode(Player p)
    {
    	missileMode++;
    	missileMode = missileMode%3;
    	switch( missileMode )
    	{
    	case 0:
    		p.sendMessage(ChatColor.GREEN + "Firing Mode : " + ChatColor.YELLOW + "Left Tube");
    		break;
    	case 1:
    		p.sendMessage(ChatColor.GREEN + "Firing Mode : " + ChatColor.YELLOW + "Right Tube");
    		break;
    	case 2:
    		p.sendMessage(ChatColor.GREEN + "Firing Mode : " + ChatColor.YELLOW + "Both");
    		break;
    	}
    }
    
    public void setMissileRange(Player p)
    {
		if(range < 250 )
			range = range + 5;
		else
			range = 0;
		p.sendMessage(ChatColor.GREEN + "Range set to " + ChatColor.YELLOW + range + ChatColor.GREEN + "m.");
    }
    
    public void fireMissileButton(Player p, boolean isVertical)
    {
    	if(p != null && checkProtectedRegion(p, p.getLocation()) )
    	{
    		p.sendMessage(ChatColor.RED + "You are in a protected region");
    		return;
    	}
    	
    	if (isVertical) {
        		if( checkTubeLoadedV() )
        		{
        			if( !checkOuterDoorClosedV() )
        			{
        				fireVertical(p);
        			}else
        			{
        				if (p != null)
        				p.sendMessage(ChatColor.YELLOW + "Missile: Open Outer Doors");
        			}
        		}else
        		{
					if (p != null)
        			p.sendMessage(ChatColor.RED + "Missile: Tube Not Loaded");
        		}
    	} else {
    	
    	if( missileMode == 0 )
    	{
    		if( checkTubeLoaded(true) )
    		{
    			if( !checkOuterDoorClosed() )
    			{
    				fireLeft(p);
    			}else
    			{
					if (p != null)
    				p.sendMessage(ChatColor.YELLOW + "Left Tube: Open Outer Doors");
    			}
    		}else
    		{
				if (p != null)
    			p.sendMessage(ChatColor.RED + "Left Tube: Tube Not Loaded");
    		}
    		
    	}else if( missileMode == 1)
    	{
    		if( checkTubeLoaded(false) )
    		{
    			if( !checkOuterDoorClosed() )
    			{
    				fireRight(p);
    			}else
    			{
					if (p != null)
    				p.sendMessage(ChatColor.YELLOW + "Right Tube: Open Outer Doors");
    			}
    			
    		}else
    		{
				if (p != null)
    			p.sendMessage(ChatColor.RED + "Right Tube: Tube Not Loaded");
    		}
    		
    	}else
    	{
    		if( checkTubeLoaded(true) && checkTubeLoaded(false) )
    		{
    			if( !checkOuterDoorClosed() )
    			{
    				fireBoth(p);
    			}else
    			{
					if (p != null)
    				p.sendMessage(ChatColor.YELLOW + "Both Tubes: Open Outer Doors");
    			}
    			
    		}else
    		{
				if (p != null)
    			p.sendMessage(ChatColor.RED + "Both Tubes: Both Tubes Not Loaded");
    		}
    	}
    }
}
    
 
    
	public void fireMissileMk1(final Player p, final Block b, final BlockFace torpHeading, final int torpDepth, final int delayShoot, final int torpRange, final boolean left, final boolean isVertical){
    	final Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		final Weapon torp = new Weapon(b, torpHeading, torpDepth, torpRange);
		AimCannon.weapons.add(torp);
		
		if( torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getTypeId() == 68 )
		{
			Sign sign = (Sign) torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getState();
			String signLine0 =  sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
			int tubeNum=0;
			if( signLine0.equalsIgnoreCase("Tube") )
			{
				String tubeString = sign.getLine(1).trim().toLowerCase();
				tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
				if( !tubeString.isEmpty() )
				{
					try{
						tubeNum = Integer.parseInt(tubeString);
					}catch (NumberFormatException nfe)
					{
						tubeNum=0;
					}
				}
			}
			torp.tubeNum=tubeNum;
		}
		
		torp.setDepth = torpDepth;
		
		if( testCraft != null )
		{
			for( String s : testCraft.crewNames )
			{
				Player pl = nc.getServer().getPlayer(s);
				if( pl != null )
				{
					if( torp.tubeNum == 0 )
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube Fired!");
					else
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube " + torp.tubeNum + " Fired!");
				}
			}
		}
		
    	
    	
		new Thread(){
			
    	@Override
			public void run() {
    		
    		setPriority(Thread.MIN_PRIORITY);
				//taskNum = -1;
    	if (!isVertical) {
    			try{
    				sleep(delayShoot);
    				
    				
    				
    				
    				for( int i=0; i<1150; i++ )
    				{
						fireMissileUpdateMk1(p, torp, i, testCraft, left, false);
						sleep(1150);
					} 
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
    		} else {
    			try{
    				sleep(delayShoot);
    				
    				
    				
    				
    				for( int i=0; i<550; i++ )
    				{
						fireMissileUpdateMk1(p, torp, i, testCraft, left, true);
						sleep(175);
					} 
				}catch (InterruptedException e) {
					e.printStackTrace();
				}	
    		}
		}
    }.start(); //, 20L);
}
    
    public void fireMissileUpdateMk1(final Player p, final Weapon torp, final int i, final Craft firingCraft, final boolean left, final boolean isVertical) {
    	if (!isVertical) {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
    	//new Thread() {
	  //  @Override
		public void run()
	    {

	    	if( !torp.dead)
	    	{
		    	if(torp.warhead.getY() < 255 && torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -1).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -2).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -3).getTypeId() == 35 )
		    	{
		    		CraftMover.playWeaponSound(torp.warhead.getLocation(), Sound.ENTITY_PLAYER_BREATH, 2.0f, 0.8f);
					if( i > 15 )
					{
						
						if( torp.warhead.getY() > 62 )
			    		{
			    			torp.warhead.setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
			    		}else
			    		{
			    			torp.warhead.setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
			    		}
						
						if( checkProtectedRegion(p, torp.warhead.getLocation()) )
						{
							p.sendMessage(ChatColor.RED + "No missiles allowed in dock area!");
							torp.destroyWeapon();
							torp.dead = true;
							return;
						}
						
						
						
						
						//new position
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						int depthDifference = torp.setDepth - torp.warhead.getY();
						if( depthDifference < 0 )
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.DOWN);
						}else if( depthDifference > 0)
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.UP);
						}
						
						if( torp.turnProgress > -1 )
						{
							
							if( torp.turnProgress == 10 )
							{
								if( torp.hdg == BlockFace.NORTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.WEST;
									else
										torp.hdg = BlockFace.EAST;
								}else if( torp.hdg == BlockFace.SOUTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.EAST;
									else
										torp.hdg = BlockFace.WEST;
								}else if( torp.hdg == BlockFace.EAST )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.NORTH;
									else
										torp.hdg = BlockFace.SOUTH;
								}else
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.SOUTH;
									else
										torp.hdg = BlockFace.NORTH;
								}
								torp.rudder = -torp.rudder;
							}
							
							if( torp.turnProgress == 20 )
							{
								if( torp.doubleTurn )
								{
									torp.turnProgress = 0;
									torp.rudder = -torp.rudder;
									torp.doubleTurn = false;
								}else
								{
									torp.turnProgress = -1;
									torp.rudder = torp.rudderSetting;
								}
							}else
								torp.turnProgress += 1;
						}
						
						if( torp.rudder != 0 )
						{
							int dirMod  = Math.abs(torp.rudder);
							if( i % dirMod == 0 )
							{
								if( torp.rudder < 0 )
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, true);
								}else
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, false);
								}
							}
						}
						
						
						
						//check new position
						if( torp.warhead.getType() == Material.AIR )
						{
		    				if( i == 149 )
							{
								if( torp.warhead.getY() > 62 )
								{
									torp.warhead.setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
								}else
								{
									torp.warhead.setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
								}
								p.sendMessage(ChatColor.YELLOW + "Missile expired.");
								torp.destroyWeapon();
								return;
							}
		    				
		    				torp.warhead.setTypeIdAndData(35, (byte) 0xB, false);
		    				torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x0, false);
		    				torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x0, false);
		    				torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
						}else if( torp.active ) ///detonate!
						{
							if( checkProtectedRegion(p, torp.warhead.getLocation()) )
							{
								p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");
								torp.destroyWeapon();
								return;
							}
							
							
							
							
							
							torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
							NavyCraft.explosion(3,  torp.warhead, false);
							torp.dead = true;
							torp.destroyWeapon();
	
							Craft checkCraft=null;
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
									if( checkCraft == null ) {
										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
										if( checkCraft == null ) {
											checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
										}
									}
								}
							}
							
							if( checkCraft == null )
								p.sendMessage(ChatColor.YELLOW + "Missile hit unknown object!");
							else
								p.sendMessage(ChatColor.GREEN + "Missile hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
										
						
						}else
						{
							torp.dead = true;
							torp.destroyWeapon();
							p.sendMessage(ChatColor.RED + "Dud Missile! Too close.");
							
						}
						
						
					}
					else/// i <= 15
					{
						if( torp.warhead.getY() > 62 || i < 5 )
						{
							torp.warhead.setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
							
							if( i == 4  )
							{
								if( firingCraft != null )
								{
									firingCraft.addBlock(torp.warhead, true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -1), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -2), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -3), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -4), true);
	
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -5), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -6), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -7), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -8), true);
								}
							}
						}else
						{
							torp.warhead.setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
						}
						
	
						//Move torp
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						
						if( torp.warhead.getType() == Material.AIR )
						{
							torp.warhead.setTypeIdAndData(35, (byte) 0xB, false);
			    			torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x0, false);
			    			torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x0, false);
			    			torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
						}else
						{
							if( firingCraft != null )
							{
								firingCraft.waitTorpLoading--;
							}
							p.sendMessage(ChatColor.RED + "Dud Missile! Too close.");
							torp.dead = true;
							torp.destroyWeapon();
						}
						
						
					}
					
					
		    		
		    	}else //else torp blocks missing, detonate
		    	{
		    		if( checkProtectedRegion(p, torp.warhead.getLocation()) )
					{
						p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");
						torp.dead = true;
						torp.destroyWeapon();
						return;
					}
					
		    		if( !torp.active )
		    		{
						p.sendMessage(ChatColor.RED + "Dud Missile! Too close.");
						torp.dead = true;
						torp.destroyWeapon();
						if( firingCraft != null )
						{
							firingCraft.waitTorpLoading--;
						}
						return;
		    		}
					
					
					torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
					NavyCraft.explosion(3,  torp.warhead, false);
					torp.dead = true;
					torp.destroyWeapon();
					
					Craft checkCraft=null;
					checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
					if( checkCraft == null ) {
						checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
						if( checkCraft == null ) {
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
								}
							}
						}
					}
					
					if( checkCraft == null )
						p.sendMessage(ChatColor.RED + "Missile detonated prematurely!");
					else
						p.sendMessage(ChatColor.GREEN + "Missile hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
					
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
					}
		    	}
		    	if( i == 15 )
		    	{
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
					}
					torp.active = true;
				}
	    	}

	    }
    	}
    	
	);
   } else {
	   nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
	    	//new Thread() {
		  //  @Override
			public void run()
		    {
				if( !torp.dead)
		    	{
		    		NavyCraft.instance.DebugMessage(Integer.toString(i), 3);
			    	if(torp.warhead.getY() < 255 && ( torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -1).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -2).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -3).getTypeId() == 35) ||  (torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(BlockFace.UP, -1).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.UP, -2).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.UP, -3).getTypeId() == 35) || (torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(BlockFace.DOWN, -1).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.DOWN, -2).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.DOWN, -3).getTypeId() == 35))
			    	{
			    		CraftMover.playWeaponSound(torp.warhead.getLocation(), Sound.ENTITY_PLAYER_BREATH, 2.0f, 0.8f);
						if( i > 15 )
						{
							if( ( torp.warhead.getY() > 58 && torp.torpRotation == 2) || ( torp.warhead.getY() > 62 && torp.torpRotation != 2 ))
				    		{
				    			torp.warhead.setType(Material.AIR);
				    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
				    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
				    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
				    		}else
				    		{
				    			torp.warhead.setType(Material.WATER);
				    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
				    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
				    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
				    		}
							
							if( checkProtectedRegion(p, torp.warhead.getLocation()) )
							{
								p.sendMessage(ChatColor.RED + "No missiles allowed in dock area!");
								torp.dead = true;
								torp.destroyWeapon();
								return;
							}
							
							if (torp.torpRotation == 4) {
						    	NavyCraft.instance.DebugMessage("Running block fix", 3);
								if( torp.warhead.getY() > 62 )
					    		{
					    			torp.warhead.setType(Material.AIR);
					    			torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.AIR);
					    			torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.AIR);
					    			torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.AIR);
					    		}else
					    		{
					    			torp.warhead.setType(Material.WATER);
					    			torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.WATER);
					    			torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.WATER);
					    			torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.WATER);
					    		}
								if( torp.warhead.getY() > 58 )
					    		{
					    			torp.warhead.setType(Material.AIR);
					    			torp.warhead.getRelative(BlockFace.DOWN, -1).setType(Material.AIR);
					    			torp.warhead.getRelative(BlockFace.DOWN, -2).setType(Material.AIR);
					    			torp.warhead.getRelative(BlockFace.DOWN, -3).setType(Material.AIR);
					    		}else
					    		{
					    			torp.warhead.setType(Material.WATER);
					    			torp.warhead.getRelative(BlockFace.DOWN, -1).setType(Material.WATER);
					    			torp.warhead.getRelative(BlockFace.DOWN, -2).setType(Material.WATER);
					    			torp.warhead.getRelative(BlockFace.DOWN, -3).setType(Material.WATER);
					    		}
								torp.torpRotation = -1;
							}
							
    						int depthDifference = torp.setDepth - torp.warhead.getY();
    						if( depthDifference > 0)
    						{
    							if (depthDifference > 4) {
    							torp.hdg = BlockFace.UP;
    							torp.torpRotation = 1;
    							} else {
    							torp.warhead = torp.warhead.getRelative(BlockFace.UP);
    							}
    						if (depthDifference == 1) {
    						torp.torpRotation = 3;
    						}
    					}
							
							if (depthDifference <= 0) torp.rangeCounter++;
							
							if (depthDifference < 0 && (torp.setRange - torp.rangeCounter < 0)) {
								if( depthDifference < -4 )
								{
								torp.hdg = BlockFace.DOWN;
								torp.torpRotation = 2;
								} else {
									torp.warhead = torp.warhead.getRelative(BlockFace.DOWN);
								}
								if (depthDifference == -1) {
								torp.torpRotation = 3;
								}
							}
							
							if (torp.torpRotation == 4 && depthDifference <= 0) {
								torp.hdg = torp.ohdg;
							}
							
								//new position
								torp.warhead = torp.warhead.getRelative(torp.hdg);								
							
							//check new position
							if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.STATIONARY_WATER || torp.warhead.getType() == Material.LAVA || torp.warhead.getType() == Material.STATIONARY_LAVA || torp.warhead.getType() == Material.AIR )
							{
			    				if( i == 500 )
								{
									if( torp.warhead.getY() > 62 )
									{
										torp.warhead.setType(Material.AIR);
										torp.warhead.getRelative(torp.hdg).setType(Material.AIR);
										torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
										torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
									}else
									{
										torp.warhead.setType(Material.WATER);
										torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
										torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
										torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
									}
									p.sendMessage(ChatColor.YELLOW + "Missile expired.");
									torp.destroyWeapon();
									return;
								}
			    				torp.warhead.setTypeIdAndData(35, (byte) 0xB, false);
			    				torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x0, false);
			    				torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x0, false);
			    				torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
							}else if( torp.active ) ///detonate!
							{
								if( checkProtectedRegion(p, torp.warhead.getLocation()) )
								{
									p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");
									torp.destroyWeapon();
									return;
								}
								
								
								
								torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
								NavyCraft.explosion(8,  torp.warhead, false);
								torp.dead = true;
								torp.destroyWeapon();
		
								Craft checkCraft=null;
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
									if( checkCraft == null ) {
										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
										if( checkCraft == null ) {
											checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
											if( checkCraft == null ) {
												checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
											}
										}
									}
								}
								
								if( checkCraft == null )
									p.sendMessage(ChatColor.YELLOW + "Missile hit unknown object!");
								else
									p.sendMessage(ChatColor.GREEN + "Missile hit " + ChatColor.YELLOW +  checkCraft.name + ChatColor.GREEN + "!");
											
							
							}else
							{
								torp.dead = true;
								torp.destroyWeapon();
								p.sendMessage(ChatColor.RED + "Dud Missile! Too close.");
							}
							
							
						}
						else/// i <= 15
						{
							if( torp.warhead.getY() > 62 || i < 5 )
							{
								torp.warhead.setType(Material.AIR);
								torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.AIR);
								torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.AIR);
								torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.AIR);
								
								if( i == 4  )
								{
									if( firingCraft != null )
									{
										firingCraft.addBlock(torp.warhead, true);
										firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -1), true);
										firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -2), true);
										firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -3), true);
										firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -4), true);
		
										firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -5), true);
										firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -6), true);
										firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -7), true);
										firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -8), true);
									}
									torp.torpRotation = 3;
								}
							}else
							{
								torp.warhead.setType(Material.WATER);
								torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.WATER);
								torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.WATER);
								torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.WATER);
							}
							
		
							//Move torp
							torp.warhead = torp.warhead.getRelative(BlockFace.UP);
							
							if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.AIR || torp.warhead.getType() == Material.STATIONARY_WATER )
							{
								torp.warhead.setTypeIdAndData(35, (byte) 0xB, false);
				    			torp.warhead.getRelative(BlockFace.UP, -1).setTypeIdAndData(35, (byte) 0x0, false);
				    			torp.warhead.getRelative(BlockFace.UP, -2).setTypeIdAndData(35, (byte) 0x0, false);
				    			torp.warhead.getRelative(BlockFace.UP, -3).setTypeIdAndData(35, (byte) 0x7, false);
							}else
							{
								if( firingCraft != null )
								{
									firingCraft.waitTorpLoading--;
									
								if(!checkOuterDoorClosedV() )
										openMissileDoorsV(p);
								}else
								{
									if(!checkOuterDoorClosedV() )
										openMissileDoorsV(p);
								}
								p.sendMessage(ChatColor.RED + "Dud Missile! Too close.");
								torp.dead = true;
								torp.destroyWeapon();
							}
							
							
						}
						
						
			    		
			    	}else //else torp blocks missing, detonate
			    	{
			    		if( checkProtectedRegion(p, torp.warhead.getLocation()) )
						{
							p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");
							torp.destroyWeapon();
							return;
						}
						
			    		if( !torp.active )
			    		{
							p.sendMessage(ChatColor.RED + "Dud Missile! Too close.");
							torp.dead = true;
							torp.destroyWeapon();
							if( firingCraft != null )
							{
								firingCraft.waitTorpLoading--;
								
								if(!checkOuterDoorClosedV() )
									openMissileDoorsV(p);
							}else
							{	
								if(!checkOuterDoorClosedV() )
									openMissileDoorsV(p);
							}
							return;
			    		}
						
						torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
						NavyCraft.explosion(8,  torp.warhead, false);
						torp.dead = true;
						torp.destroyWeapon();
						
						
						Craft checkCraft=null;
						checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
						if( checkCraft == null ) {
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
									if( checkCraft == null ) {
										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
									}
								}
							}
						}
						
						if( checkCraft == null )
							p.sendMessage(ChatColor.RED + "Missile detonated prematurely!");
						else
							p.sendMessage(ChatColor.GREEN + "Missile hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
						
						
						if( firingCraft != null )
						{
							firingCraft.waitTorpLoading--;
							
							if(!checkOuterDoorClosedV() )
								openMissileDoorsV(p);
						}else
						{
							if(!checkOuterDoorClosedV() )
								openMissileDoorsV(p);
						}
			    	}
			    	if (torp.torpRotation == 3) {
			    		torp.torpRotation = 4;
			    	}
		    	}
			    	if( i == 15 )
					{
						
						if( firingCraft != null )
						{
							firingCraft.waitTorpLoading--;
							
							if(!checkOuterDoorClosedV() )
								openMissileDoorsV(p);
						}else
						{	
							if(!checkOuterDoorClosedV() )
								openMissileDoorsV(p);
						}
						torp.active = true;
					}
		    	}

		    }
	    	
		);
   }
}
    
    public void fireMissileMk2(final Player p, final Block b, final BlockFace torpHeading, final int torpDepth, final int delayShoot, final int torpRange, final boolean left, final boolean isVertical){
    	//final int taskNum;
    	//int taskNum = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
    	final Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		final Weapon torp = new Weapon(b, torpHeading, torpDepth, torpRange);
		AimCannon.weapons.add(torp);
		
		if( torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getTypeId() == 68 )
		{
			Sign sign = (Sign) torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getState();
			String signLine0 =  sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
			int tubeNum=0;
			if( signLine0.equalsIgnoreCase("Tube") )
			{
				String tubeString = sign.getLine(1).trim().toLowerCase();
				tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
				if( !tubeString.isEmpty() )
				{
					try{
						tubeNum = Integer.parseInt(tubeString);
					}catch (NumberFormatException nfe)
					{
						tubeNum=0;
					}
				}
			}
			torp.tubeNum=tubeNum;
		}
		
		if( testCraft != null && testCraft.tubeMk1FiringDisplay > -1 )
		{
			if( testCraft.tubeMk1FiringDepth > -1)
				torp.setDepth = testCraft.tubeMk1FiringDepth;
			else
				torp.setDepth = torpDepth;
			Player onScopePlayer=null;
			for( Periscope per: testCraft.periscopes )
			{
				if( per.user != null )
				{
					onScopePlayer = per.user;
					break;
				}
			}
			

			float rotation=0;
			if( onScopePlayer != null && testCraft.tubeMk1FiringMode == -1 ) //firing periscope mode, player on scope
			{
				rotation = (float) Math.PI * onScopePlayer.getLocation().getYaw() / 180f;
				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);
				transp.add(Material.STATIONARY_WATER);
				transp.add(Material.WATER);
				transp.add(Material.LAVA);
				transp.add(Material.STATIONARY_LAVA);
				transp.add(Material.WOOL);
				float xDist = onScopePlayer.getTargetBlock(transp, 1000).getX() - torp.warhead.getX();
				float zDist = onScopePlayer.getTargetBlock(transp, 1000).getZ() - torp.warhead.getZ();
				torp.setRange=torp.calculateRange(xDist, zDist);
				torp.setDepth = onScopePlayer.getTargetBlock(transp, 1000).getY();
				torp.isGuided = true;
			}else if( testCraft.lastPeriscopeYaw != -9999 && testCraft.tubeMk1FiringMode == -1 ) //firing periscope mode, last used periscope yaw
			{
				rotation = (float) Math.PI * testCraft.lastPeriscopeYaw / 180f;
				
				float xDist = testCraft.lastPeriscopeBlock.getX() - torp.warhead.getX();
				float zDist = testCraft.lastPeriscopeBlock.getZ() - torp.warhead.getZ();
				torp.setRange=torp.calculateRange(xDist, zDist);
				torp.setDepth = testCraft.lastPeriscopeBlock.getY();
				torp.isGuided = true;
			} else if( testCraft.lastPeriscopeBlock != null && testCraft.tubeMk1FiringMode == -1 )
			
			torp.setDepth = testCraft.lastPeriscopeBlock.getY();
			torp.isGuided = true;
			if( left )
				rotation -= testCraft.tubeMk1FiringSpread*Math.PI/180f;
			else
				rotation += testCraft.tubeMk1FiringSpread*Math.PI/180f;
			
			torp.calculateHeading(rotation);
			
			
			for( String s : testCraft.crewNames )
			{
				Player pl = nc.getServer().getPlayer(s);
				if( pl != null )
				{
					if( torp.tubeNum == 0 )
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube Fired!");
					else
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube " + torp.tubeNum + " Fired!");
				}
			}
		}else
		{
				torp.setDepth = torpDepth;
		}
    	
    	
    	
		new Thread(){
			
    	@Override
	public void run() {
    		
    		setPriority(Thread.MIN_PRIORITY);
				//taskNum = -1;
    	if (!isVertical) {
    			try{
    				sleep(delayShoot);
    				
    				
    				
    				
    				for( int i=0; i<1250; i++ )
    				{
						fireMissileUpdateMk2(p, torp, i, testCraft, left, false);
						sleep(125);
					} 
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
    		} else {
    			try{
    				sleep(delayShoot);
    				
    				
    				
    				
    				for( int i=0; i<1550; i++ )
    				{
						fireMissileUpdateMk2(p, torp, i, testCraft, left, true);
						sleep(150);
					} 
				}catch (InterruptedException e) {
					e.printStackTrace();
				}	
    		}
		}
    	}.start(); //, 20L);
    }
    
    public void fireMissileUpdateMk2(final Player p, final Weapon torp, final int i, final Craft firingCraft, final boolean left, final boolean isVertical) {
    	if (!isVertical) {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
    	//new Thread() {
	  //  @Override
		public void run()
	    {
	    	//getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
	    //	}
		//setPriority(Thread.MIN_PRIORITY);
			//try
			//{ 
	    	if( !torp.dead )
	    	{
		    	if( torp.warhead.getY() < 255 && torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -1).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -2).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -3).getTypeId() == 35 )
		    	{
		    		CraftMover.playWeaponSound(torp.warhead.getLocation(), Sound.ENTITY_PLAYER_BREATH, 2.0f, 0.8f);
					if( i > 15 )
					{
						if( torp.warhead.getY() > 62 )
			    		{
			    			torp.warhead.setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
			    		}else
			    		{
			    			torp.warhead.setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
			    		}
						
						if( checkProtectedRegion(p, torp.warhead.getLocation()) )
						{
							p.sendMessage(ChatColor.RED + "No missiles allowed in dock area!");
							torp.dead = true;
							torp.destroyWeapon();
							return;
						}
						
						
						//new position
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						int depthDifference = torp.setDepth - torp.warhead.getY();
						if( depthDifference < 0 )
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.DOWN);
						}else if( depthDifference > 0)
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.UP);
						}
						
						if( torp.turnProgress > -1 )
						{
							
							if( torp.turnProgress == 10 )
							{
								if( torp.hdg == BlockFace.NORTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.WEST;
									else
										torp.hdg = BlockFace.EAST;
								}else if( torp.hdg == BlockFace.SOUTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.EAST;
									else
										torp.hdg = BlockFace.WEST;
								}else if( torp.hdg == BlockFace.EAST )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.NORTH;
									else
										torp.hdg = BlockFace.SOUTH;
								}else
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.SOUTH;
									else
										torp.hdg = BlockFace.NORTH;
								}
								torp.rudder = -torp.rudder;
							}
							
							if( torp.turnProgress == 20 )
							{
								if( torp.doubleTurn )
								{
									torp.turnProgress = 0;
									torp.rudder = -torp.rudder;
									torp.doubleTurn = false;
								}else
								{
									torp.turnProgress = -1;
									torp.rudder = torp.rudderSetting;
								}
							}else
								torp.turnProgress += 1;
						}
						
						if( torp.rudder != 0 )
						{
							int dirMod  = Math.abs(torp.rudder);
							if( i % dirMod == 0 )
							{
								if( torp.rudder < 0 )
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, true);
								}else
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, false);
								}
							}
						}
						
						
						//check new position
						if( torp.warhead.getType() == Material.AIR )
						{
		    				if( i == 249 )
							{
								if( torp.warhead.getY() > 62 )
								{
									torp.warhead.setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
								}else
								{
									torp.warhead.setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
								}
								p.sendMessage(ChatColor.YELLOW + "Missile expired.");
								torp.destroyWeapon();
								return;
							}
		    				
		    				torp.warhead.setTypeIdAndData(35, (byte) 0xE, false);
		    				torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x0, false);
		    				torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x0, false);
		    				torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
						}else if( torp.active ) ///detonate!
						{
							if( checkProtectedRegion(p, torp.warhead.getLocation()) )
							{
								p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");
								torp.dead = true;
								torp.destroyWeapon();
								
								return;
							}
	
							
							torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
							NavyCraft.explosion(5,  torp.warhead, false);
							torp.dead = true;
							torp.destroyWeapon();
							
							Craft checkCraft=null;
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
									if( checkCraft == null ) {
										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
										if( checkCraft == null ) {
											checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
										}
									}
								}
							}
							
							if( checkCraft == null )
								p.sendMessage(ChatColor.YELLOW +"Missile hit unknown object!");
							else
								p.sendMessage(ChatColor.GREEN +"Missile hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
							
						}else
						{
							p.sendMessage(ChatColor.RED + "Dud Missile! Too close.1");
							torp.dead = true;
							torp.destroyWeapon();
						}
						
						
					}
					else/// i <= 15
					{
						if( torp.warhead.getY() > 62 || i < 5 )
						{
							torp.warhead.setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
							
							if( i == 4  )
							{
								if( firingCraft != null )
								{
									firingCraft.addBlock(torp.warhead, true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -1), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -2), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -3), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -4), true);
	
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -5), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -6), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -7), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -8), true);
								}
							}
						}else
						{
							torp.warhead.setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
						}
						
						
						
						
						//Move torp
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						
						if( torp.warhead.getType() == Material.AIR )
						{
							torp.warhead.setTypeIdAndData(35, (byte) 0xE, false);
			    			torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x0, false);
			    			torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x0, false);
			    			torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
						}else
						{
							if( firingCraft != null )
							{
								firingCraft.waitTorpLoading--;
							}
							torp.dead = true;
							torp.destroyWeapon();
							p.sendMessage(ChatColor.RED + "Dud Missile! Too close.2");
						}
	
					}
					
					
		    		
		    	}else //torp blocks missing, detonate
		    	{
		    		if( checkProtectedRegion(p, torp.warhead.getLocation()) )
					{
						p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");
						return;
					}
					
		    		if( !torp.active )
		    		{
		    			p.sendMessage(ChatColor.RED + "Dud Missile! Too close.3");
						torp.dead = true;
						torp.destroyWeapon();
						if( firingCraft != null )
						{
							firingCraft.waitTorpLoading--;
						}
						return;
		    		}
					
					
					torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
					NavyCraft.explosion(5,  torp.warhead, false);
					torp.dead = true;
					torp.destroyWeapon();
					
					Craft checkCraft=null;
					checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
					if( checkCraft == null ) {
						checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
						if( checkCraft == null ) {
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
								}
							}
						}
					}
					
					if( checkCraft == null )
						p.sendMessage(ChatColor.RED + "Missile detonated prematurely!");
					else
						p.sendMessage(ChatColor.GREEN + "Missile hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
					
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
					}
		    	}
		    	
		    	if( i == 15 )
				{
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
					}
					torp.active = true;
				}
	    	}

	    }
    	}
    	
	);
    	   } else {
    		   nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
    		    	//new Thread() {
    			  //  @Override
    				public void run()
    			    {
    					if( !torp.dead )
    			    	{
    			    		NavyCraft.instance.DebugMessage(Integer.toString(i), 3);
    				    	if(( torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -1).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -2).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -3).getTypeId() == 35) ||  (torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(BlockFace.UP, -1).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.UP, -2).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.UP, -3).getTypeId() == 35) || (torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(BlockFace.DOWN, -1).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.DOWN, -2).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.DOWN, -3).getTypeId() == 35))
    				    	{
    				    		CraftMover.playWeaponSound(torp.warhead.getLocation(), Sound.ENTITY_PLAYER_BREATH, 2.0f, 0.8f);
    							if( i > 15 )
    							{
    								if( ( torp.warhead.getY() > 58 && torp.torpRotation == 2) || ( torp.warhead.getY() > 62 && torp.torpRotation != 2 ))
    					    		{
    					    			torp.warhead.setType(Material.AIR);
    					    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
    					    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
    					    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
    					    		}else
    					    		{
    					    			torp.warhead.setType(Material.WATER);
    					    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
    					    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
    					    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
    					    		}
    								
        							if( checkProtectedRegion(p, torp.warhead.getLocation()) )
        							{
        								p.sendMessage(ChatColor.RED + "No missiles allowed in dock area!");
        								torp.dead = true;
        								torp.destroyWeapon();
        								return;
        							}
    								
    								if (torp.torpRotation == 4) {
    							    	NavyCraft.instance.DebugMessage("Running block fix", 3);
    									if( torp.warhead.getY() > 62 )
    						    		{
    						    			torp.warhead.setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.AIR);
    						    		}else
    						    		{
    						    			torp.warhead.setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.WATER);
    						    		}
    									if( torp.warhead.getY() > 58 )
    						    		{
    						    			torp.warhead.setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -1).setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -2).setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -3).setType(Material.AIR);
    						    		}else
    						    		{
    						    			torp.warhead.setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -1).setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -2).setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -3).setType(Material.WATER);
    						    		}
    									torp.torpRotation = -1;
    								}
    								
    	    						int depthDifference = torp.setDepth - torp.warhead.getY();
    	    						if( depthDifference > 0)
    	    						{
    	    							if (depthDifference > 4) {
    	    							torp.hdg = BlockFace.UP;
    	    							torp.torpRotation = 1;
    	    							} else {
    	    							torp.warhead = torp.warhead.getRelative(BlockFace.UP);
    	    							}
    	    						if (depthDifference == 5) {
    	    						torp.torpRotation = 3;
    	    						}
    	    					}
    								
    								if (depthDifference <= 0) torp.rangeCounter++;
    								
    								if (depthDifference < 0 && (torp.setRange - torp.rangeCounter < 0)) {
    									if( depthDifference < -4 )
    									{
    									torp.hdg = BlockFace.DOWN;
    									torp.torpRotation = 2;
    									} else {
    										torp.warhead = torp.warhead.getRelative(BlockFace.DOWN);
    									}
    									if (depthDifference == -5) {
    									torp.torpRotation = 3;
    									}
    								}
    								
    							//new position
    							torp.warhead = torp.warhead.getRelative(torp.hdg);
    								
    								if( torp.turnProgress > -1 )
    								{
    									
    									if( torp.turnProgress == 10 )
    									{
    										BlockFace heading = null;
    										if (torp.hdg == BlockFace.DOWN || torp.hdg == BlockFace.UP)
    										heading = torp.targetDirection;
    										else
    										heading = torp.hdg;
    										
    										if( heading == BlockFace.NORTH )
    										{
    											if( torp.rudder < 0 )
    												torp.hdg = BlockFace.WEST;
    											else
    												torp.hdg = BlockFace.EAST;
    										}else if( heading == BlockFace.SOUTH )
    										{
    											if( torp.rudder < 0 )
    												torp.hdg = BlockFace.EAST;
    											else
    												torp.hdg = BlockFace.WEST;
    										}else if( heading == BlockFace.EAST )
    										{
    											if( torp.rudder < 0 )
    												torp.hdg = BlockFace.NORTH;
    											else
    												torp.hdg = BlockFace.SOUTH;
    										}else if ( heading == BlockFace.WEST)
    										{
    											if( torp.rudder < 0 )
    												torp.hdg = BlockFace.SOUTH;
    											else
    												torp.hdg = BlockFace.NORTH;
    										}
    										torp.rudder = -torp.rudder;
    									}
    									
    									if( torp.turnProgress == 20 )
    									{
    										if( torp.doubleTurn )
    										{
    											torp.turnProgress = 0;
    											torp.rudder = -torp.rudder;
    											torp.doubleTurn = false;
    										}else
    										{
    											torp.turnProgress = -1;
    											torp.rudder = torp.rudderSetting;
    										}
    									}else
    										torp.turnProgress += 1;
    								}
    								
    								if( torp.rudder != 0 )
    								{
    									int dirMod  = Math.abs(torp.rudder);
    									if( i % dirMod == 0 )
    									{
    										if( torp.rudder < 0 )
    										{
    											torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, true);
    										}else
    										{
    											torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, false);
    										}
    									}
    								}
    								
    								if (torp.torpRotation == 3) {
    									torp.hdg = torp.ohdg;
    								}
    								
    			    				if (firingCraft != null) {
    				    				Block targetBlock = null;
    				    				Player onScopePlayer=null;
    				    				for( Periscope per: firingCraft.periscopes )
    				    				{
    				    					if( per.user != null )
    				    					{
    				    						onScopePlayer = per.user;
    				    						break;
    				    					}
    				    				}
    				    				
    				    				if( onScopePlayer != null && torp.isGuided ) {
    				    					Set<Material> transp = new HashSet<>();
    				    					transp.add(Material.AIR);
    				    					transp.add(Material.STATIONARY_WATER);
    				    					transp.add(Material.WATER);
											transp.add(Material.LAVA);
											transp.add(Material.STATIONARY_LAVA);
											transp.add(Material.WOOL);
    				    					targetBlock = onScopePlayer.getTargetBlock(transp, 1000);
    				    				} else if( firingCraft.lastPeriscopeBlock != null && torp.isGuided )
    				    					targetBlock = firingCraft.lastPeriscopeBlock;

    				    				if( i >= 40 && i%torp.pingDelay == 0 && torp.turnProgress == -1 && targetBlock != null )
    				    				torp.doBlockTrack(targetBlock);
    				    			}
    								
    								
    								//check new position
									if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.STATIONARY_WATER || torp.warhead.getType() == Material.LAVA || torp.warhead.getType() == Material.STATIONARY_LAVA || torp.warhead.getType() == Material.AIR )
    								{
    				    				if( i == 500 )
    									{
    										if( torp.warhead.getY() > 62 )
    										{
    											torp.warhead.setType(Material.AIR);
    											torp.warhead.getRelative(torp.hdg).setType(Material.AIR);
    											torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
    											torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
    										}else
    										{
    											torp.warhead.setType(Material.WATER);
    											torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
    											torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
    											torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
    										}
    										p.sendMessage(ChatColor.YELLOW +"Missile expired.");
    										torp.destroyWeapon();
    										return;
    									}
    				    				torp.warhead.setTypeIdAndData(35, (byte) 0xE, false);
    				    				torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x0, false);
    				    				torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x0, false);
    				    				torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
    								}else if( torp.active ) ///detonate!
    								{
    									if( checkProtectedRegion(p, torp.warhead.getLocation()) )
    									{
    										p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");
    										torp.destroyWeapon();
    										return;
    									}
    									
    									
    									
    									
    									
    									torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
    									NavyCraft.explosion(8,  torp.warhead, false);
    									torp.dead = true;
    									torp.destroyWeapon();
    			
    									Craft checkCraft=null;
    									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
    									if( checkCraft == null ) {
    										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
    										if( checkCraft == null ) {
    											checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
    											if( checkCraft == null ) {
    												checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
    												if( checkCraft == null ) {
    													checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
    												}
    											}
    										}
    									}
    									
    									if( checkCraft == null )
    										p.sendMessage(ChatColor.YELLOW + "Missile hit unknown object!");
    									else
    										p.sendMessage(ChatColor.GREEN + "Missile hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
    												
    								
    								}else
    								{
    									torp.dead = true;
    									torp.destroyWeapon();
    									p.sendMessage(ChatColor.RED + "Dud Missile! Too close.1");
    								}
    								
    								
    							}
    							else/// i <= 15
    							{
    								if( torp.warhead.getY() > 62 || i < 5 )
    								{
    									torp.warhead.setType(Material.AIR);
    									torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.AIR);
    									torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.AIR);
    									torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.AIR);
    									
    									if( i == 4  )
    									{
    										if( firingCraft != null )
    										{
    											firingCraft.addBlock(torp.warhead, true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -1), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -2), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -3), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -4), true);
    			
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -5), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -6), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -7), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -8), true);
    										}
    									}
    								}else
    								{
    									torp.warhead.setType(Material.WATER);
    									torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.WATER);
    									torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.WATER);
    									torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.WATER);
    								}
    								
    			
    								//Move torp
    								torp.warhead = torp.warhead.getRelative(BlockFace.UP);
    								
    								if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.AIR || torp.warhead.getType() == Material.STATIONARY_WATER )
    								{
    									torp.warhead.setTypeIdAndData(35, (byte) 0xE, false);
    					    			torp.warhead.getRelative(BlockFace.UP, -1).setTypeIdAndData(35, (byte) 0x0, false);
    					    			torp.warhead.getRelative(BlockFace.UP, -2).setTypeIdAndData(35, (byte) 0x0, false);
    					    			torp.warhead.getRelative(BlockFace.UP, -3).setTypeIdAndData(35, (byte) 0x7, false);
    								}else
    								{
    									if( firingCraft != null )
    									{
    										firingCraft.waitTorpLoading--;
    										
    										if(!checkOuterDoorClosedV() )
    											openMissileDoorsV(p);
    									}else
    									{
    										
    										if(!checkOuterDoorClosedV() )
    											openMissileDoorsV(p);
    									}
    									p.sendMessage(ChatColor.RED + "Dud Missile! Too close.2");
    									torp.dead = true;
    									torp.destroyWeapon();
    								}
    								
    								if (i == 14) {
    									torp.torpRotation = 3;
    								}
    								
    							}
    							
    							
    				    		
    				    	}else //else torp blocks missing, detonate
    				    	{
    				    		if( checkProtectedRegion(p, torp.warhead.getLocation()) )
    							{
    								p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");
    								return;
    							}
    							
    				    		if( !torp.active )
    				    		{
    				    			p.sendMessage(ChatColor.RED + "Dud Missile! Too close.3");
    								torp.dead = true;
    								torp.destroyWeapon();
    								if( firingCraft != null )
    								{
    									firingCraft.waitTorpLoading--;
    									
    									if(!checkOuterDoorClosedV() )
    										openMissileDoorsV(p);
    								}else
    								{	
    									if(!checkOuterDoorClosedV() )
    										openMissileDoorsV(p);
    								}
    								return;
    				    		}
    							
    							torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
    							NavyCraft.explosion(8,  torp.warhead, false);
    							torp.dead = true;
    							torp.destroyWeapon();
    							
    							Craft checkCraft=null;
    							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
    							if( checkCraft == null ) {
    								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
    								if( checkCraft == null ) {
    									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
    									if( checkCraft == null ) {
    										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
    										if( checkCraft == null ) {
    											checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
    										}
    									}
    								}
    							}
    							
    							if( checkCraft == null )
    								p.sendMessage(ChatColor.YELLOW + "Missile detonated prematurely!");
    							else
    								p.sendMessage(ChatColor.GREEN + "Missile hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
    							
    							
    							if( firingCraft != null )
    							{
    								firingCraft.waitTorpLoading--;
    								if(!checkOuterDoorClosedV() )
    									openMissileDoorsV(p);
    							}else
    							{	
    								if(!checkOuterDoorClosedV() )
    									openMissileDoorsV(p);
    							}
    				    	}
    				    	if (torp.torpRotation == 3) {
    				    		torp.torpRotation = 4;
    				    	}
    			    	}
    				    	if( i == 15 )
    						{
    							
    							if( firingCraft != null )
    							{
    								firingCraft.waitTorpLoading--;
    								if(!checkOuterDoorClosedV() )
    									openMissileDoorsV(p);
    							}else
    							{
    								if(!checkOuterDoorClosedV() )
    									openMissileDoorsV(p);
    							}
    							torp.active = true;
    						}
    			    	}

    			    }
    		    	
    			);
    	   }
}
    
    public void fireMissileMk3(final Player p, final Block b, final BlockFace torpHeading, final int torpDepth, final int delayShoot, final int torpRange, final boolean left, final boolean isVertical){
    	//final int taskNum;
    	//int taskNum = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
    	final Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		final Weapon torp = new Weapon(b, torpHeading, torpDepth, torpRange);
		AimCannon.weapons.add(torp);
		
		Sign sign = null;
		
		if (torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getTypeId() == 68) sign = (Sign) torp.warhead.getRelative(torp.hdg, -4).getRelative(BlockFace.UP).getState();
		
		if (torp.warhead.getRelative(BlockFace.UP, -3).getRelative(direction, -2).getTypeId() == 68) sign = (Sign) torp.warhead.getRelative(BlockFace.UP, -3).getRelative(direction, -2).getState();
		
		if( sign != null )
		{
			String signLine0 =  sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
			int tubeNum = -2;
			if( left )
				tubeNum = -1;
			if( signLine0.equalsIgnoreCase("Tube") )
			{
				String tubeString = sign.getLine(1).trim().toLowerCase();
				tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
				if( !tubeString.isEmpty() )
				{
					try{
						tubeNum = Integer.parseInt(tubeString);
					}catch (NumberFormatException nfe)
					{
						tubeNum=0;
					}
				}
			}
			torp.tubeNum=tubeNum;
		}
		
		
		if( testCraft != null && torp.tubeNum > 0 && testCraft.tubeFiringMode.containsKey(torp.tubeNum) )
		{
			Player onScopePlayer=null;
			for( Periscope per: testCraft.periscopes )
			{
				if( per.user != null )
				{
					onScopePlayer = per.user;
					break;
				}
			}
			
			float rotation=0;
			if( onScopePlayer != null && testCraft.tubeFiringMode.get(torp.tubeNum) == -1 ) //firing periscope mode, player on scope
			{
				rotation = (float) Math.PI * onScopePlayer.getLocation().getYaw() / 180f;
				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);
				transp.add(Material.STATIONARY_WATER);
				transp.add(Material.WATER);
				transp.add(Material.LAVA);
				transp.add(Material.STATIONARY_LAVA);
				transp.add(Material.WOOL);
				float xDist = onScopePlayer.getTargetBlock(transp, 1000).getX() - torp.warhead.getX();
				float zDist = onScopePlayer.getTargetBlock(transp, 1000).getZ() - torp.warhead.getZ();
				torp.setRange=torp.calculateRange(xDist, zDist);
				torp.setDepth = onScopePlayer.getTargetBlock(transp, 1000).getY();
				torp.isGuided = true;
			}else if( testCraft.lastPeriscopeYaw != -9999 && testCraft.tubeFiringMode.get(torp.tubeNum) == -1 ) //firing periscope mode, last used periscope yaw
			{
				rotation = (float) Math.PI * testCraft.lastPeriscopeYaw / 180f;
				torp.setDepth = testCraft.lastPeriscopeBlock.getY();
				float xDist = testCraft.lastPeriscopeBlock.getX() - torp.warhead.getX();
				float zDist = testCraft.lastPeriscopeBlock.getZ() - torp.warhead.getZ();
				torp.setRange=torp.calculateRange(xDist, zDist);
				torp.isGuided = true;
			}else if( testCraft.tubeFiringMode.get(torp.tubeNum) >= 0 )  //firing at target
			{
				int targetID = testCraft.fireControlTargets.get(torp.tubeNum);
				Craft targetCraft = testCraft.sonarTargetIDs2.get(targetID);
				float xDist = targetCraft.getLocation().getBlockX() - torp.warhead.getX();
				float zDist = targetCraft.getLocation().getBlockZ() - torp.warhead.getZ();
				torp.setRange=torp.calculateRange(xDist, zDist);
				//rotation = torp.calculateRelBearing(xDist, zDist);
				rotation = (float) Math.PI * (torp.calculateRelBearing(xDist, zDist)-180f) / 180f;
				torp.setDepth = targetCraft.minY + targetCraft.sizeY/3;
			}
			else //firing straight mode
			{
				rotation = (float) Math.PI * (torp.weaponRotation()+180f) / 180f;
				
			}
			
			
			torp.calculateHeading(rotation);
			
			testCraft.tubeFiringMode.put(torp.tubeNum, -3);
			testCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
			
			for( String s : testCraft.crewNames )
			{
				Player pl = nc.getServer().getPlayer(s);
				if( pl != null )
				{
					if( torp.tubeNum == 0 )
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube Fired! - Depth: " + torp.setDepth);
					else
						pl.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube " + torp.tubeNum + " Fired! - Depth: " + torp.setDepth);
				}
			}
	
		} else {
			if (p != null)
			p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tube Fired! - Depth: " + torp.setDepth);
		}
    	
    	
    	
		new Thread(){
			
    	@Override
	public void run() {
    		
    		setPriority(Thread.MIN_PRIORITY);
				//taskNum = -1;
    	if (!isVertical) {
    			try{
    				sleep(delayShoot);
    				
    				
    				
    				
    				for( int i=0; i<1550; i++ )
    				{
						fireMissileUpdateMk3(p, torp, i, testCraft, left, false);
						sleep(100);
					} 
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
    		} else {
    			try{
    				sleep(delayShoot);
    				
    				
    				
    				
    				for( int i=0; i<1550; i++ )
    				{
						fireMissileUpdateMk3(p, torp, i, testCraft, left, true);
						sleep(125);
					} 
				}catch (InterruptedException e) {
					e.printStackTrace();
				}	
    		}
		}
    	}.start(); //, 20L);
    }
    
    public void fireMissileUpdateMk3(final Player p, final Weapon torp, final int i, final Craft firingCraft, final boolean left, final boolean isVertical) {
    	if (!isVertical) {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
    	//new Thread() {
	  //  @Override
		public void run()
	    {
	    	//getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
	    //	}
		//setPriority(Thread.MIN_PRIORITY);
			//try
			//{ 
	    	if( !torp.dead )
	    	{
		    	if( torp.warhead.getY() < 255 && torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -1).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -2).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -3).getTypeId() == 35 )
		    	{
		    		CraftMover.playWeaponSound(torp.warhead.getLocation(), Sound.ENTITY_PLAYER_BREATH, 2.0f, 0.8f);
					if( i > 15 )
					{
						if( torp.warhead.getY() > 62 )
			    		{
			    			torp.warhead.setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
			    		}else
			    		{
			    			torp.warhead.setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
			    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
			    		}
						
						if( checkProtectedRegion(p, torp.warhead.getLocation()) )
						{
							if (p != null)
							p.sendMessage(ChatColor.RED + "No missiles allowed in dock area!");

							if( firingCraft != null && torp.tubeNum > 0 ) {
								firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
								firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
								firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
								firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
								firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
								firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
								firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
							}
							torp.dead = true;
							torp.destroyWeapon();
							return;
						}
						
						//check sub update
						if( firingCraft != null && torp.tubeNum > 0 && firingCraft.tubeFiringMode.containsKey(torp.tubeNum) )
						{
							if( firingCraft.tubeFiringHeading.get(torp.tubeNum) != torp.torpSetHeading )
							{
								torp.torpSetHeading = firingCraft.tubeFiringHeading.get(torp.tubeNum);
							}
							if( firingCraft.tubeFiringArmed.get(torp.tubeNum) != torp.active )
							{
								torp.active = firingCraft.tubeFiringArmed.get(torp.tubeNum);
							}
							if( firingCraft.tubeFiringAuto.get(torp.tubeNum) != torp.auto )
							{
								torp.auto = firingCraft.tubeFiringAuto.get(torp.tubeNum);
							}
							
							if( !firingCraft.tubeFiringArmed.get(torp.tubeNum) && (i == firingCraft.tubeFiringArm.get(torp.tubeNum) || (i==11 && firingCraft.tubeFiringArm.get(torp.tubeNum)==10)) )
							{
								firingCraft.tubeFiringArmed.put(torp.tubeNum,true);
								torp.active = true;
							}
						}
						
						
						//new position
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						int depthDifference = torp.setDepth - torp.warhead.getY();
						if( depthDifference < 0 )
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.DOWN);
						}else if( depthDifference > 0)
						{
							torp.warhead = torp.warhead.getRelative(BlockFace.UP);
						}
						
						if( torp.turnProgress > -1 )
						{
							
							if( torp.turnProgress == 10 )
							{
								if( torp.hdg == BlockFace.NORTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.WEST;
									else
										torp.hdg = BlockFace.EAST;
								}else if( torp.hdg == BlockFace.SOUTH )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.EAST;
									else
										torp.hdg = BlockFace.WEST;
								}else if( torp.hdg == BlockFace.EAST )
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.NORTH;
									else
										torp.hdg = BlockFace.SOUTH;
								}else
								{
									if( torp.rudder < 0 )
										torp.hdg = BlockFace.SOUTH;
									else
										torp.hdg = BlockFace.NORTH;
								}
								torp.rudder = -torp.rudder;
							}
							
							if( torp.turnProgress == 20 )
							{
								if( torp.doubleTurn )
								{
									torp.turnProgress = 0;
									torp.rudder = -torp.rudder;
									torp.doubleTurn = false;
								}else
								{
									torp.turnProgress = -1;
									torp.rudder = torp.rudderSetting;
								}
							}else
								torp.turnProgress += 1;
						}
						
						if( torp.rudder != 0 )
						{
							int dirMod  = Math.abs(torp.rudder);
							if( i % dirMod == 0 )
							{
								if( torp.rudder < 0 )
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, true);
								}else
								{
									torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, false);
								}
							}
						}
						
						
						//check new position
						if( torp.warhead.getType() == Material.AIR )
						{
		    				if( i == 549 )
							{
								if( torp.warhead.getY() > 62 )
								{
									torp.warhead.setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
								}else
								{
									torp.warhead.setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
									torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
								}
								if (p != null)
								p.sendMessage(ChatColor.YELLOW + "Missile expired.");

								if( firingCraft != null && torp.tubeNum > 0 ) {
									firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
									firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
									firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
									firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
									firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
									firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
									firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
								}
								torp.destroyWeapon();
								return;
							}
		    				
		    				torp.warhead.setTypeIdAndData(35, (byte) 0x5, false);
		    				torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x0, false);
		    				torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x0, false);
		    				torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
		    				
		    				if( i >= 40 && i%torp.pingDelay == 0 && torp.turnProgress == -1 && firingCraft != null )
		    					torp.doPingTrack(firingCraft);
		    				
		    				if (firingCraft != null) {
		    				Block targetBlock = null;
		    				Player onScopePlayer=null;
		    				for( Periscope per: firingCraft.periscopes )
		    				{
		    					if( per.user != null )
		    					{
		    						onScopePlayer = per.user;
		    						break;
		    					}
		    				}
		    				
		    				if( onScopePlayer != null && torp.isGuided ) {
		    					Set<Material> transp = new HashSet<>();
		    					transp.add(Material.AIR);
		    					transp.add(Material.STATIONARY_WATER);
		    					transp.add(Material.WATER);
								transp.add(Material.LAVA);
								transp.add(Material.STATIONARY_LAVA);
								transp.add(Material.WOOL);
		    					targetBlock = onScopePlayer.getTargetBlock(transp, 1000);
		    				} else if( firingCraft.lastPeriscopeBlock != null && torp.isGuided )
		    					targetBlock = firingCraft.lastPeriscopeBlock;

		    				if( i >= 40 && i%torp.pingDelay == 0 && torp.turnProgress == -1 && targetBlock != null )
		    				torp.doBlockTrack(targetBlock);
		    			}
		    				
		    				
						}else if( torp.active ) ///detonate!
						{
							if( checkProtectedRegion(p, torp.warhead.getLocation()) )
							{
								if (p != null)
								p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");

								torp.dead = true;
								if( firingCraft != null && torp.tubeNum > 0 ) {
									firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
									firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
									firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
									firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
									firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
									firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
									firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
								}
								torp.destroyWeapon();
								
								return;
							}
	
							
							torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
							NavyCraft.explosion(8,  torp.warhead, false);
							torp.dead = true;
							torp.destroyWeapon();
							
							Craft checkCraft=null;
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
									if( checkCraft == null ) {
										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
										if( checkCraft == null ) {
											checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
										}
									}
								}
							}

							if (p != null) {
								if (checkCraft == null)
									p.sendMessage(ChatColor.YELLOW + "Missile hit unknown object!");
								else
									p.sendMessage(ChatColor.GREEN + "Missile hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
							}

							if( firingCraft != null && torp.tubeNum > 0 ) {
								firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
								firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
								firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
								firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
								firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
								firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
								firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
							}
							
						}else
						{
							if (p != null)
							p.sendMessage(ChatColor.RED + "Dud Missile! (Inactive)");

							torp.dead = true;
							if( firingCraft != null && torp.tubeNum > 0 ) {
								firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
								firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
								firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
								firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
								firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
								firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
								firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
							}
							torp.destroyWeapon();
							//CraftMover cm = new CraftMover( firingCraft, plugin);
							//cm.structureUpdate(null);
						}
						
						
					}
					else/// i <= 15
					{
						if( torp.warhead.getY() > 62 || i < 5 )
						{
							torp.warhead.setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
							
							if( i == 4  )
							{
								if( firingCraft != null )
								{
									firingCraft.addBlock(torp.warhead, true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -1), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -2), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -3), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -4), true);
	
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -5), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -6), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -7), true);
									firingCraft.addBlock(torp.warhead.getRelative(torp.hdg, -8), true);
								}
							}
						}else
						{
							torp.warhead.setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
							torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
						}
						
						
						
						
						//Move torp
						torp.warhead = torp.warhead.getRelative(torp.hdg);
						
						if( torp.warhead.getType() == Material.AIR )
						{
							torp.warhead.setTypeIdAndData(35, (byte) 0x5, false);
			    			torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x0, false);
			    			torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x0, false);
			    			torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
						}else
						{
							if( firingCraft != null && torp.tubeNum > 0 ) {
								firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
								firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
								firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
								firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
								firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
								firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
								firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
								
								firingCraft.waitTorpLoading--;
							}
							torp.dead = true;
							torp.destroyWeapon();
							if (p != null)
							p.sendMessage(ChatColor.RED + "Dud Missile! Too close.2");

						}
	
					}
					
					
		    		
		    	}else //torp blocks missing, detonate
		    	{
		    		if(checkProtectedRegion(p, torp.warhead.getLocation()) )
					{
						if (p != null)
						p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");
						return;
					}
					
		    		if( !torp.active )
		    		{
						if (p != null)
		    			p.sendMessage(ChatColor.RED + "Dud Missile! Too close.3");

						torp.dead = true;
						torp.destroyWeapon();
						if( firingCraft != null )
						{
							firingCraft.waitTorpLoading--;
						}
						return;
		    		}
					
					
					torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
					NavyCraft.explosion(8,  torp.warhead, false);
					torp.dead = true;
					torp.destroyWeapon();
					
					Craft checkCraft=null;
					checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
					if( checkCraft == null ) {
						checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
						if( checkCraft == null ) {
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
							if( checkCraft == null ) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
								}
							}
						}
					}

					if (p != null) {
						if (checkCraft == null)
							p.sendMessage(ChatColor.RED + "Missile detonated prematurely!");
						else
							p.sendMessage(ChatColor.GREEN + "Missile hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
					}
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
					}
		    		if( firingCraft != null && torp.tubeNum > 0 && firingCraft.tubeFiringMode.get(torp.tubeNum) == -3 )
		    		{
			    		firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
						firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
						firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
						firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
						firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
						firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
						firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
		    		}
		    	}
		    	
		    	if( i == 15 )
				{
					
					if( firingCraft != null )
					{
						firingCraft.waitTorpLoading--;
					}
					torp.active = true;
				}
	    	}

	    }
    	}
    	
	);
    	   } else {
    		   nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
    		    	//new Thread() {
    			  //  @Override
    				public void run()
    			    {
    					if( !torp.dead )
    			    	{
    			    		NavyCraft.instance.DebugMessage(Integer.toString(i), 3);
    				    	if(( torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -1).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -2).getTypeId() == 35 && torp.warhead.getRelative(torp.hdg, -3).getTypeId() == 35) ||  (torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(BlockFace.UP, -1).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.UP, -2).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.UP, -3).getTypeId() == 35) || (torp.warhead.getTypeId() == 35 && torp.warhead.getRelative(BlockFace.DOWN, -1).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.DOWN, -2).getTypeId() == 35 && torp.warhead.getRelative(BlockFace.DOWN, -3).getTypeId() == 35))
    				    	{
    				    		CraftMover.playWeaponSound(torp.warhead.getLocation(), Sound.ENTITY_PLAYER_BREATH, 2.0f, 0.8f);
    							if( i > 15 )
    							{
    								if( ( torp.warhead.getY() > 58 && torp.torpRotation == 2) || ( torp.warhead.getY() > 62 && torp.torpRotation != 2 ))
    					    		{
    					    			torp.warhead.setType(Material.AIR);
    					    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.AIR);
    					    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
    					    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
    					    		}else
    					    		{
    					    			torp.warhead.setType(Material.WATER);
    					    			torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
    					    			torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
    					    			torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
    					    		}
    								
        							if( checkProtectedRegion(p, torp.warhead.getLocation()) )
        							{
										if (p != null)
        								p.sendMessage(ChatColor.RED + "No missiles allowed in dock area!");
        								if( firingCraft != null && torp.tubeNum > 0 ) {
        									firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
        									firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
        									firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
        									firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
        									firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
        									firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
        									firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
        								}
        								torp.destroyWeapon();
        								torp.dead = true;
        								return;
        							}
    								
    								if (torp.torpRotation == 4) {
    							    	NavyCraft.instance.DebugMessage("Running block fix", 3);
    									if( torp.warhead.getY() > 62 )
    						    		{
    						    			torp.warhead.setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.AIR);
    						    		}else
    						    		{
    						    			torp.warhead.setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.WATER);
    						    		}
    									if( torp.warhead.getY() > 58 )
    						    		{
    						    			torp.warhead.setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -1).setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -2).setType(Material.AIR);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -3).setType(Material.AIR);
    						    		}else
    						    		{
    						    			torp.warhead.setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -1).setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -2).setType(Material.WATER);
    						    			torp.warhead.getRelative(BlockFace.DOWN, -3).setType(Material.WATER);
    						    		}
    									torp.torpRotation = -1;
    								}
    								
    	    						int depthDifference = torp.setDepth - torp.warhead.getY();
    	    						if( depthDifference > 0)
    	    						{
    	    							if (depthDifference > 4) {
    	    							torp.hdg = BlockFace.UP;
    	    							torp.torpRotation = 1;
        	    						if (depthDifference == 5) {
            	    						torp.torpRotation = 3;
            	    						}
    	    							} else {
    	    							torp.warhead = torp.warhead.getRelative(BlockFace.UP);
    	    							}
    	    					}
    								
    								if (depthDifference <= 0) torp.rangeCounter++;
    								
    								if (depthDifference < 0 && (torp.setRange - torp.rangeCounter) < 0) {
    									if( depthDifference < -4 )
    									{
    									torp.hdg = BlockFace.DOWN;
    									torp.torpRotation = 2;
        	    						if (depthDifference == -5) {
            	    						torp.torpRotation = 3;
            	    						}
    									} else {
    										torp.warhead = torp.warhead.getRelative(BlockFace.DOWN);
    									}
    								}
    								
    								//check sub update
    								if( firingCraft != null && torp.tubeNum > 0 && firingCraft.tubeFiringMode.containsKey(torp.tubeNum) )
    								{
    									if( firingCraft.tubeFiringHeading.get(torp.tubeNum) != torp.torpSetHeading )
    									{
    										torp.torpSetHeading = firingCraft.tubeFiringHeading.get(torp.tubeNum);
    									}
    									if( firingCraft.tubeFiringArmed.get(torp.tubeNum) != torp.active )
    									{
    										torp.active = firingCraft.tubeFiringArmed.get(torp.tubeNum);
    									}
    									if( firingCraft.tubeFiringAuto.get(torp.tubeNum) != torp.auto )
    									{
    										torp.auto = firingCraft.tubeFiringAuto.get(torp.tubeNum);
    									}
    									
    									if( !firingCraft.tubeFiringArmed.get(torp.tubeNum) && (i == firingCraft.tubeFiringArm.get(torp.tubeNum) || (i==11 && firingCraft.tubeFiringArm.get(torp.tubeNum)==10)) )
    									{
    										firingCraft.tubeFiringArmed.put(torp.tubeNum,true);
    										torp.active = true;
    									}
    								}
    							//new position
    							torp.warhead = torp.warhead.getRelative(torp.hdg);
    								
    								if( torp.turnProgress > -1 )
    								{
    									
    									if( torp.turnProgress == 10 )
    									{
    										BlockFace heading = null;
    										if (torp.hdg == BlockFace.DOWN || torp.hdg == BlockFace.UP)
    										heading = torp.targetDirection;
    										else
    										heading = torp.hdg;
    										
    										if( heading == BlockFace.NORTH )
    										{
    											if( torp.rudder < 0 )
    												torp.hdg = BlockFace.WEST;
    											else
    												torp.hdg = BlockFace.EAST;
    										}else if( heading == BlockFace.SOUTH )
    										{
    											if( torp.rudder < 0 )
    												torp.hdg = BlockFace.EAST;
    											else
    												torp.hdg = BlockFace.WEST;
    										}else if( heading == BlockFace.EAST )
    										{
    											if( torp.rudder < 0 )
    												torp.hdg = BlockFace.NORTH;
    											else
    												torp.hdg = BlockFace.SOUTH;
    										}else if ( heading == BlockFace.WEST)
    										{
    											if( torp.rudder < 0 )
    												torp.hdg = BlockFace.SOUTH;
    											else
    												torp.hdg = BlockFace.NORTH;
    										}
    										torp.rudder = -torp.rudder;
    									}
    									
    									if( torp.turnProgress == 20 )
    									{
    										if( torp.doubleTurn )
    										{
    											torp.turnProgress = 0;
    											torp.rudder = -torp.rudder;
    											torp.doubleTurn = false;
    										}else
    										{
    											torp.turnProgress = -1;
    											torp.rudder = torp.rudderSetting;
    										}
    									}else
    										torp.turnProgress += 1;
    								}
    								
    								if( torp.rudder != 0 )
    								{
    									int dirMod  = Math.abs(torp.rudder);
    									if( i % dirMod == 0 )
    									{
    										if( torp.rudder < 0 )
    										{
    											torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, true);
    										}else
    										{
    											torp.warhead = getDirectionFromRelative(torp.warhead, torp.hdg, false);
    										}
    									}
    								}

									if (torp.torpRotation == 3) {
									torp.hdg = torp.ohdg;
									}
    								
				    				if( i >= 40 && i%torp.pingDelay == 0 && torp.turnProgress == -1 && firingCraft != null )
				    					torp.doPingTrack(firingCraft);
				    				
				    				if (firingCraft != null) {
				    				Block targetBlock = null;
				    				Player onScopePlayer=null;
				    				for( Periscope per: firingCraft.periscopes )
				    				{
				    					if( per.user != null )
				    					{
				    						onScopePlayer = per.user;
				    						break;
				    					}
				    				}
				    				
				    				if( onScopePlayer != null && torp.isGuided ) {
				    					Set<Material> transp = new HashSet<>();
				    					transp.add(Material.AIR);
				    					transp.add(Material.STATIONARY_WATER);
				    					transp.add(Material.WATER);
										transp.add(Material.LAVA);
										transp.add(Material.STATIONARY_LAVA);
										transp.add(Material.WOOL);
				    					targetBlock = onScopePlayer.getTargetBlock(transp, 1000);
				    				} else if( firingCraft.lastPeriscopeBlock != null && torp.isGuided )
				    					targetBlock = firingCraft.lastPeriscopeBlock;

				    				if( i >= 40 && i%torp.pingDelay == 0 && torp.turnProgress == -1 && targetBlock != null )
				    				torp.doBlockTrack(targetBlock);
				    				
				    				}
    								
    								//check new position
									if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.STATIONARY_WATER || torp.warhead.getType() == Material.LAVA || torp.warhead.getType() == Material.STATIONARY_LAVA || torp.warhead.getType() == Material.AIR )
    								{
    				    				if( i == 500 )
    									{
    										if( torp.warhead.getY() > 62 )
    										{
    											torp.warhead.setType(Material.AIR);
    											torp.warhead.getRelative(torp.hdg).setType(Material.AIR);
    											torp.warhead.getRelative(torp.hdg, -2).setType(Material.AIR);
    											torp.warhead.getRelative(torp.hdg, -3).setType(Material.AIR);
    										}else
    										{
    											torp.warhead.setType(Material.WATER);
    											torp.warhead.getRelative(torp.hdg, -1).setType(Material.WATER);
    											torp.warhead.getRelative(torp.hdg, -2).setType(Material.WATER);
    											torp.warhead.getRelative(torp.hdg, -3).setType(Material.WATER);
    										}
											if (p != null)
    										p.sendMessage(ChatColor.YELLOW +"Missile expired.");
    										if( firingCraft != null && torp.tubeNum > 0 ) {
    											firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
    											firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
    											firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
    											firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
    											firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
    											firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
    											firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
    										}
    										torp.destroyWeapon();
    										return;
    									}
    				    				torp.warhead.setTypeIdAndData(35, (byte) 0x5, false);
    				    				torp.warhead.getRelative(torp.hdg, -1).setTypeIdAndData(35, (byte) 0x0, false);
    				    				torp.warhead.getRelative(torp.hdg, -2).setTypeIdAndData(35, (byte) 0x0, false);
    				    				torp.warhead.getRelative(torp.hdg, -3).setTypeIdAndData(35, (byte) 0x7, false);
    				    				
    								}else if( torp.active ) ///detonate!
    								{
    									if( checkProtectedRegion(p, torp.warhead.getLocation()) )
    									{
											if (p != null)
    										p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");
    	    								if( firingCraft != null && torp.tubeNum > 0 ) {
    	    									firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
    	    									firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
    	    									firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
    	    									firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
    	    									firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
    	    									firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
    	    									firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
    	    								}
    	    								torp.destroyWeapon();
    										torp.dead = true;
    							    		if( firingCraft != null && torp.tubeNum > 0 && firingCraft.tubeFiringMode.get(torp.tubeNum) == -3 )
    							    		{
    								    		firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
    											firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
    											firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
    											firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
    											firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
    											firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
    											firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
    											//CraftMover cm = new CraftMover( firingCraft, plugin);
    											//cm.structureUpdate(null);
    							    		}
    										return;
    									}
    									
    									
    									
    									
    									
    									torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
    									NavyCraft.explosion(10,  torp.warhead, false);
    									torp.dead = true;
    									torp.destroyWeapon();
    			
    									Craft checkCraft=null;
    									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
    									if( checkCraft == null ) {
    										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
    										if( checkCraft == null ) {
    											checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
    											if( checkCraft == null ) {
    												checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
    												if( checkCraft == null ) {
    													checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
    												}
    											}
    										}
    									}


										if (p != null) {
											if (checkCraft == null)
												p.sendMessage(ChatColor.YELLOW + "Missile hit unknown object!");
											else
												p.sendMessage(ChatColor.GREEN + "Missile hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
											if (firingCraft != null && torp.tubeNum > 0) {
												firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
												firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
												firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
												firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
												firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
												firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
												firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
											}
										}
    								
    								}else
    								{
										if (p != null)
    									p.sendMessage(ChatColor.RED + "Dud Missile! Too close. (Inactive)");
    									if( firingCraft != null && torp.tubeNum > 0 ) {
    										firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
    										firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
    										firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
    										firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
    										firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
    										firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
    										firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
    									}
    									torp.destroyWeapon();
    								}
    								
    								
    							}
    							else/// i <= 15
    							{
    								if( torp.warhead.getY() > 62 || i < 5 )
    								{
    									torp.warhead.setType(Material.AIR);
    									torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.AIR);
    									torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.AIR);
    									torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.AIR);
    									
    									if( i == 4  )
    									{
    										if( firingCraft != null )
    										{
    											firingCraft.addBlock(torp.warhead, true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -1), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -2), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -3), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -4), true);
    			
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -5), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -6), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -7), true);
    											firingCraft.addBlock(torp.warhead.getRelative(BlockFace.UP, -8), true);
    										}
    									}
    								}else
    								{
    									torp.warhead.setType(Material.WATER);
    									torp.warhead.getRelative(BlockFace.UP, -1).setType(Material.WATER);
    									torp.warhead.getRelative(BlockFace.UP, -2).setType(Material.WATER);
    									torp.warhead.getRelative(BlockFace.UP, -3).setType(Material.WATER);
    								}
    								
    			
    								//Move torp
    								torp.warhead = torp.warhead.getRelative(BlockFace.UP);
    								
    								if( torp.warhead.getType() == Material.WATER || torp.warhead.getType() == Material.AIR || torp.warhead.getType() == Material.STATIONARY_WATER )
    								{
    									torp.warhead.setTypeIdAndData(35, (byte) 0x5, false);
    					    			torp.warhead.getRelative(BlockFace.UP, -1).setTypeIdAndData(35, (byte) 0x0, false);
    					    			torp.warhead.getRelative(BlockFace.UP, -2).setTypeIdAndData(35, (byte) 0x0, false);
    					    			torp.warhead.getRelative(BlockFace.UP, -3).setTypeIdAndData(35, (byte) 0x7, false);
    								}else
    								{
										if (p != null)
    									p.sendMessage(ChatColor.RED + "Dud Missile! Too close.2");
    									torp.dead = true;
    									torp.destroyWeapon();
    									if( firingCraft != null && torp.tubeNum > 0 ) {
    										firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
    										firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
    										firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
    										firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
    										firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
    										firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
    										firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
    										firingCraft.waitTorpLoading--;
    										if(!checkOuterDoorClosedV() )
    											openMissileDoorsV(p);
    									}
    								}
    								
    								if (i == 14) {
    									torp.torpRotation = 3;
    								}
    								
    							}
    							
    							
    				    		
    				    	}else //else torp blocks missing, detonate
    				    	{
    				    		if( checkProtectedRegion(p, torp.warhead.getLocation()) )
    							{
									if (p != null)
    								p.sendMessage(ChatColor.RED + "No missile explosions in dock area.");
									torp.dead = true;
									torp.destroyWeapon();
									if( firingCraft != null && torp.tubeNum > 0 ) {
										firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
										firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
										firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
										firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
										firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
										firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
										firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
										firingCraft.waitTorpLoading--;
										if(!checkOuterDoorClosedV() )
											openMissileDoorsV(p);
									}
    								return;
    							}
    							
    				    		if( !torp.active )
    				    		{
									if (p != null)
    				    			p.sendMessage(ChatColor.RED + "Dud Missile! Too close.3");
    								torp.dead = true;
    								torp.destroyWeapon();
									if( firingCraft != null && torp.tubeNum > 0 ) {
										firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
										firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
										firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
										firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
										firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
										firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
										firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
										firingCraft.waitTorpLoading--;
										if(!checkOuterDoorClosedV() )
											openMissileDoorsV(p);
									}
    								if( firingCraft != null )
    								{
    									firingCraft.waitTorpLoading--;
    								}
									if(!checkOuterDoorClosedV() )
										openMissileDoorsV(p);
    								return;
    				    		}
    							
    							torp.warhead = torp.warhead.getRelative(torp.hdg,-1);
    							NavyCraft.explosion(10,  torp.warhead, false);
    							torp.dead = true;
    							torp.destroyWeapon();
    							
    							Craft checkCraft=null;
    							checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(torp.hdg,2).getLocation(), p);
    							if( checkCraft == null ) {
    								checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(7,7,7).getLocation(), p);
    								if( checkCraft == null ) {
    									checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-7,-7,-7).getLocation(), p);
    									if( checkCraft == null ) {
    										checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(3,-2,-3).getLocation(), p);
    										if( checkCraft == null ) {
    											checkCraft = NavyCraft.instance.entityListener.structureUpdate(torp.warhead.getRelative(-3,2,3).getLocation(), p);
    										}
    									}
    								}
    							}
								if (p != null) {
									if (checkCraft == null)
										p.sendMessage(ChatColor.YELLOW + "Missile detonated prematurely!");
									else
										p.sendMessage(ChatColor.GREEN + "Missile hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");
								}
									if (firingCraft != null && torp.tubeNum > 0 && firingCraft.tubeFiringMode.get(torp.tubeNum) == -3) {
										firingCraft.tubeFiringMode.put(torp.tubeNum, -2);
										firingCraft.tubeFiringDepth.put(torp.tubeNum, 1);
										firingCraft.tubeFiringArm.put(torp.tubeNum, 20);
										firingCraft.tubeFiringArmed.put(torp.tubeNum, false);
										firingCraft.tubeFiringHeading.put(torp.tubeNum, firingCraft.rotation);
										firingCraft.tubeFiringAuto.put(torp.tubeNum, true);
										firingCraft.tubeFiringDisplay.put(torp.tubeNum, 0);
									}
    							
    							if( firingCraft != null )
    							{
    								firingCraft.waitTorpLoading--;
    								if(!checkOuterDoorClosedV() )
    									openMissileDoorsV(p);
    							}else
    							{	
    								if(!checkOuterDoorClosedV() )
    									openMissileDoorsV(p);
    							}
    				    	}
    				    	if (torp.torpRotation == 3) {
    				    		torp.torpRotation = 4;
    				    	}
    			    	}
    				    	if( i == 15 )
    						{
    							
    							if( firingCraft != null )
    							{
    								firingCraft.waitTorpLoading--;
    								if(!checkOuterDoorClosedV() )
    									openMissileDoorsV(p);
    							}else
    							{
    								if(!checkOuterDoorClosedV() )
    									openMissileDoorsV(p);
    							}
    							torp.active = true;
    						}
    			    	}

    			    }
    		    	
    			);
    	   }
}
    
    
    public boolean isValidCannon(Block b) {
	direction = null;
	isCannon = false;
	if (b.getType() == Material.DISPENSER) {
		NavyCraft.instance.DebugMessage("Was dispenser", 5);
	if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.DIAMOND_BLOCK )
	{
		direction = BlockFace.NORTH;

	    cannonType = 3;
	}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.DIAMOND_BLOCK)
	{
		direction = BlockFace.SOUTH;
	    cannonType = 3;
	}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.DIAMOND_BLOCK)
	{
		direction = BlockFace.EAST;
	    cannonType = 3;
	}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.DIAMOND_BLOCK)
	{
		direction = BlockFace.WEST;
	    cannonType = 3;
	}else if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.EMERALD_BLOCK )
	{
		direction = BlockFace.NORTH;
	    cannonType = 7;
	}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.EMERALD_BLOCK)
	{
		direction = BlockFace.SOUTH;
	    cannonType = 7;
	}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.EMERALD_BLOCK)
	{
		direction = BlockFace.EAST;
	    cannonType = 7;
	}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.EMERALD_BLOCK)
	{
		direction = BlockFace.WEST;
	    cannonType = 7;
	}else if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.LAPIS_BLOCK )
	{
		direction = BlockFace.NORTH;
	    cannonType = 8;
	}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.LAPIS_BLOCK)
	{
		direction = BlockFace.SOUTH;
	    cannonType = 8;
	}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.LAPIS_BLOCK)
	{
		direction = BlockFace.EAST;
	    cannonType = 8;
	}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.LAPIS_BLOCK)
	{
		direction = BlockFace.WEST;
	    cannonType = 8;
	}else if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.COAL_BLOCK)
	{
		direction = BlockFace.NORTH;
	    cannonType = 11;
	}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.COAL_BLOCK)
	{
		direction = BlockFace.SOUTH;
	    cannonType = 11;
	}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.COAL_BLOCK)
	{
		direction = BlockFace.EAST;
	    cannonType = 11;
	}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.COAL_BLOCK)
	{
		direction = BlockFace.WEST;
	    cannonType = 11;
	}else if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.REDSTONE_BLOCK )
	{
		direction = BlockFace.NORTH;
	    cannonType = 12;
	}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.REDSTONE_BLOCK)
	{
		direction = BlockFace.SOUTH;
	    cannonType = 12;
	}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.REDSTONE_BLOCK)
	{
		direction = BlockFace.EAST;
	    cannonType = 12;
	}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.REDSTONE_BLOCK)
	{
		direction = BlockFace.WEST;
	    cannonType = 12;
	}else if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.GOLD_ORE )
	{
		direction = BlockFace.NORTH;
	    cannonType = 16;
	}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.GOLD_ORE)
	{
		direction = BlockFace.SOUTH;
	    cannonType = 16;
	}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.GOLD_ORE)
	{
		direction = BlockFace.EAST;
	    cannonType = 16;
	}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.GOLD_ORE)
	{
		direction = BlockFace.WEST;
	    cannonType = 16;
	}
	
		if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.PUMPKIN
			&& b.getRelative(BlockFace.NORTH, 2).getType() == Material.PUMPKIN)
		{
		    direction = BlockFace.NORTH;
		    cannonType = 0;
		    if( ammunition == -1 )
		    {
		    	ammunition = 15;
		    	initAmmo = ammunition;
		    }
		    if (b.getRelative(BlockFace.NORTH, 3).getType() == Material.PUMPKIN) {
		    	cannonLength = 3;
		    }  else {
		    	cannonLength = 2;
		    }
		    if( b.getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK )
		    {
		    	cannonType = 6;
		    	if( ammunition == -1 )
		    	{
		    		ammunition = 10;
		    		initAmmo = ammunition;
		    	}
		    }
		}
		if (b.getRelative(BlockFace.EAST, 1).getType() == Material.PUMPKIN
			&& b.getRelative(BlockFace.EAST, 2).getType() == Material.PUMPKIN)
		{
			direction = BlockFace.EAST;
			cannonType = 0;
			if( ammunition == -1 )
			{
				ammunition = 15;
				initAmmo = ammunition;
	    	}
		    if (b.getRelative(BlockFace.EAST, 3).getType() == Material.PUMPKIN)
		    	cannonLength = 3;
		    else
		    	cannonLength = 2;
		    if( b.getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK )
		    {
		    	cannonType = 6;
		    	if( ammunition == -1 )
		    	{
		    		ammunition = 10;
		    		initAmmo = ammunition;
		    	}
		    }
		}
		if (b.getRelative(BlockFace.SOUTH, 1).getType() == Material.PUMPKIN
			&& b.getRelative(BlockFace.SOUTH, 2).getType() == Material.PUMPKIN)
		{
			direction = BlockFace.SOUTH;
			cannonType = 0;
			if( ammunition == -1 )
			{
				ammunition = 15;
				initAmmo = ammunition;
	    	}
		    if (b.getRelative(BlockFace.SOUTH, 3).getType() == Material.PUMPKIN)
		    	cannonLength = 3;
		    else
		    	cannonLength = 2;
		    
		    if( b.getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK )
		    {
		    	if( ammunition == -1 )
		    	{
		    		ammunition = 10;
		    		initAmmo = ammunition;
		    	}
		    	cannonType = 6;
		    }
		}
		if (b.getRelative(BlockFace.WEST, 1).getType() == Material.PUMPKIN
			&& b.getRelative(BlockFace.WEST, 2).getType() == Material.PUMPKIN)
		{
			direction = BlockFace.WEST;
			cannonType = 0;
			if( ammunition == -1 )
			{
				ammunition = 10;
				initAmmo = ammunition;
	    	}
		    if (b.getRelative(BlockFace.WEST, 3).getType() == Material.PUMPKIN)
		    	cannonLength = 3;
		    else
		    	cannonLength = 2;
		    
		    if( b.getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK )
		    {
		    	cannonType = 6;
		    	if( ammunition == -1 )
		    	{
		    		ammunition = 10;
		    		initAmmo = ammunition;
		    	}
		    }
		}
		
		
		
		///cannon type 1 (two barrel)
		if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.GOLD_BLOCK
				&& b.getRelative(BlockFace.SOUTH, 1).getType() == Material.GOLD_BLOCK)
		{
			if (b.getRelative(BlockFace.NORTH_WEST, 1).getType() == Material.PUMPKIN
					&& b.getRelative(BlockFace.SOUTH_WEST, 1).getType() == Material.PUMPKIN)
				direction = BlockFace.WEST;
			else if(b.getRelative(BlockFace.NORTH_EAST, 1).getType() == Material.PUMPKIN
					&& b.getRelative(BlockFace.SOUTH_EAST, 1).getType() == Material.PUMPKIN)
				direction = BlockFace.EAST;
		    cannonType = 1;
		    cannonLength = 2;
		    if( ammunition == -1 )
		    {
		    	ammunition = 10;
		    	initAmmo = ammunition;
	    	}
		}
		if (b.getRelative(BlockFace.EAST, 1).getType() == Material.GOLD_BLOCK
			&& b.getRelative(BlockFace.WEST, 1).getType() == Material.GOLD_BLOCK)
		{
			if (b.getRelative(BlockFace.NORTH_WEST, 1).getType() == Material.PUMPKIN
					&& b.getRelative(BlockFace.NORTH_EAST, 1).getType() == Material.PUMPKIN)
				direction = BlockFace.NORTH;
			else if(b.getRelative(BlockFace.SOUTH_WEST, 1).getType() == Material.PUMPKIN
					&& b.getRelative(BlockFace.SOUTH_EAST, 1).getType() == Material.PUMPKIN)
				direction = BlockFace.SOUTH;
			cannonType = 1;
		    cannonLength = 2;
		    if( ammunition == -1 )
		    {
		    	ammunition = 10;
		    	initAmmo = ammunition;
	    	}
		}
		
		////check if depth charger
		if ( direction == null )
		{
			if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.WOOD && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.EAST;
			    cannonType = 9;
			    if( ammunition == -1 )
			    {
			    	ammunition = 2;
			    	initAmmo = ammunition;
			    }
			}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.WOOD && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.WEST;
			    cannonType = 9;
			    if( ammunition == -1 )
			    {
			    	ammunition = 2;
			    	initAmmo = ammunition;
			    }
			}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.WOOD && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.SOUTH;
			    cannonType = 9;
			    if( ammunition == -1 )
			    {
			    	ammunition = 2;
			    	initAmmo = ammunition;
			    }
			}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.WOOD && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.NORTH;
			    cannonType = 9;
			    if( ammunition == -1 )
			    {
			    	ammunition = 2;
			    	initAmmo = ammunition;
			    }
			}else if(b.getRelative(BlockFace.NORTH, 1).getType() == Material.SANDSTONE && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
				{
					direction = BlockFace.EAST;
				    cannonType = 10;
				    if( ammunition == -1 )
				    {
				    	ammunition = 6;
				    	initAmmo = ammunition;
				    }
			}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.SANDSTONE && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
				{
					direction = BlockFace.WEST;
				    cannonType = 10;
				    if( ammunition == -1 )
				    {
				    	ammunition = 6;
				    	initAmmo = ammunition;
				    }
			}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.SANDSTONE && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
				{
					direction = BlockFace.SOUTH;
				    cannonType = 10;
				    if( ammunition == -1 )
				    {
				    	ammunition = 6;
				    	initAmmo = ammunition;
				    }
			}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.SANDSTONE && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
				{
					direction = BlockFace.NORTH;
				    cannonType = 10;
				    if( ammunition == -1 )
				    {
				    	ammunition = 6;
				    	initAmmo = ammunition;
				    }
			}else if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.EAST;
			    cannonType = 4;
			    if( ammunition == -1 )
			    {
			    	ammunition = 10;
			    	initAmmo = ammunition;
			    }
			}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.WEST;
			    cannonType = 4;
			    if( ammunition == -1 )
			    {
			    	ammunition = 10;
			    	initAmmo = ammunition;
			    }
			}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.SOUTH;
			    cannonType = 4;
			    if( ammunition == -1 )
			    {
			    	ammunition = 10;
			    	initAmmo = ammunition;
			    }
			}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.NORTH;
			    cannonType = 4;
			    if( ammunition == -1 )
			    {
			    	ammunition = 10;
			    	initAmmo = ammunition;
			    }
			}else if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.GOLD_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.EAST;
			    cannonType = 5;
			    if( ammunition == -1 )
			    {
			    	ammunition = 15;
			    	initAmmo = ammunition;
			    }
			}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.GOLD_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.WEST;
			    cannonType = 5;
			    if( ammunition == -1 )
			    {
			    	ammunition = 15;
			    	initAmmo = ammunition;
			    }
			}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.GOLD_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.SOUTH;
			    cannonType = 5;
			    if( ammunition == -1 )
			    {
			    	ammunition = 15;
			    	initAmmo = ammunition;
			    }
			}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.GOLD_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.LAPIS_BLOCK)
			{
				direction = BlockFace.NORTH;
			    cannonType = 5;
			    if( ammunition == -1 )
			    {
			    	ammunition = 15;
			    	initAmmo = ammunition;
			    }
		}else if(b.getRelative(BlockFace.NORTH, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.REDSTONE_BLOCK)
			{
				direction = BlockFace.EAST;
			    cannonType = 15;
			    if( ammunition == -1 )
			    {
			    	ammunition = 10;
			    	initAmmo = ammunition;
			    }
		}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.REDSTONE_BLOCK)
			{
				direction = BlockFace.WEST;
			    cannonType = 15;
			    if( ammunition == -1 )
			    {
			    	ammunition = 10;
			    	initAmmo = ammunition;
			    }
		}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.REDSTONE_BLOCK)
			{
				direction = BlockFace.SOUTH;
			    cannonType = 15;
			    if( ammunition == -1 )
			    {
			    	ammunition = 10;
			    	initAmmo = ammunition;
			    }
		}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.REDSTONE_BLOCK)
			{
				direction = BlockFace.NORTH;
			    cannonType = 15;
			    if( ammunition == -1 )
			    {
			    	ammunition = 10;
			    	initAmmo = ammunition;
			    }
			} else if(b.getRelative(BlockFace.NORTH, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.STRUCTURE_BLOCK)
		{
			direction = BlockFace.EAST;
			cannonType = 20;
			if( ammunition == -1 )
			{
				ammunition = 1;
				initAmmo = ammunition;
			}
		}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.STRUCTURE_BLOCK)
		{
			direction = BlockFace.WEST;
			cannonType = 20;
			if( ammunition == -1 )
			{
				ammunition = 1;
				initAmmo = ammunition;
			}
		}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.STRUCTURE_BLOCK)
		{
			direction = BlockFace.SOUTH;
			cannonType = 20;
			if( ammunition == -1 )
			{
				ammunition = 1;
				initAmmo = ammunition;
			}
		}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.IRON_BLOCK && b.getRelative(BlockFace.DOWN, 1).getType() == Material.STRUCTURE_BLOCK)
		{
			direction = BlockFace.NORTH;
			cannonType = 20;
			if( ammunition == -1 )
			{
				ammunition = 1;
				initAmmo = ammunition;
			}
		}
		}
	} else {
		NavyCraft.instance.DebugMessage("Was dropper", 5);
	if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.COAL_BLOCK)
	{
		direction = BlockFace.NORTH;
	    cannonType = 13;
	}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.COAL_BLOCK)
	{
		direction = BlockFace.SOUTH;
	    cannonType = 13;
	}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.COAL_BLOCK)
	{
		direction = BlockFace.EAST;
	    cannonType = 13;
	}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.COAL_BLOCK)
	{
		direction = BlockFace.WEST;
	    cannonType = 13;
	}else if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.REDSTONE_BLOCK )
	{
		direction = BlockFace.NORTH;
	    cannonType = 14;
	}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.REDSTONE_BLOCK)
	{
		direction = BlockFace.SOUTH;
	    cannonType = 14;
	}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.REDSTONE_BLOCK)
	{
		direction = BlockFace.EAST;
	    cannonType = 14;
	}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.REDSTONE_BLOCK)
	{
		direction = BlockFace.WEST;
	    cannonType = 17;
	}else if (b.getRelative(BlockFace.NORTH, 1).getType() == Material.GOLD_ORE )
	{
		direction = BlockFace.NORTH;
	    cannonType = 17;
	}else if(b.getRelative(BlockFace.SOUTH, 1).getType() == Material.GOLD_ORE)
	{
		direction = BlockFace.SOUTH;
	    cannonType = 17;
	}else if(b.getRelative(BlockFace.EAST, 1).getType() == Material.GOLD_ORE)
	{
		direction = BlockFace.EAST;
	    cannonType = 17;
	}else if(b.getRelative(BlockFace.WEST, 1).getType() == Material.GOLD_ORE)
	{
		direction = BlockFace.WEST;
	    cannonType = 17;
	}
}

	
	if (direction != null) {
	    isCannon = true;
	    NavyCraft.instance.DebugMessage("It is a valid Cannon", 3);
	    return true;
	} else {
	    NavyCraft.instance.DebugMessage("No Cannon", 3);
	    return false;
	}
    }

    public int[][] rotateRight(int[][] arr) {
	int[][] result = new int[arr.length][arr.length];
	for (int x = 0; x < arr.length; x++) {
	    for (int y = 0; y < arr.length; y++) {
		result[x][y] = arr[arr.length - 1 - y][x];
	    }
	}
	return result;
    }

    public int[][] rotateLeft(int[][] arr) {
	int[][] result = new int[arr.length][arr.length];
	for (int x = 0; x < arr.length; x++) {
	    for (int y = 0; y < arr.length; y++) {
		result[x][y] = arr[y][arr.length - 1 - x];
	    }
	}
	return result;
    }

    public byte[][] rotateRightB(byte[][] arr, int[][] arro) {
	byte[][] result = new byte[arr.length][arr.length];
	byte[] cardinals;
	int blockId;
	for (int x = 0; x < arr.length; x++) {
	    for (int y = 0; y < arr.length; y++) {
		result[x][y] = arr[arr.length - 1 - y][x];
		blockId = arro[x][y];
		int dr = 270;
		
			//Block theBlock = craft.getWorldBlock(dataBlock.x, dataBlock.y, dataBlock.z);
			
			//logs
			if( blockId == 17 && result[x][y] > 3 )
			{
				if( result[x][y] < 8 )
					result[x][y] += 4;
				else
					result[x][y] -= 4;
			}
			
			//quartz block
			if( blockId == 155 && result[x][y] > 2 )
			{
				if( result[x][y] == 3)
					result[x][y] = 4;
				else
					result[x][y] = 3;
			}
			
			//hay bales
			if( blockId == 170 && result[x][y] > 3 )
			{
				if( result[x][y] < 8 )
					result[x][y] += 4;
				else
					result[x][y] -= 4;
			}
			
			//torches, skip 'em if they're centered on the tile on the ground
			if(blockId == 50 || blockId == 75 || blockId == 76) {
				if(result[x][y] == 5)
					continue;
			}
			
			if( blockId == 33 || blockId == 29 || blockId == 34 )
			{
				if( result[x][y] == 0 || result[x][y] == 1 || result[x][y] == 8 || result[x][y] == 9 )
				{
					if( result[x][y] == 0 )
						result[x][y] = 1;
					if( result[x][y] == 8 )
						result[x][y] = 9;
					continue;
				}
			}

			if(BlocksInfo.getCardinals(blockId) != null)
				cardinals = Arrays.copyOf(BlocksInfo.getCardinals(blockId), 4);
			else
				cardinals = null;

			
			////stairs
			if( blockId == 53 || blockId == 67 || blockId == 108 || blockId == 109 || blockId == 114 || blockId == 128 || blockId == 134 || blockId == 135 || blockId == 136 || blockId == 156 || blockId == 180 ) 
			{	
				if(result[x][y] > 3) 
					{	//upside down
						for(int c = 0; c < 4; c++) {
							cardinals[c] += 4;
					}
				}
			}

			if(blockId == 26) {	//bed
				if(result[x][y] >= 8) {
					for(int c = 0; c < 4; c++)
						cardinals[c] += 8;
				}
			}

			if(blockId == 64 || blockId == 71 || blockId == 193 || blockId == 194 || blockId == 195 ||	blockId == 196 || blockId == 197//wooden or steel door
					|| blockId == 93 || blockId == 94) {	//repeater

				if(result[x][y] >= 12) {	//if the door is an open top
					for(int c = 0; c < 4; c++)
						cardinals[c] += 12;
				} else if (result[x][y] >= 8) {		//if the door is a top
					for(int c = 0; c < 4; c++)
						cardinals[c] += 8;
				} else if (result[x][y] >= 4) {		//not a top, but open
					for(int c = 0; c < 4; c++)
						cardinals[c] += 4;
				}
			}

			if (blockId == 66 ) { // rails
				if(result[x][y] == 0) {
					result[x][y] = 1;
					continue;
				}
				if(result[x][y] == 1) {
					result[x][y] = 0;
					continue;
				}
			}

			if(blockId == 69) {	//lever

				if(result[x][y] == 5 || result[x][y] == 6 ||	//if it's on the floor
						result[x][y] == 13 || result[x][y] == 14) {
					cardinals = new byte[]{6, 5, 14, 13};
				}
				else if(result[x][y] > 4) {	//switched on
					for(int c = 0; c < 4; c++) {
						cardinals[c] += 8;
					}
				}
			}
			
			if(blockId == 77 || blockId == 143) {	//button

				if(result[x][y] > 4) 
					{	//switched on
						for(int c = 0; c < 4; c++) {
							cardinals[c] += 8;
					}
				}
			}
			
			if(blockId == 96||blockId == 167) {	//hatch

				if(result[x][y] > 4) 
					{	//switched on
						for(int c = 0; c < 4; c++) {
							cardinals[c] += 4;
					}
				}
			}

			if(blockId == 93 || blockId == 94) {	//repeater
				if(result[x][y] > 11) {
					for(int c = 0; c < 4; c++)
						cardinals[c] += 12;
				}
				else if(result[x][y] > 7) {
					for(int c = 0; c < 4; c++)
						cardinals[c] += 8;
				}
				else if(result[x][y] > 3) {
					for(int c = 0; c < 4; c++)
						cardinals[c] += 4;
				}
			}

			if(cardinals != null) {
				NavyCraft.instance.DebugMessage(Material.getMaterial(blockId) +
						" Cardinals are "
						+ cardinals[0] + ", "
						+ cardinals[1] + ", "
						+ cardinals[2] + ", "
						+ cardinals[3], 2);

				int i = 0;
				for(i = 0; i < 3; i++)
					if(result[x][y] == cardinals[i])
						break;

				NavyCraft.instance.DebugMessage("i starts as " + i + " which is " + cardinals[i], 2);

				i += (dr / 90);

				if(i > 3)
					i = i - 4;

				NavyCraft.instance.DebugMessage("i ends as " + i + ", which is " + cardinals[i], 2);

				result[x][y] = cardinals[i];
			}
		}
	}
	return result;
    }

    public byte[][] rotateLeftB(byte[][] arr, int[][] arro) {
    	byte[][] result = new byte[arr.length][arr.length];
    	byte[] cardinals;
    	int blockId;
    	for (int x = 0; x < arr.length; x++) {
    	    for (int y = 0; y < arr.length; y++) {
    		result[x][y] = arr[y][arr.length - 1 - x];
    		blockId = arro[x][y];
    		int dr = 90;
    		
    			//Block theBlock = craft.getWorldBlock(dataBlock.x, dataBlock.y, dataBlock.z);
    			
    			//logs
    			if( blockId == 17 && result[x][y] > 3 )
    			{
    				if( result[x][y] < 8 )
    					result[x][y] += 4;
    				else
    					result[x][y] -= 4;
    			}
    			
    			//quartz block
    			if( blockId == 155 && result[x][y] > 2 )
    			{
    				if( result[x][y] == 3)
    					result[x][y] = 4;
    				else
    					result[x][y] = 3;
    			}
    			
    			//hay bales
    			if( blockId == 170 && result[x][y] > 3 )
    			{
    				if( result[x][y] < 8 )
    					result[x][y] += 4;
    				else
    					result[x][y] -= 4;
    			}
    			
    			//torches, skip 'em if they're centered on the tile on the ground
    			if(blockId == 50 || blockId == 75 || blockId == 76) {
    				if(result[x][y] == 5)
    					continue;
    			}
    			
    			if( blockId == 33 || blockId == 29 || blockId == 34 )
    			{
    				if( result[x][y] == 0 || result[x][y] == 1 || result[x][y] == 8 || result[x][y] == 9 )
    				{
    					if( result[x][y] == 0 )
    						result[x][y] = 1;
    					if( result[x][y] == 8 )
    						result[x][y] = 9;
    					continue;
    				}
    			}

    			if(BlocksInfo.getCardinals(blockId) != null)
    				cardinals = Arrays.copyOf(BlocksInfo.getCardinals(blockId), 4);
    			else
    				cardinals = null;

    			
    			////stairs
    			if( blockId == 53 || blockId == 67 || blockId == 108 || blockId == 109 || blockId == 114 || blockId == 128 || blockId == 134 || blockId == 135 || blockId == 136 || blockId == 156 || blockId == 180 ) 
    			{	
    				if(result[x][y] > 3) 
    					{	//upside down
    						for(int c = 0; c < 4; c++) {
    							cardinals[c] += 4;
    					}
    				}
    			}

    			if(blockId == 26) {	//bed
    				if(result[x][y] >= 8) {
    					for(int c = 0; c < 4; c++)
    						cardinals[c] += 8;
    				}
    			}

    			if(blockId == 64 || blockId == 71 || blockId == 193 || blockId == 194 || blockId == 195 ||	blockId == 196 || blockId == 197//wooden or steel door
    					|| blockId == 93 || blockId == 94) {	//repeater

    				if(result[x][y] >= 12) {	//if the door is an open top
    					for(int c = 0; c < 4; c++)
    						cardinals[c] += 12;
    				} else if (result[x][y] >= 8) {		//if the door is a top
    					for(int c = 0; c < 4; c++)
    						cardinals[c] += 8;
    				} else if (result[x][y] >= 4) {		//not a top, but open
    					for(int c = 0; c < 4; c++)
    						cardinals[c] += 4;
    				}
    			}

    			if (blockId == 66 ) { // rails
    				if(result[x][y] == 0) {
    					result[x][y] = 1;
    					continue;
    				}
    				if(result[x][y] == 1) {
    					result[x][y] = 0;
    					continue;
    				}
    			}

    			if(blockId == 69) {	//lever

    				if(result[x][y] == 5 || result[x][y] == 6 ||	//if it's on the floor
    						result[x][y] == 13 || result[x][y] == 14) {
    					cardinals = new byte[]{6, 5, 14, 13};
    				}
    				else if(result[x][y] > 4) {	//switched on
    					for(int c = 0; c < 4; c++) {
    						cardinals[c] += 8;
    					}
    				}
    			}
    			
    			if(blockId == 77 || blockId == 143) {	//button

    				if(result[x][y] > 4) 
    					{	//switched on
    						for(int c = 0; c < 4; c++) {
    							cardinals[c] += 8;
    					}
    				}
    			}
    			
    			if(blockId == 96||blockId == 167) {	//hatch

    				if(result[x][y] > 4) 
    					{	//switched on
    						for(int c = 0; c < 4; c++) {
    							cardinals[c] += 4;
    					}
    				}
    			}

    			if(blockId == 93 || blockId == 94) {	//repeater
    				if(result[x][y] > 11) {
    					for(int c = 0; c < 4; c++)
    						cardinals[c] += 12;
    				}
    				else if(result[x][y] > 7) {
    					for(int c = 0; c < 4; c++)
    						cardinals[c] += 8;
    				}
    				else if(result[x][y] > 3) {
    					for(int c = 0; c < 4; c++)
    						cardinals[c] += 4;
    				}
    			}

    			if(cardinals != null) {
    				NavyCraft.instance.DebugMessage(Material.getMaterial(blockId) +
    						" Cardinals are "
    						+ cardinals[0] + ", "
    						+ cardinals[1] + ", "
    						+ cardinals[2] + ", "
    						+ cardinals[3], 2);

    				int i = 0;
    				for(i = 0; i < 3; i++)
    					if(result[x][y] == cardinals[i])
    						break;

    				NavyCraft.instance.DebugMessage("i starts as " + i + " which is " + cardinals[i], 2);

    				i += (dr / 90);

    				if(i > 3)
    					i = i - 4;

    				NavyCraft.instance.DebugMessage("i ends as " + i + ", which is " + cardinals[i], 2);

    				result[x][y] = cardinals[i];
    			}
    		}
    	}
    	return result;
    }

    // 0x1: Facing south
    // 0x2: Facing north
    // 0x3: Facing west
    // 0x4: Facing east
    public void turnCannon(Boolean right, Player p)
    {
    	if( cannonType == 6 )
    	{
    		turnCannonLayer(right, p, -1);
    		turnCannonLayer(right, p, 1);
    		turnCannonLayer(right, p, 2);
    	}
    	if ( cannonType == 3 || cannonType == 7 || cannonType == 8 || cannonType == 11 || cannonType == 12 || cannonType == 13 ) {
    		turnTorpedoLayer(right, p, 1);
        	turnTorpedoLayer(right, p, 0);
    	} else {
    	turnCannonLayer(right, p, 0);
    	}
    }
    
    
	public void turnCannonLayer(Boolean right, Player p, int offsetY) {
		// Get data
		if (p == null || (p != null && Utils.CheckEnabledWorld(p.getLocation()))) {
		int[][] arr = new int[7][7];
		byte[][] arrb = new byte[7][7];
		for (int x = 0; x < 7; x++) {
		    for (int z = 0; z < 7; z++) {
			arr[x][z] = loc.getBlock().getRelative(x - 3, 0, z - 3).getRelative(BlockFace.UP, offsetY).getTypeId();
			arrb[x][z] = loc.getBlock().getRelative(x - 3, 0, z - 3).getRelative(BlockFace.UP, offsetY).getData();
		    }
		}
	
		int[][] arro = new int[7][7];
		byte[][] arrbo = new byte[7][7];
		// Rotate
		if (right) {
		    arro = rotateLeft(arr);
		    arrbo = rotateLeftB(arrb,arro);
		} else {
		    arro = rotateRight(arr);
		    arrbo = rotateRightB(arrb,arro);
		}
	
		// Cleanup Cannon (support blocks first)
		for (int x = 0; x < 7; x++) {
		    for (int z = 0; z < 7; z++) {
			if (BlocksInfo.needsSupport(loc.getBlock().getRelative(x - 3, 0, z - 3).getRelative(BlockFace.UP, offsetY).getTypeId())) {
			    loc.getBlock().getRelative(x - 3, 0, z - 3).getRelative(BlockFace.UP, offsetY).setTypeIdAndData(0, (byte) 0, false);
				}
		    }
		}
	
		// Cleanup Rest
		for (int x = 0; x < 7; x++) {
		    for (int z = 0; z < 7; z++) {
		    	if( !(x-3==0 && z-3==0 && offsetY==0) )
		    		loc.getBlock().getRelative(x - 3, 0, z - 3).getRelative(BlockFace.UP, offsetY).setTypeIdAndData(0, (byte) 0, false);
		    }
		}
		
		
		Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		// Place cannon
		for (int x = 0; x < 7; x++) {
		    for (int z = 0; z < 7; z++) {
			if ((arro[x][z] != 69) && (arro[x][z] != 77) && (arro[x][z] != 23)) 
			{
				if(arro[x][z] != -1 && !BlocksInfo.needsSupport(arro[x][z]) && arro[x][z] != 52 && arro[x][z] != 34 && arro[x][z] != 36 )
					loc.getBlock().getRelative(x - 3, 0, z - 3).getRelative(BlockFace.UP, offsetY).setTypeIdAndData(arro[x][z], arrbo[x][z], false);
			    if( testCraft != null )
			    {
			    	testCraft.addBlock(loc.getBlock().getRelative(x - 3, 0, z - 3).getRelative(BlockFace.UP, offsetY), true);
			    }
				
			}else if( (arro[x][z] == 23) )
			{
				loc.getBlock().getRelative(x - 3, 0, z - 3).getRelative(BlockFace.UP, offsetY).setData(arrbo[x][z]);
		    }
		    }
		}
	
		// Place rest
		for (int x = 0; x < 7; x++) {
		    for (int z = 0; z < 7; z++) {
				if (BlocksInfo.needsSupport(arro[x][z]) && arro[x][z] != 63 && arro[x][z] != 68 && arro[x][z] != 65) {
			    loc.getBlock().getRelative(x - 3, 0, z - 3).getRelative(BlockFace.UP, offsetY).setTypeIdAndData(arro[x][z], arrbo[x][z], false);
			    if( testCraft != null )
			    {
			    	testCraft.addBlock(loc.getBlock().getRelative(x - 3, 0, z - 3).getRelative(BlockFace.UP, offsetY), true);
			    }
			}
		    }
		}
	
		if( testCraft != null )
		{
			//CraftMover cm = new CraftMover(testCraft, plugin);
			//cm.structureUpdate(null);
		}
		// 0x2: Facing east
		// 0x3: Facing west
		// 0x4: Facing north
		// 0x5: Facing south
		
		if( offsetY == 0 ) {
			if (right) {
				if (direction == BlockFace.NORTH) {
					direction = BlockFace.EAST;
					loc.getBlock().setData((byte) 0x3);
				} else if (direction == BlockFace.EAST) {
					direction = BlockFace.SOUTH;
					loc.getBlock().setData((byte) 0x4);
				} else if (direction == BlockFace.SOUTH) {
					direction = BlockFace.WEST;
					loc.getBlock().setData((byte) 0x2);
				} else// if (direction == BlockFace.WEST)
				{
					direction = BlockFace.NORTH;
					loc.getBlock().setData((byte) 0x5);
				}
			} else {
				if (direction == BlockFace.EAST) {
					direction = BlockFace.NORTH;
					loc.getBlock().setData((byte) 0x5);
				} else if (direction == BlockFace.SOUTH) {
					direction = BlockFace.EAST;
					loc.getBlock().setData((byte) 0x3);
				} else if (direction == BlockFace.WEST) {
					direction = BlockFace.SOUTH;
					loc.getBlock().setData((byte) 0x4);
				} else// if (direction == BlockFace.NORTH)
				{
					direction = BlockFace.WEST;
					loc.getBlock().setData((byte) 0x2);
				}
			}

			if (p != null) {
				Location teleLoc = new Location(p.getWorld(), loc.getBlock().getRelative(direction, -1).getX() + 0.5, (double) loc.getBlock().getRelative(direction, -1).getY(), loc.getBlock().getRelative(direction, -1).getZ() + 0.5);
				//p.sendMessage("player yaw=" + p.getLocation().getYaw() );
				if (right)
					teleLoc.setYaw(p.getLocation().getYaw() + 90);
				else
					teleLoc.setYaw(p.getLocation().getYaw() - 90);
				teleLoc.setPitch(p.getLocation().getPitch());
				p.teleport(teleLoc);
			}
			
			if( cannonTurnCounter < 4 && ((loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,1).getTypeId() == 5)
					|| ( cannonType == 6 && loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,2).getTypeId() == 5)))
			{
				cannonTurnCounter++;
				turnCannon(right, p);
			}else
			{
				cannonTurnCounter=0;
			}
			
		}
    }
}
    
	public void turnTorpedoLayer(Boolean right, Player p, int offsetY) {
		// Get data
		if (Utils.CheckEnabledWorld(p.getLocation())) {
		int[][] arr = new int[12][12];
		byte[][] arrb = new byte[12][12];
		for (int x = 0; x < 12; x++) {
		    for (int z = 0; z < 12; z++) {
			arr[x][z] = loc.getBlock().getRelative(x - 5, 0, z - 5).getRelative(BlockFace.UP, offsetY).getTypeId();
			arrb[x][z] = loc.getBlock().getRelative(x - 5, 0, z - 5).getRelative(BlockFace.UP, offsetY).getData();
		    }
		}
	
		int[][] arro = new int[12][12];
		byte[][] arrbo = new byte[12][12];
		// Rotate
		if (right) {
		    arro = rotateLeft(arr);
		    arrbo = rotateLeftB(arrb,arro);
		} else {
		    arro = rotateRight(arr);
		    arrbo = rotateRightB(arrb,arro);
		}
	
		// Cleanup Cannon (support blocks first)
		for (int x = 0; x < 12; x++) {
		    for (int z = 0; z < 12; z++) {
			if (BlocksInfo.needsSupport(loc.getBlock().getRelative(x - 5, 0, z - 5).getRelative(BlockFace.UP, offsetY).getTypeId())) {
			    loc.getBlock().getRelative(x - 5, 0, z - 5).getRelative(BlockFace.UP, offsetY).setTypeIdAndData(0, (byte) 0, false);
				}
		    }
		}
	
		// Cleanup Rest
		for (int x = 0; x < 12; x++) {
		    for (int z = 0; z < 12; z++) {
		    	if( !(x-5==0 && z-5==0 && offsetY==0) )
		    		loc.getBlock().getRelative(x - 5, 0, z - 5).getRelative(BlockFace.UP, offsetY).setTypeIdAndData(0, (byte) 0, false);
		    }
		}
		
		
		Craft testCraft = Craft.getCraft(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
		// Place cannon
		for (int x = 0; x < 12; x++) {
		    for (int z = 0; z < 12; z++) {
			if (!BlocksInfo.needsSupport(arro[x][z])) 
			{
				if(arro[x][z] != -1 && arro[x][z] != 52 && arro[x][z] != 34 && arro[x][z] != 36 )
					loc.getBlock().getRelative(x - 4, 0, z - 4).getRelative(BlockFace.UP, offsetY).setTypeIdAndData(arro[x][z], arrbo[x][z], false);
			    if( testCraft != null )
			    {
			    	testCraft.addBlock(loc.getBlock().getRelative(x - 5, 0, z - 5).getRelative(BlockFace.UP, offsetY), true);
			    }
				
			}else if( (arro[x][z] == 23) )
			{
				loc.getBlock().getRelative(x - 5, 0, z - 5).getRelative(BlockFace.UP, offsetY).setData(arrbo[x][z]);
		    }
		    }
		}
	
		// Place rest
		for (int x = 0; x < 12; x++) {
		    for (int z = 0; z < 12; z++) {
				if (BlocksInfo.needsSupport(arro[x][z]) && arro[x][z] != 63 && arro[x][z] != 68 && arro[x][z] != 65) {
			    loc.getBlock().getRelative(x - 4, 0, z - 4).getRelative(BlockFace.UP, offsetY).setTypeIdAndData(arro[x][z], arrbo[x][z], false);
			    if( testCraft != null )
			    {
			    	testCraft.addBlock(loc.getBlock().getRelative(x - 5, 0, z - 5).getRelative(BlockFace.UP, offsetY), true);
			    }
			}
		    }
		}
	
		if( testCraft != null )
		{
			//CraftMover cm = new CraftMover(testCraft, plugin);
			//cm.structureUpdate(null);
		}
		// 0x2: Facing east
		// 0x3: Facing west
		// 0x4: Facing north
		// 0x5: Facing south
		
		if( offsetY == 0 )
		{
			if (right) {
		    	if (direction == BlockFace.NORTH) 
		    	{
		    	    direction = BlockFace.EAST;
		    	    loc.getBlock().setData((byte) 0x3);
		    	}else if (direction == BlockFace.EAST) 
		    	{
		    	    direction = BlockFace.SOUTH;
		    	    loc.getBlock().setData((byte) 0x4);	    	 
		    	}else if (direction == BlockFace.SOUTH) 
		    	{
		    	    direction = BlockFace.WEST;
		    	    loc.getBlock().setData((byte) 0x2);
		    	}else// if (direction == BlockFace.WEST) 
		    	{
		    	    direction = BlockFace.NORTH;
		    	    loc.getBlock().setData((byte) 0x5);
		    	}
			} else
			{
			    if (direction == BlockFace.EAST)
			    {
			    	    direction = BlockFace.NORTH;
			    	    loc.getBlock().setData((byte) 0x5);
			    }else if (direction == BlockFace.SOUTH) 
			    {
			    	    direction = BlockFace.EAST;
			    	    loc.getBlock().setData((byte) 0x3);
			    }else if (direction == BlockFace.WEST) 
			    {
			    	    direction = BlockFace.SOUTH;
			    	    loc.getBlock().setData((byte) 0x4);
			    }else// if (direction == BlockFace.NORTH) 
			    {
			    	    direction = BlockFace.WEST;
			    	    loc.getBlock().setData((byte) 0x2);
			    }
			}
			
			Location teleLoc = new Location(p.getWorld(), loc.getBlock().getRelative(direction, -1).getX() + 0.5, (double)loc.getBlock().getRelative(direction, -1).getY(), loc.getBlock().getRelative(direction, -1).getZ() + 0.5);
			//p.sendMessage("player yaw=" + p.getLocation().getYaw() );
			if( right )
				teleLoc.setYaw(p.getLocation().getYaw() + 90);
			else
				teleLoc.setYaw(p.getLocation().getYaw() - 90);
			teleLoc.setPitch(p.getLocation().getPitch());
			p.teleport(teleLoc);
			
			
			if( cannonTurnCounter < 4 && ((loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,1).getTypeId() == 5)
					|| ( cannonType == 8 && loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,2).getTypeId() == 5)))
			if( cannonTurnCounter < 4 && ((loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,1).getTypeId() == 5)
					|| ( cannonType == 7 && loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,2).getTypeId() == 5)))
			if( cannonTurnCounter < 4 && ((loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,1).getTypeId() == 5)
					|| ( cannonType == 11 && loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,2).getTypeId() == 5)))
			if( cannonTurnCounter < 4 && ((loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,1).getTypeId() == 5)
					|| ( cannonType == 12 && loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,2).getTypeId() == 5)))
			if( cannonTurnCounter < 4 && ((loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,1).getTypeId() == 5)
					|| ( cannonType == 3 && loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,2).getTypeId() == 5)))
			if( cannonTurnCounter < 4 && ((loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,1).getTypeId() == 5)
					|| ( cannonType == 13 && loc.getBlock().getRelative(direction, 1).getRelative(BlockFace.DOWN,2).getTypeId() == 5)))
			{
				cannonTurnCounter++;
				turnCannon(right, p);
			}else
			{
				cannonTurnCounter=0;
			}
			
		}
    }
}
	
    public boolean checkProtectedRegion(Player player, Location loc)
    {
    	if( wgp != null )
    	{
    		if( !Utils.CheckEnabledWorld(loc) )
    		{
    			return true;
    		}
	    	RegionManager regionManager = wgp.getRegionManager(loc.getWorld());
		
			ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
			
			Iterator<ProtectedRegion> it = set.iterator();
			while( it.hasNext() )
			{
				String id = it.next().getId();
		
				String[] splits = id.split("_");
				if( splits.length == 2 )
				{
					if( splits[1].equalsIgnoreCase("safedock") || splits[1].equalsIgnoreCase("red") || splits[1].equalsIgnoreCase("blue") )
					{
						return true;
					}
				}
		    }
			return false;
		}
    	return false;
	}
    
    
	public void fireDCButton(Player p, boolean leftClick)
    {
    	if( checkProtectedRegion(p, p.getLocation()) )
    	{
    		p.sendMessage(ChatColor.RED + "You are in a protected region");
    		return;
    	}
    	
		if( leftClick )
		{
			if( charged > 0 )
			{
				if( depth == 0 )
					depth = 10;
				if( cannonType == 4 )
				{
					
					loc.getBlock().getRelative(direction,4).setTypeIdAndData(35, (byte) 0x8, false);
					loc.getBlock().getRelative(direction,4).getRelative(BlockFace.DOWN).setTypeIdAndData(35, (byte) 0x8, false);
					fireDC(p, loc.getBlock().getRelative(direction,4), depth, loc.getBlockY(), 0, 2, false);
					p.sendMessage(ChatColor.GREEN + "Depth Charge Away!");
				}else if( cannonType == 9 || cannonType == 10 )
				{
					
					loc.getBlock().getRelative(BlockFace.DOWN,5).setTypeIdAndData(35, (byte) 0x8, false);
					loc.getBlock().getRelative(BlockFace.DOWN,5).getRelative(BlockFace.DOWN).setTypeIdAndData(35, (byte) 0x7, false);
					fireDC(p, loc.getBlock().getRelative(BlockFace.DOWN,5), 0, loc.getBlock().getRelative(BlockFace.DOWN,5).getY(), 0, 2, false);
					p.sendMessage(ChatColor.GREEN + "Bomb Away!");
				}else if( cannonType == 15)
				{
					loc.getBlock().getRelative(BlockFace.DOWN,5).setTypeIdAndData(35, (byte) 0xE, false);
					loc.getBlock().getRelative(BlockFace.DOWN,5).getRelative(BlockFace.DOWN).setTypeIdAndData(35, (byte) 0xE, false);
					fireDC(p, loc.getBlock().getRelative(BlockFace.DOWN,5), 0, loc.getBlock().getRelative(BlockFace.DOWN,5).getY(), 0, 2,true);
					p.sendMessage(ChatColor.GREEN + "Flare Away!");
				}else if( cannonType == 20)
			{
				loc.getBlock().getRelative(BlockFace.DOWN,5).setTypeIdAndData(35, (byte) 0x5, false);
				loc.getBlock().getRelative(BlockFace.DOWN,5).getRelative(BlockFace.DOWN).setTypeIdAndData(35, (byte) 0x5, false);
				fireDC(p, loc.getBlock().getRelative(BlockFace.DOWN,5), 0, loc.getBlock().getRelative(BlockFace.DOWN,5).getY(), 0, 2,false);
				p.sendMessage(ChatColor.GREEN + "Nuke Away!");
			}else
				{
					
					loc.getBlock().getRelative(direction,4).setTypeIdAndData(35, (byte) 0x8, false);
					loc.getBlock().getRelative(direction,4).getRelative(BlockFace.DOWN).setTypeIdAndData(35, (byte) 0x8, false);
					fireDC(p, loc.getBlock().getRelative(direction,4), depth, loc.getBlockY(), 0, 0, false);
					
					if( direction == BlockFace.NORTH || direction == BlockFace.SOUTH )
					{
						loc.getBlock().getRelative(direction,6).getRelative(BlockFace.EAST,10).setTypeIdAndData(35, (byte) 0x8, false);
						loc.getBlock().getRelative(direction,6).getRelative(BlockFace.DOWN).getRelative(BlockFace.EAST,10).setTypeIdAndData(35, (byte) 0x8, false);
						fireDC(p, loc.getBlock().getRelative(direction,6).getRelative(BlockFace.EAST,10), depth, loc.getBlockY(), 1000, 1, false);
						loc.getBlock().getRelative(direction,6).getRelative(BlockFace.WEST,10).setTypeIdAndData(35, (byte) 0x8, false);
						loc.getBlock().getRelative(direction,6).getRelative(BlockFace.DOWN).getRelative(BlockFace.WEST,10).setTypeIdAndData(35, (byte) 0x8, false);
						fireDC(p, loc.getBlock().getRelative(direction,6).getRelative(BlockFace.WEST,10), depth, loc.getBlockY(), 1000, -1, false);
					}else
					{
						loc.getBlock().getRelative(direction,6).getRelative(BlockFace.NORTH,10).setTypeIdAndData(35, (byte) 0x8, false);
						loc.getBlock().getRelative(direction,6).getRelative(BlockFace.DOWN).getRelative(BlockFace.NORTH,10).setTypeIdAndData(35, (byte) 0x8, false);
						fireDC(p, loc.getBlock().getRelative(direction,6).getRelative(BlockFace.NORTH,10), depth, loc.getBlockY(), 1000, 1, false);
						loc.getBlock().getRelative(direction,6).getRelative(BlockFace.SOUTH,10).setTypeIdAndData(35, (byte) 0x8, false);
						loc.getBlock().getRelative(direction,6).getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH,10).setTypeIdAndData(35, (byte) 0x8, false);
						fireDC(p, loc.getBlock().getRelative(direction,6).getRelative(BlockFace.SOUTH,10), depth, loc.getBlockY(), 1000, -1, false);
					}
					p.sendMessage(ChatColor.GREEN + "Depth Charges Away!");
				}
				
				charged = 0;
			}else
			{
				if( cannonType == 4 )
					p.sendMessage(ChatColor.YELLOW + "Load Depth Charge Dropper first.");
				else if( cannonType == 5 )
					p.sendMessage(ChatColor.YELLOW + "Load Depth Charge Launcher first.");
				else
					p.sendMessage(ChatColor.YELLOW + "Load Bomb Dropper first.");
			}
		}else
		{
			depth += 10;
			if( depth > 60 )
				depth = 10;
			if( cannonType == 4 )
				p.sendMessage(ChatColor.GREEN + "Depth Charge Dropper set to " + ChatColor.YELLOW + depth + ChatColor.GREEN + " meters.");
			else if( cannonType == 5 )
				p.sendMessage(ChatColor.GREEN + "Depth Charge Launcher set to " + ChatColor.YELLOW + depth + ChatColor.GREEN + " meters.");
			else if( cannonType == 9 || cannonType == 10)
				p.sendMessage(ChatColor.GREEN + "Left click to drop bomb.");
		}
    }
    
    public void fireDC(final Player p, final Block b, final int dcDepth, final int startY, final int delayShoot, final int direction, boolean isFlare){
    	//final int taskNum;                                                      ////direction= -1 left, 0 center, 1 right, 2 single
    	//int taskNum = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
    	final Weapon dc;
    	Craft c = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    	if (isFlare) {
    		int dcDepth2 = 60;
    		dc = new Weapon(b, direction, dcDepth2, c);
    	} else if (cannonType == 20) {
			dc = new Weapon(b, direction, c, this);
		} else dc = new Weapon(b, direction, dcDepth);
    	
    	AimCannon.weapons.add(dc);
    	
    	new Thread(){
			
    	@Override
			public void run() {
    		
    		setPriority(Thread.MIN_PRIORITY);
				//taskNum = -1;
    			try{
    				sleep(delayShoot);
    				
    				int freeFallHt=0;
    				if( startY > 63 )
    					freeFallHt = startY - 63;
    				
    				for( int i=0; i <= dc.setDepth+freeFallHt; i++ )
    				{
						fireDCUpdate(p, dc, freeFallHt, i);
					if (!isFlare) {
						if( i<freeFallHt )
							sleep(60);
						else
							sleep(80);
					} else {
						sleep(500);
    				}
    			}
    				stopFall0 = false;
    				stopFall1 = false;
    				stopFallM1 = false;
    				stopFall2 = false;
    				dc.destroyWeapon();
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
    	}.start(); //, 20L);
    }
    
    
    public void fireDCUpdate(final Player p, final Weapon dc, final int freeFallHt, final int i) {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable() {
			//new Thread() {
			//  @Override

			public void run() {
				if (stopFall1 && dc.dcDirection == 1)
					return;
				if (stopFallM1 && dc.dcDirection == -1)
					return;
				if (stopFall0 && dc.dcDirection == 0)
					return;
				if (stopFall2 && dc.dcDirection == 2)
					return;


				//currentBlock = warhead.getRelative(BlockFace.DOWN, i);

				if (dc.warhead.getTypeId() != 35) {
					return;
				}

				if ((dc.weaponType == 3 && i == 30) || i >= dc.setDepth + freeFallHt || (dc.warhead.getRelative(BlockFace.DOWN, 2).getTypeId() != 8 && dc.warhead.getRelative(BlockFace.DOWN, 2).getTypeId() != 9 && dc.warhead.getRelative(BlockFace.DOWN, 2).getTypeId() != 0 && dc.warhead.getRelative(BlockFace.DOWN, 2).getTypeId() != 79)) {

					if (checkProtectedRegion(p, dc.warhead.getLocation())) {
						p.sendMessage(ChatColor.RED + "No Depth Charge explosions in dock area.");
						if (dc.warhead.getY() >= 63) {
							dc.warhead.setTypeId(0);
							dc.warhead.getRelative(BlockFace.DOWN).setTypeId(0);
						} else {
							dc.warhead.setTypeId(8);
							dc.warhead.getRelative(BlockFace.DOWN).setTypeId(8);
						}
						if (dc.dcDirection == 1)
							stopFall1 = true;
						if (dc.dcDirection == -1)
							stopFallM1 = true;
						if (dc.dcDirection == 0)
							stopFall0 = true;
						if (dc.dcDirection == 2)
							stopFall2 = true;
						return;
					}

					if (dc.weaponType == 4) {
						NavyCraft.explosion(35, dc.warhead, false);
						NavyCraft.explosion(35, dc.warhead.getRelative(BlockFace.EAST, 10), false);
						NavyCraft.explosion(35, dc.warhead.getRelative(BlockFace.WEST, 10), false);
						NavyCraft.explosion(35, dc.warhead.getRelative(BlockFace.NORTH, 10), false);
						NavyCraft.explosion(35, dc.warhead.getRelative(BlockFace.SOUTH, 10), false);
						NavyCraft.explosion(35, dc.warhead.getRelative(BlockFace.DOWN, 10), false);
					} else if (dc.weaponType != 3) {
						NavyCraft.explosion(16, dc.warhead, false);
					} else {
						Craft firingCraft = dc.firingCraft;
						if (firingCraft != null) firingCraft.activeFlares.remove(dc);
						dc.destroyWeapon();
					}

					if (dc.warhead.getY() >= 63) {
						dc.warhead.setTypeId(0);
						dc.warhead.getRelative(BlockFace.DOWN).setTypeId(0);
					} else {
						dc.warhead.setTypeId(8);
						dc.warhead.getRelative(BlockFace.DOWN).setTypeId(8);
					}

					Craft checkCraft = null;
					checkCraft = NavyCraft.instance.entityListener.structureUpdate(dc.warhead.getLocation(), p);
					if (checkCraft == null) {
						checkCraft = NavyCraft.instance.entityListener.structureUpdate(dc.warhead.getRelative(4, 4, 4).getLocation(), p);
						if (checkCraft == null) {
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(dc.warhead.getRelative(-4, -4, -4).getLocation(), p);
							if (checkCraft == null) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(dc.warhead.getRelative(2, -2, -2).getLocation(), p);
								if (checkCraft == null) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(dc.warhead.getRelative(-2, 2, 2).getLocation(), p);
								}
							}
						}
					}

					if (checkCraft != null && dc.weaponType != 3)
						p.sendMessage(ChatColor.GREEN + "Depth Charge hit " + ChatColor.YELLOW + checkCraft.name + ChatColor.GREEN + "!");


				} else {

					if (dc.warhead.getY() >= 63)
						dc.warhead.setTypeId(0);
					else
						dc.warhead.setTypeId(8);

					if (dc.setDepth == 0) //if bomb?
					{
						dc.warhead.getRelative(BlockFace.DOWN).setTypeIdAndData(35, (byte) 0x7, false);
						dc.warhead.getRelative(BlockFace.DOWN, 2).setTypeIdAndData(35, (byte) 0x7, false);
					} else {
						dc.warhead.getRelative(BlockFace.DOWN).setTypeIdAndData(35, (byte) 0x8, false);
						dc.warhead.getRelative(BlockFace.DOWN, 2).setTypeIdAndData(35, (byte) 0x8, false);
					}

					if (dc.weaponType == 3) {
						dc.warhead.getRelative(BlockFace.DOWN).setTypeIdAndData(35, (byte) 0xE, false);
						dc.warhead.getRelative(BlockFace.DOWN, 2).setTypeIdAndData(35, (byte) 0xE, false);
					}

					if (dc.weaponType == 4) {
						dc.warhead.getRelative(BlockFace.DOWN).setTypeIdAndData(35, (byte) 0x5, false);
						dc.warhead.getRelative(BlockFace.DOWN, 2).setTypeIdAndData(35, (byte) 0x5, false);
					}
					dc.warhead = dc.warhead.getRelative(BlockFace.DOWN, 1);

				}


			}
		}
	);
    }
    
    public void reload(Player p)
    {
    	ammunition = initAmmo;
    	if (p != null)
    	p.sendMessage(ChatColor.GREEN + "Weapon Systems Reloaded!");
    }
    
    public void shellThread(final int num)
    {
    	//final int taskNum;                                                      ////direction= -1 left, 0 center, 1 right, 2 single
    	//int taskNum = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
		new Thread(){
			
    	@Override
			public void run() {
    		
    		setPriority(Thread.MIN_PRIORITY);
				//taskNum = -1;
    			try{
    				//sleep(200);
    				int i = 0;
    				while((num == 1 && !tntp.isDead())||(num == 2 && !tntp2.isDead())||(num == 3 && !tntp3.isDead()))
    				{
    					sleep(100);
    					shellUpdate(num,i);
    					i++;
    				}

				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
    	}.start(); //, 20L);
    }
    
    public void shellUpdate(final int num, final int i) {
    	nc.getServer().getScheduler().scheduleSyncDelayedTask(nc, new Runnable(){
    	//new Thread() {
	  //  @Override
	    public void run()
	    {
	    	if( i == 0 )
	    	{
	    		if(num == 1)
		    	{
		    		tnt1X = tntp.getVelocity().getX();
			    	tnt1Z = tntp.getVelocity().getZ();
		    	}else if(num == 2)
		    	{
		    		tnt2X = tntp2.getVelocity().getX();
		    		tnt2Z = tntp2.getVelocity().getZ();
		    	}else if(num == 3)
		    	{
			    	tnt3X = tntp3.getVelocity().getX();
			    	tnt3Z = tntp3.getVelocity().getZ();
		    	}
	    	}else
	    	{
		    	if(num == 1 && !tntp.isDead())
		    	{
		    		if( Math.signum(tnt1X) != Math.signum(tntp.getVelocity().getX()) || Math.signum(tnt1Z) != Math.signum(tntp.getVelocity().getZ()) || Math.abs(tntp.getVelocity().getX()) < Math.abs(tnt1X/4) || Math.abs(tntp.getVelocity().getZ()) < Math.abs(tnt1Z/4) )
		    		{
		    			System.out.println("tnt1X=" + tnt1X + " tnt1Z=" + tnt1Z);
		    			System.out.println("tntp.getVelocity().getX()=" + tntp.getVelocity().getX() + " tntp.getVelocity().getZ()=" + tntp.getVelocity().getZ());
		    			tntp.setFuseTicks(1);
		    			
		    		}else
		    		{
		    			tntp.setFuseTicks(1000);
		    		}
		    	}else if(num == 2 && !tntp2.isDead())
		    	{
		    		if( tnt2X < tntp2.getVelocity().getX() - 0.5 || tnt2X > tntp2.getVelocity().getX() + 0.5 || tnt2Z < tntp2.getVelocity().getZ() - 0.5 || tnt2Z > tntp2.getVelocity().getZ() + 0.5 )
		    		{
		    			tntp2.setFuseTicks(1);
		    		}else
		    		{
		    			tntp2.setFuseTicks(1000);
		    		}
		    	}else if(num == 3 && !tntp3.isDead())
		    	{
		    		if( tnt3X < tntp3.getVelocity().getX() - 0.5 || tnt3X > tntp3.getVelocity().getX() + 0.5 || tnt3Z < tntp3.getVelocity().getZ() - 0.5 || tnt3Z > tntp3.getVelocity().getZ() + 0.5 )
		    		{
		    			tntp3.setFuseTicks(1);
		    		}else
		    		{
		    			tntp3.setFuseTicks(1000);
		    		}
		    	}
	    	}

	    }
    	}
    	
	);
    }
	public void setBlock(int id, Block block, Craft craft) {
		// if(y < 0 || y > 127 || id < 0 || id > 255){
		if ((id < 0) || (id > 255)) {
			// + " x=" + x + " y=" + y + " z=" + z);
			System.out.println("Invalid block type ID. Begin panic.");
			return;
		}



		if (block.getTypeId() == id) {
			NavyCraft.instance.DebugMessage("Tried to change a " + id + " to itself.", 5);
			return;
		}

		NavyCraft.instance.DebugMessage("Attempting to set block at " + block.getX() + ", " + block.getY() + ", " + block.getZ() + " to " + id, 5);


		try {
			if (block.setTypeId(id) == false) {
				if (craft.world.getBlockAt(block.getLocation()).setTypeId(id) == false) {
					System.out.println("Could not set block of type " + block.getTypeId() + " to type " + id + ". I tried to fix it, but I couldn't. - ONECANNON");
				} else {
					System.out.println("I hope to whatever God you believe in that this fix worked.");
				}
			}
		} catch (ClassCastException cce) {
			System.out.println("Routine cast exception.");
		}


	}
	public boolean loadTubNum(int tubeNum, Player p) { //try to load desired tube num, return true if found
		Block b = getDirectionFromRelative(loc.getBlock(), direction, true).getRelative(BlockFace.UP,1);
		if( b.getType() == Material.WALL_SIGN ) ///checking left sign
		{
			Sign sign = (Sign) b.getState();
			String signLine0 =  sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
			int tubeSignNum = -999;

			if( signLine0.equalsIgnoreCase("Tube") )
			{
				String tubeString = sign.getLine(1).trim().toLowerCase();
				tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
				if( !tubeString.isEmpty() )
				{
					try{
						tubeSignNum = Integer.parseInt(tubeString);
					}catch (NumberFormatException nfe)
					{

					}
				}

				if( tubeSignNum != -999 )
					if( tubeNum == tubeSignNum ) {
						loadTube(true, p);
						return true;
					}
			}
		}
		b = getDirectionFromRelative(loc.getBlock(), direction, false).getRelative(BlockFace.UP,1); ///checking right sign
			if (b.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) b.getState();
				String signLine0 = sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
				int tubeSignNum = -999;

				if (signLine0.equalsIgnoreCase("Tube")) {
					String tubeString = sign.getLine(1).trim().toLowerCase();
					tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
					if (!tubeString.isEmpty()) {
						try {
							tubeSignNum = Integer.parseInt(tubeString);
						} catch (NumberFormatException nfe) {

						}
					}

					if (tubeSignNum != -999)
						if (tubeNum == tubeSignNum) {
							loadTube(false, p);
							return true;
						}
				}
			}
			b = loc.getBlock().getRelative(direction, -1).getRelative(BlockFace.UP, 1);
			if (b.getType() == Material.WALL_SIGN) ///checking vertical sign
			{
				Sign sign = (Sign) b.getState();
				String signLine0 = sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
				int tubeSignNum = -999;

				if (signLine0.equalsIgnoreCase("Tube")) {
					String tubeString = sign.getLine(1).trim().toLowerCase();
					tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
					if (!tubeString.isEmpty()) {
						try {
							tubeSignNum = Integer.parseInt(tubeString);
						} catch (NumberFormatException nfe) {

						}
					}

					if (tubeSignNum != -999)
						if (tubeNum == tubeSignNum) {
							loadTubeV(p);
							return true;
						}
			}
		}
		return false;
	}
    public boolean fireTubNum(int tubeNum, Player p) { //try to fire desired tube num, return true if found
    	Block b = getDirectionFromRelative(loc.getBlock(), direction, true).getRelative(BlockFace.UP,1);
		System.out.println(b.getLocation());
    	if( b.getType() == Material.WALL_SIGN ) ///checking left sign
		{
			Sign sign = (Sign) b.getState();
			String signLine0 =  sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
			int tubeSignNum = -999;

			if( signLine0.equalsIgnoreCase("Tube") )
			{
				String tubeString = sign.getLine(1).trim().toLowerCase();
				tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
				if( !tubeString.isEmpty() )
				{
					try{
						tubeSignNum = Integer.parseInt(tubeString);
					}catch (NumberFormatException nfe)
					{

					}
				}
				System.out.println( tubeNum == tubeSignNum );
				if( tubeSignNum != -999 )
					if( tubeNum == tubeSignNum ) {
						torpedoMode = 0;
						missileMode = 0;
						if (cannonType < 10)
						fireTorpedoButton(p);
						else
						fireMissileButton(p, false);
						
						return true;
					}	
			}
		}
		b = getDirectionFromRelative(loc.getBlock(), direction, false).getRelative(BlockFace.UP,1); ///checking right sign
		System.out.println(b.getLocation());
			if (b.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) b.getState();
				String signLine0 = sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
				int tubeSignNum = -999;

				if (signLine0.equalsIgnoreCase("Tube")) {
					String tubeString = sign.getLine(1).trim().toLowerCase();
					tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
					if (!tubeString.isEmpty()) {
						try {
							tubeSignNum = Integer.parseInt(tubeString);
						} catch (NumberFormatException nfe) {

						}
					}
					System.out.println( tubeNum == tubeSignNum );
					if (tubeSignNum != -999)
						if (tubeNum == tubeSignNum) {
							torpedoMode = 1;
							missileMode = 1;
							if (cannonType < 10)
								fireTorpedoButton(p);
							else
								fireMissileButton(p, false);

							return true;
						}
				}
			}
			b = loc.getBlock().getRelative(direction, -1).getRelative(BlockFace.UP, 1);
		System.out.println(b.getLocation());
			if (b.getType() == Material.WALL_SIGN) ///checking vertical sign
			{
				Sign sign = (Sign) b.getState();
				String signLine0 = sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
				int tubeSignNum = -999;

				if (signLine0.equalsIgnoreCase("Tube")) {
					String tubeString = sign.getLine(1).trim().toLowerCase();
					tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
					if (!tubeString.isEmpty()) {
						try {
							tubeSignNum = Integer.parseInt(tubeString);
						} catch (NumberFormatException nfe) {

						}
					}

					if (tubeSignNum != -999)
						if (tubeNum == tubeSignNum) {
							fireMissileButton(p, true);
							return true;
						}
				}
		}
    	return false;
    }
    
    public boolean openTubNum(int tubeNum, Player p, boolean keepOpen) { //try to fire desired tube num, return true if found
    	
    	Block b = getDirectionFromRelative(loc.getBlock(), direction, true).getRelative(BlockFace.UP,1);
    	if( b.getType() == Material.WALL_SIGN ) ///checking left sign
		{
			Sign sign = (Sign) b.getState();
			String signLine0 =  sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
			int tubeSignNum = -999;

			if( signLine0.equalsIgnoreCase("Tube") )
			{
				String tubeString = sign.getLine(1).trim().toLowerCase();
				tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
				if( !tubeString.isEmpty() )
				{
					try{
						tubeSignNum = Integer.parseInt(tubeString);
					}catch (NumberFormatException nfe)
					{

					}
				}
				
				if( tubeSignNum != -999 )
					if( tubeNum == tubeSignNum ) {
						if (cannonType < 10) {
							if (!keepOpen) {
								openTorpedoDoors(p,false, false);
							} else {
								if (checkOuterDoorClosed())
									openTorpedoDoors(p,false, false);
							}
					} else {
						if (!keepOpen) {
							openMissileDoors(p);
						} else {
							if (checkOuterDoorClosed())
								openMissileDoors(p);
						}
					}
						
						return true;
					}	
			}
		}
		b = getDirectionFromRelative(loc.getBlock(), direction, false).getRelative(BlockFace.UP,1); ///checking right sign
			if (b.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) b.getState();
				String signLine0 = sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
				int tubeSignNum = -999;

				if (signLine0.equalsIgnoreCase("Tube")) {
					String tubeString = sign.getLine(1).trim().toLowerCase();
					tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
					if (!tubeString.isEmpty()) {
						try {
							tubeSignNum = Integer.parseInt(tubeString);
						} catch (NumberFormatException nfe) {

						}
					}

					if (tubeSignNum != -999)
						if (tubeNum == tubeSignNum) {
							if (cannonType < 10) {
								if (!keepOpen) {
									openTorpedoDoors(p, false, false);
								} else {
									if (checkOuterDoorClosed())
										openTorpedoDoors(p, false, false);
								}
							} else {
								if (!keepOpen) {
									openMissileDoors(p);
								} else {
									if (checkOuterDoorClosed())
										openMissileDoors(p);
								}
							}

							return true;
						}
				}
			}
			b = loc.getBlock().getRelative(direction, -1).getRelative(BlockFace.UP, 1);
			if (b.getType() == Material.WALL_SIGN) ///checking vertical sign
			{
				Sign sign = (Sign) b.getState();
				String signLine0 = sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
				int tubeSignNum = -999;

				if (signLine0.equalsIgnoreCase("Tube")) {
					String tubeString = sign.getLine(1).trim().toLowerCase();
					tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
					if (!tubeString.isEmpty()) {
						try {
							tubeSignNum = Integer.parseInt(tubeString);
						} catch (NumberFormatException nfe) {

						}
					}

					if (tubeSignNum != -999)
						if (tubeNum == tubeSignNum) {
							if (!keepOpen) {
								openMissileDoorsV(p);
							} else {
								if (checkOuterDoorClosedV())
									openMissileDoorsV(p);
							}
							return true;
						}
				}
			}
    	return false;
    }
    
    public static boolean findandFireTube(Craft c, int tubeNum, Player p)
    {
    	for( OneCannon oc : AimCannon.cannons ) {
    		if(oc.ownerCraft == c && oc.fireTubNum(tubeNum, p) )
    			return true;
    		}
    	return false;
    }
	public static boolean findandLoadTube(Craft c, int tubeNum, Player p)
	{
		for( OneCannon oc : AimCannon.cannons ) {
			if(oc.ownerCraft == c && oc.loadTubNum(tubeNum, p) )
				return true;
		}
		return false;
	}
    public static boolean findandOpenTube(Craft c, int tubeNum, Player p, boolean keepOpen)
    {
    	for( OneCannon oc : AimCannon.cannons ) {
    		if(oc.ownerCraft == c &&  oc.openTubNum(tubeNum, p, keepOpen) )
    			return true;
    	}
    	return false;
    }

	public void updateVector(Vector v) {
    	olook = v;
	}
	public void setImpact() {
		delay = 0;
		mode = 1;
	}
	public void setCharge(int c) { charged = c; }

	public void setDirection(BlockFace dr) {
		direction = dr;
	}

	public BlockFace getDirection() {
    	return direction;
	}
	public void setDepth(int d) {
    	depth = d;
	}

	public void getTargets(Craft craft) {
		HashMap<Integer, String> bearings = new HashMap<>();
		for (Craft c : Craft.craftList) {
			if ((c != craft) && (c.world == craft.world) && !c.sinking && !c.jammerOn && c.leftSafeDock) {
				float xDist = (c.minX + (c.sizeX / 2.0f)) - (loc.getBlockX());
				float zDist = (c.minZ + (c.sizeZ / 2.0f)) - (loc.getBlockZ());
				float dist = (float) Math.sqrt((xDist * xDist) + (zDist * zDist));

				if (dist < 500) {
					double trueBearing = 0;
					if ((xDist >= 0) && (zDist < 0)) {
						double bear = Math.atan(xDist / (-zDist));
						if (bear < 0.26) {
							trueBearing = 0;
						} else if (bear < .70) {
							trueBearing = 30;
						} else if (bear < .87) {
							trueBearing = 45;
						} else if (bear < 1.31) {
							trueBearing = 60;
						} else if (bear < 1.57) {
							trueBearing = 90;
						}
					} else if ((xDist < 0) && (zDist < 0)) {
						double bear = Math.atan(xDist / zDist);
						if (bear < 0.26) {
							trueBearing = 0;
						} else if (bear < .70) {
							trueBearing = 330;
						} else if (bear < .87) {
							trueBearing = 315;
						} else if (bear < 1.31) {
							trueBearing = 300;
						} else if (bear < 1.57) {
							trueBearing = 270;
						}
					} else if ((xDist >= 0) && (zDist > 0)) {
						double bear = Math.atan(xDist / zDist);
						if (bear < 0.26) {
							trueBearing = 180;
						} else if (bear < .70) {
							trueBearing = 150;
						} else if (bear < .87) {
							trueBearing = 135;
						} else if (bear < 1.31) {
							trueBearing = 120;
						} else if (bear < 1.57) {
							trueBearing = 90;
						}
					} else if ((xDist < 0) && (zDist > 0)) {
						double bear = Math.atan((-xDist) / zDist);
						if (bear < 0.26) {
							trueBearing = 180;
						} else if (bear < .70) {
							trueBearing = 210;
						} else if (bear < .87) {
							trueBearing = 225;
						} else if (bear < 1.31) {
							trueBearing = 240;
						} else if (bear < 1.57) {
							trueBearing = 270;
						}
					} else if ((zDist == 0) && (xDist < 0)) {
						trueBearing = 270;
					} else if ((zDist == 0) && (xDist > 0)) {
						trueBearing = 90;
					} else {
						trueBearing = 0;
					}



					int relBearing = (int) trueBearing - Utils.directionFromCardinal(direction);
					if (relBearing < 0) {
						relBearing = relBearing + 360;
					}

					targetCraft.put(c, relBearing);

				}
			}
		}
	}
	public boolean checkLever() {
    	boolean lever = false;
    	if (loc.getBlock().getRelative(BlockFace.NORTH_WEST, 1).getType() == Material.LEVER)
    		lever = true;
		if (loc.getBlock().getRelative(BlockFace.SOUTH_WEST, 1).getType() == Material.LEVER)
			lever = true;
		if (loc.getBlock().getRelative(BlockFace.SOUTH_EAST, 1).getType() == Material.LEVER)
			lever = true;
		if (loc.getBlock().getRelative(BlockFace.NORTH_EAST, 1).getType() == Material.LEVER)
			lever = true;
    	return lever;
	}
}
