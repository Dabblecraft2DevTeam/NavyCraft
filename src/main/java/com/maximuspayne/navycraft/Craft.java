/*      */ package com.maximuspayne.navycraft;
/*      */ 
/*      */ import com.sk89q.worldedit.CuboidClipboard;
/*      */ import com.sk89q.worldedit.bukkit.WorldEditPlugin;
/*      */ import com.sk89q.worldedit.session.ClipboardHolder;
/*      */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import org.bukkit.ChatColor;
/*      */ import org.bukkit.Chunk;
/*      */ import org.bukkit.Location;
/*      */ import org.bukkit.Material;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.block.Block;
/*      */ import org.bukkit.block.BlockFace;
/*      */ import org.bukkit.entity.Entity;
/*      */ import org.bukkit.entity.Item;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.inventory.PlayerInventory;
/*      */ import org.bukkit.plugin.Plugin;
/*      */ import org.bukkit.scheduler.BukkitScheduler;
/*      */ import org.bukkit.util.Vector;
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
/*      */ public class Craft
/*      */ {
/*      */   public static Plugin plugin;
/*      */   public WorldGuardPlugin wgp;
/*      */   public WorldEditPlugin wep;
/*   49 */   public static ArrayList<Craft> craftList = new ArrayList();
/*   50 */   public static ArrayList<Craft> addCraftList = new ArrayList();
/*   51 */   public static HashMap<Player, Thread> playerAbandonTimers = new HashMap();
/*   52 */   public static HashMap<Player, Craft> playerShipList = new HashMap();
/*   53 */   public static HashMap<Player, ClipboardHolder> playerClipboards = new HashMap();
/*      */   
/*   55 */   public static HashMap<Player, Integer> playerClipboardsRank = new HashMap();
/*   56 */   public static HashMap<Player, String> playerClipboardsType = new HashMap();
/*   57 */   public static HashMap<Player, String> playerClipboardsLot = new HashMap();
/*      */   
/*   59 */   public static HashMap<String, CuboidClipboard> playerStoredClipboard = new HashMap();
/*      */   
/*      */ 
/*   62 */   public static int craftIDTicker = 0;
/*      */   
/*      */   public int craftID;
/*      */   
/*      */   public CraftType type;
/*      */   public String name;
/*   68 */   public boolean doDestroy = false;
/*   69 */   public boolean doRemove = false;
/*      */   
/*      */   short[][][] matrix;
/*      */   
/*      */   ArrayList<DataBlock> dataBlocks;
/*   74 */   ArrayList<DataBlock> complexBlocks = new ArrayList();
/*      */   
/*      */   short[][][] displacedBlocks;
/*      */   
/*      */   public ArrayList<Entity> checkEntities;
/*      */   public int dx;
/*      */   public int dy;
/*   81 */   public int dz = 0;
/*      */   int sizeX;
/*      */   int sizeZ;
/*   84 */   int sizeY = 0;
/*      */   
/*      */   World world;
/*      */   
/*      */   int centerX;
/*   89 */   int centerZ = -1;
/*      */   
/*   91 */   int blockCount = 0;
/*   92 */   int flyBlockCount; int digBlockCount = 0;
/*      */   
/*      */   int maxBlocks;
/*      */   
/*   96 */   int waterLevel = -1;
/*   97 */   int newWaterLevel = -1;
/*      */   
/*   99 */   short waterType = 0;
/*      */   int minX;
/*  101 */   int maxX; int minY; int maxY; int minZ; int maxZ = 0;
/*      */   
/*      */   public String captainName;
/*      */   public String driverName;
/*  105 */   public ArrayList<String> crewNames = new ArrayList();
/*  106 */   public ArrayList<String> crewHistory = new ArrayList();
/*  107 */   public HashMap<String, Boolean> isNameOnBoard = new HashMap();
/*      */   
/*  109 */   int speed = 0;
/*      */   
/*  111 */   long lastMove = System.currentTimeMillis();
/*  112 */   public long lastUpdate = System.currentTimeMillis();
/*      */   
/*  114 */   boolean haveControl = true;
/*      */   
/*  116 */   String customName = null;
/*      */   
/*  118 */   boolean blockPlaced = false;
/*      */   
/*      */ 
/*  121 */   public int rotation = 0;
/*  122 */   int offX; int offZ = 0;
/*      */   
/*      */ 
/*  125 */   public MoveCraft_Timer timer = null;
/*  126 */   boolean isPublic = false;
/*  127 */   public boolean inHyperSpace = false;
/*  128 */   public int[] HyperSpaceMoves = new int[3];
/*  129 */   public ArrayList<Location> WayPoints = new ArrayList();
/*  130 */   public int currentWayPoint = 0;
/*  131 */   public boolean StopRequested = false;
/*      */   public Block railBlock;
/*  133 */   int remainingFuel = 0;
/*  134 */   int asyncTaskId = 0;
/*      */   
/*  136 */   boolean autoTurn = true;
/*      */   Location collisionLoc;
/*  138 */   boolean possibleCollision = false;
/*  139 */   boolean doCollide = false;
/*  140 */   boolean helmDestroyed = false;
/*  141 */   volatile boolean sinking = false;
/*  142 */   int lastDX = 0;
/*  143 */   int lastDZ = 0;
/*      */   
/*      */   Location signLoc;
/*      */   
/*      */   int blockCountStart;
/*      */   
/*      */   int lastBlockCount;
/*  150 */   public HashMap<Player, Integer> damagers = new HashMap();
/*  151 */   public int uncreditedDamage = 0;
/*      */   
/*  153 */   public ArrayList<Periscope> periscopes = new ArrayList();
/*      */   
/*  155 */   public int waitTorpLoading = 0;
/*      */   
/*      */   public Player lastCauser;
/*      */   
/*  159 */   int rudder = 0;
/*  160 */   int turnProgress = 0;
/*  161 */   int setSpeed = 0;
/*  162 */   int vertPlanes = 0;
/*  163 */   int gear = 1;
/*      */   
/*  165 */   boolean enginesOn = false;
/*  166 */   boolean isMoving = false;
/*  167 */   int keelDepth = 0;
/*  168 */   boolean onGround = true;
/*  169 */   boolean isRepairing = false;
/*  170 */   boolean doSink = false;
/*  171 */   boolean checkLanding = false;
/*  172 */   boolean submergedMode = false;
/*  173 */   boolean speedReducedCol = false;
/*  174 */   int reductionSpeed = -1;
/*  175 */   int collisionSpeed = -1;
/*      */   
/*  177 */   boolean blueTeam = false;
/*  178 */   boolean redTeam = false;
/*      */   
/*  180 */   boolean isAutoCraft = false;
/*      */   
/*  182 */   long abandonTime = 0L;
/*      */   
/*  184 */   boolean recentlyUpdated = false;
/*      */   
/*  186 */   boolean radarOn = false;
/*  187 */   long lastRadarPulse = 0L;
/*  188 */   boolean sonarOn = false;
/*  189 */   long lastSonarPulse = 0L;
/*      */   
/*  191 */   boolean leftSafeDock = false;
/*  192 */   boolean isDestroying = false;
/*      */   
/*  194 */   boolean isMerchantCraft = false;
/*      */   
/*  196 */   public HashMap<Integer, Integer> tubeFiringMode = new HashMap();
/*  197 */   public HashMap<Integer, Integer> tubeFiringDepth = new HashMap();
/*  198 */   public HashMap<Integer, Integer> tubeFiringHeading = new HashMap();
/*  199 */   public HashMap<Integer, Integer> tubeFiringArm = new HashMap();
/*  200 */   public HashMap<Integer, Boolean> tubeFiringArmed = new HashMap();
/*  201 */   public HashMap<Integer, Boolean> tubeFiringAuto = new HashMap();
/*  202 */   public HashMap<Integer, Integer> tubeFiringRudder = new HashMap();
/*  203 */   public HashMap<Integer, Integer> tubeFiringDisplay = new HashMap();
/*      */   
/*  205 */   public int tubeMk1FiringMode = -2;
/*  206 */   public int tubeMk1FiringDepth = 1;
/*  207 */   public int tubeMk1FiringSpread = 0;
/*  208 */   public int tubeMk1FiringDisplay = -1;
/*      */   
/*  210 */   public boolean hfSonarOn = false;
/*  211 */   public HashMap<Craft, Integer> sonarTargetIDs = new HashMap();
/*  212 */   public HashMap<Integer, Craft> sonarTargetIDs2 = new HashMap();
/*  213 */   public HashMap<Craft, Float> sonarTargetStrength = new HashMap();
/*      */   public Craft sonarTarget;
/*  215 */   public float sonarTargetRng = -1.0F;
/*  216 */   public int sonarTargetIndex = -1;
/*  217 */   public boolean doPing = false;
/*  218 */   public boolean hfOn = false;
/*      */   
/*  220 */   public boolean launcherOn = false;
/*      */   
/*      */ 
/*      */ 
/*  224 */   public float lastPeriscopeYaw = -9999.0F;
/*      */   
/*  226 */   public HashMap<Integer, Integer> engineIDTypes = new HashMap();
/*  227 */   public HashMap<Integer, Location> engineIDLocs = new HashMap();
/*  228 */   public HashMap<Integer, Boolean> engineIDOn = new HashMap();
/*      */   
/*  230 */   public ArrayList<Chunk> checkedChunks = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  237 */   ArrayList<DataBlock> engineBlocks = new ArrayList();
/*      */   
/*  239 */   public boolean doCost = true;
/*  240 */   public int vehicleCost = 0;
/*      */   
/*      */   public CuboidClipboard repairClipboard;
/*      */   
/*  244 */   public Location radioSignLoc = null;
/*  245 */   public int radio1 = 0;
/*  246 */   public int radio2 = 0;
/*  247 */   public int radio3 = 0;
/*  248 */   public int radio4 = 0;
/*  249 */   public int radioSelector = 1;
/*  250 */   public boolean radioSetOn = true;
/*  251 */   public long lastRadioPulse = 0L;
/*      */   
/*  253 */   public boolean abandoned = false;
/*  254 */   public boolean captainAbandoned = false;
/*  255 */   public boolean takingOver = false;
/*  256 */   public boolean cancelTakeoverTimer = false;
/*      */   
/*  258 */   public int sinkValue = 0;
/*  259 */   public String routeID = "";
/*  260 */   public int routeStage = 0;
/*  261 */   public boolean freeSpawn = false;
/*  262 */   public boolean discountSpawn = false;
/*      */   
/*  264 */   public boolean isMovingPlayers = false;
/*      */   
/*  266 */   public int noCaptain = 0;
/*  267 */   public int stuckAutoTimer = 0;
/*      */   
/*      */ 
/*  270 */   public static HashMap<String, Craft> reboardNames = new HashMap();
/*      */   
/*  272 */   public float weight = 0.0F;
/*  273 */   public float displacement = 0.0F;
/*  274 */   public float blockDisplacement = 0.0F;
/*  275 */   public float airDisplacement = 0.0F;
/*  276 */   public int currentEngineCount = 0;
/*  277 */   public int buoyancy = 0;
/*  278 */   public int buoyFloodTicker = 0;
/*      */   
/*  280 */   public float blockDispValue = 1.0F;
/*  281 */   public float airDispValue = 15.0F;
/*  282 */   public float weightMult = 1.0F;
/*  283 */   public float minDispValue = 0.33F;
/*      */   
/*  285 */   public float ballastDisplacement = 0.0F;
/*  286 */   public int ballastAirPercent = 100;
/*  287 */   public int ballastMode = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Craft(CraftType type, Player player, String customName, float Rotation, Location signBlockLoc, Plugin p)
/*      */   {
/*  294 */     if ((Rotation > 45.0F) && (Rotation < 135.0F)) {
/*  295 */       Rotation = 90.0F;
/*  296 */     } else if ((Rotation > 135.0F) && (Rotation < 225.0F)) {
/*  297 */       Rotation = 180.0F;
/*  298 */     } else if ((Rotation > 225.0F) && (Rotation < 315.0F)) {
/*  299 */       Rotation = 270.0F;
/*      */     } else {
/*  301 */       Rotation = 0.0F;
/*      */     }
/*  303 */     this.type = type;
/*  304 */     this.name = type.name;
/*  305 */     this.customName = customName;
/*  306 */     this.captainName = player.getName();
/*  307 */     this.world = player.getWorld();
/*  308 */     this.rotation = ((int)Rotation);
/*  309 */     this.signLoc = signBlockLoc;
/*  310 */     plugin = p;
/*      */     
/*  312 */     this.craftID = craftIDTicker;
/*  313 */     craftIDTicker += 1;
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
/*      */   public static Craft getPlayerCraft(Player player)
/*      */   {
/*  357 */     if (craftList.isEmpty()) {
/*  358 */       return null;
/*      */     }
/*  360 */     if (player == null) {
/*  361 */       return null;
/*      */     }
/*  363 */     for (Craft craft : craftList) {
/*  364 */       if (craft.captainName != null)
/*      */       {
/*  366 */         if (!craft.crewNames.isEmpty())
/*      */         {
/*      */ 
/*  369 */           for (String s : craft.crewNames)
/*      */           {
/*  371 */             if (s.equalsIgnoreCase(player.getName()))
/*      */             {
/*  373 */               return craft;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  379 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public static Craft getCraft(int x, int y, int z)
/*      */   {
/*  385 */     if (craftList.isEmpty()) {
/*  386 */       return null;
/*      */     }
/*  388 */     for (Craft craft : craftList) {
/*  389 */       if (craft.isIn(x, y, z)) {
/*  390 */         return craft;
/*      */       }
/*      */     }
/*  393 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public static Craft getOtherCraft(Craft c, Player p, int x, int y, int z)
/*      */   {
/*  399 */     if (craftList.isEmpty()) {
/*  400 */       return null;
/*      */     }
/*  402 */     for (Craft craft : craftList) {
/*  403 */       if ((craft.isIn(x, y, z)) && (getPlayerCraft(p) != craft)) {
/*  404 */         return craft;
/*      */       }
/*      */     }
/*  407 */     return null;
/*      */   }
/*      */   
/*      */   public void addBlock(Block block, boolean overrideAdd)
/*      */   {
/*  412 */     NavyCraft.instance.DebugMessage("Adding a block...", 4);
/*      */     
/*      */ 
/*  415 */     int x = block.getX() - this.minX;
/*  416 */     int y = block.getY() - this.minY;
/*  417 */     int z = block.getZ() - this.minZ;
/*      */     
/*      */ 
/*  420 */     if ((x < this.sizeX) && (x >= 0) && (y < this.sizeY) && (y >= 0) && (z < this.sizeZ) && (z >= 0))
/*      */     {
/*  422 */       if ((overrideAdd) || ((x < this.sizeX - 1) && (!isFree(this.matrix[(x + 1)][y][z]))) || ((x > 0) && (!isFree(this.matrix[(x - 1)][y][z]))) || 
/*  423 */         ((y < this.sizeY - 1) && (!isFree(this.matrix[x][(y + 1)][z]))) || ((y > 0) && (!isFree(this.matrix[x][(y - 1)][z]))) || 
/*  424 */         ((z < this.sizeZ - 1) && (!isFree(this.matrix[x][y][(z + 1)]))) || ((z > 0) && (!isFree(this.matrix[x][y][(z - 1)]))))
/*      */       {
/*      */ 
/*      */ 
/*  428 */         short blockId = (short)block.getTypeId();
/*      */         
/*      */ 
/*  431 */         boolean found = false;
/*  432 */         short[] arrayOfShort; int j = (arrayOfShort = this.type.structureBlocks).length; for (int i = 0; i < j; i++) { short checkblockId = arrayOfShort[i];
/*  433 */           if (blockId == checkblockId) {
/*  434 */             found = true;
/*  435 */             break;
/*      */           }
/*      */         }
/*  438 */         if ((!found) && (blockId != 0)) {
/*  439 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  444 */         if (blockId == 331) {
/*  445 */           blockId = 55;
/*  446 */         } else if (blockId == 323) {
/*  447 */           blockId = 68;
/*  448 */         } else if (blockId == 324) {
/*  449 */           blockId = 64;
/*  450 */           this.matrix[x][(y + 1)][z] = blockId;
/*  451 */           this.dataBlocks.add(new DataBlock(blockId, x, y + 1, z, block.getData() + 8));
/*  452 */           this.blockCount += 1;
/*  453 */         } else if (blockId == 330) {
/*  454 */           blockId = 71;
/*  455 */           this.matrix[x][(y + 1)][z] = blockId;
/*  456 */           this.dataBlocks.add(new DataBlock(blockId, x, y + 1, z, block.getData() + 8));
/*  457 */           this.blockCount += 1;
/*  458 */         } else if (blockId == 338) {
/*  459 */           blockId = 83;
/*  460 */         } else if (blockId >= 256) {
/*  461 */           return;
/*      */         }
/*      */         
/*  464 */         this.matrix[x][y][z] = blockId;
/*      */         
/*      */ 
/*  467 */         if (BlocksInfo.isDataBlock(blockId)) {
/*  468 */           this.dataBlocks.add(new DataBlock(blockId, x, y, z, block.getData()));
/*      */         }
/*  470 */         if (BlocksInfo.isComplexBlock(blockId)) {
/*  471 */           this.complexBlocks.add(new DataBlock(blockId, x, y, z, block.getData()));
/*      */         }
/*      */         
/*  474 */         if (!overrideAdd)
/*      */         {
/*  476 */           this.weight += blockWeight(blockId);
/*  477 */           this.blockCount += 1;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean isIn(int x, int y, int z)
/*      */   {
/*  485 */     return (x >= this.minX) && (x <= this.maxX) && (y >= this.minY) && (y <= this.maxY) && 
/*  486 */       (z >= this.minZ) && (z <= this.maxZ);
/*      */   }
/*      */   
/*      */   static void addCraft(Craft craft) {
/*  490 */     craftList.add(craft);
/*      */   }
/*      */   
/*      */   void releaseHelm() {
/*  494 */     if (this.timer != null) {
/*  495 */       this.timer.Destroy();
/*      */     }
/*  497 */     this.driverName = null;
/*      */   }
/*      */   
/*      */   void remove() {
/*  501 */     if ((this.isMerchantCraft) && (this.redTeam)) {
/*  502 */       NavyCraft.redMerchant = false;
/*  503 */     } else if ((this.isMerchantCraft) && (this.blueTeam))
/*  504 */       NavyCraft.blueMerchant = false;
/*  505 */     this.isDestroying = true;
/*  506 */     releaseCraft();
/*  507 */     craftList.remove(this);
/*  508 */     this.matrix = null;
/*  509 */     this.dataBlocks.clear();
/*  510 */     this.complexBlocks.clear();
/*      */     
/*  512 */     this.repairClipboard = null;
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean canGoThrough(int craftBlockId, int blockId, int data)
/*      */   {
/*  518 */     if (this.type.canZamboni) {
/*  519 */       return true;
/*      */     }
/*      */     
/*  522 */     if ((blockId == 0) || 
/*  523 */       ((blockId >= 8) && (blockId <= 11) && (data != 0)) || 
/*      */       
/*  525 */       (BlocksInfo.coversGrass(blockId))) {
/*  526 */       return true;
/*      */     }
/*      */     
/*  529 */     if (blockId == 7) {
/*  530 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  534 */     if ((!this.type.canNavigate) && (!this.type.canDive)) {
/*  535 */       return false;
/*      */     }
/*      */     
/*  538 */     if (craftBlockId == 0) {
/*  539 */       if (((blockId >= 8) && (blockId <= 11)) || 
/*  540 */         (blockId == 0)) {
/*  541 */         return true;
/*      */       }
/*  543 */       return false;
/*      */     }
/*      */     
/*      */ 
/*  547 */     if ((this.type.canDig) && (craftBlockId == this.type.digBlockId) && (blockId != 0)) {
/*  548 */       return true;
/*      */     }
/*      */     
/*  551 */     if (((blockId == 8) || (blockId == 9)) && 
/*  552 */       (this.waterType == 8)) {
/*  553 */       return true;
/*      */     }
/*      */     
/*  556 */     if (((blockId == 10) || (blockId == 11)) && 
/*  557 */       (this.waterType == 10)) {
/*  558 */       return true;
/*      */     }
/*  560 */     if (blockId == this.waterType) {
/*  561 */       return true;
/*      */     }
/*      */     
/*  564 */     if ((blockId == 79) && ((this.type.iceBreaker) || (this.type.canNavigate) || (this.type.canDive)) && 
/*  565 */       (this.waterType == 8))
/*  566 */       return true;
/*  567 */     return false;
/*      */   }
/*      */   
/*      */   private static boolean isFree(int blockId) {
/*  571 */     if ((blockId == 0) || (blockId == -1))
/*  572 */       return true;
/*  573 */     return false;
/*      */   }
/*      */   
/*      */   private static boolean isAirOrWater(int blockId)
/*      */   {
/*  578 */     if ((blockId == 0) || ((blockId >= 8) && (blockId <= 11)))
/*  579 */       return true;
/*  580 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isOnCraft(String playerName, boolean precise)
/*      */   {
/*  585 */     Player p = plugin.getServer().getPlayer(playerName);
/*  586 */     if (p != null) {
/*  587 */       return isOnCraft(p, precise);
/*      */     }
/*  589 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isOnCraft(Player player, boolean precise)
/*      */   {
/*  595 */     int x = (int)Math.floor(player.getLocation().getX());
/*  596 */     int y = (int)Math.floor(player.getLocation().getY());
/*  597 */     int z = (int)Math.floor(player.getLocation().getZ());
/*      */     
/*  599 */     if (isIn(x, y - 1, z))
/*      */     {
/*  601 */       if (!precise) {
/*  602 */         return true;
/*      */       }
/*      */       
/*  605 */       if (this.matrix[(x - this.minX)][(y - this.minY - 1)][(z - this.minZ)] != -1) {
/*  606 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  610 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isOnCraft(Entity player, boolean precise)
/*      */   {
/*  615 */     int x = (int)Math.floor(player.getLocation().getX());
/*  616 */     int y = (int)Math.floor(player.getLocation().getY());
/*  617 */     int z = (int)Math.floor(player.getLocation().getZ());
/*      */     
/*  619 */     if (isIn(x, y - 1, z))
/*      */     {
/*  621 */       if (!precise) {
/*  622 */         return true;
/*      */       }
/*      */       
/*  625 */       if (this.matrix[(x - this.minZ)][(y - this.minY - 1)][(z - this.minZ)] != -1) {
/*  626 */         return true;
/*      */       }
/*      */     }
/*      */     
/*  630 */     return false;
/*      */   }
/*      */   
/*      */   public boolean isCraftBlock(int x, int y, int z)
/*      */   {
/*  635 */     if ((x >= 0) && (y >= 0) && (z >= 0) && (x < this.sizeX) && (y < this.sizeY) && (z < this.sizeZ))
/*      */     {
/*  637 */       return this.matrix[x][y][z] != -1;
/*      */     }
/*  639 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean canMove(int dx, int dy, int dz)
/*      */   {
/*  651 */     ArrayList<Chunk> checkChunks = new ArrayList();
/*  652 */     if ((dx > 0) && (dz > 0))
/*      */     {
/*  654 */       dx = this.speed / 2 * dx;
/*  655 */       dz = this.speed / 2 * dz;
/*      */     }
/*      */     else {
/*  658 */       dx = this.speed * dx;
/*  659 */       dz = this.speed * dz;
/*      */     }
/*      */     
/*  662 */     if (Math.abs(this.speed * dy) > 1) {
/*  663 */       dy = this.speed * dy / 2;
/*  664 */       if ((Math.abs(dy) == 0) || (this.type.canDive) || (this.type.isTerrestrial)) {
/*  665 */         dy = (int)Math.signum(dy);
/*      */       }
/*      */     }
/*  668 */     if ((this.type.canFly) && (this.type.doesCruise))
/*      */     {
/*  670 */       if (!this.onGround)
/*      */       {
/*  672 */         if ((this.speed >= 10) && (dy != 0))
/*      */         {
/*  674 */           dy = (this.speed - 6) / 2 * (int)Math.signum(dy);
/*  675 */         } else if ((this.speed >= 8) && (dy != 0))
/*      */         {
/*  677 */           dy = (int)Math.signum(dy);
/*  678 */         } else if ((this.speed >= 8) && (dy == 0))
/*      */         {
/*  680 */           dy = 0;
/*  681 */         } else if (this.speed > 5)
/*      */         {
/*  683 */           dy = -1;
/*  684 */         } else if (this.speed > 3)
/*      */         {
/*  686 */           dy = -3;
/*  687 */         } else if (this.speed <= 3)
/*      */         {
/*  689 */           dy = -5;
/*      */         }
/*      */         
/*      */       }
/*  693 */       else if ((this.speed >= 8) && (dy > 0))
/*      */       {
/*  695 */         dy = 1;
/*      */       }
/*      */       else {
/*  698 */         dy = 0;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  703 */     if (this.checkLanding)
/*      */     {
/*  705 */       dy = 0;
/*      */     }
/*      */     
/*      */ 
/*  709 */     if ((this.minY + dy < 0) || (this.maxY + dy > 255)) {
/*  710 */       NavyCraft.instance.DebugMessage("At Max Altitude!", 4);
/*  711 */       dy = 0;
/*      */     }
/*      */     
/*      */ 
/*  715 */     if ((this.driverName != null) && (isOnCraft(this.driverName, true))) {
/*  716 */       Player driver = plugin.getServer().getPlayer(this.driverName);
/*  717 */       int X = (int)Math.floor(driver.getLocation().getX()) + dx;
/*  718 */       int Y = (int)Math.floor(driver.getLocation().getY()) + dy;
/*  719 */       int Z = (int)Math.floor(driver.getLocation().getZ()) + dz;
/*      */       
/*  721 */       Block targetBlock1 = this.world.getBlockAt(X, Y, Z);
/*  722 */       Block targetBlock2 = this.world.getBlockAt(X, Y + 1, Z);
/*  723 */       if (((!isCraftBlock(X - this.minX, Y - this.minY, Z - this.minZ)) && 
/*  724 */         (!canGoThrough(0, targetBlock1.getTypeId(), 0))) || (
/*  725 */         (!isCraftBlock(X - this.minX, Y + 1 - this.minY, Z - this.minZ)) && 
/*  726 */         (!canGoThrough(0, targetBlock2.getTypeId(), 0)))) {
/*  727 */         NavyCraft.instance.DebugMessage("Craft prevented from because...can't go through?", 4);
/*  728 */         return false;
/*      */       }
/*      */     }
/*      */     
/*      */     int z;
/*  733 */     for (int x = 0; x < this.sizeX; x++) {
/*  734 */       for (z = 0; z < this.sizeZ; z++) {
/*  735 */         for (int y = 0; y < this.sizeY; y++)
/*      */         {
/*      */ 
/*      */ 
/*  739 */           if ((!isFree(this.matrix[x][y][z])) && 
/*  740 */             (!isCraftBlock(x + dx, y + dy, z + dz)))
/*      */           {
/*  742 */             Block theBlock = this.world.getBlockAt(this.minX + x + dx, this.minY + 
/*  743 */               y + dy, this.minZ + z + dz);
/*  744 */             int blockId = theBlock.getTypeId();
/*      */             
/*      */ 
/*  747 */             int blockData = theBlock.getData();
/*      */             
/*  749 */             if (!checkChunks.contains(theBlock.getChunk())) {
/*  750 */               checkChunks.add(theBlock.getChunk());
/*      */             }
/*      */             
/*      */ 
/*  754 */             if ((blockId >= 8) && (blockId <= 11))
/*      */             {
/*      */ 
/*  757 */               if (y > this.newWaterLevel)
/*  758 */                 this.newWaterLevel = y;
/*  759 */             } else if ((dy > 0) && (blockId == 0))
/*      */             {
/*      */ 
/*  762 */               if (y - 1 < this.newWaterLevel) {
/*  763 */                 this.newWaterLevel = (y - 1);
/*      */               }
/*      */             }
/*  766 */             if (!canGoThrough(this.matrix[x][y][z], blockId, blockData)) {
/*  767 */               NavyCraft.instance.DebugMessage("Craft prevented from moving because can't go through.", 4);
/*  768 */               this.collisionLoc = new Location(this.world, this.minX + x + dx, this.minY + y + dy, this.minZ + z + dz);
/*  769 */               return false;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  779 */     for (Chunk checkChunk : checkChunks) {
/*  780 */       if (!this.world.isChunkLoaded(checkChunk)) {
/*      */         try {
/*  782 */           this.world.loadChunk(checkChunk);
/*      */         }
/*      */         catch (Exception ex) {
/*  785 */           NavyCraft.instance.DebugMessage("Craft prevented from moving because destination chunk is not loaded.", 3);
/*  786 */           return false;
/*      */         }
/*      */       }
/*      */     }
/*  790 */     this.checkedChunks = checkChunks;
/*  791 */     return true;
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
/*      */   public void buildCrew(Player newCaptain, boolean addOnly)
/*      */   {
/*  810 */     if (!addOnly)
/*      */     {
/*  812 */       this.crewNames.clear();
/*  813 */       this.isNameOnBoard.clear();
/*      */     }
/*  815 */     ArrayList<Entity> ents = getCraftEntities(false);
/*      */     
/*  817 */     for (Entity e : ents)
/*      */     {
/*  819 */       if ((e instanceof Player))
/*      */       {
/*  821 */         Player p = (Player)e;
/*      */         
/*  823 */         Craft c = getPlayerCraft(p);
/*  824 */         if ((c != null) && (c.crewNames.contains(p.getName())) && (c != this))
/*      */         {
/*  826 */           c.leaveCrew(p);
/*      */         }
/*      */         
/*  829 */         if (!this.crewNames.contains(p.getName())) {
/*  830 */           this.crewNames.add(p.getName());
/*      */         }
/*  832 */         if (!this.crewHistory.contains(p.getName())) {
/*  833 */           this.crewHistory.add(p.getName());
/*      */         }
/*  835 */         this.isNameOnBoard.put(p.getName(), Boolean.valueOf(true));
/*      */         
/*  837 */         if ((p != newCaptain) || (this.type.canFly) || (this.type.isTerrestrial))
/*      */         {
/*  839 */           if (this.type.canFly)
/*      */           {
/*  841 */             if (p.getInventory().getHelmet() == null)
/*      */             {
/*  843 */               p.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET, 1, (short)0));
/*  844 */             } else if ((!p.getInventory().contains(Material.CHAINMAIL_HELMET)) && (p.getInventory().getHelmet().getType() != Material.CHAINMAIL_HELMET))
/*      */             {
/*  846 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.CHAINMAIL_HELMET, 1, 0) });
/*      */             }
/*  848 */             if (p.getInventory().getChestplate() == null)
/*      */             {
/*  850 */               p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1, (short)0));
/*  851 */             } else if ((!p.getInventory().contains(Material.CHAINMAIL_CHESTPLATE)) && (p.getInventory().getChestplate().getType() != Material.CHAINMAIL_CHESTPLATE))
/*      */             {
/*  853 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1, 0) });
/*      */             }
/*  855 */             if (p.getInventory().getLeggings() == null)
/*      */             {
/*  857 */               p.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1, (short)0));
/*  858 */             } else if ((!p.getInventory().contains(Material.CHAINMAIL_LEGGINGS)) && (p.getInventory().getLeggings().getType() != Material.CHAINMAIL_LEGGINGS))
/*      */             {
/*  860 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.CHAINMAIL_LEGGINGS, 1, 0) });
/*      */             }
/*  862 */             if (p.getInventory().getBoots() == null)
/*      */             {
/*  864 */               p.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1, (short)0));
/*  865 */             } else if ((!p.getInventory().contains(Material.CHAINMAIL_BOOTS)) && (p.getInventory().getBoots().getType() != Material.CHAINMAIL_BOOTS))
/*      */             {
/*  867 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.CHAINMAIL_BOOTS, 1, 0) });
/*      */             }
/*  869 */           } else if (this.type.isTerrestrial)
/*      */           {
/*  871 */             if (p.getInventory().getHelmet() == null)
/*      */             {
/*  873 */               p.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET, 1, (short)0));
/*  874 */             } else if ((!p.getInventory().contains(Material.IRON_HELMET)) && (p.getInventory().getHelmet().getType() != Material.IRON_HELMET))
/*      */             {
/*  876 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_HELMET, 1, 0) });
/*      */             }
/*  878 */             if (p.getInventory().getChestplate() == null)
/*      */             {
/*  880 */               p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1, (short)0));
/*  881 */             } else if ((!p.getInventory().contains(Material.IRON_CHESTPLATE)) && (p.getInventory().getChestplate().getType() != Material.IRON_CHESTPLATE))
/*      */             {
/*  883 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_CHESTPLATE, 1, 0) });
/*      */             }
/*  885 */             if (p.getInventory().getLeggings() == null)
/*      */             {
/*  887 */               p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1, (short)0));
/*  888 */             } else if ((!p.getInventory().contains(Material.IRON_LEGGINGS)) && (p.getInventory().getLeggings().getType() != Material.IRON_LEGGINGS))
/*      */             {
/*  890 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_LEGGINGS, 1, 0) });
/*      */             }
/*  892 */             if (p.getInventory().getBoots() == null)
/*      */             {
/*  894 */               p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS, 1, (short)0));
/*  895 */             } else if ((!p.getInventory().contains(Material.IRON_BOOTS)) && (p.getInventory().getBoots().getType() != Material.IRON_BOOTS))
/*      */             {
/*  897 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.IRON_BOOTS, 1, 0) });
/*      */             }
/*      */           }
/*      */           else {
/*  901 */             if (p.getInventory().getHelmet() == null)
/*      */             {
/*  903 */               p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET, 1, (short)0));
/*  904 */             } else if ((!p.getInventory().contains(Material.LEATHER_HELMET)) && (p.getInventory().getHelmet().getType() != Material.LEATHER_HELMET))
/*      */             {
/*  906 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.LEATHER_HELMET, 1, 0) });
/*      */             }
/*  908 */             if (p.getInventory().getChestplate() == null)
/*      */             {
/*  910 */               p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1, (short)0));
/*  911 */             } else if ((!p.getInventory().contains(Material.LEATHER_CHESTPLATE)) && (p.getInventory().getChestplate().getType() != Material.LEATHER_CHESTPLATE))
/*      */             {
/*  913 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.LEATHER_CHESTPLATE, 1, 0) });
/*      */             }
/*  915 */             if (p.getInventory().getLeggings() == null)
/*      */             {
/*  917 */               p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1, (short)0));
/*  918 */             } else if ((!p.getInventory().contains(Material.LEATHER_LEGGINGS)) && (p.getInventory().getLeggings().getType() != Material.LEATHER_LEGGINGS))
/*      */             {
/*  920 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.LEATHER_LEGGINGS, 1, 0) });
/*      */             }
/*  922 */             if (p.getInventory().getBoots() == null)
/*      */             {
/*  924 */               p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1, (short)0));
/*  925 */             } else if ((!p.getInventory().contains(Material.LEATHER_BOOTS)) && (p.getInventory().getBoots().getType() != Material.LEATHER_BOOTS))
/*      */             {
/*  927 */               p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.LEATHER_BOOTS, 1, 0) });
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  934 */     this.captainName = newCaptain.getName();
/*  935 */     if (!this.crewNames.contains(this.captainName))
/*      */     {
/*  937 */       this.crewNames.add(this.captainName);
/*  938 */       this.isNameOnBoard.put(this.captainName, Boolean.valueOf(true));
/*      */     }
/*      */     
/*  941 */     if ((!this.type.canFly) && (!this.type.isTerrestrial))
/*      */     {
/*  943 */       Player captain = plugin.getServer().getPlayer(this.captainName);
/*  944 */       if (captain.getInventory().getHelmet() == null)
/*      */       {
/*  946 */         captain.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET, 1, (short)0));
/*  947 */       } else if ((!captain.getInventory().contains(Material.GOLD_HELMET)) && (captain.getInventory().getHelmet().getType() != Material.GOLD_HELMET))
/*      */       {
/*  949 */         captain.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_HELMET, 1, 0) });
/*      */       }
/*  951 */       if (captain.getInventory().getChestplate() == null)
/*      */       {
/*  953 */         captain.getInventory().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE, 1, (short)0));
/*  954 */       } else if ((!captain.getInventory().contains(Material.GOLD_CHESTPLATE)) && (captain.getInventory().getChestplate().getType() != Material.GOLD_CHESTPLATE))
/*      */       {
/*  956 */         captain.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_CHESTPLATE, 1, 0) });
/*      */       }
/*  958 */       if (captain.getInventory().getLeggings() == null)
/*      */       {
/*  960 */         captain.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS, 1, (short)0));
/*  961 */       } else if ((!captain.getInventory().contains(Material.GOLD_LEGGINGS)) && (captain.getInventory().getLeggings().getType() != Material.GOLD_LEGGINGS))
/*      */       {
/*  963 */         captain.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_LEGGINGS, 1, 0) });
/*      */       }
/*  965 */       if (captain.getInventory().getBoots() == null)
/*      */       {
/*  967 */         captain.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS, 1, (short)0));
/*  968 */       } else if ((!captain.getInventory().contains(Material.GOLD_BOOTS)) && (captain.getInventory().getBoots().getType() != Material.GOLD_BOOTS))
/*      */       {
/*  970 */         captain.getInventory().addItem(new ItemStack[] { new ItemStack(Material.GOLD_BOOTS, 1, 0) });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  975 */     this.driverName = this.captainName;
/*  976 */     this.haveControl = true;
/*      */     
/*  978 */     if (this.customName == null)
/*      */     {
/*  980 */       for (String s : this.crewNames)
/*      */       {
/*  982 */         Player p = NavyCraft.instance.getServer().getPlayer(s);
/*  983 */         if ((p != null) && (s != this.captainName))
/*      */         {
/*  985 */           p.sendMessage(ChatColor.YELLOW + "You join the crew of the " + ChatColor.WHITE + this.name.toUpperCase() + ChatColor.YELLOW + " class under the command of " + newCaptain.getDisplayName() + "!");
/*  986 */           newCaptain.sendMessage(p.getDisplayName() + ChatColor.YELLOW + " joins your crew!");
/*      */         }
/*      */         
/*      */       }
/*      */     } else {
/*  991 */       for (String s : this.crewNames)
/*      */       {
/*  993 */         Player p = NavyCraft.instance.getServer().getPlayer(s);
/*  994 */         if ((p != null) && (s != this.captainName))
/*      */         {
/*  996 */           p.sendMessage(ChatColor.YELLOW + "You join the " + ChatColor.WHITE + this.name.toUpperCase() + ChatColor.YELLOW + " under the command of " + newCaptain.getDisplayName() + "!");
/*  997 */           newCaptain.sendMessage(p.getDisplayName() + ChatColor.YELLOW + " joins your crew!");
/*      */         }
/*      */       }
/*      */     }
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
/*      */   public void releaseCraft()
/*      */   {
/* 1022 */     this.captainName = null;
/* 1023 */     this.driverName = null;
/* 1024 */     this.crewNames.clear();
/* 1025 */     this.isNameOnBoard.clear();
/* 1026 */     reboardNames.clear();
/* 1027 */     this.abandonTime = System.currentTimeMillis();
/* 1028 */     this.abandoned = false;
/* 1029 */     this.captainAbandoned = false;
/*      */     
/* 1031 */     if (!this.isDestroying)
/*      */     {
/* 1033 */       CraftMover cm = new CraftMover(this, plugin);
/* 1034 */       cm.structureUpdate(null, false);
/*      */     }
/*      */   }
/*      */   
/*      */   public ArrayList<Entity> getCraftEntities(boolean removeItems) {
/* 1039 */     ArrayList<Entity> checkEntities = new ArrayList();
/*      */     
/* 1041 */     Chunk firstChunk = this.world.getChunkAt(new Location(this.world, this.minX, this.minY, this.minZ));
/* 1042 */     Chunk lastChunk = this.world.getChunkAt(new Location(this.world, this.minX + this.sizeX, this.minY + this.sizeY, this.minZ + this.sizeZ));
/*      */     
/* 1044 */     int targetX = 0;
/* 1045 */     int targetZ = 0;
/*      */     
/*      */ 
/*      */ 
/* 1049 */     for (int x = 0; Math.abs(firstChunk.getX() - lastChunk.getX()) >= x; x++) {
/* 1050 */       targetX = 0;
/* 1051 */       if (firstChunk.getX() < lastChunk.getX()) {
/* 1052 */         targetX = firstChunk.getX() + x;
/*      */       } else {
/* 1054 */         targetX = firstChunk.getX() - x;
/*      */       }
/* 1056 */       for (int z = 0; Math.abs(firstChunk.getZ() - lastChunk.getZ()) >= z; z++) {
/* 1057 */         targetZ = 0;
/* 1058 */         if (firstChunk.getZ() < lastChunk.getZ()) {
/* 1059 */           targetZ = firstChunk.getZ() + z;
/*      */         } else {
/* 1061 */           targetZ = firstChunk.getZ() - z;
/*      */         }
/*      */         
/* 1064 */         Chunk addChunk = this.world.getChunkAt(targetX, targetZ);
/*      */         try
/*      */         {
/* 1067 */           Entity[] ents = addChunk.getEntities();
/* 1068 */           Entity[] arrayOfEntity1; int j = (arrayOfEntity1 = ents).length; for (int i = 0; i < j; i++) { Entity e = arrayOfEntity1[i];
/* 1069 */             if ((!(e instanceof Item)) && (isOnCraft(e, false))) {
/* 1070 */               checkEntities.add(e);
/* 1071 */             } else if (((e instanceof Item)) && ((this.sinking) || (removeItems)) && (isOnCraft(e, false)))
/*      */             {
/* 1073 */               e.remove();
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Exception localException) {}
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1082 */     return checkEntities;
/*      */   }
/*      */   
/*      */   public void setSpeed(int speed) {
/* 1086 */     if (speed < 1) {
/* 1087 */       this.speed = speed;
/* 1088 */     } else if (this.type.doesCruise)
/*      */     {
/* 1090 */       if (speed > this.type.maxEngineSpeed)
/*      */       {
/* 1092 */         this.speed = this.type.maxEngineSpeed;
/*      */       }
/*      */       else {
/* 1095 */         this.speed = speed;
/*      */       }
/*      */       
/*      */     }
/* 1099 */     else if (speed > this.type.maxSpeed)
/*      */     {
/* 1101 */       this.speed = this.type.maxSpeed;
/*      */     }
/*      */     else {
/* 1104 */       this.speed = speed;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getSpeed()
/*      */   {
/* 1111 */     return this.speed;
/*      */   }
/*      */   
/*      */   public void turn(int dr) {
/* 1115 */     CraftRotator cr = new CraftRotator(this, plugin);
/* 1116 */     cr.turn(dr);
/*      */   }
/*      */   
/*      */   public void engineTick()
/*      */   {
/* 1121 */     int dx = 0;
/* 1122 */     int dy = 0;
/* 1123 */     int dz = 0;
/* 1124 */     int[] returnVals = new int[3];
/*      */     
/* 1126 */     if (this.type.obeysGravity) {
/* 1127 */       dy--;
/*      */     }
/*      */     
/*      */ 
/* 1131 */     if ((this.driverName == null) || (this.engineBlocks == null))
/* 1132 */       return;
/* 1133 */     returnVals = enginesByPlayerFacing(plugin.getServer().getPlayer(this.driverName), this.engineBlocks.size());
/* 1134 */     dx = returnVals[0];
/* 1135 */     dy = returnVals[1];
/* 1136 */     dz = returnVals[2];
/*      */     
/* 1138 */     if ((dx == 0) && (dy == 0)) {}
/*      */   }
/*      */   
/*      */ 
/*      */   public int[] enginesByEngineFace(CraftMover cm)
/*      */   {
/* 1144 */     int dx = 0;int dy = 0;int dz = 0;
/*      */     
/* 1146 */     for (DataBlock edb : this.engineBlocks)
/*      */     {
/* 1148 */       Block engineBlock = cm.getWorldBlock(edb.x, edb.y, edb.z);
/* 1149 */       Block underBlock = this.world.getBlockAt(engineBlock.getX(), engineBlock.getY() - 1, engineBlock.getZ());
/*      */       
/*      */ 
/* 1152 */       engineBlock.getBlockPower();
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
/* 1166 */       int engineDirection = BlocksInfo.getCardinalDirectionFromData(engineBlock.getTypeId(), engineBlock.getData());
/* 1167 */       switch (engineDirection) {
/*      */       case 0: 
/* 1169 */         dx--;
/* 1170 */         break;
/*      */       case 1: 
/* 1172 */         dz--;
/* 1173 */         break;
/*      */       case 2: 
/* 1175 */         dz++;
/* 1176 */         break;
/*      */       case 3: 
/* 1178 */         dx++;
/*      */       }
/*      */       
/*      */       
/* 1182 */       if (underBlock.getType() == Material.REDSTONE_WIRE) { underBlock.getData();
/*      */       }
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
/* 1202 */     return new int[] { dx, dy, dz };
/*      */   }
/*      */   
/*      */   public int[] enginesByPlayerFacing(Player player, int engineCount) {
/* 1206 */     float rotation = 3.1415927F * player.getLocation().getYaw() / 180.0F;
/*      */     
/* 1208 */     float nx = -(float)Math.sin(rotation);
/* 1209 */     float nz = (float)Math.cos(rotation);
/*      */     
/* 1211 */     int[] returnVals = new int[3];
/*      */     
/* 1213 */     returnVals[0] = (engineCount * (Math.abs(nx) >= 0.5D ? 1 : 0) * (int)Math.signum(nx));
/* 1214 */     returnVals[1] = (engineCount * (Math.abs(nz) > 0.5D ? 1 : 0) * (int)Math.signum(nz));
/* 1215 */     returnVals[2] = 0;
/*      */     
/* 1217 */     return returnVals;
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
/*      */   public boolean addWayPoint(Location loc)
/*      */   {
/* 1237 */     this.WayPoints.add(loc);
/* 1238 */     return true;
/*      */   }
/*      */   
/*      */ 
/* 1242 */   public void removeWayPoint(Location loc) { this.WayPoints.remove(loc); }
/*      */   
/*      */   public void WayPointTravel(boolean forward) {
/*      */     Location nextWaypoint;
/*      */     Location nextWaypoint;
/* 1247 */     if (forward) {
/* 1248 */       nextWaypoint = (Location)this.WayPoints.get(this.currentWayPoint + 1);
/*      */     } else {
/* 1250 */       nextWaypoint = (Location)this.WayPoints.get(this.currentWayPoint - 1);
/*      */     }
/* 1252 */     this.currentWayPoint += 1;
/* 1253 */     if ((forward) && (this.WayPoints.size() >= this.currentWayPoint))
/* 1254 */       forward = false;
/* 1255 */     if ((!forward) && (this.currentWayPoint == 0)) {
/* 1256 */       forward = true;
/*      */     }
/* 1258 */     Vector deviation = new Vector();
/* 1259 */     deviation.add(getLocation().toVector());
/* 1260 */     deviation.add(nextWaypoint.toVector());
/*      */     
/* 1262 */     plugin.getServer().getPlayer(this.driverName).sendMessage(deviation.toString());
/*      */   }
/*      */   
/*      */   public void WarpToWorld(World targetWorld) {
/* 1266 */     World oldWorld = this.world;
/* 1267 */     CraftMover cm = new CraftMover(this, plugin);
/*      */     
/*      */ 
/* 1270 */     this.world = targetWorld;
/*      */     
/*      */ 
/*      */ 
/* 1274 */     this.world = oldWorld;
/*      */     
/* 1276 */     for (int x = 0; x < this.sizeX; x++) {
/* 1277 */       for (int z = 0; z < this.sizeZ; z++) {
/* 1278 */         for (int y = 0; y < this.sizeY; y++) {
/* 1279 */           Block theBlock = cm.getWorldBlock(x, y, z);
/* 1280 */           theBlock.setTypeId(0);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1285 */     this.world = targetWorld;
/*      */   }
/*      */   
/*      */ 
/*      */   public void SelfDestruct(boolean justTheTip)
/*      */   {
/* 1291 */     for (int x = 0; x < this.sizeX; x++) {
/* 1292 */       for (int z = 0; z < this.sizeZ; z++) {
/* 1293 */         for (int y = 0; y < this.sizeY; y++) {
/* 1294 */           Block theBlock = this.world.getBlockAt(this.minX + x, this.minY + y, this.minZ + z);
/* 1295 */           theBlock.setType(Material.TNT);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public Location getLocation()
/*      */   {
/* 1303 */     return new Location(this.world, this.minX + this.sizeX / 2, this.minY + this.sizeY / 2, this.minZ + this.sizeZ / 2);
/*      */   }
/*      */   
/*      */   public Location getMinLocation() {
/* 1307 */     return new Location(this.world, this.minX, this.minY, this.minZ);
/*      */   }
/*      */   
/*      */   public Location getMaxLocation() {
/* 1311 */     return new Location(this.world, this.maxX, this.maxY, this.maxZ);
/*      */   }
/*      */   
/*      */   public void destroy() {
/* 1315 */     this.isDestroying = true;
/*      */     
/*      */ 
/* 1318 */     for (int x = 0; x < this.sizeX; x++) {
/* 1319 */       for (int z = 0; z < this.sizeZ; z++) {
/* 1320 */         for (int y = 0; y < this.sizeY; y++) {
/* 1321 */           if (isCraftBlock(x, y, z))
/*      */           {
/* 1323 */             Block theBlock = this.world.getBlockAt(this.minX + x, this.minY + y, this.minZ + z);
/* 1324 */             if (theBlock.getY() < 63)
/*      */             {
/* 1326 */               theBlock.setType(Material.WATER);
/*      */             } else {
/* 1328 */               theBlock.setType(Material.AIR);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1335 */     remove();
/* 1336 */     getCraftEntities(true);
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
/*      */   public void findFuel(Block block) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static int blockHardness(int blockID)
/*      */   {
/* 1359 */     if ((blockID == 7) || (blockID == 120) || (blockID == 130) || (blockID == 137))
/*      */     {
/* 1361 */       return 4; }
/* 1362 */     if ((blockID == 49) || (blockID == 116) || (blockID == 145))
/*      */     {
/* 1364 */       return 3; }
/* 1365 */     if ((blockID == 8) || (blockID == 9) || (blockID == 10) || (blockID == 11))
/*      */     {
/* 1367 */       return -3; }
/* 1368 */     if ((blockID == 1) || (blockID == 4) || (blockID == 23) || (blockID == 41) || (blockID == 42) || (blockID == 48) || (blockID == 57) || 
/* 1369 */       (blockID == 67) || (blockID == 71) || (blockID == 86) || (blockID == 98) || (blockID == 109) || (blockID == 112) || 
/* 1370 */       (blockID == 114) || (blockID == 121) || (blockID == 122) || (blockID == 133) || (blockID == 139) || (blockID == 155) || 
/* 1371 */       (blockID == 156) || (blockID == 159) || (blockID == 172))
/*      */     {
/* 1373 */       return 2; }
/* 1374 */     if ((blockID == 2) || (blockID == 3) || (blockID == 5) || (blockID == 12) || (blockID == 13) || (blockID == 14) || (blockID == 15) || (blockID == 16) || 
/* 1375 */       (blockID == 17) || (blockID == 19) || (blockID == 21) || (blockID == 22) || (blockID == 24) || (blockID == 25) || (blockID == 29) || (blockID == 33) || (blockID == 35) || 
/* 1376 */       (blockID == 43) || (blockID == 44) || (blockID == 45) || (blockID == 47) || (blockID == 53) || (blockID == 54) || (blockID == 56) || (blockID == 58) || (blockID == 60) || (blockID == 61) || 
/* 1377 */       (blockID == 62) || (blockID == 63) || (blockID == 64) || (blockID == 65) || (blockID == 68) || (blockID == 69) || (blockID == 70) || (blockID == 72) || (blockID == 73) || (blockID == 74) || 
/* 1378 */       (blockID == 77) || (blockID == 78) || (blockID == 79) || (blockID == 80) || (blockID == 82) || (blockID == 84) || (blockID == 85) || (blockID == 91) || (blockID == 96) || (blockID == 101) || (blockID == 107) || 
/* 1379 */       (blockID == 108) || (blockID == 110) || (blockID == 113) || (blockID == 117) || (blockID == 118) || (blockID == 125) || (blockID == 126) || 
/* 1380 */       (blockID == 128) || (blockID == 134) || (blockID == 135) || (blockID == 136) || (blockID == 138) || (blockID == 143) || (blockID == 146) || (blockID == 147) || (blockID == 148) || (blockID == 154) || (blockID == 158))
/*      */     {
/* 1382 */       return 1; }
/* 1383 */     if ((blockID == 46) || (blockID == 129) || (blockID == 152))
/*      */     {
/* 1385 */       return -1; }
/* 1386 */     if ((blockID == 153) || (blockID == 170) || (blockID == 173))
/*      */     {
/* 1388 */       return -2;
/*      */     }
/*      */     
/* 1391 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public static float blockWeight(int blockID)
/*      */   {
/* 1397 */     if ((blockID == 7) || (blockID == 49) || (blockID == 130))
/*      */     {
/* 1399 */       return 20.0F; }
/* 1400 */     if ((blockID == 1) || (blockID == 2) || (blockID == 3) || (blockID == 4) || (blockID == 12) || (blockID == 13) || (blockID == 22) || (blockID == 23) || 
/* 1401 */       (blockID == 24) || (blockID == 29) || (blockID == 33) || (blockID == 41) || (blockID == 42) || (blockID == 45) || (blockID == 46) || (blockID == 48) || (blockID == 57) || (blockID == 60) || (blockID == 61) || (blockID == 62) || 
/* 1402 */       (blockID == 67) || (blockID == 71) || (blockID == 78) || (blockID == 79) || (blockID == 80) || (blockID == 82) || (blockID == 86) || (blockID == 98) || 
/* 1403 */       (blockID == 108) || (blockID == 109) || (blockID == 110) || (blockID == 112) || (blockID == 114) || (blockID == 116) || (blockID == 120) || (blockID == 121) || (blockID == 128) || (blockID == 129) || (blockID == 133) || 
/* 1404 */       (blockID == 138) || (blockID == 139) || (blockID == 145) || (blockID == 152) || (blockID == 154) || (blockID == 155) || (blockID == 156) || (blockID == 158) || (blockID == 159) || (blockID == 172))
/*      */     {
/* 1406 */       return 1.0F; }
/* 1407 */     if ((blockID == 19) || (blockID == 43) || (blockID == 54) || (blockID == 58) || (blockID == 64) || (blockID == 91) || (blockID == 96) || (blockID == 101) || (blockID == 113) || 
/* 1408 */       (blockID == 117) || (blockID == 118) || (blockID == 137) || (blockID == 153) || (blockID == 170) || (blockID == 173))
/*      */     {
/* 1410 */       return 0.5F; }
/* 1411 */     if ((blockID == 5) || (blockID == 14) || (blockID == 15) || (blockID == 16) || (blockID == 17) || (blockID == 21) || (blockID == 25) || (blockID == 44) || (blockID == 47) || 
/* 1412 */       (blockID == 53) || (blockID == 56) || (blockID == 63) || (blockID == 65) || (blockID == 68) || (blockID == 73) || 
/* 1413 */       (blockID == 74) || (blockID == 84) || (blockID == 125) || (blockID == 126) || (blockID == 134) || (blockID == 135) || (blockID == 136))
/*      */     {
/* 1415 */       return 0.25F; }
/* 1416 */     if ((blockID == 35) || (blockID == 85) || (blockID == 107))
/*      */     {
/* 1418 */       return 0.17F; }
/* 1419 */     if ((blockID != 0) && ((blockID < 8) || (blockID > 11)))
/*      */     {
/* 1421 */       return 0.1F;
/*      */     }
/*      */     
/* 1424 */     return 0.0F;
/*      */   }
/*      */   
/*      */ 
/*      */   public static boolean getAttachedBlockExists(Block inBlock, int blockID, int data)
/*      */   {
/* 1430 */     if ((getAttachedBlock(inBlock, blockID, data).getTypeId() == 0) || ((getAttachedBlock(inBlock, blockID, data).getTypeId() >= 8) && (getAttachedBlock(inBlock, blockID, data).getTypeId() <= 11))) {
/* 1431 */       return false;
/*      */     }
/* 1433 */     return true;
/*      */   }
/*      */   
/*      */   public static Block getAttachedBlock(Block inBlock, int blockID, int data)
/*      */   {
/* 1438 */     if ((blockID == 75) || (blockID == 76) || (blockID == 50))
/*      */     {
/* 1440 */       if (data == 1)
/* 1441 */         return inBlock.getRelative(BlockFace.WEST);
/* 1442 */       if (data == 2)
/* 1443 */         return inBlock.getRelative(BlockFace.EAST);
/* 1444 */       if (data == 3)
/* 1445 */         return inBlock.getRelative(BlockFace.NORTH);
/* 1446 */       if (data == 4)
/* 1447 */         return inBlock.getRelative(BlockFace.SOUTH);
/* 1448 */       if (data == 5)
/* 1449 */         return inBlock.getRelative(BlockFace.DOWN);
/* 1450 */     } else if ((blockID == 65) || (blockID == 68))
/*      */     {
/* 1452 */       if (data == 2)
/* 1453 */         return inBlock.getRelative(BlockFace.SOUTH);
/* 1454 */       if (data == 3)
/* 1455 */         return inBlock.getRelative(BlockFace.NORTH);
/* 1456 */       if (data == 4)
/* 1457 */         return inBlock.getRelative(BlockFace.EAST);
/* 1458 */       if (data == 5)
/* 1459 */         return inBlock.getRelative(BlockFace.WEST);
/* 1460 */     } else { if (blockID == 63)
/*      */       {
/* 1462 */         return inBlock.getRelative(BlockFace.DOWN); }
/* 1463 */       if ((blockID == 69) || (blockID == 77) || (blockID == 143))
/*      */       {
/* 1465 */         if (data % 8 == 0)
/* 1466 */           return inBlock.getRelative(BlockFace.UP);
/* 1467 */         if (data % 8 == 1)
/* 1468 */           return inBlock.getRelative(BlockFace.WEST);
/* 1469 */         if (data % 8 == 2)
/* 1470 */           return inBlock.getRelative(BlockFace.EAST);
/* 1471 */         if (data % 8 == 3)
/* 1472 */           return inBlock.getRelative(BlockFace.NORTH);
/* 1473 */         if (data % 8 == 4)
/* 1474 */           return inBlock.getRelative(BlockFace.SOUTH);
/* 1475 */         if (data % 8 == 5)
/* 1476 */           return inBlock.getRelative(BlockFace.DOWN);
/* 1477 */         if (data % 8 == 6)
/* 1478 */           return inBlock.getRelative(BlockFace.DOWN);
/* 1479 */         if (data % 8 == 7)
/* 1480 */           return inBlock.getRelative(BlockFace.UP);
/* 1481 */       } else if ((blockID == 70) || (blockID == 72) || (blockID == 55) || (blockID == 64) || (blockID == 71))
/*      */       {
/* 1483 */         return inBlock.getRelative(BlockFace.DOWN);
/*      */       } }
/* 1485 */     return null;
/*      */   }
/*      */   
/*      */   public void speedChange(Player player, boolean increase)
/*      */   {
/* 1490 */     if (this.helmDestroyed)
/*      */     {
/* 1492 */       if (player != null)
/* 1493 */         player.sendMessage("Helm Control or Engines Destroyed!");
/* 1494 */       return;
/*      */     }
/* 1496 */     if (increase)
/*      */     {
/* 1498 */       if (!this.type.canFly)
/*      */       {
/* 1500 */         this.setSpeed += 1;
/* 1501 */         if (this.setSpeed > this.type.maxEngineSpeed)
/*      */         {
/* 1503 */           this.setSpeed = this.type.maxEngineSpeed;
/*      */         }
/* 1505 */         if (this.setSpeed == 1)
/*      */         {
/* 1507 */           if (player != null)
/* 1508 */             player.sendMessage("Starting Engines.");
/* 1509 */           if (!this.enginesOn)
/*      */           {
/* 1511 */             this.speed = 1;
/* 1512 */             this.enginesOn = true;
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/* 1525 */         this.setSpeed += 1;
/* 1526 */         if (this.setSpeed > this.type.maxEngineSpeed)
/*      */         {
/* 1528 */           this.setSpeed = this.type.maxEngineSpeed;
/*      */         }
/* 1530 */         if (this.setSpeed == 1)
/*      */         {
/* 1532 */           if (player != null)
/* 1533 */             player.sendMessage("Starting Engines.");
/* 1534 */           if (!this.enginesOn)
/*      */           {
/* 1536 */             this.speed = 1;
/* 1537 */             this.enginesOn = true;
/*      */           }
/*      */         }
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
/* 1550 */         if ((this.setSpeed > 1) && (this.gear == 1))
/*      */         {
/* 1552 */           this.setSpeed = 1;
/*      */         }
/*      */         
/*      */       }
/*      */     }
/* 1557 */     else if (!this.type.canFly)
/*      */     {
/* 1559 */       this.setSpeed -= 1;
/* 1560 */       if (this.setSpeed <= 0)
/*      */       {
/* 1562 */         CraftMover cm = new CraftMover(this, plugin);
/* 1563 */         if ((NavyCraft.checkSpawnRegion(new Location(this.world, this.minX, this.minY, this.minZ))) || (NavyCraft.checkSpawnRegion(new Location(this.world, this.maxX, this.maxY, this.maxZ))))
/*      */         {
/* 1565 */           if (player != null)
/* 1566 */             player.sendMessage("Cannot stop engines until clear of safe dock area.");
/* 1567 */           this.setSpeed += 1;
/* 1568 */           return;
/*      */         }
/*      */         
/* 1571 */         this.setSpeed = 0;
/* 1572 */         this.turnProgress = 0;
/* 1573 */         this.rudder = 0;
/* 1574 */         player.sendMessage("Stopping Engines...");
/* 1575 */         this.enginesOn = false;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1580 */       this.setSpeed -= 1;
/* 1581 */       if ((this.setSpeed == 0) && ((this.gear > 1) || (!this.onGround)))
/*      */       {
/* 1583 */         this.setSpeed += 1;
/* 1584 */         if (player != null)
/* 1585 */           player.sendMessage("Can't reduce speed to zero in this gear");
/*      */       }
/* 1587 */       if (this.setSpeed <= 0)
/*      */       {
/* 1589 */         CraftMover cm = new CraftMover(this, plugin);
/* 1590 */         if ((NavyCraft.checkSpawnRegion(new Location(this.world, this.minX, this.minY, this.minZ))) || (NavyCraft.checkSpawnRegion(new Location(this.world, this.maxX, this.maxY, this.maxZ))))
/*      */         {
/* 1592 */           if (player != null)
/* 1593 */             player.sendMessage("Cannot stop engines until clear of safe dock area.");
/* 1594 */           this.setSpeed += 1;
/* 1595 */           return;
/*      */         }
/* 1597 */         this.setSpeed = 0;
/* 1598 */         this.turnProgress = 0;
/* 1599 */         this.rudder = 0;
/* 1600 */         if (player != null)
/* 1601 */           player.sendMessage("Stopping Engines...");
/* 1602 */         this.enginesOn = false;
/* 1603 */         return;
/*      */       }
/*      */     }
/*      */     
/* 1607 */     if (player != null)
/*      */     {
/* 1609 */       if (this.type.canFly)
/*      */       {
/* 1611 */         player.sendMessage("Throttle-" + this.setSpeed * 10 + "%");
/* 1612 */       } else if (this.type.isTerrestrial)
/*      */       {
/* 1614 */         player.sendMessage("Throttle-" + this.setSpeed * 25 + "%");
/*      */ 
/*      */       }
/* 1617 */       else if (this.setSpeed == 0)
/*      */       {
/* 1619 */         player.sendMessage("All Stop");
/* 1620 */       } else if (this.setSpeed == 1)
/*      */       {
/* 1622 */         player.sendMessage("Engines Slow");
/* 1623 */       } else if (this.setSpeed == 2)
/*      */       {
/* 1625 */         player.sendMessage("Engines 1/3");
/* 1626 */       } else if (this.setSpeed == 3)
/*      */       {
/* 1628 */         player.sendMessage("Engines 2/3");
/* 1629 */       } else if (this.setSpeed == 4)
/*      */       {
/* 1631 */         player.sendMessage("Engines Standard");
/* 1632 */       } else if (this.setSpeed == 5)
/*      */       {
/* 1634 */         player.sendMessage("Engines Full");
/* 1635 */       } else if (this.setSpeed == 6)
/*      */       {
/* 1637 */         player.sendMessage("Engines Flank!");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void gearChange(Player player, boolean increase)
/*      */   {
/* 1645 */     if (this.helmDestroyed)
/*      */     {
/* 1647 */       if (player != null)
/* 1648 */         player.sendMessage("Helm Control or Engines Destroyed!");
/* 1649 */       return;
/*      */     }
/* 1651 */     if (increase)
/*      */     {
/* 1653 */       if (!this.type.canFly)
/*      */       {
/* 1655 */         this.gear += 1;
/* 1656 */         if (this.gear == 0)
/*      */         {
/* 1658 */           this.gear += 1;
/*      */         }
/* 1660 */         if (this.gear > this.type.maxForwardGear)
/*      */         {
/* 1662 */           this.gear = this.type.maxForwardGear;
/*      */         }
/* 1664 */         if (this.gear == 1)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1674 */           if (this.isMoving)
/*      */           {
/* 1676 */             this.gear = -1;
/* 1677 */             if (player != null) {
/* 1678 */               player.sendMessage("Stop moving before changing to forward gears.");
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1685 */         this.gear += 1;
/* 1686 */         if (this.gear == 0)
/*      */         {
/* 1688 */           this.gear += 1;
/*      */         }
/* 1690 */         if (this.gear > this.type.maxForwardGear)
/*      */         {
/* 1692 */           this.gear = this.type.maxForwardGear;
/*      */         }
/* 1694 */         if (this.gear == 1)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1705 */           if (this.isMoving)
/*      */           {
/* 1707 */             this.gear = -1;
/* 1708 */             if (player != null)
/* 1709 */               player.sendMessage("Stop moving before changing to forward gears.");
/* 1710 */             return;
/*      */           }
/*      */         }
/* 1713 */         if ((this.gear == 2) && (this.onGround))
/*      */         {
/* 1715 */           this.turnProgress = 0;
/* 1716 */           this.rudder = 0;
/* 1717 */           if (player != null) {
/* 1718 */             player.sendMessage("Ready for takeoff!");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1723 */     else if (!this.type.canFly)
/*      */     {
/* 1725 */       this.gear -= 1;
/* 1726 */       if (this.gear == 0)
/*      */       {
/* 1728 */         this.gear -= 1;
/*      */       }
/*      */       
/* 1731 */       if (this.gear == -1)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1742 */         if (this.isMoving)
/*      */         {
/* 1744 */           this.gear = 1;
/* 1745 */           if (player != null) {
/* 1746 */             player.sendMessage("Stop moving before changing to reverse gears.");
/*      */           }
/*      */         }
/* 1749 */       } else if (this.gear <= this.type.maxReverseGear)
/*      */       {
/* 1751 */         this.gear = this.type.maxReverseGear;
/*      */       }
/*      */     }
/*      */     else {
/* 1755 */       this.gear -= 1;
/* 1756 */       if (this.gear == 0)
/*      */       {
/* 1758 */         this.gear -= 1;
/*      */       }
/* 1760 */       if (this.gear == -1)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1771 */         if (this.isMoving)
/*      */         {
/* 1773 */           this.gear = 1;
/* 1774 */           if (player != null)
/* 1775 */             player.sendMessage("Stop moving before changing to reverse gears.");
/* 1776 */           return;
/*      */         }
/*      */       }
/* 1779 */       if ((this.gear == 1) && ((!this.onGround) || (this.setSpeed != 1)))
/*      */       {
/* 1781 */         if (player != null)
/* 1782 */           player.sendMessage("Must be on ground and engine at idle to shift into 1...");
/* 1783 */         this.gear += 1;
/* 1784 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1789 */     if (player != null) {
/* 1790 */       player.sendMessage("Set engines to Gear-(" + this.gear + ")");
/*      */     }
/*      */   }
/*      */   
/*      */   public void rudderChange(Player player, int order, boolean turn) {
/* 1795 */     if (this.helmDestroyed)
/*      */     {
/* 1797 */       if (player != null)
/* 1798 */         player.sendMessage("Helm Control or Engines Destroyed!");
/* 1799 */       return;
/*      */     }
/*      */     
/* 1802 */     if ((this.setSpeed == 0) || (this.gear <= 0))
/*      */     {
/* 1804 */       if (player != null)
/* 1805 */         player.sendMessage("You have to be moving forward to turn.");
/* 1806 */       return;
/*      */     }
/*      */     
/* 1809 */     CraftMover cm = new CraftMover(this, plugin);
/* 1810 */     if ((NavyCraft.checkSpawnRegion(new Location(this.world, this.minX, this.minY, this.minZ))) || (NavyCraft.checkSpawnRegion(new Location(this.world, this.maxX, this.maxY, this.maxZ))))
/*      */     {
/* 1812 */       if (player != null)
/* 1813 */         player.sendMessage("Rudder disabled in safe dock area.");
/* 1814 */       return;
/*      */     }
/*      */     
/* 1817 */     if ((this.type.canFly) && (this.gear > 1) && (this.onGround))
/*      */     {
/* 1819 */       if (player != null)
/* 1820 */         player.sendMessage("You can't turn while taking off.");
/* 1821 */       return;
/*      */     }
/*      */     
/* 1824 */     if (order == 1)
/*      */     {
/* 1826 */       if ((this.rudder == 0) || ((this.rudder == 1) && (turn) && (this.turnProgress == 0)))
/*      */       {
/* 1828 */         this.rudder = 1;
/* 1829 */         if (turn)
/*      */         {
/* 1831 */           this.turnProgress = this.type.turnRadius;
/* 1832 */           if (player != null) {
/* 1833 */             player.sendMessage("Rudder Turning Right");
/*      */           }
/*      */         }
/* 1836 */         else if (player != null) {
/* 1837 */           player.sendMessage("Rudder Right");
/*      */         }
/*      */       }
/* 1840 */       else if (this.rudder == -1)
/*      */       {
/* 1842 */         if ((this.turnProgress == 0) || (this.turnProgress > this.type.turnRadius / 2))
/*      */         {
/* 1844 */           this.rudder = 0;
/* 1845 */           this.turnProgress = 0;
/* 1846 */           if (player != null) {
/* 1847 */             player.sendMessage("Rudder Centered");
/*      */           }
/*      */         }
/* 1850 */         else if (player != null) {
/* 1851 */           player.sendMessage("Too late to cancel turn, please wait.");
/*      */         }
/*      */         
/*      */       }
/* 1855 */       else if (player != null) {
/* 1856 */         player.sendMessage("Rudder already set. Look other way to cancel.");
/*      */       }
/* 1858 */     } else if (order == -1)
/*      */     {
/* 1860 */       if ((this.rudder == 0) || ((this.rudder == -1) && (turn) && (this.turnProgress == 0)))
/*      */       {
/* 1862 */         this.rudder = -1;
/* 1863 */         if (turn)
/*      */         {
/* 1865 */           this.turnProgress = this.type.turnRadius;
/* 1866 */           if (player != null) {
/* 1867 */             player.sendMessage("Rudder Turning Left");
/*      */           }
/*      */         }
/* 1870 */         else if (player != null) {
/* 1871 */           player.sendMessage("Rudder Left");
/*      */         }
/* 1873 */       } else if (this.rudder == 1)
/*      */       {
/* 1875 */         if ((this.turnProgress == 0) || (this.turnProgress > this.type.turnRadius / 2))
/*      */         {
/* 1877 */           this.rudder = 0;
/* 1878 */           this.turnProgress = 0;
/* 1879 */           if (player != null) {
/* 1880 */             player.sendMessage("Rudder Centered");
/*      */           }
/*      */         }
/* 1883 */         else if (player != null) {
/* 1884 */           player.sendMessage("Too late to cancel turn, please wait.");
/*      */         }
/*      */         
/*      */       }
/* 1888 */       else if (player != null) {
/* 1889 */         player.sendMessage("Rudder already set. Look other way to cancel.");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void leaveCrew(Player player)
/*      */   {
/* 1896 */     this.isNameOnBoard.remove(player.getName());
/* 1897 */     this.crewNames.remove(player.getName());
/*      */     
/* 1899 */     for (String s : this.crewNames)
/*      */     {
/* 1901 */       Player p = plugin.getServer().getPlayer(s);
/* 1902 */       if (p != null)
/*      */       {
/* 1904 */         p.sendMessage(ChatColor.LIGHT_PURPLE + ChatColor.ITALIC + player.getName() + " has left your crew.");
/*      */       }
/*      */     }
/*      */     
/* 1908 */     if (player.getName() == this.captainName)
/*      */     {
/* 1910 */       for (String s : this.crewNames)
/*      */       {
/* 1912 */         Player p = plugin.getServer().getPlayer(s);
/* 1913 */         if (p != null)
/*      */         {
/* 1915 */           p.sendMessage(ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Your crew has been disbanded.");
/*      */         }
/*      */       }
/* 1918 */       releaseCraft();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public static void takeoverTimerThread(final Player player, Craft craft)
/*      */   {
/* 1925 */     craft.takingOver = true;
/* 1926 */     playerAbandonTimers.put(player, new Thread()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/* 1931 */         setPriority(1);
/*      */         try
/*      */         {
/* 1934 */           for (int i = 0; i < 24; i++)
/*      */           {
/* 1936 */             sleep(5000L);
/* 1937 */             if (!Craft.this.cancelTakeoverTimer) {
/* 1938 */               Craft.takeoverTimerUpdate(player, Craft.this, i);
/*      */             } else
/* 1940 */               i = 24;
/*      */           }
/* 1942 */           Craft.this.cancelTakeoverTimer = false;
/* 1943 */           Craft.this.takingOver = false;
/*      */         } catch (InterruptedException e) {
/* 1945 */           e.printStackTrace();
/*      */         }
/*      */       }
/* 1948 */     });
/* 1949 */     ((Thread)playerAbandonTimers.get(player)).start();
/*      */   }
/*      */   
/*      */   public static void takeoverTimerUpdate(final Player player, final Craft craft, int i)
/*      */   {
/* 1954 */     plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/* 1959 */         if (this.val$i < 23)
/*      */         {
/* 1961 */           if (!craft.abandoned)
/*      */           {
/* 1963 */             craft.cancelTakeoverTimer = true;
/* 1964 */             player.sendMessage(ChatColor.RED + ChatColor.BOLD + "Takeover failed!");
/*      */           }
/*      */           
/*      */         }
/* 1968 */         else if ((craft.abandoned) && (craft.isOnCraft(player, false)) && (!craft.crewNames.contains(player.getName())))
/*      */         {
/* 1970 */           if (craft.customName != null)
/*      */           {
/* 1972 */             if (craft.captainName != null) {
/* 1973 */               Craft.plugin.getServer().broadcastMessage(ChatColor.WHITE + craft.captainName + ChatColor.YELLOW + "'s " + ChatColor.WHITE + craft.customName + ChatColor.YELLOW + " was abandoned.");
/*      */             } else {
/* 1975 */               Craft.plugin.getServer().broadcastMessage(ChatColor.YELLOW + "The " + ChatColor.WHITE + craft.customName + ChatColor.YELLOW + " was abandoned.");
/*      */             }
/*      */           }
/* 1978 */           else if (craft.captainName != null) {
/* 1979 */             Craft.plugin.getServer().broadcastMessage(ChatColor.WHITE + craft.captainName + ChatColor.YELLOW + "'s " + ChatColor.WHITE + craft.name + ChatColor.YELLOW + " was abandoned.");
/*      */           } else {
/* 1981 */             Craft.plugin.getServer().broadcastMessage(ChatColor.YELLOW + "The " + ChatColor.WHITE + craft.name + ChatColor.YELLOW + " was abandoned.");
/*      */           }
/* 1983 */           player.sendMessage(ChatColor.GREEN + ChatColor.BOLD + "Vehicle abandoned! You may now take command.");
/* 1984 */           craft.releaseCraft();
/*      */         }
/*      */         else {
/* 1987 */           player.sendMessage(ChatColor.RED + ChatColor.BOLD + "Takeover failed!");
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isDressed(Player p)
/*      */   {
/* 2000 */     if (this.type.canFly)
/*      */     {
/* 2002 */       if ((p.getInventory().getHelmet() != null) && (p.getInventory().getHelmet().getType() == Material.CHAINMAIL_HELMET))
/*      */       {
/* 2004 */         if ((p.getInventory().getChestplate() != null) && (p.getInventory().getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE))
/*      */         {
/* 2006 */           if ((p.getInventory().getLeggings() != null) && (p.getInventory().getLeggings().getType() == Material.CHAINMAIL_LEGGINGS))
/*      */           {
/* 2008 */             if ((p.getInventory().getBoots() != null) && (p.getInventory().getBoots().getType() == Material.CHAINMAIL_BOOTS))
/*      */             {
/* 2010 */               return true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2015 */       p.sendMessage(ChatColor.RED + "You need to wear pilot (chainmail) uniform to use this.");
/* 2016 */       return false; }
/* 2017 */     if (this.type.isTerrestrial)
/*      */     {
/* 2019 */       if ((p.getInventory().getHelmet() != null) && (p.getInventory().getHelmet().getType() == Material.IRON_HELMET))
/*      */       {
/* 2021 */         if ((p.getInventory().getChestplate() != null) && (p.getInventory().getChestplate().getType() == Material.IRON_CHESTPLATE))
/*      */         {
/* 2023 */           if ((p.getInventory().getLeggings() != null) && (p.getInventory().getLeggings().getType() == Material.IRON_LEGGINGS))
/*      */           {
/* 2025 */             if ((p.getInventory().getBoots() != null) && (p.getInventory().getBoots().getType() == Material.IRON_BOOTS))
/*      */             {
/* 2027 */               return true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2032 */       p.sendMessage(ChatColor.RED + "You need to wear soldier (iron) uniform to use this.");
/* 2033 */       return false;
/*      */     }
/*      */     
/* 2036 */     if ((p.getInventory().getHelmet() != null) && ((p.getInventory().getHelmet().getType() == Material.LEATHER_HELMET) || (p.getInventory().getHelmet().getType() == Material.GOLD_HELMET) || (p.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET)))
/*      */     {
/* 2038 */       if ((p.getInventory().getChestplate() != null) && ((p.getInventory().getChestplate().getType() == Material.LEATHER_CHESTPLATE) || (p.getInventory().getChestplate().getType() == Material.GOLD_CHESTPLATE) || (p.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE)))
/*      */       {
/* 2040 */         if ((p.getInventory().getLeggings() != null) && ((p.getInventory().getLeggings().getType() == Material.LEATHER_LEGGINGS) || (p.getInventory().getLeggings().getType() == Material.GOLD_LEGGINGS) || (p.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS)))
/*      */         {
/* 2042 */           if ((p.getInventory().getBoots() != null) && ((p.getInventory().getBoots().getType() == Material.LEATHER_BOOTS) || (p.getInventory().getBoots().getType() == Material.GOLD_BOOTS) || (p.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS)))
/*      */           {
/* 2044 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2049 */     p.sendMessage(ChatColor.RED + "You need to wear sailor (leather) or captain (gold) uniform to use this.");
/* 2050 */     return false;
/*      */   }
/*      */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\Craft.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */