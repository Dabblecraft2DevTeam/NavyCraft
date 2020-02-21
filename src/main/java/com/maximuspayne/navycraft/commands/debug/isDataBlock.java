package com.maximuspayne.navycraft.commands.debug;

import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.blocks.BlocksInfo;

public class isDataBlock {

	public static void call(Player player, String[] split) {
		player.sendMessage(Boolean.toString(BlocksInfo.isDataBlock(Integer.parseInt(split[1]))));
	}
}
