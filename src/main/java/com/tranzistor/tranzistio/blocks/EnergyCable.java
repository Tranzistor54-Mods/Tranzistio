package com.tranzistor.tranzistio.blocks;

import java.util.Arrays;
import com.tranzistor.tranzistio.energy.EnergyNetwork;
import com.tranzistor.tranzistio.energy.ModEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

public class EnergyCable extends SixWayBlock {
	
	protected final int transferRate;
	
	public EnergyCable(float index, Properties prop, int transferRate) {
		super(index, prop.noOcclusion());
		this.transferRate = transferRate;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false)
				.setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> state) {
		state.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);				
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		IBlockReader iReader = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockState result = super.getStateForPlacement(context);
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
	 
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState state1, IWorld world, BlockPos pos, BlockPos pos1) {
		TileEntity te = world.getBlockEntity(pos1);
		if (te != null) {
			te.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).ifPresent(newES -> {
				if (!newES.canReceive()) return;
				EnergyNetwork.traverse(world, pos, Arrays.asList(Direction.values()), this.getTransferRate(), null, (es, rate) -> {
					if (es instanceof ModEnergyStorage && es.canExtract()) {
						System.out.println("From shape");
						((ModEnergyStorage)es).addReceiver(newES, rate);
					}
				});
			});
		}
		return state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(pos1, direction.getOpposite(), world));
	}
	 
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() == this) return;
		//System.out.println("Fired: "+pos+", "+newState.getBlock()+", "+Boolean.toString(isMoving));
		EnergyNetwork.refresh(world, pos, pos);
		super.onRemove(oldState, world, pos, newState, isMoving);
	}
	 
	@SuppressWarnings("deprecation")
	@Override
	public void onPlace(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (newState.getBlock() == this) return;
		//System.out.println("Fired: "+pos+", "+newState.getBlock()+", "+Boolean.toString(isMoving));
		EnergyNetwork.refresh(world, pos, null);
		super.onPlace(oldState, world, pos, newState, isMoving);
	}
	
	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}
}
