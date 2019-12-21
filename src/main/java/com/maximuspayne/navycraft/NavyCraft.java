package com.maximuspayne.navycraft;

import com.massivecraft.factions.*;
import com.maximuspayne.aimcannon.explosiveEgg;
import com.maximuspayne.navycraft.blocks.BlocksInfo;
import com.maximuspayne.navycraft.craft.*;
import com.maximuspayne.navycraft.events.NavyCraftExplosionEvent;
import com.maximuspayne.navycraft.listeners.NavyCraft_BlockListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_EntityListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_InventoryListener;
import com.maximuspayne.navycraft.listeners.NavyCraft_PlayerListener;
import com.maximuspayne.navycraft.teleportfix.TeleportFix;
import com.maximuspayne.shipyard.Plot;
import com.maximuspayne.shipyard.PlotType;
import com.maximuspayne.shipyard.Reward;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Logger;



/**
 * MoveCraft plugin for Hey0 mod (hMod) by Yogoda
 * Ported to Bukkit by SycoPrime
 * Modified by Maximuspayne for NBZ
 * Continuing to be modified by Solmex
 * Licensed under Apache 2.0 
 * 
 * You are free to modify it for your own server
 * or use part of the code for your own plugins.
 * You don't need to credit me if you do, but I would appreciate it :)
 *
 * You are not allowed to distribute alternative versions of MoveCraft without my consent.
 * If you do cool modifications, please tell me so I can integrate it :)
 */

/**
* All additions made to the plugin by Solmex are private, and you must request to use them before putting them on a live server.
* You can however, use them for testing purposes, or any private use.
* Though if you are interested in using it on a live server contact me on discord (solmex#7439).
*/

@SuppressWarnings({"deprecation"})
public class NavyCraft extends JavaPlugin {

	static final String pluginName = "NavyCraft";
	public static String version;
	public static NavyCraft instance;

	public static Logger logger = Logger.getLogger("Minecraft");
	public boolean DebugMode = false;

	public static ArrayList<Player> aaGunnersList = new ArrayList<Player>();
	public static ArrayList<Skeleton> aaSkelesList = new ArrayList<Skeleton>();
	public static ArrayList<Player> ciwsGunnersList = new ArrayList<Player>();
	public static ArrayList<Player> ciwsFiringList = new ArrayList<Player>();
	public static ArrayList<Skeleton> ciwsSkelesList = new ArrayList<Skeleton>();
	public static ArrayList<Player> boforGunnersList = new ArrayList<Player>();
	public static ArrayList<Player> boforFiringList = new ArrayList<Player>();
	public static ArrayList<Skeleton> boforSkelesList = new ArrayList<Skeleton>();
	public static ArrayList<explosiveEgg> explosiveEggsList = new ArrayList<explosiveEgg>();
	public static HashMap<UUID, Player> shotTNTList = new HashMap<UUID, Player>();
	public static ArrayList<FallingBlock> fallingBlocksList = new ArrayList<>();
	public static HashMap<Player, Block> playerLastUseSign = new HashMap<Player, Block>();

	public static HashMap<Integer, AISpawning> AISpawnTasks = new HashMap<>();
	
	public final NavyCraft_PlayerListener playerListener = new NavyCraft_PlayerListener(this);
	public final NavyCraft_BlockListener blockListener = new NavyCraft_BlockListener(this);
	public final NavyCraft_EntityListener entityListener = new NavyCraft_EntityListener(this);
	public final NavyCraft_InventoryListener inventoryListener = new NavyCraft_InventoryListener(this);
	public final NavyCraft_InventoryListener fileListener = new NavyCraft_InventoryListener(this);

	public static Thread updateThread=null;
	public static boolean shutDown = false;
	
	public static WorldGuardPlugin wgp;
	
	public static ArrayList<Periscope> allPeriscopes = new ArrayList<Periscope>();
	
	public static HashMap<Player, Location> searchLightMap = new HashMap<Player, Location>();
	public static HashMap<Player, ArrayList<Chicken>> playerActiveParachutes = new HashMap<>();
	public static HashMap<Player, Block> playerLastBoughtSign = new HashMap<Player, Block>();
	public static HashMap<Player, Integer> playerLastBoughtCost = new HashMap<Player, Integer>();
	public static HashMap<Player, String> playerLastBoughtSignString0 = new HashMap<Player, String>();
	public static HashMap<Player, String> playerLastBoughtSignString1 = new HashMap<Player, String>();
	public static HashMap<Player, String> playerLastBoughtSignString2 = new HashMap<Player, String>();
	public ArrayList<Material> disallowedBlocks = new ArrayList<Material>();

	
	public static int spawnTime=10;
	
	public static HashMap<String, ArrayList<Plot>> playerSigns = new HashMap<String, ArrayList<Plot>>();
	
	public static HashMap<String, ArrayList<Reward>> playerRewards = new HashMap<String, ArrayList<Reward>>();
	
	public static HashMap<Sign, Integer> playerSignIndex = new HashMap<Sign, Integer>();
	
	public static HashMap<String, Integer> playerExp = new HashMap<String, Integer>();
	
	public static HashMap<String, Long> playerPayDays = new HashMap<String, Long>();
	
	public static ArrayList<String> disableHiddenChats = new ArrayList<String>();
	public static HashMap<String, Integer> playerChatRegions = new HashMap<String, Integer>();
	
	public static HashMap<String, Integer> cleanupPlayerTimes = new HashMap<String, Integer>();
	public static ArrayList<String> cleanupPlayers = new ArrayList<String>();
	
	public static HashMap<String, Long> shipTPCooldowns = new HashMap<String, Long>();
	
	public static int schedulerCounter = 0;
	public static int waveCounter = 0;
	
	public static HashMap<Player, Float> playerEngineVolumes = new HashMap<Player, Float>();
	public static HashMap<Player, Float> playerWeaponVolumes = new HashMap<Player, Float>();
	public static HashMap<Player, Float> playerOtherVolumes = new HashMap<Player, Float>();

	public static HashMap<Craft, BukkitTask> AITasks = new HashMap<>();

	private ConfigManager cfgm;

	public static HashMap<String, ArrayList<Reward>> getRewards() {
		return playerRewards;
	}

	
	public void loadProperties() {
		getConfig().options().copyDefaults(true);

		loadConfigManager();
		PlotType.initialize();
		
		CraftType.setupCraftConfig(); 
	}
	
	public void loadConfigManager() {
		cfgm = new ConfigManager();
		cfgm.setupsyConfig();
		cfgm.setupsyData();
		cfgm.setupcostData();
		cfgm.setupBaseData();
		cfgm.setupRouteData();
	}

	public void onEnable() {
		instance = this; 
		shutDown = false;
		MetricsLite metrics = new MetricsLite(this);
		
		this.saveDefaultConfig();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(entityListener, this);
		pm.registerEvents(blockListener, this);
		pm.registerEvents(inventoryListener, this);
		pm.registerEvents(fileListener, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		version = pdfFile.getVersion();

		BlocksInfo.loadBlocksInfo();
		loadProperties();
		PermissionInterface.setupPermissions(this);
		
		PluginManager manager = getServer().getPluginManager();
		 
        manager.registerEvents(new TeleportFix(this, this.getServer()), this);
		
		structureUpdateScheduler();
		
		getConfig().options().copyDefaults(true);
		
		System.out.println(pdfFile.getName() + " " + version + " plugin enabled");
		}

	public void onDisable() {
		shutDown = true;
		PermissionInterface.removePermissions(this);
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + version + " plugin disabled");
	}


	public void ToggleDebug() {
		this.DebugMode = !this.DebugMode;
		System.out.println("Debug mode set to " + this.DebugMode);
	}
	

	public boolean DebugMessage(String message, int messageLevel) {
		/* Message Levels:
		 * 0: Error
		 * 1: Something I'm currently testing
		 * 2: Something I think I just fixed
		 * 3: Something I'm pretty sure is fixed
		 * 4: Supporting information
		 * 5: Nearly frivolous information
		 */
		
		//if(this.DebugMode == true)
		if(Integer.parseInt(this.getConfig().getString("LogLevel")) >= messageLevel)
			System.out.println(message);
		return this.DebugMode;
	}

	public static void explosion(int explosionRadius, Block warhead, boolean water) {
		//destroyedBlocks list added by iiz for use in denying explosions more precisely
		HashMap<Location, Material> destroyedBlocks = new HashMap<>();

		// Getting the faction by FLocation/Location & tag
		Faction faction = null;

		// By FLocation/Location
		FLocation fLoc = new FLocation(warhead.getLocation());
		faction = Board.getInstance().getFactionAt(fLoc);

		Boolean allowed = faction.hasPlayersOnline();

		for (FPlayer player : faction.getFPlayers()) {
			if (player.getLastLoginTime() > System.currentTimeMillis() - 3600000) {
				allowed = true;
				break;
			}
		}
		if (!faction.isWilderness() && !allowed)
			return;
		System.out.println("Faction: " + faction.getTag() + " Allowed explosion: " + allowed);

		//HashMap<String, org.bukkit.Chunk> protectedChunks = new HashMap<>();

		short[][][] powerMatrix;
		powerMatrix = new short[explosionRadius * 2 + 1][explosionRadius * 2 + 1][explosionRadius * 2 + 1];

		powerMatrix[explosionRadius][explosionRadius][explosionRadius] = (short) (explosionRadius * 50);

		destroyedBlocks.put(warhead.getLocation(), warhead.getType());

		Material originalWarheadMaterial = warhead.getType();

		int refI = 0;
		int refJ = 0;
		int refK = 0;
		int curX = 0;
		int curY = 0;
		int curZ = 0;
		int refX = 0;
		int refY = 0;
		int refZ = 0;
		boolean doPower = false;
		int fuseDelay = 5;


		for (int r = 1; r < explosionRadius; r++) {
			for (int j = -r; j <= r; j++) {
				for (int i = -r; i <= r; i++) {
					for (int k = -r; k <= r; k++) {


						float refPowerMult = 1.0f;
						curX = i + explosionRadius;
						curY = j + explosionRadius;
						curZ = k + explosionRadius;

						if (powerMatrix[curX][curY][curZ] > 0)
							continue;

						if (Math.abs(i) == r)//if on x edges
						{
							refI = (int) ((Math.abs(i) - 1) * Math.signum(i));
							if (Math.abs(j) == r)//if on xy edges
							{
								refJ = (int) ((Math.abs(j) - 1) * Math.signum(j));
								if (Math.abs(k) == r && Math.abs(k) > 2) //corner piece, not innermost corners
								{
									refK = (int) ((Math.abs(k) - 1) * Math.signum(k));
									refPowerMult = 0.10f;
								} else if (Math.abs(k) >= r - 2 && Math.abs(k) > 2) //near corner piece
								{
									refPowerMult = 0.25f;
								} else    //middle xy edge
								{
									refPowerMult = 0.40f;
								}
							} else if (Math.abs(k) == r) //if on xz edges
							{
								refK = (int) ((Math.abs(k) - 1) * Math.signum(k));
								refPowerMult = 0.40f;
								if (Math.abs(j) >= r - 2 && Math.abs(j) > 2) //near corner
									refPowerMult = 0.25f;
							} else if (Math.abs(j) == r - 1)//if near xy edge
							{
								refPowerMult = 0.40f;
								if (Math.abs(k) >= r - 2 && Math.abs(k) > 2) //near xyz corner
									refPowerMult = 0.25f;
							} else if (Math.abs(k) == r - 1)//if near xz edge
							{
								refPowerMult = 0.40f;
								if (Math.abs(j) >= r - 2 && Math.abs(j) > 2)//near xyz corner
									refPowerMult = 0.25f;
							} else //somewhere else on x sides
							{
								refPowerMult = 0.60f;
							}
							doPower = true;
						} else if (Math.abs(j) == r)//if on y sides
						{
							refJ = (int) ((Math.abs(j) - 1) * Math.signum(j));
							if (Math.abs(k) == r) //if on yz edge
							{
								refK = (int) ((Math.abs(k) - 1) * Math.signum(k));
								refPowerMult = 0.40f;
								if (Math.abs(i) >= r - 2 && Math.abs(i) > 2)//near xyz corner
									refPowerMult = 0.25f;
							} else if (Math.abs(i) == r - 1)//near xy edge
							{
								refPowerMult = 0.40f;
								if (Math.abs(k) >= r - 2 && Math.abs(k) > 2)//near xyz corner
									refPowerMult = 0.25f;
							} else if (Math.abs(k) == r - 1)//near yz edge
							{
								refPowerMult = 0.40f;
								if (Math.abs(i) >= r - 2 && Math.abs(i) > 2)//near xyz corner
									refPowerMult = 0.25f;
							} else //somewhere else on y sides
							{
								refPowerMult = 0.60f;
							}
							doPower = true;
						} else if (Math.abs(k) == r)//if on z sides
						{
							refK = (int) ((Math.abs(k) - 1) * Math.signum(k));
							if (Math.abs(i) >= r - 2 && Math.abs(i) > 2)//near xz side
							{
								refPowerMult = 0.40f;
								if (Math.abs(j) >= r - 2 && Math.abs(j) > 2)//near corner
									refPowerMult = 0.25f;
							} else if (Math.abs(j) == r - 1)//near yz side
							{
								refPowerMult = 0.40f;
								if (Math.abs(i) >= r - 2 && Math.abs(i) > 2)//near corner
									refPowerMult = 0.25f;
							} else //somewhere else on z sides
							{
								refPowerMult = 0.60f;
							}
							doPower = true;
						}

						if (doPower) {
						/*
						Location loc = warhead.getRelative(i,j,k).getLocation();
						if(!protectedChunks.containsKey(loc.getChunk().toString()))
							if (!kingdomsHandler.isVulnerableToWarhead(loc)) {
								protectedChunks.put(loc.getChunk().toString(), loc.getChunk());
								continue;
							}
						*/

							refX = refI + explosionRadius;
							refY = refJ + explosionRadius;
							refZ = refK + explosionRadius;

							Block theBlock = warhead.getRelative(i, j, k);
							int blockType = theBlock.getTypeId();

							destroyedBlocks.put(theBlock.getLocation(), theBlock.getType());

							short refPower = (short) (powerMatrix[refX][refY][refZ] * refPowerMult);
							if (refPower > 0) {

								short blockResist;
								if (Craft.blockHardness(blockType) == 4) {
									blockResist = -1;
								} else if (Craft.blockHardness(blockType) == 3)//obsidian
								{
									blockResist = (short) (40 + 40 * Math.random());
								} else if (Craft.blockHardness(blockType) == 2)//iron
								{
									blockResist = (short) (20 + 20 * Math.random());
								} else if (Craft.blockHardness(blockType) == 1)//wood
								{
									blockResist = (short) (10 + 15 * Math.random());
								} else if (Craft.blockHardness(blockType) == -3)//water
								{
									blockResist = (short) (5 + 5 * Math.random());
								} else {
									blockResist = (short) (2 * Math.random());
								}

								if (Craft.blockHardness(blockType) == -1) {
									theBlock.setType(Material.AIR);
									TNTPrimed tnt = (TNTPrimed) theBlock.getWorld().spawnEntity(new Location(theBlock.getWorld(), theBlock.getX(), theBlock.getY(), theBlock.getZ()), EntityType.PRIMED_TNT);
									tnt.setFuseTicks(fuseDelay);
									fuseDelay = fuseDelay + 2;
								} else if (Craft.blockHardness(blockType) == -2) {
									theBlock.setType(Material.AIR);
									TNTPrimed tnt = (TNTPrimed) theBlock.getWorld().spawnEntity(new Location(theBlock.getWorld(), theBlock.getX(), theBlock.getY(), theBlock.getZ()), EntityType.PRIMED_TNT);
									tnt.setFuseTicks(fuseDelay);
									tnt.setYield(tnt.getYield() * 0.5f);
									fuseDelay = fuseDelay + 2;
								} else {

									if (refPower > blockResist && blockResist >= 0) {
										double x = 0;
										double y = 0;
										double z = 0;
										Location eLoc = warhead.getLocation();
										World w = eLoc.getWorld();
										Block b = theBlock;
										Location bLoc = b.getLocation();
										x = bLoc.getX() - eLoc.getX();
										y = bLoc.getY() - eLoc.getY() + .5;
										z = bLoc.getZ() - eLoc.getZ();
										FallingBlock fb = w.spawnFallingBlock(bLoc, b.getType(), b.getData());
										fb.setDropItem(false);
										fb.setVelocity(new Vector(x, y, z));
										if (!NavyCraft.fallingBlocksList.contains(fb))
											NavyCraft.fallingBlocksList.add(fb);
										if (theBlock.getY() > 62)
											theBlock.setType(Material.AIR);
										else
											theBlock.setType(Material.WATER);
									} else {
										refPower = 0;
									}
								}
								short newPower = (short) (refPower - blockResist);

								if (newPower < 5) {
									powerMatrix[curX][curY][curZ] = 0;
								} else {
									powerMatrix[curX][curY][curZ] = newPower;
								}
							}

						} else
							warhead.setType(originalWarheadMaterial);
					}
				}
			}
		}
		warhead.getWorld().createExplosion(warhead.getLocation(), explosionRadius / 2.0f);

		for (Location loc : destroyedBlocks.keySet()) {
			if (getCoreProtect() != null)
				getCoreProtect().logRemoval("explosion", loc, destroyedBlocks.get(loc), (byte) 0x0);
		}

		instance.getServer().getPluginManager().callEvent(new NavyCraftExplosionEvent(destroyedBlocks));
	}
    public static boolean checkRepairRegion(Location loc)
    {
    	
    	wgp = (WorldGuardPlugin) instance.getServer().getPluginManager().getPlugin("WorldGuard");
    	if( wgp != null && loc != null)
    	{
    		if( !!Utils.CheckEnabledWorld(loc) )
    		{
    			return false;
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
					if( splits[1].equalsIgnoreCase("repair") )
						return true;					
				}
		    }
			return false;
		}
    	return false;
	}
    
    public static boolean checkSafeDockRegion(Location loc)
    {
    	
    	wgp = (WorldGuardPlugin) instance.getServer().getPluginManager().getPlugin("WorldGuard");
    	if( wgp != null && loc != null)
    	{
    		if( !Utils.CheckEnabledWorld(loc) )
    		{
    			return false;
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
					if( splits[1].equalsIgnoreCase("safedock") )
						return true;					
				}
		    }
			return false;
		}
    	return false;
	}
    
    public static boolean checkRecallRegion(Location loc)
    {
    	
    	wgp = (WorldGuardPlugin) instance.getServer().getPluginManager().getPlugin("WorldGuard");
    	if( wgp != null && loc != null)
    	{
    		if( !Utils.CheckEnabledWorld(loc) )
    		{
    			return false;
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
					if( splits[1].equalsIgnoreCase("recall") )
						return true;					
				}
		    }
			return false;
		}
    	return false;
	}
    
	public static Faction checkFactionLand(Location loc) {

		// Getting the faction by FLocation/Location & tag
		Faction faction = null;

		// By FLocation/Location
		FLocation fLoc = new FLocation(loc);
		if (!Board.getInstance().getFactionAt(fLoc).isWilderness())
			faction = Board.getInstance().getFactionAt(fLoc);
		
		return faction;
	}
    
    public static boolean checkSpawnRegion(Location loc)
    {
    	
    	wgp = (WorldGuardPlugin) instance.getServer().getPluginManager().getPlugin("WorldGuard");
    	if( wgp != null && loc != null)
    	{
    		if( !Utils.CheckEnabledWorld(loc) )
    		{
    			return false;
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
					if( splits[1].equalsIgnoreCase("spawn") )
						return true;					
				}
		    }
			return false;
		}
    	return false;
	}
    
    public static boolean checkNoDriveRegion(Location loc)
    {
    	
    	wgp = (WorldGuardPlugin) instance.getServer().getPluginManager().getPlugin("WorldGuard");
    	if( wgp != null && loc != null)
    	{
    		if( !Utils.CheckEnabledWorld(loc) )
    		{
    			return false;
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
					if( splits[1].equalsIgnoreCase("nodrive") )
						return true;					
				}
		    }
			return false;
		}
    	return false;
	}

	public void dropItem(Block block) {		
		if(NavyCraft.instance.getConfig().getString("Drill").equalsIgnoreCase("true"))
			return;

		int itemToDrop = BlocksInfo.getDropItem(block.getTypeId());
		int quantity = BlocksInfo.getDropQuantity(block.getTypeId());

		if(itemToDrop != -1 && quantity != 0){

			for(int i=0; i<quantity; i++){
				block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(itemToDrop, 1));
			}
		}
	}
   
   public void structureUpdateScheduler()
   {
   	this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
   	//new Thread() {
	  //  @Override
		    public void run()
		    {
		    	if( Craft.craftList == null || Craft.craftList.isEmpty() )
		    	{
		    		if( !Craft.addCraftList.isEmpty() )
	    			{
	    				for( Craft c: Craft.addCraftList )
	    				{
	    					Craft.addCraft(c);
	    				}
	    				Craft.addCraftList.clear();
	    			}
		    		return;
		    	}
		    	int vehicleCount = Craft.craftList.size();
	    		int vehicleNum = (schedulerCounter) % vehicleCount;
	    		int updateNum = (schedulerCounter / vehicleCount)%4;
	    
	    		try{
	    		if( vehicleCount < 10 )
	    		{
	    			updateCraft(vehicleNum,updateNum);
	    			schedulerCounter++;
	    		}else if( vehicleCount >= 10 && vehicleCount < 20)
	    		{
	    			//vehicleNum = (vehicleNum + vehicleCount/2)%vehicleCount;
	    			updateCraft(vehicleNum,updateNum);
	    			vehicleNum = (vehicleNum + 1)%vehicleCount;
	    			schedulerCounter = schedulerCounter + 4;
	    			updateCraft(vehicleNum,updateNum);
	    			schedulerCounter = schedulerCounter - 4;
	    			
	    			if( updateNum == 3 )
		    			schedulerCounter+=5;
		    		else
		    			schedulerCounter++;
	    		}else if( vehicleCount >= 20 && vehicleCount < 30 )
	    		{
	    			//vehicleNum = (vehicleNum + vehicleCount/3)%vehicleCount;
	    			vehicleNum = (vehicleNum + 1)%vehicleCount;
	    			schedulerCounter = schedulerCounter + 4;
	    			updateCraft(vehicleNum,updateNum);
	    			vehicleNum = (vehicleNum + 1)%vehicleCount;
	    			schedulerCounter = schedulerCounter + 4;
	    			//vehicleNum = (vehicleNum + vehicleCount/3)%vehicleCount;
	    			updateCraft(vehicleNum,updateNum);
	    			schedulerCounter = schedulerCounter - 4;
	    			schedulerCounter = schedulerCounter - 4;
	    			
	    			if( updateNum == 3 )
		    			schedulerCounter+=9;
		    		else
		    			schedulerCounter++;
	    		}else
	    		{
	    			//vehicleNum = (vehicleNum + vehicleCount/4)%vehicleCount;
	    			vehicleNum = (vehicleNum + 1)%vehicleCount;
	    			schedulerCounter = schedulerCounter + 4;
	    			updateCraft(vehicleNum,updateNum);
	    			//vehicleNum = (vehicleNum + vehicleCount/4)%vehicleCount;
	    			vehicleNum = (vehicleNum + 1)%vehicleCount;
	    			schedulerCounter = schedulerCounter + 4;
	    			updateCraft(vehicleNum,updateNum);
	    			//vehicleNum = (vehicleNum + vehicleCount/4)%vehicleCount;
	    			vehicleNum = (vehicleNum + 1)%vehicleCount;
	    			schedulerCounter = schedulerCounter + 4;
	    			updateCraft(vehicleNum,updateNum);
	    			schedulerCounter = schedulerCounter - 4;
	    			schedulerCounter = schedulerCounter - 4;
	    			schedulerCounter = schedulerCounter - 4;
	    			
	    			if( updateNum == 3 )
		    			schedulerCounter+=13;
		    		else
		    			schedulerCounter++;
	    		}
	    		
	    		
	    		if( updateNum == 3 )
	    		{
	    			if( !Craft.addCraftList.isEmpty() )
	    			{
	    				for( Craft c: Craft.addCraftList )
	    				{
	    					Craft.addCraft(c);
	    				}
	    				Craft.addCraftList.clear();
	    			}
	    		}
	    		}catch(Exception e)
	    		{
	    			schedulerCounter++;
	    		}
		    }
   	}
   	, 4, 1);
	 }
   
public void updateCraft(int vehicleNum, int updateNum)
   {
	   int vehicleCount = Craft.craftList.size();
	   if( vehicleNum < Craft.craftList.size() && Craft.craftList.get(vehicleNum) != null )
	   {
			Craft checkCraft = Craft.craftList.get(vehicleNum);
			
			if( checkCraft != null && !checkCraft.isDestroying )
			{

				if( checkCraft.isMoving )
				{
					
					if( updateNum == 0 )
					{
						if( checkCraft.doRemove )
						{
							checkCraft.remove();
						}else if( checkCraft.doDestroy )
						{
							checkCraft.destroy();
						}else if( (Math.abs(checkCraft.gear) == 1 && ((schedulerCounter/4) / vehicleCount)%3 == 0)
								|| (Math.abs(checkCraft.gear) == 2 && ((schedulerCounter/4) / vehicleCount)%2 == 0) 
								|| (Math.abs(checkCraft.gear) == 3) )
						{
							if( (Math.abs(checkCraft.gear) == 1 && (System.currentTimeMillis() - checkCraft.lastMove)/1000 > 8)
									|| (Math.abs(checkCraft.gear) == 2 && (System.currentTimeMillis() - checkCraft.lastMove)/1000 >= 5)
									|| (Math.abs(checkCraft.gear) == 3 && (System.currentTimeMillis() - checkCraft.lastMove)/1000.0f >= 2.5) )
							{
								CraftMover cm = new CraftMover(checkCraft, instance);
								cm.moveUpdate();
								//System.out.println("Ship moveupdate="+ checkCraft.craftID);
							}
						}else
						{
							if( !checkCraft.recentlyUpdated )
							{
								if( (System.currentTimeMillis() - checkCraft.lastUpdate)/1000 > 1 )
								{
									CraftMover cm = new CraftMover(checkCraft, instance);
				    				cm.structureUpdate(null,true);
				    				//System.out.println("Ship structureupdate="+ checkCraft.craftID);
								}
							}else
							{
								checkCraft.recentlyUpdated = false;
							}
						}
					}else if( updateNum == 2 )
					{
						if( checkCraft.type.canFly && Math.abs(checkCraft.gear) == 3 )
						{
							if( (System.currentTimeMillis() - checkCraft.lastMove)/1000.0f >= 2.0 )
							{
								CraftMover cm = new CraftMover(checkCraft, instance);
								cm.moveUpdate();
								//System.out.println("Ship moveupdate="+ checkCraft.craftID);
							}
						}
					}
   				}else
   				{
	   				if( checkCraft.enginesOn )
	   				{		
						if( checkCraft.engineIDLocs.isEmpty() )
						{
							if( checkCraft.driverName != null )
							{
								Player p = instance.getServer().getPlayer(checkCraft.driverName);
								if( p != null )
								{
									p.sendMessage("Error: No engines detected! Check engine signs.");
								}
							}
							checkCraft.enginesOn = false;
							checkCraft.speed = 0;
						}else
						{
							for(int id: checkCraft.engineIDLocs.keySet())
							{
								checkCraft.engineIDIsOn.put(id, true);
								checkCraft.engineIDSetOn.put(id, true);
								CraftMover cm = new CraftMover(checkCraft, instance);
								cm.soundThread(checkCraft, id);
							}
						}
   			    
						checkCraft.isMoving = true;
	   				}
   				
	   				if( !checkCraft.recentlyUpdated && updateNum == 0 )
	    			{
	   					if( (System.currentTimeMillis() - checkCraft.lastUpdate)/1000 > 1 )
						{
		    				CraftMover cm = new CraftMover(checkCraft, instance);
		    				cm.structureUpdate(null,true);
		    				checkCraft.lastUpdate = System.currentTimeMillis();
						}
	    				//System.out.println("Ship structureupdate="+ checkCraft.craftID);
	    			}else if( updateNum == 0 )
	    			{
	    				checkCraft.recentlyUpdated = false;
	    			}
	   				
	   				if( checkCraft.doRemove )
					{
						checkCraft.remove();
					}else if( checkCraft.doDestroy )
					{
						checkCraft.destroy();
					}
   				}
				
				
			}
		}
   }

	public Craft createCraft(Player player, CraftType craftType, int x, int y, int z, String name, float dr, Block signBlock) {
		if (DebugMode == true && player != null)
			player.sendMessage("Attempting to create " + craftType.name
					+ "at coordinates " + x + ", "
					+ y + ", " + z);

		if (signBlock == null && player != null)
			signBlock = player.getLocation().getBlock();

		if (player != null) {
			Faction f = checkFactionLand(signBlock.getLocation());
			FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
			if (craftType.isBase && (f == null || !f.getFPlayers().contains(fplayer)))
				return null;

			if (craftType.isBase)
				name = f.getTag();
		}

		Craft craft = new Craft(craftType, player, name, dr, signBlock.getLocation(), this);

		Sign sign = (Sign) signBlock.getState();
		if (sign.getLine(3).equalsIgnoreCase("AUTO")) {
			craft.isAutoCraft = true;
			craft.isAICraft = true;
		} else if (sign.getLine(3).equalsIgnoreCase("MERCHANT")) {
			craft.isMerchantCraft = true;
			craft.isAICraft = true;
		}

		// auto-detect and create the craft
		if (!CraftBuilder.detect(craft, x, y, z)) {
			return null;
		}

		CraftMover cm = new CraftMover(craft, this);
		cm.structureUpdate(null, false);

		Craft.addCraftList.add(craft);
		//craft.cloneCraft();


		if (craft.type.canFly) {
			craft.type.maxEngineSpeed = 10;
		} else if (craft.type.isTerrestrial) {
			craft.type.maxEngineSpeed = 4;
		} else {
			craft.type.maxEngineSpeed = 6;
		}


		if (checkSpawnRegion(new Location(craft.world, craft.minX, craft.minY, craft.minZ)) || checkSpawnRegion(new Location(craft.world, craft.maxX, craft.maxY, craft.maxZ))) {
			craft.speedChange(player, true);
		}
		if (player != null) {
			craft.driverName = craft.captainName;
			if (craft.type.listenItem == true)
				player.sendMessage(ChatColor.GRAY + "With a gold sword in your hand, right-click in the direction you want to go.");
			if (craft.type.listenAnimation == true)
				player.sendMessage(ChatColor.GRAY + "Swing your arm in the direction you want to go.");
			if (craft.type.listenMovement == true)
				player.sendMessage(ChatColor.GRAY + "Move in the direction you want to go.");
		}
		if (craft.isAICraft && player == null || (player != null && PermissionInterface.CheckPerm(player, "navycraft.auto"))) {
			System.out.println("Starting AI ship at " + sign.getLocation());
			HashMap<Integer, Location> targetLocations = null;
			if (ConfigManager.routeData.getConfigurationSection("Routes." + craft.customName) != null) {
				targetLocations = new HashMap<>();
				List<String> list = new ArrayList<String>(ConfigManager.routeData.getConfigurationSection("Routes." + craft.customName).getKeys(false));
				for (String i : list) {
					if (!i.equalsIgnoreCase("doLoop") && !i.equalsIgnoreCase("doRam")) {
						int tx = ConfigManager.routeData.getInt("Routes." + craft.customName + "." + i + ".x");
						int ty = ConfigManager.routeData.getInt("Routes." + craft.customName + "." + i + ".y");
						int tz = ConfigManager.routeData.getInt("Routes." + craft.customName + "." + i + ".z");
						Location targetLocation = new Location(craft.world, tx, ty, tz);
						targetLocations.put(Integer.valueOf(i), targetLocation);
					}
				}
			}
			BukkitTask autoDriveTask = null;

			if (craft.isMerchantCraft)
				autoDriveTask = new AIMovement(craft, 0, false, null, targetLocations).runTaskTimer(this, 40, 40);
			if (craft.isAutoCraft)
				autoDriveTask = new AIMovement(craft, 1, true, null, targetLocations).runTaskTimer(this, 40, 40);

			AITasks.put(craft, autoDriveTask);
		}

		return craft;
	}

/*public static void explosion(int explosionRadius, Block warhead, boolean water)
{
	//destroyedBlocks list added by iiz for use in denying explosions more precisely
	HashMap<Location,Material> destroyedBlocks = new HashMap<>();
	
	// Getting the faction by FLocation/Location & tag
	Faction faction = null;

	// By FLocation/Location
	FLocation fLoc = new FLocation(warhead.getLocation());
	faction = Board.getInstance().getFactionAt(fLoc);
	
	Boolean allowed = faction.hasPlayersOnline();

	for (FPlayer player : faction.getFPlayers()) {
		if (player.getLastLoginTime() > System.currentTimeMillis()-3600000) {
			allowed = true;
			break;
		}
	}
	if (!faction.isWilderness() && !allowed)
		return;
	System.out.println("Faction: " + faction.getTag() + " Allowed explosion: " + allowed);
	
	//HashMap<String, org.bukkit.Chunk> protectedChunks = new HashMap<>();

	short powerMatrix[][][];
	powerMatrix = new short[explosionRadius*2+1][explosionRadius*2+1][explosionRadius*2+1];
	
	powerMatrix[explosionRadius][explosionRadius][explosionRadius] = (short)(explosionRadius*50);

	destroyedBlocks.put(warhead.getLocation(),warhead.getType());

	Material originalWarheadMaterial = warhead.getType();
	
	int refI=0;
	int refJ=0;
	int refK=0;
	int curX=0;		
	int curY=0;
	int curZ=0;
	int refX=0;		
	int refY=0;
	int refZ=0;
	boolean doPower=false;
	int fuseDelay = 5;
	
	
	for( int r=1; r<explosionRadius; r++ )
	{
		for( int j=-r; j<=r; j++ )
		{
			for( int i=-r; i<=r; i++ )
			{
				for( int k=-r; k<=r; k++ )
				{
					
					
					float refPowerMult = 1.0f;
					curX = i + explosionRadius;
					curY = j + explosionRadius;
					curZ = k + explosionRadius;
					
					if( powerMatrix[curX][curY][curZ] > 0 )
						continue;
					
					if( Math.abs(i) == r )//if on x edges
					{
						refI = (int)((Math.abs(i) - 1)*Math.signum(i));
						if( Math.abs(j) == r )//if on xy edges 
						{
							refJ = (int)((Math.abs(j) - 1)*Math.signum(j));
							if( Math.abs(k) == r && Math.abs(k) > 2) //corner piece, not innermost corners
							{
								refK = (int)((Math.abs(k) - 1)*Math.signum(k));
								refPowerMult = 0.10f;
							}else if( Math.abs(k) >= r - 2 && Math.abs(k) > 2 ) //near corner piece
							{
								refPowerMult = 0.25f;
							}else	//middle xy edge
							{
								refPowerMult = 0.40f;
							}
						}else if( Math.abs(k) == r ) //if on xz edges
						{
							refK = (int)((Math.abs(k) - 1)*Math.signum(k));
							refPowerMult = 0.40f;
							if( Math.abs(j) >= r - 2 && Math.abs(j) > 2 ) //near corner
								refPowerMult = 0.25f;
						}else if( Math.abs(j) == r - 1 )//if near xy edge
						{
							refPowerMult = 0.40f;
							if( Math.abs(k) >= r - 2 && Math.abs(k) > 2 ) //near xyz corner
								refPowerMult = 0.25f;
						}else if( Math.abs(k) == r - 1 )//if near xz edge
						{
							refPowerMult = 0.40f;
							if( Math.abs(j) >= r - 2 && Math.abs(j) > 2 )//near xyz corner
								refPowerMult = 0.25f;
						}else //somewhere else on x sides
						{
							refPowerMult = 0.60f;
						}
						doPower=true;
					}else if( Math.abs(j) == r )//if on y sides
					{
						refJ = (int)((Math.abs(j) - 1)*Math.signum(j));
						if( Math.abs(k) == r ) //if on yz edge
						{
							refK = (int)((Math.abs(k) - 1)*Math.signum(k));
							refPowerMult = 0.40f;
							if( Math.abs(i) >= r - 2 && Math.abs(i) > 2 )//near xyz corner
								refPowerMult = 0.25f;
						}else if( Math.abs(i) == r - 1 )//near xy edge
						{
							refPowerMult = 0.40f;
							if( Math.abs(k) >= r - 2 && Math.abs(k) > 2 )//near xyz corner
								refPowerMult = 0.25f;
						}else if( Math.abs(k) == r - 1 )//near yz edge
						{
							refPowerMult = 0.40f;
							if( Math.abs(i) >= r - 2 && Math.abs(i) > 2 )//near xyz corner
								refPowerMult = 0.25f;
						}else //somewhere else on y sides
						{
							refPowerMult = 0.60f;
						}
						doPower=true;
					}else if( Math.abs(k) == r )//if on z sides
					{
						refK = (int)((Math.abs(k) - 1)*Math.signum(k));
						if( Math.abs(i) >= r - 2 && Math.abs(i) > 2 )//near xz side
						{
							refPowerMult = 0.40f;
							if( Math.abs(j) >= r - 2 && Math.abs(j) > 2 )//near corner
								refPowerMult = 0.25f;
						}else if( Math.abs(j) == r - 1 )//near yz side
						{
							refPowerMult = 0.40f;
							if( Math.abs(i) >= r - 2 && Math.abs(i) > 2 )//near corner
								refPowerMult = 0.25f;
						}else //somewhere else on z sides
						{
							refPowerMult = 0.60f;
						}
						doPower=true;
					}
					
					if( doPower) {

						Location loc = warhead.getRelative(i,j,k).getLocation();
						if(!protectedChunks.containsKey(loc.getChunk().toString()))
							if (!kingdomsHandler.isVulnerableToWarhead(loc)) {
								protectedChunks.put(loc.getChunk().toString(), loc.getChunk());
								continue;
							}


						refX = refI + explosionRadius;
						refY = refJ + explosionRadius;
						refZ = refK + explosionRadius;
						
						Block theBlock = warhead.getRelative(i,j,k);
				    	int blockType = theBlock.getTypeId();

						destroyedBlocks.put(theBlock.getLocation(),theBlock.getType());

						short refPower = (short)(powerMatrix[refX][refY][refZ] * refPowerMult) ;
						if( refPower > 0 )
						{
							
							short blockResist;
							if( Craft.blockHardness(blockType) == 4 )
							{
								blockResist = -1;
							}else if( Craft.blockHardness(blockType) == 3 )//obsidian
					    	{
								blockResist = (short)(40 + 40*Math.random());
					    	}else if( Craft.blockHardness(blockType) == 2 )//iron
					    	{
					    		blockResist = (short)(20 + 20*Math.random());
					    	}else if( Craft.blockHardness(blockType) == 1 )//wood
					    	{
					    		blockResist = (short)(10 + 15*Math.random());
					    	}else if( Craft.blockHardness(blockType) == -3 )//water
					    	{
					    		blockResist=(short)(5+5*Math.random());
					    	}else
					    	{
					    		blockResist = (short)(2*Math.random());
					    	}
							
							if( Craft.blockHardness(blockType) == -1 )
							{
								theBlock.setType(Material.AIR);
								TNTPrimed tnt = (TNTPrimed)theBlock.getWorld().spawnEntity(new Location(theBlock.getWorld(), theBlock.getX(), theBlock.getY(), theBlock.getZ()), EntityType.PRIMED_TNT);
					    		tnt.setFuseTicks(fuseDelay);
								fuseDelay = fuseDelay + 2;
							}else if( Craft.blockHardness(blockType) == -2 )
							{
								theBlock.setType(Material.AIR);
								TNTPrimed tnt = (TNTPrimed)theBlock.getWorld().spawnEntity(new Location(theBlock.getWorld(), theBlock.getX(), theBlock.getY(), theBlock.getZ()), EntityType.PRIMED_TNT);
					    		tnt.setFuseTicks(fuseDelay);
					    		tnt.setYield(tnt.getYield()*0.5f);
								fuseDelay = fuseDelay + 2;
							}else
							{

								if( refPower > blockResist && blockResist >= 0 )
								{

										if( theBlock.getY() > 62 || water)
											theBlock.setType(Material.AIR);
										else
											theBlock.setType(Material.WATER);
									
								}else
								{
									refPower = 0;
								}
							}
							short newPower = (short)(refPower - blockResist);
							
							if( newPower < 5 )
							{
								powerMatrix[curX][curY][curZ] = 0;
							}else
							{
								powerMatrix[curX][curY][curZ] = newPower;
							}
						}
						
					} else
						warhead.setType(originalWarheadMaterial);
				}
			}
		}
	}
	warhead.getWorld().createExplosion(warhead.getLocation(), explosionRadius/2.0f);

	for (Location loc : destroyedBlocks.keySet()) {
	if (getCoreProtect() != null)
	getCoreProtect().logRemoval("explosion", loc, destroyedBlocks.get(loc), (byte) 0x0);
	}

	instance.getServer().getPluginManager().callEvent(new NavyCraftExplosionEvent(destroyedBlocks));
	}
	*/

public void waveUpdateScheduler()
{
	   this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
		   //	new Thread() {
		   //  @Override
		    public void run()
		    {
		    	waveCounter += 1;
		    	//int checkX = waveCounter % 250;
		    	int checkZ = (waveCounter) % 25;
		    	World world = instance.getServer().getWorld("ww1");
		    	

		    	for( int i = 0; i < 150; i++ )
		    	{
		    		//for( int j = 0; j < 10; j+=5 )
			    	//{
		    			int x = i;
		    			int z = checkZ*10 + ((waveCounter/25)%10)*5 + ((i/25)%2)*5;
			    		world.getBlockAt(-x, 63, -z).setType(Material.WATER);
			    		world.getBlockAt(-x, 63, -z+1).setType(Material.WATER);
			    		world.getBlockAt(-x, 63, -z-1).setType(Material.WATER);
			    		world.getBlockAt(-x, 64, -z).setType(Material.WATER);
			    		world.getBlockAt(-x, 62, -z).setType(Material.WATER);
			    		world.getBlockAt(-x, 62, -z+1).setType(Material.WATER);
			    		world.getBlockAt(-x, 62, -z-1).setType(Material.WATER);
			    		world.getBlockAt(-x, 62, -z-2).setType(Material.WATER);
			    		world.getBlockAt(-x, 62, -z+2).setType(Material.WATER);
			    		world.getBlockAt(-x, 63, -z+5).setType(Material.AIR);
			    		world.getBlockAt(-x, 63, -z+6).setType(Material.AIR);
			    		world.getBlockAt(-x, 63, -z+7).setType(Material.AIR);
			    		world.getBlockAt(-x, 63, -z+4).setType(Material.AIR);
			    		world.getBlockAt(-x, 63, -z+3).setType(Material.AIR);
			    		world.getBlockAt(-x, 64, -z+4).setType(Material.AIR);
			    		world.getBlockAt(-x, 64, -z+5).setType(Material.AIR);
			    		world.getBlockAt(-x, 64, -z+6).setType(Material.AIR);
			    		world.getBlockAt(-x, 62, -z+5).setType(Material.AIR);
			    		world.getBlockAt(-x, 62, -z+6).setType(Material.AIR);
			    		world.getBlockAt(-x, 62, -z+7).setType(Material.AIR);
			    		world.getBlockAt(-x, 62, -z+4).setType(Material.AIR);
			    		world.getBlockAt(-x, 62, -z+3).setType(Material.AIR);
			    		world.getBlockAt(-x, 61, -z+5).setType(Material.AIR);
			    		world.getBlockAt(-x, 61, -z+6).setType(Material.AIR);
			    		world.getBlockAt(-x, 61, -z+4).setType(Material.AIR);
			    		world.getBlockAt(-x, 60, -z+5).setType(Material.AIR);
			    	//}	
		    	}
		    }
		}, 4, 20);
	}

public static CoreProtectAPI getCoreProtect() {
    Plugin plugin = instance.getServer().getPluginManager().getPlugin("CoreProtect");
 
    // Check that CoreProtect is loaded
    if (plugin == null) {
    	System.out.println("1");
        return null;
    }

    // Check that the API is enabled
    CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
    if (CoreProtect.isEnabled() == false) {
    	System.out.println("2");
        return null;
    }

    return CoreProtect;
}

}
