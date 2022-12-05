package com.tranzistor.tranzistio.core.init;
import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.core.te.CoalGeneratorTileEntity;
import com.tranzistor.tranzistio.core.te.ElectricFurnaceTileEntity;
import com.tranzistor.tranzistio.core.te.EnergyCableTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


public class TileEntityTypesInit {

	public static DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPE = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Tranzistio.MOD_ID);
	
	public static RegistryObject<TileEntityType<CoalGeneratorTileEntity>> COAL_GENERATOR_TILE_ENTITY = TILE_ENTITY_TYPE.register("coal_generator_tile_entity", 
			()  -> TileEntityType.Builder.of(CoalGeneratorTileEntity::new, BlockInit.COAL_GENERATOR.get()).build(null));
	
	public static RegistryObject<TileEntityType<ElectricFurnaceTileEntity>> ELECTRIC_FURNACE_TILE_ENTITY = TILE_ENTITY_TYPE.register("electric_furnace_tile_entity", 
			()  -> TileEntityType.Builder.of(ElectricFurnaceTileEntity::new, BlockInit.ELECTRIC_FURNACE.get()).build(null));
	
	public static RegistryObject<TileEntityType<EnergyCableTileEntity>> ENERGY_CABLE_TILE_ENTITY = TILE_ENTITY_TYPE.register("copper_cable_tile_entity", 
			() -> TileEntityType.Builder.of(EnergyCableTileEntity::new, BlockInit.COPPER_CABLE.get()).build(null));
	
	public static void register(IEventBus bus) {
		TILE_ENTITY_TYPE.register(bus);
	}
}
