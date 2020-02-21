package com.maximuspayne.navycraft.commands.debug;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.blocks.BlocksInfo;

public class getRotation {
	@SuppressWarnings("deprecation")
	public static void call(Player player, String[] split) {
		Set<Material> meh = new HashSet<>();
		Block examineBlock = player.getTargetBlock(meh, 100);

		int blockDirection = BlocksInfo.getCardinalDirectionFromData(examineBlock.getTypeId(),examineBlock.getData());
		player.sendMessage("Block data is " + examineBlock.getData() + " direction is " + blockDirection);
	}
}
