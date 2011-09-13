package com.polymathiccoder.yap4j.common;

/**
 * An instance of this class is thrown when the conversion from one type to another is impossible.
 *
 * @author  Abdelmonaim Remani
 * @version 0.1.0
 * @since 0.1.0
 */
public class ConversionException extends RuntimeException {

    /** The error message indicating the from and to types. */
    private static final String ERROR_FROM_AND_TO = "Could not convert from %s to %s";

    /** The error message indicating the from type. */
    private static final String ERROR_FROM = "Could not convert from %s";

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new conversion exception.
     */
    public ConversionException() {
        super();
    }

    /**
     * Instantiates a new conversion exception.
     *
     * @param fromClass the type converted from
     */
    public ConversionException(final Class<?> fromClass) {
        super(String.format(ERROR_FROM, fromClass));
    }

    /**
     * Instantiates a new conversion exception.
     *
     * @param fromClass the type converted from
     * @param toClass the type converted to
     */
    public ConversionException(final Class<?> fromClass, final Class<?> toClass) {
        super(String.format(ERROR_FROM_AND_TO, fromClass, toClass));
    }
}
