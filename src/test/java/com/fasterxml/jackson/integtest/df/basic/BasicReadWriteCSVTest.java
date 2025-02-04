package com.fasterxml.jackson.integtest.df.basic;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

public class BasicReadWriteCSVTest extends BaseTest
{
    private final CsvMapper MAPPER = csvMapper();

    @Test
    public void testSimpleSequenceNoHeader() throws Exception
    {
        try (MappingIterator<PointXY> it = MAPPER.readerWithSchemaFor(PointXY.class)
                .readValues("1,2\n-3,0\n5,6\n")) {
            PointXY entry;
            
            assertTrue(it.hasNext());
            assertNotNull(entry = it.next());
            assertEquals(1, entry.x);
            assertEquals(2, entry.y);
            assertTrue(it.hasNext());
            assertNotNull(entry = it.next());
            assertEquals(-3, entry.x);
            assertEquals(0, entry.y);
            assertTrue(it.hasNext());
            assertNotNull(entry = it.next());
            assertEquals(5, entry.x);
            assertEquals(6, entry.y);
            assertFalse(it.hasNext());
        }
    }

    @Test
    public void testSimpleSequenceWithHeader() throws Exception
    {
        final CsvSchema schema = MAPPER.schemaFor(PointXY.class)
                .withHeader();
        try (MappingIterator<PointXY> it = MAPPER
                .readerFor(PointXY.class)
                .with(schema)
                .readValues("x, y \n1,2\n5,6")) {
            PointXY entry;

            assertTrue(it.hasNext());
            assertNotNull(entry = it.next());
            assertEquals(1, entry.x);
            assertEquals(2, entry.y);
            assertTrue(it.hasNext());
            assertNotNull(entry = it.next());
            assertEquals(5, entry.x);
            assertEquals(6, entry.y);
            assertFalse(it.hasNext());
        }
    }
}
