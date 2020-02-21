package com.maximuspayne.navycraft.commands.debug;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.craft.Craft;

public class diamondit {
	public static void call(Player player, String[] split) {
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
	}
}
