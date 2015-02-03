package com.aleclownes.SpellScript;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**Wrapper class for AsyncPlayerChatEvent
 * @author alownes
 *
 */
public class ChatWrapper extends Wrapper {
	
	private Player player;
	private String message;
	
	public ChatWrapper(AsyncPlayerChatEvent event){
		super(event);
		player = event.getPlayer();
		message = event.getMessage();
	}
	
	/**Gets the player who made this chat
	 * @return Player
	 */
	protected Player getPlayer(){
		return player;
	}
	
	/**Gets the PlayerWrapper associated with the speaking player
	 * @return PlayerWrapper
	 */
	public PlayerWrapper getSpeaker(){
		return new PlayerWrapper(player);
	}
	
	/**Gets the message said in the chat
	 * @return message
	 */
	public String getMessage(){
		return message;
	}
	
	@Override
	public boolean equals(Object object){
		return getObject().equals(object);
	}

}
