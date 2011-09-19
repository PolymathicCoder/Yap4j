package com.polymathiccoder.yap4j;

import java.util.List;

/**
 * Used a CSV file into a list of object and vice versa.
 *
 * @author  Abdelmonaim Remani
 * @version 0.2.0
 * @since 0.2.0
 */
public interface Parser {

    /**
     * Deserializes CSV records in file as objects of the specified type.
     *
     * @param <T> the type to parse into
     * @return the list of CSV records as objects of the specified type
     */
    <T> List<T> deserialize();

    /**
     * Serialize.
     *
     * @param <T> the type to serialize from
     * @param list the list of CSV records as objects of the specified type
     */
    <T> void serialize(List<T> list);

}