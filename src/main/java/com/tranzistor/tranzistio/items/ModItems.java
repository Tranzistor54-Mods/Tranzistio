package com.tranzistor.tranzistio.items;

import com.tranzistor.tranzistio.Tranzistio;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;

public class ModItems {
	
	public static class CraftDamageable extends Item {
		public CraftDamageable(Properties properties) {
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
	
	public static class ModFuelItems extends Item {
		private final int burnTime; 
		public ModFuelItems(int burnTime) {
			super(new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP));
			this.burnTime = burnTime;
		}
		
		@Override
		public int getBurnTime(ItemStack itemStack, IRecipeType<?> recipeType) {
			return this.burnTime;
		}
	}
}
