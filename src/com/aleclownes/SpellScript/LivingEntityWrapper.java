package com.aleclownes.SpellScript;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

public class LivingEntityWrapper extends EntityWrapper {
	
	public LivingEntityWrapper(LivingEntity live){
		super(live);
	}
	
	protected LivingEntity getLivingEntity(){
		return (LivingEntity)object;
	}
	
	public List<PotionEffect> getActivePotionEffects(){
		return new ArrayList<PotionEffect>(getLivingEntity().getActivePotionEffects());
	}
	
	public String getCustomName(){
		return getLivingEntity().getCustomName();
	}
	
	public int getRemainingAir(){
		return getLivingEntity().getRemainingAir();
	}
	
	public int getMaximumAir(){
		return getLivingEntity().getMaximumAir();
	}
	
	public double getMaxHealth(){
		return ((Damageable)getLivingEntity()).getMaxHealth();
	}
	
	public double getHealth(){
		return ((Damageable)getLivingEntity()).getHealth();
	}

}
