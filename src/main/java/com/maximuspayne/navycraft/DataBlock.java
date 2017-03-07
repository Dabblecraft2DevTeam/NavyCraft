/*    */ package com.maximuspayne.navycraft;
/*    */ 
/*    */ import org.bukkit.inventory.ItemStack;
/*    */ 
/*    */ public class DataBlock {
/*    */   int id;
/*    */   int x;
/*    */   int y;
/*    */   int z;
/*    */   int data;
/* 11 */   public ItemStack[] items = new ItemStack[27];
/* 12 */   public String[] signLines = new String[4];
/*    */   
/*    */   DataBlock(int id, int x, int y, int z, int data) {
/* 15 */     this.id = id;
/* 16 */     this.x = x;
/* 17 */     this.y = y;
/* 18 */     this.z = z;
/* 19 */     this.data = data;
/*    */   }
/*    */   
/*    */   public boolean locationMatches(int locX, int locY, int locZ) {
/* 23 */     if ((locX == this.x) && (locY == this.y) && (locZ == this.z)) {
/* 24 */       return true;
/*    */     }
/* 26 */     return false;
/*    */   }
/*    */   
/*    */   public void setItem(int slot, ItemStack origItem)
/*    */   {
/* 31 */     if (slot >= 27)
/* 32 */       return;
/* 33 */     this.items[slot] = new ItemStack(origItem.getTypeId());
/* 34 */     this.items[slot].setAmount(origItem.getAmount());
/* 35 */     this.items[slot].setData(origItem.getData());
/* 36 */     this.items[slot].setDurability(origItem.getDurability());
/*    */   }
/*    */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\DataBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */