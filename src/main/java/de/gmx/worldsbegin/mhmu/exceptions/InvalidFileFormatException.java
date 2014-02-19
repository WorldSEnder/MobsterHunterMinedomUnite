package de.gmx.worldsbegin.mhmu.exceptions;

import java.io.IOException;

public class InvalidFileFormatException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7449631551610798L;
	public InvalidFileFormatException() {
		super();
	}
	public InvalidFileFormatException(String message) {
		super(message);
	}
	public InvalidFileFormatException(String message, Throwable throwable) {
		super(message, throwable);
	}
	public InvalidFileFormatException(Throwable throwable) {
		super(throwable);
	}
}
