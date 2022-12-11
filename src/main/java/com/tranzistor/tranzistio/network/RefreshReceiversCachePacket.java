package com.tranzistor.tranzistio.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.tranzistor.tranzistio.energy.ModEnergyStorage;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.network.NetworkEvent;

public class RefreshReceiversCachePacket {

	public List<BlockPos> suppliers;
	public BlockPos ignorePos;
	
	public RefreshReceiversCachePacket(List<BlockPos> suppliers, BlockPos ignorePos) {
		this.suppliers = suppliers;
		this.ignorePos = ignorePos;
	}
	
	public static void encode(RefreshReceiversCachePacket packet, PacketBuffer buffer) {
		if (packet.ignorePos == null)
			buffer.writeBoolean(false);
		else {
			buffer.writeBoolean(true);
			buffer.writeBlockPos(packet.ignorePos);
		}
		buffer.writeInt(packet.suppliers.size());
		for (BlockPos pos : packet.suppliers)
			buffer.writeBlockPos(pos);
	}
	
	public static RefreshReceiversCachePacket decode(PacketBuffer buffer) {
		RefreshReceiversCachePacket packet = new RefreshReceiversCachePacket(new ArrayList<>(), buffer.readBoolean() ? buffer.readBlockPos() : null);
		for (int i = buffer.readInt(); i > 0; i--)
			packet.suppliers.add(buffer.readBlockPos());
		return packet;
	}
	
	public static void handle(RefreshReceiversCachePacket packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() -> {
			@SuppressWarnings("resource")
			World world = Minecraft.getInstance().level;
			for (BlockPos pos : packet.suppliers) {
				TileEntity te = world.getBlockEntity(pos);
				if (te != null)
					te.getCapability(CapabilityEnergy.ENERGY).ifPresent(es -> {
						if (es instanceof ModEnergyStorage && es.canExtract())
							((ModEnergyStorage)es).refreshReceiversCache(packet.ignorePos);
					});
			}
		});
		ctx.setPacketHandled(true);
	}
}
