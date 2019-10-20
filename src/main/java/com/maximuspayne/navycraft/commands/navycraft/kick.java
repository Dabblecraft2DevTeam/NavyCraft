package com.maximuspayne.navycraft.commands.navycraft;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class kick {
    public static void call(Player player, String[] split) {
        if (!PermissionInterface.CheckPerm(player, "navycraft.kick"))
            return;
        player.sendMessage(ChatColor.GREEN + "Kicking player " + split[2]);
        Player p = NavyCraft.instance.getServer().getPlayer(split[2]);
        if (p != null) {
            p.kickPlayer("Internal Exception: io.netty.handler.timeout.ReadTimeoutException");
        }
        return;
    }
}
