/*      */ package com.maximuspayne.navycraft;
/*      */ 
/*      */ import com.maximuspayne.AimCannonNC.OneCannon;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import org.bukkit.ChatColor;
/*      */ import org.bukkit.Location;
/*      */ import org.bukkit.Server;
/*      */ import org.bukkit.World;
/*      */ import org.bukkit.block.Block;
/*      */ import org.bukkit.entity.Entity;
/*      */ import org.bukkit.entity.Player;
/*      */ import org.bukkit.plugin.Plugin;
/*      */ import org.bukkit.util.Vector;
/*      */ 
/*      */ public class CraftRotator
/*      */ {
/*      */   public Plugin plugin;
/*      */   public Craft craft;
/*      */   public int newMinX;
/*      */   public int newMinZ;
/*      */   public int newOffX;
/*      */   public int newOffZ;
/*      */   HashMap<Location, Location> cannonLocs;
/*      */   
/*      */   public CraftRotator(Craft c, Plugin p)
/*      */   {
/*   29 */     this.craft = c;
/*   30 */     this.plugin = p;
/*   31 */     this.cannonLocs = new HashMap();
/*   32 */     if ((this.craft.offX == 0) || (this.craft.offZ == 0)) {
/*   33 */       this.craft.offX = Math.round(this.craft.sizeX / 2);
/*   34 */       this.craft.offZ = Math.round(this.craft.sizeZ / 2);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public Location getPivot()
/*      */   {
/*   41 */     double x = this.craft.minX + this.craft.offX;
/*   42 */     double z = this.craft.minZ + this.craft.offZ;
/*   43 */     Location pivot = new Location(this.craft.world, x, this.craft.minY, z, this.craft.rotation, 0.0F);
/*      */     
/*   45 */     return pivot;
/*      */   }
/*      */   
/*      */   public Vector getCraftSize() {
/*   49 */     Vector craftSize = new Vector(this.craft.sizeX, this.craft.sizeY, this.craft.sizeZ);
/*   50 */     return craftSize;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean canGoThrough(int blockId)
/*      */   {
/*   57 */     if (blockId == 0) { return true;
/*      */     }
/*   59 */     if ((!this.craft.type.canNavigate) && (!this.craft.type.canDive)) {
/*   60 */       return false;
/*      */     }
/*      */     
/*   63 */     if (((blockId == 8) || (blockId == 9)) && 
/*   64 */       (this.craft.waterType == 8)) { return true;
/*      */     }
/*      */     
/*   67 */     if (((blockId == 10) || (blockId == 11)) && 
/*   68 */       (this.craft.waterType == 10)) { return true;
/*      */     }
/*      */     
/*   71 */     if ((blockId == 79) && ((this.craft.type.canNavigate) || (this.craft.type.canDive)) && 
/*   72 */       (this.craft.waterType == 8)) { return true;
/*      */     }
/*   74 */     return false;
/*      */   }
/*      */   
/*      */   public Location rotate(Entity ent, int r, int minX, int minZ, int offX, int offZ) {
/*   78 */     return rotate(ent.getLocation(), r, true, minX, minZ, offX, offZ);
/*      */   }
/*      */   
/*      */   public Location rotate(Location point, int r, int minX, int minZ, int offX, int offZ) {
/*   82 */     return rotate(point, r, false, minX, minZ, offX, offZ);
/*      */   }
/*      */   
/*      */   public Location rotate(Location point, int r, boolean isEntity, int minX, int minZ, int offX, int offZ) {
/*      */     Location entOffset;
/*      */     Location entOffset;
/*   88 */     if (isEntity) {
/*   89 */       entOffset = new Location(this.craft.world, 0.5D, 0.0D, 0.5D);
/*      */     } else {
/*   91 */       entOffset = new Location(this.craft.world, 0.0D, 0.0D, 0.0D);
/*      */     }
/*   93 */     Location newPoint = point.clone();
/*      */     
/*   95 */     NavyCraft.instance.DebugMessage("r " + r, 2);
/*   96 */     NavyCraft.instance.DebugMessage("newPoint1 " + newPoint, 2);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  121 */     newPoint.setX(minX + rotateX(point.getX() - offX, point.getZ() - offZ, r));
/*  122 */     newPoint.setZ(minZ + rotateZ(point.getX() - offX, point.getZ() - offZ, r));
/*      */     
/*      */ 
/*  125 */     return newPoint;
/*      */   }
/*      */   
/*      */ 
/*      */   public static double rotateX(double x, double z, int r)
/*      */   {
/*  131 */     if (r == 0)
/*  132 */       return x;
/*  133 */     if (r == 90)
/*  134 */       return -z;
/*  135 */     if (r == 180)
/*  136 */       return -x;
/*  137 */     if (r == 270)
/*  138 */       return z;
/*  139 */     return x;
/*      */   }
/*      */   
/*      */   public static double rotateZ(double x, double z, int r)
/*      */   {
/*  144 */     if (r == 0)
/*  145 */       return z;
/*  146 */     if (r == 90)
/*  147 */       return x;
/*  148 */     if (r == 180)
/*  149 */       return -z;
/*  150 */     if (r == 270) {
/*  151 */       return -x;
/*      */     }
/*  153 */     return z;
/*      */   }
/*      */   
/*      */ 
/*      */   public int rotateX(int x, int z, int r)
/*      */   {
/*  159 */     NavyCraft.instance.DebugMessage("r is " + r + 
/*  160 */       ", x is " + x + 
/*  161 */       ", z is " + z, 4);
/*      */     
/*  163 */     if (r == 0)
/*  164 */       return x;
/*  165 */     if (r == 90)
/*  166 */       return -z;
/*  167 */     if (r == 180)
/*  168 */       return -x;
/*  169 */     if (r == 270)
/*  170 */       return z;
/*  171 */     return x;
/*      */   }
/*      */   
/*      */ 
/*      */   public static int rotateZ(int x, int z, int r)
/*      */   {
/*  177 */     if (r == 0)
/*  178 */       return z;
/*  179 */     if (r == 90)
/*  180 */       return x;
/*  181 */     if (r == 180)
/*  182 */       return -z;
/*  183 */     if (r == 270) {
/*  184 */       return -x;
/*      */     }
/*  186 */     return z;
/*      */   }
/*      */   
/*      */   public void setBlock(double id, int X, int Y, int Z)
/*      */   {
/*  191 */     if ((Y < 0) || (Y > 255) || (id < 0.0D) || (id > 255.0D)) {
/*  192 */       return;
/*      */     }
/*      */     
/*  195 */     if (((id == 64.0D) || (id == 63.0D)) && (NavyCraft.instance.DebugMode)) {
/*  196 */       System.out.println("This stack trace is totally expected.");
/*      */       
/*      */ 
/*  199 */       new Throwable().printStackTrace();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  204 */     this.craft.world.getBlockAt(X, Y, Z).setTypeId((int)id);
/*      */   }
/*      */   
/*      */   public void setBlock(double id, int x, int y, int z, int dx, int dy, int dz, int r) {
/*  208 */     int X = this.craft.minX + rotateX(x, z, r) + dx;
/*  209 */     int Y = this.craft.minY + y + dy;
/*  210 */     int Z = this.craft.minZ + rotateZ(x, z, r) + dz;
/*      */     
/*  212 */     setBlock(id, X, Y, Z);
/*      */   }
/*      */   
/*      */   public void setDataBlock(short id, byte data, int X, int Y, int Z) {
/*  216 */     if ((Y < 0) || (Y > 255) || (id < 0) || (id > 255)) {
/*  217 */       return;
/*      */     }
/*      */     
/*  220 */     this.craft.world.getBlockAt(X, Y, Z).setTypeId(id);
/*  221 */     this.craft.world.getBlockAt(X, Y, Z).setData(data);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public short getWorldBlockId(int x, int y, int z, int r, int minX, int minY, int minZ, int offX, int offZ)
/*      */   {
/*  232 */     short blockId = (short)this.craft.world.getBlockTypeIdAt(minX + x, 
/*  233 */       minY + y, 
/*  234 */       minZ + z);
/*      */     
/*  236 */     return blockId;
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
/*      */   public short getCraftBlockId(int x, int y, int z, int r)
/*      */   {
/*  253 */     if ((x < this.craft.minX) || (x >= this.craft.sizeX + this.craft.minX) || 
/*  254 */       (y < 0) || (y >= this.craft.sizeY) || 
/*  255 */       (z < this.craft.minZ) || (z >= this.craft.sizeZ + this.craft.minZ)) {
/*  256 */       return 255;
/*      */     }
/*      */     
/*  259 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean canMoveBlocks(int dx, int dy, int dz, int dr)
/*      */   {
/*  266 */     int newRotation = (dr + 360) % 360;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  275 */     Vector newSize = getCraftSize().clone();
/*      */     
/*  277 */     if ((dr == 90) || (dr == 270))
/*      */     {
/*  279 */       newSize.setX(getCraftSize().getZ());
/*  280 */       newSize.setZ(getCraftSize().getX());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  285 */     short[][][] newMatrix = new short[newSize.getBlockX()][newSize.getBlockY()][newSize.getBlockZ()];
/*      */     
/*      */ 
/*      */ 
/*  289 */     for (int x = 0; x < newSize.getBlockX(); x++) {
/*  290 */       for (int y = 0; y < newSize.getBlockY(); y++) {
/*  291 */         for (int z = 0; z < newSize.getBlockZ(); z++) {
/*  292 */           int newX = 0;
/*  293 */           int newZ = 0;
/*  294 */           if (dr == 90) {
/*  295 */             newX = z;
/*  296 */             newZ = newSize.getBlockX() - 1 - x;
/*  297 */           } else if (dr == 270) {
/*  298 */             newX = newSize.getBlockZ() - 1 - z;
/*  299 */             newZ = x;
/*      */           } else {
/*  301 */             newX = newSize.getBlockX() - 1 - x;
/*  302 */             newZ = newSize.getBlockZ() - 1 - z;
/*      */           }
/*      */           
/*  305 */           newMatrix[x][y][z] = this.craft.matrix[newX][y][newZ];
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  312 */     int posX = this.craft.minX + this.craft.offX;
/*  313 */     int posZ = this.craft.minZ + this.craft.offZ;
/*      */     
/*      */ 
/*  316 */     int newoffsetX = rotateX(this.craft.offX, this.craft.offZ, dr);
/*  317 */     int newoffsetZ = rotateZ(this.craft.offX, this.craft.offZ, dr);
/*      */     
/*      */ 
/*  320 */     if (newoffsetX < 0)
/*  321 */       newoffsetX = newSize.getBlockX() - 1 - Math.abs(newoffsetX);
/*  322 */     if (newoffsetZ < 0) {
/*  323 */       newoffsetZ = newSize.getBlockZ() - 1 - Math.abs(newoffsetZ);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  328 */     int newminX = posX - newoffsetX;
/*  329 */     int newminZ = posZ - newoffsetZ;
/*      */     
/*  331 */     for (int x = 0; x < newSize.getBlockX(); x++) {
/*  332 */       for (int z = 0; z < newSize.getBlockZ(); z++) {
/*  333 */         for (int y = 0; y < newSize.getBlockY(); y++)
/*      */         {
/*  335 */           if (newMatrix[x][y][z] != 255)
/*      */           {
/*  337 */             if (getCraftBlockId(x + dx + newminX, y + dy, z + dz + newminZ, dr) == 255)
/*      */             {
/*  339 */               if (!canGoThrough(getWorldBlockId(x + dx, y + dy, z + dz, newRotation, newminX, this.craft.minY, newminZ, newoffsetX, newoffsetZ)))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  345 */                 return false;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  353 */     return true;
/*      */   }
/*      */   
/*      */   public void turn(int dr)
/*      */   {
/*  358 */     if (this.craft.waitTorpLoading > 0)
/*      */     {
/*  360 */       if (this.craft.driverName != null)
/*      */       {
/*  362 */         Player p = this.plugin.getServer().getPlayer(this.craft.driverName);
/*  363 */         if (p != null)
/*  364 */           p.sendMessage(ChatColor.RED + "Torpedo Reloading Please Wait.");
/*      */       }
/*  366 */       return;
/*      */     }
/*      */     
/*  369 */     CraftMover cm = new CraftMover(this.craft, this.plugin);
/*  370 */     cm.structureUpdate(null, false);
/*      */     
/*  372 */     if (this.craft.sinking)
/*      */     {
/*  374 */       if (this.craft.driverName != null)
/*      */       {
/*  376 */         Player p = this.plugin.getServer().getPlayer(this.craft.driverName);
/*  377 */         if (p != null)
/*  378 */           p.sendMessage(ChatColor.RED + "You are sinking!");
/*      */       }
/*  380 */       return;
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
/*  394 */     if (!canMoveBlocks(0, 0, 0, dr))
/*      */     {
/*  396 */       if (this.craft.driverName != null)
/*      */       {
/*  398 */         Player p = this.plugin.getServer().getPlayer(this.craft.driverName);
/*  399 */         if (p != null)
/*  400 */           p.sendMessage("Turn Blocked");
/*      */       }
/*  402 */       return;
/*      */     }
/*      */     
/*  405 */     dr = (dr + 360) % 360;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  414 */     ArrayList<Entity> craftEntities = this.craft.getCraftEntities(false);
/*  415 */     HashMap<Entity, Location> entPreLoc = new HashMap();
/*      */     
/*      */     Location oldLoc;
/*  418 */     for (Entity e : craftEntities) {
/*  419 */       oldLoc = e.getLocation();
/*      */       
/*      */ 
/*  422 */       int newoffsetX = (int)rotateX(oldLoc.getBlockX() - (this.craft.minX + this.craft.offX), oldLoc.getBlockZ() - (this.craft.minZ + this.craft.offZ), dr);
/*  423 */       int newoffsetZ = (int)rotateZ(oldLoc.getBlockX() - (this.craft.minX + this.craft.offX), oldLoc.getBlockZ() - (this.craft.minZ + this.craft.offZ), dr);
/*      */       
/*  425 */       NavyCraft.instance.DebugMessage("New off is " + newoffsetX + ", " + newoffsetZ, 2);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  432 */       entPreLoc.put(e, new Location(this.craft.world, newoffsetX, e.getLocation().getY(), newoffsetZ));
/*      */     }
/*      */     
/*      */ 
/*  436 */     Vector moveBy = new Vector(0, 0, 0);
/*  437 */     moveBlocks(moveBy, dr);
/*      */     
/*      */ 
/*      */ 
/*  441 */     for (Entity e : craftEntities) {
/*  442 */       Player p = null;
/*  443 */       if (this.craft.driverName != null)
/*  444 */         p = this.plugin.getServer().getPlayer(this.craft.driverName);
/*  445 */       if ((p != null) && (e != p))
/*  446 */         ((Location)entPreLoc.get(e)).setYaw(((Location)entPreLoc.get(e)).getYaw() + dr);
/*  447 */       NavyCraft.instance.DebugMessage("teleporting " + entPreLoc.get(e), 2);
/*      */       
/*      */ 
/*  450 */       e.teleport(new Location(this.craft.world, ((Location)entPreLoc.get(e)).getX() + this.craft.minX + this.craft.offX + 0.5D, ((Location)entPreLoc.get(e)).getY(), ((Location)entPreLoc.get(e)).getZ() + this.craft.minZ + this.craft.offZ + 0.5D, e.getLocation().getYaw(), e.getLocation().getPitch()));
/*      */     }
/*      */     
/*  453 */     this.craft.rotation += dr;
/*  454 */     if (this.craft.rotation > 360) {
/*  455 */       this.craft.rotation -= 360;
/*  456 */     } else if (this.craft.rotation < 0) {
/*  457 */       this.craft.rotation = (360 - Math.abs(this.craft.rotation));
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
/*      */   public void moveBlocks(Vector moveBy, int dr)
/*      */   {
/*  478 */     dr = (dr + 360) % 360;
/*      */     
/*  480 */     CraftMover cm = new CraftMover(this.craft, this.plugin);
/*      */     
/*      */ 
/*  483 */     Vector newSize = getCraftSize().clone();
/*      */     
/*      */ 
/*      */ 
/*  487 */     if ((dr == 90) || (dr == 270))
/*      */     {
/*  489 */       newSize.setX(getCraftSize().getZ());
/*  490 */       newSize.setZ(getCraftSize().getX());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  497 */     short[][][] newMatrix = new short[newSize.getBlockX()][newSize.getBlockY()][newSize.getBlockZ()];
/*      */     
/*      */ 
/*  500 */     cm.storeDataBlocks();
/*  501 */     cm.storeComplexBlocks();
/*      */     
/*      */ 
/*      */ 
/*  505 */     ArrayList<DataBlock> unMovedDataBlocks = new ArrayList();
/*  506 */     ArrayList<DataBlock> unMovedComplexBlocks = new ArrayList();
/*      */     
/*      */ 
/*  509 */     while (this.craft.dataBlocks.size() > 0) {
/*  510 */       unMovedDataBlocks.add((DataBlock)this.craft.dataBlocks.get(0));
/*  511 */       this.craft.dataBlocks.remove(0);
/*      */     }
/*      */     
/*  514 */     while (this.craft.complexBlocks.size() > 0) {
/*  515 */       unMovedComplexBlocks.add((DataBlock)this.craft.complexBlocks.get(0));
/*  516 */       this.craft.complexBlocks.remove(0);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  526 */     for (int x = 0; x < newSize.getBlockX(); x++) {
/*  527 */       for (int y = 0; y < newSize.getBlockY(); y++) {
/*  528 */         for (int z = 0; z < newSize.getBlockZ(); z++) {
/*  529 */           int newX = 0;
/*  530 */           int newZ = 0;
/*  531 */           if (dr == 90) {
/*  532 */             newX = z;
/*  533 */             newZ = newSize.getBlockX() - 1 - x;
/*  534 */           } else if (dr == 270) {
/*  535 */             newX = newSize.getBlockZ() - 1 - z;
/*  536 */             newZ = x;
/*      */           } else {
/*  538 */             newX = newSize.getBlockX() - 1 - x;
/*  539 */             newZ = newSize.getBlockZ() - 1 - z;
/*      */           }
/*      */           
/*  542 */           newMatrix[x][y][z] = this.craft.matrix[newX][y][newZ];
/*      */           
/*  544 */           for (int i = 0; i < unMovedDataBlocks.size(); i++)
/*      */           {
/*  546 */             DataBlock dataBlock = (DataBlock)unMovedDataBlocks.get(i);
/*  547 */             if (dataBlock.locationMatches(newX, y, newZ))
/*      */             {
/*      */ 
/*  550 */               if (dataBlock.id == 23)
/*      */               {
/*  552 */                 Location cannonLoc = new Location(this.craft.world, dataBlock.x + this.craft.minX, dataBlock.y + this.craft.minY, dataBlock.z + this.craft.minZ);
/*  553 */                 for (OneCannon onec : com.maximuspayne.AimCannonNC.AimCannon.getCannons())
/*      */                 {
/*  555 */                   if (onec.isThisCannon(cannonLoc, false))
/*      */                   {
/*  557 */                     this.cannonLocs.put(new Location(this.craft.world, x, y, z), cannonLoc);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*  565 */               dataBlock.x = x;
/*  566 */               dataBlock.z = z;
/*      */               
/*  568 */               this.craft.dataBlocks.add(dataBlock);
/*  569 */               unMovedDataBlocks.remove(i);
/*  570 */               break;
/*      */             }
/*      */           }
/*  573 */           for (int i = 0; i < unMovedComplexBlocks.size(); i++)
/*      */           {
/*  575 */             DataBlock dataBlock = (DataBlock)unMovedComplexBlocks.get(i);
/*  576 */             if (dataBlock.locationMatches(newX, y, newZ)) {
/*  577 */               dataBlock.x = x;
/*  578 */               dataBlock.z = z;
/*      */               
/*  580 */               this.craft.complexBlocks.add(dataBlock);
/*  581 */               unMovedComplexBlocks.remove(i);
/*  582 */               break;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  593 */     for (int x = 0; x < this.craft.sizeX; x++) {
/*  594 */       for (int z = 0; z < this.craft.sizeZ; z++) {
/*  595 */         for (int y = 0; y < this.craft.sizeY; y++) {
/*  596 */           if (this.craft.matrix[x][y][z] != -1) {
/*  597 */             int blockId = this.craft.matrix[x][y][z];
/*  598 */             Block block = this.craft.world.getBlockAt(this.craft.minX + x, this.craft.minY + y, this.craft.minZ + z);
/*      */             
/*  600 */             if ((BlocksInfo.needsSupport(blockId)) && (
/*  601 */               ((blockId != 64) && (blockId != 71)) || 
/*  602 */               (block.getData() < 8)))
/*      */             {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  608 */               if ((blockId != 26) || (block.getData() <= 4))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  613 */                 setBlock(0.0D, this.craft.minX + x, this.craft.minY + y, this.craft.minZ + z);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  621 */     for (int x = 0; x < this.craft.sizeX; x++) {
/*  622 */       for (int y = 0; y < this.craft.sizeY; y++) {
/*  623 */         for (int z = 0; z < this.craft.sizeZ; z++)
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  649 */           if ((this.craft.minY + y >= 63) || (
/*  650 */             (!this.craft.type.canNavigate) && (!this.craft.type.canDive))) {
/*  651 */             setBlock(0.0D, this.craft.minX + x, this.craft.minY + y, this.craft.minZ + z);
/*      */           } else {
/*  653 */             setBlock(this.craft.waterType, this.craft.minX + x, this.craft.minY + y, this.craft.minZ + z);
/*      */           }
/*      */         }
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
/*  666 */     this.craft.matrix = newMatrix;
/*  667 */     this.craft.sizeX = newSize.getBlockX();
/*  668 */     this.craft.sizeZ = newSize.getBlockZ();
/*      */     
/*      */ 
/*  671 */     int posX = this.craft.minX + this.craft.offX;
/*  672 */     int posZ = this.craft.minZ + this.craft.offZ;
/*      */     
/*  674 */     NavyCraft.instance.DebugMessage("Min vals start " + this.craft.minX + ", " + this.craft.minZ, 2);
/*      */     
/*  676 */     NavyCraft.instance.DebugMessage("Off was " + this.craft.offX + ", " + this.craft.offZ, 2);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  681 */     int newoffsetX = rotateX(this.craft.offX, this.craft.offZ, dr);
/*  682 */     int newoffsetZ = rotateZ(this.craft.offX, this.craft.offZ, dr);
/*      */     
/*  684 */     NavyCraft.instance.DebugMessage("New off is " + newoffsetX + ", " + newoffsetZ, 2);
/*      */     
/*  686 */     if (newoffsetX < 0)
/*  687 */       newoffsetX = newSize.getBlockX() - 1 - Math.abs(newoffsetX);
/*  688 */     if (newoffsetZ < 0) {
/*  689 */       newoffsetZ = newSize.getBlockZ() - 1 - Math.abs(newoffsetZ);
/*      */     }
/*  691 */     this.craft.offX = newoffsetX;
/*  692 */     this.craft.offZ = newoffsetZ;
/*  693 */     this.newOffX = newoffsetX;
/*  694 */     this.newOffZ = newoffsetZ;
/*      */     
/*  696 */     NavyCraft.instance.DebugMessage("Off is " + this.craft.offX + ", " + this.craft.offZ, 2);
/*      */     
/*      */ 
/*  699 */     this.craft.minX = (posX - this.craft.offX);
/*  700 */     this.craft.minZ = (posZ - this.craft.offZ);
/*  701 */     this.newMinX = this.craft.minX;
/*  702 */     this.newMinZ = this.craft.minZ;
/*  703 */     this.craft.maxX = (this.craft.minX + this.craft.sizeX - 1);
/*  704 */     this.craft.maxZ = (this.craft.minZ + this.craft.sizeZ - 1);
/*      */     
/*  706 */     NavyCraft.instance.DebugMessage("Min vals end " + this.craft.minX + ", " + this.craft.minZ, 2);
/*      */     
/*  708 */     rotateCardinals(this.craft.dataBlocks, dr);
/*  709 */     rotateCardinals(this.craft.complexBlocks, dr);
/*      */     
/*      */ 
/*  712 */     for (int x = 0; x < getCraftSize().getX(); x++) {
/*  713 */       for (int y = 0; y < getCraftSize().getY(); y++) {
/*  714 */         for (int z = 0; z < getCraftSize().getZ(); z++) {
/*  715 */           int blockId = newMatrix[x][y][z];
/*      */           
/*  717 */           if ((blockId != -1) && 
/*  718 */             (!BlocksInfo.needsSupport(blockId)) && (blockId != 52) && (blockId != 34) && (blockId != 36)) {
/*  719 */             setBlock(blockId, this.craft.minX + x, this.craft.minY + y, this.craft.minZ + z);
/*  720 */           } else if ((blockId == 34) || (blockId == 36)) {
/*  721 */             setBlock(0.0D, this.craft.minX + x, this.craft.minY + y, this.craft.minZ + z);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  729 */     for (int x = 0; x < getCraftSize().getX(); x++) {
/*  730 */       for (int y = 0; y < getCraftSize().getY(); y++) {
/*  731 */         for (int z = 0; z < getCraftSize().getZ(); z++) {
/*  732 */           int blockId = newMatrix[x][y][z];
/*      */           
/*  734 */           if ((BlocksInfo.needsSupport(blockId)) && 
/*  735 */             (!BlocksInfo.isDataBlock(blockId)) && (blockId != 63) && (blockId != 68) && (blockId != 65)) {
/*  736 */             setBlock(blockId, this.craft.minX + x, this.craft.minY + y, this.craft.minZ + z);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  742 */     restoreDataBlocks(0, 0, 0);
/*  743 */     cm.restoreComplexBlocks(0, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void rotateCardinals(ArrayList<DataBlock> blocksToRotate, int dr)
/*      */   {
/*  753 */     for (DataBlock dataBlock : blocksToRotate)
/*      */     {
/*  755 */       int blockId = dataBlock.id;
/*      */       
/*      */ 
/*  758 */       if ((blockId == 17) && (dataBlock.data > 3))
/*      */       {
/*  760 */         if (dataBlock.data < 8) {
/*  761 */           dataBlock.data += 4;
/*      */         } else {
/*  763 */           dataBlock.data -= 4;
/*      */         }
/*      */       }
/*      */       
/*  767 */       if ((blockId == 155) && (dataBlock.data > 2))
/*      */       {
/*  769 */         if (dataBlock.data == 3) {
/*  770 */           dataBlock.data = 4;
/*      */         } else {
/*  772 */           dataBlock.data = 3;
/*      */         }
/*      */       }
/*      */       
/*  776 */       if ((blockId == 170) && (dataBlock.data > 3))
/*      */       {
/*  778 */         if (dataBlock.data < 8) {
/*  779 */           dataBlock.data += 4;
/*      */         } else {
/*  781 */           dataBlock.data -= 4;
/*      */         }
/*      */       }
/*      */       
/*  785 */       if (((blockId != 50) && (blockId != 75) && (blockId != 76)) || 
/*  786 */         (dataBlock.data != 5))
/*      */       {
/*      */ 
/*      */ 
/*  790 */         if ((blockId == 33) || (blockId == 29) || (blockId == 34))
/*      */         {
/*  792 */           if ((dataBlock.data == 0) || (dataBlock.data == 1) || (dataBlock.data == 8) || (dataBlock.data == 9))
/*      */           {
/*  794 */             if (dataBlock.data == 0)
/*  795 */               dataBlock.data = 1;
/*  796 */             if (dataBlock.data != 8) continue;
/*  797 */             dataBlock.data = 9;
/*  798 */             continue;
/*      */           } }
/*      */         byte[] cardinals;
/*      */         byte[] cardinals;
/*  802 */         if (BlocksInfo.getCardinals(blockId) != null) {
/*  803 */           cardinals = java.util.Arrays.copyOf(BlocksInfo.getCardinals(blockId), 4);
/*      */         } else {
/*  805 */           cardinals = null;
/*      */         }
/*      */         
/*      */ 
/*  809 */         if ((blockId == 53) || (blockId == 67) || (blockId == 108) || (blockId == 109) || (blockId == 114) || (blockId == 128) || (blockId == 134) || (blockId == 135) || (blockId == 136) || (blockId == 156) || (blockId == 180))
/*      */         {
/*  811 */           if (dataBlock.data > 3)
/*      */           {
/*  813 */             for (int c = 0; c < 4; c++) {
/*  814 */               int tmp421_419 = c; byte[] tmp421_418 = cardinals;tmp421_418[tmp421_419] = ((byte)(tmp421_418[tmp421_419] + 4));
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  820 */         if (blockId == 63) {
/*  821 */           dataBlock.data = ((dataBlock.data + 4) % 16);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*  827 */         else if (blockId == 176) {
/*  828 */           dataBlock.data = ((dataBlock.data + 4) % 16);
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  834 */           if ((blockId == 26) && 
/*  835 */             (dataBlock.data >= 8)) {
/*  836 */             for (int c = 0; c < 4; c++) {
/*  837 */               int tmp513_511 = c; byte[] tmp513_510 = cardinals;tmp513_510[tmp513_511] = ((byte)(tmp513_510[tmp513_511] + 8));
/*      */             }
/*      */           }
/*      */           
/*  841 */           if ((blockId == 64) || (blockId == 71) || (blockId == 193) || (blockId == 194) || (blockId == 195) || (blockId == 196) || (blockId == 197) || 
/*  842 */             (blockId == 93) || (blockId == 94))
/*      */           {
/*  844 */             if (dataBlock.data >= 12)
/*  845 */               for (int c = 0; c < 4; c++) {
/*  846 */                 int tmp616_614 = c; byte[] tmp616_613 = cardinals;tmp616_613[tmp616_614] = ((byte)(tmp616_613[tmp616_614] + 12));
/*  847 */               } else if (dataBlock.data >= 8)
/*  848 */               for (int c = 0; c < 4; c++) {
/*  849 */                 int tmp654_652 = c; byte[] tmp654_651 = cardinals;tmp654_651[tmp654_652] = ((byte)(tmp654_651[tmp654_652] + 8));
/*  850 */               } else if (dataBlock.data >= 4) {
/*  851 */               for (int c = 0; c < 4; c++) {
/*  852 */                 int tmp691_689 = c; byte[] tmp691_688 = cardinals;tmp691_688[tmp691_689] = ((byte)(tmp691_688[tmp691_689] + 4));
/*      */               }
/*      */             }
/*      */           }
/*  856 */           if (blockId == 66) {
/*  857 */             if (dataBlock.data == 0) {
/*  858 */               dataBlock.data = 1;
/*  859 */               continue;
/*      */             }
/*  861 */             if (dataBlock.data == 1) {
/*  862 */               dataBlock.data = 0;
/*  863 */               continue;
/*      */             }
/*      */           }
/*      */           
/*  867 */           if (blockId == 69)
/*      */           {
/*  869 */             if ((dataBlock.data == 5) || (dataBlock.data == 6) || 
/*  870 */               (dataBlock.data == 13) || (dataBlock.data == 14)) {
/*  871 */               cardinals = new byte[] { 6, 5, 14, 13 };
/*      */             }
/*  873 */             else if (dataBlock.data > 4) {
/*  874 */               for (int c = 0; c < 4; c++) {
/*  875 */                 int tmp838_836 = c; byte[] tmp838_835 = cardinals;tmp838_835[tmp838_836] = ((byte)(tmp838_835[tmp838_836] + 8));
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  880 */           if ((blockId == 77) || (blockId == 143))
/*      */           {
/*  882 */             if (dataBlock.data > 4)
/*      */             {
/*  884 */               for (int c = 0; c < 4; c++) {
/*  885 */                 int tmp887_885 = c; byte[] tmp887_884 = cardinals;tmp887_884[tmp887_885] = ((byte)(tmp887_884[tmp887_885] + 8));
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  890 */           if ((blockId == 96) || (blockId == 167))
/*      */           {
/*  892 */             if (dataBlock.data > 4)
/*      */             {
/*  894 */               for (int c = 0; c < 4; c++) {
/*  895 */                 int tmp936_934 = c; byte[] tmp936_933 = cardinals;tmp936_933[tmp936_934] = ((byte)(tmp936_933[tmp936_934] + 4));
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  900 */           if ((blockId == 93) || (blockId == 94)) {
/*  901 */             if (dataBlock.data > 11) {
/*  902 */               for (int c = 0; c < 4; c++) {
/*  903 */                 int tmp984_982 = c; byte[] tmp984_981 = cardinals;tmp984_981[tmp984_982] = ((byte)(tmp984_981[tmp984_982] + 12));
/*      */               }
/*  905 */             } else if (dataBlock.data > 7) {
/*  906 */               for (int c = 0; c < 4; c++) {
/*  907 */                 int tmp1022_1020 = c; byte[] tmp1022_1019 = cardinals;tmp1022_1019[tmp1022_1020] = ((byte)(tmp1022_1019[tmp1022_1020] + 8));
/*      */               }
/*  909 */             } else if (dataBlock.data > 3) {
/*  910 */               for (int c = 0; c < 4; c++) {
/*  911 */                 int tmp1059_1057 = c; byte[] tmp1059_1056 = cardinals;tmp1059_1056[tmp1059_1057] = ((byte)(tmp1059_1056[tmp1059_1057] + 4));
/*      */               }
/*      */             }
/*      */           }
/*  915 */           if (cardinals != null) {
/*  916 */             NavyCraft.instance.DebugMessage(org.bukkit.Material.getMaterial(blockId) + 
/*  917 */               " Cardinals are " + 
/*  918 */               cardinals[0] + ", " + 
/*  919 */               cardinals[1] + ", " + 
/*  920 */               cardinals[2] + ", " + 
/*  921 */               cardinals[3], 2);
/*      */             
/*  923 */             int i = 0;
/*  924 */             for (i = 0; i < 3; i++) {
/*  925 */               if (dataBlock.data == cardinals[i])
/*      */                 break;
/*      */             }
/*  928 */             NavyCraft.instance.DebugMessage("i starts as " + i + " which is " + cardinals[i], 2);
/*      */             
/*  930 */             i += dr / 90;
/*      */             
/*  932 */             if (i > 3) {
/*  933 */               i -= 4;
/*      */             }
/*  935 */             NavyCraft.instance.DebugMessage("i ends as " + i + ", which is " + cardinals[i], 2);
/*      */             
/*  937 */             dataBlock.data = cardinals[i];
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public void removeSupportBlocks()
/*      */   {
/*  946 */     for (int x = 0; x < this.craft.sizeX; x++) {
/*  947 */       for (int z = 0; z < this.craft.sizeZ; z++) {
/*  948 */         for (int y = this.craft.sizeY - 1; y > -1; y--)
/*      */         {
/*      */ 
/*  951 */           short blockId = this.craft.matrix[x][y][z];
/*      */           
/*      */ 
/*  954 */           if (BlocksInfo.needsSupport(blockId))
/*      */           {
/*      */ 
/*  957 */             Block block = getWorldBlock(x, y, z);
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*  962 */             if (((blockId != 64) && (blockId != 71)) || 
/*  963 */               (block.getData() < 8))
/*      */             {
/*      */ 
/*      */ 
/*  967 */               if ((blockId != 26) || 
/*  968 */                 (block.getData() < 4))
/*      */               {
/*      */ 
/*      */ 
/*  972 */                 setBlock(0, block); }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public Block getWorldBlock(int x, int y, int z) {
/*  981 */     return this.craft.world.getBlockAt(this.craft.minX + x, this.craft.minY + y, this.craft.minZ + z);
/*      */   }
/*      */   
/*      */   public void setBlock(int id, Block block)
/*      */   {
/*  986 */     if ((id < 0) || (id > 255))
/*      */     {
/*  988 */       System.out.println("Invalid block type ID. Begin panic.");
/*  989 */       return;
/*      */     }
/*      */     
/*  992 */     if (block.getTypeId() == id) {
/*  993 */       NavyCraft.instance.DebugMessage("Tried to change a " + id + " to itself.", 5);
/*  994 */       return;
/*      */     }
/*      */     
/*  997 */     NavyCraft.instance.DebugMessage("Attempting to set block at " + block.getX() + ", " + 
/*  998 */       block.getY() + ", " + block.getZ() + " to " + id, 5);
/*      */     
/* 1000 */     if (!block.setTypeId(id)) {
/* 1001 */       if (!this.craft.world.getBlockAt(block.getLocation()).setTypeId(id)) {
/* 1002 */         System.out.println("Could not set block of type " + block.getTypeId() + 
/* 1003 */           " to type " + id + ". I tried to fix it, but I couldn't.");
/*      */       } else {
/* 1005 */         System.out.println("I hope to whatever God you believe in that this fix worked.");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void restoreDataBlocks(int dx, int dy, int dz)
/*      */   {
/* 1013 */     for (DataBlock dataBlock : this.craft.dataBlocks)
/*      */     {
/*      */ 
/* 1016 */       if (BlocksInfo.needsSupport(this.craft.matrix[dataBlock.x][dataBlock.y][dataBlock.z])) {
/* 1017 */         Block block = getWorldBlock(dx + dataBlock.x, dy + dataBlock.y, dz + dataBlock.z);
/*      */         
/* 1019 */         setBlock(this.craft.matrix[dataBlock.x][dataBlock.y][dataBlock.z], block);
/*      */         
/* 1021 */         block.setData((byte)dataBlock.data);
/*      */       } else {
/* 1023 */         Block theBlock = getWorldBlock(dx + dataBlock.x, dy + dataBlock.y, dz + dataBlock.z);
/* 1024 */         if (theBlock.getTypeId() == 23)
/*      */         {
/* 1026 */           OneCannon oc = new OneCannon(theBlock.getLocation(), this.plugin);
/* 1027 */           if (oc.isValidCannon(theBlock))
/*      */           {
/* 1029 */             for (OneCannon onec : com.maximuspayne.AimCannonNC.AimCannon.getCannons())
/*      */             {
/* 1031 */               Location testLoc = new Location(this.craft.world, dataBlock.x, dataBlock.y, dataBlock.z);
/* 1032 */               if (this.cannonLocs.containsKey(testLoc))
/*      */               {
/* 1034 */                 boolean oldCannonFound = onec.isThisCannon((Location)this.cannonLocs.get(testLoc), true);
/* 1035 */                 if (oldCannonFound)
/*      */                 {
/* 1037 */                   Location newLoc = theBlock.getLocation();
/* 1038 */                   onec.setLocation(newLoc);
/* 1039 */                   this.cannonLocs.remove(testLoc);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 1045 */         theBlock.setData((byte)dataBlock.data);
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\CraftRotator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */