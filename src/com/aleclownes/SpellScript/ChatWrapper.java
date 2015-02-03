package com.aleclownes.SpellScript;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatWrapper extends Wrapper {
	
	private Player player;
	private String message;
	
	public ChatWrapper(AsyncPlayerChatEvent event){
		super(event);
		player = event.getPlayer();
		message = event.getMessage();
	}
	
	protected Player getPlayer(){
		return player;
	}
	
	public PlayerWrapper getSpeaker(){
		return new PlayerWrapper(player);
	}
	
	public String getMessage(){
		return message;
	}
	
	@Override
	public boolean equals(Object object){
		return getObject().equals(object);
	}

}
