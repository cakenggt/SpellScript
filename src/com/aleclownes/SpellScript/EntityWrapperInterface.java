package com.aleclownes.SpellScript;

import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public interface EntityWrapperInterface {
	
	public EntityType getType();
	
	public void setFireTicks();
	
	public void teleport(double x, double y, double z);
	
	public Vector getVelocity();
	
	public void setVelocity(Vector vel);
	
	public double[] getLocation();

}
