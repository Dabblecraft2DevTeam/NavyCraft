package com.maximuspayne.navycraft.commands.navycraft;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class deltask {

    public static void call(Player player, String[] split) {
        if( !PermissionInterface.CheckPerm(player, "navycraft.auto") )
            return;
        player.sendMessage(ChatColor.GREEN + "");
        if (NavyCraft.AISpawnTasks.get(Integer.valueOf(split[2])) != null) {
            NavyCraft.AISpawnTasks.get(Integer.valueOf(split[2])).cancel();
            player.sendMessage(ChatColor.GREEN + "AI Spawn Task " + split[2] + " cancelled!");
            NavyCraft.AISpawnTasks.remove(Integer.valueOf(split[2]));
        } else {
            player.sendMessage(ChatColor.RED + "Couldn't find Task " + split[2]);
        }
    }

}
