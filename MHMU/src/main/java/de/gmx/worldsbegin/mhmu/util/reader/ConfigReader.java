/**
 * 
 */
package de.gmx.worldsbegin.mhmu.util.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.IllegalFormatConversionException;

import de.gmx.worldsbegin.mhmu.util.reader.ConfigObject.EndOfMapException;

/**
 * @author Carbon
 */
public class ConfigReader {
	/**
	 * This method offers reading from an InputStream directly.
	 * 
	 * @param inStream
	 *            the stream to read from
	 * @return a {@link HashMap}, an Object[], Integer, Float, Boolean or
	 *         String. If it's a {@link HashMap} then the HashMap will have keys
	 *         of type String and values of the previously listed types. Same
	 *         thing with an array.
	 * @throws IOException
	 *             when the reader throws an IOException
	 */
	public static ConfigObject readFrom(BufferedReader reader)
			throws IOException {
		try {
			return new ConfigObject(reader);
		} catch (EndOfMapException eome) {
			return eome.lastElement;
		}
	}

	private BufferedReader reader;

	private Object readConfig;

	public ConfigReader(InputStream inStream)
			throws IllegalFormatConversionException, IOException {
		this.reader = new BufferedReader(new InputStreamReader(inStream));
		this.readConfig = readFrom(this.reader);
	}

	/**
	 * @see #readFrom(InputStream)
	 */
	public Object getReadObject() {
		return this.readConfig;
	}

	public void reset() throws IOException {
		this.reader.reset();
	}
}
