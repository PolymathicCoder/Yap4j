package com.polymathiccoder.yap4j.csv.annotation;

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
@Target({java.lang.annotation.ElementType.TYPE })
public @interface CsvFile {

    /** The default delimiter. */
    String DEFAULT_DELIMITER = ","; // NOPMD

    /**
     * The file name.
     */
    String fileName();

    /**
     * The delimiter. The default is an empty string.
     */
    String delimiter() default DEFAULT_DELIMITER;

    /**
     * Whether the file has a header or not. The default is false.
     */
    boolean noHeader() default false;
 }
