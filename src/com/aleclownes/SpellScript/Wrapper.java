package com.aleclownes.SpellScript;

/**Wrapper superclass
 * @author alownes
 *
 */
public class Wrapper {
	
	Object object;
	
	public Wrapper(Object object){
		this.object = object;
	}
	
	/**Gets the object wrapped by this wrapper
	 * @return Wrapped object
	 */
	protected Object getObject(){
		return object;
	}
	
	@Override
	public boolean equals(Object object){
		return this.object.equals(object);
	}

}
