package com.tranzistor.tranzistio.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tranzistor.tranzistio.init.RecipesTypeInit;

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
import net.minecraftforge.registries.ForgeRegistryEntry;

public class CrusherRecipe implements ICrusherRecipe {
	private final ResourceLocation id;
	private final ItemStack result;
	private final NonNullList<Ingredient> recipeItems;
	private final int crushingTime;
	private final int count;
	
	public CrusherRecipe(ResourceLocation id, ItemStack result, NonNullList<Ingredient> recipeItems, int crushingTime, int count) {
		this.id = id;
		this.result = result;
		this.recipeItems = recipeItems;
		this.crushingTime = crushingTime;
		this.count = count;
	}

	@Override
	public boolean matches(IInventory inv, World world) {
		if(recipeItems.get(0).test(inv.getItem(0))) {
			return recipeItems.get(1).test(inv.getItem(1));
		}
		return false;
	}

	@Override
	public ItemStack assemble(IInventory inv) {
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
	
	public int getCrushingTime() {
		return this.crushingTime;
	}
	
	public int getCount() {
		return this.count;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return RecipesTypeInit.CRUSHER_SERIALIZER.get();
	}
	
	public static class CrushingRecipeType implements IRecipeType<CrusherRecipe>{
		@Override
		public String toString() {
			return CrusherRecipe.TYPE_ID.toString();
		}
	}
	
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CrusherRecipe>{

		@Override
		public CrusherRecipe fromJson(ResourceLocation id, JsonObject json) {
			ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
			int crushingTime = JSONUtils.getAsInt(json, "crushingtime");
			int count = JSONUtils.getAsInt(json, "count");
			JsonArray ingredients = JSONUtils.getAsJsonArray(json, "ingredients");
			NonNullList<Ingredient> input = NonNullList.withSize(1, Ingredient.EMPTY);
			input.set(0, Ingredient.fromJson(ingredients.get(0)));
			
			return new CrusherRecipe(id, result, input, crushingTime, count);
		}

		@Override
		public CrusherRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
			NonNullList<Ingredient> input = NonNullList.withSize(1, Ingredient.EMPTY);
			input.set(0, Ingredient.fromNetwork(buffer));
			ItemStack result = buffer.readItem();
			return new CrusherRecipe(id, result, input, 0, 0);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, CrusherRecipe recipe) {
			buffer.writeInt(recipe.getIngredients().size());
			Ingredient ing = recipe.getIngredients().get(0);
			ing.toNetwork(buffer);
			buffer.writeItemStack(recipe.getResultItem(), false);
		}
		
	}
	

}
