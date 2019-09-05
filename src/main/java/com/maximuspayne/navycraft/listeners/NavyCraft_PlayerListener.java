package com.maximuspayne.navycraft.listeners;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
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

import com.earth2me.essentials.Essentials;
import com.maximuspayne.aimcannon.AimCannon;
import com.maximuspayne.aimcannon.AimCannonPlayerListener;
import com.maximuspayne.aimcannon.OneCannon;
import com.maximuspayne.aimcannon.Weapon;
import com.maximuspayne.aimcannon.ciwsFire;
import com.maximuspayne.navycraft.ConfigManager;
import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Periscope;
import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.Pump;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.navycraft.blocks.BlocksInfo;
import com.maximuspayne.navycraft.blocks.DataBlock;
import com.maximuspayne.navycraft.craft.Craft;
import com.maximuspayne.navycraft.craft.CraftMover;
import com.maximuspayne.navycraft.craft.CraftType;
import com.maximuspayne.navycraft.teleportfix.TeleportFix;
import com.maximuspayne.shipyard.Plot;
import com.maximuspayne.shipyard.PlotType;
import com.maximuspayne.shipyard.Reward;
import com.maximuspayne.shipyard.Shipyard;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.ess3.api.MaxMoneyException;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@SuppressWarnings("deprecation")
public class NavyCraft_PlayerListener implements Listener {

	private static NavyCraft plugin;
	public WorldGuardPlugin wgp;
	public static PermissionsEx pex;
	public WorldEditPlugin wep;
	public static ConfigManager cfgm;

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
		try {
		NavyCraft_FileListener.loadPlayerData(player.getName());
		} catch (Exception ex) {
			System.out.println("Something is wrong with " + player.getName() + "'s playerdata!");
			ex.printStackTrace();
		}
		if (Craft.reboardNames.containsKey(player.getName())) {
			if ((Craft.reboardNames.get(player.getName()) != null)
					&& Craft.reboardNames.get(player.getName()).crewNames.contains(player.getName())) {
				Craft c = Craft.reboardNames.get(player.getName());
				Location loc = new Location(c.world, c.minX + (c.sizeX / 2), c.maxY, c.minZ + (c.sizeZ / 2));
				player.teleport(loc);

			}
			Craft.reboardNames.remove(player.getName());

		}
		String worldName = player.getWorld().getName();
		
		pex = (PermissionsEx)plugin.getServer().getPluginManager().getPlugin("PermissionsEx");
		if( pex==null )
			return;
		
		if (!NavyCraft.playerPayDays.containsKey(player.getName()) || (NavyCraft.playerPayDays.containsKey(player.getName())
				&& (((System.currentTimeMillis() - NavyCraft.playerPayDays.get(player.getName())) / 1000) > 86400))) {
			Essentials ess;
			ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
			if (ess == null) {
				player.sendMessage(ChatColor.RED + "Essentials Economy Error");
				return;
			}
				
			for(String s:PermissionsEx.getUser(player).getPermissions(worldName)) {
				if( s.contains("navycraft") ) {
					if( s.contains("pay") ) {
						String[] split = s.split("\\.");
						try {
							int pay = Integer.parseInt(split[2]);
							List<String> groupNames = PermissionsEx.getUser(player).getParentIdentifiers("navycraft");
							String rankName="";
							for( String group : groupNames ) {
								if( PermissionsEx.getPermissionManager().getGroup(group).getRankLadder().equalsIgnoreCase("navycraft") ) {
									rankName = group;
									break;
								}
							}
	
							player.sendMessage(
									ChatColor.GREEN + "Pay day! Your pay rate is:" + ChatColor.WHITE + rankName.toUpperCase());
							try {
								ess.getUser(player).giveMoney(new BigDecimal(pay));
							} catch (MaxMoneyException e) {
								e.printStackTrace();
							}
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

		String[] msgWords = deathMsg.split("\\s");
		if (msgWords.length == 5) {
			if (msgWords[1].equalsIgnoreCase("was") && msgWords[3].equalsIgnoreCase("by")) {
				Player p = plugin.getServer().getPlayer(msgWords[4]);
				if ((p != null) && Utils.CheckEnabledWorld(p.getLocation())) {
					int newExp = 100;
					
					plugin.getServer().broadcastMessage(ChatColor.GREEN + p.getName() + " receives " + ChatColor.YELLOW
							+ newExp + ChatColor.GREEN + " rank points!");
					{
						NavyCraft_BlockListener.rewardExpPlayer(newExp, p);
						NavyCraft_BlockListener.checkRankWorld(p, newExp, p.getWorld());
						NavyCraft_FileListener.saveExperience(p.getName());
					}
				}
			}
		}
	}


	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		Craft craft = Craft.getPlayerCraft(player);
		
		if (NavyCraft.flakGunnersList.contains(player) && ((event.getFrom().getBlockX() != event.getTo().getBlockX())
				|| (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {
			NavyCraft.flakGunnersList.remove(player);
			if (player.getInventory().contains(Material.BLAZE_ROD)) {
				player.getInventory().remove(Material.BLAZE_ROD);
			}
			player.sendMessage(ChatColor.GOLD + "You get off the Flak-Gun.");
		} else if (NavyCraft.aaGunnersList.contains(player) && ((event.getFrom().getBlockX() != event.getTo().getBlockX())
				|| (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {
			NavyCraft.aaGunnersList.remove(player);
			if (player.getInventory().contains(Material.BLAZE_ROD)) {
				player.getInventory().remove(Material.BLAZE_ROD);
			}
			player.sendMessage(ChatColor.GOLD + "You get off the AA-Gun.");
			
		} else if ((NavyCraft.ciwsGunnersList.contains(player) || NavyCraft.ciwsFiringList.contains(player)) && ((event.getFrom().getBlockX() != event.getTo().getBlockX())
				|| (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {
			NavyCraft.ciwsGunnersList.remove(player);
			NavyCraft.ciwsFiringList.remove(player);
			if (player.getInventory().contains(Material.BLAZE_ROD)) {
				player.getInventory().remove(Material.BLAZE_ROD);
			}
			player.sendMessage(ChatColor.GOLD + "You get off the CIWS.");
			
		} else if (NavyCraft.searchLightMap.containsKey(player) && ((event.getFrom().getBlockX() != event.getTo().getBlockX())
				|| (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {
			
			TeleportFix.updateNMSLight(null,NavyCraft.searchLightMap.get(player));
			NavyCraft.searchLightMap.remove(player);
			if (player.getInventory().contains(Material.BLAZE_ROD)) {
				player.getInventory().remove(Material.BLAZE_ROD);
			}
			player.sendMessage(ChatColor.GOLD + "You get off the searchlight.");
			

		}else if (craft != null) {

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

			if (!craft.isNameOnBoard.isEmpty() && craft.isNameOnBoard.containsKey(player.getName())
					&& craft.isNameOnBoard.get(player.getName()) && !craft.isOnCraft(player, false)) {
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


			} else if (craft.isNameOnBoard.containsKey(player.getName()) && !craft.isNameOnBoard.get(player.getName())
					&& craft.isOnCraft(player, false)) {
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
			} else if ((playerScope != null) && ((event.getFrom().getBlockX() != event.getTo().getBlockX())
					|| (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {


				CraftMover cmer = new CraftMover(craft, plugin);
				cmer.structureUpdate(null, false);
				craft.lastPeriscopeLookLoc = player.getTargetBlock(null, 250).getLocation();
				craft.lastPeriscopeYaw = player.getLocation().getYaw();
				Location newLoc = new Location(craft.world, playerScope.signLoc.getBlockX() + .5,
						playerScope.signLoc.getBlockY() - .5, playerScope.signLoc.getBlockZ() + .5);
				newLoc.setYaw(player.getLocation().getYaw());
				player.teleport(newLoc);
				playerScope.user = null;
				///// helm
			} else if ((craft.driverName == player.getName())
					&& ((event.getFrom().getBlockX() != event.getTo().getBlockX())
							|| (event.getFrom().getBlockZ() != event.getTo().getBlockZ()))) {
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

				if ((block.getTypeId() == 63) || (block.getTypeId() == 68) && event.getHand() == EquipmentSlot.HAND) {
					NavyCraft_BlockListener.ClickedASign(player, block, false);
					return;
				}
				
				if( block.getType() == Material.SPONGE && event.getHand() == EquipmentSlot.HAND) {
					Craft testCraft = Craft.getCraft(block.getX(), block.getY(), block.getZ());
					if (testCraft != null) {
						for(Pump p: testCraft.pumps)
						{
							if( p.loc.equals(block.getLocation())) {
								player.sendMessage("Pump has " + (p.limit - p.counter) + " charges left.");
								break;
							}
						}
					}				
				}

				if( block.getType() == Material.JACK_O_LANTERN && event.getHand() == EquipmentSlot.HAND ) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.searchlight.use")) {
						player.sendMessage(ChatColor.RED + "You do not have permission to use this.");
						return;
					}
					
					if (player.getItemInHand().getTypeId() > 0) {
						player.sendMessage(ChatColor.RED + "Have nothing in your hand before using this.");
						return;
					}
					
					Location newLoc = new Location(player.getWorld(), block.getLocation().getBlockX(), block.getLocation().getBlockY()+1, block.getLocation().getBlockZ());
					player.teleport(newLoc);
					player.setItemInHand(new ItemStack(369, 1));
					NavyCraft.searchLightMap.put(player, newLoc);
					player.sendMessage(ChatColor.GOLD + "Manning Searchlight! Left Click with Blaze Rod to point!");
					player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
				}
				
				if ((block.getTypeId() == 69) && (block.getRelative(BlockFace.DOWN, 1).getTypeId() == 68) && event.getHand() == EquipmentSlot.HAND) {
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
								if ((p.signLoc.getBlockX() == block.getRelative(BlockFace.DOWN, 1).getLocation()
										.getBlockX())
										&& (p.signLoc.getBlockY() == block.getRelative(BlockFace.DOWN, 1).getLocation()
												.getBlockY())
										&& (p.signLoc.getBlockZ() == block.getRelative(BlockFace.DOWN, 1).getLocation()
												.getBlockZ())) {
									if (p.raised && !p.destroyed && (p.scopeLoc != null)) {
										if ((p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 1).getTypeId() == 113)
												&& (p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 2)
														.getTypeId() == 113)
												&& (p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 3)
														.getTypeId() == 113)
												&& (p.scopeLoc.getBlock().getRelative(BlockFace.DOWN, 4)
														.getTypeId() == 113)) {
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
				if ((NavyCraft.instance.getConfig().getString("RequireHelm") == "true")
						&& (event.getItem().getTypeId() != playerCraft.type.HelmControllerItem)) {
					return;
				}
				if (event.getHand() == EquipmentSlot.HAND)
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

		if ((action == Action.LEFT_CLICK_BLOCK) && event.hasBlock() && event.getHand() == EquipmentSlot.HAND ) {
			Block block = event.getClickedBlock();
			if ((block.getTypeId() == 63) || (block.getTypeId() == 68)) {
				NavyCraft_BlockListener.ClickedASign(player, block, true);
				return;
			}
		}

		// fire airplane gun
		if ((action == Action.LEFT_CLICK_AIR) && (player.getItemInHand().getType() == Material.GOLD_SWORD)&& event.getHand() == EquipmentSlot.HAND ) {
			if (NavyCraft.instance.getConfig().getString("RequireAmmo").equalsIgnoreCase("false") || event.getPlayer().getInventory().contains(Material.EGG) || event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
				if (NavyCraft.instance.getConfig().getString("RequireAmmo").equalsIgnoreCase("true")) {
				ItemStack m = new ItemStack(Material.EGG, 1);
				player.getInventory().removeItem(m);
				player.updateInventory();
			}
			Craft testCraft = Craft.getPlayerCraft(event.getPlayer());
			if ((testCraft != null) && (testCraft.driverName == player.getName()) && testCraft.type.canFly
					&& !testCraft.sinking && !testCraft.helmDestroyed) {
				Egg newEgg = player.launchProjectile(Egg.class);

	

				newEgg.setVelocity(newEgg.getVelocity().multiply(2.0f));
				NavyCraft.explosiveEggsList.add(newEgg);
				event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
				CraftMover.playWeaponSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5.0f,
						1.70f);

				}
			} else {
				event.getPlayer().sendMessage(ChatColor.RED + "You are out of ammunition!");
			}
		}

		// AA Gunner...
		if ((action == Action.LEFT_CLICK_AIR) && NavyCraft.aaGunnersList.contains(player)
				&& (player.getItemInHand().getType() == Material.BLAZE_ROD)&& event.getHand() == EquipmentSlot.HAND) {
			if (NavyCraft.instance.getConfig().getString("RequireAmmo").equalsIgnoreCase("false") || event.getPlayer().getInventory().contains(Material.EGG) || event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
				if (NavyCraft.instance.getConfig().getString("RequireAmmo").equalsIgnoreCase("true")) {
				ItemStack m = new ItemStack(Material.EGG, 1);
				player.getInventory().removeItem(m);
				player.updateInventory();
			}
			Egg newEgg = player.launchProjectile(Egg.class);
			newEgg.setVelocity(newEgg.getVelocity().multiply(2.0f));
			NavyCraft.explosiveEggsList.add(newEgg);
			event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
			CraftMover.playWeaponSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5.0f,
					1.70f);	
			} else {
				event.getPlayer().sendMessage(ChatColor.RED + "You are out of ammunition!");
			}
		} else if ((action == Action.LEFT_CLICK_AIR) && NavyCraft.flakGunnersList.contains(player)
					&& (player.getItemInHand().getType() == Material.BLAZE_ROD)&& event.getHand() == EquipmentSlot.HAND) {
				if (NavyCraft.instance.getConfig().getString("RequireAmmo").equalsIgnoreCase("false") || event.getPlayer().getInventory().contains(Material.EGG) || event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
					if (NavyCraft.instance.getConfig().getString("RequireAmmo").equalsIgnoreCase("true")) {
					ItemStack m = new ItemStack(Material.EGG, 1);
					player.getInventory().removeItem(m);
					player.updateInventory();
				}
				Egg newEgg = player.launchProjectile(Egg.class);
				newEgg.setVelocity(newEgg.getVelocity().multiply(1.0f));
				NavyCraft.explosiveEggsList.add(newEgg);
				event.getPlayer().getWorld().playEffect(player.getLocation(), Effect.SMOKE, 0);
				CraftMover.playWeaponSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, 5.0f,
						1.70f);	
				} else {
					event.getPlayer().sendMessage(ChatColor.RED + "You are out of ammunition!");
				}

			//// CIWS
		} else if ((action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR) && NavyCraft.ciwsGunnersList.contains(player) && (player.getItemInHand().getType() == Material.BLAZE_ROD)&& event.getHand() == EquipmentSlot.HAND) {
				if (action == Action.LEFT_CLICK_AIR && !NavyCraft.ciwsFiringList.contains(player)) {
					NavyCraft.ciwsFiringList.add(player);
					ciwsFire.fireCIWS(player);
				}
				if (action == Action.RIGHT_CLICK_AIR && NavyCraft.ciwsFiringList.contains(player)) NavyCraft.ciwsFiringList.remove(player);
				
		//// else check for movement clicking
	} else if ((action == Action.RIGHT_CLICK_AIR) && (playerCraft != null)
				&& (playerCraft.driverName == player.getName()) && (playerCraft.type.listenItem == true)) {
			if ((NavyCraft.instance.getConfig().getString("RequireHelm") == "true")
					&& (event.getItem().getTypeId() != playerCraft.type.HelmControllerItem)) {
				return;
			}
			playerUsedAnItem(player, playerCraft);
		}
		
			// Search light
			if ((action == Action.LEFT_CLICK_AIR) && NavyCraft.searchLightMap.containsKey(player)
					&& (player.getItemInHand().getType() == Material.BLAZE_ROD)&& event.getHand() == EquipmentSlot.HAND) {
				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);
				Block block=null;
				try {
					block = player.getTargetBlock(transp, 100);
				}catch( IllegalStateException e ) {
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
			if (((item == craft.type.HelmControllerItem)
					|| (item == Integer.parseInt(NavyCraft.instance.getConfig().getString("HelmID"))))
					&& !craft.isOnCraft(player, true)) {
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

			if ((player.getItemInHand().getType() == Material.FLINT_AND_STEEL)
					&& NavyCraft.cleanupPlayers.contains(player.getName())
					&& Utils.CheckEnabledWorld(player.getLocation() )) {
				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);
				transp.add(Material.STATIONARY_WATER);
				transp.add(Material.WATER);
				Block block = player.getTargetBlock(transp, 300);
				if (block != null) {
					Craft c = Craft.getCraft(block.getX(), block.getY(), block.getZ());
					if (c != null) {
						if (!((c.captainName != null) && (plugin.getServer().getPlayer(c.captainName) != null)
								&& plugin.getServer().getPlayer(c.captainName).isOnline())) {
							if (craft != null)
							{
								craft.leaveCrew(player);
							}

							c.buildCrew(player, false);

							System.out.println("Vehicle destroyed by:" + player.getName() + " Name:" + c.name + " X:"
									+ c.getLocation().getBlockX() + " Y:" + c.getLocation().getBlockY() + " Z:"
									+ c.getLocation().getBlockZ());
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

						Craft theCraft = NavyCraft.instance.createCraft(player, CraftType.getCraftType("ship"),sign.getX(), sign.getY(), sign.getZ(), "ship", 0, block.getRelative(BlockFace.UP, 1));
						if (theCraft != null) {
							if (!NavyCraft.checkNoDriveRegion(theCraft.getLocation())) {
								System.out.println("Vehicle destroyed by:" + player.getName() + " Name:" + theCraft.name
										+ " X:" + theCraft.getLocation().getBlockX() + " Y:"
										+ theCraft.getLocation().getBlockY() + " Z:"
										+ theCraft.getLocation().getBlockZ());
								theCraft.doDestroy = true;
								player.sendMessage(ChatColor.GREEN + "Vehicle destroyed.");
							} else {
								player.sendMessage(ChatColor.RED + player.getName()
										+ ", why are you trying to destroy a dock vehicle??");
								System.out
										.println(player.getName() + ", why are you trying to destroy a dock vehicle??");
							}
						} else {
							sign.setLine(0, "Aircraft");
							sign.update();
							theCraft = NavyCraft.instance.createCraft(player, CraftType.getCraftType("aircraft"),sign.getX(), sign.getY(), sign.getZ(), "aircraft", 0,block.getRelative(BlockFace.UP, 1));

							if (theCraft != null) {
								if (!NavyCraft.checkNoDriveRegion(theCraft.getLocation())) {
									System.out.println("Vehicle destroyed by:" + player.getName() + " Name:"
											+ theCraft.name + " X:" + theCraft.getLocation().getBlockX() + " Y:"
											+ theCraft.getLocation().getBlockY() + " Z:"
											+ theCraft.getLocation().getBlockZ());
									theCraft.doDestroy = true;
									player.sendMessage(ChatColor.GREEN  + "Vehicle destroyed.");
								} else {
									player.sendMessage(ChatColor.RED + player.getName()
											+ ", why are you trying to destroy a dock vehicle??");
									System.out.println(
											player.getName() + ", why are you trying to destroy a dock vehicle??");
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
			if ((player.getItemInHand().getType() == Material.SHEARS)
					&& NavyCraft.cleanupPlayers.contains(player.getName())
					&& Utils.CheckEnabledWorld(player.getLocation())
					&& !NavyCraft.checkSafeDockRegion(player.getLocation())) {
				
				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);

				Block block = player.getTargetBlock(transp, 300);
				
				if (block != null) {
					System.out.println("Shears used:" + player.getName() + " X:" + block.getX() + " Y:" + block.getY()
							+ " Z:" + block.getZ());
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
			if ((player.getItemInHand().getType() == Material.GOLD_PICKAXE)
					&& NavyCraft.cleanupPlayers.contains(player.getName())
					&& Utils.CheckEnabledWorld(player.getLocation())
					&& !NavyCraft.checkSafeDockRegion(player.getLocation())) {
				Set<Material> transp = new HashSet<>();
				transp.add(Material.AIR);
				transp.add(Material.STATIONARY_WATER);
				transp.add(Material.WATER);
				Block block = player.getTargetBlock(transp, 300);

				if (block != null) {
					System.out.println("Golden Pickaxe used:" + player.getName() + " X:" + block.getX() + " Y:"
							+ block.getY() + " Z:" + block.getZ());
					player.sendMessage(ChatColor.GOLD + "Golden Pickaxe used!");
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
					Block theBlock = player.getWorld().getBlockAt(new Location(player.getWorld(),
							craft.minX + dataBlock.x, craft.minY + dataBlock.y, craft.minZ + dataBlock.z));
					theBlock.setType(Material.GOLD_BLOCK);
				}
			} else if (split[0].equalsIgnoreCase("findcomplexblocks")) {
				Craft craft = Craft.getPlayerCraft(player);
				for (DataBlock dataBlock : craft.complexBlocks) {
					Block theBlock = player.getWorld().getBlockAt(new Location(player.getWorld(),
							craft.minX + dataBlock.x, craft.minY + dataBlock.y, craft.minZ + dataBlock.z));
					theBlock.setType(Material.GOLD_BLOCK);
				}
			} else if (split[0].equalsIgnoreCase("diamondit")) {
				Craft craft = Craft.getPlayerCraft(player);

				for (int x = 0; x < craft.sizeX; x++) {
					for (int y = 0; y < craft.sizeY; y++) {
						for (int z = 0; z < craft.sizeZ; z++) {
							if (craft.matrix[x][y][z] != -1) {
								Block theBlock = player.getWorld().getBlockAt(new Location(player.getWorld(),
										craft.minX + x, craft.minY + y, craft.minZ + z));
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
				NavyCraft.instance
						.DebugMessage("Craft size: " + craft.sizeX + " * " + craft.sizeY + " * " + craft.sizeZ, 4);

				NavyCraft.instance.DebugMessage("Craft last move: " + craft.lastMove, 4);
				// world?
				NavyCraft.instance.DebugMessage("Craft center: " + craft.centerX + ", " + craft.centerZ, 4);

				NavyCraft.instance.DebugMessage("Craft water level: " + craft.waterLevel, 4);
				NavyCraft.instance.DebugMessage("Craft new water level: " + craft.newWaterLevel, 4);
				NavyCraft.instance.DebugMessage("Craft water type: " + craft.waterType, 4);

				NavyCraft.instance.DebugMessage("Craft bounds: " + craft.minX + "->" + craft.maxX + ", " + craft.minY
						+ "->" + craft.maxY + ", " + craft.minZ + "->" + craft.maxZ, 4);

			} else if (split[0].equalsIgnoreCase("getRotation")) {
				Set<Material> meh = new HashSet<>();
				Block examineBlock = player.getTargetBlock(meh, 100);

				int blockDirection = BlocksInfo.getCardinalDirectionFromData(examineBlock.getTypeId(),
						examineBlock.getData());
				player.sendMessage("Block data is " + examineBlock.getData() + " direction is " + blockDirection);
			}
		}

		if (split[0].equalsIgnoreCase("navycraft") || split[0].equalsIgnoreCase("nc")) {
			if (split.length >= 2) {
				if (split[1].equalsIgnoreCase("types")) {
					if( !PermissionInterface.CheckPerm(player, "navycraft.basic") )
						return;
					for (CraftType craftType : CraftType.craftTypes) {
						if (craftType.canUse(player)) {
							player.sendMessage(ChatColor.GREEN + craftType.name + ChatColor.YELLOW + craftType.minBlocks
									+ "-" + craftType.maxBlocks + " blocks" + " doesCruise : " + craftType.doesCruise);
						}
					}
				} else if (split[1].equalsIgnoreCase("list")) {
					if( !PermissionInterface.CheckPerm(player, "navycraft.list") )
						return;
					if (Craft.craftList.isEmpty()) {
						player.sendMessage(ChatColor.RED + "No player controlled craft");
						// return true;
					}

					for (Craft craft : Craft.craftList) {

						player.sendMessage(ChatColor.YELLOW + "" + craft.craftID + " - " + craft.name + " commanded by " + craft.captainName + ": "
								+ craft.blockCount + " blocks");
					}
				} else if (split[1].equalsIgnoreCase("reload")) {
					if( !PermissionInterface.CheckPerm(player, "navycraft.reload") )
						return;
					NavyCraft.instance.loadProperties();
					player.sendMessage(ChatColor.GREEN + "NavyCraft configuration reloaded");
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("debug")) {
					if( !PermissionInterface.CheckPerm(player, "navycraft.debug") )
						return;
					NavyCraft.instance.ToggleDebug();
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("loglevel")) {
					if( !PermissionInterface.CheckPerm(player, "navycraft.loglevel") )
						return;
					try {
						Integer.parseInt(split[2]);
						NavyCraft.instance.getConfig().set("LogLevel", split[2]);
						player.sendMessage(ChatColor.GREEN + "LogLevel" + split[2]);
					} catch (Exception ex) {
						player.sendMessage(ChatColor.RED + "Invalid loglevel.");
					}
		    		event.setCancelled(true);
					return;
					
					// Cleanup command
				} else if (split[1].equalsIgnoreCase("cleanup")) {
					if( !PermissionInterface.CheckPerm(player, "navycraft.cleanup") )
						return;
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
					if( !PermissionInterface.CheckPerm(player, "navycraft.weapons") )
						return;
					for (Weapon w : AimCannon.weapons) {
							player.sendMessage("weapon -" + w.weaponType);
					}
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("cannons")) {
					if( !PermissionInterface.CheckPerm(player, "navycraft.cannons") )
						return;
					for (OneCannon c : AimCannon.cannons) {
							player.sendMessage("cannon -" + c.cannonType);
					}
					event.setCancelled(true);
					return;
					//admin ship commands
				} else if (split[1].equalsIgnoreCase("destroyShips")) {
					if( !PermissionInterface.CheckPerm(player, "navycraft.destroyships") )
						return;
					for (Craft c : Craft.craftList) {
						c.doDestroy = true;
					}
					player.sendMessage(ChatColor.GREEN + "All vehicles destroyed");
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("removeships")) {
					if( !PermissionInterface.CheckPerm(player, "navycraft.removeships") )
						return;
					for (Craft c : Craft.craftList) {
						c.doRemove = true;
					}
					player.sendMessage(ChatColor.GREEN + "All vehicles removed");
					event.setCancelled(true);
					return;
				} else if (split[1].equalsIgnoreCase("tpShip") || split[1].equalsIgnoreCase("tp")) {
					if (!PermissionInterface.CheckPerm(player, "navycraft.tpship"))
						return;
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
								player.teleport(
										new Location(c.world, c.getLocation().getX(), c.maxY, c.getLocation().getZ()));
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
						player.sendMessage(ChatColor.GOLD + "NavyCraft v" + ChatColor.GREEN + NavyCraft.version
								+ ChatColor.GOLD + " commands :");
						player.sendMessage(ChatColor.AQUA + "/navycraft types " + " : " + ChatColor.WHITE
								+ "list the types of craft available");
						player.sendMessage(ChatColor.AQUA + "/[craft type] " + " : " + ChatColor.WHITE
								+ "commands specific to the craft type try /ship help");
						player.sendMessage(ChatColor.AQUA + "/volume" + " : " + ChatColor.WHITE + "volume help");
						player.sendMessage(ChatColor.AQUA + "/rank" + " : " + ChatColor.WHITE + "rank status message");
						player.sendMessage(
								ChatColor.AQUA + "/sign undo " + " : " + ChatColor.WHITE + "undo a sign you paid for");
					}

					if (PermissionInterface.CheckQuietPerm(player, "navycraft.admin")) {
						player.sendMessage(ChatColor.RED + "NavyCraft Admin v" + ChatColor.GREEN + NavyCraft.version
								+ ChatColor.RED + " commands :");
						player.sendMessage(ChatColor.BLUE + "/navycraft list : " + ChatColor.WHITE
								+ "list all craft");
						player.sendMessage(
								ChatColor.BLUE + "/navycraft reload : " + ChatColor.WHITE + "reload config files");
						player.sendMessage(
								ChatColor.BLUE + "/navycraft config : " + ChatColor.WHITE + "display config settings");
						player.sendMessage(ChatColor.BLUE + "/navycraft cleanup : " + ChatColor.WHITE
								+ "enables cleanup tools, use lighter, gold pickaxe, and shears");
						player.sendMessage(ChatColor.BLUE + "/navycraft destroyships : " + ChatColor.WHITE
								+ "destroys all active ships");
						player.sendMessage(ChatColor.BLUE + "/navycraft removeships : " + ChatColor.WHITE
								+ "deactivates all active ships");
						player.sendMessage(ChatColor.BLUE + "/navycraft tpship id # : " + ChatColor.WHITE
								+ "teleport to ship ID #");
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
					player.sendMessage(ChatColor.GOLD + "NavyCraft v" + ChatColor.GREEN + NavyCraft.version
							+ ChatColor.GOLD + " commands :");
					player.sendMessage(ChatColor.AQUA + "/navycraft types " + " : " + ChatColor.WHITE
							+ "list the types of craft available");
					player.sendMessage(ChatColor.AQUA + "/[craft type] " + " : " + ChatColor.WHITE
							+ "commands specific to the craft type try /ship help");
					player.sendMessage(ChatColor.AQUA + "/volume" + " : " + ChatColor.WHITE + "volume help");
					player.sendMessage(ChatColor.AQUA + "/rank" + " : " + ChatColor.WHITE + "rank status message");
					player.sendMessage(
							ChatColor.AQUA + "/sign undo " + " : " + ChatColor.WHITE + "undo a sign you paid for");
				}

				if (PermissionInterface.CheckQuietPerm(player, "navycraft.admin")) {
					player.sendMessage(ChatColor.RED + "NavyCraft Admin v" + ChatColor.GREEN + NavyCraft.version
							+ ChatColor.RED + " commands :");
					player.sendMessage(ChatColor.BLUE + "/navycraft list : " + ChatColor.WHITE
							+ "list all craft");
					player.sendMessage(
							ChatColor.BLUE + "/navycraft reload : " + ChatColor.WHITE + "reload config files");
					player.sendMessage(
							ChatColor.BLUE + "/navycraft config : " + ChatColor.WHITE + "display config settings");
					player.sendMessage(ChatColor.BLUE + "/navycraft cleanup : " + ChatColor.WHITE
							+ "enables cleanup tools, use lighter, gold pickaxe, and shears");
					player.sendMessage(ChatColor.BLUE + "/navycraft destroyships : " + ChatColor.WHITE
							+ "destroys all active ships");
					player.sendMessage(ChatColor.BLUE + "/navycraft removeships : " + ChatColor.WHITE
							+ "deactivates all active ships");
					player.sendMessage(
							ChatColor.BLUE + "/navycraft tpship id # : " + ChatColor.WHITE + "teleport to ship ID #");
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
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + "Captain" + ChatColor.DARK_GRAY + "] "
										+ ChatColor.WHITE + player.getName() + ChatColor.GRAY + msgString);

							} else {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "Crew" + ChatColor.DARK_GRAY + "] "
										+ ChatColor.WHITE + player.getName() + ChatColor.GRAY + msgString);

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
						player.sendMessage(ChatColor.GREEN + "Your radio is active on frequency: " + ChatColor.GREEN + craft.radio1 + "" + craft.radio2 + ""
								+ craft.radio3 + "" + craft.radio4);

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
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + craft.radio1
										+ craft.radio2 + craft.radio3 + craft.radio4 + ChatColor.DARK_GRAY + "] ["
										+ craft.customName.toUpperCase() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + msgString);
							} else {
								p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + craft.radio1
										+ craft.radio2 + craft.radio3 + craft.radio4 + ChatColor.DARK_GRAY +  "] ["
										+ craft.name.toUpperCase() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + msgString);
							}
						}
					}

					for (Craft c : Craft.craftList) {
						if ((c != craft) && c.radioSetOn) {
							if (c.radio1 == craft.radio1) {
								if (c.radio2 == craft.radio2) {
									if (c.radio3 == craft.radio3) {
										if (c.radio4 == craft.radio4) {
											if ((c.world == craft.world)
													&& (c.getLocation().distance(craft.getLocation()) < 5000)) {
												for (String s : c.crewNames) {
													Player p = plugin.getServer().getPlayer(s);
													if (p != null) {
														if (craft.customName != null) {
															p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + craft.radio1
																	+ craft.radio2 + craft.radio3 + craft.radio4 + ChatColor.DARK_GRAY + "] ["
																	+ craft.customName.toUpperCase() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getName() + msgString);
														} else {
															p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + craft.radio1
																	+ craft.radio2 + craft.radio3 + craft.radio4 + ChatColor.DARK_GRAY +  "] ["
																	+ craft.name.toUpperCase() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getName() + msgString);
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
						System.out.println("[" + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + ""
								+ craft.radio4 + "] [" + craft.customName + "] " + player.getName() + msgString);
					} else {
						System.out.println("[" + craft.radio1 + "" + craft.radio2 + "" + craft.radio3 + ""
								+ craft.radio4 + "] [" + craft.name + "] " + player.getName() + msgString);
					}

					craft.lastRadioPulse = System.currentTimeMillis();

				}
				event.setCancelled(true);
				return;
				// shipyard commands
				} else if (craftName.equalsIgnoreCase("shipyard") || craftName.equalsIgnoreCase("sy") || craftName.equalsIgnoreCase("yard")) {
					if (split.length > 1) {
						if (split[1].equalsIgnoreCase("addsign")) {
							if (!PermissionInterface.CheckPerm(player, "navycraft.addsign") && !player.isOp()) {
								player.sendMessage(ChatColor.RED + "You do not have permission to add signs.");
								event.setCancelled(true);
								return;
							}
							if (player.getTargetBlock(null, 5).getTypeId() == 63) {
									Block selectSignBlock = player.getTargetBlock(null, 5);
									Sign selectSign = (Sign) selectSignBlock.getState();
									BlockFace bf;
									bf = null;
									// bf2 = null;
									switch (selectSignBlock.getData()) {
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
									if (selectSignBlock.getRelative(BlockFace.DOWN).getRelative(bf, -1).getTypeId() == 68) {
									Sign selectSign2 = (Sign) selectSignBlock.getRelative(BlockFace.DOWN).getRelative(bf, -1).getState();
									String signLine0 = selectSign.getLine(0);
									String sign2Line3 = selectSign2.getLine(3);
									
							if (signLine0.equalsIgnoreCase("*claim*")) {
								NavyCraft_FileListener.updateSign(null, selectSign2.getLine(3), selectSign.getX(), selectSign.getY(),selectSign.getZ(), selectSign.getWorld(), null, false);
							} else {
								player.sendMessage(ChatColor.RED + "That is not a valid shipyard sign! (Top sign isn't a claim sign)");
								return;
							}
							player.sendMessage(ChatColor.GREEN + "Loaded: " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW
									+ "1" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + sign2Line3 + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + " plot");
							return;
									} else {
										player.sendMessage(ChatColor.RED + "That is not a valid shipyard sign! (Botom sign is null)");
										return;
									} 
							} else {
								player.sendMessage(ChatColor.RED + "That is not a valid shipyard sign! (Top sign is null)");
								return;
							} 
							
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
								boolean isCanceled = false;
							for (PlotType pt : Shipyard.getPlots()) {
								if (typeString.equalsIgnoreCase(pt.name)) {
									isCanceled = false;
									break;
								} else {
									isCanceled = true;
								}
							}
							if (isCanceled) {
								player.sendMessage(ChatColor.RED + "Unknown lot type");
								event.setCancelled(true);
								return;
							}
								String playerString = split[2];
								if (plugin.getServer().getPlayer(playerString) == null || !plugin.getServer()
										.getPlayer(playerString).getName().equalsIgnoreCase(playerString)) {
									player.sendMessage(ChatColor.RED + "Player not found or not online.");
									event.setCancelled(true);
									return;
								}
								
								int rewNum = 0;
								try {
									rewNum = Integer.parseInt(split[4]);
								} catch (NumberFormatException e) {
									player.sendMessage(ChatColor.RED + "Invalid Number");
									event.setCancelled(true);
									return;
								}
								
								if (rewNum <= 0) {
									player.sendMessage(ChatColor.RED + "Cannot revoke plots below 0!");
									event.setCancelled(true);
									return;
								}
								String UUID = Utils.getUUIDfromPlayer(playerString);
								if (UUID != null) {
								player.sendMessage("placeholder");
								} else {
									player.sendMessage(ChatColor.RED + playerString + "has never joined the server!");
									event.setCancelled(true);
									return;
								}
							    if (rewNum == 1) {
									player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + Math.abs(rewNum) + ChatColor.DARK_GRAY
											+ " - " + ChatColor.GOLD + typeString + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN
											+ " Plot rewarded to " + ChatColor.YELLOW + playerString);
									event.setCancelled(true);
									return;
								} else {
									player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + Math.abs(rewNum) + ChatColor.DARK_GRAY
											+ " - " + ChatColor.GOLD + typeString + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN
											+ " Plot's rewarded to " + ChatColor.YELLOW + playerString);
									event.setCancelled(true);
									return;
								}
							} else if (split[1].equalsIgnoreCase("revoke")) {
								if (!PermissionInterface.CheckPerm(player, "navycraft.reward") && !player.isOp()) {
									player.sendMessage(ChatColor.RED + "You do not have permission to revoke plots.");
									event.setCancelled(true);
									return;
								}
								
								if (split.length < 5) {
									player.sendMessage(ChatColor.GOLD + "Usage - /shipyard revoke <player> <type> <amount>");
									player.sendMessage(ChatColor.YELLOW + "Example - /shipyard revoke Solmex SHIP5 1");
									event.setCancelled(true);
									return;
								}
								
								String typeString = split[3];
								boolean isCanceled = false;
							for (PlotType pt : Shipyard.getPlots()) {
								if (typeString.equalsIgnoreCase(pt.name)) {
									isCanceled = false;
									break;
								} else {
									isCanceled = true;
								}
							}
							if (isCanceled) {
								player.sendMessage(ChatColor.RED + "Unknown lot type");
								event.setCancelled(true);
								return;
							}
								
								String playerString = split[2];
								if (plugin.getServer().getPlayer(playerString) == null || !plugin.getServer()
										.getPlayer(playerString).getName().equalsIgnoreCase(playerString)) {
									player.sendMessage(ChatColor.RED + "Player not found or not online.");
									event.setCancelled(true);
									return;
								}
								
								int rewNum = 0;
								try {
									rewNum = Math.abs(Integer.parseInt(split[4])) * -1;
								} catch (NumberFormatException e) {
									player.sendMessage(ChatColor.RED + "Invalid Number");
									event.setCancelled(true);
									return;
								}
								
								if (rewNum >= 0) {
									player.sendMessage(ChatColor.RED + "Can't use a negative number!");
									event.setCancelled(true);
									return;
								}
								String UUID = Utils.getUUIDfromPlayer(playerString);
								if (UUID != null) {
								player.sendMessage("placeholder");
								} else {
									player.sendMessage(ChatColor.RED + playerString + "has never joined the server!");
									event.setCancelled(true);
									return;
								}
							    if (rewNum == 1) {
									player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + Math.abs(rewNum) + ChatColor.DARK_GRAY
											+ " - " + ChatColor.GOLD + typeString + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN
											+ " Plot revoked from " + ChatColor.YELLOW + playerString);
									event.setCancelled(true);
									return;
								} else {
									player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + Math.abs(rewNum) + ChatColor.DARK_GRAY
											+ " - " + ChatColor.GOLD + typeString + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN
											+ " Plot's revoked from " + ChatColor.YELLOW + playerString);
									event.setCancelled(true);
									return;
								}
						} else if (split[1].equalsIgnoreCase("list")) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());
							String UUID = Utils.getUUIDfromPlayer(player.getName());
						player.sendMessage(ChatColor.AQUA + "Your Shipyard Plots:");
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "ID" + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TYPE");

							if (NavyCraft.playerSigns.containsKey(UUID)) {
								for (Plot p : NavyCraft.playerSigns.get(UUID)) {
									player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(p.sign) + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + p.name);
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
								player.sendMessage(ChatColor.YELLOW + "/shipyard tp <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "teleport to the given plot ID");
							}
						} else if (split[1].equalsIgnoreCase("help")) {
							player.sendMessage(ChatColor.GOLD + "Shipyard v" + ChatColor.GREEN + NavyCraft.version
									+ ChatColor.GOLD + " commands :");
							player.sendMessage(ChatColor.AQUA + "/shipyard - Status message");
							player.sendMessage(ChatColor.AQUA + "/shipyard list - List your current plots");
							player.sendMessage(ChatColor.AQUA + "/shipyard info <id> - Information about the given plot");
							player.sendMessage(ChatColor.AQUA + "/shipyard open <plot type> - Teleport to an unclaimed plot");
							player.sendMessage(ChatColor.AQUA + "/shipyard unclaim <id> - Clears and unclaims a plot");
							player.sendMessage(ChatColor.AQUA + "/shipyard tp <id> - Teleport to the plot ID number");
							player.sendMessage(ChatColor.AQUA + "/shipyard addmember <id> <player> - Gives player permission to that plot");
							player.sendMessage(ChatColor.AQUA + "/shipyard remmember <id> <player> - Removes player permission to that plot");
							player.sendMessage(ChatColor.AQUA + "/shipyard clear <id> - Destroys all blocks within the plot");
							player.sendMessage(ChatColor.AQUA + "/shipyard rename <id> <custom name> - Renames the plot");
							player.sendMessage(ChatColor.AQUA + "/shipyard public <id> - Allows any player to select your vehicle");
							player.sendMessage(ChatColor.AQUA + "/shipyard private <id> - Allows only you and your members to select your vehicle");
							player.sendMessage(ChatColor.AQUA + "/shipyard plist <player> - List the given player's plots");
							player.sendMessage(ChatColor.AQUA + "/shipyard ptp <player> <id> - Teleport to the player's plot ID");
							player.sendMessage(ChatColor.AQUA + "/shipyard renumber <id> <newid> - Renumbers the given plot ID to new ID");
							player.sendMessage(ChatColor.AQUA + "/shipyard schem list - List saved vehicles");
							player.sendMessage(ChatColor.AQUA + "/shipyard schem plist - List the given player's saved vehicles");
							player.sendMessage(ChatColor.AQUA + "/shipyard schem load <name> <id> - Load a saved vehicle into a plot");
							player.sendMessage(ChatColor.AQUA + "/shipyard schem save <id> <name> - Saves a vehicle in a plot to a schematic");
							if (PermissionInterface.CheckQuietPerm(player, "navycraft.admin") || player.isOp()) {
								player.sendMessage(ChatColor.RED + "Shipyard Admin v" + ChatColor.GREEN + NavyCraft.version + ChatColor.RED + " commands :");
								player.sendMessage(ChatColor.BLUE + "/shipyard player <player> - View a players plot status");
								player.sendMessage(ChatColor.BLUE + "/shipyard reward <player> <type> <reason> - Rewards the specified plot type to the player");
							}
						} else if (split[1].equalsIgnoreCase("open")) {
							if (split.length == 3) {
								String typeString = split[2];

								Block tpBlock = null;
							for (PlotType pt :Shipyard.getPlots()) {
								if (typeString.equalsIgnoreCase(pt.name)) {
									tpBlock = NavyCraft_FileListener.findSignOpen(pt.name);
									break;
								}
							}

								if (tpBlock != null) {
									player.teleport(tpBlock.getLocation().add(0.5, 0.5, 0.5));
								} else {
									player.sendMessage(ChatColor.RED + "No open plots found!");
								}

							} else {
								player.sendMessage(ChatColor.YELLOW + "/shipyard open <plot type>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD +  "teleport to an unclaimed plot");
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
										wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager()
												.getPlugin("WorldGuard");
										if (wgp != null) {
											RegionManager regionManager = wgp
													.getRegionManager(plugin.getServer().getWorld("shipyard"));
											int x = foundSign.getX();
											int y = foundSign.getY();
											int z = foundSign.getZ();
											World world = foundSign.getWorld();
											String regionName = "--" + player.getName() + "-" +  NavyCraft_FileListener.getSign(x, y, z, world);

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
								player.sendMessage(ChatColor.YELLOW + "/shipyard info <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "gives information on given plot ID");
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
										wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager()
												.getPlugin("WorldGuard");
										if (wgp != null) {
											RegionManager regionManager = wgp
													.getRegionManager(plugin.getServer().getWorld("shipyard"));
											int x = foundSign.getX();
											int y = foundSign.getY();
											int z = foundSign.getZ();
											World world = foundSign.getWorld();
											String regionName = "--" + player.getName() + "-" +  NavyCraft_FileListener.getSign(x, y, z, world);

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
								player.sendMessage(ChatColor.YELLOW + "/shipyard addmember <id> <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "gives player permission to that plot");
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
										wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager()
												.getPlugin("WorldGuard");
										if (wgp != null) {
											RegionManager regionManager = wgp
													.getRegionManager(plugin.getServer().getWorld("shipyard"));
											int x = foundSign.getX();
											int y = foundSign.getY();
											int z = foundSign.getZ();
											World world = foundSign.getWorld();
											String regionName = "--" + player.getName() + "-" +  NavyCraft_FileListener.getSign(x, y, z, world);

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
										wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager()
												.getPlugin("WorldGuard");
										if (wgp != null) {
											RegionManager regionManager = wgp
													.getRegionManager(plugin.getServer().getWorld("shipyard"));
											int x1 = foundSign.getX();
											int y1 = foundSign.getY();
											int z1 = foundSign.getZ();
											World world = foundSign.getWorld();
											String regionName = "--" + player.getName() + "-" +  NavyCraft_FileListener.getSign(x1, y1, z1, world);

											int startX = regionManager.getRegion(regionName).getMinimumPoint().getBlockX();
											int endX = regionManager.getRegion(regionName).getMaximumPoint().getBlockX();
											int startZ = regionManager.getRegion(regionName).getMinimumPoint().getBlockZ();
											int endZ = regionManager.getRegion(regionName).getMaximumPoint().getBlockZ();
											int startY = regionManager.getRegion(regionName).getMinimumPoint().getBlockY();
											int endY = regionManager.getRegion(regionName).getMaximumPoint().getBlockY();

											for (int x = startX; x <= endX; x++) {
												for (int z = startZ; z <= endZ; z++) {
													for (int y = startY; y <= 62; y++) {
														plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z)
																.setType(Material.AIR);

													}
													int startYy;
													if (startY > 63) {
														startYy = startY;
													} else {
														startYy = 63;
													}
													for (int y = startYy; y <= endY; y++) {
														plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z)
																.setType(Material.AIR);
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
								player.sendMessage(ChatColor.YELLOW + "/shipyard clear <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "destroys all blocks within the plot" );
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
										Block foundBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(),
												foundSign.getY() - 1, foundSign.getZ() + 1);
										if (foundBlock2.getTypeId() != 68) {
											foundBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1,
													foundSign.getY() - 1, foundSign.getZ());
										}
										if (foundBlock2.getTypeId() == 68) {
											Sign foundSign2 = (Sign) foundBlock2.getState();
											
											wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager()
													.getPlugin("WorldGuard");
											if (wgp != null) {
												RegionManager regionManager = wgp
														.getRegionManager(plugin.getServer().getWorld("shipyard"));
												int x1 = foundSign.getX();
												int y1 = foundSign.getY();
												int z1 = foundSign.getZ();
												World world = foundSign.getWorld();
												String regionName = "--" + player.getName() + "-" +  NavyCraft_FileListener.getSign(x1, y1, z1, world);
												int startX = 0;
												int endX = 0;
												int startZ = 0;
												int endZ = 0;
												int startY = 0;
												int endY = 0;
											try {
												startX = regionManager.getRegion(regionName).getMinimumPoint().getBlockX();
												endX = regionManager.getRegion(regionName).getMaximumPoint().getBlockX();
												startZ = regionManager.getRegion(regionName).getMinimumPoint().getBlockZ();
												endZ = regionManager.getRegion(regionName).getMaximumPoint().getBlockZ();
												startY = regionManager.getRegion(regionName).getMinimumPoint().getBlockY();
												endY = regionManager.getRegion(regionName).getMaximumPoint().getBlockY();
												} catch(Exception e) {
												player.sendMessage(ChatColor.DARK_RED + "Your plots region is defined incorrectly, contact an admin!");
												event.setCancelled(true);
												return;
												}
												
												for (int x = startX; x <= endX; x++) {
													for (int z = startZ; z <= endZ; z++) {
														for (int y = startY; y <= 62; y++) {
															plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z)
																	.setType(Material.AIR);
															
														}
														int startYy;
														if (startY > 63) {
															startYy = startY;
														} else {
															startYy = 63;
														}
														for (int y = startYy; y <= endY; y++) {
															plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z)
																	.setType(Material.AIR);
														}
													}
												}
												regionManager.removeRegion(regionName);
												NavyCraft_FileListener.updateSign(null, foundSign2.getLine(3), foundSign.getX(), foundSign.getY(),foundSign.getZ(), foundSign.getWorld(), null, false);
												foundSign.setLine(0, "*Claim*");
												foundSign.setLine(1, "");
												foundSign.setLine(2, "");
												foundSign.setLine(3, "");
												foundSign.update();
												foundSign2.setLine(0, "Open");
												foundSign2.setLine(1, "1");
												foundSign2.setLine(2, "0");
												foundSign2.setLine(3, foundSign2.getLine(3).toUpperCase());
												foundSign2.update();
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
											player.sendMessage(
													ChatColor.RED + "Error: There may be a problem with your plot signs.");
										}
									} else {
										player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW
												+ "/shipyard list" + ChatColor.RED + " to see IDs");
									}
									
								} else {
									player.sendMessage(ChatColor.RED + "Invalid Plot ID");
								}
							} else {
								player.sendMessage(ChatColor.YELLOW + "/shipyard unclaim <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "destroys all blocks within the plot and unclaims it");
							}
							
						} else if (split[1].equalsIgnoreCase("aunclaim")) {
							if (!PermissionInterface.CheckPerm(player, "navycraft.aunclaim")) {
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
										Block foundBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(),
												foundSign.getY() - 1, foundSign.getZ() + 1);
										if (foundBlock2.getTypeId() != 68) {
											foundBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1,
													foundSign.getY() - 1, foundSign.getZ());
										}
										if (foundBlock2.getTypeId() == 68) {
											Sign foundSign2 = (Sign) foundBlock2.getState();
											
											wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager()
													.getPlugin("WorldGuard");
											if (wgp != null) {
												RegionManager regionManager = wgp
														.getRegionManager(plugin.getServer().getWorld("shipyard"));
												int x1 = foundSign.getX();
												int y1 = foundSign.getY();
												int z1 = foundSign.getZ();
												World world = foundSign.getWorld();
												String regionName = "--" + p + "-" +  NavyCraft_FileListener.getSign(x1, y1, z1, world);
												int startX = 0;
												int endX = 0;
												int startZ = 0;
												int endZ = 0;
												int startY = 0;
												int endY = 0;
											try {
												startX = regionManager.getRegion(regionName).getMinimumPoint().getBlockX();
												endX = regionManager.getRegion(regionName).getMaximumPoint().getBlockX();
												startZ = regionManager.getRegion(regionName).getMinimumPoint().getBlockZ();
												endZ = regionManager.getRegion(regionName).getMaximumPoint().getBlockZ();
												startY = regionManager.getRegion(regionName).getMinimumPoint().getBlockY();
												endY = regionManager.getRegion(regionName).getMaximumPoint().getBlockY();
												} catch(Exception e) {
												player.sendMessage(ChatColor.DARK_RED + "This plots region is defined incorrectly!");
												event.setCancelled(true);
												return;
												}
												for (int x = startX; x <= endX; x++) {
													for (int z = startZ; z <= endZ; z++) {
														for (int y = startY; y <= 62; y++) {
															plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z)
																	.setType(Material.AIR);
															
														}
														int startYy;
														if (startY > 63) {
															startYy = startY;
														} else {
															startYy = 63;
														}
														for (int y = startYy; y <= endY; y++) {
															plugin.getServer().getWorld("shipyard").getBlockAt(x, y, z)
																	.setType(Material.AIR);
														}
													}
												}
												regionManager.removeRegion(regionName);
												NavyCraft_FileListener.updateSign(null, foundSign2.getLine(3), foundSign.getX(), foundSign.getY(),foundSign.getZ(), foundSign.getWorld(), null, false);
												foundSign.setLine(0, "*Claim*");
												foundSign.setLine(1, "");
												foundSign.setLine(2, "");
												foundSign.setLine(3, "");
												foundSign.update();
												foundSign2.setLine(0, "Open");
												foundSign2.setLine(1, "1");
												foundSign2.setLine(2, "0");
												foundSign2.setLine(3, foundSign2.getLine(3).toUpperCase());
												foundSign2.update();
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
											player.sendMessage(
													ChatColor.RED + "Error: There may be a problem with the plots signs.");
										}
									} else {
										player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW
												+ "/shipyard list" + ChatColor.RED + " to see IDs");
									}
								} else {
									player.sendMessage(ChatColor.RED + "Invalid Plot ID");
								}
							} else {
								player.sendMessage(ChatColor.YELLOW + "/shipyard aunclaim <player> <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "destroys all blocks within the defined plot");
							}
						} else if (split[1].equalsIgnoreCase("schem") || split[1].equalsIgnoreCase("schematic")) {
						if (split.length > 2) {
							if (split[2].equalsIgnoreCase("save")) {
							if (split.length == 5) {
								int tpId = -1;
								try {
									tpId = Integer.parseInt(split[3]);
								} catch (NumberFormatException e) {
									player.sendMessage(ChatColor.RED + "Invalid Plot ID");
									event.setCancelled(true);
									return;
								}

								String nameString = split[4];
								
								if (tpId > -1) {
									NavyCraft_FileListener.loadSignData();
									NavyCraft_BlockListener.loadRewards(player.getName());

									Sign foundSign = null;
									foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

									if (foundSign != null) {
										wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager()
												.getPlugin("WorldGuard");
										if (wgp != null) {
											RegionManager regionManager = wgp
													.getRegionManager(plugin.getServer().getWorld("shipyard"));
											int x = foundSign.getX();
											int y = foundSign.getY();
											int z = foundSign.getZ();
											World world = foundSign.getWorld();
											String regionName = "--" + player.getName() + "-" +  NavyCraft_FileListener.getSign(x, y, z, world);

											ProtectedRegion region = regionManager.getRegion(regionName);
											
											Sign sign2 = (Sign) foundSign.getBlock().getRelative(BlockFace.DOWN, 1).getRelative(Utils.getBlockFace(foundSign.getBlock()), -1).getState();
											
											String name = player.getName() + "-" +  sign2.getLine(3).trim().toUpperCase();
											
											Utils.saveSchem(player, name, nameString, region, world);

											player.sendMessage(ChatColor.GREEN + "Plot Saved as " + ChatColor.DARK_GRAY + "[" + ChatColor.GOLD  + name + "-" + nameString + ".schematic" + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + ".");
										}
									} else {
										player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
									}

								} else {
									player.sendMessage(ChatColor.RED + "Invalid Plot ID");
								}
							} else {
								player.sendMessage(ChatColor.YELLOW + "/shipyard schem save <id> <name>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "saves the defined plot in a schematic" );
							}
						} else if (split[2].equalsIgnoreCase("load")) {
							if (split.length == 5) {
								int tpId = -1;
								try {
									tpId = Integer.parseInt(split[4]);
								} catch (NumberFormatException e) {
									player.sendMessage(ChatColor.RED + "Invalid Plot ID");
									event.setCancelled(true);
									return;
								}

								String nameString = split[3];
								
								if (tpId > -1) {
									NavyCraft_FileListener.loadSignData();
									NavyCraft_BlockListener.loadRewards(player.getName());

									Sign foundSign = null;
									foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);

									if (foundSign != null) {
										wgp = (WorldGuardPlugin) plugin.getServer().getPluginManager()
												.getPlugin("WorldGuard");
										if (wgp != null) {
											RegionManager regionManager = wgp
													.getRegionManager(plugin.getServer().getWorld("shipyard"));
											int x = foundSign.getX();
											int y = foundSign.getY();
											int z = foundSign.getZ();
											World world = foundSign.getWorld();
											String regionName = "--" + player.getName() + "-" +  NavyCraft_FileListener.getSign(x, y, z, world);

											ProtectedRegion region = regionManager.getRegion(regionName);
											
											Sign sign2 = (Sign) foundSign.getBlock().getRelative(BlockFace.DOWN, 1).getRelative(Utils.getBlockFace(foundSign.getBlock()), -1).getState();
											
											String name = player.getName() + "-" +  sign2.getLine(3).trim().toUpperCase() + "-" + nameString;
											Location loc = new Location(world, region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
											if (Utils.pasteSchem(name, loc)) {
											player.sendMessage(ChatColor.GREEN + "Plot loaded " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "ID: " + ChatColor.GOLD + tpId + ChatColor.DARK_GRAY + " - " + ChatColor.YELLOW + "Name: "+ ChatColor.GOLD + name + ".schematic" + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + ".");
											} else {
												player.sendMessage(ChatColor.RED + "Plot name doesn't exist in database!");
											}
										}
									} else {
										player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
									}

								} else {
									player.sendMessage(ChatColor.RED + "Invalid Plot ID");
								}
							} else {
								player.sendMessage(ChatColor.YELLOW + "/shipyard schem load <name> <id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "saves your plot in a schematic" );
							}
						} else if (split[2].equalsIgnoreCase("plist")) {
							if (split.length == 4) {
								String p = split[3];
								NavyCraft_FileListener.loadSignData();
								NavyCraft_BlockListener.loadRewards(p);
								player.sendMessage(ChatColor.AQUA + p + "'s" + " Shipyard Schematics:");
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "NAME" + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TYPE");
					            File dir = new File(NavyCraft.instance.getDataFolder(), "/schematics/");
									for (File f : dir.listFiles()) {
										String[] splits = f.getName().split("-");
										if (splits[0].equalsIgnoreCase(p)) {
										player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + f.getName() + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + splits[1]);
									}
								}
								event.setCancelled(true);
								return;
							} else {
								player.sendMessage(ChatColor.YELLOW + "/shipyard schem plist <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "list the given player's schematics");
								event.setCancelled(true);
								return;
							}
						} else if (split[2].equalsIgnoreCase("list")) {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());
							player.sendMessage(ChatColor.AQUA + "Your Shipyard Schematics:");
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "NAME" + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TYPE");
				            File dir = new File(NavyCraft.instance.getDataFolder(), "/schematics/");
								for (File f : dir.listFiles()) {
									String[] splits = f.getName().split("-");
									if (splits[0].equalsIgnoreCase(player.getName())) {
									player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + f.getName() + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + splits[1]);
								}
							}
							event.setCancelled(true);
							return;
						} else {
							player.sendMessage("Unknown command. Type \"/shipyard help\" for help.");
							event.setCancelled(true);
							return;
						}
							
						} else {
							NavyCraft_FileListener.loadSignData();
							NavyCraft_BlockListener.loadRewards(player.getName());
							player.sendMessage(ChatColor.AQUA + "Your Shipyard Schematics:");
							player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "NAME" + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TYPE");
				            File dir = new File(NavyCraft.instance.getDataFolder(), "/schematics/");
								for (File f : dir.listFiles()) {
									String[] splits = f.getName().split("-");
									if (splits[0].equalsIgnoreCase(player.getName())) {
									player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + f.getName() + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + splits[1]);
								}
							}
							event.setCancelled(true);
							return;
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
						} else if (split[1].equalsIgnoreCase("renumber")) {
							if (split.length > 3) {
								String UUID = Utils.getUUIDfromPlayer(player.getName());
								int tpId = -1;
								try {
									tpId = Integer.parseInt(split[2]);
								} catch (NumberFormatException e) {
									player.sendMessage(ChatColor.RED + "Invalid Plot ID");
									event.setCancelled(true);
									return;
								}

								int newId;
								try {
									newId = Integer.parseInt(split[3]);
								} catch (NumberFormatException e) {
									player.sendMessage(ChatColor.RED + "Invalid New ID");
									event.setCancelled(true);
									return;
								}

								if (tpId > -1 && newId > -1) {
									NavyCraft_FileListener.loadSignData();
									NavyCraft_BlockListener.loadRewards(player.getName());

									Sign foundSign = null;
									foundSign = NavyCraft_BlockListener.findSign(player.getName(), tpId);
									if (foundSign != null) {
									Block block = foundSign.getBlock();
									if (NavyCraft.playerSigns.containsKey(UUID)) {
										for (Plot p : NavyCraft.playerSigns.get(UUID)) {
											if (newId == NavyCraft.playerSignIndex.get(p.sign)) {
												player.sendMessage(ChatColor.RED + "ID already exists in database!");
												event.setCancelled(true);
												return;
											}
										}
									}
									BlockFace bf = null;
									if (block != null) {
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
								}
									Sign sign2 = (Sign) block.getRelative(BlockFace.DOWN, 1).getRelative(bf, -1).getState();
										sign2.setLine(2, String.valueOf(newId));
										sign2.update();
										NavyCraft_FileListener.updateSign(UUID, sign2.getLine(3), foundSign.getX(), foundSign.getY(), foundSign.getZ(), foundSign.getWorld(), newId, true);
										player.sendMessage(ChatColor.GREEN + "Plot renumbered.");
										NavyCraft_FileListener.loadSignData();
									} else {
										player.sendMessage(ChatColor.RED + "ID not found, use " + ChatColor.YELLOW + "/shipyard list" + ChatColor.RED + " to see IDs");
									}

								} else {
									player.sendMessage(ChatColor.RED + "Invalid Plot ID");
								}
							} else {
								player.sendMessage(ChatColor.YELLOW + "/shipyard renumber <old id> <new id>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "renames the plot");
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
										Block selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(),
												foundSign.getY() - 1, foundSign.getZ() + 1);
										if (selectSignBlock2.getTypeId() != 68) {
											selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1,
													foundSign.getY() - 1, foundSign.getZ());
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
										Block selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX(),
												foundSign.getY() - 1, foundSign.getZ() + 1);
										if (selectSignBlock2.getTypeId() != 68) {
											selectSignBlock2 = foundSign.getWorld().getBlockAt(foundSign.getX() + 1,
													foundSign.getY() - 1, foundSign.getZ());
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
								String UUID = Utils.getUUIDfromPlayer(p);
								NavyCraft_FileListener.loadSignData();
								NavyCraft_BlockListener.loadRewards(p);
								if (UUID != null) {
								player.sendMessage(ChatColor.AQUA + p + "'s Shipyard Plots:");
							for (PlotType pt : Shipyard.getPlots()) {
								int numPlots = 0;
								int numRewPlots = 0;
								if (NavyCraft.playerSigns.containsKey(UUID)) {
									for (Plot p1 : NavyCraft.playerSigns.get(UUID)) {
										if (p1.name.equalsIgnoreCase(pt.name))
											numPlots++;
									}
								}
								if (NavyCraft.playerRewards.containsKey(UUID)) {
									for (Reward r : NavyCraft.playerRewards.get(UUID)) {
										if (r.name.equalsIgnoreCase(pt.name)) {
											numRewPlots = r.amount;
										}
									}
								}
							if (numPlots > 0 || numRewPlots > 0) {
										player.sendMessage(ChatColor.GOLD + pt.name + ChatColor.DARK_GRAY + " [" +  ChatColor.GREEN + numPlots + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/"
												+ ChatColor.DARK_GRAY + "[" + ChatColor.RED + numRewPlots + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
							}
							}
							} else {
								player.sendMessage(ChatColor.RED + p + "has never joined the server!");
							}
							} else {
								player.sendMessage(ChatColor.YELLOW + "/shipyard player <playerName>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "View a players shipyard status");
							}
						} else if (split[1].equalsIgnoreCase("plist")) {
							if (split.length == 3) {
								String p = split[2];
								NavyCraft_FileListener.loadSignData();
								NavyCraft_BlockListener.loadRewards(p);
								String UUID = Utils.getUUIDfromPlayer(p);
								if (UUID != null) {
								player.sendMessage(ChatColor.AQUA + p + "'s" + " Shipyard Plots:");
								player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "ID" + ChatColor.DARK_GRAY + "]" + ChatColor.GOLD + " TYPE");
								
								if (NavyCraft.playerSigns.containsKey(UUID)) {
									for (Plot p1 : NavyCraft.playerSigns.get(UUID)) {
										player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + NavyCraft.playerSignIndex.get(p1.sign) + ChatColor.DARK_GRAY + "] " + ChatColor.GOLD + p1.name);
									}
								}
								event.setCancelled(true);
								return;	
							} else {
								player.sendMessage(ChatColor.RED + p + "has never joined the server!");
								event.setCancelled(true);
								return;
							}
							} else {
								player.sendMessage(ChatColor.YELLOW + "/shipyard plist <playerName>" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + "List the given player's plots");
								event.setCancelled(true);
								return;
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
										player.sendMessage(ChatColor.RED + "ID not found, Use:" + ChatColor.YELLOW + "/shipyard plist " + p + ChatColor.RED +  "to see IDs");
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
						String UUID = Utils.getUUIDfromPlayer(player.getName());
						player.sendMessage(ChatColor.AQUA + "Your Shipyard Plots:");
						for (PlotType pt : Shipyard.getPlots()) {
							int numPlots = 0;
							int numRewPlots = 0;
							if (NavyCraft.playerSigns.containsKey(UUID)) {
								for (Plot p1 : NavyCraft.playerSigns.get(UUID)) {
									if (p1.name.equalsIgnoreCase(pt.name))
										numPlots++;
								}
							}
							if (NavyCraft.playerRewards.containsKey(UUID)) {
								for (Reward r : NavyCraft.playerRewards.get(UUID)) {
									if (r.name.equalsIgnoreCase(pt.name)) {
										numRewPlots = r.amount;
									}
								}
							}
						if (numPlots > 0 || numRewPlots > 0) {
									player.sendMessage(ChatColor.GOLD + pt.name + ChatColor.DARK_GRAY + " [" +  ChatColor.GREEN + numPlots + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + "/"
											+ ChatColor.DARK_GRAY + "[" + ChatColor.RED + numRewPlots + ChatColor.DARK_GRAY + "]" + ChatColor.DARK_AQUA + " available");
						}
						}
					}
					event.setCancelled(true);
					return;	
				
				
				
			} else if (craftName.equalsIgnoreCase("sign")) {
				if (split.length == 2) {
					if (split[1].equalsIgnoreCase("undo")) {
						if (NavyCraft.playerLastBoughtSign.containsKey(player)) {
							if ((NavyCraft.playerLastBoughtSign.get(player).getTypeId() == 68)
									|| (NavyCraft.playerLastBoughtSign.get(player).getTypeId() == 63)) {
								Sign sign = (Sign) NavyCraft.playerLastBoughtSign.get(player).getState();
								String signString0 = sign.getLine(0).trim().toLowerCase();
								signString0 = signString0.replaceAll(ChatColor.BLUE.toString(), "");
								String signString1 = sign.getLine(1).trim().toLowerCase();
								signString1 = signString1.replaceAll(ChatColor.BLUE.toString(), "");
								String signString2 = sign.getLine(2).trim().toLowerCase();
								signString2 = signString2.replaceAll(ChatColor.BLUE.toString(), "");
								if (signString0.equalsIgnoreCase(NavyCraft.playerLastBoughtSignString0.get(player))
										&& signString1
												.equalsIgnoreCase(NavyCraft.playerLastBoughtSignString1.get(player))
										&& signString2
												.equalsIgnoreCase(NavyCraft.playerLastBoughtSignString2.get(player))) {
									NavyCraft.playerLastBoughtSign.get(player).setTypeId(0);
									Essentials ess;
									ess = (Essentials) plugin.getServer().getPluginManager().getPlugin("Essentials");
									if (ess == null) {
										player.sendMessage(ChatColor.RED + "Essentials Economy error");
										event.setCancelled(true);
										return;
									}
									player.sendMessage(ChatColor.RED + "Undoing sign and refunding player.");
									try {
										ess.getUser(player)
												.giveMoney(new BigDecimal(NavyCraft.playerLastBoughtCost.get(player)));
									} catch (MaxMoneyException e) {
										
										e.printStackTrace();
									}
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
						if (!PermissionInterface.CheckPerm(player, "navycraft.volume.engine") && !player.isOp()) {
							player.sendMessage(ChatColor.RED + "You do not have permission to set engine volume.");
							event.setCancelled(true);
							return;
						}
						if (split[2].equalsIgnoreCase("mute")) {
							float inValue = 0.0f;
							NavyCraft.playerEngineVolumes.put(player, inValue);
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
						if (!PermissionInterface.CheckPerm(player, "navycraft.volume.weapon") && !player.isOp()) {
							player.sendMessage(ChatColor.RED + "You do not have permission to set gun volume.");
							event.setCancelled(true);
							return;
						}
						if (split[2].equalsIgnoreCase("mute")) {
							float inValue = 0.0f;
							NavyCraft.playerWeaponVolumes.put(player, inValue);
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
						if (!PermissionInterface.CheckPerm(player, "navycraft.volume.other") && !player.isOp()) {
							player.sendMessage(ChatColor.RED + "You do not have permission to set other volume.");
							event.setCancelled(true);
							return;
						}
						if (split[2].equalsIgnoreCase("mute")) {
							float inValue = 0.0f;
							NavyCraft.playerOtherVolumes.put(player, inValue);
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
						if (!PermissionInterface.CheckPerm(player, "navycraft.volume.all") && !player.isOp()) {
							player.sendMessage(ChatColor.RED + "You do not have permission to set other volume.");
							event.setCancelled(true);
							return;
						}
						if (split[2].equalsIgnoreCase("mute")) {
							float inValue = 0.0f;
							NavyCraft.playerEngineVolumes.put(player, inValue);
							NavyCraft.playerWeaponVolumes.put(player, inValue);
							NavyCraft.playerOtherVolumes.put(player, inValue);
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
					player.sendMessage(ChatColor.GOLD + "Volume v" + ChatColor.GREEN + NavyCraft.version + ChatColor.GOLD
							+ " commands :");
					player.sendMessage(ChatColor.AQUA + "/volume - status message");
					player.sendMessage(ChatColor.AQUA + "/volume <type> <volume> - sets volume for type");
					player.sendMessage(ChatColor.AQUA + "/volume <type> mute - mutes volume");
					player.sendMessage(ChatColor.YELLOW + "Types: engine, weapons, other, all");
				}
				return;
			} else if (craftName.equalsIgnoreCase("explode")) {
				if (PermissionInterface.CheckPerm(player, "navycraft.explode")) {
					if (split.length == 2) {
						float inValue = 1.0f;
						try {
							inValue = Float.parseFloat(split[1]);
							if ((inValue >= 1) && (inValue <= 100.0f)) {
								NavyCraft.explosion((int)inValue, player.getLocation().getBlock(),false);
								Craft checkCraft=null;
								checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation(), player);
								if( checkCraft == null ) {
									checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(7,7,7).getLocation(), player);
									if( checkCraft == null ) {
										checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(-7,-7,-7).getLocation(), player);
										if( checkCraft == null ) {
											checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(3,-2,-3).getLocation(), player);
											if( checkCraft == null ) {
												checkCraft = NavyCraft.instance.entityListener.structureUpdate(player.getLocation().getBlock().getRelative(-3,2,3).getLocation(), player);
											}
										}
									}
								}
								
								if( checkCraft == null )
									player.sendMessage(ChatColor.GOLD + "Boom Level" + ChatColor.DARK_GRAY + "- " + ChatColor.GREEN + inValue);
								else
									player.sendMessage(ChatColor.GOLD + "Boom level " + ChatColor.DARK_GRAY + "- " + ChatColor.GREEN + inValue + ChatColor.GOLD + " done on " + ChatColor.GREEN + checkCraft.name);
							} else {
								player.sendMessage(ChatColor.RED + "Invalid explosion level, use a number from 1 to 100");
							}
						} catch (NumberFormatException e) {
							player.sendMessage(ChatColor.RED + "Invalid explosion level, use a number from 1 to 100");
						}
					} else {
						player.sendMessage(ChatColor.YELLOW + "/explode ###  number from 1-100");
					}
				}else {
					player.sendMessage(ChatColor.RED + "You do not have permission to use that.");
				}
				
				event.setCancelled(true);
				return;
			}else if (craftName.equalsIgnoreCase("explodesigns")) {
				if (PermissionInterface.CheckPerm(player, "navycraft.explodesigns")) {
					if (split.length == 2) {
						float inValue = 1.0f;
						try {
							inValue = Float.parseFloat(split[1]);
							if ((inValue >= 1) && (inValue <= 100.0f)) {
								NavyCraft.explosion((int)inValue, player.getLocation().getBlock(),true);
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
				}else{
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
						if (!PermissionInterface.CheckPerm(player, "navycraft.expview") && !player.isOp()) {
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
						if (!PermissionInterface.CheckPerm(player, "navycraft.expset") && !player.isOp()) {
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
						if (!PermissionInterface.CheckPerm(player, "navycraft.expadd") && !player.isOp()) {
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
						if (!PermissionInterface.CheckPerm(player, "navycraft.expremove") && !player.isOp()) {
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
				Craft checkCraft = Craft.getCraft(player.getLocation().getBlockX(), player.getLocation().getBlockY(),
						player.getLocation().getBlockZ());
				if (checkCraft != null) {

				} else {
					NavyCraft.instance.createCraft(player, craftType, (int) Math.floor(player.getLocation().getX()),(int) Math.floor(player.getLocation().getY() - 1),(int) Math.floor(player.getLocation().getZ()), name, player.getLocation().getYaw(), null);
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
					player.sendMessage(ChatColor.WHITE + "Invalid movement parameters. Please use " + ChatColor.AQUA
							+ "Move x y z " + ChatColor.WHITE
							+ " Where x, y, and z are whole numbers separated by spaces.");
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
				if( split.length > 2 )
				{
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

			} else if (split[1].equalsIgnoreCase("reload")
					&& (PermissionInterface.CheckPerm(player,  "navycraft.reload"))) {


				if (craft != null) {
					CraftMover cm = new CraftMover(craft, plugin);
					cm.reloadWeapons(player);
				} else {
					player.sendMessage(ChatColor.RED + "No vehicle detected.");
				}

				// }

				return true;

			}else if (split[1].equalsIgnoreCase("drive")
					&& (PermissionInterface.CheckPerm(player,  "navycraft.admindrive"))) {
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
					player.sendMessage(ChatColor.GOLD + "Using " + craft.blockCount + " of " + craftType.maxBlocks
							+ " blocks (minimum " + craftType.minBlocks + ").");

				} else {
					player.sendMessage(ChatColor.GOLD + Integer.toString(craftType.minBlocks) + "-"
							+ craftType.maxBlocks + " blocks.");
				}
				player.sendMessage(ChatColor.GOLD + "Max speed: " + craftType.maxSpeed);

				if (NavyCraft.instance.DebugMode) {
					player.sendMessage(ChatColor.GOLD + Integer.toString(craft.dataBlocks.size()) + " data Blocks, "
							+ craft.complexBlocks.size() + " complex Blocks, " + craft.engineBlocks.size()
							+ " engine Blocks," + craft.digBlockCount + " drill bits.");
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

			} else if (split[1].equalsIgnoreCase("command")
					&& (PermissionInterface.CheckPerm(player,  "navycraft.takeover"))) {
				Craft testCraft = Craft.getCraft(player.getLocation().getBlockX(), player.getLocation().getBlockY(),
						player.getLocation().getBlockZ());
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
						if (PermissionInterface.CheckPerm(player,  "navycraft.remove")) {
							craft.doRemove = true;
							if (player.getInventory().contains(Material.GOLD_SWORD)) {
								player.getInventory().remove(Material.GOLD_SWORD);
							}
							player.sendMessage(ChatColor.GREEN + "Vehicle Removed");
						} else {
							player.sendMessage(ChatColor.RED
									+ "You do not have permission for this command. Use: " + ChatColor.YELLOW + "/ship disable" + ChatColor.RED + "instead.");
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
							player.sendMessage(ChatColor.RED
									+ "You can only use that command in a repair dock within the safe dock area.");
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
						if (checkProtectedRegion(player, craft.getLocation()) || PermissionInterface.CheckPerm(player,  "navycraft.destroy")) {
							craft.doDestroy = true;
							if (player.getInventory().contains(Material.GOLD_SWORD)) {
								player.getInventory().remove(Material.GOLD_SWORD);
							}
							player.sendMessage(ChatColor.GREEN + "Vehicle Destroyed");
						} else {
							player.sendMessage(
									ChatColor.RED + "You can only use this command in a safe dock region.");
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
								player.sendMessage(ChatColor.RED + "Your vehicle will be scuttled in 1 minute.");
							} else {
								player.sendMessage(
										ChatColor.RED + "This command cannot be used within a protected region.");
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
						
						for( Entity e : ents )
						{
							if( e instanceof Player )
							{
								Player p = (Player)e;
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
									if (!NavyCraft.shipTPCooldowns.containsKey(s) || (System
											.currentTimeMillis() > (NavyCraft.shipTPCooldowns.get(s) + 60000))) {
										NavyCraft.shipTPCooldowns.put(s, System.currentTimeMillis());
										p.teleport(new Location(craft.world, craft.signLoc.getBlockX() + 0.5,
												craft.signLoc.getBlockY() + 0.1, craft.signLoc.getBlockZ() + 0.5));
									} else {
										int timeLeft = (int) (((NavyCraft.shipTPCooldowns.get(s) + 60000)
												- System.currentTimeMillis()) / 60000);
										player.sendMessage(ChatColor.RED + "Player, " + s + " is on cooldown for "
												+ timeLeft + " minute");
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
						if (!NavyCraft.shipTPCooldowns.containsKey(player.getName()) || (System
								.currentTimeMillis() > (NavyCraft.shipTPCooldowns.get(player.getName()) + 60000))) {
							NavyCraft.shipTPCooldowns.put(player.getName(), System.currentTimeMillis());
							player.teleport(new Location(craft.world, craft.signLoc.getBlockX() + 0.5,
									craft.signLoc.getBlockY() + 0.1, craft.signLoc.getBlockZ() + 0.5));
						} else {
							int timeLeft = (int) (((NavyCraft.shipTPCooldowns.get(player.getName()) + 60000)
									- System.currentTimeMillis()) / 60000);
							player.sendMessage(ChatColor.RED + "You are on cooldown for " + timeLeft + " min");
						}
					} else {
						player.sendMessage(ChatColor.RED + "Vehicle sign not located.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You are not on a crew.");
				}
				return true;
			} else if (split[1].equalsIgnoreCase("buoy") && PermissionInterface.CheckPerm(player,  "navycraft.buoy")) {
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

						player.sendMessage("Block Displacement = " + craft.blockDispValue
								+ ", use /ship buoy block <value> to set");
						player.sendMessage(
								"Air Displacement = " + craft.airDispValue + ", use /ship buoy air <value> to set");
						player.sendMessage("Minimum Displacement = " + craft.minDispValue
								+ ", use /ship buoy min <value> to set");
						player.sendMessage("Weight Multiplier = " + craft.weightMult
								+ ", use /ship buoy weight <value> to set");
					}
				}
				return true;
			} else if (split[1].equalsIgnoreCase("help")) {
				if( PermissionInterface.CheckPerm(player, "navycraft.basic") ){
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
			player.sendMessage(ChatColor.GOLD + "Displacement : " + ChatColor.WHITE + craft.displacement + " tons ("
					+ craft.blockDisplacement + " block," + craft.airDisplacement + " air)");
			player.sendMessage(ChatColor.GOLD + "Health : " + ChatColor.WHITE
					+ (int) (((float) craft.blockCount * 100) / craft.blockCountStart) + "%");
			player.sendMessage(ChatColor.GOLD + "Engines : " + ChatColor.WHITE + craft.engineIDLocs.size() + " of "
					+ craft.engineIDIsOn.size());
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
				event.getPlayer().sendMessage(ChatColor.RED + "No Bullets Allowed In Dock Area");
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

						TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld().spawnEntity(
								new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(), eggBlock.getZ()),
								org.bukkit.entity.EntityType.PRIMED_TNT);
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

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld()
									.spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(),
											eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
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

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld()
									.spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(),
											eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
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

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld()
									.spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(),
											eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
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

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld()
									.spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(),
											eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
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

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld()
									.spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(),
											eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
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

							TNTPrimed tnt = (TNTPrimed) eggBlock.getWorld()
									.spawnEntity(new Location(eggBlock.getWorld(), eggBlock.getX(), eggBlock.getY(),
											eggBlock.getZ()), org.bukkit.entity.EntityType.PRIMED_TNT);
							tnt.setFuseTicks(fuseDelay);
							fuseDelay = fuseDelay + 2;
						}
					}
				}

				event.getPlayer().getWorld().playEffect(egg.getLocation(), Effect.SMOKE, 0);
				// event.getPlayer().getWorld().playEffect(egg.getLocation(),
				// Effect.CLICK1, 0);
				CraftMover.playWeaponSound(egg.getLocation(), Sound.ENTITY_BLAZE_HURT, 1.0f, 1.00f);

				Craft otherCraft = Craft.getOtherCraft(null, event.getPlayer(), egg.getLocation().getBlockX(),
						egg.getLocation().getBlockY(), egg.getLocation().getBlockZ());
				if (otherCraft != null) {
					CraftMover cm = new CraftMover(otherCraft, plugin);
					cm.structureUpdate(event.getPlayer(), false);
				} else {
					otherCraft = Craft.getOtherCraft(null, event.getPlayer(),
							egg.getLocation().getBlock().getRelative(2, 1, 2).getX(),
							egg.getLocation().getBlock().getRelative(2, 1, 2).getY(),
							egg.getLocation().getBlock().getRelative(2, 1, 2).getZ());
					if (otherCraft != null) {
						CraftMover cm = new CraftMover(otherCraft, plugin);
						cm.structureUpdate(event.getPlayer(), false);
					} else {
						otherCraft = Craft.getOtherCraft(null, event.getPlayer(),
								egg.getLocation().getBlock().getRelative(-2, -1, -2).getX(),
								egg.getLocation().getBlock().getRelative(-2, -1, -2).getY(),
								egg.getLocation().getBlock().getRelative(-2, -1, -2).getZ());
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
				if (!Utils.CheckEnabledWorld(loc)) {
					return true;
				}
				RegionManager regionManager = wgp.getRegionManager(player.getWorld());

				ApplicableRegionSet set = regionManager.getApplicableRegions(loc);

				Iterator<ProtectedRegion> it = set.iterator();
				while (it.hasNext()) {
					String id = it.next().getId();
					String[] splits = id.split("_");
					if (splits.length == 2) {
						if (splits[1].equalsIgnoreCase("safedock") || splits[1].equalsIgnoreCase("red")
								|| splits[1].equalsIgnoreCase("blue")) {
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
	
	public static boolean checkAdminSign(Block adminSignCheck) {
		if (adminSignCheck.getTypeId() == 68 || adminSignCheck.getTypeId() == 63) {
			Sign sign = (Sign) adminSignCheck.getState();

			if ((sign.getLine(0) == null) || sign.getLine(0).trim().equals("")) {
				return false;
			}

			String signLine0 = sign.getLine(0).trim().toLowerCase();

			// remove colors
			signLine0 = signLine0.replaceAll(ChatColor.BLUE.toString(), "");

			// remove brackets
			if (signLine0.startsWith("[") || signLine0.startsWith("*")) {
				signLine0 = signLine0.substring(1, signLine0.length() - 1);
			}

			if (signLine0.equalsIgnoreCase("Claim") || signLine0.equalsIgnoreCase("Select")) {
				return true;
			}
		}
		return false;
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
					sleep(60000);
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

