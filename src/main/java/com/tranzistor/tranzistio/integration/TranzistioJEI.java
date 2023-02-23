package com.tranzistor.tranzistio.integration;

import java.util.Objects;
import java.util.stream.Collectors;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.init.RecipesTypeInit;
import com.tranzistor.tranzistio.recipes.CrusherRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class TranzistioJEI implements IModPlugin {
	
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Tranzistio.MOD_ID, "jei_plugin");
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new ModRecipeGategories.CrusherRecipeGategory(registration.getJeiHelpers().getGuiHelper()));
	}
	
	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		@SuppressWarnings("resource")
		RecipeManager manager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
		registration.addRecipes(manager.getAllRecipesFor(RecipesTypeInit.CRUSHING_RECIPE).stream()
				.filter(r -> r instanceof CrusherRecipe)
				.collect(Collectors.toList()), 
				ModRecipeGategories.CrusherRecipeGategory.UID);
	}
	
}
