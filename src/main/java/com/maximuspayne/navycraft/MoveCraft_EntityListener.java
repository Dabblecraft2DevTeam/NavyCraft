/*     */ package com.maximuspayne.navycraft;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.entity.Egg;
/*     */ import org.bukkit.entity.Entity;
/*     */ import org.bukkit.entity.LivingEntity;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.entity.Skeleton;
/*     */ import org.bukkit.event.EventHandler;
/*     */ import org.bukkit.event.EventPriority;
/*     */ import org.bukkit.event.entity.CreatureSpawnEvent;
/*     */ import org.bukkit.event.entity.EntityDamageByEntityEvent;
/*     */ import org.bukkit.event.entity.EntityDamageEvent;
/*     */ import org.bukkit.event.entity.EntityExplodeEvent;
/*     */ import org.bukkit.event.entity.EntityTargetEvent;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ 
/*     */ public class MoveCraft_EntityListener implements org.bukkit.event.Listener
/*     */ {
/*     */   private static Plugin plugin;
/*     */   
/*     */   public MoveCraft_EntityListener(Plugin p)
/*     */   {
/*  28 */     plugin = p;
/*     */   }
/*     */   
/*     */   @EventHandler(priority=EventPriority.HIGH)
/*     */   public void onEntityExplode(EntityExplodeEvent event)
/*     */   {
/*  34 */     Entity ent = event.getEntity();
/*  35 */     if ((ent != null) && ((ent instanceof org.bukkit.entity.TNTPrimed)))
/*     */     {
/*  37 */       if (event.getLocation() != null)
/*     */       {
/*  39 */         if (NavyCraft.shotTNTList.containsKey(ent.getUniqueId()))
/*     */         {
/*     */ 
/*  42 */           if ((!structureUpdate(event.getLocation(), (Player)NavyCraft.shotTNTList.get(ent.getUniqueId()))) && 
/*  43 */             (!structureUpdate(event.getLocation().getBlock().getRelative(4, 4, 4).getLocation(), (Player)NavyCraft.shotTNTList.get(ent.getUniqueId()))) && 
/*  44 */             (!structureUpdate(event.getLocation().getBlock().getRelative(-4, -4, -4).getLocation(), (Player)NavyCraft.shotTNTList.get(ent.getUniqueId()))) && 
/*  45 */             (!structureUpdate(event.getLocation().getBlock().getRelative(2, -1, -2).getLocation(), (Player)NavyCraft.shotTNTList.get(ent.getUniqueId()))))
/*  46 */             structureUpdate(event.getLocation().getBlock().getRelative(-2, 1, 2).getLocation(), (Player)NavyCraft.shotTNTList.get(ent.getUniqueId()));
/*  47 */           NavyCraft.shotTNTList.remove(ent.getUniqueId());
/*     */         }
/*     */         else {
/*  50 */           structureUpdate(event.getLocation(), null);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean structureUpdate(Location loc, Player causer)
/*     */   {
/*  59 */     Craft testcraft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
/*  60 */     if (testcraft != null)
/*     */     {
/*  62 */       CraftMover cm = new CraftMover(testcraft, plugin);
/*  63 */       cm.structureUpdate(causer, false);
/*  64 */       return true;
/*     */     }
/*  66 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH)
/*     */   public void onCreatureSpawn(CreatureSpawnEvent event)
/*     */   {
/*  76 */     event.getEntity().getWorld().getName().equalsIgnoreCase("warworld1");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH)
/*     */   public void onEntityTarget(EntityTargetEvent event)
/*     */   {
/*  89 */     if (((event.getEntity() instanceof Skeleton)) && (NavyCraft.aaSkelesList.contains((Skeleton)event.getEntity())))
/*     */     {
/*  91 */       if ((event.getTarget() instanceof Player))
/*     */       {
/*  93 */         Player target = (Player)event.getTarget();
/*  94 */         Craft skeleCraft = Craft.getCraft(target.getLocation().getBlockX(), target.getLocation().getBlockY(), target.getLocation().getBlockZ());
/*  95 */         if ((skeleCraft != null) && (!skeleCraft.crewNames.isEmpty()) && (skeleCraft.crewNames.contains(target.getName())))
/*     */         {
/*  97 */           event.setCancelled(true);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @EventHandler(priority=EventPriority.HIGH)
/*     */   public void onEntityDamage(EntityDamageEvent event)
/*     */   {
/* 109 */     if ((event.getEntity() instanceof Player))
/*     */     {
/* 111 */       Player player = (Player)event.getEntity();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 120 */       for (Periscope p : NavyCraft.allPeriscopes)
/*     */       {
/* 122 */         if (p.user == player)
/*     */         {
/* 124 */           event.setCancelled(true);
/* 125 */           return;
/*     */         }
/*     */       }
/*     */       
/* 129 */       if (event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.SUFFOCATION)
/*     */       {
/* 131 */         Craft c = Craft.getCraft(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
/* 132 */         if (c != null)
/*     */         {
/* 134 */           event.setCancelled(true);
/* 135 */           return;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 140 */       if (player.getWorld().getName().equalsIgnoreCase("warworld2"))
/*     */       {
/* 142 */         if ((event instanceof EntityDamageByEntityEvent))
/*     */         {
/* 144 */           EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent)event;
/* 145 */           if ((event2.getDamager() instanceof Player))
/*     */           {
/* 147 */             Player p = (Player)event2.getDamager();
/*     */             
/* 149 */             if ((NavyCraft.redPlayers.contains(player.getName())) && (NavyCraft.redPlayers.contains(p.getName())))
/*     */             {
/* 151 */               p.sendMessage("That player is on your team.");
/* 152 */               event.setCancelled(true);
/* 153 */               return; }
/* 154 */             if ((NavyCraft.bluePlayers.contains(player.getName())) && (NavyCraft.bluePlayers.contains(p.getName())))
/*     */             {
/* 156 */               p.sendMessage("That player is on your team.");
/* 157 */               event.setCancelled(true);
/* 158 */               return;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 166 */     if ((event instanceof EntityDamageByEntityEvent))
/*     */     {
/*     */ 
/*     */ 
/* 170 */       Entity attacker = ((EntityDamageByEntityEvent)event).getDamager();
/* 171 */       if ((attacker instanceof Egg))
/*     */       {
/* 173 */         if (NavyCraft.explosiveEggsList.contains((Egg)attacker))
/*     */         {
/*     */ 
/* 176 */           event.setDamage(5.0D);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\MoveCraft_EntityListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */