package com.tranzistor.tranzistio.te;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;

public class BaseMBPartTE extends TileEntity {
	
	protected BaseMBControllerTE master = null;
	protected BlockPos masterPos = null;
	
	public BaseMBPartTE(TileEntityType<?> te) {
		super(te);
	}
	
	public void setMaster(BaseMBControllerTE master) {
		this.master = master;
		if(master != null)
			this.masterPos = master.getBlockPos();
		else
			this.masterPos = null;
	}
	
	public BaseMBControllerTE getMaster() {
		if(this.master == null && this.masterPos != null) {
			TileEntity te = this.level.getBlockEntity(masterPos);
			if(te instanceof BaseMBControllerTE)
				return (BaseMBControllerTE)te;
		}
		return this.master;
	}
	
	public BlockPos getMasterPos() {
		return this.masterPos;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
	}
	
    protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
	   if(this.master != null)
		   return new net.minecraftforge.items.wrapper.InvWrapper(this.master);
	   return null;
    }
}
