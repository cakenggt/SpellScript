package com.aleclownes.SpellScript;

import org.bukkit.entity.Player;

/**Wrapper for Player
 * @author alownes
 *
 */
public class PlayerWrapper extends LivingEntityWrapper {
	
	public PlayerWrapper(Player player){
		super(player);
	}
	
	/**Gets wrapped player
	 * @return Player
	 */
	protected Player getPlayer(){
		return (Player)object;
	}
	
	/**Gets the player's display name
	 * @return display name
	 */
	public String getDisplayName(){
		return getPlayer().getDisplayName();
	}
	
	/**Gets the player's food level
	 * @return food level
	 */
	public int getFoodLevel(){
		return getPlayer().getFoodLevel();
	}
	
	/**Gets the item in the player's hand
	 * @return ItemStackWrapper of the player's in-hand item. Can be null.
	 */
	public ItemStackWrapper getItemInHand(){
		return getPlayer().getItemInHand() != null ? new ItemStackWrapper(getPlayer().getItemInHand()) : null;
	}
	
	/**Gets whether or not the player is online
	 * @return Whether or not the player is online
	 */
	public boolean isOnline(){
		return getPlayer().isOnline();
	}
	
	/**Gets whether or not the player is sneaking
	 * @return Whether or not the player is sneaking
	 */
	public boolean isSneaking(){
		return getPlayer().isSneaking();
	}
	
	@Override
	public boolean equals(Object object){
		return getPlayer().equals(object);
	}

}
