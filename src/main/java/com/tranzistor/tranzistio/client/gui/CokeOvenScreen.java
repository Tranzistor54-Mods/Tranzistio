package com.tranzistor.tranzistio.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.containers.CokeOvenContainer;
import com.tranzistor.tranzistio.te.CombustionChamberTE;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
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
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
}
