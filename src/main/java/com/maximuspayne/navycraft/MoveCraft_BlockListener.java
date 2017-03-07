/*      */ package com.maximuspayne.navycraft;
/*      */ 
/*      */ import com.earth2me.essentials.Essentials;
/*      */ import com.sk89q.worldedit.BlockVector;
/*      */ import com.sk89q.worldedit.CuboidClipboard;
/*      */ import com.sk89q.worldedit.EditSession;
/*      */ import com.sk89q.worldedit.EmptyClipboardException;
/*      */ import com.sk89q.worldedit.LocalSession;
/*      */ import com.sk89q.worldedit.MaxChangedBlocksException;
/*      */ import com.sk89q.worldedit.Vector;
/*      */ import com.sk89q.worldedit.bukkit.WorldEditPlugin;
/*      */ import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
/*      */ import com.sk89q.worldedit.extent.clipboard.Clipboard;
/*      */ import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
/*      */ import com.sk89q.worldedit.function.operation.Operations;
/*      */ import com.sk89q.worldedit.math.transform.AffineTransform;
/*      */ import com.sk89q.worldedit.regions.CuboidRegion;
/*      */ import com.sk89q.worldedit.regions.Region;
/*      */ import com.sk89q.worldedit.schematic.SchematicFormat;
/*      */ import com.sk89q.worldedit.session.ClipboardHolder;
/*      */ import com.sk89q.worldedit.session.PasteBuilder;
/*      */ import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
/*      */ import com.sk89q.worldguard.domains.DefaultDomain;
/*      */ import com.sk89q.worldguard.protection.managers.RegionManager;
/*      */ import com.sk89q.worldguard.protection.managers.storage.StorageException;
/*      */ import com.sk89q.worldguard.protection.regions.ProtectedRegion;
/*      */ import java.io.File;
/*      */ import java.io.PrintStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import org.anjocaido.groupmanager.GroupManager;
/*      */ import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
/*      */ import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
/*      */ import org.bukkit.ChatColor;
/*      */ import org.bukkit.GameMode;
/*      */ import org.bukkit.Location;
/*      */ import org.bukkit.Material;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.Sound;
/*      */ import org.bukkit.block.Block;
/*      */ import org.bukkit.block.BlockFace;
/*      */ import org.bukkit.block.Sign;
/*      */ import org.bukkit.entity.HumanEntity;
/*      */ import org.bukkit.event.EventHandler;
/*      */ import org.bukkit.event.EventPriority;
/*      */ import org.bukkit.event.block.BlockBreakEvent;
/*      */ import org.bukkit.event.block.BlockDispenseEvent;
/*      */ import org.bukkit.event.block.BlockFromToEvent;
/*      */ import org.bukkit.event.block.BlockPhysicsEvent;
/*      */ import org.bukkit.event.block.BlockPlaceEvent;
/*      */ import org.bukkit.event.block.BlockRedstoneEvent;
/*      */ import org.bukkit.event.block.SignChangeEvent;
/*      */ import org.bukkit.event.inventory.InventoryClickEvent;
/*      */ import org.bukkit.inventory.ItemStack;
/*      */ import org.bukkit.plugin.Plugin;
/*      */ import org.bukkit.plugin.PluginManager;
/*      */ 
/*      */ public class MoveCraft_BlockListener implements org.bukkit.event.Listener
/*      */ {
/*   64 */   public static Craft updatedCraft = null;
/*      */   private static Plugin plugin;
/*      */   public static WorldEditPlugin wep;
/*      */   public static WorldGuardPlugin wgp;
/*   68 */   public static int lastSpawn = -1;
/*      */   
/*      */   public MoveCraft_BlockListener(Plugin p) {
/*   71 */     plugin = p;
/*      */   }
/*      */   
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void onBlockPlace(BlockPlaceEvent event) {
/*   76 */     Craft theCraft = Craft.getPlayerCraft(event.getPlayer());
/*      */     
/*      */ 
/*   79 */     if (theCraft != null) {
/*   80 */       theCraft.addBlock(event.getBlock(), false);
/*      */     } else {
/*   82 */       theCraft = Craft.getCraft(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
/*   83 */       if (theCraft != null) {
/*   84 */         theCraft.addBlock(event.getBlock(), false);
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
/*      */   public static void ClickedASign(org.bukkit.entity.Player player, Block block, boolean leftClick)
/*      */   {
/*  101 */     Craft playerCraft = Craft.getPlayerCraft(player);
/*      */     
/*  103 */     Sign sign = (Sign)block.getState();
/*      */     
/*  105 */     if ((sign.getLine(0) == null) || (sign.getLine(0).trim().equals(""))) { return;
/*      */     }
/*  107 */     String craftTypeName = sign.getLine(0).trim().toLowerCase();
/*      */     
/*      */ 
/*  110 */     craftTypeName = craftTypeName.replaceAll(ChatColor.BLUE.toString(), "");
/*  111 */     int lotType = 0;
/*      */     
/*      */ 
/*  114 */     if (craftTypeName.startsWith("[")) {
/*  115 */       craftTypeName = craftTypeName.substring(1, craftTypeName.length() - 1);
/*      */     }
/*      */     
/*  118 */     if ((craftTypeName.equalsIgnoreCase("*select*")) && (block.getRelative(BlockFace.DOWN, 1).getTypeId() == 22))
/*      */     {
/*  120 */       BlockFace bf = null;
/*      */       
/*  122 */       switch (block.getData()) {
/*      */       case 8: 
/*  124 */         bf = BlockFace.SOUTH;
/*      */         
/*  126 */         break;
/*      */       case 0: 
/*  128 */         bf = BlockFace.NORTH;
/*      */         
/*  130 */         break;
/*      */       case 4: 
/*  132 */         bf = BlockFace.EAST;
/*      */         
/*  134 */         break;
/*      */       case 12: 
/*  136 */         bf = BlockFace.WEST;
/*      */         
/*  138 */         break;
/*      */       }
/*      */       
/*      */       
/*      */ 
/*  143 */       if (bf == null) {
/*  144 */         player.sendMessage("Sign error...check direction?");
/*  145 */         return;
/*      */       }
/*      */       
/*  148 */       if (block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getTypeId() == 68) {
/*  149 */         String spawnName = sign.getLine(3).trim().toLowerCase();
/*  150 */         Sign sign2 = (Sign)block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getState();
/*  151 */         String restrictedName = sign2.getLine(0).trim().toLowerCase();
/*  152 */         String rankStr = sign2.getLine(1).trim().toLowerCase();
/*  153 */         String idStr = sign2.getLine(2).trim().toLowerCase();
/*  154 */         String lotStr = sign2.getLine(3).trim().toLowerCase();
/*  155 */         spawnName = spawnName.replaceAll(ChatColor.BLUE.toString(), "");
/*  156 */         restrictedName = restrictedName.replaceAll(ChatColor.BLUE.toString(), "");
/*  157 */         rankStr = rankStr.replaceAll(ChatColor.BLUE.toString(), "");
/*  158 */         idStr = idStr.replaceAll(ChatColor.BLUE.toString(), "");
/*  159 */         lotStr = lotStr.replaceAll(ChatColor.BLUE.toString(), "");
/*      */         
/*  161 */         if (spawnName.isEmpty()) {
/*  162 */           player.sendMessage("Sign error...no type");
/*  163 */           return;
/*      */         }
/*      */         
/*  166 */         int rankReq = -1;
/*      */         try {
/*  168 */           rankReq = Integer.parseInt(rankStr);
/*      */         } catch (NumberFormatException nfe) {
/*  170 */           player.sendMessage("Sign error...invaild rank number");
/*  171 */           return;
/*      */         }
/*      */         
/*  174 */         if ((rankReq < 1) || (rankReq > 6)) {
/*  175 */           player.sendMessage("Sign error...invalid rank requirement");
/*  176 */           return;
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
/*      */ 
/*  189 */         if ((lotStr.equalsIgnoreCase("DD")) || (lotStr.equalsIgnoreCase("SHIP1"))) {
/*  190 */           lotType = 1;
/*  191 */         } else if ((lotStr.equalsIgnoreCase("SUB1")) || (lotStr.equalsIgnoreCase("SHIP2"))) {
/*  192 */           lotType = 2;
/*  193 */         } else if ((lotStr.equalsIgnoreCase("SUB2")) || (lotStr.equalsIgnoreCase("SHIP3"))) {
/*  194 */           lotType = 3;
/*  195 */         } else if ((lotStr.equalsIgnoreCase("CL")) || (lotStr.equalsIgnoreCase("SHIP4"))) {
/*  196 */           lotType = 4;
/*  197 */         } else if ((lotStr.equalsIgnoreCase("CA")) || (lotStr.equalsIgnoreCase("SHIP5"))) {
/*  198 */           lotType = 5;
/*  199 */         } else if (lotStr.equalsIgnoreCase("HANGAR1")) {
/*  200 */           lotType = 6;
/*  201 */         } else if (lotStr.equalsIgnoreCase("HANGAR2")) {
/*  202 */           lotType = 7;
/*  203 */         } else if (lotStr.equalsIgnoreCase("TANK1")) {
/*  204 */           lotType = 8;
/*      */         } else {
/*  206 */           player.sendMessage("Sign error...lot type");
/*  207 */           return;
/*      */         }
/*      */         
/*  210 */         String ownerName = sign.getLine(1) + sign.getLine(2);
/*      */         
/*  212 */         if ((!restrictedName.isEmpty()) && (!restrictedName.equalsIgnoreCase("Public")) && (!restrictedName.equalsIgnoreCase(player.getName())) && (!ownerName.equalsIgnoreCase(player.getName())) && (!player.isOp()) && (!com.maximuspayne.navycraft.plugins.PermissionInterface.CheckQuietPermission(player, "movecraft.select")))
/*      */         {
/*  214 */           int tpId = -1;
/*      */           try {
/*  216 */             tpId = Integer.parseInt(idStr);
/*      */           } catch (NumberFormatException e) {
/*  218 */             player.sendMessage("Invalid plot id");
/*  219 */             return;
/*      */           }
/*      */           
/*  222 */           if (tpId > -1) {
/*  223 */             loadShipyard();
/*      */             
/*  225 */             Sign foundSign = null;
/*  226 */             foundSign = findSign(ownerName, tpId);
/*  227 */             if ((foundSign != null) && (foundSign.getLocation().equals(sign.getLocation()))) {
/*  228 */               wgp = (WorldGuardPlugin)plugin.getServer().getPluginManager().getPlugin("WorldGuard");
/*  229 */               if (wgp != null) {
/*  230 */                 RegionManager regionManager = wgp.getRegionManager(plugin.getServer().getWorld("shipyard"));
/*  231 */                 String regionName = "--" + ownerName + "-" + tpId;
/*      */                 
/*  233 */                 if ((regionManager.getRegion(regionName) != null) && (!regionManager.getRegion(regionName).getMembers().contains(player.getName()))) {
/*  234 */                   player.sendMessage("You are not allowed to select this plot.");
/*      */                 }
/*      */               }
/*      */             }
/*      */             else {
/*  239 */               player.sendMessage("You are not allowed to select this plot.");
/*      */             }
/*      */           }
/*      */           else {
/*  243 */             player.sendMessage("Invalid plot id");
/*  244 */             return;
/*      */           }
/*      */         }
/*      */         
/*  248 */         wep = (WorldEditPlugin)plugin.getServer().getPluginManager().getPlugin("WorldEdit");
/*  249 */         if (wep == null) {
/*  250 */           player.sendMessage("WorldEdit error");
/*  251 */           return;
/*      */         }
/*      */         
/*  254 */         EditSession es = wep.createEditSession(player);
/*      */         
/*      */         int offsetZ;
/*      */         
/*  258 */         if (lotType == 1) {
/*  259 */           Location loc = block.getRelative(bf, 28).getLocation();
/*  260 */           int sizeX = 13;
/*  261 */           int sizeY = 28;
/*  262 */           int sizeZ = 28;
/*  263 */           int originX = 0;
/*  264 */           int originY = -8;
/*  265 */           int originZ = 0;
/*  266 */           int offsetX = 0;
/*  267 */           int offsetY = -7;
/*  268 */           offsetZ = -29; } else { int offsetZ;
/*  269 */           if (lotType == 2) {
/*  270 */             Location loc = block.getRelative(bf, 43).getLocation();
/*  271 */             int sizeX = 9;
/*  272 */             int sizeY = 28;
/*  273 */             int sizeZ = 43;
/*  274 */             int originX = 0;
/*  275 */             int originY = -8;
/*  276 */             int originZ = 0;
/*  277 */             int offsetX = 0;
/*  278 */             int offsetY = -7;
/*  279 */             offsetZ = -44; } else { int offsetZ;
/*  280 */             if (lotType == 3) {
/*  281 */               Location loc = block.getRelative(bf, 70).getLocation();
/*  282 */               int sizeX = 11;
/*  283 */               int sizeY = 28;
/*  284 */               int sizeZ = 70;
/*  285 */               int originX = 0;
/*  286 */               int originY = -8;
/*  287 */               int originZ = 0;
/*  288 */               int offsetX = 0;
/*  289 */               int offsetY = -7;
/*  290 */               offsetZ = -71; } else { int offsetZ;
/*  291 */               if (lotType == 4) {
/*  292 */                 Location loc = block.getRelative(bf, 55).getLocation();
/*  293 */                 int sizeX = 17;
/*  294 */                 int sizeY = 28;
/*  295 */                 int sizeZ = 55;
/*  296 */                 int originX = 0;
/*  297 */                 int originY = -8;
/*  298 */                 int originZ = 0;
/*  299 */                 int offsetX = 0;
/*  300 */                 int offsetY = -7;
/*  301 */                 offsetZ = -56; } else { int offsetZ;
/*  302 */                 if (lotType == 5) {
/*  303 */                   Location loc = block.getRelative(bf, 98).getLocation();
/*  304 */                   int sizeX = 17;
/*  305 */                   int sizeY = 28;
/*  306 */                   int sizeZ = 98;
/*  307 */                   int originX = 0;
/*  308 */                   int originY = -8;
/*  309 */                   int originZ = 0;
/*  310 */                   int offsetX = 0;
/*  311 */                   int offsetY = -7;
/*  312 */                   offsetZ = -99; } else { int offsetZ;
/*  313 */                   if (lotType == 6) {
/*  314 */                     Location loc = block.getRelative(bf, 17).getLocation();
/*  315 */                     int sizeX = 17;
/*  316 */                     int sizeY = 7;
/*  317 */                     int sizeZ = 19;
/*  318 */                     int originX = 0;
/*  319 */                     int originY = -1;
/*  320 */                     int originZ = -18;
/*  321 */                     int offsetX = -17;
/*  322 */                     int offsetY = 0;
/*  323 */                     offsetZ = -20; } else { int offsetZ;
/*  324 */                     if (lotType == 7) {
/*  325 */                       Location loc = block.getRelative(bf, 25).getLocation();
/*  326 */                       int sizeX = 25;
/*  327 */                       int sizeY = 7;
/*  328 */                       int sizeZ = 32;
/*  329 */                       int originX = 0;
/*  330 */                       int originY = -1;
/*  331 */                       int originZ = -31;
/*  332 */                       int offsetX = -25;
/*  333 */                       int offsetY = 0;
/*  334 */                       offsetZ = -33; } else { int offsetZ;
/*  335 */                       if (lotType == 8) {
/*  336 */                         Location loc = block.getRelative(bf, 12).getLocation();
/*  337 */                         int sizeX = 12;
/*  338 */                         int sizeY = 7;
/*  339 */                         int sizeZ = 19;
/*  340 */                         int originX = 0;
/*  341 */                         int originY = -1;
/*  342 */                         int originZ = -18;
/*  343 */                         int offsetX = -12;
/*  344 */                         int offsetY = 0;
/*  345 */                         offsetZ = -20;
/*      */                       }
/*      */                       else
/*      */                       {
/*  349 */                         player.sendMessage("Sign error...invalid lot"); return; } } } } } } } }
/*      */         int offsetZ;
/*      */         int offsetY;
/*      */         int offsetX;
/*  353 */         int originZ; int originY; int originX; int sizeZ; int sizeY; int sizeX; Location loc; CuboidRegion region = new CuboidRegion(new Vector(loc.getBlockX() + originX, loc.getBlockY() + originY, loc.getBlockZ() + originZ), new Vector(loc.getBlockX() + originX + sizeX - 1, loc.getBlockY() + originY + sizeY - 1, loc.getBlockZ() + originZ + sizeZ - 1));
/*      */         
/*  355 */         BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
/*      */         try
/*      */         {
/*  358 */           if ((lotType >= 6) && (lotType <= 8)) {
/*  359 */             clipboard.setOrigin(new Vector(block.getX() + 1, block.getY(), block.getZ() - sizeZ + 1));
/*      */           } else {
/*  361 */             clipboard.setOrigin(new Vector(loc.getX(), loc.getY(), loc.getZ()));
/*      */           }
/*      */           
/*  364 */           ForwardExtentCopy copy = new ForwardExtentCopy(es, region, clipboard, region.getMinimumPoint());
/*  365 */           Operations.completeLegacy(copy);
/*  366 */           wep.getSession(player).setClipboard(new ClipboardHolder(clipboard, es.getWorld().getWorldData()));
/*  367 */           Craft.playerClipboards.put(player, wep.getSession(player).getClipboard());
/*      */         }
/*      */         catch (MaxChangedBlocksException e)
/*      */         {
/*  371 */           e.printStackTrace();
/*      */         }
/*      */         catch (EmptyClipboardException e) {
/*  374 */           e.printStackTrace();
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
/*  406 */         Craft.playerClipboardsRank.put(player, Integer.valueOf(rankReq));
/*  407 */         Craft.playerClipboardsType.put(player, spawnName);
/*  408 */         Craft.playerClipboardsLot.put(player, lotStr);
/*  409 */         player.sendMessage(ChatColor.YELLOW + "Selected vehicle : " + ChatColor.WHITE + spawnName.toUpperCase());
/*      */       }
/*      */       else {
/*  412 */         player.sendMessage("Sign error...check second sign?");
/*      */       }
/*      */       
/*      */     }
/*  416 */     else if ((craftTypeName.equalsIgnoreCase("*claim*")) && (block.getRelative(BlockFace.DOWN, 1).getTypeId() == 22))
/*      */     {
/*  418 */       BlockFace bf = null;
/*      */       
/*  420 */       switch (block.getData()) {
/*      */       case 8: 
/*  422 */         bf = BlockFace.SOUTH;
/*      */         
/*  424 */         break;
/*      */       case 0: 
/*  426 */         bf = BlockFace.NORTH;
/*      */         
/*  428 */         break;
/*      */       case 4: 
/*  430 */         bf = BlockFace.EAST;
/*      */         
/*  432 */         break;
/*      */       case 12: 
/*  434 */         bf = BlockFace.WEST;
/*      */         
/*  436 */         break;
/*      */       }
/*      */       
/*      */       
/*      */ 
/*  441 */       if (bf == null) {
/*  442 */         player.sendMessage("Sign error...check direction?");
/*  443 */         return;
/*      */       }
/*      */       
/*  446 */       if (block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getTypeId() == 68) {
/*  447 */         Sign sign2 = (Sign)block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getState();
/*  448 */         String lotStr = sign2.getLine(3).trim().toLowerCase();
/*  449 */         lotStr = lotStr.replaceAll(ChatColor.BLUE.toString(), "");
/*      */         
/*  451 */         if ((lotStr.equalsIgnoreCase("DD")) || (lotStr.equalsIgnoreCase("SHIP1"))) {
/*  452 */           lotStr = "SHIP1";
/*  453 */           lotType = 1;
/*  454 */         } else if ((lotStr.equalsIgnoreCase("SUB1")) || (lotStr.equalsIgnoreCase("SHIP2"))) {
/*  455 */           lotStr = "SHIP2";
/*  456 */           lotType = 2;
/*  457 */         } else if ((lotStr.equalsIgnoreCase("SUB2")) || (lotStr.equalsIgnoreCase("SHIP3"))) {
/*  458 */           lotStr = "SHIP3";
/*  459 */           lotType = 3;
/*  460 */         } else if ((lotStr.equalsIgnoreCase("CL")) || (lotStr.equalsIgnoreCase("SHIP4"))) {
/*  461 */           lotStr = "SHIP4";
/*  462 */           lotType = 4;
/*  463 */         } else if ((lotStr.equalsIgnoreCase("CA")) || (lotStr.equalsIgnoreCase("SHIP5"))) {
/*  464 */           lotStr = "SHIP5";
/*  465 */           lotType = 5;
/*  466 */         } else if (lotStr.equalsIgnoreCase("HANGAR1")) {
/*  467 */           lotType = 6;
/*  468 */         } else if (lotStr.equalsIgnoreCase("HANGAR2")) {
/*  469 */           lotType = 7;
/*  470 */         } else if (lotStr.equalsIgnoreCase("TANK1")) {
/*  471 */           lotType = 8;
/*      */         } else {
/*  473 */           player.sendMessage("Sign error...lot type");
/*  474 */           return;
/*      */         }
/*      */         
/*  477 */         loadShipyard();
/*  478 */         loadRewards(player.getName());
/*      */         
/*      */ 
/*      */ 
/*  482 */         if (lotType == 1) {
/*  483 */           Location loc = block.getRelative(bf, 28).getLocation();
/*  484 */           int sizeX = 13;
/*  485 */           int sizeY = 28;
/*  486 */           int sizeZ = 28;
/*  487 */           int originX = 0;
/*  488 */           int originY = -8;
/*  489 */           int originZ = 0;
/*  490 */           int offsetX = 0;
/*  491 */           int offsetY = -7;
/*  492 */           int offsetZ = -29;
/*      */           
/*  494 */           int numDDs = 0;
/*  495 */           int numRewDDs = 1;
/*  496 */           if (NavyCraft.playerDDSigns.containsKey(player.getName())) {
/*  497 */             numDDs = ((ArrayList)NavyCraft.playerDDSigns.get(player.getName())).size();
/*      */           }
/*  499 */           if (NavyCraft.playerDDRewards.containsKey(player.getName())) {
/*  500 */             numRewDDs = ((Integer)NavyCraft.playerDDRewards.get(player.getName())).intValue();
/*      */           }
/*  502 */           if (numDDs >= numRewDDs) {
/*  503 */             player.sendMessage("You have no SHIP1 reward plots available.");
/*      */           }
/*      */           
/*      */         }
/*  507 */         else if (lotType == 2) {
/*  508 */           Location loc = block.getRelative(bf, 43).getLocation();
/*  509 */           int sizeX = 9;
/*  510 */           int sizeY = 28;
/*  511 */           int sizeZ = 43;
/*  512 */           int originX = 0;
/*  513 */           int originY = -8;
/*  514 */           int originZ = 0;
/*  515 */           int offsetX = 0;
/*  516 */           int offsetY = -7;
/*  517 */           int offsetZ = -44;
/*      */           
/*  519 */           int numSUB1s = 0;
/*  520 */           int numRewSUB1s = 0;
/*  521 */           if (NavyCraft.playerSUB1Signs.containsKey(player.getName())) {
/*  522 */             numSUB1s = ((ArrayList)NavyCraft.playerSUB1Signs.get(player.getName())).size();
/*      */           }
/*  524 */           if (NavyCraft.playerSUB1Rewards.containsKey(player.getName())) {
/*  525 */             numRewSUB1s = ((Integer)NavyCraft.playerSUB1Rewards.get(player.getName())).intValue();
/*      */           }
/*  527 */           if (numSUB1s >= numRewSUB1s) {
/*  528 */             player.sendMessage("You have no SHIP2 reward plots available.");
/*      */           }
/*      */         }
/*  531 */         else if (lotType == 3) {
/*  532 */           Location loc = block.getRelative(bf, 70).getLocation();
/*  533 */           int sizeX = 11;
/*  534 */           int sizeY = 28;
/*  535 */           int sizeZ = 70;
/*  536 */           int originX = 0;
/*  537 */           int originY = -8;
/*  538 */           int originZ = 0;
/*  539 */           int offsetX = 0;
/*  540 */           int offsetY = -7;
/*  541 */           int offsetZ = -71;
/*      */           
/*  543 */           int numSUB2s = 0;
/*  544 */           int numRewSUB2s = 0;
/*  545 */           if (NavyCraft.playerSUB2Signs.containsKey(player.getName())) {
/*  546 */             numSUB2s = ((ArrayList)NavyCraft.playerSUB2Signs.get(player.getName())).size();
/*      */           }
/*  548 */           if (NavyCraft.playerSUB2Rewards.containsKey(player.getName())) {
/*  549 */             numRewSUB2s = ((Integer)NavyCraft.playerSUB2Rewards.get(player.getName())).intValue();
/*      */           }
/*  551 */           if (numSUB2s >= numRewSUB2s) {
/*  552 */             player.sendMessage("You have no SHIP3 reward plots available.");
/*      */           }
/*      */         }
/*  555 */         else if (lotType == 4) {
/*  556 */           Location loc = block.getRelative(bf, 55).getLocation();
/*  557 */           int sizeX = 17;
/*  558 */           int sizeY = 28;
/*  559 */           int sizeZ = 55;
/*  560 */           int originX = 0;
/*  561 */           int originY = -8;
/*  562 */           int originZ = 0;
/*  563 */           int offsetX = 0;
/*  564 */           int offsetY = -7;
/*  565 */           int offsetZ = -56;
/*      */           
/*  567 */           int numCLs = 0;
/*  568 */           int numRewCLs = 0;
/*  569 */           if (NavyCraft.playerCLSigns.containsKey(player.getName())) {
/*  570 */             numCLs = ((ArrayList)NavyCraft.playerCLSigns.get(player.getName())).size();
/*      */           }
/*  572 */           if (NavyCraft.playerCLRewards.containsKey(player.getName())) {
/*  573 */             numRewCLs = ((Integer)NavyCraft.playerCLRewards.get(player.getName())).intValue();
/*      */           }
/*  575 */           if (numCLs >= numRewCLs) {
/*  576 */             player.sendMessage("You have no SHIP4 reward plots available.");
/*      */           }
/*      */         }
/*  579 */         else if (lotType == 5) {
/*  580 */           Location loc = block.getRelative(bf, 98).getLocation();
/*  581 */           int sizeX = 17;
/*  582 */           int sizeY = 28;
/*  583 */           int sizeZ = 98;
/*  584 */           int originX = 0;
/*  585 */           int originY = -8;
/*  586 */           int originZ = 0;
/*  587 */           int offsetX = 0;
/*  588 */           int offsetY = -7;
/*  589 */           int offsetZ = -99;
/*      */           
/*  591 */           int numCAs = 0;
/*  592 */           int numRewCAs = 0;
/*  593 */           if (NavyCraft.playerCASigns.containsKey(player.getName())) {
/*  594 */             numCAs = ((ArrayList)NavyCraft.playerCASigns.get(player.getName())).size();
/*      */           }
/*  596 */           if (NavyCraft.playerCARewards.containsKey(player.getName())) {
/*  597 */             numRewCAs = ((Integer)NavyCraft.playerCARewards.get(player.getName())).intValue();
/*      */           }
/*  599 */           if (numCAs >= numRewCAs) {
/*  600 */             player.sendMessage("You have no SHIP5 reward plots available.");
/*      */           }
/*      */         }
/*  603 */         else if (lotType == 6) {
/*  604 */           Location loc = block.getRelative(bf, 17).getLocation();
/*  605 */           int sizeX = 17;
/*  606 */           int sizeY = 7;
/*  607 */           int sizeZ = 19;
/*  608 */           int originX = 0;
/*  609 */           int originY = -1;
/*  610 */           int originZ = -18;
/*  611 */           int offsetX = -17;
/*  612 */           int offsetY = 0;
/*  613 */           int offsetZ = -20;
/*      */           
/*  615 */           int numH1s = 0;
/*  616 */           int numRewH1s = 0;
/*  617 */           if (NavyCraft.playerHANGAR1Signs.containsKey(player.getName())) {
/*  618 */             numH1s = ((ArrayList)NavyCraft.playerHANGAR1Signs.get(player.getName())).size();
/*      */           }
/*  620 */           if (NavyCraft.playerHANGAR1Rewards.containsKey(player.getName())) {
/*  621 */             numRewH1s = ((Integer)NavyCraft.playerHANGAR1Rewards.get(player.getName())).intValue();
/*      */           }
/*  623 */           if (numH1s >= numRewH1s) {
/*  624 */             player.sendMessage("You have no HANGAR1 reward plots available.");
/*      */           }
/*      */         }
/*  627 */         else if (lotType == 7) {
/*  628 */           Location loc = block.getRelative(bf, 25).getLocation();
/*  629 */           int sizeX = 25;
/*  630 */           int sizeY = 7;
/*  631 */           int sizeZ = 32;
/*  632 */           int originX = 0;
/*  633 */           int originY = -1;
/*  634 */           int originZ = -31;
/*  635 */           int offsetX = -25;
/*  636 */           int offsetY = 0;
/*  637 */           int offsetZ = -33;
/*      */           
/*  639 */           int numH2s = 0;
/*  640 */           int numRewH2s = 0;
/*  641 */           if (NavyCraft.playerHANGAR2Signs.containsKey(player.getName())) {
/*  642 */             numH2s = ((ArrayList)NavyCraft.playerHANGAR2Signs.get(player.getName())).size();
/*      */           }
/*  644 */           if (NavyCraft.playerHANGAR2Rewards.containsKey(player.getName())) {
/*  645 */             numRewH2s = ((Integer)NavyCraft.playerHANGAR2Rewards.get(player.getName())).intValue();
/*      */           }
/*  647 */           if (numH2s >= numRewH2s) {
/*  648 */             player.sendMessage("You have no HANGAR2 reward plots available.");
/*      */           }
/*      */         }
/*  651 */         else if (lotType == 8) {
/*  652 */           Location loc = block.getRelative(bf, 12).getLocation();
/*  653 */           int sizeX = 12;
/*  654 */           int sizeY = 7;
/*  655 */           int sizeZ = 19;
/*  656 */           int originX = 0;
/*  657 */           int originY = -1;
/*  658 */           int originZ = -18;
/*  659 */           int offsetX = -12;
/*  660 */           int offsetY = 0;
/*  661 */           int offsetZ = -20;
/*      */           
/*  663 */           int numT1s = 0;
/*  664 */           int numRewT1s = 0;
/*  665 */           if (NavyCraft.playerTANK1Signs.containsKey(player.getName())) {
/*  666 */             numT1s = ((ArrayList)NavyCraft.playerTANK1Signs.get(player.getName())).size();
/*      */           }
/*  668 */           if (NavyCraft.playerTANK1Rewards.containsKey(player.getName())) {
/*  669 */             numRewT1s = ((Integer)NavyCraft.playerTANK1Rewards.get(player.getName())).intValue();
/*      */           }
/*  671 */           if (numT1s >= numRewT1s) {
/*  672 */             player.sendMessage("You have no TANK1 reward plots available.");
/*      */           }
/*      */           
/*      */         }
/*      */         else
/*      */         {
/*  678 */           player.sendMessage("Sign error...invalid lot"); return; }
/*      */         int offsetZ;
/*      */         int offsetY;
/*  681 */         int offsetX; int sizeZ; int sizeY; int sizeX; Location loc; int originX = loc.getBlockX() + originX;
/*  682 */         int originY = loc.getBlockY() + originY;
/*  683 */         int originZ = loc.getBlockZ() + originZ;
/*      */         
/*  685 */         wgp = (WorldGuardPlugin)plugin.getServer().getPluginManager().getPlugin("WorldGuard");
/*  686 */         if (wgp != null) {
/*  687 */           RegionManager regionManager = wgp.getRegionManager(loc.getWorld());
/*      */           
/*      */ 
/*      */ 
/*  691 */           String regionName = "--" + player.getName() + "-" + (maxId(player) + 1);
/*      */           
/*  693 */           regionManager.addRegion(new com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion(regionName, new BlockVector(originX, originY, originZ), new BlockVector(originX + sizeX - 1, originY + sizeY - 1, originZ + sizeZ - 1)));
/*  694 */           DefaultDomain owners = new DefaultDomain();
/*  695 */           com.sk89q.worldguard.LocalPlayer lp = wgp.wrapPlayer(player);
/*  696 */           owners.addPlayer(lp);
/*  697 */           regionManager.getRegion(regionName).setOwners(owners);
/*      */           try
/*      */           {
/*  700 */             regionManager.save();
/*      */           }
/*      */           catch (StorageException e) {
/*  703 */             e.printStackTrace();
/*      */           }
/*      */           
/*  706 */           sign.setLine(0, "*Select*");
/*  707 */           if (player.getName().length() > 15) {
/*  708 */             sign.setLine(1, player.getName().substring(0, 16));
/*  709 */             sign.setLine(2, player.getName().substring(15, player.getName().length()));
/*      */           } else {
/*  711 */             sign.setLine(1, player.getName());
/*      */           }
/*      */           
/*  714 */           sign.setLine(3, "custom");
/*  715 */           sign.update();
/*      */           
/*  717 */           sign2.setLine(0, "Private");
/*  718 */           sign2.setLine(1, "1");
/*  719 */           sign2.setLine(2, maxId(player) + 1);
/*  720 */           sign2.setLine(3, lotStr);
/*  721 */           sign2.update();
/*      */           
/*  723 */           player.sendMessage(ChatColor.AQUA + ChatColor.BOLD + lotStr.toUpperCase() + "-Plot Claimed!");
/*      */         }
/*      */         else {
/*  726 */           player.sendMessage("World Guard error");
/*      */         }
/*      */       }
/*      */       else {
/*  730 */         player.sendMessage("Sign error...check second sign?");
/*      */       }
/*      */       
/*      */     }
/*  734 */     else if ((craftTypeName.equalsIgnoreCase("*recall*")) && (block.getRelative(BlockFace.DOWN, 1).getTypeId() == 22)) {
/*  735 */       int xCord = 0;
/*  736 */       int yCord = 0;
/*  737 */       int zCord = 0;
/*  738 */       String xStr = sign.getLine(1).trim().toLowerCase();
/*  739 */       String yStr = sign.getLine(2).trim().toLowerCase();
/*  740 */       String zStr = sign.getLine(3).trim().toLowerCase();
/*  741 */       xStr = xStr.replaceAll(ChatColor.BLUE.toString(), "");
/*  742 */       yStr = yStr.replaceAll(ChatColor.BLUE.toString(), "");
/*  743 */       zStr = zStr.replaceAll(ChatColor.BLUE.toString(), "");
/*      */       try {
/*  745 */         xCord = Integer.parseInt(xStr);
/*  746 */         yCord = Integer.parseInt(yStr);
/*  747 */         zCord = Integer.parseInt(zStr);
/*      */       } catch (NumberFormatException nfe) {
/*  749 */         player.sendMessage("Sign error...invalid coordinates");
/*  750 */         return;
/*      */       }
/*  752 */       player.teleport(new Location(player.getWorld(), xCord, yCord, zCord));
/*      */       
/*  754 */       if ((NavyCraft.checkRecallRegion(player.getLocation())) || (player.isOp())) {
/*  755 */         wep = (WorldEditPlugin)plugin.getServer().getPluginManager().getPlugin("WorldEdit");
/*  756 */         if (wep == null) {
/*  757 */           player.sendMessage("WorldEdit error");
/*      */         }
/*  759 */         EditSession es = wep.createEditSession(player);
/*      */         
/*  761 */         int oldLimit = es.getBlockChangeLimit();
/*  762 */         es.setBlockChangeLimit(50000);
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
/*  777 */         File loadFile = null;
/*  778 */         CuboidClipboard cc = null;
/*      */         try
/*      */         {
/*  781 */           loadFile = wep.getWorldEdit().getSafeSaveFile((com.sk89q.worldedit.entity.Player)player, plugin.getDataFolder(), player.getName(), "schematic", new String[] {
/*  782 */             "schematic" });
/*      */           
/*  784 */           cc = SchematicFormat.MCEDIT.load(loadFile);
/*      */         }
/*      */         catch (com.sk89q.worldedit.util.io.file.FilenameException|com.sk89q.worldedit.data.DataException|java.io.IOException e)
/*      */         {
/*  788 */           e.printStackTrace();
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
/*      */ 
/*      */ 
/*      */ 
/*  803 */         EditSession edits = wep.getWorldEdit().getEditSessionFactory().getEditSession((com.sk89q.worldedit.world.World)player.getWorld(), 100000);
/*      */         
/*  805 */         if ((cc != null) && (loadFile != null)) {
/*  806 */           loadFile.delete();
/*      */           
/*      */ 
/*      */ 
/*  810 */           Vector v = new Vector(player.getLocation().getBlockX(), 63, player.getLocation().getBlockZ());
/*  811 */           Block pasteBlock = new Location(player.getWorld(), player.getLocation().getBlockX(), 63.0D, player.getLocation().getBlockZ()).getBlock();
/*      */           
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/*  817 */             cc.paste(es, v, false);
/*  818 */             player.sendMessage(ChatColor.YELLOW + "Vehicle recalled from storage.");
/*  819 */             player.teleport(new Location(player.getWorld(), player.getLocation().getX(), cc.getOrigin().getY() + cc.getHeight() + 2.0D, player.getLocation().getZ()));
/*      */           }
/*      */           catch (MaxChangedBlocksException e) {
/*  822 */             e.printStackTrace();
/*  823 */             player.sendMessage("MaxChangedBlocks error");
/*      */           }
/*      */         }
/*      */         
/*  827 */         es.setBlockChangeLimit(oldLimit);
/*      */       } else {
/*  829 */         player.sendMessage(ChatColor.YELLOW + "You are not in a recall region.");
/*      */       } } else { String freeString;
/*  831 */       if ((craftTypeName.equalsIgnoreCase("*spawn*")) && (block.getRelative(BlockFace.DOWN, 1).getTypeId() == 22)) {
/*  832 */         int rotate = -1;
/*      */         
/*  834 */         BlockFace bf = null;
/*  835 */         BlockFace bf2 = null;
/*  836 */         switch (block.getData()) {
/*      */         case 8: 
/*  838 */           rotate = 180;
/*  839 */           bf = BlockFace.SOUTH;
/*  840 */           bf2 = BlockFace.WEST;
/*  841 */           break;
/*      */         case 0: 
/*  843 */           rotate = 0;
/*  844 */           bf = BlockFace.NORTH;
/*  845 */           bf2 = BlockFace.EAST;
/*  846 */           break;
/*      */         case 4: 
/*  848 */           rotate = 90;
/*  849 */           bf = BlockFace.EAST;
/*  850 */           bf2 = BlockFace.SOUTH;
/*  851 */           break;
/*      */         case 12: 
/*  853 */           rotate = 270;
/*  854 */           bf = BlockFace.WEST;
/*  855 */           bf2 = BlockFace.NORTH;
/*  856 */           break;
/*      */         }
/*      */         
/*      */         
/*      */ 
/*  861 */         if (rotate == -1) {
/*  862 */           player.sendMessage("Sign error...check direction?");
/*  863 */           return;
/*      */         }
/*      */         
/*  866 */         if (!Craft.playerClipboards.containsKey(player)) {
/*  867 */           player.sendMessage("Go to the Shipyard and select a vehicle first.");
/*  868 */           return;
/*      */         }
/*      */         
/*  871 */         if (!checkSpawnerClear(player, block, bf, bf2)) {
/*  872 */           player.sendMessage("Vehicle in the way.");
/*  873 */           return;
/*      */         }
/*      */         
/*  876 */         boolean isAutoSpawn = false;
/*  877 */         boolean isMerchantSpawn = false;
/*  878 */         freeString = sign.getLine(2).trim().toLowerCase();
/*  879 */         freeString = freeString.replaceAll(ChatColor.BLUE.toString(), "");
/*  880 */         if (freeString.equalsIgnoreCase("auto")) {
/*  881 */           isAutoSpawn = true;
/*  882 */           if (!player.isOp()) {
/*  883 */             player.sendMessage("You cannot use an auto spawner.");
/*  884 */             return;
/*      */           }
/*      */         }
/*      */         
/*  888 */         if (freeString.equalsIgnoreCase("merchant")) {
/*  889 */           isMerchantSpawn = true;
/*      */         }
/*      */         
/*  892 */         String typeString = sign.getLine(1).trim().toLowerCase();
/*  893 */         typeString = typeString.replaceAll(ChatColor.BLUE.toString(), "");
/*  894 */         if ((!typeString.isEmpty()) && (!typeString.equalsIgnoreCase((String)Craft.playerClipboardsType.get(player))) && (!typeString.equalsIgnoreCase((String)Craft.playerClipboardsLot.get(player)))) {
/*  895 */           player.sendMessage(player.getName() + ", you cannot spawn this type of vehicle here.");
/*  896 */           return;
/*      */         }
/*      */         
/*  899 */         int freeSpawnRankLimit = 0;
/*  900 */         String freeSpawnRankStr = sign.getLine(3).trim().toLowerCase();
/*  901 */         freeSpawnRankStr = freeSpawnRankStr.replaceAll(ChatColor.BLUE.toString(), "");
/*  902 */         if (!freeSpawnRankStr.isEmpty()) {
/*      */           try {
/*  904 */             freeSpawnRankLimit = Integer.parseInt(freeSpawnRankStr);
/*      */           } catch (NumberFormatException nfe) {
/*  906 */             player.sendMessage("Sign error...invaild rank limit");
/*  907 */             return;
/*      */           }
/*      */         }
/*      */         
/*  911 */         if (freeSpawnRankLimit < 0) {
/*  912 */           player.sendMessage("Sign error...invaild rank limit");
/*  913 */           return;
/*      */         }
/*      */         
/*  916 */         int playerRank = 1;
/*  917 */         Plugin groupPlugin = plugin.getServer().getPluginManager().getPlugin("GroupManager");
/*  918 */         if (groupPlugin != null) {
/*  919 */           if (!plugin.getServer().getPluginManager().isPluginEnabled(groupPlugin)) {
/*  920 */             plugin.getServer().getPluginManager().enablePlugin(groupPlugin);
/*      */           }
/*  922 */           GroupManager gm = (GroupManager)groupPlugin;
/*  923 */           WorldsHolder wd = gm.getWorldsHolder();
/*  924 */           String groupName = wd.getWorldData(player).getUser(player.getName()).getGroupName();
/*      */           
/*  926 */           if (groupName.equalsIgnoreCase("Default")) {
/*  927 */             playerRank = 1;
/*  928 */           } else if (groupName.equalsIgnoreCase("LtJG")) {
/*  929 */             playerRank = 2;
/*  930 */           } else if (groupName.equalsIgnoreCase("Lieutenant")) {
/*  931 */             playerRank = 3;
/*  932 */           } else if (groupName.equalsIgnoreCase("Ltcm")) {
/*  933 */             playerRank = 4;
/*  934 */           } else if (groupName.equalsIgnoreCase("Commander")) {
/*  935 */             playerRank = 5;
/*  936 */           } else if (groupName.equalsIgnoreCase("Captain")) {
/*  937 */             playerRank = 6;
/*  938 */           } else if (groupName.equalsIgnoreCase("RearAdmiral1")) {
/*  939 */             playerRank = 7;
/*  940 */           } else if (groupName.equalsIgnoreCase("RearAdmiral2")) {
/*  941 */             playerRank = 8;
/*  942 */           } else if (groupName.equalsIgnoreCase("ViceAdmiral")) {
/*  943 */             playerRank = 9;
/*  944 */           } else if (groupName.equalsIgnoreCase("Admiral")) {
/*  945 */             playerRank = 10;
/*  946 */           } else if (groupName.equalsIgnoreCase("FleetAdmiral")) {
/*  947 */             playerRank = 11;
/*  948 */           } else if (groupName.equalsIgnoreCase("Admin")) {
/*  949 */             playerRank = 11;
/*  950 */           } else if (groupName.equalsIgnoreCase("BattleMod")) {
/*  951 */             playerRank = 9;
/*  952 */           } else if (groupName.equalsIgnoreCase("WW-Mod")) {
/*  953 */             playerRank = 8;
/*  954 */           } else if (groupName.equalsIgnoreCase("Moderator")) {
/*  955 */             playerRank = 7;
/*  956 */           } else if (groupName.equalsIgnoreCase("SVR-Mod")) {
/*  957 */             playerRank = 10;
/*      */           }
/*      */         } else {
/*  960 */           player.sendMessage("Group manager error");
/*  961 */           return;
/*      */         }
/*      */         
/*  964 */         if ((playerRank < ((Integer)Craft.playerClipboardsRank.get(player)).intValue()) && (freeSpawnRankLimit < ((Integer)Craft.playerClipboardsRank.get(player)).intValue())) {
/*  965 */           player.sendMessage("You do not have the rank to spawn this vehicle.");
/*  966 */           return;
/*      */         }
/*      */         
/*      */ 
/*  970 */         Essentials ess = (Essentials)plugin.getServer().getPluginManager().getPlugin("Essentials");
/*  971 */         if (ess == null) {
/*  972 */           player.sendMessage("Essentials Economy error");
/*  973 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  980 */         if ((isMerchantSpawn) && 
/*  981 */           (NavyCraft.checkTeamRegion(block.getLocation()) > 0)) {
/*  982 */           if ((NavyCraft.checkTeamRegion(block.getLocation()) == 2) && (NavyCraft.redMerchant)) {
/*  983 */             player.sendMessage(ChatColor.RED + "Red team already has an active merchant.");
/*  984 */             return; }
/*  985 */           if ((NavyCraft.checkTeamRegion(block.getLocation()) == 1) && (NavyCraft.blueMerchant)) {
/*  986 */             player.sendMessage(ChatColor.RED + "Blue team already has an active merchant.");
/*  987 */             return;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  992 */         wep = (WorldEditPlugin)plugin.getServer().getPluginManager().getPlugin("WorldEdit");
/*  993 */         if (wep == null) {
/*  994 */           player.sendMessage("WorldEdit error");
/*  995 */           return;
/*      */         }
/*  997 */         EditSession es = wep.createEditSession(player);
/*      */         try
/*      */         {
/* 1000 */           int oldLimit = es.getBlockChangeLimit();
/* 1001 */           es.setBlockChangeLimit(50000);
/*      */           
/* 1003 */           ClipboardHolder ch = (ClipboardHolder)Craft.playerClipboards.get(player);
/* 1004 */           com.sk89q.worldedit.LocalPlayer lplayer = wep.wrapPlayer(player);
/*      */           
/* 1006 */           int width = ch.getClipboard().getRegion().getWidth();
/* 1007 */           int length = ch.getClipboard().getRegion().getLength();
/* 1008 */           int moveForward = 0;
/* 1009 */           if (width > length) {
/* 1010 */             moveForward = width;
/*      */           } else {
/* 1012 */             moveForward = length;
/*      */           }
/*      */           
/* 1015 */           AffineTransform transform = new AffineTransform();
/* 1016 */           transform = transform.rotateY(-rotate);
/* 1017 */           ch.setTransform(transform);
/* 1018 */           Block pasteBlock = block.getRelative(bf, moveForward + 2);
/* 1019 */           Vector pasteVector = new Vector(pasteBlock.getLocation().getX(), pasteBlock.getLocation().getY(), pasteBlock.getLocation().getZ());
/*      */           
/* 1021 */           com.sk89q.worldedit.function.operation.Operation operation = ch.createPaste(es, ch.getWorldData())
/*      */           
/* 1023 */             .to(pasteVector).ignoreAirBlocks(false).build();
/* 1024 */           Operations.completeLegacy(operation);
/*      */           
/* 1026 */           transform = transform.rotateY(rotate);
/* 1027 */           ch.setTransform(transform);
/* 1028 */           es.flushQueue();
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
/* 1045 */           player.sendMessage("Spawned vehicle : " + ((String)Craft.playerClipboardsType.get(player)).toUpperCase());
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1050 */           es.setBlockChangeLimit(oldLimit);
/*      */           
/* 1052 */           int shiftRight = 0;
/* 1053 */           int shiftForward = 0;
/* 1054 */           int shiftUp = 0;
/* 1055 */           int shiftDown = 0;
/*      */           
/* 1057 */           if ((!isAutoSpawn) && (!isMerchantSpawn)) return;
/* 1058 */           if ((((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("DD")) || (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SHIP1"))) {
/* 1059 */             shiftRight = 12;
/* 1060 */             shiftForward = 28;
/* 1061 */             shiftUp = 20;
/* 1062 */             shiftDown = 8;
/* 1063 */           } else if ((((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SUB1")) || (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SHIP2"))) {
/* 1064 */             shiftRight = 8;
/* 1065 */             shiftForward = 43;
/* 1066 */             shiftUp = 20;
/* 1067 */             shiftDown = 8;
/* 1068 */           } else if ((((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SUB2")) || (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SHIP3"))) {
/* 1069 */             shiftRight = 10;
/* 1070 */             shiftForward = 70;
/* 1071 */             shiftUp = 20;
/* 1072 */             shiftDown = 8;
/* 1073 */           } else if ((((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("CL")) || (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SHIP4"))) {
/* 1074 */             shiftRight = 16;
/* 1075 */             shiftForward = 55;
/* 1076 */             shiftUp = 20;
/* 1077 */             shiftDown = 8;
/* 1078 */           } else if ((((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("CA")) || (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SHIP5"))) {
/* 1079 */             shiftRight = 16;
/* 1080 */             shiftForward = 98;
/* 1081 */             shiftUp = 20;
/* 1082 */             shiftDown = 8;
/* 1083 */           } else if (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("HANGAR1")) {
/* 1084 */             shiftRight = 16;
/* 1085 */             shiftForward = 19;
/*      */             
/*      */ 
/* 1088 */             shiftUp = 7;
/* 1089 */             shiftDown = 0;
/* 1090 */           } else if (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("HANGAR2")) {
/* 1091 */             shiftRight = 24;
/* 1092 */             shiftForward = 32;
/*      */             
/*      */ 
/* 1095 */             shiftUp = 7;
/* 1096 */             shiftDown = 0;
/* 1097 */           } else if (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("TANK1")) {
/* 1098 */             shiftRight = 11;
/* 1099 */             shiftForward = 19;
/* 1100 */             shiftUp = 7;
/* 1101 */             shiftDown = 0;
/*      */           } else {
/* 1103 */             player.sendMessage("Unknown lot type error2!");
/*      */           }
/*      */           
/* 1106 */           Block rightLimit = block.getRelative(bf2, shiftRight).getRelative(bf, shiftForward).getRelative(BlockFace.UP, shiftUp);
/* 1107 */           Block leftLimit = block.getRelative(bf, 1).getRelative(BlockFace.DOWN, shiftDown);
/*      */           
/*      */ 
/* 1110 */           int rightX = rightLimit.getX();
/* 1111 */           int rightY = rightLimit.getY();
/* 1112 */           int rightZ = rightLimit.getZ();
/* 1113 */           int leftX = leftLimit.getX();
/* 1114 */           int leftY = leftLimit.getY();
/* 1115 */           int leftZ = leftLimit.getZ();
/*      */           int endX;
/* 1117 */           int startX; int endX; if (rightX < leftX) {
/* 1118 */             int startX = rightX;
/* 1119 */             endX = leftX;
/*      */           } else {
/* 1121 */             startX = leftX;
/* 1122 */             endX = rightX; }
/*      */           int endZ;
/* 1124 */           int startZ; int endZ; if (rightZ < leftZ) {
/* 1125 */             int startZ = rightZ;
/* 1126 */             endZ = leftZ;
/*      */           } else {
/* 1128 */             startZ = leftZ;
/* 1129 */             endZ = rightZ;
/*      */           }
/*      */           
/* 1132 */           for (int x = startX; x <= endX; x++) {
/* 1133 */             for (int y = leftY; y <= rightY; y++) {
/* 1134 */               for (int z = startZ; z <= endZ; z++) {
/* 1135 */                 if (player.getWorld().getBlockAt(x, y, z).getTypeId() == 68) {
/* 1136 */                   Block shipSignBlock = player.getWorld().getBlockAt(x, y, z);
/* 1137 */                   Sign shipSign = (Sign)shipSignBlock.getState();
/* 1138 */                   String signLine0 = shipSign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/* 1139 */                   CraftType craftType = CraftType.getCraftType(signLine0);
/* 1140 */                   if (craftType != null) {
/* 1141 */                     String name = shipSign.getLine(1);
/*      */                     
/* 1143 */                     if (name.trim().equals("")) {
/* 1144 */                       name = null;
/*      */                     }
/*      */                     
/* 1147 */                     int shipx = shipSignBlock.getX();
/* 1148 */                     int shipy = shipSignBlock.getY();
/* 1149 */                     int shipz = shipSignBlock.getZ();
/*      */                     
/* 1151 */                     int direction = shipSignBlock.getData();
/*      */                     
/*      */ 
/* 1154 */                     shipx += (direction == 5 ? -1 : direction == 4 ? 1 : 0);
/* 1155 */                     shipz += (direction == 3 ? -1 : direction == 2 ? 1 : 0);
/*      */                     
/* 1157 */                     float dr = 0.0F;
/*      */                     
/* 1159 */                     switch (shipSignBlock.getData()) {
/*      */                     case 2: 
/* 1161 */                       dr = 180.0F;
/* 1162 */                       break;
/*      */                     case 3: 
/* 1164 */                       dr = 0.0F;
/* 1165 */                       break;
/*      */                     case 4: 
/* 1167 */                       dr = 90.0F;
/* 1168 */                       break;
/*      */                     case 5: 
/* 1170 */                       dr = 270.0F;
/*      */                     }
/*      */                     
/* 1173 */                     Craft theCraft = NavyCraft.instance.createCraft(player, craftType, shipx, shipy, shipz, name, dr, shipSignBlock, true);
/*      */                     
/* 1175 */                     CraftMover cm = new CraftMover(theCraft, plugin);
/* 1176 */                     cm.structureUpdate(null, false);
/*      */                     
/* 1178 */                     if (isAutoSpawn) {
/* 1179 */                       theCraft.isAutoCraft = true;
/* 1180 */                       theCraft.speedChange(null, true);
/* 1181 */                       theCraft.speedChange(null, true);
/* 1182 */                       theCraft.speedChange(null, true);
/* 1183 */                       theCraft.speedChange(null, true);
/*      */                       
/* 1185 */                       double randomNum = Math.random();
/* 1186 */                       String speedStr = "LOW";
/* 1187 */                       if (randomNum >= 0.3D) {
/* 1188 */                         theCraft.gearChange(null, true);
/* 1189 */                         speedStr = "MODERATE";
/*      */                       }
/* 1191 */                       if (randomNum >= 0.7D) {
/* 1192 */                         theCraft.gearChange(null, true);
/* 1193 */                         speedStr = "HIGH";
/*      */                       }
/* 1195 */                       String dirStr = "EAST";
/* 1196 */                       if (randomNum >= 0.8D) {
/* 1197 */                         theCraft.rudderChange(null, 1, false);
/* 1198 */                         dirStr = "SOUTH-EAST";
/* 1199 */                       } else if (randomNum <= 0.2D) {
/* 1200 */                         theCraft.rudderChange(null, -1, false);
/* 1201 */                         dirStr = "NORTH-EAST";
/*      */                       }
/*      */                       
/* 1204 */                       plugin.getServer().broadcastMessage(ChatColor.YELLOW + "**ENEMY MERCHANT ALERT**");
/* 1205 */                       plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Enemy Maru Ammo Carrier sighted near west central side of ocean...");
/* 1206 */                       plugin.getServer().broadcastMessage(ChatColor.YELLOW + "coordinates x=-30 z=-300, headed " + dirStr + " at " + speedStr + " speed...");
/* 1207 */                       plugin.getServer().broadcastMessage(ChatColor.YELLOW + "player who sinks this vehicle will win a cash reward!");
/* 1208 */                     } else if (isMerchantSpawn) {
/* 1209 */                       theCraft.isMerchantCraft = true;
/*      */                       
/* 1211 */                       if (theCraft.redTeam) {
/* 1212 */                         NavyCraft.redMerchant = true;
/* 1213 */                         plugin.getServer().broadcastMessage(ChatColor.YELLOW + "**" + ChatColor.RED + "Red Team" + ChatColor.YELLOW + " has spawned a merchant!**");
/* 1214 */                       } else if (theCraft.blueTeam)
/*      */                       {
/* 1216 */                         NavyCraft.blueMerchant = true;
/* 1217 */                         plugin.getServer().broadcastMessage(ChatColor.YELLOW + "**" + ChatColor.BLUE + "Blue Team" + ChatColor.YELLOW + " has spawned a merchant!**");
/*      */                       }
/*      */                     }
/*      */                     
/*      */ 
/* 1222 */                     return;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/* 1228 */           player.sendMessage("No ship sign located!");
/*      */         }
/*      */         catch (MaxChangedBlocksException e)
/*      */         {
/* 1232 */           player.sendMessage("Max changed blocks error"); return;
/*      */         } }
/*      */       boolean periscopeFound;
/* 1235 */       if (craftTypeName.equalsIgnoreCase("periscope")) {
/* 1236 */         if ((!player.hasPermission("movecraft.periscope.use")) && (!player.isOp())) {
/* 1237 */           player.sendMessage(ChatColor.RED + "You do not have permission to use this sign");
/* 1238 */           return;
/*      */         }
/*      */         
/* 1241 */         if (NavyCraft.aaGunnersList.contains(player)) {
/* 1242 */           NavyCraft.aaGunnersList.remove(player);
/* 1243 */           if (player.getInventory().contains(Material.BLAZE_ROD)) {
/* 1244 */             player.getInventory().remove(Material.BLAZE_ROD);
/*      */           }
/* 1246 */           player.sendMessage(ChatColor.YELLOW + "You get off the AA-Gun.");
/*      */         }
/*      */         
/* 1249 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1250 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player)))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1255 */           BlockFace bf = BlockFace.NORTH;
/*      */           
/* 1257 */           switch (block.getData()) {
/*      */           case 2: 
/* 1259 */             bf = BlockFace.EAST;
/* 1260 */             break;
/*      */           case 3: 
/* 1262 */             bf = BlockFace.WEST;
/* 1263 */             break;
/*      */           case 4: 
/* 1265 */             bf = BlockFace.SOUTH;
/* 1266 */             break;
/*      */           case 5: 
/* 1268 */             bf = BlockFace.NORTH;
/*      */           }
/*      */           
/*      */           
/* 1272 */           periscopeFound = false;
/* 1273 */           CraftMover cmer = new CraftMover(c, plugin);
/* 1274 */           cmer.structureUpdate(null, false);
/* 1275 */           for (Periscope p : c.periscopes)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1280 */             if ((block.getLocation().getBlockX() == p.signLoc.getBlockX()) && (block.getLocation().getBlockY() == p.signLoc.getBlockY()) && (block.getLocation().getBlockZ() == p.signLoc.getBlockZ())) {
/* 1281 */               periscopeFound = true;
/* 1282 */               if (p.user != null) {
/* 1283 */                 player.sendMessage("Player already on scope.");
/* 1284 */               } else if ((p.raised) && (!p.destroyed) && (p.scopeLoc != null)) {
/* 1285 */                 player.sendMessage("Periscope On!");
/* 1286 */                 player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/* 1287 */                 Location newLoc = new Location(playerCraft.world, p.scopeLoc.getBlockX() + 0.5D, p.scopeLoc.getBlockY() + 0.5D, p.scopeLoc.getBlockZ() + 0.5D);
/* 1288 */                 newLoc.setYaw(player.getLocation().getYaw());
/* 1289 */                 player.teleport(newLoc);
/* 1290 */                 p.user = player;
/* 1291 */               } else if ((!p.destroyed) && (p.scopeLoc != null)) {
/* 1292 */                 player.sendMessage("Raise Periscope First.");
/*      */               } else {
/* 1294 */                 player.sendMessage("Periscope destroyed!");
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 1299 */           if (!periscopeFound) {
/* 1300 */             Periscope newPeriscope = new Periscope(block.getLocation(), c.periscopes.size());
/* 1301 */             sign.setLine(1, "||" + newPeriscope.periscopeID + "||");
/* 1302 */             sign.setLine(2, "|| ||");
/* 1303 */             sign.setLine(3, "DOWN");
/* 1304 */             sign.update();
/* 1305 */             c.periscopes.add(newPeriscope);
/* 1306 */             NavyCraft.allPeriscopes.add(newPeriscope);
/*      */             
/* 1308 */             CraftMover cm = new CraftMover(c, plugin);
/* 1309 */             cm.structureUpdate(null, false);
/*      */             
/* 1311 */             if ((!newPeriscope.destroyed) && (newPeriscope.scopeLoc != null)) {
/* 1312 */               Location newLoc = new Location(playerCraft.world, newPeriscope.scopeLoc.getBlockX() + 0.5D, newPeriscope.scopeLoc.getBlockY() + 0.5D, newPeriscope.scopeLoc.getBlockZ() + 0.5D);
/* 1313 */               newLoc.setYaw(player.getLocation().getYaw());
/* 1314 */               player.teleport(newLoc);
/* 1315 */               newPeriscope.user = player;
/* 1316 */               player.sendMessage("Periscope Started!");
/* 1317 */               player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/* 1322 */           player.sendMessage("Start the sub before using the periscope!");
/*      */         }
/* 1324 */       } else if (craftTypeName.equalsIgnoreCase("subdrive")) {
/* 1325 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1326 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1327 */           if (c.submergedMode)
/*      */           {
/*      */ 
/* 1330 */             player.sendMessage("Starting Diesel Engines (SURFACE MODE)");
/* 1331 */             player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/* 1332 */             c.submergedMode = false;
/* 1333 */             c.vertPlanes = 0;
/*      */             
/*      */ 
/* 1336 */             for (periscopeFound = c.engineIDOn.keySet().iterator(); periscopeFound.hasNext();) { int eng = ((Integer)periscopeFound.next()).intValue();
/* 1337 */               int engineType = ((Integer)c.engineIDTypes.get(Integer.valueOf(eng))).intValue();
/* 1338 */               if ((engineType != 0) && (engineType != 1) && (engineType != 2) && (engineType != 4) && (engineType != 9)) {
/* 1339 */                 c.engineIDOn.put(Integer.valueOf(eng), Boolean.valueOf(true));
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1346 */             for (String s : c.crewNames) {
/* 1347 */               org.bukkit.entity.Player p = plugin.getServer().getPlayer(s);
/* 1348 */               if (p != null) {
/* 1349 */                 p.sendMessage("Surface the boat!");
/*      */               }
/*      */             }
/*      */             
/* 1353 */             surfaceBellThread(sign.getBlock().getLocation());
/*      */ 
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/* 1360 */             player.sendMessage("Starting Electric Engines (READY TO DIVE)");
/* 1361 */             player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/* 1362 */             c.submergedMode = true;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1368 */             for (periscopeFound = c.engineIDOn.keySet().iterator(); periscopeFound.hasNext();) { int eng = ((Integer)periscopeFound.next()).intValue();
/* 1369 */               int engineType = ((Integer)c.engineIDTypes.get(Integer.valueOf(eng))).intValue();
/* 1370 */               if ((engineType != 0) && (engineType != 1) && (engineType != 2) && (engineType != 4) && (engineType != 9)) {
/* 1371 */                 c.engineIDOn.put(Integer.valueOf(eng), Boolean.valueOf(false));
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1378 */             for (String s : c.crewNames) {
/* 1379 */               org.bukkit.entity.Player p = plugin.getServer().getPlayer(s);
/* 1380 */               if (p != null) {
/* 1381 */                 p.sendMessage("DIVE! DIVE!");
/*      */               }
/*      */             }
/* 1384 */             divingBellThread(sign.getBlock().getLocation());
/*      */           }
/* 1386 */           CraftMover cm = new CraftMover(c, plugin);
/* 1387 */           cm.signUpdates(block);
/*      */         } else {
/* 1389 */           player.sendMessage("Start the sub before using this sign.");
/*      */         }
/* 1391 */       } else if (craftTypeName.equalsIgnoreCase("ballasttanks")) {
/* 1392 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1393 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1394 */           c.ballastMode = ((c.ballastMode + 1) % 4);
/* 1395 */           CraftMover cm = new CraftMover(c, plugin);
/* 1396 */           cm.signUpdates(block);
/*      */         }
/*      */         else {
/* 1399 */           player.sendMessage("Start the sub before using this sign.");
/*      */         }
/* 1401 */       } else if (craftTypeName.equalsIgnoreCase("firecontrol")) {
/* 1402 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1403 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1404 */           int tubeNum = 0;
/* 1405 */           String line2 = "";
/* 1406 */           String line3 = "";
/* 1407 */           String tubeString = sign.getLine(1).trim().toLowerCase();
/* 1408 */           tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
/* 1409 */           if (!tubeString.isEmpty()) {
/*      */             try {
/* 1411 */               tubeNum = Integer.parseInt(tubeString);
/*      */             } catch (NumberFormatException nfe) {
/* 1413 */               tubeNum = 0;
/*      */             }
/*      */           }
/* 1416 */           if ((tubeNum != 0) && (c.tubeFiringMode.containsKey(Integer.valueOf(tubeNum)))) {
/* 1417 */             if (leftClick) {
/* 1418 */               player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/* 1419 */               if (((Integer)c.tubeFiringMode.get(Integer.valueOf(tubeNum))).intValue() == -3)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1425 */                 if (((Integer)c.tubeFiringDisplay.get(Integer.valueOf(tubeNum))).intValue() == 0) {
/* 1426 */                   if (((Boolean)c.tubeFiringAuto.get(Integer.valueOf(tubeNum))).booleanValue()) {
/* 1427 */                     player.sendMessage("Cannot change depth in auto mode.");
/*      */                   } else {
/* 1429 */                     if (player.isSneaking()) {
/* 1430 */                       c.tubeFiringDepth.put(Integer.valueOf(tubeNum), Integer.valueOf(((Integer)c.tubeFiringDepth.get(Integer.valueOf(tubeNum))).intValue() + 5));
/*      */                     } else {
/* 1432 */                       c.tubeFiringDepth.put(Integer.valueOf(tubeNum), Integer.valueOf(((Integer)c.tubeFiringDepth.get(Integer.valueOf(tubeNum))).intValue() + 1));
/*      */                     }
/* 1434 */                     if (((Integer)c.tubeFiringDepth.get(Integer.valueOf(tubeNum))).intValue() > 60) {
/* 1435 */                       c.tubeFiringDepth.put(Integer.valueOf(tubeNum), Integer.valueOf(0));
/*      */                     }
/*      */                   }
/* 1438 */                 } else if (((Integer)c.tubeFiringDisplay.get(Integer.valueOf(tubeNum))).intValue() == 1) {
/* 1439 */                   c.tubeFiringArmed.put(Integer.valueOf(tubeNum), Boolean.valueOf(!((Boolean)c.tubeFiringArmed.get(Integer.valueOf(tubeNum))).booleanValue()));
/* 1440 */                 } else if (((Integer)c.tubeFiringDisplay.get(Integer.valueOf(tubeNum))).intValue() == 2) {
/* 1441 */                   c.tubeFiringAuto.put(Integer.valueOf(tubeNum), Boolean.valueOf(!((Boolean)c.tubeFiringAuto.get(Integer.valueOf(tubeNum))).booleanValue()));
/*      */                 }
/* 1443 */               } else if (((Integer)c.tubeFiringMode.get(Integer.valueOf(tubeNum))).intValue() == -2)
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 1448 */                 if (((Integer)c.tubeFiringDisplay.get(Integer.valueOf(tubeNum))).intValue() == 0) {
/* 1449 */                   if (player.isSneaking()) {
/* 1450 */                     c.tubeFiringDepth.put(Integer.valueOf(tubeNum), Integer.valueOf(((Integer)c.tubeFiringDepth.get(Integer.valueOf(tubeNum))).intValue() + 5));
/*      */                   } else {
/* 1452 */                     c.tubeFiringDepth.put(Integer.valueOf(tubeNum), Integer.valueOf(((Integer)c.tubeFiringDepth.get(Integer.valueOf(tubeNum))).intValue() + 1));
/*      */                   }
/* 1454 */                   if (((Integer)c.tubeFiringDepth.get(Integer.valueOf(tubeNum))).intValue() > 60) {
/* 1455 */                     c.tubeFiringDepth.put(Integer.valueOf(tubeNum), Integer.valueOf(0));
/*      */                   }
/* 1457 */                 } else if (((Integer)c.tubeFiringDisplay.get(Integer.valueOf(tubeNum))).intValue() == 1) {
/* 1458 */                   if (player.isSneaking()) {
/* 1459 */                     c.tubeFiringArm.put(Integer.valueOf(tubeNum), Integer.valueOf(((Integer)c.tubeFiringArm.get(Integer.valueOf(tubeNum))).intValue() + 50));
/*      */                   } else {
/* 1461 */                     c.tubeFiringArm.put(Integer.valueOf(tubeNum), Integer.valueOf(((Integer)c.tubeFiringArm.get(Integer.valueOf(tubeNum))).intValue() + 10));
/*      */                   }
/* 1463 */                   if (((Integer)c.tubeFiringArm.get(Integer.valueOf(tubeNum))).intValue() > 250) {
/* 1464 */                     c.tubeFiringArm.put(Integer.valueOf(tubeNum), Integer.valueOf(20));
/*      */                   }
/*      */                 }
/* 1467 */               } else if (((Integer)c.tubeFiringMode.get(Integer.valueOf(tubeNum))).intValue() == -1) {
/* 1468 */                 if (((Integer)c.tubeFiringDisplay.get(Integer.valueOf(tubeNum))).intValue() == 0) {
/* 1469 */                   if (player.isSneaking()) {
/* 1470 */                     c.tubeFiringDepth.put(Integer.valueOf(tubeNum), Integer.valueOf(((Integer)c.tubeFiringDepth.get(Integer.valueOf(tubeNum))).intValue() + 5));
/*      */                   } else {
/* 1472 */                     c.tubeFiringDepth.put(Integer.valueOf(tubeNum), Integer.valueOf(((Integer)c.tubeFiringDepth.get(Integer.valueOf(tubeNum))).intValue() + 1));
/*      */                   }
/* 1474 */                   if (((Integer)c.tubeFiringDepth.get(Integer.valueOf(tubeNum))).intValue() > 60) {
/* 1475 */                     c.tubeFiringDepth.put(Integer.valueOf(tubeNum), Integer.valueOf(0));
/*      */                   }
/* 1477 */                 } else if (((Integer)c.tubeFiringDisplay.get(Integer.valueOf(tubeNum))).intValue() == 1) {
/* 1478 */                   if (player.isSneaking()) {
/* 1479 */                     c.tubeFiringArm.put(Integer.valueOf(tubeNum), Integer.valueOf(((Integer)c.tubeFiringArm.get(Integer.valueOf(tubeNum))).intValue() + 50));
/*      */                   } else {
/* 1481 */                     c.tubeFiringArm.put(Integer.valueOf(tubeNum), Integer.valueOf(((Integer)c.tubeFiringArm.get(Integer.valueOf(tubeNum))).intValue() + 10));
/*      */                   }
/* 1483 */                   if (((Integer)c.tubeFiringArm.get(Integer.valueOf(tubeNum))).intValue() > 250) {
/* 1484 */                     c.tubeFiringArm.put(Integer.valueOf(tubeNum), Integer.valueOf(20));
/*      */                   }
/*      */                   
/*      */                 }
/*      */               }
/*      */             }
/*      */             else
/*      */             {
/* 1492 */               player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/* 1493 */               if (player.isSneaking()) {
/* 1494 */                 if (((Integer)c.tubeFiringMode.get(Integer.valueOf(tubeNum))).intValue() == -3) {
/* 1495 */                   player.sendMessage("Cannot change mode while torpedo is live.");
/* 1496 */                 } else if (((Integer)c.tubeFiringMode.get(Integer.valueOf(tubeNum))).intValue() == -2) {
/* 1497 */                   c.tubeFiringMode.put(Integer.valueOf(tubeNum), Integer.valueOf(-1));
/* 1498 */                   c.tubeFiringDisplay.put(Integer.valueOf(tubeNum), Integer.valueOf(0));
/*      */ 
/*      */                 }
/* 1501 */                 else if (((Integer)c.tubeFiringMode.get(Integer.valueOf(tubeNum))).intValue() == -1) {
/* 1502 */                   c.tubeFiringMode.put(Integer.valueOf(tubeNum), Integer.valueOf(-2));
/*      */                   
/* 1504 */                   c.tubeFiringDisplay.put(Integer.valueOf(tubeNum), Integer.valueOf(0));
/*      */                 }
/*      */                 
/*      */ 
/*      */               }
/* 1509 */               else if (((Integer)c.tubeFiringMode.get(Integer.valueOf(tubeNum))).intValue() == -3) {
/* 1510 */                 c.tubeFiringDisplay.put(Integer.valueOf(tubeNum), Integer.valueOf((((Integer)c.tubeFiringDisplay.get(Integer.valueOf(tubeNum))).intValue() + 1) % 3));
/*      */ 
/*      */               }
/* 1513 */               else if (((Integer)c.tubeFiringMode.get(Integer.valueOf(tubeNum))).intValue() == -2) {
/* 1514 */                 c.tubeFiringDisplay.put(Integer.valueOf(tubeNum), Integer.valueOf((((Integer)c.tubeFiringDisplay.get(Integer.valueOf(tubeNum))).intValue() + 1) % 2));
/*      */ 
/*      */               }
/* 1517 */               else if (((Integer)c.tubeFiringMode.get(Integer.valueOf(tubeNum))).intValue() == -1) {
/* 1518 */                 c.tubeFiringDisplay.put(Integer.valueOf(tubeNum), Integer.valueOf((((Integer)c.tubeFiringDisplay.get(Integer.valueOf(tubeNum))).intValue() + 1) % 2));
/*      */               }
/*      */               
/*      */             }
/*      */             
/*      */           }
/* 1524 */           else if (tubeNum != 0) {
/* 1525 */             c.tubeFiringMode.put(Integer.valueOf(tubeNum), Integer.valueOf(-2));
/* 1526 */             c.tubeFiringDepth.put(Integer.valueOf(tubeNum), Integer.valueOf(1));
/* 1527 */             c.tubeFiringArm.put(Integer.valueOf(tubeNum), Integer.valueOf(20));
/* 1528 */             c.tubeFiringArmed.put(Integer.valueOf(tubeNum), Boolean.valueOf(false));
/* 1529 */             c.tubeFiringHeading.put(Integer.valueOf(tubeNum), Integer.valueOf(c.rotation));
/* 1530 */             c.tubeFiringAuto.put(Integer.valueOf(tubeNum), Boolean.valueOf(true));
/* 1531 */             c.tubeFiringRudder.put(Integer.valueOf(tubeNum), Integer.valueOf(0));
/* 1532 */             c.tubeFiringDisplay.put(Integer.valueOf(tubeNum), Integer.valueOf(0));
/*      */           }
/*      */           else
/*      */           {
/* 1536 */             player.sendMessage("Sign error");
/*      */           }
/* 1538 */           CraftMover cm = new CraftMover(c, plugin);
/* 1539 */           cm.signUpdates(block);
/*      */         } else {
/* 1541 */           player.sendMessage("Start the vehicle before using this sign.");
/*      */         }
/* 1543 */       } else if (craftTypeName.equalsIgnoreCase("tdc")) {
/* 1544 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1545 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1546 */           if (leftClick) {
/* 1547 */             player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */             
/* 1549 */             if (c.tubeMk1FiringDisplay == 0) {
/* 1550 */               if (player.isSneaking()) {
/* 1551 */                 c.tubeMk1FiringDepth += 5;
/*      */               } else {
/* 1553 */                 c.tubeMk1FiringDepth += 1;
/*      */               }
/* 1555 */               if (c.tubeMk1FiringDepth > 60) {
/* 1556 */                 c.tubeMk1FiringDepth = 0;
/*      */               }
/*      */             }
/*      */             else {
/* 1560 */               if (player.isSneaking()) {
/* 1561 */                 c.tubeMk1FiringSpread -= 5;
/*      */               } else {
/* 1563 */                 c.tubeMk1FiringSpread += 5;
/*      */               }
/* 1565 */               if ((c.tubeMk1FiringSpread > 30) || (c.tubeMk1FiringSpread < 0)) {
/* 1566 */                 c.tubeMk1FiringSpread = 0;
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1573 */             player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/* 1574 */             if (player.isSneaking())
/*      */             {
/* 1576 */               if (c.tubeMk1FiringDisplay == -1) {
/* 1577 */                 c.tubeMk1FiringDisplay = 0;
/*      */ 
/*      */ 
/*      */               }
/* 1581 */               else if (c.tubeMk1FiringMode == -2) {
/* 1582 */                 c.tubeMk1FiringMode = -1;
/*      */ 
/*      */               }
/* 1585 */               else if (c.tubeMk1FiringMode == -1) {
/* 1586 */                 c.tubeMk1FiringMode = -2;
/*      */ 
/*      */               }
/*      */               
/*      */ 
/*      */             }
/* 1592 */             else if ((c.tubeMk1FiringDisplay == -1) || (c.tubeMk1FiringDisplay == 1)) {
/* 1593 */               c.tubeMk1FiringDisplay = 0;
/*      */             }
/*      */             else
/*      */             {
/* 1597 */               c.tubeMk1FiringDisplay = 1;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*      */ 
/* 1603 */           CraftMover cm = new CraftMover(c, plugin);
/* 1604 */           cm.signUpdates(block);
/*      */         } else {
/* 1606 */           player.sendMessage("Start the vehicle before using this sign.");
/*      */         }
/* 1608 */       } else if (craftTypeName.equalsIgnoreCase("radar")) {
/* 1609 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1610 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1611 */           if (!c.radarOn) {
/* 1612 */             c.radarOn = true;
/* 1613 */             player.sendMessage("Radar ACTIVATED!");
/* 1614 */             player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.5F, 1.0F);
/*      */           } else {
/* 1616 */             c.radarOn = false;
/* 1617 */             player.sendMessage("Radar DEACTIVATED!");
/* 1618 */             player.getWorld().playSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 0.5F, 1.0F);
/*      */           }
/* 1620 */           CraftMover cm = new CraftMover(c, plugin);
/* 1621 */           cm.signUpdates(block);
/*      */         } else {
/* 1623 */           player.sendMessage("Start the vehicle before using this sign.");
/*      */         }
/* 1625 */       } else if (craftTypeName.equalsIgnoreCase("sonar")) {
/* 1626 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1627 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1628 */           if (!c.sonarOn) {
/* 1629 */             c.sonarOn = true;
/* 1630 */             player.sendMessage("Sonar ACTIVATED!");
/* 1631 */             player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */           } else {
/* 1633 */             c.sonarOn = false;
/* 1634 */             player.sendMessage("Sonar DEACTIVATED!");
/* 1635 */             player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */           }
/* 1637 */           CraftMover cm = new CraftMover(c, plugin);
/* 1638 */           cm.signUpdates(block);
/*      */         } else {
/* 1640 */           player.sendMessage("Start the vehicle before using this sign.");
/*      */         }
/* 1642 */       } else if (craftTypeName.equalsIgnoreCase("hfsonar")) {
/* 1643 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1644 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1645 */           if (!c.hfOn) {
/* 1646 */             c.hfOn = true;
/* 1647 */             player.sendMessage("High Frequency Sonar ACTIVATED!");
/* 1648 */             player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */           } else {
/* 1650 */             c.hfOn = false;
/* 1651 */             player.sendMessage("High Frequency Sonar DEACTIVATED!");
/* 1652 */             player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */           }
/* 1654 */           CraftMover cm = new CraftMover(c, plugin);
/* 1655 */           cm.signUpdates(block);
/*      */         } else {
/* 1657 */           player.sendMessage("Start the vehicle before using this sign.");
/*      */         }
/* 1659 */       } else if (craftTypeName.equalsIgnoreCase("passivesonar")) {
/* 1660 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1661 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1662 */           if (!c.sonarTargetIDs.isEmpty()) {
/* 1663 */             c.sonarTargetIndex += 1;
/* 1664 */             if (c.sonarTargetIndex >= c.sonarTargetIDs.size()) {
/* 1665 */               c.sonarTargetIndex = 0;
/*      */             }
/* 1667 */             while ((c.sonarTargetIndex < c.sonarTargetIDs.size()) && (((Craft)c.sonarTargetIDs2.get(Integer.valueOf(c.sonarTargetIndex))).sinking)) {
/* 1668 */               c.sonarTargetIndex += 1;
/*      */             }
/* 1670 */             if (c.sonarTargetIndex == c.sonarTargetIDs.size())
/*      */             {
/* 1672 */               c.sonarTargetIndex = -1;
/* 1673 */               c.sonarTarget = null;
/* 1674 */               c.sonarTargetRng = -1.0F;
/*      */             } else {
/* 1676 */               c.sonarTarget = ((Craft)c.sonarTargetIDs2.get(Integer.valueOf(c.sonarTargetIndex)));
/* 1677 */               c.sonarTargetRng = -1.0F;
/*      */             }
/*      */             
/*      */ 
/* 1681 */             player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */           }
/* 1683 */           CraftMover cm = new CraftMover(c, plugin);
/* 1684 */           cm.signUpdates(block);
/*      */         } else {
/* 1686 */           player.sendMessage("Start the vehicle before using this sign.");
/*      */         }
/* 1688 */       } else if (craftTypeName.equalsIgnoreCase("activesonar")) {
/* 1689 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1690 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1691 */           c.doPing = true;
/* 1692 */           player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */           
/*      */ 
/* 1695 */           CraftMover cm = new CraftMover(c, plugin);
/* 1696 */           cm.signUpdates(block);
/*      */         } else {
/* 1698 */           player.sendMessage("Start the vehicle before using this sign.");
/*      */         }
/* 1700 */       } else if (craftTypeName.equalsIgnoreCase("aa-gun")) {
/* 1701 */         if ((!player.hasPermission("movecraft.aa-gun.use")) && (!player.isOp())) {
/* 1702 */           player.sendMessage(ChatColor.RED + "You do not have permission to use this sign");
/* 1703 */           return;
/*      */         }
/* 1705 */         BlockFace bf = BlockFace.NORTH;
/*      */         
/* 1707 */         switch (block.getData()) {
/*      */         case 2: 
/* 1709 */           bf = BlockFace.SOUTH;
/* 1710 */           break;
/*      */         case 3: 
/* 1712 */           bf = BlockFace.NORTH;
/* 1713 */           break;
/*      */         case 4: 
/* 1715 */           bf = BlockFace.EAST;
/* 1716 */           break;
/*      */         case 5: 
/* 1718 */           bf = BlockFace.WEST;
/*      */         }
/*      */         
/*      */         
/* 1722 */         if (player.getItemInHand().getTypeId() > 0) {
/* 1723 */           player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
/* 1724 */           return;
/*      */         }
/*      */         
/* 1727 */         Location newLoc = new Location(player.getWorld(), block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockX() + 0.5D, block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockY(), block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockZ() + 0.5D);
/* 1728 */         player.teleport(newLoc);
/*      */         
/* 1730 */         player.setItemInHand(new ItemStack(369, 1));
/* 1731 */         NavyCraft.aaGunnersList.add(player);
/* 1732 */         player.sendMessage(ChatColor.YELLOW + "Manning AA-Gun! Left Click with Blaze Rod to fire!");
/* 1733 */         player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 1740 */       else if (craftTypeName.equalsIgnoreCase("launcher")) {
/* 1741 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1742 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1743 */           if (!c.launcherOn) {
/* 1744 */             if ((c.speed == 0) && (c.setSpeed == 0) && (c.driverName == null)) {
/* 1745 */               c.launcherOn = true;
/* 1746 */               player.sendMessage("Vehicle launcher armed!");
/*      */             } else {
/* 1748 */               player.sendMessage("Come to full stop and release helm before launching vehicles.");
/*      */             }
/*      */           } else {
/* 1751 */             c.launcherOn = false;
/* 1752 */             player.sendMessage("Vehicle launcher disarmed!");
/*      */           }
/* 1754 */           CraftMover cm = new CraftMover(c, plugin);
/* 1755 */           cm.signUpdates(block);
/* 1756 */           player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */         }
/*      */         else {
/* 1759 */           player.sendMessage("Start the vehicle before using this sign.");
/*      */         }
/* 1761 */       } else if (craftTypeName.equalsIgnoreCase("radio")) {
/* 1762 */         Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1763 */         if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1764 */           if (player.isSneaking()) {
/* 1765 */             if (leftClick) {
/* 1766 */               c.radioSelector -= 1;
/* 1767 */               if (c.radioSelector == 0) {
/* 1768 */                 c.radioSelector = 5;
/*      */               }
/*      */             } else {
/* 1771 */               c.radioSelector += 1;
/* 1772 */               if (c.radioSelector == 6) {
/* 1773 */                 c.radioSelector = 1;
/*      */               }
/*      */             }
/*      */           }
/* 1777 */           else if (c.radioSelector == 1) {
/* 1778 */             if (!leftClick) {
/* 1779 */               c.radio1 += 1;
/* 1780 */               if (c.radio1 > 9) {
/* 1781 */                 c.radio1 = 0;
/*      */               }
/*      */             } else {
/* 1784 */               c.radio1 -= 1;
/* 1785 */               if (c.radio1 < 0) {
/* 1786 */                 c.radio1 = 9;
/*      */               }
/*      */             }
/* 1789 */           } else if (c.radioSelector == 2) {
/* 1790 */             if (!leftClick) {
/* 1791 */               c.radio2 += 1;
/* 1792 */               if (c.radio2 > 9) {
/* 1793 */                 c.radio2 = 0;
/*      */               }
/*      */             } else {
/* 1796 */               c.radio2 -= 1;
/* 1797 */               if (c.radio2 < 0) {
/* 1798 */                 c.radio2 = 9;
/*      */               }
/*      */             }
/* 1801 */           } else if (c.radioSelector == 3) {
/* 1802 */             if (!leftClick) {
/* 1803 */               c.radio3 += 1;
/* 1804 */               if (c.radio3 > 9) {
/* 1805 */                 c.radio3 = 0;
/*      */               }
/*      */             } else {
/* 1808 */               c.radio3 -= 1;
/* 1809 */               if (c.radio3 < 0) {
/* 1810 */                 c.radio3 = 9;
/*      */               }
/*      */             }
/* 1813 */           } else if (c.radioSelector == 4) {
/* 1814 */             if (!leftClick) {
/* 1815 */               c.radio4 += 1;
/* 1816 */               if (c.radio4 > 9) {
/* 1817 */                 c.radio4 = 0;
/*      */               }
/*      */             } else {
/* 1820 */               c.radio4 -= 1;
/* 1821 */               if (c.radio4 < 0) {
/* 1822 */                 c.radio4 = 9;
/*      */               }
/*      */             }
/* 1825 */           } else if (c.radioSelector == 5) {
/* 1826 */             c.radioSetOn = (!c.radioSetOn);
/* 1827 */             if (c.radioSetOn) {
/* 1828 */               player.sendMessage("Radio turned on.");
/*      */             } else {
/* 1830 */               player.sendMessage("Radio turned off.");
/*      */             }
/*      */           }
/*      */           
/*      */ 
/* 1835 */           CraftMover cm = new CraftMover(c, plugin);
/* 1836 */           cm.signUpdates(block);
/* 1837 */           player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */         }
/*      */         else {
/* 1840 */           player.sendMessage("Start the vehicle before using this sign.");
/*      */         } } else { CraftMover cm;
/* 1842 */         if (craftTypeName.equalsIgnoreCase("engine")) {
/* 1843 */           Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
/* 1844 */           if ((c != null) && (Craft.getPlayerCraft(player) == c) && (c.isDressed(player))) {
/* 1845 */             String engineNumStr = sign.getLine(1).trim().toLowerCase();
/* 1846 */             engineNumStr = engineNumStr.replaceAll(ChatColor.BLUE.toString(), "");
/* 1847 */             int engNum = -1;
/* 1848 */             if (!engineNumStr.isEmpty()) {
/*      */               try {
/* 1850 */                 engNum = Integer.parseInt(engineNumStr);
/*      */               } catch (NumberFormatException nfe) {
/* 1852 */                 engNum = -1;
/*      */               }
/*      */             }
/*      */             
/* 1856 */             if ((engNum > -1) && (c.engineIDLocs.containsKey(Integer.valueOf(engNum)))) {
/* 1857 */               if (((Boolean)c.engineIDOn.get(Integer.valueOf(engNum))).booleanValue()) {
/* 1858 */                 player.sendMessage("Stopping Engine:" + engNum + "!");
/* 1859 */                 c.engineIDOn.put(Integer.valueOf(engNum), Boolean.valueOf(false));
/* 1860 */               } else if (c.submergedMode) {
/* 1861 */                 if ((((Integer)c.engineIDTypes.get(Integer.valueOf(engNum))).intValue() != 0) && (((Integer)c.engineIDTypes.get(Integer.valueOf(engNum))).intValue() != 1) && (((Integer)c.engineIDTypes.get(Integer.valueOf(engNum))).intValue() != 2) && (((Integer)c.engineIDTypes.get(Integer.valueOf(engNum))).intValue() != 4) && (((Integer)c.engineIDTypes.get(Integer.valueOf(engNum))).intValue() != 9)) {
/* 1862 */                   player.sendMessage("Cannot start this engine while set to dive!");
/*      */                 } else {
/* 1864 */                   player.sendMessage("Starting Engine:" + engNum + "!");
/* 1865 */                   c.engineIDOn.put(Integer.valueOf(engNum), Boolean.valueOf(true));
/*      */                 }
/*      */               } else {
/* 1868 */                 player.sendMessage("Starting Engine:" + engNum + "!");
/* 1869 */                 c.engineIDOn.put(Integer.valueOf(engNum), Boolean.valueOf(true));
/*      */               }
/* 1871 */               player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 1.0F);
/*      */             }
/*      */             
/* 1874 */             cm = new CraftMover(c, plugin);
/* 1875 */             cm.signUpdates(block);
/*      */           }
/*      */           else {
/* 1878 */             player.sendMessage("Start the vehicle before using this sign.");
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1883 */           CraftType craftType = CraftType.getCraftType(craftTypeName);
/*      */           
/*      */ 
/* 1886 */           if ((craftType != null) || (craftTypeName.equalsIgnoreCase("helm")))
/*      */           {
/* 1888 */             if (NavyCraft.checkNoDriveRegion(player.getLocation())) {
/* 1889 */               player.sendMessage(ChatColor.RED + "You do not have permission to drive vehicles in this area. Please use a spawner.");
/* 1890 */               return;
/*      */             }
/* 1892 */             Craft testCraft = Craft.getCraft(block.getX(), block.getY(), block.getZ());
/*      */             
/* 1894 */             if ((testCraft != null) && ((testCraft.captainName == null) || (testCraft.abandoned) || ((testCraft.captainAbandoned) && (!craftTypeName.equalsIgnoreCase("helm")))))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1902 */               if ((!player.hasPermission("movecraft." + testCraft.type.name + ".takeover")) && (!player.isOp())) {
/* 1903 */                 player.sendMessage(ChatColor.RED + "You do not have permission to takeover this vehicle.");
/* 1904 */                 return;
/*      */               }
/*      */               
/* 1907 */               if ((testCraft.isAutoCraft) && (!player.isOp())) {
/* 1908 */                 player.sendMessage(ChatColor.RED + "You cannot drive an auto-pilot vehicle.");
/* 1909 */                 return;
/*      */               }
/* 1911 */               if (player.getItemInHand().getTypeId() > 0) {
/* 1912 */                 player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
/* 1913 */                 return;
/*      */               }
/*      */               
/* 1916 */               if (craftTypeName.equalsIgnoreCase("helm")) {
/* 1917 */                 player.sendMessage("There is no captain. Use main vehicle sign.");
/* 1918 */                 return;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1926 */               if ((testCraft.abandoned) && (testCraft.captainName != null) && (player.getName() != testCraft.captainName))
/*      */               {
/*      */ 
/* 1929 */                 if (!testCraft.takingOver) {
/* 1930 */                   Craft.takeoverTimerThread(player, testCraft);
/*      */                 }
/* 1932 */                 player.sendMessage(ChatColor.YELLOW + "This vehicle will become abandoned in 2 minutes.");
/* 1933 */                 return; }
/* 1934 */               if ((testCraft.captainAbandoned) && (testCraft.crewNames.contains(player.getName())) && (testCraft.captainName != null)) {
/* 1935 */                 testCraft.captainName = player.getName();
/* 1936 */                 testCraft.captainAbandoned = false;
/* 1937 */                 player.sendMessage(ChatColor.GREEN + ChatColor.BOLD + "You take command of the vehicle.");
/* 1938 */                 for (String s : testCraft.crewNames) {
/* 1939 */                   org.bukkit.entity.Player p = plugin.getServer().getPlayer(s);
/* 1940 */                   if ((p != null) && (s != player.getName())) {
/* 1941 */                     p.sendMessage(ChatColor.LIGHT_PURPLE + ChatColor.BOLD + player.getName() + " takes command of your crew.");
/*      */                   }
/*      */                 }
/* 1944 */                 return;
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 1950 */               player.setItemInHand(new ItemStack(283, 1));
/*      */               
/* 1952 */               testCraft.buildCrew(player, false);
/* 1953 */               if (testCraft.customName != null) {
/* 1954 */                 player.sendMessage(ChatColor.YELLOW + "You take command of the " + ChatColor.WHITE + testCraft.customName.toUpperCase() + ChatColor.YELLOW + " class!");
/*      */               } else {
/* 1956 */                 player.sendMessage(ChatColor.YELLOW + "You take command of the " + ChatColor.WHITE + testCraft.name.toUpperCase() + ChatColor.YELLOW + " class!");
/*      */               }
/* 1958 */               player.sendMessage(ChatColor.YELLOW + "You take control of the helm.");
/* 1959 */               testCraft.haveControl = true;
/* 1960 */               testCraft.launcherOn = false;
/*      */               
/*      */ 
/*      */ 
/*      */               Location newLoc;
/*      */               
/*      */ 
/*      */               Location newLoc;
/*      */               
/*      */ 
/* 1970 */               if ((block.getRelative(BlockFace.DOWN).getTypeId() != 0) && (block.getRelative(BlockFace.DOWN).getTypeId() != 68)) {
/* 1971 */                 newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5D, block.getLocation().getBlockY(), block.getLocation().getBlockZ() + 0.5D);
/*      */               } else {
/* 1973 */                 newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5D, block.getLocation().getBlockY() - 1.0D, block.getLocation().getBlockZ() + 0.5D);
/*      */               }
/* 1975 */               newLoc.setYaw(player.getLocation().getYaw());
/* 1976 */               player.teleport(newLoc);
/*      */               
/* 1978 */               CraftMover cm = new CraftMover(testCraft, plugin);
/* 1979 */               cm.structureUpdate(null, false);
/*      */               
/* 1981 */               if (craftType != testCraft.type) {
/* 1982 */                 testCraft.type = craftType;
/*      */               }
/* 1984 */               return; }
/* 1985 */             if ((testCraft != null) && (!testCraft.launcherOn))
/*      */             {
/* 1987 */               if ((testCraft.driverName != null) && (testCraft.driverName != player.getName())) {
/* 1988 */                 player.sendMessage(testCraft.driverName + ChatColor.YELLOW + " already has the helm.");
/* 1989 */               } else { if ((testCraft.driverName != null) && (testCraft.driverName == player.getName())) {
/* 1990 */                   player.sendMessage(ChatColor.YELLOW + "Avoid clicking on the sign while driving.");
/*      */                   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1997 */                   return;
/*      */                 }
/* 1999 */                 if (player.getItemInHand().getTypeId() > 0) {
/* 2000 */                   player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
/* 2001 */                   return;
/*      */                 }
/*      */                 
/* 2004 */                 if (playerCraft != testCraft) {
/* 2005 */                   player.sendMessage(ChatColor.RED + "You are not on this crew.");
/*      */                   
/* 2007 */                   return;
/*      */                 }
/*      */                 
/* 2010 */                 if (!testCraft.isDressed(player)) return;
/* 2011 */                 if ((testCraft.type != craftType) && (!craftTypeName.equalsIgnoreCase("helm"))) {
/* 2012 */                   player.sendMessage(ChatColor.RED + "Vehicle sign differs from class...try /ship remove?");
/* 2013 */                   return;
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2020 */                 testCraft.driverName = player.getName();
/* 2021 */                 player.sendMessage(ChatColor.YELLOW + "You take control of the helm.");
/* 2022 */                 testCraft.haveControl = true;
/* 2023 */                 if ((craftType != null) && (craftType != testCraft.type)) {
/* 2024 */                   testCraft.type = craftType;
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/*      */                 Location newLoc;
/*      */                 
/*      */ 
/*      */                 Location newLoc;
/*      */                 
/*      */ 
/* 2035 */                 if ((block.getRelative(BlockFace.DOWN).getTypeId() != 0) && (block.getRelative(BlockFace.DOWN).getTypeId() != 68)) {
/* 2036 */                   newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5D, block.getLocation().getBlockY(), block.getLocation().getBlockZ() + 0.5D);
/*      */                 } else {
/* 2038 */                   newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5D, block.getLocation().getBlockY() - 1.0D, block.getLocation().getBlockZ() + 0.5D);
/*      */                 }
/* 2040 */                 newLoc.setYaw(player.getLocation().getYaw());
/* 2041 */                 player.teleport(newLoc);
/* 2042 */                 player.setItemInHand(new ItemStack(283, 1));
/*      */                 
/* 2044 */                 CraftMover cm = new CraftMover(testCraft, plugin);
/* 2045 */                 cm.structureUpdate(null, false);
/*      */               }
/* 2047 */               return; }
/* 2048 */             if (testCraft != null)
/*      */             {
/* 2050 */               if ((craftType == testCraft.type) || (craftTypeName.equalsIgnoreCase("helm"))) {
/* 2051 */                 player.sendMessage("Cannot use main vehicle sign, helm sign, or sign of same type while launcher is armed.");
/* 2052 */                 return; }
/* 2053 */               if (testCraft.speed != 0) {
/* 2054 */                 player.sendMessage("Cannot launch vehicles while main vehicle is moving.");
/*      */               }
/*      */               
/*      */             }
/* 2058 */             else if (craftTypeName.equalsIgnoreCase("helm")) {
/* 2059 */               player.sendMessage("Start the craft first. Use main vehicle sign.");
/* 2060 */               return;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2071 */             if ((!player.hasPermission("movecraft." + craftType.name + ".start")) && (!player.isOp())) {
/* 2072 */               player.sendMessage(ChatColor.RED + "You do not have permission to initialize this type of vehicle.");
/* 2073 */               return;
/*      */             }
/*      */             
/* 2076 */             if (player.getItemInHand().getTypeId() > 0) {
/* 2077 */               player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
/* 2078 */               return;
/*      */             }
/*      */             
/* 2081 */             String name = sign.getLine(1);
/*      */             
/* 2083 */             if (name.trim().equals("")) {
/* 2084 */               name = null;
/*      */             }
/*      */             
/* 2087 */             int x = block.getX();
/* 2088 */             int y = block.getY();
/* 2089 */             int z = block.getZ();
/*      */             
/* 2091 */             int direction = block.getData();
/*      */             
/*      */ 
/* 2094 */             x += (direction == 5 ? -1 : direction == 4 ? 1 : 0);
/* 2095 */             z += (direction == 3 ? -1 : direction == 2 ? 1 : 0);
/*      */             
/* 2097 */             float dr = 0.0F;
/*      */             
/* 2099 */             switch (block.getData()) {
/*      */             case 2: 
/* 2101 */               dr = 180.0F;
/* 2102 */               break;
/*      */             case 3: 
/* 2104 */               dr = 0.0F;
/* 2105 */               break;
/*      */             case 4: 
/* 2107 */               dr = 90.0F;
/* 2108 */               break;
/*      */             case 5: 
/* 2110 */               dr = 270.0F;
/*      */             }
/*      */             
/* 2113 */             player.setItemInHand(new ItemStack(283, 1));
/* 2114 */             Craft theCraft = NavyCraft.instance.createCraft(player, craftType, x, y, z, name, dr, block, false);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2124 */             if (theCraft != null)
/*      */             {
/*      */               Location newLoc;
/*      */               
/*      */ 
/*      */               Location newLoc;
/*      */               
/*      */ 
/* 2132 */               if ((block.getRelative(BlockFace.DOWN).getTypeId() != 0) && (block.getRelative(BlockFace.DOWN).getTypeId() != 68)) {
/* 2133 */                 newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5D, block.getLocation().getBlockY(), block.getLocation().getBlockZ() + 0.5D);
/*      */               } else {
/* 2135 */                 newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5D, block.getLocation().getBlockY() - 1.0D, block.getLocation().getBlockZ() + 0.5D);
/*      */               }
/* 2137 */               newLoc.setYaw(player.getLocation().getYaw());
/* 2138 */               player.teleport(newLoc);
/*      */               
/* 2140 */               CraftMover cm = new CraftMover(theCraft, plugin);
/* 2141 */               cm.structureUpdate(null, false);
/* 2142 */               sign.getLine(3).equalsIgnoreCase("center");
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/* 2147 */               player.setItemInHand(null);
/*      */             }
/*      */             
/* 2150 */             return; }
/* 2151 */           if ((craftTypeName.equalsIgnoreCase("engage")) && (sign.getLine(1).equalsIgnoreCase("hyperdrive"))) {
/* 2152 */             if (playerCraft == null) {
/* 2153 */               player.kickPlayer("Don't.");
/* 2154 */               return;
/*      */             }
/* 2156 */             Craft_Hyperspace.enterHyperSpace(playerCraft);
/* 2157 */             sign.setLine(0, "Disengage Hyperdrive");
/* 2158 */           } else if ((craftTypeName.equalsIgnoreCase("disengage")) && (sign.getLine(1).equalsIgnoreCase("hyperdrive"))) {
/* 2159 */             if (playerCraft == null) {
/* 2160 */               player.kickPlayer("I am TIRED of these MOTHER____ING noobs on this MOTHER____ING server.");
/* 2161 */               return;
/*      */             }
/* 2163 */             Craft_Hyperspace.exitHyperSpace(playerCraft);
/* 2164 */             sign.setLine(0, "Engage Hyperdrive");
/*      */           }
/*      */         }
/*      */       }
/*      */     } }
/*      */   
/* 2170 */   public static org.bukkit.entity.Player matchPlayerName(String subName) { Set<org.bukkit.entity.Player> playersOnline = new java.util.HashSet();
/* 2171 */     playersOnline.addAll(NavyCraft.instance.getServer().getOnlinePlayers());
/* 2172 */     ArrayList<org.bukkit.entity.Player> userList = new ArrayList();
/*      */     
/* 2174 */     for (org.bukkit.entity.Player p : playersOnline) {
/* 2175 */       if (p.getName().contains(subName)) {
/* 2176 */         userList.add(p);
/*      */       }
/*      */     }
/*      */     
/* 2180 */     if (userList.size() == 1) {
/* 2181 */       return (org.bukkit.entity.Player)userList.get(0);
/*      */     }
/* 2183 */     System.out.println("Attempted to find player matching " + subName + " but failed.");
/* 2184 */     return null;
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
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void onSignChange(SignChangeEvent event)
/*      */   {
/* 2200 */     NavyCraft.instance.DebugMessage("A SIGN CHANGED!", 3);
/*      */     
/* 2202 */     org.bukkit.entity.Player player = event.getPlayer();
/* 2203 */     String craftTypeName = event.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/*      */     
/*      */ 
/* 2206 */     if (craftTypeName.startsWith("[")) {
/* 2207 */       craftTypeName = craftTypeName.substring(1, craftTypeName.length() - 1);
/*      */     }
/* 2209 */     if (craftTypeName.startsWith("*")) {
/* 2210 */       craftTypeName = craftTypeName.substring(1, craftTypeName.length() - 1);
/*      */     }
/*      */     
/*      */ 
/* 2214 */     CraftType craftType = CraftType.getCraftType(craftTypeName);
/*      */     
/* 2216 */     if ((!player.isOp()) && ((craftType != null) || (craftTypeName.equalsIgnoreCase("helm")) || (craftTypeName.equalsIgnoreCase("periscope")) || (craftTypeName.equalsIgnoreCase("nav")) || (craftTypeName.equalsIgnoreCase("aa-gun")) || (craftTypeName.equalsIgnoreCase("select")) || (craftTypeName.equalsIgnoreCase("claim")) || (craftTypeName.equalsIgnoreCase("spawn")) || (craftTypeName.equalsIgnoreCase("recall")) || (craftTypeName.equalsIgnoreCase("target")) || (craftTypeName.equalsIgnoreCase("radar")) || (craftTypeName.equalsIgnoreCase("detector")) || (craftTypeName.equalsIgnoreCase("sonar")) || (craftTypeName.equalsIgnoreCase("hydrophone")) || (craftTypeName.equalsIgnoreCase("subdrive")) || (craftTypeName.equalsIgnoreCase("firecontrol")) || (craftTypeName.equalsIgnoreCase("passivesonar")) || (craftTypeName.equalsIgnoreCase("activesonar")) || (craftTypeName.equalsIgnoreCase("hfsonar")) || (craftTypeName.equalsIgnoreCase("launcher")) || (craftTypeName.equalsIgnoreCase("engine")) || (craftTypeName.equalsIgnoreCase("tdc")) || (craftTypeName.equalsIgnoreCase("radio"))) && (!com.maximuspayne.navycraft.plugins.PermissionInterface.CheckPermission(player, "movecraft." + craftTypeName + ".create"))) {
/* 2217 */       player.sendMessage("You don't have permission to create this type of sign!");
/* 2218 */       event.setCancelled(true);
/*      */     }
/*      */     
/* 2221 */     Craft theCraft = Craft.getPlayerCraft(event.getPlayer());
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
/* 2239 */     theCraft = Craft.getCraft(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
/* 2240 */     if ((theCraft != null) && 
/* 2241 */       (!player.isOp()) && ((craftTypeName.equalsIgnoreCase("helm")) || (craftTypeName.equalsIgnoreCase("periscope")) || (craftTypeName.equalsIgnoreCase("nav")) || (craftTypeName.equalsIgnoreCase("aa-gun")) || (craftTypeName.equalsIgnoreCase("select")) || (craftTypeName.equalsIgnoreCase("spawn")) || (craftTypeName.equalsIgnoreCase("recall")) || (craftTypeName.equalsIgnoreCase("target")) || (craftTypeName.equalsIgnoreCase("radar")) || (craftTypeName.equalsIgnoreCase("detector")) || (craftTypeName.equalsIgnoreCase("sonar")) || (craftTypeName.equalsIgnoreCase("hydrophone")) || (craftTypeName.equalsIgnoreCase("subdrive")) || (craftTypeName.equalsIgnoreCase("firecontrol")) || (craftTypeName.equalsIgnoreCase("passivesonar")) || (craftTypeName.equalsIgnoreCase("activesonar")) || (craftTypeName.equalsIgnoreCase("hfsonar")) || (craftTypeName.equalsIgnoreCase("launcher")) || (craftTypeName.equalsIgnoreCase("engine")) || (craftTypeName.equalsIgnoreCase("tdc")) || (craftTypeName.equalsIgnoreCase("radio")))) {
/* 2242 */       player.sendMessage("You cannot create this sign on a running vehicle");
/* 2243 */       event.setCancelled(true);
/* 2244 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 2249 */     if (((player.getWorld().getName().equalsIgnoreCase("warworld1")) || (player.getWorld().getName().equalsIgnoreCase("warworld3"))) && ((craftTypeName.equalsIgnoreCase("helm")) || (craftTypeName.equalsIgnoreCase("nav")) || (craftTypeName.equalsIgnoreCase("periscope")) || (craftTypeName.equalsIgnoreCase("aa-gun")) || (craftTypeName.equalsIgnoreCase("radar")) || (craftTypeName.equalsIgnoreCase("detector")) || (craftTypeName.equalsIgnoreCase("sonar")) || (craftTypeName.equalsIgnoreCase("hydrophone")) || (craftTypeName.equalsIgnoreCase("subdrive")) || (craftTypeName.equalsIgnoreCase("firecontrol")) || (craftTypeName.equalsIgnoreCase("passivesonar")) || (craftTypeName.equalsIgnoreCase("activesonar")) || (craftTypeName.equalsIgnoreCase("hfsonar")) || (craftTypeName.equalsIgnoreCase("launcher")) || (craftTypeName.equalsIgnoreCase("engine")) || (craftTypeName.equalsIgnoreCase("tdc")) || (craftTypeName.equalsIgnoreCase("radio")))) {
/* 2250 */       int cost = 0;
/* 2251 */       if (craftTypeName.equalsIgnoreCase("helm")) {
/* 2252 */         cost = 50;
/* 2253 */       } else if (craftTypeName.equalsIgnoreCase("nav")) {
/* 2254 */         cost = 50;
/* 2255 */       } else if (craftTypeName.equalsIgnoreCase("periscope")) {
/* 2256 */         cost = 100;
/* 2257 */       } else if (craftTypeName.equalsIgnoreCase("aa-gun")) {
/* 2258 */         cost = 100;
/* 2259 */       } else if (craftTypeName.equalsIgnoreCase("radio")) {
/* 2260 */         cost = 50;
/* 2261 */       } else if (craftTypeName.equalsIgnoreCase("radar")) {
/* 2262 */         cost = 200;
/* 2263 */       } else if (craftTypeName.equalsIgnoreCase("detector")) {
/* 2264 */         cost = 50;
/* 2265 */       } else if (craftTypeName.equalsIgnoreCase("sonar")) {
/* 2266 */         cost = 250;
/* 2267 */       } else if (craftTypeName.equalsIgnoreCase("hydrophone")) {
/* 2268 */         cost = 100;
/* 2269 */       } else if (craftTypeName.equalsIgnoreCase("subdrive")) {
/* 2270 */         cost = 50;
/* 2271 */       } else if (craftTypeName.equalsIgnoreCase("firecontrol")) {
/* 2272 */         cost = 1000;
/* 2273 */       } else if (craftTypeName.equalsIgnoreCase("tdc")) {
/* 2274 */         cost = 400;
/* 2275 */       } else if (craftTypeName.equalsIgnoreCase("passivesonar")) {
/* 2276 */         cost = 2000;
/* 2277 */       } else if (craftTypeName.equalsIgnoreCase("activesonar")) {
/* 2278 */         cost = 2000;
/* 2279 */       } else if (craftTypeName.equalsIgnoreCase("hfsonar")) {
/* 2280 */         cost = 2000;
/* 2281 */       } else if (craftTypeName.equalsIgnoreCase("engine")) {
/* 2282 */         String engineTypeStr = event.getLine(2).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/* 2283 */         if (engineTypeStr != null) {
/* 2284 */           if (engineTypeStr.equalsIgnoreCase("Diesel 1")) {
/* 2285 */             cost = 100;
/*      */           }
/* 2287 */           if (engineTypeStr.equalsIgnoreCase("Motor 1")) {
/* 2288 */             cost = 150;
/*      */           }
/* 2290 */           if (engineTypeStr.equalsIgnoreCase("Diesel 2")) {
/* 2291 */             cost = 250;
/*      */           }
/* 2293 */           if (engineTypeStr.equalsIgnoreCase("Boiler 1")) {
/* 2294 */             cost = 250;
/*      */           }
/* 2296 */           if (engineTypeStr.equalsIgnoreCase("Diesel 3")) {
/* 2297 */             cost = 1000;
/*      */           }
/* 2299 */           if (engineTypeStr.equalsIgnoreCase("Gasoline 1")) {
/* 2300 */             cost = 50;
/*      */           }
/* 2302 */           if (engineTypeStr.equalsIgnoreCase("Boiler 2")) {
/* 2303 */             cost = 600;
/*      */           }
/* 2305 */           if (engineTypeStr.equalsIgnoreCase("Boiler 3")) {
/* 2306 */             cost = 1250;
/*      */           }
/* 2308 */           if (engineTypeStr.equalsIgnoreCase("Gasoline 2")) {
/* 2309 */             cost = 100;
/*      */           }
/* 2311 */           if (engineTypeStr.equalsIgnoreCase("Nuclear")) {
/* 2312 */             cost = 10000;
/*      */           }
/* 2314 */           if (engineTypeStr.equalsIgnoreCase("Airplane 1")) {
/* 2315 */             cost = 50;
/*      */           }
/* 2317 */           if (engineTypeStr.equalsIgnoreCase("Airplane 2")) {
/* 2318 */             cost = 80;
/*      */           }
/* 2320 */           if (engineTypeStr.equalsIgnoreCase("Airplane 3")) {
/* 2321 */             cost = 120;
/*      */           }
/* 2323 */           if (engineTypeStr.equalsIgnoreCase("Airplane 4")) {
/* 2324 */             cost = 160;
/*      */           }
/* 2326 */           if (engineTypeStr.equalsIgnoreCase("Airplane 7")) {
/* 2327 */             cost = 500;
/*      */           }
/* 2329 */           if (engineTypeStr.equalsIgnoreCase("Airplane 5")) {
/* 2330 */             cost = 400;
/*      */           }
/* 2332 */           if (engineTypeStr.equalsIgnoreCase("Airplane 6")) {
/* 2333 */             cost = 500;
/*      */           }
/* 2335 */           if (engineTypeStr.equalsIgnoreCase("Airplane 8")) {
/* 2336 */             cost = 5000;
/*      */           }
/* 2338 */           if (engineTypeStr.equalsIgnoreCase("Tank 1")) {
/* 2339 */             cost = 50;
/*      */           }
/* 2341 */           if (engineTypeStr.equalsIgnoreCase("Tank 2")) {
/* 2342 */             cost = 5000;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 2347 */       if (cost > 0)
/*      */       {
/* 2349 */         Essentials ess = (Essentials)plugin.getServer().getPluginManager().getPlugin("Essentials");
/* 2350 */         if (ess == null) {
/* 2351 */           player.sendMessage("Essentials Economy error");
/* 2352 */           return;
/*      */         }
/* 2354 */         if (!ess.getUser(player).canAfford(new BigDecimal(cost))) {
/* 2355 */           player.sendMessage(ChatColor.YELLOW + "You cannot afford this sign:" + ChatColor.RED + "$" + cost);
/* 2356 */           event.setCancelled(true);
/* 2357 */           return;
/*      */         }
/* 2359 */         ess.getUser(player).takeMoney(new BigDecimal(cost));
/* 2360 */         NavyCraft.playerLastBoughtSign.put(player, event.getBlock());
/* 2361 */         NavyCraft.playerLastBoughtCost.put(player, Integer.valueOf(cost));
/* 2362 */         NavyCraft.playerLastBoughtSignString0.put(player, craftTypeName);
/* 2363 */         String string1 = event.getLine(1).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/* 2364 */         NavyCraft.playerLastBoughtSignString1.put(player, string1);
/* 2365 */         String string2 = event.getLine(2).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/* 2366 */         NavyCraft.playerLastBoughtSignString2.put(player, string2);
/* 2367 */         player.sendMessage(ChatColor.YELLOW + "You purchase sign for " + ChatColor.GREEN + "$" + cost + ChatColor.YELLOW + ". Type " + ChatColor.WHITE + "\"/sign undo\"" + ChatColor.YELLOW + " to cancel.");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void onBlockPhysics(BlockPhysicsEvent event)
/*      */   {
/* 2375 */     if (!event.isCancelled())
/*      */     {
/* 2377 */       Block block = event.getBlock();
/*      */       
/*      */ 
/* 2380 */       if (Craft_Hyperspace.hyperspaceBlocks.contains(block)) {
/* 2381 */         event.setCancelled(true);
/*      */       }
/*      */       
/* 2384 */       if ((block.getTypeId() == 63) || (block.getTypeId() == 68) || (block.getTypeId() == 50) || (block.getTypeId() == 75) || (block.getTypeId() == 76) || (block.getTypeId() == 65) || (block.getTypeId() == 64) || (block.getTypeId() == 71) || (block.getTypeId() == 70) || (block.getTypeId() == 72) || (block.getTypeId() == 143)) {
/* 2385 */         Craft c = Craft.getCraft(block.getX(), block.getY(), block.getZ());
/* 2386 */         if (c != null)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2393 */           if ((event.getChangedTypeId() != 0) && (((block.getTypeId() != 71) && (block.getTypeId() != 64)) || ((event.getChangedTypeId() != 69) && (event.getChangedTypeId() != 77) && (event.getChangedTypeId() != 55) && (event.getChangedTypeId() != 70) && (event.getChangedTypeId() != 72) && (block.getTypeId() != 143) && (block.getTypeId() != 75) && (block.getTypeId() != 76) && (block.getTypeId() != 50))))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 2398 */             event.setCancelled(true);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void onBlockFromTo(BlockFromToEvent event) {
/* 2407 */     if (!event.isCancelled()) {
/* 2408 */       Block block = event.getToBlock();
/*      */       
/* 2410 */       if (((block.getTypeId() == 75) || (block.getTypeId() == 76) || (block.getTypeId() == 65) || (block.getTypeId() == 69) || (block.getTypeId() == 77) || (block.getTypeId() == 70) || (block.getTypeId() == 72) || (block.getTypeId() == 68) || (block.getTypeId() == 63) || (block.getTypeId() == 143) || (block.getTypeId() == 55)) && 
/* 2411 */         (Craft.getCraft(block.getX(), block.getY(), block.getZ()) != null))
/*      */       {
/* 2413 */         block.setTypeId(8);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void onBlockRedstoneChange(BlockRedstoneEvent event)
/*      */   {
/* 2421 */     int blockId = event.getBlock().getTypeId();
/* 2422 */     Location loc = event.getBlock().getLocation();
/*      */     
/*      */ 
/* 2425 */     if ((blockId == 29) || (blockId == 33)) {
/* 2426 */       Craft craft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
/*      */       
/* 2428 */       if (craft != null) {
/* 2429 */         org.bukkit.entity.Player p = plugin.getServer().getPlayer(craft.driverName);
/* 2430 */         if (p != null) {
/* 2431 */           p.sendMessage("You just did something with a piston, didn't you?");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void onBlockBreak(BlockBreakEvent event) {
/* 2439 */     org.bukkit.entity.Player p = event.getPlayer();
/* 2440 */     if ((p.getWorld().getName().equalsIgnoreCase("warworld2")) && (p.getGameMode() != GameMode.CREATIVE)) {
/* 2441 */       if (MoveCraft_PlayerListener.checkForTarget(event.getBlock())) {
/* 2442 */         p.sendMessage(ChatColor.RED + "This sign can only be destroyed by an explosive!");
/* 2443 */         event.setCancelled(true);
/* 2444 */         return;
/*      */       }
/*      */       
/* 2447 */       Block checkBlock = event.getBlock().getRelative(BlockFace.NORTH);
/* 2448 */       if ((checkBlock.getTypeId() == 68) && (MoveCraft_PlayerListener.checkForTarget(checkBlock))) {
/* 2449 */         p.sendMessage(ChatColor.RED + "This sign can only be destroyed by an explosive!");
/* 2450 */         event.setCancelled(true);
/* 2451 */         return;
/*      */       }
/* 2453 */       checkBlock = event.getBlock().getRelative(BlockFace.SOUTH);
/* 2454 */       if ((checkBlock.getTypeId() == 68) && (MoveCraft_PlayerListener.checkForTarget(checkBlock))) {
/* 2455 */         p.sendMessage(ChatColor.RED + "This sign can only be destroyed by an explosive!");
/* 2456 */         event.setCancelled(true);
/* 2457 */         return;
/*      */       }
/* 2459 */       checkBlock = event.getBlock().getRelative(BlockFace.EAST);
/* 2460 */       if ((checkBlock.getTypeId() == 68) && (MoveCraft_PlayerListener.checkForTarget(checkBlock))) {
/* 2461 */         p.sendMessage(ChatColor.RED + "This sign can only be destroyed by an explosive!");
/* 2462 */         event.setCancelled(true);
/* 2463 */         return;
/*      */       }
/* 2465 */       checkBlock = event.getBlock().getRelative(BlockFace.WEST);
/* 2466 */       if ((checkBlock.getTypeId() == 68) && (MoveCraft_PlayerListener.checkForTarget(checkBlock))) {
/* 2467 */         p.sendMessage(ChatColor.RED + "This sign can only be destroyed by an explosive!");
/* 2468 */         event.setCancelled(true);
/*      */       }
/*      */       
/*      */     }
/* 2472 */     else if (p.getWorld().getName().equalsIgnoreCase("warworld1"))
/*      */     {
/* 2474 */       Block checkBlock = event.getBlock();
/* 2475 */       int craftBlockId = checkBlock.getTypeId();
/*      */       
/* 2477 */       Craft checkCraft = Craft.getCraft(checkBlock.getX(), checkBlock.getY(), checkBlock.getZ());
/*      */       
/* 2479 */       if (checkCraft != null) {
/* 2480 */         if ((craftBlockId == 46) && (p.getGameMode() != GameMode.CREATIVE)) {
/* 2481 */           p.sendMessage(ChatColor.RED + "Can't break vehicle TNT.");
/* 2482 */           event.setCancelled(true);
/* 2483 */           return; }
/* 2484 */         if ((craftBlockId == 75) || (craftBlockId == 76) || (craftBlockId == 65) || (craftBlockId == 68) || (craftBlockId == 63) || (craftBlockId == 69) || (craftBlockId == 77) || (craftBlockId == 70) || (craftBlockId == 72) || (craftBlockId == 55) || (craftBlockId == 143) || (craftBlockId == 64) || (craftBlockId == 71)) {
/* 2485 */           int arrayX = checkBlock.getX() - checkCraft.minX;
/* 2486 */           int arrayY = checkBlock.getY() - checkCraft.minY;
/* 2487 */           int arrayZ = checkBlock.getZ() - checkCraft.minZ;
/* 2488 */           checkCraft.matrix[arrayX][arrayY][arrayZ] = -1;
/*      */           
/* 2490 */           if (((craftBlockId == 64) && (checkBlock.getRelative(BlockFace.UP).getTypeId() == 64)) || ((craftBlockId == 71) && (checkBlock.getRelative(BlockFace.UP).getTypeId() == 71))) {
/* 2491 */             checkBlock.getRelative(BlockFace.UP).setTypeId(0);
/* 2492 */             checkCraft.matrix[arrayX][(arrayY + 1)][arrayZ] = -1;
/*      */           }
/* 2494 */           if (((craftBlockId == 64) && (checkBlock.getRelative(BlockFace.DOWN).getTypeId() == 64)) || ((craftBlockId == 71) && (checkBlock.getRelative(BlockFace.DOWN).getTypeId() == 71))) {
/* 2495 */             checkBlock.getRelative(BlockFace.DOWN).setTypeId(0);
/* 2496 */             checkCraft.matrix[arrayX][(arrayY - 1)][arrayZ] = -1;
/*      */           }
/* 2498 */           for (DataBlock complexBlock : checkCraft.complexBlocks) {
/* 2499 */             if (complexBlock.locationMatches(arrayX, arrayY, arrayZ)) {
/* 2500 */               checkCraft.complexBlocks.remove(complexBlock);
/* 2501 */               break;
/*      */             }
/*      */           }
/* 2504 */           for (DataBlock dataBlock : checkCraft.dataBlocks) {
/* 2505 */             if (dataBlock.locationMatches(arrayX, arrayY, arrayZ)) {
/* 2506 */               checkCraft.dataBlocks.remove(dataBlock);
/* 2507 */               break;
/*      */             }
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
/*      */   public static void divingBellThread(Location loc)
/*      */   {
/* 2546 */     new Thread()
/*      */     {
/*      */       public void run()
/*      */       {
/* 2523 */         setPriority(1);
/*      */         
/*      */         try
/*      */         {
/* 2527 */           org.bukkit.World cw = MoveCraft_BlockListener.this.getWorld();
/* 2528 */           for (int i = 0; i < 8; i++) {
/* 2529 */             sleep(200L);
/* 2530 */             if (i % 2 == 0) {
/* 2531 */               cw.playSound(MoveCraft_BlockListener.this, Sound.BLOCK_NOTE_PLING, 1.0F, 1.2F);
/*      */             } else {
/* 2533 */               cw.playSound(MoveCraft_BlockListener.this, Sound.BLOCK_NOTE_PLING, 1.0F, 1.0F);
/*      */             }
/*      */             
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*      */         catch (InterruptedException e)
/*      */         {
/*      */ 
/* 2543 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     }.start();
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
/*      */   public static void surfaceBellThread(Location loc)
/*      */   {
/* 2570 */     new Thread()
/*      */     {
/*      */       public void run()
/*      */       {
/* 2557 */         setPriority(1);
/*      */         
/*      */         try
/*      */         {
/* 2561 */           org.bukkit.World cw = MoveCraft_BlockListener.this.getWorld();
/* 2562 */           for (int i = 0; i < 2; i++) {
/* 2563 */             sleep(300L);
/* 2564 */             cw.playSound(MoveCraft_BlockListener.this, Sound.BLOCK_NOTE_PLING, 1.0F, 2.0F);
/*      */           }
/*      */         } catch (InterruptedException e) {
/* 2567 */           e.printStackTrace();
/*      */         }
/*      */       }
/*      */     }.start();
/*      */   }
/*      */   
/*      */   public static boolean checkSpawnerClear(org.bukkit.entity.Player player, Block block, BlockFace bf, BlockFace bf2) {
/* 2574 */     int shiftRight = 0;
/* 2575 */     int shiftForward = 0;
/* 2576 */     int shiftUp = 0;
/* 2577 */     int shiftDown = 0;
/* 2578 */     if ((((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("DD")) || (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SHIP1"))) {
/* 2579 */       shiftRight = 12;
/* 2580 */       shiftForward = 28;
/* 2581 */       shiftUp = 20;
/* 2582 */       shiftDown = 8;
/* 2583 */     } else if ((((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SUB1")) || (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SHIP2"))) {
/* 2584 */       shiftRight = 8;
/* 2585 */       shiftForward = 43;
/* 2586 */       shiftUp = 20;
/* 2587 */       shiftDown = 8;
/* 2588 */     } else if ((((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SUB2")) || (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SHIP3"))) {
/* 2589 */       shiftRight = 10;
/* 2590 */       shiftForward = 70;
/* 2591 */       shiftUp = 20;
/* 2592 */       shiftDown = 8;
/* 2593 */     } else if ((((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("CL")) || (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SHIP4"))) {
/* 2594 */       shiftRight = 16;
/* 2595 */       shiftForward = 55;
/* 2596 */       shiftUp = 20;
/* 2597 */       shiftDown = 8;
/* 2598 */     } else if ((((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("CA")) || (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("SHIP5"))) {
/* 2599 */       shiftRight = 16;
/* 2600 */       shiftForward = 98;
/* 2601 */       shiftUp = 20;
/* 2602 */       shiftDown = 8;
/* 2603 */     } else if (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("HANGAR1")) {
/* 2604 */       shiftRight = 16;
/* 2605 */       shiftForward = 19;
/* 2606 */       shiftUp = 7;
/* 2607 */       shiftDown = 0;
/* 2608 */     } else if (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("HANGAR2")) {
/* 2609 */       shiftRight = 24;
/* 2610 */       shiftForward = 32;
/* 2611 */       shiftUp = 7;
/* 2612 */       shiftDown = 0;
/* 2613 */     } else if (((String)Craft.playerClipboardsLot.get(player)).equalsIgnoreCase("TANK1")) {
/* 2614 */       shiftRight = 11;
/* 2615 */       shiftForward = 19;
/* 2616 */       shiftUp = 7;
/* 2617 */       shiftDown = 0;
/*      */     } else {
/* 2619 */       player.sendMessage("Unknown lot type error2!");
/*      */     }
/*      */     
/* 2622 */     Block rightLimit = block.getRelative(bf2, shiftRight).getRelative(bf, shiftForward).getRelative(BlockFace.UP, shiftUp);
/* 2623 */     Block leftLimit = block.getRelative(bf, 1).getRelative(BlockFace.DOWN, shiftDown);
/*      */     
/*      */ 
/* 2626 */     int rightX = rightLimit.getX();
/* 2627 */     int rightY = rightLimit.getY();
/* 2628 */     int rightZ = rightLimit.getZ();
/* 2629 */     int leftX = leftLimit.getX();
/* 2630 */     int leftY = leftLimit.getY();
/* 2631 */     int leftZ = leftLimit.getZ();
/*      */     int endX;
/* 2633 */     int startX; int endX; if (rightX < leftX) {
/* 2634 */       int startX = rightX;
/* 2635 */       endX = leftX;
/*      */     } else {
/* 2637 */       startX = leftX;
/* 2638 */       endX = rightX; }
/*      */     int endZ;
/* 2640 */     int startZ; int endZ; if (rightZ < leftZ) {
/* 2641 */       int startZ = rightZ;
/* 2642 */       endZ = leftZ;
/*      */     } else {
/* 2644 */       startZ = leftZ;
/* 2645 */       endZ = rightZ;
/*      */     }
/*      */     
/* 2648 */     for (Craft c : Craft.craftList) {
/* 2649 */       if ((c.world == block.getWorld()) && 
/* 2650 */         (c.maxX >= startX) && (c.minX <= endX) && 
/* 2651 */         (c.maxZ >= startZ) && (c.minZ <= endZ) && 
/* 2652 */         (c.maxY >= leftY) && (c.minY <= rightY)) { return false;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2657 */     return true;
/*      */   }
/*      */   
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void inventoryClickEvent(InventoryClickEvent event) {
/* 2662 */     if ((!event.isCancelled()) && 
/* 2663 */       ((event.getWhoClicked().getWorld().getName().equalsIgnoreCase("warworld1")) || (event.getWhoClicked().getWorld().getName().equalsIgnoreCase("warworld3"))) && 
/* 2664 */       (event.getInventory().getType() == org.bukkit.event.inventory.InventoryType.DISPENSER) && (event.getRawSlot() == 4) && ((event.getCurrentItem().getTypeId() == 388) || (event.getCursor().getTypeId() == 388))) {
/* 2665 */       event.setCancelled(true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static void autoSpawnSign(Block spawnSignBlock, String routeID)
/*      */   {
/* 2673 */     if (plugin.getServer().getWorld("warworld1").getPlayers().isEmpty())
/*      */     {
/* 2675 */       return;
/*      */     }
/* 2677 */     org.bukkit.entity.Player player = (org.bukkit.entity.Player)plugin.getServer().getWorld("warworld1").getPlayers().get(0);
/*      */     
/* 2679 */     int spawnType = 0;
/* 2680 */     if (spawnSignBlock == null) {
/* 2681 */       int newLastSpawn = 0;
/*      */       do {
/* 2683 */         double rand = Math.random();
/* 2684 */         if (rand < 0.15D) {
/* 2685 */           newLastSpawn = 0;
/* 2686 */           spawnSignBlock = plugin.getServer().getWorld("warworld1").getBlockAt(64046, 64, 1525);
/* 2687 */           if (rand < 0.05D) {
/* 2688 */             routeID = "AB1";
/* 2689 */           } else if (rand < 0.1D) {
/* 2690 */             routeID = "AB2";
/*      */           } else {
/* 2692 */             routeID = "AB3";
/*      */           }
/* 2694 */         } else if (rand < 0.3D) {
/* 2695 */           newLastSpawn = 0;
/* 2696 */           spawnSignBlock = plugin.getServer().getWorld("warworld1").getBlockAt(64046, 64, 1525);
/* 2697 */           if (rand < 0.2D) {
/* 2698 */             routeID = "AC1";
/* 2699 */           } else if (rand < 0.25D) {
/* 2700 */             routeID = "AC2";
/*      */           } else {
/* 2702 */             routeID = "AC3";
/*      */           }
/* 2704 */         } else if (rand < 0.45D) {
/* 2705 */           newLastSpawn = 1;
/* 2706 */           spawnSignBlock = plugin.getServer().getWorld("warworld1").getBlockAt(1857, 64, 64757);
/* 2707 */           if (rand < 0.38D) {
/* 2708 */             routeID = "CB1";
/*      */           } else {
/* 2710 */             routeID = "CB2";
/*      */           }
/* 2712 */         } else if (rand < 0.6D) {
/* 2713 */           newLastSpawn = 1;
/* 2714 */           spawnSignBlock = plugin.getServer().getWorld("warworld1").getBlockAt(1857, 64, 64757);
/* 2715 */           if (rand < 0.53D) {
/* 2716 */             routeID = "CA1";
/*      */           } else {
/* 2718 */             routeID = "CA2";
/*      */           }
/* 2720 */         } else if (rand < 0.75D) {
/* 2721 */           newLastSpawn = 2;
/* 2722 */           spawnSignBlock = plugin.getServer().getWorld("warworld1").getBlockAt(-19, 64, 63875);
/* 2723 */           if (rand < 0.68D) {
/* 2724 */             routeID = "BC1";
/*      */           } else {
/* 2726 */             routeID = "BC2";
/*      */           }
/* 2728 */         } else if (rand < 0.9D) {
/* 2729 */           newLastSpawn = 3;
/* 2730 */           spawnSignBlock = plugin.getServer().getWorld("warworld1").getBlockAt(1476, 64, 1920);
/* 2731 */           if (rand < 0.8D) {
/* 2732 */             routeID = "D1";
/* 2733 */           } else if (rand < 0.85D) {
/* 2734 */             routeID = "D2";
/*      */           } else {
/* 2736 */             routeID = "D3";
/*      */           }
/*      */         } else {
/* 2739 */           newLastSpawn = 2;
/* 2740 */           spawnSignBlock = plugin.getServer().getWorld("warworld1").getBlockAt(-19, 64, 63875);
/* 2741 */           if (rand < 0.95D) {
/* 2742 */             routeID = "BA1";
/*      */           } else {
/* 2744 */             routeID = "BA2";
/*      */           }
/*      */         }
/* 2747 */       } while (lastSpawn == newLastSpawn);
/* 2748 */       lastSpawn = newLastSpawn;
/*      */     }
/*      */     
/*      */ 
/* 2752 */     if ((spawnSignBlock == null) || ((spawnSignBlock.getTypeId() != 68) && (spawnSignBlock.getTypeId() != 63))) {
/* 2753 */       System.out.println("Autospawn:No sign detected error");
/* 2754 */       return;
/*      */     }
/*      */     
/* 2757 */     Sign sign = (Sign)spawnSignBlock.getState();
/*      */     
/* 2759 */     Block block = spawnSignBlock;
/* 2760 */     int rotate = -1;
/*      */     
/* 2762 */     BlockFace bf = null;
/* 2763 */     BlockFace bf2 = null;
/* 2764 */     switch (block.getData()) {
/*      */     case 8: 
/* 2766 */       rotate = 180;
/* 2767 */       bf = BlockFace.SOUTH;
/* 2768 */       bf2 = BlockFace.WEST;
/* 2769 */       break;
/*      */     case 0: 
/* 2771 */       rotate = 0;
/* 2772 */       bf = BlockFace.NORTH;
/* 2773 */       bf2 = BlockFace.EAST;
/* 2774 */       break;
/*      */     case 4: 
/* 2776 */       rotate = 90;
/* 2777 */       bf = BlockFace.EAST;
/* 2778 */       bf2 = BlockFace.SOUTH;
/* 2779 */       break;
/*      */     case 12: 
/* 2781 */       rotate = 270;
/* 2782 */       bf = BlockFace.WEST;
/* 2783 */       bf2 = BlockFace.NORTH;
/* 2784 */       break;
/*      */     }
/*      */     
/*      */     
/*      */ 
/* 2789 */     if (rotate == -1) { return;
/*      */     }
/* 2791 */     boolean isAutoSpawn = false;
/* 2792 */     String freeString = sign.getLine(2).trim().toLowerCase();
/* 2793 */     freeString = freeString.replaceAll(ChatColor.BLUE.toString(), "");
/* 2794 */     if (freeString.equalsIgnoreCase("auto")) {
/* 2795 */       isAutoSpawn = true;
/*      */     }
/*      */     
/* 2798 */     String typeString = sign.getLine(1).trim().toLowerCase();
/* 2799 */     typeString = typeString.replaceAll(ChatColor.BLUE.toString(), "");
/*      */     
/* 2801 */     wep = (WorldEditPlugin)plugin.getServer().getPluginManager().getPlugin("WorldEdit");
/* 2802 */     if (wep == null) {
/* 2803 */       System.out.println("WorldEdit error");
/* 2804 */       return;
/*      */     }
/* 2806 */     EditSession es = wep.createEditSession(player);
/*      */     try
/*      */     {
/* 2809 */       int oldLimit = es.getBlockChangeLimit();
/* 2810 */       es.setBlockChangeLimit(50000);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2819 */       int value = 0;
/* 2820 */       double rand = Math.random();
/* 2821 */       Block selectSignBlock; if (rand < 0.6D) {
/* 2822 */         Block selectSignBlock = plugin.getServer().getWorld("warworld1").getBlockAt(-78, 66, 157);
/* 2823 */         spawnType = 0;
/* 2824 */         value = 400 + (int)(Math.random() * 400.0D);
/* 2825 */       } else if (rand < 0.7D) {
/* 2826 */         Block selectSignBlock = plugin.getServer().getWorld("warworld1").getBlockAt(-108, 66, 157);
/* 2827 */         spawnType = 1;
/* 2828 */         value = 1000 + (int)(Math.random() * 1000.0D);
/*      */       } else {
/* 2830 */         selectSignBlock = plugin.getServer().getWorld("warworld1").getBlockAt(-96, 66, 157);
/* 2831 */         spawnType = 2;
/* 2832 */         value = 700 + (int)(Math.random() * 500.0D);
/*      */       }
/*      */       
/* 2835 */       if ((selectSignBlock.getTypeId() != 68) && (selectSignBlock.getTypeId() != 63)) {
/* 2836 */         System.out.println("Autospawn:No select sign detected error");
/* 2837 */         return;
/*      */       }
/*      */       
/* 2840 */       CuboidClipboard ccb = autoSelectSign((Sign)selectSignBlock.getState(), player);
/*      */       
/* 2842 */       if (ccb == null) {
/* 2843 */         System.out.println("Autospawn:Failed to select clipboard");
/* 2844 */         return;
/*      */       }
/*      */       
/* 2847 */       Block pasteBlock = block.getRelative(bf, 0);
/* 2848 */       ccb.rotate2D(rotate);
/* 2849 */       ccb.paste(es, new Vector(pasteBlock.getX(), pasteBlock.getY() - 1, pasteBlock.getZ()), false);
/* 2850 */       es.flushQueue();
/* 2851 */       ccb.rotate2D(-rotate);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2860 */       es.setBlockChangeLimit(oldLimit);
/*      */       
/* 2862 */       if (isAutoSpawn)
/*      */       {
/* 2864 */         int shiftRight = 16;
/* 2865 */         int shiftForward = 55;
/* 2866 */         int shiftUp = 20;
/* 2867 */         int shiftDown = 8;
/*      */         
/* 2869 */         Block rightLimit = block.getRelative(bf2, shiftRight).getRelative(bf, shiftForward).getRelative(BlockFace.UP, shiftUp);
/* 2870 */         Block leftLimit = block.getRelative(bf, 1).getRelative(BlockFace.DOWN, shiftDown);
/*      */         
/*      */ 
/* 2873 */         int rightX = rightLimit.getX();
/* 2874 */         int rightY = rightLimit.getY();
/* 2875 */         int rightZ = rightLimit.getZ();
/* 2876 */         int leftX = leftLimit.getX();
/* 2877 */         int leftY = leftLimit.getY();
/* 2878 */         int leftZ = leftLimit.getZ();
/*      */         int endX;
/* 2880 */         int startX; int endX; if (rightX < leftX) {
/* 2881 */           int startX = rightX;
/* 2882 */           endX = leftX;
/*      */         } else {
/* 2884 */           startX = leftX;
/* 2885 */           endX = rightX; }
/*      */         int endZ;
/* 2887 */         int startZ; int endZ; if (rightZ < leftZ) {
/* 2888 */           int startZ = rightZ;
/* 2889 */           endZ = leftZ;
/*      */         } else {
/* 2891 */           startZ = leftZ;
/* 2892 */           endZ = rightZ;
/*      */         }
/*      */         
/* 2895 */         for (int x = startX; x <= endX; x++) {
/* 2896 */           for (int y = leftY; y <= rightY; y++) {
/* 2897 */             for (int z = startZ; z <= endZ; z++) {
/* 2898 */               if (player.getWorld().getBlockAt(x, y, z).getTypeId() == 68) {
/* 2899 */                 Block shipSignBlock = player.getWorld().getBlockAt(x, y, z);
/* 2900 */                 Sign shipSign = (Sign)shipSignBlock.getState();
/* 2901 */                 String signLine0 = shipSign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/* 2902 */                 CraftType craftType = CraftType.getCraftType(signLine0);
/* 2903 */                 if (craftType != null) {
/* 2904 */                   String name = shipSign.getLine(1);
/*      */                   
/* 2906 */                   if (name.trim().equals("")) {
/* 2907 */                     name = null;
/*      */                   }
/*      */                   
/* 2910 */                   int shipx = shipSignBlock.getX();
/* 2911 */                   int shipy = shipSignBlock.getY();
/* 2912 */                   int shipz = shipSignBlock.getZ();
/*      */                   
/* 2914 */                   int direction = shipSignBlock.getData();
/*      */                   
/*      */ 
/* 2917 */                   shipx += (direction == 5 ? -1 : direction == 4 ? 1 : 0);
/* 2918 */                   shipz += (direction == 3 ? -1 : direction == 2 ? 1 : 0);
/*      */                   
/* 2920 */                   float dr = 0.0F;
/*      */                   
/* 2922 */                   switch (shipSignBlock.getData()) {
/*      */                   case 2: 
/* 2924 */                     dr = 180.0F;
/* 2925 */                     break;
/*      */                   case 3: 
/* 2927 */                     dr = 0.0F;
/* 2928 */                     break;
/*      */                   case 4: 
/* 2930 */                     dr = 90.0F;
/* 2931 */                     break;
/*      */                   case 5: 
/* 2933 */                     dr = 270.0F;
/*      */                   }
/*      */                   
/* 2936 */                   Craft theCraft = NavyCraft.instance.createCraft(player, craftType, shipx, shipy, shipz, name, dr, shipSignBlock, true);
/*      */                   
/* 2938 */                   if (theCraft == null) {
/* 2939 */                     System.out.println("Autospawner: Failed to create craft.");
/* 2940 */                     return;
/*      */                   }
/*      */                   
/* 2943 */                   CraftMover cm = new CraftMover(theCraft, plugin);
/* 2944 */                   cm.structureUpdate(null, false);
/*      */                   
/* 2946 */                   if (isAutoSpawn) {
/* 2947 */                     theCraft.isAutoCraft = true;
/* 2948 */                     theCraft.speedChange(null, true);
/* 2949 */                     theCraft.speedChange(null, true);
/* 2950 */                     theCraft.speedChange(null, true);
/* 2951 */                     theCraft.speedChange(null, true);
/*      */                     
/* 2953 */                     double randomNum = Math.random();
/* 2954 */                     if (randomNum >= 0.5D) {
/* 2955 */                       theCraft.gearChange(null, true);
/*      */                     }
/*      */                     
/* 2958 */                     theCraft.sinkValue = value;
/* 2959 */                     theCraft.routeID = routeID;
/* 2960 */                     if (spawnType == 0) {
/* 2961 */                       System.out.println("Maru spawned route=" + routeID);
/* 2962 */                     } else if (spawnType == 1) {
/* 2963 */                       System.out.println("T2 spawned route=" + routeID);
/*      */                     } else {
/* 2965 */                       System.out.println("Victory Cargo spawned route=" + routeID);
/*      */                     }
/*      */                   }
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
/* 2986 */                   return;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2992 */         System.out.println("No ship sign located!");
/*      */       }
/*      */     }
/*      */     catch (MaxChangedBlocksException e) {
/* 2996 */       System.out.println("Max changed blocks error");
/* 2997 */       return;
/*      */     }
/*      */   }
/*      */   
/*      */   public static CuboidClipboard autoSelectSign(Sign sign, org.bukkit.entity.Player player)
/*      */   {
/* 3003 */     BlockFace bf = null;
/* 3004 */     Block block = sign.getBlock();
/*      */     
/* 3006 */     switch (block.getData()) {
/*      */     case 8: 
/* 3008 */       bf = BlockFace.SOUTH;
/*      */       
/* 3010 */       break;
/*      */     case 0: 
/* 3012 */       bf = BlockFace.NORTH;
/*      */       
/* 3014 */       break;
/*      */     case 4: 
/* 3016 */       bf = BlockFace.EAST;
/*      */       
/* 3018 */       break;
/*      */     case 12: 
/* 3020 */       bf = BlockFace.WEST;
/*      */       
/* 3022 */       break;
/*      */     }
/*      */     
/*      */     
/*      */ 
/* 3027 */     if (bf == null) {
/* 3028 */       System.out.println("Sign error...check direction?");
/* 3029 */       return null;
/*      */     }
/*      */     
/* 3032 */     if (block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getTypeId() == 68) {
/* 3033 */       String spawnName = sign.getLine(3).trim().toLowerCase();
/* 3034 */       Sign sign2 = (Sign)block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getState();
/* 3035 */       String lotStr = sign2.getLine(3).trim().toLowerCase();
/* 3036 */       spawnName = spawnName.replaceAll(ChatColor.BLUE.toString(), "");
/* 3037 */       lotStr = lotStr.replaceAll(ChatColor.BLUE.toString(), "");
/*      */       
/* 3039 */       int lotType = 0;
/* 3040 */       if ((lotStr.equalsIgnoreCase("DD")) || (lotStr.equalsIgnoreCase("SHIP1"))) {
/* 3041 */         lotType = 1;
/* 3042 */       } else if ((lotStr.equalsIgnoreCase("SUB1")) || (lotStr.equalsIgnoreCase("SHIP2"))) {
/* 3043 */         lotType = 2;
/* 3044 */       } else if ((lotStr.equalsIgnoreCase("SUB2")) || (lotStr.equalsIgnoreCase("SHIP3"))) {
/* 3045 */         lotType = 3;
/* 3046 */       } else if ((lotStr.equalsIgnoreCase("CL")) || (lotStr.equalsIgnoreCase("SHIP4"))) {
/* 3047 */         lotType = 4;
/* 3048 */       } else if ((lotStr.equalsIgnoreCase("CA")) || (lotStr.equalsIgnoreCase("SHIP5"))) {
/* 3049 */         lotType = 5;
/* 3050 */       } else if (lotStr.equalsIgnoreCase("HANGAR1")) {
/* 3051 */         lotType = 6;
/* 3052 */       } else if (lotStr.equalsIgnoreCase("HANGAR2")) {
/* 3053 */         lotType = 7;
/* 3054 */       } else if (lotStr.equalsIgnoreCase("TANK1")) {
/* 3055 */         lotType = 8;
/*      */       } else {
/* 3057 */         System.out.println("Sign error...lot type");
/* 3058 */         return null;
/*      */       }
/*      */       
/* 3061 */       wep = (WorldEditPlugin)plugin.getServer().getPluginManager().getPlugin("WorldEdit");
/* 3062 */       if (wep == null) {
/* 3063 */         System.out.println("WorldEdit error");
/* 3064 */         return null;
/*      */       }
/*      */       
/* 3067 */       EditSession es = wep.createEditSession(player);
/*      */       
/*      */       int offsetZ;
/*      */       
/* 3071 */       if (lotType == 1) {
/* 3072 */         Location loc = block.getRelative(bf, 28).getLocation();
/* 3073 */         int sizeX = 13;
/* 3074 */         int sizeY = 28;
/* 3075 */         int sizeZ = 28;
/* 3076 */         int originX = 0;
/* 3077 */         int originY = -8;
/* 3078 */         int originZ = 0;
/* 3079 */         int offsetX = 0;
/* 3080 */         int offsetY = -7;
/* 3081 */         offsetZ = -29; } else { int offsetZ;
/* 3082 */         if (lotType == 2) {
/* 3083 */           Location loc = block.getRelative(bf, 43).getLocation();
/* 3084 */           int sizeX = 9;
/* 3085 */           int sizeY = 28;
/* 3086 */           int sizeZ = 43;
/* 3087 */           int originX = 0;
/* 3088 */           int originY = -8;
/* 3089 */           int originZ = 0;
/* 3090 */           int offsetX = 0;
/* 3091 */           int offsetY = -7;
/* 3092 */           offsetZ = -44; } else { int offsetZ;
/* 3093 */           if (lotType == 3) {
/* 3094 */             Location loc = block.getRelative(bf, 70).getLocation();
/* 3095 */             int sizeX = 11;
/* 3096 */             int sizeY = 28;
/* 3097 */             int sizeZ = 70;
/* 3098 */             int originX = 0;
/* 3099 */             int originY = -8;
/* 3100 */             int originZ = 0;
/* 3101 */             int offsetX = 0;
/* 3102 */             int offsetY = -7;
/* 3103 */             offsetZ = -71; } else { int offsetZ;
/* 3104 */             if (lotType == 4) {
/* 3105 */               Location loc = block.getRelative(bf, 55).getLocation();
/* 3106 */               int sizeX = 17;
/* 3107 */               int sizeY = 28;
/* 3108 */               int sizeZ = 55;
/* 3109 */               int originX = 0;
/* 3110 */               int originY = -8;
/* 3111 */               int originZ = 0;
/* 3112 */               int offsetX = 0;
/* 3113 */               int offsetY = -7;
/* 3114 */               offsetZ = -56; } else { int offsetZ;
/* 3115 */               if (lotType == 5) {
/* 3116 */                 Location loc = block.getRelative(bf, 98).getLocation();
/* 3117 */                 int sizeX = 17;
/* 3118 */                 int sizeY = 28;
/* 3119 */                 int sizeZ = 98;
/* 3120 */                 int originX = 0;
/* 3121 */                 int originY = -8;
/* 3122 */                 int originZ = 0;
/* 3123 */                 int offsetX = 0;
/* 3124 */                 int offsetY = -7;
/* 3125 */                 offsetZ = -99; } else { int offsetZ;
/* 3126 */                 if (lotType == 6) {
/* 3127 */                   Location loc = block.getRelative(bf, 17).getLocation();
/* 3128 */                   int sizeX = 17;
/* 3129 */                   int sizeY = 7;
/* 3130 */                   int sizeZ = 19;
/* 3131 */                   int originX = 0;
/* 3132 */                   int originY = -1;
/* 3133 */                   int originZ = -18;
/* 3134 */                   int offsetX = -17;
/* 3135 */                   int offsetY = 0;
/* 3136 */                   offsetZ = -20; } else { int offsetZ;
/* 3137 */                   if (lotType == 7) {
/* 3138 */                     Location loc = block.getRelative(bf, 25).getLocation();
/* 3139 */                     int sizeX = 25;
/* 3140 */                     int sizeY = 7;
/* 3141 */                     int sizeZ = 32;
/* 3142 */                     int originX = 0;
/* 3143 */                     int originY = -1;
/* 3144 */                     int originZ = -31;
/* 3145 */                     int offsetX = -25;
/* 3146 */                     int offsetY = 0;
/* 3147 */                     offsetZ = -33; } else { int offsetZ;
/* 3148 */                     if (lotType == 8) {
/* 3149 */                       Location loc = block.getRelative(bf, 12).getLocation();
/* 3150 */                       int sizeX = 12;
/* 3151 */                       int sizeY = 7;
/* 3152 */                       int sizeZ = 19;
/* 3153 */                       int originX = 0;
/* 3154 */                       int originY = -1;
/* 3155 */                       int originZ = -18;
/* 3156 */                       int offsetX = -12;
/* 3157 */                       int offsetY = 0;
/* 3158 */                       offsetZ = -20;
/*      */                     }
/*      */                     else
/*      */                     {
/* 3162 */                       System.out.println("Sign error...invalid lot");
/* 3163 */                       return null; } } } } } } } }
/*      */       int offsetZ;
/*      */       int offsetY;
/* 3166 */       int offsetX; int originZ; int originY; int originX; int sizeZ; int sizeY; int sizeX; Location loc; CuboidRegion region = new CuboidRegion(new Vector(loc.getBlockX() + originX, loc.getBlockY() + originY, loc.getBlockZ() + originZ), new Vector(loc.getBlockX() + originX + sizeX, loc.getBlockY() + originY + sizeY, loc.getBlockZ() + originZ + sizeZ));
/*      */       
/* 3168 */       BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
/* 3169 */       clipboard.setOrigin(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
/* 3170 */       com.sk89q.worldedit.world.registry.WorldData worldData = com.sk89q.worldedit.world.registry.LegacyWorldData.getInstance();
/* 3171 */       ClipboardHolder ch = new ClipboardHolder(clipboard, worldData);
/* 3172 */       wep.getSession(player).setClipboard(ch);
/*      */       
/* 3174 */       CuboidClipboard cclipb = new CuboidClipboard(new Vector(sizeX, sizeY, sizeZ), new Vector(loc.getBlockX() + originX, loc.getBlockY() + originY, loc.getBlockZ() + originZ), new Vector(offsetX, offsetY, offsetZ));
/* 3175 */       return cclipb;
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
/* 3190 */     System.out.println("Sign error...check second sign?");
/* 3191 */     return null;
/*      */   }
/*      */   
/*      */   public static void loadDDSigns()
/*      */   {
/* 3196 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3199 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_StartX"));
/* 3200 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_EndX"));
/* 3201 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_WidthX"));
/* 3202 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_StartZ"));
/* 3203 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_EndZ"));
/* 3204 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_WidthZ"));
/* 3205 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3209 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3210 */         if ((syworld.getBlockAt(x, 64, z).getTypeId() == 63) && (syworld.getBlockAt(x, 63, z + 1).getTypeId() == 68)) {
/* 3211 */           Block selectSignBlock = syworld.getBlockAt(x, 64, z);
/* 3212 */           Block selectSignBlock2 = syworld.getBlockAt(x, 63, z + 1);
/* 3213 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3214 */           Sign selectSign2 = (Sign)selectSignBlock2.getState();
/* 3215 */           String signLine0 = selectSign.getLine(0);
/* 3216 */           String sign2Line2 = selectSign2.getLine(2);
/*      */           
/* 3218 */           if (signLine0.equalsIgnoreCase("*select*")) {
/* 3219 */             String playerName = selectSign.getLine(1);
/* 3220 */             String playerName2 = selectSign.getLine(2);
/*      */             
/* 3222 */             if ((playerName2 != null) && (!playerName2.isEmpty())) {
/* 3223 */               playerName = playerName + playerName2;
/*      */             }
/*      */             
/* 3226 */             if (playerName != null)
/*      */             {
/*      */ 
/*      */ 
/* 3230 */               int idNum = -1;
/*      */               try {
/* 3232 */                 idNum = Integer.parseInt(sign2Line2);
/*      */               } catch (NumberFormatException nfe) {
/*      */                 continue;
/*      */               }
/* 3236 */               if (idNum != -1)
/*      */               {
/*      */ 
/*      */ 
/* 3240 */                 if (!NavyCraft.playerDDSigns.containsKey(playerName)) {
/* 3241 */                   NavyCraft.playerDDSigns.put(playerName, new ArrayList());
/* 3242 */                   ((ArrayList)NavyCraft.playerDDSigns.get(playerName)).add(selectSign);
/* 3243 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } else {
/* 3245 */                   ((ArrayList)NavyCraft.playerDDSigns.get(playerName)).add(selectSign);
/* 3246 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static Block findDDOpen() {
/* 3256 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3259 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_StartX"));
/* 3260 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_EndX"));
/* 3261 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_WidthX"));
/* 3262 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_StartZ"));
/* 3263 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_EndZ"));
/* 3264 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship1_WidthZ"));
/* 3265 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3269 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3270 */         if ((syworld.getBlockAt(x, 64, z).getTypeId() == 63) && (syworld.getBlockAt(x, 63, z + 1).getTypeId() == 68)) {
/* 3271 */           Block selectSignBlock = syworld.getBlockAt(x, 64, z);
/* 3272 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3273 */           String signLine0 = selectSign.getLine(0);
/*      */           
/* 3275 */           if (signLine0.equalsIgnoreCase("*claim*")) return selectSignBlock;
/*      */         }
/*      */       }
/*      */     }
/* 3279 */     return null;
/*      */   }
/*      */   
/*      */   public static void loadSUB1Signs() {
/* 3283 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3286 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_StartX"));
/* 3287 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_EndX"));
/* 3288 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_WidthX"));
/* 3289 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_StartZ"));
/* 3290 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_EndZ"));
/* 3291 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_WidthZ"));
/* 3292 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3296 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3297 */         if ((syworld.getBlockAt(x, 64, z).getTypeId() == 63) && (syworld.getBlockAt(x, 63, z + 1).getTypeId() == 68)) {
/* 3298 */           Block selectSignBlock = syworld.getBlockAt(x, 64, z);
/* 3299 */           Block selectSignBlock2 = syworld.getBlockAt(x, 63, z + 1);
/* 3300 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3301 */           Sign selectSign2 = (Sign)selectSignBlock2.getState();
/* 3302 */           String signLine0 = selectSign.getLine(0);
/* 3303 */           String sign2Line2 = selectSign2.getLine(2);
/*      */           
/* 3305 */           if (signLine0.equalsIgnoreCase("*select*")) {
/* 3306 */             String playerName = selectSign.getLine(1);
/* 3307 */             String playerName2 = selectSign.getLine(2);
/*      */             
/* 3309 */             if ((playerName2 != null) && (!playerName2.isEmpty())) {
/* 3310 */               playerName = playerName + playerName2;
/*      */             }
/*      */             
/* 3313 */             if (playerName != null)
/*      */             {
/*      */ 
/*      */ 
/* 3317 */               int idNum = -1;
/*      */               try {
/* 3319 */                 idNum = Integer.parseInt(sign2Line2);
/*      */               } catch (NumberFormatException nfe) {
/*      */                 continue;
/*      */               }
/* 3323 */               if (idNum != -1)
/*      */               {
/*      */ 
/*      */ 
/* 3327 */                 if (!NavyCraft.playerSUB1Signs.containsKey(playerName)) {
/* 3328 */                   NavyCraft.playerSUB1Signs.put(playerName, new ArrayList());
/* 3329 */                   ((ArrayList)NavyCraft.playerSUB1Signs.get(playerName)).add(selectSign);
/* 3330 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } else {
/* 3332 */                   ((ArrayList)NavyCraft.playerSUB1Signs.get(playerName)).add(selectSign);
/* 3333 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static Block findSUB1Open() {
/* 3343 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3346 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_StartX"));
/* 3347 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_EndX"));
/* 3348 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_WidthX"));
/* 3349 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_StartZ"));
/* 3350 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_EndZ"));
/* 3351 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship2_WidthZ"));
/* 3352 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3356 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3357 */         if ((syworld.getBlockAt(x, 64, z).getTypeId() == 63) && (syworld.getBlockAt(x, 63, z + 1).getTypeId() == 68)) {
/* 3358 */           Block selectSignBlock = syworld.getBlockAt(x, 64, z);
/* 3359 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3360 */           String signLine0 = selectSign.getLine(0);
/*      */           
/* 3362 */           if (signLine0.equalsIgnoreCase("*claim*")) return selectSignBlock;
/*      */         }
/*      */       }
/*      */     }
/* 3366 */     return null;
/*      */   }
/*      */   
/*      */   public static void loadSUB2Signs() {
/* 3370 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3373 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_StartX"));
/* 3374 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_EndX"));
/* 3375 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_WidthX"));
/* 3376 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_StartZ"));
/* 3377 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_EndZ"));
/* 3378 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_WidthZ"));
/* 3379 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3383 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3384 */         if ((syworld.getBlockAt(x, 64, z).getTypeId() == 63) && (syworld.getBlockAt(x, 63, z + 1).getTypeId() == 68)) {
/* 3385 */           Block selectSignBlock = syworld.getBlockAt(x, 64, z);
/* 3386 */           Block selectSignBlock2 = syworld.getBlockAt(x, 63, z + 1);
/* 3387 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3388 */           Sign selectSign2 = (Sign)selectSignBlock2.getState();
/* 3389 */           String signLine0 = selectSign.getLine(0);
/* 3390 */           String sign2Line2 = selectSign2.getLine(2);
/*      */           
/* 3392 */           if (signLine0.equalsIgnoreCase("*select*")) {
/* 3393 */             String playerName = selectSign.getLine(1);
/* 3394 */             String playerName2 = selectSign.getLine(2);
/*      */             
/* 3396 */             if ((playerName2 != null) && (!playerName2.isEmpty())) {
/* 3397 */               playerName = playerName + playerName2;
/*      */             }
/*      */             
/* 3400 */             if (playerName != null)
/*      */             {
/*      */ 
/*      */ 
/* 3404 */               int idNum = -1;
/*      */               try {
/* 3406 */                 idNum = Integer.parseInt(sign2Line2);
/*      */               } catch (NumberFormatException nfe) {
/*      */                 continue;
/*      */               }
/* 3410 */               if (idNum != -1)
/*      */               {
/*      */ 
/*      */ 
/* 3414 */                 if (!NavyCraft.playerSUB2Signs.containsKey(playerName)) {
/* 3415 */                   NavyCraft.playerSUB2Signs.put(playerName, new ArrayList());
/* 3416 */                   ((ArrayList)NavyCraft.playerSUB2Signs.get(playerName)).add(selectSign);
/* 3417 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } else {
/* 3419 */                   ((ArrayList)NavyCraft.playerSUB2Signs.get(playerName)).add(selectSign);
/* 3420 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static Block findSUB2Open() {
/* 3430 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3433 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_StartX"));
/* 3434 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_EndX"));
/* 3435 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_WidthX"));
/* 3436 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_StartZ"));
/* 3437 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_EndZ"));
/* 3438 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship3_WidthZ"));
/* 3439 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/* 3442 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3443 */         if ((syworld.getBlockAt(x, 64, z).getTypeId() == 63) && (syworld.getBlockAt(x, 63, z + 1).getTypeId() == 68)) {
/* 3444 */           Block selectSignBlock = syworld.getBlockAt(x, 64, z);
/* 3445 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3446 */           String signLine0 = selectSign.getLine(0);
/*      */           
/* 3448 */           if (signLine0.equalsIgnoreCase("*claim*")) return selectSignBlock;
/*      */         }
/*      */       }
/*      */     }
/* 3452 */     return null;
/*      */   }
/*      */   
/*      */   public static void loadCLSigns() {
/* 3456 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3459 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_StartX"));
/* 3460 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_EndX"));
/* 3461 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_WidthX"));
/* 3462 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_StartZ"));
/* 3463 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_EndZ"));
/* 3464 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_WidthZ"));
/* 3465 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3469 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3470 */         if ((syworld.getBlockAt(x, 64, z).getTypeId() == 63) && (syworld.getBlockAt(x, 63, z + 1).getTypeId() == 68)) {
/* 3471 */           Block selectSignBlock = syworld.getBlockAt(x, 64, z);
/* 3472 */           Block selectSignBlock2 = syworld.getBlockAt(x, 63, z + 1);
/* 3473 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3474 */           Sign selectSign2 = (Sign)selectSignBlock2.getState();
/* 3475 */           String signLine0 = selectSign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/* 3476 */           String sign2Line2 = selectSign2.getLine(2).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/*      */           
/* 3478 */           if (signLine0.equalsIgnoreCase("*select*")) {
/* 3479 */             String playerName = selectSign.getLine(1);
/* 3480 */             String playerName2 = selectSign.getLine(2);
/*      */             
/* 3482 */             if ((playerName2 != null) && (!playerName2.isEmpty())) {
/* 3483 */               playerName = playerName + playerName2;
/*      */             }
/*      */             
/* 3486 */             if (playerName != null)
/*      */             {
/*      */ 
/*      */ 
/* 3490 */               int idNum = -1;
/*      */               try {
/* 3492 */                 idNum = Integer.parseInt(sign2Line2);
/*      */               } catch (NumberFormatException nfe) {
/*      */                 continue;
/*      */               }
/* 3496 */               if (idNum != -1)
/*      */               {
/*      */ 
/*      */ 
/* 3500 */                 if (!NavyCraft.playerCLSigns.containsKey(playerName)) {
/* 3501 */                   NavyCraft.playerCLSigns.put(playerName, new ArrayList());
/* 3502 */                   ((ArrayList)NavyCraft.playerCLSigns.get(playerName)).add(selectSign);
/* 3503 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } else {
/* 3505 */                   ((ArrayList)NavyCraft.playerCLSigns.get(playerName)).add(selectSign);
/* 3506 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static Block findCLOpen() {
/* 3516 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3519 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_StartX"));
/* 3520 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_EndX"));
/* 3521 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_WidthX"));
/* 3522 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_StartZ"));
/* 3523 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_EndZ"));
/* 3524 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship4_WidthZ"));
/* 3525 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3529 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3530 */         if ((syworld.getBlockAt(x, 64, z).getTypeId() == 63) && (syworld.getBlockAt(x, 63, z + 1).getTypeId() == 68)) {
/* 3531 */           Block selectSignBlock = syworld.getBlockAt(x, 64, z);
/* 3532 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3533 */           String signLine0 = selectSign.getLine(0);
/*      */           
/* 3535 */           if (signLine0.equalsIgnoreCase("*claim*")) return selectSignBlock;
/*      */         }
/*      */       }
/*      */     }
/* 3539 */     return null;
/*      */   }
/*      */   
/*      */   public static void loadCASigns() {
/* 3543 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3546 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_StartX"));
/* 3547 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_EndX"));
/* 3548 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_WidthX"));
/* 3549 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_StartZ"));
/* 3550 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_EndZ"));
/* 3551 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_WidthZ"));
/* 3552 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3556 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3557 */         if ((syworld.getBlockAt(x, 64, z).getTypeId() == 63) && (syworld.getBlockAt(x, 63, z + 1).getTypeId() == 68)) {
/* 3558 */           Block selectSignBlock = syworld.getBlockAt(x, 64, z);
/* 3559 */           Block selectSignBlock2 = syworld.getBlockAt(x, 63, z + 1);
/* 3560 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3561 */           Sign selectSign2 = (Sign)selectSignBlock2.getState();
/* 3562 */           String signLine0 = selectSign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/* 3563 */           String sign2Line2 = selectSign2.getLine(2).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/*      */           
/* 3565 */           if (signLine0.equalsIgnoreCase("*select*")) {
/* 3566 */             String playerName = selectSign.getLine(1);
/* 3567 */             String playerName2 = selectSign.getLine(2);
/*      */             
/* 3569 */             if ((playerName2 != null) && (!playerName2.isEmpty())) {
/* 3570 */               playerName = playerName + playerName2;
/*      */             }
/*      */             
/* 3573 */             if (playerName != null)
/*      */             {
/*      */ 
/*      */ 
/* 3577 */               int idNum = -1;
/*      */               try {
/* 3579 */                 idNum = Integer.parseInt(sign2Line2);
/*      */               } catch (NumberFormatException nfe) {
/*      */                 continue;
/*      */               }
/* 3583 */               if (idNum != -1)
/*      */               {
/*      */ 
/*      */ 
/* 3587 */                 if (!NavyCraft.playerCASigns.containsKey(playerName)) {
/* 3588 */                   NavyCraft.playerCASigns.put(playerName, new ArrayList());
/* 3589 */                   ((ArrayList)NavyCraft.playerCASigns.get(playerName)).add(selectSign);
/* 3590 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } else {
/* 3592 */                   ((ArrayList)NavyCraft.playerCASigns.get(playerName)).add(selectSign);
/* 3593 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static Block findCAOpen() {
/* 3603 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3606 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_StartX"));
/* 3607 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_EndX"));
/* 3608 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_WidthX"));
/* 3609 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_StartZ"));
/* 3610 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_EndZ"));
/* 3611 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Ship5_WidthZ"));
/* 3612 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/* 3615 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3616 */         if ((syworld.getBlockAt(x, 64, z).getTypeId() == 63) && (syworld.getBlockAt(x, 63, z + 1).getTypeId() == 68)) {
/* 3617 */           Block selectSignBlock = syworld.getBlockAt(x, 64, z);
/* 3618 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3619 */           String signLine0 = selectSign.getLine(0);
/*      */           
/* 3621 */           if (signLine0.equalsIgnoreCase("*claim*")) return selectSignBlock;
/*      */         }
/*      */       }
/*      */     }
/* 3625 */     return null;
/*      */   }
/*      */   
/*      */   public static void loadHANGAR1Signs() {
/* 3629 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3632 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_StartX"));
/* 3633 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_EndX"));
/* 3634 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_WidthX"));
/* 3635 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_StartZ"));
/* 3636 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_EndZ"));
/* 3637 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_WidthZ"));
/* 3638 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3642 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3643 */         if ((syworld.getBlockAt(x, 65, z).getTypeId() == 63) && (syworld.getBlockAt(x + 1, 64, z).getTypeId() == 68)) {
/* 3644 */           Block selectSignBlock = syworld.getBlockAt(x, 65, z);
/* 3645 */           Block selectSignBlock2 = syworld.getBlockAt(x + 1, 64, z);
/* 3646 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3647 */           Sign selectSign2 = (Sign)selectSignBlock2.getState();
/* 3648 */           String signLine0 = selectSign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/* 3649 */           String sign2Line2 = selectSign2.getLine(2).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/*      */           
/* 3651 */           if (signLine0.equalsIgnoreCase("*select*")) {
/* 3652 */             String playerName = selectSign.getLine(1);
/* 3653 */             String playerName2 = selectSign.getLine(2);
/*      */             
/* 3655 */             if ((playerName2 != null) && (!playerName2.isEmpty())) {
/* 3656 */               playerName = playerName + playerName2;
/*      */             }
/*      */             
/* 3659 */             if (playerName != null)
/*      */             {
/*      */ 
/*      */ 
/* 3663 */               int idNum = -1;
/*      */               try {
/* 3665 */                 idNum = Integer.parseInt(sign2Line2);
/*      */               } catch (NumberFormatException nfe) {
/*      */                 continue;
/*      */               }
/* 3669 */               if (idNum != -1)
/*      */               {
/*      */ 
/*      */ 
/* 3673 */                 if (!NavyCraft.playerHANGAR1Signs.containsKey(playerName)) {
/* 3674 */                   NavyCraft.playerHANGAR1Signs.put(playerName, new ArrayList());
/* 3675 */                   ((ArrayList)NavyCraft.playerHANGAR1Signs.get(playerName)).add(selectSign);
/* 3676 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } else {
/* 3678 */                   ((ArrayList)NavyCraft.playerHANGAR1Signs.get(playerName)).add(selectSign);
/* 3679 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static Block findHANGAR1Open() {
/* 3689 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3692 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_StartX"));
/* 3693 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_EndX"));
/* 3694 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_WidthX"));
/* 3695 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_StartZ"));
/* 3696 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_EndZ"));
/* 3697 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar1_WidthZ"));
/* 3698 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3702 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3703 */         if ((syworld.getBlockAt(x, 65, z).getTypeId() == 63) && (syworld.getBlockAt(x + 1, 64, z).getTypeId() == 68)) {
/* 3704 */           Block selectSignBlock = syworld.getBlockAt(x, 65, z);
/* 3705 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3706 */           String signLine0 = selectSign.getLine(0);
/*      */           
/* 3708 */           if (signLine0.equalsIgnoreCase("*claim*")) return selectSignBlock;
/*      */         }
/*      */       }
/*      */     }
/* 3712 */     return null;
/*      */   }
/*      */   
/*      */   public static void loadHANGAR2Signs() {
/* 3716 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/* 3718 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_StartX"));
/* 3719 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_EndX"));
/* 3720 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_WidthX"));
/* 3721 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_StartZ"));
/* 3722 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_EndZ"));
/* 3723 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_WidthZ"));
/* 3724 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/* 3727 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3728 */         if ((syworld.getBlockAt(x, 64, z).getTypeId() == 63) && (syworld.getBlockAt(x + 1, 64, z).getTypeId() == 68)) {
/* 3729 */           Block selectSignBlock = syworld.getBlockAt(x, 64, z);
/* 3730 */           Block selectSignBlock2 = syworld.getBlockAt(x + 1, 63, z);
/* 3731 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3732 */           Sign selectSign2 = (Sign)selectSignBlock2.getState();
/* 3733 */           String signLine0 = selectSign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/* 3734 */           String sign2Line2 = selectSign2.getLine(2).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/*      */           
/* 3736 */           if (signLine0.equalsIgnoreCase("*select*")) {
/* 3737 */             String playerName = selectSign.getLine(1);
/* 3738 */             String playerName2 = selectSign.getLine(2);
/*      */             
/* 3740 */             if ((playerName2 != null) && (!playerName2.isEmpty())) {
/* 3741 */               playerName = playerName + playerName2;
/*      */             }
/*      */             
/* 3744 */             if (playerName != null)
/*      */             {
/*      */ 
/*      */ 
/* 3748 */               int idNum = -1;
/*      */               try {
/* 3750 */                 idNum = Integer.parseInt(sign2Line2);
/*      */               } catch (NumberFormatException nfe) {
/*      */                 continue;
/*      */               }
/* 3754 */               if (idNum != -1)
/*      */               {
/*      */ 
/*      */ 
/* 3758 */                 if (!NavyCraft.playerHANGAR2Signs.containsKey(playerName)) {
/* 3759 */                   NavyCraft.playerHANGAR2Signs.put(playerName, new ArrayList());
/* 3760 */                   ((ArrayList)NavyCraft.playerHANGAR2Signs.get(playerName)).add(selectSign);
/* 3761 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } else {
/* 3763 */                   ((ArrayList)NavyCraft.playerHANGAR2Signs.get(playerName)).add(selectSign);
/* 3764 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static Block findHANGAR2Open() {
/* 3774 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/* 3776 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_StartX"));
/* 3777 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_EndX"));
/* 3778 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_WidthX"));
/* 3779 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_StartZ"));
/* 3780 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_EndZ"));
/* 3781 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Hangar2_WidthZ"));
/* 3782 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/* 3785 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3786 */         if ((syworld.getBlockAt(x, 65, z).getTypeId() == 63) && (syworld.getBlockAt(x + 1, 64, z).getTypeId() == 68)) {
/* 3787 */           Block selectSignBlock = syworld.getBlockAt(x, 65, z);
/* 3788 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3789 */           String signLine0 = selectSign.getLine(0);
/*      */           
/* 3791 */           if (signLine0.equalsIgnoreCase("*claim*")) return selectSignBlock;
/*      */         }
/*      */       }
/*      */     }
/* 3795 */     return null;
/*      */   }
/*      */   
/*      */   public static void loadTANK1Signs() {
/* 3799 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3802 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_StartX"));
/* 3803 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_EndX"));
/* 3804 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_WidthX"));
/* 3805 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_StartZ"));
/* 3806 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_EndZ"));
/* 3807 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_WidthZ"));
/* 3808 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3812 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3813 */         if ((syworld.getBlockAt(x, 65, z).getTypeId() == 63) && (syworld.getBlockAt(x + 1, 64, z).getTypeId() == 68)) {
/* 3814 */           Block selectSignBlock = syworld.getBlockAt(x, 65, z);
/* 3815 */           Block selectSignBlock2 = syworld.getBlockAt(x + 1, 64, z);
/* 3816 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3817 */           Sign selectSign2 = (Sign)selectSignBlock2.getState();
/* 3818 */           String signLine0 = selectSign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/* 3819 */           String sign2Line2 = selectSign2.getLine(2).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
/*      */           
/* 3821 */           if (signLine0.equalsIgnoreCase("*select*")) {
/* 3822 */             String playerName = selectSign.getLine(1);
/* 3823 */             String playerName2 = selectSign.getLine(2);
/*      */             
/* 3825 */             if ((playerName2 != null) && (!playerName2.isEmpty())) {
/* 3826 */               playerName = playerName + playerName2;
/*      */             }
/*      */             
/* 3829 */             if (playerName != null)
/*      */             {
/*      */ 
/*      */ 
/* 3833 */               int idNum = -1;
/*      */               try {
/* 3835 */                 idNum = Integer.parseInt(sign2Line2);
/*      */               } catch (NumberFormatException nfe) {
/*      */                 continue;
/*      */               }
/* 3839 */               if (idNum != -1)
/*      */               {
/*      */ 
/*      */ 
/* 3843 */                 if (!NavyCraft.playerTANK1Signs.containsKey(playerName)) {
/* 3844 */                   NavyCraft.playerTANK1Signs.put(playerName, new ArrayList());
/* 3845 */                   ((ArrayList)NavyCraft.playerTANK1Signs.get(playerName)).add(selectSign);
/* 3846 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } else {
/* 3848 */                   ((ArrayList)NavyCraft.playerTANK1Signs.get(playerName)).add(selectSign);
/* 3849 */                   NavyCraft.playerSignIndex.put(selectSign, Integer.valueOf(idNum));
/*      */                 } }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public static Block findTANK1Open() {
/* 3859 */     org.bukkit.World syworld = plugin.getServer().getWorld("shipyard");
/*      */     
/*      */ 
/* 3862 */     int startX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_StartX"));
/* 3863 */     int endX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_EndX"));
/* 3864 */     int widthX = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_WidthX"));
/* 3865 */     int startZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_StartZ"));
/* 3866 */     int endZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_EndZ"));
/* 3867 */     int widthZ = Integer.parseInt(NavyCraft.instance.ConfigSetting("Tank1_WidthZ"));
/* 3868 */     for (int x = startX; x <= endX; x += widthX)
/*      */     {
/*      */ 
/*      */ 
/* 3872 */       for (int z = startZ; z <= endZ; z += widthZ) {
/* 3873 */         if ((syworld.getBlockAt(x, 65, z).getTypeId() == 63) && (syworld.getBlockAt(x + 1, 64, z).getTypeId() == 68)) {
/* 3874 */           Block selectSignBlock = syworld.getBlockAt(x, 65, z);
/* 3875 */           Sign selectSign = (Sign)selectSignBlock.getState();
/* 3876 */           String signLine0 = selectSign.getLine(0);
/*      */           
/* 3878 */           if (signLine0.equalsIgnoreCase("*claim*")) return selectSignBlock;
/*      */         }
/*      */       }
/*      */     }
/* 3882 */     return null;
/*      */   }
/*      */   
/*      */   public static void loadShipyard() {
/* 3886 */     for (String s : NavyCraft.playerDDSigns.keySet()) {
/* 3887 */       ((ArrayList)NavyCraft.playerDDSigns.get(s)).clear();
/*      */     }
/* 3889 */     NavyCraft.playerDDSigns.clear();
/* 3890 */     for (String s : NavyCraft.playerSUB1Signs.keySet()) {
/* 3891 */       ((ArrayList)NavyCraft.playerSUB1Signs.get(s)).clear();
/*      */     }
/* 3893 */     NavyCraft.playerSUB1Signs.clear();
/* 3894 */     for (String s : NavyCraft.playerSUB2Signs.keySet()) {
/* 3895 */       ((ArrayList)NavyCraft.playerSUB2Signs.get(s)).clear();
/*      */     }
/* 3897 */     NavyCraft.playerSUB2Signs.clear();
/* 3898 */     for (String s : NavyCraft.playerCLSigns.keySet()) {
/* 3899 */       ((ArrayList)NavyCraft.playerCLSigns.get(s)).clear();
/*      */     }
/* 3901 */     NavyCraft.playerCLSigns.clear();
/* 3902 */     for (String s : NavyCraft.playerCASigns.keySet()) {
/* 3903 */       ((ArrayList)NavyCraft.playerCASigns.get(s)).clear();
/*      */     }
/* 3905 */     NavyCraft.playerCASigns.clear();
/* 3906 */     for (String s : NavyCraft.playerHANGAR1Signs.keySet()) {
/* 3907 */       ((ArrayList)NavyCraft.playerHANGAR1Signs.get(s)).clear();
/*      */     }
/* 3909 */     NavyCraft.playerHANGAR1Signs.clear();
/* 3910 */     for (String s : NavyCraft.playerHANGAR2Signs.keySet()) {
/* 3911 */       ((ArrayList)NavyCraft.playerHANGAR2Signs.get(s)).clear();
/*      */     }
/* 3913 */     NavyCraft.playerHANGAR2Signs.clear();
/* 3914 */     for (String s : NavyCraft.playerTANK1Signs.keySet()) {
/* 3915 */       ((ArrayList)NavyCraft.playerTANK1Signs.get(s)).clear();
/*      */     }
/* 3917 */     NavyCraft.playerTANK1Signs.clear();
/* 3918 */     loadDDSigns();
/* 3919 */     loadSUB1Signs();
/* 3920 */     loadSUB2Signs();
/* 3921 */     loadCLSigns();
/* 3922 */     loadCASigns();
/* 3923 */     loadHANGAR1Signs();
/* 3924 */     loadHANGAR2Signs();
/* 3925 */     loadTANK1Signs();
/*      */   }
/*      */   
/*      */   public static void loadRewards(String player)
/*      */   {
/* 3930 */     Essentials ess = (Essentials)plugin.getServer().getPluginManager().getPlugin("Essentials");
/* 3931 */     if (ess == null) {
/* 3932 */       System.out.println("Essentials Economy error");
/* 3933 */       return;
/*      */     }
/* 3935 */     NavyCraft.playerDDRewards.clear();
/* 3936 */     NavyCraft.playerSUB1Rewards.clear();
/* 3937 */     NavyCraft.playerSUB2Rewards.clear();
/* 3938 */     NavyCraft.playerCLRewards.clear();
/* 3939 */     NavyCraft.playerCARewards.clear();
/* 3940 */     NavyCraft.playerHANGAR1Rewards.clear();
/* 3941 */     NavyCraft.playerHANGAR2Rewards.clear();
/* 3942 */     NavyCraft.playerTANK1Rewards.clear();
/*      */     
/* 3944 */     String groupName = "";
/* 3945 */     Plugin groupPlugin = plugin.getServer().getPluginManager().getPlugin("GroupManager");
/* 3946 */     if (groupPlugin != null) {
/* 3947 */       if (!plugin.getServer().getPluginManager().isPluginEnabled(groupPlugin)) {
/* 3948 */         plugin.getServer().getPluginManager().enablePlugin(groupPlugin);
/*      */       }
/* 3950 */       GroupManager gm = (GroupManager)groupPlugin;
/* 3951 */       WorldsHolder wd = gm.getWorldsHolder();
/* 3952 */       groupName = wd.getWorldData("warworld1").getUser(player).getGroupName();
/*      */       
/* 3954 */       if (groupName.equalsIgnoreCase("Default")) {
/* 3955 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(1));
/* 3956 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(1));
/* 3957 */       } else if (groupName.equalsIgnoreCase("LtJG")) {
/* 3958 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(1));
/* 3959 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(1));
/* 3960 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(1));
/* 3961 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(1));
/* 3962 */       } else if (groupName.equalsIgnoreCase("Lieutenant")) {
/* 3963 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(2));
/* 3964 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(2));
/* 3965 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(1));
/* 3966 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(1));
/* 3967 */       } else if (groupName.equalsIgnoreCase("Ltcm")) {
/* 3968 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(2));
/* 3969 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(2));
/* 3970 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(2));
/* 3971 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(2));
/* 3972 */       } else if (groupName.equalsIgnoreCase("Commander")) {
/* 3973 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(2));
/* 3974 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(2));
/* 3975 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(2));
/* 3976 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(2));
/* 3977 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(1));
/* 3978 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(1));
/* 3979 */       } else if (groupName.equalsIgnoreCase("Captain")) {
/* 3980 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(3));
/* 3981 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(3));
/* 3982 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(3));
/* 3983 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(3));
/* 3984 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(1));
/* 3985 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(1));
/* 3986 */         NavyCraft.playerCLRewards.put(player, Integer.valueOf(1));
/* 3987 */       } else if ((groupName.equalsIgnoreCase("RearAdmiral1")) || (groupName.equalsIgnoreCase("Trainer"))) {
/* 3988 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(4));
/* 3989 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(4));
/* 3990 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(3));
/* 3991 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(3));
/* 3992 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(2));
/* 3993 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(2));
/* 3994 */         NavyCraft.playerCLRewards.put(player, Integer.valueOf(1));
/* 3995 */       } else if ((groupName.equalsIgnoreCase("RearAdmiral2")) || (groupName.equalsIgnoreCase("DockMaster")) || (groupName.equalsIgnoreCase("MilitaryPolice"))) {
/* 3996 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(4));
/* 3997 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(4));
/* 3998 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(4));
/* 3999 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(4));
/* 4000 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(2));
/* 4001 */         NavyCraft.playerCLRewards.put(player, Integer.valueOf(2));
/* 4002 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(2));
/* 4003 */         NavyCraft.playerCARewards.put(player, Integer.valueOf(1));
/* 4004 */       } else if ((groupName.equalsIgnoreCase("ViceAdmiral")) || (groupName.equalsIgnoreCase("BattleMod"))) {
/* 4005 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(4));
/* 4006 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(4));
/* 4007 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(4));
/* 4008 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(4));
/* 4009 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(3));
/* 4010 */         NavyCraft.playerCLRewards.put(player, Integer.valueOf(3));
/* 4011 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(3));
/* 4012 */         NavyCraft.playerCARewards.put(player, Integer.valueOf(1));
/* 4013 */       } else if (groupName.equalsIgnoreCase("Admiral")) {
/* 4014 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(5));
/* 4015 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(5));
/* 4016 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(5));
/* 4017 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(5));
/* 4018 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(4));
/* 4019 */         NavyCraft.playerCLRewards.put(player, Integer.valueOf(4));
/* 4020 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(4));
/* 4021 */         NavyCraft.playerCARewards.put(player, Integer.valueOf(1));
/* 4022 */       } else if (groupName.equalsIgnoreCase("FleetAdmiral")) {
/* 4023 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(6));
/* 4024 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(6));
/* 4025 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(6));
/* 4026 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(6));
/* 4027 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(6));
/* 4028 */         NavyCraft.playerCLRewards.put(player, Integer.valueOf(6));
/* 4029 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(4));
/* 4030 */         NavyCraft.playerCARewards.put(player, Integer.valueOf(1));
/* 4031 */       } else if (groupName.equalsIgnoreCase("Admin")) {
/* 4032 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(6));
/* 4033 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(6));
/* 4034 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(6));
/* 4035 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(6));
/* 4036 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(6));
/* 4037 */         NavyCraft.playerCLRewards.put(player, Integer.valueOf(6));
/* 4038 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(4));
/* 4039 */         NavyCraft.playerCARewards.put(player, Integer.valueOf(1));
/* 4040 */       } else if (groupName.equalsIgnoreCase("BattleMod")) {
/* 4041 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(4));
/* 4042 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(4));
/* 4043 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(4));
/* 4044 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(4));
/* 4045 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(3));
/* 4046 */         NavyCraft.playerCLRewards.put(player, Integer.valueOf(3));
/* 4047 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(3));
/* 4048 */       } else if (groupName.equalsIgnoreCase("WW-Mod")) {
/* 4049 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(4));
/* 4050 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(4));
/* 4051 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(4));
/* 4052 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(4));
/* 4053 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(2));
/* 4054 */         NavyCraft.playerCLRewards.put(player, Integer.valueOf(2));
/* 4055 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(2));
/* 4056 */       } else if (groupName.equalsIgnoreCase("Moderator")) {
/* 4057 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(4));
/* 4058 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(4));
/* 4059 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(3));
/* 4060 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(3));
/* 4061 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(2));
/* 4062 */         NavyCraft.playerCLRewards.put(player, Integer.valueOf(1));
/* 4063 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(2));
/* 4064 */       } else if (groupName.equalsIgnoreCase("SVR-Mod")) {
/* 4065 */         NavyCraft.playerDDRewards.put(player, Integer.valueOf(5));
/* 4066 */         NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(5));
/* 4067 */         NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(5));
/* 4068 */         NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(5));
/* 4069 */         NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(4));
/* 4070 */         NavyCraft.playerCLRewards.put(player, Integer.valueOf(4));
/* 4071 */         NavyCraft.playerCARewards.put(player, Integer.valueOf(1));
/* 4072 */         NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(4));
/*      */       }
/*      */       
/* 4075 */       groupName = wd.getWorldData("warworld2").getUser(player).getGroupName();
/*      */       
/* 4077 */       if (groupName.equalsIgnoreCase("Default")) {
/* 4078 */         if (NavyCraft.playerDDRewards.containsKey(player)) {
/* 4079 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerDDRewards.get(player)).intValue() + 0));
/*      */         } else {
/* 4081 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(1));
/*      */         }
/* 4083 */       } else if (groupName.equalsIgnoreCase("LtJG")) {
/* 4084 */         if (NavyCraft.playerDDRewards.containsKey(player)) {
/* 4085 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerDDRewards.get(player)).intValue() + 1));
/*      */         } else {
/* 4087 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(1));
/*      */         }
/* 4089 */         if (NavyCraft.playerSUB1Rewards.containsKey(player)) {
/* 4090 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerSUB1Rewards.get(player)).intValue() + 1));
/*      */         } else {
/* 4092 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(1));
/*      */         }
/* 4094 */         if (NavyCraft.playerHANGAR1Rewards.containsKey(player)) {
/* 4095 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerHANGAR1Rewards.get(player)).intValue() + 1));
/*      */         } else {
/* 4097 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(1));
/*      */         }
/* 4099 */         if (NavyCraft.playerTANK1Rewards.containsKey(player)) {
/* 4100 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerTANK1Rewards.get(player)).intValue() + 1));
/*      */         } else {
/* 4102 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(1));
/*      */         }
/* 4104 */       } else if (groupName.equalsIgnoreCase("Lieutenant")) {
/* 4105 */         if (NavyCraft.playerDDRewards.containsKey(player)) {
/* 4106 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerDDRewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4108 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4110 */         if (NavyCraft.playerSUB1Rewards.containsKey(player)) {
/* 4111 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerSUB1Rewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4113 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4115 */         if (NavyCraft.playerHANGAR1Rewards.containsKey(player)) {
/* 4116 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerHANGAR1Rewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4118 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4120 */         if (NavyCraft.playerTANK1Rewards.containsKey(player)) {
/* 4121 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerTANK1Rewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4123 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4125 */       } else if (groupName.equalsIgnoreCase("Ltcm")) {
/* 4126 */         if (NavyCraft.playerDDRewards.containsKey(player)) {
/* 4127 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerDDRewards.get(player)).intValue() + 3));
/*      */         } else {
/* 4129 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(3));
/*      */         }
/* 4131 */         if (NavyCraft.playerSUB1Rewards.containsKey(player)) {
/* 4132 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerSUB1Rewards.get(player)).intValue() + 3));
/*      */         } else {
/* 4134 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(3));
/*      */         }
/* 4136 */         if (NavyCraft.playerHANGAR1Rewards.containsKey(player)) {
/* 4137 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerHANGAR1Rewards.get(player)).intValue() + 3));
/*      */         } else {
/* 4139 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(3));
/*      */         }
/* 4141 */         if (NavyCraft.playerTANK1Rewards.containsKey(player)) {
/* 4142 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerTANK1Rewards.get(player)).intValue() + 3));
/*      */         } else {
/* 4144 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(3));
/*      */         }
/* 4146 */         if (NavyCraft.playerSUB2Rewards.containsKey(player)) {
/* 4147 */           NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerSUB2Rewards.get(player)).intValue() + 1));
/*      */         } else {
/* 4149 */           NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(1));
/*      */         }
/* 4151 */         if (NavyCraft.playerHANGAR2Rewards.containsKey(player)) {
/* 4152 */           NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerHANGAR2Rewards.get(player)).intValue() + 1));
/*      */         } else {
/* 4154 */           NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(1));
/*      */         }
/* 4156 */       } else if (groupName.equalsIgnoreCase("Commander")) {
/* 4157 */         if (NavyCraft.playerDDRewards.containsKey(player)) {
/* 4158 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerDDRewards.get(player)).intValue() + 4));
/*      */         } else {
/* 4160 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(4));
/*      */         }
/* 4162 */         if (NavyCraft.playerSUB1Rewards.containsKey(player)) {
/* 4163 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerSUB1Rewards.get(player)).intValue() + 4));
/*      */         } else {
/* 4165 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(4));
/*      */         }
/* 4167 */         if (NavyCraft.playerHANGAR1Rewards.containsKey(player)) {
/* 4168 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerHANGAR1Rewards.get(player)).intValue() + 4));
/*      */         } else {
/* 4170 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(4));
/*      */         }
/* 4172 */         if (NavyCraft.playerTANK1Rewards.containsKey(player)) {
/* 4173 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerTANK1Rewards.get(player)).intValue() + 4));
/*      */         } else {
/* 4175 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(4));
/*      */         }
/* 4177 */         if (NavyCraft.playerSUB2Rewards.containsKey(player)) {
/* 4178 */           NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerSUB2Rewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4180 */           NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4182 */         if (NavyCraft.playerHANGAR2Rewards.containsKey(player)) {
/* 4183 */           NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerHANGAR2Rewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4185 */           NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4187 */         if (NavyCraft.playerCLRewards.containsKey(player)) {
/* 4188 */           NavyCraft.playerCLRewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerCLRewards.get(player)).intValue() + 1));
/*      */         } else {
/* 4190 */           NavyCraft.playerCLRewards.put(player, Integer.valueOf(1));
/*      */         }
/* 4192 */       } else if (groupName.equalsIgnoreCase("Captain")) {
/* 4193 */         if (NavyCraft.playerDDRewards.containsKey(player)) {
/* 4194 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerDDRewards.get(player)).intValue() + 5));
/*      */         } else {
/* 4196 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(5));
/*      */         }
/* 4198 */         if (NavyCraft.playerSUB1Rewards.containsKey(player)) {
/* 4199 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerSUB1Rewards.get(player)).intValue() + 5));
/*      */         } else {
/* 4201 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(5));
/*      */         }
/* 4203 */         if (NavyCraft.playerHANGAR1Rewards.containsKey(player)) {
/* 4204 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerHANGAR1Rewards.get(player)).intValue() + 5));
/*      */         } else {
/* 4206 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(5));
/*      */         }
/* 4208 */         if (NavyCraft.playerTANK1Rewards.containsKey(player)) {
/* 4209 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerTANK1Rewards.get(player)).intValue() + 5));
/*      */         } else {
/* 4211 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(5));
/*      */         }
/* 4213 */         if (NavyCraft.playerSUB2Rewards.containsKey(player)) {
/* 4214 */           NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerSUB2Rewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4216 */           NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4218 */         if (NavyCraft.playerHANGAR2Rewards.containsKey(player)) {
/* 4219 */           NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerHANGAR2Rewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4221 */           NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4223 */         if (NavyCraft.playerCLRewards.containsKey(player)) {
/* 4224 */           NavyCraft.playerCLRewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerCLRewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4226 */           NavyCraft.playerCLRewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4228 */         if (NavyCraft.playerCARewards.containsKey(player)) {
/* 4229 */           NavyCraft.playerCARewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerCARewards.get(player)).intValue() + 1));
/*      */         } else {
/* 4231 */           NavyCraft.playerCARewards.put(player, Integer.valueOf(1));
/*      */         }
/*      */       } else {
/* 4234 */         if (NavyCraft.playerDDRewards.containsKey(player)) {
/* 4235 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerDDRewards.get(player)).intValue() + 5));
/*      */         } else {
/* 4237 */           NavyCraft.playerDDRewards.put(player, Integer.valueOf(5));
/*      */         }
/* 4239 */         if (NavyCraft.playerSUB1Rewards.containsKey(player)) {
/* 4240 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerSUB1Rewards.get(player)).intValue() + 5));
/*      */         } else {
/* 4242 */           NavyCraft.playerSUB1Rewards.put(player, Integer.valueOf(5));
/*      */         }
/* 4244 */         if (NavyCraft.playerHANGAR1Rewards.containsKey(player)) {
/* 4245 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerHANGAR1Rewards.get(player)).intValue() + 5));
/*      */         } else {
/* 4247 */           NavyCraft.playerHANGAR1Rewards.put(player, Integer.valueOf(5));
/*      */         }
/* 4249 */         if (NavyCraft.playerTANK1Rewards.containsKey(player)) {
/* 4250 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerTANK1Rewards.get(player)).intValue() + 5));
/*      */         } else {
/* 4252 */           NavyCraft.playerTANK1Rewards.put(player, Integer.valueOf(5));
/*      */         }
/* 4254 */         if (NavyCraft.playerSUB2Rewards.containsKey(player)) {
/* 4255 */           NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerSUB2Rewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4257 */           NavyCraft.playerSUB2Rewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4259 */         if (NavyCraft.playerHANGAR2Rewards.containsKey(player)) {
/* 4260 */           NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerHANGAR2Rewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4262 */           NavyCraft.playerHANGAR2Rewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4264 */         if (NavyCraft.playerCLRewards.containsKey(player)) {
/* 4265 */           NavyCraft.playerCLRewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerCLRewards.get(player)).intValue() + 2));
/*      */         } else {
/* 4267 */           NavyCraft.playerCLRewards.put(player, Integer.valueOf(2));
/*      */         }
/* 4269 */         if (NavyCraft.playerCARewards.containsKey(player)) {
/* 4270 */           NavyCraft.playerCARewards.put(player, Integer.valueOf(((Integer)NavyCraft.playerCARewards.get(player)).intValue() + 1));
/*      */         } else {
/* 4272 */           NavyCraft.playerCARewards.put(player, Integer.valueOf(1));
/*      */         }
/*      */       }
/*      */       
/* 4276 */       NavyCraft.loadRewardsFile();
/*      */     }
/*      */     else {
/* 4279 */       System.out.println("Group manager error");
/* 4280 */       return;
/*      */     }
/*      */   }
/*      */   
/*      */   public static Sign findSign(String player, int id) {
/* 4285 */     Sign foundSign = null;
/* 4286 */     if (NavyCraft.playerDDSigns.containsKey(player)) {
/* 4287 */       for (Sign s : (ArrayList)NavyCraft.playerDDSigns.get(player)) {
/* 4288 */         if (id == ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4289 */           foundSign = s;
/*      */         }
/*      */       }
/*      */     }
/* 4293 */     if ((foundSign == null) && (NavyCraft.playerSUB1Signs.containsKey(player))) {
/* 4294 */       for (Sign s : (ArrayList)NavyCraft.playerSUB1Signs.get(player)) {
/* 4295 */         if (id == ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4296 */           foundSign = s;
/*      */         }
/*      */       }
/*      */     }
/* 4300 */     if ((foundSign == null) && (NavyCraft.playerSUB2Signs.containsKey(player))) {
/* 4301 */       for (Sign s : (ArrayList)NavyCraft.playerSUB2Signs.get(player)) {
/* 4302 */         if (id == ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4303 */           foundSign = s;
/*      */         }
/*      */       }
/*      */     }
/* 4307 */     if ((foundSign == null) && (NavyCraft.playerCLSigns.containsKey(player))) {
/* 4308 */       for (Sign s : (ArrayList)NavyCraft.playerCLSigns.get(player)) {
/* 4309 */         if (id == ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4310 */           foundSign = s;
/*      */         }
/*      */       }
/*      */     }
/* 4314 */     if ((foundSign == null) && (NavyCraft.playerCASigns.containsKey(player))) {
/* 4315 */       for (Sign s : (ArrayList)NavyCraft.playerCASigns.get(player)) {
/* 4316 */         if (id == ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4317 */           foundSign = s;
/*      */         }
/*      */       }
/*      */     }
/* 4321 */     if ((foundSign == null) && (NavyCraft.playerHANGAR1Signs.containsKey(player))) {
/* 4322 */       for (Sign s : (ArrayList)NavyCraft.playerHANGAR1Signs.get(player)) {
/* 4323 */         if (id == ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4324 */           foundSign = s;
/*      */         }
/*      */       }
/*      */     }
/* 4328 */     if ((foundSign == null) && (NavyCraft.playerHANGAR2Signs.containsKey(player))) {
/* 4329 */       for (Sign s : (ArrayList)NavyCraft.playerHANGAR2Signs.get(player)) {
/* 4330 */         if (id == ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4331 */           foundSign = s;
/*      */         }
/*      */       }
/*      */     }
/* 4335 */     if ((foundSign == null) && (NavyCraft.playerTANK1Signs.containsKey(player))) {
/* 4336 */       for (Sign s : (ArrayList)NavyCraft.playerTANK1Signs.get(player)) {
/* 4337 */         if (id == ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4338 */           foundSign = s;
/*      */         }
/*      */       }
/*      */     }
/* 4342 */     return foundSign;
/*      */   }
/*      */   
/*      */   public static int maxId(org.bukkit.entity.Player player) {
/* 4346 */     int foundHighest = -1;
/* 4347 */     if (NavyCraft.playerDDSigns.containsKey(player.getName())) {
/* 4348 */       for (Sign s : (ArrayList)NavyCraft.playerDDSigns.get(player.getName())) {
/* 4349 */         if (foundHighest < ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4350 */           foundHighest = ((Integer)NavyCraft.playerSignIndex.get(s)).intValue();
/*      */         }
/*      */       }
/*      */     }
/* 4354 */     if (NavyCraft.playerSUB1Signs.containsKey(player.getName())) {
/* 4355 */       for (Sign s : (ArrayList)NavyCraft.playerSUB1Signs.get(player.getName())) {
/* 4356 */         if (foundHighest < ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4357 */           foundHighest = ((Integer)NavyCraft.playerSignIndex.get(s)).intValue();
/*      */         }
/*      */       }
/*      */     }
/* 4361 */     if (NavyCraft.playerSUB2Signs.containsKey(player.getName())) {
/* 4362 */       for (Sign s : (ArrayList)NavyCraft.playerSUB2Signs.get(player.getName())) {
/* 4363 */         if (foundHighest < ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4364 */           foundHighest = ((Integer)NavyCraft.playerSignIndex.get(s)).intValue();
/*      */         }
/*      */       }
/*      */     }
/* 4368 */     if (NavyCraft.playerCLSigns.containsKey(player.getName())) {
/* 4369 */       for (Sign s : (ArrayList)NavyCraft.playerCLSigns.get(player.getName())) {
/* 4370 */         if (foundHighest < ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4371 */           foundHighest = ((Integer)NavyCraft.playerSignIndex.get(s)).intValue();
/*      */         }
/*      */       }
/*      */     }
/* 4375 */     if (NavyCraft.playerCASigns.containsKey(player.getName())) {
/* 4376 */       for (Sign s : (ArrayList)NavyCraft.playerCASigns.get(player.getName())) {
/* 4377 */         if (foundHighest < ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4378 */           foundHighest = ((Integer)NavyCraft.playerSignIndex.get(s)).intValue();
/*      */         }
/*      */       }
/*      */     }
/* 4382 */     if (NavyCraft.playerHANGAR1Signs.containsKey(player.getName())) {
/* 4383 */       for (Sign s : (ArrayList)NavyCraft.playerHANGAR1Signs.get(player.getName())) {
/* 4384 */         if (foundHighest < ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4385 */           foundHighest = ((Integer)NavyCraft.playerSignIndex.get(s)).intValue();
/*      */         }
/*      */       }
/*      */     }
/* 4389 */     if (NavyCraft.playerHANGAR2Signs.containsKey(player.getName())) {
/* 4390 */       for (Sign s : (ArrayList)NavyCraft.playerHANGAR2Signs.get(player.getName())) {
/* 4391 */         if (foundHighest < ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4392 */           foundHighest = ((Integer)NavyCraft.playerSignIndex.get(s)).intValue();
/*      */         }
/*      */       }
/*      */     }
/* 4396 */     if (NavyCraft.playerTANK1Signs.containsKey(player.getName())) {
/* 4397 */       for (Sign s : (ArrayList)NavyCraft.playerTANK1Signs.get(player.getName())) {
/* 4398 */         if (foundHighest < ((Integer)NavyCraft.playerSignIndex.get(s)).intValue()) {
/* 4399 */           foundHighest = ((Integer)NavyCraft.playerSignIndex.get(s)).intValue();
/*      */         }
/*      */       }
/*      */     }
/* 4403 */     return foundHighest;
/*      */   }
/*      */   
/*      */   @EventHandler(priority=EventPriority.HIGH)
/*      */   public void onBlockDispense(BlockDispenseEvent event) {
/* 4408 */     if ((!event.isCancelled()) && 
/* 4409 */       (event.getBlock().getWorld().getName().equalsIgnoreCase("warworld1")) && (event.getItem().getType() == Material.EMERALD)) {
/* 4410 */       event.setCancelled(true);
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\MoveCraft_BlockListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */