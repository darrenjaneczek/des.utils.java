package com.desbars.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for Rules class and inner classes.
 */
public class ResourceBundleWrapperTest {

	static final File propertiesFile;
	static final ResourceBundleWrapper bundleWrapper;

	static {
		URL propertiesUrl = ResourceBundleWrapperTest.class.getResource("Messages.properties");
		String propertiesFileLocation = propertiesUrl.getFile();

		propertiesFileLocation = propertiesFileLocation.replace("Messages.properties",
				ResourceBundleWrapperTest.class.getSimpleName() + ".properties");

		propertiesFile = new File(propertiesFileLocation);

		bundleWrapper = ResourceBundleWrapper.forClass(ResourceBundleWrapperTest.class);
	}

	@BeforeEach
	public void testSetup() throws IOException {
		deletePropertiesFile();
	}

	@AfterAll
	public static void deletePropertiesFile() throws IOException {
		if (propertiesFile.exists()) {
			Files.delete(propertiesFile.toPath());
		}
		bundleWrapper.reset();
	}

	
	@Test
	public void test_forClass_singleton() throws FileNotFoundException, IOException {
		
		ResourceBundleWrapper sameWrapper = ResourceBundleWrapper.forClass(this.getClass());
		assertSame(bundleWrapper, sameWrapper);
	}
	
	@Test
	public void test_forName_singleton() throws FileNotFoundException, IOException {
		
		ResourceBundleWrapper wrapper1 = ResourceBundleWrapper.forName("FAKE");
		ResourceBundleWrapper wrapper2 = ResourceBundleWrapper.forName("FAKE");
		assertSame(wrapper1, wrapper2);
	}
	
	@Test
	public void test_forName_same_as_forClass() throws FileNotFoundException, IOException {
		
		ResourceBundleWrapper nameWrapper = ResourceBundleWrapper.forName(this.getClass().getCanonicalName());
		assertSame(bundleWrapper, nameWrapper);
	}
	
	@Test
	public void test_getInteger_mockValue() throws FileNotFoundException, IOException {
		FileWriter writer = new FileWriter(propertiesFile);

		Properties mockProps = new Properties();

		mockProps.put("mockValue", "256");
		mockProps.store(writer, "test_getInteger_mockValue");
		writer.close();

		IValue<Integer> mockValue = bundleWrapper.getInteger("mockValue");
		assertEquals(256, mockValue.get());
	}

	@Test
	public void test_getInteger_unparseable() throws FileNotFoundException, IOException {
		FileWriter writer = new FileWriter(propertiesFile);

		Properties mockProps = new Properties();

		mockProps.put("unparseable", "two-fifty-six");
		mockProps.store(writer, "test_getInteger_unparseable");
		writer.close();

		IValue<Integer> mockValue = bundleWrapper.getInteger("unparseable");

		try {
			mockValue.get();
		} catch (ResourceBundleWrapper.BundlePropertyException e) {
			assertTrue(e.getMessage().contains(ResourceBundleWrapper.BECAUSE_NUMBER_FORMAT));
		}
	}

	@Test
	public void test_getInteger_singleton() throws FileNotFoundException, IOException {
		FileWriter writer = new FileWriter(propertiesFile);

		Properties mockProps = new Properties();

		mockProps.put("mockValue", "256");
		mockProps.store(writer, "test_getInteger_singleton");
		writer.close();

		IValue<Integer> mockValue1 = bundleWrapper.getInteger("mockValue");
		IValue<Integer> mockValue2 = bundleWrapper.getInteger("mockValue");

		assertSame(mockValue1, mockValue2);

	}
	
	@Test
	public void test_getInteger_IValue_get_singleton() throws FileNotFoundException, IOException {
		FileWriter writer = new FileWriter(propertiesFile);

		Properties mockProps = new Properties();

		mockProps.put("mockValue", "256");
		mockProps.store(writer, "test_getInteger_singleton");
		writer.close();

		IValue<Integer> mockValue = bundleWrapper.getInteger("mockValue");

		assertSame(mockValue.get(), mockValue.get());

	}

	@Test
	public void test_getStringValue_mockValue() throws FileNotFoundException, IOException {
		FileWriter writer = new FileWriter(propertiesFile);

		Properties mockProps = new Properties();

		mockProps.put("mockValue", "hello");
		mockProps.store(writer, "test_getStringValue_mockValue");
		writer.close();

		String mockValue = bundleWrapper.getStringValue("mockValue");
		assertEquals("hello", mockValue);
	}

	@Test
	public void test_getStringValue_keyNotFound() throws FileNotFoundException, IOException {
		FileWriter writer = new FileWriter(propertiesFile);

		Properties mockProps = new Properties();

		mockProps.store(writer, "test_getStringValue_mockValue");
		writer.close();

		try {
			bundleWrapper.getStringValue("mockValue");
		} catch (ResourceBundleWrapper.BundlePropertyException e) {
			assertTrue(e.getMessage().contains(ResourceBundleWrapper.BECAUSE_KEY_NOT_FOUND));
		}
	}

	@Test
	public void test_getStringValue_bundleNotLoaded() throws FileNotFoundException, IOException {
		ResourceBundleWrapper unloadedWrapper = ResourceBundleWrapper.forName("FAKE");
		try {
			unloadedWrapper.getStringValue("fake");
		} catch (ResourceBundleWrapper.BundlePropertyException e) {
			assertTrue(e.getMessage().contains(ResourceBundleWrapper.BECAUSE_BUNDLE_NOT_LOADED));
		}
	}

	@Test
	public void test_getStringValueOptional_success() throws FileNotFoundException, IOException {
		FileWriter writer = new FileWriter(propertiesFile);

		Properties mockProps = new Properties();

		mockProps.put("mockValue", "hello");
		mockProps.store(writer, "test_getStringValueOptional_success");
		writer.close();

		String mockValue = bundleWrapper.getStringValueOptional("mockValue");
		assertEquals("hello", mockValue);
	}

	@Test
	public void test_getStringValueOptional_failure() throws FileNotFoundException, IOException {
		String mockValue = bundleWrapper.getStringValueOptional("mockValue");
		assertEquals("[mockValue]", mockValue);
	}

	
	public static enum MockEnum {
		A,
		B,
		C
	}
	
	@Test
	public void test_getEnumValueKey() {
		String expected = "MockEnum.A.myKey";
		String actual = ResourceBundleWrapper.getEnumValueKey(MockEnum.A, "myKey");
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_getInteger_enum_key() throws Exception {
		
		FileWriter writer = new FileWriter(propertiesFile);

		Properties mockProps = new Properties();

		Integer expected = 512;
		
		mockProps.put("MockEnum.A.myKey", expected.toString());
		mockProps.store(writer, "test_getInteger_enum_key");
		writer.close();

		IValue<Integer> getter = bundleWrapper.getInteger(MockEnum.A, "myKey");
		
		Integer actual = getter.get();
		
		assertEquals(expected, actual);
		
		
	}
	
//	@Test
//	public void test_getInteger_resetValue() throws FileNotFoundException, IOException {
//
//		FileReader reader = new FileReader(propertiesFile);
//		FileWriter writer = new FileWriter(propertiesFile);
//
//		Properties mockProps = new Properties();
//		mockProps.load(reader);
//		reader.close();
//
//		mockProps.put("resetValue", "1024");
//		mockProps.store(writer, "test_getInteger_resetValue wrote initial value");
//		writer.close();
//
//		IValue<Integer> resetValue = Rules.getInteger("resetValue");
//		assertEquals(1024, resetValue.get());
//
//		// Now let's change the file.
//
//		mockProps.put("resetValue", "2048");
//		writer = new FileWriter(propertiesFile);
//		mockProps.store(writer, "test_getInteger_resetValue wrote reset value");
//		writer.close();
//
//		// Value is still 1024 because we haven't reset yet
//		assertEquals(1024, resetValue.get());
//
//		// So let's reset and read the new value of 2048.
//		Rules.reset();
//
//		assertEquals(2048, resetValue.get());
//
//	}
//
//	@Test
//	public void test_AdType_AUCTION_fee_matchesProperties() throws IOException {
//
//		FileReader reader = new FileReader(propertiesFile);
//		Properties props = new Properties();
//
//		props.load(reader);
//		reader.close();
//
//		String value = props.get("AdType.AUCTION.fee").toString();
//		Integer expected = Integer.parseInt(value);
//		Integer actual = AdType.AUCTION.getFee();
//
//		assertEquals(expected, actual);
//	}
//
//	@Test
//	public void test_AdType_BUY_IT_NOW_fee_matchesProperties() throws IOException {
//
//		FileReader reader = new FileReader(propertiesFile);
//		Properties props = new Properties();
//
//		props.load(reader);
//		reader.close();
//
//		String value = props.get("AdType.BUY_IT_NOW.fee").toString();
//		Integer expected = Integer.parseInt(value);
//		Integer actual = AdType.BUY_IT_NOW.getFee();
//
//		assertEquals(expected, actual);
//	}
//
//	@Test
//	public void test_UserType_NORMAL_discount_matchesProperties() throws IOException {
//
//		FileReader reader = new FileReader(propertiesFile);
//		Properties props = new Properties();
//
//		props.load(reader);
//		reader.close();
//
//		String value = props.get("UserType.NORMAL.discount").toString();
//		Integer expected = Integer.parseInt(value);
//		Integer actual = UserType.NORMAL.getDiscount();
//
//		assertEquals(expected, actual);
//	}
//
//	@Test
//	public void test_UserType_Company_discount_matchesProperties() throws IOException {
//
//		FileReader reader = new FileReader(propertiesFile);
//		Properties props = new Properties();
//
//		props.load(reader);
//		reader.close();
//
//		String value = props.get("UserType.COMPANY.discount").toString();
//		Integer expected = Integer.parseInt(value);
//		Integer actual = UserType.COMPANY.getDiscount();
//
//		assertEquals(expected, actual);
//	}
////    

}
