package com.desbars.util;

import static com.desbars.util.TestUtil.assertStartsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ReflectionUtilTest {

	
	static class Mock {

		List<String[]> callParams = new LinkedList<>();

		String returnValue;

		public String methodMock(String param0, String param1) {
			callParams.add(new String[] { param0, param1 });
			return returnValue;
		}
	}
	
	@Test
	public void test_tryMethod() throws Exception {

		Mock mock = new Mock();
		// Determine what value the method should return;
		String expected = "test_tryMethod";
		mock.returnValue = "test_tryMethod";

		Method method = mock.getClass().getMethod("methodMock", String.class, String.class);
		
		String actual = ReflectionUtil.tryMethod(method, mock, "Hello", "World");
		
		assertEquals(expected, actual);
		String[] args = mock.callParams.get(0);
		assertEquals(args[0], "Hello");
		assertEquals(args[1], "World");
	}
	
	@Test
	public void test_tryMethod_illegalArgs() throws Exception {

		Mock mock = new Mock();
		// Determine what value the method should return;
		mock.returnValue = "test_tryMethod_illegalArgs";

		Method method = mock.getClass().getMethod("methodMock", String.class, String.class);

		// Will return a null result because the call failed
		String expected = null;
		String actual = ReflectionUtil.tryMethod(method, mock, "Hello");
		
		assertEquals(expected, actual, "Expect null when the call does not happen");
		assertEquals(0, mock.callParams.size(), "Expect zero calls when the call does not happen");
	}

	@Test
	public void test_getFieldSummary() throws Exception {

		Mock mock = new Mock();
		mock.returnValue = "test_getFieldSummary";

		String summary = ReflectionUtil.getFieldSummary(mock, "returnValue", "nonExistent", "bad format");
		
		int returnValue = summary.indexOf("returnValue");
		int nonExistent = summary.indexOf("nonExistent");
		int bad_format = summary.indexOf("bad format");
		
		assertStartsWith("returnValue: " + mock.returnValue, summary.substring(returnValue));
		assertStartsWith("nonExistent: NoSuchFieldException", summary.substring(nonExistent));
		assertStartsWith("bad format: NoSuchFieldException", summary.substring(bad_format));
	}

	
}
