package com.tranzistor.tranzistio.te;

import com.tranzistor.tranzistio.init.TileEntityTypesInit;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class FluidTankTileEntity extends TileEntity{
	public final FluidTank fluidStorage;
	private LazyOptional<FluidTank> loFluidStorage;

	public FluidTankTileEntity(TileEntityType<?> te, int capacity) {
		super(te);
		this.fluidStorage = new FluidTank(capacity);
		this.loFluidStorage = LazyOptional.of(() -> this.fluidStorage);
	}
	
	public FluidTankTileEntity(int capacity) {
		this(TileEntityTypesInit.FLUID_TANK_TILE_ENTITY_BASE.get(), capacity);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		CompoundNBT nbt = new CompoundNBT();
		fluidStorage.writeToNBT(nbt);
		compound.put("FluidStorage", nbt);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.fluidStorage.readFromNBT(compound.getCompound("FluidStorage"));
		super.load(state, compound);
	}
	
	public Fluid getFluid() {
		return this.fluidStorage.getFluid().getRawFluid();
	}
	
	public int getCapacity() {
		return this.fluidStorage.getCapacity();
	}
	
	public int getSpace() {
		return this.fluidStorage.getSpace();
	}
	
	public int getFluidAmount() {
		return this.fluidStorage.getFluidAmount();
	}
	
	public void setFluid(FluidStack stack) {
		this.fluidStorage.setFluid(stack);
	}
	
	public FluidTank getFluidStorage() {
		return this.fluidStorage;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return this.loFluidStorage.cast();
		}
		return super.getCapability(cap, side);
	}
	
	
	@Override
	protected void invalidateCaps() {
		this.loFluidStorage.invalidate();
		super.invalidateCaps();
	}
	
	public CompoundNBT saveToTag(CompoundNBT tag) {
		CompoundNBT nbt = new CompoundNBT();
		fluidStorage.writeToNBT(nbt);
		tag.put("FluidStorage", nbt);
		return tag;
	}
	
	public void loadFromTag(CompoundNBT tag) {
		fluidStorage.readFromNBT(tag.getCompound("FluidStorage"));
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
