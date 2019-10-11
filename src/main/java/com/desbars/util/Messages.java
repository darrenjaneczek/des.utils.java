package com.desbars.util;

/**
 * A utility class to provide configurable messages to the program.
 * 
 * Uses a `ResourceBundleWrapper` to reference the `Messages.properties` file.
 * 
 * Note: This class is not meant to be constructable.
 * 
 * Note: If this class is renamed, the properties file must also be renamed to
 * match.
 * 
 * @author Darren
 *
 */
class Messages {

	private static final ResourceBundleWrapper RESOURCE_BUNDLE;
	static {
		RESOURCE_BUNDLE = ResourceBundleWrapper.forClass(Messages.class);
	}

	public static String TYPE_CANNOT_BE_NULL = Messages.get("TYPE_CANNOT_BE_NULL");
	public static String DATE_CANNOT_BE_NULL = Messages.get("DATE_CANNOT_BE_NULL");
	public static String INVALID_ENUM = Messages.get("INVALID_ENUM");
	

	private Messages() {
		// Not meant to be constructable...
	}

	/**
	 * Obtain a String value by `key` from the `Messages.properties` file.
	 * 
	 * This method immediately resolves the resulting message string.
	 * 
	 * If it is not defined in the file, a string that contains the `key` will be
	 * returned as an alternative. E.g., for `"MY_KEY"`, the result would be
	 * `"[MY_KEY]"`.
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		String value = RESOURCE_BUNDLE.getStringValueOptional(key);
		return value;
	}

}
