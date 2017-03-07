/*     */ package com.maximuspayne.navycraft.config;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Set;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ public class ConfigFile
/*     */ {
/*  10 */   public String filename = "navycraft.xml";
/*  11 */   public HashMap<String, String> ConfigSettings = new HashMap();
/*  12 */   public HashMap<String, String> ConfigComments = new HashMap();
/*     */   
/*     */   public ConfigFile() {
/*  15 */     this.ConfigSettings.put("CraftReleaseDelay", "15");
/*  16 */     this.ConfigSettings.put("UniversalRemoteId", "294");
/*     */     
/*  18 */     this.ConfigSettings.put("RequireOp", "true");
/*  19 */     this.ConfigSettings.put("StructureBlocks", 
/*  20 */       "4,5,14,15,16,17,19,20,21,22,23,25,26,27,28,30,35,41,42,43,44,46,47,48,49,50,51,53,54,55,56,57,58,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,80,81,82,84,85,86,87,88,89,91,92,93,94,96,98,101,102,106,107,109,112,113,114,118,121,122,123,124,125,126,129,131,132,133,134,135,136,139,143,144,147,148,149,150,151,152,153,154,156,157,158,159,170,171,172,173,155,0");
/*  21 */     this.ConfigSettings.put("allowHoles", "false");
/*  22 */     this.ConfigSettings.put("EnableAsyncMovement", "false");
/*  23 */     this.ConfigSettings.put("ExperimentalMovementMultiplier", "1.0");
/*  24 */     this.ConfigSettings.put("TryNudge", "false");
/*  25 */     this.ConfigSettings.put("LogLevel", "0");
/*  26 */     this.ConfigSettings.put("RequireRemote", "false");
/*  27 */     this.ConfigSettings.put("EngineBlockId", "61");
/*  28 */     this.ConfigSettings.put("HungryHungryDrill", "false");
/*  29 */     this.ConfigSettings.put("WriteDefaultCraft", "true");
/*  30 */     this.ConfigSettings.put("ForbiddenBlocks", "29,33,34,36,52,90,95,97,116,119,120,130,137,138,145,146");
/*  31 */     this.ConfigSettings.put("DisableHyperSpaceField", "false");
/*     */     
/*  33 */     this.ConfigSettings.put("Ship1_StartX", "16");
/*  34 */     this.ConfigSettings.put("Ship1_EndX", "1286");
/*  35 */     this.ConfigSettings.put("Ship1_WidthX", "14");
/*  36 */     this.ConfigSettings.put("Ship1_StartZ", "-462");
/*  37 */     this.ConfigSettings.put("Ship1_EndZ", "-18");
/*  38 */     this.ConfigSettings.put("Ship1_WidthZ", "37");
/*  39 */     this.ConfigSettings.put("Ship2_StartX", "16");
/*  40 */     this.ConfigSettings.put("Ship2_EndX", "1296");
/*  41 */     this.ConfigSettings.put("Ship2_WidthX", "10");
/*  42 */     this.ConfigSettings.put("Ship2_StartZ", "33");
/*  43 */     this.ConfigSettings.put("Ship2_EndZ", "241");
/*  44 */     this.ConfigSettings.put("Ship2_WidthZ", "52");
/*  45 */     this.ConfigSettings.put("Ship3_StartX", "-1091");
/*  46 */     this.ConfigSettings.put("Ship3_EndX", "-35");
/*  47 */     this.ConfigSettings.put("Ship3_WidthX", "12");
/*  48 */     this.ConfigSettings.put("Ship3_StartZ", "60");
/*  49 */     this.ConfigSettings.put("Ship3_EndZ", "297");
/*  50 */     this.ConfigSettings.put("Ship3_WidthZ", "79");
/*  51 */     this.ConfigSettings.put("Ship4_StartX", "-1085");
/*  52 */     this.ConfigSettings.put("Ship4_EndX", "-41");
/*  53 */     this.ConfigSettings.put("Ship4_WidthX", "18");
/*  54 */     this.ConfigSettings.put("Ship4_StartZ", "-210");
/*  55 */     this.ConfigSettings.put("Ship4_EndZ", "-18");
/*  56 */     this.ConfigSettings.put("Ship4_WidthZ", "64");
/*  57 */     this.ConfigSettings.put("Ship5_StartX", "16");
/*  58 */     this.ConfigSettings.put("Ship5_EndX", "1270");
/*  59 */     this.ConfigSettings.put("Ship5_WidthX", "22");
/*  60 */     this.ConfigSettings.put("Ship5_StartZ", "349");
/*  61 */     this.ConfigSettings.put("Ship5_EndZ", "454");
/*  62 */     this.ConfigSettings.put("Ship5_WidthZ", "105");
/*  63 */     this.ConfigSettings.put("Hangar1_StartX", "-1067");
/*  64 */     this.ConfigSettings.put("Hangar1_EndX", "-31");
/*  65 */     this.ConfigSettings.put("Hangar1_WidthX", "23");
/*  66 */     this.ConfigSettings.put("Hangar1_StartZ", "-828");
/*  67 */     this.ConfigSettings.put("Hangar1_EndZ", "-278");
/*  68 */     this.ConfigSettings.put("Hangar1_WidthZ", "25");
/*  69 */     this.ConfigSettings.put("Hangar2_StartX", "0");
/*  70 */     this.ConfigSettings.put("Hangar2_EndX", "100");
/*  71 */     this.ConfigSettings.put("Hangar2_WidthX", "10");
/*  72 */     this.ConfigSettings.put("Hangar2_StartZ", "0");
/*  73 */     this.ConfigSettings.put("Hangar2_EndZ", "100");
/*  74 */     this.ConfigSettings.put("Hangar2_WidthZ", "10");
/*  75 */     this.ConfigSettings.put("Tank1_StartX", "22");
/*  76 */     this.ConfigSettings.put("Tank1_EndX", "832");
/*  77 */     this.ConfigSettings.put("Tank1_WidthX", "18");
/*  78 */     this.ConfigSettings.put("Tank1_StartZ", "-932");
/*  79 */     this.ConfigSettings.put("Tank1_EndZ", "-500");
/*  80 */     this.ConfigSettings.put("Tank1_WidthZ", "24");
/*     */     
/*  82 */     this.ConfigComments.put("CraftReleaseDelay", "<Number:15> The amount of time between when a user exists a craft and when the craft automatically releases.");
/*     */     
/*  84 */     this.ConfigComments.put("UniversalRemoteId", "<Number:294> The item ID of the remote control that works on all vehicles.");
/*  85 */     this.ConfigComments.put("RequireOp", "<TRUE/false> Only users with Bukkit-given 'op' can use craft.");
/*  86 */     this.ConfigComments.put("StructureBlocks", "The blocks that define the structure of the craft. It is recommended not to use blocks like stone, dirt, and grass.");
/*     */     
/*  88 */     this.ConfigComments.put("allowHoles", "<true/FALSE> Are holes allowed in craft (for submarines, drills, etc.)");
/*  89 */     this.ConfigComments.put("EnableAsyncMovement", "<true/FALSE> Puts craft movement in asyncronous threading. This is experimental, and might not work. There could be a preformance increase from it if it does, though.");
/*     */     
/*  91 */     this.ConfigComments.put("TryNudge", "<true/FALSE> 'Nudge' the player rather than moving them. Currently broken.");
/*  92 */     this.ConfigComments.put("LogLevel", "<Number:1> The amount of output to display to the console. 1 means nothing beyond what Bukkit normally does, 2 means suspected errors, 3 means errors and notifications, and 4 means suspected errors, notifications, and status messages.");
/*     */     
/*     */ 
/*  95 */     this.ConfigComments.put("RequireRemote", "<true/FALSE> The vehicle only moves if the remote item is in the player's hand.");
/*  96 */     this.ConfigComments.put("EngineBlockId", "<block ID:61> The ID of the block to use as engines for craft types which do not  explicitly define their own individual engine type in their craft type file.");
/*     */     
/*  98 */     this.ConfigComments.put("HungryHungryDrill", "<true/FALSE> Any craft types which can drill will eat blocks rather than creating items.");
/*     */     
/* 100 */     this.ConfigComments.put("WriteDefaultCraft", "Whether or not to create the default craft type files on plugin enable.");
/* 101 */     this.ConfigComments.put("ForbiddenBlocks", "Blocks that prevent craft from being created if they are anywhere in the craft leave 'null' for none.");
/*     */     
/* 103 */     this.ConfigComments.put("DisableHyperSpaceField", "Prevents the hyperspace field blocks from appearing.");
/*     */     
/* 105 */     com.maximuspayne.navycraft.NavyCraft.instance.configFile = this;
/*     */     
/* 107 */     XMLHandler.load();
/*     */     
/* 109 */     XMLHandler.save(); }
/*     */   
/*     */   public void ListSettings(Player player) { Object[] arrayOfObject;
/*     */     int j;
/* 113 */     int i; if (player != null) {
/* 114 */       player.sendMessage("Movecraft config settings:");
/* 115 */       j = (arrayOfObject = this.ConfigSettings.keySet().toArray()).length; for (i = 0; i < j; i++) { Object configLine = arrayOfObject[i];
/* 116 */         String configKey = (String)configLine;
/* 117 */         player.sendMessage(configKey + "=" + (String)this.ConfigSettings.get(configKey));
/*     */       }
/*     */     }
/*     */     else {
/* 121 */       System.out.println("Movecraft config settings:");
/* 122 */       j = (arrayOfObject = this.ConfigSettings.keySet().toArray()).length; for (i = 0; i < j; i++) { Object configLine = arrayOfObject[i];
/* 123 */         String configKey = (String)configLine;
/* 124 */         System.out.println(configKey + "=" + (String)this.ConfigSettings.get(configKey));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String GetSetting(String setting) {
/* 130 */     return (String)this.ConfigSettings.get(setting);
/*     */   }
/*     */   
/*     */   public void ChangeSetting(String settingName, String settingValue) {}
/*     */   
/*     */   public void SaveSetting(String settingName) {}
/*     */   
/*     */   public void CheckSetting(String settingName, String defaultValue) {}
/*     */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\config\ConfigFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */