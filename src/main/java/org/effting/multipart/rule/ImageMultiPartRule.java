package org.effting.multipart.rule;

import java.util.HashSet;
import java.util.Set;

import org.effting.multipart.MultiPartFileContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.staroski.rules.Specification;
import br.com.staroski.rules.UnattendedException;

/**
 * 
 * @author Charles Kafels Effting.
 *
 */
public class ImageMultiPartRule implements Specification<MultiPartFileContext> {

	private static final Set<String> SUPPORTS = new HashSet<>();
	
	static {
		SUPPORTS.add("image/png");
		SUPPORTS.add("image/jpeg");
		SUPPORTS.add("image/pjpeg");
	}
	
	private final Logger logger = LoggerFactory.getLogger(ImageMultiPartRule.class);
	
	/**
	 * 
	 */
	public ImageMultiPartRule() {
	}
	
	@Override
	public void verify(MultiPartFileContext object) throws UnattendedException {
		// Reference to get the content type of the file.
		String contentType = object.getContentType();
		
		// Check if the content type is supported.
		if (!SUPPORTS.contains(contentType)) {
			// Log.
			logger.debug(String.format("The content type '%s' is not supported", contentType));
			
			// Add a brief detail.
			throw new UnattendedException("O arquivo enviado não é suportado.");
		}
	}
}
