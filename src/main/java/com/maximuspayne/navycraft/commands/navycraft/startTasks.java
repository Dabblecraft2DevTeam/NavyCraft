package com.maximuspayne.navycraft.commands.navycraft;

import com.maximuspayne.navycraft.ConfigManager;
import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.craft.AISpawning;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class startTasks {

    public static void call(Player player, String split[]) {
        if (ConfigManager.routeData.getConfigurationSection("Spawning") != null) {
            List<String> list = new ArrayList<String>(ConfigManager.routeData.getConfigurationSection("Spawning").getKeys(false));
            List<String> list2 = new ArrayList<String>(ConfigManager.routeData.getConfigurationSection("Routes").getKeys(false));
            HashMap<String, List<String>> list3 = new HashMap<>();
            //example: PANAMA, TUGPANAMA
            for (String i : list) {
                for (String a : list2) {
                    if (i.endsWith(a)) {
                        if (list3.get(a) != null) {
                            list3.get(a).add(i);
                            System.out.println(a + " added to " + i);
                        } else {
                            list3.put(a, new ArrayList<>());
                            list3.get(a).add(i);
                        }
                    }
                }
            }
            for (String i : list3.keySet()) {
                int maxNumber = list.size() * 1200;
                int currentNumber = 0;
                for (String s: list3.get(i)) {
                    AISpawning aiSpawning = new AISpawning(s);
                    aiSpawning.runTaskTimer(NavyCraft.instance, currentNumber*60, maxNumber*60);
                    int id = 0;
                    if (!NavyCraft.AISpawnTasks.isEmpty())
                        id = NavyCraft.AISpawnTasks.size() + 1;
                    if (!NavyCraft.AISpawnTasks.containsValue(aiSpawning))
                        NavyCraft.AISpawnTasks.put(id, aiSpawning);
                    currentNumber += 1200;
                }
            }
            }
        }
    }
