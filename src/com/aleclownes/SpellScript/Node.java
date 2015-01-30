package com.aleclownes.SpellScript;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Node implements NodeInterface {
	
	Location loc;
	Vector direction;
	int power = 1000;
	Player sender;
	UUID uid;

	public Node(Player sender){
		if (sender != null){
			this.sender = sender;
			this.loc = sender.getEyeLocation();
			this.direction = loc.getDirection();
		}
		uid = UUID.randomUUID();
	}

	@Override
	public double[] getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector getDirection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void breakBlock() {
		// TODO Auto-generated method stub

	}

	@Override
	public void burn() {
		// TODO Auto-generated method stub

	}

	@Override
	public Material inspectBlock() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void move(double x, double y, double z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveInDirection(double distance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void damage(double damage) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getPower() {
		return power;
	}

	@Override
	public void sleep(int ticks) {
		try {
			Thread.sleep((long)ticks);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void selfDestruct() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EntityWrapperInterface[] getNearbyEntities(double distance) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void suicide() {
		Thread.currentThread().stop();
	}

	@Override
	public String getCaster() {
		return sender.getName();
	}

}
