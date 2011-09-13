package com.polymathiccoder.yap4j.csv;

/**
 * An instance of this class is thrown when the parsing is not possible.
 *
 * @author  Abdelmonaim Remani
 * @version 0.1.0
 * @since 0.1.0
 */
public class ParsingException extends RuntimeException {

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new parsing exception.
     */
    public ParsingException() {
        super();
    }

    /**
     * Instantiates a new parsing exception.
     *
     * @param message the message
     */
    public ParsingException(final String message) {
        super(message);
    }
}
