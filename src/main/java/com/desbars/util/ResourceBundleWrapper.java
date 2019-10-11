package com.desbars.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Utility class to wrap `ResourceBundle` to access resource property files.
 * 
 * The loading the properties file, and reading the properties does not occur
 * until one of the `getString`, `getInteger`, etc. method are called.
 * Subsequent calls to these methods will attempt to load the properties file
 * again.
 * 
 * @author Darren
 */
public class ResourceBundleWrapper {

	private final String bundleName;

	/**
	 * An interface responsible for retrieving the `ResourceBundle`.
	 *
	 * @author Darren
	 */
	private interface IBundleGetter {
		ResourceBundle getBundle();

		void reset();
	}

	/**
	 * An anonymous instantiation of `IBundleGetter`.
	 * 
	 * This mechanism is to force lazy evaluation and ensure that the
	 * `ResourceBundle` is obtained ONLY through the `bundleGetter` and not through
	 * direct access.
	 */
	private IBundleGetter bundleGetter = new IBundleGetter() {

		private ResourceBundle bundle;

		public ResourceBundle getBundle() {
			if (this.bundle == null) {
				bundle = ResourceBundle.getBundle(bundleName);
			}
			return bundle;
		}

		public void reset() {
			this.bundle = null;
		}

	};

	private static Map<String, ResourceBundleWrapper> instancesByName;

	/**
	 * Obtain a unique instance of `ResourceBundleWrapper` for a given `Class`.
	 * 
	 * The properties file used for the `ResourceBundle` is based on `theClass`
	 * canonical name. For a class named `Thing` in package `my.package` (or a class
	 * with canonical name `my.package.Thing`, a `Thing.properties` file in the
	 * package directory `my.package` will be used.
	 * 
	 * Note that the example used in `forName` will yield the exact same instances
	 * as the example used in `forClass`.
	 * 
	 * @param theClass
	 * @return
	 */
	public static ResourceBundleWrapper forClass(Class<?> theClass) {
		return forName(theClass.getCanonicalName());
	}

	/**
	 * Obtain a unique instance of a `ResourceBundleWrapper` for `bundleName`.
	 * 
	 * The properties file used for the `ResourceBundle` is based on the bundleName.
	 * For example, `"my.package.Thing"` will look for a `Thing.properties` file in
	 * the package directory `my.package`.
	 * 
	 * Note that the example used in `forName` will yield the exact same instances
	 * as the example used in `forClass`.
	 * 
	 * @param bundleName
	 * @return
	 */
	public static ResourceBundleWrapper forName(String bundleName) {

		if (instancesByName == null) {
			instancesByName = new HashMap<>();
		}

		ResourceBundleWrapper wrapper = instancesByName.get(bundleName);
		if (wrapper == null) {
			wrapper = new ResourceBundleWrapper(bundleName);
			instancesByName.put(bundleName, wrapper);
		}

		return wrapper;
	}

	private ResourceBundleWrapper(String bundleName) {
		this.bundleName = bundleName;
	}

	/**
	 * Get an string value for a specified key.
	 * 
	 * @param key
	 * @return string value for key
	 * @throws BundlePropertyException if the properties file does not exist, or if
	 *                                 the key does not have a value in the
	 *                                 properties file.
	 */
	public String getStringValue(String key) throws BundlePropertyException {
		ResourceBundle bundle = null;
		try {
			bundle = bundleGetter.getBundle();
		} catch (MissingResourceException e) {
			throw this.new BundlePropertyException(key, e, BECAUSE_BUNDLE_NOT_LOADED);
		}

		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			throw this.new BundlePropertyException(key, e, BECAUSE_KEY_NOT_FOUND);
		}

	}
	
	String getStringValueOptional(String key) { 
		String value;
		try {
			value = getStringValue(key);
			return value;
		} catch (BundlePropertyException e) {
			return getPlaceholderString(key);
		}
	}

	static String getPlaceholderString(String key) {
		return String.format("[%s]", key); //$NON-NLS-1$
	}

	
	private Map<String, ValueGetter<Integer>> integerGetters = new HashMap<>();

	/**
	 * Get an integer value for a specified key on the properties file.
	 * 
	 * The `key` is expected to be a key in the properties files for this resource
	 * bundle. If the `key` is not found, or the value for `key` cannot be parsed
	 * into an integer, a `BundlePropertyException` exception will occur when the
	 * returned `IValue` `get` method is called (but not when this method is
	 * called).
	 * 
	 * @param key
	 * @return `IValue` integer getter for key
	 */
	public IValue<Integer> getInteger(String key) {

		ValueGetter<Integer> getter = integerGetters.get(key);

		if (getter == null) {

			getter = new ValueGetter<Integer>(key) {
				@Override
				protected Integer parseFromString(String stringValue) {
					try {
						return Integer.parseInt(stringValue);
					} catch (NumberFormatException e) {
						
						
						throw ResourceBundleWrapper.this.new BundlePropertyException( //
								key, //
								e,
								BECAUSE_NUMBER_FORMAT, //
								e.getMessage() //
						);
					}
				}
			};

			integerGetters.put(key, getter);
		}
		return getter;

	}

	// These are not being used... Disabling for now.
//	private Map<String, ValueGetter<String>> stringGetters = new HashMap<>();
//
//	public IValue<String> getString(String key) {
//		ValueGetter<String> getter = stringGetters.get(key);
//
//		if (getter == null) {
//
//			getter = new ValueGetter<String>(key) {
//				@Override
//				protected String parseFromString(String stringValue) {
//					return stringValue;
//				}
//			};
//
//			stringGetters.put(key, getter);
//		}
//		return getter;
//	}

	/**
	 * Using an enum value and a property, create a enum value key
	 * 
	 * The result has form: `[enumValue classname].[enumValue instance name].[key]`
	 *
	 * @param enumValue
	 * @param key
	 * @return the resulting enum value key
	 */
	public static String getEnumValueKey(Enum<?> enumValue, String key) {
		String enumClassName = enumValue.getClass().getSimpleName();
		String enumName = enumValue.name();
		String resourceKey = String.format("%s.%s.%s", enumClassName, enumName, key); //$NON-NLS-1$
		return resourceKey;
	}

	/**
	 * Delegates for `getInteger`, but converts `enumValue` and `key` into a single
	 * key.
	 * 
	 * The resulting enum value key will be obtained from `getEnumValueKey`, which
	 * will be delegated to the regular `getInteger` as its `key`
	 *
	 * The resulting enum value key has form: `[enumValue classname].[enumValue
	 * instance name].[key]`
	 *
	 * @param enumValue
	 * @param key
	 * @return `IValue` integer getter for enum value key
	 */
	public IValue<Integer> getInteger(Enum<?> enumValue, String key) throws BundlePropertyException {
		return this.getInteger(getEnumValueKey(enumValue, key));
	}

	private abstract class ValueGetter<E> implements IValue<E> {
		private E value;
		private final String key;

		private ValueGetter(String key) {
			this.key = key;
		}

		protected abstract E parseFromString(String stringValue);

		public E get() {

			if (value == null) {
				String stringValue = getStringValue(key);
				value = parseFromString(stringValue);
			}

			return value;
		}
	}

	/**
	 * Resets any loaded values in this resource bundle.
	 */
	public void reset() {
		ResourceBundle.clearCache();
		bundleGetter.reset();

		for (Collection<? extends ValueGetter<?>> valueGetters : Arrays.asList( //
				// not being used // stringGetters.values(), //
				integerGetters.values())) {
			for (ValueGetter<?> valueGetter : valueGetters) {
				valueGetter.value = null;
			}
		}
	}

	/****************
	 * Exception code
	 *
	 */

	/**
	 * Thrown whenever a bundle property is requested, but a value cannot be
	 * resolved.
	 * 
	 * Cases: - When a key is not found in the properties file - If a key has a
	 * value that cannot be converted to the requested type
	 * 
	 * @author Darren
	 *
	 */
	public class BundlePropertyException extends RuntimeException {
		private static final long serialVersionUID = -3746398027621227201L;

		BundlePropertyException(String key, Throwable cause, String... reasons) {
			super(generateKeyFailMessage(bundleName, key) + String.join(", ", reasons), cause);
		}
	}

	private static String generateKeyFailMessage(String bundleName, String key) {
		return FAILED_TO_RETRIEVE_KEY + bundleName + ", " + key + ": ";
	}

	/********************
	 * Messages
	 */

	static {

		// These don't go through the typical Messages class `get` method
		// due to

		ResourceBundleWrapper messages = forClass(Messages.class);

		FAILED_TO_RETRIEVE_KEY = messages.getStringValue("ResourceBundleWrapper.FAILED_TO_RETRIEVE_KEY"); //$NON-NLS-1$
		BECAUSE_BUNDLE_NOT_LOADED = messages.getStringValue("ResourceBundleWrapper.BECAUSE_BUNDLE_NOT_LOADED"); //$NON-NLS-1$
		BECAUSE_KEY_NOT_FOUND = messages.getStringValue("ResourceBundleWrapper.BECAUSE_KEY_NOT_FOUND"); //$NON-NLS-1$
		BECAUSE_NUMBER_FORMAT = messages.getStringValue("ResourceBundleWrapper.BECAUSE_NUMBER_FORMAT"); //$NON-NLS-1$

	}

	static final String FAILED_TO_RETRIEVE_KEY;

	static final String BECAUSE_BUNDLE_NOT_LOADED;

	static final String BECAUSE_KEY_NOT_FOUND;

	static final String BECAUSE_NUMBER_FORMAT;
}
