package com.tranzistor.tranzistio.util;

import net.minecraft.state.BooleanProperty;

public interface IBaseMBPart {
	public static interface IBaseMBPartBlock {
		public static final BooleanProperty INVISIBLE = BooleanProperty.create("invisible");
	}
	
	public static interface IBaseMBPartController {
		public static final BooleanProperty FORMED = BooleanProperty.create("formed");
	}
}
