package com.tranzistor.tranzistio.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import com.tranzistor.tranzistio.network.FluidSyncC2S;
import com.tranzistor.tranzistio.network.ModNetwork;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ModFluidUtil {
		
	public static FluidStack readFluid(JsonObject json) {
		return FluidStack.CODEC.decode(JsonOps.INSTANCE, json).result().orElseThrow(() -> {
				return new JsonSyntaxException("Invalid/Unknown fluid");
		}).getFirst();
	}
	
	public static JsonElement toJson(FluidStack stack) {
		return FluidStack.CODEC.encodeStart(JsonOps.INSTANCE, stack).result().get();
	}
	
	public static void operateWithBucket(ItemStack stack, FluidTank fluidStorage, LockableLootTileEntity te, int indexOfSlot) {
		stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler ->{
			int drainAmount = Math.min(fluidStorage.getSpace(), 1000);
			FluidStack stack1 = handler.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
			if(fluidStorage.isFluidValid(stack1) && canInjectFluid(stack1, fluidStorage)) {
				stack1 = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
				fillFluidStorage(stack1, handler.getContainer(), fluidStorage, te, indexOfSlot);
			}
		});
	}
	
	public static boolean canInjectFluid(FluidStack stack, FluidTank fluidStorage) {
		if(fluidStorage.getFluid().getFluid() != stack.getFluid() && fluidStorage.getFluid().getFluid() != Fluids.EMPTY)
			return false;
		if(fluidStorage.getSpace() == 0)
			return false;
		return true;
	}
	
	public static void fillFluidStorage(FluidStack stack, ItemStack container, FluidTank fluidStorage, LockableLootTileEntity te, int index) {
		fluidStorage.fill(stack, IFluidHandler.FluidAction.EXECUTE);
		te.setItem(index, container);
	}
	
	public static void fillFluidStorageFromHand(FluidTank fluidStorage, FluidStack stack, ItemStack container, PlayerEntity player, BlockPos pos) {
			fluidStorage.fill(stack, IFluidHandler.FluidAction.EXECUTE);
			player.setItemInHand(Hand.MAIN_HAND, container);
			ModNetwork.CHANNEL.sendToServer(new FluidSyncC2S(fluidStorage.getFluid(), pos, container));
	}
	
	public static void drainFluidStorageFromHand(FluidTank fluidStorage, ItemStack container, PlayerEntity player, BlockPos pos, int drainAmount) {
		fluidStorage.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
		player.setItemInHand(Hand.MAIN_HAND, container);
		if(fluidStorage.getFluidAmount() == 0) 
			fluidStorage.setFluid(FluidStack.EMPTY);
		ModNetwork.CHANNEL.sendToServer(new FluidSyncC2S(fluidStorage.getFluid(), pos, container));
	}
	
	public static void emptyTheFluidStorage(FluidTank fluidStorage, BlockPos pos) {
		fluidStorage.setFluid(FluidStack.EMPTY);
		ModNetwork.CHANNEL.sendToServer(new FluidSyncC2S(fluidStorage.getFluid(), pos, ItemStack.EMPTY));
	}
}
