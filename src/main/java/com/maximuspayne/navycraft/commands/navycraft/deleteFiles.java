package com.maximuspayne.navycraft.commands.navycraft;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.PermissionInterface;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;

public class deleteFiles {

    public static void call(Player player, String[] split) {
        if( !PermissionInterface.CheckPerm(player, "navycraft.cannons") )
            return;
        player.sendMessage(ChatColor.GREEN + "Deleting files containing " + split[2]);
        WorldEditPlugin wep = (WorldEditPlugin) NavyCraft.instance.getServer().getPluginManager().getPlugin("WorldEdit");
        if (wep == null) {
            return;
        }
        File dir = new File(wep.getConfig().getString("saving.dir"));
        for (File f : dir.listFiles()) {
            String delString = split[2].toUpperCase();
            if (f.getName().contains(delString)) {
                System.out.println("Deleting " + f.getName());
                f.delete();
            }
        }
        return;
    }

}
