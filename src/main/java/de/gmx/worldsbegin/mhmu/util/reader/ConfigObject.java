package de.gmx.worldsbegin.mhmu.util.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.gmx.worldsbegin.mhmu.exceptions.EndOfStreamException;
import de.gmx.worldsbegin.mhmu.exceptions.InvalidFileFormatException;

public class ConfigObject {
	@SuppressWarnings("serial")
	public class EndOfMapException extends Exception {
		public ConfigObject lastElement;
		protected EndOfMapException(ConfigObject confObj) {
			this.lastElement = confObj;
		}
	}

	public enum ObjectType {
		Integer, Double, String, Map,
		// TODO Array,
		Empty;
	}

	/**
	 * returns position i in array and -1 if not existant
	 * 
	 * @param i
	 * @param ar
	 * @return
	 */
	private static int isIn(int i, int[] ar) {
		if (ar == null)
			return -1;
		for (int j = 0; j < ar.length; ++j) {
			if (ar[j] == i)
				return j;
		}
		return -1;
	}

	private int pointAt = 0;
	private ObjectType objectType;
	private int integerV;

	private double doubleV;
	private String string;
	private Map<String, ConfigObject> map;
	// private ConfigObject[] array;

	/**
	 * reads a new Object from the reader. If there is nothing availabel then
	 * this defaults to an empty String object and the objectType of this object
	 * is also {@link ObjectType#String}.
	 * 
	 * @param fromReader
	 *            the input reader to read from
	 * @throws IOException
	 *             if an ioexception fromReader occurs
	 * @throws EndOfMapException
	 */
	public ConfigObject(BufferedReader fromReader) throws IOException,
			EndOfMapException {
		try {
			int cp = 0;
			do {
				cp = this.readNextCP(fromReader);
			} while (Character.isWhitespace(cp));

			if (cp == '{') { // Type.Map
				this.objectType = ObjectType.Map;
				this.map = new HashMap<String, ConfigObject>();
				maploop : while (true) {
					String key = "";
					try {
						StringBuilder keyBuilder = new StringBuilder();
						int ret = this.readLitteral(fromReader, keyBuilder,
								true, false, ':', '}');
						switch (ret) {
							case -3 : // The map is over, we hit } without input
								break maploop;
							case 3 : // Key found but then we hit end of the map
								throw new InvalidFileFormatException(
										String.format(
												"Illegal mapping found around point %d. Key without Object.",
												this.pointAt));
							case 0 : // End of file... no closing
							case -1 : // brackets
								throw new InvalidFileFormatException(
										String.format(
												"Encountered endOfFile before the a map was closed with \"}\" at point %d.",
												this.pointAt));
							case -2 : // key is empty
								throw new InvalidFileFormatException(
										String.format(
												"Illegal key for a map at point %d. The key is empty.",
												this.pointAt));
							case 2 : // we hit a ':'
								break;
							case 1 :
								throw new IllegalStateException(
										"Should not happen..."); // DEBUG
						}
						key = keyBuilder.toString().replaceAll("^\\s*", "");

						if (this.map.containsKey(key))
							throw new InvalidFileFormatException(
									String.format(
											"One of the maps contains two mappings with the same key. This happened near index %d.",
											this.pointAt));
						this.map.put(key, new ConfigObject(fromReader));
					} catch (EndOfMapException eome) {
						if (this.map.containsKey(key))
							throw new InvalidFileFormatException(
									String.format(
											"One of the maps contains two mappings with the same key. This happened near index %d.",
											this.pointAt));
						this.map.put(key, eome.lastElement);
						break;
					}
				}
			} else {
				StringBuilder stringBuild = new StringBuilder();
				int ret = this.readLitteral(fromReader, stringBuild, false,
						cp == '"', '}');
				switch (ret) {
					case -2 :
					case -1 :
						this.objectType = ObjectType.Empty;
						throw new EndOfMapException(this);
					case 1 :
						String str = stringBuild.toString();
						try {
							this.doubleV = Double.parseDouble(str);
							this.objectType = ObjectType.Double;
						} catch (NumberFormatException nfe) {
							try {
								this.integerV = Integer.parseInt(str);
								this.objectType = ObjectType.Integer;
							} catch (NumberFormatException nfe2) {
								this.objectType = ObjectType.String;
								this.string = str;
							}
						}
						break;
					case 0 :
					case 2 :
						String str1 = stringBuild.toString();
						try {
							this.doubleV = Double.parseDouble(str1);
							this.objectType = ObjectType.Double;
						} catch (NumberFormatException nfe) {
							try {
								this.integerV = Integer.parseInt(str1);
								this.objectType = ObjectType.Integer;
							} catch (NumberFormatException nfe2) {
								this.objectType = ObjectType.String;
								this.string = str1;
							}
						}
						throw new EndOfMapException(this);

					default :
						break;
				}
			}
		} catch (EndOfStreamException eofe) {
			// Ok... then this is String-Type with an empty String
			this.objectType = ObjectType.Empty;
			this.string = "";
		}
	}

	// public ConfigObject[] getArray() {
	// if (this.objectType == ObjectType.Array)
	// return this.array;
	// throw new IllegalStateException(
	// "ConfigObject is not of objectType Array.");
	// }

	public double getDouble() {
		if (this.objectType == ObjectType.Double)
			return this.doubleV;
		throw new IllegalStateException(
				"ConfigObject is not of objectType Double.");
	}

	public int getInteger() {
		if (this.objectType == ObjectType.Integer)
			return this.integerV;
		throw new IllegalStateException(
				"ConfigObject is not of objectType Integer.");
	}

	public Map<String, ConfigObject> getMap() {
		if (this.objectType == ObjectType.Map)
			return this.map;
		throw new IllegalStateException(
				"ConfigObject is not of objectType Map.");
	}

	public String getString() {
		if (this.objectType == ObjectType.String)
			return this.string;
		throw new IllegalStateException(
				"ConfigObject is not of objectType String.");
	}

	public ObjectType getType() {
		return this.objectType;
	}

	/**
	 * This method stops on the first non-escaped ':' or whitespace character.
	 * 
	 * @param reader
	 *            The {@link BufferedReader} to read from. This allows us to
	 *            read UTF-8
	 * @param codePointsToStop
	 *            codePoints to stop
	 * @return the return status <br>
	 *         0 means endOfFile but data <br>
	 *         -1 means endOfFile and no data <br>
	 *         1 means we hit a whitespace <br>
	 *         2+i means we hit codePointToStop[i] <br>
	 *         all those numbers can be negative in case we didn't read anything
	 * @throws IOException
	 * @throws EndOfStreamException
	 */
	private int readLitteral(BufferedReader reader, StringBuilder readStr,
			boolean ignoreWhitespace, boolean initLiteral,
			int... codePointsToStop) throws IOException {
		if (readStr == null)
			throw new IllegalArgumentException(
					"The StringBuilder cannot be null.");

		boolean literal = initLiteral;
		boolean escaped = false;
		boolean firstNonWhitespaceRead = false;
		try {
			for (int cp = this.readNextCP(reader);; cp = this
					.readNextCP(reader)) {

				if (!Character.isWhitespace(cp) && !firstNonWhitespaceRead) {
					int c = isIn(cp, codePointsToStop);
					if (c != -1)
						return -1 * (2 + c);
					firstNonWhitespaceRead = true;
				} else if (!ignoreWhitespace
						&& !(literal || escaped || !firstNonWhitespaceRead))
					return 1;

				if (escaped) {
					readStr.appendCodePoint(cp);
					escaped = false;
					continue;
				}

				if (!literal) {
					int c = isIn(cp, codePointsToStop);
					if (c != -1)
						return c + 2;
				}

				if (cp == '\\') {
					escaped = true;
				} else if (cp == '"') {
					literal = !literal;
				} else {
					readStr.appendCodePoint(cp);
				}
			}
		} catch (EndOfStreamException eose) {
			if (firstNonWhitespaceRead)
				return 0;
			return -1;
		}
	}

	private int readNextCP(BufferedReader reader) throws IOException,
			EndOfStreamException {
		int cp = reader.read();
		this.pointAt++;
		if (cp != -1)
			return cp;
		throw new EndOfStreamException();
	}

	// public ConfigObject reinterpret(ObjectType type) {
	// switch (this.objectType) {
	// case value :
	// break;
	//
	// default :
	// break;
	// }
	// }
}
