package com.tranzistor.tranzistio.core.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.core.containers.CoalGeneratorContainer;
import com.tranzistor.tranzistio.core.containers.ElectricFurnaceContainer;
import com.tranzistor.tranzistio.energy.ModEnergyStorage;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.datafix.fixes.FurnaceRecipes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ElectricFurnaceTileEntity extends AbstractFurnaceTileEntity implements ITickableTileEntity, ISidedInventory{
	
	
	public final ModEnergyStorage energyStorage;
	public static int slots = 2;
	public int smeltingProgress = 0, maxSmeltingProgress = 0;
	private static final int[] ENTER_SLOT = new int[] {0};
	private static final int[] EXIT_SLOT = new int[] {1};
	private LazyOptional<ModEnergyStorage> loEnergyStorage;
	
	protected NonNullList<ItemStack> items = NonNullList.withSize(slots, ItemStack.EMPTY);
	
	protected ElectricFurnaceTileEntity(TileEntityType<?> tileEntityType, IRecipeType<? extends AbstractCookingRecipe> recipe) {
		super(tileEntityType, recipe);
		this.energyStorage = new ModEnergyStorage(this,
				10000, 0, 50);
		this.loEnergyStorage = LazyOptional.of(() -> this.energyStorage);
		this.isEmpty();
	}

	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction direction) {
		if(cap == CapabilityEnergy.ENERGY) {
			return this.loEnergyStorage.cast();
		}
			return super.getCapability(cap);
	}
	
	@Override
	protected void invalidateCaps() {
		this.loEnergyStorage.invalidate();
		super.invalidateCaps();
	}

	@Override
	public int getContainerSize() {
		return slots;
	}
	
	public int getEnergy() {
		return this.energyStorage.getEnergyStored();
	}
	
	public int getMaxProgress() {
		return this.maxSmeltingProgress;
	}

	public int getProgress() {
		return this.smeltingProgress;
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		return direction == Direction.DOWN ? EXIT_SLOT : ENTER_SLOT;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction direction) {
		if(slot == 1) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return slot == 1;
	}

	@Override
	public void tick() {
		ItemStack stack = this.getItem(0);
		@SuppressWarnings("unchecked")
		IRecipe<?> irecipe = this.level.getRecipeManager().getRecipeFor((IRecipeType<AbstractCookingRecipe>)this.recipeType, this, this.level).orElse(null);
		int cookingTime = this.getTotalCookTime() - 25;
		boolean hasEnergy = this.energyStorage.getEnergyStored() > 0;
		boolean exitSlotIsNotFull =  this.items.get(1).getCount() < this.getMaxStackSize(); 
		@SuppressWarnings("static-access")
		boolean hasRecipe = this.recipeType.equals(recipeType.SMELTING);
		
		
		if(this.smeltingProgress == 0) {
			if(exitSlotIsNotFull && hasRecipe) {
				this.maxSmeltingProgress = cookingTime;
				this.smeltingProgress = cookingTime;
				stack.shrink(1);
			}
			else {
				this.maxSmeltingProgress = 0;
			}
			
			if(this.smeltingProgress > 0 && hasEnergy) {
				this.energyStorage.setEnergy(this.energyStorage.getEnergyStored() - 30);
				this.smeltingProgress--;
			}
			if(this.smeltingProgress == 0) {
				//this.setItem(1, this);
			}
		}
	}
	
	

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + Tranzistio.MOD_ID + ".electric_furnace");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory playerInventory) {
		return new ElectricFurnaceContainer(id, playerInventory, this);
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		ItemStackHelper.saveAllItems(compound, this.items);
		compound.putInt("smeltingProgress", this.smeltingProgress);
		compound.putInt("MaxSmeltingProgress", this.maxSmeltingProgress);
		compound.putInt("Energy", this.getEnergy());
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
	
	protected boolean canWork(@Nullable IRecipe<?> recipe) {
	      if (!this.items.get(0).isEmpty() && recipe != null) {
	         @SuppressWarnings("unchecked")
			ItemStack itemstack = ((IRecipe<ISidedInventory>) recipe).assemble(this);
	         if (itemstack.isEmpty()) {
	            return false;
	         } else {
	            ItemStack itemstack1 = this.items.get(1);
	            if (itemstack1.isEmpty()) {
	               return true;
	            } else if (!itemstack1.sameItem(itemstack)) {
	               return false;
	            } else if (itemstack1.getCount() + itemstack.getCount() <= this.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
	               return true;
	            } else {
	               return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); 
	            }
	         }
	      } else {
	         return false;
	      }
	}
	
	 private void work(@Nullable IRecipe<?> recipe) {
	      if (recipe != null && this.canBurn(recipe)) {
	         ItemStack itemstack = this.items.get(0);
	         @SuppressWarnings("unchecked")
			ItemStack itemstack1 = ((IRecipe<ISidedInventory>) recipe).assemble(this);
	         ItemStack itemstack2 = this.items.get(2);
	         if (itemstack2.isEmpty()) {
	            this.items.set(2, itemstack1.copy());
	         } else if (itemstack2.getItem() == itemstack1.getItem()) {
	            itemstack2.grow(itemstack1.getCount());
	         }

	         if (!this.level.isClientSide) {
	            this.setRecipeUsed(recipe);
	         }
	         itemstack.shrink(1);
	      }
	   }

}
