package com.polymathiccoder.yap4j.csv;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import com.polymathiccoder.yap4j.csv.annotation.CsvEntry;
import com.polymathiccoder.yap4j.csv.annotation.CsvFile;

/**
 * Used to examines metadata to build a parsing model and instantiates a CSV parser.
 *
 * @author  Abdelmonaim Remani
 * @version 0.1.0
 * @since 0.1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE) // CHECKSTYLE IGNORE
public final class CsvParserFactory { // NOPMD
    /**
     * Creates a new CsvParser object.
     *
     * @param clazz the type to create the parser for
     * @return the CSV parser
     */
    public static CsvParser createParser(final Class<?> clazz) {
        //Process annotations and build parsing model
        final CsvParserFactory.ParsingModel.Record fileParsingModel = new CsvParserFactory.ParsingModel.Record(
                ((CsvFile) clazz.getAnnotation(CsvFile.class)).fileName(),
                ((CsvFile) clazz.getAnnotation(CsvFile.class)).delimiter(),
                ((CsvFile) clazz.getAnnotation(CsvFile.class)).noHeader());

        final List<ParsingModel.Entry> fieldParsingModels = new ArrayList<ParsingModel.Entry>(); // NOPMD
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            final Annotation annotation = field.getAnnotation(CsvEntry.class);
            if (annotation != null) {
                fieldParsingModels.add(new CsvParserFactory.ParsingModel.Entry(// NOPMD
                        field.getName(),
                        ((CsvEntry) annotation).header(),
                        ((CsvEntry) annotation).position(),
                        ((CsvEntry) annotation).format(),
                        ((CsvEntry) annotation).optional(),
                        ((CsvEntry) annotation).ignore(),
                        ((CsvEntry) annotation)
                                .defaultValue()));
            }
        }

        //Create parser
        return new CsvParser(clazz, new ParsingModel(fileParsingModel, fieldParsingModels));
    }

    //CHECKSTYLE:OFF
    /**
     * A Parsing model.
     *
     * @author  Abdelmonaim Remani
     * @version 0.1.0
     * @since 0.1.0
     */
    public static class ParsingModel {

        /** The parsing model - Class/CSV record metadata. */
        public final transient Record record;

        /** The parsing model - Bean Field/CSV entry metadata. */
        public final transient List<Entry> entries;

        /** The CSV entry positions. */
        public final transient Map<Entry, Integer> fieldPositions;

        /**
         * Instantiates a new parsing model.
         *
         * @param record the Class/CSV record metadata
         * @param entries the Bean Field/CSV entry metadata
         */
        public ParsingModel(final Record record, final List<Entry> entries) {
            this.record = record;
            this.entries = entries;
            this.fieldPositions = new HashMap<Entry, Integer>();
        }

        /**
         * Bean Field/CSV entry metadata.
         *
         * @author  Abdelmonaim Remani
         * @version 0.1.0
         * @since 0.1.0
         */
        @RequiredArgsConstructor
        public static class Entry {

            /** The field name in Java class. */
            public final transient String beanFieldName;

            /** The header in the CSV file. */
            public final transient String header;

            /** The position in the CSV file. */
            public final transient int position;

            /** The format in the CSV file. */
            public final transient String format;

            /** Whether it is optional or not. */
            public final transient boolean optional;

            /** Whether it is to be ignored or not. */
            public final transient boolean ignore;

            /** The default value. */
            public final transient String defaultValue;
        }

        /**
         * Class/CSV record metadata.
         *
         * @author  Abdelmonaim Remani
         * @version 0.1.0
         * @since 0.1.0
         */
        @RequiredArgsConstructor
        public static class Record {

            /** The file name. */
            public final transient String fileName;

            /** The delimiter. */
            public final transient String delimiter;

            /** Whether the file has a header or not. */
            public final transient boolean noHeader;
        }
    }
  //CHECKSTYLE:ON
}
