package org.effting.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import org.apache.tika.Tika;

import br.com.staroski.rules.Rule;

/**
 * 
 * @author Charles Kafels effting.
 *
 */
public final class MultiPartFile {
	
	/**
	 * 
	 */
	private static final class Context implements MultiPartFileContext {

		private Path tempFile;
		private String type;
		
		/**
		 * 
		 * @param tempFile
		 * @param type
		 */
		public Context(Path tempFile, String type) {
			this.tempFile = tempFile;
			this.type = type;
		}

		@Override
		public Path getTempFile() {
			return tempFile;
		}
		
		@Override
		public String getContentType() {
			return type;
		}
	}

	/**
	 * 10MB
	 */
	public static final long DEFAULT_MAX_SIZE = 10 * 1024 * 1024;
	
	private long maxSize = DEFAULT_MAX_SIZE;
	private InputStream inputStream;
	private Tika tika;
	private Rule<MultiPartFileContext> rules;

	/**
	 * 
	 * @param inputStream
	 * @return
	 */
	public static MultiPartFile get(InputStream inputStream) {
		return new MultiPartFile(inputStream);
	}

	/**
	 * 
	 * @param is
	 */
	private MultiPartFile(InputStream is) {
		// Requires a non-null stream.
		Objects.requireNonNull(is, "inputStream cannot be null");
		
		inputStream = is;
		// Determine the MIME type of the input stream.
		tika = new Tika();
	}
	
	/**
	 * 
	 * @param size
	 * @return
	 */
	public MultiPartFile maxSize(int size) {
		maxSize = size * 1024 * 1024;
		return this;
	}
	
	/**
	 * 
	 * @param rules
	 * @return
	 */
	public MultiPartFile isSatisfiedBy(Rule<MultiPartFileContext> rules) {
		this.rules = rules;
		return this;
	}
	
	/**
	 * 
	 * @param target
	 */
	public void writeTo(String target) throws IOException {
		writeTo(Paths.get(target));
	}
	
	/**
	 * 
	 * @param target
	 * @throws IOException
	 * @throws UnsatisfiedMultipartRulesException 
	 */
	public void writeTo(Path target) throws IOException {
		// Temporary file to check before moving to the target location. 
		Path temp = null;
		// OutputStream which will be written. 
		OutputStream outputStream = null; 
		
		try {
			// Creates the temporary file.
			temp = Files.createTempFile("multipart_", ".tmp");
			// Open the temporary file to write.
			outputStream = Files.newOutputStream(temp);
			
			int read;
			final byte[] bytes = new byte[2048];
			long actualFileSize = 0;
			
			while ((read = inputStream.read(bytes)) != -1) {
				// Increments the file size.
				actualFileSize += read;
				
				// Checks if the file size exceeds the limit.
				if (actualFileSize > maxSize) {
					throw new IllegalStateException(String.format("The uploaded file exceeds the maximum allowed size (%d)", maxSize));
				}
				
				// Write to the output stream.
				outputStream.write(bytes, 0, read);
			}
			
			// Flush any buffered bytes.
			outputStream.flush();
			
			// Check if temporary file satisfies the rules.
			applyRules(temp);
			
			// Move the file to the target location.
			Files.move(temp, createTargetDirectory(target), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			// Propagates the exception.
			throw e;
		} finally {
			// Close the output.
			if (outputStream != null) {
				outputStream.close();
			}
			
			// Close the input.
			inputStream.close();
			// GC.
			inputStream = null;
			
			// If the temp file was created.
			if (temp != null) {
				// Attempt to delete it.
				Files.deleteIfExists(temp);
			}
		}
	}
	
	/**
	 * 
	 * @param temp
	 */
	protected void applyRules(Path temp) throws IOException {
		// Are there rules to apply?
		if (rules != null) {
			// Create the context.
			Context context = new Context(temp, tika.detect(temp.toFile()));
			
			// Apply them.
			if (rules.not().isSatisfiedBy(context)) {
				// Throw an exception showing the details.
				throw new UnsatisfiedMultipartRulesException(rules.getDetails());
			}
		}
	}
	
	/**
	 * 
	 * @param target
	 * @return
	 * @throws IOException
	 */
	private static Path createTargetDirectory(Path target) throws IOException {
		// Create the directory structure.
		Files.createDirectories(target.getParent());
		// Return the current target to maintain the readability.
		return target;
	}
}
