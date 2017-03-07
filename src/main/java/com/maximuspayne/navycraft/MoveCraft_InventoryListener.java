/*    */ package com.maximuspayne.navycraft;
/*    */ 
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.EventPriority;
/*    */ import org.bukkit.event.inventory.CraftItemEvent;
/*    */ import org.bukkit.inventory.ItemStack;
/*    */ import org.bukkit.inventory.Recipe;
/*    */ import org.bukkit.plugin.Plugin;
/*    */ 
/*    */ public class MoveCraft_InventoryListener implements org.bukkit.event.Listener
/*    */ {
/*    */   private static Plugin plugin;
/*    */   
/*    */   public MoveCraft_InventoryListener(Plugin p)
/*    */   {
/* 17 */     plugin = p;
/*    */   }
/*    */   
/*    */   @org.bukkit.event.EventHandler(priority=EventPriority.HIGH)
/*    */   public void onCraftItem(CraftItemEvent event)
/*    */   {
/* 23 */     if ((event.getRecipe().getResult().getType() == Material.STONE_SWORD) || 
/* 24 */       (event.getRecipe().getResult().getType() == Material.IRON_SWORD) || 
/* 25 */       (event.getRecipe().getResult().getType() == Material.GOLD_SWORD) || 
/* 26 */       (event.getRecipe().getResult().getType() == Material.DIAMOND_SWORD) || 
/* 27 */       (event.getRecipe().getResult().getType() == Material.LEATHER_BOOTS) || 
/* 28 */       (event.getRecipe().getResult().getType() == Material.LEATHER_LEGGINGS) || 
/* 29 */       (event.getRecipe().getResult().getType() == Material.LEATHER_CHESTPLATE) || 
/* 30 */       (event.getRecipe().getResult().getType() == Material.LEATHER_HELMET) || 
/* 31 */       (event.getRecipe().getResult().getType() == Material.CHAINMAIL_BOOTS) || 
/* 32 */       (event.getRecipe().getResult().getType() == Material.CHAINMAIL_LEGGINGS) || 
/* 33 */       (event.getRecipe().getResult().getType() == Material.CHAINMAIL_CHESTPLATE) || 
/* 34 */       (event.getRecipe().getResult().getType() == Material.CHAINMAIL_HELMET) || 
/* 35 */       (event.getRecipe().getResult().getType() == Material.GOLD_BOOTS) || 
/* 36 */       (event.getRecipe().getResult().getType() == Material.GOLD_LEGGINGS) || 
/* 37 */       (event.getRecipe().getResult().getType() == Material.GOLD_CHESTPLATE) || 
/* 38 */       (event.getRecipe().getResult().getType() == Material.GOLD_HELMET) || 
/* 39 */       (event.getRecipe().getResult().getType() == Material.IRON_BOOTS) || 
/* 40 */       (event.getRecipe().getResult().getType() == Material.IRON_LEGGINGS) || 
/* 41 */       (event.getRecipe().getResult().getType() == Material.IRON_CHESTPLATE) || 
/* 42 */       (event.getRecipe().getResult().getType() == Material.IRON_HELMET) || 
/* 43 */       (event.getRecipe().getResult().getType() == Material.DIAMOND_BOOTS) || 
/* 44 */       (event.getRecipe().getResult().getType() == Material.DIAMOND_LEGGINGS) || 
/* 45 */       (event.getRecipe().getResult().getType() == Material.DIAMOND_CHESTPLATE) || 
/* 46 */       (event.getRecipe().getResult().getType() == Material.DIAMOND_HELMET))
/*    */     {
/* 48 */       if (event.getWhoClicked().getType() == org.bukkit.entity.EntityType.PLAYER)
/*    */       {
/* 50 */         Player p = (Player)event.getWhoClicked();
/* 51 */         p.sendMessage("Sorry, you are not allowed to craft this item. Purchase it from a Safe Dock instead.");
/*    */       }
/* 53 */       event.setCancelled(true);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\MoveCraft_InventoryListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */