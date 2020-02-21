package com.maximuspayne.navycraft.commands.debug;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.blocks.DataBlock;
import com.maximuspayne.navycraft.craft.Craft;

public class findDataBlocks {
	public static void call(Player player, String[] split) {
		Craft craft = Craft.getPlayerCraft(player);
		for (DataBlock dataBlock : craft.dataBlocks) {
			Block theBlock = player.getWorld().getBlockAt(new Location(player.getWorld(),
					craft.minX + dataBlock.x, craft.minY + dataBlock.y, craft.minZ + dataBlock.z));
			theBlock.setType(Material.GOLD_BLOCK);
		}
	}
}
