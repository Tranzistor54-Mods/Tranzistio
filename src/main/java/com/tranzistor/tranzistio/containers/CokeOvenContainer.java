package com.tranzistor.tranzistio.containers;

import java.util.Objects;

import com.tranzistor.tranzistio.init.BlockInit;
import com.tranzistor.tranzistio.init.ContainersInit;
import com.tranzistor.tranzistio.te.CombustionChamberTE;
import net.minecraft.inventory.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class CokeOvenContainer extends Container {
	
	public final CombustionChamberTE te;
	private final IWorldPosCallable canInteractWithCallable;

	public CokeOvenContainer(final int windowId, final PlayerInventory playerInv, final CombustionChamberTE te) {
		super(ContainersInit.COKE_OVEN.get(), windowId);
		this.te = te;
		this.canInteractWithCallable = IWorldPosCallable.create(te.getLevel(), te.getBlockPos());
		
		//Enter slots
		int n = 0;
		for(int x = 13; x <= 49; x += 18) {
			this.addSlot(new Slot((IInventory) te, n, x, 12));
			n++;
		}
		
		//Fuel slots
		n = 3;
		for(int x = 13; x <= 49; x += 18) {
			this.addSlot(new Slot((IInventory) te, n, x, 52) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return ForgeHooks.getBurnTime(stack, IRecipeType.SMELTING) > 0;
				}
			});
			n++;
		}
		
		//Exit slots
		n = 6;
		for(int y = 14; y <= 50; y += 18) {
			this.addSlot(new Slot((IInventory) te, n, 114, y) {
				@Override
				public boolean mayPlace(ItemStack stack) { return false; }
			});
			n++;
		}
		
		//Bucket slot
		this.addSlot(new Slot((IInventory) te, 9, 145, 12) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent() && stack.getCount() == 1;
			}
		});
		
		//Player Inventory
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 9; col++) {
				this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 166 - (4 - row) * 18 - 10));
			}
		}
		
		//hotbar
		for(int col = 0; col < 9; col++) {
			this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
		}
		
	}
	
	public CokeOvenContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}
	
	private static CombustionChamberTE getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "Player inventory cannot be null");
		Objects.requireNonNull(data, "Packet buffer cannot be null");
		final TileEntity te = playerInventory.player.level.getBlockEntity(data.readBlockPos());
		if(te instanceof CombustionChamberTE) {
			return (CombustionChamberTE) te;
		}
		throw new IllegalStateException("Tile entity is Not Correct");
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return stillValid(canInteractWithCallable, player, BlockInit.COMBUSTION_CHAMBER.get());
	}
	
	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if(slot != null && slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			int innerSlots = CombustionChamberTE.slots;
			if(index < innerSlots && !this.moveItemStackTo(stack1, innerSlots, innerSlots+36, true))
				return ItemStack.EMPTY;
			else if (!this.moveItemStackTo(stack1, 0, 10, false))
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
