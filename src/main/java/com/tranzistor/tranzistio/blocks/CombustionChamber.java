package com.tranzistor.tranzistio.blocks;

import com.tranzistor.tranzistio.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.te.BaseMBControllerTE;
import com.tranzistor.tranzistio.util.IBaseMBPart.IBaseMBPartController;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class CombustionChamber extends HorizontalBlock implements IBaseMBPartController{
	//public static final BooleanProperty FORMED = BooleanProperty.create("formed");
	public CombustionChamber() {
		super(AbstractBlock.Properties.of(Material.METAL)
				.strength(5f, 6f)
				.harvestLevel(2)
				.harvestTool(ToolType.PICKAXE)
				.requiresCorrectToolForDrops()
				.sound(SoundType.METAL)
				.lightLevel(state -> {
					if(state.getValue(FORMED))
						return 1;
					return 0;
				}));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(FORMED, false);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, FORMED);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityTypesInit.COMBUSTION_CHAMBER_TILE_ENTITY.get().create();
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState p_149645_1_) {
		return BlockRenderType.MODEL;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState state1, boolean isMoving) {
		if(!state.is(state1.getBlock())) {
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof BaseMBControllerTE) {
				((BaseMBControllerTE)te).setMBState(false);
			}
		}
		super.onRemove(state, world, pos, state1, isMoving);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onPlace(BlockState state, World world, BlockPos pos, BlockState state1, boolean isMoving) {
		if(!state.is(state1.getBlock())) {
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof BaseMBControllerTE) {
				((BaseMBControllerTE)te).checkMB();
			}
		}
		super.onPlace(state, world, pos, state1, isMoving);
	}
	

}
