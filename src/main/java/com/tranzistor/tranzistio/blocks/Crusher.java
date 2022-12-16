package com.tranzistor.tranzistio.blocks;

import com.tranzistor.tranzistio.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.te.CrusherTileEntity;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

public class Crusher extends HorizontalBlock {
	public static final BooleanProperty WORKING = BooleanProperty.create("working");
	public Crusher() {
		super(AbstractBlock.Properties.of(Material.METAL).strength(5f, 6f).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().sound(SoundType.METAL));
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WORKING, false);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, WORKING);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityTypesInit.CRUSHER_TILE_ENTITY.get().create();
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		if (world.isClientSide) {
			return ActionResultType.SUCCESS;
		} else {
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof CrusherTileEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (CrusherTileEntity) te, pos);
			}
			return ActionResultType.CONSUME;
		}
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState p_149645_1_) {
		return BlockRenderType.MODEL;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState oldBlockstate, World world, BlockPos blockpos, BlockState newBlockstate, boolean isMoving) {
		if(!oldBlockstate.is(newBlockstate.getBlock())) {
			TileEntity tileentity = world.getBlockEntity(blockpos);
			if (tileentity instanceof CrusherTileEntity) {
				InventoryHelper.dropContents(world, blockpos, (CrusherTileEntity)tileentity);
			}
			super.onRemove(oldBlockstate, world, blockpos, newBlockstate, isMoving);
		}
	}
}
