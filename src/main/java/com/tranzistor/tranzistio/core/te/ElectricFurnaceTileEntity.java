package com.tranzistor.tranzistio.core.te;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class ElectricFurnaceTileEntity extends LockableLootTileEntity implements ITickableTileEntity, ISidedInventory{
	
	public int slots = 2;
	public int smeltingProgress = 0, maxSmeltingProgress = 0;
	
	protected ElectricFurnaceTileEntity(TileEntityType<?> tileEntityType) {
		super(tileEntityType);
	}

	@Override
	public int getContainerSize() {
		return slots;
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canPlaceItemThroughFace(int p_180462_1_, ItemStack p_180462_2_, Direction p_180462_3_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> p_199721_1_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ITextComponent getDefaultName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
		// TODO Auto-generated method stub
		return null;
	}

}
