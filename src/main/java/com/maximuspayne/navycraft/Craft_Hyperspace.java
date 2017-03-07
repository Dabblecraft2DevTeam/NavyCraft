/*     */ package com.maximuspayne.navycraft;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class Craft_Hyperspace
/*     */ {
/*  12 */   public static ArrayList<Block> hyperspaceBlocks = new ArrayList();
/*  13 */   public static Material hyperSpaceBlock = Material.PORTAL;
/*     */   public static Plugin plugin;
/*     */   
/*     */   public Craft_Hyperspace(Plugin p)
/*     */   {
/*  18 */     plugin = p;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void enterHyperSpace(Craft craft)
/*     */   {
/*  24 */     surroundCraft(craft, Boolean.valueOf(true));
/*  25 */     craft.inHyperSpace = true;
/*     */     
/*  27 */     Player driver = plugin.getServer().getPlayer(craft.driverName);
/*  28 */     if (driver != null) {
/*  29 */       driver.sendMessage("You have entered hyperspace. Type \"/" + craft.type.name + " hyperspace\" to exit.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void exitHyperSpace(Craft craft)
/*     */   {
/*  39 */     surroundCraft(craft, Boolean.valueOf(false));
/*     */     
/*  41 */     CraftMover cm = new CraftMover(craft, plugin);
/*     */     
/*  43 */     craft.HyperSpaceMoves[0] = 0;
/*  44 */     craft.HyperSpaceMoves[1] = 0;
/*  45 */     craft.HyperSpaceMoves[2] = 0;
/*     */     
/*  47 */     craft.inHyperSpace = false;
/*     */     
/*  49 */     Player driver = plugin.getServer().getPlayer(craft.driverName);
/*  50 */     if (driver != null) {
/*  51 */       driver.sendMessage("You exit hyperspace " + 
/*  52 */         (craft.HyperSpaceMoves[0] + craft.HyperSpaceMoves[1]) * 16 + 
/*  53 */         " blocks from where you started.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void hyperSpaceMove(Craft craft, int dx, int dy, int dz)
/*     */   {
/*  60 */     craft.HyperSpaceMoves[0] += dx;
/*  61 */     craft.HyperSpaceMoves[1] += dy;
/*  62 */     craft.HyperSpaceMoves[2] += dz;
/*     */     
/*  64 */     Player driver = plugin.getServer().getPlayer(craft.driverName);
/*  65 */     if (driver != null)
/*  66 */       driver.sendMessage("You are now " + 
/*  67 */         craft.HyperSpaceMoves[0] * 16 + " X, " + craft.HyperSpaceMoves[1] * 16 + "Y, " + craft.HyperSpaceMoves[2] * 16 + 
/*  68 */         "Z blocks from where you started.");
/*     */   }
/*     */   
/*     */   public static void setBlock(Block block, Boolean fieldOn) {
/*  72 */     if (fieldOn.booleanValue()) {
/*  73 */       hyperspaceBlocks.add(block);
/*  74 */       block.setType(hyperSpaceBlock);
/*     */     }
/*     */     else {
/*  77 */       hyperspaceBlocks.remove(block);
/*  78 */       block.setType(Material.AIR);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void surroundCraft(Craft craft, Boolean fieldOn)
/*     */   {
/*  84 */     if (NavyCraft.instance.ConfigSetting("DisableHyperSpaceField").equalsIgnoreCase("true")) {
/*  85 */       return;
/*     */     }
/*     */     
/*     */ 
/*  89 */     int bufferAmount = 2;
/*     */     
/*     */ 
/*  92 */     for (int x = craft.minX - bufferAmount; x < craft.maxX + bufferAmount; x++) {
/*  93 */       for (int y = craft.minY - bufferAmount; y < craft.maxY + bufferAmount; y++) {
/*  94 */         Block fieldBlock = craft.world.getBlockAt(x, y, craft.minZ - bufferAmount);
/*  95 */         setBlock(fieldBlock, fieldOn);
/*  96 */         fieldBlock = craft.world.getBlockAt(x, y, craft.maxZ + bufferAmount);
/*  97 */         setBlock(fieldBlock, fieldOn);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 102 */     for (int z = craft.minZ - bufferAmount; z < craft.maxZ + bufferAmount; z++) {
/* 103 */       for (int y = craft.minY - bufferAmount; y < craft.maxY + bufferAmount; y++) {
/* 104 */         Block fieldBlock = craft.world.getBlockAt(craft.minX - bufferAmount, y, z);
/* 105 */         setBlock(fieldBlock, fieldOn);
/* 106 */         fieldBlock = craft.world.getBlockAt(craft.maxX + bufferAmount, y, z);
/* 107 */         setBlock(fieldBlock, fieldOn);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 112 */     for (int x = craft.minX - bufferAmount; x < craft.maxX + bufferAmount; x++) {
/* 113 */       for (int z = craft.minZ - bufferAmount; z < craft.maxZ + bufferAmount; z++) {
/* 114 */         Block fieldBlock = craft.world.getBlockAt(x, craft.minY - bufferAmount, z);
/* 115 */         setBlock(fieldBlock, fieldOn);
/* 116 */         fieldBlock = craft.world.getBlockAt(x, craft.maxY + bufferAmount, z);
/* 117 */         setBlock(fieldBlock, fieldOn);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\Craft_Hyperspace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */