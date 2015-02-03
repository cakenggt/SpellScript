package com.aleclownes.SpellScript;

public class NodeWrapper extends Wrapper {
	
	public NodeWrapper(Node node){
		super(node);
	}
	
	protected Node getNode(){
		return (Node)object;
	}
	
	public double[] getLocation(){
		return getNode().getLocation();
	}
	
	public double getPower(){
		return getNode().getPower();
	}
	
	public PlayerWrapper getCaster(){
		return getNode().getCaster();
	}
	
	public String getCommand(){
		return getNode().getCommand();
	}
	
	public String[] getArgs(){
		return getNode().getArgs();
	}
	
	public boolean isAlive(){
		return getNode().isAlive();
	}
	
	public Object getMapValue(String key){
		return getNode().getMap().get(key);
	}
	
	@Override
	public boolean equals(Object object){
		return getNode().equals(object);
	}

}
