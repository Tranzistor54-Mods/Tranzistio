package com.tranzistor.tranzistio.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.containers.CrusherContainer;
import com.tranzistor.tranzistio.energy.ISidedEnergyContainer;
import com.tranzistor.tranzistio.energy.ModEnergyStorage;
import com.tranzistor.tranzistio.init.BlockInit;
import com.tranzistor.tranzistio.init.RecipesTypeInit;
import com.tranzistor.tranzistio.init.TileEntityTypesInit;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import com.tranzistor.tranzistio.recipes.CrusherRecipe;
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
import net.minecraftforge.items.IItemHandler;

public class CrusherTileEntity extends LockableLootTileEntity implements ITickableTileEntity, ISidedInventory, ISidedEnergyContainer {

	public final ModEnergyStorage energyStorage = new ModEnergyStorage(this, 8192, 32, 0);
	private LazyOptional<ModEnergyStorage> loEnergyStorage = LazyOptional.of(() -> this.energyStorage);
	public static int slots = 2;
	public int crushingProgress = 0, maxCrushingProgress = 0;
	private static final int[] ENTER_SLOT = new int[] { 0 };
	private static final int[] EXIT_SLOT = new int[] { 1 };
	protected NonNullList<ItemStack> items = NonNullList.withSize(slots, ItemStack.EMPTY);
	
	protected CrusherTileEntity(TileEntityType<?> te) {
		super(te);
	}
	
	public CrusherTileEntity() {
		this(TileEntityTypesInit.CRUSHER_TILE_ENTITY.get());
	}

	@Override
	public int getContainerSize() {
		return slots;
	}

	@Override
	public boolean canReceiveEnergy(Direction direction) {
		BlockState state = this.getLevel().getBlockState(this.getBlockPos());
		return state.getBlock() == BlockInit.CRUSHER.get() && state.getValue(HorizontalBlock.FACING) != direction;
	}

	@Override
	public boolean canOutputEnergy(Direction direction) {
		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		return direction == Direction.DOWN ? EXIT_SLOT : ENTER_SLOT;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction p_180462_3_) {
		return slot != 1;
	}
	
	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack p_180461_2_, Direction p_180461_3_) {
		return slot == 1;
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		ItemStackHelper.saveAllItems(compound, this.items);
		compound.putInt("CrushingProgress", this.crushingProgress);
		compound.putInt("MaxCrushingProgress", this.maxCrushingProgress);
		compound.putInt("Energy", this.energyStorage.getEnergyStored());
		return super.save(compound);
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		ItemStackHelper.loadAllItems(compound, this.items);
		this.crushingProgress = compound.getInt("CrushingProgress");
		this.maxCrushingProgress = compound.getInt("MaxCrushingProgress");
		this.energyStorage.setEnergy(compound.getInt("Energy"));
		super.load(state, compound);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void tick() {
		IRecipe<?> iRecipe = this.level.getRecipeManager().getRecipeFor(RecipesTypeInit.CRUSHING_RECIPE, this, this.level).orElse(null);
		boolean isNotFull = this.items.get(1).getCount() + this.getCountOfResult() < this.getMaxStackSize();
		ItemStack itemStack = this.items.get(0);
		if(this.crushingProgress == 0) {
			if(canCrush(iRecipe) && isNotFull && this.energyStorage.getEnergyStored() > this.getConsumeEnergyRate()) {
				this.maxCrushingProgress = this.getTotalCrushingTime();
				this.crushingProgress = this.getTotalCrushingTime();
			}
			else {
				this.maxCrushingProgress = 0;
			}
		}
		
		if(this.crushingProgress > 0 && this.energyStorage.spendEnergy(this.getConsumeEnergyRate())) {
			if(--crushingProgress == 0 && !itemStack.isEmpty()) {
				ItemStack itemStack1 = ((IRecipe<ISidedInventory>) iRecipe).assemble(this);
				ItemStack itemStack2 = this.items.get(1);
				itemStack.shrink(1);
				if(itemStack2.isEmpty()) 
					this.setItem(1, itemStack1.copy());
				else if(isNotFull)
					itemStack2.grow(this.getCountOfResult());
			}
			if(itemStack.isEmpty()) {
				this.maxCrushingProgress = 0;
				this.crushingProgress = 0;
			}
			this.setChanged();
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean canCrush(@Nullable IRecipe<?> iRecipe) {
		if (this.items.get(0).isEmpty() || iRecipe == null || this.energyStorage.getEnergyStored() < this.getConsumeEnergyRate())
			return false;
		ItemStack itemstack = ((IRecipe<ISidedInventory>) iRecipe).assemble(this);
		if (itemstack.isEmpty())
			return false;
		ItemStack itemstack1 = this.items.get(1);
		if (itemstack1.isEmpty())
			return true;
		if (!itemstack1.sameItem(itemstack))
			return false;
		int resultAmount = itemstack1.getCount() + itemstack.getCount() * this.getCountOfResult();
		return resultAmount <= this.getMaxStackSize() && resultAmount <= itemstack1.getMaxStackSize();
	}
	
	public int getTotalCrushingTime() {
		return this.level.getRecipeManager().getRecipeFor(RecipesTypeInit.CRUSHING_RECIPE, this, this.level)
				.map(CrusherRecipe::getCrushingTime).orElse(300);
	}
	
	public int getCountOfResult() {
		return this.level.getRecipeManager().getRecipeFor(RecipesTypeInit.CRUSHING_RECIPE, this, this.level).map(CrusherRecipe::getCount).orElse(1);
	}
	
	public int getConsumeEnergyRate() {
		return 32;
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
		return new TranslationTextComponent("container." + Tranzistio.MOD_ID + ".crusher");
	}

	@Override
	protected Container createMenu(int windowId, PlayerInventory playerInv) {
		return new CrusherContainer(windowId, playerInv, this);
	}
	
	public int getCrushingProgress() {
		return this.crushingProgress;
	}
	
	public int getMaxCrushingProgress() {
		return this.maxCrushingProgress;
	}
	
	@Override
	protected IItemHandler createUnSidedHandler() {
		return new net.minecraftforge.items.wrapper.InvWrapper(this) {
			@Override
			@Nonnull
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				if (slot != 1)
					return ItemStack.EMPTY;
				return super.extractItem(slot, amount, simulate);
			}

			@Override
			@Nonnull
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (slot == 1)
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
