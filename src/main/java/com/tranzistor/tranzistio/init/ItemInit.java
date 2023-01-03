package com.tranzistor.tranzistio.init;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.items.CraftDamageable;
import com.tranzistor.tranzistio.items.Destroyer;

import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Tranzistio.MOD_ID);
	
	public static final RegistryObject<Item> RUBY = ITEMS.register("ruby", () -> new Item(new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP)));
	public static final RegistryObject<Item> ASH = ITEMS.register("ash", () -> new Item(new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP)));
	public static final RegistryObject<Item> HAMMER = ITEMS.register("hammer", () -> new CraftDamageable(new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP).durability(64)));
	public static final RegistryObject<Item> HAMMERED_IRON_ORE = ITEMS.register("hammered_iron_ore", () -> new Item(new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP)));
	public static final RegistryObject<Item> VACUUM_TUBE = ITEMS.register("electrical_lamp", () -> new Item(new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP)));
	public static final RegistryObject<Item> DESTROYER = ITEMS.register("destroyer", () -> new Destroyer(new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP).durability(64).addToolType(ToolType.PICKAXE, 2)));
	public static final RegistryObject<Item> EPOXY_RESIN_BUCKET = ITEMS.register("epoxy_resin_bucket", () -> new BucketItem(() -> FluidInit.EPOXY_RESIN_FLUID.get(), new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP).stacksTo(1)));
	public static final RegistryObject<Item> GLASS_DUST = ITEMS.register("glass_dust", () -> new Item(new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP)));
	public static final RegistryObject<Item> COPPER_FOIL = ITEMS.register("copper_foil", () -> new Item(new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP)));
	public static final RegistryObject<Item> COMPRESSED_ORGANIC_FIBER = ITEMS.register("compressed_organic_fiber", () -> new Item(new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP)));
	public static final RegistryObject<Item> PRINTED_BOARD = ITEMS.register("printed_board", () -> new Item(new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP)));
	
}
