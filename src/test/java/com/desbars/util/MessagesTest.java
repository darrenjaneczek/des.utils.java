package com.desbars.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Unit test for Rules class and inner classes.
 */
public class MessagesTest {



	@Test
	public void test_get_unknownKey() throws FileNotFoundException, IOException {

		String brokenValue = Messages.get("test_get_unknownKey()");
		assertEquals("[test_get_unknownKey()]", brokenValue);

	}

	@Test
	public void test_get_knownKey() throws FileNotFoundException, IOException {

		String expected = Messages.TYPE_CANNOT_BE_NULL;
		String actual = Messages.get("TYPE_CANNOT_BE_NULL");
		
		assertEquals(expected, actual);

	}
}
