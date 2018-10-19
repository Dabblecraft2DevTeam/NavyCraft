package com.maximuspayne.navycraft.listeners;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import com.earth2me.essentials.Essentials;
import com.maximuspayne.aimcannon.AimCannonPlayerListener;
import com.maximuspayne.aimcannon.OneCannon;
import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Periscope;
import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.craft.Craft;
import com.maximuspayne.navycraft.craft.CraftMover;
import com.maximuspayne.navycraft.craft.CraftType;
import com.maximuspayne.shipyard.Plot;
import com.maximuspayne.shipyard.PlotType;
import com.maximuspayne.shipyard.Reward;
import com.maximuspayne.shipyard.Shipyard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;

import net.ess3.api.MaxMoneyException;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@SuppressWarnings({ "deprecation"})
public class NavyCraft_BlockListener implements Listener {
	public static Craft updatedCraft = null;
	private static NavyCraft plugin;
	public static PermissionsEx pex;
	public static WorldEditPlugin wep;
	public static WorldGuardPlugin wgp;
	public static CraftMover cm;
	public static OneCannon onec;
	public static int lastSpawn = -1;

	public NavyCraft_BlockListener(NavyCraft p) {
		plugin = p;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		Craft theCraft = Craft.getPlayerCraft(event.getPlayer());
		// System.out.println("Updated craft is " + updatedCraft.name + " of type " + updatedCraft.type.name);

		if (theCraft != null) {
			if (theCraft.crewNames.contains(event.getPlayer().getName()) || event.getPlayer().isOp() || PermissionInterface.CheckQuietPerm(event.getPlayer(), "navycraft.pbes")) {
				theCraft.addBlock(event.getBlock(), false);
			} else {
				event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks on enemy ships!");
				event.setCancelled(true);
			}
		} else {
			theCraft = Craft.getCraft(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
			if (theCraft != null) {
				if (theCraft.crewNames.contains(event.getPlayer().getName()) || event.getPlayer().isOp() || PermissionInterface.CheckQuietPerm(event.getPlayer(), "navycraft.pbes")) {
					theCraft.addBlock(event.getBlock(), false);
				} else {
					event.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks on enemy ships!");
					event.setCancelled(true);
				}
			}
		}
	}
    
	public static void ClickedASign(Player player, Block block, boolean leftClick) {
		// String world = block.getWorld().getName();
		Craft playerCraft = Craft.getPlayerCraft(player);

		Sign sign = (Sign) block.getState();

		if ((sign.getLine(0) == null) || sign.getLine(0).trim().equals("")) { return; }

		String craftTypeName = sign.getLine(0).trim().toLowerCase();

		// remove colors
		craftTypeName = craftTypeName.replaceAll(ChatColor.BLUE.toString(), "");

		// remove brackets
		if (craftTypeName.startsWith("[")) {
			craftTypeName = craftTypeName.substring(1, craftTypeName.length() - 1);
		}

		if (craftTypeName.equalsIgnoreCase("*select*") && (block.getRelative(BlockFace.DOWN, 1).getTypeId() == 22)) {
			BlockFace bf;
			bf = null;
			// bf2 = null;
			switch (block.getData()) {
				case (byte) 0x8:// n
					bf = BlockFace.SOUTH;
					// bf2 = BlockFace.NORTH;
					break;
				case (byte) 0x0:// s
					bf = BlockFace.NORTH;
					// bf2 = BlockFace.SOUTH;
					break;
				case (byte) 0x4:// w
					bf = BlockFace.EAST;
					// bf2 = BlockFace.WEST;
					break;
				case (byte) 0xC:// e
					bf = BlockFace.WEST;
					// bf2 = BlockFace.EAST;
					break;
				default:
					break;
			}

			if (bf == null) {
				player.sendMessage(ChatColor.DARK_RED + "Sign Error: Check Direction?");
				return;
			}

			if (block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getTypeId() == 68) {
				String spawnName = sign.getLine(3).trim().toLowerCase();
				Sign sign2 = (Sign) block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getState();
				String restrictedName = sign2.getLine(0).trim().toLowerCase();
				String rankStr = sign2.getLine(1).trim().toLowerCase();
				String idStr = sign2.getLine(2).trim().toLowerCase();
				String lotStr = sign2.getLine(3).trim().toLowerCase();
				spawnName = spawnName.replaceAll(ChatColor.BLUE.toString(), "");
				restrictedName = restrictedName.replaceAll(ChatColor.BLUE.toString(), "");
				rankStr = rankStr.replaceAll(ChatColor.BLUE.toString(), "");
				idStr = idStr.replaceAll(ChatColor.BLUE.toString(), "");
				lotStr = lotStr.replaceAll(ChatColor.BLUE.toString(), "");

				if (spawnName.isEmpty()) {
					player.sendMessage(ChatColor.DARK_RED + "Sign Error: No Type");
					return;
				}

				int rankReq = -1;
				try {
					rankReq = Integer.parseInt(rankStr);
				} catch (NumberFormatException nfe) {
					player.sendMessage(ChatColor.DARK_RED + "Sign Error: Invaild Rank Number");
					return;
				}

				if ((rankReq < 1) || (rankReq > 10)) {
					player.sendMessage(ChatColor.DARK_RED + "Sign Error: Invalid Rank Requirement");
					return;
				}
				String ownerName = null;
				if (!sign.getLine(1).isEmpty()) {
				ownerName = sign.getLine(1) + sign.getLine(2);
				}
				if (!restrictedName.isEmpty() && !restrictedName.equalsIgnoreCase("Public") && !restrictedName.equalsIgnoreCase(player.getName()) && (ownerName != null && !ownerName.equalsIgnoreCase(player.getName())) && !player.isOp() && !PermissionInterface.CheckQuietPerm(player, "NavyCraft.select")) {
					int tpId = -1;
					try {
						tpId = Integer.parseInt(idStr);
					} catch (NumberFormatException e) {
						player.sendMessage("Invalid plot id");
						return;
					}

					if (tpId > -1) {
						NavyCraft_FileListener.loadSignData();
						NavyCraft_BlockListener.loadRewards(ownerName);
						Sign foundSign = null;
						foundSign = NavyCraft_BlockListener.findSign(ownerName, tpId);
						if ((foundSign != null) && foundSign.getLocation().equals(sign.getLocation())) {
							wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
							if (wgp != null) {
								RegionManager regionManager = wgp.getRegionManager(plugin.getServer().getWorld("shipyard"));
								int x = foundSign.getX();
								int y = foundSign.getY();
								int z = foundSign.getZ();
								World world = foundSign.getWorld();
								String regionName = "--" + ownerName + "-" +  NavyCraft_FileListener.getSign(x, y, z, world);
								if ((regionManager.getRegion(regionName) != null) && !regionManager.getRegion(regionName).getMembers().contains(player.getUniqueId())) {
									player.sendMessage("You are not allowed to select this plot.");
									return;
								}
							}
						} else {
							player.sendMessage("You are not allowed to select this plot.");
							return;
						}
					} else {
						player.sendMessage("Invalid Plot ID");
						return;
					}
				}

				wep = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
				if (wep == null) {
					player.sendMessage("WorldEdit error");
					return;
				}

				EditSession es = wep.createEditSession(player);

				Location loc = null;
				int sizeX= 0, sizeY = 0, sizeZ = 0, originX = 0, originY = 0, originZ = 0;
				String name = null;
				boolean doFix = false;
				boolean dontSelect = false;
				for (PlotType pt : Shipyard.getPlots()) {
					if (pt.name.equalsIgnoreCase(lotStr)) {
					name = pt.name;
					doFix = pt.doFix;
					dontSelect = pt.dontSelect;
					sizeX = pt.sizeX;
					sizeY = pt.sizeY;
					sizeZ = pt.sizeZ;
					originX= pt.originX;
					originY = pt.originY;
					originZ = pt.originZ;
					loc = block.getRelative(bf, pt.bfr).getLocation();
				}
			}

				if (name == null || sizeX == 0 || sizeY == 0 || sizeZ == 0 || loc == null){
					player.sendMessage(ChatColor.DARK_RED + "Sign Error: Invalid Lot");
					return;
				}
				
				if (dontSelect) {
					player.sendMessage(ChatColor.RED + "You can't select this plot type!");
					return;
				}

				CuboidRegion region = new CuboidRegion(new Vector(loc.getBlockX() + originX, loc.getBlockY() + originY, loc.getBlockZ() + originZ), new Vector((loc.getBlockX() + originX + sizeX) - 1, (loc.getBlockY() + originY + sizeY) - 1, (loc.getBlockZ() + originZ + sizeZ) - 1));

				BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
				try {

					if (doFix) {
						clipboard.setOrigin(new Vector(block.getX() + 1, block.getY(), (block.getZ() - sizeZ) + 1));
					} else {
						clipboard.setOrigin(new Vector(loc.getX(), loc.getY(), loc.getZ()));
					}

					ForwardExtentCopy copy = new ForwardExtentCopy(es, region, clipboard, region.getMinimumPoint());
					Operations.completeLegacy(copy);
					wep.getSession(player).setClipboard(new ClipboardHolder(clipboard, es.getWorld().getWorldData()));
					Craft.playerClipboards.put(player, wep.getSession(player).getClipboard());

				} catch (MaxChangedBlocksException e) {
					e.printStackTrace();
				} catch (EmptyClipboardException e) {
					e.printStackTrace();
				}
				Craft.playerClipboardsRank.put(player, rankReq);
				Craft.playerClipboardsType.put(player, spawnName);
				Craft.playerClipboardsLot.put(player, lotStr);
				player.sendMessage(ChatColor.GREEN + "Selected vehicle: " + ChatColor.WHITE + spawnName.toUpperCase());

			} else {
				player.sendMessage(ChatColor.DARK_RED + "Sign Error: Check Second Sign?");
				return;
			}

		} else if (craftTypeName.equalsIgnoreCase("*claim*") && (block.getRelative(BlockFace.DOWN, 1).getTypeId() == 22)) {
				BlockFace bf;
				bf = null;
				
				// bf2 = null;
				switch (block.getData()) {
					case (byte) 0x8:// n
						bf = BlockFace.SOUTH;
						// bf2 = BlockFace.NORTH;
						break;
					case (byte) 0x0:// s
						bf = BlockFace.NORTH;
						// bf2 = BlockFace.SOUTH;
						break;
					case (byte) 0x4:// w
						bf = BlockFace.EAST;
						// bf2 = BlockFace.WEST;
						break;
					case (byte) 0xC:// e
						bf = BlockFace.WEST;
						// bf2 = BlockFace.EAST;
						break;
					default:
						break;
				}
			
				if (bf == null) {
					player.sendMessage(ChatColor.DARK_RED + "Sign Error: Check Direction?");
					return;
				}
			
				if (block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getTypeId() == 68) {
					Sign sign2 = (Sign) block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getState();
					String lotStr = sign2.getLine(3).trim().toLowerCase();
					lotStr = lotStr.replaceAll(ChatColor.BLUE.toString(), "");
			

					NavyCraft_FileListener.loadSignData();
					NavyCraft_BlockListener.loadRewards(player.getName());
					String UUID = Utils.getUUIDfromPlayer(player.getName());
					Location loc = null;
					int sizeX= 0, sizeY = 0, sizeZ = 0, originX = 0, originY = 0, originZ = 0;
					String name = null;
					int numPlots = 0;
					int numRewPlots = 0;
					for (PlotType pt : Shipyard.getPlots()) {
						if (pt.name.equalsIgnoreCase(lotStr)) {
						name = pt.name;
						sizeX = pt.sizeX;
						sizeY = pt.sizeY;
						sizeZ = pt.sizeZ;
						originX= pt.originX;
						originY = pt.originY;
						originZ = pt.originZ;
						loc = block.getRelative(bf, pt.bfr).getLocation();
						if (NavyCraft.playerSigns.containsKey(UUID)) {
							for (Plot p : NavyCraft.playerSigns.get(UUID)) {
								if (p.name.equalsIgnoreCase(pt.name)) {
									numPlots++;
								}
							}
						}
						if (NavyCraft.playerRewards.containsKey(UUID)) {
							for (Reward r : NavyCraft.playerRewards.get(UUID)) {
								if (r.name.equalsIgnoreCase(pt.name)) {
									numRewPlots = r.amount;
								}
							}
						}
					}
				}
					if (numPlots >= numRewPlots) {
						player.sendMessage("You have no " + name + " reward plots available.");
						return;
					}
			
				if (name == null || sizeX == 0 || sizeY == 0 || sizeZ == 0 || loc == null){
					NavyCraft.instance.DebugMessage(name, 3);
					NavyCraft.instance.DebugMessage(String.valueOf(sizeX), 3);
					NavyCraft.instance.DebugMessage(String.valueOf(sizeY), 3);
					NavyCraft.instance.DebugMessage(String.valueOf(sizeZ), 3);
					NavyCraft.instance.DebugMessage(loc.toString(), 3);
						player.sendMessage(ChatColor.DARK_RED + "Sign Error: Invalid Lot");
						return;
					}
					
					originX = loc.getBlockX() + originX;
					originY = loc.getBlockY() + originY;
					originZ = loc.getBlockZ() + originZ;
			
					wgp = (WorldGuardPlugin)plugin.getServer().getPluginManager().getPlugin("WorldGuard");
					if (wgp != null) {
						RegionManager regionManager = wgp.getRegionManager(loc.getWorld());
			
						// ApplicableRegionSet set = regionManager.getApplicableRegions(loc);
			
						sign.setLine(0, "*Select*");
						if (player.getName().length() > 15) {
							sign.setLine(1, player.getName().substring(0, 16));
							sign.setLine(2, player.getName().substring(15, player.getName().length()));
						if (player.getName() != sign.getLine(1) + sign.getLine(2)) {
							sign.setLine(1, player.getName());
							sign.setLine(2, null);
						}
						} else {
							sign.setLine(1, player.getName());
						}
			
						sign.setLine(3, "custom");
						sign.update();
			
						sign2.setLine(0, "Private");
						sign2.setLine(1, "1");
						sign2.setLine(2, "" + (maxId(player) + 1));
						sign2.setLine(3, lotStr.toUpperCase());
						sign2.update();
			
						player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + lotStr.toUpperCase() + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + " Claimed!");
						
						int x = sign.getX();
						int y = sign.getY();
						int z = sign.getZ();
						World world = sign.getWorld();
						NavyCraft_FileListener.updateSign(UUID, lotStr, x, y, z, world, maxId(player) + 1, true);
						String regionName = "--" + player.getName() + "-" +  NavyCraft_FileListener.getSign(x, y, z, world);
						
						regionManager.addRegion(new com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion(regionName, new com.sk89q.worldedit.BlockVector(originX, originY, originZ), new com.sk89q.worldedit.BlockVector((originX + sizeX) - 1, (originY + sizeY) - 1, (originZ + sizeZ) - 1)));
						DefaultDomain owners = new DefaultDomain();
						com.sk89q.worldguard.LocalPlayer lp = wgp.wrapPlayer(player);
						owners.addPlayer(lp);
						regionManager.getRegion(regionName).setOwners(owners);
			
						try {
							regionManager.save();
						} catch (StorageException e) {
							e.printStackTrace();
						}
					} else {
						player.sendMessage("World Guard error");
					}
			
				} else {
					player.sendMessage(ChatColor.DARK_RED + "Sign error: Check Second Sign?");
					return;
				}
		} else if (craftTypeName.equalsIgnoreCase("*spawn*") && (block.getRelative(BlockFace.DOWN, 1).getTypeId() == 22)) {
			int rotate = -1;
			BlockFace bf, bf2;
			bf = null;
			bf2 = null;
			switch (block.getData()) {
				case (byte) 0x8:// n
					rotate = 180;
					bf = BlockFace.SOUTH;
					bf2 = BlockFace.WEST;
					break;
				case (byte) 0x0:// s
					rotate = 0;
					bf = BlockFace.NORTH;
					bf2 = BlockFace.EAST;
					break;
				case (byte) 0x4:// w
					rotate = 90;
					bf = BlockFace.EAST;
					bf2 = BlockFace.SOUTH;
					break;
				case (byte) 0xC:// e
					rotate = 270;
					bf = BlockFace.WEST;
					bf2 = BlockFace.NORTH;
					break;
				default:
					break;
			}

			if (rotate == -1) {
				player.sendMessage(ChatColor.DARK_RED + "Sign Error: Check Direction?");
				return;
			}

			if (!Craft.playerClipboards.containsKey(player)) {
				player.sendMessage(ChatColor.RED + "Go to the Shipyard and select a vehicle first.");
				return;
			}

			if (!checkSpawnerClear(player, block, bf, bf2)) {
				player.sendMessage(ChatColor.RED + "Vehicle in the way.");
				return;
			}

			String typeString = sign.getLine(1).trim().toLowerCase();
			typeString = typeString.replaceAll(ChatColor.BLUE.toString(), "");
			if (!typeString.isEmpty() && !typeString.equalsIgnoreCase(Craft.playerClipboardsType.get(player)) && !typeString.equalsIgnoreCase(Craft.playerClipboardsLot.get(player))) {
				player.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.RED + ", you cannot spawn this type of vehicle here.");
				return;
			}

			int freeSpawnRankLimit = 0;
			String freeSpawnRankStr = sign.getLine(3).trim().toLowerCase();
			freeSpawnRankStr = freeSpawnRankStr.replaceAll(ChatColor.BLUE.toString(), "");
			if (!freeSpawnRankStr.isEmpty()) {
				try {
					freeSpawnRankLimit = Integer.parseInt(freeSpawnRankStr);
				} catch (NumberFormatException nfe) {
					player.sendMessage(ChatColor.DARK_RED + "Sign Error: Invaild Rank Limit");
					return;
				}
			}

			if (freeSpawnRankLimit < 0) {
				player.sendMessage(ChatColor.DARK_RED + "Sign Error: Invaild Rank Limit");
				return;
			}

			int playerRank = 1;
			String worldName = player.getWorld().getName();
			for(String s:PermissionsEx.getUser(player).getPermissions(worldName)) {
				if( s.contains("navycraft") ) {
					if( s.contains("spawn") ) {
						String[] split = s.split("\\.");
						try {
							playerRank = Integer.parseInt(split[2]);	
						} catch (Exception ex) {
							System.out.println("Invalid perm-" + s);
						}
			}

			if ((playerRank < Craft.playerClipboardsRank.get(player)) && (freeSpawnRankLimit < Craft.playerClipboardsRank.get(player)) && !player.isOp()) {
				player.sendMessage(ChatColor.RED + "You do not have the rank to spawn this vehicle.");
				return;
			}
		}
	}


			Essentials ess;
			ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			if (ess == null) {
				player.sendMessage("Essentials Economy error");
				return;
			}

			wep = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
			if (wep == null) {
				player.sendMessage("WorldEdit error");
				return;
			}
			EditSession es = wep.createEditSession(player);

			try {
				int oldLimit = es.getBlockChangeLimit();
				es.setBlockChangeLimit(100000);

				ClipboardHolder ch = Craft.playerClipboards.get(player);

				int width = ch.getClipboard().getRegion().getWidth();
				int length = ch.getClipboard().getRegion().getLength();
				int moveForward = 0;
				if (width > length) {
					moveForward = width;
				} else {
					moveForward = length;
				}

				AffineTransform transform = new AffineTransform();
				transform = transform.rotateY(-rotate);
				ch.setTransform(transform);
				Block pasteBlock = block.getRelative(bf, moveForward + 2);
				Vector pasteVector = new Vector(pasteBlock.getLocation().getX(), pasteBlock.getLocation().getY(), pasteBlock.getLocation().getZ());
				Operation operation;
				operation = ch.createPaste(es, ch.getWorldData())
						.to(pasteVector).ignoreAirBlocks(false).build();
				Operations.completeLegacy(operation);

				transform = transform.rotateY(rotate);
				ch.setTransform(transform);
				es.flushQueue();

				player.sendMessage(ChatColor.GREEN + "Spawned vehicle: " + ChatColor.WHITE + Craft.playerClipboardsType.get(player).toUpperCase());


				es.setBlockChangeLimit(oldLimit);

				int shiftRight = 0;
				int shiftForward = 0;
				int shiftUp = 0;
				int shiftDown = 0;

					Block rightLimit = block.getRelative(bf2, shiftRight).getRelative(bf, shiftForward).getRelative(BlockFace.UP, shiftUp);
					Block leftLimit = block.getRelative(bf, 1).getRelative(BlockFace.DOWN, shiftDown);
					int rightX, rightY, rightZ;
					int leftX, leftY, leftZ;
					rightX = rightLimit.getX();
					rightY = rightLimit.getY();
					rightZ = rightLimit.getZ();
					leftX = leftLimit.getX();
					leftY = leftLimit.getY();
					leftZ = leftLimit.getZ();
					int startX, endX, startZ, endZ;
					if (rightX < leftX) {
						startX = rightX;
						endX = leftX;
					} else {
						startX = leftX;
						endX = rightX;
					}
					if (rightZ < leftZ) {
						startZ = rightZ;
						endZ = leftZ;
					} else {
						startZ = leftZ;
						endZ = rightZ;
					}

					for (int x = startX; x <= endX; x++) {
						for (int y = leftY; y <= rightY; y++) {
							for (int z = startZ; z <= endZ; z++) {
								if (player.getWorld().getBlockAt(x, y, z).getTypeId() == 68) {
									Block shipSignBlock = player.getWorld().getBlockAt(x, y, z);
									Sign shipSign = (Sign) shipSignBlock.getState();
									String signLine0 = shipSign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
									CraftType craftType = CraftType.getCraftType(signLine0);
									if (craftType != null) {
										String name = shipSign.getLine(1);// .replaceAll("§.", "");

										if (name.trim().equals("")) {
											name = null;
										}

										int shipx = shipSignBlock.getX();
										int shipy = shipSignBlock.getY();
										int shipz = shipSignBlock.getZ();

										int direction = shipSignBlock.getData();

										// get the block the sign is attached to
										shipx = shipx + (direction == 4 ? 1 : (direction == 5 ? -1 : 0));
										shipz = shipz + (direction == 2 ? 1 : (direction == 3 ? -1 : 0));

										float dr = 0;

										switch (shipSignBlock.getData()) {
											case (byte) 0x2:// n
												dr = 180;
												break;
											case (byte) 0x3:// s
												dr = 0;
												break;
											case (byte) 0x4:// w
												dr = 90;
												break;
											case (byte) 0x5:// e
												dr = 270;
												break;
										}
										Craft theCraft = NavyCraft.instance.createCraft(player, craftType, shipx, shipy, shipz, name, dr, shipSignBlock);

										CraftMover cm = new CraftMover(theCraft, plugin);
										cm.structureUpdate(null, false);
										return;
									}
								}
							}
						}
				}

			} catch (MaxChangedBlocksException e) {
				player.sendMessage("Max changed blocks error");
				return;
			}
		} else if (craftTypeName.equalsIgnoreCase("*clear*")) {
			int rotate = -1;
			BlockFace bf, bf2;
			bf = null;
			bf2 = null;
			switch (block.getData()) {
				case (byte) 0x2:// n
					rotate = 180;
					bf = BlockFace.SOUTH;
					bf2 = BlockFace.WEST;
					break;
				case (byte) 0x3:// s
					rotate = 0;
					bf = BlockFace.NORTH;
					bf2 = BlockFace.EAST;
					break;
				case (byte) 0x4:// w
					rotate = 90;
					bf = BlockFace.EAST;
					bf2 = BlockFace.SOUTH;
					break;
				case (byte) 0x5:// e
					rotate = 270;
					bf = BlockFace.WEST;
					bf2 = BlockFace.NORTH;
					break;
				default:
					break;
			}

			if (rotate == -1) {
				player.sendMessage(ChatColor.DARK_RED + "Sign Error: Check Direction?");
				return;
			}
			if (block.getRelative(bf, 1).getTypeId() != 22) {
				return;
			}
			
				String typeString = sign.getLine(1).trim().toLowerCase();
				int shiftRight = 0;
				int shiftForward = 0;
				int shiftUp = 0;
				int shiftDown = 0;
				boolean typeFound = false;
				for (PlotType pt : Shipyard.getPlots()) {
					if (typeString.equalsIgnoreCase(pt.name)) {
						if (!pt.doFix) {
						shiftRight = pt.sizeX - 1;
						shiftForward = pt.sizeZ;
						shiftUp = 20;
						shiftDown = 8;
						} else {
							shiftRight = -pt.sizeX - 1;
							shiftForward = pt.sizeZ + 1;
							shiftUp = pt.sizeY;
							shiftDown = 0;
						}
						typeFound = true;
						break;
					}
				}

				if (!typeFound) {
					player.sendMessage(ChatColor.DARK_RED + "Sign Error: Lot Type");
					return;
				}
					Block rightLimit = block.getRelative(bf2, shiftRight).getRelative(bf, shiftForward + 2).getRelative(BlockFace.UP, shiftUp);
					Block leftLimit = block.getRelative(bf, 2).getRelative(BlockFace.DOWN, shiftDown);
					int rightX, rightY, rightZ;
					int leftX, leftY, leftZ;
					rightX = rightLimit.getX();
					rightY = rightLimit.getY();
					rightZ = rightLimit.getZ();
					leftX = leftLimit.getX();
					leftY = leftLimit.getY();
					leftZ = leftLimit.getZ();
					int startX, endX, startZ, endZ;
					if (rightX < leftX) {
						startX = rightX;
						endX = leftX;
					} else {
						startX = leftX;
						endX = rightX;
					}
					if (rightZ < leftZ) {
						startZ = rightZ;
						endZ = leftZ;
					} else {
						startZ = leftZ;
						endZ = rightZ;
					}

					for (int x = startX; x <= endX; x++) {
						for (int y = leftY; y <= rightY; y++) {
							for (int z = startZ; z <= endZ; z++) {
								if (player.getWorld().getBlockAt(x, y, z).getY() < 63) {
									player.getWorld().getBlockAt(x, y, z).setType(Material.WATER);
									} else {
										player.getWorld().getBlockAt(x, y, z).setType(Material.AIR);
									}
								}
							}
						}
					player.sendMessage(ChatColor.GREEN + "Cleared Type: " + ChatColor.WHITE + typeString.toUpperCase());
		} else if (craftTypeName.equalsIgnoreCase("periscope")) {
			if (!PermissionInterface.CheckPerm(player, "navycraft.periscope.use")) {
				player.sendMessage(ChatColor.RED + "You do not have permission to use this sign");
				return;
			}
			if (NavyCraft.aaGunnersList.contains(player)) {
				NavyCraft.aaGunnersList.remove(player);
				if (player.getInventory().contains(Material.BLAZE_ROD)) {
					player.getInventory().remove(Material.BLAZE_ROD);
				}
				player.sendMessage(ChatColor.GOLD + "You get off the AA-Gun.");
			}
			if (NavyCraft.ciwsGunnersList.contains(player)) {
				NavyCraft.ciwsGunnersList.remove(player);
				if (player.getInventory().contains(Material.BLAZE_ROD)) {
					player.getInventory().remove(Material.BLAZE_ROD);
				}
				player.sendMessage(ChatColor.GOLD + "You get off the CIWS.");
			}
			if (NavyCraft.ciwsFiringList.contains(player)) {
				NavyCraft.ciwsFiringList.remove(player);
			}
			if (NavyCraft.flakGunnersList.contains(player)) {
				NavyCraft.flakGunnersList.remove(player);
				if (player.getInventory().contains(Material.BLAZE_ROD)) {
					player.getInventory().remove(Material.BLAZE_ROD);
				}
				player.sendMessage(ChatColor.GOLD + "You get off the Flak-Gun.");
			}

			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {


				boolean periscopeFound = false;
				CraftMover cmer = new CraftMover(c, plugin);
				cmer.structureUpdate(null, false);
				for (Periscope p : c.periscopes) {
					if ((block.getLocation().getBlockX() == p.signLoc.getBlockX()) && (block.getLocation().getBlockY() == p.signLoc.getBlockY()) && (block.getLocation().getBlockZ() == p.signLoc.getBlockZ())) {
						periscopeFound = true;
						if (p.user != null) {
							player.sendMessage(ChatColor.RED + "Player already on scope.");
						} else if (p.raised && !p.destroyed && (p.scopeLoc != null)) {
							player.sendMessage(ChatColor.GREEN + "Periscope On!");
							CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
							Location newLoc = new Location(playerCraft.world, p.scopeLoc.getBlockX() + 0.5, p.scopeLoc.getBlockY() + 0.5, p.scopeLoc.getBlockZ() + 0.5);
							newLoc.setYaw(player.getLocation().getYaw());
							player.teleport(newLoc);
							p.user = player;
						} else if (!p.destroyed && (p.scopeLoc != null)) {
							player.sendMessage(ChatColor.RED + "Raise Periscope First.");
						} else {
							player.sendMessage(ChatColor.RED + "Periscope destroyed!");
						}
					}
				}

				if (!periscopeFound) {
					Periscope newPeriscope = new Periscope(block.getLocation(), c.periscopes.size());
					sign.setLine(1, "||" + newPeriscope.periscopeID + "||");
					sign.setLine(2, "|| ||");
					sign.setLine(3, "DOWN");
					sign.update();
					c.periscopes.add(newPeriscope);
					NavyCraft.allPeriscopes.add(newPeriscope);

					CraftMover cm = new CraftMover(c, plugin);
					cm.structureUpdate(null, false);

					if (!newPeriscope.destroyed && (newPeriscope.scopeLoc != null)) {
						Location newLoc = new Location(playerCraft.world, newPeriscope.scopeLoc.getBlockX() + 0.5, newPeriscope.scopeLoc.getBlockY() + 0.5, newPeriscope.scopeLoc.getBlockZ() + 0.5);
						newLoc.setYaw(player.getLocation().getYaw());
						player.teleport(newLoc);
						newPeriscope.user = player;
						player.sendMessage(ChatColor.GREEN + "Periscope Started!");
						CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
					}
				}

			} else {
				player.sendMessage(ChatColor.RED + "Start the sub before using the periscope!");
			}
		} else if (craftTypeName.equalsIgnoreCase("subdrive")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				if (c.submergedMode) {
					// if( 63 - c.minY == c.keelDepth )
					// {
					player.sendMessage(ChatColor.GOLD + "Starting Diesel Engines (SURFACE MODE)");
					CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
					c.submergedMode = false;
					c.vertPlanes = 0;
					// c.type.maxEngineSpeed = c.type.maxSurfaceSpeed;

					for (int eng : c.engineIDIsOn.keySet()) {
						//int engineType = c.engineIDTypes.get(eng);
						//if ((engineType != 0) && (engineType != 1) && (engineType != 2) && (engineType != 4) && (engineType != 9)) {
						if( c.engineIDSetOn.get(eng) && !c.engineIDIsOn.get(eng) ) {
							c.engineIDIsOn.put(eng, true);
						}
					}


					for (String s : c.crewNames) {
						Player p = plugin.getServer().getPlayer(s);
						if (p != null) {
							p.sendMessage(ChatColor.GREEN + "Surface the boat!");
						}
					}

					surfaceBellThread(sign.getBlock().getLocation());

				} else {
					player.sendMessage(ChatColor.GOLD + "Starting Electric Engines (READY TO DIVE)");
					CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
					c.submergedMode = true;

					for (int eng : c.engineIDIsOn.keySet()) {
						int engineType = c.engineIDTypes.get(eng);
						if ((engineType != 0) && (engineType != 1) && (engineType != 2) && (engineType != 4) && (engineType != 9)) {
							c.engineIDIsOn.put(eng, false);
						}
					}


					for (String s : c.crewNames) {
						Player p = plugin.getServer().getPlayer(s);
						if (p != null) {
							p.sendMessage(ChatColor.DARK_AQUA + "DIVE! DIVE!");
						}
					}
					divingBellThread(sign.getBlock().getLocation());
				}
				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);
			} else {
				player.sendMessage(ChatColor.RED + "Start the sub before using this sign.");
			}
		} else if (craftTypeName.equalsIgnoreCase("ballasttanks")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				c.ballastMode = (c.ballastMode + 1) % 4;
				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);

			} else {
				player.sendMessage(ChatColor.RED + "Start the sub before using this sign.");
			}
		} else if (craftTypeName.equalsIgnoreCase("firecontrol")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				int tubeNum = 0;
				String tubeString = sign.getLine(1).trim().toLowerCase();
				tubeString = tubeString.replaceAll(ChatColor.BLUE.toString(), "");
				if (!tubeString.isEmpty()) {
					try {
						tubeNum = Integer.parseInt(tubeString);
					} catch (NumberFormatException nfe) {
						tubeNum = 0;
					}
				}
				if ((tubeNum != 0) && c.tubeFiringMode.containsKey(tubeNum)) {
					if (leftClick) {
						CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
						if (c.tubeFiringMode.get(tubeNum) == -3) {
							if (c.tubeFiringDisplay.get(tubeNum) == 0) {
								if (c.tubeFiringAuto.get(tubeNum)) {
									player.sendMessage(ChatColor.RED + "Cannot change depth in auto mode.");
								} else {
									if (player.isSneaking()) {
										c.tubeFiringDepth.put(tubeNum, (c.tubeFiringDepth.get(tubeNum) + 5));
									} else {
										c.tubeFiringDepth.put(tubeNum, (c.tubeFiringDepth.get(tubeNum) + 1));
									}
									if (c.tubeFiringDepth.get(tubeNum) > 60) {
										c.tubeFiringDepth.put(tubeNum, 0);
									}
								}
							} else if (c.tubeFiringDisplay.get(tubeNum) == 1) {
								c.tubeFiringArmed.put(tubeNum, !c.tubeFiringArmed.get(tubeNum));
							} else if (c.tubeFiringDisplay.get(tubeNum) == 2) {
								c.tubeFiringAuto.put(tubeNum, !c.tubeFiringAuto.get(tubeNum));
							}
						} else if (c.tubeFiringMode.get(tubeNum) == -2) {

							if (c.tubeFiringDisplay.get(tubeNum) == 0) {
								if (player.isSneaking()) {
									c.tubeFiringDepth.put(tubeNum, (c.tubeFiringDepth.get(tubeNum) + 5));
								} else {
									c.tubeFiringDepth.put(tubeNum, (c.tubeFiringDepth.get(tubeNum) + 1));
								}
								if (c.tubeFiringDepth.get(tubeNum) > 60) {
									c.tubeFiringDepth.put(tubeNum, 0);
								}
							} else if (c.tubeFiringDisplay.get(tubeNum) == 1) {
								if (player.isSneaking()) {
									c.tubeFiringArm.put(tubeNum, (c.tubeFiringArm.get(tubeNum) + 50));
								} else {
									c.tubeFiringArm.put(tubeNum, (c.tubeFiringArm.get(tubeNum) + 10));
								}
								if (c.tubeFiringArm.get(tubeNum) > 250) {
									c.tubeFiringArm.put(tubeNum, 20);
								}
							}
						} else if (c.tubeFiringMode.get(tubeNum) == -1) {
							if (c.tubeFiringDisplay.get(tubeNum) == 0) {
								if (player.isSneaking()) {
									c.tubeFiringDepth.put(tubeNum, (c.tubeFiringDepth.get(tubeNum) + 5));
								} else {
									c.tubeFiringDepth.put(tubeNum, (c.tubeFiringDepth.get(tubeNum) + 1));
								}
								if (c.tubeFiringDepth.get(tubeNum) > 60) {
									c.tubeFiringDepth.put(tubeNum, 0);
								}
							} else if (c.tubeFiringDisplay.get(tubeNum) == 1) {
								if (player.isSneaking()) {
									c.tubeFiringArm.put(tubeNum, (c.tubeFiringArm.get(tubeNum) + 50));
								} else {
									c.tubeFiringArm.put(tubeNum, (c.tubeFiringArm.get(tubeNum) + 10));
								}
								if (c.tubeFiringArm.get(tubeNum) > 250) {
									c.tubeFiringArm.put(tubeNum, 20);
								}
							}
						}

					} else // right click
					{
						CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
						if (player.isSneaking()) {
							if (c.tubeFiringMode.get(tubeNum) == -3) {
								player.sendMessage(ChatColor.RED + "Cannot change mode while torpedo is live.");
							} else if (c.tubeFiringMode.get(tubeNum) == -2) {
								c.tubeFiringMode.put(tubeNum, -1);
								c.tubeFiringDisplay.put(tubeNum, 0);

							} else if (c.tubeFiringMode.get(tubeNum) == -1) {
								c.tubeFiringMode.put(tubeNum, -2);

								c.tubeFiringDisplay.put(tubeNum, 0);
							}
						} else {
							if (c.tubeFiringMode.get(tubeNum) == -3) {
								c.tubeFiringDisplay.put(tubeNum, (c.tubeFiringDisplay.get(tubeNum) + 1) % 3);

							} else if (c.tubeFiringMode.get(tubeNum) == -2) {
								c.tubeFiringDisplay.put(tubeNum, (c.tubeFiringDisplay.get(tubeNum) + 1) % 2);

							} else if (c.tubeFiringMode.get(tubeNum) == -1) {
								c.tubeFiringDisplay.put(tubeNum, (c.tubeFiringDisplay.get(tubeNum) + 1) % 2);

							}
						}
					}
				} else if (tubeNum != 0) {
					c.tubeFiringMode.put(tubeNum, -2);
					c.tubeFiringDepth.put(tubeNum, 1);
					c.tubeFiringArm.put(tubeNum, 20);
					c.tubeFiringArmed.put(tubeNum, false);
					c.tubeFiringHeading.put(tubeNum, c.rotation);
					c.tubeFiringAuto.put(tubeNum, true);
					c.tubeFiringRudder.put(tubeNum, 0);
					c.tubeFiringDisplay.put(tubeNum, 0);

				} else {
					player.sendMessage(ChatColor.DARK_RED + "Sign Error");
				}
				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);
			} else {
				player.sendMessage(ChatColor.RED + "Start the vehicle before using this sign.");
			}
		} else if (craftTypeName.equalsIgnoreCase("tdc")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				if (leftClick) {
					CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

					if (c.tubeMk1FiringDisplay == 0) {
						if (player.isSneaking()) {
							c.tubeMk1FiringDepth += 5;
						} else {
							c.tubeMk1FiringDepth += 1;
						}
						if (c.tubeMk1FiringDepth > 60) {
							c.tubeMk1FiringDepth = 0;
						}
					} else // if( c.tubeMk1FiringDisplay == 1 )
					{
						if (player.isSneaking()) {
							c.tubeMk1FiringSpread -= 5;
						} else {
							c.tubeMk1FiringSpread += 5;
						}
						if ((c.tubeMk1FiringSpread > 30) || (c.tubeMk1FiringSpread < 0)) {
							c.tubeMk1FiringSpread = 0;
						}
					}

				} else // right click
				{
					CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
					if (player.isSneaking()) {

						if (c.tubeMk1FiringDisplay == -1) {
							c.tubeMk1FiringDisplay = 0;
						} else {
							if (c.tubeMk1FiringMode == -2) {
								c.tubeMk1FiringMode = -1;
							} else if (c.tubeMk1FiringMode == -1) {
								c.tubeMk1FiringMode = -2;
							}
						}
					} else {
						if ((c.tubeMk1FiringDisplay == -1) || (c.tubeMk1FiringDisplay == 1)) {
							c.tubeMk1FiringDisplay = 0;

						} else {
							c.tubeMk1FiringDisplay = 1;

						}
					}
				}
				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);
			} else {
				player.sendMessage(ChatColor.RED + "Start the vehicle before using this sign.");
			}
		} else if (craftTypeName.equalsIgnoreCase("radar")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				if (!c.radarOn) {
					c.radarOn = true;
					player.sendMessage(ChatColor.GREEN + "Radar ACTIVATED!");
					CraftMover.playOtherSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.5f, 1.0f);
				} else {
					c.radarOn = false;
					player.sendMessage(ChatColor.RED + "Radar DEACTIVATED!");
					CraftMover.playOtherSound(player.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 0.5f, 1.0f);
				}
				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);
			} else {
				player.sendMessage(ChatColor.RED + "Start the vehicle before using this sign.");
			}
		} else if (craftTypeName.equalsIgnoreCase("sonar")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				if (!c.sonarOn) {
					c.sonarOn = true;
					player.sendMessage(ChatColor.GREEN + "Sonar ACTIVATED!");
					CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
				} else {
					c.sonarOn = false;
					player.sendMessage(ChatColor.RED + "Sonar DEACTIVATED!");
					CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
				}
				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);
			} else {
				player.sendMessage(ChatColor.RED + "Start the vehicle before using this sign.");
			}
		} else if (craftTypeName.equalsIgnoreCase("hfsonar")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				if (!c.hfOn) {
					c.hfOn = true;
					player.sendMessage(ChatColor.GREEN + "High Frequency Sonar ACTIVATED!");
					CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
				} else {
					c.hfOn = false;
					player.sendMessage(ChatColor.RED + "High Frequency Sonar DEACTIVATED!");
					CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
				}
				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);
			} else {
				player.sendMessage(ChatColor.RED + "Start the vehicle before using this sign.");
			}
		} else if (craftTypeName.equalsIgnoreCase("passivesonar")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				if (!c.sonarTargetIDs.isEmpty()) {
					c.sonarTargetIndex += 1;
					if (c.sonarTargetIndex >= c.sonarTargetIDs.size()) {
						c.sonarTargetIndex = 0;
					}
					while ((c.sonarTargetIndex < c.sonarTargetIDs.size()) && c.sonarTargetIDs2.get(c.sonarTargetIndex).sinking) {
						c.sonarTargetIndex += 1;
					}
					if (c.sonarTargetIndex == c.sonarTargetIDs.size()) // never found
					{
						c.sonarTargetIndex = -1;
						c.sonarTarget = null;
						c.sonarTargetRng = -1;
					} else {
						c.sonarTarget = c.sonarTargetIDs2.get(c.sonarTargetIndex);
						c.sonarTargetRng = -1;
					}

					CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
				}
				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);
			} else {
				player.sendMessage(ChatColor.RED + "Start the vehicle before using this sign.");
			}
		} else if (craftTypeName.equalsIgnoreCase("activesonar")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				c.doPing = true;
				CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);
			} else {
				player.sendMessage(ChatColor.RED + "Start the vehicle before using this sign.");
			}
		} else if (craftTypeName.equalsIgnoreCase("flak-gun")) {
			if (!PermissionInterface.CheckPerm(player, "navycraft.flak-gun.use")) {
				player.sendMessage(ChatColor.RED + "You do not have permission to use this sign");
				return;
			}
			BlockFace bf = BlockFace.NORTH;

			switch (block.getData()) {
				case (byte) 0x2:// n
					bf = BlockFace.SOUTH;
					break;
				case (byte) 0x3:// s
					bf = BlockFace.NORTH;
					break;
				case (byte) 0x4:// w
					bf = BlockFace.EAST;
					break;
				case (byte) 0x5:// e
					bf = BlockFace.WEST;
					break;
			}

			if (player.getItemInHand().getTypeId() > 0) {
				player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
				return;
			}

			Location newLoc = new Location(player.getWorld(), block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockX() + 0.5, block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockY(), block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockZ() + 0.5);
			player.teleport(newLoc);

			player.setItemInHand(new ItemStack(369, 1));
			NavyCraft.flakGunnersList.add(player);
			player.sendMessage(ChatColor.GOLD + "Manning Flak-Gun! Left Click with Blaze Rod to fire!");
			CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

		} else if (craftTypeName.equalsIgnoreCase("aa-gun")) {
			if (!PermissionInterface.CheckPerm(player, "navycraft.aa-gun.use")) {
				player.sendMessage(ChatColor.RED + "You do not have permission to use this sign");
				return;
			}
			BlockFace bf = BlockFace.NORTH;

			switch (block.getData()) {
				case (byte) 0x2:// n
					bf = BlockFace.SOUTH;
					break;
				case (byte) 0x3:// s
					bf = BlockFace.NORTH;
					break;
				case (byte) 0x4:// w
					bf = BlockFace.EAST;
					break;
				case (byte) 0x5:// e
					bf = BlockFace.WEST;
					break;
			}

			if (player.getItemInHand().getTypeId() > 0) {
				player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
				return;
			}

			Location newLoc = new Location(player.getWorld(), block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockX() + 0.5, block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockY(), block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockZ() + 0.5);
			player.teleport(newLoc);

			player.setItemInHand(new ItemStack(369, 1));
			NavyCraft.aaGunnersList.add(player);
			player.sendMessage(ChatColor.GOLD + "Manning AA-Gun! Left Click with Blaze Rod to fire!");
			CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);


		}else if (craftTypeName.equalsIgnoreCase("ciws")) {
			if (!PermissionInterface.CheckPerm(player, "navycraft.ciws.use")) {
				player.sendMessage(ChatColor.RED + "You do not have permission to use this sign");
				return;
			}
			BlockFace bf = BlockFace.NORTH;

			switch (block.getData()) {
				case (byte) 0x2:// n
					bf = BlockFace.SOUTH;
					break;
				case (byte) 0x3:// s
					bf = BlockFace.NORTH;
					break;
				case (byte) 0x4:// w
					bf = BlockFace.EAST;
					break;
				case (byte) 0x5:// e
					bf = BlockFace.WEST;
					break;
			}

			if (player.getItemInHand().getTypeId() > 0) {
				player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
				return;
			}

			Location newLoc = new Location(player.getWorld(), block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockX() + 0.5, block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockY(), block.getRelative(bf).getRelative(BlockFace.UP).getLocation().getBlockZ() + 0.5);
			player.teleport(newLoc);

			player.setItemInHand(new ItemStack(369, 1));
			NavyCraft.ciwsGunnersList.add(player);
			player.sendMessage(ChatColor.GOLD + "Manning CIWS! Left Click with Blaze Rod to fire!");
			CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);


		} else if (craftTypeName.equalsIgnoreCase("launcher")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				if (!c.launcherOn) {
					if ((c.speed == 0) && (c.setSpeed == 0) && (c.driverName == null)) {
						c.launcherOn = true;
						player.sendMessage(ChatColor.GREEN + "Vehicle launcher armed!");
					} else {
						player.sendMessage(ChatColor.RED + "Come to full stop and release helm before launching vehicles.");
					}
				} else {
					c.launcherOn = false;
					player.sendMessage(ChatColor.RED + "Vehicle launcher disarmed!");
				}
				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);
				CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

			} else {
				player.sendMessage(ChatColor.RED + "Start the vehicle before using this sign.");
			}
		} else if (craftTypeName.equalsIgnoreCase("radio")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				if (player.isSneaking()) {
					if (leftClick) {
						c.radioSelector = c.radioSelector - 1;
						if (c.radioSelector == 0) {
							c.radioSelector = 5;
						}
					} else {
						c.radioSelector = c.radioSelector + 1;
						if (c.radioSelector == 6) {
							c.radioSelector = 1;
						}
					}
				} else {
					if (c.radioSelector == 1) {
						if (!leftClick) {
							c.radio1 = (c.radio1 + 1);
							if (c.radio1 > 9) {
								c.radio1 = 0;
							}
						} else {
							c.radio1 = (c.radio1 - 1);
							if (c.radio1 < 0) {
								c.radio1 = 9;
							}
						}
					} else if (c.radioSelector == 2) {
						if (!leftClick) {
							c.radio2 = (c.radio2 + 1);
							if (c.radio2 > 9) {
								c.radio2 = 0;
							}
						} else {
							c.radio2 = (c.radio2 - 1);
							if (c.radio2 < 0) {
								c.radio2 = 9;
							}
						}
					} else if (c.radioSelector == 3) {
						if (!leftClick) {
							c.radio3 = (c.radio3 + 1);
							if (c.radio3 > 9) {
								c.radio3 = 0;
							}
						} else {
							c.radio3 = (c.radio3 - 1);
							if (c.radio3 < 0) {
								c.radio3 = 9;
							}
						}
					} else if (c.radioSelector == 4) {
						if (!leftClick) {
							c.radio4 = (c.radio4 + 1);
							if (c.radio4 > 9) {
								c.radio4 = 0;
							}
						} else {
							c.radio4 = (c.radio4 - 1);
							if (c.radio4 < 0) {
								c.radio4 = 9;
							}
						}
					} else if (c.radioSelector == 5) {
						c.radioSetOn = !c.radioSetOn;
						if (c.radioSetOn) {
							player.sendMessage(ChatColor.GREEN + "Radio turned on.");
						} else {
							player.sendMessage(ChatColor.RED + "Radio turned off.");
						}
					}
				}

				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);
				CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

			} else {
				player.sendMessage(ChatColor.RED + "Start the vehicle before using this sign.");
			}
		} else if (craftTypeName.equalsIgnoreCase("engine")) {
			Craft c = Craft.getCraft(block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
			if ((c != null) && (Craft.getPlayerCraft(player) == c) && c.isDressed(player)) {
				String engineNumStr = sign.getLine(1).trim().toLowerCase();
				engineNumStr = engineNumStr.replaceAll(ChatColor.BLUE.toString(), "");
				int engNum = -1;
				if (!engineNumStr.isEmpty()) {
					try {
						engNum = Integer.parseInt(engineNumStr);
					} catch (NumberFormatException nfe) {
						engNum = -1;
					}
				}

				if ((engNum > -1) && c.engineIDLocs.containsKey(engNum)) {
					if (c.engineIDIsOn.get(engNum)) {
						player.sendMessage(ChatColor.RED + "Stopping Engine: " + ChatColor.YELLOW + engNum + "!");
						c.engineIDIsOn.put(engNum, false);
						c.engineIDSetOn.put(engNum, false);
					} else if (c.submergedMode) {
						if ((c.engineIDTypes.get(engNum) != 0) && (c.engineIDTypes.get(engNum) != 1) && (c.engineIDTypes.get(engNum) != 2) && (c.engineIDTypes.get(engNum) != 4) && (c.engineIDTypes.get(engNum) != 9)) {
							player.sendMessage(ChatColor.RED + "Cannot start this engine while set to dive!");
						} else {
							player.sendMessage(ChatColor.GREEN + "Starting Engine: " + ChatColor.YELLOW + engNum + "!");
							c.engineIDIsOn.put(engNum, true);
							c.engineIDSetOn.put(engNum, true);
						}
					} else {
						player.sendMessage(ChatColor.GREEN + "Starting Engine: "+ ChatColor.YELLOW + engNum + "!");
						c.engineIDIsOn.put(engNum, true);
						c.engineIDSetOn.put(engNum, true);
					}
					CraftMover.playOtherSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
				}

				CraftMover cm = new CraftMover(c, plugin);
				cm.signUpdates(block);

			} else {
				player.sendMessage("Start the vehicle before using this sign.");
			}
		} else {

			// if the first line of the sign is a craft type, get the matching craft type.
			CraftType craftType = CraftType.getCraftType(craftTypeName);

			// it is a registred craft type !
			if ((craftType != null) || craftTypeName.equalsIgnoreCase("helm")) {

				if (NavyCraft.checkNoDriveRegion(player.getLocation())) {
					player.sendMessage(ChatColor.RED + "You do not have permission to drive vehicles in this area. Please use a spawner.");
					return;
				}
				Craft testCraft = Craft.getCraft(block.getX(), block.getY(), block.getZ());

				if ((testCraft != null) && ((testCraft.captainName == null) || testCraft.abandoned || (testCraft.captainAbandoned && !craftTypeName.equalsIgnoreCase("helm")))) {
					// check restrictions


					if (!PermissionInterface.CheckPerm(player, "navycraft." + craftType.name)) {
						player.sendMessage(ChatColor.RED + "You do not have permission to use this type of vehicle.");
						return;
					}

					if (player.getItemInHand().getTypeId() > 0) {
						player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
						return;
					}

					if (craftTypeName.equalsIgnoreCase("helm")) {
						player.sendMessage(ChatColor.RED + "There is no captain. Use main vehicle sign.");
						return;
					}


					if (testCraft.abandoned && (testCraft.captainName != null) && (player.getName() != testCraft.captainName)) {
						if (!testCraft.takingOver) {
							Craft.takeoverTimerThread(player, testCraft);
						}
						player.sendMessage(ChatColor.RED + "This vehicle will become abandoned in 2 minutes.");
						return;
					} else if (testCraft.captainAbandoned && testCraft.crewNames.contains(player.getName()) && (testCraft.captainName != null)) {
						testCraft.captainName = player.getName();
						testCraft.captainAbandoned = false;
						player.sendMessage(ChatColor.GREEN + "You take command of the vehicle.");
						for (String s : testCraft.crewNames) {
							Player p = plugin.getServer().getPlayer(s);
							if ((p != null) && (s != player.getName())) {
								p.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + player.getName() + " takes command of your crew.");
							}
						}
						return;
					}

					player.setItemInHand(new ItemStack(283, 1));

					testCraft.buildCrew(player, false);
					if (testCraft.customName != null) {
						player.sendMessage(ChatColor.GOLD + "You take command of the " + ChatColor.WHITE + testCraft.customName.toUpperCase() + ChatColor.GOLD + " class!");
					} else {
						player.sendMessage(ChatColor.GOLD + "You take command of the " + ChatColor.WHITE + testCraft.name.toUpperCase() + ChatColor.GOLD + " class!");
					}
					player.sendMessage(ChatColor.GOLD + "You take control of the helm.");
					testCraft.haveControl = true;
					testCraft.launcherOn = false;

					Location newLoc;
					if ((block.getRelative(BlockFace.DOWN).getTypeId() != 0) && (block.getRelative(BlockFace.DOWN).getTypeId() != 68)) {
						newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY(), block.getLocation().getBlockZ() + 0.5);
					} else {
						newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5, (double) block.getLocation().getBlockY() - 1, block.getLocation().getBlockZ() + 0.5);
					}
					newLoc.setYaw(player.getLocation().getYaw());
					player.teleport(newLoc);

					CraftMover cm = new CraftMover(testCraft, plugin);
					cm.structureUpdate(null, false);

					if (craftType != testCraft.type) {
						testCraft.type = craftType;
					}
					return;
				} else if ((testCraft != null) && !testCraft.launcherOn) /// set driver
				{
					if ((testCraft.driverName != null) && (testCraft.driverName != player.getName())) {
						player.sendMessage(testCraft.driverName + ChatColor.GOLD + " already has the helm.");
					} else if ((testCraft.driverName != null) && (testCraft.driverName == player.getName())) {
						player.sendMessage(ChatColor.GOLD + "Avoid clicking on the sign while driving.");

						return;
					} else {
						if (player.getItemInHand().getTypeId() > 0) {
							player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
							return;
						}

						if (playerCraft != testCraft) {
							player.sendMessage(ChatColor.RED + "You are not on this crew.");

							return;
						}

						if (!testCraft.isDressed(player)) { return; }
						if ((testCraft.type != craftType) && !craftTypeName.equalsIgnoreCase("helm")) {
							player.sendMessage(ChatColor.RED + "Vehicle sign differs from class, Use" + ChatColor.YELLOW + "/ship release.");
							return;
						}

						testCraft.driverName = player.getName();
						player.sendMessage(ChatColor.GOLD + "You take control of the helm.");
						testCraft.haveControl = true;
						if ((craftType != null) && (craftType != testCraft.type)) {
							testCraft.type = craftType;
						}

						Location newLoc;
						if ((block.getRelative(BlockFace.DOWN).getTypeId() != 0) && (block.getRelative(BlockFace.DOWN).getTypeId() != 68)) {
							newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY(), block.getLocation().getBlockZ() + 0.5);
						} else {
							newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5, (double) block.getLocation().getBlockY() - 1, block.getLocation().getBlockZ() + 0.5);
						}
						newLoc.setYaw(player.getLocation().getYaw());
						player.teleport(newLoc);
						player.setItemInHand(new ItemStack(283, 1));

						CraftMover cm = new CraftMover(testCraft, plugin);
						cm.structureUpdate(null, false);
					}
					return;
				} else if (testCraft != null) // launcher is on
				{
					if ((craftType == testCraft.type) || craftTypeName.equalsIgnoreCase("helm")) {
						player.sendMessage(ChatColor.RED + "Cannot use main vehicle sign, helm sign, or sign of same type while launcher is armed.");
						return;
					} else if (testCraft.speed != 0) {
						player.sendMessage(ChatColor.RED + "Cannot launch vehicles while main vehicle is moving.");
						return;
					}
					/// continue below to launch new vehicle!
				} else if (craftTypeName.equalsIgnoreCase("helm")) {
					player.sendMessage(ChatColor.RED + "Start the craft first. Use main vehicle sign.");
					return;
				}


				if (!PermissionInterface.CheckPerm(player, "navycraft." + craftType.name)) {
					player.sendMessage(ChatColor.RED + "You do not have permission to use this type of vehicle.");
					return;
				}

				if (player.getItemInHand().getTypeId() > 0) {
					player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
					return;
				}

				String name = sign.getLine(1);// .replaceAll("§.", "");

				if (name.trim().equals("")) {
					name = null;
				}

				int x = block.getX();
				int y = block.getY();
				int z = block.getZ();

				int direction = block.getData();

				// get the block the sign is attached to
				x = x + (direction == 4 ? 1 : (direction == 5 ? -1 : 0));
				z = z + (direction == 2 ? 1 : (direction == 3 ? -1 : 0));

				float dr = 0;

				switch (block.getData()) {
					case (byte) 0x2:// n
						dr = 180;
						break;
					case (byte) 0x3:// s
						dr = 0;
						break;
					case (byte) 0x4:// w
						dr = 90;
						break;
					case (byte) 0x5:// e
						dr = 270;
						break;
				}
				player.setItemInHand(new ItemStack(283, 1));
				Craft theCraft = NavyCraft.instance.createCraft(player, craftType, x, y, z, name, dr, block);


				if (theCraft != null) {
					Location newLoc;
					if ((block.getRelative(BlockFace.DOWN).getTypeId() != 0) && (block.getRelative(BlockFace.DOWN).getTypeId() != 68)) {
						newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY(), block.getLocation().getBlockZ() + 0.5);
					} else {
						newLoc = new Location(player.getWorld(), block.getLocation().getBlockX() + 0.5, (double) block.getLocation().getBlockY() - 1, block.getLocation().getBlockZ() + 0.5);
					}
					newLoc.setYaw(player.getLocation().getYaw());
					player.teleport(newLoc);

					CraftMover cm = new CraftMover(theCraft, plugin);
					cm.structureUpdate(null, false);
					if (sign.getLine(3).equalsIgnoreCase("center")) {
					}
				} else {
					player.setItemInHand(null);
				}

				return;
			}
		}
	}

	public static Player matchPlayerName(String subName) {
		Set<Player> playersOnline = new HashSet<>();
		playersOnline.addAll(NavyCraft.instance.getServer().getOnlinePlayers());
		ArrayList<Player> userList = new ArrayList<>();

		for (Player p : playersOnline) {
			if (p.getName().contains(subName)) {
				userList.add(p);
			}
		}

		if (userList.size() == 1) {
			return userList.get(0);
		} else {
			System.out.println("Attempted to find player matching " + subName + " but failed.");
			return null;
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onSignChange(SignChangeEvent event) {
		NavyCraft.instance.DebugMessage("A SIGN CHANGED!", 3);

		Player player = event.getPlayer();
		String craftTypeName = event.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");

		// remove brackets and stars
		if (craftTypeName.startsWith("[")) {
			craftTypeName = craftTypeName.substring(1, craftTypeName.length() - 1);
		}
		if (craftTypeName.startsWith("*")) {
			craftTypeName = craftTypeName.substring(1, craftTypeName.length() - 1);
		}

		// if the first line of the sign is a craft type, get the matching craft type.
		CraftType craftType = CraftType.getCraftType(craftTypeName);

		if (!player.isOp() && (((craftType != null) || craftTypeName.equalsIgnoreCase("flak-gun") || craftTypeName.equalsIgnoreCase("helm") || craftTypeName.equalsIgnoreCase("periscope") || craftTypeName.equalsIgnoreCase("nav") || craftTypeName.equalsIgnoreCase("radar") || craftTypeName.equalsIgnoreCase("detector") || craftTypeName.equalsIgnoreCase("sonar") || craftTypeName.equalsIgnoreCase("hydrophone") || craftTypeName.equalsIgnoreCase("subdrive") || craftTypeName.equalsIgnoreCase("firecontrol") || craftTypeName.equalsIgnoreCase("passivesonar") || craftTypeName.equalsIgnoreCase("activesonar") || craftTypeName.equalsIgnoreCase("hfsonar") || craftTypeName.equalsIgnoreCase("launcher") || craftTypeName.equalsIgnoreCase("engine") || craftTypeName.equalsIgnoreCase("tdc") || craftTypeName.equalsIgnoreCase("radio")) && !PermissionInterface.CheckPerm(player, "navycraft.signcreate"))) {
			player.sendMessage(ChatColor.RED + "You don't have permission to create this type of sign!");
			event.setCancelled(true);
			return;
		}
		
		if (!player.isOp() && craftType != null && craftType.adminBuild && !PermissionInterface.CheckPerm(player, "navycraft.admincraft") ) {
			player.sendMessage(ChatColor.RED + "You don't have permission to create this type of sign!");
			event.setCancelled(true);
			return;
		}
		if (!player.isOp() && ((craftTypeName.equalsIgnoreCase("claim")  && craftTypeName.equalsIgnoreCase("select") || craftTypeName.equalsIgnoreCase("spawn") || craftTypeName.equalsIgnoreCase("clear") || craftTypeName.equalsIgnoreCase("target") ) && !PermissionInterface.CheckPerm(player, "navycraft.adminsigncreate"))) {
			player.sendMessage(ChatColor.RED + "You don't have permission to create this type of sign!");
			event.setCancelled(true);
			return;
		}

		Craft theCraft = Craft.getPlayerCraft(event.getPlayer());
		// System.out.println("Updated craft is " + updatedCraft.name + " of type " + updatedCraft.type.name);

		theCraft = Craft.getCraft(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
		if (theCraft != null) {
			if (((craftTypeName.equalsIgnoreCase("flak-gun") || craftTypeName.equalsIgnoreCase("ciws") || craftTypeName.equalsIgnoreCase("helm") || craftTypeName.equalsIgnoreCase("periscope") || craftTypeName.equalsIgnoreCase("nav") || craftTypeName.equalsIgnoreCase("aa-gun") || craftTypeName.equalsIgnoreCase("radar") || craftTypeName.equalsIgnoreCase("detector") || craftTypeName.equalsIgnoreCase("sonar") || craftTypeName.equalsIgnoreCase("hydrophone") || craftTypeName.equalsIgnoreCase("subdrive") || craftTypeName.equalsIgnoreCase("firecontrol") || craftTypeName.equalsIgnoreCase("passivesonar") || craftTypeName.equalsIgnoreCase("activesonar") || craftTypeName.equalsIgnoreCase("hfsonar") || craftTypeName.equalsIgnoreCase("launcher") || craftTypeName.equalsIgnoreCase("engine") || craftTypeName.equalsIgnoreCase("tdc") || craftTypeName.equalsIgnoreCase("radio")))) {
				player.sendMessage(ChatColor.RED + "You cannot create this sign on a running vehicle");
				event.setCancelled(true);
				return;
			}
		}
		// }

		if (Utils.CheckEnabledWorld(player.getLocation()) && ((craftTypeName.equalsIgnoreCase("flak-gun") || craftTypeName.equalsIgnoreCase("ciws") || craftTypeName.equalsIgnoreCase("helm") || craftTypeName.equalsIgnoreCase("nav") || craftTypeName.equalsIgnoreCase("periscope") || craftTypeName.equalsIgnoreCase("aa-gun") || craftTypeName.equalsIgnoreCase("radar") || craftTypeName.equalsIgnoreCase("detector") || craftTypeName.equalsIgnoreCase("sonar") || craftTypeName.equalsIgnoreCase("hydrophone") || craftTypeName.equalsIgnoreCase("subdrive") || craftTypeName.equalsIgnoreCase("firecontrol") || craftTypeName.equalsIgnoreCase("passivesonar") || craftTypeName.equalsIgnoreCase("activesonar") || craftTypeName.equalsIgnoreCase("hfsonar") || craftTypeName.equalsIgnoreCase("launcher") || craftTypeName.equalsIgnoreCase("engine") || craftTypeName.equalsIgnoreCase("tdc") || craftTypeName.equalsIgnoreCase("radio")))) {
			int cost = 0;
			if (craftTypeName.equalsIgnoreCase("helm")) {
				cost = 50;
			} else if (craftTypeName.equalsIgnoreCase("nav")) {
				cost = 50;
			} else if (craftTypeName.equalsIgnoreCase("periscope")) {
				cost = 100;
			} else if (craftTypeName.equalsIgnoreCase("aa-gun")) {
				cost = 100;
			} else if (craftTypeName.equalsIgnoreCase("flak-gun")) {
				cost = 200;
			} else if (craftTypeName.equalsIgnoreCase("ciws")) {
				cost = 1250;
			} else if (craftTypeName.equalsIgnoreCase("radio")) {
				cost = 50;
			} else if (craftTypeName.equalsIgnoreCase("radar")) {
				cost = 200;
			} else if (craftTypeName.equalsIgnoreCase("detector")) {
				cost = 50;
			} else if (craftTypeName.equalsIgnoreCase("sonar")) {
				cost = 250;
			} else if (craftTypeName.equalsIgnoreCase("hydrophone")) {
				cost = 100;
			} else if (craftTypeName.equalsIgnoreCase("subdrive")) {
				cost = 50;
			} else if (craftTypeName.equalsIgnoreCase("firecontrol")) {
				cost = 1000;
			} else if (craftTypeName.equalsIgnoreCase("tdc")) {
				cost = 400;
			} else if (craftTypeName.equalsIgnoreCase("passivesonar")) {
				cost = 1250;
			} else if (craftTypeName.equalsIgnoreCase("activesonar")) {
				cost = 1250;
			} else if (craftTypeName.equalsIgnoreCase("hfsonar")) {
				cost = 1250;
			} else if (craftTypeName.equalsIgnoreCase("engine")) {
				String engineTypeStr = event.getLine(2).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
				if (engineTypeStr != null) {
					if (engineTypeStr.equalsIgnoreCase("Diesel 1")) {
						cost = 100;
					}
					if (engineTypeStr.equalsIgnoreCase("Motor 1")) {
						cost = 150;
					}
					if (engineTypeStr.equalsIgnoreCase("Diesel 2")) {
						cost = 250;
					}
					if (engineTypeStr.equalsIgnoreCase("Boiler 1")) {
						cost = 250;
					}
					if (engineTypeStr.equalsIgnoreCase("Diesel 3")) {
						cost = 1000;
					}
					if (engineTypeStr.equalsIgnoreCase("Gasoline 1")) {
						cost = 50;
					}
					if (engineTypeStr.equalsIgnoreCase("Boiler 2")) {
						cost = 600;
					}
					if (engineTypeStr.equalsIgnoreCase("Boiler 3")) {
						cost = 1250;
					}
					if (engineTypeStr.equalsIgnoreCase("Gasoline 2")) {
						cost = 100;
					}
					if (engineTypeStr.equalsIgnoreCase("Nuclear")) {
						cost = 5000;
					}
					if (engineTypeStr.equalsIgnoreCase("Airplane 1")) {
						cost = 50;
					}
					if (engineTypeStr.equalsIgnoreCase("Airplane 2")) {
						cost = 80;
					}
					if (engineTypeStr.equalsIgnoreCase("Airplane 3")) {
						cost = 120;
					}
					if (engineTypeStr.equalsIgnoreCase("Airplane 4")) {
						cost = 160;
					}
					if (engineTypeStr.equalsIgnoreCase("Airplane 7")) {
						cost = 500;
					}
					if (engineTypeStr.equalsIgnoreCase("Airplane 5")) {
						cost = 400;
					}
					if (engineTypeStr.equalsIgnoreCase("Airplane 6")) {
						cost = 500;
					}
					if (engineTypeStr.equalsIgnoreCase("Airplane 8")) {
						cost = 5000;
					}
					if (engineTypeStr.equalsIgnoreCase("Tank 1")) {
						cost = 50;
					}
					if (engineTypeStr.equalsIgnoreCase("Tank 2")) {
						cost = 5000;
					}
				}
			}

			if (cost > 0) {
				Essentials ess;
				ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
				if (ess == null) {
					player.sendMessage("Essentials Economy error");
					return;
				}
				if (!PermissionInterface.CheckQuietPerm(player, "navycraft.free") && !ess.getUser(player).canAfford(new BigDecimal(cost))) {
					player.sendMessage(ChatColor.YELLOW + "You cannot afford this sign:" + ChatColor.RED + "$" + cost);
					event.setCancelled(true);
					return;
				}else if( !PermissionInterface.CheckQuietPerm(player, "navycraft.free") ) {
					ess.getUser(player).takeMoney(new BigDecimal(cost));
					player.sendMessage(ChatColor.YELLOW + "You purchase sign for " + ChatColor.GREEN + "$" + cost + ChatColor.YELLOW + ". Type " + ChatColor.WHITE + "\"/sign undo\"" + ChatColor.YELLOW + " to cancel.");
				}else
				{
					player.sendMessage(ChatColor.YELLOW + "You purchase sign for " + ChatColor.GREEN + "FREE" + ChatColor.YELLOW + ". Type " + ChatColor.WHITE + "\"/sign undo\"" + ChatColor.YELLOW + " to cancel.");
				}
				NavyCraft.playerLastBoughtSign.put(player, event.getBlock());
				NavyCraft.playerLastBoughtCost.put(player, cost);
				NavyCraft.playerLastBoughtSignString0.put(player, craftTypeName);
				String string1 = event.getLine(1).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
				NavyCraft.playerLastBoughtSignString1.put(player, string1);
				String string2 = event.getLine(2).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
				NavyCraft.playerLastBoughtSignString2.put(player, string2);
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPhysics(final BlockPhysicsEvent event) {
		if (!event.isCancelled()) {

			final Block block = event.getBlock();

			
			if ((block.getTypeId() == 63) || (block.getTypeId() == 68) || (block.getTypeId() == 50) || (block.getTypeId() == 75) || (block.getTypeId() == 76) || (block.getTypeId() == 65) || (block.getTypeId() == 64) || (block.getTypeId() == 71) || (block.getTypeId() == 70) || (block.getTypeId() == 72) || (block.getTypeId() == 143)) {
				Craft c = Craft.getCraft(block.getX(), block.getY(), block.getZ());
				if (c != null) {

					// if not iron door being controlled by circuit...
					if ((event.getChangedTypeId() != 0) && !(((block.getTypeId() == 71) || (block.getTypeId() == 64)) && ((event.getChangedTypeId() == 69) || (event.getChangedTypeId() == 77) || (event.getChangedTypeId() == 55) || (event.getChangedTypeId() == 70) || (event.getChangedTypeId() == 72) || (block.getTypeId() == 143) || (block.getTypeId() == 75) || (block.getTypeId() == 76) || (block.getTypeId() == 50)))) {

						event.setCancelled(true);
					}
				}
			}
			
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockFromTo(final BlockFromToEvent event) {
		if (!event.isCancelled()) {
			final Block block = event.getToBlock();

			if ((block.getTypeId() == 75) || (block.getTypeId() == 76) || (block.getTypeId() == 65) || (block.getTypeId() == 69) || (block.getTypeId() == 77) || (block.getTypeId() == 70) || (block.getTypeId() == 72) || (block.getTypeId() == 68) || (block.getTypeId() == 63) || (block.getTypeId() == 143) || (block.getTypeId() == 55)) {
				if (Craft.getCraft(block.getX(), block.getY(), block.getZ()) != null) {
					// event.setCancelled(true);
					block.setTypeId(8);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		int blockId = event.getBlock().getTypeId();
		Location loc = event.getBlock().getLocation();
		// System.out.println(blockId);

		if ((blockId == 29) || (blockId == 33)) { // piston / sticky piston (base)
			Craft craft = Craft.getCraft(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

			if (craft != null) {
				Player p = plugin.getServer().getPlayer(craft.driverName);
				if (p != null) {
					p.sendMessage("You just did something with a piston, didn't you?");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void on(BlockBreakEvent event) {
    	if (event.getBlock().getType() == Material.COAL_ORE && PermissionInterface.CheckQuietPerm(event.getPlayer(), "navycraft.dropchance")) {
    		Random pick = new Random();
			int chance = 0;
			for (int counter = 1; counter <= 1; counter++) {
				chance = 1 + pick.nextInt(10);
			}
    			ItemStack item = new ItemStack(Material.SULPHUR, chance);
    			event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), item);
    	}
		Craft theCraft = Craft.getPlayerCraft(event.getPlayer());
		// System.out.println("Updated craft is " + updatedCraft.name + " of type " + updatedCraft.type.name);

		if (theCraft != null) {
			if (theCraft.crewNames.contains(event.getPlayer().getName()) || event.getPlayer().isOp() || PermissionInterface.CheckQuietPerm(event.getPlayer(), "navycraft.bbes")) {
				return;
			} else {
				event.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks on enemy ships!");
				event.setCancelled(true);
			}
		} else {
			theCraft = Craft.getCraft(event.getBlock().getX(), event.getBlock().getY(), event.getBlock().getZ());
			if (theCraft != null) {
				if (theCraft.crewNames.contains(event.getPlayer().getName()) || event.getPlayer().isOp() || PermissionInterface.CheckQuietPerm(event.getPlayer(), "navycraft.bbes")) {
					return;
				} else {
					event.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks on enemy ships!");
					event.setCancelled(true);
				}
			}
		}
}

	public static void divingBellThread(final Location loc) {
		new Thread() {

			@Override
			public void run() {

				setPriority(Thread.MIN_PRIORITY);

				// taskNum = -1;
				try {
					for (int i = 0; i < 8; i++) {
						sleep(200);
						if ((i % 2) == 0) {
							CraftMover.playOtherSound(loc, Sound.BLOCK_NOTE_PLING, 1.0f, 1.2f);
						} else {
							CraftMover.playOtherSound(loc, Sound.BLOCK_NOTE_PLING, 1.0f, 1);
						}

					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start(); // , 20L);
	}

	public static void surfaceBellThread(final Location loc) {
		new Thread() {

			@Override
			public void run() {

				setPriority(Thread.MIN_PRIORITY);

				// taskNum = -1;
				try {
					for (int i = 0; i < 2; i++) {
						sleep(300);
						CraftMover.playOtherSound(loc, Sound.BLOCK_NOTE_PLING, 1.0f, 2);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static boolean checkSpawnerClear(Player player, Block block, BlockFace bf, BlockFace bf2) {
		int shiftRight = 0;
		int shiftForward = 0;
		int shiftUp = 0;
		int shiftDown = 0;
		boolean typeFound = false;
		for (PlotType pt : Shipyard.getPlots()) {
		if (Craft.playerClipboardsLot.get(player).equalsIgnoreCase(pt.name)) {
			shiftRight = pt.sizeX - 1;
			shiftForward = pt.sizeZ;
			if (!pt.doFix) {
			shiftUp = 20;
			shiftDown = 8;
			} else {
				shiftUp = pt.sizeY;
				shiftDown = 0;
			}
			typeFound = true;
			break;
		}
	}
		if (!typeFound) {
			player.sendMessage("Unknown lot type error2!");
		}
		Block rightLimit = block.getRelative(bf2, shiftRight).getRelative(bf, shiftForward).getRelative(BlockFace.UP, shiftUp);
		Block leftLimit = block.getRelative(bf, 1).getRelative(BlockFace.DOWN, shiftDown);
		int rightX, rightY, rightZ;
		int leftX, leftY, leftZ;
		rightX = rightLimit.getX();
		rightY = rightLimit.getY();
		rightZ = rightLimit.getZ();
		leftX = leftLimit.getX();
		leftY = leftLimit.getY();
		leftZ = leftLimit.getZ();
		int startX, endX, startZ, endZ;
		if (rightX < leftX) {
			startX = rightX;
			endX = leftX;
		} else {
			startX = leftX;
			endX = rightX;
		}
		if (rightZ < leftZ) {
			startZ = rightZ;
			endZ = leftZ;
		} else {
			startZ = leftZ;
			endZ = rightZ;
		}

		for (Craft c : Craft.craftList) {
			if (c.world == block.getWorld()) {
				if ((c.maxX >= startX) && (c.minX <= endX)) {
					if ((c.maxZ >= startZ) && (c.minZ <= endZ)) {
						if ((c.maxY >= leftY) && (c.minY <= rightY)) { return false; }
					}
				}
			}
		}
		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void inventoryClickEvent(final InventoryClickEvent event) {
		if (!event.isCancelled()) {
			if ( Utils.CheckEnabledWorld(event.getWhoClicked().getLocation()) ) {
				if ((event.getInventory().getType() == InventoryType.DISPENSER) && (event.getRawSlot() == 4) && ((event.getCurrentItem().getTypeId() == 388) || (event.getCursor().getTypeId() == 388))) {
					event.setCancelled(true);
				}
			}

		}
	}
	
	public static void loadRewards(String player) {
		NavyCraft.playerRewards.clear();
		String UUID = Utils.getUUIDfromPlayer(player);
		
		String worldName = "";
		if (NavyCraft.instance.getConfig().getString("EnabledWorlds") != "null") {
			String[] worlds = NavyCraft.instance.getConfig().getString("EnabledWorlds").split(",");
			worldName = worlds[0];
		} else {
			worldName = NavyCraft.instance.getServer().getPlayer(player).getWorld().getName();
		}
		
		pex = (PermissionsEx) NavyCraft.instance.getServer().getPluginManager().getPlugin("PermissionsEx");
		if (pex == null)
			return;
		
		ArrayList<Reward> list = new ArrayList<Reward>();
		for (String s : PermissionsEx.getUser(player).getPermissions(worldName)) {
			for (PlotType pt : Shipyard.getPlots()) {
			if (s.contains("navycraft")) {
					if (s.contains(pt.name.toLowerCase())) {
						String[] split = s.split("\\.");
						try {
						int num = Integer.parseInt(split[2]);
						Reward r = new Reward(pt.name, num);
							for (Reward r2 : list) {
								if (r2.name.equalsIgnoreCase(r.name)) {
									r = new Reward (pt.name, r2.amount + r.amount);
								}
							}
							list.add(r);
					} catch (Exception ex) {
						System.out.println("Invalid perm-" + s);
						break;
					}
				}
			}
		}
	}
		NavyCraft.playerRewards.put(UUID, list);
	}

	public static Sign findSign(String player, int id) {
		String UUID = Utils.getUUIDfromPlayer(player);
		if (UUID != null) {
		Sign foundSign = null;
		if (NavyCraft.playerSigns.containsKey(UUID)) {
			for (Plot p : NavyCraft.playerSigns.get(UUID)) {
				if (id == NavyCraft.playerSignIndex.get(p.sign)) {
					foundSign = p.sign;
				}
			}
		}
		return foundSign;
	} else {
		return null;
	}
}

	public static int maxId(Player player) {
		int foundHighest = -1;
		String UUID = Utils.getUUIDfromPlayer(player.getName());
		if (UUID != null) {
		NavyCraft.instance.DebugMessage("UUID check passed", 3);
		if (NavyCraft.playerSigns.containsKey(UUID)) {
			NavyCraft.instance.DebugMessage("Player signs contained player", 3);
			for (Plot p : NavyCraft.playerSigns.get(UUID)) {
				NavyCraft.instance.DebugMessage("Checking player plot:" + p.name, 3);
				if (foundHighest < NavyCraft.playerSignIndex.get(p.sign)) {
					foundHighest = NavyCraft.playerSignIndex.get(p.sign);
					NavyCraft.instance.DebugMessage(String.valueOf(foundHighest), 3);
				}
			}
		}
	} else {
		player.sendMessage(ChatColor.RED + "Error UUID was null?");
	}
		return foundHighest;
}


	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockDispense(final BlockDispenseEvent event) {
		if (!event.isCancelled()) {
			if (Utils.CheckEnabledWorld(event.getBlock().getLocation()) && (event.getItem().getType() == Material.EMERALD)) {
				event.setCancelled(true);
			}

		}
		
		if(!event.isCancelled())
			AimCannonPlayerListener.onBlockDispense(event);
	}
	
	public static void showRank(Player player, String p) {
		int exp = 0;
		int exp1 = 0;
		String worldName = null;
		
		NavyCraft_FileListener.loadExperience(p);
		
		pex = (PermissionsEx)plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
		
		int rankExp=0;
		for(String s:PermissionsEx.getUser(p).getPermissions(worldName)) {
			if( s.contains("navycraft") ) {
				if( s.contains("exp") ) {
					String[] split = s.split("\\.");
					try {
						rankExp = Integer.parseInt(split[2]);	
					} catch (Exception ex) {
						System.out.println("Invalid perm-" + s);
					}
				}
			}
		}
		
		List<String> groupNames = PermissionsEx.getUser(p).getParentIdentifiers("navycraft");
		for( String s : groupNames ) {
			if( PermissionsEx.getPermissionManager().getGroup(s).getRankLadder().equalsIgnoreCase("navycraft") ) {
				if (NavyCraft.playerExp.containsKey(p)) {
					exp = NavyCraft.playerExp.get(p);
				}
				player.sendMessage(ChatColor.GRAY + p + "'s rank is " + ChatColor.WHITE + s.toUpperCase()
						+ ChatColor.GRAY + " and has " + ChatColor.WHITE + exp + "/" + rankExp
						+ ChatColor.GRAY + " rank points.");
				return;
	   } else { 
		   exp1 = NavyCraft.playerExp.get(p);
			String[] groupName = PermissionsEx.getUser(p).getGroupsNames();
			for( String g : groupName ) {
			player.sendMessage(ChatColor.GRAY + p + "'s rank is " + ChatColor.WHITE + g.toUpperCase()
			+ ChatColor.GRAY + " and has " + ChatColor.WHITE + exp1
			+ ChatColor.GRAY + " rank points.");
	return;
	       }
		}
	}
}
	public static void getRank(Player player) {
		int exp = 0;
		int exp1 = 0;
		String worldName = player.getWorld().getName();
		
		NavyCraft_FileListener.loadExperience(player.getName());
		
		pex = (PermissionsEx)plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
		
		int rankExp=0;
		for(String s:PermissionsEx.getUser(player).getPermissions(worldName)) {
			if( s.contains("navycraft") ) {
				if( s.contains("exp") ) {
					String[] split = s.split("\\.");
					try {
						rankExp = Integer.parseInt(split[2]);	
					} catch (Exception ex) {
						System.out.println("Invalid perm-" + s);
					}
				}
			}
		}
		List<String> groupNames = PermissionsEx.getUser(player).getParentIdentifiers("navycraft");
		for( String s : groupNames ) {
			if( PermissionsEx.getPermissionManager().getGroup(s).getRankLadder().equalsIgnoreCase("navycraft") ) {
				if (NavyCraft.playerExp.containsKey(player.getName())) {
					exp = NavyCraft.playerExp.get(player.getName());
				}
				player.sendMessage(ChatColor.GRAY + "Your rank is " + ChatColor.WHITE + s.toUpperCase()
						+ ChatColor.GRAY + " and you have " + ChatColor.WHITE + exp + "/" + rankExp
						+ ChatColor.GRAY + " rank points.");
				if( exp >= rankExp )
				{
							checkRankWorld(player, exp, player.getWorld());
				}
				return;
			   } else { 
					if (NavyCraft.playerExp.containsKey(player.getName())) {
					exp1 = NavyCraft.playerExp.get(player.getName());
					}
					String[] groupName = PermissionsEx.getUser(player).getGroupsNames();
					for( String g : groupName ) {
					player.sendMessage(ChatColor.GRAY + "Your rank is " + ChatColor.WHITE + g.toUpperCase()
					+ ChatColor.GRAY + " and you have " + ChatColor.WHITE + exp1
					+ ChatColor.GRAY + " rank points.");
				return;
			}
	   }
	}
}
	public static void rewardExpPlayer(int newExp, Player player) {
		int cash = newExp / 2;
		int rewardedExp = newExp;
		NavyCraft_FileListener.loadExperience(player.getName());
		 if (NavyCraft.playerExp.containsKey(player.getName())) {
			newExp = NavyCraft.playerExp.get(player.getName()) + newExp;
			NavyCraft.playerExp.put(player.getName(), newExp);
		} else {
			NavyCraft.playerExp.put(player.getName(), newExp);
		}
		 	Essentials ess;
			ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			if( ess == null )
			{
				player.sendMessage("Essentials Economy error");
				return;
			}
			try {
				ess.getUser(player).giveMoney(new BigDecimal(cash));
				player.sendMessage(ChatColor.GRAY + "You were rewarded with " + ChatColor.GREEN + "$" + cash + ChatColor.GRAY + ".");
			} catch (MaxMoneyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		player.sendMessage(ChatColor.GRAY + "You were rewarded with " + ChatColor.GREEN + rewardedExp + ChatColor.GRAY + " rank points.");
		player.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + newExp + ChatColor.GRAY + " rank points.");
			
		checkRankWorld(player, newExp, player.getWorld());	
		NavyCraft_FileListener.saveExperience(player.getName());
	}
	
	public static void rewardExpCraft(int newExp, Craft craft) {
		int playerNewExp = newExp;
		for (String s : craft.crewNames) {
			Player p = plugin.getServer().getPlayer(s);
			if (p != null) {
				NavyCraft_FileListener.loadExperience(p.getName());
				playerNewExp = newExp;
				if (NavyCraft.playerExp.containsKey(p.getName())) {
					playerNewExp = NavyCraft.playerExp.get(p.getName()) + newExp;
					NavyCraft.playerExp.put(p.getName(), playerNewExp);
				} else {
					NavyCraft.playerExp.put(p.getName(), playerNewExp);
				}
				NavyCraft_FileListener.saveExperience(p.getName());
			 	Essentials ess;
				ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
				if( ess == null )
				{
					p.sendMessage("Essentials Economy error");
					return;
				}
				try {
					ess.getUser(p.getName()).giveMoney(new BigDecimal(newExp / 2));
					p.sendMessage(ChatColor.GRAY + "You were rewarded with " + ChatColor.GREEN + "$" + newExp / 2 + ChatColor.GRAY + ".");
				} catch (MaxMoneyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				p.sendMessage(ChatColor.GRAY + "You were rewarded with " + ChatColor.GREEN + newExp + ChatColor.GRAY + " rank points.");
				p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + playerNewExp + ChatColor.GRAY + " rank points.");
				checkRankWorld(p, playerNewExp, craft.world);
			}
		}
	}
	
	public static void setExpPlayer(int newExp, String p) {
		NavyCraft.playerExp.put(p, newExp);
		NavyCraft_FileListener.saveExperience(p);
	}
	
	public static void removeExpPlayer(int newExp, String p) {
		if (NavyCraft.playerExp.containsKey(p)) {
			newExp = NavyCraft.playerExp.get(p) - newExp;
			NavyCraft.playerExp.put(p, newExp);
		} else {
			NavyCraft.playerExp.put(p, newExp);
		}
		NavyCraft_FileListener.saveExperience(p);
	}
	
	public static void addExpPlayer(int newExp, String p) {
		if (NavyCraft.playerExp.containsKey(p)) {
			newExp = NavyCraft.playerExp.get(p) + newExp;
			NavyCraft.playerExp.put(p, newExp);
		} else {
			NavyCraft.playerExp.put(p, newExp);
		}
		NavyCraft_FileListener.saveExperience(p);
	}
	public static void checkRankWorld(Player playerIn, int newExp, World world) {
		String worldName = world.getName();
		
		pex = (PermissionsEx)plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
		if( pex==null )
			return;
		
		for(String s:PermissionsEx.getUser(playerIn).getPermissions(worldName)) {
			if( s.contains("navycraft") ) {
				if( s.contains("exp") ) {
					String[] split = s.split("\\.");
					try {
						int rankExp = Integer.parseInt(split[2]);
						String rankName = "";
						while( newExp >= rankExp ) {
							PermissionsEx.getUser(playerIn).promote(null, "navycraft");
							for(String p:PermissionsEx.getUser(playerIn).getPermissions(worldName)) {
							if( p.contains("navycraft") ) {
								if( p.contains("exp") ) {
								String[] split2 = p.split("\\.");
								try {
								rankExp = Integer.parseInt(split2[2]);
								} catch (Exception ex) {
									ex.printStackTrace();
									System.out.println("Invalid perm-" + p);
								}
						}
					}
							}
						}
						List<String> groupNames = PermissionsEx.getUser(playerIn).getParentIdentifiers("navycraft");
						for( String group : groupNames ) {
							if( PermissionsEx.getPermissionManager().getGroup(group).getRankLadder().equalsIgnoreCase("navycraft") ) {
								if (rankName != group) {
								rankName = group;
								break;
								} else {
									return;
								}
							}
						}
						plugin.getServer().broadcastMessage(ChatColor.GREEN + playerIn.getName() + " has been promoted to the rank of " + ChatColor.YELLOW + rankName.toUpperCase() + ChatColor.GREEN + "!");
						
							
					} catch (Exception ex) {
						ex.printStackTrace();
						System.out.println("Invalid perm-" + s);
					}
				}
			}
		}
	}
}
