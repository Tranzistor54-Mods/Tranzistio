package com.tranzistor.tranzistio.energy;

import net.minecraft.util.Direction;

public interface ISidedEnergyContainer {
	boolean canReceiveEnergy(Direction direction);
	boolean canOutputEnergy(Direction direction);
}
