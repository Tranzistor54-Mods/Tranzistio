package com.tranzistor.tranzistio.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.core.containers.ElectricFurnaceContainer;
import com.tranzistor.tranzistio.core.te.ElectricFurnaceTileEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElectricFurnaceScreen extends ContainerScreen<ElectricFurnaceContainer> {
	
	private static final ResourceLocation ELECTRIC_FURNACE_SCREEN = new ResourceLocation(Tranzistio.MOD_ID, "textures/gui/electric_furnace.png");
	private final ElectricFurnaceTileEntity te;

	public ElectricFurnaceScreen(ElectricFurnaceContainer container, PlayerInventory playerInventory, ITextComponent component) {
		super(container, playerInventory, component);
		this.te = (ElectricFurnaceTileEntity) container.te;
		this.leftPos = 0;
		this.topPos = 0;
		this.imageWidth = 175;
		this.imageHeight = 201;
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		this.minecraft.textureManager.bind(ELECTRIC_FURNACE_SCREEN);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
		if(te.maxSmeltingProgress > 0) {
			this.blit(matrixStack, this.leftPos +  79, this.topPos + 35, 176, 14, (te.maxSmeltingProgress - te.smeltingProgress) * 24 / te.maxSmeltingProgress, 16);
		}
		int energyPixels = te.getEnergy() * 50 / te.getMaxEnergy();
		this.blit(matrixStack, this.leftPos + 163, this.topPos + 65 - energyPixels, 176, 82 - energyPixels, 7, energyPixels);
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTiks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTiks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}
	
	@Override
	protected void renderLabels(MatrixStack matrixstack, int x, int y) {
		this.font.draw(matrixstack, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
	}

}
