package com.tranzistor.tranzistio.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.blocks.ElectricFurnace;
import com.tranzistor.tranzistio.containers.ElectricFurnaceContainer;
import com.tranzistor.tranzistio.energy.ISidedEnergyContainer;
import com.tranzistor.tranzistio.energy.ModEnergyStorage;
import com.tranzistor.tranzistio.init.BlockInit;
import com.tranzistor.tranzistio.init.TileEntityTypesInit;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
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

public class ElectricFurnaceTileEntity extends LockableLootTileEntity
		implements ITickableTileEntity, ISidedInventory, ISidedEnergyContainer {
	public final ModEnergyStorage energyStorage = new ModEnergyStorage(this, 8192, 32, 0);
	private LazyOptional<ModEnergyStorage> loEnergyStorage = LazyOptional.of(() -> this.energyStorage);
	public static int slots = 2;
	public int smeltingProgress = 0, maxSmeltingProgress = 0;
	private static final int[] ENTER_SLOT = new int[] { 0 };
	private static final int[] EXIT_SLOT = new int[] { 1 };
	protected NonNullList<ItemStack> items = NonNullList.withSize(slots, ItemStack.EMPTY);

	protected ElectricFurnaceTileEntity(TileEntityType<?> te) {
		super(te);
	}

	public ElectricFurnaceTileEntity() {
		this(TileEntityTypesInit.ELECTRIC_FURNACE_TILE_ENTITY.get());
	}

	@Override
	public int getContainerSize() {
		return slots;
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
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction p_180461_3_) {
		return slot == 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void tick() {
		IRecipe<?> iRecipe = this.level.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, this, this.level).orElse(null);
		boolean isNotFull = this.items.get(1).getCount() < this.getMaxStackSize();
		ItemStack itemStack = this.items.get(0);
		if (this.smeltingProgress == 0) {
			if (canSmelt(iRecipe) && isNotFull && this.energyStorage.getEnergyStored() > this.getConsumeEnergyRate()) {
				this.maxSmeltingProgress = (int) (this.getTotalCookTime() * 0.8) / 10;
				this.smeltingProgress = (int) (this.getTotalCookTime() * 0.8) / 10;
				this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ElectricFurnace.WORKING, true), 3);
			} else {
				this.maxSmeltingProgress = 0;
				this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(ElectricFurnace.WORKING, false), 3);
			  }
		}

		if (this.smeltingProgress > 0 && this.energyStorage.spendEnergy(this.getConsumeEnergyRate())) {
			if (--smeltingProgress == 0 && !itemStack.isEmpty()) {
				ItemStack itemStack1 = ((IRecipe<ISidedInventory>) iRecipe).assemble(this);
				ItemStack itemStack2 = this.items.get(1);
				itemStack.shrink(1);
				if (itemStack2.isEmpty())
					this.setItem(1, itemStack1.copy());
				else
					itemStack2.grow(1);
			}
			if (itemStack.isEmpty()) {
				this.maxSmeltingProgress = 0;
				this.smeltingProgress = 0;
			}
			this.setChanged();
		}
	}

	@SuppressWarnings("unchecked")
	private boolean canSmelt(@Nullable IRecipe<?> iRecipe) {
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
		int resultAmount = itemstack1.getCount() + itemstack.getCount();
		return resultAmount <= this.getMaxStackSize() && resultAmount <= itemstack1.getMaxStackSize();
	}

	protected int getTotalCookTime() {
		return this.level.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, this, this.level)
				.map(AbstractCookingRecipe::getCookingTime).orElse(200);
	}

	public int getConsumeEnergyRate() {
		return 2;
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
		return new TranslationTextComponent("container." + Tranzistio.MOD_ID + ".electric_furnace");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory playerInventory) {
		return new ElectricFurnaceContainer(id, playerInventory, this);
	}

	public int getMaxProgress() {
		return this.maxSmeltingProgress;
	}

	public int getProgress() {
		return this.smeltingProgress;
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
	public CompoundNBT save(CompoundNBT compound) {
		ItemStackHelper.saveAllItems(compound, this.items);
		compound.putInt("smeltingProgress", this.smeltingProgress);
		compound.putInt("MaxSmeltingProgress", this.maxSmeltingProgress);
		compound.putInt("Energy", this.energyStorage.getEnergyStored());
		return super.save(compound);
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		ItemStackHelper.loadAllItems(compound, this.items);
		this.smeltingProgress = compound.getInt("smeltingProgress");
		this.maxSmeltingProgress = compound.getInt("MaxSmeltingProgress");
		this.energyStorage.setEnergy(compound.getInt("Energy"));
		super.load(state, compound);
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

	@Override
	public boolean canReceiveEnergy(Direction direction) {
		BlockState state = this.getLevel().getBlockState(this.getBlockPos());
		return state.getBlock() == BlockInit.ELECTRIC_FURNACE.get() && state.getValue(HorizontalBlock.FACING) != direction;
	}

	@Override
	public boolean canOutputEnergy(Direction direction) {
		return false;
	}
}
