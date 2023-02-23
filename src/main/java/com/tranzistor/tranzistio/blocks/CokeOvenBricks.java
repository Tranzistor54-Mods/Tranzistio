package com.tranzistor.tranzistio.blocks;

import com.tranzistor.tranzistio.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.network.ModNetwork;
import com.tranzistor.tranzistio.network.MultiblockSyncS2C;
import com.tranzistor.tranzistio.te.BaseMBControllerTE;
import com.tranzistor.tranzistio.te.CokeOvenBricksTE;
import com.tranzistor.tranzistio.te.CombustionChamberTE;
import com.tranzistor.tranzistio.util.IBaseMBPart.IBaseMBPartBlock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkHooks;

public class CokeOvenBricks extends Block implements IBaseMBPartBlock{
	
	public CokeOvenBricks() {
		super(AbstractBlock.Properties.of(Material.STONE).harvestLevel(2).harvestTool(ToolType.PICKAXE).strength(5f, 6f).requiresCorrectToolForDrops().sound(SoundType.STONE).noOcclusion());
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.defaultBlockState().setValue(INVISIBLE, false);
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(INVISIBLE);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityTypesInit.COKE_OVEN_BRICKS_TILE_ENTITY.get().create();
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		if(world.isClientSide) {
			return ActionResultType.SUCCESS;
		}
		else {
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof CokeOvenBricksTE) {
				CokeOvenBricksTE te1 = (CokeOvenBricksTE) te;
				if(te1.getMaster() != null && te1.getMaster().getMBState()) {
					NetworkHooks.openGui((ServerPlayerEntity)player, (CombustionChamberTE)te1.getMaster(), te1.getMasterPos());
				}
				else {
					return ActionResultType.PASS;
				}
			}
			return ActionResultType.CONSUME;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState state1, boolean isMoving) {
		if(!state.is(state1.getBlock())) {
			TileEntity te = world.getBlockEntity(pos);
			if(te instanceof CokeOvenBricksTE) {
				CokeOvenBricksTE te1 = (CokeOvenBricksTE) te;
				if(te1.getMaster() != null) {
					BlockPos masterPos = te1.getMasterPos();
					te1.getMaster().setMBState(false);;
					if(!world.isClientSide) {
						for (ServerPlayerEntity player : ((ServerWorld)world).players())
							ModNetwork.CHANNEL.sendTo(new MultiblockSyncS2C(false, masterPos), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
					}
				}
			}
		}
		//world.setBlock(pos, world.getBlockState(pos).setValue(INVISIBLE, false), 3);
		super.onRemove(state, world, pos, state1, isMoving);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onPlace(BlockState state, World world, BlockPos pos, BlockState state1, boolean isMoving) {
		if(!state.is(state1.getBlock()))
			findMaster(pos, world);
		super.onPlace(state, world, pos, state1, isMoving);
	}
	
	public void findMaster(BlockPos pos, World world) {
		int rX = pos.getX(), rY = pos.getY(), rZ = pos.getZ();
		for(int y = rY - 1; y <= rY + 1; y++) {
			for(int z = rZ - 1; z <= rZ + 1; z++) {
				for(int x = rX - 1; x <= rX + 1; x++) {
					BlockPos chPos = new BlockPos(x, y, z);
					if(chPos == pos)
						continue;
					TileEntity te = world.getBlockEntity(chPos);
					if(te instanceof BaseMBControllerTE) {
						BaseMBControllerTE te1 = (BaseMBControllerTE) te;
						te1.checkMB();
					}
				}
			}
		}
	}

}
