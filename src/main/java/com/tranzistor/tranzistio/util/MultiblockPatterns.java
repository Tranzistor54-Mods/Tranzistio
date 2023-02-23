package com.tranzistor.tranzistio.util;

import com.tranzistor.tranzistio.init.BlockInit;

import net.minecraft.block.Block;

public class MultiblockPatterns {
	public static int[][][] cokeOvenPat = {
		{
			{0, 0, 0},
			{0, 0, 0},
			{0, 0, 0}
		},
		{
			{0, 0, 0},
			{0, 1, 0},
			{0, 0, 0}
		},
		{
			{0, 0, 0},
			{0, 0, 0},
			{0, 0, 0}
		}
	};
	
	public static Block[] applBlocks = {
			BlockInit.COKE_OVEN_BRICKS.get(),
			BlockInit.COMBUSTION_CHAMBER.get()
	};
}
