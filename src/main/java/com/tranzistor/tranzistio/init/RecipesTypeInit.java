package com.tranzistor.tranzistio.init;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.recipes.CrusherRecipe;

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
	
	public static IRecipeType<CrusherRecipe> CRUSHING_RECIPE = new CrusherRecipe.CrushingRecipeType();
	
	public static void register(IEventBus bus) {
		RECIPE_SERIALIZER.register(bus);
		Registry.register(Registry.RECIPE_TYPE, CrusherRecipe.TYPE_ID, CRUSHING_RECIPE);
	}
}
