package com.tranzistor.tranzistio.containers;

import java.util.Objects;

import com.tranzistor.tranzistio.init.BlockInit;
import com.tranzistor.tranzistio.init.ContainersInit;

import com.tranzistor.tranzistio.te.FluidFillerTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class FluidFillerContainer extends Container {
	
	public final FluidFillerTileEntity te;
	private final IWorldPosCallable canInteractWithCallable;
	
	public FluidFillerContainer(final int windowId, final PlayerInventory playerInventory, final FluidFillerTileEntity te) {
		super(ContainersInit.FLUID_FILLER.get(), windowId);
		this.te = te; 
		this.canInteractWithCallable = IWorldPosCallable.create(te.getLevel(), te.getBlockPos());
		
		//Filler slots
		this.addSlot(new Slot((IInventory) te, 0, 9, 9) {  // bucket slot
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent() && stack.getCount() == 1;
			}
		}); 
		
		int num = 1;
		for(int y = 9; y <= 43; y += 17) {
			for(int x = 62; x <= 96; x += 17) {
				this.addSlot(new Slot((IInventory) te, num, x, y)); // grid slots
				num++;
			}
		}
		
		this.addSlot(new Slot((IInventory) te, 10, 150, 27) { // exit slot
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

	public FluidFillerContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}

	private static FluidFillerTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "Player inventory cannot be null");
		Objects.requireNonNull(data, "Packet buffer cannot be null");
		final TileEntity te = playerInventory.player.level.getBlockEntity(data.readBlockPos());
		if(te instanceof FluidFillerTileEntity) {
			return (FluidFillerTileEntity) te;
		}
		throw new IllegalStateException("Tile entity is Not Correct");
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return stillValid(canInteractWithCallable, player, BlockInit.FLUID_FILLER.get());
	}
	
	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if(slot != null && slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			int innerSlots = FluidFillerTileEntity.slots;
			if(index < innerSlots && !this.moveItemStackTo(stack1, innerSlots, innerSlots+36, true))
				return ItemStack.EMPTY;
			else if (!this.moveItemStackTo(stack1, 0, 11, false))
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
