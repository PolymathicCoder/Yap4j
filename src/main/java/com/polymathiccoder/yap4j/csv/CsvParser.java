package com.polymathiccoder.yap4j.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import lombok.Cleanup;
import lombok.extern.java.Log;

import org.apache.commons.lang3.StringUtils;

import com.polymathiccoder.yap4j.common.TypeConverter;
import com.polymathiccoder.yap4j.csv.annotation.CsvEntry;

/**
 * Used a CSV file into a list of object and vice versa.
 *
 * @author  Abdelmonaim Remani
 * @version 0.1.0
 * @since 0.1.0
 */
@Log
public final class CsvParser {

    /** The type to parse into. */
    private final transient Class<?> clazz;

    /** The parsing model. */
    private final transient CsvParserFactory.ParsingModel parsingModel;

    /**
     * Instantiates a new CSV parser.
     *
     * @param clazz the type to parse into
     * @param parsingModel the parsing model
     */
    public CsvParser(final Class<?> clazz, final CsvParserFactory.ParsingModel parsingModel) {
        this.clazz = clazz;
        this.parsingModel = parsingModel;
    }

    /**
     * Deserializes CSV records in file as objects of the specified type.
     *
     * @param <T> the type to parse into
     * @return the list of CSV records as objects of the specified type
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> deserialize() {
        final List<T> list = new ArrayList<T>();
        final File file = new File(parsingModel.record.fileName);
        try {
            @Cleanup final Scanner scanner = new Scanner(file);

            //Map bean fields to position
            if (parsingModel.record.noHeader) {
                mapBeanFieldsToPositionsWithoutRecordHeader();
            } else {
                mapBeanFieldsToPositionsWithRecordHeader(scanner.nextLine());
            }

            //Process data lines
            while (scanner.hasNext()) {
                list.add((T) parseDataLine(scanner.next()));
            }

        } catch (FileNotFoundException fileNotFoundException) { // NOPMD
            log.severe("Could not find the file " + parsingModel.record.fileName);
            return list; // NOPMD
        }

        return (List<T>) list;
    }

    /**
     * Maps the bean fields to their corresponding positions in the CSV file.
     *
     * Logic Tree (Y is always to the right and N is always to the left):
     *
     *  Position? *
     *           / \
     *          /   \
     *         Exp Set
     *
     */
    private void mapBeanFieldsToPositionsWithoutRecordHeader() {
        //File with no header line
        for (CsvParserFactory.ParsingModel.Entry field : parsingModel.entries) {
            if (field.ignore) {
                continue;
            }
            if (field.position != CsvEntry.DEFAULT_POSITION) { // NOPMD
                //Position set
                parsingModel.fieldPositions.put(field, field.position);
            } else {
                //No position set
                throw new ParsingException(ParsingErrorMessages.ERROR_NO_POSITION_SPECIFIED_FOR_FILE_WITH_NO_RECORD_HEADER);
            }
        }
    }

    /**
     * Maps the bean fields to their corresponding positions in the CSV file according to the record header.
     *
     * Logic Tree (Y is always to the right and N is always to the left):
     *
     * Header? *
     *        / \
     *       /   \
     *      Exp Set
     *
     * @param headerLine the record header line
     */
    private void mapBeanFieldsToPositionsWithRecordHeader(final String headerLine) {
        //File with header line
        final String[] csvFileHeaders = headerLine.split(parsingModel.record.delimiter);
        for (int i = 0; i < csvFileHeaders.length; i++) {
            for (CsvParserFactory.ParsingModel.Entry field : parsingModel.entries) {
                if (field.ignore) {
                    continue;
                }
                if (StringUtils.isEmpty(field.header)) {
                    //No header set
                    throw new ParsingException(ParsingErrorMessages.ERROR_NO_HEADER_SPECIFIED_FOR_FILE_WITH_RECORD_HEADER);
                }
                if (field.header.equals(csvFileHeaders[i])) {
                    //Header set
                    parsingModel.fieldPositions.put(field, Integer.valueOf(i));
                }
            }
        }
    }

    /**
     * Parses a data line in the CSV file.
     *
     * Logic Tree (Y is always to the right and N is always to the left):
     *
     *             Value? *
     *                   / \
     *                  /   \
     *                 /     \
     * Default value? *      Set
     *               / \
     *              /   \
     *             /     \
     *  Optional? *      Set
     *           / \
     *          /   \
     *         /     \
     *       Exp      * Primitive?
     *       / \
     *      /   \
     *     /     \
     *   Set     Exp
     *
     * @param dataline the data line to be parsed
     * @return the parsed object
     */
    private Object parseDataLine(final String dataline) {
        try {
            final String[] tokens = StringUtils.splitByWholeSeparatorPreserveAllTokens(dataline, parsingModel.record.delimiter); // NOPMD

            //Construct the beans
            final Object instance = clazz.newInstance(); // NOPMD
            for (CsvParserFactory.ParsingModel.Entry entry : parsingModel.entries) {
                if (parsingModel.fieldPositions.get(entry) != null) {
                    final String value = tokens[((Integer) parsingModel.fieldPositions.get(entry)).intValue()];
                    final Field beanField = clazz.getDeclaredField(entry.beanFieldName);
                    beanField.setAccessible(true);
                    //Value?
                    Object convertedValue = null; // NOPMD
                    if (!StringUtils.isEmpty(value)) { // NOPMD
                        convertedValue = TypeConverter.INSTANCE.fromString(value, beanField.getType(), entry.format);
                    } else {
                        //Default Value?
                        if (!StringUtils.isEmpty(entry.defaultValue)) { // NOPMD
                            convertedValue = TypeConverter.INSTANCE.fromString(entry.defaultValue , beanField.getType(), entry.format);
                        } else {
                            //Optional?
                            if (entry.optional) {
                                //Primitive?
                                if (beanField.getType().isPrimitive()) {
                                    throw new ParsingException(ParsingErrorMessages.ERROR_OPTIONAL_PRIMITIVE_NO_VALUE_NO_DEFAULT);
                                }
                            } else {
                                throw new ParsingException(ParsingErrorMessages.ERROR_NON_OPTIONAL_NO_VALUE_NO_DEFAULT);
                            }
                        }
                    }

                    beanField.set(instance, convertedValue);
                }
            }
            return instance; // NOPMD
        } catch (Exception exception) {
            if (exception instanceof ParsingException) { // NOPMD
                throw (ParsingException) exception;
            }
            log.severe(String.format(ParsingErrorMessages.ERROR_GENERIC, parsingModel.record.fileName, List.class.getSimpleName(), clazz.getSimpleName()));
        }
        return null;
    }

    /**
     * Used to lookup type parsing error messages.
     */
    private static final class ParsingErrorMessages {

        /** A generic error message. */
        private static final String ERROR_GENERIC = "Could not parse %s into a %s<%s>";

        /** The error message when a non optional has no value or default. */
        private static final String ERROR_NON_OPTIONAL_NO_VALUE_NO_DEFAULT = "A non-optional field must be assigned either a value or default value"; // NOPMD

        /** The error message when an optional primitive has no value or default. */
        private static final String ERROR_OPTIONAL_PRIMITIVE_NO_VALUE_NO_DEFAULT = "A primitive field must be assigned either a value or default value"; // NOPMD

        /** The error message when no corresponding header is specified for field and the file has a record header. */
        private static final String ERROR_NO_HEADER_SPECIFIED_FOR_FILE_WITH_RECORD_HEADER = "A field must be assigned a header when the file to-be-parsed has a record header"; // NOPMD

        /** The error message when no corresponding position is specified for field and the file has a no record header. */
        private static final String ERROR_NO_POSITION_SPECIFIED_FOR_FILE_WITH_NO_RECORD_HEADER = "A field must be assigned a position when the file to-be-parsed has no record header"; // NOPMD

        /**
         * Prevents instantiation.
         */
        private ParsingErrorMessages() {
            throw new UnsupportedOperationException();
        }
    }
}
