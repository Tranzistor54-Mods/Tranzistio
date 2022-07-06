package com.tranzistor.tranzistio.core.init;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.core.containers.CoalGeneratorContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainersInit {
	public static DeferredRegister<ContainerType<?>> CONTAINERS = 
			DeferredRegister.create(ForgeRegistries.CONTAINERS, Tranzistio.MOD_ID);
	
	public static RegistryObject<ContainerType<CoalGeneratorContainer>> COAL_GENERATOR = CONTAINERS.register("coal_generator_container", () -> IForgeContainerType.create(CoalGeneratorContainer::new));
	
	public static void register(IEventBus bus) {
		CONTAINERS.register(bus);
	}
}
