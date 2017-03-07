/*     */ package com.maximuspayne.navycraft;
/*     */ 
/*     */ import com.maximuspayne.navycraft.config.ConfigFile;
/*     */ import com.maximuspayne.navycraft.plugins.PermissionInterface;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CraftType
/*     */ {
/*  22 */   public String name = "";
/*  23 */   public String driveCommand = "pilot";
/*     */   
/*  25 */   int minBlocks = 9;
/*  26 */   int maxBlocks = 500;
/*  27 */   int maxSpeed = 4;
/*     */   
/*  29 */   int flyBlockType = 0;
/*  30 */   int digBlockId = 0;
/*  31 */   int engineBlockId = 0;
/*  32 */   double flyBlockPercent = 0.0D;
/*  33 */   double digBlockPercent = 0.0D;
/*  34 */   int digBlockDurability = 0;
/*  35 */   int fuelItemId = 0;
/*  36 */   int fuelConsumptionMultiplier = 1;
/*     */   
/*  38 */   int remoteControllerItem = 0;
/*     */   
/*  40 */   boolean canFly = false;
/*  41 */   boolean canNavigate = false;
/*  42 */   boolean canDive = false;
/*  43 */   boolean iceBreaker = false;
/*  44 */   boolean bomber = false;
/*  45 */   boolean canDig = false;
/*  46 */   boolean obeysGravity = false;
/*  47 */   boolean isTerrestrial = false;
/*  48 */   boolean requiresRails = false;
/*     */   
/*     */ 
/*  51 */   boolean doesCruise = false;
/*  52 */   boolean canZamboni = false;
/*  53 */   int maxEngineSpeed = 4;
/*  54 */   int maxForwardGear = 3;
/*  55 */   int maxReverseGear = -2;
/*  56 */   int turnRadius = 4;
/*  57 */   int maxSurfaceSpeed = 4;
/*  58 */   int maxSubmergedSpeed = 3;
/*     */   
/*  60 */   String sayOnControl = "You control the craft";
/*  61 */   String sayOnRelease = "You release the craft";
/*     */   
/*  63 */   short[] structureBlocks = null;
/*  64 */   short[] extendedBlocks = null;
/*  65 */   short[] restrictedBlocks = null;
/*  66 */   short[] forbiddenBlocks = null;
/*     */   
/*  68 */   public static ArrayList<CraftType> craftTypes = new ArrayList();
/*     */   
/*  70 */   boolean listenItem = true;
/*  71 */   boolean listenAnimation; boolean listenMovement = false;
/*     */   
/*     */   public CraftType(String name) {
/*  74 */     this.name = name;
/*     */     
/*  76 */     String[] bob = NavyCraft.instance.ConfigSetting("StructureBlocks").split(",");
/*  77 */     short[] juan = new short[bob.length + 1];
/*  78 */     for (int i = 0; i < bob.length; i++)
/*  79 */       juan[i] = Short.parseShort(bob[i]);
/*  80 */     this.structureBlocks = juan;
/*     */     
/*  82 */     if (NavyCraft.instance.ConfigSetting("ForbiddenBlocks") != "null") {
/*  83 */       bob = NavyCraft.instance.ConfigSetting("ForbiddenBlocks").split(",");
/*  84 */       juan = new short[bob.length];
/*  85 */       for (int i = 0; i < bob.length; i++) {
/*     */         try {
/*  87 */           juan[i] = Short.parseShort(bob[i]);
/*     */         }
/*     */         catch (Exception localException) {}
/*     */       }
/*     */       
/*  92 */       if ((juan != null) && (juan.length > 0) && (juan[0] != 0)) {
/*  93 */         this.forbiddenBlocks = juan;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static CraftType getCraftType(String name) {
/*  99 */     for (CraftType type : craftTypes) {
/* 100 */       if (type.name.equalsIgnoreCase(name)) {
/* 101 */         return type;
/*     */       }
/*     */     }
/* 104 */     return null;
/*     */   }
/*     */   
/*     */   public String getCommand() {
/* 108 */     return "/" + this.name.toLowerCase();
/*     */   }
/*     */   
/*     */   public Boolean canUse(Player player) {
/* 112 */     if (PermissionInterface.CheckPermission(player, "movecraft." + this.name.toLowerCase() + "." + this.driveCommand)) {
/* 113 */       return Boolean.valueOf(true);
/*     */     }
/* 115 */     return Boolean.valueOf(false);
/*     */   }
/*     */   
/*     */ 
/*     */   private static void loadDefaultCraftTypes()
/*     */   {
/* 121 */     if (getCraftType("boat") == null)
/* 122 */       craftTypes.add(getDefaultCraftType("boat"));
/* 123 */     if (getCraftType("ship") == null) {
/* 124 */       craftTypes.add(getDefaultCraftType("ship"));
/*     */     }
/*     */     
/* 127 */     if (getCraftType("bomber") == null)
/* 128 */       craftTypes.add(getDefaultCraftType("bomber"));
/* 129 */     if (getCraftType("aircraft") == null)
/* 130 */       craftTypes.add(getDefaultCraftType("aircraft"));
/* 131 */     if (getCraftType("airship") == null)
/* 132 */       craftTypes.add(getDefaultCraftType("airship"));
/* 133 */     if (getCraftType("UFO") == null) {
/* 134 */       craftTypes.add(getDefaultCraftType("UFO"));
/*     */     }
/*     */     
/* 137 */     if (getCraftType("submarine") == null)
/* 138 */       craftTypes.add(getDefaultCraftType("submarine"));
/* 139 */     if (getCraftType("drill") == null)
/* 140 */       craftTypes.add(getDefaultCraftType("drill"));
/* 141 */     if (getCraftType("car") == null)
/* 142 */       craftTypes.add(getDefaultCraftType("car"));
/* 143 */     if (getCraftType("train") == null) {
/* 144 */       craftTypes.add(getDefaultCraftType("train"));
/*     */     }
/*     */   }
/*     */   
/*     */   private static CraftType getDefaultCraftType(String name) {
/* 149 */     CraftType craftType = new CraftType(name);
/*     */     
/* 151 */     if (name.equalsIgnoreCase("template"))
/*     */     {
/* 153 */       setAttribute(
/* 154 */         craftType, 
/* 155 */         "structureBlocks", 
/* 156 */         "4,5,17,19,20,35,41,42,43,44,45,46,47,48,49,50,53,57,65,67,68,69,75,76,77,85,87,88,89");
/*     */     }
/* 158 */     else if (name.equalsIgnoreCase("boat")) {
/* 159 */       craftType.driveCommand = "sail";
/* 160 */       craftType.canNavigate = true;
/* 161 */       craftType.minBlocks = 9;
/* 162 */       craftType.maxBlocks = 500;
/* 163 */       craftType.maxSpeed = 4;
/* 164 */       craftType.sayOnControl = "You're on a boat !";
/* 165 */       craftType.sayOnRelease = "You release the helm";
/*     */     }
/* 167 */     else if (name.equalsIgnoreCase("ship")) {
/* 168 */       craftType.driveCommand = "sail";
/* 169 */       craftType.canNavigate = true;
/* 170 */       craftType.minBlocks = 50;
/* 171 */       craftType.maxBlocks = 1000;
/* 172 */       craftType.maxSpeed = 6;
/* 173 */       craftType.sayOnControl = "You're on a ship !";
/* 174 */       craftType.sayOnRelease = "You release the helm";
/*     */     }
/* 176 */     else if (name.equalsIgnoreCase("icebreaker")) {
/* 177 */       craftType.driveCommand = "sail";
/* 178 */       craftType.canNavigate = true;
/* 179 */       craftType.minBlocks = 50;
/* 180 */       craftType.maxBlocks = 1000;
/* 181 */       craftType.maxSpeed = 4;
/* 182 */       craftType.iceBreaker = true;
/* 183 */       craftType.sayOnControl = "Let's break some ice !";
/* 184 */       craftType.sayOnRelease = "You release the helm";
/*     */     }
/* 186 */     else if (name.equalsIgnoreCase("drill")) {
/* 187 */       craftType.driveCommand = "drive";
/* 188 */       craftType.canNavigate = true;
/* 189 */       craftType.minBlocks = 20;
/* 190 */       craftType.maxBlocks = 1000;
/* 191 */       craftType.maxSpeed = 1;
/* 192 */       craftType.canDig = true;
/* 193 */       craftType.canDive = true;
/* 194 */       craftType.digBlockId = 57;
/*     */       
/* 196 */       craftType.sayOnControl = "Armageddon, but down.";
/* 197 */       craftType.sayOnRelease = (name + " controls released.");
/*     */     }
/* 199 */     else if (name.equalsIgnoreCase("aircraft")) {
/* 200 */       craftType.driveCommand = "pilot";
/* 201 */       craftType.canFly = true;
/* 202 */       craftType.minBlocks = 9;
/* 203 */       craftType.maxBlocks = 1000;
/* 204 */       craftType.maxSpeed = 6;
/* 205 */       craftType.sayOnControl = "You're on an aircraft !";
/* 206 */       craftType.sayOnRelease = "You release the joystick";
/*     */     }
/* 208 */     else if (name.equalsIgnoreCase("bomber")) {
/* 209 */       craftType.driveCommand = "pilot";
/* 210 */       craftType.canFly = true;
/* 211 */       craftType.minBlocks = 20;
/* 212 */       craftType.maxBlocks = 1000;
/* 213 */       craftType.maxSpeed = 4;
/* 214 */       craftType.bomber = true;
/* 215 */       craftType.sayOnControl = "You're on a bomber !";
/* 216 */       craftType.sayOnRelease = "You release the joystick";
/*     */     }
/* 218 */     else if (name.equalsIgnoreCase("airship")) {
/* 219 */       craftType.driveCommand = "pilot";
/* 220 */       craftType.canFly = true;
/* 221 */       craftType.minBlocks = 9;
/* 222 */       craftType.maxBlocks = 1000;
/* 223 */       craftType.maxSpeed = 6;
/* 224 */       craftType.flyBlockType = 35;
/* 225 */       craftType.flyBlockPercent = 60.0D;
/* 226 */       craftType.sayOnControl = "You're on an airship !";
/* 227 */       craftType.sayOnRelease = "You release the control panel";
/*     */     }
/* 229 */     else if (name.equalsIgnoreCase("UFO")) {
/* 230 */       craftType.driveCommand = "pilot";
/* 231 */       craftType.canFly = true;
/* 232 */       craftType.minBlocks = 9;
/* 233 */       craftType.maxBlocks = 1000;
/* 234 */       craftType.maxSpeed = 9;
/* 235 */       craftType.flyBlockType = 89;
/* 236 */       craftType.flyBlockPercent = 4.0D;
/* 237 */       craftType.sayOnControl = "You're on a UFO !";
/* 238 */       craftType.sayOnRelease = "You release the control panel";
/*     */     }
/* 240 */     else if (name.equalsIgnoreCase("USO")) {
/* 241 */       craftType.driveCommand = "pilot";
/* 242 */       craftType.canFly = true;
/* 243 */       craftType.canDive = true;
/* 244 */       craftType.minBlocks = 9;
/* 245 */       craftType.maxBlocks = 1000;
/* 246 */       craftType.maxSpeed = 9;
/* 247 */       craftType.flyBlockType = 89;
/* 248 */       craftType.flyBlockPercent = 4.0D;
/* 249 */       craftType.sayOnControl = "You're on a USO !";
/* 250 */       craftType.sayOnRelease = "You release the control panel";
/*     */     }
/* 252 */     else if (name.equalsIgnoreCase("submarine")) {
/* 253 */       craftType.driveCommand = "dive";
/* 254 */       craftType.canDive = true;
/* 255 */       craftType.minBlocks = 10;
/* 256 */       craftType.maxBlocks = 1000;
/* 257 */       craftType.maxSpeed = 3;
/* 258 */       craftType.sayOnControl = "You're into a submarine !";
/* 259 */       craftType.sayOnRelease = "You release the helm";
/*     */     }
/* 261 */     else if (name.equalsIgnoreCase("car")) {
/* 262 */       craftType.driveCommand = "drive";
/* 263 */       craftType.canNavigate = true;
/* 264 */       craftType.isTerrestrial = true;
/* 265 */       craftType.obeysGravity = true;
/* 266 */       craftType.minBlocks = 10;
/* 267 */       craftType.maxBlocks = 1000;
/* 268 */       craftType.maxSpeed = 3;
/* 269 */       craftType.sayOnControl = "You blew a .07! You're good to go!";
/* 270 */       craftType.sayOnRelease = "Remember where you parked!";
/*     */     }
/* 272 */     else if (name.equalsIgnoreCase("train")) {
/* 273 */       craftType.driveCommand = "conduct";
/* 274 */       craftType.canNavigate = true;
/* 275 */       craftType.isTerrestrial = true;
/* 276 */       craftType.requiresRails = true;
/* 277 */       craftType.obeysGravity = true;
/* 278 */       craftType.minBlocks = 10;
/* 279 */       craftType.maxBlocks = 1000;
/* 280 */       craftType.maxSpeed = 3;
/* 281 */       craftType.sayOnControl = "All aboard! Ha ha ha ha ha ha haaaa!";
/* 282 */       craftType.sayOnRelease = "Last stop.";
/*     */     }
/*     */     
/* 285 */     return craftType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void setAttribute(CraftType craftType, String attribute, String value)
/*     */   {
/* 292 */     if (attribute.equalsIgnoreCase("driveCommand")) {
/* 293 */       craftType.driveCommand = value;
/* 294 */     } else if (attribute.equalsIgnoreCase("minBlocks")) {
/* 295 */       craftType.minBlocks = Integer.parseInt(value);
/* 296 */     } else if (attribute.equalsIgnoreCase("maxBlocks")) {
/* 297 */       craftType.maxBlocks = Integer.parseInt(value);
/* 298 */     } else if (attribute.equalsIgnoreCase("maxSpeed")) {
/* 299 */       craftType.maxSpeed = Integer.parseInt(value);
/* 300 */     } else if (attribute.equalsIgnoreCase("flyBlockType")) {
/* 301 */       craftType.flyBlockType = Integer.parseInt(value);
/* 302 */     } else if (attribute.equalsIgnoreCase("flyBlockPercent")) {
/* 303 */       craftType.flyBlockPercent = Double.parseDouble(value);
/* 304 */     } else if (attribute.equalsIgnoreCase("digBlockId")) {
/* 305 */       craftType.digBlockId = Integer.parseInt(value);
/* 306 */     } else if (attribute.equalsIgnoreCase("digBlockDurability")) {
/* 307 */       craftType.digBlockDurability = Integer.parseInt(value);
/* 308 */     } else if (attribute.equalsIgnoreCase("fuelItemId")) {
/* 309 */       craftType.fuelItemId = Integer.parseInt(value);
/* 310 */     } else if (attribute.equalsIgnoreCase("fuelConsumptionMultiplier")) {
/* 311 */       craftType.fuelConsumptionMultiplier = Integer.parseInt(value);
/* 312 */     } else if (attribute.equalsIgnoreCase("canNavigate")) {
/* 313 */       craftType.canNavigate = Boolean.parseBoolean(value);
/* 314 */     } else if (attribute.equalsIgnoreCase("isTerrestrial")) {
/* 315 */       craftType.isTerrestrial = Boolean.parseBoolean(value);
/* 316 */     } else if (attribute.equalsIgnoreCase("requiresRails")) {
/* 317 */       craftType.requiresRails = Boolean.parseBoolean(value);
/* 318 */     } else if (attribute.equalsIgnoreCase("canFly")) {
/* 319 */       craftType.canFly = Boolean.parseBoolean(value);
/* 320 */     } else if (attribute.equalsIgnoreCase("canDive")) {
/* 321 */       craftType.canDive = Boolean.parseBoolean(value);
/* 322 */     } else if (attribute.equalsIgnoreCase("canDig")) {
/* 323 */       craftType.canDig = Boolean.parseBoolean(value);
/* 324 */     } else if (attribute.equalsIgnoreCase("canZamboni")) {
/* 325 */       craftType.canZamboni = Boolean.parseBoolean(value);
/* 326 */     } else if (attribute.equalsIgnoreCase("obeysGravity")) {
/* 327 */       craftType.obeysGravity = Boolean.parseBoolean(value);
/*     */ 
/*     */     }
/* 330 */     else if (attribute.equalsIgnoreCase("doesCruise")) {
/* 331 */       craftType.doesCruise = Boolean.parseBoolean(value);
/*     */     }
/* 333 */     else if (attribute.equalsIgnoreCase("maxEngineSpeed"))
/*     */     {
/* 335 */       craftType.maxEngineSpeed = Integer.parseInt(value);
/* 336 */       craftType.maxSurfaceSpeed = Integer.parseInt(value);
/*     */     }
/* 338 */     else if (attribute.equalsIgnoreCase("maxForwardGear")) {
/* 339 */       craftType.maxForwardGear = Integer.parseInt(value);
/* 340 */     } else if (attribute.equalsIgnoreCase("maxReverseGear")) {
/* 341 */       craftType.maxReverseGear = Integer.parseInt(value);
/* 342 */     } else if (attribute.equalsIgnoreCase("turnRadius")) {
/* 343 */       craftType.turnRadius = Integer.parseInt(value);
/* 344 */     } else if (attribute.equalsIgnoreCase("maxSubmergedSpeed")) {
/* 345 */       craftType.maxSubmergedSpeed = Integer.parseInt(value);
/*     */ 
/*     */ 
/*     */     }
/* 349 */     else if (attribute.equalsIgnoreCase("bomber")) {
/* 350 */       craftType.bomber = Boolean.parseBoolean(value);
/* 351 */     } else if (attribute.equalsIgnoreCase("sayOnControl")) {
/* 352 */       craftType.sayOnControl = value;
/* 353 */     } else if (attribute.equalsIgnoreCase("sayOnRelease")) {
/* 354 */       craftType.sayOnRelease = value;
/* 355 */     } else if (attribute.equalsIgnoreCase("remoteControllerItem")) {
/* 356 */       craftType.remoteControllerItem = Integer.parseInt(value);
/* 357 */     } else if (attribute.equalsIgnoreCase("listenItem")) {
/* 358 */       craftType.listenItem = Boolean.parseBoolean(value);
/* 359 */     } else if (attribute.equalsIgnoreCase("listenAnimation")) {
/* 360 */       craftType.listenAnimation = Boolean.parseBoolean(value);
/* 361 */     } else if (attribute.equalsIgnoreCase("listenMovement")) {
/* 362 */       craftType.listenMovement = Boolean.parseBoolean(value);
/* 363 */     } else if (attribute.equalsIgnoreCase("engineBlockId")) {
/* 364 */       craftType.engineBlockId = Integer.parseInt(value);
/* 365 */       if (BlocksInfo.getCardinals(craftType.engineBlockId) == null) {
/* 366 */         System.out.println("Invalid engine block specified in craft type " + 
/* 367 */           craftType.name + ". Use something that has a face, like a furnace or a dispenser.");
/* 368 */         craftType.engineBlockId = 0;
/*     */       }
/*     */     } else { int j;
/* 371 */       if (attribute.equalsIgnoreCase("structureBlocks")) {
/* 372 */         String[] split = value.split(",");
/* 373 */         craftType.structureBlocks = new short[split.length];
/* 374 */         int i = 0;
/* 375 */         String[] arrayOfString1; j = (arrayOfString1 = split).length; for (int i = 0; i < j; i++) { String blockId = arrayOfString1[i];
/* 376 */           craftType.structureBlocks[i] = Short.parseShort(blockId);
/* 377 */           i++; } } else { Object localObject;
/*     */         int k;
/* 379 */         if (attribute.equalsIgnoreCase("restrictedBlocks")) {
/* 380 */           if (craftType.structureBlocks == null) {
/* 381 */             return;
/*     */           }
/* 383 */           ArrayList<Short> restrictedBlocks = new ArrayList();
/* 384 */           ArrayList<Short> newStructureBlocks = new ArrayList();
/*     */           
/* 386 */           String[] split = value.split(",");
/*     */           
/* 388 */           k = (localObject = split).length; for (j = 0; j < k; j++) { String s = localObject[j];
/*     */             try
/*     */             {
/* 391 */               restrictedBlocks.add(Short.valueOf(Short.parseShort(s)));
/*     */             }
/*     */             catch (NumberFormatException ex) {
/* 394 */               System.out.println("Tried to remove invalid block ID " + s + 
/* 395 */                 " from structureblocks of craft type " + craftType.name);
/*     */             }
/*     */           }
/* 398 */           k = (localObject = craftType.structureBlocks).length; for (j = 0; j < k; j++) { Short i = Short.valueOf(localObject[j]);
/* 399 */             if (!restrictedBlocks.contains(i))
/* 400 */               newStructureBlocks.add(i);
/*     */           }
/* 402 */           Short[] nsb = new Short[newStructureBlocks.size()];
/*     */           
/* 404 */           newStructureBlocks.toArray(nsb);
/*     */ 
/*     */ 
/*     */         }
/* 408 */         else if (attribute.equalsIgnoreCase("extendedBlocks")) {
/* 409 */           if (craftType.structureBlocks == null) {
/* 410 */             return;
/*     */           }
/* 412 */           String[] split = value.split(",");
/* 413 */           short[] newStructureBlocks = new short[craftType.structureBlocks.length + split.length];
/*     */           
/* 415 */           for (int i = 0; i < craftType.structureBlocks.length; i++) {
/* 416 */             newStructureBlocks[i] = craftType.structureBlocks[i];
/*     */           }
/*     */           
/* 419 */           int i = 0;
/* 420 */           k = (localObject = split).length; for (j = 0; j < k; j++) { String s = localObject[j];
/*     */             try
/*     */             {
/* 423 */               newStructureBlocks[(craftType.structureBlocks.length + i)] = Short.parseShort(s);
/*     */             }
/*     */             catch (NumberFormatException ex) {
/* 426 */               System.out.println("Tried to add invalid block ID " + s + 
/* 427 */                 " to structureblocks of craft type " + craftType.name);
/*     */             }
/*     */           }
/* 430 */           craftType.structureBlocks = newStructureBlocks;
/* 431 */         } else if (attribute.equalsIgnoreCase("forbiddenBlocks")) {
/* 432 */           String[] split = value.split(",");
/* 433 */           craftType.forbiddenBlocks = new short[split.length];
/* 434 */           for (int i = 0; i < split.length; i++)
/* 435 */             craftType.forbiddenBlocks[i] = Short.parseShort(split[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/* 441 */   public static void saveType(File dir, CraftType craftType, boolean force) { File craftFile = new File(dir + File.separator + 
/* 442 */       craftType.name + ".txt");
/*     */     
/* 444 */     if (!craftFile.exists()) {
/*     */       try {
/* 446 */         craftFile.createNewFile();
/*     */       } catch (IOException ex) {
/* 448 */         return;
/*     */       }
/*     */       
/*     */     } else {
/* 452 */       return;
/*     */     }
/*     */     try {
/* 455 */       BufferedWriter writer = new BufferedWriter(
/* 456 */         new FileWriter(craftFile));
/*     */       
/* 458 */       writeAttribute(writer, "driveCommand", craftType.driveCommand, 
/* 459 */         force);
/* 460 */       writeAttribute(writer, "minBlocks", craftType.minBlocks, true);
/* 461 */       writeAttribute(writer, "maxBlocks", craftType.maxBlocks, force);
/*     */       
/*     */ 
/* 464 */       if (craftType.structureBlocks != null) {
/* 465 */         String line = "structureBlocks=";
/* 466 */         short[] arrayOfShort; int j = (arrayOfShort = craftType.structureBlocks).length; for (int i = 0; i < j; i++) { short blockId = arrayOfShort[i];
/*     */           
/* 468 */           line = line + blockId + ",";
/*     */         }
/*     */         
/* 471 */         writer.write(line.substring(0, line.length() - 1));
/* 472 */         writer.newLine();
/*     */       }
/*     */       
/* 475 */       writeAttribute(writer, "maxSpeed", craftType.maxSpeed, force);
/* 476 */       writeAttribute(writer, "flyBlockType", craftType.flyBlockType, force);
/* 477 */       writeAttribute(writer, "flyBlockPercent", craftType.flyBlockPercent, force);
/* 478 */       writeAttribute(writer, "digBlockId", craftType.digBlockId, force);
/* 479 */       writeAttribute(writer, "digBlockDurability", craftType.digBlockDurability, force);
/* 480 */       writeAttribute(writer, "fuelItemId", craftType.fuelItemId, force);
/* 481 */       writeAttribute(writer, "fuelConsumptionMultiplier", craftType.fuelConsumptionMultiplier, force);
/* 482 */       writeAttribute(writer, "canNavigate", craftType.canNavigate, force);
/* 483 */       writeAttribute(writer, "isTerrestrial", craftType.isTerrestrial, force);
/* 484 */       writeAttribute(writer, "requiresRails", craftType.requiresRails, force);
/* 485 */       writeAttribute(writer, "canFly", craftType.canFly, force);
/* 486 */       writeAttribute(writer, "canDive", craftType.canDive, force);
/* 487 */       writeAttribute(writer, "canDig", craftType.canDig, force);
/* 488 */       writeAttribute(writer, "obeysGravity", craftType.obeysGravity, force);
/*     */       
/* 490 */       writeAttribute(writer, "bomber", craftType.bomber, force);
/* 491 */       writeAttribute(writer, "sayOnControl", craftType.sayOnControl, force);
/* 492 */       writeAttribute(writer, "sayOnRelease", craftType.sayOnRelease, force);
/*     */       
/* 494 */       writer.close();
/*     */     }
/*     */     catch (IOException localIOException1) {}
/*     */   }
/*     */   
/*     */   public static void saveTypes(File dir)
/*     */   {
/* 501 */     for (CraftType craftType : craftTypes) {
/* 502 */       saveType(dir, craftType, false);
/*     */     }
/*     */     
/*     */ 
/* 506 */     saveType(dir, getDefaultCraftType("template"), true);
/*     */   }
/*     */   
/*     */   private static void writeAttribute(BufferedWriter writer, String attribute, String value, boolean force)
/*     */     throws IOException
/*     */   {
/* 512 */     if (((value == null) || (value.trim().equals(""))) && (!force))
/* 513 */       return;
/* 514 */     writer.write(attribute + "=" + value);
/* 515 */     writer.newLine();
/*     */   }
/*     */   
/*     */   private static void writeAttribute(BufferedWriter writer, String attribute, int value, boolean force) throws IOException
/*     */   {
/* 520 */     if ((value == 0) && (!force))
/* 521 */       return;
/* 522 */     writer.write(attribute + "=" + value);
/* 523 */     writer.newLine();
/*     */   }
/*     */   
/*     */   private static void writeAttribute(BufferedWriter writer, String attribute, double value, boolean force) throws IOException
/*     */   {
/* 528 */     if ((value == 0.0D) && (!force))
/* 529 */       return;
/* 530 */     writer.write(attribute + "=" + value);
/* 531 */     writer.newLine();
/*     */   }
/*     */   
/*     */   private static void writeAttribute(BufferedWriter writer, String attribute, boolean value, boolean force) throws IOException
/*     */   {
/* 536 */     if ((!value) && (!force))
/* 537 */       return;
/* 538 */     writer.write(attribute + "=" + value);
/* 539 */     writer.newLine();
/*     */   }
/*     */   
/*     */   public static void loadTypes(File dir) {
/* 543 */     File[] craftTypesList = dir.listFiles();
/* 544 */     craftTypes.clear();
/*     */     File[] arrayOfFile1;
/* 546 */     int j = (arrayOfFile1 = craftTypesList).length; for (int i = 0; i < j; i++) { File craftFile = arrayOfFile1[i];
/*     */       
/* 548 */       if ((craftFile.isFile()) && (craftFile.getName().endsWith(".txt")))
/*     */       {
/* 550 */         String craftName = craftFile.getName().split("\\.")[0];
/*     */         
/*     */ 
/* 553 */         if (!craftName.equalsIgnoreCase("template"))
/*     */         {
/*     */ 
/* 556 */           CraftType craftType = new CraftType(craftName);
/*     */           
/* 558 */           craftType.remoteControllerItem = Integer.parseInt(NavyCraft.instance.ConfigSetting("UniversalRemoteId"));
/* 559 */           craftType.engineBlockId = Integer.parseInt(NavyCraft.instance.ConfigSetting("EngineBlockId"));
/*     */           try
/*     */           {
/* 562 */             BufferedReader reader = new BufferedReader(new FileReader(
/* 563 */               craftFile));
/*     */             
/*     */             String line;
/* 566 */             while ((line = reader.readLine()) != null)
/*     */             {
/*     */               String line;
/* 569 */               String[] split = line.split("=");
/*     */               
/* 571 */               if (split.length >= 2) {
/* 572 */                 setAttribute(craftType, split[0], split[1]);
/*     */               }
/*     */             }
/* 575 */             reader.close();
/*     */           }
/*     */           catch (IOException localIOException) {}
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 582 */           if (BlocksInfo.getCardinals(craftType.engineBlockId) == null) {
/* 583 */             craftType.engineBlockId = -1;
/* 584 */             System.out.println("Warning, craft type " + craftType.name + " has an invalid engine block ID. " + 
/* 585 */               "Please use a block which has a facing direction (default is furnace, ID 61).");
/*     */           }
/*     */           
/* 588 */           craftTypes.add(craftType);
/*     */         }
/*     */       }
/*     */     }
/* 592 */     if (((String)NavyCraft.instance.configFile.ConfigSettings.get("WriteDefaultCraft")).equalsIgnoreCase("true")) {
/* 593 */       loadDefaultCraftTypes();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\CraftType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */