package com.aleclownes.SpellScript;

import org.bukkit.entity.Player;

public class PlayerWrapper extends LivingEntityWrapper {
	
	public PlayerWrapper(Player player){
		super(player);
	}
	
	protected Player getPlayer(){
		return (Player)object;
	}
	
	public String getDisplayName(){
		return getPlayer().getDisplayName();
	}
	
	public int getFoodLevel(){
		return getPlayer().getFoodLevel();
	}
	
	public ItemStackWrapper getItemInHand(){
		return new ItemStackWrapper(getPlayer().getItemInHand());
	}
	
	public boolean isOnline(){
		return getPlayer().isOnline();
	}
	
	@Override
	public boolean equals(Object object){
		return getPlayer().equals(object);
	}

}
