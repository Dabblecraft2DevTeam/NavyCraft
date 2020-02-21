package com.maximuspayne.aimcannon;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.craft.Craft;
import com.maximuspayne.navycraft.craft.CraftMover;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Random;


public class Weapon {
	
	Craft firingCraft;
	public Block warhead;
	BlockFace hdg;
	BlockFace ohdg;
	int setDepth;
	int rudder=0;
	int rudderSetting=0;
	int turnProgress = -1;
	int torpSetHeading=-1;
	int torpRotation;
	int setRange;
	int rangeCounter;
	boolean doubleTurn=false;
	int tubeNum=0;
	boolean active=false;
	boolean auto=true;
	public boolean dead = false;
	int dcDirection=0;
	public int weaponType = 0;  //0=torpedo, 1=depth charge // 2=missile // 3= flare // 4=nuke
	public int pingDelay = 20;
	boolean isGuided = false;
	BlockFace targetDirection;
	
	public Weapon(Block b, BlockFace bf, int depth) //torpedo
	{
		weaponType = 0;
		warhead = b;
		hdg = bf;
		setDepth = depth;
	}
	
	public Weapon(Block b, int dcdirectionIn, int depth, Craft c) //flare
	{
		firingCraft = c;
		weaponType = 3;
		warhead = b;
		dcDirection = dcdirectionIn;
		setDepth = depth;
		if (c != null) c.activeFlares.add(this);
	}

	public Weapon(Block b, int dcdirectionIn, Craft c, OneCannon onec) //nuke
	{
		firingCraft = c;
		weaponType = 4;
		warhead = b;
		dcDirection = dcdirectionIn;
		setDepth = 0;
        NavyCraft.instance.getServer().broadcastMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "A Super Weapon has been deployed at X: " + b.getX() + " Y: " + b.getY() + " Z: " + b.getZ() + ".");
		onec.loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.AIR);
	}
	
	public Weapon(Block b, BlockFace bf, int depth, int range) //missile
	{
		weaponType = 2;
		warhead = b;
		hdg = bf;
		ohdg = bf;
		setDepth = depth;
		torpRotation = 0;
		setRange = range;
	}
	
	public Weapon(Block b, int dcdirectionIn, int depth) //dc/bomb
	{
		weaponType = 1;
		warhead = b;
		dcDirection = dcdirectionIn;
		setDepth = depth;
	}
	
	public int weaponRotation()
	{
		int torpRotation=0;
		
		if( hdg == BlockFace.SOUTH )
			torpRotation=180;
		else if( hdg == BlockFace.WEST )
			torpRotation=270;
		else if( hdg == BlockFace.EAST )
			torpRotation=90;
		else if( hdg == BlockFace.NORTH )
			torpRotation=0;
		
		return torpRotation;
	}
	
	public int calculateRange(float xDist, float zDist)
	{
		int range=0;
		if( hdg == BlockFace.SOUTH )
			range=(int) zDist; 
		else if( hdg == BlockFace.WEST )
			range=(int) -xDist;
		else if( hdg == BlockFace.EAST )
			range=(int) xDist; 
		else if( hdg == BlockFace.NORTH )
			range=(int) -zDist;
		System.out.println(range);
		return range;
	}
	
	public void calculateHeading(float rotation)
	{	
		
		float nx = -(float) Math.sin(rotation);
		float nz = (float) Math.cos(rotation);
		
		int currentRotation = weaponRotation()%360;
		

		
	////north
		if( currentRotation == 0 )
		{
			if( nx > 0.5 )
			{
				rudder = 1;
				turnProgress = 0;
				if(  Math.abs(nz) > .07 )
				{
					rudderSetting = (int)(1.0f / nz);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}
			else if( nx < -0.5 )
			{
				rudder = -1;
				turnProgress = 0;
				if( Math.abs(nz) > .07 )
				{
					rudderSetting = -(int)(1.0f / nz);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}else if( nz < 0 )
			{
				if(  Math.abs(nx) > .07 )
				{
					rudder = (int)(1.0f / nx);
					if( rudder > 10 )
						rudder = 10;
					else if( rudder < -10 )
						rudder = -10;
					rudderSetting = rudder;
				}
			}else
			{
				if( nx < 0 )
				{
					doubleTurn = true;
					rudder = -1;
					turnProgress = 0;
				}else if( nx > 0 )
				{
					doubleTurn = true;
					rudder = 1;
					turnProgress = 0;
				}
				if(  Math.abs(nx) > .07 )
				{
					rudderSetting = (int)(1.0f / -nx);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}
			
		
		//////south
		}else if( currentRotation == 180 )
		{
			
			if( nx > 0.5 )
			{
				rudder = -1;
				turnProgress = 0;
				if(  Math.abs(nz) > .07 )
				{
					rudderSetting = (int)(1.0f / nz);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}
			else if( nx < -0.5 )
			{
				rudder = 1;
				turnProgress = 0;
				if(  Math.abs(nz) > .07 )
				{
					rudderSetting = (int)(1.0f / -nz);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}else if( nz > 0 )
			{
				if(  Math.abs(nx) > .07 )
				{
					rudder = (int)(1.0f / -nx);
					if( rudder > 10 )
						rudder = 10;
					else if( rudder < -10 )
						rudder = -10;
					rudderSetting = rudder;
				}
			}else
			{
				if( nx < 0 )
				{
					doubleTurn = true;
					rudder = 1;
					turnProgress = 0;
				}else if( nx > 0 )
				{
					doubleTurn = true;
					rudder = -1;
					turnProgress = 0;
				}
				if(  Math.abs(nx) > .07 )
				{
					rudderSetting = (int)(1.0f / nx);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}
		//////east
		}else if( currentRotation == 90 )
		{
			
			if( nz > 0.5 )
			{
				rudder = 1;
				turnProgress = 0;
				if(  Math.abs(nx) > .07 )
				{
					rudderSetting = (int)(1.0f / -nx);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}
			else if( nz < -0.5 )
			{
				rudder = -1;
				turnProgress = 0;
				if(  Math.abs(nx) > .07 )
				{
					rudderSetting = (int)(1.0f / nx);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}else if( nx > 0 )
			{
				if(  Math.abs(nz) > .07 )
				{
					rudder = (int)(1.0f / nz);
					if( rudder > 10 )
						rudder = 10;
					else if( rudder < -10 )
						rudder = -10;
					rudderSetting = rudder;
				}
			}else
			{
				if( nz < 0 )
				{
					doubleTurn = true;
					rudder = -1;
					turnProgress = 0;
				}else if( nz > 0 )
				{
					doubleTurn = true;
					rudder = 1;
					turnProgress = 0;
				}
				if(  Math.abs(nz) > .07 )
				{
					rudderSetting = (int)(1.0f / -nz);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}
		//////////////west
		}else if( currentRotation == 270 )
		{
			if( nz > 0.5 )
			{
				rudder = -1;
				turnProgress = 0;
				if(  Math.abs(nx) > .07 )
				{
					rudderSetting = (int)(1.0f / -nx);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}
			else if( nz < -0.5 )
			{
				rudder = 1;
				turnProgress = 0;
				if(  Math.abs(nx) > .07 )
				{
					rudderSetting = (int)(1.0f / nx);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}else if( nx < 0 )
			{
				if(  Math.abs(nz) > .07 )
				{
					rudder = (int)(1.0f / -nz);
					if( rudder > 10 )
						rudder = 10;
					else if( rudder < -10 )
						rudder = -10;
					rudderSetting = rudder;
				}
			}else
			{
				if( nz < 0 )
				{
					doubleTurn = true;
					rudder = 1;
					turnProgress = 0;
				}else if( nz > 0 )
				{
					doubleTurn = true;
					rudder = -1;
					turnProgress = 0;
				}
				if(  Math.abs(nz) > .07 )
				{
					rudderSetting = (int)(1.0f / nz);
					if( rudderSetting > 10 )
						rudderSetting = 10;
					else if( rudderSetting < -10 )
						rudderSetting = -10;
				}
			}
		}
	}
	
	public float calculateRelBearing(float xDist, float zDist) 
	{
		float trueBearing = 0;
		if ((xDist >= 0) && (zDist < 0)) {
			double bear = Math.atan(xDist / (-zDist));
			if (bear < 0.12) {
				trueBearing = 0;
			} else if (bear < .38) {
				trueBearing = 15;
			} else if (bear < .65) {
				trueBearing = 30;
			} else if (bear < .91) {
				trueBearing = 45;
			} else if (bear < 1.17) {
				trueBearing = 60;
			} else if (bear < 1.43) {
				trueBearing = 75;
			} else if (bear < 1.57) {
				trueBearing = 90;
			}
		} else if ((xDist < 0) && (zDist < 0)) {
			double bear = Math.atan(xDist / zDist);
			if (bear < 0.12) {
				trueBearing = 0;
			} else if (bear < .38) {
				trueBearing = 345;
			} else if (bear < .65) {
				trueBearing = 330;
			} else if (bear < .91) {
				trueBearing = 315;
			} else if (bear < 1.17) {
				trueBearing = 300;
			} else if (bear < 1.43) {
				trueBearing = 285;
			} else if (bear < 1.57) {
				trueBearing = 270;
			}
		} else if ((xDist >= 0) && (zDist > 0)) {
			double bear = Math.atan(xDist / zDist);
			if (bear < 0.12) {
				trueBearing = 180;
			} else if (bear < .38) {
				trueBearing = 165;
			} else if (bear < .65) {
				trueBearing = 150;
			} else if (bear < .91) {
				trueBearing = 135;
			} else if (bear < 1.17) {
				trueBearing = 120;
			} else if (bear < 1.43) {
				trueBearing = 105;
			} else if (bear < 1.57) {
				trueBearing = 90;
			}
		} else if ((xDist < 0) && (zDist > 0)) {
			double bear = Math.atan((-xDist) / zDist);
			if (bear < 0.12) {
				trueBearing = 180;
			} else if (bear < .38) {
				trueBearing = 195;
			} else if (bear < .65) {
				trueBearing = 210;
			} else if (bear < .91) {
				trueBearing = 225;
			} else if (bear < 1.17) {
				trueBearing = 240;
			} else if (bear < 1.43) {
				trueBearing = 255;
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

        float relBearing = trueBearing - weaponRotation();
		if (relBearing < 0) {
			relBearing = relBearing + 360;
		}

		return trueBearing;
	}
	
	public void doPingTrack(Craft c)
	{
		int targetID = -1;
		float xDist = 0;
		float zDist = 0;
		if( c.fireControlTargets.containsKey(tubeNum) )
			targetID = c.fireControlTargets.get(tubeNum);
		Craft targetCraft = c.sonarTargetIDs2.get(targetID);
		if( targetCraft == null )
			return;
		if (!targetCraft.jammerOn) {
		if (targetCraft.activeFlares.isEmpty()) {
		xDist = targetCraft.getLocation().getBlockX() - warhead.getX();
		zDist = targetCraft.getLocation().getBlockZ() - warhead.getZ();
		targetDirection = warhead.getFace(targetCraft.getLocation().getBlock());
		if (torpRotation == 3) 
			hdg = targetDirection;
		int depth = 0;
		if (weaponType == 0) {
		depth = 63 - (targetCraft.minY + targetCraft.sizeY/3);
		if (depth < 0) depth = 0;
		} else {
		depth = targetCraft.minY + targetCraft.sizeY/3;
		}
		setDepth = depth;
		} else {
			Random rand = new Random();
			Weapon dc = targetCraft.activeFlares.get(rand.nextInt(targetCraft.activeFlares.size()));
			xDist = dc.warhead.getLocation().getBlockX() - warhead.getX();
			zDist = dc.warhead.getLocation().getBlockZ() - warhead.getZ();
			targetDirection = warhead.getFace(dc.warhead);
			int depth = 0;
			if (weaponType == 0) {
			depth = 63 - dc.warhead.getY();
			if (depth < 0) depth = 0;
			} else {
			depth = dc.warhead.getY();
			}
			setDepth = depth;
		}
		//rotation = torp.calculateRelBearing(xDist, zDist);
		float rotation = (float) Math.PI * (calculateRelBearing(xDist, zDist)-180f) / 180f;
		calculateHeading(rotation);
		
		double distance = Math.sqrt(xDist*xDist + zDist*zDist);
		if( distance < 20 )
			pingDelay = 10;
		else if( distance < 50 )
			pingDelay = 20;
		else if( distance < 200 )
			pingDelay = 40;
		else
			pingDelay = 60;
		torpedoPingThread(warhead.getLocation());
		torpedoPingThread(targetCraft.getLocation());
		}
	}
	
	public void doBlockTrack(Block b)
	{
		if (b != null) {
		float xDist = b.getX() - warhead.getX();
		float zDist = b.getZ() - warhead.getZ();
		targetDirection = warhead.getFace(b);
		if (torpRotation == 3) 
			hdg = targetDirection;
		int depth = 0;
		if (weaponType == 0) {
		depth = 63 - b.getY();
		if (depth < 0) depth = 0;
		} else {
		depth = b.getY();
		}
		setDepth = depth;
		//rotation = torp.calculateRelBearing(xDist, zDist);
		float rotation = (float) Math.PI * (calculateRelBearing(xDist, zDist)-180f) / 180f;
		calculateHeading(rotation);
		
		double distance = Math.sqrt(xDist*xDist + zDist*zDist);
		if( distance < 20 )
			pingDelay = 10;
		else if( distance < 50 )
			pingDelay = 20;
		else if( distance < 200 )
			pingDelay = 40;
		else
			pingDelay = 60;
		}
	}
	
	public static void torpedoPingThread(final Location loc) {
		Craft c = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		new Thread() {

			@Override
			public void run() {

				setPriority(Thread.MIN_PRIORITY);

				// taskNum = -1;
				try {
					for (int i = 0; i < 2; i++) {
						sleep(400);
						if( i==0 ) {
							if (c != null) c.trackingStrength = 2;
							CraftMover.playOtherSound(loc, Sound.BLOCK_NOTE_HARP, 2.0f, 1.6f);
						} else {
							if (c != null) c.trackingStrength = 3;
							CraftMover.playOtherSound(loc, Sound.BLOCK_NOTE_HARP, 2.0f, 2.0f);
						}
					}
					if (c != null) c.trackingStrength = 0;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start(); // , 20L);
	}
	
	public void destroyWeapon()
	{
		dead = true;
        AimCannon.weapons.remove(this);
	}
}
