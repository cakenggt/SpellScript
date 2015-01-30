package com.aleclownes.SpellScript;

import org.bukkit.entity.Item;

public class ItemWrapper extends EntityWrapper {
	
	public ItemWrapper (Item item){
		super(item);
	}
	
	protected Item getItem(){
		return (Item)object;
	}
	
	public ItemStackWrapper getItemStack(){
		return new ItemStackWrapper(getItem().getItemStack());
	}
	
	public int getPickupDelay(){
		return getItem().getPickupDelay();
	}

}
