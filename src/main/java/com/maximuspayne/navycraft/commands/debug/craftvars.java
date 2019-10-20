package com.maximuspayne.navycraft.commands.debug;

import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.NavyCraft;
import com.maximuspayne.navycraft.craft.Craft;

public class craftvars {
	public static void call(Player player, String[] split) {
		Craft craft = Craft.getPlayerCraft(player);

		NavyCraft.instance.DebugMessage("Craft type: " + craft.type, 4);
		NavyCraft.instance.DebugMessage("Craft name: " + craft.name, 4);

		// may need to make multidimensional
		NavyCraft.instance.DebugMessage("Craft matrix size: " + craft.matrix.length, 4);
		NavyCraft.instance.DebugMessage("Craft block count: " + craft.blockCount, 4);
		NavyCraft.instance.DebugMessage("Craft data block count: " + craft.dataBlocks.size(), 4);
		NavyCraft.instance.DebugMessage("Craft complex block count: " + craft.complexBlocks.size(), 4);

		NavyCraft.instance.DebugMessage("Craft speed: " + craft.speed, 4);
		NavyCraft.instance
				.DebugMessage("Craft size: " + craft.sizeX + " * " + craft.sizeY + " * " + craft.sizeZ, 4);

		NavyCraft.instance.DebugMessage("Craft last move: " + craft.lastMove, 4);
		// world?
		NavyCraft.instance.DebugMessage("Craft center: " + craft.centerX + ", " + craft.centerZ, 4);

		NavyCraft.instance.DebugMessage("Craft water level: " + craft.waterLevel, 4);
		NavyCraft.instance.DebugMessage("Craft new water level: " + craft.newWaterLevel, 4);
		NavyCraft.instance.DebugMessage("Craft water type: " + craft.waterType, 4);

		NavyCraft.instance.DebugMessage("Craft bounds: " + craft.minX + "->" + craft.maxX + ", " + craft.minY
				+ "->" + craft.maxY + ", " + craft.minZ + "->" + craft.maxZ, 4);
	}
}
