package com.aleclownes.SpellScript;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

public class EntityWrapper extends Wrapper {
	
	public EntityWrapper(Entity entity){
		super(entity);
	}
	
	protected Entity getEntity(){
		return (Entity)object;
	}
	
	public EntityType getEntityType(){
		return getEntity().getType();
	}
	
	public double[] getLocation(){
		return Node.locationToArray(getEntity().getLocation());
	}
	
	public Vector getVelocity(){
		return getEntity().getVelocity();
	}
	
	public int getFireTicks(){
		return getEntity().getFireTicks();
	}
	
	public boolean isDead(){
		return getEntity().isDead();
	}
	
	public int getEntityId(){
		return getEntity().getEntityId();
	}
	
	public EntityWrapper getPassenger(){
		return new EntityWrapper(getEntity().getPassenger());
	}
	
	public float getFallDistance(){
		return getEntity().getFallDistance();
	}

}
