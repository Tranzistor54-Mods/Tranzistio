package com.tranzistor.tranzistio.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tranzistor.tranzistio.init.RecipesTypeInit;
import com.tranzistor.tranzistio.recipes.IModRecipes.ICokeOvenRecipe;
import com.tranzistor.tranzistio.util.ModFluidUtil;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class CokeOvenRecipe implements ICokeOvenRecipe {
	private final ResourceLocation id;
	private final ItemStack result;
	private final NonNullList<Ingredient> recipeItems;
	private final FluidStack fluid;
	private final int cokingTime;
	
	public CokeOvenRecipe(ResourceLocation id, ItemStack result, NonNullList<Ingredient> recipeItems, int cokingTime, FluidStack fluid) {
		this.id = id;
		this.result = result;
		this.recipeItems = recipeItems;
		this.cokingTime = cokingTime;
		this.fluid = fluid;
	}

	@Override
	public boolean matches(IInventory inv, World world) {
		if(recipeItems.get(0).test(inv.getItem(1))) {
			return recipeItems.get(0).test(inv.getItem(1));
		}
		return false;
	}

	@Override
	public ItemStack assemble(IInventory p_77572_1_) {
		return result;
	}

	@Override
	public ItemStack getResultItem() {
		return result.copy();
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}
	
	public FluidStack getFluid() {
		return fluid;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return recipeItems;
	}
	
	public int getCokingTime() {
		return cokingTime;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return RecipesTypeInit.COKE_OVEN_SERIALIZER.get();
	}
	
	public static class CokingRecipeType implements IRecipeType<CokeOvenRecipe> {
		@Override
		public String toString() {
			return CokeOvenRecipe.TYPE_ID.toString();
		}
	}
	
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CokeOvenRecipe> {

		@Override
		public CokeOvenRecipe fromJson(ResourceLocation id, JsonObject json) {
			ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
			int cokingTime = JSONUtils.getAsInt(json, "cokingtime");
			int count = JSONUtils.getAsInt(json, "count");
			result.setCount(count);
			FluidStack fluid = ModFluidUtil.readFluid(json.get("fluid").getAsJsonObject());
			JsonArray ingredient = JSONUtils.getAsJsonArray(json, "ingredient");
			NonNullList<Ingredient> input = NonNullList.withSize(1, Ingredient.EMPTY);
			input.set(0, Ingredient.fromJson(ingredient.get(0)));
			return new CokeOvenRecipe(id, result, input, cokingTime, fluid);
		}

		@Override
		public CokeOvenRecipe fromNetwork(ResourceLocation id,PacketBuffer buffer) {
			NonNullList<Ingredient> input = NonNullList.withSize(1, Ingredient.EMPTY);
			input.set(0, Ingredient.fromNetwork(buffer));
			ItemStack result = buffer.readItem();
			return new CokeOvenRecipe(id, result, input, 0, null);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, CokeOvenRecipe recipe) {
			Ingredient ing = recipe.getIngredients().get(0);
			ing.toNetwork(buffer);
			buffer.writeItemStack(recipe.getResultItem(), false);	
		}
	}
}
