package com.aleclownes.SpellScript;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class SpellScript extends JavaPlugin implements Listener, Runnable {
	
	List<NodeThread> nodeThreadList = new ArrayList<NodeThread>();
	public static final double chatRadius = 10;
	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this, 20L, 1L);
		getLogger().info(this + " enabled!");
	}

	@Override
	public void onDisable(){
		getLogger().info(this + " disabled!");
	}
	
	@Override
	public void run() {
		for (NodeThread nodeThread : nodeThreadList){
			Location loc = nodeThread.getNode().loc;
			loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		World world = event.getPlayer().getWorld();
		String message = event.getMessage();
		for (Entity entity : world.getEntities()){
			if (entity instanceof InventoryHolder){
				InventoryHolder holder = (InventoryHolder)entity;
				for (ItemStack stack : holder.getInventory()){
					checkStack(player, message, stack);
				}
			}
			else if (entity instanceof ItemFrame){
				ItemFrame frame = (ItemFrame)entity;
				checkStack(player, message, frame.getItem());
			}
			else if (entity instanceof Item){
				Item item = (Item)entity;
				checkStack(player, message, item.getItemStack());
			}
		}
	}
	
	public static void checkStack(Player player, String message, ItemStack stack){
		String[] split = message.split(" ");
		if (split.length > 0){
			ItemMeta meta = stack.getItemMeta();
			if (split[0].equals(meta.getDisplayName())){
				String[] args = new String[split.length-1];
				//TODO finish this method
			}
		}
	}
	
	public static void main(String[] args){
		String command = "importPackage = null; importClass = null;"
				+ "println(1);"
				+ "node.sleep(2000);"
				+ "println(2);"
				+ "println(node.uid);";
		Node node = new Node(null);
		NodeThread thread = new NodeThread(new NodeRunnable(node, command, args));
		thread.start();
	}

}
