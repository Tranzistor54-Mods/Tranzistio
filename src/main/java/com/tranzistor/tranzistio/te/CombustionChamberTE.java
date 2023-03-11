package com.tranzistor.tranzistio.te;

import javax.annotation.Nullable;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.containers.CokeOvenContainer;
import com.tranzistor.tranzistio.init.RecipesTypeInit;
import com.tranzistor.tranzistio.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.network.ModNetwork;
import com.tranzistor.tranzistio.network.MultiblockSyncS2C;
import com.tranzistor.tranzistio.util.MultiblockPatterns;
import com.tranzistor.tranzistio.recipes.CokeOvenRecipe;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkDirection;

public class CombustionChamberTE extends BaseMBControllerTE {
	
	public static final int slots = 10;
	public static final int[] ENTER_SLOTS = new int[] { 0, 1, 2 };
	public static final int[] FUEL_SLOTS = new int[] { 3, 4, 5 };
	public static final int[] EXIT_SLOTS = new int[] { 6, 7, 8 };
	public static final int BUCKET_SLOT = 9;
	protected NonNullList<ItemStack> items = NonNullList.withSize(slots, ItemStack.EMPTY);
	private final FluidTank fluidStorage;
	private LazyOptional<FluidTank> loFluidStorage;
	public int progress = 0, maxProgress = 0, burningTime = 0, maxBurningTime = 0;
	
	public CombustionChamberTE(TileEntityType<?> te, int[][][] pattern) {
		super(te, pattern);
		this.fluidStorage = new FluidTank(16000);
		this.loFluidStorage = LazyOptional.of(() -> this.fluidStorage);
	}
	
	public CombustionChamberTE(int[][][] pattern) {
		this(TileEntityTypesInit.COMBUSTION_CHAMBER_TILE_ENTITY.get(), pattern);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void tick() {
		if(this.mbIsFormed) {
			operateWithSlots();
			IRecipe<?> iRecipe = this.level.getRecipeManager().getRecipeFor(RecipesTypeInit.COKING_RECIPE, this, this.level).orElse(null);
			if(this.canCoke(iRecipe)) {
				if(this.burningTime == 0)
					setBurningTime();
				if(this.progress == 0) {
					if(this.burningTime > 0) {
						this.maxProgress = this.getTotalWorkingTime();
						this.progress = this.getTotalWorkingTime();
					}
					else
						this.maxProgress = 0;
				}
				if(this.progress > 0 && this.burningTime > 0) {
					if(--progress == 0) {
						ItemStack stack1 = ((IRecipe<ISidedInventory>) iRecipe).assemble(this);
						if(this.fluidStorage.getFluid() == null)
							this.fluidStorage.setFluid(this.getOutputFluid());
						else
							this.fluidStorage.fill(getOutputFluid(), FluidAction.EXECUTE);
						this.items.get(1).shrink(1);
						ItemStack stack2 = this.items.get(7);
						if(stack2.isEmpty())
							this.setItem(7, stack1.copy());
						else if(stack2.sameItem(stack1) && stack2.getCount() < 64)
							stack2.grow(stack1.getCount());
					}
					burningTime--;
				}
			}
			else {
				this.progress = 0;
				this.maxProgress = 0;
			}
			this.setChanged();
		}
	}
	
	public void setBurningTime() {
		ItemStack stack = this.items.get(4);
		this.burningTime = ForgeHooks.getBurnTime(stack, IRecipeType.SMELTING);
		this.maxBurningTime = ForgeHooks.getBurnTime(stack, IRecipeType.SMELTING);
		stack.shrink(1);
	}
	
	@SuppressWarnings("unchecked")
	public boolean canCoke(@Nullable IRecipe<?> iRecipe) {
		if(iRecipe == null || this.fluidStorage.getFluidAmount() >= this.fluidStorage.getCapacity()) 
			return false;
		ItemStack stack = ((IRecipe<ISidedInventory>) iRecipe).assemble(this);
		if(stack.isEmpty())
			return false;
		ItemStack stack1 = this.items.get(7);
		if(stack1.isEmpty())
			return true;
		if(!stack1.sameItem(stack))
			return false;
		int resultAmount = stack1.getCount() + stack.getCount();
		return resultAmount <= this.getMaxStackSize() && resultAmount <= stack1.getMaxStackSize();
	}
	
	public int getTotalWorkingTime() {
		return this.level.getRecipeManager().getRecipeFor(RecipesTypeInit.COKING_RECIPE, this, this.level)
				.map(CokeOvenRecipe::getCokingTime).orElse(600);
	}
	
	public FluidStack getOutputFluid() {
		return this.level.getRecipeManager().getRecipeFor(RecipesTypeInit.COKING_RECIPE, this, this.level)
				.map(CokeOvenRecipe::getFluid).orElse(null);
	}
	
	@Override
	public void checkMB() {
		World world = this.level;
		int rX = this.worldPosition.getX(), rY = this.worldPosition.getY(), rZ = this.worldPosition.getZ();
		for(int y = -1; y <= 1; y++) {
			for(int z = -1; z <= 1; z++) {
				for(int x = -1; x <= 1; x++) {
					BlockPos pos = new BlockPos(rX + x, rY + y, rZ + z);
					if(!world.getBlockState(pos).getBlock().equals(MultiblockPatterns.applBlocks[this.mbPattern[y + 1][z + 1][x + 1]])){
						return;
					}
				}
			}
		}
		this.setMBState(true);
		shouldRecheck = false;
		if(!world.isClientSide) {
			for (ServerPlayerEntity player : ((ServerWorld)world).players())
				ModNetwork.CHANNEL.sendTo(new MultiblockSyncS2C(this.mbIsFormed, this.worldPosition), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
	
	public void operateWithSlots() {
		ItemStack 
		//Additional slots
		stack0 = this.getItem(0), //Additional enter slot
		stack2 = this.getItem(2), //Additional enter slot
		stack3 = this.getItem(3), //Additional fuel slot
		stack5 = this.getItem(5), //Additional fuel slot
		stack6 = this.getItem(6), //Additional exit slot
		stack8 = this.getItem(8), //Additional exit slot
		//Main slots
		stack1 = this.getItem(1), //Main enter slot
		stack4 = this.getItem(4), //Main fuel slot
		stack7 = this.getItem(7); //Main exit slot
		
		boolean flag = stack0.isEmpty()
				&& stack1.isEmpty()
				&& stack2.isEmpty()
				&& stack3.isEmpty()
				&& stack4.isEmpty()
				&& stack5.isEmpty()
				&& stack6.isEmpty()
				&& stack7.isEmpty()
				&& stack8.isEmpty();
		if(flag)
			return;
		int m0 = stack1.getMaxStackSize(), m1 = stack4.getMaxStackSize();
		if(stack0.sameItem(stack1)) {
			int t = Math.min(m0 - stack1.getCount(), stack0.getCount());
			stack1.grow(t);
			stack0.shrink(t);
		}	
		if(stack2.sameItem(stack1)) {
			int t = Math.min(m0 - stack1.getCount(), stack2.getCount());
			stack1.grow(t);
			stack2.shrink(t);
		}
		if(stack1.isEmpty()) {
			if(!stack0.isEmpty()) {
				this.setItem(1, stack0.copy());
				stack0.shrink(stack0.getCount());
				return;
			}
			if(!stack2.isEmpty()) {
				this.setItem(1, stack2.copy());
				stack2.shrink(stack2.getCount());
				return;
			}
		}
		if(stack4.sameItem(stack3)) {
			int t = Math.min(m1 - stack4.getCount(), stack3.getCount());
			stack4.grow(t);
			stack3.shrink(t);
		}
		if(stack4.sameItem(stack5)) {
			int t = Math.min(m1 - stack4.getCount(), stack5.getCount());
			stack4.grow(t);
			stack5.shrink(t);
		}
		if(stack4.isEmpty()) {
			if(!stack3.isEmpty()) {
				this.setItem(4, stack3.copy());
				stack3.shrink(stack3.getCount());
				return;
			}
			if(!stack5.isEmpty()) {
				this.setItem(4, stack5.copy());
				stack5.shrink(stack5.getCount());
				return;
			}
		}
		
		if(stack6.isEmpty() && !stack7.isEmpty() && (stack8.getCount() == 64 || !stack8.sameItem(stack7))) {
			this.setItem(6, stack7.copy());
			stack7.shrink(stack7.getCount());
			return;
		}
		if(stack8.isEmpty() && !stack7.isEmpty() && (stack6.getCount() == 64 || !stack6.sameItem(stack7))) {
			this.setItem(8, stack7.copy());
			stack7.shrink(stack7.getCount());
			return;
		}
		if(stack6.sameItem(stack7)) {
			int t = Math.min(64 - stack6.getCount(), stack7.getCount());
			stack6.grow(t);
			stack7.shrink(t);
		}
		if(stack8.sameItem(stack7)) {
			int t = Math.min(64 - stack6.getCount(), stack7.getCount());
			stack8.grow(t);
			stack7.shrink(t);
		}
	}
	
	public FluidTank getFluidStorage() {
		return this.fluidStorage;
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
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap) {
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return this.loFluidStorage.cast();
		return super.getCapability(cap);
	}
	
	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		this.loFluidStorage.invalidate();
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if(this.mbIsFormed) {
			compound.putBoolean("mbIsFormed", true);
			CompoundNBT nbt = new CompoundNBT();
			fluidStorage.writeToNBT(nbt);
			ItemStackHelper.saveAllItems(compound, this.items);
			compound.put("FluidStorage", nbt);
			compound.putInt("Progress", this.progress);
			compound.putInt("MaxProgress", this.maxProgress);
			compound.putInt("BurningTime", this.burningTime);
			compound.putInt("MaxBurningTime", this.maxBurningTime);
		}
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.mbIsFormed = compound.getBoolean("mbIsFormed");
		this.shouldRecheck = true;
		if(this.mbIsFormed) {
			this.fluidStorage.readFromNBT(compound.getCompound("FluidStorage"));
			this.progress = compound.getInt("Progress");
			this.maxProgress = compound.getInt("MaxProgress");
			this.burningTime = compound.getInt("BurningTime");
			this.maxBurningTime = compound.getInt("MaxBurningTime");
			ItemStackHelper.loadAllItems(compound, this.items);
		}
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
	public int getContainerSize() {
		return slots;
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return slot != 6 && slot != 7 && slot != 8;
	}
	
	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return slot == 6 || slot == 7 || slot == 8;
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
		return new TranslationTextComponent("container." + Tranzistio.MOD_ID + ".coke_oven");
	}
	
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv) {
		return new CokeOvenContainer(windowId, playerInv, this);
	}
	
	@Override
	public int[] getSlotsForFace(Direction dir) {
		return dir == Direction.DOWN ? EXIT_SLOTS : ENTER_SLOTS;
	}
}
