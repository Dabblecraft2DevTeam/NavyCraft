/*     */ package com.maximuspayne.navycraft;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.plugin.Plugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ 
/*     */ public class MoveCraft_Timer
/*     */ {
/*     */   Plugin plugin;
/*     */   Timer timer;
/*     */   Craft craft;
/*     */   Player player;
/*  18 */   public static HashMap<Player, MoveCraft_Timer> playerTimers = new HashMap();
/*  19 */   public static HashMap<Craft, MoveCraft_Timer> takeoverTimers = new HashMap();
/*  20 */   public static HashMap<Craft, MoveCraft_Timer> abandonTimers = new HashMap();
/*     */   
/*     */   public MoveCraft_Timer(Plugin plug, int seconds, Craft vehicle, Player p, String state, boolean forward)
/*     */   {
/*  24 */     this.plugin = plug;
/*  25 */     this.craft = vehicle;
/*  26 */     this.player = p;
/*  27 */     this.timer = new Timer();
/*  28 */     if (state.equals("engineCheck")) {
/*  29 */       this.timer.scheduleAtFixedRate(new EngineTask(), 1000L, 1000L);
/*  30 */     } else if (state.equals("engineCheck")) {
/*  31 */       this.timer.schedule(new AutoMoveTask(forward), 1000L);
/*  32 */     } else if (state.equals("abandonCheck")) {
/*  33 */       this.timer.scheduleAtFixedRate(new ReleaseTask(), seconds * 1000, 60000L);
/*  34 */     } else if (state.equals("takeoverCheck")) {
/*  35 */       this.timer.scheduleAtFixedRate(new TakeoverTask(), seconds * 1000, 60000L);
/*  36 */     } else if (state.equals("takeoverCaptainCheck")) {
/*  37 */       this.timer.scheduleAtFixedRate(new TakeoverCaptainTask(), seconds * 1000, 60000L);
/*     */     }
/*     */   }
/*     */   
/*     */   public void SetState(String newState) {}
/*     */   
/*     */   public void Destroy()
/*     */   {
/*  45 */     this.timer.cancel();
/*  46 */     this.craft = null;
/*     */   }
/*     */   
/*     */   class EngineTask extends TimerTask { EngineTask() {}
/*     */     
/*  51 */     public void run() { if (MoveCraft_Timer.this.craft == null) {
/*  52 */         MoveCraft_Timer.this.timer.cancel();
/*     */       } else {
/*  54 */         MoveCraft_Timer.this.craft.engineTick();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   class AutoMoveTask extends TimerTask {
/*  60 */     boolean MovingForward = false;
/*     */     
/*     */     public void run() {
/*  63 */       MoveCraft_Timer.this.craft.WayPointTravel(this.MovingForward);
/*  64 */       MoveCraft_Timer.this.timer.schedule(new AutoMoveTask(MoveCraft_Timer.this, this.MovingForward), 1000L);
/*     */     }
/*     */     
/*     */     public AutoMoveTask(boolean Forward) {
/*  68 */       this.MovingForward = Forward;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   class ReleaseTask
/*     */     extends TimerTask
/*     */   {
/*     */     ReleaseTask() {}
/*     */     
/*     */     public void run()
/*     */     {
/*  80 */       if ((MoveCraft_Timer.this.craft != null) && (MoveCraft_Timer.this.craft.isNameOnBoard.containsKey(MoveCraft_Timer.this.player.getName())) && 
/*  81 */         (!((Boolean)MoveCraft_Timer.this.craft.isNameOnBoard.get(MoveCraft_Timer.this.player.getName())).booleanValue())) {
/*  82 */         MoveCraft_Timer.this.releaseCraftSync();
/*     */       }
/*     */       
/*  85 */       MoveCraft_Timer.this.timer.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void releaseCraftSync()
/*     */   {
/*  94 */     this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
/*     */     {
/*     */       public void run() {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   class TakeoverTask
/*     */     extends TimerTask
/*     */   {
/*     */     TakeoverTask() {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void run()
/*     */     {
/* 114 */       MoveCraft_Timer.this.takeoverCraftSync();
/*     */       
/*     */ 
/* 117 */       MoveCraft_Timer.this.timer.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void takeoverCraftSync()
/*     */   {
/* 126 */     this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 131 */         if ((MoveCraft_Timer.this.craft.abandoned) && (MoveCraft_Timer.this.player != null) && (MoveCraft_Timer.this.player.isOnline()) && (MoveCraft_Timer.this.craft.isOnCraft(MoveCraft_Timer.this.player, false)))
/*     */         {
/* 133 */           MoveCraft_Timer.this.craft.releaseCraft();
/* 134 */           MoveCraft_Timer.this.player.sendMessage(ChatColor.YELLOW + "Vehicle released! Take command.");
/*     */         }
/*     */         else {
/* 137 */           MoveCraft_Timer.this.player.sendMessage(ChatColor.YELLOW + "Takeover failed.");
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   class TakeoverCaptainTask
/*     */     extends TimerTask
/*     */   {
/*     */     TakeoverCaptainTask() {}
/*     */     
/*     */ 
/*     */ 
/*     */     public void run()
/*     */     {
/* 155 */       MoveCraft_Timer.this.takeoverCaptainCraftSync();
/*     */       
/*     */ 
/* 158 */       MoveCraft_Timer.this.timer.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void takeoverCaptainCraftSync()
/*     */   {
/* 167 */     this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 173 */         if ((MoveCraft_Timer.this.craft.captainAbandoned) && (MoveCraft_Timer.this.player != null) && (MoveCraft_Timer.this.player.isOnline()) && (MoveCraft_Timer.this.craft.isOnCraft(MoveCraft_Timer.this.player, false)))
/*     */         {
/* 175 */           MoveCraft_Timer.this.craft.releaseCraft();
/* 176 */           MoveCraft_Timer.this.player.sendMessage(ChatColor.YELLOW + "Vehicle released! Take command.");
/*     */         }
/*     */         else {
/* 179 */           MoveCraft_Timer.this.player.sendMessage(ChatColor.YELLOW + "Takeover failed.");
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\MoveCraft_Timer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */