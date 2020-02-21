package com.maximuspayne.navycraft.commands.navycraft;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.craft.AISpawning;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class auto {

    public static void call(Player player, String[] split) {
        if( !PermissionInterface.CheckPerm(player, "navycraft.auto") )
            return;
        player.sendMessage(ChatColor.GREEN + "Starting merchant autospawn task!");
            int ticks1 = Integer.valueOf(split[2]) * 20;
            int ticks2 = Integer.valueOf(split[3]) * 20;
            String schemName = split[4];
            AISpawning aiSpawning = new AISpawning(schemName);
            aiSpawning.runTaskTimer(NavyCraft.instance, ticks1, ticks2);
        int id = 0;
        if (!NavyCraft.AISpawnTasks.isEmpty())
            id = NavyCraft.AISpawnTasks.size() + 1;
        if (!NavyCraft.AISpawnTasks.containsValue(aiSpawning))
            NavyCraft.AISpawnTasks.put(id, aiSpawning);
        }

}
