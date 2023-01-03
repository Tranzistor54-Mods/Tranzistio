package com.tranzistor.tranzistio.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.blocks.FluidFiller;
import com.tranzistor.tranzistio.energy.ISidedEnergyContainer;
import com.tranzistor.tranzistio.energy.ModEnergyStorage;
import com.tranzistor.tranzistio.init.BlockInit;
import com.tranzistor.tranzistio.init.RecipesTypeInit;
import com.tranzistor.tranzistio.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.containers.FluidFillerContainer;
import com.tranzistor.tranzistio.recipes.FluidFillerRecipe;
import com.tranzistor.tranzistio.util.ModFluidUtil;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;

public class FluidFillerTileEntity extends LockableLootTileEntity implements ITickableTileEntity, ISidedInventory, ISidedEnergyContainer {
	
	public final ModEnergyStorage energyStorage;
	public final FluidTank fluidStorage;
	private LazyOptional<ModEnergyStorage> loEnergyStorage;
	private LazyOptional<FluidTank> loFluidStorage;
	public static int slots = 11;
	public int progress = 0, maxProgress = 0;
	private static final int[] ENTER_SLOTS = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, };
	private static final int[] EXIT_SLOTS = new int[] { 10 };
	protected NonNullList<ItemStack> items = NonNullList.withSize(slots, ItemStack.EMPTY);
	
	protected FluidFillerTileEntity(TileEntityType<?> te, int capacity, int consumeRate, int fluidCapacity) {
		super(te);
		this.energyStorage = new ModEnergyStorage(this, capacity, consumeRate, 0);
		this.loEnergyStorage = LazyOptional.of(() -> this.energyStorage);
		this.fluidStorage = new FluidTank(fluidCapacity);
		this.loFluidStorage = LazyOptional.of(() -> this.fluidStorage);
	}
	
	public FluidFillerTileEntity(int capacity, int consumeRate, int fluidCapacity) {
		this(TileEntityTypesInit.FLUID_FILLER_TILE_ENTITY.get(), capacity, consumeRate, fluidCapacity);
	}

	@Override
	public int getContainerSize() {
		return slots;
	}

	@Override
	public boolean canReceiveEnergy(Direction direction) {
		BlockState state = this.getLevel().getBlockState(this.getBlockPos());
		return state.getBlock() == BlockInit.FLUID_FILLER.get() && state.getValue(HorizontalBlock.FACING) != direction;
	}

	@Override
	public boolean canOutputEnergy(Direction direction) {
		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		return direction == Direction.DOWN ? EXIT_SLOTS : ENTER_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction p_180462_3_) {
		return slot != 10;
	}
	
	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack p_180461_2_, Direction p_180461_3_) {
		return slot == 10;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		CompoundNBT nbt = new CompoundNBT();
		fluidStorage.writeToNBT(nbt);
		ItemStackHelper.saveAllItems(compound, this.items);
		compound.put("FluidStorage", nbt);
		compound.putInt("Progress", this.progress);
		compound.putInt("MaxProgress", this.maxProgress);
		compound.putInt("Energy", this.energyStorage.getEnergyStored());
		return super.save(compound);
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		ItemStackHelper.loadAllItems(compound, this.items);
		this.progress = compound.getInt("Progress");
		this.maxProgress = compound.getInt("MaxProgress");
		this.energyStorage.setEnergy(compound.getInt("Energy"));
		this.fluidStorage.readFromNBT(compound.getCompound("FluidStorage"));
		super.load(state, compound);
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void tick() {
		IRecipe<?> iRecipe = this.level.getRecipeManager().getRecipeFor(RecipesTypeInit.FLUID_FILLING_RECIPE, this, this.level).orElse(null);
		boolean isNotFull = this.items.get(10).getCount() < this.getMaxStackSize();
		ItemStack stack = this.items.get(0);
		if(!stack.isEmpty() && stack.getItem() != Items.MILK_BUCKET) {
			ModFluidUtil.operateWithBucket(stack, fluidStorage, this, 0);
			
		}
		if(this.canFill(iRecipe)) {
			if(this.progress == 0) {
				if(isNotFull && this.energyStorage.getEnergyStored() > this.getConsumeEnergyRate()) {
					this.maxProgress = this.getTotalWorkingTime();
					this.progress = this.getTotalWorkingTime();
					this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(FluidFiller.WORKING, true), 3);
				}
				else {
					this.maxProgress = 0;
				}
			}
			if(this.progress > 0 && this.energyStorage.spendEnergy(this.getConsumeEnergyRate())) {
				if(--progress == 0) {
					ItemStack stack1 = ((IRecipe<ISidedInventory>) iRecipe).assemble(this);
					//this.shrinkItemsInGrid();
					this.fluidStorage.drain(this.getFluidForRecipe().getAmount(), FluidAction.EXECUTE);
					this.shrinkItemsInGrid();
					ItemStack stack2 = this.items.get(10);
					if(stack2.isEmpty()) 
						this.setItem(10, stack1.copy());
					else if(isNotFull) 
						stack2.grow(stack1.getCount());
				}
			}
		}
		else {
			this.progress = 0;
			this.maxProgress = 0;
			this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(FluidFiller.WORKING, false), 3);
		}
		this.setChanged();
	}
	
	@SuppressWarnings("unchecked")
	public boolean canFill(@Nullable IRecipe<?> iRecipe) {
		if(iRecipe == null || this.energyStorage.getEnergyStored() < this.getConsumeEnergyRate() || this.getFluid() != this.getFluidForRecipe().getFluid() || this.fluidStorage.getFluidAmount() < this.getFluidForRecipe().getAmount())
			return false;
		ItemStack stack = ((IRecipe<ISidedInventory>) iRecipe).assemble(this);
		if(stack.isEmpty())
			return false;
		ItemStack stack1 = this.items.get(10);
		if(stack1.isEmpty()) 
			return true;
		if(!stack1.sameItem(stack))
			return false;
		int resultAmount = stack1.getCount() + stack.getCount();
		return resultAmount <= this.getMaxStackSize() && resultAmount <= stack1.getMaxStackSize();
	}
	
	public boolean checkItems() {
		for(int i = 1; i < 10; i++) {
			if(this.items.get(i).isEmpty())
				return false;
		}
		return true;
	}
	
	public void shrinkItemsInGrid() {
		for(int i = 1; i < 10; i++) {
			this.items.get(i).shrink(1);
		}
	}
	
	public int getConsumeEnergyRate() {
		return 64;
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
	
	public FluidStack getFluidForRecipe() {
		return this.level.getRecipeManager().getRecipeFor(RecipesTypeInit.FLUID_FILLING_RECIPE, this, this.level)
				.map(FluidFillerRecipe::getFluid).orElse(null);
	}
	
	public int getTotalWorkingTime() {
		return this.level.getRecipeManager().getRecipeFor(RecipesTypeInit.FLUID_FILLING_RECIPE, this, this.level)
				.map(FluidFillerRecipe::getWorkingTime).orElse(350);
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> itemsIn) {
		this.items = itemsIn;
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + Tranzistio.MOD_ID + ".fluid_filler");
	}

	@Override
	protected Container createMenu(int windowId, PlayerInventory playerInv) {
		return new FluidFillerContainer(windowId, playerInv, this);
	}
	
	public int getProgress() {
		return this.progress;
	}
	
	public int getMaxProgress() {
		return this.maxProgress;
	}
	
	@Override
	protected IItemHandler createUnSidedHandler() {
		return new net.minecraftforge.items.wrapper.InvWrapper(this) {
			@Override
			@Nonnull
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				if (slot != 10)
					return ItemStack.EMPTY;
				return super.extractItem(slot, amount, simulate);
			}

			@Override
			@Nonnull
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (slot == 10)
					return stack;
				return super.insertItem(slot, stack, simulate);
			}
		};
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityEnergy.ENERGY && this.canReceiveEnergy(side)) {
			return this.loEnergyStorage.cast();
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return this.loFluidStorage.cast();
		}
		
		return super.getCapability(cap, side);
	}

	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		this.loEnergyStorage.invalidate();
		this.loFluidStorage.invalidate();
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
/*	public void operateWithBucket(ItemStack stack) {
		stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
			int drainAmount = Math.min(this.getSpace(), 1000);
			FluidStack stack1 = handler.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
			if(this.fluidStorage.isFluidValid(stack1) && canInjectFluid(stack1)) {
				stack1 = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
				this.fillFluidStorage(stack1, handler.getContainer());
			}
		});
	}
	
	public boolean canInjectFluid(FluidStack stack) {
		if(this.fluidStorage.getFluid() == stack && this.getFluid() != Fluids.EMPTY) 
			return true;
		if(this.getSpace() != 0)
			return true;
		return false;
	}
	
	public void fillFluidStorage(FluidStack stack, ItemStack container) {
		this.fluidStorage.fill(stack, IFluidHandler.FluidAction.EXECUTE);
		this.setItem(0, container);
	}
	
	public void fillFluidStorageFromHand(FluidStack stack, PlayerEntity player, ItemStack container) {
		this.fluidStorage.fill(stack, IFluidHandler.FluidAction.EXECUTE);
		player.setItemInHand(Hand.MAIN_HAND, container);
		ModNetwork.CHANNEL.sendToServer(new FluidSyncC2S(this.fluidStorage.getFluid(), worldPosition, container));
	}
	
	public void drainFluidStorageFromHand(PlayerEntity player, int drainAmount, ItemStack container) {
		this.fluidStorage.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
		player.setItemInHand(Hand.MAIN_HAND, container);
		if(this.fluidStorage.getFluidAmount() == 0) {
			this.fluidStorage.setFluid(new FluidStack(FluidStack.EMPTY, 0));
		}
		ModNetwork.CHANNEL.sendToServer(new FluidSyncC2S(this.fluidStorage.getFluid(), worldPosition, container));
	}
	
	public void updateFluidStorage() {
		this.fluidStorage.drain(this.getFluidAmount(), FluidAction.EXECUTE);
		this.fluidStorage.setFluid(new FluidStack(Fluids.EMPTY, 0));
			ModNetwork.CHANNEL.sendToServer(new FluidSyncC2S(this.fluidStorage.getFluid(), worldPosition, ItemStack.EMPTY));
	}*/

