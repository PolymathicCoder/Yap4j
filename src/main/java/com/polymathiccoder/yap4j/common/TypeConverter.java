package com.polymathiccoder.yap4j.common;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeParserBucket;

/**
 * Used to perform type conversions.
 *
 * @author  Abdelmonaim Remani
 * @version 0.1.0
 * @since 0.1.0
 */
public enum TypeConverter { // FIXME Must refactor to relieve this horrible cyclomatic complexity
    /** The singleton instance. */
    INSTANCE;

    /**
     * Converts from string to many type.
     *
     * @param value the string to be converted
     * @param toClass the type to convert to
     * @param format the format of the string to be converted. This is required to convert to temporal types
     * @return the converted object
     */
    @SuppressWarnings("unchecked")
    public Object fromString(final String value, @SuppressWarnings("rawtypes") final Class toClass, final String format) { // FIXME Must refactor to relieve this horrible cyclomatic complexity
        checkArgument(!StringUtils.isEmpty(value), TypeConversionErrorMessages.ERROR_CONVERT_FROM_NULL_OR_EMPTY_STRING);

        Object converted = null; // NOPMD
        // URL
        if (toClass == URL.class) {
            try {
                converted = new URL(value); // NOPMD
            } catch (MalformedURLException malformedURLException) { // NOPMD
                throw new ConversionException(String.class, toClass); // NOPMD
            }
        // Locale
        } else if (toClass == Locale.class) {
            if (Arrays.asList(Locale.getISOLanguages()).contains(value)) {
                converted = new Locale(value);
            } else {
                throw new ConversionException(String.class, toClass);
            }
        // Temporal
        } else if (toClass == Date.class) {
            checkArgument(!StringUtils.isEmpty(format), TypeConversionErrorMessages.ERROR_MISSING_FORMAT);
            final DateConverter dateConverter = new DateConverter();
            dateConverter.setPattern(format);
            converted = dateConverter.convert(Date.class, value);
        } else if (toClass == TimeZone.class) {
            if (Arrays.asList(TimeZone.getAvailableIDs()).contains(value)) {
                converted = TimeZone.getTimeZone(value);
            } else {
                throw new ConversionException(String.class, toClass);
            }
        } else if (toClass == Period.class) {
            checkArgument(!StringUtils.isEmpty(format), TypeConversionErrorMessages.ERROR_MISSING_FORMAT);
            converted = new JodaConverter(value, format).toPeriod();
        // Enum
        } else if (toClass.isEnum()) {
            if (StringUtils.isNumeric(value)) {
                final EnumSet<?> enumValues = EnumSet.allOf(toClass);
                for (Enum<?> e : enumValues) {
                    if (Integer.parseInt(value) == e.ordinal()) {
                        converted = e; // NOPMD
                        break;
                    }
                }
                if (converted == null) {
                    throw new ConversionException(String.class, toClass);
                }
            } else {
                if (EnumUtils.isValidEnum(toClass, value)) {
                    converted = EnumUtils.getEnum(toClass, value);
                } else {
                    throw new ConversionException(String.class, toClass);
                }
            }
        // Other
        } else {
            try {
                converted = ConvertUtils.convert(value, toClass); // NOPMD
            } catch (org.apache.commons.beanutils.ConversionException conversionException) { // NOPMD
                throw new ConversionException(String.class, toClass); // NOPMD
            }
        }

        return converted;
    }

    /**
     * Converts any type to a string.
     *
     * @param value the value to convert
     * @return the string
     */
    public String toString(final Object value) {
        checkNotNull(value, TypeConversionErrorMessages.ERROR_CONVERT_FROM_NULL_OR_EMPTY_STRING);
        return value.toString();
    }

    /**
     * Used to convert temporal types from string in a specific format using
     * standard Java date and time patterns. Refer to {@link java.text.SimpleDateFormat the list of the patterns}
     */
    private static class JodaConverter extends DateTimeParserBucket {

        /** The parsed values mapped to their field type. */
        private final transient Map<DateTimeFieldType, Integer> values;

        /* (non-Javadoc)
         * @see org.joda.time.format.DateTimeParserBucket#saveField(org.joda.time.DateTimeFieldType, int)
         */
        @Override
        public void saveField(final DateTimeFieldType fieldType, final int value) {
            //Intercept the call to saveField
            values.put(fieldType, value);
            super.saveField(fieldType, value);
        }

        /**
         * Instantiates a new joda converter.
         *
         * @param value the value
         * @param format the format
         */
        public JodaConverter(final String value, final String format) {
            super(0, null, null, 0, 0);
            values = new HashMap<DateTimeFieldType, Integer>();
            try {
                //call to trigger parsing and construct the map. Refer to the overriden method above saveField.
                DateTimeFormat.forPattern(format).getParser().parseInto(this, value, 0);
            } catch (IllegalArgumentException illegalArgumentException) { // NOPMD
                throw new ConversionException(String.class); // NOPMD
            }
        }

        /**
         * To period.
         *
         * @return the period
         */
        public Period toPeriod() {
            Period period = new Period(); // NOPMD
            for (Map.Entry<DateTimeFieldType, Integer> entry : values
                    .entrySet()) {
                try {
                    period = period.withField(entry.getKey().getDurationType(),
                            entry.getValue());
                } catch (IllegalArgumentException illegalArgumentException) { // NOPMD
                    throw new ConversionException(String.class, Period.class); // NOPMD
                }
            }
            return period;
        }
    }
}

/**
 * Used to lookup type conversions error messages.
 */
final class TypeConversionErrorMessages {

    // CHECKSTYLE:OFF
    /** The error message when converting from a null or an empty string. */
    public static final String ERROR_CONVERT_FROM_NULL_OR_EMPTY_STRING = "Cannot convert a null or empty String"; // NOPMD

    /** The error message when the format is missing. */
    public static final String ERROR_MISSING_FORMAT = "You must provide a format to convert from String"; // NOPMD
    // CHECKSTYLE:ON
    /**
     * Prevents instantiation.
     */
    private TypeConversionErrorMessages() {
        throw new UnsupportedOperationException();
    }
}
