package com.maximuspayne.navycraft.Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ThreadCommand {
    public void onEnable() {
        // Register our command "kit" (set an instance of your command class as executor)
    	if (cmd.getName().equalsIgnoreCase("basic"));
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // Here we need to give items to our player
        }

        // If the player (or console) uses our command correct, we can return true
        return true;
    }
}
