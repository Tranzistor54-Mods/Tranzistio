package com.tranzistor.tranzistio.core.containers;

import java.util.Objects;
import com.tranzistor.tranzistio.core.init.BlockInit;
import com.tranzistor.tranzistio.core.init.ContainersInit;
import com.tranzistor.tranzistio.core.te.ElectricFurnaceTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

public class ElectricFurnaceContainer extends Container{
	
	public final ElectricFurnaceTileEntity te;
	private final IWorldPosCallable canInteractWithCallable;

	public ElectricFurnaceContainer(final int windowId, final PlayerInventory playerInventory, final ElectricFurnaceTileEntity te) {
		super(ContainersInit.ELECTRIC_FURNACE.get(), windowId);
		this.te = te; 
		this.canInteractWithCallable = IWorldPosCallable.create(te.getLevel(), te.getBlockPos());
		
		//furnace slots
		this.addSlot(new Slot((IInventory) te, 0, 59, 35));

		this.addSlot(new Slot((IInventory) te, 1, 108, 35) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
		});
		
		//Player Inventory
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 166 - (4 - row) * 18 - 10));
			}
		}
		
		//hotbar
		for(int col = 0; col < 9; col++) {
			this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
		}
	}

	public ElectricFurnaceContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}

	private static ElectricFurnaceTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "Player inventory cannot be null");
		Objects.requireNonNull(data, "Packet buffer cannot be null");
		final TileEntity te = playerInventory.player.level.getBlockEntity(data.readBlockPos());
		if(te instanceof ElectricFurnaceTileEntity) {
			return (ElectricFurnaceTileEntity) te;
		}
		throw new IllegalStateException("Tile entity is Not Correct");
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return stillValid(canInteractWithCallable, player, BlockInit.ELECTRIC_FURNACE.get());
	}
	
	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if(slot != null && slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			int innerSlots = ElectricFurnaceTileEntity.slots;
			if(index < innerSlots && !this.moveItemStackTo(stack1, innerSlots, innerSlots+36, true))
				return ItemStack.EMPTY;
			else if (!this.moveItemStackTo(stack1, 0, 1, false))
				return ItemStack.EMPTY;
			
			if(stack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			}
			else {
				slot.setChanged();
			}
			if (stack.getCount() == stack1.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, stack1);
		}
		return stack;
	}
	
	

}
