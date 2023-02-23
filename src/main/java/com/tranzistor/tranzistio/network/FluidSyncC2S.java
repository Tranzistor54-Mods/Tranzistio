package com.tranzistor.tranzistio.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkEvent;

public class FluidSyncC2S {
		
	public FluidStack fluid;
	public BlockPos pos;
	public ItemStack container;
	
	public FluidSyncC2S() {}
	
	public FluidSyncC2S(FluidStack fluid, BlockPos pos, ItemStack container) {
		this.fluid = fluid;
		this.pos = pos;
		this.container = container;
	}
	
	public static void encode(FluidSyncC2S packet, PacketBuffer buffer) {
		buffer.writeFluidStack(packet.fluid);
		buffer.writeBlockPos(packet.pos);
		if(packet.container == null)
		   buffer.writeBoolean(false);
		else {
			buffer.writeBoolean(true);
			buffer.writeItem(packet.container);
		}
	}
	
	public static FluidSyncC2S decode(PacketBuffer buffer) {
		return new FluidSyncC2S(buffer.readFluidStack(), buffer.readBlockPos(), buffer.readBoolean() ? buffer.readItem() : null);
	}
	
	public static void handle(FluidSyncC2S packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			ServerPlayerEntity player = ctx.getSender();
			World world = player.getCommandSenderWorld();
			TileEntity tile = world.getBlockEntity(packet.pos);
			tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(te ->{
				if(te instanceof FluidTank)
					((FluidTank)te).setFluid(packet.fluid);
			});
				if(packet.container != null) {
					player.setItemInHand(Hand.MAIN_HAND, packet.container);
				}
		});
		ctx.setPacketHandled(true);
	}
}
