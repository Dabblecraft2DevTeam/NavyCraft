package com.maximuspayne.navycraft.listeners;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.craft.Craft;

@SuppressWarnings("deprecation")
public class NavyCraft_Timer extends BukkitRunnable {

	Plugin plugin;
	Timer timer;
	Craft craft;
	Player player;
	// public String state = "";
	public static HashMap<Player, NavyCraft_Timer> playerTimers = new HashMap<Player, NavyCraft_Timer>();
	public static HashMap<Craft, NavyCraft_Timer> takeoverTimers = new HashMap<Craft, NavyCraft_Timer>();
	public static HashMap<Craft, NavyCraft_Timer> abandonTimers = new HashMap<Craft, NavyCraft_Timer>();

	public NavyCraft_Timer(Plugin plug, int seconds, Craft vehicle, Player p, String state, boolean forward) {
		// toolkit = Toolkit.getDefaultToolkit();
		plugin = plug;
		craft = vehicle;
		player = p;
		timer = new Timer();
		if (state.equals("engineCheck")) {
			timer.scheduleAtFixedRate(new EngineTask(), 1000, 1000);
		} else if (state.equals("abandonCheck")) {
			timer.scheduleAtFixedRate(new ReleaseTask(), seconds * 1000, 60000);
		} else if (state.equals("takeoverCheck")) {
			timer.scheduleAtFixedRate(new TakeoverTask(), seconds * 1000, 60000);
		} else if (state.equals("takeoverCaptainCheck")) {
			timer.scheduleAtFixedRate(new TakeoverCaptainTask(), seconds * 1000, 60000);
		}
	}

	public void Destroy() {
		timer.cancel();
		craft = null;
	}

	class EngineTask extends TimerTask {
		@Override
		public void run() {
			if (craft == null) {
				timer.cancel();
			} else {
				craft.engineTick();
			}
			return;
		}
	}

	class ReleaseTask extends TimerTask {
		@Override
		public void run() {
			if ((craft != null) && craft.isNameOnBoard.containsKey(player.getName())) {
				if (!craft.isNameOnBoard.get(player.getName())) {
					releaseCraftSync();
				}

			}
			timer.cancel();
			return;
		}
	}

	public void releaseCraftSync() {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
		});
	}

	class TakeoverTask extends TimerTask {
		@Override
		public void run() {
			takeoverCraftSync();

			timer.cancel();
			return;
		}
	}

	public void takeoverCraftSync() {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			if (craft.abandoned && (player != null) && player.isOnline() && craft.isOnCraft(player, false)) {
				craft.releaseCraft();
				player.sendMessage(ChatColor.GREEN + "Vehicle released! Take command.");
			} else {
				player.sendMessage(ChatColor.RED + "Takeover failed.");
			}

		});
	}

	class TakeoverCaptainTask extends TimerTask {
		@Override
		public void run() {

			takeoverCaptainCraftSync();

			timer.cancel();
			return;
		}
	}

	public void takeoverCaptainCraftSync() {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			if (craft.captainAbandoned && (player != null) && player.isOnline() && craft.isOnCraft(player, false)) {
				craft.releaseCraft();
				player.sendMessage(ChatColor.GREEN + "Vehicle released! Take command.");
			} else {
				player.sendMessage(ChatColor.RED + "Takeover failed.");
			}

		});
	}

	// Shipyard initial Sign Loading
	public static void loadSHIP1() {
		NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, () -> {
			File shipyarddata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "shipyarddata");
			File c = new File(shipyarddata, File.separator + "config.yml");
			FileConfiguration syConfig = YamlConfiguration.loadConfiguration(c);
			World syworld = NavyCraft.instance.getServer().getWorld(syConfig.getString("SHIP1World"));
			int startX = syConfig.getInt("SHIP1SX");
			int endX = syConfig.getInt("SHIP1EX");
			int widthX = syConfig.getInt("SHIP1WX");
			int y = syConfig.getInt("SHIP1Y");
			int startZ = syConfig.getInt("SHIP1SZ");
			int endZ = syConfig.getInt("SHIP1EZ");
			int widthZ = syConfig.getInt("SHIP1WZ");
			for (int x = startX; x <= endX; x += widthX) {
				for (int z = startZ; z >= endZ; z -= widthZ) {
					if ((syworld.getBlockAt(x, y, z).getTypeId() == 63) && (syworld.getBlockAt(x, y - 1, z + 1).getTypeId() == 68)) {
						Block selectSignBlock = syworld.getBlockAt(x, y, z);
						Sign selectSign = (Sign) selectSignBlock.getState();
						String signLine0 = selectSign.getLine(0);

						if (signLine0.equalsIgnoreCase("*claim*")) {

							NavyCraft_FileListener.saveSign("SHIP1", "shipyard", x, y, z);

						}
					}
				}
			}
		});
	}

	public static void loadSHIP2() {
		NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, () -> {
			File shipyarddata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "shipyarddata");
			File c = new File(shipyarddata, File.separator + "config.yml");
			FileConfiguration syConfig = YamlConfiguration.loadConfiguration(c);
			World syworld = NavyCraft.instance.getServer().getWorld(syConfig.getString("SHIP2World"));
			int startX = syConfig.getInt("SHIP2SX");
			int endX = syConfig.getInt("SHIP2EX");
			int widthX = syConfig.getInt("SHIP2WX");
			int y = syConfig.getInt("SHIP2Y");
			int startZ = syConfig.getInt("SHIP2SZ");
			int endZ = syConfig.getInt("SHIP2EZ");
			int widthZ = syConfig.getInt("SHIP2WZ");
			for (int x = startX; x <= endX; x += widthX) {
				for (int z = startZ; z <= endZ; z += widthZ) {
					if ((syworld.getBlockAt(x, y, z).getTypeId() == 63) && (syworld.getBlockAt(x, y - 1, z + 1).getTypeId() == 68)) {
						Block selectSignBlock = syworld.getBlockAt(x, y, z);
						Sign selectSign = (Sign) selectSignBlock.getState();
						String signLine0 = selectSign.getLine(0);

						if (signLine0.equalsIgnoreCase("*claim*")) {
							NavyCraft_FileListener.saveSign("SHIP2", "shipyard", x, y, z);
						}
					}
				}
			}
		});
	}

	public static void loadSHIP3() {
		NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, () -> {
			File shipyarddata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "shipyarddata");
			File c = new File(shipyarddata, File.separator + "config.yml");
			FileConfiguration syConfig = YamlConfiguration.loadConfiguration(c);
			World syworld = NavyCraft.instance.getServer().getWorld(syConfig.getString("SHIP3World"));
			int startX = syConfig.getInt("SHIP3SX");
			int endX = syConfig.getInt("SHIP3EX");
			int widthX = syConfig.getInt("SHIP3WX");
			int y = syConfig.getInt("SHIP3Y");
			int startZ = syConfig.getInt("SHIP3SZ");
			int endZ = syConfig.getInt("SHIP3EZ");
			int widthZ = syConfig.getInt("SHIP3WZ");
			for (int x = startX; x >= endX; x -= widthX) {
				for (int z = startZ; z <= endZ; z += widthZ) {
					if ((syworld.getBlockAt(x, y, z).getTypeId() == 63) && (syworld.getBlockAt(x, y - 1, z + 1).getTypeId() == 68)) {
						Block selectSignBlock = syworld.getBlockAt(x, y, z);
						Sign selectSign = (Sign) selectSignBlock.getState();
						String signLine0 = selectSign.getLine(0);

						if (signLine0.equalsIgnoreCase("*claim*")) {
							NavyCraft_FileListener.saveSign("SHIP3", "shipyard", x, y, z);
						}
					}
				}
			}
		});
	}

	public static void loadSHIP4() {
		NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, () -> {
			File shipyarddata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "shipyarddata");
			File c = new File(shipyarddata, File.separator + "config.yml");
			FileConfiguration syConfig = YamlConfiguration.loadConfiguration(c);
			World syworld = NavyCraft.instance.getServer().getWorld(syConfig.getString("SHIP4World"));
			int startX = syConfig.getInt("SHIP4SX");
			int endX = syConfig.getInt("SHIP4EX");
			int widthX = syConfig.getInt("SHIP4WX");
			int y = syConfig.getInt("SHIP4Y");
			int startZ = syConfig.getInt("SHIP4SZ");
			int endZ = syConfig.getInt("SHIP4EZ");
			int widthZ = syConfig.getInt("SHIP4WZ");
			for (int x = startX; x >= endX; x -= widthX) {
				for (int z = startZ; z >= endZ; z -= widthZ) {
					if ((syworld.getBlockAt(x, y, z).getTypeId() == 63) && (syworld.getBlockAt(x, y - 1, z + 1).getTypeId() == 68)) {
						Block selectSignBlock = syworld.getBlockAt(x, y, z);
						Sign selectSign = (Sign) selectSignBlock.getState();
						String signLine0 = selectSign.getLine(0);

						if (signLine0.equalsIgnoreCase("*claim*")) {

							NavyCraft_FileListener.saveSign("SHIP4", "shipyard", x, y, z);
						}

					}
				}
			}
		});
	}

	public static void loadSHIP5() {
		NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, () -> {
			File shipyarddata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "shipyarddata");
			File c = new File(shipyarddata, File.separator + "config.yml");
			FileConfiguration syConfig = YamlConfiguration.loadConfiguration(c);
			World syworld = NavyCraft.instance.getServer().getWorld(syConfig.getString("SHIP5World"));
			int startX = syConfig.getInt("SHIP5SX");
			int endX = syConfig.getInt("SHIP5EX");
			int widthX = syConfig.getInt("SHIP5WX");
			int y = syConfig.getInt("SHIP5Y");
			int startZ = syConfig.getInt("SHIP5SZ");
			int endZ = syConfig.getInt("SHIP5EZ");
			int widthZ = syConfig.getInt("SHIP5WZ");
			for (int x = startX; x <= endX; x += widthX) {
				for (int z = startZ; z >= endZ; z -= widthZ) {
					if ((syworld.getBlockAt(x, y, z).getTypeId() == 63) && (syworld.getBlockAt(x, y - 1, z + 1).getTypeId() == 68)) {
						Block selectSignBlock = syworld.getBlockAt(x, y, z);
						Sign selectSign = (Sign) selectSignBlock.getState();
						String signLine0 = selectSign.getLine(0);

						if (signLine0.equalsIgnoreCase("*claim*")) {

							NavyCraft_FileListener.saveSign("SHIP5", "shipyard", x, y, z);
						}
					}
				}
			}
		});
	}

	public static void loadHANGAR1() {
		NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, () -> {
			File shipyarddata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "shipyarddata");
			File c = new File(shipyarddata, File.separator + "config.yml");
			FileConfiguration syConfig = YamlConfiguration.loadConfiguration(c);
			World syworld = NavyCraft.instance.getServer().getWorld(syConfig.getString("HANGAR1World"));
			int startX = syConfig.getInt("HANGAR1SX");
			int endX = syConfig.getInt("HANGAR1EX");
			int widthX = syConfig.getInt("HANGAR1WX");
			int y = syConfig.getInt("HANGAR1Y");
			int startZ = syConfig.getInt("HANGAR1SZ");
			int endZ = syConfig.getInt("HANGAR1EZ");
			int widthZ = syConfig.getInt("HANGAR1WZ");
			for (int x = startX; x >= endX; x -= widthX) {
				for (int z = startZ; z >= endZ; z -= widthZ) {
					if ((syworld.getBlockAt(x, y, z).getTypeId() == 63) && (syworld.getBlockAt(x + 1, y - 1, z).getTypeId() == 68)) {
						Block selectSignBlock = syworld.getBlockAt(x, y, z);
						Sign selectSign = (Sign) selectSignBlock.getState();
						String signLine0 = selectSign.getLine(0);

						if (signLine0.equalsIgnoreCase("*claim*")) {
							NavyCraft_FileListener.saveSign("HANGAR1", "shipyard", x, y, z);
						}
					}
				}
			}
		});
	}

	public static void loadHANGAR2() {
		NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, () -> {
			File shipyarddata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "shipyarddata");
			File c = new File(shipyarddata, File.separator + "config.yml");
			FileConfiguration syConfig = YamlConfiguration.loadConfiguration(c);
			World syworld = NavyCraft.instance.getServer().getWorld(syConfig.getString("HANGAR2World"));
			int startX = syConfig.getInt("HANGAR2SX");
			int endX = syConfig.getInt("HANGAR2EX");
			int widthX = syConfig.getInt("HANGAR2WX");
			int y = syConfig.getInt("HANGAR2Y");
			int startZ = syConfig.getInt("HANGAR2SZ");
			int endZ = syConfig.getInt("HANGAR2EZ");
			int widthZ = syConfig.getInt("HANGAR2WZ");
			for (int x = startX; x >= endX; x -= widthX) {
				for (int z = startZ; z >= endZ; z -= widthZ) {
					if ((syworld.getBlockAt(x, y, z).getTypeId() == 63) && (syworld.getBlockAt(x + 1, y - 1, z).getTypeId() == 68)) {
						Block selectSignBlock = syworld.getBlockAt(x, y, z);
						Sign selectSign = (Sign) selectSignBlock.getState();
						String signLine0 = selectSign.getLine(0);

						if (signLine0.equalsIgnoreCase("*claim*")) {
							NavyCraft_FileListener.saveSign("HANGAR2", "shipyard", x, y, z);
						}
					}
				}
			}
		});
	}

	public static void loadTANK1() {
		NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, () -> {
			File shipyarddata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "shipyarddata");
			File c = new File(shipyarddata, File.separator + "config.yml");
			FileConfiguration syConfig = YamlConfiguration.loadConfiguration(c);
			World syworld = NavyCraft.instance.getServer().getWorld(syConfig.getString("TANK1World"));
			int startX = syConfig.getInt("TANK1SX");
			int endX = syConfig.getInt("TANK1EX");
			int widthX = syConfig.getInt("TANK1WX");
			int y = syConfig.getInt("TANK1Y");
			int startZ = syConfig.getInt("TANK1SZ");
			int endZ = syConfig.getInt("TANK1EZ");
			int widthZ = syConfig.getInt("TANK1WZ");
			for (int x = startX; x <= endX; x += widthX) {
				for (int z = startZ; z >= endZ; z -= widthZ) {
					if ((syworld.getBlockAt(x, y, z).getTypeId() == 63) && (syworld.getBlockAt(x + 1, y - 1, z).getTypeId() == 68)) {
						Block selectSignBlock = syworld.getBlockAt(x, y, z);
						Sign selectSign = (Sign) selectSignBlock.getState();
						String signLine0 = selectSign.getLine(0);

						if (signLine0.equalsIgnoreCase("*claim*")) {
							NavyCraft_FileListener.saveSign("TANK1", "shipyard", x, y, z);
						}
					}
				}
			}
		});
	}

	public static void loadTANK2() {
		NavyCraft.instance.getServer().getScheduler().scheduleSyncDelayedTask(NavyCraft.instance, () -> {
			File shipyarddata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "shipyarddata");
			File c = new File(shipyarddata, File.separator + "config.yml");
			FileConfiguration syConfig = YamlConfiguration.loadConfiguration(c);
			World syworld = NavyCraft.instance.getServer().getWorld(syConfig.getString("TANK2World"));
			int startX = syConfig.getInt("TANK2SX");
			int endX = syConfig.getInt("TANK2EX");
			int widthX = syConfig.getInt("TANK2WX");
			int y = syConfig.getInt("TANK2Y");
			int startZ = syConfig.getInt("TANK2SZ");
			int endZ = syConfig.getInt("TANK2EZ");
			int widthZ = syConfig.getInt("TANK2WZ");
			for (int x = startX; x <= endX; x += widthX) {
				for (int z = startZ; z >= endZ; z -= widthZ) {
					if ((syworld.getBlockAt(x, y, z).getTypeId() == 63) && (syworld.getBlockAt(x + 1, y - 1, z).getTypeId() == 68)) {
						Block selectSignBlock = syworld.getBlockAt(x, y, z);
						Sign selectSign = (Sign) selectSignBlock.getState();
						String signLine0 = selectSign.getLine(0);

						if (signLine0.equalsIgnoreCase("*claim*")) {
							NavyCraft_FileListener.saveSign("TANK2", "shipyard", x, y, z);
						}
					}
				}
			}
		});
	}

	@Override
	public void run() {
	}
}
