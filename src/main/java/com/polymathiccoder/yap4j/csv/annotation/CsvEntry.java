package com.polymathiccoder.yap4j.csv.annotation;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to supply Class/CSV record metadata.
 *
 * @author  Abdelmonaim Remani
 * @version 0.1.0
 * @since 0.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD })
public @interface CsvEntry {

    /** The default position. */
    int DEFAULT_POSITION = -1; // NOPMD

    /**
     * The header in the CSV file. The default is an empty string.
     */
    String header() default EMPTY;

    /**
     * The position in the CSV file. The default is the invalid static value -1.
     */
    int position() default DEFAULT_POSITION;

    /**
     * The format in the CSV file. The default is an empty string.
     */
    String format() default EMPTY;

    /**
     * Whether it is optional or not. The default is false.
     */
    boolean optional() default false;

    /**
     * Whether it is to be ignored or not. The default is false.
     */
    boolean ignore() default false;

    /**
     * Default value. The default is an empty string.
     */
    String defaultValue() default EMPTY;
 }
