package com.aleclownes.SpellScript;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

/**Wrapper class for Entity
 * @author alownes
 *
 */
public class EntityWrapper extends Wrapper {
	
	public EntityWrapper(Entity entity){
		super(entity);
	}
	
	/**Gets the wrapped Entity
	 * @return Entity
	 */
	protected Entity getEntity(){
		return (Entity)object;
	}
	
	/**Gets the EntityType of the entity
	 * @return EntityType
	 */
	public EntityType getEntityType(){
		return getEntity().getType();
	}
	
	/**Gets the location of the entity
	 * @return Location as an array of 3 double
	 */
	public double[] getLocation(){
		return Node.locationToArray(getEntity().getLocation());
	}
	
	/**Gets the velocity of the entity
	 * @return Velocity as Vector
	 */
	public Vector getVelocity(){
		return getEntity().getVelocity();
	}
	
	/**Gets the ticks left where the entity is on fire
	 * @return fire ticks
	 */
	public int getFireTicks(){
		return getEntity().getFireTicks();
	}
	
	/**Gets whether or not the entity exists anymore
	 * @return is dead
	 */
	public boolean isDead(){
		return getEntity().isDead();
	}
	
	/**Gets the entity Id of the entity
	 * @return entity Id
	 */
	public int getEntityId(){
		return getEntity().getEntityId();
	}
	
	/**Gets the entity's passenger
	 * @return Passenger entity, can be null
	 */
	public EntityWrapper getPassenger(){
		return getEntity().getPassenger() != null ? new EntityWrapper(getEntity().getPassenger()) : null;
	}
	
	/**Gets the fall distance of the entity
	 * @return fall distance
	 */
	public float getFallDistance(){
		return getEntity().getFallDistance();
	}
	
	@Override
	public boolean equals(Object object){
		return getEntity().equals(object);
	}

}
