package com.aleclownes.SpellScript;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
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

	public Node(SpellScript p, Player sender, ChatWrapper chatWrapper, String command, String[] args){
		if (sender != null){
			this.sender = sender;
			this.loc = sender.getEyeLocation();
			this.direction = loc.getDirection();
		}
		uid = UUID.randomUUID();
		this.chatWrapper = chatWrapper;
		this.command = command;
		this.args = args;
		this.p = p;
		inv = p.getServer().createInventory(null, 54);
	}
	
	/**If enough power exists in the node, take the required power away. If it doesn't exist, kill the node.
	 * @param power
	 */
	private void checkPower(double power){
		if (this.power > power){
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
		//TODO somehow do this with the BukkitTask
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
		entity.getEntity().teleport(dest);
	}
	
	public double getPower() {
		return power;
	}
	
	public void takePower(final LivingEntityWrapper live, double amount){
		amount = Math.min(amount, 1000.0);
		final double health = amount/SpellScript.powerToHealthRatio;
		checkPower(1);
		double receivedPower = Math.min(health, live.getHealth())*SpellScript.powerToHealthRatio;
		live.getLivingEntity().damage(health);
		power += receivedPower;
	}
	
	public void takePower(NodeWrapper live, double amount){
		//TODO auto
	}
	
	public void givePower(LivingEntityWrapper live, double amount){
		//TODO auto
	}
	
	public void givePower(NodeWrapper live, double amount){
		//TODO auto
	}
	
	public Vector getVelocity(EntityWrapper entity){
		//TODO auto
		return null;
	}
	
	public void setVelocity(EntityWrapper entity, Vector vector){
		//TODO auto
	}

	public EntityWrapper[] getNearbyEntities(double distance) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public NodeWrapper[] getNearbyNodes(double distance){
		//TODO
		return null;
	}

	public void breakBlock() {
		// TODO Auto-generated method stub

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
	
	public void setPassenger(EntityWrapper passenger, EntityWrapper vehicle){
		//TODO
	}
	
	public void setFireTicks(EntityWrapper entity, int ticks){
		//TODO
	}
	
	public void setFallDistance(EntityWrapper entity, float distance){
		//TODO
	}
	
	public void announce(PlayerWrapper player, String message){
		checkPower(1);
		player.getPlayer().sendMessage(message);
	}
	
	public void addPotionEffect(LivingEntityWrapper live, PotionEffect effect){
		//TODO
	}
	
	public PotionEffect createPotionEffect(String potionEffectType, int duration, int amplifier){
		//TODO
		return null;
	}
	
	public void setRemainingAir(LivingEntityWrapper live, int air){
		//TODO
	}
	
	public void setFoodLevel(PlayerWrapper live, int level){
		//TODO
	}
	
	public void sendBlockChange(PlayerWrapper player, double[] location, String type, byte data){
		//TODO
	}
	
	public void setItemMeta(int slot, ItemMeta meta){
		//TODO
	}
	
	public void setPickupDelay(ItemWrapper item, int delay){
		//TODO
	}

	public void burn() {
		// TODO Auto-generated method stub

	}

	public Material inspectBlock() {
		// TODO Auto-generated method stub
		return null;
	}

	public void selfDestruct() {
		// TODO Auto-generated method stub
		
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
