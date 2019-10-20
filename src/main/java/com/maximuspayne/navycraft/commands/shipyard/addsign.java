package com.maximuspayne.navycraft.commands.shipyard;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.maximuspayne.navycraft.PermissionInterface;
import com.maximuspayne.navycraft.listeners.NavyCraft_FileListener;

@SuppressWarnings("deprecation")
public class addsign {

	public static void call(Player player, String[] split) {
		if (!PermissionInterface.CheckPerm(player, "navycraft.addsign") && !player.isOp()) {
			player.sendMessage(ChatColor.RED + "You do not have permission to add signs.");
			return;
		}
		if (player.getTargetBlock(null, 5).getTypeId() == 63) {
				Block selectSignBlock = player.getTargetBlock(null, 5);
				Sign selectSign = (Sign) selectSignBlock.getState();
				BlockFace bf;
				bf = null;
				// bf2 = null;
				switch (selectSignBlock.getData()) {
					case (byte) 0x8:// n
						bf = BlockFace.SOUTH;
						// bf2 = BlockFace.NORTH;
						break;
					case (byte) 0x0:// s
						bf = BlockFace.NORTH;
						// bf2 = BlockFace.SOUTH;
						break;
					case (byte) 0x4:// w
						bf = BlockFace.EAST;
						// bf2 = BlockFace.WEST;
						break;
					case (byte) 0xC:// e
						bf = BlockFace.WEST;
						// bf2 = BlockFace.EAST;
						break;
					default:
						break;
				}

				if (bf == null) {
					player.sendMessage(ChatColor.DARK_RED + "Sign Error: Check Direction?");
					return;
				}
				if (selectSignBlock.getRelative(BlockFace.DOWN).getRelative(bf, -1).getTypeId() == 68) {
				Sign selectSign2 = (Sign) selectSignBlock.getRelative(BlockFace.DOWN).getRelative(bf, -1).getState();
				String signLine0 = selectSign.getLine(0);
				String sign2Line3 = selectSign2.getLine(3);
				
		if (signLine0.equalsIgnoreCase("*claim*")) {
			NavyCraft_FileListener.updateSign(null, selectSign2.getLine(3), selectSign.getX(), selectSign.getY(),selectSign.getZ(), selectSign.getWorld(), null, false);
		} else {
			player.sendMessage(ChatColor.RED + "That is not a valid shipyard sign! (Top sign isn't a claim sign)");
			return;
		}
		player.sendMessage(ChatColor.GREEN + "Loaded: " + ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW
				+ "1" + ChatColor.DARK_GRAY + " - " + ChatColor.GOLD + sign2Line3 + ChatColor.DARK_GRAY + "]" + ChatColor.GREEN + " plot");
		return;
				} else {
					player.sendMessage(ChatColor.RED + "That is not a valid shipyard sign! (Botom sign is null)");
					return;
				}
		} else {
			player.sendMessage(ChatColor.RED + "That is not a valid shipyard sign! (Top sign is null)");
			return;
		}
	}

}
