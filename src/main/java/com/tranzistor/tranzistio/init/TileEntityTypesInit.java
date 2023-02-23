package com.tranzistor.tranzistio.init;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.te.CoalGeneratorTileEntity;
import com.tranzistor.tranzistio.te.CokeOvenBricksTE;
import com.tranzistor.tranzistio.te.CombustionChamberTE;
import com.tranzistor.tranzistio.te.CrusherTileEntity;
import com.tranzistor.tranzistio.te.ElectricFurnaceTileEntity;
import com.tranzistor.tranzistio.te.FluidFillerTileEntity;
import com.tranzistor.tranzistio.te.FluidTankTileEntity;
import com.tranzistor.tranzistio.util.MultiblockPatterns;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityTypesInit {

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Tranzistio.MOD_ID);
	
	
	public static final RegistryObject<TileEntityType<CoalGeneratorTileEntity>> COAL_GENERATOR_TILE_ENTITY = TILE_ENTITY_TYPE.register("coal_generator_tile_entity", 
			()  -> TileEntityType.Builder.of(CoalGeneratorTileEntity::new, BlockInit.COAL_GENERATOR.get()).build(null));
	
	public static final RegistryObject<TileEntityType<ElectricFurnaceTileEntity>> ELECTRIC_FURNACE_TILE_ENTITY = TILE_ENTITY_TYPE.register("electric_furnace_tile_entity", 
			()  -> TileEntityType.Builder.of(ElectricFurnaceTileEntity::new, BlockInit.ELECTRIC_FURNACE.get()).build(null));
	
	public static final RegistryObject<TileEntityType<CrusherTileEntity>> CRUSHER_TILE_ENTITY = TILE_ENTITY_TYPE.register("crusher_tile_entity", 
			()  -> TileEntityType.Builder.of(() -> new CrusherTileEntity(8192, 32), BlockInit.CRUSHER.get()).build(null));
	
	public static final RegistryObject<TileEntityType<FluidFillerTileEntity>> FLUID_FILLER_TILE_ENTITY = TILE_ENTITY_TYPE.register("fluid_filler_tile_entity", 
			()  -> TileEntityType.Builder.of(() -> new FluidFillerTileEntity(8192, 64, 16000), BlockInit.FLUID_FILLER.get()).build(null));
	
	public static final RegistryObject<TileEntityType<FluidTankTileEntity>> FLUID_TANK_TILE_ENTITY_BASE = TILE_ENTITY_TYPE.register("fluid_tank_tile_entity_base",
			()  -> TileEntityType.Builder.of(() -> new FluidTankTileEntity(16000), BlockInit.FLUID_TANK_BASE.get()).build(null));
	
	public static final RegistryObject<TileEntityType<CombustionChamberTE>> COMBUSTION_CHAMBER_TILE_ENTITY = TILE_ENTITY_TYPE.register("combustion_chamber_tile_entity", 
			()  -> TileEntityType.Builder.of(() -> new CombustionChamberTE(MultiblockPatterns.cokeOvenPat), BlockInit.COMBUSTION_CHAMBER.get()).build(null));
	
	public static final RegistryObject<TileEntityType<CokeOvenBricksTE>> COKE_OVEN_BRICKS_TILE_ENTITY = TILE_ENTITY_TYPE.register("coke_oven_bricks_tile_entity", 
			()  -> TileEntityType.Builder.of(() -> new CokeOvenBricksTE(), BlockInit.COKE_OVEN_BRICKS.get()).build(null));
}
