package com.aleclownes.SpellScript;

public class NodeThread extends Thread {
	
	NodeRunnable runnable;
	
	public NodeThread(NodeRunnable runnable){
		super(runnable);
		this.runnable = runnable;
	}
	
	public Node getNode(){
		return runnable.getNode();
	}

}
