package com.aleclownes.SpellScript;

import java.util.UUID;

/**Wrapper for Node
 * @author alownes
 *
 */
public class NodeWrapper extends Wrapper {
	
	public NodeWrapper(Node node){
		super(node);
	}
	
	/**Gets the wrapped node
	 * @return Node
	 */
	protected Node getNode(){
		return (Node)object;
	}
	
	/**Gets the node's location
	 * @return location as an array of 3 double
	 */
	public double[] getLocation(){
		return getNode().getLocation();
	}
	
	/**Gets the node's remaining power
	 * @return power
	 */
	public double getPower(){
		return getNode().getPower();
	}
	
	/**Gets the node's caster
	 * @return Caster player as a PlayerWrapper
	 */
	public PlayerWrapper getCaster(){
		return getNode().getCaster();
	}
	
	/**Gets the code the node is executing
	 * @return Code as a string
	 */
	public String getCommand(){
		return getNode().getCommand();
	}
	
	/**Gets the arguments that were passed to the node
	 * @return Arguments as a String array
	 */
	public String[] getArgs(){
		return getNode().getArgs();
	}
	
	/**Gets whether or not the node is still executing code and exists in the world
	 * @return Whether the node is still alive
	 */
	public boolean isAlive(){
		return getNode().isAlive();
	}
	
	/**Gets the value in the node's map associated with this key
	 * @param key - key to the node's specified value
	 * @return Value associated with the specified key in the node's map
	 */
	public Object getMapValue(String key){
		return getNode().getMap().get(key);
	}
	
	/**Gets the node's uid
	 * @return UUID
	 */
	public UUID getUid(){
		return getNode().getUid();
	}
	
	@Override
	public boolean equals(Object object){
		return getNode().equals(object);
	}

}
