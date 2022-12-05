package com.tranzistor.tranzistio.core.te;

import java.util.ArrayList;
import java.util.Arrays;

import com.tranzistor.tranzistio.core.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.energy.ModEnergyStorage;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

public class EnergyCableTileEntity extends TileEntity {

	public final ModEnergyStorage energyStorage;
	private LazyOptional<ModEnergyStorage> loEnergyStorage;
	public EnergyCableTileEntity master;
	public BlockPos masterPos;

	public EnergyCableTileEntity(TileEntityType<?> teType) {
		super(teType);
		this.energyStorage = new ModEnergyStorage(this, 2048, 1024);
		this.loEnergyStorage = LazyOptional.of(() -> this.energyStorage);
	}

	public EnergyCableTileEntity() {
		this(TileEntityTypesInit.ENERGY_CABLE_TILE_ENTITY.get());
	}

	public EnergyCableTileEntity getMaster() {
		try {
			tryLoadMaster();
			if (this.master != null && this.master != this) {
				return this.master.getMaster();
			}
		}
		catch(StackOverflowError e) {
			System.out.println();
		}	
		return this;
			
	}

	public int getMaxEnergy() {
		return this.getMaster().energyStorage.getMaxEnergyStored();
	}

	public int getEnergy() {
		return this.getMaster().energyStorage.getEnergyStored();
	}

	public void setMaster(EnergyCableTileEntity master) {
		if (master == this)
			return;
		if (master != null)
			master.energyStorage.setEnergy(this.getEnergy() + master.getEnergy());
		this.master = master;

	}

	public void setEnergy(int energy) {
		this.getMaster().energyStorage.setEnergy(energy);
	}

	public boolean isMaster() {
		tryLoadMaster();
		return this.master == null;
	}
	
	public void tryLoadMaster() {
		if(masterPos != null) {
			this.master = (EnergyCableTileEntity) this.getLevel().getBlockEntity(masterPos);
			this.masterPos = null;
		}
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (this.master != null) {
			compound.putInt("MasterX", this.master.getBlockPos().getX());
			compound.putInt("MasterY", this.master.getBlockPos().getY());
			compound.putInt("MasterZ", this.master.getBlockPos().getZ());
		} else
			compound.putInt("Energy", this.getEnergy());
		return super.save(compound);
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.setEnergy(compound.getInt("Energy"));
		if(compound.contains("MasterX") && compound.contains("MasterY") && compound.contains("MasterZ")) {
			masterPos = new BlockPos(compound.getInt("MasterX"), compound.getInt("MasterY"), compound.getInt("MasterZ"));
			if(masterPos.equals(this.getBlockPos())) {
				masterPos = null;
			}
		}
		super.load(state, compound);
	}

	@Override
	public void onLoad() {
		World world = this.getLevel();
		BlockPos pos = this.getBlockPos();
		ArrayList<BlockPos> poses = new ArrayList<>(Arrays.asList(pos.north(), pos.east(), pos.south(), pos.west(), pos.above(), pos.below()));
		for (BlockPos pos1 : poses) {
			TileEntity te = world.getBlockEntity(pos1);
			if (te instanceof EnergyCableTileEntity && te != null) {
				if (this.master == null) {
					this.master = ((EnergyCableTileEntity) te).getMaster();
				} else {
					((EnergyCableTileEntity) te).getMaster().setMaster(this.master);
				}
			}
		}
		System.out.println(this.getMaster().getBlockPos());
		super.onLoad();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityEnergy.ENERGY) {
			return this.loEnergyStorage.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		this.loEnergyStorage.invalidate();
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.save(new CompoundNBT());
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.getBlockPos(), 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.load(null, pkt.getTag());
	}

}
