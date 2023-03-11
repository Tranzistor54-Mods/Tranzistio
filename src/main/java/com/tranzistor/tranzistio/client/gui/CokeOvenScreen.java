package com.tranzistor.tranzistio.client.gui;

import java.util.Arrays;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.containers.CokeOvenContainer;
import com.tranzistor.tranzistio.te.CombustionChamberTE;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
@OnlyIn(Dist.CLIENT)
public class CokeOvenScreen  extends ContainerScreen<CokeOvenContainer> {
	
	private static final ResourceLocation COKE_OVEN_SCREEN = new ResourceLocation(Tranzistio.MOD_ID, "textures/gui/coke_oven.png");
	private final CombustionChamberTE te;
	
	public CokeOvenScreen(CokeOvenContainer container, PlayerInventory playerInv, ITextComponent text) {
		super(container, playerInv, text);
		this.te = (CombustionChamberTE) container.te;
		this.leftPos = 0; 
		this.topPos = 0;
		this.imageWidth = 175;
		this.imageHeight = 201;
	}
	
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		this.minecraft.textureManager.bind(COKE_OVEN_SCREEN);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
		
		if(te.maxProgress > 0) 
			this.blit(matrixStack, this.leftPos + 76, this.topPos + 31, 176, 0, (te.maxProgress - te.progress) * 24 / te.maxProgress, 16);
		
		if(te.maxBurningTime > 0) {
			int k = (te.maxBurningTime - te.burningTime) * 18 / te.maxBurningTime;
			this.blit(matrixStack, this.leftPos + 30, this.topPos + 31 + k, 176, 24 + k, 17, 18 - k);
		}
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderFluid(matrixStack, te.getFluid());
		
		if(isHoveringFluid(mouseX, mouseY) && te.getFluid() != Fluids.EMPTY) {
			net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(matrixStack, Arrays.asList(new StringTextComponent(
					te.getFluid().getAttributes().getDisplayName(te.getFluidStorage().getFluid()).getString()+ ": " + te.getFluidStorage().getFluidAmount() + "/" + te.getFluidStorage().getCapacity() + " mB")), mouseX, mouseY, width, height, -1, font);
		}
	}
	
	protected void renderFluid(MatrixStack matrixStack, Fluid fluid) {
		int fluidPixels = te.getFluidStorage().getFluidAmount() * 2 / 1000;
		ResourceLocation fluidLoc = fluid.getAttributes().getStillTexture();
		TextureAtlasSprite fluidTexture = this.minecraft.getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(fluidLoc);
		setGLColorFromInt(fluid.getAttributes().getColor());
		this.minecraft.textureManager.bind(PlayerContainer.BLOCK_ATLAS);
		blit(matrixStack, this.leftPos + 145, this.topPos + 68 - fluidPixels, 1, 16, fluidPixels, fluidTexture);
	}
	
	protected boolean isHoveringFluid(int mouseX, int mouseY) {
		return mouseX > this.leftPos + 144 && mouseX < this.leftPos + 161 && mouseY > this.topPos + 35 && mouseY < this.topPos + 68;
	}
	
	@SuppressWarnings("deprecation")
	protected void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255F;

        RenderSystem.color4f(red, green, blue, alpha);
    }
}
