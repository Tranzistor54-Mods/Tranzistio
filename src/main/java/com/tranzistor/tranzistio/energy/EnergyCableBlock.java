package com.tranzistor.tranzistio.energy;

import java.util.ArrayList;
import java.util.Arrays;

import com.tranzistor.tranzistio.core.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.core.machines.CoalGenerator;
import com.tranzistor.tranzistio.core.machines.ElectricFurnace;
import com.tranzistor.tranzistio.core.te.EnergyCableTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class EnergyCableBlock extends SixWayBlock{
	
	public final int indexOfType;
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	
	public EnergyCableBlock(float index, Properties prop, int indexOfType) {
		super(index, AbstractBlock.Properties.of(Material.METAL).strength(5f, 6f).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion());
		this.indexOfType = indexOfType;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, Boolean.valueOf(false))
				.setValue(EAST, Boolean.valueOf(false))
				.setValue(SOUTH, Boolean.valueOf(false))
				.setValue(WEST, Boolean.valueOf(false))
				.setValue(UP, Boolean.valueOf(false))
				.setValue(DOWN, Boolean.valueOf(false)));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> state) {
		state.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);				
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		IBlockReader iReader = context.getLevel();
		BlockPos blockPos = context.getClickedPos();
		BlockPos blockPos1 = blockPos.north();
		BlockPos blockPos2 = blockPos.south();
		BlockPos blockPos3 = blockPos.east();
		BlockPos blockPos4 = blockPos.west();
		BlockPos blockPos5 = blockPos.above();
		BlockPos blockPos6 = blockPos.below();
		BlockState blockState1 = iReader.getBlockState(blockPos1);
		BlockState blockState2 = iReader.getBlockState(blockPos2);
		BlockState blockState3 = iReader.getBlockState(blockPos3);
		BlockState blockState4 = iReader.getBlockState(blockPos4);
		BlockState blockState5 = iReader.getBlockState(blockPos5);
		BlockState blockState6 = iReader.getBlockState(blockPos6);
		return super.getStateForPlacement(context)
				.setValue(NORTH, Boolean.valueOf(this.connectsTo(blockState1)))
			    .setValue(SOUTH, Boolean.valueOf(this.connectsTo(blockState2)))
				.setValue(EAST, Boolean.valueOf(this.connectsTo(blockState3)))
				.setValue(WEST, Boolean.valueOf(this.connectsTo(blockState4)))
				.setValue(UP, Boolean.valueOf(this.connectsTo(blockState5)))
				.setValue(DOWN,	Boolean.valueOf(this.connectsTo(blockState6)));
	}
	
	public boolean connectsTo(BlockState state) {
	      Block block = state.getBlock();
	      boolean flag = block instanceof EnergyCableBlock || block instanceof CoalGenerator || block instanceof ElectricFurnace;
	      boolean flag1 = isSameCable(block);
	      return flag || flag1;
    }
	
	public boolean isSameCable(Block block) {
		return block instanceof EnergyCableBlock && ((EnergyCableBlock) block).getIndex() == this.indexOfType;
	}
	
	public int getIndex() {
		return this.indexOfType;
	}
	 
	 @Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState state1, IWorld world, BlockPos pos, BlockPos pos1) {
		 return state.setValue(PROPERTY_BY_DIRECTION.get(direction), Boolean.valueOf(this.connectsTo(state1)));
	}
	 
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState p_196243_4_, boolean p_196243_5_) {
		ArrayList<BlockPos> dirs = new ArrayList<>(Arrays.asList(pos.north(), pos.east(), pos.south(), pos.west(), pos.above(), pos.below()));
		dirs.removeIf(pos1 -> !isSameCable(world.getBlockState(pos1).getBlock()));
		EnergyCableTileEntity desCable = (EnergyCableTileEntity) world.getBlockEntity(pos);
		if (desCable.isMaster() && !dirs.isEmpty()) { 
			EnergyCableTileEntity te = (EnergyCableTileEntity) world.getBlockEntity(dirs.remove(0));
			te.master = null; 
			desCable.setMaster(te);
		}
		ArrayList<ArrayList<BlockPos>> usedLists = new ArrayList<>();
		outerLoop:
		for (BlockPos cablePos : dirs) { 
			for (ArrayList<BlockPos> list : usedLists) 
				if (list.contains(cablePos))
					continue outerLoop;
			EnergyCableTileEntity cableTe = (EnergyCableTileEntity) world.getBlockEntity(cablePos);
			if (cableTe.isMaster()) 
				continue;
			BlockPos masterPos = cableTe.getMaster().getBlockPos();
			ArrayList<BlockPos> newNetwork = new ArrayList<>();
			newNetwork.add(cablePos);
			int i = 0;
			while (i < newNetwork.size()) {
				BlockPos pos1 = newNetwork.get(i);
				for (BlockPos pos2 : Arrays.asList(pos1.north(), pos1.east(), pos1.south(), pos1.west(), pos1.above(), pos1.below())) {
					if (pos2.equals(masterPos)) 
						continue outerLoop; 
					if (!newNetwork.contains(pos2) && !pos.equals(pos2) && isSameCable(world.getBlockState(pos2).getBlock()))
						newNetwork.add(pos2);
				}
				i++;
			}
			newNetwork.remove(0); 
			cableTe.master = null; 
			for (BlockPos pos1 : newNetwork)
				((EnergyCableTileEntity) world.getBlockEntity(pos1)).setMaster(cableTe);
			usedLists.add(newNetwork);
		}
		super.onRemove(state, world, pos, p_196243_4_, p_196243_5_);
	}
	
	
	 @Override
	public boolean hasTileEntity(BlockState state) {
		 return true;
	} 
	 
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityTypesInit.ENERGY_CABLE_TILE_ENTITY.get().create();
	}
}
