package com.tranzistor.tranzistio.init;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.recipes.CokeOvenRecipe;
import com.tranzistor.tranzistio.recipes.CrusherRecipe;
import com.tranzistor.tranzistio.recipes.FluidFillerRecipe;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RecipesTypeInit {
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Tranzistio.MOD_ID);
	
	public static final RegistryObject<CrusherRecipe.Serializer> CRUSHER_SERIALIZER = RECIPE_SERIALIZER.register("crushing", CrusherRecipe.Serializer::new);
	public static final RegistryObject<FluidFillerRecipe.Serializer> FLUID_FILLER_SERIALEZER = RECIPE_SERIALIZER.register("fluid_filling", FluidFillerRecipe.Serializer::new);
	public static final RegistryObject<CokeOvenRecipe.Serializer> COKE_OVEN_SERIALIZER = RECIPE_SERIALIZER.register("coking", CokeOvenRecipe.Serializer::new);
	
	public static IRecipeType<CrusherRecipe> CRUSHING_RECIPE = new CrusherRecipe.CrushingRecipeType();
	public static IRecipeType<FluidFillerRecipe> FLUID_FILLING_RECIPE = new FluidFillerRecipe.FluidFillingRecipeType();
	public static IRecipeType<CokeOvenRecipe> COKING_RECIPE = new CokeOvenRecipe.CokingRecipeType();
	
	public static void register(IEventBus bus) {
		RECIPE_SERIALIZER.register(bus);
		Registry.register(Registry.RECIPE_TYPE, CrusherRecipe.TYPE_ID, CRUSHING_RECIPE);
		Registry.register(Registry.RECIPE_TYPE, FluidFillerRecipe.TYPE_ID, FLUID_FILLING_RECIPE);
		Registry.register(Registry.RECIPE_TYPE, CokeOvenRecipe.TYPE_ID, COKING_RECIPE);
	}
}
