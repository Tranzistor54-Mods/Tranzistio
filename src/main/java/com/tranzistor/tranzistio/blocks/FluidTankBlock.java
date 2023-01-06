package com.tranzistor.tranzistio.blocks;

import java.util.List;

import com.tranzistor.tranzistio.init.BlockInit;
import com.tranzistor.tranzistio.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.te.FluidTankTileEntity;
import com.tranzistor.tranzistio.util.ModFluidUtil;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidTankBlock extends Block{
	
	public final int CAPACITY;
	public FluidTankBlock(int capacity) {
		super(AbstractBlock.Properties
				.of(Material.METAL)
				.strength(5f, 6f)
				.harvestLevel(2)
				.harvestTool(ToolType.PICKAXE)
				.requiresCorrectToolForDrops()
				.sound(SoundType.METAL)
				.noOcclusion());
		this.CAPACITY = capacity;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return TileEntityTypesInit.FLUID_TANK_TILE_ENTITY_BASE.get().create();
	}
	
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		FluidTankTileEntity te = (FluidTankTileEntity) world.getBlockEntity(pos);
		ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
		if(world.isClientSide) {
			stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler ->{
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
		return ActionResultType.PASS;
	}
	
	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		TileEntity tile = world.getBlockEntity(pos);
		if(tile instanceof FluidTankTileEntity) {
			if(!player.isCreative()) {
				FluidTankTileEntity te = (FluidTankTileEntity) tile;
				ItemStack stack = this.getTankByCapacity(CAPACITY);
				CompoundNBT nbt = te.saveToTag(new CompoundNBT());
				
				if(!nbt.isEmpty() && !te.getFluidStorage().getFluid().isEmpty())
					stack.addTagElement("BlockEntityTag", nbt);
				
				ItemEntity entity = new ItemEntity(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, stack);
				entity.setDefaultPickUpDelay();
				world.addFreshEntity(entity);
			}
		}
		super.playerWillDestroy(world, pos, state, player);
	}
	
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, IBlockReader bReader, List<ITextComponent> text, ITooltipFlag flag) {
		super.appendHoverText(stack, bReader, text, flag);
		CompoundNBT nbt = stack.getTagElement("BlockEntityTag");
		FluidTank fluidTank = new FluidTank(CAPACITY);
		if(nbt != null && nbt.contains("FluidStorage"))
		    fluidTank.readFromNBT(nbt.getCompound("FluidStorage"));
		if(fluidTank.getFluid().isEmpty())
		    text.add(new TranslationTextComponent("Empty: 0/" + fluidTank.getCapacity()));
		else {
		    String name = fluidTank.getFluid().getFluid().getAttributes().getDisplayName(fluidTank.getFluid()).getString();
		    text.add(new TranslationTextComponent(name + ": " + fluidTank.getFluidAmount() + "/" + fluidTank.getCapacity()));
		}
	}
	
	
	@Override
	public BlockRenderType getRenderShape(BlockState p_149645_1_) {
		return BlockRenderType.MODEL;
	}
	public ItemStack getTankByCapacity(int capacity) {
		return new ItemStack(BlockInit.FLUID_TANK_BASE.get());
	}
	
	

}
