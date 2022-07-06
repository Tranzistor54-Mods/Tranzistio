package com.tranzistor.tranzistio.core.itemGroup;

import com.tranzistor.tranzistio.core.init.ItemInit;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class TranzistioItemGroup extends ItemGroup{
	
	public static final TranzistioItemGroup TRANZISTIO = new TranzistioItemGroup(ItemGroup.TABS.length, "tranzistio");
	public TranzistioItemGroup(int index, String label) {
		super(index, label);
	}

	@Override
	public ItemStack makeIcon() {
		return new ItemStack(ItemInit.RUBY.get());
	}

}
