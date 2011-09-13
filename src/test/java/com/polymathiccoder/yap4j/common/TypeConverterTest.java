package com.polymathiccoder.yap4j.common;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * A test class for TypeConverter.
 *
 * @author  Abdelmonaim Remani
 * @version 0.1.0
 * @since 0.1.0
 */
@RunWith(value = Parameterized.class)
@Log
@AllArgsConstructor
public class TypeConverterTest {
    //Rules, Parameters, and DataPoints
    /** The value to be converted. */
    private final transient String value;

    /** The type to convert to. */
    private final transient Class<?> toClass;

    /** The format of the string to be converted. */
    private final transient String format;

    /** The expected value. */
    private final transient Object expected;

    /**
     * Returns valid parameters for the test.
     *
     * @return the test parameters
     */
    @Parameters
    public static Collection<Object[]> validParameters() {
        Collection <Object[]> data = null; // NOPMD
        try {
            data = Arrays.asList(new Object[][] {
                //URL
                {"http://www.IAmAUrl1.com", URL.class, null, new URL("http://www.IAmAUrl1.com") },
                {"ar", Locale.class, null, new Locale("ar") },
                //Locale
                {"ar", Locale.class, null, new Locale("ar") },
                //Temporal
                {"01/01/2011", Date.class, "MM/dd/yyyy", new DateTime(2011, 1, 1, 0, 0, 0, 0).toDate() },
                {"01/01/2011 11:11:11", Date.class, "MM/dd/yyyy HH:mm:ss", new DateTime(2011, 1, 1, 11, 11, 11, 0).toDate() },
                {"Africa/Casablanca", TimeZone.class, null, TimeZone.getTimeZone("Africa/Casablanca") },
                {"25:11:11", Period.class, "HH:mm:ss", new Period(0, 0, 0, 0, 25, 11, 11, 0) },
                //Enum
                {"VALUE_1", Enumer.class, null, Enumer.VALUE_1 },
                {"0", Enumer.class, null, Enumer.VALUE_1 },
                //String
                {"IAmString1", String.class, null, "IAmString1" },
                //Primitives and their wrappers
                {"0", byte.class, null, (byte) 0 },
                {"0", Byte.class, null, Byte.valueOf("0") },
                {"0", short.class, null, (short) 0 }, // NOPMD
                {"0", Short.class, null, Short.valueOf("0") },
                {"1", int.class, null, 1 },
                {"1", Integer.class, null, Integer.valueOf(1) },
                {"0", long.class, null, 0L },
                {"0", Long.class, null, Long.valueOf(0L) },
                {"0", float.class, null, 0.0f },
                {"0", Float.class, null, new Float(0f) },
                {"0", double.class, null, 0d },
                {"0", Double.class, null, new Double(0d) },
                {"x", char.class, null, 'x' },
                {"x", Character.class, null, new Character('x') },
                {"true", boolean.class, null, true },
                {"true", Boolean.class, null, Boolean.TRUE },
                {"1", boolean.class, null, true },
                {"1", Boolean.class, null, Boolean.TRUE },
                {"false", boolean.class, null, false },
                {"false", Boolean.class, null, Boolean.FALSE },
                {"0", boolean.class, null, false },
                {"0", Boolean.class, null, Boolean.FALSE },
                {"9", boolean.class, null, false },
                {"9", Boolean.class, null, Boolean.FALSE }
            });
        } catch (NumberFormatException numberFormatException) { // NOPMD
            log.severe("This should never happen");
        } catch (MalformedURLException malformedURLException) { // NOPMD
            log.severe("This should never happen");
        }
        return data;
    }

    //Tests, and Theories
    /**
     * Test valid data.
     */
    @Test
    public void testValid() {
        assertThat(TypeConverter.INSTANCE.fromString(value, toClass, format), equalTo(expected));
    }

    //Data
    /**
     * A Sample Enumerator.
     */
    public static enum Enumer {
        /** Value 1. */
        VALUE_1,
        /** Value 2. */
        VALUE_2;
    }
}
