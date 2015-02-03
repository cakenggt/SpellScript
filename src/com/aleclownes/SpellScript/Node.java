package com.aleclownes.SpellScript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**Node is the object which is manipulated by the javascript code written by the player. It is referenced with the "node" variable 
 * in the javascript code. Most methods in this object take Power, which your node can replenish by stealing health from living
 * entities or other nodes. When your node runs out of power, it will silently die. You can also kill a node prematurely by right clicking
 * with it's ticket in your hand. A node's location is represented in the world by a mobspawner flame effect.
 * 
 * Only public methods are accessible through the javascript code.
 * @author alownes
 *
 */
public class Node {

	SpellScript p;
	Location loc;
	Vector direction;
	double power = 1000;
	Player sender;
	UUID uid;
	ChatWrapper chatWrapper;
	String command;
	String[] args;
	Inventory inv;
	BukkitTask task;
	BukkitScheduler sched;
	Map<String, Object> map = new HashMap<String, Object>();

	public Node(SpellScript p, Player sender, ChatWrapper chatWrapper, String command, String[] args, Location loc){
		this.sender = sender;
		this.loc = loc;
		this.direction = loc.getDirection();
		uid = UUID.randomUUID();
		this.chatWrapper = chatWrapper;
		this.command = command;
		this.args = args;
		this.p = p;
		sched = p.getServer().getScheduler();
		inv = p.getServer().createInventory(null, 54);
	}

	/**If enough power exists in the node, take the required power away. If it doesn't exist, kill the node.
	 * @param power
	 */
	private void checkPower(double power){
		if (this.power >= power){
			this.power -= power;
		}
		else{
			suicide();
		}
	}

	protected void setChatWrapper(ChatWrapper chatWrapper){
		this.chatWrapper = chatWrapper;
	}

	/**Gets the ChatWrapper that represents the most recent player chat in that world
	 * @return ChatWrapper
	 */
	public ChatWrapper getChatWrapper(){
		return chatWrapper;
	}

	/**
	 * Destroys the node
	 */
	public void suicide() {
		task.cancel();
	}

	/**Makes the node sleep for the specified number of seconds
	 * @param seconds - Seconds to sleep
	 */
	public void sleep(double seconds) {
		try {
			Thread.sleep((long) (seconds*1000.0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Moves the node forward on it's set direction
	 * @power 1
	 */
	public void moveForward() {
		checkPower(1);
		loc = loc.add(direction);	
	}

	/**Sets the node's direction
	 * @param direction - Vector
	 */
	public void setDirection(Vector direction){
		if (direction.length() > 1){
			direction = direction.normalize();
		}
		this.direction = direction;
	}

	/**Gets the node's direction
	 * @return Vector
	 */
	public Vector getDirection() {
		return direction;
	}

	/**Gets the node's location
	 * @return The node's location as an array of 3 doubles
	 */
	public double[] getLocation() {
		return locationToArray(loc);
	}

	/**Converts the location to a double array usable in JS
	 * @param loc - Location to convert
	 * @return double array of size 3 representing the location
	 */
	static double[] locationToArray(Location loc){
		double[] array = new double[3];
		array[0] = loc.getX();
		array[1] = loc.getY();
		array[2] = loc.getZ();
		return array;
	}

	/**Teleports the entity to the specified location. The entity must be within 1 meter.
	 * @param entity - Entity to teleport
	 * @param location - Location to teleport to
	 * @power Square root of the distance
	 */
	public void teleport(final EntityWrapper entity, double[] location){
		if (entity.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		float pitch = entity.getEntity().getLocation().getPitch();
		float yaw = entity.getEntity().getLocation().getYaw();
		final Location dest = new Location(loc.getWorld(), location[0], location[1], location[2], yaw, pitch);
		checkPower(Math.sqrt(dest.distance(loc)));
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					entity.getEntity().teleport(dest);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Gets the power in the node
	 * @return power in the node
	 */
	public double getPower() {
		return power;
	}

	/**Gets the node's map for storing information visible to other nodes
	 * @return - Map<String, Object>
	 */
	public Map<String, Object> getMap() {
		return map;
	}

	/**Takes power as health from a living entity, giving it to the node. A maximum of 1000 power can
	 * be drained from a living entity at a time. 1 health = 1000 power. The entity must be within
	 * 1 meter.
	 * @param live - Living entity
	 * @param amount - less than or equal to 1000
	 * @power 1
	 */
	public void takePower(final LivingEntityWrapper live, double amount){
		if (live.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		amount = Math.min(amount, 1000.0);
		final double health = amount/SpellScript.powerToHealthRatio;
		checkPower(1);
		double receivedPower = Math.min(health, live.getHealth())*SpellScript.powerToHealthRatio;
		power += receivedPower;
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					live.getLivingEntity().damage(health);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Takes power from a node, giving it to this node. A maximum of 1000 power can be drained from
	 * a node at a time. The node must be within 1 meter.
	 * @param node - Target
	 * @param amount - Less than or equal to 1000
	 * @power 1
	 */
	public void takePower(final NodeWrapper node, double amount){
		if (node.getNode().loc.distance(loc) > 1){
			return;
		}
		final double newAmount = Math.min(amount, 1000.0);
		checkPower(1);
		double receivedPower = Math.min(newAmount, node.getNode().getPower());
		power += receivedPower;
		node.getNode().power -= receivedPower;
	}

	/**Gives power to a living entity in the form of health. A maximum of 1000 power can be given at
	 * a time. There is 1000 power per 1 health. The entity must be within 1 meter.
	 * @param live - LivingEntity
	 * @param amount - Less than or equal to 1000
	 * @power 1
	 */
	public void givePower(final LivingEntityWrapper live, double amount){
		if (live.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		amount = Math.min(amount, 1000.0);
		double receivedPower = Math.min(amount, power);
		final double health = receivedPower/SpellScript.powerToHealthRatio;
		checkPower(1);
		power -= receivedPower;
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					live.getLivingEntity().setHealth(((Damageable)live.getLivingEntity()).getHealth() + health);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Gives power to a node. A maximum of 1000 power can be given at a time. The node must be within 1 meter.
	 * @param node - Receiving node
	 * @param amount - Less than or equal to 1000
	 * @power 1
	 */
	public void givePower(NodeWrapper node, double amount){
		if (node.getNode().loc.distance(loc) > 1){
			return;
		}
		final double newAmount = Math.min(amount, 1000.0);
		checkPower(1);
		double receivedPower = Math.min(newAmount, getPower());
		power -= receivedPower;
		node.getNode().power += receivedPower;
	}

	/**Sets the velocity of the entity. The entity must be within 1 meter.
	 * @param entity - Entity to set the velocity of
	 * @param vector - Vector for velocity
	 * @power 10
	 */
	public void setVelocity(final EntityWrapper entity, final Vector vector){
		if (entity.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		checkPower(10);
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					entity.getEntity().setVelocity(vector);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Gets a list of nearby entities within a radius
	 * @param distance - Radius to check for entities in
	 * @return List of EntityWrapper
	 * @power 1
	 */
	public List<EntityWrapper> getNearbyEntities(double distance) {
		checkPower(1);
		List<EntityWrapper> nearby = new ArrayList<EntityWrapper>();
		for (Entity entity : loc.getWorld().getEntities()){
			if (entity.getLocation().distance(loc) <= distance){
				nearby.add((EntityWrapper)WrapperFactory.wrap(entity));
			}
		}
		return nearby;
	}

	/**Gets a list of nearby nodes within a radius
	 * @param distance - Radius to check for nodes in
	 * @return List of NodeWrapper
	 * @power 1
	 */
	public List<NodeWrapper> getNearbyNodes(double distance){
		checkPower(1);
		List<NodeWrapper> nearby = new ArrayList<NodeWrapper>();
		for (Node node : p.nodeList){
			if (node.loc.distance(loc) <= distance){
				nearby.add(new NodeWrapper(node));
			}
		}
		return nearby;
	}

	/**
	 * Breaks the block the node is currently occupying.
	 * @power 100
	 */
	public void breakBlock() {
		checkPower(100);
		final Location blockLoc = loc.clone();
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					blockLoc.getBlock().breakNaturally();
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**Gets the ItemStackWrapper in the specified slot of the node's inventory. The inventory is 54 spaces large.
	 * @param slot - Specified slot
	 * @return ItemStackWrapper
	 */
	public ItemStackWrapper getItemInInv(int slot){
		ItemStack stack = inv.getItem(slot);
		return stack != null ? new ItemStackWrapper(stack) : null;
	}

	/**Places the block in the current slot in the location the node resides. This is cancelled if the block the node is occupying is
	 * not Air or if the item in the slot is not a block.
	 * @param slot - Slot number
	 * @power 10
	 */
	public void placeBlock(int slot){
		final ItemStack stack = inv.getItem(slot);
		if (loc.getBlock().getType() == Material.AIR && stack != null && stack.getType().isBlock()){
			checkPower(10);
			if (stack.getAmount() > 1){
				stack.setAmount(stack.getAmount()-1);
			}
			else{
				inv.setItem(slot, null);
			}
			try {
				sched.callSyncMethod(p, new Callable<Boolean>(){
					@Override
					public Boolean call(){
						loc.getBlock().setType(stack.getType());
						return true;
					}
				}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**Incorporates the selected ItemWrapper into the node's inventory. The item must be within 1 meter.
	 * @param item - Selected ItemWrapper
	 * @power 10
	 */
	public void takeItem(final ItemWrapper item){
		if (item.getItem().getLocation().distance(loc) <= 1) {
			checkPower(10);
			final Map<Integer, ItemStack> dropped = inv.addItem(item.getItem()
					.getItemStack());
			try {
				sched.callSyncMethod(p, new Callable<Boolean>() {
					@Override
					public Boolean call() {
						item.getItem().remove();
						for (ItemStack stack : dropped.values()) {
							item.getItem().getWorld()
									.dropItemNaturally(loc, stack);
						}
						return true;
					}
				}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**Drops the item in the current slot and returns it's ItemWrapper.
	 * @param slot - Specified slot of the node's inventory
	 * @param amount - Number of items to drop
	 * @return ItemWrapper of the dropped item
	 * @power 10
	 */
	public ItemWrapper dropItem(int slot, int amount){
		ItemStack stack = inv.getItem(slot);
		if (amount > 0 && stack != null){
			checkPower(10);
			amount = Math.min(amount, stack.getAmount());
			if (amount == stack.getAmount()){
				inv.setItem(slot, null);
			}
			else{
				stack.setAmount(stack.getAmount() - amount);
			}
			final ItemStack dropped = stack.clone();
			dropped.setAmount(amount);
			try {
				return sched.callSyncMethod(p, new Callable<ItemWrapper>(){
					@Override
					public ItemWrapper call(){
						return new ItemWrapper(loc.getWorld().dropItemNaturally(loc, dropped));
					}
				}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**Gets the node's caster.
	 * @return PlayerWrapper
	 */
	public PlayerWrapper getCaster() {
		return new PlayerWrapper(sender);
	}

	/**Gets the node's command.
	 * @return String command
	 */
	public String getCommand(){
		return command;
	}

	/**Gets the node's arguments.
	 * @return String array of arguments
	 */
	public String[] getArgs(){
		return args;
	}

	/**Sets the vehicle entity's passenger. The vehicle entity and passenger entity must be within 1 meter.
	 * @param passenger - EntityWrapper
	 * @param vehicle - EntityWrapper
	 * @power 50
	 */
	public void setPassenger(final EntityWrapper passenger, final EntityWrapper vehicle){
		if (passenger.getEntity().getLocation().distance(loc) > 1 || vehicle.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		checkPower(50);
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					vehicle.getEntity().setPassenger(passenger.getEntity());
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Sets the fire ticks of the entity. The entity must be within 1 meter.
	 * @param entity - EntityWrapper
	 * @param ticks - ticks the entity will be on fire
	 * @power The difference between the current fire ticks and the specified amount
	 */
	public void setFireTicks(final EntityWrapper entity, final int ticks){
		if (entity.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		checkPower(Math.abs(entity.getEntity().getFireTicks() - ticks));
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					entity.getEntity().setFireTicks(ticks);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Sets the fall distance of the entity. The entity must be within 1 meter.
	 * @param entity - EntityWrapper
	 * @param distance - fall distance
	 * @power difference between the current fall distance and the specified amount
	 */
	public void setFallDistance(final EntityWrapper entity, final float distance){
		if (entity.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		checkPower(Math.abs(entity.getEntity().getFallDistance()-distance));
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					entity.getEntity().setFallDistance(distance);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Sends a message to the specified player.
	 * @param player - PlayerWrapper
	 * @param message - String
	 * @power 1
	 */
	public void announce(final PlayerWrapper player, final String message){
		checkPower(1);
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					player.getPlayer().sendMessage(message);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Adds a potion effect to the living entity. The entity must be within 1 meter.
	 * @param live - LivingEntityWrapper
	 * @param effect - PotionEffect
	 * @power duration multiplied by amplifier+1
	 */
	public void addPotionEffect(final LivingEntityWrapper live, final PotionEffect effect){
		if (live.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		checkPower((effect.getAmplifier()+1)*effect.getDuration());
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					live.getLivingEntity().addPotionEffect(effect);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Creates a potion effect.
	 * @param potionEffectType - String name of potion effect type
	 * @param duration - Duration in ticks
	 * @param amplifier - Amplifier. The base effect is 0
	 * @return Created potion effect
	 */
	public PotionEffect createPotionEffect(String potionEffectType, int duration, int amplifier){
		return new PotionEffect(PotionEffectType.getByName(potionEffectType), duration, amplifier);
	}

	/**Sets the living entity's remaining air. Must be within 1 meter of entity.
	 * @param live - LivingEntityWrapper
	 * @param air - remaining air in ticks
	 * @power difference between current remaining air and specified value
	 */
	public void setRemainingAir(final LivingEntityWrapper live, final int air){
		if (live.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		checkPower(Math.abs(live.getLivingEntity().getRemainingAir()-air));
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					live.getLivingEntity().setRemainingAir(air);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Sets the food level of the specified player. Must be within 1 meter of the player.
	 * @param live - PlayerWrapper
	 * @param level - Food level
	 * @power difference between current food level and specified value
	 */
	public void setFoodLevel(final PlayerWrapper live, final int level){
		if (live.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		checkPower(Math.abs(live.getPlayer().getFoodLevel()-level));
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					live.getPlayer().setFoodLevel(level);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Sends a block change to the player. This is an illusion and no map change will actually occur.
	 * @param player - PlayerWrapper
	 * @param location - Location of block change as an array of 3 doubles
	 * @param type - Material of block change as a string
	 * @param data - Data as byte
	 * @power 10
	 */
	public void sendBlockChange(final PlayerWrapper player, double[] location, final String type, final byte data){
		if (player.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		checkPower(10);
		final Location loca = new Location(loc.getWorld(), location[0], location[1], location[2]);
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@SuppressWarnings("deprecation")
				@Override
				public Boolean call(){
					player.getPlayer().sendBlockChange(loca, Material.valueOf(type), data);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Sets the item meta of an item in the inventory.
	 * @param slot - Slot number
	 * @param meta - ItemMeta
	 */
	public void setItemMeta(int slot, ItemMeta meta){
		ItemStack stack = inv.getItem(slot);
		stack.setItemMeta(meta);
	}

	/**Sets the pickup delay of an ItemWrapper. Must be within 1 meter of the item.
	 * @param item - ItemWrapper
	 * @param delay - delay in ticks
	 * @power difference between current pickup delay and specified value
	 */
	public void setPickupDelay(final ItemWrapper item, final int delay){
		if (item.getEntity().getLocation().distance(loc) > 1){
			return;
		}
		checkPower(Math.abs(item.getItem().getPickupDelay() - delay));
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					item.getItem().setPickupDelay(delay);
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**Sets the current block the node resides in on fire if the block is air.
	 * @power 50
	 */
	public void burn() {
		final Block block = loc.getBlock();
		if (block.getType() == Material.AIR){
			checkPower(50);
			try {
				sched.callSyncMethod(p, new Callable<Boolean>(){
					@Override
					public Boolean call(){
						block.setType(Material.FIRE);
						return true;
					}
				}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**Gets the material the block the node resides in is made of.
	 * @return Material
	 */
	public Material inspectBlock() {
		return loc.getBlock().getType();
	}

	/**
	 * Creates an explosion at the location of the node which has a power of (remainingPower/1000)
	 * @power 1000
	 */
	public void selfDestruct() {
		checkPower(1000);
		final World world = loc.getWorld();
		try {
			sched.callSyncMethod(p, new Callable<Boolean>(){
				@Override
				public Boolean call(){
					world.createExplosion(loc, (float) (getPower()/1000));
					return true;
				}
			}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		suicide();
	}

	/**Gets whether or not the node is still alive
	 * @return true if yes, false if no
	 */
	public boolean isAlive(){
		int taskId = task.getTaskId();
		return (p.getServer().getScheduler().isQueued(taskId) || 
				p.getServer().getScheduler().isCurrentlyRunning(taskId));
	}

	void setTask(BukkitTask task){
		this.task = task;
	}

	@Override
	public boolean equals(Object object){
		if (object == null) { return false; }
		if (object == this) { return true; }
		if (object.getClass() != getClass()) {
			return false;
		}
		Node rhs = (Node) object;
		return new EqualsBuilder()
		.append(uid, rhs.uid)
		.isEquals();
	}

}
