package com.tranzistor.tranzistio.network;

import java.util.function.Supplier;

import com.tranzistor.tranzistio.te.BaseMBControllerTE;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class MultiblockSyncS2C {
	
	public boolean mbIsFormed;
	public BlockPos masterPos;
	
	public MultiblockSyncS2C(boolean mbIsFormed, BlockPos masterPos) {
		this.mbIsFormed = mbIsFormed;
		this.masterPos = masterPos;
	}
	
	public static void encode(MultiblockSyncS2C packet, PacketBuffer buffer) {
		buffer.writeBoolean(packet.mbIsFormed);
		buffer.writeBlockPos(packet.masterPos);
	}
	
	public static MultiblockSyncS2C decode(PacketBuffer buffer) {
		MultiblockSyncS2C packet = new MultiblockSyncS2C(buffer.readBoolean(), buffer.readBlockPos());
		return packet;
	}
	
	public static void handle(MultiblockSyncS2C packet, Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		ctx.enqueueWork(() ->{
			@SuppressWarnings("resource")
			World world = Minecraft.getInstance().level;
			TileEntity te = world.getBlockEntity(packet.masterPos);
			if(te instanceof BaseMBControllerTE) {
				((BaseMBControllerTE)te).setMBState(packet.mbIsFormed);
			}
		});
		ctx.setPacketHandled(true);
	}
}
