package com.tranzistor.tranzistio.te;

import com.tranzistor.tranzistio.init.TileEntityTypesInit;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

public class CokeOvenBricksTE extends BaseMBPartTE{

	public CokeOvenBricksTE(TileEntityType<?> te) {
		super(te);
	}
	
	public CokeOvenBricksTE() {
		this(TileEntityTypesInit.COKE_OVEN_BRICKS_TILE_ENTITY.get());
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if(this.masterPos != null) {
			compound.putInt("MasterX", this.masterPos.getX());
			compound.putInt("MasterY", this.masterPos.getY());
			compound.putInt("MasterZ", this.masterPos.getZ());
		}
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		int x = compound.getInt("MasterX");
		int y = compound.getInt("MasterY");
		int z = compound.getInt("MasterZ");
		this.masterPos = new BlockPos(x, y, z);
		super.load(state, compound);
	}
}
