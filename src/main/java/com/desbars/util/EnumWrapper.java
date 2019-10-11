package com.desbars.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class to wrap around `Enum` types and provide additional functionality.
 * 
 * @author Darren
 *
 * @param <E> an `Enum` type
 */
public class EnumWrapper<E extends Enum<?>> {

	private final Map<String, E> stringToEnum;

	private final Method values;
	
	private static Map<Class<?>, EnumWrapper<?>> wrapperMap = new HashMap<>();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <E extends Enum<E>> EnumWrapper<E> forEnum(Class<E> cls) {
		EnumWrapper<?> wrapper = wrapperMap.get(cls);
		if (wrapper == null) {
			wrapper = new EnumWrapper(cls);
			wrapperMap.put(cls, wrapper);
		}
		
		return (EnumWrapper<E>)wrapper;
	}
	
	private EnumWrapper(Class<E> enumType) {
		try {
			this.values = enumType.getMethod("values"); // Get `values` method from enum
			this.stringToEnum = new HashMap<>();

			for (E value : values()) {
				String name = value.name();
				stringToEnum.putIfAbsent(name.toUpperCase(), value);
				String unspacedName = name.replaceAll("_", "");

				if (unspacedName != name) {
					stringToEnum.putIfAbsent(unspacedName.toUpperCase(), value);
				}
			}
			
			for (E value : values()) {
				// Put the original enum name as a key, and overwrite any existing
				// upperCase synonyms
				stringToEnum.put(value.name(), value);
			}
			

		} catch (NoSuchMethodException | ClassCastException e) {
			// This should not happen unless a non-enum class is passed as `enumType`
			throw new IllegalArgumentException(Messages.INVALID_ENUM + enumType, e);
		}

	}

	/**
	 * Associate a `name` with an enum `value` when obtained by `valueOf`.
	 * 
	 * @param name
	 * @param value
	 * @throws IllegalArgumentException if the `name` already is being used
	 */
	public void setSynonym(String name, E value) {
		E oldValue = stringToEnum.putIfAbsent(name, value);
		if (oldValue != null) {
			throw new IllegalArgumentException(SYNONYM_ALREADY_EXISTS + name + " -> " + oldValue);
		}
	}

	/**
	 * Delegate to the wrapped enum's `values` method
	 * 
	 * @return the array of all enums of the wrapped type.
	 */
	public E[] values() {
		return ReflectionUtil.tryMethod(values, null);
	}

	/**
	 * Obtain an enum by its name or a synonym declared by `setSynonym
	 * 
	 * @param name
	 * @return enum associated with the given `name`.
	 */
	public E valueOf(String name) {
		// Try basic value
		E value = stringToEnum.get(name);
		
		if (value != null) {
			return value;
		}
	
		// Try a potential uppercase synonym
		name = name.toUpperCase();
		value = stringToEnum.get(name);
		
		if (value != null) {
			return value;
		}
		else {
			throw new IllegalArgumentException(Messages.INVALID_ENUM + name);
		}
		
	}

	
	public static final String SYNONYM_ALREADY_EXISTS = Messages.get("EnumWrapper.SYNONYM_ALREADY_EXISTS");
}
