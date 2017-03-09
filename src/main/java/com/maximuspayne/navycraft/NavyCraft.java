package com.maximuspayne.navycraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.maximuspayne.navycraft.config.ConfigFile;
import com.maximuspayne.navycraft.plugins.PermissionInterface;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


/**
 * MoveCraft plugin for Hey0 mod (hMod) by Yogoda
 * Ported to Bukkit by SycoPrime
 *
 * You are free to modify it for your own server
 * or use part of the code for your own plugins.
 * You don't need to credit me if you do, but I would appreciate it :)
 *
 * You are not allowed to distribute alternative versions of MoveCraft without my consent.
 * If you do cool modifications, please tell me so I can integrate it :)
 */

public class NavyCraft extends JavaPlugin {

	static final String pluginName = "NavyCraft";
	static String version;
	public static NavyCraft instance;

	public static Logger logger = Logger.getLogger("Minecraft");
	boolean DebugMode = false;

	public ConfigFile configFile;

	public static ArrayList<Player> aaGunnersList = new ArrayList<Player>();
	public static ArrayList<Skeleton> aaSkelesList = new ArrayList<Skeleton>();
	public static ArrayList<Egg> explosiveEggsList = new ArrayList<Egg>();
	public static HashMap<UUID, Player> shotTNTList = new HashMap<UUID, Player>();
	
	public static HashMap<String, Integer> playerScoresWW1 = new HashMap<String, Integer>();
	public static HashMap<String, Integer> playerScoresWW2 = new HashMap<String, Integer>();
	
	public final MoveCraft_PlayerListener playerListener = new MoveCraft_PlayerListener(this);
	public final MoveCraft_BlockListener blockListener = new MoveCraft_BlockListener(this);
	public final MoveCraft_EntityListener entityListener = new MoveCraft_EntityListener(this);
	public final MoveCraft_InventoryListener inventoryListener = new MoveCraft_InventoryListener(this);
	
	public static int battleMode=-1; //-1 false, 0 queue, 1 battle
	public static int battleType=-1;
	public static boolean battleLockTeams=false;
	public static ArrayList<String> bluePlayers = new ArrayList<String>();
	public static ArrayList<String> redPlayers = new ArrayList<String>();
	public static ArrayList<String> anyPlayers = new ArrayList<String>();
	public static ArrayList<String> playerKits = new ArrayList<String>();
	public static Location redSpawn;
	public static Location blueSpawn;
	public static int redPoints=0;
	public static int bluePoints=0;
	public static long battleStartTime;
	public static long battleLength;
	public static boolean redMerchant = false;
	public static boolean blueMerchant = false;
	
	public static enum battleTypes { battle1, battle2 }; //1

	public static Thread updateThread=null;
	public static Thread npcMerchantThread=null;
	public static boolean shutDown = false;
	
	public static WorldGuardPlugin wgp;
	
	public static ArrayList<Periscope> allPeriscopes = new ArrayList<Periscope>();
	
	public static HashMap<Player, Block> playerLastBoughtSign = new HashMap<Player, Block>();
	public static HashMap<Player, Integer> playerLastBoughtCost = new HashMap<Player, Integer>();
	public static HashMap<Player, String> playerLastBoughtSignString0 = new HashMap<Player, String>();
	public static HashMap<Player, String> playerLastBoughtSignString1 = new HashMap<Player, String>();
	public static HashMap<Player, String> playerLastBoughtSignString2 = new HashMap<Player, String>();
	public static HashMap<String, Long> playerPayDays = new HashMap<String, Long>();
	
	public static int spawnTime=10;
	
	public static HashMap<String, ArrayList<Sign>> playerDDSigns = new HashMap<String, ArrayList<Sign>>();
	public static HashMap<String, ArrayList<Sign>> playerSUB1Signs = new HashMap<String, ArrayList<Sign>>();
	public static HashMap<String, ArrayList<Sign>> playerCLSigns = new HashMap<String, ArrayList<Sign>>();
	public static HashMap<String, ArrayList<Sign>> playerSUB2Signs = new HashMap<String, ArrayList<Sign>>();
	public static HashMap<String, ArrayList<Sign>> playerCASigns = new HashMap<String, ArrayList<Sign>>();
	public static HashMap<String, ArrayList<Sign>> playerHANGAR1Signs = new HashMap<String, ArrayList<Sign>>();
	public static HashMap<String, ArrayList<Sign>> playerHANGAR2Signs = new HashMap<String, ArrayList<Sign>>();
	public static HashMap<String, ArrayList<Sign>> playerTANK1Signs = new HashMap<String, ArrayList<Sign>>();
	
	public static HashMap<String, Integer> playerDDRewards = new HashMap<String, Integer>();
	public static HashMap<String, Integer> playerSUB1Rewards = new HashMap<String, Integer>();
	public static HashMap<String, Integer> playerSUB2Rewards = new HashMap<String, Integer>();
	public static HashMap<String, Integer> playerCLRewards = new HashMap<String, Integer>();
	public static HashMap<String, Integer> playerCARewards = new HashMap<String, Integer>();
	public static HashMap<String, Integer> playerHANGAR1Rewards = new HashMap<String, Integer>();
	public static HashMap<String, Integer> playerHANGAR2Rewards = new HashMap<String, Integer>();
	public static HashMap<String, Integer> playerTANK1Rewards = new HashMap<String, Integer>();
	public static HashMap<Sign, Integer> playerSignIndex = new HashMap<Sign, Integer>();
	
	public static ArrayList<String> disableHiddenChats = new ArrayList<String>();
	public static HashMap<String, Integer> playerChatRegions = new HashMap<String, Integer>();
	
	public static HashMap<String, Integer> cleanupPlayerTimes = new HashMap<String, Integer>();
	public static ArrayList<String> cleanupPlayers = new ArrayList<String>();
	
	public static HashMap<String, Long> shipTPCooldowns = new HashMap<String, Long>();
	
	public static int schedulerCounter = 0;

	public void loadProperties() {
		configFile = new ConfigFile();

		File dir = getDataFolder();
		if (!dir.exists())
			dir.mkdir();

		CraftType.loadTypes(dir);
		//This setting was removed as of 0.6.9, craft type file creation has been commented out of the whole thing,
			//craft type files are to be distributed with the plugin 
		CraftType.saveTypes(dir);
		
		loadExperience();
		
	}
	
	public void onLoad() {
		
	}

	public void onEnable() {
		// getServer().getScheduler().scheduleSyncDelayedTask(this, loadSensors, 20*5);
		instance = this;

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(entityListener, this);
		pm.registerEvents(blockListener, this);
		pm.registerEvents(inventoryListener, this);
		
		/*pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_ANIMATION, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_EGG_THROW, playerListener, Priority.Normal, this);

		pm.registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		
		pm.registerEvent(Event.Type.BLOCK_PHYSICS, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Priority.Normal, this);
		
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);*/
		
		//pm.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, blockListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		version = pdfFile.getVersion();

		BlocksInfo.loadBlocksInfo();
		loadProperties();
		PermissionInterface.setupPermissions();
		
		PluginManager manager = getServer().getPluginManager();
		 
        manager.registerEvents(new TeleportFix(this, this.getServer()), this);
		
		structureUpdateScheduler();

		System.out.println(pdfFile.getName() + " " + version + " plugin enabled");
	}

	public void onDisable() {
		shutDown = true;
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " " + version + " plugin disabled");
	}

	/*public void unboardCraft(Player player, Craft craft) {
		if (craft != null && player != null) {
			craft.isNameOnBoard.put(player.getName(), false);
			//player.sendMessage(ChatColor.YELLOW + craft.type.sayOnRelease);
			
			if( craft.driverName == player.getName() )
			{
				player.sendMessage(ChatColor.YELLOW + "You release the helm");
				craft.releaseHelm();
			}
			
			
			boolean checkAbandon = true;
			for( String s : craft.crewNames )
			{
				Player p = this.getServer().getPlayer(s);
				if( p != null )
				{
					if( craft.isNameOnBoard.get(p.getName()) )
					{
						checkAbandon = false;
					}
				}
			}
			
			if( checkAbandon )
			{
				if( craft.customName != null )
				{
					if( craft.captainName != null )
						this.getServer().broadcastMessage(ChatColor.WHITE + craft.captainName + ChatColor.YELLOW + "'s " + ChatColor.WHITE + craft.customName + ChatColor.YELLOW + " was abandoned.");
					else
						this.getServer().broadcastMessage(ChatColor.YELLOW + "The " + ChatColor.WHITE + craft.customName + ChatColor.YELLOW + " was abandoned.");
				}else
				{
					if( craft.captainName != null )
						this.getServer().broadcastMessage(ChatColor.WHITE + craft.captainName + ChatColor.YELLOW + "'s " + ChatColor.WHITE + craft.name + ChatColor.YELLOW + " was abandoned.");
					else
						this.getServer().broadcastMessage(ChatColor.YELLOW + "The " + ChatColor.WHITE + craft.name + ChatColor.YELLOW + " was abandoned.");
				}
				craft.abandoned = true;
				craft.captainAbandoned = true;
				//craft.releaseCraft();
			}else if( craft.captainName == player.getName() )
			{
				craft.captainAbandoned = true;
				//craft.releaseCraft();
			}
		}
	}*/

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
		if(Integer.parseInt(this.ConfigSetting("LogLevel")) >= messageLevel)
			System.out.println(message);
		return this.DebugMode;
	}

	public Craft createCraft(Player player, CraftType craftType, int x, int y, int z, String name, float dr, Block signBlock, boolean autoShip) {
		//if( npcMerchantThread == null )
			//npcMerchantThread();
		
		if (DebugMode == true)
			player.sendMessage("Attempting to create " + craftType.name
					+ "at coordinates " + Integer.toString(x) + ", "
					+ Integer.toString(y) + ", " + Integer.toString(z));

		//Craft craft = Craft.getPlayerCraft(player);

		// release any old craft the player had
		//if (craft != null) {
		//	releaseCraft(player, craft);
		//}

		//float pRot = (float) Math.PI * player.getLocation().getYaw() / 180f;
		
		Craft craft = new Craft(craftType, player, name, dr, signBlock.getLocation(), this);

		
		// auto-detect and create the craft
		if (!CraftBuilder.detect(craft, x, y, z, autoShip)) {
			return null;
		}
		
		if( autoShip )
			craft.captainName = null;
		
		/*if(craft.engineBlocks.size() > 0)
			craft.timer = new MoveCraft_Timer(this, 0, craft, player, "engineCheck", false);
		else {
			if(craft.type.requiresRails) {
				//craft.railMove();
			}
		}*/
		
		if( !craft.redTeam && !craft.blueTeam )
		{
			if( checkTeamRegion(player.getLocation()) > 0 )
			{
				if( checkTeamRegion(player.getLocation()) == 1 )
				{
					craft.blueTeam = true;
					player.sendMessage(ChatColor.BLUE + "You start a blue team vehicle!");
				}
				else
				{
					craft.redTeam = true;
					player.sendMessage(ChatColor.RED + "You start a red team vehicle!");
				}
			}
		}
		
		CraftMover cm = new CraftMover(craft, this);
		cm.structureUpdate(null,false);


		Craft.addCraftList.add(craft);
		//craft.cloneCraft();
		
		
		if( craft.type.canFly )
		{
			craft.type.maxEngineSpeed = 10;
		}else if( craft.type.isTerrestrial )
		{
			craft.type.maxEngineSpeed = 4;
		}else
		{
			craft.type.maxEngineSpeed = 6;
		}
		
		
		if( checkSpawnRegion(new Location(craft.world, craft.minX, craft.minY, craft.minZ)) || checkSpawnRegion(new Location(craft.world, craft.maxX, craft.maxY, craft.maxZ)) )
		{
			craft.speedChange(player, true);
		}
		
		if( !autoShip )
		{
			craft.driverName = craft.captainName;
			if(craft.type.listenItem == true)
				player.sendMessage(ChatColor.GRAY + "With a gold sword in your hand, right-click in the direction you want to go.");
			if(craft.type.listenAnimation == true)
				player.sendMessage(ChatColor.GRAY + "Swing your arm in the direction you want to go.");
			if(craft.type.listenMovement == true)
				player.sendMessage(ChatColor.GRAY + "Move in the direction you want to go.");
		}
		return craft;
	}
	
    public static int checkTeamRegion(Location loc) /// 0 no region, 1 blue team, 2 red team
    {
    	
    	wgp = (WorldGuardPlugin) instance.getServer().getPluginManager().getPlugin("WorldGuard");
    	if( wgp != null && loc != null)
    	{
    		if( !loc.getWorld().getName().equalsIgnoreCase("warworld1") &&  !loc.getWorld().getName().equalsIgnoreCase("warworld2") &&  !loc.getWorld().getName().equalsIgnoreCase("warworld3") )
    		{
    			return 0;
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
					if( splits[1].equalsIgnoreCase("blue") )
						return 1;
					else if( splits[1].equalsIgnoreCase("red") )
						return 2;
					
				}
		
				
		    }
			return 0;
		}
    	return 0;
	}
    
    public static boolean checkStorageRegion(Location loc)
    {
    	
    	wgp = (WorldGuardPlugin) instance.getServer().getPluginManager().getPlugin("WorldGuard");
    	if( wgp != null && loc != null)
    	{
    		if( !loc.getWorld().getName().equalsIgnoreCase("warworld1") )
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
					if( splits[1].equalsIgnoreCase("storage") )
						return true;					
				}
		    }
			return false;
		}
    	return false;
	}
    
    public static boolean checkRepairRegion(Location loc)
    {
    	
    	wgp = (WorldGuardPlugin) instance.getServer().getPluginManager().getPlugin("WorldGuard");
    	if( wgp != null && loc != null)
    	{
    		if( !loc.getWorld().getName().equalsIgnoreCase("warworld1") && !loc.getWorld().getName().equalsIgnoreCase("warworld2") )
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
    		if( !loc.getWorld().getName().equalsIgnoreCase("warworld1") && !loc.getWorld().getName().equalsIgnoreCase("warworld2") )
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
    		if( !loc.getWorld().getName().equalsIgnoreCase("warworld1") )
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
    
    public static boolean checkSpawnRegion(Location loc)
    {
    	
    	wgp = (WorldGuardPlugin) instance.getServer().getPluginManager().getPlugin("WorldGuard");
    	if( wgp != null && loc != null)
    	{
    		if( !loc.getWorld().getName().equalsIgnoreCase("warworld1") && !loc.getWorld().getName().equalsIgnoreCase("warworld2") )
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
    		if( !loc.getWorld().getName().equalsIgnoreCase("warworld1") && !loc.getWorld().getName().equalsIgnoreCase("warworld2") )
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
	
	public String ConfigSetting(String setting) {
		if(configFile.ConfigSettings.containsKey(setting))
			return configFile.ConfigSettings.get(setting);
		else {
			System.out.println("Sycoprime needs to be notified that a non-existing config setting '" + setting + 
					"' was attempted to be accessed.");
			return "";
		}
	}

	public void dropItem(Block block) {		
		if(NavyCraft.instance.ConfigSetting("HungryHungryDrill").equalsIgnoreCase("true"))
			return;

		int itemToDrop = BlocksInfo.getDropItem(block.getTypeId());
		int quantity = BlocksInfo.getDropQuantity(block.getTypeId());

		if(itemToDrop != -1 && quantity != 0){

			for(int i=0; i<quantity; i++){
				block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(itemToDrop, 1));
			}
		}
	}
	
	@SuppressWarnings("resource")
	public static void loadExperience()
	{
		String path = File.separator + "PlayerExpWW1.txt";
        File file = new File(path);
        FileReader fr;
        BufferedReader reader;
		try {
			fr = new FileReader(file.getName());
			reader = new BufferedReader(fr);

			
	        String line = null;
	        
	        
	        try {
	        	playerScoresWW1.clear();
	        	
				while ((line=reader.readLine()) != null) {
					String[] strings = line.split(",");
					if( strings.length != 2 )
					{
						System.out.println("Player EXP Load Error3");
						return;
					}
					
					playerScoresWW1.put(strings[0], Integer.valueOf(strings[1]));
					
				}

				
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Player EXP Load Error2");
				return;
			}
	        
	        reader.close();  // Close to unlock.

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Player EXP Load Error1");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Player EXP Load Error4");
			return;
		}
		
		
		
		String path2 = File.separator + "PlayerExpWW2.txt";
        File file2 = new File(path2);
        FileReader fr2;
        BufferedReader reader2;
		try {
			fr2 = new FileReader(file2.getName());
			reader2 = new BufferedReader(fr2);

			
	        String line2 = null;
	        
	        
	        try {
	        	playerScoresWW2.clear();
	        	
				while ((line2=reader2.readLine()) != null) {
					String[] strings = line2.split(",");
					if( strings.length != 2 )
					{
						System.out.println("Player EXP Load Error5");
						return;
					}
					
					playerScoresWW2.put(strings[0], Integer.valueOf(strings[1]));
					
				}

				
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Player EXP Load Error6");
				return;
			}
	        
	        reader2.close();  // Close to unlock.

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Player EXP Load Error7");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Player EXP Load Error8");
			return;
		}
	}
	
	@SuppressWarnings("resource")
	public static void saveExperience()
	{
		String path = File.separator + "PlayerExpWW1.txt";
        File file = new File(path);
        FileWriter fw;
        BufferedWriter writer;
	
		try {
			fw = new FileWriter(file.getName());
			writer = new BufferedWriter(fw);
	        String line = null;
	        
	        if( playerScoresWW1.isEmpty() )
	        {
	        	System.out.println("Player Save Exp Error1");
	        	return;
	        }
	        
			for( String s : playerScoresWW1.keySet() )
			{
				line = s + "," + playerScoresWW1.get(s).toString();
				try {
					writer.write(line);
					writer.newLine();
				} catch (IOException e) {
					System.out.println("Player Save Exp Error2");
					e.printStackTrace();
					return;
				}
				
				
			}
			
			writer.close();
		} catch (IOException e2) {
			System.out.println("Player Save Exp Error4");
			e2.printStackTrace();
			return;
		}
		
		
		String path2 = File.separator + "PlayerExpWW2.txt";
        File file2 = new File(path2);
        FileWriter fw2;
        BufferedWriter writer2;
	
		try {
			fw2 = new FileWriter(file2.getName());
			writer2 = new BufferedWriter(fw2);
	        String line = null;
	        
	        if( playerScoresWW2.isEmpty() )
	        {
	        	System.out.println("Player Save Exp Error5");
	        	return;
	        }
	        
			for( String s : playerScoresWW2.keySet() )
			{
				line = s + "," + playerScoresWW2.get(s).toString();
				try {
					writer2.write(line);
					writer2.newLine();
				} catch (IOException e) {
					System.out.println("Player Save Exp Error6");
					e.printStackTrace();
					return;
				}
				
				
			}
			
			writer2.close();
		} catch (IOException e2) {
			System.out.println("Player Save Exp Error7");
			e2.printStackTrace();
			return;
		}

	}
	
	
	/*public void structureUpdateThread(){
    	//final int taskNum;
    	//int taskNum = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
		updateThread = new Thread(){
			
    	@Override
			public void run() {
    		
    		setPriority(Thread.MIN_PRIORITY);
    		
			try{
				int i=0;
				while(!shutDown)
				{
					sleep(2000);
					structureUpdateUpdate(i);
					i++;
				}
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			}
    	}; //, 20L);
    	updateThread.start();
    }
	
   public void structureUpdateUpdate(final int i)
    {
    	this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
    	//new Thread() {
	  //  @Override
		    public void run()
		    {
		    	if( Craft.craftList == null || Craft.craftList.isEmpty() )
		    	{
		    		return;
		    	}
		    	int vehicleCount = Craft.craftList.size();
		    	if( vehicleCount > 0 )
		    	{
		    		int vehicleNum = i % vehicleCount;
		    		if( Craft.craftList.get(vehicleNum) != null )
		    		{
		    			Craft checkCraft = Craft.craftList.get(vehicleNum);
		    			if( ((System.currentTimeMillis() - checkCraft.lastUpdate) / 1000) > 10 )
		    			{
		    				CraftMover cm = new CraftMover(checkCraft, instance);
		    				cm.structureUpdate(null);
		    			}
		    		}
		    	}
		    }
    	}
    	);
	 }*/
   
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
	    		
	    		/*if( vehicleCount < 8 && vehicleCount >= 4 )
	    		{
	    			if (((schedulerCounter/4) / vehicleCount)%3 == 0)
	    			{
	    				updateCraft(vehicleNum,updateNum);
	    			}
	    			schedulerCounter++;
	    		}else if( vehicleCount < 4 && vehicleCount > 1 )
	    		{
	    			if (((schedulerCounter/4) / vehicleCount)%5 == 0)
	    			{
	    				updateCraft(vehicleNum,updateNum);
	    			}
	    			schedulerCounter++;
	    		}else if( vehicleCount == 1 )
	    		{
	    			if (((schedulerCounter/4) / vehicleCount)%7 == 0)
	    			{
	    				updateCraft(vehicleNum,updateNum);
	    			}
	    			schedulerCounter++;
	    		}else */
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
					/*if( checkCraft.moveTicker > 0 )
					{
						CraftMover cm = new CraftMover(checkCraft, instance);
						cm.moveUpdate();
						//System.out.println("Ship moveupdate="+ checkCraft.craftID +",ticker=" + checkCraft.moveTicker);
					}else*/
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
								checkCraft.engineIDOn.put(id, true);
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
   
	public void npcMerchantThread()
	{
    	//final int taskNum;
    	//int taskNum = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
		npcMerchantThread = new Thread(){
			
    	@Override
			public void run() {
    		
    		setPriority(Thread.MIN_PRIORITY);
    		
			try{
				int i=0;
				sleep(30000);
				while(!shutDown)
				{
					npcMerchantUpdate(i);
					i++;
					
					sleep(spawnTime*60000);
				}
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			}
    	}; //, 20L);
    	npcMerchantThread.start();
    }
	
   public void npcMerchantUpdate(final int i)
   {
    	this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
    	//new Thread() {
	  //  @Override
		    public void run()
		    {
		    	if( NavyCraft.shutDown )
					return;
		    	
		    	MoveCraft_BlockListener.autoSpawnSign(null, "");
		    }
    	}
    	);
	}
   
	//@SuppressWarnings("resource")
	public static void loadRewardsFile()
	{
		String path = File.separator + "PlayerPlotRewards.txt";
        File file = new File(path);
        FileReader fr;
        BufferedReader reader;
		try {
			fr = new FileReader(file.getName());
			reader = new BufferedReader(fr);

	        String line = null;
	        
	        try {
	        	
				while ((line=reader.readLine()) != null) 
				{
					String[] strings = line.split(",");
					if( strings.length != 4 )
					{
						System.out.println("Player Reward Load Error1");
						reader.close(); 
						return;
					}
					
					if( strings[1].equalsIgnoreCase("dd") || strings[1].equalsIgnoreCase("ship1") )
					{
						if( playerDDRewards.containsKey(strings[0]) )
							 playerDDRewards.put(strings[0], playerDDRewards.get(strings[0]) + 1);
						else
							 playerDDRewards.put(strings[0], 1);
					}else if( strings[1].equalsIgnoreCase("sub1") || strings[1].equalsIgnoreCase("ship2") )
					{
						{
							if( playerSUB1Rewards.containsKey(strings[0]) )
								playerSUB1Rewards.put(strings[0], playerSUB1Rewards.get(strings[0]) + 1);
							else
								playerSUB1Rewards.put(strings[0], 1);
						}
					}else if( strings[1].equalsIgnoreCase("sub2") || strings[1].equalsIgnoreCase("ship3") )
					{
						{
							if( playerSUB2Rewards.containsKey(strings[0]) )
								playerSUB2Rewards.put(strings[0], playerSUB2Rewards.get(strings[0]) + 1);
							else
								playerSUB2Rewards.put(strings[0], 1);
						}
					}else if( strings[1].equalsIgnoreCase("cl") || strings[1].equalsIgnoreCase("ship4") )
					{
						{
							if( playerCLRewards.containsKey(strings[0]) )
								playerCLRewards.put(strings[0], playerCLRewards.get(strings[0]) + 1);
							else
								playerCLRewards.put(strings[0], 1);
						}
					}else if( strings[1].equalsIgnoreCase("ca") || strings[1].equalsIgnoreCase("ship5") )
					{
						{
							if( playerCARewards.containsKey(strings[0]) )
								playerCARewards.put(strings[0], playerCARewards.get(strings[0]) + 1);
							else
								playerCARewards.put(strings[0], 1);
						}
					}else if( strings[1].equalsIgnoreCase("hangar1") )
					{
						{
							if( playerHANGAR1Rewards.containsKey(strings[0]) )
								playerHANGAR1Rewards.put(strings[0], playerHANGAR1Rewards.get(strings[0]) + 1);
							else
								playerHANGAR1Rewards.put(strings[0], 1);
						}
					}else if( strings[1].equalsIgnoreCase("hangar2") )
					{
						{
							if( playerHANGAR2Rewards.containsKey(strings[0]) )
								playerHANGAR2Rewards.put(strings[0], playerHANGAR2Rewards.get(strings[0]) + 1);
							else
								playerHANGAR2Rewards.put(strings[0], 1);
						}
					}else if( strings[1].equalsIgnoreCase("tank1") )
					{
						{
							if( playerTANK1Rewards.containsKey(strings[0]) )
								playerTANK1Rewards.put(strings[0], playerTANK1Rewards.get(strings[0]) + 1);
							else
								playerTANK1Rewards.put(strings[0], 1);
						}
					}else
					{
						System.out.println("Player Reward Load Error:Unknown Reward");
					}
					
				}

				
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Player Reward Load Error2");
				return;
			}
	        
	        reader.close();  // Close to unlock.

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Player Reward Load Error3");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Player Reward Load Error4");
			return;
		}
	}
	
	public static void saveRewardsFile(String s)
	{
		String path = File.separator + "PlayerPlotRewards.txt";
        File file = new File(path);
        FileWriter fw;
        BufferedWriter writer;
	
		try {
			fw = new FileWriter(file.getName(), true);
			writer = new BufferedWriter(fw);
	        
			try {
				writer.write(s);
				writer.newLine();
			} catch (IOException e) {
				System.out.println("Player Save Reward Error1");
				e.printStackTrace();
				writer.close();
				return;
			}
			
			writer.close();
		} catch (IOException e2) {
			System.out.println("Player Save Reward Error2");
			e2.printStackTrace();
			return;
		}
	}
	
	public static void explosion(int explosionRadius, Block warhead)
	{
		short powerMatrix[][][];
		powerMatrix = new short[explosionRadius*2+1][explosionRadius*2+1][explosionRadius*2+1];
		
		powerMatrix[explosionRadius][explosionRadius][explosionRadius] = (short)(explosionRadius*50);
		
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
						if( Math.abs(i) == r )
						{
							refI = (int)((Math.abs(i) - 1)*Math.signum(i));
							if( Math.abs(j) == r )
							{
								refJ = (int)((Math.abs(j) - 1)*Math.signum(j));
								if( Math.abs(k) == r )
								{
									refK = (int)((Math.abs(k) - 1)*Math.signum(k));
									refPowerMult = 0.14f;
								}else if( Math.abs(k) == r - 1 )
								{
									refPowerMult = 0.14f;
								}else	
								{
									refPowerMult = 0.33f;
								}
							}else if( Math.abs(k) == r )
							{
								refK = (int)((Math.abs(k) - 1)*Math.signum(k));
								refPowerMult = 0.33f;
								if( Math.abs(j) == r - 1 )
									refPowerMult = 0.14f;
							}else if( Math.abs(j) == r - 1 )
							{
								refPowerMult = 0.33f;
								if( Math.abs(k) == r - 1 )
									refPowerMult = 0.14f;
							}else if( Math.abs(k) == r - 1 )
							{
								refPowerMult = 0.33f;
								if( Math.abs(j) == r - 1 )
									refPowerMult = 0.14f;
							}
							doPower=true;
						}else if( Math.abs(j) == r )
						{
							refJ = (int)((Math.abs(j) - 1)*Math.signum(j));
							if( Math.abs(k) == r )
							{
								refK = (int)((Math.abs(k) - 1)*Math.signum(k));
								refPowerMult = 0.33f;
								if( Math.abs(i) == r - 1 )
									refPowerMult = 0.14f;
							}else if( Math.abs(i) == r - 1 )
							{
								refPowerMult = 0.33f;
								if( Math.abs(k) == r - 1 )
									refPowerMult = 0.14f;
							}else if( Math.abs(k) == r - 1 )
							{
								refPowerMult = 0.33f;
								if( Math.abs(i) == r - 1 )
									refPowerMult = 0.14f;
							}
							doPower=true;
						}else if( Math.abs(k) == r )
						{
							refK = (int)((Math.abs(k) - 1)*Math.signum(k));
							if( Math.abs(i) == r - 1 )
							{
								refPowerMult = 0.33f;
								if( Math.abs(j) == r - 1 )
									refPowerMult = 0.14f;
							}else if( Math.abs(j) == r - 1 )
							{
								refPowerMult = 0.33f;
								if( Math.abs(i) == r - 1 )
									refPowerMult = 0.14f;
							}
							doPower=true;
						}
						
						if( doPower )
						{
							curX = i + explosionRadius;
							curY = j + explosionRadius;
							curZ = k + explosionRadius;
							refX = refI + explosionRadius;
							refY = refJ + explosionRadius;
							refZ = refK + explosionRadius;
							
							Block theBlock = warhead.getRelative(i,j,k);
					    	int blockType = theBlock.getTypeId();
							
							short refPower = (short)(powerMatrix[refX][refY][refZ] * refPowerMult) ;
							if( refPower > 0 )
							{
								
								short blockResist;
								if( Craft.blockHardness(blockType) == 4 )
								{
									blockResist = -1;
								}else if( Craft.blockHardness(blockType) == 3 )
						    	{
									blockResist = (short)(40 + 40*Math.random());
						    	}else if( Craft.blockHardness(blockType) == 2 )
						    	{
						    		blockResist = (short)(20 + 20*Math.random());
						    	}else if( Craft.blockHardness(blockType) == 1 )
						    	{
						    		blockResist = (short)(10 + 15*Math.random());
						    	}else if( Craft.blockHardness(blockType) == -3 )
						    	{
						    		blockResist=(short)(10+10*Math.random());
						    	}else
						    	{
						    		blockResist = (short)(5+5*Math.random());
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
										if( theBlock.getY() > 62 )
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
							
						}
					}
				}
			}
		}
		warhead.getWorld().createExplosion(warhead.getLocation(), explosionRadius);
		/*for( int i = 0; i < explosionRadius*2+1; i++ )
		{
			String kString = "";
			for( int k = 0; k < explosionRadius*2+1; k++ )
			{
				kString += powerMatrix[i][explosionRadius+1][k] + " ";
			}
			instance.getServer().broadcastMessage(kString);
		}*/
	}
}