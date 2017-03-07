/*     */ package com.maximuspayne.navycraft.plugins;
/*     */ 
/*     */ import com.maximuspayne.navycraft.CraftType;
/*     */ import com.maximuspayne.navycraft.NavyCraft;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.permissions.Permission;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PermissionInterface
/*     */ {
/*     */   public static void setupPermissions()
/*     */   {
/*  19 */     PluginManager pm = NavyCraft.instance.getServer().getPluginManager();
/*     */     
/*  21 */     if (pm != null) {
/*  22 */       pm.addPermission(new Permission("movecraft.periscope.use"));
/*  23 */       pm.addPermission(new Permission("movecraft.aa-gun.use"));
/*  24 */       pm.addPermission(new Permission("movecraft.periscope.create"));
/*  25 */       pm.addPermission(new Permission("movecraft.aa-gun.create"));
/*     */       
/*  27 */       for (CraftType type : CraftType.craftTypes)
/*     */       {
/*  29 */         pm.addPermission(new Permission("movecraft." + type.name + ".release"));
/*  30 */         pm.addPermission(new Permission("movecraft." + type.name + ".info"));
/*  31 */         pm.addPermission(new Permission("movecraft." + type.name + ".takeover"));
/*  32 */         pm.addPermission(new Permission("movecraft." + type.name + ".start"));
/*  33 */         pm.addPermission(new Permission("movecraft." + type.name + ".create"));
/*  34 */         pm.addPermission(new Permission("movecraft." + type.name + ".sink"));
/*  35 */         pm.addPermission(new Permission("movecraft." + type.name + ".remove"));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean CheckPermission(Player player, String command)
/*     */   {
/*  71 */     command = command.replace(" ", ".");
/*  72 */     NavyCraft.instance.DebugMessage("Checking if " + player.getName() + " can " + command, 3);
/*     */     
/*     */ 
/*  75 */     if ((player.hasPermission(command)) || (player.isOp()))
/*     */     {
/*  77 */       NavyCraft.instance.DebugMessage("Player has permissions: " + command, 3);
/*  78 */       NavyCraft.instance.DebugMessage("Player isop: " + 
/*  79 */         player.isOp(), 3);
/*  80 */       return true;
/*     */     }
/*     */     
/*  83 */     player.sendMessage("You do not have permission to perform " + command);
/*  84 */     return false;
/*     */   }
/*     */   
/*     */   public static boolean CheckQuietPermission(Player player, String command)
/*     */   {
/*  89 */     command = command.replace(" ", ".");
/*  90 */     NavyCraft.instance.DebugMessage("Checking if " + player.getName() + " can " + command, 3);
/*     */     
/*     */ 
/*  93 */     if ((player.hasPermission(command)) || (player.isOp()))
/*     */     {
/*  95 */       NavyCraft.instance.DebugMessage("Player has permissions: " + command, 3);
/*  96 */       NavyCraft.instance.DebugMessage("Player isop: " + 
/*  97 */         player.isOp(), 3);
/*  98 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 102 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\plugins\PermissionInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */