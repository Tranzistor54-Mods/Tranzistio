package com.tranzistor.tranzistio.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tranzistor.tranzistio.te.FluidTankTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

@OnlyIn(Dist.CLIENT)
public class FluidTankTileEntityRender extends TileEntityRenderer<FluidTankTileEntity>{
	
	public static final float TANK_THICKNESS = 0.12f;
	public static final float TANK_TOP = 0.05f;
	Minecraft mc = Minecraft.getInstance();
	
	public FluidTankTileEntityRender(TileEntityRendererDispatcher disp) {
		super(disp);
	}

	@Override
	public void render(FluidTankTileEntity te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int overlay, int light1) {
		IVertexBuilder builder = buffer.getBuffer(RenderType.solid());
		FluidStack fluidStack = te.getFluidStorage().getFluid();
		Fluid fluid = te.getFluid();
		ResourceLocation fluidLoc = fluid.getAttributes().getStillTexture();
		TextureAtlasSprite sprite = mc.getTextureAtlas(PlayerContainer.BLOCK_ATLAS).apply(fluidLoc);
		int color = fluid.getAttributes().getColor();
		float a = ((color >> 24) & 0xFF) / 255F;
        float r = (color >> 16 & 0xFF) / 255.0F;
        float g = (color >> 8 & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float scale = 0.87f / te.getCapacity() * te.getFluidAmount();
        int light = 230;
		
		if(te.isRemoved() || te == null)
			return;
		
		if(fluidStack.isEmpty())
			return;
		
		if(scale == 0.0f)
			return;
		
		float sum = TANK_THICKNESS / 12 + scale + 0.01f, f = 1 - TANK_THICKNESS;
		float u1 = sprite.getU0();
        float v1 = sprite.getV0();
        float u2 = sprite.getU1();
        float v2 = sprite.getV1();
     
        mc.textureManager.bind(PlayerContainer.BLOCK_ATLAS);
        setGLColorFromInt(color);
        
		stack.pushPose();
		//TOP
		builder.vertex(stack.last().pose(), TANK_THICKNESS, sum, TANK_THICKNESS).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), TANK_THICKNESS, sum, f).color(r, g, b, a).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), f, sum, f).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), f, sum, TANK_THICKNESS).color(r, g, b, a).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		
		//SIDES
		//NORTH
		builder.vertex(stack.last().pose(), TANK_THICKNESS, 0.02f, TANK_THICKNESS).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), TANK_THICKNESS, sum, TANK_THICKNESS).color(r, g, b, a).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), f, sum, TANK_THICKNESS).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), f, 0.02f, TANK_THICKNESS).color(r, g, b, a).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		
		//SOUTH
		builder.vertex(stack.last().pose(), TANK_THICKNESS, sum, f).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), TANK_THICKNESS, 0.02f, f).color(r, g, b, a).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), f, 0.02f, f).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), f, sum, f).color(r, g, b, a).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		
		//WEST
		builder.vertex(stack.last().pose(), TANK_THICKNESS, sum, TANK_THICKNESS).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), TANK_THICKNESS, 0.02f, TANK_THICKNESS).color(r, g, b, a).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), TANK_THICKNESS, 0.02f, f).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), TANK_THICKNESS, sum, f).color(r, g, b, a).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		
		//EAST
		builder.vertex(stack.last().pose(), f, 0.02f, TANK_THICKNESS).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), f, sum, TANK_THICKNESS).color(r, g, b, a).uv(u1, v2).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), f, sum, f).color(r, g, b, a).uv(u2, v2).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		builder.vertex(stack.last().pose(), f, 0.02f, f).color(r, g, b, a).uv(u2, v1).overlayCoords(overlay).uv2(light).normal(stack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
		stack.popPose();
		
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
