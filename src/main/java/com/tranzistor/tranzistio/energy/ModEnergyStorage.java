package com.tranzistor.tranzistio.energy;


import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.EnergyStorage;

public class ModEnergyStorage extends EnergyStorage{
		public TileEntity tileEntity;
	
	    public ModEnergyStorage(TileEntity tileEntity, int capacity)
	    {
		   super(capacity, capacity, capacity, 0);
		   this.tileEntity = tileEntity;
	    }

	    public ModEnergyStorage(TileEntity tileEntity, int capacity, int maxTransfer)
	    {
	    	super(capacity, maxTransfer, maxTransfer, 0);
	    	this.tileEntity = tileEntity;
	    }

	    public ModEnergyStorage(TileEntity tileEntity, int capacity, int maxReceive, int maxExtract)
	    {
	        super(capacity, maxReceive, maxExtract, 0);
	        this.tileEntity = tileEntity;
	    }
	    
	    public ModEnergyStorage(TileEntity tileEntity, int capacity, int maxReceive, int maxExtract, int energy)
	    {
	    	 super(capacity, maxReceive, maxExtract, energy);
	    	 this.tileEntity = tileEntity;
	    }
	    
	    @Override
	    public int receiveEnergy(int maxReceive, boolean simulate) {
	    	this.tileEntity.setChanged();
	    	return super.receiveEnergy(maxReceive, simulate);
	    }
	    
	    @Override
	    public int extractEnergy(int maxExtract, boolean simulate) {
	    	this.tileEntity.setChanged();
	    	return super.extractEnergy(maxExtract, simulate);
	    }
	    
	    public void readFromNBT(CompoundNBT compound) {
	    	this.energy = compound.getInt("Energy");
	    	this.maxReceive = compound.getInt("MaxRecieve");
	    	this.maxExtract = compound.getInt("MaxExtract");
	    	this.capacity = compound.getInt("Capacity");
	    }
	    
	    public void writeToNBT(CompoundNBT compound) {
	    	compound.putInt("Energy", this.energy);
	    	compound.putInt("MaxReceive", this.maxReceive);
	    	compound.putInt("MaxExtract", this.maxExtract);
	    	compound.putInt("Capacity", this.capacity);
	    }
	    
	    public void setEnergy(int energy) {
	    	this.energy = Math.max(0, Math.min(energy, this.capacity));
	    }
	    
	    
	    
}
