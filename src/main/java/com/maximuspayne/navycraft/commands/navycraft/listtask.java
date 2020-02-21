package com.maximuspayne.navycraft.commands.navycraft;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class listtask {

    public static void call(Player player, String[] split) {
        if( !PermissionInterface.CheckPerm(player, "navycraft.auto") )
            return;
        player.sendMessage(ChatColor.GREEN + "AI Ship Tasks:");
        for (int i : NavyCraft.AISpawnTasks.keySet()) {
            player.sendMessage(ChatColor.YELLOW + "" + i + " - " + NavyCraft.AISpawnTasks.get(i).schemName);
        }
    }

}
