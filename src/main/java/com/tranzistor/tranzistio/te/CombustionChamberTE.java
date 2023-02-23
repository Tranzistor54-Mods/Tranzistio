package com.tranzistor.tranzistio.te;

import com.tranzistor.tranzistio.Tranzistio;
import com.tranzistor.tranzistio.containers.CokeOvenContainer;
import com.tranzistor.tranzistio.init.TileEntityTypesInit;
import com.tranzistor.tranzistio.network.ModNetwork;
import com.tranzistor.tranzistio.network.MultiblockSyncS2C;
import com.tranzistor.tranzistio.util.MultiblockPatterns;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.items.IItemHandler;

public class CombustionChamberTE extends BaseMBControllerTE {
	
	public static final int slots = 10;
	public static final int[] ENTER_SLOTS = new int[] { 0, 1, 2 };
	public static final int[] FUEL_SLOTS = new int[] { 3, 4, 5 };
	public static final int[] EXIT_SLOTS = new int[] { 6, 7, 8 };
	public static final int BUCKET_SLOT = 9;
	protected NonNullList<ItemStack> items = NonNullList.withSize(slots, ItemStack.EMPTY);
	
	public CombustionChamberTE(TileEntityType<?> te, int[][][] pattern) {
		super(te, pattern);
	}
	
	public CombustionChamberTE(int[][][] pattern) {
		this(TileEntityTypesInit.COMBUSTION_CHAMBER_TILE_ENTITY.get(), pattern);
	}
	
	@Override
	public void tick() {
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
	
	@Override
	protected IItemHandler createUnSidedHandler() {
		return super.createUnSidedHandler();
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if(this.mbIsFormed)
			compound.putBoolean("mbIsFormed", true);
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		this.mbIsFormed = compound.getBoolean("mbIsFormed");
		this.shouldRecheck = true;
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
