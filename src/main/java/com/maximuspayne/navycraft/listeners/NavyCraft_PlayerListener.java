package com.maximuspayne.navycraft.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.maximuspayne.aimcannon.AimCannon;
import com.maximuspayne.aimcannon.AimCannonPlayerListener;
import com.maximuspayne.aimcannon.OneCannon;
import com.maximuspayne.aimcannon.Weapon;
import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Periscope;
import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.Pump;
import com.maximuspayne.navycraft.blocks.BlocksInfo;
import com.maximuspayne.navycraft.blocks.DataBlock;
import com.maximuspayne.navycraft.craft.Craft;
import com.maximuspayne.navycraft.craft.CraftMover;
import com.maximuspayne.navycraft.craft.CraftType;
import com.maximuspayne.navycraft.teleportfix.TeleportFix;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import ru.tehkode.permissions.bukkit.PermissionsEx;

@SuppressWarnings("deprecation")
public class NavyCraft_PlayerListener implements Listener {

	private static NavyCraft plugin;
	public WorldGuardPlugin wgp;
	public static PermissionsEx pex;
	public WorldEditPlugin wep;

	Thread timerThread;

	public NavyCraft_PlayerListener(NavyCraft p) {
		plugin = p;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		Craft craft = Craft.getPlayerCraft(player);

		if (craft != null) {
			if (craft.isNameOnBoard.get(player.getName())) {
				Craft.reboardNames.put(player.getName(), craft);

				craft.isNameOnBoard.put(player.getName(), false);
				if (craft.driverName == player.getName()) {
					craft.haveControl = false;
					craft.releaseHelm();
				}

				boolean abandonCheck = true;
				for (String s : craft.isNameOnBoard.keySet()) {
					if (craft.isNameOnBoard.get(s)) {
						abandonCheck = false;
					}
				}

				if (abandonCheck) {
					craft.abandoned = true;
					craft.captainAbandoned = true;
				} else if (player.getName() == craft.captainName) {
					craft.captainAbandoned = true;
				}
			}

			for (Periscope p : craft.periscopes) {
				if (p.user == player) {
					p.user = null;
					break;
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		NavyCraft_FileListener.loadPlayerData(player.getName());
		if (Craft.reboardNames.containsKey(player.getName())) {
			if ((Craft.reboardNames.get(player.getName()) != null) && Craft.reboardNames.get(player.getName()).crewNames.contains(player.getName())) {
				Craft c = Craft.reboardNames.get(player.getName());
				Location loc = new Location(c.world, c.minX + (c.sizeX / 2), c.maxY, c.minZ + (c.sizeZ / 2));
				player.teleport(loc);

			}
			Craft.reboardNames.remove(player.getName());

		}
		String worldName = player.getWorld().getName();

		pex = (PermissionsEx) plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
		if (pex == null) {
			return;
		}

		if (!NavyCraft.playerPayDays.containsKey(player.getName()) || (NavyCraft.playerPayDays.containsKey(player.getName()) && (((System.currentTimeMillis() - NavyCraft.playerPayDays.get(player.getName())) / 1000) > 86400))) {

			for (String s : PermissionsEx.getUser(player).getPermissions(worldName)) {
				if (s.contains("navycraft")) {
					if (s.contains("pay")) {
						String[] split = s.split("\\.");
						try {
							int pay = Integer.parseInt(split[2]);

							List<String> groupNames = PermissionsEx.getUser(player).getParentIdentifiers("navycraft");
							String rankName = "";
							for (String group : groupNames) {
								if (PermissionsEx.getPermissionManager().getGroup(group).getRankLadder().equalsIgnoreCase("navycraft")) {
									rankName = group;
									break;
								}
							}

							player.sendMessage(ChatColor.GREEN + "Pay day! Your pay rate is:" + ChatColor.WHITE + rankName.toUpperCase());
							NavyCraft.econ.depositPlayer(player, pay);
							// ess.getUser(player).giveMoney(new BigDecimal(pay));
							NavyCraft.playerPayDays.put(player.getName(), System.currentTimeMillis());

						} catch (Exception ex) {
							ex.printStackTrace();
							System.out.println("Invalid perm-" + s);
						}
					}
				}
			}
		}
	}

	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent event) {
		String deathMsg = event.getDeathMessage();

		if ((NavyCraft.battleMode > 0) && PermissionInterface.CheckEnabledWorld(event.getEntity().getLocation())) {
			CraftMover.battleLogger(deathMsg);
		}

		String[] msgWords = deathMsg.split("\\s");
		if (msgWords.length == 5) {
			if (msgWords[1].equalsIgnoreCase("was") && msgWords[3].equalsIgnoreCase("by")) {
				Player p = plugin.getServer().getPlayer(msgWords[4]);
				if ((p != null) && PermissionInterface.CheckEnabledWorld(p.getLocation())) {
					int newExp = 100;

					plugin.getServer().broadcastMessage(ChatColor.GREEN + p.getName() + " receives " + ChatColor.YELLOW + newExp + ChatColor.GREEN + " rank points!");
					{
						NavyCraft_BlockListener.rewardExpPlayer(newExp, p);
						NavyCraft_BlockListener.checkRankWorld(p, newExp, p.getWorld());
						NavyCraft_FileListener.saveExperience(p.getName());
					}
				}
			}
		}

		if (!NavyCraft.playerKits.isEmpty() && NavyCraft.playerKits.contains(event.getEntity().getName())) {
			NavyCraft.playerKits.remove(event.getEntity().getName());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		Craft craft = Craft.getPlayerCraft(player);

		if (NavyCraft.flakGunnersList.contains(player) && ((event.getFrom().getBlockX() != event.getTo().getBlockX()) || (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {
			NavyCraft.flakGunnersList.remove(player);
			if (player.getInventory().contains(Material.BLAZE_ROD)) {
				player.getInventory().remove(Material.BLAZE_ROD);
			}
			player.sendMessage(ChatColor.GOLD + "You get off the Flak-Gun.");
		}

		if (NavyCraft.aaGunnersList.contains(player) && ((event.getFrom().getBlockX() != event.getTo().getBlockX()) || (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {
			NavyCraft.aaGunnersList.remove(player);
			if (player.getInventory().contains(Material.BLAZE_ROD)) {
				player.getInventory().remove(Material.BLAZE_ROD);
			}
			player.sendMessage(ChatColor.GOLD + "You get off the AA-Gun.");

		} else if (NavyCraft.searchLightMap.containsKey(player) && ((event.getFrom().getBlockX() != event.getTo().getBlockX()) || (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {

			TeleportFix.updateNMSLight(null, NavyCraft.searchLightMap.get(player));
			NavyCraft.searchLightMap.remove(player);
			if (player.getInventory().contains(Material.BLAZE_ROD)) {
				player.getInventory().remove(Material.BLAZE_ROD);
			}
			player.sendMessage(ChatColor.GOLD + "You get off the searchlight.");

		} else if (craft != null) {

			if (craft.isMovingPlayers) {
				return;
			}

			Periscope playerScope = null;
			for (Periscope p : craft.periscopes) {
				if (p.user == player) {
					playerScope = p;
					break;
				}
			}

			if (!craft.isNameOnBoard.isEmpty() && craft.isNameOnBoard.containsKey(player.getName()) && craft.isNameOnBoard.get(player.getName()) && !craft.isOnCraft(player, false)) {
				if (craft.customName != null) {
					player.sendMessage(ChatColor.GOLD + "You get off the " + craft.customName);
				} else {
					player.sendMessage(ChatColor.GOLD + "You get off the " + craft.name + " class.");

				}

				craft.isNameOnBoard.put(player.getName(), false);
				if (craft.driverName == player.getName()) {
					player.sendMessage(ChatColor.GOLD + "You release the helm");
					craft.haveControl = false;
					craft.releaseHelm();
				}

				boolean abandonCheck = true;
				for (String s : craft.isNameOnBoard.keySet()) {
					if (craft.isNameOnBoard.get(s)) {
						abandonCheck = false;
					}
				}

				if (abandonCheck) {
					craft.abandoned = true;
					craft.captainAbandoned = true;
				} else if (player.getName() == craft.captainName) {
					craft.captainAbandoned = true;
				}

			} else if (craft.isNameOnBoard.containsKey(player.getName()) && !craft.isNameOnBoard.get(player.getName()) && craft.isOnCraft(player, false)) {
				player.sendMessage(ChatColor.GOLD + "Welcome on board");

				craft.isNameOnBoard.put(player.getName(), true);

				if (craft.abandoned) {
					craft.abandoned = false;
				}
				if (craft.captainAbandoned && player.getName().equalsIgnoreCase(craft.captainName)) {
					craft.captainAbandoned = false;
				}

				if (player.getName() == craft.driverName) {
					craft.haveControl = true;

				}
			} else if (craft.type.listenMovement == true) {
				Location fromLoc = event.getFrom();
				Location toLoc = event.getTo();
				int dx = toLoc.getBlockX() - fromLoc.getBlockX();
				int dy = toLoc.getBlockY() - fromLoc.getBlockY();
				int dz = toLoc.getBlockZ() - fromLoc.getBlockZ();

				CraftMover cm = new CraftMover(craft, plugin);
				cm.calculateMove(dx, dy, dz);

				//// periscope
			} else if ((playerScope != null) && ((event.getFrom().getBlockX() != event.getTo().getBlockX()) || (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {

				CraftMover cmer = new CraftMover(craft, plugin);
				cmer.structureUpdate(null, false);
				craft.lastPeriscopeYaw = player.getLocation().getYaw();
				Location newLoc = new Location(craft.world, playerScope.signLoc.getBlockX() + .5, playerScope.signLoc.getBlockY() - .5, playerScope.signLoc.getBlockZ() + .5);
				newLoc.setYaw(player.getLocation().getYaw());
				player.teleport(newLoc);
				playerScope.user = null;
				///// helm
			} else if ((craft.driverName == player.getName()) && ((event.getFrom().getBlockX() != event.getTo().getBlockX()) || (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {
				craft.releaseHelm();
				craft.haveControl = false;
				if (player.getInventory().contains(Material.GOLD_SWORD)) {
					player.getInventory().remove(Material.GOLD_SWORD);
				}
				player.sendMessage(ChatColor.GOLD + "You release the helm.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();

		Craft playerCraft = Craft.getPlayerCraft(player);

		if (action == Action.RIGHT_CLICK_BLOCK) {

			if (event.hasBlock()) {
				Block block = event.getClickedBlock();
				NavyCraft.instance.DebugMessage("The action has a block " + block + " associated with it.", 4);

				if ((block.getTypeId() == 63) || ((block.getTypeId() == 68) && (event.getHand() == EquipmentSlot.HAND))) {
					NavyCraft_BlockListener.ClickedASign(player, block, false);
					return;
				}

				if ((block.getType() == Material.SPONGE) && (event.getHand() == EquipmentSlot.HAND)) {
					Craft testCraft = Craft.getCraft(block.getX(), block.getY(), block.getZ());
					if (testCraft != null) {
						for (Pump p : testCraft.pumps) {
							if (p.loc.equals(block.getLocation())) {
								player.sendMessage("Pump has " + (p.limit - p.counter) + " charges left.");
								break;
							}
						}
					}
				}

				if ((block.getType() == Material.JACK_O_LANTERN) && (event.getHand() == EquipmentSlot.HAND)) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.basic")) {
						player.sendMessage(ChatColor.RED + "You do not have permission to use this.");
						return;
					}

					if (player.getItemInHand().getTypeId() > 0) {
						player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
						return;
					}

					Location newLoc = new Location(player.getWorld(), block.getLocation().getBlockX(), block.getLocation().getBlockY() + 1, block.getLocation().getBlockZ());
					player.teleport(newLoc);
					player.setItemInHand(new ItemStack(369, 1));
					NavyCraft.searchLightMap.put(player, newLoc);
					player.sendMessage(ChatColor.GOLD + "Manning Searchlight! Left Click with Blaze Rod to point!");
					player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
				}

				if ((block.getTypeId() == 69) && (block.getRelative(BlockFace.DOWN, 1).getTypeId() == 68) && (event.getHand() == EquipmentSlot.HAND)) {
					Craft testCraft = Craft.getCraft(block.getX(), block.getY(), block.getZ());
					if (testCraft != null) {
						Sign sign = (Sign) block.getRelative(BlockFace.DOWN, 1).getState();

						if ((sign.getLine(0) == null) || sign.getLine(0).trim().equals("")) {
							return;
						}

						String craftTypeName = sign.getLine(0).trim().toLowerCase();

						// remove colors
						craftTypeName = craftTypeName.replaceAll(ChatColor.BLUE.toString(), "");

						// remove brackets
						if (craftTypeName.startsWith("[")) {
							craftTypeName = craftTypeName.substring(1, craftTypeName.length() - 1);
						}

						if (craftTypeName.equalsIgnoreCase("periscope")) {

							CraftMover cmer = new CraftMover(testCraft, plugin);
							cmer.structureUpdate(null, false);
							for (Periscope p : testCraft.periscopes) {
								if ((p.signLoc.getBlockX() == block.getRelative(BlockFace.DOWN, 1).getLocation().getBlockX()) && (p.signLoc.getBlockY() == block.getRelative(BlockFace.DOWN, 1).getLocation().getBlockY()) && (p.signLoc.getBlockZ() == block.getRelative(BlockFace.DOWN, 1).getLocation().getBlockZ())) {
									if (p.raised && !p.destroyed && (p.scopeLoc != null)) {
										if ((p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 1).getTypeId() == 113) && (p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 2).getTypeId() == 113) && (p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 3).getTypeId() == 113) && (p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 4).getTypeId() == 113)) {
											p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 1).setTypeId(0);
											p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 2).setTypeId(0);
											p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 3).setTypeId(0);
											p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 4).setTypeId(0);
											p.raised = false;
											CraftMover cm = new CraftMover(testCraft, plugin);
											cm.structureUpdate(null, false);

											player.sendMessage("Down Periscope!");
										} else {
											p.destroyed = true;
										}
									} else if (!p.destroyed && (p.scopeLoc != null)) {
										p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 1).setTypeId(113);
										testCraft.addBlock(p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 1), true);
										p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 2).setTypeId(113);
										testCraft.addBlock(p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 2), true);
										p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 3).setTypeId(113);
										testCraft.addBlock(p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 3), true);
										p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 4).setTypeId(113);
										testCraft.addBlock(p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 4), true);
										p.raised = true;
										CraftMover cm = new CraftMover(testCraft, plugin);
										cm.structureUpdate(null, false);
										player.sendMessage("Up Periscope!");
									} else {
										player.sendMessage("Periscope Destroyed!");
									}
								}
							}
						}
					}
					return;
				}

				if ((NavyCraft.instance.getConfig().getString("RequireHelm") == "true") && (playerCraft != null)) {
					playerCraft.addBlock(block, false);
				}

			}

			if ((playerCraft != null) && (playerCraft.driverName == player.getName())) {
				if ((NavyCraft.instance.getConfig().getString("RequireHelm") == "true") && (event.getItem().getTypeId() != playerCraft.type.HelmControllerItem)) {
					return;
				}
				if (event.getHand() == EquipmentSlot.HAND) {
					playerUsedAnItem(player, playerCraft);
				}
			} else {
				Vector pVel = player.getVelocity();
				if ((player.getLocation().getPitch() < 90) || (player.getLocation().getPitch() > 180)) {
					pVel.setX(pVel.getX() + 1);
				} else {
					pVel.setY(pVel.getY() + 1);
				}
			}
		}

		if ((action == Action.LEFT_CLICK_BLOCK) && event.hasBlock() && (event.getHand() == EquipmentSlot.HAND)) {
			Block block = event.getClickedBlock();
			if ((block.getTypeId() == 63) || (block.getTypeId() == 68)) {
				NavyCraft_BlockListener.ClickedASign(player, block, true);
				return;
			}
		}

		// fire airplane gun
		if ((action == Action.LEFT_CLICK_AIR) && (player.getItemInHand().getType() == Material.GOLD_SWORD) && (event.getHand() == EquipmentSlot.HAND)) {
			Craft testCraft = Craft.getPlayerCraft(event.getPlayer());
			if ((testCraft != null) && (testCraft.driverName == player.getName()) && testCraft.type.canFly && !testCraft.sinking && !testCraft.helmDestroyed) {
				Egg newEgg = player.launchProjectile(Egg.class);

				newEgg.setVelocity(newEgg.getVelocity().multiply(2.0f));
				NavyCraft.explosiveEggsList.add(newEgg);
				event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
				CraftMover.playWeaponSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5.0f, 1.70f);

			}

		}

		// AA Gunner...
		if ((action == Action.LEFT_CLICK_AIR) && NavyCraft.aaGunnersList.contains(player) && (player.getItemInHand().getType() == Material.BLAZE_ROD) && (event.getHand() == EquipmentSlot.HAND)) {
			Egg newEgg = player.launchProjectile(Egg.class);
			newEgg.setVelocity(newEgg.getVelocity().multiply(2.0f));
			NavyCraft.explosiveEggsList.add(newEgg);
			event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
			CraftMover.playWeaponSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5.0f, 1.70f);
		}
		// Flak Gunner...
		if ((action == Action.LEFT_CLICK_AIR) && NavyCraft.flakGunnersList.contains(player) && (player.getItemInHand().getType() == Material.BLAZE_ROD) && (event.getHand() == EquipmentSlot.HAND)) {
			Egg newEgg = player.launchProjectile(Egg.class);
			newEgg.setVelocity(newEgg.getVelocity().multiply(1.0f));
			NavyCraft.explosiveEggsList.add(newEgg);
			event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
			CraftMover.playWeaponSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5.0f, 1.70f);

			//// else check for movement clicking
		} else if ((action == Action.RIGHT_CLICK_AIR) && (playerCraft != null) && (playerCraft.driverName == player.getName()) && (playerCraft.type.listenItem == true)) {
			if ((NavyCraft.instance.getConfig().getString("RequireHelm") == "true") && (event.getItem().getTypeId() != playerCraft.type.HelmControllerItem)) {
				return;
			}
			playerUsedAnItem(player, playerCraft);
		}

		// Search light
		if ((action == Action.LEFT_CLICK_AIR) && NavyCraft.searchLightMap.containsKey(player) && (player.getItemInHand().getType() == Material.BLAZE_ROD) && (event.getHand() == EquipmentSlot.HAND)) {
			Set<Material> transp = new HashSet<>();
			transp.add(Material.AIR);
			Block block = null;
			try {
				block = player.getTargetBlock(transp, 100);
			} catch (IllegalStateException e) {
			}

			if (block != null) {
				TeleportFix.updateNMSLight(block.getLocation(), NavyCraft.searchLightMap.get(player));
				NavyCraft.searchLightMap.put(player, block.getLocation());
				player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
			}

		}

		AimCannonPlayerListener.onPlayerInteract(event);
	}

	public void playerUsedAnItem(Player player, Craft craft) {

		// minimum time between 2 swings
		if ((System.currentTimeMillis() - craft.lastMove) < (1.0 * 1000)) {
			return;
		}

		if (craft.blockCount <= 0) {
			craft.releaseCraft();
			return;
		}

		ItemStack pItem = player.getItemInHand();
		int item = pItem.getTypeId();

		// the craft won't budge if you have any tool in the hand
		if (!craft.haveControl) {
			if (((item == craft.type.HelmControllerItem) || (item == Integer.parseInt(NavyCraft.instance.getConfig().getString("HelmID")))) && !craft.isOnCraft(player, true)) {
				if (craft.haveControl) {
				} else {
					NavyCraft_Timer timer = NavyCraft_Timer.playerTimers.get(player);
					if (timer != null) {
						timer.Destroy();
					}
				}
				craft.haveControl = !craft.haveControl;
			} else {
				return;
			}
		} else if (item == Material.GOLD_SWORD.getId()) {
			if (craft.type.doesCruise && craft.autoTurn) {
				float rotation = ((float) Math.PI * player.getLocation().getYaw()) / 180f;
				float nx = -(float) Math.sin(rotation);
				float nz = (float) Math.cos(rotation);

				int dx = (Math.abs(nx) >= 0.5 ? 1 : 0) * (int) Math.signum(nx);
				int dz = (Math.abs(nz) > 0.5 ? 1 : 0) * (int) Math.signum(nz);
				int dy = 0;

				///// Planes
				if (craft.type.canFly || craft.type.canDig) {

					float p = player.getLocation().getPitch();

					dy = -(Math.abs(p) >= 45 ? 1 : 0) * (int) Math.signum(p);

					if (dy == 1) {
						if (craft.vertPlanes == 0) {
							craft.vertPlanes = 1;
							player.sendMessage(ChatColor.GOLD + "Up Elevator");
						} else if (craft.vertPlanes == -1) {
							craft.vertPlanes = 0;
							player.sendMessage(ChatColor.GOLD + "Neutral Elevator");
						} else {
							player.sendMessage(ChatColor.RED + "Elevator already up");
						}
						return;
					} else if (dy == -1) {
						if (craft.vertPlanes == 0) {
							craft.vertPlanes = -1;
							player.sendMessage(ChatColor.GOLD + "Down Elevator");
						} else if (craft.vertPlanes == 1) {
							craft.vertPlanes = 0;
							player.sendMessage(ChatColor.GOLD + "Neutral Elevator");
						} else {
							player.sendMessage(ChatColor.RED + "Elevator already down");
						}
						return;
					}
				}

				/// subs
				if (craft.type.canDive) {
					float p = player.getLocation().getPitch();

					dy = -(Math.abs(p) >= 45 ? 1 : 0) * (int) Math.signum(p);

					if (dy != 0) {
						if (!craft.submergedMode) {
							player.sendMessage(ChatColor.RED + "Set engines to dive first.");
							return;
						}
					}

					if (dy == 1) {
						if (craft.vertPlanes == 0) {
							craft.vertPlanes = 1;
							player.sendMessage(ChatColor.GOLD + "Diving Planes Up Bubble");
						} else if (craft.vertPlanes == -1) {
							craft.vertPlanes = 0;
							player.sendMessage(ChatColor.GOLD + "Diving Planes Neutral");
						} else {
							player.sendMessage(ChatColor.RED + "Diving Planes already up");
						}
						return;
					} else if (dy == -1) {
						if (craft.vertPlanes == 0) {
							craft.vertPlanes = -1;
							player.sendMessage(ChatColor.GOLD + "Diving Planes Down Bubble");
						} else if (craft.vertPlanes == 1) {
							craft.vertPlanes = 0;
							player.sendMessage(ChatColor.GOLD + "Diving Planes Neutral");
						} else {
							player.sendMessage(ChatColor.RED + "Diving Planes already down");
						}
						return;
					}
				}

				///// turning

				//// north
				if ((craft.rotation % 360) == 0) {
					if (nx > 0.866) {
						if (craft.rudder == 0) {
							player.sendMessage(ChatColor.GOLD + "Turning to heading 090");
						}
						craft.rudderChange(player, 1, true);
						return;
					} else if (nx < -0.866) {
						if (craft.rudder == 0) {
							player.sendMessage(ChatColor.GOLD + "Turning to heading 270");
						}
						craft.rudderChange(player, -1, true);
						return;
					} else if (dx == 1) {
						craft.rudderChange(player, 1, false);
						return;
					} else if (dx == -1) {
						craft.rudderChange(player, -1, false);
						return;
					} else if (dz == -1) {
						if (player.isSneaking()) {
							craft.gearChange(player, true);
						} else {
							craft.speedChange(player, true);
						}
						return;
					} else if (dz == 1) {
						if (player.isSneaking()) {
							craft.gearChange(player, false);

						} else {
							craft.speedChange(player, false);
						}
						return;
					}

					//// south
				} else if (craft.rotation == 180) {

					if (nx > 0.866) {
						if (craft.rudder == 0) {
							player.sendMessage(ChatColor.GOLD + "Turning to heading 090");
						}
						craft.rudderChange(player, -1, true);
						return;
					} else if (nx < -0.866) {
						if (craft.rudder == 0) {
							player.sendMessage(ChatColor.GOLD + "Turning to heading 270");
						}
						craft.rudderChange(player, 1, true);
						return;
					} else if (dx == 1) {
						craft.rudderChange(player, -1, false);
						return;
					} else if (dx == -1) {
						craft.rudderChange(player, 1, false);
						return;
					} else if (dz == -1) {
						if (player.isSneaking()) {
							craft.gearChange(player, false);
						} else {
							craft.speedChange(player, false);
						}
						return;
					} else if (dz == 1) {
						if (player.isSneaking()) {
							craft.gearChange(player, true);

						} else {
							craft.speedChange(player, true);
						}
						return;
					}

					//// east
				} else if (craft.rotation == 90) {

					if (nz > 0.866) {
						if (craft.rudder == 0) {
							player.sendMessage(ChatColor.GOLD + "Turning to heading 180");
						}
						craft.rudderChange(player, 1, true);
						return;
					} else if (nz < -0.866) {
						if (craft.rudder == 0) {
							player.sendMessage(ChatColor.GOLD + "Turning to heading 000");
						}
						craft.rudderChange(player, -1, true);
						return;
					} else if (dz == 1) {
						craft.rudderChange(player, 1, false);
						return;
					} else if (dz == -1) {
						craft.rudderChange(player, -1, false);
						return;
					} else if (dx == -1) {
						if (player.isSneaking()) {
							craft.gearChange(player, false);
						} else {
							craft.speedChange(player, false);
						}
						return;
					} else if (dx == 1) {
						if (player.isSneaking()) {
							craft.gearChange(player, true);

						} else {
							craft.speedChange(player, true);
						}
						return;
					}
					//// west
				} else if (craft.rotation == 270) {
					if (nz > 0.866) {
						if (craft.rudder == 0) {
							player.sendMessage(ChatColor.GOLD + "Turning to heading 180");
						}
						craft.rudderChange(player, -1, true);
						return;
					} else if (nz < -0.866) {
						if (craft.rudder == 0) {
							player.sendMessage(ChatColor.GOLD + "Turning to heading 000");
						}
						craft.rudderChange(player, 1, true);
						return;
					} else if (dz == 1) {
						craft.rudderChange(player, -1, false);
						return;
					} else if (dz == -1) {
						craft.rudderChange(player, 1, false);
						return;
					} else if (dx == -1) {
						if (player.isSneaking()) {
							craft.gearChange(player, true);
						} else {
							craft.speedChange(player, true);
						}
						return;
					} else if (dx == 1) {
						if (player.isSneaking()) {
							craft.gearChange(player, false);

						} else {
							craft.speedChange(player, false);
						}
						return;
					}
				}

			} else // old style movement
			{
				float rotation = ((float) Math.PI * player.getLocation().getYaw()) / 180f;

				float nx = -(float) Math.sin(rotation);
				float nz = (float) Math.cos(rotation);

				int dx = (Math.abs(nx) >= 0.5 ? 1 : 0) * (int) Math.signum(nx);
				int dz = (Math.abs(nz) > 0.5 ? 1 : 0) * (int) Math.signum(nz);
				int dy = 0;

				// we are on a flying object, handle height change
				if (craft.type.canFly || craft.type.canDive || craft.type.canDig) {

					float p = player.getLocation().getPitch();

					dy = -(Math.abs(p) >= 25 ? 1 : 0) * (int) Math.signum(p);

					// move straight up or straight down
					if (Math.abs(player.getLocation().getPitch()) >= 75) {
						dx = 0;
						dz = 0;
					}
				}

				if (craft.autoTurn) {
					if ((craft.rotation % 360) == 0) {
						if (nx > 0.866) {
							craft.turn(90);
							return;
						} else if (nx < -0.866) {
							craft.turn(270);
							return;
						}
					} else if (craft.rotation == 180) {
						if (nx > 0.866) {
							craft.turn(270);
							return;
						} else if (nx < -0.866) {
							craft.turn(90);
							return;
						}
					} else if (craft.rotation == 90) {
						if (nz > 0.866) {
							craft.turn(90);
							return;
						} else if (nz < -0.866) {
							craft.turn(270);
							return;
						}
					} else if (craft.rotation == 270) {
						if (nz > 0.866) {
							craft.turn(270);
							return;
						} else if (nz < -0.866) {
							craft.turn(90);
							return;
						}
					}
				}
				CraftMover cm = new CraftMover(craft, plugin);
				cm.calculateMove(dx, dy, dz);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
			Player player = event.getPlayer();
			Craft craft = Craft.getPlayerCraft(player);

			if ((player.getItemInHand().getType() == Material.FLINT_AND_STEEL) && NavyCraft.cleanupPlayers.contains(player.getName()) && PermissionInterface.CheckEnabledWorld(player.getLocation())) {
				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);
				transp.add(Material.STATIONARY_WATER);
				transp.add(Material.WATER);
				Block block = player.getTargetBlock(transp, 300);
				if (block != null) {
					Craft c = Craft.getCraft(block.getX(), block.getY(), block.getZ());
					if (c != null) {
						if (!((c.captainName != null) && (plugin.getServer().getPlayer(c.captainName) != null) && plugin.getServer().getPlayer(c.captainName).isOnline())) {
							if (craft != null) {
								craft.leaveCrew(player);
							}

							c.buildCrew(player, false);

							System.out.println("Vehicle destroyed by:" + player.getName() + " Name:" + c.name + " X:" + c.getLocation().getBlockX() + " Y:" + c.getLocation().getBlockY() + " Z:" + c.getLocation().getBlockZ());
							c.doDestroy = true;
							player.sendMessage(ChatColor.GREEN + "Vehicle destroyed.");
						} else {
							player.sendMessage(ChatColor.RED + "Vehicle's captain is online.");
						}
					} else {
						block.getRelative(BlockFace.UP, 1).setTypeId(63);
						Sign sign = (Sign) block.getRelative(BlockFace.UP, 1).getState();
						sign.setLine(0, "Ship");
						sign.update();

						Craft theCraft = NavyCraft.instance.createCraft(player, CraftType.getCraftType("ship"), sign.getX(), sign.getY(), sign.getZ(), "ship", 0, block.getRelative(BlockFace.UP, 1));
						if (theCraft != null) {
							if (!NavyCraft.checkNoDriveRegion(theCraft.getLocation())) {
								System.out.println("Vehicle destroyed by:" + player.getName() + " Name:" + theCraft.name + " X:" + theCraft.getLocation().getBlockX() + " Y:" + theCraft.getLocation().getBlockY() + " Z:" + theCraft.getLocation().getBlockZ());
								theCraft.doDestroy = true;
								player.sendMessage(ChatColor.GREEN + "Vehicle destroyed.");
							} else {
								player.sendMessage(ChatColor.RED + player.getName() + ", why are you trying to destroy a dock vehicle??");
								System.out.println(player.getName() + ", why are you trying to destroy a dock vehicle??");
							}
						} else {
							sign.setLine(0, "Aircraft");
							sign.update();
							theCraft = NavyCraft.instance.createCraft(player, CraftType.getCraftType("aircraft"), sign.getX(), sign.getY(), sign.getZ(), "aircraft", 0, block.getRelative(BlockFace.UP, 1));

							if (theCraft != null) {
								if (!NavyCraft.checkNoDriveRegion(theCraft.getLocation())) {
									System.out.println("Vehicle destroyed by:" + player.getName() + " Name:" + theCraft.name + " X:" + theCraft.getLocation().getBlockX() + " Y:" + theCraft.getLocation().getBlockY() + " Z:" + theCraft.getLocation().getBlockZ());
									theCraft.doDestroy = true;
									player.sendMessage(ChatColor.GREEN + "Vehicle destroyed.");
								} else {
									player.sendMessage(ChatColor.RED + player.getName() + ", why are you trying to destroy a dock vehicle??");
									System.out.println(player.getName() + ", why are you trying to destroy a dock vehicle??");
								}
							} else {
								player.sendMessage(ChatColor.RED + "No vehicle could be detected.");
								block.getRelative(BlockFace.UP, 1).setTypeId(0);
							}
						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "No block detected");
				}
				return;
			}
			if ((player.getItemInHand().getType() == Material.SHEARS) && NavyCraft.cleanupPlayers.contains(player.getName()) && PermissionInterface.CheckEnabledWorld(player.getLocation()) && !NavyCraft.checkSafeDockRegion(player.getLocation())) {

				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);

				Block block = player.getTargetBlock(transp, 300);

				if (block != null) {
					System.out.println("Shears used:" + player.getName() + " X:" + block.getX() + " Y:" + block.getY() + " Z:" + block.getZ());
					player.sendMessage(ChatColor.GOLD + "Shears used!");
					for (int x = block.getX() - 7; x <= (block.getX() + 7); x++) {
						for (int z = block.getZ() - 7; z <= (block.getZ() + 7); z++) {
							for (int y = block.getY() - 7; y <= (block.getY() + 7); y++) {
								Block theBlock = block.getWorld().getBlockAt(x, y, z);
								if (theBlock.getType() != Material.BEDROCK) {

									if (theBlock.getY() < 63) {
										theBlock.setType(Material.WATER);
									} else {
										theBlock.setType(Material.AIR);
									}
								}
								// TNT tnt = (TNT) theBlock.getState();
							}
						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "No block detected");
				}
				return;
			}
			if ((player.getItemInHand().getType() == Material.GOLD_SPADE) && NavyCraft.cleanupPlayers.contains(player.getName()) && PermissionInterface.CheckEnabledWorld(player.getLocation()) && !NavyCraft.checkSafeDockRegion(player.getLocation())) {
				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);
				transp.add(Material.STATIONARY_WATER);
				transp.add(Material.WATER);
				Block block = player.getTargetBlock(transp, 300);

				if (block != null) {
					System.out.println("Golden Shovel used:" + player.getName() + " X:" + block.getX() + " Y:" + block.getY() + " Z:" + block.getZ());
					player.sendMessage(ChatColor.GOLD + "Golden Shovel used!");
					for (int x = block.getX() - 7; x <= (block.getX() + 7); x++) {
						for (int z = block.getZ() - 7; z <= (block.getZ() + 7); z++) {
							for (int y = block.getY() - 7; y <= (block.getY() + 7); y++) {
								Block theBlock = block.getWorld().getBlockAt(x, y, z);
								for (Short s : CraftType.getCraftType("ship").structureBlocks) {
									if ((s == theBlock.getTypeId()) && (theBlock.getType() != Material.BEDROCK)) {
										if (theBlock.getY() < 63) {
											theBlock.setType(Material.WATER);
										} else {
											theBlock.setType(Material.AIR);
										}
									}
								}

							}
						}
					}
				} else {
					player.sendMessage("No block detected");
				}
				return;
			}

			if ((craft != null) && (craft.driverName == player.getName()) && (craft.type.listenAnimation == true)) {
				playerUsedAnItem(player, craft);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String[] split = event.getMessage().split(" ");
		split[0] = split[0].substring(1);

		// debug commands
		if (NavyCraft.instance.DebugMode == true) {
			if (split[0].equalsIgnoreCase("isDataBlock")) {
				player.sendMessage(Boolean.toString(BlocksInfo.isDataBlock(Integer.parseInt(split[1]))));
			} else if (split[0].equalsIgnoreCase("isComplexBlock")) {
				player.sendMessage(Boolean.toString(BlocksInfo.isComplexBlock(Integer.parseInt(split[1]))));

			} else if (split[0].equalsIgnoreCase("finddatablocks")) {
				Craft craft = Craft.getPlayerCraft(player);
				for (DataBlock dataBlock : craft.dataBlocks) {
					Block theBlock = player.getWorld().getBlockAt(new Location(player.getWorld(), craft.minX + dataBlock.x, craft.minY + dataBlock.y, craft.minZ + dataBlock.z));
					theBlock.setType(Material.GOLD_BLOCK);
				}
			} else if (split[0].equalsIgnoreCase("findcomplexblocks")) {
				Craft craft = Craft.getPlayerCraft(player);
				for (DataBlock dataBlock : craft.complexBlocks) {
					Block theBlock = player.getWorld().getBlockAt(new Location(player.getWorld(), craft.minX + dataBlock.x, craft.minY + dataBlock.y, craft.minZ + dataBlock.z));
					theBlock.setType(Material.GOLD_BLOCK);
				}
			} else if (split[0].equalsIgnoreCase("diamondit")) {
				Craft craft = Craft.getPlayerCraft(player);

				for (int x = 0; x < craft.sizeX; x++) {
					for (int y = 0; y < craft.sizeY; y++) {
						for (int z = 0; z < craft.sizeZ; z++) {
							if (craft.matrix[x][y][z] != -1) {
								Block theBlock = player.getWorld().getBlockAt(new Location(player.getWorld(), craft.minX + x, craft.minY + y, craft.minZ + z));
								theBlock.setType(Material.DIAMOND_BLOCK);
							}
						}
					}
				}
			} else if (split[0].equalsIgnoreCase("craftvars")) {
				Craft craft = Craft.getPlayerCraft(player);

				NavyCraft.instance.DebugMessage("Craft type: " + craft.type, 4);
				NavyCraft.instance.DebugMessage("Craft name: " + craft.name, 4);

				// may need to make multidimensional
				NavyCraft.instance.DebugMessage("Craft matrix size: " + craft.matrix.length, 4);
				NavyCraft.instance.DebugMessage("Craft block count: " + craft.blockCount, 4);
				NavyCraft.instance.DebugMessage("Craft data block count: " + craft.dataBlocks.size(), 4);
				NavyCraft.instance.DebugMessage("Craft complex block count: " + craft.complexBlocks.size(), 4);

				NavyCraft.instance.DebugMessage("Craft speed: " + craft.speed, 4);
				NavyCraft.instance.DebugMessage("Craft size: " + craft.sizeX + " * " + craft.sizeY + " * " + craft.sizeZ, 4);

				NavyCraft.instance.DebugMessage("Craft last move: " + craft.lastMove, 4);
				// world?
				NavyCraft.instance.DebugMessage("Craft center: " + craft.centerX + ", " + craft.centerZ, 4);

				NavyCraft.instance.DebugMessage("Craft water level: " + craft.waterLevel, 4);
				NavyCraft.instance.DebugMessage("Craft new water level: " + craft.newWaterLevel, 4);
				NavyCraft.instance.DebugMessage("Craft water type: " + craft.waterType, 4);

				NavyCraft.instance.DebugMessage("Craft bounds: " + craft.minX + "->" + craft.maxX + ", " + craft.minY + "->" + craft.maxY + ", " + craft.minZ + "->" + craft.maxZ, 4);

			} else if (split[0].equalsIgnoreCase("getRotation")) {
				Set<Material> meh = new HashSet<>();
				Block examineBlock = player.getTargetBlock(meh, 100);

				int blockDirection = BlocksInfo.getCardinalDirectionFromData(examineBlock.getTypeId(), examineBlock.getData());
				player.sendMessage("Block data is " + examineBlock.getData() + " direction is " + blockDirection);
			}
		}

		if (split[0].equalsIgnoreCase("navycraft") || split[0].equalsIgnoreCase("nc")) {
			if (split.length >= 2) {
				if (split[1].equalsIgnoreCase("types")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.basic")) {
						return;
					}
					for (CraftType craftType : CraftType.craftTypes) {
						if (craftType.canUse(player)) {
							player.sendMessage(ChatColor.GREEN + craftType.name + ChatColor.YELLOW + craftType.minBlocks + "-" + craftType.maxBlocks + " blocks" + " doesCruise : " + craftType.doesCruise);
						}
					}
				} else if (split[1].equalsIgnoreCase("list")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin")) {
						return;
					}
					if (Craft.craftList.isEmpty()) {
						player.sendMessage(ChatColor.RED + "No player controlled craft");
						// return true;
					}

					for (Craft craft : Craft.craftList) {

						player.sendMessage(ChatColor.YELLOW + "" + craft.craftID + " - " + craft.name + " commanded by " + craft.captainName + ": " + craft.blockCount + " blocks");
					}
				} else if (split[1].equalsIgnoreCase("reload")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin")) {
						return;
					}
					NavyCraft.instance.loadProperties();
					player.sendMessage(ChatColor.GREEN + "NavyCraft configuration reloaded");
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("debug")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin")) {
						return;
					}
					NavyCraft.instance.ToggleDebug();
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("loglevel")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin")) {
						return;
					}
					try {
						Integer.parseInt(split[2]);
						NavyCraft.instance.getConfig().set("LogLevel", split[2]);
					} catch (Exception ex) {
						player.sendMessage(ChatColor.RED + "Invalid loglevel.");
					}
					event.setCancelled(true);
					return;

					// Cleanup command
				} else if (split[1].equalsIgnoreCase("cleanup")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin")) {
						return;
					}
					if (NavyCraft.cleanupPlayers.contains(player.getName())) {
						NavyCraft.cleanupPlayers.remove(player.getName());
						player.sendMessage(ChatColor.GOLD + "Exiting cleanup mode.");
					} else {

						NavyCraft.cleanupPlayers.add(player.getName());
						player.sendMessage(ChatColor.GREEN + "Entering cleanup mode.");
					}
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("weapons")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin")) {
						return;
					}
					for (Weapon w : AimCannon.weapons) {
						player.sendMessage("weapon -" + w.weaponType);
					}
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("cannons")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin")) {
						return;
					}
					for (OneCannon c : AimCannon.cannons) {
						player.sendMessage("cannon -" + c.cannonType);
					}
					event.setCancelled(true);
					return;
					// admin ship commands
				} else if (split[1].equalsIgnoreCase("destroyShips")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin")) {
						return;
					}
					for (Craft c : Craft.craftList) {
						c.doDestroy = true;
					}
					player.sendMessage(ChatColor.GREEN + "All vehicles destroyed");
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("removeships")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin")) {
						return;
					}
					for (Craft c : Craft.craftList) {
						c.doRemove = true;
					}
					player.sendMessage(ChatColor.GREEN + "All vehicles removed");
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("tpShip") || split[1].equalsIgnoreCase("tp")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin")) {
						return;
					}
					int shipNum = -1;
					if (split.length == 3) {
						try {
							shipNum = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid ID Number");
							e.printStackTrace();
						}
					}
					if (shipNum != -1) {
						for (Craft c : Craft.craftList) {
							if (shipNum == c.craftID) {
								player.teleport(new Location(c.world, c.getLocation().getX(), c.maxY, c.getLocation().getZ()));
								event.setCancelled(true);
								return;
							}
						}
						player.sendMessage(ChatColor.RED + "ID Number not found");
					} else {
						player.sendMessage(ChatColor.RED + "Invalid ID Number");
					}

					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("help")) {
					if (PermissionInterface.CheckPerm(player, "navycraft.basic")) {
						player.sendMessage(ChatColor.GOLD + "NavyCraft v" + ChatColor.GREEN + NavyCraft.version + ChatColor.GOLD + " commands :");
						player.sendMessage(ChatColor.AQUA + "/navycraft types " + " : " + ChatColor.WHITE + "list the types of craft available");
						player.sendMessage(ChatColor.AQUA + "/[craft type] " + " : " + ChatColor.WHITE + "commands specific to the craft type try /ship help");
						player.sendMessage(ChatColor.AQUA + "/volume" + " : " + ChatColor.WHITE + "volume help");
						player.sendMessage(ChatColor.AQUA + "/rank" + " : " + ChatColor.WHITE + "rank status message");
						player.sendMessage(ChatColor.AQUA + "/sign undo " + " : " + ChatColor.WHITE + "undo a sign you paid for");
					}

					if (PermissionInterface.CheckQuietPerm(player, "navycraft.admin")) {
						player.sendMessage(ChatColor.RED + "NavyCraft Admin v" + ChatColor.GREEN + NavyCraft.version + ChatColor.RED + " commands :");
						player.sendMessage(ChatColor.BLUE + "/navycraft list : " + ChatColor.WHITE + "list all craft");
						player.sendMessage(ChatColor.BLUE + "/navycraft reload : " + ChatColor.WHITE + "reload config files");
						player.sendMessage(ChatColor.BLUE + "/navycraft config : " + ChatColor.WHITE + "display config settings");
						player.sendMessage(ChatColor.BLUE + "/navycraft cleanup : " + ChatColor.WHITE + "enables cleanup tools, use lighter, gold spade, and shears");
						player.sendMessage(ChatColor.BLUE + "/navycraft destroyships : " + ChatColor.WHITE + "destroys all active ships");
						player.sendMessage(ChatColor.BLUE + "/navycraft removeships : " + ChatColor.WHITE + "deactivates all active ships");
						player.sendMessage(ChatColor.BLUE + "/navycraft tpship id # : " + ChatColor.WHITE + "teleport to ship ID #");
					}
					event.setCancelled(true);
				} else {
					player.sendMessage("Unknown command. Type \"/navycraft help\" for help.");
					event.setCancelled(true);
					return;
				}
				// nc help
			} else {
				if (PermissionInterface.CheckPerm(player, "navycraft.basic")) {
					player.sendMessage(ChatColor.GOLD + "NavyCraft v" + ChatColor.GREEN + NavyCraft.version + ChatColor.GOLD + " commands :");
					player.sendMessage(ChatColor.AQUA + "/navycraft types " + " : " + ChatColor.WHITE + "list the types of craft available");
					player.sendMessage(ChatColor.AQUA + "/[craft type] " + " : " + ChatColor.WHITE + "commands specific to the craft type try /ship help");
					player.sendMessage(ChatColor.AQUA + "/volume" + " : " + ChatColor.WHITE + "volume help");
					player.sendMessage(ChatColor.AQUA + "/rank" + " : " + ChatColor.WHITE + "rank status message");
					player.sendMessage(ChatColor.AQUA + "/sign undo " + " : " + ChatColor.WHITE + "undo a sign you paid for");
				}

				if (PermissionInterface.CheckQuietPerm(player, "navycraft.admin")) {
					player.sendMessage(ChatColor.RED + "NavyCraft Admin v" + ChatColor.GREEN + NavyCraft.version + ChatColor.RED + " commands :");
					player.sendMessage(ChatColor.BLUE + "/navycraft list : " + ChatColor.WHITE + "list all craft");
					player.sendMessage(ChatColor.BLUE + "/navycraft reload : " + ChatColor.WHITE + "reload config files");
					player.sendMessage(ChatColor.BLUE + "/navycraft config : " + ChatColor.WHITE + "display config settings");
					player.sendMessage(ChatColor.BLUE + "/navycraft cleanup : " + ChatColor.WHITE + "enables cleanup tools, use lighter, gold spade, and shears");
					player.sendMessage(ChatColor.BLUE + "/navycraft destroyships : " + ChatColor.WHITE + "destroys all active ships");
					player.sendMessage(ChatColor.BLUE + "/navycraft removeships : " + ChatColor.WHITE + "deactivates all active ships");
					player.sendMessage(ChatColor.BLUE + "/navycraft tpship id # : " + ChatColor.WHITE + "teleport to ship ID #");
				}
				event.setCancelled(true);
				return;
			}
		}
		String craftName = split[0];

		CraftType craftType = CraftType.getCraftType(craftName);
		//// CREW chat
		if (craftName.equalsIgnoreCase("crew")) {
			Craft craft = Craft.getPlayerCraft(player);
			if (craft == null) {
				player.sendMessage(ChatColor.RED + "You are not on a crew!");
				event.setCancelled(true);
				return;
			}

			if (split.length == 1) {
				player.sendMessage(craft.name + "'s" + ChatColor.GOLD + " Crew:");
				if (craft.captainName != null) {
					player.sendMessage(ChatColor.DARK_AQUA + "Captain: " + ChatColor.GREEN + craft.captainName);
				}
				for (String s : craft.crewNames) {
					if (s != craft.captainName) {
						player.sendMessage(ChatColor.BLUE + s);
					}
				}
			} else {
				String msgString;
				msgString = ": " + ChatColor.RESET;
				for (int i = 1; i < split.length; i++) {
					msgString += split[i] + " ";
					msgString = ChatColor.translateAlternateColorCodes('&', msgString);
				}

				for (String s : craft.crewNames) {
					Player p = plugin.getServer().getPlayer(s);
					if (p != null) {
						if (player.getName() == craft.captainName) {
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "Captain" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + player.getName() + ChatColor.GRAY + msgString);

						} else {
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "Crew" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + player.getName() + ChatColor.GRAY + msgString);

						}
					}
				}

				if (player.getName() == craft.captainName) {
					System.out.println("[" + craft.name + "]" + "[Captain] " + player.getName() + msgString);
				} else {
					System.out.println("[" + craft.name + "]" + "[Crew] " + player.getName() + msgString);
				}
			}
			event.setCancelled(true);
			return;
		}
		// radio command
		else if (craftName.equalsIgnoreCase("radio") || craftName.equalsIgnoreCase("ra")) {
			Craft craft = Craft.getPlayerCraft(player);
			if (craft == null) {
				player.sendMessage(ChatColor.RED + "You are not on a crew!");
				event.setCancelled(true);
				return;
			}

			if (split.length == 1) {
				if ((craft.radioSignLoc != null) && (craft.maxY >= 63) && craft.radioSetOn) {
					player.sendMessage(ChatColor.GREEN + "Your radio is active on frequency: " + ChatColor.GREEN + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + "" + craft.radio4);

					int craftCount = 0;
					for (Craft c : Craft.craftList) {
						if (c.captainName != null) {
							if (c.radio1 == craft.radio1) {
								if (c.radio2 == craft.radio2) {
									if (c.radio3 == craft.radio3) {
										if (c.radio4 == craft.radio4) {
											craftCount++;
										}
									}
								}
							}
						}
					}
					player.sendMessage(ChatColor.GOLD + "There are " + ChatColor.GREEN + craftCount + ChatColor.GOLD + " vehicles on your frequency.");

				} else if ((craft.radioSignLoc != null) && craft.radioSetOn) {
					player.sendMessage(ChatColor.RED + "Your radio will not work underwater.");
				} else if (craft.radioSignLoc != null) {
					player.sendMessage(ChatColor.RED + "Your radio is turned off.");
				} else {
					player.sendMessage(ChatColor.RED + "No radio detected!");
				}
			} else {
				if (craft.radioSignLoc == null) {
					player.sendMessage(ChatColor.RED + "No radio detected!");
					event.setCancelled(true);
					return;
				}

				if (!craft.radioSetOn) {
					player.sendMessage(ChatColor.RED + "Your radio is turned off.");
					event.setCancelled(true);
					return;
				}

				if (craft.maxY < 63) {
					player.sendMessage(ChatColor.RED + "Your radio will not work underwater.");
					event.setCancelled(true);
					return;
				}

				if ((craft.radio1 == 0) && (craft.radio2 == 0) && (craft.radio3 == 0) && (craft.radio4 == 0)) {
					player.sendMessage(ChatColor.RED + "0000 is invalid frequency, use Radio sign to change.");
					event.setCancelled(true);
					return;
				}

				String msgString;
				msgString = ": " + ChatColor.RESET;
				for (int i = 1; i < split.length; i++) {
					msgString += split[i] + " ";
				}

				for (String s : craft.crewNames) {
					Player p = plugin.getServer().getPlayer(s);
					if (p != null) {
						if (craft.customName != null) {
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + craft.radio1 + craft.radio2 + craft.radio3 + craft.radio4 + ChatColor.DARK_GRAY + "] [" + craft.customName.toUpperCase() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + msgString);
						} else {
							p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + craft.radio1 + craft.radio2 + craft.radio3 + craft.radio4 + ChatColor.DARK_GRAY + "] [" + craft.name.toUpperCase() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + msgString);
						}
					}
				}

				for (Craft c : Craft.craftList) {
					if ((c != craft) && c.radioSetOn) {
						if (c.radio1 == craft.radio1) {
							if (c.radio2 == craft.radio2) {
								if (c.radio3 == craft.radio3) {
									if (c.radio4 == craft.radio4) {
										if ((c.world == craft.world) && (c.getLocation().distance(craft.getLocation()) < 5000)) {
											for (String s : c.crewNames) {
												Player p = plugin.getServer().getPlayer(s);
												if (p != null) {
													if (craft.customName != null) {
														p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + craft.radio1 + craft.radio2 + craft.radio3 + craft.radio4 + ChatColor.DARK_GRAY + "] [" + craft.customName.toUpperCase() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getName() + msgString);
													} else {
														p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + craft.radio1 + craft.radio2 + craft.radio3 + craft.radio4 + ChatColor.DARK_GRAY + "] [" + craft.name.toUpperCase() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getName() + msgString);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}

				if (craft.customName != null) {
					System.out.println("[" + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + "" + craft.radio4 + "] [" + craft.customName + "] " + player.getName() + msgString);
				} else {
					System.out.println("[" + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + "" + craft.radio4 + "] [" + craft.name + "] " + player.getName() + msgString);
				}

				craft.lastRadioPulse = System.currentTimeMillis();

			}
			event.setCancelled(true);
			return;
			// shipyard commands
		} else if (craftName.equalsIgnoreCase("shipyard") || craftName.equalsIgnoreCase("sy") || craftName.equalsIgnoreCase("yard")) {
			if (split.length > 1) {
				if (split[1].equalsIgnoreCase("load")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.load") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to reward plots.");
						event.setCancelled(true);
						return;
					}
					if (split.length < 3) {
						player.sendMessage(ChatColor.GOLD + "Usage - /shipyard load <type>");
						player.sendMessage(ChatColor.YELLOW + "Example - /shipyard load <type>");
						event.setCancelled(true);
						return;
					}

					String typeString = split[2];
					if (!typeString.equalsIgnoreCase("SHIP1") && !typeString.equalsIgnoreCase("SHIP2") && !typeString.equalsIgnoreCase("SHIP3") && !typeString.equalsIgnoreCase("SHIP4") && !typeString.equalsIgnoreCase("SHIP5") && !typeString.equalsIgnoreCase("HANGAR1") && !typeString.equalsIgnoreCase("HANGAR2") && !typeString.equalsIgnoreCase("TANK1") && !typeString.equalsIgnoreCase("TANK2") && !typeString.equalsIgnoreCase("all")) {
						player.sendMessage(ChatColor.RED + "Unknown lot type");
						event.setCancelled(true);
						return;
					}
					if (typeString.equalsIgnoreCase("SHIP1")) {
						NavyCraft_Timer.loadSHIP1();
					}
					if (typeString.equalsIgnoreCase("SHIP2")) {
						NavyCraft_Timer.loadSHIP2();
					}
					if (typeString.equalsIgnoreCase("SHIP3")) {
						NavyCraft_Timer.loadSHIP3();
					}
					if (typeString.equalsIgnoreCase("SHIP4")) {
						NavyCraft_Timer.loadSHIP4();
					}
					if (typeString.equalsIgnoreCase("SHIP5")) {
						NavyCraft_Timer.loadSHIP5();
					}
					if (typeString.equalsIgnoreCase("HANGAR1")) {
						NavyCraft_Timer.loadHANGAR1();
					}
					if (typeString.equalsIgnoreCase("HANGAR2")) {
						NavyCraft_Timer.loadHANGAR2();
					}
					if (typeString.equalsIgnoreCase("TANK1")) {
						NavyCraft_Timer.loadTANK1();
					}
					if (typeString.equalsIgnoreCase("TANK2")) {
						NavyCraft_Timer.loadTANK2();
					}
					if (typeString.equalsIgnoreCase("all")) {
						NavyCraft_Timer.loadSHIP1();
						NavyCraft_Timer.loadSHIP2();
						NavyCraft_Timer.loadSHIP3();
						NavyCraft_Timer.loadSHIP4();
						NavyCraft_Timer.loadSHIP5();
						NavyCraft_Timer.loadHANGAR1();
						NavyCraft_Timer.loadHANGAR2();
						NavyCraft_Timer.loadTANK1();
						NavyCraft_Timer.loadTANK2();
					}

					File shipyarddata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "shipyarddata");
					File f = new File(shipyarddata, File.separator + "signs.yml");
					FileConfiguration syData = YamlConfiguration.loadConfiguration(f);
					List<String> list = new ArrayList<String>(syData.getConfigurationSection("Signs").getKeys(false));
					int size = list.size();
					int amount = size;
					player.sendMessage(ChatColor.GREEN + "Loaded: " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + amount + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + " plots");
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("reward")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.reward") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to reward plots.");
						event.setCancelled(true);
						return;
					}

					if (split.length < 5) {
						player.sendMessage(ChatColor.GOLD + "Usage - /shipyard reward <player> <type> <amount>");
						player.sendMessage(ChatColor.YELLOW + "Example - /shipyard reward Solmex SHIP5 1");
						event.setCancelled(true);
						return;
					}

					String typeString = split[3];
					if (!typeString.equalsIgnoreCase("SHIP1") && !typeString.equalsIgnoreCase("SHIP2") && !typeString.equalsIgnoreCase("SHIP3") && !typeString.equalsIgnoreCase("SHIP4") && !typeString.equalsIgnoreCase("SHIP5") && !typeString.equalsIgnoreCase("HANGAR1") && !typeString.equalsIgnoreCase("HANGAR2") && !typeString.equalsIgnoreCase("TANK1") && !typeString.equalsIgnoreCase("TANK2") && !typeString.equalsIgnoreCase("MAP1") && !typeString.equalsIgnoreCase("MAP2") && !typeString.equalsIgnoreCase("MAP3") && !typeString.equalsIgnoreCase("MAP4") && !typeString.equalsIgnoreCase("MAP5")) {
						player.sendMessage(ChatColor.RED + "Unknown lot type");
						event.setCancelled(true);
						return;
					}

					String playerString = split[2];
					if ((plugin.getServer().getPlayer(playerString) == null) || !plugin.getServer().getPlayer(playerString).getName().equalsIgnoreCase(playerString)) {
						player.sendMessage(ChatColor.RED + "Player not found or not online.");
						event.setCancelled(true);
						return;
					}
					File userdata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "userdata");
					File f = new File(userdata, File.separator + playerString + ".yml");
					FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);

					int rewNum = 0;
					try {
						rewNum = Integer.parseInt(split[4]);
					} catch (NumberFormatException e) {
						player.sendMessage(ChatColor.RED + "Invalid Number");
						event.setCancelled(true);
						return;
					}

					if ((playerData.getInt(playerString + "." + typeString) <= 0) && (rewNum < 0)) {
						player.sendMessage(ChatColor.RED + "Cannot revoke plots below 0!");
						event.setCancelled(true);
						return;
					}

					NavyCraft_FileListener.saveRewardsFile(playerString, typeString, rewNum);

					if (rewNum < 0) {
						player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + Math.abs(rewNum) + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + typeString + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + " Plot's revoked from " + ChatColor.YELLOW + playerString);
						event.setCancelled(true);
						return;
					} else if (rewNum == -1) {
						player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + Math.abs(rewNum) + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + typeString + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + " Plot revoked from " + ChatColor.YELLOW + playerString);
						event.setCancelled(true);
						return;
					} else if (rewNum == 1) {
						player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + Math.abs(rewNum) + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + typeString + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + " Plot rewarded to " + ChatColor.YELLOW + playerString);
						event.setCancelled(true);
						return;
					} else {
						player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + Math.abs(rewNum) + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + typeString + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + " Plot's rewarded to " + ChatColor.YELLOW + playerString);
						event.setCancelled(true);
						return;
					}
				} else if (split[1].equalsIgnoreCase("list")) {
					NavyCraft_FileListener.loadSignData();
					NavyCraft_BlockListener.loadRewards(player.getName());
					player.sendMessage(ChatColor.AQUA + "Your Shipyard Plots:");
					player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "ID" + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TYPE");

					if (NavyCraft.playerSHIP1Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerSHIP1Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " SHIP1");
						}
					}
					if (NavyCraft.playerSHIP2Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerSHIP2Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " SHIP2");
						}
					}
					if (NavyCraft.playerSHIP3Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerSHIP3Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " SHIP3");
						}
					}
					if (NavyCraft.playerSHIP4Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerSHIP4Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " SHIP4");
						}
					}
					if (NavyCraft.playerSHIP5Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerSHIP5Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " SHIP5");
						}
					}
					if (NavyCraft.playerHANGAR1Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerHANGAR1Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " HANGAR1");
						}
					}
					if (NavyCraft.playerHANGAR2Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerHANGAR2Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " HANGAR2");
						}
					}
					if (NavyCraft.playerTANK1Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerTANK1Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TANK1");
						}
					}
					if (NavyCraft.playerTANK2Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerTANK2Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TANK2");
						}
					}
					if (NavyCraft.playerMAP1Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerMAP1Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " MAP1");
						}
					}
					if (NavyCraft.playerMAP2Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerMAP2Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " MAP2");
						}
					}
					if (NavyCraft.playerMAP3Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerMAP3Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " MAP3");
						}
					}
					if (NavyCraft.playerMAP4Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerMAP4Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " MAP4");
						}
					}
					if (NavyCraft.playerMAP5Signs.containsKey(player.getName())) {
						for (Sign s : NavyCraft.playerMAP5Signs.get(player.getName())) {
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " MAP5");
						}
					}

				} else if (split[1].equalsIgnoreCase("tp")) {
					if (split.length == 3) {
						int tpId = -1;
						try {
							tpId = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
							event.setCancelled(true);
							return;
						}

						if (tpId > -1) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());

							Sign foundSign = null;
							foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

							if (foundSign != null) {
								player.teleport(foundSign.getLocation().add(0.5, 0.5, 0.5));
							} else {
								player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
							}

						} else {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard tp <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "teleport to a plot id");
					}
				} else if (split[1].equalsIgnoreCase("help")) {
					player.sendMessage(ChatColor.GOLD + "Shipyard v" + ChatColor.GREEN + NavyCraft.version + ChatColor.GOLD + " commands :");
					player.sendMessage(ChatColor.AQUA + "/shipyard - Status message");
					player.sendMessage(ChatColor.AQUA + "/shipyard list - List your current plots");
					player.sendMessage(ChatColor.AQUA + "/shipyard info <id> - Information about plot");
					player.sendMessage(ChatColor.AQUA + "/shipyard open <plot type> - Teleport to an unclaimed plot");
					player.sendMessage(ChatColor.AQUA + "/shipyard unclaim <id> - Unclaimes a plot");
					player.sendMessage(ChatColor.AQUA + "/shipyard tp <id> - Teleport to the plot id number");
					player.sendMessage(ChatColor.AQUA + "/shipyard addmember <id> <player> - Gives player permission to that plot");
					player.sendMessage(ChatColor.AQUA + "/shipyard remmember <id> <player> - Removes player permission to that plot");
					player.sendMessage(ChatColor.AQUA + "/shipyard clear <id> - Destroys all blocks within the plot");
					player.sendMessage(ChatColor.AQUA + "/shipyard rename <id> <custom name> - Renames the plot");
					player.sendMessage(ChatColor.AQUA + "/shipyard public <id> - Allows any player to select your vehicle");
					player.sendMessage(ChatColor.AQUA + "/shipyard private <id> - Allows only you and your members to select your vehicle");
					player.sendMessage(ChatColor.AQUA + "/shipyard plist <player> - List the given player's plots");
					player.sendMessage(ChatColor.AQUA + "/shipyard ptp <player> <id> - Teleport to the player's plot id");
					if (PermissionInterface.CheckQuietPerm(player, "navycraft.admin") || player.isOp()) {
						player.sendMessage(ChatColor.RED + "Shipyard Admin v" + ChatColor.GREEN + NavyCraft.version + ChatColor.RED + " commands :");
						player.sendMessage(ChatColor.BLUE + "/shipyard player <player> - View a players plot status");
						player.sendMessage(ChatColor.BLUE + "/shipyard reward <player> <type> <reason> - Rewards the specified plot type to the player");
					}
				} else if (split[1].equalsIgnoreCase("open")) {
					if (split.length == 3) {
						String typeString = split[2];

						Block tpBlock = null;
						if (typeString.equalsIgnoreCase("SHIP1")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("SHIP1");
						} else if (typeString.equalsIgnoreCase("SHIP2")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("SHIP2");
						} else if (typeString.equalsIgnoreCase("SHIP3")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("SHIP3");
						} else if (typeString.equalsIgnoreCase("SHIP4")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("SHIP4");
						} else if (typeString.equalsIgnoreCase("SHIP5")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("SHIP5");
						} else if (typeString.equalsIgnoreCase("HANGAR1")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("HANGAR1");
						} else if (typeString.equalsIgnoreCase("HANGAR2")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("HANGAR2");
						} else if (typeString.equalsIgnoreCase("TANK1")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("TANK1");
						} else if (typeString.equalsIgnoreCase("TANK2")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("TANK2");
						} else if (typeString.equalsIgnoreCase("MAP1")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("MAP1");
						} else if (typeString.equalsIgnoreCase("MAP2")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("MAP2");
						} else if (typeString.equalsIgnoreCase("MAP3")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("MAP3");
						} else if (typeString.equalsIgnoreCase("MAP4")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("MAP4");
						} else if (typeString.equalsIgnoreCase("MAP5")) {
							tpBlock = NavyCraft_FileListener.findSignOpen("MAP5");
						} else {
							player.sendMessage(ChatColor.RED + "Unknown lot type");
							event.setCancelled(true);
							return;
						}

						if (tpBlock != null) {
							player.teleport(tpBlock.getLocation().add(0.5, 0.5, 0.5));
						} else {
							player.sendMessage(ChatColor.RED + "No open plots found!");
						}

					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard open <plot type>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "teleport to an unclaimed plot");
					}
				} else if (split[1].equalsIgnoreCase("info")) {
					if (split.length == 3) {
						int tpId = -1;
						try {
							tpId = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
							event.setCancelled(true);
							return;
						}

						if (tpId > -1) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());

							Sign foundSign = null;
							foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

							if (foundSign != null) {
								wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
								if (wgp != null) {
									RegionManager regionManager = wgp.getRegionManager(plugin.getServer().getWorld("shipyard"));
									String regionName = "--" + player.getName() + "-" + tpId;

									DefaultDomain dd = regionManager.getRegion(regionName).getMembers();

									player.sendMessage(ChatColor.GOLD + "Info: " + ChatColor.DARK_GRAY + "[" + player.getName() + " - " + ChatColor.GREEN + tpId + ChatColor.DARK_GRAY + "]");
									String members = ChatColor.GOLD + "Plot Members: " + ChatColor.RESET;
									for (String s : dd.getPlayers()) {
										members += s + ", ";
									}
									player.sendMessage(members);
								}
							} else {
								player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
							}

						} else {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard addmember <id> <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "gives player permission to that plot");
					}
				} else if (split[1].equalsIgnoreCase("addmember")) {
					if (split.length == 4) {
						int tpId = -1;
						try {
							tpId = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
							event.setCancelled(true);
							return;
						}

						if (tpId > -1) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());

							Sign foundSign = null;
							foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

							if (foundSign != null) {
								wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
								if (wgp != null) {
									RegionManager regionManager = wgp.getRegionManager(plugin.getServer().getWorld("shipyard"));
									String regionName = "--" + player.getName() + "-" + tpId;

									String playerInName = split[3];
									Player p = plugin.getServer().getPlayer(playerInName);
									if (p == null) {
										player.sendMessage(ChatColor.RED + "Player not found");
										event.setCancelled(true);
										return;
									}
									com.sk89q.worldguard.LocalPlayer lp = wgp.wrapPlayer(p);

									regionManager.getRegion(regionName).getMembers().addPlayer(lp);

									try {
										regionManager.save();
									} catch (StorageException e) {
										e.printStackTrace();
									}

									player.sendMessage(ChatColor.GREEN + "Player Added.");
								}
							} else {
								player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
							}

						} else {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard addmember <id> <player>");
					}
				} else if (split[1].equalsIgnoreCase("remmember")) {
					if (split.length == 4) {
						int tpId = -1;
						try {
							tpId = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
							event.setCancelled(true);
							return;
						}

						if (tpId > -1) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());

							Sign foundSign = null;
							foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

							if (foundSign != null) {
								wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
								if (wgp != null) {
									RegionManager regionManager = wgp.getRegionManager(plugin.getServer().getWorld("shipyard"));
									String regionName = "--" + player.getName() + "-" + tpId;

									String playerInName = split[3];

									if (!regionManager.getRegion(regionName).getMembers().contains(playerInName)) {
										player.sendMessage(ChatColor.RED + "Member not found.");
										event.setCancelled(true);
										return;
									}

									regionManager.getRegion(regionName).getMembers().removePlayer(playerInName);

									try {
										regionManager.save();
									} catch (StorageException e) {
										e.printStackTrace();
									}

									player.sendMessage(ChatColor.GREEN + "Player removed.");
								}
							} else {
								player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
							}

						} else {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard remmember <id> <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "removes player permission to that plot");
					}
				} else if (split[1].equalsIgnoreCase("clear")) {
					if (split.length == 3) {
						int tpId = -1;
						try {
							tpId = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
							event.setCancelled(true);
							return;
						}

						if (tpId > -1) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());

							Sign foundSign = null;
							foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

							if (foundSign != null) {
								wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
								if (wgp != null) {
									RegionManager regionManager = wgp.getRegionManager(plugin.getServer().getWorld("shipyard"));
									String regionName = "--" + player.getName() + "-" + tpId;

									int startX = regionManager.getRegion(regionName).getMinimumPoint().getBlockX();
									int endX = regionManager.getRegion(regionName).getMaximumPoint().getBlockX();
									int startZ = regionManager.getRegion(regionName).getMinimumPoint().getBlockZ();
									int endZ = regionManager.getRegion(regionName).getMaximumPoint().getBlockZ();
									int startY = regionManager.getRegion(regionName).getMinimumPoint().getBlockY();
									int endY = regionManager.getRegion(regionName).getMaximumPoint().getBlockY();

									for (int x = startX; x <= endX; x++) {
										for (int z = startZ; z <= endZ; z++) {
											for (int y = startY; y <= 62; y++) {
												plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z).setType(Material.AIR);

											}
											int startYy;
											if (startY > 63) {
												startYy = startY;
											} else {
												startYy = 63;
											}
											for (int y = startYy; y <= endY; y++) {
												plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z).setType(Material.AIR);
											}
										}
									}

									player.sendMessage(ChatColor.GREEN + "Plot Cleared.");
								}
							} else {
								player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
							}

						} else {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard clear <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "destroys all blocks within the plot");
					}

				} else if (split[1].equalsIgnoreCase("unclaim")) {
					if (split.length == 3) {
						int tpId = -1;
						try {
							tpId = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
							event.setCancelled(true);
							return;
						}

						if (tpId > -1) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());

							Sign foundSign = null;
							foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

							if (foundSign != null) {
								Block foundBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(), foundSign.getY() - 1, foundSign.getZ() + 1);
								if (foundBlock2.getTypeId() != 68) {
									foundBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1, foundSign.getY() - 1, foundSign.getZ());
								}
								if (foundBlock2.getTypeId() == 68) {
									Sign foundSign2 = (Sign) foundBlock2.getState();

									wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
									if (wgp != null) {
										RegionManager regionManager = wgp.getRegionManager(plugin.getServer().getWorld("shipyard"));
										String regionName = "--" + player.getName() + "-" + tpId;

										int startX = regionManager.getRegion(regionName).getMinimumPoint().getBlockX();
										int endX = regionManager.getRegion(regionName).getMaximumPoint().getBlockX();
										int startZ = regionManager.getRegion(regionName).getMinimumPoint().getBlockZ();
										int endZ = regionManager.getRegion(regionName).getMaximumPoint().getBlockZ();
										int startY = regionManager.getRegion(regionName).getMinimumPoint().getBlockY();
										int endY = regionManager.getRegion(regionName).getMaximumPoint().getBlockY();

										for (int x = startX; x <= endX; x++) {
											for (int z = startZ; z <= endZ; z++) {
												for (int y = startY; y <= 62; y++) {
													plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z).setType(Material.AIR);

												}
												int startYy;
												if (startY > 63) {
													startYy = startY;
												} else {
													startYy = 63;
												}
												for (int y = startYy; y <= endY; y++) {
													plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z).setType(Material.AIR);
												}
											}
										}
										regionManager.removeRegion(regionName);
										NavyCraft_FileListener.saveUnclaimedSign(foundSign2.getLine(3), foundSign.getWorld().getName(), foundSign.getX(), foundSign.getY(), foundSign.getZ());
										foundSign.setLine(0, "*Claim*");
										foundSign.setLine(1, "");
										foundSign.setLine(2, "");
										foundSign.setLine(3, "");
										foundSign.update();
										NavyCraft_FileListener.loadSignData();
										NavyCraft_BlockListener.loadRewards(player.getName());
										try {
											regionManager.save();
										} catch (StorageException e) {
											e.printStackTrace();
										}
										player.sendMessage(ChatColor.GREEN + "Plot Unclaimed.");
									}
								} else {
									player.sendMessage(ChatColor.RED + "Error: There may be a problem with your plot signs.");
								}
							} else {
								player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
							}

						} else {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard unclaim <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "destroys all blocks within the plot");
					}

				} else if (split[1].equalsIgnoreCase("aunclaim")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin")) {
						event.setCancelled(true);
						return;
					}
					if (split.length == 4) {
						int tpId = -1;
						String p = split[2];
						try {
							tpId = Integer.parseInt(split[3]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
							event.setCancelled(true);
							return;
						}
						if (tpId > -1) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(p);

							Sign foundSign = null;
							foundSign = NavyCraft_BlockListener.findSign(p, tpId);

							if (foundSign != null) {
								Block foundBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(), foundSign.getY() - 1, foundSign.getZ() + 1);
								if (foundBlock2.getTypeId() != 68) {
									foundBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1, foundSign.getY() - 1, foundSign.getZ());
								}
								if (foundBlock2.getTypeId() == 68) {
									Sign foundSign2 = (Sign) foundBlock2.getState();

									wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
									if (wgp != null) {
										RegionManager regionManager = wgp.getRegionManager(plugin.getServer().getWorld("shipyard"));
										String regionName = "--" + p + "-" + tpId;

										int startX = regionManager.getRegion(regionName).getMinimumPoint().getBlockX();
										int endX = regionManager.getRegion(regionName).getMaximumPoint().getBlockX();
										int startZ = regionManager.getRegion(regionName).getMinimumPoint().getBlockZ();
										int endZ = regionManager.getRegion(regionName).getMaximumPoint().getBlockZ();
										int startY = regionManager.getRegion(regionName).getMinimumPoint().getBlockY();
										int endY = regionManager.getRegion(regionName).getMaximumPoint().getBlockY();

										for (int x = startX; x <= endX; x++) {
											for (int z = startZ; z <= endZ; z++) {
												for (int y = startY; y <= 62; y++) {
													plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z).setType(Material.AIR);

												}
												int startYy;
												if (startY > 63) {
													startYy = startY;
												} else {
													startYy = 63;
												}
												for (int y = startYy; y <= endY; y++) {
													plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z).setType(Material.AIR);
												}
											}
										}
										regionManager.removeRegion(regionName);
										NavyCraft_FileListener.saveUnclaimedSign(foundSign2.getLine(3), foundSign.getWorld().getName(), foundSign.getX(), foundSign.getY(), foundSign.getZ());
										foundSign.setLine(0, "*Claim*");
										foundSign.setLine(1, "");
										foundSign.setLine(2, "");
										foundSign.setLine(3, "");
										foundSign.update();
										NavyCraft_FileListener.loadSignData();
										NavyCraft_BlockListener.loadRewards(player.getName());
										try {
											regionManager.save();
										} catch (StorageException e) {
											e.printStackTrace();
										}
										player.sendMessage(ChatColor.GREEN + "Plot Unclaimed.");
									}
								} else {
									player.sendMessage(ChatColor.RED + "Error: There may be a problem with your plot signs.");
								}
							} else {
								player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
							}
						} else {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard unclaim <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "destroys all blocks within the plot");
					}

				} else if (split[1].equalsIgnoreCase("rename")) {
					if (split.length > 3) {
						int tpId = -1;
						try {
							tpId = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
							event.setCancelled(true);
							return;
						}

						String nameString;
						nameString = "";
						for (int i = 3; i < split.length; i++) {
							nameString += split[i] + " ";
						}

						if (tpId > -1) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());

							Sign foundSign = null;
							foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

							if (foundSign != null) {
								foundSign.setLine(3, nameString);
								foundSign.update();
								player.sendMessage(ChatColor.GREEN + "Plot renamed.");
							} else {
								player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
							}

						} else {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard rename <id> <custom name>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "renames the plot");
					}
				} else if (split[1].equalsIgnoreCase("public")) {
					if (split.length == 3) {
						int tpId = -1;
						try {
							tpId = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
							event.setCancelled(true);
							return;
						}

						if (tpId > -1) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());

							Sign foundSign = null;
							foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

							if (foundSign != null) {
								Block selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(), foundSign.getY() - 1, foundSign.getZ() + 1);
								if (selectSignBlock2.getTypeId() != 68) {
									selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1, foundSign.getY() - 1, foundSign.getZ());
								}
								if (selectSignBlock2.getTypeId() == 68) {
									Sign selectSign2 = (Sign) selectSignBlock2.getState();
									selectSign2.setLine(0, "Public");
									selectSign2.update();
									player.sendMessage(ChatColor.GREEN + "Plot set to PUBLIC" + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "Any player may select it.");
								} else {
									player.sendMessage(ChatColor.RED + "Error: There may be a problem with your plot signs.");
								}
							} else {
								player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
							}

						} else {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard public <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "allows any player to select your vehicle");
					}
				} else if (split[1].equalsIgnoreCase("private")) {
					if (split.length == 3) {
						int tpId = -1;
						try {
							tpId = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
							event.setCancelled(true);
							return;
						}

						if (tpId > -1) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());

							Sign foundSign = null;
							foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

							if (foundSign != null) {
								Block selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(), foundSign.getY() - 1, foundSign.getZ() + 1);
								if (selectSignBlock2.getTypeId() != 68) {
									selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1, foundSign.getY() - 1, foundSign.getZ());
								}
								if (selectSignBlock2.getTypeId() == 68) {
									Sign selectSign2 = (Sign) selectSignBlock2.getState();
									selectSign2.setLine(0, "Private");
									selectSign2.update();
									player.sendMessage(ChatColor.GREEN + "Plot set to PRIVATE" + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "Only you and your plot members can SELECT it.");
								} else {
									player.sendMessage(ChatColor.DARK_RED + "Error: There may be a problem with your plot signs.");
								}
							} else {
								player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
							}

						} else {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard private <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "allows any player to select your vehicle");
					}
				} else if (split[1].equalsIgnoreCase("player")) {
					if (split.length == 3) {
						String p = split[2];
						NavyCraft_FileListener.loadSignData();
						NavyCraft_BlockListener.loadRewards(p);
						player.sendMessage(ChatColor.AQUA + p + "'s Shipyard Plots:");
						if (NavyCraft.playerSHIP1Signs.containsKey(p)) {
							int numSHIP1s = NavyCraft.playerSHIP1Signs.get(p).size();
							if (NavyCraft.playerSHIP1Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "SHIP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "SHIP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numSHIP1s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerSHIP1Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "SHIP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerSHIP2Signs.containsKey(p)) {
							int numSHIP2s = NavyCraft.playerSHIP2Signs.get(p).size();
							if (NavyCraft.playerSHIP2Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "SHIP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "SHIP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numSHIP2s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerSHIP2Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "SHIP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerSHIP3Signs.containsKey(p)) {
							int numSHIP3s = NavyCraft.playerSHIP3Signs.get(p).size();
							if (NavyCraft.playerSHIP3Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "SHIP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP3s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP3Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "SHIP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP3s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numSHIP3s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerSHIP3Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "SHIP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP3Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerSHIP4Signs.containsKey(p)) {
							int numSHIP4s = NavyCraft.playerSHIP4Signs.get(p).size();
							if (NavyCraft.playerSHIP4Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "SHIP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP4s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP4Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "SHIP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP4s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numSHIP4s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerSHIP4Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "SHIP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP4Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerSHIP5Signs.containsKey(p)) {
							int numSHIP5s = NavyCraft.playerSHIP5Signs.get(p).size();
							if (NavyCraft.playerSHIP5Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "SHIP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP5s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP5Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "SHIP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP5s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numSHIP5s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerSHIP5Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "SHIP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP5Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerHANGAR1Signs.containsKey(p)) {
							int numHANGAR1s = NavyCraft.playerHANGAR1Signs.get(p).size();
							if (NavyCraft.playerHANGAR1Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "HANGAR1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numHANGAR1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerHANGAR1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "HANGAR1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numHANGAR1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numHANGAR1s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerHANGAR1Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "HANGAR1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerHANGAR1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerHANGAR2Signs.containsKey(p)) {
							int numHANGAR2s = NavyCraft.playerHANGAR2Signs.get(p).size();
							if (NavyCraft.playerHANGAR2Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "HANGAR2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numHANGAR2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerHANGAR2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "HANGAR2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numHANGAR2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numHANGAR2s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerHANGAR2Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "HANGAR2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerHANGAR2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerTANK1Signs.containsKey(p)) {
							int numTANK1s = NavyCraft.playerTANK1Signs.get(p).size();
							if (NavyCraft.playerTANK1Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "TANK1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numTANK1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerTANK1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "TANK1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numTANK1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numTANK1s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerTANK1Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "TANK1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerTANK1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerTANK2Signs.containsKey(p)) {
							int numTANK2s = NavyCraft.playerTANK2Signs.get(p).size();
							if (NavyCraft.playerTANK2Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "TANK2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numTANK2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerTANK2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "TANK2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numTANK2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numTANK2s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerTANK2Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "TANK2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerTANK2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerMAP1Signs.containsKey(p)) {
							int numMAP1s = NavyCraft.playerMAP1Signs.get(p).size();
							if (NavyCraft.playerMAP1Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "MAP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "MAP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numMAP1s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerMAP1Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "MAP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerMAP2Signs.containsKey(p)) {
							int numMAP2s = NavyCraft.playerMAP2Signs.get(p).size();
							if (NavyCraft.playerMAP2Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "MAP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "MAP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numMAP2s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerMAP2Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "MAP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerMAP3Signs.containsKey(p)) {
							int numMAP3s = NavyCraft.playerMAP3Signs.get(p).size();
							if (NavyCraft.playerMAP3Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "MAP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP3s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP3Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "MAP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP3s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numMAP3s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerMAP3Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "MAP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP3Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerMAP4Signs.containsKey(p)) {
							int numMAP4s = NavyCraft.playerMAP4Signs.get(p).size();
							if (NavyCraft.playerMAP4Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "MAP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP4s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP4Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "MAP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP4s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numMAP4s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerMAP4Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "MAP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP4Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
						if (NavyCraft.playerMAP5Signs.containsKey(p)) {
							int numMAP5s = NavyCraft.playerMAP5Signs.get(p).size();
							if (NavyCraft.playerMAP5Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "MAP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP5s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP5Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							} else {
								player.sendMessage(ChatColor.GOLD + "MAP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP5s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numMAP5s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						} else {
							if (NavyCraft.playerMAP5Rewards.containsKey(p)) {
								player.sendMessage(ChatColor.GOLD + "MAP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP5Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard player <playerName>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "View a players shipyard status");
					}
				} else if (split[1].equalsIgnoreCase("plist")) {
					if (split.length == 3) {
						String p = split[2];
						NavyCraft_FileListener.loadSignData();
						NavyCraft_BlockListener.loadRewards(p);
						player.sendMessage(ChatColor.AQUA + p + "'s" + " Shipyard Plots:");
						player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "ID" + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TYPE");

						if (NavyCraft.playerSHIP1Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerSHIP1Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " SHIP1");
							}
						}
						if (NavyCraft.playerSHIP2Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerSHIP2Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " SHIP2");
							}
						}
						if (NavyCraft.playerSHIP3Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerSHIP3Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " SHIP3");
							}
						}
						if (NavyCraft.playerSHIP4Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerSHIP4Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " SHIP4");
							}
						}
						if (NavyCraft.playerSHIP5Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerSHIP5Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " SHIP5");
							}
						}
						if (NavyCraft.playerHANGAR1Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerHANGAR1Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " HANGAR1");
							}
						}
						if (NavyCraft.playerHANGAR2Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerHANGAR2Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " HANGAR2");
							}
						}
						if (NavyCraft.playerTANK1Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerTANK1Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TANK1");
							}
						}
						if (NavyCraft.playerTANK2Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerTANK2Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TANK2");
							}
						}
						if (NavyCraft.playerMAP1Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerMAP1Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " MAP1");
							}
						}
						if (NavyCraft.playerMAP2Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerMAP2Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " MAP2");
							}
						}
						if (NavyCraft.playerMAP3Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerMAP3Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " MAP3");
							}
						}
						if (NavyCraft.playerMAP4Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerMAP4Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " MAP4");
							}
						}
						if (NavyCraft.playerMAP5Signs.containsKey(p)) {
							for (Sign s : NavyCraft.playerMAP5Signs.get(p)) {
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(s) + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " MAP5");
							}
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard plist <playerName>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "List the given player's plots");
					}
				} else if (split[1].equalsIgnoreCase("ptp")) {
					if (split.length == 4) {
						String p = split[2];
						int tpId = -1;
						try {
							tpId = Integer.parseInt(split[3]);
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
							event.setCancelled(true);
							return;
						}
						if (tpId > -1) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(p);

							Sign foundSign = null;
							foundSign = NavyCraft_BlockListener.findSign(p, tpId);

							if (foundSign != null) {
								player.teleport(foundSign.getLocation().add(0.5, 0.5, 0.5));
							} else {
								player.sendMessage(ChatColor.RED + "ID not found, Use:" + ChatColor.YELLOW + "/shipyard plist" + ChatColor.RED + "to see IDs");
							}

						} else {
							player.sendMessage(ChatColor.RED + "Invalid Plot ID");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/shipyard ptp <playerName> <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "teleport to a player's plot id");
					}
				} else {
					player.sendMessage("Unknown command. Type \"/shipyard help\" for help.");
					event.setCancelled(true);
					return;
				}
			} else {
				NavyCraft_FileListener.loadSignData();
				NavyCraft_BlockListener.loadRewards(player.getName());
				String p = player.getName();
				player.sendMessage(ChatColor.AQUA + "Your Shipyard Plots:");
				if (NavyCraft.playerSHIP1Signs.containsKey(p)) {
					int numSHIP1s = NavyCraft.playerSHIP1Signs.get(p).size();
					if (NavyCraft.playerSHIP1Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "SHIP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "SHIP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numSHIP1s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerSHIP1Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "SHIP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerSHIP2Signs.containsKey(p)) {
					int numSHIP2s = NavyCraft.playerSHIP2Signs.get(p).size();
					if (NavyCraft.playerSHIP2Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "SHIP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "SHIP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numSHIP2s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerSHIP2Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "SHIP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerSHIP3Signs.containsKey(p)) {
					int numSHIP3s = NavyCraft.playerSHIP3Signs.get(p).size();
					if (NavyCraft.playerSHIP3Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "SHIP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP3s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP3Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "SHIP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP3s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numSHIP3s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerSHIP3Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "SHIP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP3Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerSHIP4Signs.containsKey(p)) {
					int numSHIP4s = NavyCraft.playerSHIP4Signs.get(p).size();
					if (NavyCraft.playerSHIP4Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "SHIP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP4s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP4Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "SHIP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP4s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numSHIP4s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerSHIP4Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "SHIP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP4Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerSHIP5Signs.containsKey(p)) {
					int numSHIP5s = NavyCraft.playerSHIP5Signs.get(p).size();
					if (NavyCraft.playerSHIP5Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "SHIP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP5s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP5Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "SHIP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numSHIP5s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numSHIP5s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerSHIP5Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "SHIP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerSHIP5Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerHANGAR1Signs.containsKey(p)) {
					int numHANGAR1s = NavyCraft.playerHANGAR1Signs.get(p).size();
					if (NavyCraft.playerHANGAR1Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "HANGAR1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numHANGAR1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerHANGAR1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "HANGAR1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numHANGAR1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerHANGAR1Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "HANGAR1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerHANGAR1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerHANGAR2Signs.containsKey(p)) {
					int numHANGAR2s = NavyCraft.playerHANGAR2Signs.get(p).size();
					if (NavyCraft.playerHANGAR2Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "HANGAR2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numHANGAR2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerHANGAR2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "HANGAR2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numHANGAR2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerHANGAR2Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "HANGAR2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerHANGAR2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerTANK1Signs.containsKey(p)) {
					int numTANK1s = NavyCraft.playerTANK1Signs.get(p).size();
					if (NavyCraft.playerTANK1Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "TANK1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numTANK1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerTANK1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "TANK1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numTANK1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerTANK1Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "TANK1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerTANK1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerTANK2Signs.containsKey(p)) {
					int numTANK2s = NavyCraft.playerTANK2Signs.get(p).size();
					if (NavyCraft.playerTANK2Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "TANK2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numTANK2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerTANK2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "TANK2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numTANK2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerTANK2Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "TANK2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerTANK2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerMAP1Signs.containsKey(p)) {
					int numMAP1s = NavyCraft.playerMAP1Signs.get(p).size();
					if (NavyCraft.playerMAP1Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "MAP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "MAP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP1s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numMAP1s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerMAP1Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "MAP1 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP1Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerMAP2Signs.containsKey(p)) {
					int numMAP2s = NavyCraft.playerMAP2Signs.get(p).size();
					if (NavyCraft.playerMAP2Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "MAP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "MAP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP2s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numMAP2s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerMAP2Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "MAP2 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP2Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerMAP3Signs.containsKey(p)) {
					int numMAP3s = NavyCraft.playerMAP3Signs.get(p).size();
					if (NavyCraft.playerMAP3Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "MAP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP3s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP3Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "MAP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP3s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numMAP3s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerMAP3Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "MAP3 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP3Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerMAP4Signs.containsKey(p)) {
					int numMAP4s = NavyCraft.playerMAP4Signs.get(p).size();
					if (NavyCraft.playerMAP4Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "MAP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP4s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP4Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "MAP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP4s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numMAP4s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerMAP4Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "MAP4 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP4Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
				if (NavyCraft.playerMAP5Signs.containsKey(p)) {
					int numMAP5s = NavyCraft.playerMAP5Signs.get(p).size();
					if (NavyCraft.playerMAP5Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "MAP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP5s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP5Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					} else {
						player.sendMessage(ChatColor.GOLD + "MAP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + numMAP5s + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + numMAP5s + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				} else {
					if (NavyCraft.playerMAP4Rewards.containsKey(p)) {
						player.sendMessage(ChatColor.GOLD + "MAP5 " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "0" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + "[" + ChatColor.RED + NavyCraft.playerMAP5Rewards.get(p) + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
					}
				}
			}
			event.setCancelled(true);
			return;

		} else if (craftName.equalsIgnoreCase("sign")) {
			if (split.length == 2) {
				if (split[1].equalsIgnoreCase("undo")) {
					if (NavyCraft.playerLastBoughtSign.containsKey(player)) {
						if ((NavyCraft.playerLastBoughtSign.get(player).getTypeId() == 68) || (NavyCraft.playerLastBoughtSign.get(player).getTypeId() == 63)) {
							Sign sign = (Sign) NavyCraft.playerLastBoughtSign.get(player).getState();
							String signString0 = sign.getLine(0).trim().toLowerCase();
							signString0 = signString0.replaceAll(ChatColor.BLUE.toString(), "");
							String signString1 = sign.getLine(1).trim().toLowerCase();
							signString1 = signString1.replaceAll(ChatColor.BLUE.toString(), "");
							String signString2 = sign.getLine(2).trim().toLowerCase();
							signString2 = signString2.replaceAll(ChatColor.BLUE.toString(), "");
							if (signString0.equalsIgnoreCase(NavyCraft.playerLastBoughtSignString0.get(player)) && signString1.equalsIgnoreCase(NavyCraft.playerLastBoughtSignString1.get(player)) && signString2.equalsIgnoreCase(NavyCraft.playerLastBoughtSignString2.get(player))) {
								NavyCraft.playerLastBoughtSign.get(player).setTypeId(0);
								player.sendMessage(ChatColor.RED + "Undoing sign and refunding player.");
								NavyCraft.econ.depositPlayer(player, NavyCraft.playerLastBoughtCost.get(player));
								// ess.getUser(player).giveMoney(new
								// BigDecimal(NavyCraft.playerLastBoughtCost.get(player)));
								NavyCraft.playerLastBoughtSign.remove(player);
								NavyCraft.playerLastBoughtCost.remove(player);
								NavyCraft.playerLastBoughtSignString0.remove(player);
								NavyCraft.playerLastBoughtSignString1.remove(player);
								NavyCraft.playerLastBoughtSignString2.remove(player);
							} else {
								player.sendMessage(ChatColor.RED + "Incorrect sign detected.");
							}

						} else {
							player.sendMessage(ChatColor.RED + "No sign detected to undo.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "Nothing to undo.");
					}
					event.setCancelled(true);
					return;
				}
			}

		} else if (craftName.equalsIgnoreCase("team")) {
			if (!NavyCraft.redPlayers.contains(player.getName()) && !NavyCraft.bluePlayers.contains(player.getName()) && !NavyCraft.anyPlayers.contains(player.getName()) && !PermissionInterface.CheckQuietPerm(player, "navycraft.battle") && !player.isOp()) {
				player.sendMessage(ChatColor.RED + "You are not on a team!");
				event.setCancelled(true);
				return;
			}

			if (split.length == 1) {
				if (NavyCraft.redPlayers.contains(player.getName())) {
					player.sendMessage(ChatColor.RED + "You are on Team Red.");
				} else if (NavyCraft.bluePlayers.contains(player.getName())) {
					player.sendMessage(ChatColor.BLUE + "You are on Team Blue.");
				} else if (NavyCraft.bluePlayers.contains(player.getName())) {
					player.sendMessage(ChatColor.YELLOW + "You are queued for Any Team.");
				}
				String teamString = ChatColor.RED + "Team Red Players: ";
				for (String s : NavyCraft.redPlayers) {
					teamString = teamString + s + ", ";
				}
				player.sendMessage(teamString);
				teamString = ChatColor.BLUE + "Team Blue Players: ";
				for (String s : NavyCraft.bluePlayers) {
					teamString = teamString + s + ", ";
				}
				player.sendMessage(teamString);

				if (!NavyCraft.anyPlayers.isEmpty()) {
					teamString = ChatColor.YELLOW + "Any Team Players: ";
					for (String s : NavyCraft.anyPlayers) {
						teamString = teamString + s + ", ";
					}
					player.sendMessage(teamString);
				}

			} else {
				if (!NavyCraft.redPlayers.contains(player.getName()) && !NavyCraft.bluePlayers.contains(player.getName())) {
					player.sendMessage(ChatColor.RED + "You are not on a team!");
					event.setCancelled(true);
					return;
				}

				String msgString;
				msgString = "";
				for (int i = 1; i < split.length; i++) {
					msgString += split[i] + " ";
				}

				if (NavyCraft.redPlayers.contains(player.getName())) {
					for (String s : NavyCraft.redPlayers) {
						if ((plugin.getServer().getPlayer(s) != null) && plugin.getServer().getPlayer(s).isOnline()) {
							plugin.getServer().getPlayer(s).sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "Team Red" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + player.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + msgString);
						}
					}
					System.out.println("[Team Red] <" + player.getName() + "> " + msgString);
				} else {
					for (String s : NavyCraft.bluePlayers) {
						if ((plugin.getServer().getPlayer(s) != null) && plugin.getServer().getPlayer(s).isOnline()) {
							plugin.getServer().getPlayer(s).sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_BLUE + "Team Blue" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getName() + ChatColor.GRAY + ": " + ChatColor.RESET + msgString);
						}
					}
					System.out.println("[Team Blue] <" + player.getName() + "> " + msgString);
				}
			}
			event.setCancelled(true);
			return;
		} else if (craftName.equalsIgnoreCase("soldier")) {
			if (player.getWorld().getName().equalsIgnoreCase("BattleWorld")) {
				if (player.getWorld().getName().equalsIgnoreCase("BattleWorld") && (NavyCraft.battleMode > 0)) {
					if (!NavyCraft.playerKits.contains(player.getName())) {
						player.sendMessage(ChatColor.GREEN + "Soldier Kit Given");
						player.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
						player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE, 1));
						player.getInventory().addItem(new ItemStack(Material.IRON_AXE, 1));
						player.getInventory().addItem(new ItemStack(Material.BOW, 1));
						player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
						player.getInventory().addItem(new ItemStack(Material.LADDER, 10));
						player.getInventory().addItem(new ItemStack(Material.COOKED_FISH, 64));
						player.getInventory().addItem(new ItemStack(Material.WOOD, 20));
						player.getInventory().addItem(new ItemStack(Material.SMOOTH_BRICK, 20));
						player.getInventory().addItem(new ItemStack(Material.GLASS, 10));
						player.getInventory().addItem(new ItemStack(Material.STONE_BUTTON, 5));
						player.getInventory().addItem(new ItemStack(Material.LEVER, 5));
						player.getInventory().addItem(new ItemStack(Material.BOAT, 1));
						player.getInventory().addItem(new ItemStack(Material.TNT, 1));
						player.getInventory().addItem(new ItemStack(Material.REDSTONE_TORCH_ON, 1));
						NavyCraft.playerKits.add(player.getName());
					} else {
						player.sendMessage(ChatColor.RED + "You only get one Soldier kit per life!");
					}
				} else if (player.getWorld().getName().equalsIgnoreCase("shipyard")) {
					player.sendMessage(ChatColor.RED + "You can only get this kit in the overworld.");
				} else {
					player.sendMessage(ChatColor.RED + "You can't use that kit right now");
				}
			} else {
				// use essentials instead to make a kit.
				player.sendMessage(ChatColor.RED + "You can only get this kit during an official battle.");
			}
		} else if (craftName.equalsIgnoreCase("battle")) {
			if (split.length == 1) {
				if (NavyCraft.battleMode == -1) {
					player.sendMessage(ChatColor.RED + "No active battle.");
					player.sendMessage(ChatColor.YELLOW + "Use /battle help for more help");
					if (PermissionInterface.CheckQuietPerm(player, "navycraft.battle") || player.isOp()) {
						player.sendMessage(ChatColor.YELLOW + "Use /battle new to start new battle");
					}
				} else if (NavyCraft.battleMode == 0) {
					String battleTypeStr = "";
					switch (NavyCraft.battleType) {
					case 1:
						battleTypeStr = ChatColor.YELLOW + "Tunisia (desert-tanks and airplanes)";
						break;
					case 2:
						battleTypeStr = ChatColor.BLUE + "Tarawa (island-ships and airplanes)";
						break;
					case 3:
						battleTypeStr = ChatColor.DARK_AQUA + "North Sea (open ocean-ships)";
						break;
					}
					player.sendMessage("New Battle Queuing for " + battleTypeStr + "!");
					if (NavyCraft.bluePlayers.contains(player.getName())) {
						player.sendMessage(ChatColor.BLUE + "You are queued for team blue!");
					} else if (NavyCraft.redPlayers.contains(player.getName())) {
						player.sendMessage(ChatColor.RED + "You are queued for team red!");
					} else if (NavyCraft.anyPlayers.contains(player.getName())) {
						player.sendMessage(ChatColor.YELLOW + "You are queued for any team!");
					}

					player.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.YELLOW + "/battle " + ChatColor.RED + "red" + ChatColor.DARK_GRAY + "|" + ChatColor.BLUE + "blue" + ChatColor.DARK_GRAY + "|" + ChatColor.YELLOW + "any" + ChatColor.YELLOW + "to queue!");
					player.sendMessage("There are " + ChatColor.RED + NavyCraft.redPlayers.size() + " red " + ChatColor.WHITE + "and " + ChatColor.BLUE + NavyCraft.bluePlayers.size() + " blue " + ChatColor.WHITE + " and " + NavyCraft.anyPlayers.size() + " unassigned.");
					if (PermissionInterface.CheckQuietPerm(player, "navycraft.battle") || player.isOp()) {
						player.sendMessage(ChatColor.GREEN + "Use /battle start to start battle");
					}
				} else /// active battle
				{
					String battleTypeStr = "";
					String scoreUpdateStr = "";
					switch (NavyCraft.battleType) {
					case 1:
						battleTypeStr = ChatColor.YELLOW + "Tunisia (desert-tanks and airplanes)";
						scoreUpdateStr = "Time Left: " + (int) ((NavyCraft.battleLength - (System.currentTimeMillis() - NavyCraft.battleStartTime)) / 60000.0f) + "min  Score: " + ChatColor.RED + NavyCraft.redPoints + " Team Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Team Blue";
						break;
					case 2:
						battleTypeStr = ChatColor.BLUE + "Tarawa (island-ships and airplanes)";
						scoreTarawa();
						scoreUpdateStr = "Time Left: " + (int) ((NavyCraft.battleLength - (System.currentTimeMillis() - NavyCraft.battleStartTime)) / 60000.0f) + "min  Score: " + ChatColor.RED + NavyCraft.redPoints + " Team Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Team Blue";
						break;
					case 3:
						battleTypeStr = ChatColor.DARK_AQUA + "North Sea (open ocean-ships)";
						scoreUpdateStr = "Time Left: " + (int) ((NavyCraft.battleLength - (System.currentTimeMillis() - NavyCraft.battleStartTime)) / 60000.0f) + "min  Score: " + ChatColor.RED + NavyCraft.redPoints + " Team Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Team Blue";
						break;
					}
					player.sendMessage(ChatColor.RED + "Battle in progress!" + battleTypeStr);
					if (!scoreUpdateStr.equalsIgnoreCase("")) {
						player.sendMessage(scoreUpdateStr);
					}

					if (NavyCraft.bluePlayers.contains(player.getName())) {
						player.sendMessage(ChatColor.BLUE + "You are on team blue!");
					} else if (NavyCraft.redPlayers.contains(player.getName())) {
						player.sendMessage(ChatColor.RED + "You are on team red!");
					} else if (!NavyCraft.battleLockTeams) {
						player.sendMessage(ChatColor.YELLOW + "You can join this battle by typing" + ChatColor.YELLOW + "/battle " + ChatColor.RED + "red" + ChatColor.DARK_GRAY + "|" + ChatColor.BLUE + "blue" + ChatColor.DARK_GRAY + "|" + ChatColor.YELLOW + "any");
					}
					player.sendMessage("There are " + ChatColor.RED + NavyCraft.redPlayers.size() + " red " + ChatColor.WHITE + "and " + ChatColor.BLUE + NavyCraft.bluePlayers.size() + " blue");
				}
			} else {
				if (split[1].equalsIgnoreCase("red")) {
					if (NavyCraft.battleMode == 0) {
						if (NavyCraft.bluePlayers.contains(player.getName())) {
							NavyCraft.bluePlayers.remove(player.getName());
						}
						if (NavyCraft.anyPlayers.contains(player.getName())) {
							NavyCraft.anyPlayers.remove(player.getName());
						}
						if (!NavyCraft.redPlayers.contains(player.getName())) {
							NavyCraft.redPlayers.add(player.getName());
							plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED + " queues for the Red Team!");
						} else {
							player.sendMessage(ChatColor.RED + "You are already on that team");
						}
					} else if (NavyCraft.battleMode > 0) {
						if (!NavyCraft.battleLockTeams && ((NavyCraft.battleLength - (System.currentTimeMillis() - NavyCraft.battleStartTime)) > 300000)) {
							if (NavyCraft.redPlayers.contains(player.getName()) || NavyCraft.bluePlayers.contains(player.getName())) {
								player.sendMessage(ChatColor.RED + "Already on a team first use " + ChatColor.YELLOW + "/battle exit");
								event.setCancelled(true);
								return;
							}

							if (NavyCraft.redPlayers.size() > NavyCraft.bluePlayers.size()) {
								player.sendMessage(ChatColor.RED + "Teams unbalanced, Must join Blue");
								event.setCancelled(true);
								return;
							}

							if (NavyCraft.bluePlayers.contains(player.getName())) {
								NavyCraft.bluePlayers.remove(player.getName());
							}
							if (!NavyCraft.redPlayers.contains(player.getName())) {
								NavyCraft.redPlayers.add(player.getName());
								plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED + " joins the Red Team!");
								player.teleport(NavyCraft.redSpawn);
							}
						} else {
							player.sendMessage(ChatColor.DARK_RED + "Teams locked");
						}
					}

				} else if (split[1].equalsIgnoreCase("blue")) {
					if (NavyCraft.battleMode == 0) {
						if (NavyCraft.redPlayers.contains(player.getName())) {
							NavyCraft.redPlayers.remove(player.getName());
						}
						if (NavyCraft.anyPlayers.contains(player.getName())) {
							NavyCraft.anyPlayers.remove(player.getName());
						}
						if (!NavyCraft.bluePlayers.contains(player.getName())) {
							NavyCraft.bluePlayers.add(player.getName());
							plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.BLUE + " queues for the Blue Team!");
						} else {
							player.sendMessage("You are already on that team");
						}
					} else if (NavyCraft.battleMode > 0) {
						if (!NavyCraft.battleLockTeams && ((NavyCraft.battleLength - (System.currentTimeMillis() - NavyCraft.battleStartTime)) > 300000)) {
							if (NavyCraft.redPlayers.contains(player.getName()) || NavyCraft.bluePlayers.contains(player.getName())) {
								player.sendMessage(ChatColor.RED + "Already on a team, first use" + ChatColor.YELLOW + "/battle exit");
								event.setCancelled(true);
								return;
							}

							if (NavyCraft.redPlayers.size() < NavyCraft.bluePlayers.size()) {
								player.sendMessage(ChatColor.RED + "Teams unbalanced, Must choose Red");
								event.setCancelled(true);
								return;
							}

							if (NavyCraft.redPlayers.contains(player.getName())) {
								NavyCraft.redPlayers.remove(player.getName());
							}
							if (!NavyCraft.bluePlayers.contains(player.getName())) {
								NavyCraft.bluePlayers.add(player.getName());
								plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.BLUE + " joins the Blue Team!");
								player.teleport(NavyCraft.blueSpawn);
							}
						} else {
							player.sendMessage(ChatColor.DARK_RED + "Teams locked");
						}
					}

				} else if (split[1].equalsIgnoreCase("any")) {
					if (NavyCraft.battleMode == 0) {
						if (NavyCraft.bluePlayers.contains(player.getName())) {
							NavyCraft.bluePlayers.remove(player.getName());
						}
						if (NavyCraft.redPlayers.contains(player.getName())) {
							NavyCraft.redPlayers.remove(player.getName());
						}
						if (!NavyCraft.anyPlayers.contains(player.getName())) {
							NavyCraft.anyPlayers.add(player.getName());
							plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + " queues for either Team!");
						} else {
							player.sendMessage(ChatColor.RED + "You are already on that team");
						}
					} else if (NavyCraft.battleMode > 0) {
						if (!NavyCraft.battleLockTeams && ((NavyCraft.battleLength - (System.currentTimeMillis() - NavyCraft.battleStartTime)) > 300000)) {
							if (NavyCraft.redPlayers.contains(player.getName()) || NavyCraft.bluePlayers.contains(player.getName())) {
								player.sendMessage(ChatColor.RED + "Already on a team, first use " + ChatColor.YELLOW + "/battle exit");
								event.setCancelled(true);
								return;
							}

							if (NavyCraft.redPlayers.size() > NavyCraft.bluePlayers.size()) {
								if (NavyCraft.redPlayers.contains(player.getName())) {
									NavyCraft.redPlayers.remove(player.getName());
								}
								if (!NavyCraft.bluePlayers.contains(player.getName())) {
									NavyCraft.bluePlayers.add(player.getName());
									plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.BLUE + " joins the Blue Team!");
									player.teleport(NavyCraft.blueSpawn);
								}
							} else if (NavyCraft.redPlayers.size() < NavyCraft.bluePlayers.size()) {
								if (NavyCraft.bluePlayers.contains(player.getName())) {
									NavyCraft.bluePlayers.remove(player.getName());
								}
								if (!NavyCraft.redPlayers.contains(player.getName())) {
									NavyCraft.redPlayers.add(player.getName());
									plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED + " joins the Red Team!");
									player.teleport(NavyCraft.redSpawn);
								}
							} else {
								if (Math.random() < .5) {
									if (NavyCraft.bluePlayers.contains(player.getName())) {
										NavyCraft.bluePlayers.remove(player.getName());
									}
									if (!NavyCraft.redPlayers.contains(player.getName())) {
										NavyCraft.redPlayers.add(player.getName());
										plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED + " joins the Red Team!");
										player.teleport(NavyCraft.redSpawn);
									}
								} else {
									if (NavyCraft.redPlayers.contains(player.getName())) {
										NavyCraft.redPlayers.remove(player.getName());
									}
									if (!NavyCraft.bluePlayers.contains(player.getName())) {
										NavyCraft.bluePlayers.add(player.getName());
										plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.BLUE + " joins the Blue Team!");
										player.teleport(NavyCraft.blueSpawn);
									}
								}
							}
						} else {
							player.sendMessage(ChatColor.DARK_RED + "Teams locked");
						}
					}

				} else if (split[1].equalsIgnoreCase("help")) {
					if (PermissionInterface.CheckPerm(player, "navycraft.basic")) {
						player.sendMessage(ChatColor.GOLD + "Battles v" + NavyCraft.version + " commands :");
						player.sendMessage(ChatColor.AQUA + "/battle (red,blue,any) " + " : " + ChatColor.WHITE + "joins the battle as red, blue, or any");
						player.sendMessage(ChatColor.AQUA + "/battle exit " + " : " + ChatColor.WHITE + "exits the battle");
						player.sendMessage(ChatColor.AQUA + "/team " + " : " + ChatColor.WHITE + "displays your team");
						player.sendMessage(ChatColor.AQUA + "/team (text) " + " : " + ChatColor.WHITE + "displays a message to your team");
					}
					if (PermissionInterface.CheckQuietPerm(player, "navycraft.battle")) {
						player.sendMessage(ChatColor.RED + "Battles Admin v" + ChatColor.GREEN + NavyCraft.version + ChatColor.RED + "commands :");
						player.sendMessage(ChatColor.BLUE + "/battle new : " + ChatColor.WHITE + "queues a battle");
						player.sendMessage(ChatColor.BLUE + "/battle start : " + ChatColor.WHITE + "starts a queued battle");
						player.sendMessage(ChatColor.BLUE + "/battle cancel : " + ChatColor.WHITE + "cancels a queued battle");
						player.sendMessage(ChatColor.BLUE + "/battle end : " + ChatColor.WHITE + "ends a battle");
						player.sendMessage(ChatColor.BLUE + "/battle kick (player) : " + ChatColor.WHITE + "kicks a player from the battle");
						player.sendMessage(ChatColor.BLUE + "/battle kickall : " + ChatColor.WHITE + "kicks all players out of Battle World");
						player.sendMessage(ChatColor.BLUE + "/battle lock : " + ChatColor.WHITE + "locks team joining");
						player.sendMessage(ChatColor.BLUE + "/battle list : " + ChatColor.WHITE + "lists battle types");
					}

				} else if (split[1].equalsIgnoreCase("exit")) {
					if (!NavyCraft.redPlayers.contains(player.getName()) && !NavyCraft.bluePlayers.contains(player.getName()) && !NavyCraft.anyPlayers.contains(player.getName())) {
						if (PermissionInterface.CheckEnabledWorld(player.getLocation())) {
							Location spawnLoc = plugin.getServer().getWorlds().get(0).getSpawnLocation();
							player.teleport(spawnLoc);
						}
						player.sendMessage(ChatColor.RED + "You are not on a team.");
						event.setCancelled(true);
						return;
					}

					if (NavyCraft.redPlayers.contains(player.getName())) {
						NavyCraft.redPlayers.remove(player.getName());
					}
					if (NavyCraft.bluePlayers.contains(player.getName())) {
						NavyCraft.bluePlayers.remove(player.getName());
					}
					if (NavyCraft.anyPlayers.contains(player.getName())) {
						NavyCraft.anyPlayers.remove(player.getName());
					}
					plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + " has quit the battle!");

					if (PermissionInterface.CheckEnabledWorld(player.getLocation())) {
						Location spawnLoc = plugin.getServer().getWorlds().get(0).getSpawnLocation();
						player.teleport(spawnLoc);
					}
				} else if (split[1].equalsIgnoreCase("kick")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.battle") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to kick players from the battle");
						event.setCancelled(true);
						return;
					}

					if (split.length != 3) {
						player.sendMessage(ChatColor.RED + "Improper format, use " + ChatColor.YELLOW + "/battle kick (player)");
						event.setCancelled(true);
						return;
					}

					Player testPlayer = plugin.getServer().getPlayer(split[2]);
					if (testPlayer != null) {
						if (NavyCraft.bluePlayers.contains(testPlayer.getName())) {
							NavyCraft.bluePlayers.remove(testPlayer.getName());
						} else if (NavyCraft.redPlayers.contains(testPlayer.getName())) {
							NavyCraft.redPlayers.remove(testPlayer.getName());
						} else if (NavyCraft.anyPlayers.contains(testPlayer.getName())) {
							NavyCraft.anyPlayers.remove(testPlayer.getName());
						} else {
							if (PermissionInterface.CheckEnabledWorld(player.getLocation())) {
								Location spawnLoc = plugin.getServer().getWorlds().get(0).getSpawnLocation();
								testPlayer.teleport(spawnLoc);
							}
							player.sendMessage(ChatColor.YELLOW + testPlayer.getName() + ChatColor.RED + " is not on a team.");
							event.setCancelled(true);
							return;
						}
						plugin.getServer().broadcastMessage(ChatColor.YELLOW + testPlayer.getName() + " was kicked from the battle!");
						if (PermissionInterface.CheckEnabledWorld(player.getLocation())) {
							Location spawnLoc = plugin.getServer().getWorlds().get(0).getSpawnLocation();
							testPlayer.teleport(spawnLoc);
						}

					} else {
						if (NavyCraft.bluePlayers.contains(split[2])) {
							NavyCraft.bluePlayers.remove(split[2]);
							plugin.getServer().broadcastMessage(ChatColor.YELLOW + split[2] + " was kicked from the battle!");
						} else if (NavyCraft.redPlayers.contains(split[2])) {
							NavyCraft.redPlayers.remove(split[2]);
							plugin.getServer().broadcastMessage(ChatColor.YELLOW + split[2] + " was kicked from the battle!");
						} else if (NavyCraft.anyPlayers.contains(split[2])) {
							NavyCraft.anyPlayers.remove(split[2]);
							plugin.getServer().broadcastMessage(ChatColor.YELLOW + split[2] + " was kicked from the battle!");
						} else {
							player.sendMessage(ChatColor.RED + "Player not found.");
						}

						event.setCancelled(true);
						return;
					}

				} else if (split[1].equalsIgnoreCase("kickall")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.battle") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to kick everyone from the battle");
						event.setCancelled(true);
						return;
					}

					List<Player> ww2Players = player.getWorld().getPlayers();
					Location spawnLoc = plugin.getServer().getWorlds().get(0).getSpawnLocation();
					for (Player p : ww2Players) {
						if (p != player) {
							p.teleport(spawnLoc);
						}
					}
					plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + " evicts everyone from the Battle!");

				} else if (split[1].equalsIgnoreCase("cancel")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.battle") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to cancel battles");
						event.setCancelled(true);
						return;
					}

					if (NavyCraft.battleMode < 0) {
						player.sendMessage("No active battle.");
					} else {
						plugin.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "OFFICIAL BATTLE CANCELLED!");
						if ((timerThread != null) && timerThread.isAlive()) {
							timerThread.stop();
						}

						NavyCraft.battleMode = -1;
						NavyCraft.battleType = -1;
						NavyCraft.battleLockTeams = false;
						NavyCraft.redPlayers.clear();
						NavyCraft.bluePlayers.clear();
						NavyCraft.anyPlayers.clear();
						NavyCraft.playerKits.clear();
						NavyCraft.redPoints = 0;
						NavyCraft.bluePoints = 0;
						NavyCraft.redMerchant = false;
						NavyCraft.blueMerchant = false;
					}
				} else if (split[1].equalsIgnoreCase("new")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.battle") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to start battles");
						event.setCancelled(true);
						return;
					}

					if (NavyCraft.battleMode >= 1) {
						player.sendMessage(ChatColor.RED + "Battle already started! Use " + ChatColor.YELLOW + "/battle end" + ChatColor.RED + " first");
					} else if (NavyCraft.battleMode == 0) {
						player.sendMessage(ChatColor.RED + "Battle already created, Do you mean " + ChatColor.YELLOW + "/battle start?");
					} else {
						int battleType = -1;
						if (split.length == 3) {
							if (split[2].equalsIgnoreCase("list")) {
								player.sendMessage("Battle List");
								player.sendMessage("1 - Tunisia (Desert-Tanks and Airplanes)");
								player.sendMessage("2 - Tarawa (Island-Ships and Airplanes)");
								player.sendMessage("3 - North Sea (Open Ocean-Ships)");
								// Anythings possible.
								event.setCancelled(true);
								return;
							}
							try {
								battleType = Integer.parseInt(split[2]);
							} catch (NumberFormatException e) {
								player.sendMessage("Invalid battle type option");
								e.printStackTrace();
							}
						}

						if (battleType == -1) {
							battleType = (int) (Math.random() * 3) + 1;
						}

						String battleTypeStr = "";
						switch (battleType) {
						case 1:
							if (!checkTunisia()) {
								player.sendMessage("Tunisia bases need to be repaired first!");
								event.setCancelled(true);
								return;
							}
							battleTypeStr = ChatColor.YELLOW + "Tunisia (desert-tanks and airplanes)";
							break;
						case 2:
							if (!checkTarawa()) {
								player.sendMessage("Tarawa bases need to be repaired first!");
								event.setCancelled(true);
								return;
							}
							battleTypeStr = ChatColor.BLUE + "Tarawa (island-ships and airplanes)";
							break;
						case 3:
							/*
							 * if( !checkNorthSea() ) { player.
							 * sendMessage("North Sea bases need to be repaired first!" );
							 * event.setCancelled(true); return; }
							 */
							battleTypeStr = ChatColor.DARK_AQUA + "North Sea (open ocean-ships)";
							break;
						case 4:
							battleTypeStr = "Normandy";
							break;
						case 5:
							battleTypeStr = "Wake Island";
							break;
						case 6:
							battleTypeStr = "Omaha";
							break;
						default:
							player.sendMessage(ChatColor.RED + "Invalid battle type option, use " + ChatColor.YELLOW + "/battle new (1,2,3)");
							return;
						}
						plugin.getServer().broadcastMessage(ChatColor.GREEN + "*** OFFICIAL BATTLE QUEUE OPEN!!! ***");
						plugin.getServer().broadcastMessage(ChatColor.AQUA + "*** Battle Map: " + battleTypeStr + ChatColor.AQUA + " ***");
						plugin.getServer().broadcastMessage(ChatColor.AQUA + "***" + ChatColor.YELLOW + " Started by: " + ChatColor.DARK_RED + player.getName() + ChatColor.AQUA + " ***");
						plugin.getServer().broadcastMessage(ChatColor.GREEN + "*** Type " + ChatColor.YELLOW + "/battle " + ChatColor.RED + "red" + ChatColor.DARK_GRAY + "|" + ChatColor.BLUE + "blue" + ChatColor.DARK_GRAY + "|" + ChatColor.YELLOW + "any" + ChatColor.GREEN + " to queue! ***");
						NavyCraft.battleMode = 0;
						NavyCraft.battleType = battleType;
						NavyCraft.battleLockTeams = false;
						NavyCraft.redPlayers.clear();
						NavyCraft.bluePlayers.clear();
						NavyCraft.anyPlayers.clear();
						NavyCraft.playerKits.clear();
						NavyCraft.redPoints = 0;
						NavyCraft.bluePoints = 0;
						NavyCraft.redMerchant = false;
						NavyCraft.blueMerchant = false;
					}
				} else if (split[1].equalsIgnoreCase("spam")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.battle") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to start battles");
						event.setCancelled(true);
						return;
					}

					String battleTypeStr = "";
					switch (NavyCraft.battleType) {
					case 1:

						battleTypeStr = ChatColor.YELLOW + "Tunisia (desert-tanks and airplanes)";
						break;
					case 2:
						battleTypeStr = ChatColor.BLUE + "Tarawa (island-ships and airplanes)";
						break;
					case 3:
						battleTypeStr = ChatColor.DARK_AQUA + "North Sea (open ocean-ships)";
						break;
					case 4:
						battleTypeStr = "Normandy";
						break;
					case 5:
						battleTypeStr = "Wake Island";
						break;
					case 6:
						battleTypeStr = "Omaha";
						break;
					default:
						player.sendMessage(ChatColor.RED + "Invalid battle type option, use " + ChatColor.YELLOW + "/battle new" + ChatColor.RED + "list to view!");
						return;
					}
					plugin.getServer().broadcastMessage(ChatColor.GREEN + "*** OFFICIAL BATTLE QUEUE OPEN!!! ***");
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "*** Battle Map: " + battleTypeStr + ChatColor.AQUA + " ***");
					plugin.getServer().broadcastMessage(ChatColor.AQUA + "***" + ChatColor.YELLOW + " Started by: " + ChatColor.DARK_RED + player.getName() + ChatColor.AQUA + " ***");
					plugin.getServer().broadcastMessage(ChatColor.GREEN + "*** Type " + ChatColor.YELLOW + "/battle " + ChatColor.RED + "red" + ChatColor.DARK_GRAY + "|" + ChatColor.BLUE + "blue" + ChatColor.DARK_GRAY + "|" + ChatColor.YELLOW + "any" + ChatColor.GREEN + " to queue! ***");

				} else if (split[1].equalsIgnoreCase("start")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.battle") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to start battles");
						event.setCancelled(true);
						return;
					}

					if (NavyCraft.battleMode >= 1) {
						player.sendMessage(ChatColor.RED + "Battle already started!");
					} else if (NavyCraft.battleMode == -1) {
						player.sendMessage(ChatColor.YELLOW + "Do /battle new first!");
					} else {
						while (NavyCraft.anyPlayers.size() > 0) {
							if (NavyCraft.redPlayers.size() < NavyCraft.bluePlayers.size()) {
								NavyCraft.redPlayers.add(NavyCraft.anyPlayers.get(0));
								plugin.getServer().broadcastMessage(ChatColor.YELLOW + NavyCraft.anyPlayers.get(0) + ChatColor.RED + " joins the Red Team!");
							} else if (NavyCraft.redPlayers.size() > NavyCraft.bluePlayers.size()) {
								NavyCraft.bluePlayers.add(NavyCraft.anyPlayers.get(0));
								plugin.getServer().broadcastMessage(ChatColor.YELLOW + NavyCraft.anyPlayers.get(0) + ChatColor.BLUE + " joins the Blue Team!");
							} else {
								if (Math.random() < .5) {
									NavyCraft.redPlayers.add(NavyCraft.anyPlayers.get(0));
									plugin.getServer().broadcastMessage(ChatColor.YELLOW + NavyCraft.anyPlayers.get(0) + ChatColor.RED + " joins the Red Team!");
								} else {
									NavyCraft.bluePlayers.add(NavyCraft.anyPlayers.get(0));
									plugin.getServer().broadcastMessage(ChatColor.YELLOW + NavyCraft.anyPlayers.get(0) + ChatColor.BLUE + " joins the Blue Team!");
								}
							}
							NavyCraft.anyPlayers.remove(0);
						}

						/// battle types
						NavyCraft.redSpawn = new Location(player.getWorld(), 100, 64, 100);
						NavyCraft.blueSpawn = new Location(player.getWorld(), 200, 64, 200);
						String redWelcomeStr = "";
						String blueWelcomeStr = "";
						String logStr = "";
						if (NavyCraft.battleType == 1) {
							NavyCraft.redSpawn = new Location(player.getWorld(), -356, 69, 1114);
							NavyCraft.blueSpawn = new Location(player.getWorld(), -650, 67, 1485);
							redWelcomeStr = ChatColor.RED + "Welcome to Tunisia : Red Team Base!";
							blueWelcomeStr = ChatColor.BLUE + "Welcome to Tunisia : Blue Team Base!";
							logStr = "Battlezone: Tunisia";
							NavyCraft.battleLength = 1800000;
						} else if (NavyCraft.battleType == 2) {
							NavyCraft.redSpawn = new Location(player.getWorld(), 199, 60, -1065);
							NavyCraft.blueSpawn = new Location(player.getWorld(), -322, 75, -1166);
							redWelcomeStr = ChatColor.RED + "Welcome to Tarawa : Red Team Base!";
							blueWelcomeStr = ChatColor.BLUE + "Welcome to Tarawa : Blue Team Fleet!";
							logStr = "Battlezone: Tarawa";
							NavyCraft.battleLength = 1800000;
						} else if (NavyCraft.battleType == 3) {
							NavyCraft.redSpawn = new Location(player.getWorld(), -503.5, 64, -711.5);
							NavyCraft.blueSpawn = new Location(player.getWorld(), -629.5, 64, -105.5);
							redWelcomeStr = ChatColor.RED + "Welcome to the North Sea : Red Team Fleet!";
							blueWelcomeStr = ChatColor.BLUE + "Welcome to the North Sea : Blue Team Fleet!";
							logStr = "Battlezone: North Sea";
							NavyCraft.battleLength = 3600000;
						}

						plugin.getServer().broadcastMessage(ChatColor.GREEN + "*** OFFICIAL BATTLE STARTED! ***");
						CraftMover.battleLogger("***OFFICIAL BATTLE STARTED!!!***");
						CraftMover.battleLogger(logStr);
						for (String s : NavyCraft.redPlayers) {
							Player p = plugin.getServer().getPlayer(s);
							if (p != null) {
								p.teleport(NavyCraft.redSpawn);
								p.sendMessage(redWelcomeStr);
							}
						}
						for (String s : NavyCraft.bluePlayers) {
							Player p = plugin.getServer().getPlayer(s);
							if (p != null) {
								p.teleport(NavyCraft.blueSpawn);
								p.sendMessage(blueWelcomeStr);
							}
						}

						NavyCraft.battleMode = 1;
						battleTimerThread();
						NavyCraft.battleStartTime = System.currentTimeMillis();
					}
				} else if (split[1].equalsIgnoreCase("end")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.battle") && !player.isOp()) {
						player.sendMessage("You do not have permission to start battles");
						event.setCancelled(true);
						return;
					}

					if (NavyCraft.battleMode < 0) {
						player.sendMessage("No active battle.");
					} else {
						if (NavyCraft.battleMode > 0) {
							switch (NavyCraft.battleType) {
							case 1:
								endTunisia();
								break;
							case 2:
								endTarawa();
								break;
							case 3:
								endNorthSea();
								break;
							case 4:

								break;
							case 5:

								break;
							case 6:

								break;
							}
						} else {
							plugin.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "OFFICIAL BATTLE CANCELLED!");
						}

						if ((timerThread != null) && timerThread.isAlive()) {
							timerThread.stop();
						}

						NavyCraft.battleMode = -1;
						NavyCraft.battleType = -1;
						NavyCraft.battleLockTeams = false;
						NavyCraft.redPlayers.clear();
						NavyCraft.bluePlayers.clear();
						NavyCraft.anyPlayers.clear();
						NavyCraft.playerKits.clear();
						NavyCraft.redPoints = 0;
						NavyCraft.bluePoints = 0;
						NavyCraft.redMerchant = false;
						NavyCraft.blueMerchant = false;
					}
				} else if (split[1].equalsIgnoreCase("lock") && (NavyCraft.battleMode >= 0)) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.battle") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to start battles");
						event.setCancelled(true);
						return;
					}

					if (!NavyCraft.battleLockTeams) {
						player.sendMessage(ChatColor.DARK_RED + "Teams locked");
						NavyCraft.battleLockTeams = true;
					} else {
						player.sendMessage(ChatColor.DARK_GREEN + "Teams open");
						NavyCraft.battleLockTeams = false;
					}
				}
			}
			event.setCancelled(true);
			return;

		} else if (craftName.equalsIgnoreCase("volume")) {
			if (split.length > 1) {
				if (split[1].equalsIgnoreCase("help")) {
					player.sendMessage(ChatColor.GOLD + "Volume v" + ChatColor.GREEN + NavyCraft.version + ChatColor.GOLD + " commands :");
					player.sendMessage(ChatColor.AQUA + "/volume - status message");
					player.sendMessage(ChatColor.AQUA + "/volume <type> <volume> - sets volume for type");
					player.sendMessage(ChatColor.AQUA + "/volume <type> mute - mutes volume");
					player.sendMessage(ChatColor.YELLOW + "Types: engine, weapons, other, all");
				}
				if (split[1].equalsIgnoreCase("engine")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.basic") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to set engine volume.");
						event.setCancelled(true);
						return;
					}
					if (split[2].equalsIgnoreCase("mute")) {
						float inValue = 0.0f;
						NavyCraft.playerEngineVolumes.put(player, inValue);
						NavyCraft_FileListener.saveVolume(player.getName());
						player.sendMessage(ChatColor.GOLD + "Engine volume muted");
						event.setCancelled(true);
						return;
					}
					{
						if (split.length == 3) {
							float inValue = 1.0f;
							try {
								inValue = Float.parseFloat(split[2]);
								if ((inValue >= 0) && (inValue <= 100.0f)) {
									NavyCraft.playerEngineVolumes.put(player, inValue);
									NavyCraft_FileListener.saveVolume(player.getName());
									player.sendMessage(ChatColor.GOLD + "Volume set for engines - " + ChatColor.GREEN + inValue + "%");
								} else {
									player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
								}
							} catch (NumberFormatException e) {
								player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
							}
						} else {
							player.sendMessage(ChatColor.YELLOW + "Change engine volume with /volume engine <%> with % from 0 to 100");
						}
						event.setCancelled(true);
						return;
					}
				}
				if (split[1].equalsIgnoreCase("weapon")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.basic") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to set gun volume.");
						event.setCancelled(true);
						return;
					}
					if (split[2].equalsIgnoreCase("mute")) {
						float inValue = 0.0f;
						NavyCraft.playerWeaponVolumes.put(player, inValue);
						NavyCraft_FileListener.saveVolume(player.getName());
						player.sendMessage(ChatColor.GOLD + "Gun volume muted");
						event.setCancelled(true);
						return;
					}
					{
						if (split.length == 3) {
							float inValue = 1.0f;
							try {
								inValue = Float.parseFloat(split[2]);
								if ((inValue >= 0) && (inValue <= 100.0f)) {
									NavyCraft.playerWeaponVolumes.put(player, inValue);
									NavyCraft_FileListener.saveVolume(player.getName());
									player.sendMessage(ChatColor.GOLD + "Volume set for weapons - " + ChatColor.GREEN + inValue + "%");
								} else {
									player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
								}
							} catch (NumberFormatException e) {
								player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
							}
						} else {
							player.sendMessage(ChatColor.YELLOW + "Change weapon volume with /volume weapon <%> with % from 0 to 100");
						}
						event.setCancelled(true);
						return;
					}
				}
				if (split[1].equalsIgnoreCase("other")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.basic") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to set other volume.");
						event.setCancelled(true);
						return;
					}
					if (split[2].equalsIgnoreCase("mute")) {
						float inValue = 0.0f;
						NavyCraft.playerOtherVolumes.put(player, inValue);
						NavyCraft_FileListener.saveVolume(player.getName());
						player.sendMessage(ChatColor.GOLD + "Other volumes muted");
						event.setCancelled(true);
						return;
					}
					{
						if (split.length == 3) {
							float inValue = 1.0f;
							try {
								inValue = Float.parseFloat(split[2]);
								if ((inValue >= 0) && (inValue <= 100.0f)) {
									NavyCraft.playerOtherVolumes.put(player, inValue);
									NavyCraft_FileListener.saveVolume(player.getName());
									player.sendMessage(ChatColor.GOLD + "Volume set for other - " + ChatColor.GREEN + inValue + "%");
								} else {
									player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
								}
							} catch (NumberFormatException e) {
								player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
							}
						} else {
							player.sendMessage(ChatColor.YELLOW + "Change other volume with /volume other <%> with % from 0 to 100");
						}
						event.setCancelled(true);
						return;
					}
				}
				if (split[1].equalsIgnoreCase("all")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.basic") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to set other volume.");
						event.setCancelled(true);
						return;
					}
					if (split[2].equalsIgnoreCase("mute")) {
						float inValue = 0.0f;
						NavyCraft.playerEngineVolumes.put(player, inValue);
						NavyCraft.playerWeaponVolumes.put(player, inValue);
						NavyCraft.playerOtherVolumes.put(player, inValue);
						NavyCraft_FileListener.saveVolume(player.getName());
						player.sendMessage(ChatColor.GOLD + "All volume muted");
						event.setCancelled(true);
						return;
					}
					{
						if (split.length == 3) {
							float inValue = 1.0f;
							try {
								inValue = Float.parseFloat(split[2]);
								if ((inValue >= 0) && (inValue <= 100.0f)) {
									NavyCraft.playerOtherVolumes.put(player, inValue);
									NavyCraft.playerWeaponVolumes.put(player, inValue);
									NavyCraft.playerEngineVolumes.put(player, inValue);
									NavyCraft_FileListener.saveVolume(player.getName());
									player.sendMessage(ChatColor.GOLD + "Volume set for all - " + ChatColor.GREEN + inValue + "%");
								} else {
									player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
								}
							} catch (NumberFormatException e) {
								player.sendMessage(ChatColor.RED + "Invalid volume percent, use a number from 0 to 100");
							}
						} else {
							player.sendMessage(ChatColor.YELLOW + "Change all volume with /volume all <%> with % from 0 to 100");
						}
						event.setCancelled(true);
						return;
					}
				} else {
					player.sendMessage("Unknown command. Type \"/volume\" for help.");
					event.setCancelled(true);
					return;
				}
			} else {
				player.sendMessage(ChatColor.GOLD + "Volume v" + ChatColor.GREEN + NavyCraft.version + ChatColor.GOLD + " commands :");
				player.sendMessage(ChatColor.AQUA + "/volume - status message");
				player.sendMessage(ChatColor.AQUA + "/volume <type> <volume> - sets volume for type");
				player.sendMessage(ChatColor.AQUA + "/volume <type> mute - mutes volume");
				player.sendMessage(ChatColor.YELLOW + "Types: engine, weapons, other, all");
			}
			return;
		} else if (craftName.equalsIgnoreCase("explode")) {
			if (PermissionInterface.CheckPerm(player, "navycraft.admin")) {
				if (split.length == 2) {
					float inValue = 1.0f;
					try {
						inValue = Float.parseFloat(split[1]);
						if ((inValue >= 1) && (inValue <= 100.0f)) {
							NavyCraft.explosion((int) inValue, player.getLocation().getBlock(), false);
							Craft checkCraft = null;
							checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation(), player);
							if (checkCraft == null) {
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(7, 7, 7).getLocation(), player);
								if (checkCraft == null) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(-7, -7, -7).getLocation(), player);
									if (checkCraft == null) {
										checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(3, -2, -3).getLocation(), player);
										if (checkCraft == null) {
											checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(-3, 2, 3).getLocation(), player);
										}
									}
								}
							}

							if (checkCraft == null) {
								player.sendMessage(ChatColor.GOLD + "Boom Level" + ChatColor.DARK_GRAY + "- " + ChatColor.GREEN + inValue);
							} else {
								player.sendMessage(ChatColor.GOLD + "Boom level " + ChatColor.DARK_GRAY + "- " + ChatColor.GREEN + inValue + ChatColor.GOLD + " done on " + ChatColor.GREEN + checkCraft.name);
							}
						} else {
							player.sendMessage(ChatColor.RED + "Invalid explosion level, use a number from 1 to 100");
						}
					} catch (NumberFormatException e) {
						player.sendMessage(ChatColor.RED + "Invalid explosion level, use a number from 1 to 100");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "/explode ###  number from 1-100");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to use that.");
			}

			event.setCancelled(true);
			return;
		} else if (craftName.equalsIgnoreCase("explodesigns")) {
			if (PermissionInterface.CheckPerm(player, "navycraft.admin")) {
				if (split.length == 2) {
					float inValue = 1.0f;
					try {
						inValue = Float.parseFloat(split[1]);
						if ((inValue >= 1) && (inValue <= 100.0f)) {
							NavyCraft.explosion((int) inValue, player.getLocation().getBlock(), true);
							player.sendMessage(ChatColor.GOLD + "Boom Level" + ChatColor.DARK_GRAY + "- " + ChatColor.GREEN + inValue);
						} else {
							player.sendMessage(ChatColor.RED + "Invalid explosion level, use a number from 1 to 100");
						}
					} catch (NumberFormatException e) {
						player.sendMessage(ChatColor.RED + "Invalid explosion level, use a number from 1 to 100");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "/explode ###  number from 1-100");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to use that.");
			}

			event.setCancelled(true);
			return;
		} else if (craftName.equalsIgnoreCase("rank")) {
			if (split.length > 1) {
				if (split[1].equalsIgnoreCase("help")) {
					player.sendMessage(ChatColor.GOLD + "Rank v" + ChatColor.GREEN + NavyCraft.version + ChatColor.GOLD + " commands :");
					player.sendMessage(ChatColor.AQUA + "/rank - view your rank");
					player.sendMessage(ChatColor.AQUA + "/rank view <player> - view players exp");
					player.sendMessage(ChatColor.AQUA + "/rank list - list ranks in the plugin");
					if (PermissionInterface.CheckQuietPerm(player, "navycraft.admin") || player.isOp()) {
						player.sendMessage(ChatColor.RED + "Rank Admin v" + ChatColor.GREEN + NavyCraft.version + ChatColor.RED + "commands :");
						player.sendMessage(ChatColor.BLUE + "/rank set <player> <exp> - set a players exp");
						player.sendMessage(ChatColor.BLUE + "/rank add <player> <exp> - give exp to a player");
						player.sendMessage(ChatColor.BLUE + "/rank remove <player> <exp> - remove exp from a player");
					}
				}
				if (split[1].equalsIgnoreCase("list")) {
					player.sendMessage(ChatColor.WHITE + "Rank list :");
					player.sendMessage(ChatColor.DARK_AQUA + "Ensign - 0 EXP");
					player.sendMessage(ChatColor.GREEN + "LT. JT - 1000 EXP");
					player.sendMessage(ChatColor.DARK_GREEN + "Lieutenant - 6000");
					player.sendMessage(ChatColor.WHITE + "Lt. Cmdr - 18000");
					player.sendMessage(ChatColor.DARK_PURPLE + "Commander - 54000");
					player.sendMessage(ChatColor.YELLOW + "Captian - 162000");
					player.sendMessage(ChatColor.GOLD + "R Admiral - 486000");
					player.sendMessage(ChatColor.GOLD + "R Admiral+ - 1458000");
					player.sendMessage(ChatColor.GOLD + "Vice Admiral - 1858000");
					player.sendMessage(ChatColor.GOLD + "Admiral - 2000000");
				}
				if (split[1].equalsIgnoreCase("view")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.basic") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to view players ranks.");
						event.setCancelled(true);
						return;
					}

					if (split.length < 3) {
						player.sendMessage(ChatColor.GOLD + "Usage - /rank view <player>");
						player.sendMessage(ChatColor.GOLD + "Example - /rank view Solmex");
						event.setCancelled(true);
						return;
					}
					String p = split[2];
					{
						NavyCraft_BlockListener.showRank(player, p);
					}
				}
				if (split[1].equalsIgnoreCase("set")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to set exp.");
						event.setCancelled(true);
						return;
					}

					if (split.length < 4) {
						player.sendMessage(ChatColor.GOLD + "Usage - /rank set <player> <exp>");
						player.sendMessage(ChatColor.GOLD + "Example - /rank set Solmex 100");
						event.setCancelled(true);
						return;
					}
					int newExp = Math.abs(Integer.parseInt(split[3]));
					String p = split[2];
					{
						NavyCraft_BlockListener.setExpPlayer(newExp, p);
					}
					{
						NavyCraft_BlockListener.showRank(player, p);
					}
				}
				if (split[1].equalsIgnoreCase("add")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to add exp.");
						event.setCancelled(true);
						return;
					}

					if (split.length < 4) {
						player.sendMessage(ChatColor.GOLD + "Usage - /rank remove <player> <exp>");
						player.sendMessage(ChatColor.GOLD + "Example - /rank add Solmex 100");
						event.setCancelled(true);
						return;
					}
					int newExp = Math.abs(Integer.parseInt(split[3]));
					String p = split[2];
					{
						NavyCraft_BlockListener.addExpPlayer(newExp, p);
					}
					{
						NavyCraft_BlockListener.showRank(player, p);
					}
				}
				if (split[1].equalsIgnoreCase("remove")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.admin") && !player.isOp()) {
						player.sendMessage(ChatColor.RED + "You do not have permission to remove exp.");
						event.setCancelled(true);
						return;
					}

					if (split.length < 4) {
						player.sendMessage(ChatColor.GOLD + "Usage - /rank remove <player> <exp>");
						player.sendMessage(ChatColor.GOLD + "Example - /rank remove Solmex 100");
						event.setCancelled(true);
						return;
					}
					int newExp = Math.abs(Integer.parseInt(split[3]));
					String p = split[2];
					{
						NavyCraft_BlockListener.removeExpPlayer(newExp, p);
					}
					{
						NavyCraft_BlockListener.showRank(player, p);
					}
				}
				event.setCancelled(true);
				return;

			} else {
				{
					NavyCraft_BlockListener.getRank(player);
				}
				event.setCancelled(true);
				return;
			}
		} else if (craftType != null) {

			if (processCommand(craftType, player, split) == true) {
				event.setCancelled(true);
			}
		} else {
			Craft craft = Craft.getPlayerCraft(player);

			if (craft == null) {
				return;
			}

			int i = 0;
			while (i < split.length) {
				String tmpName = split[0];
				// build out tmpName with 0 + i
				if (tmpName.equalsIgnoreCase(craft.name)) {
					if (processCommand(craftType, player, split) == true) {
						event.setCancelled(true);
					}
				}
				i++;
			}
		}
		return;
	}

	public boolean processCommand(CraftType craftType, Player player, String[] split) {

		Craft craft = Craft.getPlayerCraft(player);

		if (split.length >= 2) {

			if (split[1].equalsIgnoreCase(craftType.driveCommand)) {

				String name = craftType.name;
				if ((split.length > 2) && (split[2] != null)) {
					name = split[2];
				}

				// try to detect and create the craft
				// use the block the player is standing on
				Craft checkCraft = Craft.getCraft(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
				if (checkCraft != null) {

				} else {
					NavyCraft.instance.createCraft(player, craftType, (int) Math.floor(player.getLocation().getX()), (int) Math.floor(player.getLocation().getY() - 1), (int) Math.floor(player.getLocation().getZ()), name, player.getLocation().getYaw(), null);
				}
				return true;

			} else if (split[1].equalsIgnoreCase("move")) {
				try {
					int dx = Integer.parseInt(split[2]);
					int dy = Integer.parseInt(split[3]);
					int dz = Integer.parseInt(split[4]);

					CraftMover cm = new CraftMover(craft, plugin);
					cm.calculateMove(dx, dy, dz);
				} catch (Exception ex) {
					player.sendMessage(ChatColor.WHITE + "Invalid movement parameters. Please use " + ChatColor.AQUA + "Move x y z " + ChatColor.WHITE + " Where x, y, and z are whole numbers separated by spaces.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("setspeed")) {
				int speed = Math.abs(Integer.parseInt(split[2]));

				if ((speed < 1) || (speed > craftType.maxSpeed)) {
					player.sendMessage(ChatColor.YELLOW + "Allowed speed between 1 and " + craftType.maxSpeed);
					return true;
				}

				craft.setSpeed(speed);
				player.sendMessage(ChatColor.YELLOW + craft.name + "'s speed set to " + craft.speed);

				return true;

			} else if (split[1].equalsIgnoreCase("setname")) {
				if (split.length > 2) {
					craft.name = split[2];
					player.sendMessage(ChatColor.YELLOW + craft.type.name + "'s name set to " + craft.name);
					return true;
				}

			} else if (split[1].equalsIgnoreCase("release")) {
				if (craft != null) {
					if ((craft.captainName == player.getName()) || player.isOp()) {
						player.sendMessage(ChatColor.GOLD + "You release command of the ship");
						craft.releaseCraft();
						if (player.getInventory().contains(Material.GOLD_SWORD)) {
							player.getInventory().remove(Material.GOLD_SWORD);
						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "No vehicle detected.");
				}
				return true;

			} else if (split[1].equalsIgnoreCase("reload") && (PermissionInterface.CheckPerm(player, "navycraft.reload"))) {

				if (craft != null) {
					CraftMover cm = new CraftMover(craft, plugin);
					cm.reloadWeapons(player);
				} else {
					player.sendMessage(ChatColor.RED + "No vehicle detected.");
				}

				// }

				return true;

			} else if (split[1].equalsIgnoreCase("drive") && (PermissionInterface.CheckPerm(player, "navycraft.admin"))) {
				if (player.getItemInHand().getTypeId() > 0) {
					player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
					return true;
				}
				craft.driverName = player.getName();
				craft.haveControl = true;
				player.sendMessage(ChatColor.GOLD + "You take control of the helm.");
				player.setItemInHand(new ItemStack(283, 1));
				CraftMover cm = new CraftMover(craft, plugin);
				cm.structureUpdate(null, false);

				return true;

			} else if (split[1].equalsIgnoreCase("info")) {

				player.sendMessage(ChatColor.DARK_AQUA + craftType.name);
				if (craft != null) {
					player.sendMessage(ChatColor.GOLD + "Using " + craft.blockCount + " of " + craftType.maxBlocks + " blocks (minimum " + craftType.minBlocks + ").");

				} else {
					player.sendMessage(ChatColor.GOLD + Integer.toString(craftType.minBlocks) + "-" + craftType.maxBlocks + " blocks.");
				}
				player.sendMessage(ChatColor.GOLD + "Max speed: " + craftType.maxSpeed);

				if (NavyCraft.instance.DebugMode) {
					player.sendMessage(ChatColor.GOLD + Integer.toString(craft.dataBlocks.size()) + " data Blocks, " + craft.complexBlocks.size() + " complex Blocks, " + craft.engineBlocks.size() + " engine Blocks," + craft.digBlockCount + " drill bits.");
				}

				String canDo = ChatColor.YELLOW + craftType.name + "s can ";

				if (craftType.canFly) {
					canDo += "fly, ";
				}

				if (craftType.canDive) {
					canDo += "dive, ";
				}

				if (craftType.canDig) {
					canDo += "dig, ";
				}

				if (craftType.canNavigate) {
					canDo += " navigate on both water and lava, ";
				}

				player.sendMessage(canDo);

				return true;

			} else if (split[1].equalsIgnoreCase("command") && (PermissionInterface.CheckPerm(player, "navycraft.admin"))) {
				Craft testCraft = Craft.getCraft(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
				if (testCraft != null) {
					if (craft != null)// && playerCraft.type == craftType) {
					{

						craft.leaveCrew(player);
					}

					testCraft.buildCrew(player, false);

					CraftMover cm = new CraftMover(testCraft, plugin);
					cm.structureUpdate(null, false);
					if (testCraft.captainName == player.getName()) {
						player.sendMessage(ChatColor.GOLD + "You admin-hijack this vehicle!");
					}
				}
				return true;
			} else if (split[1].equalsIgnoreCase("remove")) {
				if (craft != null) {
					if ((craft.captainName == player.getName()) || (player.isOnline() && player.isOp())) {
						if (PermissionInterface.CheckPerm(player, "navycraft.admin")) {
							craft.doRemove = true;
							if (player.getInventory().contains(Material.GOLD_SWORD)) {
								player.getInventory().remove(Material.GOLD_SWORD);
							}
							player.sendMessage(ChatColor.GREEN + "Vehicle Removed");
						} else {
							player.sendMessage(ChatColor.RED + "You do not have permission for this command. Use: " + ChatColor.YELLOW + "/ship disable" + ChatColor.RED + "instead.");
						}

					} else {
						player.sendMessage(ChatColor.RED + "You do not command this ship.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "No vehicle detected.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("disable")) {
				if (craft != null) {
					if ((craft.captainName == player.getName()) || (player.isOnline() && player.isOp())) {
						if (NavyCraft.checkRepairRegion(craft.getLocation())) {
							craft.doRemove = true;
							if (player.getInventory().contains(Material.GOLD_SWORD)) {
								player.getInventory().remove(Material.GOLD_SWORD);
							}
							player.sendMessage(ChatColor.GREEN + "Vehicle disabled.");
						} else if (!checkProtectedRegion(player, player.getLocation())) {
							craft.helmDestroyed = true;
							craft.setSpeed = 0;
							playerDisableThread(player, craft);
							player.sendMessage(ChatColor.RED + "Your vehicle will be fully disabled in 1 minute.");
						} else {
							player.sendMessage(ChatColor.RED + "You can only use that command in a repair dock within the safe dock area.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "You do not command this ship.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "No vehicle detected.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("destroy")) {
				if (craft != null) {
					if ((craft.captainName == player.getName()) || player.isOp()) {
						if (checkProtectedRegion(player, craft.getLocation()) || PermissionInterface.CheckPerm(player, "navycraft.basic")) {
							craft.doDestroy = true;
							if (player.getInventory().contains(Material.GOLD_SWORD)) {
								player.getInventory().remove(Material.GOLD_SWORD);
							}
							player.sendMessage(ChatColor.GREEN + "Vehicle Destroyed");
						} else {
							player.sendMessage(ChatColor.RED + "You can only use this command in a safe dock region.");
						}

					} else {
						player.sendMessage(ChatColor.RED + "You do not command this ship.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "No vehicle detected.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("sink")) {
				if (craft != null) {
					if (!craft.sinking) {
						if (craft.captainName == player.getName()) {
							if (!checkProtectedRegion(player, craft.getLocation())) {

								craft.helmDestroyed = true;
								craft.setSpeed = 0;
								playerSinkThread(craft);
								player.sendMessage(ChatColor.RED + "Your vehicle will be scuttled in 3 minutes.");
							} else {
								player.sendMessage(ChatColor.RED + "This command cannot be used within a protected region.");
							}
						} else {
							player.sendMessage(ChatColor.RED + "You do not command this ship.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "You are already sinking!");
					}
				} else {
					player.sendMessage(ChatColor.RED + "No vehicle detected.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("update")) {

				return true;
			} else if (split[1].equalsIgnoreCase("turn")) {

				if (craft != null) {
					if (craft.autoTurn) {
						if ((split.length > 2) && (split[2] != null)) {
							if (split[2].equalsIgnoreCase("right")) {
								craft.turn(90);
								return true;
							} else if (split[2].equalsIgnoreCase("left")) {
								craft.turn(270);
								return true;
							} else if (split[2].equalsIgnoreCase("around")) {
								craft.turn(180);
								return true;
							}
							return false;
						} else {
							return false;
						}
					} else {
						player.sendMessage(ChatColor.RED + "You cannot use this command on this vehicle.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "No vehicle detected.");
				}
			} else if (split[1].equalsIgnoreCase("leave")) {
				if (craft != null) {
					craft.leaveCrew(player);
					player.sendMessage(ChatColor.GREEN + "You leave the crew.");
				} else {
					player.sendMessage(ChatColor.RED + "You are not on a crew.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("crew")) {
				if (craft != null) {
					if (craft.captainName == player.getName()) {
						craft.buildCrew(player, false);
					} else {
						player.sendMessage(ChatColor.RED + "You are not the captain of this crew.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You are not on a crew.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("add")) {
				if (craft != null) {
					if (craft.captainName == player.getName()) {
						ArrayList<Entity> ents = craft.getCraftEntities(false);

						for (Entity e : ents) {
							if (e instanceof Player) {
								Player p = (Player) e;
								if (p.getName() == craft.captainName) {
									craft.buildCrew(player, true);
								}
							}
						}
					} else {
						player.sendMessage(ChatColor.RED + "You are not the captain of this crew.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You are not on a crew.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("summon")) {
				if (craft != null) {
					if (craft.captainName == player.getName()) {
						if (craft.signLoc != null) {
							player.sendMessage(ChatColor.GOLD + "Summoning crew to your vehicle.");
							for (String s : craft.crewNames) {
								Player p = plugin.getServer().getPlayer(s);
								if (p != null) {
									if (!NavyCraft.shipTPCooldowns.containsKey(s) || (System.currentTimeMillis() > (NavyCraft.shipTPCooldowns.get(s) + 60000))) {
										NavyCraft.shipTPCooldowns.put(s, System.currentTimeMillis());
										p.teleport(new Location(craft.world, craft.signLoc.getBlockX() + 0.5, craft.signLoc.getBlockY() + 0.1, craft.signLoc.getBlockZ() + 0.5));
									} else {
										int timeLeft = (int) (((NavyCraft.shipTPCooldowns.get(s) + 60000) - System.currentTimeMillis()) / 60000);
										player.sendMessage(ChatColor.RED + "Player, " + s + " is on cooldown for " + timeLeft + " minute");
									}
								} else {
									player.sendMessage(ChatColor.RED + "Player, " + s + " not located.");
								}
							}
						} else {
							player.sendMessage(ChatColor.RED + "Vehicle sign not located.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "You are not the captain of this crew.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You are not on a crew.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("tp")) {
				if (craft != null) {
					if (craft.signLoc != null) {
						if (!NavyCraft.shipTPCooldowns.containsKey(player.getName()) || (System.currentTimeMillis() > (NavyCraft.shipTPCooldowns.get(player.getName()) + 60000))) {
							NavyCraft.shipTPCooldowns.put(player.getName(), System.currentTimeMillis());
							player.teleport(new Location(craft.world, craft.signLoc.getBlockX() + 0.5, craft.signLoc.getBlockY() + 0.1, craft.signLoc.getBlockZ() + 0.5));
						} else {
							int timeLeft = (int) (((NavyCraft.shipTPCooldowns.get(player.getName()) + 60000) - System.currentTimeMillis()) / 60000);
							player.sendMessage(ChatColor.RED + "You are on cooldown for " + timeLeft + " min");
						}
					} else {
						player.sendMessage(ChatColor.RED + "Vehicle sign not located.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You are not on a crew.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("buoy") && PermissionInterface.CheckPerm(player, "navycraft.admin")) {
				if (craft != null) {
					if ((split.length > 3) && split[2].equalsIgnoreCase("block")) {
						float blockValue = 0.33f;
						try {
							blockValue = Float.parseFloat(split[3]);
							if ((blockValue >= 0.01f) && (blockValue <= 100.0f)) {
								craft.blockDispValue = blockValue;
							} else {
								player.sendMessage(ChatColor.RED + "Invalid block displacement value, use 0.01 to 100.0");
							}
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid block displacement value, use 0.01 to 100.0");
						}
					} else if ((split.length > 3) && split[2].equalsIgnoreCase("air")) {
						float airValue = 5.00f;
						try {
							airValue = Float.parseFloat(split[3]);
							if ((airValue >= 0.01f) && (airValue <= 100.0f)) {
								craft.airDispValue = airValue;
							} else {
								player.sendMessage(ChatColor.RED + "Invalid air displacement value, use 0.01 to 100.0");
							}
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid air displacement value, use 0.01 to 100.0");
						}
					} else if ((split.length > 3) && split[2].equalsIgnoreCase("min")) {
						float minValue = 5.00f;
						try {
							minValue = Float.parseFloat(split[3]);
							if ((minValue >= 0.01f) && (minValue <= 100.0f)) {
								craft.minDispValue = minValue;
							} else {
								player.sendMessage("Invalid min displacement value, use 0.01 to 100.0");
							}
						} catch (NumberFormatException e) {
							player.sendMessage("Invalid min displacement value, use 0.01 to 100.0");
						}
					} else if ((split.length > 3) && split[2].equalsIgnoreCase("weight")) {
						float weightValue = 1.00f;
						try {
							weightValue = Float.parseFloat(split[3]);
							if ((weightValue >= 0.01f) && (weightValue <= 100.0f)) {
								craft.weightMult = weightValue;
							} else {
								player.sendMessage("Invalid weight multiplier value, use 0.01 to 100.0");
							}
						} catch (NumberFormatException e) {
							player.sendMessage("Invalid weight multiplier value, use 0.01 to 100.0");
						}
					} else {

						player.sendMessage("Block Displacement = " + craft.blockDispValue + ", use /ship buoy block <value> to set");
						player.sendMessage("Air Displacement = " + craft.airDispValue + ", use /ship buoy air <value> to set");
						player.sendMessage("Minimum Displacement = " + craft.minDispValue + ", use /ship buoy min <value> to set");
						player.sendMessage("Weight Multiplier = " + craft.weightMult + ", use /ship buoy weight <value> to set");
					}
				}
				return true;
			} else if (split[1].equalsIgnoreCase("help")) {
				if (PermissionInterface.CheckPerm(player, "navycraft.basic")) {
					player.sendMessage(ChatColor.GOLD + "Vehicle Commands v" + ChatColor.GREEN + NavyCraft.version + ChatColor.GOLD + " :");
					player.sendMessage(ChatColor.AQUA + "/ship - Ship Status");
					player.sendMessage(ChatColor.AQUA + "/ship tp - Teleport to your vehicle (1 min cooldown)");
					player.sendMessage(ChatColor.AQUA + "/ship leave - Leave the crew of your ship");
					player.sendMessage(ChatColor.AQUA + "/radio <message> - (or /ra) Send radio message (if equipped)");
					player.sendMessage(ChatColor.AQUA + "/radio - (or /ra) Radio status");
					player.sendMessage(ChatColor.AQUA + "/crew <message> - Send message to your crew");
					player.sendMessage(ChatColor.AQUA + "/crew - Crew status");
					player.sendMessage(ChatColor.DARK_AQUA + "/ship release - (Cpt) Release your command of the ship");
					player.sendMessage(ChatColor.DARK_AQUA + "/ship crew - (Cpt) Recreates your crew with players on your vehicle");
					player.sendMessage(ChatColor.DARK_AQUA + "/ship add - (Cpt) Add players on your vehicle to your crew");
					player.sendMessage(ChatColor.DARK_AQUA + "/ship summon - (Cpt) Teleports you and your crew to your vehicle (10 min cooldown)");
					player.sendMessage(ChatColor.DARK_AQUA + "/ship repair - (Cpt) Repairs your vehicle if in repair dock region");
					player.sendMessage(ChatColor.DARK_AQUA + "/ship store - (Cpt) Stores your vehicle if in a storage dock region");
					player.sendMessage(ChatColor.DARK_AQUA + "/ship disable - (Cpt) Deactivates a vehicle, so that it can be modified");
					player.sendMessage(ChatColor.DARK_AQUA + "/ship sink - (Cpt) Scuttles your vehicle after a timer");
					player.sendMessage(ChatColor.DARK_AQUA + "/ship destroy - (Cpt) Destroys your vehicle, usable in safedock region");
				}
				if (PermissionInterface.CheckQuietPerm(player, "navycraft.admin")) {
					player.sendMessage(ChatColor.BLUE + "/ship command - (Mod) Steal command of a ship");
					player.sendMessage(ChatColor.BLUE + "/ship remove - (Mod) Instantly disable a ship");
					player.sendMessage(ChatColor.BLUE + "/ship drive - (Mod) Drive without sign");
					player.sendMessage(ChatColor.BLUE + "/ship buoy - (Mod) View and modify buoyancy variables");
				}
				return true;
			} else {
				player.sendMessage("Unknown command. Type \"/ship help\" for help.");
				return true;
			}
		} else {
			if (craft != null) {
				player.sendMessage(ChatColor.GOLD + "Vehicle Status");
				player.sendMessage(ChatColor.GOLD + "Type : " + ChatColor.WHITE + craft.name);
				if (craft.customName != null) {
					player.sendMessage(ChatColor.GOLD + "Name : " + ChatColor.WHITE + craft.customName);
				} else {
					player.sendMessage(ChatColor.GOLD + "Name : " + ChatColor.WHITE + craft.name);
				}
				player.sendMessage(ChatColor.GOLD + "Captain : " + ChatColor.DARK_AQUA + craft.captainName);
				player.sendMessage(ChatColor.GOLD + "Crew : " + ChatColor.BLUE + craft.crewNames.size());
				player.sendMessage(ChatColor.GOLD + "Size : " + ChatColor.WHITE + craft.blockCount + " blocks");
				player.sendMessage(ChatColor.GOLD + "Weight (current) : " + ChatColor.WHITE + craft.weightCurrent + " tons");
				player.sendMessage(ChatColor.GOLD + "Weight (start) : " + ChatColor.WHITE + craft.weightStart + " tons");
				player.sendMessage(ChatColor.GOLD + "Displacement : " + ChatColor.WHITE + craft.displacement + " tons (" + craft.blockDisplacement + " block," + craft.airDisplacement + " air)");
				player.sendMessage(ChatColor.GOLD + "Health : " + ChatColor.WHITE + (int) (((float) craft.blockCount * 100) / craft.blockCountStart) + "%");
				player.sendMessage(ChatColor.GOLD + "Engines : " + ChatColor.WHITE + craft.engineIDLocs.size() + " of " + craft.engineIDIsOn.size());
			} else {
				player.sendMessage(ChatColor.GOLD + "You have no active vehicle.");
			}

			return true;
		}
		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerEggThrow(PlayerEggThrowEvent event) {
		// event.getEgg().remove();
		Egg egg = event.getEgg();

		if (NavyCraft.explosiveEggsList.contains(egg)) {
			if (checkProtectedRegion(event.getPlayer(), egg.getLocation())) {
				event.getPlayer().sendMessage(ChatColor.RED + "No AA Allowed In Dock Area");
				return;
			}

			event.setHatching(false);

			Block eggBlock = egg.getLocation().getBlock();
			int fuseDelay = 5;
			if (eggBlock.getY() >= 63) {
				Craft checkCraft = Craft.getPlayerCraft(event.getPlayer());
				if (checkCraft != null) {
					if (checkCraft.isIn(eggBlock.getX(), eggBlock.getY(), eggBlock.getZ())) {
						return;
					}
				}

				int blockType = eggBlock.getTypeId();
				double randomNum = Math.random();
				if ((blockType != 0) && (blockType != 8) && (blockType != 9)) {

					if (Craft.blockHardness(blockType) == 1) {
						if (randomNum >= .3) {
							eggBlock.setTypeId(0);
						}
					} else if (Craft.blockHardness(blockType) == 0) {
						eggBlock.setTypeId(0);
					} else if ((Craft.blockHardness(blockType) == 46) && (randomNum >= 0.5)) {

						TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld().spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(), eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
						tnt.setFuseTicks(fuseDelay);
						fuseDelay = fuseDelay + 100;
					}
				}

				//// north south
				randomNum = Math.random();

				if (randomNum >= .2) {
					blockType = eggBlock.getRelative(BlockFace.NORTH).getTypeId();
					if ((blockType != 0) && (blockType != 8) && (blockType != 9)) {

						if (Craft.blockHardness(blockType) == 1) {
							if (randomNum >= .3) {
								eggBlock.getRelative(BlockFace.NORTH).setTypeId(0);
							}
						} else if (Craft.blockHardness(blockType) == 0) {
							eggBlock.getRelative(BlockFace.NORTH).setTypeId(0);
						} else if ((Craft.blockHardness(blockType) == 46) && (randomNum >= 0.5)) {

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld().spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(), eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
							tnt.setFuseTicks(fuseDelay);
							fuseDelay = fuseDelay + 2;
						}
					}

					blockType = eggBlock.getRelative(BlockFace.SOUTH).getTypeId();
					if ((blockType != 0) && (blockType != 8) && (blockType != 9)) {

						if (Craft.blockHardness(blockType) == 1) {
							if (randomNum >= .3) {
								eggBlock.getRelative(BlockFace.SOUTH).setTypeId(0);
							}
						} else if (Craft.blockHardness(blockType) == 0) {
							eggBlock.getRelative(BlockFace.SOUTH).setTypeId(0);
						} else if ((Craft.blockHardness(blockType) == 46) && (randomNum >= 0.5)) {

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld().spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(), eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
							tnt.setFuseTicks(fuseDelay);
							fuseDelay = fuseDelay + 2;
						}
					}
				}

				///// east/west
				randomNum = Math.random();

				if (randomNum >= .2) {
					blockType = eggBlock.getRelative(BlockFace.EAST).getTypeId();
					if ((blockType != 0) && (blockType != 8) && (blockType != 9)) {

						if (Craft.blockHardness(blockType) == 1) {
							if (randomNum >= .3) {
								eggBlock.getRelative(BlockFace.EAST).setTypeId(0);
							}
						} else if (Craft.blockHardness(blockType) == 0) {
							eggBlock.getRelative(BlockFace.EAST).setTypeId(0);
						} else if ((Craft.blockHardness(blockType) == 46) && (randomNum >= 0.5)) {

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld().spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(), eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
							tnt.setFuseTicks(fuseDelay);

							fuseDelay = fuseDelay + 2;
						}
					}

					blockType = eggBlock.getRelative(BlockFace.WEST).getTypeId();
					if ((blockType != 0) && (blockType != 8) && (blockType != 9)) {

						if (Craft.blockHardness(blockType) == 1) {
							if (randomNum >= .3) {
								eggBlock.getRelative(BlockFace.WEST).setTypeId(0);
							}
						} else if (Craft.blockHardness(blockType) == 0) {
							eggBlock.getRelative(BlockFace.WEST).setTypeId(0);
						} else if ((Craft.blockHardness(blockType) == 46) && (randomNum >= 0.5)) {

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld().spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(), eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
							tnt.setFuseTicks(fuseDelay);
							fuseDelay = fuseDelay + 2;
						}
					}
				}

				///// up down
				randomNum = Math.random();

				if (randomNum >= .2) {
					blockType = eggBlock.getRelative(BlockFace.UP).getTypeId();
					if ((blockType != 0) && (blockType != 8) && (blockType != 9)) {

						if (Craft.blockHardness(blockType) == 1) {
							if (randomNum >= .3) {
								eggBlock.getRelative(BlockFace.UP).setTypeId(0);
							}
						} else if (Craft.blockHardness(blockType) == 0) {
							eggBlock.getRelative(BlockFace.UP).setTypeId(0);
						} else if ((Craft.blockHardness(blockType) == 46) && (randomNum >= .5)) {

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld().spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(), eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
							tnt.setFuseTicks(fuseDelay);
							fuseDelay = fuseDelay + 2;
						}
					}

					blockType = eggBlock.getRelative(BlockFace.DOWN).getTypeId();
					if ((blockType != 0) && (blockType != 8) && (blockType != 9)) {

						if (Craft.blockHardness(blockType) == 1) {
							if (randomNum >= .3) {
								eggBlock.getRelative(BlockFace.DOWN).setTypeId(0);
							}
						} else if (Craft.blockHardness(blockType) == 0) {
							eggBlock.getRelative(BlockFace.DOWN).setTypeId(0);
						} else if ((Craft.blockHardness(blockType) == 46) && (randomNum >= .5)) {

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld().spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(), eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
							tnt.setFuseTicks(fuseDelay);
							fuseDelay = fuseDelay + 2;
						}
					}
				}

				event.getPlayer().getWorld().playEffect(egg.getLocation(), Effect.SMOKE, 0);
				// event.getPlayer().getWorld().playEffect(egg.getLocation(),
				// Effect.CLICK1, 0);
				CraftMover.playWeaponSound(egg.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.0f, 1.00f);

				Craft otherCraft = Craft.getOtherCraft(null, event.getPlayer(), egg.getLocation().getBlockX(), egg.getLocation().getBlockY(), egg.getLocation().getBlockZ());
				if (otherCraft != null) {
					CraftMover cm = new CraftMover(otherCraft, plugin);
					cm.structureUpdate(event.getPlayer(), false);
				} else {
					otherCraft = Craft.getOtherCraft(null, event.getPlayer(), egg.getLocation().getBlock().getRelative(2, 1, 2).getX(), egg.getLocation().getBlock().getRelative(2, 1, 2).getY(), egg.getLocation().getBlock().getRelative(2, 1, 2).getZ());
					if (otherCraft != null) {
						CraftMover cm = new CraftMover(otherCraft, plugin);
						cm.structureUpdate(event.getPlayer(), false);
					} else {
						otherCraft = Craft.getOtherCraft(null, event.getPlayer(), egg.getLocation().getBlock().getRelative(-2, -1, -2).getX(), egg.getLocation().getBlock().getRelative(-2, -1, -2).getY(), egg.getLocation().getBlock().getRelative(-2, -1, -2).getZ());
						if (otherCraft != null) {
							CraftMover cm = new CraftMover(otherCraft, plugin);
							cm.structureUpdate(event.getPlayer(), false);
						}
					}
				}

			}
			NavyCraft.explosiveEggsList.remove(egg);
			egg.remove();
		}
	}

	public boolean checkProtectedRegion(Player player, Location loc) {
		if ((player != null) && (loc != null)) {
			wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
			if (wgp != null) {
				if (!PermissionInterface.CheckEnabledWorld(loc)) {
					return true;
				}
				RegionManager regionManager = wgp.getRegionManager(player.getWorld());

				ApplicableRegionSet set = regionManager.getApplicableRegions(loc);

				Iterator<ProtectedRegion> it = set.iterator();
				while (it.hasNext()) {
					String id = it.next().getId();
					String[] splits = id.split("_");
					if (splits.length == 2) {
						if (splits[1].equalsIgnoreCase("safedock") || splits[1].equalsIgnoreCase("red") || splits[1].equalsIgnoreCase("blue")) {
							return true;
						}
					}

				}
				return false;
			}
			return false;
		}
		return true; // reach here in error, return true to protect property
	}

	public void endTunisia() {
		int blueTargetPoints = 0;
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-263, 63, 1066))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-274, 63, 1065))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-299, 63, 1076))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-316, 67, 1072))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-332, 67, 1072))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-371, 70, 1065))) {
			blueTargetPoints += 500;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-344, 63, 1101))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-344, 84, 1184))) {
			blueTargetPoints += 500;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-367, 77, 1178))) {
			blueTargetPoints += 200;
		}

		int redTargetPoints = 0;
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-611, 64, 1529))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-631, 64, 1539))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-642, 64, 1539))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-664, 68, 1537))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-677, 68, 1537))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-699, 71, 1489))) {
			redTargetPoints += 500;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-695, 64, 1522))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-609, 75, 1478))) {
			redTargetPoints += 500;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-633, 68, 1468))) {
			redTargetPoints += 200;
		}
		int playerNewExp = 0;
		if (redTargetPoints > 0) {
			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				playerNewExp = redTargetPoints;
				if ((p != null) && p.isOnline()) {
					if (NavyCraft.playerExp.containsKey(p.getName())) {
						playerNewExp = NavyCraft.playerExp.get(p.getName()) + playerNewExp;
						NavyCraft.playerExp.put(p.getName(), playerNewExp);
					} else {
						NavyCraft.playerExp.put(p.getName(), playerNewExp);
					}
					p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + playerNewExp + ChatColor.GRAY + " rank points.");
				}
				NavyCraft_FileListener.saveExperience(p.getName());
			}
		}

		if (blueTargetPoints > 0) {
			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				playerNewExp = blueTargetPoints;
				if ((p != null) && p.isOnline()) {
					if (NavyCraft.playerExp.containsKey(p.getName())) {
						playerNewExp = NavyCraft.playerExp.get(p.getName()) + playerNewExp;
						NavyCraft.playerExp.put(p.getName(), playerNewExp);
					} else {
						NavyCraft.playerExp.put(p.getName(), playerNewExp);
					}
					p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + playerNewExp + ChatColor.GRAY + " rank points.");
				}
				NavyCraft_FileListener.saveExperience(p.getName());
			}
		}

		NavyCraft.redPoints += redTargetPoints;
		NavyCraft.bluePoints += blueTargetPoints;

		String winStr = "";
		String scoreStr = "";
		String winLogStr = "";
		String scoreLogStr = "";
		if (NavyCraft.redPoints > NavyCraft.bluePoints) {
			winStr = ChatColor.RED + "" + ChatColor.BOLD + "Red Team Wins!!";
			scoreStr = "Final Team Scores: " + ChatColor.RED + NavyCraft.redPoints + " Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue";
			winLogStr = "Red Team Wins!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.redPoints + " Red  " + NavyCraft.bluePoints + " Blue";

		} else if (NavyCraft.redPoints < NavyCraft.bluePoints) {
			winStr = ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Team Wins!!";
			scoreStr = "Final Team Scores: " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue  " + ChatColor.RED + NavyCraft.redPoints + " Red";
			winLogStr = "Blue Team Wins!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.bluePoints + " Blue  " + NavyCraft.redPoints + " Red";

		} else {
			winStr = ChatColor.YELLOW + "" + ChatColor.BOLD + "Tie Game!!";
			scoreStr = "Final Team Scores: " + ChatColor.RED + NavyCraft.redPoints + " Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue";
			winLogStr = "Tie Game!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.redPoints + " Red  " + NavyCraft.bluePoints + " Blue";
		}

		plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "***OFFICIAL BATTLE ENDED!***");
		plugin.getServer().broadcastMessage(winStr);
		plugin.getServer().broadcastMessage(scoreStr);
		CraftMover.battleLogger("***OFFICIAL BATTLE ENDED!***");
		CraftMover.battleLogger(winLogStr);
		CraftMover.battleLogger(scoreLogStr);
	}

	public void scoreTarawa() {
		int blueTargetPoints = 0;
		int redTargetPoints = 0;

		// shore battiers
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(194, 66, -1180))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}

		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(179, 66, -1110))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(163, 66, -1064))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(125, 66, -1023))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(82, 66, -977))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(171, 66, -867))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}

		// buildings
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(186, 65, -1079))) {
			redTargetPoints += 300;
		} else {
			blueTargetPoints += 300;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(167, 66, -1086))) {
			redTargetPoints += 300;
		} else {
			blueTargetPoints += 300;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(197, 60, -1066))) {
			redTargetPoints += 500;
		} else {
			blueTargetPoints += 500;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(201, 66, -1156))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(201, 79, -1149))) {
			redTargetPoints += 150;
		} else {
			blueTargetPoints += 150;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(220, 79, -977))) {
			redTargetPoints += 150;
		} else {
			blueTargetPoints += 150;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(220, 66, -984))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}

		NavyCraft.redPoints = redTargetPoints;
		NavyCraft.bluePoints = blueTargetPoints;
	}

	public void endTarawa() {
		int blueTargetPoints = 0;
		int redTargetPoints = 0;

		// shore battiers
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(194, 66, -1180))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}

		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(179, 66, -1110))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(163, 66, -1064))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(125, 66, -1023))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(82, 66, -977))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(171, 66, -867))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}

		// buildings
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(186, 65, -1079))) {
			redTargetPoints += 300;
		} else {
			blueTargetPoints += 300;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(167, 66, -1086))) {
			redTargetPoints += 300;
		} else {
			blueTargetPoints += 300;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(197, 60, -1066))) {
			redTargetPoints += 500;
		} else {
			blueTargetPoints += 500;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(201, 66, -1156))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(201, 79, -1149))) {
			redTargetPoints += 150;
		} else {
			blueTargetPoints += 150;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(220, 79, -977))) {
			redTargetPoints += 150;
		} else {
			blueTargetPoints += 150;
		}
		if (checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(220, 66, -984))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}

		int playerNewExp = 0;
		if (redTargetPoints > 0) {
			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				playerNewExp = redTargetPoints;
				if ((p != null) && p.isOnline()) {
					if (NavyCraft.playerExp.containsKey(p.getName())) {
						playerNewExp = NavyCraft.playerExp.get(p.getName()) + playerNewExp;
						NavyCraft.playerExp.put(p.getName(), playerNewExp);
					} else {
						NavyCraft.playerExp.put(p.getName(), playerNewExp);
					}
					p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + playerNewExp + ChatColor.GRAY + " rank points.");
				}
			}
		}

		if (blueTargetPoints > 0) {
			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				playerNewExp = blueTargetPoints;
				if ((p != null) && p.isOnline()) {
					if (NavyCraft.playerExp.containsKey(p.getName())) {
						playerNewExp = NavyCraft.playerExp.get(p.getName()) + playerNewExp;
						NavyCraft.playerExp.put(p.getName(), playerNewExp);
					} else {
						NavyCraft.playerExp.put(p.getName(), playerNewExp);
					}
					p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + playerNewExp + ChatColor.GRAY + " rank points.");
				}
				NavyCraft_FileListener.saveExperience(p.getName());
			}
		}

		NavyCraft.redPoints = redTargetPoints;
		NavyCraft.bluePoints = blueTargetPoints;

		String winStr = "";
		String scoreStr = "";
		String winLogStr = "";
		String scoreLogStr = "";

		if (NavyCraft.redPoints > NavyCraft.bluePoints) {
			winStr = ChatColor.RED + "" + ChatColor.BOLD + "Red Team Wins!!";
			scoreStr = "Final Team Scores: " + ChatColor.RED + NavyCraft.redPoints + " Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue";
			winLogStr = "Red Team Wins!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.redPoints + " Red  " + NavyCraft.bluePoints + " Blue";

		} else if (NavyCraft.redPoints < NavyCraft.bluePoints) {
			winStr = ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Team Wins!!";
			scoreStr = "Final Team Scores: " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue  " + ChatColor.RED + NavyCraft.redPoints + " Red";
			winLogStr = "Blue Team Wins!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.bluePoints + " Blue  " + NavyCraft.redPoints + " Red";

		} else {
			winStr = ChatColor.YELLOW + "" + ChatColor.BOLD + "Tie Game!!";
			scoreStr = "Final Team Scores: " + ChatColor.RED + NavyCraft.redPoints + " Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue";
			winLogStr = "Tie Game!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.redPoints + " Red  " + NavyCraft.bluePoints + " Blue";
		}

		plugin.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "*** OFFICIAL BATTLE ENDED! ***");
		plugin.getServer().broadcastMessage(winStr);
		plugin.getServer().broadcastMessage(scoreStr);
		CraftMover.battleLogger("*** OFFICIAL BATTLE ENDED! ***");
		CraftMover.battleLogger(winLogStr);
		CraftMover.battleLogger(scoreLogStr);

	}

	public void endNorthSea() {
		String winStr = "";
		String scoreStr = "";
		String winLogStr = "";
		String scoreLogStr = "";

		if (NavyCraft.redPoints > NavyCraft.bluePoints) {
			winStr = ChatColor.RED + "" + ChatColor.BOLD + "Red Team Wins!!";
			scoreStr = "Final Team Scores: " + ChatColor.RED + NavyCraft.redPoints + " Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue";
			winLogStr = "Red Team Wins!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.redPoints + " Red  " + NavyCraft.bluePoints + " Blue";

		} else if (NavyCraft.redPoints < NavyCraft.bluePoints) {
			winStr = ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Team Wins!!";
			scoreStr = "Final Team Scores: " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue  " + ChatColor.RED + NavyCraft.redPoints + " Red";
			winLogStr = "Blue Team Wins!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.bluePoints + " Blue  " + NavyCraft.redPoints + " Red";

		} else {
			winStr = ChatColor.YELLOW + "" + ChatColor.BOLD + "Tie Game!!";
			scoreStr = "Final Team Scores: " + ChatColor.RED + NavyCraft.redPoints + " Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue";
			winLogStr = "Tie Game!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.redPoints + " Red  " + NavyCraft.bluePoints + " Blue";
		}

		plugin.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "*** OFFICIAL BATTLE ENDED! ***");
		plugin.getServer().broadcastMessage(winStr);
		plugin.getServer().broadcastMessage(scoreStr);
		CraftMover.battleLogger("***OFFICIAL BATTLE ENDED!***");
		CraftMover.battleLogger(winLogStr);
		CraftMover.battleLogger(scoreLogStr);

	}

	public boolean checkTunisia() {
		// red
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-263, 63, 1066))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-274, 63, 1065))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-299, 63, 1076))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-316, 67, 1072))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-332, 67, 1072))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-371, 70, 1065))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-344, 63, 1101))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-344, 84, 1184))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-367, 77, 1178))) {
			return false;
		}
		// blue
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-611, 64, 1529))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-631, 64, 1539))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-642, 64, 1539))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-664, 68, 1537))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-677, 68, 1537))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-699, 71, 1489))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-695, 64, 1522))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-609, 75, 1478))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(-633, 68, 1468))) {
			return false;
		}
		return true;
	}

	public boolean checkTarawa() {
		// shore battiers
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(194, 66, -1180))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(179, 66, -1110))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(163, 66, -1064))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(125, 66, -1023))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(82, 66, -977))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(171, 66, -867))) {
			return false;
		}

		// buildings
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(186, 65, -1079))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(167, 66, -1086))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(197, 60, -1066))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(201, 66, -1156))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(201, 79, -1149))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(220, 79, -977))) {
			return false;
		}
		if (!checkForTarget(plugin.getServer().getWorld("BattleWorld").getBlockAt(220, 66, -984))) {
			return false;
		}
		return true;
	}

	public static boolean checkForTarget(Block targetSignCheck) {
		if (targetSignCheck.getTypeId() == 68) {
			Sign sign = (Sign) targetSignCheck.getState();

			if ((sign.getLine(0) == null) || sign.getLine(0).trim().equals("")) {
				return false;
			}

			String signLine0 = sign.getLine(0).trim().toLowerCase();

			// remove colors
			signLine0 = signLine0.replaceAll(ChatColor.BLUE.toString(), "");

			// remove brackets
			if (signLine0.startsWith("[")) {
				signLine0 = signLine0.substring(1, signLine0.length() - 1);
			}

			if (signLine0.equalsIgnoreCase("Target")) {
				return true;
			}
		}
		return false;
	}

	public void battleTimerThread() {
		// final int taskNum;
		// int taskNum =
		// plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,
		// new Runnable(){
		timerThread = new Thread() {

			@Override
			public void run() {

				setPriority(Thread.MIN_PRIORITY);

				try {
					sleep(NavyCraft.battleLength);
					stopTimer();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}; // , 20L);
		timerThread.start();
	}

	public void stopTimer() {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			if (NavyCraft.battleMode > 0) {
				switch (NavyCraft.battleType) {
				case 1:
					endTunisia();
					break;
				case 2:
					endTarawa();
					break;
				case 3:
					endNorthSea();
					break;
				case 4:

					break;
				case 5:

					break;
				case 6:

					break;
				}
			} else {
				return;
			}

			NavyCraft.battleMode = -1;
			NavyCraft.battleType = -1;
			NavyCraft.battleLockTeams = false;
			NavyCraft.redPlayers.clear();
			NavyCraft.bluePlayers.clear();
			NavyCraft.anyPlayers.clear();
			NavyCraft.playerKits.clear();
			NavyCraft.redPoints = 0;
			NavyCraft.bluePoints = 0;
			NavyCraft.redMerchant = false;
			NavyCraft.blueMerchant = false;
		});
	}

	public void playerDisableThread(final Player player, final Craft craft) {

		Thread td = new Thread() {

			@Override
			public void run() {

				setPriority(Thread.MIN_PRIORITY);

				try {
					sleep(180000);
					playerDisableUpdate(player, craft);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}; // , 20L);
		td.start();
	}

	public void playerDisableUpdate(final Player player, final Craft craft) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			if ((craft != null) && (player != null) && player.isOnline()) {
				craft.doRemove = true;
				if (player.getInventory().contains(Material.GOLD_SWORD)) {
					player.getInventory().remove(Material.GOLD_SWORD);
				}
				player.sendMessage(ChatColor.GREEN + "Vehicle disabled.");
			}
		});
	}

	public void playerSinkThread(final Craft craft) {

		Thread td = new Thread() {

			@Override
			public void run() {

				setPriority(Thread.MIN_PRIORITY);

				try {
					sleep(180000);
					playerSinkUpdate(craft);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}; // , 20L);
		td.start();
	}

	public void playerSinkUpdate(final Craft craft) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			if (craft != null) {
				craft.sinking = true;
				CraftMover cm = new CraftMover(craft, plugin);
				cm.sinkingThread();
				for (String s : craft.crewNames) {
					Player p = plugin.getServer().getPlayer(s);
					if (p != null) {
						p.sendMessage(ChatColor.RED + "***We're sinking!***");
						p.sendMessage(ChatColor.RED + "***All Hands Abandon Ship!***");
					}
				}
			}
		});
	}

}
