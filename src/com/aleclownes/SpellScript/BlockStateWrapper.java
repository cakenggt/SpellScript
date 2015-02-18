package com.aleclownes.SpellScript;

import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

/**Wrapper class for BlockState
 * @author alownes
 *
 */
public class BlockStateWrapper extends Wrapper {
	
	public BlockStateWrapper (BlockState state){
		super(state);
	}
	
	/**Gets the wrapped BlockState
	 * @return BlockState
	 */
	protected BlockState getBlockState(){
		return (BlockState)object;
	}
	
	/**Gets the MaterialData associated with this BlockState
	 * @return MaterialData
	 */
	public MaterialData getData(){
		return getBlockState().getData();
	}

}
