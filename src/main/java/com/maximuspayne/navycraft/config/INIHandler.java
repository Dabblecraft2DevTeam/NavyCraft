/*    */ package com.maximuspayne.navycraft.config;
/*    */ 
/*    */ import com.maximuspayne.navycraft.NavyCraft;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.BufferedWriter;
/*    */ import java.io.File;
/*    */ import java.io.FileReader;
/*    */ import java.io.FileWriter;
/*    */ import java.io.IOException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Set;
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
/*    */ public class INIHandler
/*    */ {
/*    */   public void load(File MCConfig)
/*    */   {
/*    */     try
/*    */     {
/* 35 */       BufferedReader in = new BufferedReader(new FileReader(MCConfig));
/*    */       
/*    */       String line;
/* 38 */       while ((line = in.readLine()) != null) {
/* 39 */         String line = line.trim();
/*    */         
/* 41 */         if (!line.startsWith("#"))
/*    */         {
/*    */ 
/* 44 */           String[] split = line.split("=");
/*    */           
/* 46 */           NavyCraft.instance.configFile.ConfigSettings.put(split[0], split[1]);
/*    */         } }
/* 48 */       in.close();
/*    */     }
/*    */     catch (IOException localIOException) {}
/*    */   }
/*    */   
/*    */   public void save(File MCConfig)
/*    */   {
/*    */     try {
/* 56 */       BufferedWriter bw = new BufferedWriter(new FileWriter(MCConfig));
/*    */       Object[] arrayOfObject;
/* 58 */       int j = (arrayOfObject = NavyCraft.instance.configFile.ConfigSettings.keySet().toArray()).length; for (int i = 0; i < j; i++) { Object configLine = arrayOfObject[i];
/* 59 */         String configKey = (String)configLine;
/* 60 */         bw.write(configKey + "=" + (String)NavyCraft.instance.configFile.ConfigSettings.get(configKey) + System.getProperty("line.separator"));
/*    */       }
/* 62 */       bw.close();
/*    */     }
/*    */     catch (IOException localIOException) {}
/*    */   }
/*    */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\config\INIHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */