/*    */ package com.maximuspayne.navycraft.database;
/*    */ 
/*    */ import com.avaje.ebean.annotation.CacheStrategy;
/*    */ import com.avaje.ebean.validation.NotNull;
/*    */ import javax.persistence.Entity;
/*    */ import javax.persistence.Id;
/*    */ import javax.persistence.Table;
/*    */ import org.bukkit.Bukkit;
/*    */ import org.bukkit.Server;
/*    */ import org.bukkit.World;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Entity
/*    */ @CacheStrategy
/*    */ @Table(name="minecartowners")
/*    */ public class EBeansHandler
/*    */ {
/*    */   public class MinecartOwner
/*    */   {
/*    */     @Id
/*    */     private int id;
/*    */     @NotNull
/*    */     private String owner;
/*    */     @NotNull
/*    */     private String world;
/*    */     
/*    */     public MinecartOwner()
/*    */     {
/* 31 */       this.owner = "none";
/*    */     }
/*    */     
/*    */     public void setId(int id) {
/* 35 */       this.id = id;
/*    */     }
/*    */     
/*    */     public int getId() {
/* 39 */       return this.id;
/*    */     }
/*    */     
/*    */     public MinecartOwner(String owner) {
/* 43 */       this.owner = owner;
/*    */     }
/*    */     
/*    */     public String getOwner() {
/* 47 */       return this.owner;
/*    */     }
/*    */     
/*    */     public void setOwner(String owner) {
/* 51 */       this.owner = owner;
/*    */     }
/*    */     
/*    */     public void setWorld(String world) {
/* 55 */       this.world = world;
/*    */     }
/*    */     
/*    */     public String getWorld() {
/* 59 */       return this.world;
/*    */     }
/*    */     
/*    */     public World getBukkitWorld() {
/* 63 */       return Bukkit.getServer().getWorld(this.world);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\database\EBeansHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */