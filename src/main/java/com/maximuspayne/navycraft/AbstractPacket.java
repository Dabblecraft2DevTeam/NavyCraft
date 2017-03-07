/*    */ package com.maximuspayne.navycraft;
/*    */ 
/*    */ import com.comphenix.protocol.PacketType;
/*    */ import com.comphenix.protocol.ProtocolLibrary;
/*    */ import com.comphenix.protocol.ProtocolManager;
/*    */ import com.comphenix.protocol.events.PacketContainer;
/*    */ import com.google.common.base.Objects;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import org.bukkit.entity.Player;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractPacket
/*    */ {
/*    */   protected PacketContainer handle;
/*    */   
/*    */   protected AbstractPacket(PacketContainer handle, PacketType type)
/*    */   {
/* 40 */     if (handle == null)
/* 41 */       throw new IllegalArgumentException("Packet handle cannot be NULL.");
/* 42 */     if (!Objects.equal(handle.getType(), type)) {
/* 43 */       throw new IllegalArgumentException(
/* 44 */         handle.getHandle() + " is not a packet of type " + type);
/*    */     }
/* 46 */     this.handle = handle;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public PacketContainer getHandle()
/*    */   {
/* 54 */     return this.handle;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void sendPacket(Player receiver)
/*    */   {
/*    */     try
/*    */     {
/* 64 */       ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, getHandle());
/*    */     } catch (InvocationTargetException e) {
/* 66 */       throw new RuntimeException("Cannot send packet.", e);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void recievePacket(Player sender)
/*    */   {
/*    */     try
/*    */     {
/* 77 */       ProtocolLibrary.getProtocolManager().recieveClientPacket(sender, getHandle());
/*    */     } catch (Exception e) {
/* 79 */       throw new RuntimeException("Cannot recieve packet.", e);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\AbstractPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */