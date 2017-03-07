/*     */ package com.maximuspayne.navycraft;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.Stack;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CraftBuilder
/*     */ {
/*     */   private static Craft craft;
/*  21 */   public static boolean waitStopMakingThatCraft = false;
/*  22 */   private static Short nullBlock = Short.valueOf((short)-1);
/*     */   
/*     */ 
/*     */   private static Stack<BlockLoc> blocksStack;
/*     */   
/*     */   private static HashMap<Integer, HashMap<Integer, HashMap<Integer, Short>>> dmatrix;
/*     */   
/*     */ 
/*     */   private static boolean isFree(int x, int y, int z)
/*     */   {
/*  32 */     if ((x < 0) || (x >= craft.sizeX) || 
/*  33 */       (y < 0) || (y >= craft.sizeY) || 
/*  34 */       (z < 0) || (z >= craft.sizeZ)) {
/*  35 */       return true;
/*     */     }
/*  37 */     int blockId = craft.matrix[x][y][z];
/*     */     
/*  39 */     if ((blockId == 0) || (blockId == -1)) {
/*  40 */       return true;
/*     */     }
/*  42 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Short get(int x, int y, int z)
/*     */   {
/*  50 */     HashMap<Integer, HashMap<Integer, Short>> xRow = (HashMap)dmatrix.get(new Integer(x));
/*  51 */     if (xRow != null)
/*     */     {
/*  53 */       HashMap<Integer, Short> yRow = (HashMap)xRow.get(new Integer(y));
/*     */       
/*  55 */       if (yRow != null) {
/*  56 */         return (Short)yRow.get(new Integer(z));
/*     */       }
/*     */     }
/*     */     
/*  60 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void set(short blockType, int x, int y, int z)
/*     */   {
/*  68 */     HashMap<Integer, HashMap<Integer, Short>> xRow = (HashMap)dmatrix.get(new Integer(x));
/*  69 */     if (xRow == null) {
/*  70 */       xRow = new HashMap();
/*  71 */       dmatrix.put(new Integer(x), xRow);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  77 */     HashMap<Integer, Short> yRow = (HashMap)xRow.get(new Integer(y));
/*     */     
/*  79 */     if (yRow == null) {
/*  80 */       yRow = new HashMap();
/*  81 */       xRow.put(new Integer(y), yRow);
/*     */     }
/*     */     
/*  84 */     Short type = (Short)yRow.get(new Integer(z));
/*     */     
/*  86 */     if (type == null) {
/*  87 */       yRow.put(new Integer(z), new Short(blockType));
/*     */     }
/*     */   }
/*     */   
/*     */   private static void detectWater(int x, int y, int z) {
/*  92 */     if (craft.isCraftBlock(x, y, z)) { return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  98 */     Block theBlock = craft.world.getBlockAt(craft.minX + x, craft.minY + y, craft.minZ + z);
/*  99 */     int blockId = theBlock.getTypeId();
/*     */     
/*     */ 
/* 102 */     if (((blockId == 8) || (blockId == 9)) && (theBlock.getData() == 0)) {
/* 103 */       if (y > craft.waterLevel) craft.waterLevel = 63;
/* 104 */       craft.waterType = 8;
/* 105 */       return;
/*     */     }
/*     */     
/*     */ 
/* 109 */     if (((blockId == 10) || (blockId == 11)) && (theBlock.getData() == 0)) {
/* 110 */       if (y > craft.waterLevel) craft.waterLevel = y;
/* 111 */       craft.waterType = 10;
/* 112 */       return;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static void removeWater()
/*     */   {
/*     */     boolean updated;
/*     */     
/*     */     do
/*     */     {
/* 123 */       updated = false;
/*     */       
/* 125 */       for (int x = 0; x < craft.sizeX; x++) {
/* 126 */         for (int z = 0; z < craft.sizeZ; z++) {
/* 127 */           for (int y = 0; y < craft.sizeY; y++)
/*     */           {
/* 129 */             if ((craft.matrix[x][y][z] >= 8) && (craft.matrix[x][y][z] <= 11) && (y <= craft.waterLevel))
/*     */             {
/* 131 */               if ((isFree(x + 1, y, z)) || 
/* 132 */                 (isFree(x - 1, y, z)) || 
/* 133 */                 (isFree(x, y, z + 1)) || 
/* 134 */                 (isFree(x, y, z - 1)) || 
/* 135 */                 (isFree(x, y - 1, z)))
/*     */               {
/* 137 */                 craft.matrix[x][y][z] = -1;
/* 138 */                 updated = true;
/*     */               }
/*     */               
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */       }
/* 146 */     } while (updated);
/*     */   }
/*     */   
/*     */   private static void removeAir()
/*     */   {
/* 151 */     BlockLoc block = (BlockLoc)blocksStack.pop();
/*     */     
/*     */ 
/* 154 */     if ((block.x < 0) || (block.x > craft.maxX - craft.minX) || 
/* 155 */       (block.y < 0) || (block.y > craft.maxY - craft.minY) || 
/* 156 */       (block.z < 0) || (block.z > craft.maxZ - craft.minZ)) {
/* 157 */       return;
/*     */     }
/*     */     
/*     */ 
/* 161 */     if (craft.matrix[block.x][block.y][block.z] != 0) {
/* 162 */       return;
/*     */     }
/* 164 */     craft.matrix[block.x][block.y][block.z] = -1;
/*     */     
/*     */ 
/* 167 */     blocksStack.push(new BlockLoc(block.x + 1, block.y, block.z));
/* 168 */     blocksStack.push(new BlockLoc(block.x - 1, block.y, block.z));
/* 169 */     blocksStack.push(new BlockLoc(block.x, block.y + 1, block.z));
/* 170 */     blocksStack.push(new BlockLoc(block.x, block.y - 1, block.z));
/* 171 */     blocksStack.push(new BlockLoc(block.x, block.y, block.z + 1));
/* 172 */     blocksStack.push(new BlockLoc(block.x, block.y, block.z - 1));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static boolean createAirBubble()
/*     */   {
/* 179 */     NavyCraft.instance.DebugMessage("Adding an air bubble.", 4);
/*     */     
/* 181 */     BlockLoc block = (BlockLoc)blocksStack.pop();
/*     */     
/*     */ 
/* 184 */     if ((block.x < 0) || (block.x > craft.maxX - craft.minX) || 
/* 185 */       (block.y < 0) || (block.y > craft.maxY - craft.minY) || 
/* 186 */       (block.z < 0) || (block.z > craft.maxZ - craft.minZ))
/*     */     {
/* 188 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 192 */     if (craft.matrix[block.x][block.y][block.z] == 0) {
/* 193 */       return true;
/*     */     }
/* 195 */     if (craft.matrix[block.x][block.y][block.z] == -1)
/*     */     {
/*     */ 
/* 198 */       craft.matrix[block.x][block.y][block.z] = 0;
/*     */     }
/*     */     else {
/* 201 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 205 */     blocksStack.push(new BlockLoc(block.x + 1, block.y, block.z));
/* 206 */     blocksStack.push(new BlockLoc(block.x - 1, block.y, block.z));
/* 207 */     blocksStack.push(new BlockLoc(block.x, block.y + 1, block.z));
/* 208 */     blocksStack.push(new BlockLoc(block.x, block.y - 1, block.z));
/* 209 */     blocksStack.push(new BlockLoc(block.x, block.y, block.z + 1));
/* 210 */     blocksStack.push(new BlockLoc(block.x, block.y, block.z - 1));
/*     */     
/* 212 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static boolean secondPassDetection()
/*     */   {
/* 221 */     for (int x = 0; x < craft.sizeX; x++) {
/* 222 */       for (int z = 0; z < craft.sizeZ; z++)
/*     */       {
/* 224 */         boolean floor = false;
/*     */         
/* 226 */         for (int y = 0; y < craft.sizeY; y++)
/*     */         {
/*     */ 
/* 229 */           if ((!floor) && (craft.matrix[x][y][z] != -1)) {
/* 230 */             floor = true;
/*     */           } else {
/* 232 */             if ((floor) && (craft.matrix[x][y][z] == -1))
/*     */             {
/* 234 */               Block block = craft.world.getBlockAt(craft.minX + x, craft.minY + y, craft.minZ + z);
/* 235 */               int blockId = block.getTypeId();
/*     */               
/* 237 */               craft.matrix[x][y][z] = ((short)blockId);
/*     */               
/* 239 */               if (BlocksInfo.isDataBlock(blockId)) {
/* 240 */                 addDataBlock(blockId, craft.minX + x, craft.minY + y, craft.minZ + z);
/*     */               }
/*     */               
/* 243 */               if (BlocksInfo.isComplexBlock(blockId)) {
/* 244 */                 addComplexBlock(blockId, craft.minX + x, craft.minY + y, craft.minZ + z);
/* 245 */                 craft.findFuel(block);
/*     */               }
/*     */               
/* 248 */               if ((craft.type.engineBlockId != 0) && (blockId == craft.type.engineBlockId)) {
/* 249 */                 addEngineBlock(blockId, craft.minX + x, craft.minY + y, craft.minZ + z);
/*     */               }
/*     */               
/*     */ 
/* 253 */               if (blockId == 79) {
/* 254 */                 craft.matrix[x][y][z] = -1;
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 263 */             if ((craft.waterType != 0) && (craft.matrix[x][y][z] != -1)) {
/* 264 */               detectWater(x + 1, y, z);
/* 265 */               detectWater(x - 1, y, z);
/* 266 */               detectWater(x, y, z + 1);
/* 267 */               detectWater(x, y, z - 1);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 274 */     if (craft.waterLevel != -1) {
/* 275 */       removeWater();
/*     */     }
/*     */     
/*     */ 
/* 279 */     if ((craft.type.canDive) || (craft.type.canNavigate))
/*     */     {
/*     */ 
/* 282 */       for (int x = 0; x < craft.sizeX; x++) {
/* 283 */         for (int z = 0; z < craft.sizeZ; z++) {
/* 284 */           for (int y = 0; y < craft.sizeY; y++) {
/* 285 */             if (craft.matrix[x][y][z] == 0) {
/* 286 */               craft.matrix[x][y][z] = -1;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 291 */       blocksStack = new Stack();
/*     */       
/*     */ 
/*     */ 
/* 295 */       blocksStack.push(new BlockLoc((int)Math.floor(NavyCraft.instance.getServer().getPlayer(craft.captainName).getLocation().getX()) - craft.minX, 
/* 296 */         (int)Math.floor(NavyCraft.instance.getServer().getPlayer(craft.captainName).getLocation().getY() + 1.0D - craft.minY), 
/* 297 */         (int)Math.floor(NavyCraft.instance.getServer().getPlayer(craft.captainName).getLocation().getZ()) - craft.minZ));
/*     */       
/*     */       do
/*     */       {
/* 301 */         if ((!createAirBubble()) && (NavyCraft.instance.ConfigSetting("allowHoles").equalsIgnoreCase("false")) && (craft.type.canDive)) {
/* 302 */           NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.YELLOW + "This " + craft.type.name + " has holes, it needs to be waterproof");
/* 303 */           return false;
/*     */         }
/*     */         
/* 306 */       } while (!blocksStack.isEmpty());
/*     */       
/* 308 */       blocksStack = null;
/*     */       
/*     */ 
/* 311 */       for (int x = 0; x < craft.sizeX; x++) {
/* 312 */         for (int z = 0; z < craft.sizeZ; z++) {
/* 313 */           for (int y = 0; y < craft.sizeY; y++) {
/* 314 */             if (craft.matrix[x][y][z] == -1) {
/* 315 */               craft.matrix[x][y][z] = 0;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 321 */       for (int x = 0; x < craft.sizeX; x++) {
/* 322 */         for (int z = 0; z < craft.sizeZ; z++) {
/* 323 */           for (int y = 0; y < craft.sizeY; y++) {
/* 324 */             if ((craft.matrix[x][y][z] == 0) && (
/* 325 */               (x == 0) || 
/* 326 */               (y == 0) || 
/* 327 */               (z == 0) || 
/* 328 */               (x == craft.sizeX - 1) || 
/* 329 */               (y == craft.sizeY - 1) || 
/* 330 */               (z == craft.sizeZ - 1)))
/*     */             {
/* 332 */               blocksStack = new Stack();
/* 333 */               blocksStack.push(new BlockLoc(x, y, z));
/*     */               do
/*     */               {
/* 336 */                 removeAir();
/*     */               }
/* 338 */               while (!blocksStack.isEmpty());
/*     */               
/* 340 */               blocksStack = null;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 346 */       blocksStack = null;
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/* 351 */     else if (craft.waterLevel != -1)
/*     */     {
/*     */ 
/* 354 */       for (int x = 0; x < craft.sizeX; x++) {
/* 355 */         for (int z = 0; z < craft.sizeZ; z++) {
/* 356 */           for (int y = craft.waterLevel + 1; y < craft.sizeY; y++) {
/* 357 */             if (craft.matrix[x][y][z] == 0) {
/* 358 */               craft.matrix[x][y][z] = -1;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     } else {
/* 364 */       for (int x = 0; x < craft.sizeX; x++) {
/* 365 */         for (int z = 0; z < craft.sizeZ; z++) {
/* 366 */           for (int y = 0; y < craft.sizeY; y++) {
/* 367 */             if (craft.matrix[x][y][z] == 0) {
/* 368 */               craft.matrix[x][y][z] = -1;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 375 */     return true;
/*     */   }
/*     */   
/*     */   private static void addDataBlock(int id, int x, int y, int z)
/*     */   {
/* 380 */     craft.dataBlocks.add(new DataBlock(id, x - craft.minX, y - craft.minY, z - craft.minZ, 
/* 381 */       craft.world.getBlockAt(x, y, z).getData()));
/*     */   }
/*     */   
/*     */   private static void addComplexBlock(int id, int x, int y, int z) {
/* 385 */     craft.complexBlocks.add(new DataBlock(id, x - craft.minX, y - craft.minY, z - craft.minZ, 
/* 386 */       craft.world.getBlockAt(x, y, z).getData()));
/*     */   }
/*     */   
/*     */   private static void addEngineBlock(int id, int x, int y, int z)
/*     */   {
/* 391 */     craft.engineBlocks.add(new DataBlock(id, x - craft.minX, y - craft.minY, z - craft.minZ, 
/* 392 */       craft.world.getBlockAt(x, y, z).getData()));
/*     */   }
/*     */   
/*     */ 
/*     */   private static void createMatrix()
/*     */   {
/* 398 */     if ((craft.sizeX <= 1) || (craft.sizeY <= 1) || (craft.sizeZ <= 1))
/*     */     {
/* 400 */       return;
/*     */     }
/* 402 */     craft.matrix = new short[craft.sizeX][craft.sizeY][craft.sizeZ];
/* 403 */     craft.displacedBlocks = new short[craft.matrix[0].length + 1][craft.matrix[1].length + 1][craft.matrix[2].length + 1];
/* 404 */     craft.dataBlocks = new ArrayList();
/* 405 */     craft.complexBlocks = new ArrayList();
/*     */     
/* 407 */     for (int x = 0; x < craft.sizeX; x++) {
/* 408 */       for (z = 0; z < craft.sizeZ; z++) {
/* 409 */         for (int y = 0; y < craft.sizeY; y++) {
/* 410 */           craft.matrix[x][y][z] = -1;
/*     */         }
/*     */       }
/*     */     }
/*     */     Iterator localIterator1;
/* 415 */     for (int z = dmatrix.keySet().iterator(); z.hasNext(); 
/*     */         
/* 417 */         localIterator1.hasNext())
/*     */     {
/* 415 */       Integer x = (Integer)z.next();
/* 416 */       HashMap<Integer, HashMap<Integer, Short>> xRow = (HashMap)dmatrix.get(x);
/* 417 */       localIterator1 = xRow.keySet().iterator(); continue;Integer y = (Integer)localIterator1.next();
/* 418 */       HashMap<Integer, Short> yRow = (HashMap)xRow.get(y);
/* 419 */       for (Integer z : yRow.keySet())
/*     */       {
/* 421 */         short blockId = ((Short)yRow.get(z)).shortValue();
/*     */         
/* 423 */         if (blockId != -1)
/*     */         {
/*     */ 
/* 426 */           craft.matrix[(x.intValue() - craft.minX)][(y.intValue() - craft.minY)][(z.intValue() - craft.minZ)] = blockId;
/*     */           
/* 428 */           if (BlocksInfo.isDataBlock(blockId)) {
/* 429 */             addDataBlock(blockId, x.intValue(), y.intValue(), z.intValue());
/*     */           }
/* 431 */           if (BlocksInfo.isComplexBlock(blockId)) {
/* 432 */             addComplexBlock(blockId, x.intValue(), y.intValue(), z.intValue());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 438 */     dmatrix = null;
/*     */   }
/*     */   
/*     */   private static void detectBlock(int x, int y, int z, int dir)
/*     */   {
/* 443 */     Short blockType = get(x, y, z);
/*     */     
/*     */ 
/* 446 */     if (blockType != null) { return;
/*     */     }
/* 448 */     blockType = new Short((short)craft.world.getBlockAt(x, y, z).getTypeId());
/* 449 */     int BlockData = craft.world.getBlockAt(x, y, z).getData();
/*     */     
/*     */ 
/* 452 */     if ((craft.type.forbiddenBlocks != null) && (craft.type.forbiddenBlocks.length > 0) && (!waitStopMakingThatCraft)) {
/* 453 */       for (int i = 0; i < craft.type.forbiddenBlocks.length; i++) {
/* 454 */         if (blockType.shortValue() == craft.type.forbiddenBlocks[i]) {
/* 455 */           NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage("Forbidden block of type " + Material.getMaterial(blockType.shortValue()) + " found. " + 
/* 456 */             "Remove all blocks of that type in order to pilot this craft.");
/* 457 */           waitStopMakingThatCraft = true;
/* 458 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 464 */     if (((blockType.shortValue() == 8) || (blockType.shortValue() == 9)) && (BlockData == 0)) {
/* 465 */       if (y > craft.waterLevel) craft.waterLevel = y;
/* 466 */       craft.waterType = 8;
/* 467 */       set(nullBlock.shortValue(), x, y, z);
/* 468 */       return;
/*     */     }
/*     */     
/*     */ 
/* 472 */     if (((blockType.shortValue() == 10) || (blockType.shortValue() == 11)) && (BlockData == 0)) {
/* 473 */       if (y > craft.waterLevel) craft.waterLevel = y;
/* 474 */       craft.waterType = 10;
/* 475 */       set(nullBlock.shortValue(), x, y, z);
/* 476 */       return;
/*     */     }
/*     */     
/*     */ 
/* 480 */     if (blockType.shortValue() == 0) {
/* 481 */       set(nullBlock.shortValue(), x, y, z);
/* 482 */       return;
/*     */     }
/*     */     
/*     */ 
/* 486 */     if (blockType.shortValue() == 55) {
/* 487 */       if (dir != 1) {
/* 488 */         set(nullBlock.shortValue(), x, y, z);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */     }
/* 495 */     else if (craft.type.structureBlocks == null)
/*     */     {
/*     */ 
/* 498 */       if ((blockType.shortValue() != 4) && 
/* 499 */         (blockType.shortValue() != 5) && 
/* 500 */         (blockType.shortValue() != 17) && 
/* 501 */         (blockType.shortValue() != 19) && 
/* 502 */         (blockType.shortValue() != 20) && 
/* 503 */         (blockType.shortValue() != 35) && 
/* 504 */         ((blockType.shortValue() < 41) || (blockType.shortValue() > 50)) && 
/* 505 */         (blockType.shortValue() != 53) && 
/* 506 */         (blockType.shortValue() != 55) && 
/* 507 */         (blockType.shortValue() != 57) && 
/* 508 */         (blockType.shortValue() != 65) && 
/* 509 */         (blockType.shortValue() != 67) && 
/* 510 */         (blockType.shortValue() != 68) && 
/* 511 */         (blockType.shortValue() != 69) && 
/* 512 */         (blockType.shortValue() != 75) && 
/* 513 */         (blockType.shortValue() != 76) && 
/* 514 */         (blockType.shortValue() != 77) && 
/* 515 */         (blockType.shortValue() != 85) && 
/* 516 */         (blockType.shortValue() != 87) && 
/* 517 */         (blockType.shortValue() != 88) && 
/* 518 */         (blockType.shortValue() != 89))
/*     */       {
/* 520 */         set(nullBlock.shortValue(), x, y, z);
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 527 */       boolean found = false;
/* 528 */       short[] arrayOfShort; int j = (arrayOfShort = craft.type.structureBlocks).length; for (int i = 0; i < j; i++) { short blockId = arrayOfShort[i];
/* 529 */         if (blockType.shortValue() == blockId) {
/* 530 */           found = true;
/* 531 */           break;
/*     */         }
/*     */       }
/* 534 */       if (!found) {
/* 535 */         set(nullBlock.shortValue(), x, y, z);
/* 536 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 541 */     set(blockType.shortValue(), x, y, z);
/* 542 */     craft.blockCount += 1;
/*     */     
/* 544 */     craft.weight += Craft.blockWeight(blockType.shortValue());
/*     */     
/* 546 */     if (craft.blockCount > craft.type.maxBlocks) {
/* 547 */       return;
/*     */     }
/*     */     
/* 550 */     if (blockType.shortValue() == craft.type.flyBlockType) {
/* 551 */       craft.flyBlockCount += 1;
/*     */     }
/*     */     
/* 554 */     if (blockType.shortValue() == craft.type.digBlockId) {
/* 555 */       craft.digBlockCount += 1;
/*     */     }
/*     */     
/* 558 */     if (x < craft.minX) craft.minX = x;
/* 559 */     if (x > craft.maxX) craft.maxX = x;
/* 560 */     if (y < craft.minY) craft.minY = y;
/* 561 */     if (y > craft.maxY) craft.maxY = y;
/* 562 */     if (z < craft.minZ) craft.minZ = z;
/* 563 */     if (z > craft.maxZ) { craft.maxZ = z;
/*     */     }
/*     */     
/* 566 */     if (BlocksInfo.needsSupport(blockType.shortValue())) { return;
/*     */     }
/* 568 */     blocksStack.push(new BlockLoc(x, y, z));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void detectBlock(BlockLoc block)
/*     */   {
/* 577 */     detectBlock(block.x + 1, block.y, block.z, 1);
/* 578 */     detectBlock(block.x - 1, block.y, block.z, 2);
/* 579 */     detectBlock(block.x, block.y + 1, block.z, 1);
/* 580 */     detectBlock(block.x, block.y - 1, block.z, 6);
/* 581 */     detectBlock(block.x, block.y, block.z + 1, 3);
/* 582 */     detectBlock(block.x, block.y, block.z - 1, 4);
/*     */     
/*     */ 
/* 585 */     detectBlock(block.x + 1, block.y - 1, block.z, -1);
/* 586 */     detectBlock(block.x - 1, block.y - 1, block.z, -1);
/* 587 */     detectBlock(block.x, block.y - 1, block.z + 1, -1);
/* 588 */     detectBlock(block.x, block.y - 1, block.z - 1, -1);
/* 589 */     detectBlock(block.x + 1, block.y + 1, block.z, -1);
/* 590 */     detectBlock(block.x - 1, block.y + 1, block.z, -1);
/* 591 */     detectBlock(block.x, block.y + 1, block.z + 1, -1);
/* 592 */     detectBlock(block.x, block.y + 1, block.z - 1, -1);
/*     */   }
/*     */   
/*     */   public static boolean detect(Craft craft, int X, int Y, int Z, boolean autoShip)
/*     */   {
/* 597 */     waitStopMakingThatCraft = false;
/* 598 */     craft = craft;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 603 */     dmatrix = new HashMap();
/*     */     
/* 605 */     craft.blockCount = 0;
/*     */     
/* 607 */     craft.minX = (craft.maxX = X);
/* 608 */     craft.minY = (craft.maxY = Y);
/* 609 */     craft.minZ = (craft.maxZ = Z);
/*     */     
/* 611 */     blocksStack = new Stack();
/* 612 */     blocksStack.push(new BlockLoc(X, Y, Z));
/*     */     
/*     */     do
/*     */     {
/* 616 */       detectBlock((BlockLoc)blocksStack.pop());
/*     */     }
/* 618 */     while (!blocksStack.isEmpty());
/*     */     
/* 620 */     blocksStack = null;
/*     */     
/* 622 */     if (waitStopMakingThatCraft) {
/* 623 */       return false;
/*     */     }
/*     */     
/* 626 */     if (craft.blockCount > craft.type.maxBlocks) {
/* 627 */       NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "Unable to detect the " + craft.name + ", be sure it is not connected");
/* 628 */       NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + " to the ground, or maybe it is too big for this type of craft");
/* 629 */       NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "The maximum size is " + craft.type.maxBlocks + " blocks");
/* 630 */       return false;
/*     */     }
/*     */     
/* 633 */     if (craft.blockCount < craft.type.minBlocks)
/*     */     {
/* 635 */       if (craft.blockCount == 0) {
/* 636 */         NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "There is no " + craft.name + " here");
/* 637 */         NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "Be sure you are standing on a block");
/*     */       }
/*     */       else {
/* 640 */         NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "This " + craft.name + " is too small !");
/* 641 */         NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "You need to add " + (craft.type.minBlocks - craft.blockCount) + " blocks");
/*     */       }
/*     */       
/* 644 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 650 */     for (Craft c : Craft.craftList) {
/* 651 */       if ((c != craft) && (c.captainName != null) && (!c.launcherOn))
/*     */       {
/* 653 */         if (((c.minX >= craft.minX) || (c.maxX >= craft.minX)) && ((craft.minX >= c.minX) || (craft.maxX >= c.minX)) && 
/* 654 */           ((c.minY >= craft.minY) || (c.maxY >= craft.minY)) && ((craft.minY >= c.minY) || (craft.maxY >= c.minY)) && 
/* 655 */           ((c.minZ >= craft.minZ) || (c.maxZ >= craft.minZ)) && ((craft.minZ >= c.minZ) || (craft.maxZ >= c.minZ))) {
/* 656 */           NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + c.captainName + " is already controling this " + craft.name);
/* 657 */           return false;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 663 */     craft.sizeX = (craft.maxX - craft.minX + 1);
/* 664 */     craft.sizeY = (craft.maxY - craft.minY + 1);
/* 665 */     craft.sizeZ = (craft.maxZ - craft.minZ + 1);
/*     */     
/*     */ 
/* 668 */     if (craft.waterLevel != -1) {
/* 669 */       craft.waterLevel -= craft.minY;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 678 */     createMatrix();
/*     */     
/* 680 */     if (craft.matrix == null) {
/* 681 */       return false;
/*     */     }
/* 683 */     if (!secondPassDetection()) {
/* 684 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 688 */     if ((craft.type.canNavigate) && (!craft.type.canFly) && (craft.waterType == 0) && (!craft.type.canDig) && (!craft.type.isTerrestrial)) {
/* 689 */       NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "This " + craft.name + " is not on water...");
/* 690 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 694 */     if ((craft.type.canDive) && (!craft.type.canFly) && (craft.waterType == 0) && (!craft.type.canDig)) {
/* 695 */       NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "This " + craft.name + " is not into water...");
/* 696 */       return false;
/*     */     }
/*     */     
/* 699 */     if ((craft.type.canFly) && (!craft.type.canNavigate) && (!craft.type.canDive) && (craft.waterLevel > -1)) {
/* 700 */       NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "This " + craft.name + " is into water...");
/* 701 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 706 */     if ((craft.type.flyBlockType != 0) && (craft.type.flyBlockPercent > 0.0D))
/*     */     {
/*     */ 
/*     */ 
/* 710 */       int flyBlocksNeeded = (int)Math.floor((craft.blockCount - craft.flyBlockCount) * ((float)craft.type.flyBlockPercent * 0.01D) / (1.0D - (float)craft.type.flyBlockPercent * 0.01D));
/*     */       
/* 712 */       if (flyBlocksNeeded < 1) {
/* 713 */         flyBlocksNeeded = 1;
/*     */       }
/* 715 */       if (craft.flyBlockCount < flyBlocksNeeded) {
/* 716 */         NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "Not enough " + BlocksInfo.getName(craft.type.flyBlockType) + " to make this " + craft.name + " move");
/* 717 */         NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "You need to add " + (flyBlocksNeeded - craft.flyBlockCount) + " more");
/* 718 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 723 */     if ((craft.type.canDig) && (craft.type.digBlockId != 0))
/*     */     {
/*     */ 
/* 726 */       int digBlocksNeeded = (int)Math.floor((craft.blockCount - craft.digBlockCount) * ((float)craft.type.digBlockPercent * 0.01D) / (1.0D - (float)craft.type.digBlockPercent * 0.01D));
/* 727 */       if (digBlocksNeeded < 1) {
/* 728 */         digBlocksNeeded = 1;
/*     */       }
/* 730 */       if (craft.digBlockCount < digBlocksNeeded) {
/* 731 */         NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "Not enough " + BlocksInfo.getName(craft.type.digBlockId) + " to make this " + craft.name + " move");
/* 732 */         NavyCraft.instance.getServer().getPlayer(craft.captainName).sendMessage(ChatColor.RED + "You need to add " + (digBlocksNeeded - craft.digBlockCount) + " more");
/* 733 */         return false;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 742 */     if (!autoShip)
/*     */     {
/* 744 */       Player captain = NavyCraft.instance.getServer().getPlayer(craft.captainName);
/* 745 */       craft.buildCrew(captain, false);
/* 746 */       if (craft.customName != null) {
/* 747 */         captain.sendMessage(ChatColor.YELLOW + "You Launch the " + ChatColor.WHITE + craft.customName.toUpperCase() + ChatColor.YELLOW + " class!");
/*     */       } else {
/* 749 */         captain.sendMessage(ChatColor.YELLOW + "You Launch the " + ChatColor.WHITE + craft.name.toUpperCase() + ChatColor.YELLOW + " class!");
/*     */       }
/*     */     }
/*     */     
/* 753 */     if (craft.type.requiresRails) {
/* 754 */       int xMid = craft.matrix.length / 2;
/* 755 */       int zMid = craft.matrix[0][0].length / 2;
/*     */       
/* 757 */       Block belowBlock = craft.world.getBlockAt(craft.minX + xMid, craft.minY - 1, craft.minZ + zMid);
/*     */       
/* 759 */       if (belowBlock.getType() == Material.RAILS) {
/* 760 */         craft.railBlock = belowBlock;
/*     */       }
/*     */     }
/* 763 */     craft.blockCountStart = craft.blockCount;
/* 764 */     craft.lastBlockCount = craft.blockCount;
/* 765 */     craft.keelDepth = (63 - craft.minY);
/* 766 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\CraftBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */