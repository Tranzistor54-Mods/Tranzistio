package com.tranzistor.tranzistio.init;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.containers.CoalGeneratorContainer;
import com.tranzistor.tranzistio.containers.CrusherContainer;
import com.tranzistor.tranzistio.containers.ElectricFurnaceContainer;
import com.tranzistor.tranzistio.containers.FluidFillerContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainersInit {
	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Tranzistio.MOD_ID);
	
	public static final RegistryObject<ContainerType<CoalGeneratorContainer>> COAL_GENERATOR = CONTAINERS.register("coal_generator_container", () -> IForgeContainerType.create(CoalGeneratorContainer::new));
	public static final RegistryObject<ContainerType<ElectricFurnaceContainer>> ELECTRIC_FURNACE = CONTAINERS.register("electric_furnace_container", () -> IForgeContainerType.create(ElectricFurnaceContainer::new));
	public static final RegistryObject<ContainerType<CrusherContainer>> CRUSHER = CONTAINERS.register("crusher_container", () -> IForgeContainerType.create(CrusherContainer::new));
	public static final RegistryObject<ContainerType<FluidFillerContainer>> FLUID_FILLER = CONTAINERS.register("fluid_filler_container", () -> IForgeContainerType.create(FluidFillerContainer::new));
	
}
