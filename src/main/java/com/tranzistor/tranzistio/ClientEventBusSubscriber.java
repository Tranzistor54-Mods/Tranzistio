package com.tranzistor.tranzistio;

import com.tranzistor.tranzistio.client.gui.CoalGeneratorScreen;
import com.tranzistor.tranzistio.client.gui.ElectricFurnaceScreen;
import com.tranzistor.tranzistio.client.gui.CrusherScreen;
import com.tranzistor.tranzistio.client.gui.FluidFillerScreen;
import com.tranzistor.tranzistio.init.BlockInit;
import com.tranzistor.tranzistio.init.ContainersInit;
import com.tranzistor.tranzistio.init.FluidInit;
import com.tranzistor.tranzistio.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.render.FluidTankTileEntityRender;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Tranzistio.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
	
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		ScreenManager.register(ContainersInit.COAL_GENERATOR.get(), CoalGeneratorScreen::new);
		ScreenManager.register(ContainersInit.ELECTRIC_FURNACE.get(), ElectricFurnaceScreen::new);
		ScreenManager.register(ContainersInit.CRUSHER.get(), CrusherScreen::new);
		ScreenManager.register(ContainersInit.FLUID_FILLER.get(), FluidFillerScreen::new);
		ClientRegistry.bindTileEntityRenderer(TileEntityTypesInit.FLUID_TANK_TILE_ENTITY_BASE.get(), FluidTankTileEntityRender::new);
		event.enqueueWork(() -> {
    		RenderTypeLookup.setRenderLayer(FluidInit.EPOXY_RESIN_FLUID.get(), RenderType.translucent());
    		RenderTypeLookup.setRenderLayer(FluidInit.EPOXY_RESIN_BLOCK.get(), RenderType.translucent());
    		RenderTypeLookup.setRenderLayer(FluidInit.EPOXY_RESIN_FLOWING.get(), RenderType.translucent());
    		RenderTypeLookup.setRenderLayer(BlockInit.FLUID_TANK_BASE.get(), RenderType.translucent());
    	
    	});
	}

}
