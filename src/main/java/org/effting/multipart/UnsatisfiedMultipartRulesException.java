package org.effting.multipart;

import java.util.Collection;

/**
 * 
 * @author Charles Kafels Effting.
 *
 */
public class UnsatisfiedMultipartRulesException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Collection<String> details;
	
	/**
	 * 
	 * @param details
	 */
	public UnsatisfiedMultipartRulesException(Collection<String> details) {
		super("The current MultiPartFileContext doesn't satisfy the rules applied.");
		this.details = details;
	}
	
	/**
	 * 
	 * @return
	 */
	public Collection<String> getDetails() {
		return details;
	}
}
