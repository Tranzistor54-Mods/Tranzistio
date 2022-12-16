package com.tranzistor.tranzistio.client.gui;

import java.util.Arrays;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.containers.CrusherContainer;
import com.tranzistor.tranzistio.te.CrusherTileEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

 @OnlyIn(Dist.CLIENT)
public class CrusherScreen extends ContainerScreen<CrusherContainer> {
	public static final ResourceLocation CRUSHER_SCREEN = new ResourceLocation(Tranzistio.MOD_ID, "textures/gui/crusher_gui.png");
	private final CrusherTileEntity te;
	public CrusherScreen(CrusherContainer container, PlayerInventory inv, ITextComponent text) {
		super(container, inv, text);
		this.te = (CrusherTileEntity) container.te;
		this.leftPos = 0;
		this.topPos = 0;
		this.imageWidth = 175;
		this.imageHeight = 201;
	}
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		this.minecraft.textureManager.bind(CRUSHER_SCREEN);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageWidth);
		if (te.maxCrushingProgress > 0)
            this.blit(matrixStack, this.leftPos + 79, this.topPos + 35, 176, 14, (te.maxCrushingProgress - te.crushingProgress) * 24 / te.maxCrushingProgress, 16);
		int energyPixels = te.energyStorage.getEnergyStored() * 50 / te.energyStorage.getMaxEnergyStored();
		this.blit(matrixStack, this.leftPos + 163, this.topPos + 65 - energyPixels, 176, 82 - energyPixels, 7, energyPixels);
		if (isHoveringEnergy(mouseX, mouseY)) {
			RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            this.fillGradient(matrixStack, this.leftPos + 163, this.topPos + 15, this.leftPos + 170, this.topPos + 65, slotColor, slotColor);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
		}
		
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTiсks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTiсks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
		if (isHoveringEnergy(mouseX, mouseY))
			net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(matrixStack, Arrays.asList(new StringTextComponent(
					te.energyStorage.getEnergyStored()+"/"+te.energyStorage.getMaxEnergyStored()+" FE")), mouseX, mouseY, width, height, -1, font);
	}
	
	@Override
	protected void renderLabels(MatrixStack matrixstack, int x, int y) {
		this.font.draw(matrixstack, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
	}
	
	protected boolean isHoveringEnergy(int mouseX, int mouseY) {
		return mouseX > this.leftPos + 162 && mouseX < this.leftPos + 170 && mouseY > this.topPos + 14 && mouseY < this.topPos + 65;
	}

}
