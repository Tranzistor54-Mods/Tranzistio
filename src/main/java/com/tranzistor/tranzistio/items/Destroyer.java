package com.tranzistor.tranzistio.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class Destroyer extends Item {

	public Destroyer(Properties properties) {
		super(properties);
	}

	@Override
	public boolean mineBlock(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
		int xPos = pos.getX(), yPos = pos.getY(), zPos = pos.getZ();
		Vector3d playerRotation = entity.getViewVector(0);
		Vector3d rayPath = playerRotation.scale(100);
		Vector3d from = entity.getEyePosition(0);
		Vector3d to = from.add(rayPath);
		RayTraceContext rayCtx = new RayTraceContext(from, to, RayTraceContext.BlockMode.OUTLINE,
				RayTraceContext.FluidMode.ANY, null);
		BlockRayTraceResult rayHit = world.clip(rayCtx);
		Direction direction = rayHit.getDirection();

		if (!isCorrectToolForDrops(state))
			return false;
		
		switch (direction) {
		case NORTH:
		case SOUTH:
			for (int i = xPos - 1; i < xPos + 2; i++) {
				for (int j = yPos - 1; j < yPos + 2; j++) {
					BlockPos pos2 = new BlockPos(i, j, zPos);
					BlockState newState = world.getBlockState(pos2);
					if (newState.getHarvestTool() == ToolType.PICKAXE && newState.getHarvestLevel() <= 2) {
						world.destroyBlock(pos2, true);
					}
				}
			}
			break;
		case EAST:
		case WEST:
			for (int i = zPos - 1; i < zPos + 2; i++) {
				for (int j = yPos - 1; j < yPos + 2; j++) {
					BlockPos pos2 = new BlockPos(xPos, j, i);
					BlockState newState = world.getBlockState(pos2);
					if (newState.getHarvestTool() == ToolType.PICKAXE && newState.getHarvestLevel() <= 2) {
						world.destroyBlock(pos2, true);
					}
				}
			}
			break;
		case UP:
		case DOWN:
			for (int i = xPos - 1; i < xPos + 2; i++) {
				for (int j = zPos - 1; j < zPos + 2; j++) {
					BlockPos pos2 = new BlockPos(i, yPos, j);
					BlockState newState = world.getBlockState(pos2);
					if (newState.getHarvestTool() == ToolType.PICKAXE && newState.getHarvestLevel() <= 2) {
						world.destroyBlock(pos2, true);
					}
				}
			}
		}

		if (!world.isClientSide && state.getDestroySpeed(world, pos) != 0.0F) {
			stack.hurtAndBreak(1, entity, (p_220038_0_) -> {
				p_220038_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
			});
		}

		return true;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return state.getHarvestTool() == ToolType.PICKAXE && state.getHarvestLevel() <= 2 ? 12f : 1f;
	}

	@Override
	public boolean isDamageable(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isCorrectToolForDrops(BlockState state) {
		return state.getHarvestTool() == ToolType.PICKAXE && state.getHarvestLevel() <= 2;
	}
}
