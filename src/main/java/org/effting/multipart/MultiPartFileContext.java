package org.effting.multipart;

import java.nio.file.Path;

/**
 * 
 * @author Charles Kafels Effting.
 *
 */
public interface MultiPartFileContext {

	/**
	 * 
	 * @return
	 */
	public Path getTempFile();
	
	/**
	 * 
	 * @return the MIME type of the file.
	 */
	public String getContentType();
}
