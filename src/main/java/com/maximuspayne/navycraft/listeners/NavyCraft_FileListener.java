package com.maximuspayne.navycraft.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import com.maximuspayne.navycraft.ConfigManager;
import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.Utils;
import com.maximuspayne.shipyard.Plot;
import ru.tehkode.permissions.bukkit.PermissionsEx;

@SuppressWarnings("deprecation")
public class NavyCraft_FileListener implements Listener {
	
	public NavyCraft_FileListener(NavyCraft p) {
	}
	
	public static PermissionsEx pex;

	public static void loadSignData() {
		List<String> list = new ArrayList<String>(ConfigManager.syData.getConfigurationSection("Signs").getKeys(false));
		int size = list.size();
		NavyCraft.playerSigns.clear();
		if (size == 0) return;
		for (String num : list) {
			if (ConfigManager.syData.getString("Signs." + num + ".isClaimed").equalsIgnoreCase("true")) {
				String type = ConfigManager.syData.getString("Signs." + num + ".type");
				World world = NavyCraft.instance.getServer().getWorld(ConfigManager.syData.getString("Signs." + num + "." + "world"));
				int x = ConfigManager.syData.getInt("Signs." + num + ".x");
				int y = ConfigManager.syData.getInt("Signs." + num + ".y");
				int z = ConfigManager.syData.getInt("Signs." + num + ".z");
				int id = ConfigManager.syData.getInt("Signs." + num + ".id");
				String UUID = ConfigManager.syData.getString("Signs." + num + ".uuid");
				Block selectSignBlock = world.getBlockAt(x, y, z);
				if (selectSignBlock.getTypeId() == 63) {
				Sign selectSign = (Sign) selectSignBlock.getState();
				Plot plot = new Plot(type, selectSign);
				if (!NavyCraft.playerSigns.containsKey(UUID)) {
					NavyCraft.playerSigns.put(UUID, new ArrayList<Plot>());
					NavyCraft.playerSigns.get(UUID).add(plot);
					NavyCraft.playerSignIndex.put(selectSign, id);
				} else {
					NavyCraft.playerSigns.get(UUID).add(plot);
					NavyCraft.playerSignIndex.put(selectSign, id);
				}
			}
		}
	}
}

	public static Block findSignOpen(String type) {
		Block selectSignBlock = null;
		List<String> list = new ArrayList<String>(ConfigManager.syData.getConfigurationSection("Signs").getKeys(false));
		if (list.size() == 0) return selectSignBlock;
		for (String num : list) {
			if (ConfigManager.syData.getString("Signs." + num + ".isClaimed").equalsIgnoreCase("false")) {
				String ptype = ConfigManager.syData.getString("Signs." + num + "." + "type");
				World world = NavyCraft.instance.getServer().getWorld(ConfigManager.syData.getString("Signs." + num + "." + "world"));
				int x = ConfigManager.syData.getInt("Signs." + num + "." + "x");
				int y = ConfigManager.syData.getInt("Signs." + num + "." + "y");
				int z = ConfigManager.syData.getInt("Signs." + num + "." + "z");
				if (type.equalsIgnoreCase(ptype)) {
					selectSignBlock = world.getBlockAt(x, y, z);
					if (selectSignBlock.getTypeId() == 63) {
					break;
					}
				}
			}
		}
		return selectSignBlock;
	}
	
	public static String getSign(int x, int y, int z, World world) {
		List<String> list = new ArrayList<String>(ConfigManager.syData.getConfigurationSection("Signs").getKeys(false));
		for (String num : list) {
				World world1 = NavyCraft.instance.getServer().getWorld(ConfigManager.syData.getString("Signs." + num + "." + "world"));
				int x1 = ConfigManager.syData.getInt("Signs." + num + "." + "x");
				int y1 = ConfigManager.syData.getInt("Signs." + num + "." + "y");
				int z1 = ConfigManager.syData.getInt("Signs." + num + "." + "z");
				Location loc1 = new Location(world1, x1, y1, z1);
				Location loc = new Location(world, x, y, z);
				if (loc.equals(loc1)) {
					return num;
				}
		}
		return null;
	}
	
	public static void updateSign(String uuid, String type, int x, int y, int z, World world, Object id, boolean isClaimed) {
		NavyCraft.instance.DebugMessage("UUID =" + uuid, 3);
		Block selectSignBlock = world.getBlockAt(x, y, z);
		if (selectSignBlock.getTypeId() == 63) {
		String num = getSign(x, y, z, world);
		List<String> list = new ArrayList<String>(ConfigManager.syData.getConfigurationSection("Signs").getKeys(false));
		if (num == null && !list.isEmpty()) {
			num = String.valueOf(Integer.valueOf(list.get(list.size() - 1)) + 1);
		}
			ConfigManager.syData.set("Signs." + num + "." + "type", type.toUpperCase());
			ConfigManager.syData.set("Signs." + num + "." + "world", world.getName());
			ConfigManager.syData.set("Signs." + num + "." + "x", x);
			ConfigManager.syData.set("Signs." + num + "." + "y", y);
			ConfigManager.syData.set("Signs." + num + "." + "z", z);
			ConfigManager.syData.set("Signs." + num + "." + "isClaimed", isClaimed);
			ConfigManager.syData.set("Signs." + num + "." + "uuid", uuid);
			ConfigManager.syData.set("Signs." + num + "." + "id", id);
			ConfigManager.savesyData();
		}
}
	
	public static void loadPlayerData(String player) {
		String UUID = Utils.getUUIDfromPlayer(player);
		File userdata = new File(NavyCraft.instance.getServer().getPluginManager().getPlugin("NavyCraft").getDataFolder(), File.separator + "userdata");
		File f = new File(userdata, File.separator + UUID + ".yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
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
		for (String s : PermissionsEx.getUser(player).getPermissions(worldName)) {
			if (!s.contains("navycraft")) {
					if (!s.contains("rank")) {
						if (f != null) {
						PermissionsEx.getUser(player).addPermission("navycraft.rank." + playerData.getInt("exp"));
						f.delete();
						} else {
						PermissionsEx.getUser(player).addPermission("navycraft.rank.0");
						}
					}
			}
		}
	loadExperience(player);
}

	public static void loadExperience(String player) {
		int exp = 0;
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
		
		for (String s : PermissionsEx.getUser(player).getPermissions(worldName)) {
			if (s.contains("navycraft")) {
					if (s.contains("rank")) {
						String[] split = s.split("\\.");
						try {
						exp = Integer.parseInt(split[2]);
					} catch (Exception ex) {
						System.out.println("Invalid perm-" + s);
						break;
					}
				}
			}
		}
		NavyCraft.playerExp.put(player, exp);
	}
	
	public static void saveExperience(String player) {
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
		
		for (String s : PermissionsEx.getUser(player).getPermissions(worldName)) {
			if (s.contains("navycraft")) {
					if (s.contains("rank")) {
						try {
						PermissionsEx.getUser(player).removePermission(s);
					} catch (Exception ex) {
						System.out.println("Invalid perm-" + s);
						break;
					}
				}
			}
		}
		PermissionsEx.getUser(player).addPermission("navycraft.rank." + NavyCraft.playerExp.get(player));
	}

}