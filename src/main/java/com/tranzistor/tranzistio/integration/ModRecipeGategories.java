package com.tranzistor.tranzistio.integration;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.init.BlockInit;
import com.tranzistor.tranzistio.recipes.CrusherRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ModRecipeGategories {
	public static class CrusherRecipeGategory implements IRecipeCategory<CrusherRecipe> {
		public static final ResourceLocation UID = new ResourceLocation(Tranzistio.MOD_ID, "crushing");
		public static final ResourceLocation TEXTURE = new ResourceLocation(Tranzistio.MOD_ID, "textures/gui/crusher_gui.png");
		
		private final IDrawable background;
		private final IDrawable icon;
		
		public CrusherRecipeGategory(IGuiHelper helper) {
			this.background = helper.createDrawable(TEXTURE, 0, 0, 175, 81);
			this.icon = helper.createDrawableIngredient(new ItemStack(BlockInit.CRUSHER.get()));
		}
		
		@Override
		public ResourceLocation getUid() {
			return UID;
		}
	
		@Override
		public Class<? extends CrusherRecipe> getRecipeClass() {
			return CrusherRecipe.class;
		}
	
		@Override
		public String getTitle() {
			return BlockInit.CRUSHER.get().getName().getString();
		}
	
		@Override
		public IDrawable getBackground() {
			return this.background;
		}
	
		@Override
		public IDrawable getIcon() {
			return this.icon;
		}
	
		@Override
		public void setIngredients(CrusherRecipe recipe, IIngredients ingredients) {
			ingredients.setInputIngredients(recipe.getIngredients());
			ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
		}
	
		@Override
		public void setRecipe(IRecipeLayout recipeLayout, CrusherRecipe recipe, IIngredients ingredients) {
			recipeLayout.getItemStacks().init(0, true, 58, 34);
			recipeLayout.getItemStacks().init(1, false, 107, 34);
			recipeLayout.getItemStacks().set(ingredients);
		}
	}
}
