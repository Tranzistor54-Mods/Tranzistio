package com.tranzistor.tranzistio.core.init;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.core.itemGroup.TranzistioItemGroup;

import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Tranzistio.MOD_ID);
	
	public static final RegistryObject<Item> RUBY = ITEMS.register("ruby", () -> new Item(new Item.Properties().tab(TranzistioItemGroup.TRANZISTIO)));
	public static final RegistryObject<Item> ASH = ITEMS.register("ash", () -> new Item(new Item.Properties().tab(TranzistioItemGroup.TRANZISTIO)));
	
	public static void register (IEventBus bus) {
		ITEMS.register(bus);
	}
}
