package com.aleclownes.SpellScript;

import org.bukkit.entity.Item;

/**Wrapper for Item Entity
 * @author alownes
 *
 */
public class ItemWrapper extends EntityWrapper {
	
	public ItemWrapper (Item item){
		super(item);
	}
	
	/**Gets the wrapped Item
	 * @return wrapped Item
	 */
	protected Item getItem(){
		return (Item)object;
	}
	
	/**Gets the wrapper for the ItemStack associated with this item
	 * @return ItemStackWrapper
	 */
	public ItemStackWrapper getItemStack(){
		return new ItemStackWrapper(getItem().getItemStack());
	}
	
	/**Gets the pickup delay for this Item entity
	 * @return pickup delay in ticks
	 */
	public int getPickupDelay(){
		return getItem().getPickupDelay();
	}
	
	@Override
	public boolean equals(Object object){
		return getItem().equals(object);
	}

}
