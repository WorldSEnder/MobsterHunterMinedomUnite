package de.gmx.worldsbegin.mhmu.exceptions;

import java.io.IOException;

public class EndOfStreamException extends IOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 14911205513817816L;
	public EndOfStreamException() {
		super();
	}
	public EndOfStreamException(String message) {
		super(message);
	}
	public EndOfStreamException(String message, Throwable throwable) {
		super(message, throwable);
	}
	public EndOfStreamException(Throwable throwable) {
		super(throwable);
	}
}
