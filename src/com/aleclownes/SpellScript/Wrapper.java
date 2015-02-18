package com.aleclownes.SpellScript;

import org.apache.commons.lang.builder.EqualsBuilder;

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
		if (object == null) { return false; }
		if (object == this) { return true; }
		if (object.getClass() != getClass()) {
			return false;
		}
		Wrapper rhs = (Wrapper) object;
		return new EqualsBuilder()
		.append(getObject(), rhs.getObject())
		.isEquals();
	}

}
