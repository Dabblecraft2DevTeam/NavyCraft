/*     */ package com.maximuspayne.navycraft;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import net.minecraft.server.v1_10_R1.EntityHuman;
/*     */ import net.minecraft.server.v1_10_R1.EntityTracker;
/*     */ import net.minecraft.server.v1_10_R1.EntityTrackerEntry;
/*     */ import net.minecraft.server.v1_10_R1.IntHashMap;
/*     */ import net.minecraft.server.v1_10_R1.WorldServer;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
/*     */ import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.Listener;
/*     */ import org.bukkit.event.player.PlayerTeleportEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TeleportFix
/*     */   implements Listener
/*     */ {
/*     */   private Server server;
/*     */   private Plugin plugin;
/*  33 */   private final int TELEPORT_FIX_DELAY = 100;
/*     */   
/*     */   public TeleportFix(Plugin plugin, Server server) {
/*  36 */     this.plugin = plugin;
/*  37 */     this.server = server;
/*     */   }
/*     */   
/*     */   @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
/*     */   public void onPlayerTeleport(PlayerTeleportEvent event)
/*     */   {
/*  43 */     final Player player = event.getPlayer();
/*  44 */     final int visibleDistance = this.server.getViewDistance() * 16;
/*     */     
/*     */ 
/*  47 */     this.server.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/*  51 */         TeleportFix.this.updateEntities(TeleportFix.this.getPlayersWithin(player, visibleDistance));
/*     */         
/*  53 */         System.out.println("Applying fix ... " + visibleDistance);
/*     */       }
/*  55 */     }, 100L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void updateEntities(List<Player> observers)
/*     */   {
/*  62 */     for (Player player : observers) {
/*  63 */       updateEntity(player, observers);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void updateEntity(Entity entity, List<Player> observers)
/*     */   {
/*  70 */     World world = entity.getWorld();
/*  71 */     WorldServer worldServer = ((CraftWorld)world).getHandle();
/*     */     
/*  73 */     EntityTracker tracker = worldServer.tracker;
/*  74 */     EntityTrackerEntry entry = 
/*  75 */       (EntityTrackerEntry)tracker.trackedEntities.get(entity.getEntityId());
/*     */     
/*  77 */     List<EntityHuman> nmsPlayers = getNmsPlayers(observers);
/*     */     
/*     */ 
/*  80 */     entry.trackedPlayers.removeAll(nmsPlayers);
/*  81 */     entry.scanPlayers(nmsPlayers);
/*     */   }
/*     */   
/*     */   private List<EntityHuman> getNmsPlayers(List<Player> players) {
/*  85 */     List<EntityHuman> nsmPlayers = new ArrayList();
/*     */     
/*  87 */     for (Player bukkitPlayer : players) {
/*  88 */       CraftPlayer craftPlayer = (CraftPlayer)bukkitPlayer;
/*  89 */       nsmPlayers.add(craftPlayer.getHandle());
/*     */     }
/*     */     
/*  92 */     return nsmPlayers;
/*     */   }
/*     */   
/*     */   private List<Player> getPlayersWithin(Player player, int distance) {
/*  96 */     List<Player> res = new ArrayList();
/*  97 */     int d2 = distance * distance;
/*     */     
/*  99 */     for (Player p : this.server.getOnlinePlayers()) {
/* 100 */       if ((p.getWorld() == player.getWorld()) && 
/* 101 */         (p.getLocation().distanceSquared(player.getLocation()) <= d2)) {
/* 102 */         res.add(p);
/*     */       }
/*     */     }
/*     */     
/* 106 */     return res;
/*     */   }
/*     */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\TeleportFix.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */