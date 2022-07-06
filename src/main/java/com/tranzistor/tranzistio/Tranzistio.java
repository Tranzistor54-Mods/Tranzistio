package com.tranzistor.tranzistio;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.tranzistor.tranzistio.core.init.BlockInit;
import com.tranzistor.tranzistio.core.init.ContainersInit;
import com.tranzistor.tranzistio.core.init.FeatureInit;
import com.tranzistor.tranzistio.core.init.ItemInit;
import com.tranzistor.tranzistio.core.init.TileEntityTypesInit;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("tranzistio")
@Mod.EventBusSubscriber(modid = Tranzistio.MOD_ID, bus = Bus.MOD)
public class Tranzistio{

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "tranzistio";

    public Tranzistio() {
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	ItemInit.register(bus);
    	BlockInit.register(bus);
    	TileEntityTypesInit.register(bus);
    	ContainersInit.register(bus);
    	
    	MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, FeatureInit::addOres);
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    
   
}  
   

