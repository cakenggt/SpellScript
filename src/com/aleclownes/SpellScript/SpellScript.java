package com.aleclownes.SpellScript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class SpellScript extends JavaPlugin implements Listener, Runnable {

	List<Node> nodeList = new ArrayList<Node>();
	public static final double chatRadius = 10;
	public static final String token = "SpellScript:";
	public static final String ticket = "SpellScript Ticket:";
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
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if (sender instanceof Player){
			player = (Player)sender;
		}
		if (cmd.getName().equalsIgnoreCase("SpellScript") && player != null) {
			player = (Player)sender;
			ItemStack inHand = player.getItemInHand();
			Collection<Item> items = player.getWorld().getEntitiesByClass(Item.class);
			if (inHand.getItemMeta() instanceof BookMeta){
				BookMeta book = (BookMeta)inHand.getItemMeta();
				String command = "";
				for (String page : book.getPages()){
					command += page;
				}
				for (Item item : items){
					if (item.getLocation().distance(player.getLocation()) <= 5){
						ItemStack itemStack = item.getItemStack();
						ItemMeta meta = itemStack.getItemMeta();
						List<String> lore = new ArrayList<String>();
						if (meta.hasLore()){
							lore = meta.getLore();
						}
						lore.add(token + command);
						meta.setLore(lore);
						itemStack.setItemMeta(meta);
						item.setItemStack(itemStack);
					}
				}
			}
			return true;
		}
		return false; 
	}

	@Override
	public void run() {
		Iterator<Node> it = nodeList.iterator();
		while (it.hasNext()){
			Node node = it.next();
			if (node.isAlive()){
				Location loc = node.loc;
				loc.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
			}
			else{
				it.remove();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTicketActivation(PlayerInteractEvent event){
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
			Player player = event.getPlayer();
			ItemStack inHand = player.getItemInHand();
			if (inHand.getType() == Material.PAPER){
				ItemMeta meta = inHand.getItemMeta();
				if (meta.hasLore()){
					List<String> lore = meta.getLore();
					Iterator<String> it = lore.iterator();
					while(it.hasNext()){
						String line = it.next();
						if (line.startsWith(ticket)){
							UUID uid = UUID.fromString(line.substring(ticket.length()));
							Iterator<Node> nodeIt = nodeList.iterator();
							while(nodeIt.hasNext()){
								Node node = nodeIt.next();
								if (node.uid.equals(uid)){
									node.task.cancel();
									it.remove();
								}
							}
							it.remove();
						}
					}
					meta.setLore(lore);
				}
				inHand.setItemMeta(meta);
			}
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
					if (stack != null){
						checkStack(event, player, message, stack, entity.getLocation());
					}
				}
			}
			else if (entity instanceof ItemFrame){
				ItemFrame frame = (ItemFrame)entity;
				if (frame.getItem() != null){
					checkStack(event, player, message, frame.getItem(), entity.getLocation());
				}
			}
			else if (entity instanceof Item){
				Item item = (Item)entity;
				checkStack(event, player, message, item.getItemStack(), entity.getLocation());
			}
		}
		ChatWrapper chat = new ChatWrapper(event);
		for (Node node : nodeList){
			if (node.loc.getWorld().equals(world)){
				node.setChatWrapper(chat);
			}
		}
	}

	public void checkStack(AsyncPlayerChatEvent event, Player player, String message, ItemStack stack, Location loc){
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
							Node node = new Node(this, player, new ChatWrapper(event), command, args, loc);
							node.setTask(getServer().getScheduler().runTaskLaterAsynchronously(this, new NodeRunnable(this, node), 1));
							nodeList.add(node);
							it.remove();
							ItemStack ticket = new ItemStack(Material.PAPER);
							ItemMeta ticketMeta = ticket.getItemMeta();
							ticketMeta.setLore(Arrays.asList(SpellScript.ticket + node.uid.toString()));
							ticket.setItemMeta(ticketMeta);
							for (ItemStack droppedStack : player.getInventory().addItem(ticket).values()){
								player.getWorld().dropItem(player.getLocation(), droppedStack);
							}
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
				+ "println(2);"
				+ "println(new Vector() instanceof Vector);"
				+ "println(test instanceof com.aleclownes.SpellScript.Node);";
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		try {
			engine.eval("importClass(org.bukkit.util.Vector);importPackage(org.bukkit.potion);");
			engine.eval("importPackage = null; importClass = null");
			engine.put("test", new NodeRunnable(null, null));
			engine.eval(command);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
