package com.tranzistor.tranzistio.init;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.te.CoalGeneratorTileEntity;
import com.tranzistor.tranzistio.te.CrusherTileEntity;
import com.tranzistor.tranzistio.te.ElectricFurnaceTileEntity;

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
			()  -> TileEntityType.Builder.of(CrusherTileEntity::new, BlockInit.CRUSHER.get()).build(null));
}
