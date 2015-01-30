package com.aleclownes.SpellScript;

import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

public class BlockStateWrapper extends Wrapper {
	
	public BlockStateWrapper (BlockState state){
		super(state);
	}
	
	protected BlockState getBlockState(){
		return (BlockState)object;
	}
	
	public MaterialData getData(){
		return getBlockState().getData();
	}

}
