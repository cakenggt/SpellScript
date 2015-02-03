package com.aleclownes.SpellScript;

public class Wrapper {
	
	Object object;
	
	public Wrapper(Object object){
		this.object = object;
	}
	
	protected Object getObject(){
		return object;
	}
	
	@Override
	public boolean equals(Object object){
		return this.object.equals(object);
	}

}
