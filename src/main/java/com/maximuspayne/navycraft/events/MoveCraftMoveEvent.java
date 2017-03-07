/*    */ package com.maximuspayne.navycraft.events;
/*    */ 
/*    */ import com.maximuspayne.navycraft.Craft;
/*    */ import org.bukkit.event.Cancellable;
/*    */ import org.bukkit.event.Event;
/*    */ import org.bukkit.event.HandlerList;
/*    */ import org.bukkit.util.Vector;
/*    */ 
/*    */ public class MoveCraftMoveEvent
/*    */   extends Event
/*    */   implements Cancellable
/*    */ {
/*    */   private Vector movement;
/*    */   private final Craft craft;
/*    */   private boolean cancelled;
/*    */   
/*    */   public MoveCraftMoveEvent(Craft craft, int x, int y, int z)
/*    */   {
/* 19 */     this.movement = new Vector(x, y, z);
/* 20 */     this.craft = craft;
/* 21 */     this.cancelled = false;
/*    */   }
/*    */   
/*    */   public Vector getMovement() {
/* 25 */     return this.movement;
/*    */   }
/*    */   
/*    */   public void setMovement(Vector movement) {
/* 29 */     this.movement = movement;
/*    */   }
/*    */   
/*    */   public Craft getCraft() {
/* 33 */     return this.craft;
/*    */   }
/*    */   
/*    */   public boolean isCancelled()
/*    */   {
/* 38 */     return this.cancelled;
/*    */   }
/*    */   
/*    */   public void setCancelled(boolean cancelled)
/*    */   {
/* 43 */     this.cancelled = cancelled;
/*    */   }
/*    */   
/*    */ 
/*    */   public HandlerList getHandlers()
/*    */   {
/* 49 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\events\MoveCraftMoveEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */