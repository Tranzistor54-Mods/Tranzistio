package com.tranzistor.tranzistio.util;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.client.gui.CoalGeneratorScreen;
import com.tranzistor.tranzistio.core.init.ContainersInit;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Tranzistio.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)

public class ClientEventBusSubscriber {
	
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		ScreenManager.register(ContainersInit.COAL_GENERATOR.get(), CoalGeneratorScreen::new);
	}

}
