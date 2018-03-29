package com.maximuspayne.navycraft.blocks;

import org.bukkit.inventory.ItemStack;

public class DataBlock {
	public int id;
	public int x;
	public int y;
	public int z;
	public int data;
	public ItemStack[] items = new ItemStack[27];
	public String[] signLines = new String[4];

	public DataBlock(int id, int x, int y, int z, int data) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
	}
	
	public boolean locationMatches(int locX, int locY, int locZ) {
		if(locX == x && locY == y && locZ == z)
			return true;
		else
			return false;
	}
	
	@SuppressWarnings("deprecation")
	public void setItem(int slot, ItemStack origItem){
		//items[slot] = new ItemStack(itemType);
		if( slot >= 27 )
			return;
		items[slot] = new ItemStack(origItem.getTypeId());
		items[slot].setAmount(origItem.getAmount());
		items[slot].setData(origItem.getData());
		items[slot].setDurability(origItem.getDurability());
	}
}
