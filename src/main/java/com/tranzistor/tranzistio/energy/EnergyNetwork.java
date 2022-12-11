package com.tranzistor.tranzistio.energy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.tranzistor.tranzistio.blocks.EnergyCable;
import com.tranzistor.tranzistio.network.ModNetwork;
import com.tranzistor.tranzistio.network.RefreshReceiversCachePacket;

import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.network.NetworkDirection;

public class EnergyNetwork {

	public static void traverse(IBlockReader world, BlockPos origin, Iterable<Direction> outDirs, int initialTransferRate, BlockPos ignorePos, BiConsumer<IEnergyStorage, Integer> action) {
		HashMap<BlockPos, Tuple<Integer, Direction>> network = new LinkedHashMap<>();
		int i = 0;
		for (Direction outDir : outDirs) {
			BlockPos outPos = origin.relative(outDir);
			if (outPos.equals(ignorePos))
				continue;
			network.put(outPos, new Tuple<>(initialTransferRate, outDir.getOpposite()));
			while (i < network.size()) {
				Iterator<Entry<BlockPos, Tuple<Integer, Direction>>> iter = network.entrySet().iterator();
				for (int j = 0; j < i; j++)
					iter.next();
				Entry<BlockPos, Tuple<Integer, Direction>> current = iter.next();
				Block block = world.getBlockState(current.getKey()).getBlock();
				if (block instanceof EnergyCable) {
					int newRate = Math.min(current.getValue().getA(), ((EnergyCable)block).getTransferRate());
					for (Direction dir : Direction.values()) {
						if (dir == current.getValue().getB())
							continue;
						BlockPos pos = current.getKey().relative(dir);
						if ((!network.containsKey(pos) || network.get(pos).getA() < newRate) && !pos.equals(ignorePos))
							network.put(pos, new Tuple<>(newRate, dir.getOpposite()));
					}
				} else {
					TileEntity te = world.getBlockEntity(current.getKey());
					if (te != null)
						te.getCapability(CapabilityEnergy.ENERGY, current.getValue().getB()).ifPresent(es -> {
							action.accept(es, current.getValue().getA());
						});
				}
				i++;
			}
		}
	}

	public static void traverse(IBlockReader world, BlockPos origin, Iterable<Direction> outDirs, BiConsumer<IEnergyStorage, Entry<BlockPos, Direction>> action) {
		HashMap<BlockPos, Direction> network = new LinkedHashMap<>();
		int i = 0;
		for (Direction dir : Direction.values()) {
			BlockPos nearPos = origin.relative(dir);
			if (network.containsKey(nearPos) && world.getBlockState(nearPos).getBlock() instanceof EnergyCable)
				continue;
			network.put(nearPos, dir.getOpposite());
			while (i < network.size()) {
				Iterator<Entry<BlockPos, Direction>> iter = network.entrySet().iterator();
				for (int j = 0; j < i; j++)
					iter.next();
				Entry<BlockPos, Direction> current = iter.next();
				Block block = world.getBlockState(current.getKey()).getBlock();
				if (block instanceof EnergyCable) {
					for (Direction dir1 : Direction.values()) {
						if (dir1 == current.getValue())
							continue;
						BlockPos pos1 = current.getKey().relative(dir);
						if (!network.containsKey(pos1))
							network.put(pos1, dir.getOpposite());
					}
				} else {
					TileEntity te = world.getBlockEntity(current.getKey());
					if (te != null)
						te.getCapability(CapabilityEnergy.ENERGY, current.getValue()).ifPresent(es -> {
							action.accept(es, current);
						});
				}
				i++;
			}
		}
	}

	public static void refresh(World world, BlockPos pos, BlockPos ignorePos) {
		ArrayList<BlockPos> refreshed = new ArrayList<>();
		traverse(world, pos, Arrays.asList(Direction.values()), (es, loc) -> {
					if (es instanceof ModEnergyStorage && es.canExtract()) {
						((ModEnergyStorage)es).refreshReceiversCache(ignorePos);
						refreshed.add(loc.getKey());
					}
				});
		if (!refreshed.isEmpty()) {
			for (ServerPlayerEntity player : ((ServerWorld)world).players())
				ModNetwork.CHANNEL.sendTo(new RefreshReceiversCachePacket(refreshed, ignorePos), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}
