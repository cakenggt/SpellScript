package com.aleclownes.SpellScript;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

public abstract class WrapperFactory {
	
	private WrapperFactory(){}
	
	public Wrapper wrap(Object object){
		if (object instanceof Node){
			return new NodeWrapper((Node)object);
		}
		else if (object instanceof Entity){
			if (object instanceof LivingEntity){
				if (object instanceof Player){
					return new PlayerWrapper((Player)object);
				}
			}
			else if (object instanceof Item){
				return new ItemWrapper((Item)object);
			}
			return new EntityWrapper((Entity)object);
		}
		else if (object instanceof ItemStack){
			return new ItemStackWrapper((ItemStack)object);
		}
		else if (object instanceof Block){
			return new BlockWrapper((Block)object);
		}
		else if (object instanceof BlockState){
			return new BlockStateWrapper((BlockState)object);
		}
		else if (object instanceof Event){
			if (object instanceof AsyncPlayerChatEvent){
				return new ChatWrapper((AsyncPlayerChatEvent)object);
			}
		}
		return new Wrapper(object);
	}

}
