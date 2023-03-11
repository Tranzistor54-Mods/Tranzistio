package com.tranzistor.tranzistio.recipes;

import com.tranzistor.tranzistio.Tranzistio;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public interface IModRecipes {
	
	public static interface IFluidFillerRecipe extends IRecipe<IInventory> {
		ResourceLocation TYPE_ID = new ResourceLocation(Tranzistio.MOD_ID, "fluid_filling");
		
		@Override
		default IRecipeType<?> getType() {
			return Registry.RECIPE_TYPE.getOptional(TYPE_ID).get();
		}
		
		@Override
		default boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
			return true;
		}
		
		@Override
		default boolean isSpecial() {
			return true;
		}
	}
	
	public static interface ICrusherRecipe extends IRecipe<IInventory> {
		ResourceLocation TYPE_ID = new ResourceLocation(Tranzistio.MOD_ID, "crushing");
		
		@Override
		default IRecipeType<?> getType() {
			return Registry.RECIPE_TYPE.getOptional(TYPE_ID).get();
		}
		
		@Override
		default boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
			return true;
		}
		
		@Override
		default boolean isSpecial() {
			return true;
		}
	}
	
	public static interface ICokeOvenRecipe extends IRecipe<IInventory> {
		ResourceLocation TYPE_ID = new ResourceLocation(Tranzistio.MOD_ID, "coking");
		
		@Override
		default IRecipeType<?> getType() {
			return Registry.RECIPE_TYPE.getOptional(TYPE_ID).get();
		}
		
		@Override
		default boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
			return true;
		}
		
		@Override
		default boolean isSpecial() {
			return true;
		}
	}
}
