/*    */ package com.maximuspayne.navycraft;
/*    */ 
/*    */ import java.io.BufferedWriter;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import java.util.HashMap;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class Benchmarker
/*    */ {
/* 12 */   public HashMap<String, Long> breaks = new HashMap();
/* 13 */   public long lastTime = 0L;
/*    */   
/*    */   public Benchmarker() {
/* 16 */     this.lastTime = System.currentTimeMillis();
/*    */   }
/*    */   
/*    */   public boolean addBreak(String name) {
/* 20 */     if (this.breaks.containsKey(name)) {
/* 21 */       return false;
/*    */     }
/*    */     
/* 24 */     long nowTime = System.currentTimeMillis();
/* 25 */     long breakTime = nowTime - this.lastTime;
/* 26 */     this.lastTime = nowTime;
/*    */     
/* 28 */     this.breaks.put(name, Long.valueOf(breakTime));
/* 29 */     return true;
/*    */   }
/*    */   
/*    */   public void addBreak() {
/* 33 */     String breakName = "break " + this.breaks.size();
/*    */     
/* 35 */     addBreak(breakName);
/*    */   }
/*    */   
/*    */   public void echoToConsole() { Object[] arrayOfObject;
/* 39 */     int j = (arrayOfObject = this.breaks.keySet().toArray()).length; for (int i = 0; i < j; i++) { Object breakPoints = arrayOfObject[i];
/* 40 */       String breakNames = (String)breakPoints;
/* 41 */       System.out.println(breakNames + "=" + this.breaks.get(breakNames));
/*    */     }
/*    */   }
/*    */   
/*    */   public void writeToFile(String fileName) {
/* 46 */     if (fileName == "") {
/* 47 */       fileName = "MoveCraft-BenchMark.txt";
/*    */     }
/*    */     
/* 50 */     File benchmarkFile = new File(NavyCraft.instance.getDataFolder(), fileName);
/* 51 */     if (!benchmarkFile.exists()) {
/* 52 */       return;
/*    */     }
/*    */     try
/*    */     {
/* 56 */       BufferedWriter bw = new BufferedWriter(new java.io.FileWriter(benchmarkFile));
/*    */       Object[] arrayOfObject;
/* 58 */       int j = (arrayOfObject = this.breaks.keySet().toArray()).length; for (int i = 0; i < j; i++) { Object breakPoints = arrayOfObject[i];
/* 59 */         String breakNames = (String)breakPoints;
/* 60 */         bw.write(breakNames + "=" + this.breaks.get(breakNames) + System.getProperty("line.separator"));
/*    */       }
/* 62 */       bw.close();
/*    */     }
/*    */     catch (IOException localIOException) {}
/*    */   }
/*    */   
/*    */ 
/*    */   public void echoSysInfo()
/*    */   {
/* 70 */     System.out.println("Available processors (cores): " + 
/* 71 */       Runtime.getRuntime().availableProcessors());
/*    */     
/*    */ 
/* 74 */     System.out.println("Free memory (bytes): " + 
/* 75 */       Runtime.getRuntime().freeMemory());
/*    */     
/*    */ 
/* 78 */     long maxMemory = Runtime.getRuntime().maxMemory();
/*    */     
/* 80 */     System.out.println("Maximum memory (bytes): " + (
/* 81 */       maxMemory == Long.MAX_VALUE ? "no limit" : Long.valueOf(maxMemory)));
/*    */     
/*    */ 
/* 84 */     System.out.println("Total memory (bytes): " + 
/* 85 */       Runtime.getRuntime().totalMemory());
/*    */     
/* 87 */     System.getProperties().list(System.out);
/*    */     
/*    */ 
/*    */     try
/*    */     {
/* 92 */       Runtime.getRuntime().exec("notepad.exe");
/*    */     } catch (IOException e) {
/* 94 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\Benchmarker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */