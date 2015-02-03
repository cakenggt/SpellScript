package com.aleclownes.SpellScript;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**Wrapper class for ItemStack
 * @author alownes
 *
 */
public class ItemStackWrapper extends Wrapper {
	
	public ItemStackWrapper(ItemStack stack){
		super(stack);
	}
	
	/**Get the wrapped ItemStack
	 * @return ItemStack
	 */
	protected ItemStack getItemStack(){
		return (ItemStack)object;
	}
	
	/**Gets the material of the ItemStack
	 * @return Material
	 */
	public Material getType(){
		return getItemStack().getType();
	}
	
	/**Gets the size of the ItemStack
	 * @return size of ItemStack
	 */
	public int getAmount(){
		return getItemStack().getAmount();
	}
	
	/**Gets the ItemMeta of the ItemStack
	 * @return ItemMeta
	 */
	public ItemMeta getItemMeta(){
		return getItemStack().getItemMeta();
	}
	
	@Override
	public boolean equals(Object object){
		return getItemStack().equals(object);
	}

}
