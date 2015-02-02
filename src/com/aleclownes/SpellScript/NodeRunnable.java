package com.aleclownes.SpellScript;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class NodeRunnable implements Runnable {

	Node node;
	SpellScript p;

	public Node getNode() {
		return node;
	}

	public NodeRunnable(SpellScript p, Node node){
		this.node = node;
		this.p = p;
	}

	@Override
	public void run() {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		engine.put("node", node);
		engine.put("args", node.getArgs());
		try {
			engine.eval("importClass(org.bukkit.util.Vector);importPackage(org.bukkit.potion)");
			engine.eval("importPackage = null; importClass = null");
			engine.eval(node.getCommand());
		} catch (final Exception e) {
			p.getServer().getScheduler().runTask(p, new Runnable(){
				public void run() {
					node.sender.sendMessage(e.getMessage());
				}
			});

		}
	}

}
