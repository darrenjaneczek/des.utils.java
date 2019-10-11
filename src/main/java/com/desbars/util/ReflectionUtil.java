package com.desbars.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A utility class that simplifies reflection.
 * 
 * Note: This class is not meant to be constructable.
 * 
 * @author Darren
 *
 */
public class ReflectionUtil {

	private ReflectionUtil() {

	}

	/**
	 * Try to call a method.
	 * 
	 * @param method the method to be called
	 * @param methodOwner the instance that the method belongs to (null for statics)
	 * @param args any number of arguments needed for the `method`.
	 * @return the result of the method if successful, `null` otherwise.
	 */
	@SuppressWarnings("unchecked")
	public static <E> E tryMethod(Method method, Object methodOwner, Object... args) {

		E result = null;

		try {
			// Invoke the static method [Enum].values();
			Object returned = method.invoke(methodOwner, args);
			result = (E) returned;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		}

		return result;
	}

	/**
	 * Provide a string summary of an object using the given `fields`.
	 * 
	 * @param object an object to provide a summary for.
	 * @param fields a list of field names to be shown in the summary.
	 * @return
	 */
	public static String getFieldSummary(Object object, String... fields) {
		final String BULLET = "  - ";
		final String ENDL = System.lineSeparator();

		StringBuilder sb = new StringBuilder();
		sb.append(object.getClass().getSimpleName()).append('\n');
		for (String fieldName : fields) {
			Object value = null;
			try {
				Field field = object.getClass().getDeclaredField(fieldName);
				value = field.get(object);
			} catch (Exception e) {
				value = e.getClass().getSimpleName();
			}
			sb.append(BULLET).append(fieldName).append(": ").append(value).append(ENDL);
		}
		return sb.toString();
	}
}
