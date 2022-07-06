package com.tranzistor.tranzistio.core.init;

import java.util.function.Supplier;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.core.itemGroup.TranzistioItemGroup;
import com.tranzistor.tranzistio.core.machines.CoalGenerator;
import com.tranzistor.tranzistio.core.machines.ElectricFurnace;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockInit  {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Tranzistio.MOD_ID);
	
	public static final RegistryObject<Block> RUBY_BLOCK = registerBlock("ruby_block",
			() -> new Block(AbstractBlock.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY)
					.strength(5f, 6f).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().sound(SoundType.METAL)));
	
	public static final RegistryObject<Block> RUBY_ORE = registerBlock("ruby_ore", () -> new Block(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
					.strength(5f, 6f).harvestLevel(3).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().sound(SoundType.STONE)));
	
	public static final RegistryObject<Block> COAL_GENERATOR = registerBlock("coal_generator", () -> new CoalGenerator(AbstractBlock.Properties.of(Material.METAL)
			.strength(5f,6f).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().sound(SoundType.METAL)));
	
	public static final RegistryObject<Block> ELECTRIC_FURNACE = registerBlock("electric_furnace", () -> new ElectricFurnace(AbstractBlock.Properties.of(Material.METAL)
			.strength(5f, 6f).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().sound(SoundType.METAL)));
	
	private static <T extends Block>RegistryObject<T> registerBlock (String name, Supplier<T> block){
		RegistryObject<T> toReturn = BLOCKS.register(name, block);
		registerBlockItem(name, toReturn);
		return toReturn;
	}
	
	
	private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
		ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(TranzistioItemGroup.TRANZISTIO)));
	}
	
	
	public static void register (IEventBus bus) {
		BLOCKS.register(bus);
	}

}
