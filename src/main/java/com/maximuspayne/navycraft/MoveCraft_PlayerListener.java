package com.maximuspayne.navycraft;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Egg;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.earth2me.essentials.Essentials;
import com.maximuspayne.navycraft.plugins.PermissionInterface;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.ess3.api.MaxMoneyException;
import net.minecraft.server.v1_10_R1.EntityPlayer;

public class MoveCraft_PlayerListener implements Listener {

	public Plugin plugin;
	public WorldGuardPlugin wgp;
	public WorldEditPlugin wep;

	Thread timerThread;

	public MoveCraft_PlayerListener(Plugin p) {
		plugin = p;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		Craft craft = Craft.getPlayerCraft(player);

		if (craft != null) {
			if (craft.isNameOnBoard.get(player.getName())) {
				Craft.reboardNames.put(player.getName(), craft);
				// MoveCraft_Timer.playerTimers.put(player,
				// new MoveCraft_Timer(plugin, 30, craft, player, "abandonCheck", false));
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

		if (Craft.reboardNames.containsKey(player.getName())) {
			if ((Craft.reboardNames.get(player.getName()) != null) && Craft.reboardNames.get(player.getName()).crewNames.contains(player.getName())) {
				Craft c = Craft.reboardNames.get(player.getName());
				Location loc = new Location(c.world, c.minX + (c.sizeX / 2), c.maxY, c.minZ + (c.sizeZ / 2));
				player.teleport(loc);

			}
			Craft.reboardNames.remove(player.getName());

		}

		if (!NavyCraft.playerPayDays.containsKey(player.getName()) || (NavyCraft.playerPayDays.containsKey(player.getName()) && (((System.currentTimeMillis() - NavyCraft.playerPayDays.get(player.getName())) / 1000) > 86400))) {
			Essentials ess;
			ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			if (ess == null) {
				player.sendMessage("Essentials Economy error");
				return;
			}

			int playerPay = 0;
			String groupName = "";
			Plugin groupPlugin = plugin.getServer().getPluginManager().getPlugin("GroupManager");
			if (groupPlugin != null) {
				if (!plugin.getServer().getPluginManager().isPluginEnabled(groupPlugin)) {
					plugin.getServer().getPluginManager().enablePlugin(groupPlugin);
				}
				GroupManager gm = (GroupManager) groupPlugin;
				WorldsHolder wd = gm.getWorldsHolder();
				groupName = wd.getWorldData("warworld1").getUser(player.getName()).getGroupName();

				if (groupName.equalsIgnoreCase("Default")) {
					playerPay = 1000;
				} else if (groupName.equalsIgnoreCase("LtJG")) {
					playerPay = 1300;
				} else if (groupName.equalsIgnoreCase("Lieutenant")) {
					playerPay = 1800;
				} else if (groupName.equalsIgnoreCase("Ltcm")) {
					playerPay = 2500;
				} else if (groupName.equalsIgnoreCase("Commander")) {
					playerPay = 3400;
				} else if (groupName.equalsIgnoreCase("Captain")) {
					playerPay = 4500;
				} else if (groupName.equalsIgnoreCase("RearAdmiral1") || groupName.equalsIgnoreCase("Trainer")) {
					playerPay = 5800;
				} else if (groupName.equalsIgnoreCase("RearAdmiral2") || groupName.equalsIgnoreCase("DockMaster") || groupName.equalsIgnoreCase("MilitaryPolice")) {
					playerPay = 7300;
				} else if (groupName.equalsIgnoreCase("ViceAdmiral") || groupName.equalsIgnoreCase("BattleMod")) {
					playerPay = 9000;
				} else if (groupName.equalsIgnoreCase("Admiral")) {
					playerPay = 10900;
				} else if (groupName.equalsIgnoreCase("FleetAdmiral")) {
					playerPay = 13000;
				} else if (groupName.equalsIgnoreCase("Admin")) {
					playerPay = 13000;
				} else if (groupName.equalsIgnoreCase("BattleMod")) {
					playerPay = 9000;
				} else if (groupName.equalsIgnoreCase("WW-Mod")) {
					playerPay = 7300;
				} else if (groupName.equalsIgnoreCase("Moderator")) {
					playerPay = 5800;
				} else if (groupName.equalsIgnoreCase("SVR-Mod")) {
					playerPay = 10900;
				}
			} else {
				player.sendMessage("Group manager error");
				return;
			}
			player.sendMessage(ChatColor.GREEN + "Pay day! Your pay rate is:" + ChatColor.WHITE + groupName.toUpperCase());
			try {
				ess.getUser(player).giveMoney(new BigDecimal(playerPay));
			} catch (MaxMoneyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			NavyCraft.playerPayDays.put(player.getName(), System.currentTimeMillis());
		}
	}

	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		if (event.getPlayer().getWorld().getName().equalsIgnoreCase("warworld2")) {
			if (Craft.playerClipboards.containsKey(event.getPlayer())) {
				Craft.playerClipboards.remove(event.getPlayer());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent event) {
		String deathMsg = event.getDeathMessage();

		if ((NavyCraft.battleMode > 0) && event.getEntity().getLocation().getWorld().getName().equalsIgnoreCase("warworld2")) {
			CraftMover.battleLogger(deathMsg);
		}

		String[] msgWords = deathMsg.split("\\s");
		if (msgWords.length == 5) {
			if (msgWords[1].equalsIgnoreCase("was") && msgWords[3].equalsIgnoreCase("by")) {
				Player p = plugin.getServer().getPlayer(msgWords[4]);
				if ((p != null) && p.getWorld().getName().equalsIgnoreCase("warworld1")) {
					int newExp = 100;
					plugin.getServer().broadcastMessage(ChatColor.GREEN + p.getName() + " receives " + ChatColor.YELLOW + newExp + ChatColor.GREEN + " rank points!");
					if (NavyCraft.playerScoresWW1.containsKey(p.getName())) {
						newExp = NavyCraft.playerScoresWW1.get(p.getName()) + newExp;
						NavyCraft.playerScoresWW1.put(p.getName(), newExp);
					} else {
						NavyCraft.playerScoresWW1.put(p.getName(), newExp);
					}
					p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + newExp + ChatColor.GRAY + " rank points.");
					CraftMover.checkRankWW1(p, newExp);
					NavyCraft.saveExperience();
				}

				if ((p != null) && p.getWorld().getName().equalsIgnoreCase("warworld2") && (NavyCraft.battleMode > 0)) {
					if ((!NavyCraft.redPlayers.contains(event.getEntity().getName()) && !NavyCraft.bluePlayers.contains(event.getEntity().getName())) || (!NavyCraft.redPlayers.contains(p.getName()) && !NavyCraft.bluePlayers.contains(p.getName())) || (NavyCraft.redPlayers.contains(p.getName()) && !NavyCraft.bluePlayers.contains(event.getEntity().getName())) || (NavyCraft.bluePlayers.contains(p.getName()) && !NavyCraft.redPlayers.contains(event.getEntity().getName()))) { return; }
					int newExp = 100;
					plugin.getServer().broadcastMessage(ChatColor.GREEN + p.getName() + " receives " + ChatColor.YELLOW + newExp + ChatColor.GREEN + " rank points!");

					if (NavyCraft.battleType == 1) {
						if (NavyCraft.redPlayers.contains(p.getName())) {
							NavyCraft.redPoints += newExp;
						} else {
							NavyCraft.bluePoints += newExp;
						}
					}

					if (NavyCraft.playerScoresWW2.containsKey(p.getName())) {
						newExp = NavyCraft.playerScoresWW2.get(p.getName()) + newExp;
						NavyCraft.playerScoresWW2.put(p.getName(), newExp);
					} else {
						NavyCraft.playerScoresWW2.put(p.getName(), newExp);
					}
					p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + newExp + ChatColor.GRAY + " rank points.");
					CraftMover.checkRankWW2(p, newExp);
					NavyCraft.saveExperience();
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

		if ((NavyCraft.checkSafeDockRegion(player.getLocation()) && !player.getWorld().getName().equalsIgnoreCase("warworld2")) || (!player.getWorld().getName().equalsIgnoreCase("warworld1") && !player.getWorld().getName().equalsIgnoreCase("warworld2"))) {
			if (NavyCraft.playerChatRegions.containsKey(player.getName())) {
				if (NavyCraft.playerChatRegions.get(player.getName()) != 0) {
					player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Joining [Global] channel...");
					NavyCraft.playerChatRegions.put(player.getName(), 0);
				}
			} else {
				player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Joining [Global] channel...");
				NavyCraft.playerChatRegions.put(player.getName(), 0);
			}
		} else {
			if (NavyCraft.playerChatRegions.containsKey(player.getName())) {
				if (NavyCraft.playerChatRegions.get(player.getName()) != 1) {
					player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Joining [Talk] channel...");
					NavyCraft.playerChatRegions.put(player.getName(), 1);
				}
			} else {
				player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Joining [Talk] channel...");
				NavyCraft.playerChatRegions.put(player.getName(), 1);
			}
		}

		if (NavyCraft.aaGunnersList.contains(player) && ((event.getFrom().getBlockX() != event.getTo().getBlockX()) || (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {
			NavyCraft.aaGunnersList.remove(player);
			if (player.getInventory().contains(Material.BLAZE_ROD)) {
				player.getInventory().remove(Material.BLAZE_ROD);
			}
			player.sendMessage(ChatColor.YELLOW + "You get off the AA-Gun.");

		} else if (craft != null) {
			// craft.setSpeed(1);

			if (craft.isMovingPlayers) { return; }

			Periscope playerScope = null;
			for (Periscope p : craft.periscopes) {
				if (p.user == player) {
					playerScope = p;
					break;
				}
			}

			if (!craft.isNameOnBoard.isEmpty() && craft.isNameOnBoard.containsKey(player.getName()) && craft.isNameOnBoard.get(player.getName()) && !craft.isOnCraft(player, false)) {
				if (craft.customName != null) {
					player.sendMessage(ChatColor.YELLOW + "You get off the " + craft.customName);
				} else {
					player.sendMessage(ChatColor.YELLOW + "You get off the " + craft.name + " class.");
					// player.sendMessage(ChatColor.GRAY + "Type /" + craft.name
					// + " remote for remote control");
					// player.sendMessage(ChatColor.YELLOW + "If you don't return, you'll lose control in 15 seconds.");
				}

				craft.isNameOnBoard.put(player.getName(), false);
				if (craft.driverName == player.getName()) {
					player.sendMessage(ChatColor.YELLOW + "You release the helm");
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
					// aft.abandonTimerThread(player, craft, false);
				} else if (player.getName() == craft.captainName) {
					craft.captainAbandoned = true;
					// craft.abandonTimerThread(player, craft, true);
				}

				/*
				 * int CraftReleaseDelay = 30; /*try { CraftReleaseDelay =
				 * Integer.parseInt(MoveCraft.instance.ConfigSetting("CraftReleaseDelay")); } catch
				 * (NumberFormatException ex) { System.out.println("ERROR with playermove. Could not parse " +
				 * MoveCraft.instance.ConfigSetting("CraftReleaseDelay")); }
				 */
				/*
				 * if(CraftReleaseDelay != 0) MoveCraft_Timer.playerTimers.put(player, new MoveCraft_Timer(plugin,
				 * CraftReleaseDelay, craft, player, "abandonCheck", false));
				 */
				// craft.timer = new MoveCraft_Timer(CraftReleaseDelay, craft, "abandonCheck", false);
			} else if (craft.isNameOnBoard.containsKey(player.getName()) && !craft.isNameOnBoard.get(player.getName()) && craft.isOnCraft(player, false)) {
				player.sendMessage(ChatColor.YELLOW + "Welcome on board");

				craft.isNameOnBoard.put(player.getName(), true);

				if (craft.abandoned) {
					craft.abandoned = false;
				}
				if (craft.captainAbandoned && player.getName().equalsIgnoreCase(craft.captainName)) {
					craft.captainAbandoned = false;
				}

				if (player.getName() == craft.driverName) {
					craft.haveControl = true;
					// if(craft.timer != null)
					// craft.timer.Destroy();
					// MoveCraft_Timer timer = MoveCraft_Timer.playerTimers.get(player);
					// if(timer != null)
					// timer.Destroy();
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

				/*
				 * if( craft.moveTicker > 0 ) return;
				 */
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
				player.sendMessage(ChatColor.YELLOW + "You release the helm.");
				CraftMover cm = new CraftMover(craft, plugin);
				// cm.structureUpdate(null);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();

		Craft playerCraft = Craft.getPlayerCraft(player);

		if (action == Action.RIGHT_CLICK_BLOCK) {

			if (event.hasBlock()) {
				Block block = event.getClickedBlock();

				NavyCraft.instance.DebugMessage("The action has a block " + block + " associated with it.", 4);

				if ((block.getTypeId() == 63) || (block.getTypeId() == 68)) {
					MoveCraft_BlockListener.ClickedASign(player, block, false);
					return;
				}

				if ((block.getTypeId() == 69) && (block.getRelative(BlockFace.DOWN, 1).getTypeId() == 68)) {
					Craft testCraft = Craft.getCraft(block.getX(), block.getY(), block.getZ());
					if (testCraft != null) {
						Sign sign = (Sign) block.getRelative(BlockFace.DOWN, 1).getState();

						if ((sign.getLine(0) == null) || sign.getLine(0).trim().equals("")) { return; }

						String craftTypeName = sign.getLine(0).trim().toLowerCase();

						// remove colors
						craftTypeName = craftTypeName.replaceAll(ChatColor.BLUE.toString(), "");

						// remove brackets
						if (craftTypeName.startsWith("[")) {
							craftTypeName = craftTypeName.substring(1, craftTypeName.length() - 1);
						}

						if (craftTypeName.equalsIgnoreCase("periscope")) {
							BlockFace bf = BlockFace.NORTH;

							switch (block.getRelative(BlockFace.DOWN, 1).getData()) {
								case (byte) 0x2:// n
									bf = BlockFace.EAST;
									break;
								case (byte) 0x3:// s
									bf = BlockFace.WEST;
									break;
								case (byte) 0x4:// w
									bf = BlockFace.SOUTH;
									break;
								case (byte) 0x5:// e
									bf = BlockFace.NORTH;
									break;
							}
							/*
							 * if( testCraft.moveTicker > 0 ) { event.setCancelled(true); return; }
							 */

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

				if ((block.getTypeId() == 54) || (block.getTypeId() == 23) || (block.getTypeId() == 61)) {
					// Need to handle workbench as well...

					return;
				}

				if ((NavyCraft.instance.ConfigSetting("RequireRemote") == "true") && (playerCraft != null)) {
					playerCraft.addBlock(block, false);
				}

			}

			if ((playerCraft != null) && (playerCraft.driverName == player.getName())) {
				if ((NavyCraft.instance.ConfigSetting("RequireRemote") == "true") && (event.getItem().getTypeId() != playerCraft.type.remoteControllerItem)) { return; }

				playerUsedAnItem(player, playerCraft);
			} else {
				Vector pVel = player.getVelocity();
				if ((player.getLocation().getPitch() < 90) || (player.getLocation().getPitch() > 180)) {
					pVel.setX(pVel.getX() + 1);
				} else {
					pVel.setY(pVel.getY() + 1);
				}
			}
		}

		if ((action == Action.LEFT_CLICK_BLOCK) && event.hasBlock()) {
			Block block = event.getClickedBlock();
			if ((block.getTypeId() == 63) || (block.getTypeId() == 68)) {
				MoveCraft_BlockListener.ClickedASign(player, block, true);
				return;
			}
		}

		// fire airplane gun
		if ((action == Action.LEFT_CLICK_AIR) && (player.getItemInHand().getType() == Material.GOLD_SWORD)) {
			Craft testCraft = Craft.getPlayerCraft(event.getPlayer());
			if ((testCraft != null) && (testCraft.driverName == player.getName()) && testCraft.type.canFly && !testCraft.sinking && !testCraft.helmDestroyed) {
				Egg newEgg = player.launchProjectile(Egg.class);

				// player.sendMessage(newEgg.getLocation().toString());
				// newEgg.teleport(newEgg.getLocation().add(newEgg.getVelocity().multiply(5)));
				// player.sendMessage(newEgg.getLocation().toString());

				newEgg.setVelocity(newEgg.getVelocity().multiply(2.0f));
				NavyCraft.explosiveEggsList.add(newEgg);
				event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
				event.getPlayer().getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5.0f, 1.70f);
				// event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.CLICK2, 0);
			}

		}

		// AA Gunner...
		if ((action == Action.LEFT_CLICK_AIR) && NavyCraft.aaGunnersList.contains(player) && (player.getItemInHand().getType() == Material.BLAZE_ROD)) {
			Egg newEgg = player.launchProjectile(Egg.class);
			newEgg.setVelocity(newEgg.getVelocity().multiply(1.5f));
			NavyCraft.explosiveEggsList.add(newEgg);
			event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
			// event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.CLICK2, 0);
			event.getPlayer().getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5.0f, 1.70f);

			//// else check for movement clicking
		} else if ((action == Action.RIGHT_CLICK_AIR) && (playerCraft != null) && (playerCraft.driverName == player.getName()) && (playerCraft.type.listenItem == true)) {
			if ((NavyCraft.instance.ConfigSetting("RequireRemote") == "true") && (event.getItem().getTypeId() != playerCraft.type.remoteControllerItem)) { return; }

			playerUsedAnItem(player, playerCraft);
		}

		/*
		 * if(action == Action.RIGHT_CLICK_AIR && playerCraft == null && MoveCraft.instance.DebugMode) { Vector pVel =
		 * player.getVelocity(); int dx = 3; int dy = 0; int dz = 0; pVel = pVel.add(new Vector(dx, dy, dz));
		 * player.setVelocity(pVel); }
		 */
	}

	public void playerUsedAnItem(Player player, Craft craft) {

		// minimum time between 2 swings
		if ((System.currentTimeMillis() - craft.lastMove) < (1.0 * 1000)) { return; }

		if (craft.blockCount <= 0) {
			craft.releaseCraft();
			return;
		}

		ItemStack pItem = player.getItemInHand();
		int item = pItem.getTypeId();

		// MoveCraft.instance.DebugMessage(player.getName() + " used item " + Integer.toString(item));

		// the craft won't budge if you have any tool in the hand
		if (!craft.haveControl) {
			if (((item == craft.type.remoteControllerItem) || (item == Integer.parseInt(NavyCraft.instance.ConfigSetting("UniversalRemoteId")))) && !craft.isOnCraft(player, true) && PermissionInterface.CheckPermission(player, "remote")) {
				if (craft.haveControl) {
					player.sendMessage(ChatColor.YELLOW + "You switch off the remote controller");
				} else {
					MoveCraft_Timer timer = MoveCraft_Timer.playerTimers.get(player);
					if (timer != null) {
						timer.Destroy();
					}
					player.sendMessage(ChatColor.YELLOW + "You switch on the remote controller");
				}
				craft.haveControl = !craft.haveControl;
			} else {
				return;
				/////////////// *******************//////////////////////////////////////////////////
			}
		} else if (item == Material.GOLD_SWORD.getId()) {
			// cruise movmeent
			if (craft.type.doesCruise && craft.autoTurn) {
				float rotation = ((float) Math.PI * player.getLocation().getYaw()) / 180f;

				// Not really sure what the N stands for...
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
							player.sendMessage("Up Elevator");
						} else if (craft.vertPlanes == -1) {
							craft.vertPlanes = 0;
							player.sendMessage("Neutral Elevator");
						} else {
							player.sendMessage("Elevator already up");
						}
						return;
					} else if (dy == -1) {
						if (craft.vertPlanes == 0) {
							craft.vertPlanes = -1;
							player.sendMessage("Down Elevator");
						} else if (craft.vertPlanes == 1) {
							craft.vertPlanes = 0;
							player.sendMessage("Neutral Elevator");
						} else {
							player.sendMessage("Elevator already down");
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
							player.sendMessage("Set engines to dive first.");
							return;
						}
					}

					if (dy == 1) {
						if (craft.vertPlanes == 0) {
							craft.vertPlanes = 1;
							player.sendMessage("Diving Planes Up Bubble");
						} else if (craft.vertPlanes == -1) {
							craft.vertPlanes = 0;
							player.sendMessage("Diving Planes Neutral");
						} else {
							player.sendMessage("Diving Planes already up");
						}
						return;
					} else if (dy == -1) {
						if (craft.vertPlanes == 0) {
							craft.vertPlanes = -1;
							player.sendMessage("Diving Planes Down Bubble");
						} else if (craft.vertPlanes == 1) {
							craft.vertPlanes = 0;
							player.sendMessage("Diving Planes Neutral");
						} else {
							player.sendMessage("Diving Planes already down");
						}
						return;
					}
				}

				////////////// turning

				//// north
				if ((craft.rotation % 360) == 0) {
					if (nx > 0.866) {
						if (craft.rudder == 0) {
							player.sendMessage("Turning to heading 090");
						}
						craft.rudderChange(player, 1, true);
						return;
					} else if (nx < -0.866) {
						if (craft.rudder == 0) {
							player.sendMessage("Turning to heading 270");
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

					////// south
				} else if (craft.rotation == 180) {

					if (nx > 0.866) {
						if (craft.rudder == 0) {
							player.sendMessage("Turning to heading 090");
						}
						craft.rudderChange(player, -1, true);
						return;
					} else if (nx < -0.866) {
						if (craft.rudder == 0) {
							player.sendMessage("Turning to heading 270");
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

					////// east
				} else if (craft.rotation == 90) {

					if (nz > 0.866) {
						if (craft.rudder == 0) {
							player.sendMessage("Turning to heading 180");
						}
						craft.rudderChange(player, 1, true);
						return;
					} else if (nz < -0.866) {
						if (craft.rudder == 0) {
							player.sendMessage("Turning to heading 000");
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
					////////////// west
				} else if (craft.rotation == 270) {
					if (nz > 0.866) {
						if (craft.rudder == 0) {
							player.sendMessage("Turning to heading 180");
						}
						craft.rudderChange(player, -1, true);
						return;
					} else if (nz < -0.866) {
						if (craft.rudder == 0) {
							player.sendMessage("Turning to heading 000");
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

				// Not really sure what the N stands for...
				float nx = -(float) Math.sin(rotation);
				float nz = (float) Math.cos(rotation);

				int dx = (Math.abs(nx) >= 0.5 ? 1 : 0) * (int) Math.signum(nx);
				int dz = (Math.abs(nz) > 0.5 ? 1 : 0) * (int) Math.signum(nz);
				int dy = 0;

				/*
				 * if(dx != 0) rotation = dx * 90; else rotation = dz * 180;
				 *
				 * rotation = player.getLocation().getYaw(); if(rotation < 0) rotation = 360 + rotation; if(rotation >
				 * 45 && rotation < 135) rotation = 90; else if(rotation > 135 && rotation < 225) rotation = 180; else
				 * if (rotation > 225 && rotation < 315) rotation = 270; else rotation = 0;
				 */

				// player.sendMessage("Craft rotation is " + craft.rotation + ". Player rotation is " + rotation +
				// "Difference is " + (rotation - craft.rotation));

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

				/*
				 * int dr = (int)rotation - craft.rotation; if(dr < 0) dr = 360 + dr; if(dr > 360) dr = dr - 360;
				 */

				/*
				 * if(dr != 0 && dy == 0) { CraftRotator cr = new CraftRotator(craft); cr.turn(dr); } else
				 */

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

			if ((player.getItemInHand().getType() == Material.FLINT_AND_STEEL) && NavyCraft.cleanupPlayers.contains(player.getName()) && (player.getWorld().getName().equalsIgnoreCase("warworld1") || player.getWorld().getName().equalsIgnoreCase("warworld2"))) {
				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);
				transp.add(Material.STATIONARY_WATER);
				transp.add(Material.WATER);
				Block block = player.getTargetBlock(transp, 300);
				if (block != null) {
					Craft c = Craft.getCraft(block.getX(), block.getY(), block.getZ());
					if (c != null) {
						if (!((c.captainName != null) && (plugin.getServer().getPlayer(c.captainName) != null) && plugin.getServer().getPlayer(c.captainName).isOnline())) {
							if (craft != null)// && playerCraft.type == craftType) {
							{
								craft.leaveCrew(player);
							}

							c.buildCrew(player, false);

							// CraftMover cm = new CraftMover(c, plugin);
							// cm.structureUpdate(null);
							System.out.println("Vehicle destroyed by:" + player.getName() + " Name:" + c.name + " X:" + c.getLocation().getBlockX() + " Y:" + c.getLocation().getBlockY() + " Z:" + c.getLocation().getBlockZ());
							c.doDestroy = true;
							player.sendMessage("Vehicle destroyed.");
						} else {
							player.sendMessage("Vehicle's captain is online.");
						}
					} else {
						block.getRelative(BlockFace.UP, 1).setTypeId(63);
						Sign sign = (Sign) block.getRelative(BlockFace.UP, 1).getState();
						sign.setLine(0, "Ship");
						sign.update();

						Craft theCraft = NavyCraft.instance.createCraft(player, CraftType.getCraftType("ship"), sign.getX(), sign.getY(), sign.getZ(), "ship", 0, block.getRelative(BlockFace.UP, 1), false);
						if (theCraft != null) {
							if (!NavyCraft.checkNoDriveRegion(theCraft.getLocation())) {
								System.out.println("Vehicle destroyed by:" + player.getName() + " Name:" + theCraft.name + " X:" + theCraft.getLocation().getBlockX() + " Y:" + theCraft.getLocation().getBlockY() + " Z:" + theCraft.getLocation().getBlockZ());
								theCraft.doDestroy = true;
								player.sendMessage("Vehicle destroyed.");
							} else {
								player.sendMessage(ChatColor.RED + player.getName() + ", why are you trying to destroy a dock vehicle??");
								System.out.println(player.getName() + ", why are you trying to destroy a dock vehicle??");
							}
						} else {
							sign.setLine(0, "Aircraft");
							sign.update();
							theCraft = NavyCraft.instance.createCraft(player, CraftType.getCraftType("aircraft"), sign.getX(), sign.getY(), sign.getZ(), "aircraft", 0, block.getRelative(BlockFace.UP, 1), false);

							if (theCraft != null) {
								if (!NavyCraft.checkNoDriveRegion(theCraft.getLocation())) {
									System.out.println("Vehicle destroyed by:" + player.getName() + " Name:" + theCraft.name + " X:" + theCraft.getLocation().getBlockX() + " Y:" + theCraft.getLocation().getBlockY() + " Z:" + theCraft.getLocation().getBlockZ());
									theCraft.doDestroy = true;
									player.sendMessage("Vehicle destroyed.");
								} else {
									player.sendMessage(ChatColor.RED + player.getName() + ", why are you trying to destroy a dock vehicle??");
									System.out.println(player.getName() + ", why are you trying to destroy a dock vehicle??");
								}
							} else {
								player.sendMessage("No vehicle could be detected.");
								block.getRelative(BlockFace.UP, 1).setTypeId(0);
							}
						}
					}
				} else {
					player.sendMessage("No block detected");
				}
				return;
			}
			if ((player.getItemInHand().getType() == Material.SHEARS) && NavyCraft.cleanupPlayers.contains(player.getName()) && (player.getWorld().getName().equalsIgnoreCase("warworld1") || player.getWorld().getName().equalsIgnoreCase("warworld2")) && !NavyCraft.checkSafeDockRegion(player.getLocation())) {
				HashSet<Byte> hs = new HashSet<>();
				hs.add((byte) 0x0);
				Block block = player.getTargetBlock(hs, 200);
				if (block != null) {
					System.out.println("Shears used:" + player.getName() + " X:" + block.getX() + " Y:" + block.getY() + " Z:" + block.getZ());
					player.sendMessage("Shears used!");
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
					player.sendMessage("No block detected");
				}
				return;
			}
			if ((player.getItemInHand().getType() == Material.GOLD_SPADE) && NavyCraft.cleanupPlayers.contains(player.getName()) && (player.getWorld().getName().equalsIgnoreCase("warworld1") || player.getWorld().getName().equalsIgnoreCase("warworld2")) && !NavyCraft.checkSafeDockRegion(player.getLocation())) {
				HashSet<Byte> hs = new HashSet<>();
				hs.add((byte) 0x0);
				hs.add((byte) 0x8);
				hs.add((byte) 0x9);
				Block block = player.getTargetBlock(hs, 200);

				if (block != null) {
					System.out.println("Golden Shovel used:" + player.getName() + " X:" + block.getX() + " Y:" + block.getY() + " Z:" + block.getZ());
					player.sendMessage("Golden Shovel used!");
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

		if (NavyCraft.battleMode > 0) {
			Player player = event.getPlayer();
			if (NavyCraft.redPlayers.contains(player.getName()) && (NavyCraft.redSpawn != null)) {
				event.setRespawnLocation(NavyCraft.redSpawn);
			} else if (NavyCraft.bluePlayers.contains(player.getName()) && (NavyCraft.blueSpawn != null)) {
				event.setRespawnLocation(NavyCraft.blueSpawn);
			}
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		// public void onPlayerCommand(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String[] split = event.getMessage().split(" ");
		split[0] = split[0].substring(1);

		// debug commands
		if (NavyCraft.instance.DebugMode == true) {
			if (split[0].equalsIgnoreCase("isDataBlock")) {
				player.sendMessage(Boolean.toString(BlocksInfo.isDataBlock(Integer.parseInt(split[1]))));
			} else if (split[0].equalsIgnoreCase("isComplexBlock")) {
				player.sendMessage(Boolean.toString(BlocksInfo.isComplexBlock(Integer.parseInt(split[1]))));
				/*
				 * } else if (split[0].equalsIgnoreCase("findcenter")) { Craft craft = Craft.getCraft(player); Location
				 * blockLoc = new Location(player.getWorld(), craft.posX + craft.offX, craft.posY, craft.posZ +
				 * craft.offZ); Block mcBlock = player.getWorld().getBlockAt(blockLoc);
				 * mcBlock.setType(Material.GOLD_BLOCK);
				 */
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
				// MoveCraft.instance.DebugMessage("Craft position: " + craft.posX + ", " + craft.posY + ", " +
				// craft.posZ, 4);
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

		if (split[0].equalsIgnoreCase("movecraft") || split[0].equalsIgnoreCase("navycraft") || split[0].equalsIgnoreCase("nc")) {
			if (!PermissionInterface.CheckPermission(player, "movecraft." + event.getMessage().substring(1))) { return; }

			if (split.length >= 2) {
				if (split[1].equalsIgnoreCase("types")) {

					for (CraftType craftType : CraftType.craftTypes) {
						if (craftType.canUse(player)) {
							player.sendMessage(ChatColor.GREEN + craftType.name + ChatColor.YELLOW + craftType.minBlocks + "-" + craftType.maxBlocks + " blocks" + " speed : " + craftType.maxSpeed);
						}
					}
				} else if (split[1].equalsIgnoreCase("list")) {
					// list all craft currently controlled by a player

					if (Craft.craftList.isEmpty()) {
						player.sendMessage(ChatColor.YELLOW + "no player controlled craft");
						// return true;
					}

					for (Craft craft : Craft.craftList) {

						player.sendMessage(ChatColor.YELLOW + craft.name + " commanded by " + craft.captainName + " : " + craft.blockCount + " blocks");
					}
				} else if (split[1].equalsIgnoreCase("reload")) {
					NavyCraft.instance.loadProperties();
					player.sendMessage(ChatColor.YELLOW + "MoveCraft configuration reloaded");
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("debug")) {
					NavyCraft.instance.ToggleDebug();
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("loglevel")) {
					try {
						Integer.parseInt(split[2]);
						NavyCraft.instance.configFile.ConfigSettings.put("LogLevel", split[2]);
					} catch (Exception ex) {
						player.sendMessage("Invalid loglevel.");
					}
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("config")) {
					NavyCraft.instance.configFile.ListSettings(player);
					return;
				} else if (split[1].equalsIgnoreCase("spawntimer")) {

					int timerMin = -1;
					if (split.length == 3) {
						try {
							timerMin = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage("Invalid timer value");
							e.printStackTrace();
						}
					}
					if ((timerMin >= 1) || (timerMin <= 60)) {

						NavyCraft.spawnTime = timerMin;
						player.sendMessage("Spawn time set to " + timerMin + " minutes.");
					} else {
						player.sendMessage("Invalid timer value..between 1 to 60 minutes");
					}

					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("cleanup")) {
					if (NavyCraft.cleanupPlayers.contains(player.getName())) {
						NavyCraft.cleanupPlayers.remove(player.getName());
						player.sendMessage("Exiting cleanup mode.");
					} else {
						/*
						 * Essentials ess; ess = (Essentials)
						 * plugin.getServer().getPluginManager().getPlugin("Essentials"); if( ess == null ) {
						 * player.sendMessage("Essentials Economy error"); return; }
						 *
						 * Plugin groupPlugin = plugin.getServer().getPluginManager().getPlugin("GroupManager"); if
						 * (groupPlugin != null) { if
						 * (!plugin.getServer().getPluginManager().isPluginEnabled(groupPlugin)) {
						 * plugin.getServer().getPluginManager().enablePlugin(groupPlugin); } GroupManager gm =
						 * (GroupManager) groupPlugin; WorldsHolder wd = gm.getWorldsHolder(); Group g = new
						 * Group("Cleanup");
						 * wd.getWorldData("warworld1").getUser(player.getName()).addPermission("worldedit.superpickaxe"
						 * ); wd.getWorldData("warworld1").getUser(player.getName()).addPermission(
						 * "worldedit.superpickaxe.area"); }
						 */

						NavyCraft.cleanupPlayers.add(player.getName());
						player.sendMessage("Entering cleanup mode.");
					}
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("hidden")) {
					if (NavyCraft.disableHiddenChats.contains(player.getName())) {
						NavyCraft.disableHiddenChats.remove(player.getName());
						player.sendMessage("You will now see hidden chat messages.");
					} else {
						NavyCraft.disableHiddenChats.add(player.getName());

						player.sendMessage("You will no longer view hidden chat messages.");
					}
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("listShips") || split[1].equalsIgnoreCase("ls")) {
					for (Craft c : Craft.craftList) {
						if (c.isAutoCraft) {
							if (c.customName != null) {
								player.sendMessage(c.craftID + "-" + c.customName + " Route=" + c.routeID + " Stage=" + c.routeStage);
							} else {
								player.sendMessage(c.craftID + "-" + c.name + " Route=" + c.routeID + " Stage=" + c.routeStage);
							}
						} else {
							if (c.customName != null) {
								player.sendMessage(c.craftID + "-" + c.customName);
							} else {
								player.sendMessage(c.craftID + "-" + c.name);
							}
						}
					}
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("destroyShips")) {
					for (Craft c : Craft.craftList) {
						c.doDestroy = true;
					}
					player.sendMessage("All vehicles destroyed");
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("removeships")) {
					for (Craft c : Craft.craftList) {
						c.doRemove = true;
					}
					player.sendMessage("All vehicles removed");
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("destroyauto")) {
					ArrayList<Craft> craftCheckList = new ArrayList<>();
					for (Craft c : Craft.craftList) {
						craftCheckList.add(c);
					}
					int count = 0;
					for (Craft c : craftCheckList) {
						if (c.isAutoCraft) {
							c.doDestroy = true;
							count++;
						}
					}
					craftCheckList.clear();

					player.sendMessage("All auto vehicles destroyed-" + count);
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("destroystuck")) {
					ArrayList<Craft> craftCheckList = new ArrayList<>();
					for (Craft c : Craft.craftList) {
						craftCheckList.add(c);
					}
					int count = 0;
					for (Craft c : craftCheckList) {
						if (c.isAutoCraft && ((c.speed == 0) || (c.speed == 1))) {
							c.doDestroy = true;
							count++;
						}
					}
					craftCheckList.clear();

					player.sendMessage("All stopped auto vehicles destroyed-" + count);
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("tpShip") || split[1].equalsIgnoreCase("tp")) {

					int shipNum = -1;
					if (split.length == 3) {
						try {
							shipNum = Integer.parseInt(split[2]);
						} catch (NumberFormatException e) {
							player.sendMessage("Invalid id number");
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
						player.sendMessage("ID Number not found");
					} else {
						player.sendMessage("Invalid id number");
					}

					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("loadShips")) {
					for (int x = -1800; x <= 2000; x++) {
						for (int y = 30; y <= 128; y++) {
							for (int z = -1100; z <= 1700; z++) {
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
										player.sendMessage("x=" + x + " y=" + y + " z=" + z);
										Craft theCraft = NavyCraft.instance.createCraft(player, craftType, shipx, shipy, shipz, name, dr, shipSignBlock, true);
										if (theCraft != null) {
											if (name != null) {
												player.sendMessage(name + " activated!");
											} else {
												player.sendMessage(signLine0 + " activated!");
											}
											CraftMover cm = new CraftMover(theCraft, plugin);
											cm.structureUpdate(null, false);
										} else {
											player.getWorld().getBlockAt(x, y, z).setTypeId(0);
										}
									}
								}
							}
						}
					}
					player.sendMessage("All vehicles loaded in ocean area");
					event.setCancelled(true);
					return;
				}
			} else {
				player.sendMessage(ChatColor.WHITE + "NavyCraft v" + NavyCraft.version + " commands :");
				player.sendMessage(ChatColor.YELLOW + "/navycraft types " + " : " + ChatColor.WHITE + "list the types of craft available");
				player.sendMessage(ChatColor.YELLOW + "/navycraft list : " + ChatColor.WHITE + "list the current player controled craft");
				player.sendMessage(ChatColor.YELLOW + "/navycraft reload : " + ChatColor.WHITE + "reload config files");
				player.sendMessage(ChatColor.YELLOW + "/[craft type] " + " : " + ChatColor.WHITE + "commands specific to the craft type");
			}
			event.setCancelled(true);
		} else if (split[0].equalsIgnoreCase("release")) {
			// MoveCraft.instance.releaseCraft(player, Craft.getPlayerCraft(player));
			Craft c = Craft.getPlayerCraft(player);
			if ((c != null) && (c.captainName == player.getName())) {
				c.releaseHelm();
				c.releaseCraft();
			}
			event.setCancelled(true);
		} else if (split[0].equalsIgnoreCase("remote")) {
			player.sendMessage("0");
			Craft craft = Craft.getPlayerCraft(player);
			if ((craft != null) && (craft.driverName == player.getName())) {
				split[0] = craft.type.name;
				split[1] = "remote";

				if (!PermissionInterface.CheckPermission(player, "movecraft." + event.getMessage().substring(1))) {
					event.setCancelled(true);
					return;
				}
				player.sendMessage("1");
				if (processCommand(craft.type, player, split) == true) {
					event.setCancelled(true);
				}
			} else {
				player.sendMessage("You have no craft to remote :( Hurry and get one before they're sold out!");
			}
		} else {

			String craftName = split[0];

			CraftType craftType = CraftType.getCraftType(craftName);
			//// CREW chat
			if (craftName.equalsIgnoreCase("crew")) {
				Craft craft = Craft.getPlayerCraft(player);
				if (craft == null) {
					player.sendMessage("You are not on a crew!");
					event.setCancelled(true);
					return;
				}

				if (split.length == 1) {
					player.sendMessage("Your " + craft.name + " crew...");
					if (craft.captainName != null) {
						player.sendMessage("Captain - " + craft.captainName);
					}
					for (String s : craft.crewNames) {
						if (s != craft.captainName) {
							player.sendMessage(s);
						}
					}
				} else {
					String msgString;
					msgString = "> ";
					for (int i = 1; i < split.length; i++) {
						msgString += split[i] + " ";
					}

					for (String s : craft.crewNames) {
						Player p = plugin.getServer().getPlayer(s);
						if (p != null) {
							if (player.getName() == craft.captainName) {
								p.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Captain] <" + player.getName() + msgString);

							} else {
								p.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "[Crew] <" + player.getName() + msgString);

							}
						}
					}

					if (player.getName() == craft.captainName) {
						System.out.println("[Captain] <" + player.getName() + msgString);
					} else {
						System.out.println("[Crew] <" + player.getName() + msgString);
					}
				}
				event.setCancelled(true);
				return;
			} else if (craftName.equalsIgnoreCase("radio") || craftName.equalsIgnoreCase("ra")) {
				Craft craft = Craft.getPlayerCraft(player);
				if (craft == null) {
					player.sendMessage("You are not on a crew!");
					event.setCancelled(true);
					return;
				}

				if (split.length == 1) {
					if ((craft.radioSignLoc != null) && (craft.maxY >= 63) && craft.radioSetOn) {
						player.sendMessage("Your radio is Active on frequency-" + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + "" + craft.radio4);

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
						player.sendMessage("There are " + craftCount + " vehicles on your frequency.");

					} else if ((craft.radioSignLoc != null) && craft.radioSetOn) {
						player.sendMessage("Your radio is disabled because you are underwater...");
					} else if (craft.radioSignLoc != null) {
						player.sendMessage("Your radio is turned off.");
					} else {
						player.sendMessage("No radio detected...");
					}
				} else {
					if (craft.radioSignLoc == null) {
						player.sendMessage("No radio detected...");
						event.setCancelled(true);
						return;
					}

					if (!craft.radioSetOn) {
						player.sendMessage("Your radio is turned off.");
						event.setCancelled(true);
						return;
					}

					if (craft.maxY < 63) {
						player.sendMessage("Your radio will not work underwater.");
						event.setCancelled(true);
						return;
					}

					if ((craft.radio1 == 0) && (craft.radio2 == 0) && (craft.radio3 == 0) && (craft.radio4 == 0)) {
						player.sendMessage("0000 is invalid frequency, use Radio sign to change.");
						event.setCancelled(true);
						return;
					}

					String msgString;
					msgString = "> ";
					for (int i = 1; i < split.length; i++) {
						msgString += split[i] + " ";
					}

					for (String s : craft.crewNames) {
						Player p = plugin.getServer().getPlayer(s);
						if (p != null) {
							if (craft.customName != null) {
								p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "[Radio-" + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + "" + craft.radio4 + "] <" + craft.customName.toUpperCase() + "><" + player.getName() + msgString);
							} else {
								p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "[Radio-" + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + "" + craft.radio4 + "] <" + craft.name.toUpperCase() + "><" + player.getName() + msgString);
							}
						}
					}

					for (Craft c : Craft.craftList) {
						if ((c != craft) && c.radioSetOn) {
							if (c.radio1 == craft.radio1) {
								if (c.radio2 == craft.radio2) {
									if (c.radio3 == craft.radio3) {
										if (c.radio4 == craft.radio4) {
											if ((c.world == craft.world) && (c.getLocation().distance(craft.getLocation()) < 2000)) {
												for (String s : c.crewNames) {
													Player p = plugin.getServer().getPlayer(s);
													if (p != null) {
														if (craft.customName != null) {
															p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "[Radio-" + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + "" + craft.radio4 + "] <" + craft.customName.toUpperCase() + "><" + player.getName() + msgString);
														} else {
															p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "[Radio-" + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + "" + craft.radio4 + "] <" + craft.name.toUpperCase() + "><" + player.getName() + msgString);
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
						System.out.println("[Radio-" + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + "" + craft.radio4 + "]<" + craft.customName + "><" + player.getName() + msgString);
					} else {
						System.out.println("[Radio-" + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + "" + craft.radio4 + "]<" + craft.name + "><" + player.getName() + msgString);
					}

					craft.lastRadioPulse = System.currentTimeMillis();

				}
				event.setCancelled(true);
				return;
			} else if (craftName.equalsIgnoreCase("shipyard")) {
				if (split.length > 1) {
					if (split[1].equalsIgnoreCase("reward")) {
						if (!PermissionInterface.CheckPermission(player, "movecraft.reward") && !player.isOp()) {
							player.sendMessage(ChatColor.RED + "You do not have permission to reward plots.");
							event.setCancelled(true);
							return;
						}

						if (split.length < 5) {
							player.sendMessage("Usage - /shipyard reward <player> <type> <reason>");
							player.sendMessage("Example - /shipyard reward Maximuspayne SHIP1 Donation");
							event.setCancelled(true);
							return;
						}

						String reasonString;
						reasonString = "";
						for (int i = 4; i < split.length; i++) {
							reasonString += split[i] + " ";
						}

						String typeString = split[3];
						if (!typeString.equalsIgnoreCase("DD") && !typeString.equalsIgnoreCase("SHIP1") && !typeString.equalsIgnoreCase("SUB1") && !typeString.equalsIgnoreCase("SHIP2") && !typeString.equalsIgnoreCase("SUB2") && !typeString.equalsIgnoreCase("SHIP3") && !typeString.equalsIgnoreCase("CL") && !typeString.equalsIgnoreCase("SHIP4") && !typeString.equalsIgnoreCase("CA") && !typeString.equalsIgnoreCase("SHIP5") && !typeString.equalsIgnoreCase("HANGAR1") && !typeString.equalsIgnoreCase("HANGAR2") && !typeString.equalsIgnoreCase("TANK1")) {
							player.sendMessage("Unknown lot type");
							event.setCancelled(true);
							return;
						}

						String playerString = split[2];
						if ((plugin.getServer().getPlayer(playerString) == null) || !plugin.getServer().getPlayer(playerString).getName().equalsIgnoreCase(playerString)) {
							player.sendMessage("Player not found or not online.");
							event.setCancelled(true);
							return;
						}

						String outputString = playerString + "," + typeString + "," + player.getName() + "," + reasonString;

						NavyCraft.saveRewardsFile(outputString);

						player.sendMessage("Plot rewarded");

					} else if (split[1].equalsIgnoreCase("list")) {
						MoveCraft_BlockListener.loadShipyard();
						MoveCraft_BlockListener.loadRewards(player.getName());
						player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Your shipyard plots...");
						player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "<ID> - TYPE");

						if (NavyCraft.playerDDSigns.containsKey(player.getName())) {
							for (Sign s : NavyCraft.playerDDSigns.get(player.getName())) {
								player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - SHIP1");
							}
						}
						if (NavyCraft.playerSUB1Signs.containsKey(player.getName())) {
							for (Sign s : NavyCraft.playerSUB1Signs.get(player.getName())) {
								player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - SHIP2");
							}
						}
						if (NavyCraft.playerSUB2Signs.containsKey(player.getName())) {
							for (Sign s : NavyCraft.playerSUB2Signs.get(player.getName())) {
								player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - SHIP3");
							}
						}
						if (NavyCraft.playerCLSigns.containsKey(player.getName())) {
							for (Sign s : NavyCraft.playerCLSigns.get(player.getName())) {
								player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - SHIP4");
							}
						}
						if (NavyCraft.playerCASigns.containsKey(player.getName())) {
							for (Sign s : NavyCraft.playerCASigns.get(player.getName())) {
								player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - SHIP5");
							}
						}
						if (NavyCraft.playerHANGAR1Signs.containsKey(player.getName())) {
							for (Sign s : NavyCraft.playerHANGAR1Signs.get(player.getName())) {
								player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - HANGAR1");
							}
						}
						if (NavyCraft.playerHANGAR2Signs.containsKey(player.getName())) {
							for (Sign s : NavyCraft.playerHANGAR2Signs.get(player.getName())) {
								player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - HANGAR2");
							}
						}
						if (NavyCraft.playerTANK1Signs.containsKey(player.getName())) {
							for (Sign s : NavyCraft.playerTANK1Signs.get(player.getName())) {
								player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - TANK1");
							}
						}

					} else if (split[1].equalsIgnoreCase("tp")) {
						if (split.length == 3) {
							int tpId = -1;
							try {
								tpId = Integer.parseInt(split[2]);
							} catch (NumberFormatException e) {
								player.sendMessage("Invalid plot id");
								event.setCancelled(true);
								return;
							}

							if (tpId > -1) {
								MoveCraft_BlockListener.loadShipyard();
								MoveCraft_BlockListener.loadRewards(player.getName());

								Sign foundSign = null;
								foundSign = MoveCraft_BlockListener.findSign(player.getName(), tpId);

								if (foundSign != null) {
									player.teleport(foundSign.getLocation().add(0.5, 0.5, 0.5));
								} else {
									player.sendMessage("ID not found...use \"/shipyard list\" to see IDs");
								}

							} else {
								player.sendMessage("Invalid plot id");
							}
						} else {
							player.sendMessage("/shipyard tp <id> - teleport to a plot id");
						}
					} else if (split[1].equalsIgnoreCase("open")) {
						if (split.length == 3) {
							String typeString = split[2];

							Block tpBlock = null;
							if (typeString.equalsIgnoreCase("DD") || typeString.equalsIgnoreCase("SHIP1")) {
								tpBlock = MoveCraft_BlockListener.findDDOpen();
							} else if (typeString.equalsIgnoreCase("SUB1") || typeString.equalsIgnoreCase("SHIP2")) {
								tpBlock = MoveCraft_BlockListener.findSUB1Open();
							} else if (typeString.equalsIgnoreCase("SUB2") || typeString.equalsIgnoreCase("SHIP3")) {
								tpBlock = MoveCraft_BlockListener.findSUB2Open();
							} else if (typeString.equalsIgnoreCase("CL") || typeString.equalsIgnoreCase("SHIP4")) {
								tpBlock = MoveCraft_BlockListener.findCLOpen();
							} else if (typeString.equalsIgnoreCase("CA") || typeString.equalsIgnoreCase("SHIP5")) {
								tpBlock = MoveCraft_BlockListener.findCAOpen();
							} else if (typeString.equalsIgnoreCase("HANGAR1")) {
								tpBlock = MoveCraft_BlockListener.findHANGAR1Open();
							} else if (typeString.equalsIgnoreCase("HANGAR2")) {
								tpBlock = MoveCraft_BlockListener.findHANGAR2Open();
							} else if (typeString.equalsIgnoreCase("TANK1")) {
								tpBlock = MoveCraft_BlockListener.findTANK1Open();
							} else {
								player.sendMessage("Unknown lot type");
								event.setCancelled(true);
								return;
							}

							if (tpBlock != null) {
								player.teleport(tpBlock.getLocation().add(0.5, 0.5, 0.5));
							} else {
								player.sendMessage("No open plots found!");
							}

						} else {
							player.sendMessage("/shipyard open <plot type> - teleport to an unclaimed plot");
						}
					} else if (split[1].equalsIgnoreCase("info")) {
						if (split.length == 3) {
							int tpId = -1;
							try {
								tpId = Integer.parseInt(split[2]);
							} catch (NumberFormatException e) {
								player.sendMessage("Invalid plot id");
								event.setCancelled(true);
								return;
							}

							if (tpId > -1) {
								MoveCraft_BlockListener.loadShipyard();
								MoveCraft_BlockListener.loadRewards(player.getName());

								Sign foundSign = null;
								foundSign = MoveCraft_BlockListener.findSign(player.getName(), tpId);

								if (foundSign != null) {
									wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
									if (wgp != null) {
										RegionManager regionManager = wgp.getRegionManager(plugin.getServer().getWorld("shipyard"));
										String regionName = "--" + player.getName() + "-" + tpId;

										DefaultDomain dd = regionManager.getRegion(regionName).getMembers();

										player.sendMessage("Info-" + player.getName() + "-" + tpId);
										String members = "Members-";
										for (String s : dd.getPlayers()) {
											members += s + ", ";
										}
										player.sendMessage(members);
									}
								} else {
									player.sendMessage("ID not found...use \"/shipyard list\" to see IDs");
								}

							} else {
								player.sendMessage("Invalid plot id");
							}
						} else {
							player.sendMessage("/shipyard addmember <id> <player>");
						}
					} else if (split[1].equalsIgnoreCase("addmember")) {
						if (split.length == 4) {
							int tpId = -1;
							try {
								tpId = Integer.parseInt(split[2]);
							} catch (NumberFormatException e) {
								player.sendMessage("Invalid plot id");
								event.setCancelled(true);
								return;
							}

							if (tpId > -1) {
								MoveCraft_BlockListener.loadShipyard();
								MoveCraft_BlockListener.loadRewards(player.getName());

								Sign foundSign = null;
								foundSign = MoveCraft_BlockListener.findSign(player.getName(), tpId);

								if (foundSign != null) {
									wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
									if (wgp != null) {
										RegionManager regionManager = wgp.getRegionManager(plugin.getServer().getWorld("shipyard"));
										String regionName = "--" + player.getName() + "-" + tpId;

										String playerInName = split[3];
										Player p = plugin.getServer().getPlayer(playerInName);
										if (p == null) {
											player.sendMessage("Player not found");
											event.setCancelled(true);
											return;
										}
										com.sk89q.worldguard.LocalPlayer lp = wgp.wrapPlayer(p);

										regionManager.getRegion(regionName).getMembers().addPlayer(lp);

										try {
											regionManager.save();
										} catch (StorageException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										player.sendMessage("Player added.");
									}
								} else {
									player.sendMessage("ID not found...use \"/shipyard list\" to see IDs");
								}

							} else {
								player.sendMessage("Invalid plot id");
							}
						} else {
							player.sendMessage("/shipyard addmember <id> <player>");
						}
					} else if (split[1].equalsIgnoreCase("remmember")) {
						if (split.length == 4) {
							int tpId = -1;
							try {
								tpId = Integer.parseInt(split[2]);
							} catch (NumberFormatException e) {
								player.sendMessage("Invalid plot id");
								event.setCancelled(true);
								return;
							}

							if (tpId > -1) {
								MoveCraft_BlockListener.loadShipyard();
								MoveCraft_BlockListener.loadRewards(player.getName());

								Sign foundSign = null;
								foundSign = MoveCraft_BlockListener.findSign(player.getName(), tpId);

								if (foundSign != null) {
									wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager().getPlugin("WorldGuard");
									if (wgp != null) {
										RegionManager regionManager = wgp.getRegionManager(plugin.getServer().getWorld("shipyard"));
										String regionName = "--" + player.getName() + "-" + tpId;

										String playerInName = split[3];

										if (!regionManager.getRegion(regionName).getMembers().contains(playerInName)) {
											player.sendMessage("Member not found");
											event.setCancelled(true);
											return;
										}

										regionManager.getRegion(regionName).getMembers().removePlayer(playerInName);

										try {
											regionManager.save();
										} catch (StorageException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										player.sendMessage("Player removed.");
									}
								} else {
									player.sendMessage("ID not found...use \"/shipyard list\" to see IDs");
								}

							} else {
								player.sendMessage("Invalid plot id");
							}
						} else {
							player.sendMessage("/shipyard remmember <id> <player>");
						}
					} else if (split[1].equalsIgnoreCase("clear")) {
						if (split.length == 3) {
							int tpId = -1;
							try {
								tpId = Integer.parseInt(split[2]);
							} catch (NumberFormatException e) {
								player.sendMessage("Invalid plot id");
								event.setCancelled(true);
								return;
							}

							if (tpId > -1) {
								MoveCraft_BlockListener.loadShipyard();
								MoveCraft_BlockListener.loadRewards(player.getName());

								Sign foundSign = null;
								foundSign = MoveCraft_BlockListener.findSign(player.getName(), tpId);

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
													plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z).setType(Material.WATER);

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

										player.sendMessage("Plot cleared.");
									}
								} else {
									player.sendMessage("ID not found...use \"/shipyard list\" to see IDs");
								}

							} else {
								player.sendMessage("Invalid plot id");
							}
						} else {
							player.sendMessage("/shipyard clear <id>");
						}
					} else if (split[1].equalsIgnoreCase("rename")) {
						if (split.length > 3) {
							int tpId = -1;
							try {
								tpId = Integer.parseInt(split[2]);
							} catch (NumberFormatException e) {
								player.sendMessage("Invalid plot id");
								event.setCancelled(true);
								return;
							}

							String nameString;
							nameString = "";
							for (int i = 3; i < split.length; i++) {
								nameString += split[i] + " ";
							}

							if (tpId > -1) {
								MoveCraft_BlockListener.loadShipyard();
								MoveCraft_BlockListener.loadRewards(player.getName());

								Sign foundSign = null;
								foundSign = MoveCraft_BlockListener.findSign(player.getName(), tpId);

								if (foundSign != null) {
									foundSign.setLine(3, nameString);
									foundSign.update();
									player.sendMessage("Plot renamed.");
								} else {
									player.sendMessage("ID not found...use \"/shipyard list\" to see IDs");
								}

							} else {
								player.sendMessage("Invalid plot id");
							}
						} else {
							player.sendMessage("/shipyard rename <id> <custom name>");
						}
					} else if (split[1].equalsIgnoreCase("public")) {
						if (split.length == 3) {
							int tpId = -1;
							try {
								tpId = Integer.parseInt(split[2]);
							} catch (NumberFormatException e) {
								player.sendMessage("Invalid plot id");
								event.setCancelled(true);
								return;
							}

							if (tpId > -1) {
								MoveCraft_BlockListener.loadShipyard();
								MoveCraft_BlockListener.loadRewards(player.getName());

								Sign foundSign = null;
								foundSign = MoveCraft_BlockListener.findSign(player.getName(), tpId);

								if (foundSign != null) {
									Block selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(), foundSign.getY() - 1, foundSign.getZ() + 1);
									if (selectSignBlock2.getTypeId() != 68) {
										selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1, foundSign.getY() - 1, foundSign.getZ());
									}
									if (selectSignBlock2.getTypeId() == 68) {
										Sign selectSign2 = (Sign) selectSignBlock2.getState();
										selectSign2.setLine(0, "Public");
										selectSign2.update();
										player.sendMessage("Plot set to PUBLIC : Any player may SELECT it.");
									} else {
										player.sendMessage("Error: There may be a problem with your plot signs.");
									}
								} else {
									player.sendMessage("ID not found...use \"/shipyard list\" to see IDs");
								}

							} else {
								player.sendMessage("Invalid plot id");
							}
						} else {
							player.sendMessage("/shipyard public <id>");
						}
					} else if (split[1].equalsIgnoreCase("private")) {
						if (split.length == 3) {
							int tpId = -1;
							try {
								tpId = Integer.parseInt(split[2]);
							} catch (NumberFormatException e) {
								player.sendMessage("Invalid plot id");
								event.setCancelled(true);
								return;
							}

							if (tpId > -1) {
								MoveCraft_BlockListener.loadShipyard();
								MoveCraft_BlockListener.loadRewards(player.getName());

								Sign foundSign = null;
								foundSign = MoveCraft_BlockListener.findSign(player.getName(), tpId);

								if (foundSign != null) {
									Block selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(), foundSign.getY() - 1, foundSign.getZ() + 1);
									if (selectSignBlock2.getTypeId() != 68) {
										selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1, foundSign.getY() - 1, foundSign.getZ());
									}
									if (selectSignBlock2.getTypeId() == 68) {
										Sign selectSign2 = (Sign) selectSignBlock2.getState();
										selectSign2.setLine(0, "Private");
										selectSign2.update();
										player.sendMessage("Plot set to PRIVATE : Only you and your plot members can SELECT it.");
									} else {
										player.sendMessage("Error: There may be a problem with your plot signs.");
									}
								} else {
									player.sendMessage("ID not found...use \"/shipyard list\" to see IDs");
								}

							} else {
								player.sendMessage("Invalid plot id");
							}
						} else {
							player.sendMessage("/shipyard private <id>");
						}
					} else if (split[1].equalsIgnoreCase("player")) {
						if (split.length == 3) {
							String p = split[2];

							MoveCraft_BlockListener.loadShipyard();
							MoveCraft_BlockListener.loadRewards(p);
							player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + p + "'s shipyard plots...");
							if (NavyCraft.playerDDSigns.containsKey(p)) {
								int numDDs = NavyCraft.playerDDSigns.get(p).size();
								if (NavyCraft.playerDDRewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "SHIP1 - " + numDDs + " claimed of " + ChatColor.YELLOW + NavyCraft.playerDDRewards.get(p) + " available");
								} else {
									player.sendMessage(ChatColor.AQUA + "SHIP1 - " + numDDs + " claimed");
								}
							} else {
								if (NavyCraft.playerDDRewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "SHIP1 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerDDRewards.get(p) + " available");
								}
							}
							if (NavyCraft.playerSUB1Signs.containsKey(p)) {
								int numSUB1s = NavyCraft.playerSUB1Signs.get(p).size();

								if (NavyCraft.playerSUB1Rewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "SHIP2 - " + numSUB1s + " claimed of " + ChatColor.YELLOW + NavyCraft.playerSUB1Rewards.get(p) + " available");
								} else {
									player.sendMessage(ChatColor.AQUA + "SHIP2 - " + numSUB1s + " claimed");
								}
							} else {
								if (NavyCraft.playerSUB1Rewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "SHIP2 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerSUB1Rewards.get(p) + " available");
								}
							}
							if (NavyCraft.playerSUB2Signs.containsKey(p)) {
								int numSUB2s = NavyCraft.playerSUB2Signs.get(p).size();
								if (NavyCraft.playerSUB2Rewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "SHIP3 - " + numSUB2s + " claimed of " + ChatColor.YELLOW + NavyCraft.playerSUB2Rewards.get(p) + " available");
								} else {
									player.sendMessage(ChatColor.AQUA + "SHIP3 - " + numSUB2s + " claimed");
								}
							} else {
								if (NavyCraft.playerSUB2Rewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "SHIP3 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerSUB2Rewards.get(p) + " available");
								}
							}
							if (NavyCraft.playerCLSigns.containsKey(p)) {
								int numCLs = NavyCraft.playerCLSigns.get(p).size();
								if (NavyCraft.playerCLRewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "SHIP4 - " + numCLs + " claimed of " + ChatColor.YELLOW + NavyCraft.playerCLRewards.get(p) + " available");
								} else {
									player.sendMessage(ChatColor.AQUA + "SHIP4 - " + numCLs + " claimed");
								}
							} else {
								if (NavyCraft.playerCLRewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "SHIP4 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerCLRewards.get(p) + " available");
								}
							}
							if (NavyCraft.playerCASigns.containsKey(p)) {
								int numCLs = NavyCraft.playerCASigns.get(p).size();
								if (NavyCraft.playerCARewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "SHIP5 - " + numCLs + " claimed of " + ChatColor.YELLOW + NavyCraft.playerCARewards.get(p) + " available");
								} else {
									player.sendMessage(ChatColor.AQUA + "SHIP5 - " + numCLs + " claimed");
								}
							} else {
								if (NavyCraft.playerCARewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "SHIP5 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerCARewards.get(p) + " available");
								}
							}
							if (NavyCraft.playerHANGAR1Signs.containsKey(p)) {
								int numH1s = NavyCraft.playerHANGAR1Signs.get(p).size();
								if (NavyCraft.playerHANGAR1Rewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "HANGAR1 - " + numH1s + " claimed of " + ChatColor.YELLOW + NavyCraft.playerHANGAR1Rewards.get(p) + " available");
								} else {
									player.sendMessage(ChatColor.AQUA + "HANGAR1 - " + numH1s + " claimed");
								}
							} else {
								if (NavyCraft.playerHANGAR1Rewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "HANGAR1 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerHANGAR1Rewards.get(p) + " available");
								}
							}
							if (NavyCraft.playerHANGAR2Signs.containsKey(p)) {
								int numH2s = NavyCraft.playerHANGAR2Signs.get(p).size();
								if (NavyCraft.playerHANGAR2Rewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "HANGAR2 - " + numH2s + " claimed of " + ChatColor.YELLOW + NavyCraft.playerHANGAR2Rewards.get(p) + " available");
								} else {
									player.sendMessage(ChatColor.AQUA + "HANGAR2 - " + numH2s + " claimed");
								}
							} else {
								if (NavyCraft.playerHANGAR2Rewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "HANGAR2 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerHANGAR2Rewards.get(p) + " available");
								}
							}
							if (NavyCraft.playerTANK1Signs.containsKey(p)) {
								int numT1s = NavyCraft.playerTANK1Signs.get(p).size();
								if (NavyCraft.playerTANK1Rewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "TANK1 - " + numT1s + " claimed of " + ChatColor.YELLOW + NavyCraft.playerTANK1Rewards.get(p) + " available");
								} else {
									player.sendMessage(ChatColor.AQUA + "TANK1 - " + numT1s + " claimed");
								}
							} else {
								if (NavyCraft.playerTANK1Rewards.containsKey(p)) {
									player.sendMessage(ChatColor.AQUA + "TANK1 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerTANK1Rewards.get(p) + " available");
								}
							}
						} else {
							player.sendMessage("/shipyard player <playerName>");
						}
					} else if (split[1].equalsIgnoreCase("plist")) {
						if (split.length == 3) {
							String p = split[2];

							MoveCraft_BlockListener.loadShipyard();
							MoveCraft_BlockListener.loadRewards(p);
							player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + p + "'s shipyard plots...");
							player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "<ID> - TYPE");

							if (NavyCraft.playerDDSigns.containsKey(p)) {
								for (Sign s : NavyCraft.playerDDSigns.get(p)) {
									player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - SHIP1");
								}
							}
							if (NavyCraft.playerSUB1Signs.containsKey(p)) {
								for (Sign s : NavyCraft.playerSUB1Signs.get(p)) {
									player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - SHIP2");
								}
							}
							if (NavyCraft.playerSUB2Signs.containsKey(p)) {
								for (Sign s : NavyCraft.playerSUB2Signs.get(p)) {
									player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - SHIP3");
								}
							}
							if (NavyCraft.playerCLSigns.containsKey(p)) {
								for (Sign s : NavyCraft.playerCLSigns.get(p)) {
									player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - SHIP4");
								}
							}
							if (NavyCraft.playerCASigns.containsKey(p)) {
								for (Sign s : NavyCraft.playerCASigns.get(p)) {
									player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - SHIP5");
								}
							}
							if (NavyCraft.playerHANGAR1Signs.containsKey(p)) {
								for (Sign s : NavyCraft.playerHANGAR1Signs.get(p)) {
									player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - HANGAR1");
								}
							}
							if (NavyCraft.playerHANGAR2Signs.containsKey(p)) {
								for (Sign s : NavyCraft.playerHANGAR2Signs.get(p)) {
									player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - HANGAR2");
								}
							}
							if (NavyCraft.playerTANK1Signs.containsKey(p)) {
								for (Sign s : NavyCraft.playerTANK1Signs.get(p)) {
									player.sendMessage(ChatColor.AQUA + "<" + NavyCraft.playerSignIndex.get(s) + "> - TANK1");
								}
							}
						} else {
							player.sendMessage("/shipyard playerlist <playerName>");
						}
					} else if (split[1].equalsIgnoreCase("ptp")) {
						if (split.length == 4) {
							String p = split[2];
							int tpId = -1;
							try {
								tpId = Integer.parseInt(split[3]);
							} catch (NumberFormatException e) {
								player.sendMessage("Invalid plot id");
								event.setCancelled(true);
								return;
							}

							if (tpId > -1) {
								MoveCraft_BlockListener.loadShipyard();
								MoveCraft_BlockListener.loadRewards(p);

								Sign foundSign = null;
								foundSign = MoveCraft_BlockListener.findSign(p, tpId);

								if (foundSign != null) {
									player.teleport(foundSign.getLocation().add(0.5, 0.5, 0.5));
								} else {
									player.sendMessage("ID not found...use \"/shipyard plist\" to see IDs");
								}

							} else {
								player.sendMessage("Invalid plot id");
							}
						} else {
							player.sendMessage("/shipyard ptp <playerName> <id> - teleport to a player's plot id");
						}
					} else {
						player.sendMessage("/shipyard - Status message");
						player.sendMessage("/shipyard list - List your current plots");
						player.sendMessage("/shipyard info <id> - Information about plot");
						player.sendMessage("/shipyard open <plot type> - teleport to an unclaimed plot");
						player.sendMessage("/shipyard tp <id> - Teleport to the plot id number");
						player.sendMessage("/shipyard addmember <id> <player> - Gives player permission to that plot");
						player.sendMessage("/shipyard remmember <id> <player> - Removes player permission to that plot");
						player.sendMessage("/shipyard clear <id> - Destroys all blocks within the plot");
						player.sendMessage("/shipyard rename <id> <custom name> - Renames the plot");
						player.sendMessage("/shipyard public <id> - Allows any player to select your vehicle");
						player.sendMessage("/shipyard private <id> - Allows only you and your members to select your vehicle");
						player.sendMessage("/shipyard plist <player> - List the given player's plots");
						player.sendMessage("/shipyard ptp <player> <id> - Teleport to the player's plot id");
						if (PermissionInterface.CheckPermission(player, "movecraft.reward") || player.isOp()) {
							player.sendMessage("/shipyard player <player> - View a players plot status");
							player.sendMessage("/shipyard reward <player> <type> <reason> - Rewards the specified plot type to the player");
						}
					}
				} else {
					MoveCraft_BlockListener.loadShipyard();
					MoveCraft_BlockListener.loadRewards(player.getName());
					player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "Your shipyard plots...");
					if (NavyCraft.playerDDSigns.containsKey(player.getName())) {
						int numDDs = NavyCraft.playerDDSigns.get(player.getName()).size();
						if (NavyCraft.playerDDRewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "SHIP1 - " + numDDs + " claimed of " + ChatColor.YELLOW + NavyCraft.playerDDRewards.get(player.getName()) + " available");
						} else {
							player.sendMessage(ChatColor.AQUA + "SHIP1 - " + numDDs + " claimed");
						}
					} else {
						if (NavyCraft.playerDDRewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "SHIP1 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerDDRewards.get(player.getName()) + " available");
						}
					}
					if (NavyCraft.playerSUB1Signs.containsKey(player.getName())) {
						int numSUB1s = NavyCraft.playerSUB1Signs.get(player.getName()).size();

						if (NavyCraft.playerSUB1Rewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "SHIP2 - " + numSUB1s + " claimed of " + ChatColor.YELLOW + NavyCraft.playerSUB1Rewards.get(player.getName()) + " available");
						} else {
							player.sendMessage(ChatColor.AQUA + "SHIP2 - " + numSUB1s + " claimed");
						}
					} else {
						if (NavyCraft.playerSUB1Rewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "SHIP2 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerSUB1Rewards.get(player.getName()) + " available");
						}
					}
					if (NavyCraft.playerSUB2Signs.containsKey(player.getName())) {
						int numSUB2s = NavyCraft.playerSUB2Signs.get(player.getName()).size();
						if (NavyCraft.playerSUB2Rewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "SHIP3 - " + numSUB2s + " claimed of " + ChatColor.YELLOW + NavyCraft.playerSUB2Rewards.get(player.getName()) + " available");
						} else {
							player.sendMessage(ChatColor.AQUA + "SHIP3 - " + numSUB2s + " claimed");
						}
					} else {
						if (NavyCraft.playerSUB2Rewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "SHIP3 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerSUB2Rewards.get(player.getName()) + " available");
						}
					}
					if (NavyCraft.playerCLSigns.containsKey(player.getName())) {
						int numCLs = NavyCraft.playerCLSigns.get(player.getName()).size();
						if (NavyCraft.playerCLRewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "SHIP4 - " + numCLs + " claimed of " + ChatColor.YELLOW + NavyCraft.playerCLRewards.get(player.getName()) + " available");
						} else {
							player.sendMessage(ChatColor.AQUA + "SHIP4 - " + numCLs + " claimed");
						}
					} else {
						if (NavyCraft.playerCLRewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "SHIP4 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerCLRewards.get(player.getName()) + " available");
						}
					}
					if (NavyCraft.playerCASigns.containsKey(player.getName())) {
						int numCLs = NavyCraft.playerCASigns.get(player.getName()).size();
						if (NavyCraft.playerCARewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "SHIP5 - " + numCLs + " claimed of " + ChatColor.YELLOW + NavyCraft.playerCARewards.get(player.getName()) + " available");
						} else {
							player.sendMessage(ChatColor.AQUA + "SHIP5 - " + numCLs + " claimed");
						}
					} else {
						if (NavyCraft.playerCARewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "SHIP5 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerCARewards.get(player.getName()) + " available");
						}
					}
					if (NavyCraft.playerHANGAR1Signs.containsKey(player.getName())) {
						int numH1s = NavyCraft.playerHANGAR1Signs.get(player.getName()).size();
						if (NavyCraft.playerHANGAR1Rewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "HANGAR1 - " + numH1s + " claimed of " + ChatColor.YELLOW + NavyCraft.playerHANGAR1Rewards.get(player.getName()) + " available");
						} else {
							player.sendMessage(ChatColor.AQUA + "HANGAR1 - " + numH1s + " claimed");
						}
					} else {
						if (NavyCraft.playerHANGAR1Rewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "HANGAR1 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerHANGAR1Rewards.get(player.getName()) + " available");
						}
					}
					if (NavyCraft.playerHANGAR2Signs.containsKey(player.getName())) {
						int numH2s = NavyCraft.playerHANGAR2Signs.get(player.getName()).size();
						if (NavyCraft.playerHANGAR2Rewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "HANGAR2 - " + numH2s + " claimed of " + ChatColor.YELLOW + NavyCraft.playerHANGAR2Rewards.get(player.getName()) + " available");
						} else {
							player.sendMessage(ChatColor.AQUA + "HANGAR2 - " + numH2s + " claimed");
						}
					} else {
						if (NavyCraft.playerHANGAR2Rewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "HANGAR2 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerHANGAR2Rewards.get(player.getName()) + " available");
						}
					}
					if (NavyCraft.playerTANK1Signs.containsKey(player.getName())) {
						int numT1s = NavyCraft.playerTANK1Signs.get(player.getName()).size();
						if (NavyCraft.playerTANK1Rewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "TANK1 - " + numT1s + " claimed of " + ChatColor.YELLOW + NavyCraft.playerTANK1Rewards.get(player.getName()) + " available");
						} else {
							player.sendMessage(ChatColor.AQUA + "TANK1 - " + numT1s + " claimed");
						}
					} else {
						if (NavyCraft.playerTANK1Rewards.containsKey(player.getName())) {
							player.sendMessage(ChatColor.AQUA + "TANK1 - 0 claimed of " + ChatColor.YELLOW + NavyCraft.playerTANK1Rewards.get(player.getName()) + " available");
						}
					}
				}
				event.setCancelled(true);
				return;
			} else if (craftName.equalsIgnoreCase("em")) {
				if (split.length > 1) {
					String msgString;
					msgString = ChatColor.DARK_PURPLE + "* " + player.getDisplayName() + ChatColor.DARK_PURPLE + "> " + ChatColor.ITALIC;
					for (int i = 1; i < split.length; i++) {
						msgString += split[i] + " ";
					}

					if ((!player.getWorld().getName().equalsIgnoreCase("warworld1") && !player.getWorld().getName().equalsIgnoreCase("warworld2")) || (NavyCraft.checkSafeDockRegion(player.getLocation()) && !player.getWorld().getName().equalsIgnoreCase("warworld2"))) {
						for (Player p : plugin.getServer().getOnlinePlayers()) {
							if (NavyCraft.checkSafeDockRegion(p.getLocation()) || !p.getWorld().getName().equalsIgnoreCase("warworld1") || ((p.isOp() || p.hasPermission("movecraft.hidden")) && !NavyCraft.disableHiddenChats.contains(p.getName()))) {
								p.sendMessage(msgString);
							}
						}
					} else {
						for (Player p : plugin.getServer().getOnlinePlayers()) {
							if (p.getWorld().getName().equalsIgnoreCase("warworld1") && player.getWorld().getName().equalsIgnoreCase("warworld1")) {
								double dist = p.getLocation().distance(player.getLocation());
								if ((dist <= 50) || ((p.isOp() || p.hasPermission("movecraft.hidden")) && !NavyCraft.disableHiddenChats.contains(p.getName()))) {
									p.sendMessage(msgString);
								}
							} else if (p.getWorld().getName().equalsIgnoreCase("warworld2") && player.getWorld().getName().equalsIgnoreCase("warworld2")) {
								double dist = p.getLocation().distance(player.getLocation());
								if ((dist <= 50) || ((p.isOp() || p.hasPermission("movecraft.hidden")) && !NavyCraft.disableHiddenChats.contains(p.getName()))) {
									p.sendMessage(msgString);
								}
							}
						}
					}
				} else {
					player.sendMessage("Use /em <action> to describe an action");
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
									Essentials ess;
									ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
									if (ess == null) {
										player.sendMessage("Essentials Economy error");
										event.setCancelled(true);
										return;
									}
									player.sendMessage("Undoing sign and refunding player.");
									try {
										ess.getUser(player).giveMoney(new BigDecimal(NavyCraft.playerLastBoughtCost.get(player)));
									} catch (MaxMoneyException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									NavyCraft.playerLastBoughtSign.remove(player);
									NavyCraft.playerLastBoughtCost.remove(player);
									NavyCraft.playerLastBoughtSignString0.remove(player);
									NavyCraft.playerLastBoughtSignString1.remove(player);
									NavyCraft.playerLastBoughtSignString2.remove(player);
								} else {
									player.sendMessage("Incorrect sign detected.");
								}

							} else {
								player.sendMessage("No sign detected to undo.");
							}
						} else {
							player.sendMessage("Nothing to undo.");
						}
						event.setCancelled(true);
						return;
					}
				}

			} else if (craftName.equalsIgnoreCase("team")) {
				if (!NavyCraft.redPlayers.contains(player.getName()) && !NavyCraft.bluePlayers.contains(player.getName()) && !NavyCraft.anyPlayers.contains(player.getName()) && !PermissionInterface.CheckQuietPermission(player, "movecraft.battle") && !player.isOp()) {
					player.sendMessage("You are not on a team!");
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
						player.sendMessage("You are not on a team!");
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
								plugin.getServer().getPlayer(s).sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "[Team Red]" + ChatColor.GRAY + " <" + player.getName() + "> " + ChatColor.WHITE + msgString);
							}
						}
						System.out.println("[Team Red] <" + player.getName() + "> " + msgString);
					} else {
						for (String s : NavyCraft.bluePlayers) {
							if ((plugin.getServer().getPlayer(s) != null) && plugin.getServer().getPlayer(s).isOnline()) {
								plugin.getServer().getPlayer(s).sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "[Team Blue]" + ChatColor.GRAY + " <" + player.getName() + "> " + ChatColor.WHITE + msgString);
							}
						}
						System.out.println("[Team Blue] <" + player.getName() + "> " + msgString);
					}
				}
				event.setCancelled(true);
				return;
			} else if (craftName.equalsIgnoreCase("sailor")) {
				if (player.getWorld().getName().equalsIgnoreCase("WarWorld1") || player.getWorld().getName().equalsIgnoreCase("WarWorld2")) {
					if (player.getWorld().getName().equalsIgnoreCase("WarWorld2") && (NavyCraft.battleMode > 0)) {
						if (!NavyCraft.playerKits.contains(player.getName())) {
							player.sendMessage("Anchors Aweigh!");
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
							player.sendMessage("You only get one sailor kit per life!");
						}
					} else if (player.getWorld().getName().equalsIgnoreCase("WarWorld1")) {
						player.sendMessage("Command retired...use the Kit sign at a Safe Dock instead.");
					} else {
						player.sendMessage("You can't use that kit right now");
					}
				} else {
					player.sendMessage("You can only get this kit in the War Worlds.");
				}
			} else if (craftName.equalsIgnoreCase("battle")) {

				if (split.length == 1) {
					if (NavyCraft.battleMode == -1) {
						player.sendMessage("No active battle.");
						if (PermissionInterface.CheckQuietPermission(player, "movecraft.battle") || player.isOp()) {
							player.sendMessage("Use /battle new to start new battle");
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
							case 4:
								battleTypeStr = "Normandy";
								break;
							case 5:
								battleTypeStr = "Wake Island";
								break;
							case 6:
								battleTypeStr = "Omaha";
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

						player.sendMessage("Type \"/battle red|blue|any\" to queue!");
						player.sendMessage("There are " + ChatColor.RED + NavyCraft.redPlayers.size() + " red " + ChatColor.WHITE + "and " + ChatColor.BLUE + NavyCraft.bluePlayers.size() + " blue " + ChatColor.WHITE + " and " + NavyCraft.anyPlayers.size() + " unassigned.");
						if (PermissionInterface.CheckQuietPermission(player, "movecraft.battle") || player.isOp()) {
							player.sendMessage("Use /battle start to start battle");
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
							case 4:
								battleTypeStr = "Normandy";
								break;
							case 5:
								battleTypeStr = "Wake Island";
								break;
							case 6:
								battleTypeStr = "Omaha";
								break;
						}
						player.sendMessage("Battle in progress..." + battleTypeStr);
						if (!scoreUpdateStr.equalsIgnoreCase("")) {
							player.sendMessage(scoreUpdateStr);
						}

						if (NavyCraft.bluePlayers.contains(player.getName())) {
							player.sendMessage(ChatColor.BLUE + "You are on team blue!");
						} else if (NavyCraft.redPlayers.contains(player.getName())) {
							player.sendMessage(ChatColor.RED + "You are on team red!");
						} else if (!NavyCraft.battleLockTeams) {
							player.sendMessage("You can join this battle by typing \"/battle red|blue|any\"");
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
								player.sendMessage("You are already on that team");
							}
						} else if (NavyCraft.battleMode > 0) {
							if (!NavyCraft.battleLockTeams && ((NavyCraft.battleLength - (System.currentTimeMillis() - NavyCraft.battleStartTime)) > 300000)) {
								if (NavyCraft.redPlayers.contains(player.getName()) || NavyCraft.bluePlayers.contains(player.getName())) {
									player.sendMessage("Already on a team...first use /battle exit");
									event.setCancelled(true);
									return;
								}

								if (NavyCraft.redPlayers.size() > NavyCraft.bluePlayers.size()) {
									player.sendMessage("Too many on red...try again later");
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
								player.sendMessage("Teams locked");
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
									player.sendMessage("Already on a team...first use /battle exit");
									event.setCancelled(true);
									return;
								}

								if (NavyCraft.redPlayers.size() < NavyCraft.bluePlayers.size()) {
									player.sendMessage("Too many on blue...try again later");
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
								player.sendMessage("Teams locked");
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
								player.sendMessage("You are already on that team");
							}
						} else if (NavyCraft.battleMode > 0) {
							if (!NavyCraft.battleLockTeams && ((NavyCraft.battleLength - (System.currentTimeMillis() - NavyCraft.battleStartTime)) > 300000)) {
								if (NavyCraft.redPlayers.contains(player.getName()) || NavyCraft.bluePlayers.contains(player.getName())) {
									player.sendMessage("Already on a team...first use /battle exit");
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
								player.sendMessage("Teams locked");
							}
						}

					} else if (split[1].equalsIgnoreCase("exit")) {
						if (!NavyCraft.redPlayers.contains(player.getName()) && !NavyCraft.bluePlayers.contains(player.getName()) && !NavyCraft.anyPlayers.contains(player.getName())) {
							if (player.getWorld().getName().equalsIgnoreCase("warworld2")) {
								Location spawnLoc = plugin.getServer().getWorld("warworld1").getSpawnLocation();
								player.teleport(spawnLoc);
							}
							player.sendMessage("You are not on a team.");
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

						if (player.getWorld().getName().equalsIgnoreCase("warworld2")) {
							Location spawnLoc = plugin.getServer().getWorld("warworld1").getSpawnLocation();
							player.teleport(spawnLoc);
						}
					} else if (split[1].equalsIgnoreCase("kick")) {
						if (!PermissionInterface.CheckPermission(player, "movecraft.battle") && !player.isOp()) {
							player.sendMessage("You do not have permission to start battles");
							event.setCancelled(true);
							return;
						}

						if (split.length != 3) {
							player.sendMessage("Improper format...use /battle kick playerName");
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
								if (testPlayer.getWorld().getName().equalsIgnoreCase("warworld2")) {
									Location spawnLoc = plugin.getServer().getWorld("warworld1").getSpawnLocation();
									testPlayer.teleport(spawnLoc);
								}
								player.sendMessage("Player is not on a team.");
								event.setCancelled(true);
								return;
							}
							plugin.getServer().broadcastMessage(ChatColor.YELLOW + testPlayer.getName() + " was kicked from the battle!");
							if (testPlayer.getWorld().getName().equalsIgnoreCase("warworld2")) {
								Location spawnLoc = plugin.getServer().getWorld("warworld1").getSpawnLocation();
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
								player.sendMessage("Player not found.");
							}

							event.setCancelled(true);
							return;
						}

					} else if (split[1].equalsIgnoreCase("kickall")) {
						if (!PermissionInterface.CheckPermission(player, "movecraft.battle") && !player.isOp()) {
							player.sendMessage("You do not have permission to start battles");
							event.setCancelled(true);
							return;
						}

						List<Player> ww2Players = plugin.getServer().getWorld("warworld2").getPlayers();
						Location spawnLoc = plugin.getServer().getWorld("warworld1").getSpawnLocation();
						for (Player p : ww2Players) {
							if (p != player) {
								p.teleport(spawnLoc);
							}
						}
						plugin.getServer().broadcastMessage(ChatColor.YELLOW + player.getName() + " evicts everyone from WarWorld2!");

					} else if (split[1].equalsIgnoreCase("cancel")) {
						if (!PermissionInterface.CheckPermission(player, "movecraft.battle") && !player.isOp()) {
							player.sendMessage("You do not have permission to start battles");
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
						if (!PermissionInterface.CheckPermission(player, "movecraft.battle") && !player.isOp()) {
							player.sendMessage("You do not have permission to start battles");
							event.setCancelled(true);
							return;
						}

						if (NavyCraft.battleMode >= 1) {
							player.sendMessage("Battle already started! Use \"/battle end\" first");
						} else if (NavyCraft.battleMode == 0) {
							player.sendMessage("Battle already created...do you mean \"/battle start\"?");
						} else {
							int battleType = -1;
							if (split.length == 3) {
								if (split[2].equalsIgnoreCase("list")) {
									player.sendMessage("Battle List");
									player.sendMessage("1 - Tunisia (Desert-Tanks and Airplanes)");
									player.sendMessage("2 - Tarawa (Island-Ships and Airplanes)");
									player.sendMessage("3 - North Sea (Open Ocean-Ships)");
									player.sendMessage("And more coming soon...");
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
									 * if( !checkNorthSea() ) {
									 * player.sendMessage("North Sea bases need to be repaired first!");
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
									player.sendMessage("Invalid battle type option, use /battle new list to view!");
									return;
							}
							plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "***OFFICIAL BATTLE QUEUE OPEN!!!***");
							plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "***Battlezone: " + battleTypeStr + " Started by: " + player.getName() + "***");
							plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "***Type \"/battle red|blue|any\" to queue!***");
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
						if (!PermissionInterface.CheckPermission(player, "movecraft.battle") && !player.isOp()) {
							player.sendMessage("You do not have permission to start battles");
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
								player.sendMessage("Invalid battle type option, use /battle new list to view!");
								return;
						}
						plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "***OFFICIAL BATTLE QUEUE OPEN!!!***");
						plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "***Battlezone: " + battleTypeStr + " Started by: " + player.getName() + "***");
						plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "***Type \"/battle red|blue|any\" to queue!***");

					} else if (split[1].equalsIgnoreCase("start")) {
						if (!PermissionInterface.CheckPermission(player, "movecraft.battle") && !player.isOp()) {
							player.sendMessage("You do not have permission to start battles");
							event.setCancelled(true);
							return;
						}

						if (NavyCraft.battleMode >= 1) {
							player.sendMessage("Battle already started!");
						} else if (NavyCraft.battleMode == -1) {
							player.sendMessage("Do \"/battle new\" first!");
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
							NavyCraft.redSpawn = new Location(plugin.getServer().getWorld("warworld2"), 100, 64, 100);
							NavyCraft.blueSpawn = new Location(plugin.getServer().getWorld("warworld2"), 200, 64, 200);
							String redWelcomeStr = "";
							String blueWelcomeStr = "";
							String logStr = "";
							if (NavyCraft.battleType == 1) {
								NavyCraft.redSpawn = new Location(plugin.getServer().getWorld("warworld2"), -356, 69, 1114);
								NavyCraft.blueSpawn = new Location(plugin.getServer().getWorld("warworld2"), -650, 67, 1485);
								redWelcomeStr = ChatColor.RED + "Welcome to Tunisia : Red Team Base!";
								blueWelcomeStr = ChatColor.BLUE + "Welcome to Tunisia : Blue Team Base!";
								logStr = "Battlezone: Tunisia";
								NavyCraft.battleLength = 1800000;
								// MoveCraft.battleLength = 330000;
							} else if (NavyCraft.battleType == 2) {
								NavyCraft.redSpawn = new Location(plugin.getServer().getWorld("warworld2"), 199, 60, -1065);
								NavyCraft.blueSpawn = new Location(plugin.getServer().getWorld("warworld2"), -322, 75, -1166);
								redWelcomeStr = ChatColor.RED + "Welcome to Tarawa : Red Team Base!";
								blueWelcomeStr = ChatColor.BLUE + "Welcome to Tarawa : Blue Team Fleet!";
								logStr = "Battlezone: Tarawa";
								NavyCraft.battleLength = 1800000;
							} else if (NavyCraft.battleType == 3) {
								NavyCraft.redSpawn = new Location(plugin.getServer().getWorld("warworld2"), -614.5, 64, -712.5);
								NavyCraft.blueSpawn = new Location(plugin.getServer().getWorld("warworld2"), -629.5, 64, 106.5);
								redWelcomeStr = ChatColor.RED + "Welcome to the North Sea : Red Team Fleet!";
								blueWelcomeStr = ChatColor.BLUE + "Welcome to the North Sea : Blue Team Fleet!";
								logStr = "Battlezone: North Sea";
								NavyCraft.battleLength = 3600000;
							}

							plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "***OFFICIAL BATTLE STARTED!!!***");
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
						if (!PermissionInterface.CheckPermission(player, "movecraft.battle") && !player.isOp()) {
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
						if (!PermissionInterface.CheckPermission(player, "movecraft.battle") && !player.isOp()) {
							player.sendMessage("You do not have permission to start battles");
							event.setCancelled(true);
							return;
						}

						if (!NavyCraft.battleLockTeams) {
							player.sendMessage("Teams locked");
							NavyCraft.battleLockTeams = true;
						} else {
							player.sendMessage("Teams open");
							NavyCraft.battleLockTeams = false;
						}
					}
				}
				event.setCancelled(true);
				return;
			} else if (craftName.equalsIgnoreCase("zamboni")) {
				for (Craft c : Craft.craftList) {
					if (c.type.canZamboni) {
						player.teleport(new Location(c.world, c.minX + (c.sizeX / 2.0f), c.maxY + 1, c.minZ + (c.sizeZ / 2.0f)));
						event.setCancelled(true);
						return;
					}
				}

				for (int x = 75; x <= 1275; x = x + 50) {
					for (int z = -800; z <= 450; z++) {
						if (player.getWorld().getBlockAt(x, 81, z).getTypeId() == 68) {
							Sign sign = (Sign) player.getWorld().getBlockAt(x, 81, z).getState();
							String signLine0 = sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
							if (signLine0.equalsIgnoreCase("zamboni")) {
								player.teleport(sign.getLocation());
								event.setCancelled(true);
								return;
							}
						}
					}
				}

				for (int x = 75; x <= 1275; x = x + 50) {
					for (int z = -800; z <= 450; z++) {
						if (player.getWorld().getBlockAt(x, 42, z).getTypeId() == 68) {
							Sign sign = (Sign) player.getWorld().getBlockAt(x, 42, z).getState();
							String signLine0 = sign.getLine(0).trim().toLowerCase().replaceAll(ChatColor.BLUE.toString(), "");
							if (signLine0.equalsIgnoreCase("zamboni")) {
								player.teleport(sign.getLocation());
								event.setCancelled(true);
								return;
							}
						}
					}
				}

				player.sendMessage("Zamboni not found!");
			} else if (craftName.equalsIgnoreCase("rank")) {
				Plugin groupPlugin = plugin.getServer().getPluginManager().getPlugin("GroupManager");
				if (groupPlugin != null) {
					if (!plugin.getServer().getPluginManager().isPluginEnabled(groupPlugin)) {
						plugin.getServer().getPluginManager().enablePlugin(groupPlugin);
					}
					GroupManager gm = (GroupManager) groupPlugin;
					WorldsHolder wd = gm.getWorldsHolder();
					String groupName = wd.getWorldData(player).getUser(player.getName()).getGroupName();

					int nextRankXP = 0;
					if (groupName.equalsIgnoreCase("Default")) {
						nextRankXP = 2000;
						groupName = "Ensign";
					} else if (groupName.equalsIgnoreCase("LtJG")) {
						nextRankXP = 6000;
						groupName = "Lieutenant Junior Grade";
					} else if (groupName.equalsIgnoreCase("Lieutenant")) {
						nextRankXP = 18000;
						groupName = "Lieutenant";
					} else if (groupName.equalsIgnoreCase("Ltcm")) {
						nextRankXP = 54000;
						groupName = "Lieutenant Commander";
					} else if (groupName.equalsIgnoreCase("Commander")) {
						nextRankXP = 162000;
						groupName = "Commander";
					} else if (groupName.equalsIgnoreCase("Captain")) {
						nextRankXP = 486000;
						groupName = "Captain";
					} else if (groupName.equalsIgnoreCase("RearAdmiral1")) {
						nextRankXP = 1458000;
						groupName = "Rear Admiral (Lower)";
					} else if (groupName.equalsIgnoreCase("RearAdmiral2")) {
						nextRankXP = 4374000;
						groupName = "Rear Admiral (Upper)";
					} else if (groupName.equalsIgnoreCase("ViceAdmiral")) {
						nextRankXP = 13122000;
						groupName = "Vice Admiral";
					} else if (groupName.equalsIgnoreCase("Admiral")) {
						groupName = "Admiral";
					} else if (groupName.equalsIgnoreCase("FleetAdmiral")) {
						groupName = "Fleet Admiral";
					} else if (groupName.equalsIgnoreCase("Admin")) {
						groupName = "Admin";
					} else if (groupName.equalsIgnoreCase("BattleMod")) {
						groupName = "Battle Moderator";
					} else if (groupName.equalsIgnoreCase("WW-Mod")) {
						groupName = "WW Moderator";
					} else if (groupName.equalsIgnoreCase("Moderator")) {
						groupName = "Moderator";
					} else if (groupName.equalsIgnoreCase("SVR-Mod")) {
						groupName = "Moderator";
					}

					if (player.getWorld().getName().equalsIgnoreCase("warworld1")) {
						int exp = 0;
						if (NavyCraft.playerScoresWW1.containsKey(player.getName())) {
							exp = NavyCraft.playerScoresWW1.get(player.getName());
						}
						player.sendMessage(ChatColor.GRAY + "Your WW1 rank is " + ChatColor.WHITE + groupName + ChatColor.GRAY + " and you have " + ChatColor.WHITE + exp + "/" + nextRankXP + ChatColor.GRAY + " rank points.");
					} else if (player.getWorld().getName().equalsIgnoreCase("warworld2")) {
						int exp = 0;
						if (NavyCraft.playerScoresWW2.containsKey(player.getName())) {
							exp = NavyCraft.playerScoresWW2.get(player.getName());
						}
						player.sendMessage(ChatColor.GRAY + "Your WW2 rank is " + ChatColor.WHITE + groupName + ChatColor.GRAY + " and you have " + ChatColor.WHITE + exp + "/" + nextRankXP + ChatColor.GRAY + " rank points.");
					} else {
						player.sendMessage("That command does not work in this world");
					}
				} else {
					player.sendMessage("Group manager error");
					return;
				}
				event.setCancelled(true);

			} else if (craftType != null) {
				// if (!PermissionInterface.CheckPermission(player, "movecraft." + event.getMessage().substring(1))) {
				// event.setCancelled(true);
				// return;
				// }

				if (processCommand(craftType, player, split) == true) {
					event.setCancelled(true);
				}
			} else {
				Craft craft = Craft.getPlayerCraft(player);

				if (craft == null) { return; }

				int i = 0;
				while (i < split.length) {
					String tmpName = split[0];
					// build out tmpName with 0 + i
					if (tmpName.equalsIgnoreCase(craft.name)) {
						// if (!PermissionInterface.CheckPermission(player, "movecraft." +
						// event.getMessage().substring(1))) {
						// event.setCancelled(true);
						// return;
						// }
						if (processCommand(craftType, player, split) == true) {
							event.setCancelled(true);
						}
					}
					i++;
				}
			}
		}

		return;
	}

	public boolean processCommand(CraftType craftType, Player player, String[] split) {

		Craft craft = Craft.getPlayerCraft(player);

		if (split.length >= 2) {

			/*
			 * if(craft == null && !split[1].equalsIgnoreCase(craftType.driveCommand) &&
			 * !split[1].equalsIgnoreCase("remote")) return false;
			 */

			if (split[1].equalsIgnoreCase(craftType.driveCommand)) {

				/*
				 * if(!craftType.canUse(player)){ player.sendMessage(ChatColor.RED +
				 * "You are not allowed to use this type of craft"); return false; }
				 */

				String name = craftType.name;
				if ((split.length > 2) && (split[2] != null)) {
					name = split[2];
				}

				// try to detect and create the craft
				// use the block the player is standing on
				Craft checkCraft = Craft.getCraft(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
				if (checkCraft != null) {

				} else {
					NavyCraft.instance.createCraft(player, craftType, (int) Math.floor(player.getLocation().getX()), (int) Math.floor(player.getLocation().getY() - 1), (int) Math.floor(player.getLocation().getZ()), name, player.getLocation().getYaw(), null, false);
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
				craft.name = split[2];
				player.sendMessage(ChatColor.YELLOW + craft.type.name + "'s name set to " + craft.name);
				return true;

			} else if (split[1].equalsIgnoreCase("remote")) {
				if ((craft == null) || (craft.type != craftType)) {
					Set<Material> meh = new HashSet<>();
					Block targetBlock = player.getTargetBlock(meh, 100);

					if (targetBlock != null) {
						NavyCraft.instance.createCraft(player, craftType, targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), null, player.getLocation().getYaw(), null, false);
						Craft.getPlayerCraft(player).isNameOnBoard.put(player.getName(), false);
					} else {
						player.sendMessage("Couldn't find a target within 100 blocks. " + "If your admin asks reeeaaaaaally nicely, I might add distance as a config setting.");
					}

					return true;
				}

				if (craft.isOnCraft(player, true)) {
					player.sendMessage(ChatColor.YELLOW + "You are on the " + craftType.name + ", remote control not possible");
				} else {
					if (craft.haveControl) {
						player.sendMessage(ChatColor.YELLOW + "You switch off the remote controller");
					} else {
						MoveCraft_Timer timer = MoveCraft_Timer.playerTimers.get(player);
						if (timer != null) {
							timer.Destroy();
						}
						player.sendMessage(ChatColor.YELLOW + "You switch on the remote controller");
					}

					craft.haveControl = !craft.haveControl;
				}

				return true;

			} else if (split[1].equalsIgnoreCase("release") && (player.hasPermission("movecraft." + craftType.name + ".release") || player.isOp())) {
				// MoveCraft.instance.releaseCraft(player, craft);
				if (craft != null) {
					if ((craft.captainName == player.getName()) || player.isOp()) {
						player.sendMessage(ChatColor.YELLOW + "You release command of the ship");
						craft.releaseCraft();
						if (player.getInventory().contains(Material.GOLD_SWORD)) {
							player.getInventory().remove(Material.GOLD_SWORD);
						}
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "No vehicle detected.");
				}
				return true;

			} else if (split[1].equalsIgnoreCase("reload") && (player.hasPermission("movecraft." + craftType.name + ".reload") || player.isOp())) {

				// if( Craft.playerShipList.containsKey(player) )
				// {
				/*
				 * Craft newCraft = Craft.playerShipList.get(player); craft.matrix = newCraft.matrix; craft.dataBlocks =
				 * newCraft.dataBlocks; craft.complexBlocks = newCraft.complexBlocks; craft.rotation =
				 * newCraft.rotation; craft.isRepairing = true; CraftMover cm = new CraftMover(craft, plugin);
				 * //cm.move(-1, 0, -1, false, true); cm.calculatedMove(1,0,1); craft.isRepairing = false;
				 * player.sendMessage(ChatColor.YELLOW + "Your vehicle has been repaired!");
				 */
				if (craft != null) {
					CraftMover cm = new CraftMover(craft, plugin);
					cm.reloadWeapons(player);
				} else {
					player.sendMessage(ChatColor.YELLOW + "No vehicle detected.");
				}

				// }

				return true;

			} /*
				 * else if (split[1].equalsIgnoreCase("store") && (player.hasPermission("movecraft." + craftType.name +
				 * ".store") || player.isOp()) ) { if( craft != null ) { if( player.getName() == craft.captainName ) {
				 * if( MoveCraft.checkStorageRegion(craft.getLocation()) || player.isOp() ) { try { wep =
				 * (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit"); if( wep == null ) {
				 * player.sendMessage("WorldEdit error"); return true; }
				 *
				 * EditSession es = wep.createEditSession(player);
				 *
				 * /* CuboidClipboard cc = new CuboidClipboard(new
				 * com.sk89q.worldedit.Vector(craft.sizeX,craft.sizeY,craft.sizeZ), new
				 * com.sk89q.worldedit.Vector(craft.minX,craft.minY,craft.minZ), new
				 * com.sk89q.worldedit.Vector(craft.minX-player.getLocation().getBlockX(), craft.minY - 63,
				 * craft.minZ-player.getLocation().getBlockZ())); //CuboidClipboard cc = new CuboidClipboard(new
				 * com.sk89q.worldedit.Vector(craft.sizeX,craft.sizeY,craft.sizeZ), new
				 * com.sk89q.worldedit.Vector(craft.minX,craft.minY,craft.minZ), new com.sk89q.worldedit.Vector(0
				 * ,craft.minY - 63, 0)); /* wep.getWorldEdit().getSession(player.getName()).setClipboard(cc);
				 * wep.getWorldEdit().getSession(player.getName()).getClipboard().copy(es); CuboidClipboard clipboard =
				 * wep.getWorldEdit().getSession(player.getName()).getClipboard();
				 *
				 * Craft.playerStoredClipboard.put(player.getName(), clipboard);
				 *
				 * //String path = File.separator + "testMC.schematic"; //File file = new File(path);
				 *
				 * LocalPlayer lp = wep.wrapPlayer(player);
				 *
				 *
				 * try { File saveFile;
				 *
				 * saveFile = wep.getWorldEdit().getSafeSaveFile(lp, plugin.getDataFolder(), player.getName(),
				 * "schematic", new String[]{"schematic"}); try {
				 * wep.getWorldEdit().getSession(player.getName()).getClipboard().saveSchematic(saveFile);
				 * es.flushQueue(); player.sendMessage("Vehicle stored! Use *Recall* sign on dock to respawn.");
				 * craft.doDestroy = true; //player.sendMessage("sizeX=" + craft.sizeX + " sizeY=" + craft.sizeY +
				 * " sizeZ=" + craft.sizeZ); //player.sendMessage("minX=" + craft.minX + " minY=" + craft.minY +
				 * " minZ=" + craft.minZ); //player.sendMessage("offsetX=" + (craft.minX -
				 * craft.getLocation().getBlockX() ) + " offsetY=" + (craft.minY - craft.getLocation().getBlockY() ) +
				 * " offsetZ=" + (craft.minZ - craft.getLocation().getBlockZ() )); } catch (IOException e) {
				 * e.printStackTrace(); } catch (DataException e) { e.printStackTrace(); }
				 *
				 * } catch (FilenameException e) { e.printStackTrace(); }
				 *
				 *
				 * } catch (EmptyClipboardException e) {
				 *
				 * player.sendMessage("WorldEdit clipboard error"); return true; } }else {
				 * player.sendMessage(ChatColor.YELLOW + "You are not in a storage area."); } }else {
				 * player.sendMessage(ChatColor.YELLOW + "This is not your vehicle."); } }else {
				 * player.sendMessage(ChatColor.YELLOW + "No vehicle detected."); } return true; }else if
				 * (split[1].equalsIgnoreCase("repair") && (player.hasPermission("movecraft." + craftType.name +
				 * ".repair") || player.isOp()) ) { if( craft != null ) { if(
				 * MoveCraft.checkRepairRegion(craft.getLocation()) || player.isOp() ) { if( player.getName() ==
				 * craft.captainName ) { wep = (WorldEditPlugin)
				 * plugin.getServer().getPluginManager().getPlugin("WorldEdit"); if( wep == null ) {
				 * player.sendMessage("WorldEdit error"); return true; } EditSession es = wep.createEditSession(player);
				 *
				 *
				 * int oldLimit = es.getBlockChangeLimit(); es.setBlockChangeLimit(50000);
				 * wep.getWorldEdit().getSession(player.getName()).setClipboard(craft.repairClipboard);
				 * //wep.getWorldEdit().getSession(player.getName()).getClipboard().rotate2D(rotate); Block pasteBlock =
				 * new Location(player.getWorld(), player.getLocation().getBlockX(), 63,
				 * player.getLocation().getBlockZ()).getBlock(); try { craft.doDestroy = true;
				 * wep.getWorldEdit().getSession(player.getName()).getClipboard().paste(es, new
				 * com.sk89q.worldedit.Vector(pasteBlock.getX(), pasteBlock.getY(), pasteBlock.getZ()), false);
				 * es.flushQueue(); //wep.getWorldEdit().getSession(player.getName()).getClipboard().rotate2D(-rotate);
				 * if( craft.customName != null ) player.sendMessage(ChatColor.GREEN + "Repaired vehicle : " +
				 * ChatColor.WHITE + craft.customName ); else player.sendMessage(ChatColor.GREEN + "Repaired vehicle : "
				 * + ChatColor.WHITE + craft.name ); player.sendMessage(ChatColor.YELLOW + "Retake the helm." );
				 *
				 * } catch (MaxChangedBlocksException e) { e.printStackTrace(); } catch (EmptyClipboardException e) {
				 * e.printStackTrace(); } es.setBlockChangeLimit(oldLimit); }else { player.sendMessage(ChatColor.YELLOW
				 * + "This is not your vehicle."); }
				 *
				 * }else { player.sendMessage(ChatColor.YELLOW + "You are not in a repair dock region."); } }else {
				 * player.sendMessage(ChatColor.YELLOW + "No vehicle detected."); } return true; }else if
				 * (split[1].equalsIgnoreCase("recall") && (player.hasPermission("movecraft." + craftType.name +
				 * ".recall") || player.isOp()) ) { if( MoveCraft.checkRecallRegion(player.getLocation()) ||
				 * player.isOp() ) { wep = (WorldEditPlugin)
				 * plugin.getServer().getPluginManager().getPlugin("WorldEdit"); if( wep == null ) {
				 * player.sendMessage("WorldEdit error"); return true; } EditSession es = wep.createEditSession(player);
				 *
				 *
				 * int oldLimit = es.getBlockChangeLimit(); es.setBlockChangeLimit(50000);
				 *
				 *
				 * try { LocalPlayer lp = wep.wrapPlayer(player); File loadFile; loadFile =
				 * wep.getWorldEdit().getSafeSaveFile(lp, plugin.getDataFolder(), player.getName(), "schematic", new
				 * String[]{"schematic"}); wep.getWorldEdit().getSession(player.getName()).getClipboard();
				 * CuboidClipboard cc = CuboidClipboard.loadSchematic(loadFile); loadFile.delete(); if( cc != null ) {
				 * wep.getWorldEdit().getSession(player.getName()).setClipboard(cc);
				 * //wep.getWorldEdit().getSession(player.getName()).getClipboard().rotate2D(rotate); Block pasteBlock =
				 * new Location(player.getWorld(), player.getLocation().getBlockX(), 63,
				 * player.getLocation().getBlockZ()).getBlock(); try {
				 *
				 * wep.getWorldEdit().getSession(player.getName()).getClipboard().paste(es, new
				 * com.sk89q.worldedit.Vector(pasteBlock.getX(), pasteBlock.getY(), pasteBlock.getZ()), false);
				 * es.flushQueue();
				 *
				 * player.sendMessage(ChatColor.YELLOW + "Vehicle recalled from storage." ); player.teleport(new
				 * Location(player.getWorld(), player.getLocation().getX(), cc.getOrigin().getY() + cc.getHeight() + 2,
				 * player.getLocation().getZ())); } catch (MaxChangedBlocksException e) { e.printStackTrace();
				 * player.sendMessage("MaxChangedBlocks error"); } catch (EmptyClipboardException e) {
				 * e.printStackTrace(); player.sendMessage("EmptyClipboard error"); } }
				 *
				 *
				 *
				 * } catch (FilenameException e1) { e1.printStackTrace(); player.sendMessage("FilenameException error");
				 * } catch (EmptyClipboardException e) { e.printStackTrace();
				 * player.sendMessage("EmptyClipboard error"); } catch (DataException e) {
				 * player.sendMessage("DataException error"); e.printStackTrace(); } catch (IOException e) {
				 * player.sendMessage("No stored vehicle found."); //e.printStackTrace(); }
				 * es.setBlockChangeLimit(oldLimit); }else { player.sendMessage(ChatColor.YELLOW +
				 * "You are not in a recall region."); } return true; }
				 */else if (split[1].equalsIgnoreCase("drive") && (player.hasPermission("movecraft." + craftType.name + ".drive") || player.isOp())) {
				if (player.getItemInHand().getTypeId() > 0) {
					player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
					return true;
				}
				craft.driverName = player.getName();
				craft.haveControl = true;
				player.sendMessage(ChatColor.YELLOW + "You take control of the helm.");
				player.setItemInHand(new ItemStack(283, 1));
				CraftMover cm = new CraftMover(craft, plugin);
				cm.structureUpdate(null, false);

				return true;

			} else if (split[1].equalsIgnoreCase("info") && (player.hasPermission("movecraft." + craftType.name + ".info") || player.isOp())) {

				player.sendMessage(ChatColor.WHITE + craftType.name);
				if (craft != null) {
					player.sendMessage(ChatColor.YELLOW + "Using " + craft.blockCount + " of " + craftType.maxBlocks + " blocks (minimum " + craftType.minBlocks + ").");
					// Integer.toString(craftType.minBlocks) + "-" + craftType.maxBlocks + " blocks." +
					// " (Using " + + ".)");
				} else {
					player.sendMessage(ChatColor.YELLOW + Integer.toString(craftType.minBlocks) + "-" + craftType.maxBlocks + " blocks.");
				}
				player.sendMessage(ChatColor.YELLOW + "Max speed: " + craftType.maxSpeed);

				if (NavyCraft.instance.DebugMode) {
					player.sendMessage(ChatColor.YELLOW + Integer.toString(craft.dataBlocks.size()) + " data Blocks, " + craft.complexBlocks.size() + " complex Blocks, " + craft.engineBlocks.size() + " engine Blocks," + craft.digBlockCount + " drill bits.");
				}

				// player.sendMessage("Engine block ID: " + craft.type.engineBlockId);

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

				if (craftType.flyBlockType != 0) {
					int flyBlocksNeeded = (int) Math.floor(((craft.blockCount - craft.flyBlockCount) * ((float) craft.type.flyBlockPercent * 0.01)) / (1 - ((float) craft.type.flyBlockPercent * 0.01)));

					if (flyBlocksNeeded < 1) {
						flyBlocksNeeded = 1;
					}

					player.sendMessage(ChatColor.YELLOW + "Flight requirement: " + craftType.flyBlockPercent + "%" + " of " + BlocksInfo.getName(craft.type.flyBlockType) + "(" + flyBlocksNeeded + ")");
				}

				if (craft.type.fuelItemId != 0) {
					player.sendMessage(craft.remainingFuel + " units of fuel on board. " + "Movement requires type " + craft.type.fuelItemId);
				}

				return true;

			} else if (split[1].equalsIgnoreCase("hyperspace")) {
				if (!craft.inHyperSpace) {
					Craft_Hyperspace.enterHyperSpace(craft);
				} else {
					Craft_Hyperspace.exitHyperSpace(craft);
				}
				return true;
			} else if (split[1].equalsIgnoreCase("addwaypoint")) {
				// if(split[2].equalsIgnoreCase("absolute"))
				if (split[2].equalsIgnoreCase("relative")) {
					Location newLoc = craft.WayPoints.get(craft.WayPoints.size() - 1);
					if (!split[3].equalsIgnoreCase("0")) {
						newLoc.setX(newLoc.getX() + Integer.parseInt(split[3]));
					} else if (!split[4].equalsIgnoreCase("0")) {
						newLoc.setY(newLoc.getY() + Integer.parseInt(split[4]));
					} else if (!split[5].equalsIgnoreCase("0")) {
						newLoc.setZ(newLoc.getZ() + Integer.parseInt(split[5]));
					}

					craft.addWayPoint(newLoc);
				} else {
					craft.addWayPoint(player.getLocation());
				}

				player.sendMessage("Added waypoint...");

			} else if (split[1].equalsIgnoreCase("autotravel")) {
				if (split[2].equalsIgnoreCase("true")) {
					new MoveCraft_Timer(plugin, 0, craft, player, "automove", true);
				} else {
					new MoveCraft_Timer(plugin, 0, craft, player, "automove", false);
				}

			} else if (split[1].equalsIgnoreCase("dock")) {
				if (craft != null) {
					if (craft.driverName == player.getName()) {
						if (craft.autoTurn) {
							player.sendMessage("Docking mode engaged");
						} else {
							player.sendMessage("Docking mode disengaged");
						}
						craft.autoTurn = !craft.autoTurn;
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "No vehicle detected.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("command") && (player.hasPermission("movecraft." + craftType.name + ".command") || player.isOp())) {
				Craft testCraft = Craft.getCraft(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
				if (testCraft != null) {
					if (craft != null)// && playerCraft.type == craftType) {
					{
						/*
						 * MoveCraft.instance.unboardCraft(player, craft); craft.isNameOnBoard.remove(player.getName());
						 * craft.crewNames.remove(player.getName());
						 */
						craft.leaveCrew(player);
					}

					// String oldDriver = null;
					// if( testCraft.driverName != null && testCraft.driverName != player.getName() )
					// oldDriver = craft.driverName;
					testCraft.buildCrew(player, false);

					// if( oldDriver != null )
					// {
					// testCraft.driverName = oldDriver;
					// }

					CraftMover cm = new CraftMover(testCraft, plugin);
					cm.structureUpdate(null, false);
					if (testCraft.captainName == player.getName()) {
						player.sendMessage("You admin-hijack this vehicle!");
					}
				}
				return true;
			} else if (split[1].equalsIgnoreCase("remove")) {
				if (craft != null) {
					if ((craft.captainName == player.getName()) || (player.isOnline() && player.isOp())) {
						if (player.isOp() || player.hasPermission("movecraft.ship.command")) {
							craft.doRemove = true;
							if (player.getInventory().contains(Material.GOLD_SWORD)) {
								player.getInventory().remove(Material.GOLD_SWORD);
							}
							player.sendMessage("Vehicle Removed");
						} else {
							player.sendMessage(ChatColor.RED + "You do not have permission for this command. Use \"/ship disable\" instead.");
						}

					} else {
						player.sendMessage(ChatColor.RED + "You do not command this ship.");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "No vehicle detected.");
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
							player.sendMessage("Vehicle disabled.");
						} else if (!checkProtectedRegion(player, player.getLocation())) {
							craft.helmDestroyed = true;
							craft.setSpeed = 0;
							playerDisableThread(player, craft);
							player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Your vehicle will be fully disabled in 3 minutes.");
						} else {
							player.sendMessage(ChatColor.RED + "You can only use that command in a repair dock within the safe dock area.");
						}
					} else {
						player.sendMessage(ChatColor.RED + "You do not command this ship.");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "No vehicle detected.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("destroy")) {

				if (player.hasPermission("movecraft." + craftType.name + ".destroy") || player.isOp()) {
					if (craft != null) {
						if ((craft.captainName == player.getName()) || player.isOp()) {
							if (checkProtectedRegion(player, craft.getLocation()) || player.isOp() || player.hasPermission("movecraft.ship.command")) {
								craft.doDestroy = true;
								if (player.getInventory().contains(Material.GOLD_SWORD)) {
									player.getInventory().remove(Material.GOLD_SWORD);
								}
								player.sendMessage("Vehicle Destroyed");
							} else {
								player.sendMessage(ChatColor.RED + "You can only use this command in a safe dock region.");
							}

						} else {
							player.sendMessage(ChatColor.RED + "You do not command this ship.");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "No vehicle detected.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("sink")) {
				if (player.hasPermission("movecraft." + craftType.name + ".sink") || player.isOp()) {
					if (craft != null) {
						if (!craft.sinking) {
							if (craft.captainName == player.getName()) {
								if (!checkProtectedRegion(player, craft.getLocation())) {
									if (craft.isMerchantCraft && craft.redTeam) {
										NavyCraft.redMerchant = false;
									} else if (craft.isMerchantCraft && craft.blueTeam) {
										NavyCraft.blueMerchant = false;
									}

									craft.helmDestroyed = true;
									craft.setSpeed = 0;
									playerSinkThread(craft);
									player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Your vehicle will be scuttled in 3 minutes.");
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
						player.sendMessage(ChatColor.YELLOW + "No vehicle detected.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("update")) {
				if (player.hasPermission("movecraft." + craftType.name + ".update") || player.isOp()) {
					CraftPlayer cp;
					EntityPlayer ep;
					Chunk checkChunk = player.getLocation().getChunk();
					cp = (CraftPlayer) player;
					ep = cp.getHandle();
					// TODO MAKE SURE THIS WORKS
					// if (!ep.getChunkCoordinates().equals(new BlockPosition(checkChunk.getX(), 0, checkChunk.getZ()))
					// /* !ep.chunkCoordIntPairQueue.contains(new ChunkCoordIntPair(checkChunk.getX(),
					// checkChunk.getZ()))*/ ) {
					// ep.chunkCoordIntPairQueue.add(new ChunkCoordIntPair(checkChunk.getX(), checkChunk.getZ()));
					// }

					// if( craft != null && craft.checkEntities != null)
					// {
					// if( !craft.sinking )
					// {
					// if( craft.captainName == player.getName() )
					// {
					// if( !craft.checkEntities.isEmpty() )
					// {
					/*
					 * ProtocolManager manager = ProtocolLibrary.getProtocolManager(); CommonEntity ce; for( Entity e :
					 * plugin.getServer().getWorld("WarWorld1").getEntities() ) { if( !(e instanceof Player) ) { ce =
					 * CommonEntity.get(e); ce.getNetworkController().makeVisible(
					 * plugin.getServer().getPlayer("Maximuspayne")); manager.updateEntity(e,
					 * plugin.getServer().getWorld("WarWorld1").getPlayers()); } }
					 * plugin.getServer().broadcastMessage("manual update");
					 */
					// }
					// }
					// }
					// }
				}
				return true;
			} else if (split[1].equalsIgnoreCase("turn")) {
				// if(!player.getName().equalsIgnoreCase("sycoprime"))
				// return false;
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
						player.sendMessage(ChatColor.YELLOW + "You cannot use this command on this vehicle.");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "No vehicle detected.");
				}
			} else if (split[1].equalsIgnoreCase("warpdrive")) {
				// if the player just said "warpdrive", list the worlds they can warp to
				if (split.length == 1) {
					List<World> worlds = NavyCraft.instance.getServer().getWorlds();
					player.sendMessage("You can warp to: ");
					for (World world : worlds) {
						player.sendMessage(world.getName());
					}
				} else {
					World targetWorld = NavyCraft.instance.getServer().getWorld(split[2]);
					if (targetWorld != null) {
						craft.WarpToWorld(targetWorld);
					} else if (player.isOp()) { // create the world, if the player is an op
						if ((split.length > 3) && split[3].equalsIgnoreCase("nether")) {
							// MoveCraft.instance.getServer().createWorld(split[2], Environment.NETHER);
						} else {
							// MoveCraft.instance.getServer().createWorld(split[2], Environment.NORMAL);
						}

						while (targetWorld == null) {
							try {
								wait(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							targetWorld = NavyCraft.instance.getServer().getWorld(split[2]);
						}
						Chunk targetChunk = targetWorld.getChunkAt(new Location(targetWorld, craft.minX, craft.minY, craft.minZ));
						targetWorld.loadChunk(targetChunk);

						craft.WarpToWorld(targetWorld);
					}
				}
			} else if (split[1].equalsIgnoreCase("leave")) {
				if (craft != null) {
					craft.leaveCrew(player);
					player.sendMessage(ChatColor.YELLOW + "You leave the crew.");
				} else {
					player.sendMessage(ChatColor.YELLOW + "You are not on a crew.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("crew")) {
				if (craft != null) {
					if (craft.captainName == player.getName()) {
						craft.buildCrew(player, false);
					} else {
						player.sendMessage(ChatColor.YELLOW + "You are not the captain of this crew.");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "You are not on a crew.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("add")) {
				if (craft != null) {
					if (craft.captainName == player.getName()) {
						craft.buildCrew(player, true);
					} else {
						player.sendMessage(ChatColor.YELLOW + "You are not the captain of this crew.");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "You are not on a crew.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("summon")) {
				if (craft != null) {
					if (craft.captainName == player.getName()) {
						if (craft.signLoc != null) {
							player.sendMessage(ChatColor.YELLOW + "Summoning crew to your vehicle...");
							for (String s : craft.crewNames) {
								Player p = plugin.getServer().getPlayer(s);
								if (p != null) {
									if (!NavyCraft.shipTPCooldowns.containsKey(s) || (System.currentTimeMillis() > (NavyCraft.shipTPCooldowns.get(s) + 600000))) {
										NavyCraft.shipTPCooldowns.put(s, System.currentTimeMillis());
										p.teleport(new Location(craft.world, craft.signLoc.getBlockX() + 0.5, craft.signLoc.getBlockY() + 0.1, craft.signLoc.getBlockZ() + 0.5));
									} else {
										int timeLeft = (int) (((NavyCraft.shipTPCooldowns.get(s) + 600000) - System.currentTimeMillis()) / 60000);
										player.sendMessage(ChatColor.RED + "Player-" + s + " is on cooldown for " + timeLeft + " min");
									}
								} else {
									player.sendMessage(ChatColor.RED + "Player-" + s + " not located.");
								}
							}
						} else {
							player.sendMessage(ChatColor.RED + "Vehicle sign not located.");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "You are not the captain of this crew.");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "You are not on a crew.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("tp")) {
				if (craft != null) {
					if (craft.signLoc != null) {
						if (!NavyCraft.shipTPCooldowns.containsKey(player.getName()) || (System.currentTimeMillis() > (NavyCraft.shipTPCooldowns.get(player.getName()) + 600000))) {
							NavyCraft.shipTPCooldowns.put(player.getName(), System.currentTimeMillis());
							player.teleport(new Location(craft.world, craft.signLoc.getBlockX() + 0.5, craft.signLoc.getBlockY() + 0.1, craft.signLoc.getBlockZ() + 0.5));
						} else {
							int timeLeft = (int) (((NavyCraft.shipTPCooldowns.get(player.getName()) + 600000) - System.currentTimeMillis()) / 60000);
							player.sendMessage(ChatColor.RED + "You are on cooldown for " + timeLeft + " min");
						}
					} else {
						player.sendMessage(ChatColor.RED + "Vehicle sign not located.");
					}
				} else {
					player.sendMessage(ChatColor.YELLOW + "You are not on a crew.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("buoy")) {
				if (craft != null) {
					if ((split.length > 3) && split[2].equalsIgnoreCase("block")) {
						float blockValue = 0.33f;
						try {
							blockValue = Float.parseFloat(split[3]);
							if ((blockValue >= 0.01f) && (blockValue <= 100.0f)) {
								craft.blockDispValue = blockValue;
							} else {
								player.sendMessage("Invalid block displacement value, use 0.01 to 100.0");
							}
						} catch (NumberFormatException e) {
							player.sendMessage("Invalid block displacement value, use 0.01 to 100.0");
						}
					} else if ((split.length > 3) && split[2].equalsIgnoreCase("air")) {
						float airValue = 5.00f;
						try {
							airValue = Float.parseFloat(split[3]);
							if ((airValue >= 0.01f) && (airValue <= 100.0f)) {
								craft.airDispValue = airValue;
							} else {
								player.sendMessage("Invalid air displacement value, use 0.01 to 100.0");
							}
						} catch (NumberFormatException e) {
							player.sendMessage("Invalid air displacement value, use 0.01 to 100.0");
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

						player.sendMessage("Block Displacement = " + craft.blockDispValue + ", use \"/ship buoy block <value>\" to set");
						player.sendMessage("Air Displacement = " + craft.airDispValue + ", use \"/ship buoy air <value>\" to set");
						player.sendMessage("Minimum Displacement = " + craft.minDispValue + ", use \"/ship buoy min <value>\" to set");
						player.sendMessage("Weight Multiplier = " + craft.weightMult + ", use \"/ship buoy weight <value>\" to set");
					}
				}
				return true;
			} else {
				player.sendMessage("/ship - Ship Status");
				player.sendMessage("/ship release - (Cpt) Release your command of the ship");
				player.sendMessage("/ship leave - Leave the crew of your ship");
				player.sendMessage("/ship crew - (Cpt) Recreates your crew with players on your vehicle");
				player.sendMessage("/ship add - (Cpt) Add players on your vehicle to your crew");
				player.sendMessage("/ship summon - (Cpt) Teleports you and your crew to your vehicle (10 min cooldown)");
				player.sendMessage("/ship tp - Teleport to your vehicle (10 min cooldown)");
				player.sendMessage("/ship repair - (Cpt) Repairs your vehicle if in repair dock region");
				player.sendMessage("/ship store - (Cpt) Stores your vehicle if in a storage dock region");
				player.sendMessage("/ship disable - (Cpt) Deactivates a vehicle, so that it can be modified");
				player.sendMessage("/ship sink - (Cpt) Scuttles your vehicle after a timer");
				player.sendMessage("/ship destroy - (Cpt) Destroys your vehicle, usable in safedock region");
				player.sendMessage("/radio <message> - (or /ra) Send radio message (if equipped)");
				player.sendMessage("/radio - (or /ra) Radio status");
				player.sendMessage("/crew <message> - Send message to your crew");
				player.sendMessage("/crew - Crew status");
				if (PermissionInterface.CheckQuietPermission(player, "movecraft.ship.command") || player.isOp()) {
					player.sendMessage("/ship command - (Mod) Steal command of a ship");
					player.sendMessage("/ship remove - (Mod) Instantly disable a ship");
				}
				return true;
			}
		}

		if (craft != null) {
			player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Vehicle Status");
			player.sendMessage(ChatColor.YELLOW + "Type : " + ChatColor.WHITE + craft.name);
			if (craft.customName != null) {
				player.sendMessage(ChatColor.YELLOW + "Name : " + ChatColor.WHITE + craft.customName);
			} else {
				player.sendMessage(ChatColor.YELLOW + "Name : " + ChatColor.WHITE + craft.name);
			}
			player.sendMessage(ChatColor.YELLOW + "Captain : " + ChatColor.WHITE + craft.captainName);
			player.sendMessage(ChatColor.YELLOW + "Crew : " + ChatColor.WHITE + craft.crewNames.size());
			player.sendMessage(ChatColor.YELLOW + "Size : " + ChatColor.WHITE + craft.blockCount + " blocks");
			player.sendMessage(ChatColor.YELLOW + "Weight : " + ChatColor.WHITE + craft.weight + " tons");
			player.sendMessage(ChatColor.YELLOW + "Displacement : " + ChatColor.WHITE + craft.displacement + " tons (" + craft.blockDisplacement + " block," + craft.airDisplacement + " air)");
			player.sendMessage(ChatColor.YELLOW + "Health : " + ChatColor.WHITE + (int) (((float) craft.blockCount * 100) / craft.blockCountStart) + "%");
			player.sendMessage(ChatColor.YELLOW + "Engines : " + ChatColor.WHITE + craft.engineIDLocs.size() + " of " + craft.engineIDOn.size());
			if (craft.isAutoCraft) {
				player.sendMessage(ChatColor.YELLOW + "Auto Merchant : " + ChatColor.WHITE + craft.routeID + ":" + craft.routeStage);
			}
		} else {
			player.sendMessage(ChatColor.YELLOW + "You have no active vehicle.");
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
					if (checkCraft.isIn(eggBlock.getX(), eggBlock.getY(), eggBlock.getZ())) { return; }
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
						// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
						// cWorld.createExplosion(eggBlock.getLocation(), 6);

						// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
						// EntityTNTPrimed tnt = new EntityTNTPrimed(cWorld.getHandle(), eggBlock.getLocation().getX(),
						// eggBlock.getLocation().getY(), eggBlock.getLocation().getZ());
						// cWorld.getHandle().addEntity(tnt);
						// tnt.fuseTicks = fuseDelay;
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
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// cWorld.createExplosion(eggBlock.getRelative(BlockFace.NORTH).getLocation(), 6);
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// EntityTNTPrimed tnt = new EntityTNTPrimed(cWorld.getHandle(),
							// eggBlock.getLocation().getX(), eggBlock.getLocation().getY(),
							// eggBlock.getLocation().getZ());
							// cWorld.getHandle().addEntity(tnt);
							// tnt.fuseTicks = fuseDelay;
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
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// cWorld.createExplosion(eggBlock.getRelative(BlockFace.SOUTH).getLocation(), 6);
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// EntityTNTPrimed tnt = new EntityTNTPrimed(cWorld.getHandle(),
							// eggBlock.getLocation().getX(), eggBlock.getLocation().getY(),
							// eggBlock.getLocation().getZ());
							// cWorld.getHandle().addEntity(tnt);
							// tnt.fuseTicks = fuseDelay;
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
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// cWorld.createExplosion(eggBlock.getRelative(BlockFace.EAST).getLocation(), 6);
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// EntityTNTPrimed tnt = new EntityTNTPrimed(cWorld.getHandle(),
							// eggBlock.getLocation().getX(), eggBlock.getLocation().getY(),
							// eggBlock.getLocation().getZ());
							// cWorld.getHandle().addEntity(tnt);
							// tnt.fuseTicks = fuseDelay;
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
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// cWorld.createExplosion(eggBlock.getRelative(BlockFace.WEST).getLocation(), 6);
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// EntityTNTPrimed tnt = new EntityTNTPrimed(cWorld.getHandle(),
							// eggBlock.getLocation().getX(), eggBlock.getLocation().getY(),
							// eggBlock.getLocation().getZ());
							// cWorld.getHandle().addEntity(tnt);
							// tnt.fuseTicks = fuseDelay;
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
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// cWorld.createExplosion(eggBlock.getRelative(BlockFace.UP).getLocation(), 6);
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// EntityTNTPrimed tnt = new EntityTNTPrimed(cWorld.getHandle(),
							// eggBlock.getLocation().getX(), eggBlock.getLocation().getY(),
							// eggBlock.getLocation().getZ());
							// cWorld.getHandle().addEntity(tnt);
							// tnt.fuseTicks = fuseDelay;
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
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// cWorld.createExplosion(eggBlock.getRelative(BlockFace.DOWN).getLocation(), 6);
							// CraftWorld cWorld = (CraftWorld) eggBlock.getWorld();
							// EntityTNTPrimed tnt = new EntityTNTPrimed(cWorld.getHandle(),
							// eggBlock.getLocation().getX(), eggBlock.getLocation().getY(),
							// eggBlock.getLocation().getZ());
							// cWorld.getHandle().addEntity(tnt);
							// tnt.fuseTicks = fuseDelay;
							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld().spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(), eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
							tnt.setFuseTicks(fuseDelay);
							fuseDelay = fuseDelay + 2;
						}
					}
				}

				event.getPlayer().getWorld().playEffect(egg.getLocation(), Effect.SMOKE, 0);
				// event.getPlayer().getWorld().playEffect(egg.getLocation(), Effect.CLICK1, 0);
				event.getPlayer().getWorld().playSound(egg.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.0f, 1.00f);

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
				if (!loc.getWorld().getName().equalsIgnoreCase("warworld1") && !loc.getWorld().getName().equalsIgnoreCase("warworld2") && !loc.getWorld().getName().equalsIgnoreCase("warworld3")) { return true; }
				RegionManager regionManager = wgp.getRegionManager(player.getWorld());

				ApplicableRegionSet set = regionManager.getApplicableRegions(loc);

				Iterator<ProtectedRegion> it = set.iterator();
				while (it.hasNext()) {
					String id = it.next().getId();
					String[] splits = id.split("_");
					if (splits.length == 2) {
						if (splits[1].equalsIgnoreCase("safedock") || splits[1].equalsIgnoreCase("red") || splits[1].equalsIgnoreCase("blue")) { return true; }
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
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-263, 63, 1066))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-274, 63, 1065))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-299, 63, 1076))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-316, 67, 1072))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-332, 67, 1072))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-371, 70, 1065))) {
			blueTargetPoints += 500;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-344, 63, 1101))) {
			blueTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-344, 84, 1184))) {
			blueTargetPoints += 500;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-367, 77, 1178))) {
			blueTargetPoints += 200;
		}

		int redTargetPoints = 0;
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-611, 64, 1529))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-631, 64, 1539))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-642, 64, 1539))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-664, 68, 1537))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-677, 68, 1537))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-699, 71, 1489))) {
			redTargetPoints += 500;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-695, 64, 1522))) {
			redTargetPoints += 200;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-609, 75, 1478))) {
			redTargetPoints += 500;
		}
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-633, 68, 1468))) {
			redTargetPoints += 200;
		}
		int playerNewExp = 0;
		if (redTargetPoints > 0) {
			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				playerNewExp = redTargetPoints;
				if ((p != null) && p.isOnline()) {
					if (NavyCraft.playerScoresWW2.containsKey(p.getName())) {
						playerNewExp = NavyCraft.playerScoresWW2.get(p.getName()) + playerNewExp;
						NavyCraft.playerScoresWW2.put(p.getName(), playerNewExp);
					} else {
						NavyCraft.playerScoresWW2.put(p.getName(), playerNewExp);
					}
					p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + playerNewExp + ChatColor.GRAY + " rank points.");
					CraftMover.checkRankWW2(p, playerNewExp);

				}
			}
			NavyCraft.saveExperience();
		}

		if (blueTargetPoints > 0) {
			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				playerNewExp = blueTargetPoints;
				if ((p != null) && p.isOnline()) {
					if (NavyCraft.playerScoresWW2.containsKey(p.getName())) {
						playerNewExp = NavyCraft.playerScoresWW2.get(p.getName()) + playerNewExp;
						NavyCraft.playerScoresWW2.put(p.getName(), playerNewExp);
					} else {
						NavyCraft.playerScoresWW2.put(p.getName(), playerNewExp);
					}
					p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + playerNewExp + ChatColor.GRAY + " rank points.");
					CraftMover.checkRankWW2(p, playerNewExp);

				}
			}
			NavyCraft.saveExperience();
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

			Essentials ess;
			ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			if (ess == null) { return; }

			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p)));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else if (NavyCraft.redPoints < NavyCraft.bluePoints) {
			winStr = ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Team Wins!!";
			scoreStr = "Final Team Scores: " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue  " + ChatColor.RED + NavyCraft.redPoints + " Red";
			winLogStr = "Blue Team Wins!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.bluePoints + " Blue  " + NavyCraft.redPoints + " Red";

			Essentials ess;
			ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			if (ess == null) { return; }

			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p)));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			winStr = ChatColor.YELLOW + "" + ChatColor.BOLD + "Tie Game!!";
			scoreStr = "Final Team Scores: " + ChatColor.RED + NavyCraft.redPoints + " Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue";
			winLogStr = "Tie Game!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.redPoints + " Red  " + NavyCraft.bluePoints + " Blue";

			Essentials ess;
			ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			if (ess == null) { return; }

			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "***OFFICIAL BATTLE ENDED!***");
		plugin.getServer().broadcastMessage(winStr);
		plugin.getServer().broadcastMessage(scoreStr);
		CraftMover.battleLogger("***OFFICIAL BATTLE ENDED!***");
		CraftMover.battleLogger(winLogStr);
		CraftMover.battleLogger(scoreLogStr);

	}

	public int rankPayWW2(Player player) {
		int playerPay = 0;
		String groupName = "";
		Plugin groupPlugin = plugin.getServer().getPluginManager().getPlugin("GroupManager");
		if (groupPlugin != null) {
			if (!plugin.getServer().getPluginManager().isPluginEnabled(groupPlugin)) {
				plugin.getServer().getPluginManager().enablePlugin(groupPlugin);
			}
			GroupManager gm = (GroupManager) groupPlugin;
			WorldsHolder wd = gm.getWorldsHolder();
			groupName = wd.getWorldData("warworld2").getUser(player.getName()).getGroupName();

			if (groupName.equalsIgnoreCase("Default")) {
				playerPay = 1000;
			} else if (groupName.equalsIgnoreCase("LtJG")) {
				playerPay = 1300;
			} else if (groupName.equalsIgnoreCase("Lieutenant")) {
				playerPay = 1800;
			} else if (groupName.equalsIgnoreCase("Ltcm")) {
				playerPay = 2500;
			} else if (groupName.equalsIgnoreCase("Commander")) {
				playerPay = 3400;
			} else if (groupName.equalsIgnoreCase("Captain")) {
				playerPay = 4500;
			} else if (groupName.equalsIgnoreCase("RearAdmiral1") || groupName.equalsIgnoreCase("Trainer")) {
				playerPay = 5800;
			} else if (groupName.equalsIgnoreCase("RearAdmiral2") || groupName.equalsIgnoreCase("DockMaster") || groupName.equalsIgnoreCase("MilitaryPolice")) {
				playerPay = 7300;
			} else if (groupName.equalsIgnoreCase("ViceAdmiral") || groupName.equalsIgnoreCase("BattleMod")) {
				playerPay = 9000;
			} else if (groupName.equalsIgnoreCase("Admiral")) {
				playerPay = 10900;
			} else if (groupName.equalsIgnoreCase("FleetAdmiral")) {
				playerPay = 13000;
			} else if (groupName.equalsIgnoreCase("Admin")) {
				playerPay = 13000;
			} else if (groupName.equalsIgnoreCase("BattleMod")) {
				playerPay = 9000;
			} else if (groupName.equalsIgnoreCase("WW-Mod")) {
				playerPay = 7300;
			} else if (groupName.equalsIgnoreCase("Moderator")) {
				playerPay = 5800;
			} else if (groupName.equalsIgnoreCase("SVR-Mod")) {
				playerPay = 10900;
			}
		}
		return playerPay;
	}

	public void scoreTarawa() {
		int blueTargetPoints = 0;
		int redTargetPoints = 0;

		// shore battiers
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(194, 66, -1180))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}

		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(179, 66, -1110))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(163, 66, -1064))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(125, 66, -1023))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(82, 66, -977))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(171, 66, -867))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}

		// buildings
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(186, 65, -1079))) {
			redTargetPoints += 300;
		} else {
			blueTargetPoints += 300;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(167, 66, -1086))) {
			redTargetPoints += 300;
		} else {
			blueTargetPoints += 300;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(197, 60, -1066))) {
			redTargetPoints += 500;
		} else {
			blueTargetPoints += 500;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(201, 66, -1156))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(201, 79, -1149))) {
			redTargetPoints += 150;
		} else {
			blueTargetPoints += 150;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(220, 79, -977))) {
			redTargetPoints += 150;
		} else {
			blueTargetPoints += 150;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(220, 66, -984))) {
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
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(194, 66, -1180))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}

		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(179, 66, -1110))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(163, 66, -1064))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(125, 66, -1023))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(82, 66, -977))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(171, 66, -867))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}

		// buildings
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(186, 65, -1079))) {
			redTargetPoints += 300;
		} else {
			blueTargetPoints += 300;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(167, 66, -1086))) {
			redTargetPoints += 300;
		} else {
			blueTargetPoints += 300;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(197, 60, -1066))) {
			redTargetPoints += 500;
		} else {
			blueTargetPoints += 500;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(201, 66, -1156))) {
			redTargetPoints += 200;
		} else {
			blueTargetPoints += 200;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(201, 79, -1149))) {
			redTargetPoints += 150;
		} else {
			blueTargetPoints += 150;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(220, 79, -977))) {
			redTargetPoints += 150;
		} else {
			blueTargetPoints += 150;
		}
		if (checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(220, 66, -984))) {
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
					if (NavyCraft.playerScoresWW2.containsKey(p.getName())) {
						playerNewExp = NavyCraft.playerScoresWW2.get(p.getName()) + playerNewExp;
						NavyCraft.playerScoresWW2.put(p.getName(), playerNewExp);
					} else {
						NavyCraft.playerScoresWW2.put(p.getName(), playerNewExp);
					}
					p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + playerNewExp + ChatColor.GRAY + " rank points.");
					CraftMover.checkRankWW2(p, playerNewExp);
				}
			}
		}

		if (blueTargetPoints > 0) {
			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				playerNewExp = blueTargetPoints;
				if ((p != null) && p.isOnline()) {
					if (NavyCraft.playerScoresWW2.containsKey(p.getName())) {
						playerNewExp = NavyCraft.playerScoresWW2.get(p.getName()) + playerNewExp;
						NavyCraft.playerScoresWW2.put(p.getName(), playerNewExp);
					} else {
						NavyCraft.playerScoresWW2.put(p.getName(), playerNewExp);
					}
					p.sendMessage(ChatColor.GRAY + "You now have " + ChatColor.WHITE + playerNewExp + ChatColor.GRAY + " rank points.");
					CraftMover.checkRankWW2(p, playerNewExp);
				}
			}
		}
		NavyCraft.saveExperience();

		NavyCraft.redPoints = redTargetPoints;
		NavyCraft.bluePoints = blueTargetPoints;

		String winStr = "";
		String scoreStr = "";
		String winLogStr = "";
		String scoreLogStr = "";

		Essentials ess;
		ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
		if (ess == null) { return; }

		if (NavyCraft.redPoints > NavyCraft.bluePoints) {
			winStr = ChatColor.RED + "" + ChatColor.BOLD + "Red Team Wins!!";
			scoreStr = "Final Team Scores: " + ChatColor.RED + NavyCraft.redPoints + " Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue";
			winLogStr = "Red Team Wins!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.redPoints + " Red  " + NavyCraft.bluePoints + " Blue";

			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p)));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else if (NavyCraft.redPoints < NavyCraft.bluePoints) {
			winStr = ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Team Wins!!";
			scoreStr = "Final Team Scores: " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue  " + ChatColor.RED + NavyCraft.redPoints + " Red";
			winLogStr = "Blue Team Wins!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.bluePoints + " Blue  " + NavyCraft.redPoints + " Red";

			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p)));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			winStr = ChatColor.YELLOW + "" + ChatColor.BOLD + "Tie Game!!";
			scoreStr = "Final Team Scores: " + ChatColor.RED + NavyCraft.redPoints + " Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue";
			winLogStr = "Tie Game!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.redPoints + " Red  " + NavyCraft.bluePoints + " Blue";

			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "***OFFICIAL BATTLE ENDED!***");
		plugin.getServer().broadcastMessage(winStr);
		plugin.getServer().broadcastMessage(scoreStr);
		CraftMover.battleLogger("***OFFICIAL BATTLE ENDED!***");
		CraftMover.battleLogger(winLogStr);
		CraftMover.battleLogger(scoreLogStr);

	}

	public void endNorthSea() {
		String winStr = "";
		String scoreStr = "";
		String winLogStr = "";
		String scoreLogStr = "";

		Essentials ess;
		ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
		if (ess == null) { return; }

		if (NavyCraft.redPoints > NavyCraft.bluePoints) {
			winStr = ChatColor.RED + "" + ChatColor.BOLD + "Red Team Wins!!";
			scoreStr = "Final Team Scores: " + ChatColor.RED + NavyCraft.redPoints + " Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue";
			winLogStr = "Red Team Wins!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.redPoints + " Red  " + NavyCraft.bluePoints + " Blue";

			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p)));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else if (NavyCraft.redPoints < NavyCraft.bluePoints) {
			winStr = ChatColor.BLUE + "" + ChatColor.BOLD + "Blue Team Wins!!";
			scoreStr = "Final Team Scores: " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue  " + ChatColor.RED + NavyCraft.redPoints + " Red";
			winLogStr = "Blue Team Wins!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.bluePoints + " Blue  " + NavyCraft.redPoints + " Red";

			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p)));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			winStr = ChatColor.YELLOW + "" + ChatColor.BOLD + "Tie Game!!";
			scoreStr = "Final Team Scores: " + ChatColor.RED + NavyCraft.redPoints + " Red  " + ChatColor.BLUE + NavyCraft.bluePoints + " Blue";
			winLogStr = "Tie Game!!";
			scoreLogStr = "Final Team Scores: " + NavyCraft.redPoints + " Red  " + NavyCraft.bluePoints + " Blue";

			for (String s : NavyCraft.bluePlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			for (String s : NavyCraft.redPlayers) {
				Player p = plugin.getServer().getPlayer(s);
				if ((p != null) && p.isOnline()) {
					try {
						ess.getUser(p).giveMoney(new BigDecimal(rankPayWW2(p) / 4));
					} catch (MaxMoneyException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		plugin.getServer().broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "***OFFICIAL BATTLE ENDED!***");
		plugin.getServer().broadcastMessage(winStr);
		plugin.getServer().broadcastMessage(scoreStr);
		CraftMover.battleLogger("***OFFICIAL BATTLE ENDED!***");
		CraftMover.battleLogger(winLogStr);
		CraftMover.battleLogger(scoreLogStr);

	}

	public boolean checkTunisia() {
		// red
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-263, 63, 1066))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-274, 63, 1065))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-299, 63, 1076))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-316, 67, 1072))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-332, 67, 1072))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-371, 70, 1065))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-344, 63, 1101))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-344, 84, 1184))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-367, 77, 1178))) { return false; }
		// blue
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-611, 64, 1529))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-631, 64, 1539))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-642, 64, 1539))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-664, 68, 1537))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-677, 68, 1537))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-699, 71, 1489))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-695, 64, 1522))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-609, 75, 1478))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(-633, 68, 1468))) { return false; }
		return true;
	}

	public boolean checkTarawa() {
		// shore battiers
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(194, 66, -1180))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(179, 66, -1110))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(163, 66, -1064))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(125, 66, -1023))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(82, 66, -977))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(171, 66, -867))) { return false; }

		// buildings
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(186, 65, -1079))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(167, 66, -1086))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(197, 60, -1066))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(201, 66, -1156))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(201, 79, -1149))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(220, 79, -977))) { return false; }
		if (!checkForTarget(plugin.getServer().getWorld("warworld2").getBlockAt(220, 66, -984))) { return false; }
		return true;
	}

	public static boolean checkForTarget(Block targetSignCheck) {
		if (targetSignCheck.getTypeId() == 68) {
			Sign sign = (Sign) targetSignCheck.getState();

			if ((sign.getLine(0) == null) || sign.getLine(0).trim().equals("")) { return false; }

			String signLine0 = sign.getLine(0).trim().toLowerCase();

			// remove colors
			signLine0 = signLine0.replaceAll(ChatColor.BLUE.toString(), "");

			// remove brackets
			if (signLine0.startsWith("[")) {
				signLine0 = signLine0.substring(1, signLine0.length() - 1);
			}

			if (signLine0.equalsIgnoreCase("Target")) { return true; }
		}
		return false;
	}

	public void battleTimerThread() {
		// final int taskNum;
		// int taskNum = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
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
		// final int taskNum;
		// int taskNum = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
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
				player.sendMessage("Vehicle disabled.");
			}
		});
	}

	public void playerSinkThread(final Craft craft) {
		// final int taskNum;
		// int taskNum = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
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

	/*
	 * @EventHandler(priority = EventPriority.LOWEST) public void onAsyncPlayerChat (AsyncPlayerChatEvent event) {
	 *
	 * if( !event.isCancelled() ) { Player player = event.getPlayer(); ArrayList<Player> removePlayers = new
	 * ArrayList<Player>(); if(
	 * !(player.getWorld().getName().equalsIgnoreCase("warworld1")||player.getWorld().getName().equalsIgnoreCase(
	 * "warworld2")) ||
	 * (NavyCraft.checkSafeDockRegion(player.getLocation())&&!player.getWorld().getName().equalsIgnoreCase("warworld2"))
	 * ) { for( Player p: event.getRecipients() ) { if( (!NavyCraft.checkSafeDockRegion(p.getLocation()) &&
	 * p.getWorld().getName().equalsIgnoreCase("warworld1")) || p.getWorld().getName().equalsIgnoreCase("warworld2") ) {
	 * if( (p.isOp() || p.hasPermission("movecraft.hidden")) && !NavyCraft.disableHiddenChats.contains(p.getName()) )
	 * p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "*Hidden Chat*"); else removePlayers.add(p); } }
	 * event.setMessage(ChatColor.AQUA + "[Global] " + ChatColor.WHITE + event.getMessage()); }else if(
	 * player.getWorld().getName().equalsIgnoreCase("warworld1") ||
	 * player.getWorld().getName().equalsIgnoreCase("warworld2") ) { for( Player p: event.getRecipients() ) { if(
	 * player.getWorld().getName().equalsIgnoreCase("warworld1") ) { if(
	 * !p.getWorld().getName().equalsIgnoreCase("warworld1") ) { if( (p.isOp() || p.hasPermission("movecraft.hidden"))
	 * && !NavyCraft.disableHiddenChats.contains(p.getName()) ) p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD +
	 * "*Hidden Chat*"); else removePlayers.add(p);
	 *
	 * }else { double dist = p.getLocation().distance(player.getLocation()); if( dist > 50 ) { if( (p.isOp() ||
	 * p.hasPermission("movecraft.hidden")) && !NavyCraft.disableHiddenChats.contains(p.getName()) )
	 * p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "*Hidden Chat*"); else removePlayers.add(p); } } }else //ww2
	 * { if( !p.getWorld().getName().equalsIgnoreCase("warworld2") ) { if( (p.isOp() ||
	 * p.hasPermission("movecraft.hidden")) && !NavyCraft.disableHiddenChats.contains(p.getName()) )
	 * p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "*Hidden Chat*"); else removePlayers.add(p);
	 *
	 * }else { double dist = p.getLocation().distance(player.getLocation()); if( dist > 50 ) { if( (p.isOp() ||
	 * p.hasPermission("movecraft.hidden")) && !NavyCraft.disableHiddenChats.contains(p.getName()) )
	 * p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "*Hidden Chat*"); else removePlayers.add(p); } } } }
	 * event.setMessage(ChatColor.GREEN + "[Talk] " + ChatColor.WHITE + event.getMessage()); }else {
	 * event.setMessage(ChatColor.AQUA + "[Global] " + ChatColor.WHITE + event.getMessage()); }
	 *
	 * for( Player p: removePlayers ) { event.getRecipients().remove(p); } }
	 *
	 * }
	 */

}
