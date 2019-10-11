package com.desbars.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static com.desbars.util.TestUtil.assertStartsWith;

import org.junit.jupiter.api.Test;


public class EnumWrapperTest {

	static enum MockEnum0123 {
		ZERO, ONE, TWO, THREE
	}

	@Test
	public void test_EnumWrapper_values() {

		EnumWrapper<MockEnum0123> wrapper = EnumWrapper.forEnum(MockEnum0123.class);

		MockEnum0123[] expected = MockEnum0123.values();
		MockEnum0123[] actual = wrapper.values();

		assertArrayEquals(expected, actual);
	}

	@Test
	public void test_EnumWrapper_invalidEnum() {

		Class<?> nonEnumClass = EnumWrapperTest.class;
		@SuppressWarnings("unchecked")
		Class<MockEnum0123> fradulentEnumClass = (Class<MockEnum0123>) nonEnumClass;

		try {
			EnumWrapper.forEnum(fradulentEnumClass);
		} catch (IllegalArgumentException e) {
			assertStartsWith(Messages.INVALID_ENUM, e.getMessage());
		}

	}

	@Test
	public void test_EnumWrapper_valueOf() {

		EnumWrapper<MockEnum0123> wrapper = EnumWrapper.forEnum(MockEnum0123.class);

		assertSame(MockEnum0123.ONE, wrapper.valueOf("ONE"));
		assertSame(MockEnum0123.ONE, wrapper.valueOf("one"));
	}

	@Test
	public void test_EnumWrapper_valueOf_illegalArg() {

		EnumWrapper<MockEnum0123> wrapper = EnumWrapper.forEnum(MockEnum0123.class);
		try {
			wrapper.valueOf("1");
		} catch (IllegalArgumentException e) {
			assertStartsWith(Messages.INVALID_ENUM, e.getMessage());
		}
	}

	@Test
	public void test_EnumWrapper_setSynonym() {

		EnumWrapper<MockEnum0123> wrapper = EnumWrapper.forEnum(MockEnum0123.class);

		wrapper.setSynonym("uno", MockEnum0123.ONE);

		assertSame(MockEnum0123.ONE, wrapper.valueOf("uno"));
	}

	@Test
	public void test_EnumWrapper_setSynonym_alreadyExists() {

		EnumWrapper<MockEnum0123> wrapper = EnumWrapper.forEnum(MockEnum0123.class);

		wrapper.setSynonym("test_EnumWrapper_setSynonym_alreadyExists", MockEnum0123.ONE);
		try {
			wrapper.setSynonym("test_EnumWrapper_setSynonym_alreadyExists", MockEnum0123.TWO);
		} catch (IllegalArgumentException e) {
			assertStartsWith(EnumWrapper.SYNONYM_ALREADY_EXISTS, e.getMessage());
		}
	}

	static enum MockEnumSpaced {
		TEST_ZERO, TEST_ONE, TEST_TWO
	}

	@Test
	public void test_EnumWrapper_valueOf_spacedNames() {

		EnumWrapper<MockEnumSpaced> wrapper = EnumWrapper.forEnum(MockEnumSpaced.class);

		assertSame(MockEnumSpaced.TEST_ZERO, wrapper.valueOf("TEST_ZERO"));
		assertSame(MockEnumSpaced.TEST_ZERO, wrapper.valueOf("TESTZERO"));
		assertSame(MockEnumSpaced.TEST_ZERO, wrapper.valueOf("TestZero"));
		assertSame(MockEnumSpaced.TEST_ZERO, wrapper.valueOf("testzero"));
	}
}
