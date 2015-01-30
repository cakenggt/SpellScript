package com.aleclownes.SpellScript;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackWrapper extends Wrapper {
	
	public ItemStackWrapper(ItemStack stack){
		super(stack);
	}
	
	protected ItemStack getItemStack(){
		return (ItemStack)object;
	}
	
	public Material getType(){
		return getItemStack().getType();
	}
	
	public int getAmount(){
		return getItemStack().getAmount();
	}
	
	public ItemMeta getItemMeta(){
		return getItemStack().getItemMeta();
	}

}
