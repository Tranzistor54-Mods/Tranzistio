package com.tranzistor.tranzistio.energy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class ModEnergyStorage extends EnergyStorage {
		public TileEntity tileEntity;
		protected HashMap<IEnergyStorage, Integer> receivers_cache; // target, max transfer rate
	
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
	    
	    public boolean spendEnergy(int amount) {
	    	if (this.getEnergyStored() < amount)
	    		return false;
	    	this.tileEntity.setChanged();
	    	this.setEnergy(this.getEnergyStored() - amount);
	    	return true;
	    }
	    
	    public ArrayList<Direction> getOutputDirections() {
	    	ArrayList<Direction> dirs = new ArrayList<>(Arrays.asList(Direction.values()));
			dirs.removeIf(dir -> tileEntity instanceof ISidedEnergyContainer && !((ISidedEnergyContainer)tileEntity).canOutputEnergy(dir));
			return dirs;
	    }
	    
	    public void transferEnergyAround() {
	    	if (!this.canExtract() || this.energy == 0) return;
	    	if (this.receivers_cache == null)
	    		this.refreshReceiversCache(null);
	    	else {
	    		this.receivers_cache.entrySet().removeIf(e -> !e.getKey().canReceive());
	    		for (Direction dir : this.getOutputDirections()) {
	    			TileEntity te = tileEntity.getLevel().getBlockEntity(tileEntity.getBlockPos().relative(dir));
	    			if (te != null)
	    				te.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).ifPresent(es -> this.addReceiver(es, this.maxExtract));
	    		}
	    	}
	    	HashMap<IEnergyStorage, Integer> can_receive = new HashMap<>(this.receivers_cache);
	    	can_receive.replaceAll((es, max) -> es.receiveEnergy(max, true));
	    	int energyLeftToTransfer = Math.min(this.energy, this.maxExtract);
	    	ArrayList<Entry<IEnergyStorage, Integer>> receivers = new ArrayList<>(can_receive.entrySet());
	    	receivers.sort((a,b) -> a.getValue().compareTo(b.getValue()));
	    	Iterator<Entry<IEnergyStorage, Integer>> iter = receivers.iterator();
	    	while (iter.hasNext() && energyLeftToTransfer > 0) {
	    		Entry<IEnergyStorage, Integer> target = iter.next();
	    		int toTransfer = Math.min(Math.max(energyLeftToTransfer/receivers.size(), 1), target.getValue());
	    		energyLeftToTransfer -= this.extractEnergy(target.getKey().receiveEnergy(toTransfer, false), false);
	    		iter.remove();
	    	}
	    }
	    
	    public void refreshReceiversCache(BlockPos ignorePos) {
	    	this.receivers_cache = new HashMap<>();
			EnergyNetwork.traverse(tileEntity.getLevel(), tileEntity.getBlockPos(), this.getOutputDirections(), this.maxExtract, ignorePos, this::addReceiver);
			System.out.println("Cache refreshed and now has "+this.receivers_cache.size()+" receivers");
		}
	    
	    public void addReceiver(IEnergyStorage target, Integer maxTransferRate) {
	    	if (receivers_cache == null ||(receivers_cache.containsKey(target) && receivers_cache.get(target) >= maxTransferRate) || !target.canReceive() || target == this)
	    		return;
	    	receivers_cache.put(target, maxTransferRate);
			System.out.println("New receiver "+target.getEnergyStored()+" in the cache which now has "+this.receivers_cache.size()+" receivers");
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
	    
	    public void setCapacity(int capacity) {
	    	this.capacity = capacity;
	    }
	    
	    public void setTransferRate(int transferRate) {
	    	this.maxExtract = transferRate;
	    	this.maxReceive = transferRate;
	    }
	    
	    @Override
	    public boolean canExtract()
	    {
	        return super.canExtract() && !tileEntity.isRemoved();
	    }

	    @Override
	    public boolean canReceive()
	    {
	        return super.canReceive() && !tileEntity.isRemoved();
	    }
}
