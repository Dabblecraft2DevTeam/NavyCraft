/*    */ package com.maximuspayne.navycraft;
/*    */ 
/*    */ import org.bukkit.Location;
/*    */ 
/*    */ public class Periscope
/*    */ {
/*    */   public Location signLoc;
/*    */   public Location scopeLoc;
/*    */   public int periscopeID;
/* 10 */   public boolean raised = true;
/* 11 */   public boolean destroyed = false;
/*    */   public org.bukkit.entity.Player user;
/*    */   
/*    */   public Periscope(Location signLocIn, int idIn) {
/* 15 */     this.signLoc = signLocIn;
/* 16 */     this.periscopeID = idIn;
/*    */   }
/*    */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\Periscope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */