package com.tranzistor.tranzistio.init;

import java.util.function.Supplier;

import com.tranzistor.tranzistio.Tranzistio;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidAttributes.Builder;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidInit {
	
	public static final ResourceLocation FLUID_OVERLAY = new ResourceLocation("minecraft","block/water_overlay");
	
	//Epoxy resin
	public static final ResourceLocation EPOXY_RESIN_STILL = new ResourceLocation(Tranzistio.MOD_ID, "fluids/epoxy_resin_still");
	public static final ResourceLocation EPOXY_RESIN_FLOW = new ResourceLocation(Tranzistio.MOD_ID, "fluids/epoxy_resin_flow");
	
	//Creosote oil
	public static final ResourceLocation CREOSOTE_OIL_STILL = new ResourceLocation(Tranzistio.MOD_ID, "fluids/creosote_oil_still");
	public static final ResourceLocation CREOSOTE_OIL_FLOW = new ResourceLocation(Tranzistio.MOD_ID, "fluids/creosote_oil_flow");
	
	
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Tranzistio.MOD_ID);
	
	public static final RegistryObject<FlowingFluid> EPOXY_RESIN_FLUID = FLUIDS.register("epoxy_resin_still", () -> new ForgeFlowingFluid.Source(FluidInit.EPOXY_RESIN_PROP));
	public static final RegistryObject<FlowingFluid> EPOXY_RESIN_FLOWING = FLUIDS.register("epoxy_resin_flow", () -> new ForgeFlowingFluid.Flowing(FluidInit.EPOXY_RESIN_PROP));
	public static final RegistryObject<FlowingFluidBlock> EPOXY_RESIN_BLOCK = BlockInit.BLOCKS.register("epoxy_resin", () -> new FlowingFluidBlock(() -> FluidInit.EPOXY_RESIN_FLUID.get(), AbstractBlock.Properties
			.of(Material.WATER)
			.strength(100f)
			.noDrops()));
	
	public static final RegistryObject<FlowingFluid> CREOSOTE_OIL_FLUID = FLUIDS.register("creosote_oil_fluid", () -> new ForgeFlowingFluid.Source(FluidInit.CREOSOTE_OIL_PROP));
	public static final RegistryObject<FlowingFluid> CREOSOTE_OIL_FLOWING = FLUIDS.register("creosote_oil_flow", () -> new ForgeFlowingFluid.Flowing(FluidInit.CREOSOTE_OIL_PROP));
	public static final RegistryObject<FlowingFluidBlock> CREOSOTE_OIL_BLOCK = BlockInit.BLOCKS.register("creosote_oil", () -> new FlowingFluidBlock(() -> FluidInit.CREOSOTE_OIL_FLUID.get(), AbstractBlock.Properties
			.of(Material.WATER)
			.strength(100f)
			.noDrops()));
	
	public static final CustomFluidProperties EPOXY_RESIN_PROP = new CustomFluidProperties(() -> EPOXY_RESIN_FLUID.get(), 
			() -> EPOXY_RESIN_FLOWING.get(), 
			FluidAttributes.builder(EPOXY_RESIN_STILL, EPOXY_RESIN_FLOW).density(25).viscosity(20).overlay(FLUID_OVERLAY).temperature(300), 
			() -> FluidInit.EPOXY_RESIN_BLOCK.get(), 
			() -> ItemInit.EPOXY_RESIN_BUCKET.get(), 4, 2);
	
	public static final CustomFluidProperties CREOSOTE_OIL_PROP = new CustomFluidProperties(() -> CREOSOTE_OIL_FLUID.get(), 
			() -> CREOSOTE_OIL_FLOWING.get(), 
			FluidAttributes.builder(CREOSOTE_OIL_STILL, CREOSOTE_OIL_FLOW).density(20).viscosity(10).overlay(FLUID_OVERLAY).temperature(300), 
			() -> FluidInit.CREOSOTE_OIL_BLOCK.get(), 
			() -> ItemInit.CREOSOTE_OIL_BUCKET.get(), 2, 6);
	
	public static class CustomFluidProperties extends ForgeFlowingFluid.Properties{
		public CustomFluidProperties(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing, Builder attributes, Supplier<? extends FlowingFluidBlock> block, Supplier<? extends Item> bucket, int decrease, int slopeDist) {
			super(still, flowing, attributes);
			this.block(block);
			this.bucket(bucket);
			this.levelDecreasePerBlock(decrease);
			this.slopeFindDistance(slopeDist); 
		}
	}
}
