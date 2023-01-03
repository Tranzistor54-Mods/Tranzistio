package com.tranzistor.tranzistio.blocks;

import com.tranzistor.tranzistio.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.te.FluidFillerTileEntity;
import com.tranzistor.tranzistio.util.ModFluidUtil;

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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

public class FluidFiller extends HorizontalBlock {
	public static final BooleanProperty WORKING = BooleanProperty.create("working");
	
	public FluidFiller() {
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
		return TileEntityTypesInit.FLUID_FILLER_TILE_ENTITY.get().create();
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		FluidFillerTileEntity te = (FluidFillerTileEntity) world.getBlockEntity(pos);
		ItemStack stack = player.getItemInHand(hand);
		if(world.isClientSide) {
			stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
				int drainAmount = Math.min(te.getSpace(), 1000);
				int fillAmount = te.getFluidAmount() >= 1000 ? 1000 : 0;
				FluidStack stack1 = handler.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
				
				if(!stack1.isEmpty() && ModFluidUtil.canInjectFluid(stack1, te.fluidStorage)) {
					stack1 = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
					ModFluidUtil.fillFluidStorageFromHand(te.fluidStorage, stack1, handler.getContainer(), player, pos);
				}
				
				if(stack1.isEmpty() && fillAmount != 0) {
					int fillAmount1 = handler.fill(te.fluidStorage.getFluid(), IFluidHandler.FluidAction.EXECUTE);
					ModFluidUtil.drainFluidStorageFromHand(te.fluidStorage, handler.getContainer(), player, pos, fillAmount1);
				}
			});
			return ActionResultType.SUCCESS;
		}
		else {
			if(!stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent() && stack.getItem() != Items.MILK_BUCKET)
				NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
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
			if (tileentity instanceof FluidFillerTileEntity) {
				InventoryHelper.dropContents(world, blockpos, (FluidFillerTileEntity)tileentity);
			}
			super.onRemove(oldBlockstate, world, blockpos, newBlockstate, isMoving);
		}
	}
}