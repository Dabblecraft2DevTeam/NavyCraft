/*     */ package com.maximuspayne.navycraft;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import org.bukkit.Material;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BlocksInfo
/*     */ {
/*  12 */   public static BlockInfo[] blocks = new BlockInfo['ÿ'];
/*     */   
/*     */ 
/*     */   public static void loadBlocksInfo()
/*     */   {
/*  17 */     blocks[0] = new BlockInfo(0, "air", false, false, false, -1, 0, false, null);
/*  18 */     blocks[1] = new BlockInfo(1, "smoothstone", false, false, false, 4, 1, false, null);
/*  19 */     blocks[2] = new BlockInfo(2, "grass", false, false, false, 3, 1, false, null);
/*  20 */     blocks[3] = new BlockInfo(3, "dirt", true, false, false, 3, 1, false, null);
/*  21 */     blocks[4] = new BlockInfo(4, "cobblestone", false, false, false, false, null);
/*  22 */     blocks[5] = new BlockInfo(5, "wood", true, false, false, false, null);
/*  23 */     blocks[6] = new BlockInfo(6, "sapling", true, false, false, false, null);
/*  24 */     blocks[7] = new BlockInfo(7, "adminium", false, false, false, false, null);
/*  25 */     blocks[8] = new BlockInfo(8, "water", false, false, false, -1, 0, false, null);
/*  26 */     blocks[9] = new BlockInfo(9, "water", false, false, false, -1, 0, false, null);
/*  27 */     blocks[10] = new BlockInfo(10, "lava", true, false, false, -1, 0, false, null);
/*  28 */     blocks[11] = new BlockInfo(11, "lava", true, false, false, -1, 0, false, null);
/*  29 */     blocks[12] = new BlockInfo(12, "sand", false, false, false, false, null);
/*  30 */     blocks[13] = new BlockInfo(13, "gravel", false, false, false, false, null);
/*  31 */     blocks[14] = new BlockInfo(14, "gold ore", false, false, false, false, null);
/*  32 */     blocks[15] = new BlockInfo(15, "iron ore", false, false, false, false, null);
/*  33 */     blocks[16] = new BlockInfo(16, "charcoal", false, false, false, 263, 1, false, null);
/*  34 */     blocks[17] = new BlockInfo(17, "trunk", true, false, false, false, null);
/*  35 */     blocks[18] = new BlockInfo(18, "leaves", true, false, false, -1, 0, false, null);
/*  36 */     blocks[19] = new BlockInfo(19, "sponge", false, false, false, false, null);
/*  37 */     blocks[20] = new BlockInfo(20, "glass", false, false, false, -1, 0, false, null);
/*  38 */     blocks[21] = new BlockInfo(21, "lapis ore", false, false, false, 21, 0, false, null);
/*  39 */     blocks[22] = new BlockInfo(22, "lapis block", false, false, false, 22, 0, false, null);
/*  40 */     blocks[23] = new BlockInfo(23, "dispenser", true, false, true, new byte[] { 4, 2, 5, 3 }, null);
/*  41 */     blocks[24] = new BlockInfo(24, "sandstone", true, false, false, false, null);
/*  42 */     blocks[25] = new BlockInfo(25, "note", true, false, false, false, null);
/*  43 */     blocks[26] = new BlockInfo(26, "bed", true, true, false, 355, 1, false, null);
/*  44 */     blocks[27] = new BlockInfo(27, "power rail", true, true, false, false, null);
/*  45 */     blocks[28] = new BlockInfo(28, "detector rail", true, true, false, false, null);
/*  46 */     blocks[29] = new BlockInfo(29, "sticky piston", true, true, true, new byte[] { 2, 5, 3, 4 }, null);
/*  47 */     blocks[30] = new BlockInfo(30, "web", false, false, false, false, null);
/*  48 */     blocks[31] = new BlockInfo(31, "tall grass", true, true, false, true, null);
/*  49 */     blocks[32] = new BlockInfo(32, "dead bush", true, true, false, true, null);
/*  50 */     blocks[33] = new BlockInfo(33, "piston", true, true, true, new byte[] { 2, 5, 3, 4 }, null);
/*  51 */     blocks[34] = new BlockInfo(34, "piston head", false, false, false, new byte[] { 2, 5, 3, 4 }, null);
/*  52 */     blocks[35] = new BlockInfo(35, "wool", true, false, false, 35, 1, false, null);
/*  53 */     blocks[36] = new BlockInfo(36, "piston extended", false, false, false, false, null);
/*  54 */     blocks[37] = new BlockInfo(37, "yellow flower", false, true, false, true, null);
/*  55 */     blocks[38] = new BlockInfo(38, "red flower", false, true, false, true, null);
/*  56 */     blocks[39] = new BlockInfo(39, "brown mushroom", false, true, false, true, null);
/*  57 */     blocks[40] = new BlockInfo(40, "red mushroom", false, true, false, true, null);
/*  58 */     blocks[41] = new BlockInfo(41, "gold block", false, false, false, 266, 9, false, null);
/*  59 */     blocks[42] = new BlockInfo(42, "iron block", false, false, false, 265, 9, false, null);
/*  60 */     blocks[43] = new BlockInfo(43, "double steps", true, false, false, 44, 2, false, null);
/*  61 */     blocks[44] = new BlockInfo(44, "step", true, false, false, false, null);
/*  62 */     blocks[45] = new BlockInfo(45, "brick", false, false, false, 336, 4, false, null);
/*  63 */     blocks[46] = new BlockInfo(46, "TNT", false, false, false, false, null);
/*  64 */     blocks[47] = new BlockInfo(47, "library", false, false, false, false, null);
/*  65 */     blocks[48] = new BlockInfo(48, "mossy cobblestone", false, false, false, false, null);
/*  66 */     blocks[49] = new BlockInfo(49, "obsidian", false, false, false, false, null);
/*  67 */     blocks[50] = new BlockInfo(50, "torch", true, true, false, false, null);
/*  68 */     blocks[51] = new BlockInfo(51, "fire", true, true, false, -1, 0, false, null);
/*  69 */     blocks[52] = new BlockInfo(52, "spawner", true, false, false, false, null);
/*  70 */     blocks[53] = new BlockInfo(53, "wooden stair", true, false, false, false, null);
/*  71 */     blocks[54] = new BlockInfo(54, "chest", true, false, true, false, null);
/*  72 */     blocks[55] = new BlockInfo(55, "redstone dust", true, true, false, 331, 1, false, null);
/*  73 */     blocks[56] = new BlockInfo(56, "diamond", false, false, false, 264, 1, false, null);
/*  74 */     blocks[57] = new BlockInfo(57, "diamond block", false, false, false, 264, 9, false, null);
/*  75 */     blocks[58] = new BlockInfo(58, "workbench", false, false, false, false, null);
/*  76 */     blocks[59] = new BlockInfo(59, "seed", true, true, false, 295, 1, false, null);
/*  77 */     blocks[60] = new BlockInfo(60, "field", true, false, false, 3, 1, false, null);
/*  78 */     blocks[61] = new BlockInfo(61, "furnace", false, true, 4, 8, new byte[] { 4, 2, 5, 3 }, null);
/*  79 */     blocks[62] = new BlockInfo(62, "furnace", false, true, 4, 8, new byte[] { 4, 2, 5, 3 }, null);
/*  80 */     blocks[63] = new BlockInfo(63, "sign", false, true, true, 323, 1, false, null);
/*  81 */     blocks[64] = new BlockInfo(64, "wooden door", true, true, false, 5, 3, false, null);
/*  82 */     blocks[65] = new BlockInfo(65, "ladder", true, true, false, false, null);
/*  83 */     blocks[66] = new BlockInfo(66, "rail", true, true, false, false, null);
/*  84 */     blocks[67] = new BlockInfo(67, "cobblestone stair", true, false, false, false, null);
/*  85 */     blocks[68] = new BlockInfo(68, "sign", false, true, true, 323, 1, false, null);
/*  86 */     blocks[69] = new BlockInfo(69, "lever", true, true, false, false, null);
/*  87 */     blocks[70] = new BlockInfo(70, "pressure plate", true, true, false, false, null);
/*  88 */     blocks[71] = new BlockInfo(71, "steel door", true, true, false, 265, 3, false, null);
/*  89 */     blocks[72] = new BlockInfo(72, "wooden pressure plate", true, true, false, false, null);
/*  90 */     blocks[73] = new BlockInfo(73, "redstone ore", false, false, false, 331, 4, false, null);
/*  91 */     blocks[74] = new BlockInfo(74, "redstone ore", false, false, false, 331, 4, false, null);
/*  92 */     blocks[75] = new BlockInfo(75, "redstone torch", true, true, false, false, null);
/*  93 */     blocks[76] = new BlockInfo(76, "redstone torch", true, true, false, false, null);
/*  94 */     blocks[77] = new BlockInfo(77, "stone button", true, true, false, false, null);
/*  95 */     blocks[78] = new BlockInfo(78, "snow", true, true, false, 332, 1, true, null);
/*  96 */     blocks[79] = new BlockInfo(79, "ice", false, false, false, false, null);
/*  97 */     blocks[80] = new BlockInfo(80, "snow block", false, false, false, false, null);
/*  98 */     blocks[81] = new BlockInfo(81, "cacti", true, true, false, false, null);
/*  99 */     blocks[82] = new BlockInfo(82, "clay", false, true, false, false, null);
/* 100 */     blocks[83] = new BlockInfo(83, "reed", true, true, false, 338, 1, false, null);
/* 101 */     blocks[84] = new BlockInfo(84, "jukebox", true, false, false, false, null);
/* 102 */     blocks[85] = new BlockInfo(85, "fence", false, false, false, false, null);
/* 103 */     blocks[86] = new BlockInfo(86, "pumpkin", true, false, false, new byte[] { 3, 0, 1, 2 }, null);
/* 104 */     blocks[87] = new BlockInfo(87, "hellstone", false, false, false, false, null);
/* 105 */     blocks[88] = new BlockInfo(88, "mud", false, false, false, false, null);
/* 106 */     blocks[89] = new BlockInfo(89, "lightstone", false, false, false, false, null);
/* 107 */     blocks[90] = new BlockInfo(90, "portal", true, true, false, false, null);
/* 108 */     blocks[91] = new BlockInfo(91, "pumpkin", true, false, false, new byte[] { 3, 0, 1, 2 }, null);
/* 109 */     blocks[92] = new BlockInfo(92, "cake", true, false, false, false, null);
/* 110 */     blocks[93] = new BlockInfo(93, "repeater", true, true, false, new byte[] { 3, 0, 2, 1 }, null);
/* 111 */     blocks[94] = new BlockInfo(94, "repeater", true, true, false, new byte[] { 3, 0, 2, 1 }, null);
/* 112 */     blocks[96] = new BlockInfo(96, "trapdoor", true, true, false, false, null);
/* 113 */     blocks[97] = new BlockInfo(97, "hidden silverfish", true, false, false, false, null);
/* 114 */     blocks[98] = new BlockInfo(98, "stone brick", true, false, false, false, null);
/* 115 */     blocks[99] = new BlockInfo(99, "huge brown mushroom", true, false, false, false, null);
/* 116 */     blocks[100] = new BlockInfo(100, "huge red mushroom", true, false, false, false, null);
/* 117 */     blocks[101] = new BlockInfo(101, "iron bars", false, false, false, false, null);
/* 118 */     blocks[102] = new BlockInfo(102, "glass pane", false, false, false, false, null);
/* 119 */     blocks[103] = new BlockInfo(103, "melon", false, false, false, false, null);
/* 120 */     blocks[104] = new BlockInfo(104, "pumpkin stem", true, false, false, false, null);
/* 121 */     blocks[105] = new BlockInfo(105, "melon stem", true, true, false, false, null);
/* 122 */     blocks[106] = new BlockInfo(106, "vines", true, true, false, false, null);
/* 123 */     blocks[107] = new BlockInfo(107, "fence gate", true, true, false, new byte[] { 3, 0, 1, 2 }, null);
/* 124 */     blocks[108] = new BlockInfo(108, "brick stairs", true, false, false, false, null);
/* 125 */     blocks[109] = new BlockInfo(109, "stone brick stairs", true, false, false, false, null);
/* 126 */     blocks[110] = new BlockInfo(110, "mycelium", false, false, false, false, null);
/* 127 */     blocks[111] = new BlockInfo(111, "lilypad", false, true, false, false, null);
/* 128 */     blocks[112] = new BlockInfo(112, "netherbrick", false, false, false, false, null);
/* 129 */     blocks[113] = new BlockInfo(113, "nether brick fence", false, false, false, false, null);
/* 130 */     blocks[114] = new BlockInfo(114, "nether brick stairs", true, false, false, false, null);
/* 131 */     blocks[115] = new BlockInfo(115, "nether wart", true, true, false, false, null);
/* 132 */     blocks[116] = new BlockInfo(116, "enchantment table", true, false, true, false, null);
/* 133 */     blocks[117] = new BlockInfo(117, "brewing stand", true, true, true, false, null);
/* 134 */     blocks[118] = new BlockInfo(118, "cauldron", true, true, false, false, null);
/* 135 */     blocks[119] = new BlockInfo(119, "end portal", false, true, true, false, null);
/* 136 */     blocks[120] = new BlockInfo(120, "end portal frame", true, false, false, new byte[] { 3, 0, 1, 2 }, null);
/* 137 */     blocks[121] = new BlockInfo(121, "end stone", false, false, false, false, null);
/* 138 */     blocks[122] = new BlockInfo(122, "dragon egg", false, true, false, false, null);
/* 139 */     blocks[123] = new BlockInfo(123, "redstone lamp off", false, false, false, false, null);
/* 140 */     blocks[124] = new BlockInfo(124, "redstone lamp on", false, false, false, false, null);
/* 141 */     blocks[125] = new BlockInfo(125, "wooden double slab", true, false, false, 126, 2, false, null);
/* 142 */     blocks[126] = new BlockInfo(126, "wooden slab", true, false, false, false, null);
/* 143 */     blocks[127] = new BlockInfo(127, "cocoa plant", true, true, false, false, null);
/* 144 */     blocks[''] = new BlockInfo(128, "sandstone stairs", true, false, false, false, null);
/* 145 */     blocks[''] = new BlockInfo(129, "emerald ore", false, false, false, false, null);
/* 146 */     blocks[''] = new BlockInfo(130, "ender chest", true, false, true, false, null);
/* 147 */     blocks[''] = new BlockInfo(131, "tripwire hook", true, true, true, false, null);
/* 148 */     blocks[''] = new BlockInfo(132, "tripwire", true, true, true, false, null);
/* 149 */     blocks[''] = new BlockInfo(133, "emerald block", false, false, false, false, null);
/* 150 */     blocks[''] = new BlockInfo(134, "spruce wood stairs", true, false, false, false, null);
/* 151 */     blocks[''] = new BlockInfo(135, "birch wood stairs", true, false, false, false, null);
/* 152 */     blocks[''] = new BlockInfo(136, "jungle wood stairs", true, false, false, false, null);
/* 153 */     blocks[''] = new BlockInfo(137, "command block", true, false, true, false, null);
/* 154 */     blocks[''] = new BlockInfo(138, "beacon block", true, false, true, false, null);
/* 155 */     blocks[''] = new BlockInfo(139, "cobblestone wall", true, false, false, false, null);
/* 156 */     blocks[''] = new BlockInfo(140, "flower pot", true, false, true, false, null);
/* 157 */     blocks[''] = new BlockInfo(141, "carrots", true, true, false, false, null);
/* 158 */     blocks[''] = new BlockInfo(142, "potatoes", true, true, false, false, null);
/* 159 */     blocks[''] = new BlockInfo(143, "wood button", true, true, false, false, null);
/* 160 */     blocks[''] = new BlockInfo(144, "head", true, true, false, false, null);
/* 161 */     blocks[''] = new BlockInfo(145, "anvil", true, true, false, false, null);
/* 162 */     blocks[''] = new BlockInfo(146, "trapped chest", true, false, true, false, null);
/* 163 */     blocks[''] = new BlockInfo(147, "weighted light pressure plate", true, true, false, false, null);
/* 164 */     blocks[''] = new BlockInfo(148, "weighted heavy pressure plate", true, true, false, false, null);
/* 165 */     blocks[''] = new BlockInfo(149, "comparator on", true, true, false, new byte[] { 3, 0, 2, 1 }, null);
/* 166 */     blocks[''] = new BlockInfo(150, "comparator off", true, true, false, new byte[] { 3, 0, 2, 1 }, null);
/* 167 */     blocks[''] = new BlockInfo(151, "daylight sensor", true, true, false, false, null);
/* 168 */     blocks[''] = new BlockInfo(152, "block of redstone", false, false, false, false, null);
/* 169 */     blocks[''] = new BlockInfo(153, "nether quartz ore", false, false, false, false, null);
/* 170 */     blocks[''] = new BlockInfo(154, "hopper", true, false, true, new byte[] { 4, 2, 5, 3 }, null);
/* 171 */     blocks[''] = new BlockInfo(155, "block of quartz", true, false, false, false, null);
/* 172 */     blocks[''] = new BlockInfo(156, "quartz stairs", true, false, false, false, null);
/* 173 */     blocks[''] = new BlockInfo(157, "activator rail", true, true, false, false, null);
/* 174 */     blocks[''] = new BlockInfo(158, "dropper", true, false, true, new byte[] { 4, 2, 5, 3 }, null);
/* 175 */     blocks[''] = new BlockInfo(159, "stained clay", true, false, false, false, null);
/* 176 */     blocks['ª'] = new BlockInfo(170, "hay bale", true, false, false, false, null);
/* 177 */     blocks['«'] = new BlockInfo(171, "carpet", true, false, false, false, null);
/* 178 */     blocks['¬'] = new BlockInfo(172, "hardened clay", false, false, false, false, null);
/* 179 */     blocks['­'] = new BlockInfo(173, "coal block", false, false, false, false, null);
/* 180 */     blocks['®'] = new BlockInfo(174, "packed ice", false, false, false, false, null);
/* 181 */     blocks['¯'] = new BlockInfo(175, "flowers", false, true, false, true, null);
/* 182 */     blocks['°'] = new BlockInfo(176, "free stand banner", true, true, false, false, null);
/* 183 */     blocks['±'] = new BlockInfo(177, "wall banner", true, true, false, false, null);
/* 184 */     blocks['²'] = new BlockInfo(178, "inverse daylight sensor", true, true, false, false, null);
/* 185 */     blocks['³'] = new BlockInfo(179, "red sandstone", true, false, false, false, null);
/* 186 */     blocks['´'] = new BlockInfo(180, "red sandstone stairs", true, false, false, false, null);
/* 187 */     blocks['µ'] = new BlockInfo(181, "double red sandstone slab", true, false, false, false, null);
/* 188 */     blocks['¶'] = new BlockInfo(182, "red sandstone slab", true, false, false, false, null);
/* 189 */     blocks['·'] = new BlockInfo(183, "spruce fence gate", true, true, false, new byte[] { 3, 0, 1, 2 }, null);
/* 190 */     blocks['¸'] = new BlockInfo(184, "birch fence gate", true, true, false, new byte[] { 3, 0, 1, 2 }, null);
/* 191 */     blocks['¹'] = new BlockInfo(185, "jungle fence gate", true, true, false, new byte[] { 3, 0, 1, 2 }, null);
/* 192 */     blocks['º'] = new BlockInfo(186, "dark oakfence gate", true, true, false, new byte[] { 3, 0, 1, 2 }, null);
/* 193 */     blocks['»'] = new BlockInfo(187, "acacia fence gate", true, true, false, new byte[] { 3, 0, 1, 2 }, null);
/* 194 */     blocks['¼'] = new BlockInfo(188, "spruce fence", false, false, false, false, null);
/* 195 */     blocks['½'] = new BlockInfo(189, "birch fence", false, false, false, false, null);
/* 196 */     blocks['¾'] = new BlockInfo(190, "jungle fence", false, false, false, false, null);
/* 197 */     blocks['¿'] = new BlockInfo(191, "dark oak fence", false, false, false, false, null);
/* 198 */     blocks['À'] = new BlockInfo(192, "acacia fence", false, false, false, false, null);
/* 199 */     blocks['Á'] = new BlockInfo(193, "spruce wooden door", true, true, false, 5, 3, false, null);
/* 200 */     blocks['Â'] = new BlockInfo(194, "birch wooden door", true, true, false, 5, 3, false, null);
/* 201 */     blocks['Ã'] = new BlockInfo(195, "jungle wooden door", true, true, false, 5, 3, false, null);
/* 202 */     blocks['Ä'] = new BlockInfo(196, "acacia wooden door", true, true, false, 5, 3, false, null);
/* 203 */     blocks['Å'] = new BlockInfo(197, "dark oak wooden door", true, true, false, 5, 3, false, null);
/*     */     
/*     */ 
/* 206 */     blocks[26].cardinalDirections = new byte[] { 1, 2, 3 };
/*     */     
/* 208 */     blocks[50].cardinalDirections = new byte[] { 2, 4, 1, 3 };
/*     */     
/*     */ 
/*     */ 
/* 212 */     blocks[53].cardinalDirections = new byte[] { 1, 3, 0, 2 };
/* 213 */     blocks[67].cardinalDirections = new byte[] { 1, 3, 0, 2 };
/* 214 */     blocks[108].cardinalDirections = new byte[] { 1, 3, 0, 2 };
/* 215 */     blocks[109].cardinalDirections = new byte[] { 1, 3, 0, 2 };
/* 216 */     blocks[114].cardinalDirections = new byte[] { 1, 3, 0, 2 };
/* 217 */     blocks[''].cardinalDirections = new byte[] { 1, 3, 0, 2 };
/* 218 */     blocks[''].cardinalDirections = new byte[] { 1, 3, 0, 2 };
/* 219 */     blocks[''].cardinalDirections = new byte[] { 1, 3, 0, 2 };
/* 220 */     blocks[''].cardinalDirections = new byte[] { 1, 3, 0, 2 };
/* 221 */     blocks[''].cardinalDirections = new byte[] { 1, 3, 0, 2 };
/* 222 */     blocks['´'].cardinalDirections = new byte[] { 1, 3, 0, 2 };
/*     */     
/*     */ 
/*     */ 
/* 226 */     blocks[63].cardinalDirections = new byte[] { 5, 3, 4, 2 };
/*     */     
/* 228 */     blocks[64].cardinalDirections = new byte[] { 0, 1, 2, 3 };
/*     */     
/* 230 */     blocks[65].cardinalDirections = new byte[] { 4, 2, 5, 3 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 235 */     blocks[68].cardinalDirections = new byte[] { 5, 3, 4, 2 };
/*     */     
/*     */ 
/* 238 */     blocks[69].cardinalDirections = new byte[] { 1, 3, 2, 4 };
/*     */     
/* 240 */     blocks[71].cardinalDirections = new byte[] { 0, 1, 2, 3 };
/*     */     
/* 242 */     blocks[75].cardinalDirections = new byte[] { 2, 4, 1, 3 };
/*     */     
/* 244 */     blocks[76].cardinalDirections = new byte[] { 2, 4, 1, 3 };
/*     */     
/* 246 */     blocks[77].cardinalDirections = new byte[] { 1, 3, 2, 4 };
/*     */     
/*     */ 
/*     */ 
/* 250 */     blocks[93].cardinalDirections = new byte[] { 2, 3, 0, 1 };
/*     */     
/* 252 */     blocks[94].cardinalDirections = new byte[] { 2, 3, 0, 1 };
/*     */     
/* 254 */     blocks[''].cardinalDirections = new byte[] { 2, 3, 0, 1 };
/*     */     
/* 256 */     blocks[''].cardinalDirections = new byte[] { 2, 3, 0, 1 };
/*     */     
/* 258 */     blocks[96].cardinalDirections = new byte[] { 3, 1, 2 };
/*     */     
/* 260 */     blocks['±'].cardinalDirections = new byte[] { 5, 3, 4, 2 };
/*     */   }
/*     */   
/*     */   public static String getName(int blockId) {
/* 264 */     return Material.getMaterial(blockId).name();
/*     */   }
/*     */   
/*     */   public static boolean isDataBlock(int blockId) {
/* 268 */     if ((blockId != -1) && (blocks[blockId] == null)) {
/* 269 */       NavyCraft.instance.DebugMessage("blocks(" + blockId + " is null!", 0);
/* 270 */       return false;
/*     */     }
/*     */     
/* 273 */     return (blockId != -1) && (blocks[blockId].isDataBlock);
/*     */   }
/*     */   
/*     */   public static boolean isComplexBlock(int blockId) {
/* 277 */     if ((blockId != -1) && (blocks[blockId] == null)) {
/* 278 */       return false;
/*     */     }
/*     */     
/* 281 */     return (blockId != -1) && (blocks[blockId].isComplexBlock);
/*     */   }
/*     */   
/*     */   public static boolean needsSupport(int blockId) {
/* 285 */     if (blockId == -1)
/* 286 */       return false;
/* 287 */     if (blocks[blockId] == null) {
/* 288 */       return false;
/*     */     }
/* 290 */     return blocks[blockId].needSupport;
/*     */   }
/*     */   
/*     */   public static boolean coversGrass(int blockId) {
/* 294 */     if ((blockId != -1) && (blocks[blockId] == null)) {
/* 295 */       return false;
/*     */     }
/* 297 */     return (blockId != -1) && (blocks[blockId].isGrassCover);
/*     */   }
/*     */   
/*     */   public static int getDropItem(int blockId) {
/* 301 */     if ((blockId != -1) && (blocks[blockId] == null)) {
/* 302 */       return -1;
/*     */     }
/* 304 */     return blocks[blockId].dropItem;
/*     */   }
/*     */   
/*     */   public static int getDropQuantity(int blockId) {
/* 308 */     if ((blockId != -1) && (blocks[blockId] == null)) {
/* 309 */       return 0;
/*     */     }
/* 311 */     return blocks[blockId].dropQuantity;
/*     */   }
/*     */   
/*     */   public static int getCardinalDirectionFromData(int BlockId, short BlockData) {
/* 315 */     if (blocks[BlockId].cardinalDirections == null) {
/* 316 */       System.out.println("Tried to get cardinals for " + BlockId + ", which has no cardinals.");
/* 317 */       return -1;
/*     */     }
/*     */     
/* 320 */     for (int i = 0; i < blocks[BlockId].cardinalDirections.length; i++) {
/* 321 */       if (BlockData == blocks[BlockId].cardinalDirections[i]) {
/* 322 */         return i;
/*     */       }
/*     */     }
/* 325 */     return -1;
/*     */   }
/*     */   
/*     */   public static String getCardinalDirection(int BlockId, short BlockData) {
/* 329 */     if (blocks[BlockId].cardinalDirections == null) {
/* 330 */       return "Woops";
/*     */     }
/* 332 */     switch (getCardinalDirectionFromData(BlockId, BlockData)) {
/*     */     case 0: 
/* 334 */       return "North";
/*     */     case 1: 
/* 336 */       return "East";
/*     */     case 2: 
/* 338 */       return "West";
/*     */     case 3: 
/* 340 */       return "South";
/*     */     }
/*     */     
/* 343 */     return "";
/*     */   }
/*     */   
/*     */   public static byte[] getCardinals(int BlockId) {
/* 347 */     if (blocks[BlockId] == null) {
/* 348 */       System.out.println("NO BLOCK INFO FOUND FOR " + BlockId + "! PANIC!");
/* 349 */       return null;
/*     */     }
/*     */     
/* 352 */     byte[] returnVal = blocks[BlockId].cardinalDirections;
/*     */     
/* 354 */     if (blocks[BlockId].cardinalDirections == null) {
/* 355 */       return null;
/*     */     }
/* 357 */     return returnVal;
/*     */   }
/*     */   
/*     */   public static class BlockInfo {
/*     */     int id;
/*     */     boolean isDataBlock;
/*     */     boolean needSupport;
/*     */     boolean isComplexBlock;
/* 365 */     int dropItem = -1;
/* 366 */     int dropQuantity = 0;
/*     */     boolean isGrassCover;
/* 368 */     private byte[] cardinalDirections = null;
/*     */     
/*     */ 
/*     */     private BlockInfo(int id, String name, boolean isDataBlock, boolean needSupport, boolean isComplexBlock, boolean isGrassCover)
/*     */     {
/* 373 */       this(id, name, isDataBlock, needSupport, isComplexBlock, id, 1, isGrassCover);
/*     */     }
/*     */     
/*     */     private BlockInfo(int id, String name, boolean isDataBlock, boolean needSupport, boolean isComplexBlock, byte[] cardinals)
/*     */     {
/* 378 */       this(id, name, isDataBlock, needSupport, isComplexBlock, id, 1, false);
/* 379 */       this.cardinalDirections = cardinals;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private BlockInfo(int id, String name, boolean isDataBlock, boolean isComplexBlock, int dropItem, int dropQuantity, byte[] cardinals)
/*     */     {
/* 387 */       this.id = id;
/* 388 */       this.isDataBlock = isDataBlock;
/* 389 */       this.isComplexBlock = isComplexBlock;
/* 390 */       this.dropItem = dropItem;
/* 391 */       this.dropQuantity = dropQuantity;
/* 392 */       this.cardinalDirections = cardinals;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private BlockInfo(int id, String name, boolean isDataBlock, boolean needSupport, boolean isComplexBlock, int dropItem, int dropQuantity, boolean isGrassCover)
/*     */     {
/* 400 */       this.id = id;
/*     */       
/* 402 */       this.isDataBlock = isDataBlock;
/* 403 */       this.needSupport = needSupport;
/* 404 */       this.isComplexBlock = isComplexBlock;
/* 405 */       this.dropItem = dropItem;
/* 406 */       this.dropQuantity = dropQuantity;
/* 407 */       this.isGrassCover = isGrassCover;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\keough99\Desktop\jd-gui-windows-1.4.0\Navalstuff.jar!\com\maximuspayne\navycraft\BlocksInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */