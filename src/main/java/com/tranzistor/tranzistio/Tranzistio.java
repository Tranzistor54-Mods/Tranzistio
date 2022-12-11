package com.tranzistor.tranzistio;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tranzistor.tranzistio.init.BlockInit;
import com.tranzistor.tranzistio.init.ContainersInit;
import com.tranzistor.tranzistio.init.FeatureInit;
import com.tranzistor.tranzistio.init.ItemInit;
import com.tranzistor.tranzistio.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.network.ModNetwork;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

@Mod("tranzistio")
public class Tranzistio {

    @SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "tranzistio";
    
	public static final ItemGroup TRANZISTIO_ITEM_GROUP = new ItemGroup(ItemGroup.TABS.length, "tranzistio") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemInit.RUBY.get());
		}
	};

    public Tranzistio() {
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	
    	for (DeferredRegister<?> register : new DeferredRegister[] {
    			ItemInit.ITEMS, BlockInit.BLOCKS, TileEntityTypesInit.TILE_ENTITY_TYPE, ContainersInit.CONTAINERS})
    		register.register(bus);
    	
    	MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, FeatureInit::addOres);
    	ModNetwork.init();
    }
   
}  
   

