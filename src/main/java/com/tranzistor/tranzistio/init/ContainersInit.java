package com.tranzistor.tranzistio.init;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.containers.CoalGeneratorContainer;
import com.tranzistor.tranzistio.containers.ElectricFurnaceContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainersInit {
	public static DeferredRegister<ContainerType<?>> CONTAINERS = 
			DeferredRegister.create(ForgeRegistries.CONTAINERS, Tranzistio.MOD_ID);
	
	public static RegistryObject<ContainerType<CoalGeneratorContainer>> COAL_GENERATOR = CONTAINERS.register("coal_generator_container", () -> IForgeContainerType.create(CoalGeneratorContainer::new));
	public static RegistryObject<ContainerType<ElectricFurnaceContainer>> ELECTRIC_FURNACE = CONTAINERS.register("electric_furnace_container", () -> IForgeContainerType.create(ElectricFurnaceContainer::new));

}
