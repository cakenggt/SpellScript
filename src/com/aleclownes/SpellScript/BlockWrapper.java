package com.aleclownes.SpellScript;

import org.bukkit.block.Block;

/**Wrapper for Block
 * @author alownes
 *
 */
public class BlockWrapper extends Wrapper {
	
	public BlockWrapper(Block block){
		super(block);
	}
	
	/**Gets the wrapped Block
	 * @return Block
	 */
	protected Block getBlock(){
		return (Block)object;
	}
	
	/**Gets the BlockStateWrapper which wraps this block's BlockState
	 * @return BlockStateWrapper
	 */
	public BlockStateWrapper getBlockState(){
		return new BlockStateWrapper(getBlock().getState());
	}
	
	/**Gets the location of this block
	 * @return Location as an array of 3 int
	 */
	public int[] getLocation(){
		int[] loc = new int[3];
		loc[0] = getBlock().getX();
		loc[1] = getBlock().getY();
		loc[2] = getBlock().getZ();
		return loc;
	}
	
	@Override
	public boolean equals(Object object){
		return getBlock().equals(object);
	}

}
