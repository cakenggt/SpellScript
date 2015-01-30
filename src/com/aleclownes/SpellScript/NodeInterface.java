package com.aleclownes.SpellScript;

import org.bukkit.Material;
import org.bukkit.util.Vector;

public interface NodeInterface {
	
	public double[] getLocation();
	
	public Vector getDirection();
	
	public void breakBlock();
	
	public void burn();
	
	public Material inspectBlock();
	
	public void move(double x, double y, double z);
	
	public void moveInDirection(double distance);
	
	public void damage(double damage);
	
	public int getPower();
	
	public void sleep(int ticks);
	
	public void selfDestruct();
	
	public EntityWrapperInterface[] getNearbyEntities(double distance);
	
	public void suicide();
	
	public String getCaster();

}
