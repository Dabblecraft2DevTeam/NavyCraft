/*    */ package com.maximuspayne.navycraft;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class BlockLoc
/*    */ {
/*    */   int x;
/*    */   
/*    */ 
/*    */ 
/*    */   int y;
/*    */   
/*    */ 
/*    */ 
/*    */   int z;
/*    */   
/*    */ 
/*    */ 
/*    */   public BlockLoc(int x, int y, int z)
/*    */   {
/* 22 */     this.x = x;
/* 23 */     this.y = y;
/* 24 */     this.z = z;
/*    */   }
/*    */   
/*    */   public boolean equals(Object object)
/*    */   {
/* 29 */     if (!(object instanceof BlockLoc)) {
/* 30 */       return false;
/*    */     }
/* 32 */     BlockLoc block = (BlockLoc)object;
/* 33 */     return (this.x == block.x) && (this.y == block.y) && (this.z == block.z);
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 38 */     return Integer.valueOf(this.x).hashCode() >> 13 ^ 
/* 39 */       Integer.valueOf(this.y).hashCode() >> 7 ^ 
/* 40 */       Integer.valueOf(this.z).hashCode();
/*    */   }
/*    */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\BlockLoc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */