package com.tranzistor.tranzistio.te;

import com.tranzistor.tranzistio.util.IBaseMBPart.IBaseMBPartBlock;
import com.tranzistor.tranzistio.util.IBaseMBPart.IBaseMBPartController;
import com.tranzistor.tranzistio.util.MultiblockPatterns;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
//import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class BaseMBControllerTE extends LockableLootTileEntity implements ITickableTileEntity, ISidedInventory {
	
	protected final int[][][] mbPattern;
	protected boolean mbIsFormed = false, shouldRecheck = false;
	
	public BaseMBControllerTE(TileEntityType<?> p_i48289_1_, int[][][] pattern) {
		super(p_i48289_1_);
		this.mbPattern = pattern;
	}
	
	public int[][][] getPattern() {
		return this.mbPattern;
	}
	
	public boolean getMBState() {
		return this.mbIsFormed;
	}
	
	public void setMBState(boolean isFormed) {
	    this.mbIsFormed = isFormed;
	    World world = this.level;
	    BlockPos center = this.worldPosition;
	    int x = center.getX(), y =  center.getY(), z =  center.getZ();
	    for(int dX = -1; dX <= 1; dX++) {
	        for(int dY = -1; dY <= 1; dY++) {
	            for(int dZ = -1; dZ <= 1; dZ++) {
	                BlockPos pos = new BlockPos(x+dX, y+dY, z+dZ);
	                TileEntity te = world.getBlockEntity(pos);
	                if(te instanceof BaseMBPartTE)
	                    ((BaseMBPartTE) te).setMaster(isFormed ? this : null);
	            }
	        }
	    }
	    if(isFormed)
	    	setupMB();
	    else
	    	dissasembleMB();   
	    System.out.println(isFormed ? "Formed!" : "Dismantled!");
	}
	
	public void setupMB() {
		World world = this.level;
		BlockPos mPos = this.worldPosition;
		int x = mPos.getX(), y =  mPos.getY(), z =  mPos.getZ();
	    for(int dX = -1; dX <= 1; dX++) {
	        for(int dY = -1; dY <= 1; dY++) {
	            for(int dZ = -1; dZ <= 1; dZ++) {
	                BlockPos pos = new BlockPos(x + dX, y + dY, z + dZ);
	                Block block = MultiblockPatterns.applBlocks[this.mbPattern[dY + 1][dZ + 1][dX + 1]];
	                Block block1 = world.getBlockState(pos).getBlock();
	                if(block1.equals(block)) {
	                	if(block1 instanceof IBaseMBPartBlock) 
	                		world.setBlock(pos, world.getBlockState(pos).setValue(IBaseMBPartBlock.INVISIBLE, true), 3);
	                	
	                	if(block1 instanceof IBaseMBPartController) 
	                		world.setBlock(pos, world.getBlockState(pos).setValue(IBaseMBPartController.FORMED, true), 3);
	                	
	                }
	            }
	        }
	    }
	}
	
	public void dissasembleMB() {
		World world = this.level;
		BlockPos mPos = this.worldPosition;
		int x = mPos.getX(), y =  mPos.getY(), z =  mPos.getZ();
	    for(int dX = -1; dX <= 1; dX++) {
	        for(int dY = -1; dY <= 1; dY++) {
	            for(int dZ = -1; dZ <= 1; dZ++) {
	                BlockPos pos = new BlockPos(x + dX, y + dY, z + dZ);
	                Block block = MultiblockPatterns.applBlocks[this.mbPattern[dY + 1][dZ + 1][dX + 1]];
	                Block block1 = world.getBlockState(pos).getBlock();
	                if(block1.equals(block)) {
	                	if(block1 instanceof IBaseMBPartBlock) {
	                		world.setBlock(pos, world.getBlockState(pos).setValue(IBaseMBPartBlock.INVISIBLE, false), 3);
	                	}
	                	if(block1 instanceof IBaseMBPartController) {
	                		world.setBlock(pos, world.getBlockState(pos).setValue(IBaseMBPartController.FORMED, false), 3);
	                	}
	                }
	            }
	        }
	    }
	}
	
	public void shouldRecheck(boolean bool) {
		this.shouldRecheck = bool;
	}

	@Override
	public void tick() {
	}
	
	public void checkMB() {
	}
	
	@Override
	public CompoundNBT save(CompoundNBT compound) {
		return super.save(compound);
	}
	
	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
	}

	@Override
	public int getContainerSize() {
		return 0;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return null;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> p_199721_1_) {
	}

	@Override
	protected ITextComponent getDefaultName() {
		return null;
	}

	@Override
	protected Container createMenu(int windowId, PlayerInventory inv) {
		return null;
	}

	@Override
	public int[] getSlotsForFace(Direction p_180463_1_) {
		return null;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
		return false;
	}

}
