package com.aleclownes.SpellScript;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class NodeRunnable implements Runnable {
	
	String command = "";
	String[] args;
	Node node;
	
	public Node getNode() {
		return node;
	}

	public NodeRunnable(Node node, String command, String[] args){
		this.node = node;
		this.command = command;
		this.args = args;
	}

	@Override
	public void run() {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		engine.put("node", (NodeInterface)node);
		engine.put("args", args);
		try {
			engine.eval(command);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

}
