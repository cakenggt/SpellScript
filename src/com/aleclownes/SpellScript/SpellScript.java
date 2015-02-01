package com.aleclownes.SpellScript;

import java.util.ArrayList;
import java.util.Iterator;
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
	
	List<Node> nodeList = new ArrayList<Node>();
	public static final double chatRadius = 10;
	public static final String token = "SpellScript:";
	public static final double powerToHealthRatio = 1000.0;
	
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
		for (Node node : nodeList){
			Location loc = node.loc;
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
					checkStack(event, player, message, stack);
				}
			}
			else if (entity instanceof ItemFrame){
				ItemFrame frame = (ItemFrame)entity;
				checkStack(event, player, message, frame.getItem());
			}
			else if (entity instanceof Item){
				Item item = (Item)entity;
				checkStack(event, player, message, item.getItemStack());
			}
		}
	}
	
	public void checkStack(AsyncPlayerChatEvent event, Player player, String message, ItemStack stack){
		String[] split = message.split(" ");
		if (split.length > 0){
			ItemMeta meta = stack.getItemMeta();
			if (split[0].equals(meta.getDisplayName())){
				if (meta.hasLore()){
					List<String> lore = meta.getLore();
					String[] args = new String[split.length-1];
					for (int i = 1; i < split.length; i++){
						args[i-1] = split[i];
					}
					Iterator<String> it = lore.iterator();
					while (it.hasNext()){
						String loreLine = it.next();
						if (loreLine.startsWith(token)){
							String command = loreLine.substring(token.length());
							Node node = new Node(this, player, new ChatWrapper(event), command, args);
							node.setTask(getServer().getScheduler().runTask(this, new NodeRunnable(this, node)));
							nodeList.add(node);
							it.remove();
						}
					}
					meta.setLore(lore);
					stack.setItemMeta(meta);
				}
			}
		}
	}
	
	public static void main(String[] args){
		String command = "importPackage = null; importClass = null;"
				+ "println(1);"
				+ "node.sleep(2000);"
				+ "println(2);"
				+ "println(node.uid);";
		Node node = new Node(null, null, null, command, args);
		NodeThread thread = new NodeThread(new NodeRunnable(null, node));
		thread.start();
	}

}
