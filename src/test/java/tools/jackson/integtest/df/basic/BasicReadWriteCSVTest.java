package tools.jackson.integtest.df.basic;

import com.fasterxml.jackson.databind.*;

import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

import tools.jackson.integtest.BaseTest;

public class BasicReadWriteCSVTest extends BaseTest
{
    private final CsvMapper MAPPER = csvMapper();

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
