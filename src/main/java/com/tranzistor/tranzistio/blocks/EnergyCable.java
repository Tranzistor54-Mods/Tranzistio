package com.tranzistor.tranzistio.blocks;

import java.util.Arrays;
import com.tranzistor.tranzistio.energy.EnergyNetwork;
import com.tranzistor.tranzistio.energy.ModEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

public class EnergyCable extends SixWayBlock { 
	public static final BooleanProperty WATERLOGGED = BooleanProperty.create("waterlogged");
	protected final int transferRate;
	
	public EnergyCable(float index, Properties prop, int transferRate) {
		super(index, prop.noOcclusion());
		this.transferRate = transferRate;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false)
				.setValue(WEST, false).setValue(UP, false).setValue(DOWN, false).setValue(WATERLOGGED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> state) {
		state.add(NORTH, SOUTH, WEST, EAST, UP, DOWN, WATERLOGGED);				
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		IBlockReader iReader = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		FluidState fluidstate = context.getLevel().getFluidState(blockPos);
		boolean canLog = fluidstate.getType() != Fluids.EMPTY;
		BlockState result = super.getStateForPlacement(context).setValue(WATERLOGGED, canLog);
		for (Direction dir : Direction.values())
			result = result.setValue(SixWayBlock.PROPERTY_BY_DIRECTION.get(dir), this.connectsTo(blockPos.relative(dir), dir.getOpposite(), iReader));
		return result;
	}
	
	public boolean connectsTo(BlockPos pos, Direction side, IBlockReader reader) {
		BlockState state = reader.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof EnergyCable)
			return true;
		else {
			TileEntity te = reader.getBlockEntity(pos);
			return te != null && te.getCapability(CapabilityEnergy.ENERGY, side).isPresent();
		}
    }
	
	public boolean isSameCable(Block block) {
		return block instanceof EnergyCable && (EnergyCable) block == this;
	}
	
	public int getTransferRate() {
		return this.transferRate;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public FluidState getFluidState(BlockState state) {
	    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	 
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState state1, IWorld world, BlockPos pos, BlockPos pos1) {
		if (state.getValue(WATERLOGGED)) {
	         world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
	    }
		
		TileEntity te = world.getBlockEntity(pos1);
		if (te != null) {
			te.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).ifPresent(newES -> {
				if (!newES.canReceive()) return;
				EnergyNetwork.traverse(world, pos, Arrays.asList(Direction.values()), this.getTransferRate(), null, (es, rate) -> {
					if (es instanceof ModEnergyStorage && es.canExtract())
						((ModEnergyStorage)es).addReceiver(newES, rate);
				});
			});
		}
		return state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(pos1, direction.getOpposite(), world));
	}
	 
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() == this) return;
		EnergyNetwork.refresh(world, pos, pos);
		super.onRemove(oldState, world, pos, newState, isMoving);
	}
	 
	@SuppressWarnings("deprecation")
	@Override
	public void onPlace(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() == this) return;
		EnergyNetwork.refresh(world, pos, null);
		super.onPlace(oldState, world, pos, newState, isMoving);
	}
	
	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}
}
