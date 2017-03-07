/*    */ package com.maximuspayne.navycraft.events;
/*    */ 
/*    */ import com.maximuspayne.navycraft.Craft;
/*    */ import org.bukkit.event.Cancellable;
/*    */ import org.bukkit.event.Event;
/*    */ import org.bukkit.event.HandlerList;
/*    */ 
/*    */ public class MoveCraftTurnEvent
/*    */   extends Event implements Cancellable
/*    */ {
/*    */   private int degrees;
/*    */   private boolean cancelled;
/*    */   private final Craft craft;
/*    */   
/*    */   public MoveCraftTurnEvent(Craft craft, int degrees)
/*    */   {
/* 17 */     this.craft = craft;
/* 18 */     setDegrees(degrees);
/* 19 */     this.cancelled = false;
/*    */   }
/*    */   
/*    */   public boolean isCancelled()
/*    */   {
/* 24 */     return this.cancelled;
/*    */   }
/*    */   
/*    */   public void setCancelled(boolean cancelled)
/*    */   {
/* 29 */     this.cancelled = cancelled;
/*    */   }
/*    */   
/*    */   public void setDegrees(int degrees) {
/* 33 */     this.degrees = degrees;
/*    */   }
/*    */   
/*    */   public int getDegrees() {
/* 37 */     return this.degrees;
/*    */   }
/*    */   
/*    */   public Craft getCraft() {
/* 41 */     return this.craft;
/*    */   }
/*    */   
/*    */ 
/*    */   public HandlerList getHandlers()
/*    */   {
/* 47 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\events\MoveCraftTurnEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */