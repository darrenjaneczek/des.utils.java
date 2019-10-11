package com.desbars.util;

/**
 * A generic interface for obtained encapsulated values.
 * 
 * This allows an encapsulated value to be obtained lazily/on demand.
 * 
 * @author Darren
 *
 * @param <E>
 */
public interface IValue<E> {
	E get();
}
