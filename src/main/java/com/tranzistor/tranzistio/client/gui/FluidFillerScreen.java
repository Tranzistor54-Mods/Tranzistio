package com.tranzistor.tranzistio.client.gui;

import java.util.Arrays;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.containers.FluidFillerContainer;
import com.tranzistor.tranzistio.te.FluidFillerTileEntity;
import com.tranzistor.tranzistio.util.ModFluidUtil;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FluidFillerScreen extends ContainerScreen<FluidFillerContainer> {
	
	private static final ResourceLocation FLUID_FILLER_SCREEN = new ResourceLocation(Tranzistio.MOD_ID, "textures/gui/fluid_filler.png");
	private final FluidFillerTileEntity te;

	public FluidFillerScreen(FluidFillerContainer container, PlayerInventory playerInventory, ITextComponent component) {
		super(container, playerInventory, component);
		this.te = (FluidFillerTileEntity) container.te;
		this.leftPos = 0;
		this.topPos = 0;
		this.imageWidth = 175;
		this.imageHeight = 201;
	}

	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
		this.minecraft.textureManager.bind(FLUID_FILLER_SCREEN);
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;
		this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
		
		this.addButton(new Button(this.leftPos + 8, this.topPos + 27, 18, 6, new TranslationTextComponent(""), (button) -> {
			//this.te.updateFluidStorage();
			ModFluidUtil.emptyTheFluidStorage(te.fluidStorage, te.getBlockPos());
		}));
		
		
		int energyPixels = te.energyStorage.getEnergyStored() * 50 / te.energyStorage.getMaxEnergyStored();
		this.blit(matrixStack, this.leftPos + 62, this.topPos + 67, 226 - energyPixels, 17, energyPixels, 7);
		
		if(te.maxProgress > 0) {
			this.blit(matrixStack, this.leftPos + 117, this.topPos + 27, 176, 0, (te.maxProgress - te.progress) * 24 / te.maxProgress, 16);
		}
		
		
		if (isHoveringEnergy(mouseX, mouseY)) {
			RenderSystem.disableDepthTest();
            RenderSystem.colorMask(true, true, true, false);
            this.fillGradient(matrixStack, this.leftPos + 62, this.topPos + 67, this.leftPos + 112, this.topPos + 74, slotColor, slotColor);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
		}
		
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTiks) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTiks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
		this.renderFluid(matrixStack, te.getFluid());
		
		if (isHoveringEnergy(mouseX, mouseY)) {
			net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(matrixStack, Arrays.asList(new StringTextComponent(
					te.energyStorage.getEnergyStored()+"/"+te.energyStorage.getMaxEnergyStored()+" FE")), mouseX, mouseY, width, height, -1, font);
		}
		
		if(isHoveringFluid(mouseX, mouseY) && te.getFluid() != Fluids.EMPTY) {
			net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(matrixStack, Arrays.asList(new StringTextComponent(
					te.getFluid().getAttributes().getDisplayName(te.fluidStorage.getFluid()).getString()+ ": " + te.fluidStorage.getFluidAmount() + "/" + te.fluidStorage.getCapacity() + " mB")), mouseX, mouseY, width, height, -1, font);
		}
	}
	
	@Override
	protected void renderLabels(MatrixStack matrixstack, int x, int y) {
		this.font.draw(matrixstack, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
	}
	
	protected void renderFluid(MatrixStack matrixStack, Fluid fluid) {
		int fluidPixels = te.fluidStorage.getFluidAmount() * 2 / 1000;
		ResourceLocation fluidLoc = fluid.getAttributes().getStillTexture();
		TextureAtlasSprite fluidTexture = this.minecraft.getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(fluidLoc);
		setGLColorFromInt(fluid.getAttributes().getColor());
		this.minecraft.textureManager.bind(PlayerContainer.BLOCK_ATLAS);
		blit(matrixStack, this.leftPos + 9, this.topPos + 68 - fluidPixels, 1, 38, fluidPixels, fluidTexture);
		
	}
	
	protected boolean isHoveringEnergy(int mouseX, int mouseY) {
		return mouseX > this.leftPos + 61 && mouseX < this.leftPos + 112 && mouseY > this.topPos + 66 && mouseY < this.topPos + 74;
	}
	
	protected boolean isHoveringFluid(int mouseX, int mouseY) {
		return mouseX > this.leftPos + 8 && mouseX < this.leftPos + 47 && mouseY > this.topPos + 35 && mouseY < this.topPos + 69;
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
