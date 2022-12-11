package com.tranzistor.tranzistio.te;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.containers.CoalGeneratorContainer;
import com.tranzistor.tranzistio.energy.ISidedEnergyContainer;
import com.tranzistor.tranzistio.energy.ModEnergyStorage;
import com.tranzistor.tranzistio.init.BlockInit;
import com.tranzistor.tranzistio.init.ItemInit;
import com.tranzistor.tranzistio.init.TileEntityTypesInit;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandler;


public class CoalGeneratorTileEntity extends LockableLootTileEntity implements ITickableTileEntity, ISidedInventory, ISidedEnergyContainer {
	
	public final ModEnergyStorage energyStorage;
	public static int slots = 2;
	public int progress = 0, maxProgress = 0;
	private static final int[] FUEL_SLOTS = new int[]{0};
	private static final int[] ASH_SLOTS = new int[]{1};
	protected NonNullList<ItemStack> items = NonNullList.withSize(slots, ItemStack.EMPTY);
	private LazyOptional<ModEnergyStorage> loEnergyStorage;

	public CoalGeneratorTileEntity(TileEntityType<?> tileEntityType) {
		super(tileEntityType);
		this.energyStorage = new ModEnergyStorage(this, 8192, 0, 32);
		this.loEnergyStorage = LazyOptional.of(() -> this.energyStorage);
		this.isEmpty();
	}

	public CoalGeneratorTileEntity() {
		this(TileEntityTypesInit.COAL_GENERATOR_TILE_ENTITY.get());
	}

	public int getMaxProgress() {
		return this.maxProgress;
	}

	public int getProgress() {
		return this.progress;
	}

	public static int getBurnTime(ItemStack stack) {
		return ForgeHooks.getBurnTime(stack, IRecipeType.SMELTING);
	}
	
	public int getProductionRate() {
		return 32;
	}
	
	@Override
	protected IItemHandler createUnSidedHandler() {
		return new net.minecraftforge.items.wrapper.InvWrapper(this) {
			@Override
		    @Nonnull
		    public ItemStack extractItem(int slot, int amount, boolean simulate)
		    {
				if (slot != 1)
					return ItemStack.EMPTY;
				return super.extractItem(slot, amount, simulate);
		    }
			@Override
		    @Nonnull
		    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
		    {
				if (slot == 1)
					return stack;
				return super.insertItem(slot, stack, simulate);
		    }
		};
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if(cap == CapabilityEnergy.ENERGY && this.canOutputEnergy(side)) {
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
		super.load(state, compound);
	}

	@Override
	public void tick() {
		ItemStack stack = this.getItem(0);
		int fuelValue = getBurnTime(stack);
		boolean isNotFull = this.energyStorage.getEnergyStored() < this.energyStorage.getMaxEnergyStored() && this.items.get(1).getCount() < this.getMaxStackSize();
		if (this.progress == 0) {
			if (fuelValue > 0 && isNotFull) {
				this.maxProgress = fuelValue;
				this.progress = fuelValue;
				stack.shrink(1);
			} else
				this.maxProgress = 0;
		}
		
		if (this.progress > 0) {
			this.energyStorage.setEnergy(this.energyStorage.getEnergyStored() + getProductionRate());
			if (--progress == 0) {
				ItemStack ash = this.getItem(1);
				if (ash.isEmpty())
					this.setItem(1, new ItemStack(ItemInit.ASH.get()));
				else
					ash.grow(1);
			}
			this.setChanged();
		}
		this.energyStorage.transferEnergyAround();
	}
	
	@Override
	public int getContainerSize() {
		return slots;
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
		return new TranslationTextComponent("container." + Tranzistio.MOD_ID + ".coal_generator");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory playerInventory) {
		return new CoalGeneratorContainer(id, playerInventory, this);
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		return direction == Direction.DOWN ? ASH_SLOTS : FUEL_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
		if (slot == 1)
			return false;
		return getBurnTime(stack) > 0;
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return slot == 1;
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
		return false;
	}

	@Override
	public boolean canOutputEnergy(Direction direction) {
		BlockState state = this.getLevel().getBlockState(this.getBlockPos());
		return state.getBlock() == BlockInit.COAL_GENERATOR.get() && state.getValue(HorizontalBlock.FACING) != direction;
	}
}






