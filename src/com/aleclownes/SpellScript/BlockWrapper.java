package com.aleclownes.SpellScript;

import org.bukkit.block.Block;

public class BlockWrapper extends Wrapper {
	
	public BlockWrapper(Block block){
		super(block);
	}
	
	protected Block getBlock(){
		return (Block)object;
	}
	
	public BlockStateWrapper getBlockState(){
		return new BlockStateWrapper(getBlock().getState());
	}
	
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
