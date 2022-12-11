package com.tranzistor.tranzistio.network;

import java.util.Optional;

import com.tranzistor.tranzistio.Tranzistio;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModNetwork {
	public static final String NETWORK_VERSION = "1.0";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Tranzistio.MOD_ID, "network"), () -> NETWORK_VERSION,
			ver -> ver.equals(NETWORK_VERSION), ver -> ver.equals(NETWORK_VERSION));
	
	public static void init() {
		CHANNEL.registerMessage(0, RefreshReceiversCachePacket.class,
				RefreshReceiversCachePacket::encode, RefreshReceiversCachePacket::decode,
				RefreshReceiversCachePacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}
}
