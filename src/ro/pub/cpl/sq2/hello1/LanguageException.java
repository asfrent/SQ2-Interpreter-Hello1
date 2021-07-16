package ro.pub.cpl.sq2.hello1;

public class LanguageException extends Error {
	private static final long serialVersionUID = 1L;
	final static String ERROR_MESSAGE_LINE = "Error in line ";
	final static String ERROR_MESSAGE = "Error: ";

	int lineNumber;
	String message;

	@Override
	public String toString() {
		if (lineNumber > 0)
			return ERROR_MESSAGE_LINE + lineNumber + ": " + message;
		else
			return ERROR_MESSAGE + message;
	}

	/** Create a semantic error */
	public LanguageException(String m) {
		super();
		message = m;
	}

	/** Create a semantic error with source code location */
	public LanguageException(String message, int aLineNumber) {
		this(message);
		lineNumber = aLineNumber;
	}
}