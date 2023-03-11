package com.tranzistor.tranzistio.recipes;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.tranzistor.tranzistio.init.RecipesTypeInit;
import com.tranzistor.tranzistio.recipes.IModRecipes.IFluidFillerRecipe;
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

public class FluidFillerRecipe implements IFluidFillerRecipe {
	
	private final ResourceLocation id;
	private final ItemStack result;
	private final FluidStack fluid;
	private final int workingTime;
	private final NonNullList<Ingredient> recipeItems;
	
	public FluidFillerRecipe(ResourceLocation id, ItemStack result, FluidStack fluid, int workingTime, NonNullList<Ingredient> recipeItems) {
		this.id = id;
		this.result = result;
		this.fluid = fluid;
		this.workingTime = workingTime;
		this.recipeItems = recipeItems;
	}
	
	@Override
	public boolean matches(IInventory inv, World world) {
		for(int i = 1; i <= 9; i++) {
			if(!recipeItems.get(i - 1).test(inv.getItem(i)))
				return false;
		}
		return true;
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
	
	public int getWorkingTime() {
		return workingTime;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return RecipesTypeInit.FLUID_FILLER_SERIALEZER.get();
	}
	
	private static NonNullList<Ingredient> dissolvePattern(String[] p_192402_0_, Map<String, Ingredient> p_192402_1_, int p_192402_2_, int p_192402_3_) {
	      NonNullList<Ingredient> nonnulllist = NonNullList.withSize(p_192402_2_ * p_192402_3_, Ingredient.EMPTY);
	      Set<String> set = Sets.newHashSet(p_192402_1_.keySet());
	      set.remove(" ");

	      for(int i = 0; i < p_192402_0_.length; ++i) {
	         for(int j = 0; j < p_192402_0_[i].length(); ++j) {
	            String s = p_192402_0_[i].substring(j, j + 1);
	            Ingredient ingredient = p_192402_1_.get(s);
	            if (ingredient == null) {
	               throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
	            }

	            set.remove(s);
	            nonnulllist.set(j + p_192402_2_ * i, ingredient);
	         }
	      }

	      if (!set.isEmpty()) {
	         throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
	      } else {
	         return nonnulllist;
	      }
	   }

	 
	   private static String[] shrink(String... p_194134_0_) {
	      int i = Integer.MAX_VALUE;
	      int j = 0;
	      int k = 0;
	      int l = 0;

	      for(int i1 = 0; i1 < p_194134_0_.length; ++i1) {
	         String s = p_194134_0_[i1];
	         i = Math.min(i, firstNonSpace(s));
	         int j1 = lastNonSpace(s);
	         j = Math.max(j, j1);
	         if (j1 < 0) {
	            if (k == i1) {
	               ++k;
	            }

	            ++l;
	         } else {
	            l = 0;
	         }
	      }

	      if (p_194134_0_.length == l) {
	         return new String[0];
	      } else {
	         String[] astring = new String[p_194134_0_.length - l - k];

	         for(int k1 = 0; k1 < astring.length; ++k1) {
	            astring[k1] = p_194134_0_[k1 + k].substring(i, j + 1);
	         }

	         return astring;
	      }
	   }

	   private static int firstNonSpace(String p_194135_0_) {
	      int i;
	      for(i = 0; i < p_194135_0_.length() && p_194135_0_.charAt(i) == ' '; ++i) {
	      }

	      return i;
	   }

	   private static int lastNonSpace(String p_194136_0_) {
	      int i;
	      for(i = p_194136_0_.length() - 1; i >= 0 && p_194136_0_.charAt(i) == ' '; --i) {
	      }

	      return i;
	   }

	   private static String[] patternFromJson(JsonArray p_192407_0_) {
	      String[] astring = new String[p_192407_0_.size()];
	      if (astring.length > 3) {
	         throw new JsonSyntaxException("Invalid pattern: too many rows, " + 3 + " is maximum");
	      } else if (astring.length == 0) {
	         throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
	      } else {
	         for(int i = 0; i < astring.length; ++i) {
	            String s = JSONUtils.convertToString(p_192407_0_.get(i), "pattern[" + i + "]");
	            if (s.length() > 3) {
	               throw new JsonSyntaxException("Invalid pattern: too many columns, " + 3 + " is maximum");
	            }

	            if (i > 0 && astring[0].length() != s.length()) {
	               throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
	            }

	            astring[i] = s;
	         }

	         return astring;
	      }
	   }

	   private static Map<String, Ingredient> keyFromJson(JsonObject p_192408_0_) {
	      Map<String, Ingredient> map = Maps.newHashMap();

	      for(Entry<String, JsonElement> entry : p_192408_0_.entrySet()) {
	         if (entry.getKey().length() != 1) {
	            throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
	         }

	         if (" ".equals(entry.getKey())) {
	            throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
	         }

	         map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
	      }

	      map.put(" ", Ingredient.EMPTY);
	      return map;
	   }
	
	public static class FluidFillingRecipeType implements IRecipeType<FluidFillerRecipe>{
		@Override
		public String toString() {
			return FluidFillerRecipe.TYPE_ID.toString();
		}
	}
	
	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FluidFillerRecipe> {

		@Override
		public FluidFillerRecipe fromJson(ResourceLocation id, JsonObject json) {
			ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
			FluidStack fluid = ModFluidUtil.readFluid(json.get("fluid").getAsJsonObject());
			int workingTime = JSONUtils.getAsInt(json, "workingtime");
			int countOfResult = JSONUtils.getAsInt(json, "count");
			result.setCount(countOfResult);
			Map<String, Ingredient> map = FluidFillerRecipe.keyFromJson(JSONUtils.getAsJsonObject(json, "key"));
			String[] astring = FluidFillerRecipe.shrink(FluidFillerRecipe.patternFromJson(JSONUtils.getAsJsonArray(json, "pattern")));
			int j = astring.length;
			int i = astring[0].length();
			NonNullList<Ingredient> ingredients = FluidFillerRecipe.dissolvePattern(astring, map, j, i);
			return new FluidFillerRecipe(id, result, fluid, workingTime, ingredients);
		}

		@Override
		public FluidFillerRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
			NonNullList<Ingredient> input = NonNullList.withSize(9, Ingredient.EMPTY);
			for(int i = 0; i < input.size(); i++) {
				input.set(i, Ingredient.fromNetwork(buffer));
			}
			ItemStack result = buffer.readItem();
			return new FluidFillerRecipe(id, result, null, 0, input);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, FluidFillerRecipe recipe) {
			for(Ingredient ing : recipe.recipeItems) {
				ing.toNetwork(buffer);
			}
			buffer.writeItem(recipe.result);
		}		
	}	
}
