package com.aleclownes.SpellScript;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

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
	
	public ChatWrapper getChatWrapper(){
		return chatWrapper;
	}
	
	public void suicide() {
		task.cancel();
	}

	public void sleep(double seconds) {
		try {
			Thread.sleep((long) (seconds*1000.0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void moveForward() {
		checkPower(1);
		loc = loc.add(direction);	
	}

	public void setDirection(Vector direction){
		if (direction.length() > 1){
			direction = direction.normalize();
		}
		this.direction = direction;
	}

	public Vector getDirection() {
		return direction;
	}
	
	public double[] getLocation() {
		return locationToArray(loc);
	}
	
	static double[] locationToArray(Location loc){
		double[] array = new double[3];
		array[0] = loc.getX();
		array[1] = loc.getY();
		array[2] = loc.getZ();
		return array;
	}
	
	public void teleport(final EntityWrapper entity, double[] location){
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
	
	public double getPower() {
		return power;
	}
	
	public void takePower(final LivingEntityWrapper live, double amount){
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
	
	public void takePower(final NodeWrapper node, double amount){
		final double newAmount = Math.min(amount, 1000.0);
		checkPower(1);
		double receivedPower = Math.min(newAmount, node.getNode().getPower());
		power += receivedPower;
		node.getNode().power -= receivedPower;
	}
	
	public void givePower(final LivingEntityWrapper live, double amount){
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
	
	public void givePower(NodeWrapper node, double amount){
		final double newAmount = Math.min(amount, 1000.0);
		checkPower(1);
		double receivedPower = Math.min(newAmount, getPower());
		power -= receivedPower;
		node.getNode().power += receivedPower;
	}
	
	public void setVelocity(final EntityWrapper entity, final Vector vector){
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

	public List<EntityWrapper> getNearbyEntities(double distance) {
		List<EntityWrapper> nearby = new ArrayList<EntityWrapper>();
		for (Entity entity : loc.getWorld().getEntities()){
			if (entity.getLocation().distance(loc) <= distance){
				nearby.add(new EntityWrapper(entity));
			}
		}
		return nearby;
	}
	
	public List<NodeWrapper> getNearbyNodes(double distance){
		List<NodeWrapper> nearby = new ArrayList<NodeWrapper>();
		for (Node node : p.nodeList){
			if (node.loc.distance(loc) <= distance){
				nearby.add(new NodeWrapper(node));
			}
		}
		return nearby;
	}

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
	
	public void placeBlock(int slot){
		//TODO
	}
	
	public void takeItem(ItemWrapper item){
		//TODO
	}
	
	public ItemWrapper dropItem(int slot, int amount){
		//TODO
		return null;
	}

	public PlayerWrapper getCaster() {
		return new PlayerWrapper(sender);
	}

	public String getCommand(){
		return command;
	}

	public String[] getArgs(){
		return args;
	}
	
	public void setPassenger(final EntityWrapper passenger, final EntityWrapper vehicle){
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
	
	public void setFireTicks(final EntityWrapper entity, final int ticks){
		checkPower(50);
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
	
	public void setFallDistance(final EntityWrapper entity, final float distance){
		checkPower(50);
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
	
	public void addPotionEffect(final LivingEntityWrapper live, final PotionEffect effect){
		checkPower(50);
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
	
	public PotionEffect createPotionEffect(String potionEffectType, int duration, int amplifier){
		return new PotionEffect(PotionEffectType.getByName(potionEffectType), duration, amplifier);
	}
	
	public void setRemainingAir(final LivingEntityWrapper live, final int air){
		checkPower(50);
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
	
	public void setFoodLevel(final PlayerWrapper live, final int level){
		checkPower(100);
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
	
	public void sendBlockChange(final PlayerWrapper player, double[] location, final String type, final byte data){
		checkPower(50);
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
	
	public void setItemMeta(int slot, ItemMeta meta){
		ItemStack stack = inv.getItem(slot);
		stack.setItemMeta(meta);
	}
	
	public void setPickupDelay(final ItemWrapper item, final int delay){
		checkPower(50);
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

	public Material inspectBlock() {
		return loc.getBlock().getType();
	}

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
	
	public boolean isAlive(){
		int taskId = task.getTaskId();
		return (p.getServer().getScheduler().isQueued(taskId) || 
				p.getServer().getScheduler().isCurrentlyRunning(taskId));
	}
	
	void setTask(BukkitTask task){
		this.task = task;
	}

}
