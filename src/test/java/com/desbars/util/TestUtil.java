package com.desbars.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtil {
	
	private TestUtil() {
		
	}
	
	public static void assertStartsWith(String expectedPrefix, String actual) {
		assertEquals(expectedPrefix, actual.substring(0, expectedPrefix.length()));
	}
}
