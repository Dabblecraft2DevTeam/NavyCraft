/*      */ package com.maximuspayne.navycraft;
/*      */ 
/*      */ import com.maximuspayne.navycraft.config.ConfigFile;
/*      */ import com.maximuspayne.navycraft.plugins.PermissionInterface;
/*      */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*      */ import com.sk89q.worldguard.protection.ApplicableRegionSet;
/*      */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*      */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileReader;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Set;
/*      */ import java.util.UUID;
/*      */ import java.util.logging.Logger;
/*      */ import org.bukkit.ChatColor;
/*      */ import org.bukkit.Location;
/*      */ import org.bukkit.Material;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.block.Block;
/*      */ import org.bukkit.block.Sign;
/*      */ import org.bukkit.entity.Egg;
/*      */ import org.bukkit.entity.EntityType;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.entity.Skeleton;
/*      */ import org.bukkit.entity.TNTPrimed;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.plugin.PluginDescriptionFile;
/*      */ import org.bukkit.plugin.PluginManager;
/*      */ import org.bukkit.plugin.java.JavaPlugin;
/*      */ import org.bukkit.scheduler.BukkitScheduler;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class NavyCraft
/*      */   extends JavaPlugin
/*      */ {
/*      */   static final String pluginName = "NavyCraft";
/*      */   static String version;
/*      */   public static NavyCraft instance;
/*   58 */   public static Logger logger = Logger.getLogger("Minecraft");
/*   59 */   boolean DebugMode = false;
/*      */   
/*      */   public ConfigFile configFile;
/*      */   
/*   63 */   public static ArrayList<Player> aaGunnersList = new ArrayList();
/*   64 */   public static ArrayList<Skeleton> aaSkelesList = new ArrayList();
/*   65 */   public static ArrayList<Egg> explosiveEggsList = new ArrayList();
/*   66 */   public static HashMap<UUID, Player> shotTNTList = new HashMap();
/*      */   
/*   68 */   public static HashMap<String, Integer> playerScoresWW1 = new HashMap();
/*   69 */   public static HashMap<String, Integer> playerScoresWW2 = new HashMap();
/*      */   
/*   71 */   public final MoveCraft_PlayerListener playerListener = new MoveCraft_PlayerListener(this);
/*   72 */   public final MoveCraft_BlockListener blockListener = new MoveCraft_BlockListener(this);
/*   73 */   public final MoveCraft_EntityListener entityListener = new MoveCraft_EntityListener(this);
/*   74 */   public final MoveCraft_InventoryListener inventoryListener = new MoveCraft_InventoryListener(this);
/*      */   
/*   76 */   public static int battleMode = -1;
/*   77 */   public static int battleType = -1;
/*   78 */   public static boolean battleLockTeams = false;
/*   79 */   public static ArrayList<String> bluePlayers = new ArrayList();
/*   80 */   public static ArrayList<String> redPlayers = new ArrayList();
/*   81 */   public static ArrayList<String> anyPlayers = new ArrayList();
/*   82 */   public static ArrayList<String> playerKits = new ArrayList();
/*      */   public static Location redSpawn;
/*      */   public static Location blueSpawn;
/*   85 */   public static int redPoints = 0;
/*   86 */   public static int bluePoints = 0;
/*      */   public static long battleStartTime;
/*      */   public static long battleLength;
/*   89 */   public static boolean redMerchant = false;
/*   90 */   public static boolean blueMerchant = false;
/*      */   
/*   92 */   public static enum battleTypes { battle1,  battle2; }
/*      */   
/*   94 */   public static Thread updateThread = null;
/*   95 */   public static Thread npcMerchantThread = null;
/*   96 */   public static boolean shutDown = false;
/*      */   
/*      */   public static WorldGuardPlugin wgp;
/*      */   
/*  100 */   public static ArrayList<Periscope> allPeriscopes = new ArrayList();
/*      */   
/*  102 */   public static HashMap<Player, Block> playerLastBoughtSign = new HashMap();
/*  103 */   public static HashMap<Player, Integer> playerLastBoughtCost = new HashMap();
/*  104 */   public static HashMap<Player, String> playerLastBoughtSignString0 = new HashMap();
/*  105 */   public static HashMap<Player, String> playerLastBoughtSignString1 = new HashMap();
/*  106 */   public static HashMap<Player, String> playerLastBoughtSignString2 = new HashMap();
/*  107 */   public static HashMap<String, Long> playerPayDays = new HashMap();
/*      */   
/*  109 */   public static int spawnTime = 10;
/*      */   
/*  111 */   public static HashMap<String, ArrayList<Sign>> playerDDSigns = new HashMap();
/*  112 */   public static HashMap<String, ArrayList<Sign>> playerSUB1Signs = new HashMap();
/*  113 */   public static HashMap<String, ArrayList<Sign>> playerCLSigns = new HashMap();
/*  114 */   public static HashMap<String, ArrayList<Sign>> playerSUB2Signs = new HashMap();
/*  115 */   public static HashMap<String, ArrayList<Sign>> playerCASigns = new HashMap();
/*  116 */   public static HashMap<String, ArrayList<Sign>> playerHANGAR1Signs = new HashMap();
/*  117 */   public static HashMap<String, ArrayList<Sign>> playerHANGAR2Signs = new HashMap();
/*  118 */   public static HashMap<String, ArrayList<Sign>> playerTANK1Signs = new HashMap();
/*      */   
/*  120 */   public static HashMap<String, Integer> playerDDRewards = new HashMap();
/*  121 */   public static HashMap<String, Integer> playerSUB1Rewards = new HashMap();
/*  122 */   public static HashMap<String, Integer> playerSUB2Rewards = new HashMap();
/*  123 */   public static HashMap<String, Integer> playerCLRewards = new HashMap();
/*  124 */   public static HashMap<String, Integer> playerCARewards = new HashMap();
/*  125 */   public static HashMap<String, Integer> playerHANGAR1Rewards = new HashMap();
/*  126 */   public static HashMap<String, Integer> playerHANGAR2Rewards = new HashMap();
/*  127 */   public static HashMap<String, Integer> playerTANK1Rewards = new HashMap();
/*  128 */   public static HashMap<Sign, Integer> playerSignIndex = new HashMap();
/*      */   
/*  130 */   public static ArrayList<String> disableHiddenChats = new ArrayList();
/*  131 */   public static HashMap<String, Integer> playerChatRegions = new HashMap();
/*      */   
/*  133 */   public static HashMap<String, Integer> cleanupPlayerTimes = new HashMap();
/*  134 */   public static ArrayList<String> cleanupPlayers = new ArrayList();
/*      */   
/*  136 */   public static HashMap<String, Long> shipTPCooldowns = new HashMap();
/*      */   
/*  138 */   public static int schedulerCounter = 0;
/*      */   
/*      */   public void loadProperties() {
/*  141 */     this.configFile = new ConfigFile();
/*      */     
/*  143 */     File dir = getDataFolder();
/*  144 */     if (!dir.exists()) {
/*  145 */       dir.mkdir();
/*      */     }
/*  147 */     CraftType.loadTypes(dir);
/*      */     
/*      */ 
/*  150 */     CraftType.saveTypes(dir);
/*      */     
/*  152 */     loadExperience();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void onLoad() {}
/*      */   
/*      */ 
/*      */   public void onEnable()
/*      */   {
/*  162 */     instance = this;
/*      */     
/*  164 */     PluginManager pm = getServer().getPluginManager();
/*  165 */     pm.registerEvents(this.playerListener, this);
/*  166 */     pm.registerEvents(this.entityListener, this);
/*  167 */     pm.registerEvents(this.blockListener, this);
/*  168 */     pm.registerEvents(this.inventoryListener, this);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  188 */     PluginDescriptionFile pdfFile = getDescription();
/*  189 */     version = pdfFile.getVersion();
/*      */     
/*  191 */     BlocksInfo.loadBlocksInfo();
/*  192 */     loadProperties();
/*  193 */     PermissionInterface.setupPermissions();
/*      */     
/*  195 */     PluginManager manager = getServer().getPluginManager();
/*      */     
/*  197 */     manager.registerEvents(new TeleportFix(this, getServer()), this);
/*      */     
/*  199 */     structureUpdateScheduler();
/*      */     
/*  201 */     System.out.println(pdfFile.getName() + " " + version + " plugin enabled");
/*      */   }
/*      */   
/*      */   public void onDisable() {
/*  205 */     shutDown = true;
/*  206 */     PluginDescriptionFile pdfFile = getDescription();
/*  207 */     System.out.println(pdfFile.getName() + " " + version + " plugin disabled");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void ToggleDebug()
/*      */   {
/*  262 */     this.DebugMode = (!this.DebugMode);
/*  263 */     System.out.println("Debug mode set to " + this.DebugMode);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean DebugMessage(String message, int messageLevel)
/*      */   {
/*  277 */     if (Integer.parseInt(ConfigSetting("LogLevel")) >= messageLevel)
/*  278 */       System.out.println(message);
/*  279 */     return this.DebugMode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Craft createCraft(Player player, CraftType craftType, int x, int y, int z, String name, float dr, Block signBlock, boolean autoShip)
/*      */   {
/*  286 */     if (this.DebugMode) {
/*  287 */       player.sendMessage("Attempting to create " + craftType.name + 
/*  288 */         "at coordinates " + Integer.toString(x) + ", " + 
/*  289 */         Integer.toString(y) + ", " + Integer.toString(z));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  300 */     Craft craft = new Craft(craftType, player, name, dr, signBlock.getLocation(), this);
/*      */     
/*      */ 
/*      */ 
/*  304 */     if (!CraftBuilder.detect(craft, x, y, z, autoShip)) {
/*  305 */       return null;
/*      */     }
/*      */     
/*  308 */     if (autoShip) {
/*  309 */       craft.captainName = null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  319 */     if ((!craft.redTeam) && (!craft.blueTeam))
/*      */     {
/*  321 */       if (checkTeamRegion(player.getLocation()) > 0)
/*      */       {
/*  323 */         if (checkTeamRegion(player.getLocation()) == 1)
/*      */         {
/*  325 */           craft.blueTeam = true;
/*  326 */           player.sendMessage(ChatColor.BLUE + "You start a blue team vehicle!");
/*      */         }
/*      */         else
/*      */         {
/*  330 */           craft.redTeam = true;
/*  331 */           player.sendMessage(ChatColor.RED + "You start a red team vehicle!");
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  336 */     CraftMover cm = new CraftMover(craft, this);
/*  337 */     cm.structureUpdate(null, false);
/*      */     
/*      */ 
/*  340 */     Craft.addCraftList.add(craft);
/*      */     
/*      */ 
/*      */ 
/*  344 */     if (craft.type.canFly)
/*      */     {
/*  346 */       craft.type.maxEngineSpeed = 10;
/*  347 */     } else if (craft.type.isTerrestrial)
/*      */     {
/*  349 */       craft.type.maxEngineSpeed = 4;
/*      */     }
/*      */     else {
/*  352 */       craft.type.maxEngineSpeed = 6;
/*      */     }
/*      */     
/*      */ 
/*  356 */     if ((checkSpawnRegion(new Location(craft.world, craft.minX, craft.minY, craft.minZ))) || (checkSpawnRegion(new Location(craft.world, craft.maxX, craft.maxY, craft.maxZ))))
/*      */     {
/*  358 */       craft.speedChange(player, true);
/*      */     }
/*      */     
/*  361 */     if (!autoShip)
/*      */     {
/*  363 */       craft.driverName = craft.captainName;
/*  364 */       if (craft.type.listenItem)
/*  365 */         player.sendMessage(ChatColor.GRAY + "With a gold sword in your hand, right-click in the direction you want to go.");
/*  366 */       if (craft.type.listenAnimation)
/*  367 */         player.sendMessage(ChatColor.GRAY + "Swing your arm in the direction you want to go.");
/*  368 */       if (craft.type.listenMovement)
/*  369 */         player.sendMessage(ChatColor.GRAY + "Move in the direction you want to go.");
/*      */     }
/*  371 */     return craft;
/*      */   }
/*      */   
/*      */ 
/*      */   public static int checkTeamRegion(Location loc)
/*      */   {
/*  377 */     wgp = (WorldGuardPlugin)instance.getServer().getPluginManager().getPlugin("WorldGuard");
/*  378 */     if ((wgp != null) && (loc != null))
/*      */     {
/*  380 */       if ((!loc.getWorld().getName().equalsIgnoreCase("warworld1")) && (!loc.getWorld().getName().equalsIgnoreCase("warworld2")) && (!loc.getWorld().getName().equalsIgnoreCase("warworld3")))
/*      */       {
/*  382 */         return 0;
/*      */       }
/*  384 */       RegionManager regionManager = wgp.getRegionManager(loc.getWorld());
/*      */       
/*  386 */       ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
/*      */       
/*  388 */       Iterator<ProtectedRegion> it = set.iterator();
/*  389 */       while (it.hasNext())
/*      */       {
/*  391 */         String id = ((ProtectedRegion)it.next()).getId();
/*  392 */         String[] splits = id.split("_");
/*  393 */         if (splits.length == 2)
/*      */         {
/*  395 */           if (splits[1].equalsIgnoreCase("blue"))
/*  396 */             return 1;
/*  397 */           if (splits[1].equalsIgnoreCase("red")) {
/*  398 */             return 2;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  404 */       return 0;
/*      */     }
/*  406 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean checkStorageRegion(Location loc)
/*      */   {
/*  412 */     wgp = (WorldGuardPlugin)instance.getServer().getPluginManager().getPlugin("WorldGuard");
/*  413 */     if ((wgp != null) && (loc != null))
/*      */     {
/*  415 */       if (!loc.getWorld().getName().equalsIgnoreCase("warworld1"))
/*      */       {
/*  417 */         return false;
/*      */       }
/*  419 */       RegionManager regionManager = wgp.getRegionManager(loc.getWorld());
/*      */       
/*  421 */       ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
/*      */       
/*  423 */       Iterator<ProtectedRegion> it = set.iterator();
/*  424 */       while (it.hasNext())
/*      */       {
/*  426 */         String id = ((ProtectedRegion)it.next()).getId();
/*  427 */         String[] splits = id.split("_");
/*  428 */         if (splits.length == 2)
/*      */         {
/*  430 */           if (splits[1].equalsIgnoreCase("storage"))
/*  431 */             return true;
/*      */         }
/*      */       }
/*  434 */       return false;
/*      */     }
/*  436 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean checkRepairRegion(Location loc)
/*      */   {
/*  442 */     wgp = (WorldGuardPlugin)instance.getServer().getPluginManager().getPlugin("WorldGuard");
/*  443 */     if ((wgp != null) && (loc != null))
/*      */     {
/*  445 */       if ((!loc.getWorld().getName().equalsIgnoreCase("warworld1")) && (!loc.getWorld().getName().equalsIgnoreCase("warworld2")))
/*      */       {
/*  447 */         return false;
/*      */       }
/*  449 */       RegionManager regionManager = wgp.getRegionManager(loc.getWorld());
/*      */       
/*  451 */       ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
/*      */       
/*  453 */       Iterator<ProtectedRegion> it = set.iterator();
/*  454 */       while (it.hasNext())
/*      */       {
/*  456 */         String id = ((ProtectedRegion)it.next()).getId();
/*  457 */         String[] splits = id.split("_");
/*  458 */         if (splits.length == 2)
/*      */         {
/*  460 */           if (splits[1].equalsIgnoreCase("repair"))
/*  461 */             return true;
/*      */         }
/*      */       }
/*  464 */       return false;
/*      */     }
/*  466 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean checkSafeDockRegion(Location loc)
/*      */   {
/*  472 */     wgp = (WorldGuardPlugin)instance.getServer().getPluginManager().getPlugin("WorldGuard");
/*  473 */     if ((wgp != null) && (loc != null))
/*      */     {
/*  475 */       if ((!loc.getWorld().getName().equalsIgnoreCase("warworld1")) && (!loc.getWorld().getName().equalsIgnoreCase("warworld2")))
/*      */       {
/*  477 */         return false;
/*      */       }
/*  479 */       RegionManager regionManager = wgp.getRegionManager(loc.getWorld());
/*      */       
/*  481 */       ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
/*      */       
/*  483 */       Iterator<ProtectedRegion> it = set.iterator();
/*  484 */       while (it.hasNext())
/*      */       {
/*  486 */         String id = ((ProtectedRegion)it.next()).getId();
/*  487 */         String[] splits = id.split("_");
/*  488 */         if (splits.length == 2)
/*      */         {
/*  490 */           if (splits[1].equalsIgnoreCase("safedock"))
/*  491 */             return true;
/*      */         }
/*      */       }
/*  494 */       return false;
/*      */     }
/*  496 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean checkRecallRegion(Location loc)
/*      */   {
/*  502 */     wgp = (WorldGuardPlugin)instance.getServer().getPluginManager().getPlugin("WorldGuard");
/*  503 */     if ((wgp != null) && (loc != null))
/*      */     {
/*  505 */       if (!loc.getWorld().getName().equalsIgnoreCase("warworld1"))
/*      */       {
/*  507 */         return false;
/*      */       }
/*  509 */       RegionManager regionManager = wgp.getRegionManager(loc.getWorld());
/*      */       
/*  511 */       ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
/*      */       
/*  513 */       Iterator<ProtectedRegion> it = set.iterator();
/*  514 */       while (it.hasNext())
/*      */       {
/*  516 */         String id = ((ProtectedRegion)it.next()).getId();
/*  517 */         String[] splits = id.split("_");
/*  518 */         if (splits.length == 2)
/*      */         {
/*  520 */           if (splits[1].equalsIgnoreCase("recall"))
/*  521 */             return true;
/*      */         }
/*      */       }
/*  524 */       return false;
/*      */     }
/*  526 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean checkSpawnRegion(Location loc)
/*      */   {
/*  532 */     wgp = (WorldGuardPlugin)instance.getServer().getPluginManager().getPlugin("WorldGuard");
/*  533 */     if ((wgp != null) && (loc != null))
/*      */     {
/*  535 */       if ((!loc.getWorld().getName().equalsIgnoreCase("warworld1")) && (!loc.getWorld().getName().equalsIgnoreCase("warworld2")))
/*      */       {
/*  537 */         return false;
/*      */       }
/*  539 */       RegionManager regionManager = wgp.getRegionManager(loc.getWorld());
/*      */       
/*  541 */       ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
/*      */       
/*  543 */       Iterator<ProtectedRegion> it = set.iterator();
/*  544 */       while (it.hasNext())
/*      */       {
/*  546 */         String id = ((ProtectedRegion)it.next()).getId();
/*  547 */         String[] splits = id.split("_");
/*  548 */         if (splits.length == 2)
/*      */         {
/*  550 */           if (splits[1].equalsIgnoreCase("spawn"))
/*  551 */             return true;
/*      */         }
/*      */       }
/*  554 */       return false;
/*      */     }
/*  556 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean checkNoDriveRegion(Location loc)
/*      */   {
/*  562 */     wgp = (WorldGuardPlugin)instance.getServer().getPluginManager().getPlugin("WorldGuard");
/*  563 */     if ((wgp != null) && (loc != null))
/*      */     {
/*  565 */       if ((!loc.getWorld().getName().equalsIgnoreCase("warworld1")) && (!loc.getWorld().getName().equalsIgnoreCase("warworld2")))
/*      */       {
/*  567 */         return false;
/*      */       }
/*  569 */       RegionManager regionManager = wgp.getRegionManager(loc.getWorld());
/*      */       
/*  571 */       ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
/*      */       
/*  573 */       Iterator<ProtectedRegion> it = set.iterator();
/*  574 */       while (it.hasNext())
/*      */       {
/*  576 */         String id = ((ProtectedRegion)it.next()).getId();
/*  577 */         String[] splits = id.split("_");
/*  578 */         if (splits.length == 2)
/*      */         {
/*  580 */           if (splits[1].equalsIgnoreCase("nodrive"))
/*  581 */             return true;
/*      */         }
/*      */       }
/*  584 */       return false;
/*      */     }
/*  586 */     return false;
/*      */   }
/*      */   
/*      */   public String ConfigSetting(String setting) {
/*  590 */     if (this.configFile.ConfigSettings.containsKey(setting)) {
/*  591 */       return (String)this.configFile.ConfigSettings.get(setting);
/*      */     }
/*  593 */     System.out.println("Sycoprime needs to be notified that a non-existing config setting '" + setting + 
/*  594 */       "' was attempted to be accessed.");
/*  595 */     return "";
/*      */   }
/*      */   
/*      */   public void dropItem(Block block)
/*      */   {
/*  600 */     if (instance.ConfigSetting("HungryHungryDrill").equalsIgnoreCase("true")) {
/*  601 */       return;
/*      */     }
/*  603 */     int itemToDrop = BlocksInfo.getDropItem(block.getTypeId());
/*  604 */     int quantity = BlocksInfo.getDropQuantity(block.getTypeId());
/*      */     
/*  606 */     if ((itemToDrop != -1) && (quantity != 0))
/*      */     {
/*  608 */       for (int i = 0; i < quantity; i++) {
/*  609 */         block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(itemToDrop, 1));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static void loadExperience()
/*      */   {
/*  617 */     String path = File.separator + "PlayerExpWW1.txt";
/*  618 */     File file = new File(path);
/*      */     
/*      */     try
/*      */     {
/*  622 */       FileReader fr = new FileReader(file.getName());
/*  623 */       BufferedReader reader = new BufferedReader(fr);
/*      */       
/*      */ 
/*  626 */       String line = null;
/*      */       
/*      */       try
/*      */       {
/*  630 */         playerScoresWW1.clear();
/*      */         
/*  632 */         while ((line = reader.readLine()) != null) {
/*  633 */           String[] strings = line.split(",");
/*  634 */           if (strings.length != 2)
/*      */           {
/*  636 */             System.out.println("Player EXP Load Error3");
/*  637 */             return;
/*      */           }
/*      */           
/*  640 */           playerScoresWW1.put(strings[0], Integer.valueOf(strings[1]));
/*      */         }
/*      */         
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*  646 */         e.printStackTrace();
/*  647 */         System.out.println("Player EXP Load Error2");
/*  648 */         return;
/*      */       }
/*      */       
/*  651 */       reader.close();
/*      */     }
/*      */     catch (FileNotFoundException e) {
/*  654 */       e.printStackTrace();
/*  655 */       System.out.println("Player EXP Load Error1");
/*  656 */       return;
/*      */     } catch (IOException e) {
/*  658 */       e.printStackTrace();
/*  659 */       System.out.println("Player EXP Load Error4"); return;
/*      */     }
/*      */     
/*      */     BufferedReader reader;
/*      */     
/*      */     FileReader fr;
/*  665 */     String path2 = File.separator + "PlayerExpWW2.txt";
/*  666 */     File file2 = new File(path2);
/*      */     
/*      */     try
/*      */     {
/*  670 */       FileReader fr2 = new FileReader(file2.getName());
/*  671 */       BufferedReader reader2 = new BufferedReader(fr2);
/*      */       
/*      */ 
/*  674 */       String line2 = null;
/*      */       
/*      */       try
/*      */       {
/*  678 */         playerScoresWW2.clear();
/*      */         
/*  680 */         while ((line2 = reader2.readLine()) != null) {
/*  681 */           String[] strings = line2.split(",");
/*  682 */           if (strings.length != 2)
/*      */           {
/*  684 */             System.out.println("Player EXP Load Error5");
/*  685 */             return;
/*      */           }
/*      */           
/*  688 */           playerScoresWW2.put(strings[0], Integer.valueOf(strings[1]));
/*      */         }
/*      */         
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*  694 */         e.printStackTrace();
/*  695 */         System.out.println("Player EXP Load Error6");
/*  696 */         return;
/*      */       }
/*      */       
/*  699 */       reader2.close();
/*      */     }
/*      */     catch (FileNotFoundException e) {
/*  702 */       e.printStackTrace();
/*  703 */       System.out.println("Player EXP Load Error7");
/*  704 */       return;
/*      */     } catch (IOException e) {
/*  706 */       e.printStackTrace();
/*  707 */       System.out.println("Player EXP Load Error8"); return;
/*      */     }
/*      */     BufferedReader reader2;
/*      */     FileReader fr2;
/*      */   }
/*      */   
/*      */   public static void saveExperience()
/*      */   {
/*  715 */     String path = File.separator + "PlayerExpWW1.txt";
/*  716 */     File file = new File(path);
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  721 */       FileWriter fw = new FileWriter(file.getName());
/*  722 */       BufferedWriter writer = new BufferedWriter(fw);
/*  723 */       String line = null;
/*      */       
/*  725 */       if (playerScoresWW1.isEmpty())
/*      */       {
/*  727 */         System.out.println("Player Save Exp Error1");
/*  728 */         return;
/*      */       }
/*      */       
/*  731 */       for (String s : playerScoresWW1.keySet())
/*      */       {
/*  733 */         line = s + "," + ((Integer)playerScoresWW1.get(s)).toString();
/*      */         try {
/*  735 */           writer.write(line);
/*  736 */           writer.newLine();
/*      */         } catch (IOException e) {
/*  738 */           System.out.println("Player Save Exp Error2");
/*  739 */           e.printStackTrace();
/*  740 */           return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  746 */       writer.close();
/*      */     } catch (IOException e2) {
/*  748 */       System.out.println("Player Save Exp Error4");
/*  749 */       e2.printStackTrace(); return;
/*      */     }
/*      */     
/*      */     BufferedWriter writer;
/*      */     FileWriter fw;
/*  754 */     String path2 = File.separator + "PlayerExpWW2.txt";
/*  755 */     File file2 = new File(path2);
/*      */     
/*      */ 
/*      */     try
/*      */     {
/*  760 */       FileWriter fw2 = new FileWriter(file2.getName());
/*  761 */       BufferedWriter writer2 = new BufferedWriter(fw2);
/*  762 */       String line = null;
/*      */       
/*  764 */       if (playerScoresWW2.isEmpty())
/*      */       {
/*  766 */         System.out.println("Player Save Exp Error5");
/*  767 */         return;
/*      */       }
/*      */       
/*  770 */       for (String s : playerScoresWW2.keySet())
/*      */       {
/*  772 */         line = s + "," + ((Integer)playerScoresWW2.get(s)).toString();
/*      */         try {
/*  774 */           writer2.write(line);
/*  775 */           writer2.newLine();
/*      */         } catch (IOException e) {
/*  777 */           System.out.println("Player Save Exp Error6");
/*  778 */           e.printStackTrace();
/*  779 */           return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  785 */       writer2.close();
/*      */     } catch (IOException e2) {
/*  787 */       System.out.println("Player Save Exp Error7");
/*  788 */       e2.printStackTrace(); return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     BufferedWriter writer2;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     FileWriter fw2;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void structureUpdateScheduler()
/*      */   {
/*  853 */     getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*  858 */         if ((Craft.craftList == null) || (Craft.craftList.isEmpty()))
/*      */         {
/*  860 */           if (!Craft.addCraftList.isEmpty())
/*      */           {
/*  862 */             for (Craft c : Craft.addCraftList)
/*      */             {
/*  864 */               Craft.addCraft(c);
/*      */             }
/*  866 */             Craft.addCraftList.clear();
/*      */           }
/*  868 */           return;
/*      */         }
/*  870 */         int vehicleCount = Craft.craftList.size();
/*  871 */         int vehicleNum = NavyCraft.schedulerCounter % vehicleCount;
/*  872 */         int updateNum = NavyCraft.schedulerCounter / vehicleCount % 4;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*  897 */           if (vehicleCount < 10)
/*      */           {
/*  899 */             NavyCraft.this.updateCraft(vehicleNum, updateNum);
/*  900 */             NavyCraft.schedulerCounter += 1;
/*  901 */           } else if ((vehicleCount >= 10) && (vehicleCount < 20))
/*      */           {
/*      */ 
/*  904 */             NavyCraft.this.updateCraft(vehicleNum, updateNum);
/*  905 */             vehicleNum = (vehicleNum + 1) % vehicleCount;
/*  906 */             NavyCraft.schedulerCounter += 4;
/*  907 */             NavyCraft.this.updateCraft(vehicleNum, updateNum);
/*  908 */             NavyCraft.schedulerCounter -= 4;
/*      */             
/*  910 */             if (updateNum == 3) {
/*  911 */               NavyCraft.schedulerCounter += 5;
/*      */             } else
/*  913 */               NavyCraft.schedulerCounter += 1;
/*  914 */           } else if ((vehicleCount >= 20) && (vehicleCount < 30))
/*      */           {
/*      */ 
/*  917 */             vehicleNum = (vehicleNum + 1) % vehicleCount;
/*  918 */             NavyCraft.schedulerCounter += 4;
/*  919 */             NavyCraft.this.updateCraft(vehicleNum, updateNum);
/*  920 */             vehicleNum = (vehicleNum + 1) % vehicleCount;
/*  921 */             NavyCraft.schedulerCounter += 4;
/*      */             
/*  923 */             NavyCraft.this.updateCraft(vehicleNum, updateNum);
/*  924 */             NavyCraft.schedulerCounter -= 4;
/*  925 */             NavyCraft.schedulerCounter -= 4;
/*      */             
/*  927 */             if (updateNum == 3) {
/*  928 */               NavyCraft.schedulerCounter += 9;
/*      */             } else {
/*  930 */               NavyCraft.schedulerCounter += 1;
/*      */             }
/*      */           }
/*      */           else {
/*  934 */             vehicleNum = (vehicleNum + 1) % vehicleCount;
/*  935 */             NavyCraft.schedulerCounter += 4;
/*  936 */             NavyCraft.this.updateCraft(vehicleNum, updateNum);
/*      */             
/*  938 */             vehicleNum = (vehicleNum + 1) % vehicleCount;
/*  939 */             NavyCraft.schedulerCounter += 4;
/*  940 */             NavyCraft.this.updateCraft(vehicleNum, updateNum);
/*      */             
/*  942 */             vehicleNum = (vehicleNum + 1) % vehicleCount;
/*  943 */             NavyCraft.schedulerCounter += 4;
/*  944 */             NavyCraft.this.updateCraft(vehicleNum, updateNum);
/*  945 */             NavyCraft.schedulerCounter -= 4;
/*  946 */             NavyCraft.schedulerCounter -= 4;
/*  947 */             NavyCraft.schedulerCounter -= 4;
/*      */             
/*  949 */             if (updateNum == 3) {
/*  950 */               NavyCraft.schedulerCounter += 13;
/*      */             } else {
/*  952 */               NavyCraft.schedulerCounter += 1;
/*      */             }
/*      */           }
/*      */           
/*  956 */           if (updateNum == 3)
/*      */           {
/*  958 */             if (!Craft.addCraftList.isEmpty())
/*      */             {
/*  960 */               for (Craft c : Craft.addCraftList)
/*      */               {
/*  962 */                 Craft.addCraft(c);
/*      */               }
/*  964 */               Craft.addCraftList.clear();
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Exception e) {
/*  969 */           NavyCraft.schedulerCounter += 1;
/*      */         }
/*      */         
/*      */       }
/*  973 */     }, 4L, 1L);
/*      */   }
/*      */   
/*      */   public void updateCraft(int vehicleNum, int updateNum)
/*      */   {
/*  978 */     int vehicleCount = Craft.craftList.size();
/*  979 */     if ((vehicleNum < Craft.craftList.size()) && (Craft.craftList.get(vehicleNum) != null))
/*      */     {
/*  981 */       Craft checkCraft = (Craft)Craft.craftList.get(vehicleNum);
/*      */       
/*  983 */       if ((checkCraft != null) && (!checkCraft.isDestroying))
/*      */       {
/*      */ 
/*  986 */         if (checkCraft.isMoving)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  994 */           if (updateNum == 0)
/*      */           {
/*  996 */             if (checkCraft.doRemove)
/*      */             {
/*  998 */               checkCraft.remove();
/*  999 */             } else if (checkCraft.doDestroy)
/*      */             {
/* 1001 */               checkCraft.destroy();
/* 1002 */             } else if (((Math.abs(checkCraft.gear) == 1) && (schedulerCounter / 4 / vehicleCount % 3 == 0)) || 
/* 1003 */               ((Math.abs(checkCraft.gear) == 2) && (schedulerCounter / 4 / vehicleCount % 2 == 0)) || 
/* 1004 */               (Math.abs(checkCraft.gear) == 3))
/*      */             {
/* 1006 */               if (((Math.abs(checkCraft.gear) == 1) && ((System.currentTimeMillis() - checkCraft.lastMove) / 1000L > 8L)) || 
/* 1007 */                 ((Math.abs(checkCraft.gear) == 2) && ((System.currentTimeMillis() - checkCraft.lastMove) / 1000L >= 5L)) || (
/* 1008 */                 (Math.abs(checkCraft.gear) == 3) && ((float)(System.currentTimeMillis() - checkCraft.lastMove) / 1000.0F >= 2.5D)))
/*      */               {
/* 1010 */                 CraftMover cm = new CraftMover(checkCraft, instance);
/* 1011 */                 cm.moveUpdate();
/*      */               }
/*      */               
/*      */ 
/*      */             }
/* 1016 */             else if (!checkCraft.recentlyUpdated)
/*      */             {
/* 1018 */               if ((System.currentTimeMillis() - checkCraft.lastUpdate) / 1000L > 1L)
/*      */               {
/* 1020 */                 CraftMover cm = new CraftMover(checkCraft, instance);
/* 1021 */                 cm.structureUpdate(null, true);
/*      */               }
/*      */               
/*      */             }
/*      */             else {
/* 1026 */               checkCraft.recentlyUpdated = false;
/*      */             }
/*      */           }
/* 1029 */           else if (updateNum == 2)
/*      */           {
/* 1031 */             if ((checkCraft.type.canFly) && (Math.abs(checkCraft.gear) == 3))
/*      */             {
/* 1033 */               if ((float)(System.currentTimeMillis() - checkCraft.lastMove) / 1000.0F >= 2.0D)
/*      */               {
/* 1035 */                 CraftMover cm = new CraftMover(checkCraft, instance);
/* 1036 */                 cm.moveUpdate();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1043 */           if (checkCraft.enginesOn)
/*      */           {
/* 1045 */             if (checkCraft.engineIDLocs.isEmpty())
/*      */             {
/* 1047 */               if (checkCraft.driverName != null)
/*      */               {
/* 1049 */                 Player p = instance.getServer().getPlayer(checkCraft.driverName);
/* 1050 */                 if (p != null)
/*      */                 {
/* 1052 */                   p.sendMessage("Error: No engines detected! Check engine signs.");
/*      */                 }
/*      */               }
/* 1055 */               checkCraft.enginesOn = false;
/* 1056 */               checkCraft.speed = 0;
/*      */             }
/*      */             else {
/* 1059 */               for (Iterator localIterator = checkCraft.engineIDLocs.keySet().iterator(); localIterator.hasNext();) { int id = ((Integer)localIterator.next()).intValue();
/*      */                 
/* 1061 */                 checkCraft.engineIDOn.put(Integer.valueOf(id), Boolean.valueOf(true));
/* 1062 */                 CraftMover cm = new CraftMover(checkCraft, instance);
/* 1063 */                 cm.soundThread(checkCraft, id);
/*      */               }
/*      */             }
/*      */             
/* 1067 */             checkCraft.isMoving = true;
/*      */           }
/*      */           
/* 1070 */           if ((!checkCraft.recentlyUpdated) && (updateNum == 0))
/*      */           {
/* 1072 */             if ((System.currentTimeMillis() - checkCraft.lastUpdate) / 1000L > 1L)
/*      */             {
/* 1074 */               CraftMover cm = new CraftMover(checkCraft, instance);
/* 1075 */               cm.structureUpdate(null, true);
/* 1076 */               checkCraft.lastUpdate = System.currentTimeMillis();
/*      */             }
/*      */           }
/* 1079 */           else if (updateNum == 0)
/*      */           {
/* 1081 */             checkCraft.recentlyUpdated = false;
/*      */           }
/*      */           
/* 1084 */           if (checkCraft.doRemove)
/*      */           {
/* 1086 */             checkCraft.remove();
/* 1087 */           } else if (checkCraft.doDestroy)
/*      */           {
/* 1089 */             checkCraft.destroy();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void npcMerchantThread()
/*      */   {
/* 1102 */     npcMerchantThread = new Thread()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/* 1107 */         setPriority(1);
/*      */         try
/*      */         {
/* 1110 */           int i = 0;
/* 1111 */           sleep(30000L);
/* 1112 */           while (!NavyCraft.shutDown)
/*      */           {
/* 1114 */             NavyCraft.this.npcMerchantUpdate(i);
/* 1115 */             i++;
/*      */             
/* 1117 */             sleep(NavyCraft.spawnTime * 60000);
/*      */           }
/*      */         } catch (InterruptedException e) {
/* 1120 */           e.printStackTrace();
/*      */         }
/*      */       }
/* 1123 */     };
/* 1124 */     npcMerchantThread.start();
/*      */   }
/*      */   
/*      */   public void npcMerchantUpdate(int i)
/*      */   {
/* 1129 */     getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/* 1134 */         if (NavyCraft.shutDown) {
/* 1135 */           return;
/*      */         }
/* 1137 */         MoveCraft_BlockListener.autoSpawnSign(null, "");
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void loadRewardsFile()
/*      */   {
/* 1146 */     String path = File.separator + "PlayerPlotRewards.txt";
/* 1147 */     File file = new File(path);
/*      */     
/*      */     try
/*      */     {
/* 1151 */       FileReader fr = new FileReader(file.getName());
/* 1152 */       BufferedReader reader = new BufferedReader(fr);
/*      */       
/* 1154 */       String line = null;
/*      */       
/*      */       try
/*      */       {
/* 1158 */         while ((line = reader.readLine()) != null)
/*      */         {
/* 1160 */           String[] strings = line.split(",");
/* 1161 */           if (strings.length != 4)
/*      */           {
/* 1163 */             System.out.println("Player Reward Load Error1");
/* 1164 */             reader.close();
/* 1165 */             return;
/*      */           }
/*      */           
/* 1168 */           if ((strings[1].equalsIgnoreCase("dd")) || (strings[1].equalsIgnoreCase("ship1")))
/*      */           {
/* 1170 */             if (playerDDRewards.containsKey(strings[0])) {
/* 1171 */               playerDDRewards.put(strings[0], Integer.valueOf(((Integer)playerDDRewards.get(strings[0])).intValue() + 1));
/*      */             } else
/* 1173 */               playerDDRewards.put(strings[0], Integer.valueOf(1));
/* 1174 */           } else if ((strings[1].equalsIgnoreCase("sub1")) || (strings[1].equalsIgnoreCase("ship2")))
/*      */           {
/*      */ 
/* 1177 */             if (playerSUB1Rewards.containsKey(strings[0])) {
/* 1178 */               playerSUB1Rewards.put(strings[0], Integer.valueOf(((Integer)playerSUB1Rewards.get(strings[0])).intValue() + 1));
/*      */             } else {
/* 1180 */               playerSUB1Rewards.put(strings[0], Integer.valueOf(1));
/*      */             }
/* 1182 */           } else if ((strings[1].equalsIgnoreCase("sub2")) || (strings[1].equalsIgnoreCase("ship3")))
/*      */           {
/*      */ 
/* 1185 */             if (playerSUB2Rewards.containsKey(strings[0])) {
/* 1186 */               playerSUB2Rewards.put(strings[0], Integer.valueOf(((Integer)playerSUB2Rewards.get(strings[0])).intValue() + 1));
/*      */             } else {
/* 1188 */               playerSUB2Rewards.put(strings[0], Integer.valueOf(1));
/*      */             }
/* 1190 */           } else if ((strings[1].equalsIgnoreCase("cl")) || (strings[1].equalsIgnoreCase("ship4")))
/*      */           {
/*      */ 
/* 1193 */             if (playerCLRewards.containsKey(strings[0])) {
/* 1194 */               playerCLRewards.put(strings[0], Integer.valueOf(((Integer)playerCLRewards.get(strings[0])).intValue() + 1));
/*      */             } else {
/* 1196 */               playerCLRewards.put(strings[0], Integer.valueOf(1));
/*      */             }
/* 1198 */           } else if ((strings[1].equalsIgnoreCase("ca")) || (strings[1].equalsIgnoreCase("ship5")))
/*      */           {
/*      */ 
/* 1201 */             if (playerCARewards.containsKey(strings[0])) {
/* 1202 */               playerCARewards.put(strings[0], Integer.valueOf(((Integer)playerCARewards.get(strings[0])).intValue() + 1));
/*      */             } else {
/* 1204 */               playerCARewards.put(strings[0], Integer.valueOf(1));
/*      */             }
/* 1206 */           } else if (strings[1].equalsIgnoreCase("hangar1"))
/*      */           {
/*      */ 
/* 1209 */             if (playerHANGAR1Rewards.containsKey(strings[0])) {
/* 1210 */               playerHANGAR1Rewards.put(strings[0], Integer.valueOf(((Integer)playerHANGAR1Rewards.get(strings[0])).intValue() + 1));
/*      */             } else {
/* 1212 */               playerHANGAR1Rewards.put(strings[0], Integer.valueOf(1));
/*      */             }
/* 1214 */           } else if (strings[1].equalsIgnoreCase("hangar2"))
/*      */           {
/*      */ 
/* 1217 */             if (playerHANGAR2Rewards.containsKey(strings[0])) {
/* 1218 */               playerHANGAR2Rewards.put(strings[0], Integer.valueOf(((Integer)playerHANGAR2Rewards.get(strings[0])).intValue() + 1));
/*      */             } else {
/* 1220 */               playerHANGAR2Rewards.put(strings[0], Integer.valueOf(1));
/*      */             }
/* 1222 */           } else if (strings[1].equalsIgnoreCase("tank1"))
/*      */           {
/*      */ 
/* 1225 */             if (playerTANK1Rewards.containsKey(strings[0])) {
/* 1226 */               playerTANK1Rewards.put(strings[0], Integer.valueOf(((Integer)playerTANK1Rewards.get(strings[0])).intValue() + 1));
/*      */             } else {
/* 1228 */               playerTANK1Rewards.put(strings[0], Integer.valueOf(1));
/*      */             }
/*      */           }
/*      */           else {
/* 1232 */             System.out.println("Player Reward Load Error:Unknown Reward");
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/* 1239 */         e.printStackTrace();
/* 1240 */         System.out.println("Player Reward Load Error2");
/* 1241 */         return;
/*      */       }
/*      */       
/* 1244 */       reader.close();
/*      */     }
/*      */     catch (FileNotFoundException e) {
/* 1247 */       e.printStackTrace();
/* 1248 */       System.out.println("Player Reward Load Error3");
/* 1249 */       return;
/*      */     } catch (IOException e) {
/* 1251 */       e.printStackTrace();
/* 1252 */       System.out.println("Player Reward Load Error4"); return;
/*      */     }
/*      */     BufferedReader reader;
/*      */     FileReader fr;
/*      */   }
/*      */   
/*      */   public static void saveRewardsFile(String s) {
/* 1259 */     String path = File.separator + "PlayerPlotRewards.txt";
/* 1260 */     File file = new File(path);
/*      */     
/*      */ 
/*      */     try
/*      */     {
/* 1265 */       FileWriter fw = new FileWriter(file.getName(), true);
/* 1266 */       BufferedWriter writer = new BufferedWriter(fw);
/*      */       try
/*      */       {
/* 1269 */         writer.write(s);
/* 1270 */         writer.newLine();
/*      */       } catch (IOException e) {
/* 1272 */         System.out.println("Player Save Reward Error1");
/* 1273 */         e.printStackTrace();
/* 1274 */         writer.close();
/* 1275 */         return;
/*      */       }
/*      */       
/* 1278 */       writer.close();
/*      */     } catch (IOException e2) {
/* 1280 */       System.out.println("Player Save Reward Error2");
/* 1281 */       e2.printStackTrace(); return;
/*      */     }
/*      */     BufferedWriter writer;
/*      */     FileWriter fw;
/*      */   }
/*      */   
/*      */   public static void explosion(int explosionRadius, Block warhead)
/*      */   {
/* 1289 */     short[][][] powerMatrix = new short[explosionRadius * 2 + 1][explosionRadius * 2 + 1][explosionRadius * 2 + 1];
/*      */     
/* 1291 */     powerMatrix[explosionRadius][explosionRadius][explosionRadius] = ((short)(explosionRadius * 50));
/*      */     
/* 1293 */     int refI = 0;
/* 1294 */     int refJ = 0;
/* 1295 */     int refK = 0;
/* 1296 */     int curX = 0;
/* 1297 */     int curY = 0;
/* 1298 */     int curZ = 0;
/* 1299 */     int refX = 0;
/* 1300 */     int refY = 0;
/* 1301 */     int refZ = 0;
/* 1302 */     boolean doPower = false;
/* 1303 */     int fuseDelay = 5;
/*      */     
/*      */ 
/* 1306 */     for (int r = 1; r < explosionRadius; r++)
/*      */     {
/* 1308 */       for (int j = -r; j <= r; j++)
/*      */       {
/* 1310 */         for (int i = -r; i <= r; i++)
/*      */         {
/* 1312 */           for (int k = -r; k <= r; k++)
/*      */           {
/* 1314 */             float refPowerMult = 1.0F;
/* 1315 */             if (Math.abs(i) == r)
/*      */             {
/* 1317 */               refI = (int)((Math.abs(i) - 1) * Math.signum(i));
/* 1318 */               if (Math.abs(j) == r)
/*      */               {
/* 1320 */                 refJ = (int)((Math.abs(j) - 1) * Math.signum(j));
/* 1321 */                 if (Math.abs(k) == r)
/*      */                 {
/* 1323 */                   refK = (int)((Math.abs(k) - 1) * Math.signum(k));
/* 1324 */                   refPowerMult = 0.14F;
/* 1325 */                 } else if (Math.abs(k) == r - 1)
/*      */                 {
/* 1327 */                   refPowerMult = 0.14F;
/*      */                 }
/*      */                 else {
/* 1330 */                   refPowerMult = 0.33F;
/*      */                 }
/* 1332 */               } else if (Math.abs(k) == r)
/*      */               {
/* 1334 */                 refK = (int)((Math.abs(k) - 1) * Math.signum(k));
/* 1335 */                 refPowerMult = 0.33F;
/* 1336 */                 if (Math.abs(j) == r - 1)
/* 1337 */                   refPowerMult = 0.14F;
/* 1338 */               } else if (Math.abs(j) == r - 1)
/*      */               {
/* 1340 */                 refPowerMult = 0.33F;
/* 1341 */                 if (Math.abs(k) == r - 1)
/* 1342 */                   refPowerMult = 0.14F;
/* 1343 */               } else if (Math.abs(k) == r - 1)
/*      */               {
/* 1345 */                 refPowerMult = 0.33F;
/* 1346 */                 if (Math.abs(j) == r - 1)
/* 1347 */                   refPowerMult = 0.14F;
/*      */               }
/* 1349 */               doPower = true;
/* 1350 */             } else if (Math.abs(j) == r)
/*      */             {
/* 1352 */               refJ = (int)((Math.abs(j) - 1) * Math.signum(j));
/* 1353 */               if (Math.abs(k) == r)
/*      */               {
/* 1355 */                 refK = (int)((Math.abs(k) - 1) * Math.signum(k));
/* 1356 */                 refPowerMult = 0.33F;
/* 1357 */                 if (Math.abs(i) == r - 1)
/* 1358 */                   refPowerMult = 0.14F;
/* 1359 */               } else if (Math.abs(i) == r - 1)
/*      */               {
/* 1361 */                 refPowerMult = 0.33F;
/* 1362 */                 if (Math.abs(k) == r - 1)
/* 1363 */                   refPowerMult = 0.14F;
/* 1364 */               } else if (Math.abs(k) == r - 1)
/*      */               {
/* 1366 */                 refPowerMult = 0.33F;
/* 1367 */                 if (Math.abs(i) == r - 1)
/* 1368 */                   refPowerMult = 0.14F;
/*      */               }
/* 1370 */               doPower = true;
/* 1371 */             } else if (Math.abs(k) == r)
/*      */             {
/* 1373 */               refK = (int)((Math.abs(k) - 1) * Math.signum(k));
/* 1374 */               if (Math.abs(i) == r - 1)
/*      */               {
/* 1376 */                 refPowerMult = 0.33F;
/* 1377 */                 if (Math.abs(j) == r - 1)
/* 1378 */                   refPowerMult = 0.14F;
/* 1379 */               } else if (Math.abs(j) == r - 1)
/*      */               {
/* 1381 */                 refPowerMult = 0.33F;
/* 1382 */                 if (Math.abs(i) == r - 1)
/* 1383 */                   refPowerMult = 0.14F;
/*      */               }
/* 1385 */               doPower = true;
/*      */             }
/*      */             
/* 1388 */             if (doPower)
/*      */             {
/* 1390 */               curX = i + explosionRadius;
/* 1391 */               curY = j + explosionRadius;
/* 1392 */               curZ = k + explosionRadius;
/* 1393 */               refX = refI + explosionRadius;
/* 1394 */               refY = refJ + explosionRadius;
/* 1395 */               refZ = refK + explosionRadius;
/*      */               
/* 1397 */               Block theBlock = warhead.getRelative(i, j, k);
/* 1398 */               int blockType = theBlock.getTypeId();
/*      */               
/* 1400 */               short refPower = (short)(int)(powerMatrix[refX][refY][refZ] * refPowerMult);
/* 1401 */               if (refPower > 0)
/*      */               {
/*      */                 short blockResist;
/*      */                 short blockResist;
/* 1405 */                 if (Craft.blockHardness(blockType) == 4)
/*      */                 {
/* 1407 */                   blockResist = -1; } else { short blockResist;
/* 1408 */                   if (Craft.blockHardness(blockType) == 3)
/*      */                   {
/* 1410 */                     blockResist = (short)(int)(40.0D + 40.0D * Math.random()); } else { short blockResist;
/* 1411 */                     if (Craft.blockHardness(blockType) == 2)
/*      */                     {
/* 1413 */                       blockResist = (short)(int)(20.0D + 20.0D * Math.random()); } else { short blockResist;
/* 1414 */                       if (Craft.blockHardness(blockType) == 1)
/*      */                       {
/* 1416 */                         blockResist = (short)(int)(10.0D + 15.0D * Math.random()); } else { short blockResist;
/* 1417 */                         if (Craft.blockHardness(blockType) == -3)
/*      */                         {
/* 1419 */                           blockResist = (short)(int)(10.0D + 10.0D * Math.random());
/*      */                         }
/*      */                         else
/* 1422 */                           blockResist = (short)(int)(5.0D + 5.0D * Math.random());
/*      */                       }
/*      */                     } } }
/* 1425 */                 if (Craft.blockHardness(blockType) == -1)
/*      */                 {
/* 1427 */                   theBlock.setType(Material.AIR);
/* 1428 */                   TNTPrimed tnt = (TNTPrimed)theBlock.getWorld().spawnEntity(new Location(theBlock.getWorld(), theBlock.getX(), theBlock.getY(), theBlock.getZ()), EntityType.PRIMED_TNT);
/* 1429 */                   tnt.setFuseTicks(fuseDelay);
/* 1430 */                   fuseDelay += 2;
/* 1431 */                 } else if (Craft.blockHardness(blockType) == -2)
/*      */                 {
/* 1433 */                   theBlock.setType(Material.AIR);
/* 1434 */                   TNTPrimed tnt = (TNTPrimed)theBlock.getWorld().spawnEntity(new Location(theBlock.getWorld(), theBlock.getX(), theBlock.getY(), theBlock.getZ()), EntityType.PRIMED_TNT);
/* 1435 */                   tnt.setFuseTicks(fuseDelay);
/* 1436 */                   tnt.setYield(tnt.getYield() * 0.5F);
/* 1437 */                   fuseDelay += 2;
/*      */ 
/*      */                 }
/* 1440 */                 else if ((refPower > blockResist) && (blockResist >= 0))
/*      */                 {
/* 1442 */                   if (theBlock.getY() > 62) {
/* 1443 */                     theBlock.setType(Material.AIR);
/*      */                   } else {
/* 1445 */                     theBlock.setType(Material.WATER);
/*      */                   }
/*      */                 } else {
/* 1448 */                   refPower = 0;
/*      */                 }
/*      */                 
/* 1451 */                 short newPower = (short)(refPower - blockResist);
/*      */                 
/* 1453 */                 if (newPower < 5)
/*      */                 {
/* 1455 */                   powerMatrix[curX][curY][curZ] = 0;
/*      */                 }
/*      */                 else {
/* 1458 */                   powerMatrix[curX][curY][curZ] = newPower;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1467 */     warhead.getWorld().createExplosion(warhead.getLocation(), explosionRadius);
/*      */   }
/*      */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\NavyCraft.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */