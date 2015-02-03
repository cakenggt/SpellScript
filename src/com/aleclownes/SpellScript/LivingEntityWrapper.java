package com.aleclownes.SpellScript;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

/**Wrapper for LivingEntity
 * @author alownes
 *
 */
public class LivingEntityWrapper extends EntityWrapper {
	
	public LivingEntityWrapper(LivingEntity live){
		super(live);
	}
	
	/**Gets the wrapped LivingEntity
	 * @return LivingEntity
	 */
	protected LivingEntity getLivingEntity(){
		return (LivingEntity)object;
	}
	
	/**Gets a list of the active potion effects on this living entity
	 * @return List of PotionEffect
	 */
	public List<PotionEffect> getActivePotionEffects(){
		return new ArrayList<PotionEffect>(getLivingEntity().getActivePotionEffects());
	}
	
	/**Gets the custom name of this living entity
	 * @return Custom name
	 */
	public String getCustomName(){
		return getLivingEntity().getCustomName();
	}
	
	/**Gets the living entity's remaining air
	 * @return remaining air
	 */
	public int getRemainingAir(){
		return getLivingEntity().getRemainingAir();
	}
	
	/**Get's the living entity's maximum air
	 * @return maximum air
	 */
	public int getMaximumAir(){
		return getLivingEntity().getMaximumAir();
	}
	
	/**Get's the living entity's max health
	 * @return max health
	 */
	public double getMaxHealth(){
		return ((Damageable)getLivingEntity()).getMaxHealth();
	}
	
	/**Get's the living entity's current health
	 * @return current health
	 */
	public double getHealth(){
		return ((Damageable)getLivingEntity()).getHealth();
	}
	
	@Override
	public boolean equals(Object object){
		return getLivingEntity().equals(object);
	}

}
