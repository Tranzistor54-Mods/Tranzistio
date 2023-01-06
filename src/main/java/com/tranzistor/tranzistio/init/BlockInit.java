package com.tranzistor.tranzistio.init;

import java.util.function.Supplier;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.blocks.CoalGenerator;
import com.tranzistor.tranzistio.blocks.Crusher;
import com.tranzistor.tranzistio.blocks.ElectricFurnace;
import com.tranzistor.tranzistio.blocks.EnergyCable;
import com.tranzistor.tranzistio.blocks.FluidFiller;
import com.tranzistor.tranzistio.blocks.FluidTankBlock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
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
	
	public static final RegistryObject<Block> COAL_GENERATOR = registerBlock("coal_generator", () -> new CoalGenerator());
	public static final RegistryObject<Block> ELECTRIC_FURNACE = registerBlock("electric_furnace", () -> new ElectricFurnace());
	public static final RegistryObject<Block> CRUSHER = registerBlock("crusher", () -> new Crusher());
	public static final RegistryObject<Block> FLUID_FILLER = registerBlock("fluid_filler", () -> new FluidFiller());
	public static final RegistryObject<Block> FLUID_TANK_BASE = registerBlock("fluid_tank_base", () -> new FluidTankBlock(16000));
	public static final RegistryObject<Block> COPPER_CABLE = registerBlock("copper_cable", () -> new EnergyCable(0.125f, AbstractBlock.Properties.of(Material.METAL)
			.strength(5f, 6f).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().sound(SoundType.METAL), 1024));
	
	
	private static <T extends Block>RegistryObject<T> registerBlock (String name, Supplier<T> blockSupplier){
		RegistryObject<T> block = BLOCKS.register(name, blockSupplier);
		ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(Tranzistio.TRANZISTIO_ITEM_GROUP)));
		return block;
	}

}
