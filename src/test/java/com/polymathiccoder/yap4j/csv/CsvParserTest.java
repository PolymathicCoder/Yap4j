package com.polymathiccoder.yap4j.csv; // NOPMD

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.polymathiccoder.yap4j.csv.annotation.CsvEntry;
import com.polymathiccoder.yap4j.csv.annotation.CsvFile;

/**
 * A test class for CsvParser.
 *
 * @author  Abdelmonaim Remani
 * @version 0.1.0
 * @since 0.1.0
 */
@RunWith(value = Theories.class)
@Log
public class CsvParserTest {
    //Rules, Parameters, and DataPoints
    /** A rule to test whether an exception has been thrown or not. */
    @Rule
    public final transient ExpectedException exception = ExpectedException.none(); // CHECKSTYLE IGNORE

    /**
     * Returns parameters for the theory.
     *
     * @return the test parameters
     */
    @DataPoints
    public static Object[] parameters() {
        return Arrays.asList(new Object[] {
            ImmutablePair.of(Valid_WithHeader.class, Valid_WithHeader.expected),
            ImmutablePair.of(Valid_WithoutHeader.class, Valid_WithoutHeader.expected),
            ImmutablePair.of(Valid_WithDelimiter.class, Valid_WithDelimiter.expected),
            ImmutablePair.of(Valid_OptionalFields.class, Valid_OptionalFields.expected),
            ImmutablePair.of(Valid_IgnoredFields.class, Valid_IgnoredFields.expected),
            ImmutablePair.of(Valid_DefaultFields.class, Valid_DefaultFields.expected),

            ImmutablePair.of(Invalid_WithoutHeader.class, Invalid_WithoutHeader.expected),
            ImmutablePair.of(Invalid_WithHeader.class, Invalid_WithoutHeader.expected),
            ImmutablePair.of(Invalid_NoDefaultNonOptionalPrimitive.class, Invalid_NoDefaultNonOptionalPrimitive.expected),
            ImmutablePair.of(Invalid_NoDefaultNonOptionalNonPrimitive.class, Invalid_NoDefaultNonOptionalNonPrimitive.expected)
        }).toArray();
    }

    //Tests, and Theories
    /**
     * Theory to test with valid data.
     */
    @Theory // NOPMD
    public void testValid(final ImmutablePair<Class<?>, List<?>> datum) {
        assumeThat(datum.right, not(instanceOf(Exception.class)));
        final List<?> actual = Collections.unmodifiableList(CsvParserFactory.createParser(datum.left).deserialize());
        assertEquals(datum.right.size(), actual.size());
        assertTrue(actual.containsAll(datum.right));
    }

    /**
     * Theory to test with valid data.
     */
    @Theory // NOPMD
    public void testInvalid(final ImmutablePair<Class<?>, List<?>> datum) {
        assumeThat(datum.right, instanceOf(Exception.class));
        exception.expect(ParsingException.class);
        CsvParserFactory.createParser(datum.left).deserialize();
    }

    //Data
    /**
     * A Sample Enumerator.
     */
    private static enum Enumer {
        /** Value 1. */
        VALUE_1,
        /** Value 2. */
        VALUE_2;
    }

    // CHECKSTYLE:OFF
    /*For this to work on Eclipse, go to
     * Java -> Compiler -> Errors/Warnings -> Annotations ->
     * Unhandled Token in "@SuppressWarnings", and set it to ignore.
     */
    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.AvoidDuplicateLiterals" })
    @Data @AllArgsConstructor @NoArgsConstructor
    @CsvFile(fileName = "data/withHeader.csv")
    public static class Valid_WithHeader {
        @CsvEntry(header = "csv_primitive")
        private int primitive;
        @CsvEntry(header = "csv_string")
        private String string;
        @CsvEntry(header = "csv_date", format = "MM/dd/yyyy")
        private Date date;
        @CsvEntry(header = "csv_enumer")
        private Enumer enumer;
        @CsvEntry(header = "csv_period", format = "HH:mm:ss")
        private Period period;
        @CsvEntry(header = "csv_timeZone")
        private TimeZone timeZone;
        @CsvEntry(header = "csv_locale")
        private Locale locale;
        @CsvEntry(header = "emp_url")
        private URL url;

        public static List<Valid_WithHeader> expected;
        static {
            try {
                expected = Arrays.asList(
                    new Valid_WithHeader(1, "I'mString1", new DateTime(2011, 1, 1, 0, 0, 0, 0).toDate(), Enumer.VALUE_1,
                            new Period(0, 0, 0, 0, 11, 11, 11, 0), TimeZone.getTimeZone("Africa/Casablanca"),
                            new Locale("ar"), new URL("http://www.IAmAUrl1.com")),
                    new Valid_WithHeader(2, "I'mString2", new DateTime(2012, 2, 2, 0, 0, 0, 0).toDate(), Enumer.VALUE_2,
                            new Period(0, 0, 0, 0, 22, 22, 22, 0), TimeZone.getTimeZone("America/Los_Angeles"),
                            new Locale("en"), new URL("http://www.IAmAUrl2.com"))
                );
            } catch (MalformedURLException malformedURLException) { // NOPMD
                log.severe("This should never happen");
            }
        }
    }

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.AvoidDuplicateLiterals" })
    @Data @AllArgsConstructor @NoArgsConstructor
    @CsvFile(fileName = "data/withoutHeader.csv", noHeader = true)
    public static class Valid_WithoutHeader {
        @CsvEntry(position = 0)
        private int primitive;
        @CsvEntry(position = 1)
        private String string;
        @CsvEntry(position = 2, format = "MM/dd/yyyy")
        private Date date;
        @CsvEntry(position = 3)
        private Enumer enumer;
        @CsvEntry(position = 4, format = "HH:mm:ss")
        private Period period;
        @CsvEntry(position = 5)
        private TimeZone timeZone;
        @CsvEntry(position = 6)
        private Locale locale;
        @CsvEntry(position = 7)
        private URL url;

        public static List<Valid_WithoutHeader> expected;
        static {
            try {
                expected = Arrays.asList(
                    new Valid_WithoutHeader(1, "I'mString1", new DateTime(2011, 1, 1, 0, 0, 0, 0).toDate(), Enumer.VALUE_1,
                            new Period(0, 0, 0, 0, 11, 11, 11, 0), TimeZone.getTimeZone("Africa/Casablanca"),
                            new Locale("ar"), new URL("http://www.IAmAUrl1.com")),
                    new Valid_WithoutHeader(2, "I'mString2", new DateTime(2012, 2, 2, 0, 0, 0, 0).toDate(), Enumer.VALUE_2,
                            new Period(0, 0, 0, 0, 22, 22, 22, 0), TimeZone.getTimeZone("America/Los_Angeles"),
                            new Locale("en"), new URL("http://www.IAmAUrl2.com"))
                );
            } catch (MalformedURLException malformedURLException) { // NOPMD
                log.severe("This should never happen");
            }
        }
    }

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.AvoidDuplicateLiterals" })
    @Data @AllArgsConstructor @NoArgsConstructor
    @CsvFile(fileName = "data/withDelimiter.csv", delimiter = ";")
    public static class Valid_WithDelimiter {
        @CsvEntry(header = "csv_primitive")
        private int primitive;
        @CsvEntry(header = "csv_string")
        private String string;
        @CsvEntry(header = "csv_date", format = "MM/dd/yyyy")
        private Date date;
        @CsvEntry(header = "csv_enumer")
        private Enumer enumer;
        @CsvEntry(header = "csv_period", format = "HH:mm:ss")
        private Period period;
        @CsvEntry(header = "csv_timeZone")
        private TimeZone timeZone;
        @CsvEntry(header = "csv_locale")
        private Locale locale;
        @CsvEntry(header = "emp_url")
        private URL url;

        public static List<Valid_WithDelimiter> expected;
        static {
            try {
                expected = Arrays.asList(
                    new Valid_WithDelimiter(1, "I'mString1", new DateTime(2011, 1, 1, 0, 0, 0, 0).toDate(), Enumer.VALUE_1,
                            new Period(0, 0, 0, 0, 11, 11, 11, 0), TimeZone.getTimeZone("Africa/Casablanca"),
                            new Locale("ar"), new URL("http://www.IAmAUrl1.com")),
                    new Valid_WithDelimiter(2, "I'mString2", new DateTime(2012, 2, 2, 0, 0, 0, 0).toDate(), Enumer.VALUE_2,
                            new Period(0, 0, 0, 0, 22, 22, 22, 0), TimeZone.getTimeZone("America/Los_Angeles"),
                            new Locale("en"), new URL("http://www.IAmAUrl2.com"))
                );
            } catch (MalformedURLException malformedURLException) { // NOPMD
                log.severe("This should never happen");
            }
        }
    }

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.AvoidDuplicateLiterals" })
    @Data @AllArgsConstructor @NoArgsConstructor
    @CsvFile(fileName = "data/missingFields.csv")
    public static class Valid_OptionalFields {
        @CsvEntry(header = "csv_primitive", defaultValue="-1", optional = true)
        private int primitive;
        @CsvEntry(header = "csv_string", optional = true)
        private String string;
        @CsvEntry(header = "csv_date", format = "MM/dd/yyyy", optional = true)
        private Date date;
        @CsvEntry(header = "csv_enumer", optional = true)
        private Enumer enumer;
        @CsvEntry(header = "csv_period", format = "HH:mm:ss", optional = true)
        private Period period;
        @CsvEntry(header = "csv_timeZone", optional = true)
        private TimeZone timeZone;
        @CsvEntry(header = "csv_locale", optional = true)
        private Locale locale;
        @CsvEntry(header = "emp_url", optional = true)
        private URL url;

        public static List<Valid_OptionalFields> expected;
        static {
            try {
                expected = Arrays.asList(
                    new Valid_OptionalFields(-1, "I'mString1", new DateTime(2011, 1, 1, 0, 0, 0, 0).toDate(), Enumer.VALUE_1,
                            new Period(0, 0, 0, 0, 11, 11, 11, 0), TimeZone.getTimeZone("Africa/Casablanca"),
                            new Locale("ar"), new URL("http://www.IAmAUrl1.com")),
                    new Valid_OptionalFields(2, null, new DateTime(2012, 2, 2, 0, 0, 0, 0).toDate(), Enumer.VALUE_2,
                            new Period(0, 0, 0, 0, 22, 22, 22, 0), TimeZone.getTimeZone("America/Los_Angeles"),
                            new Locale("en"), new URL("http://www.IAmAUrl2.com")),
                    new Valid_OptionalFields(1, "I'mString1", null, Enumer.VALUE_1,
                            new Period(0, 0, 0, 0, 11, 11, 11, 0), TimeZone.getTimeZone("Africa/Casablanca"),
                            new Locale("ar"), new URL("http://www.IAmAUrl1.com")),
                    new Valid_OptionalFields(2, "I'mString2", new DateTime(2012, 2, 2, 0, 0, 0, 0).toDate(), null,
                            new Period(0, 0, 0, 0, 22, 22, 22, 0), TimeZone.getTimeZone("America/Los_Angeles"),
                            new Locale("en"), new URL("http://www.IAmAUrl2.com")),
                    new Valid_OptionalFields(1, "I'mString1", new DateTime(2011, 1, 1, 0, 0, 0, 0).toDate(), Enumer.VALUE_1,
                            null, TimeZone.getTimeZone("Africa/Casablanca"),
                            new Locale("ar"), new URL("http://www.IAmAUrl1.com")),
                    new Valid_OptionalFields(2, "I'mString2", new DateTime(2012, 2, 2, 0, 0, 0, 0).toDate(), Enumer.VALUE_2,
                            new Period(0, 0, 0, 0, 22, 22, 22, 0), null,
                            new Locale("en"), new URL("http://www.IAmAUrl2.com")),
                    new Valid_OptionalFields(1, "I'mString1", new DateTime(2011, 1, 1, 0, 0, 0, 0).toDate(), Enumer.VALUE_1,
                            new Period(0, 0, 0, 0, 11, 11, 11, 0), TimeZone.getTimeZone("Africa/Casablanca"),
                            null, new URL("http://www.IAmAUrl1.com")),
                    new Valid_OptionalFields(2, "I'mString2", new DateTime(2012, 2, 2, 0, 0, 0, 0).toDate(), Enumer.VALUE_2,
                            new Period(0, 0, 0, 0, 22, 22, 22, 0), TimeZone.getTimeZone("America/Los_Angeles"),
                            new Locale("en"), null)
                );
            } catch (MalformedURLException malformedURLException) { // NOPMD
                log.severe("This should never happen");
            }
        }
    }

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.AvoidDuplicateLiterals" })
    @Data @AllArgsConstructor @NoArgsConstructor
    @CsvFile(fileName = "data/emptyFields.csv")
    public static class Valid_IgnoredFields {
        @CsvEntry(header = "csv_primitive", ignore = true)
        private int primitive;
        @CsvEntry(header = "csv_string", ignore = true)
        private String string;
        @CsvEntry(header = "csv_date", format = "MM/dd/yyyy", ignore = true)
        private Date date;
        @CsvEntry(header = "csv_enumer", ignore = true)
        private Enumer enumer;
        @CsvEntry(header = "csv_period", format = "HH:mm:ss", ignore = true)
        private Period period;
        @CsvEntry(header = "csv_timeZone", ignore = true)
        private TimeZone timeZone;
        @CsvEntry(header = "csv_locale", ignore = true)
        private Locale locale;
        @CsvEntry(header = "emp_url", ignore = true)
        private URL url;

        public static List<Valid_IgnoredFields> expected;
        static {
            expected = Arrays.asList(
                new Valid_IgnoredFields(0, null, null, null, null, null, null, null),
                new Valid_IgnoredFields(0, null, null, null, null, null, null, null)
            );
        }
    }

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.AvoidDuplicateLiterals" })
    @Data @AllArgsConstructor @NoArgsConstructor
    @CsvFile(fileName = "data/emptyFields.csv")
    public static class Valid_DefaultFields {
        @CsvEntry(header = "csv_primitive", defaultValue="1")
        private int primitive;
        @CsvEntry(header = "csv_string", defaultValue="I'mString1")
        private String string;
        @CsvEntry(header = "csv_date", format = "MM/dd/yyyy", defaultValue="01/01/2011")
        private Date date;
        @CsvEntry(header = "csv_enumer", defaultValue="VALUE_1")
        private Enumer enumer;
        @CsvEntry(header = "csv_period", format = "HH:mm:ss", defaultValue="11:11:11")
        private Period period;
        @CsvEntry(header = "csv_timeZone", defaultValue="Africa/Casablanca")
        private TimeZone timeZone;
        @CsvEntry(header = "csv_locale", defaultValue="ar")
        private Locale locale;
        @CsvEntry(header = "emp_url", defaultValue="http://www.IAmAUrl1.com")
        private URL url;

        public static List<Valid_DefaultFields> expected;
        static {
            try {
                expected = Arrays.asList(
                    new Valid_DefaultFields(1, "I'mString1", new DateTime(2011, 1, 1, 0, 0, 0, 0).toDate(), Enumer.VALUE_1,
                            new Period(0, 0, 0, 0, 11, 11, 11, 0), TimeZone.getTimeZone("Africa/Casablanca"),
                            new Locale("ar"), new URL("http://www.IAmAUrl1.com")),
                    new Valid_DefaultFields(1, "I'mString1", new DateTime(2011, 1, 1, 0, 0, 0, 0).toDate(), Enumer.VALUE_1,
                            new Period(0, 0, 0, 0, 11, 11, 11, 0), TimeZone.getTimeZone("Africa/Casablanca"),
                            new Locale("ar"), new URL("http://www.IAmAUrl1.com"))
                );
            } catch (MalformedURLException malformedURLException) { // NOPMD
                log.severe("This should never happen");
            }
        }
    }

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.AvoidDuplicateLiterals" })
    @Data @AllArgsConstructor @NoArgsConstructor
    @CsvFile(fileName = "data/emptyFields.csv")
    public static class Invalid_WithoutHeader {
        @CsvEntry(position = 0)
        private int primitive;
        @CsvEntry(position = 1)
        private String string;
        @CsvEntry(position = 2, format = "MM/dd/yyyy")
        private Date date;
        @CsvEntry(position = 3)
        private Enumer enumer;
        @CsvEntry(position = 4, format = "HH:mm:ss")
        private Period period;
        @CsvEntry(position = 5)
        private TimeZone timeZone;
        @CsvEntry(position = 6)
        private Locale locale;
        @CsvEntry(position = 7)
        private URL url;

        public static ParsingException expected = new ParsingException();
    }

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.AvoidDuplicateLiterals" })
    @Data @AllArgsConstructor @NoArgsConstructor
    @CsvFile(fileName = "data/emptyFields.csv", noHeader = true)
    public static class Invalid_WithHeader {
        @CsvEntry(header = "csv_primitive")
        private int primitive;
        @CsvEntry(header = "csv_string")
        private String string;
        @CsvEntry(header = "csv_date", format = "MM/dd/yyyy")
        private Date date;
        @CsvEntry(header = "csv_enumer")
        private Enumer enumer;
        @CsvEntry(header = "csv_period", format = "HH:mm:ss")
        private Period period;
        @CsvEntry(header = "csv_timeZone")
        private TimeZone timeZone;
        @CsvEntry(header = "csv_locale")
        private Locale locale;
        @CsvEntry(header = "emp_url")
        private URL url;

        public static ParsingException expected = new ParsingException();
    }

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.AvoidDuplicateLiterals" })
    @Data @AllArgsConstructor @NoArgsConstructor
    @CsvFile(fileName = "data/emptyFields.csv")
    public static class Invalid_NoDefaultNonOptionalPrimitive {
        @CsvEntry(header = "csv_primitive")
        private int primitive;

        public static ParsingException expected = new ParsingException();
    }

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.AvoidDuplicateLiterals" })
    @Data @AllArgsConstructor @NoArgsConstructor
    @CsvFile(fileName = "data/emptyFields.csv")
    public static class Invalid_NoDefaultNonOptionalNonPrimitive {
        @CsvEntry(header = "csv_string")
        private String string;

        public static ParsingException expected = new ParsingException();
    }
    // CHECKSTYLE:ON
}
