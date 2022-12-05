package com.tranzistor.tranzistio.core.advancedItems;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Hammer extends Item {

	public Hammer(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		ItemStack stack = itemStack.copy();
		if(stack.hurt(1, random, null)) {
			return ItemStack.EMPTY;
		}
		else {
			return stack;
		}
	}
	
}
