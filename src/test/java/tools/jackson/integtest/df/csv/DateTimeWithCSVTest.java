package tools.jackson.integtest.df.csv;

import java.util.Calendar;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.SerializationFeature;

import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;
import tools.jackson.datatype.joda.JodaModule;

import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DateTimeWithCSVTest extends BaseTest
{
    static class DateTimeWrapper {
        public DateTime dt;

        protected DateTimeWrapper() { }
        public DateTimeWrapper(DateTime v) { dt = v; }
    }

    private final static DateTime TEST_DATETIME = DateTime.parse("1972-12-28T12:00:01.000Z");
    private final static Calendar TEST_CALENDAR = TEST_DATETIME.toCalendar(Locale.getDefault());

    private final CsvMapper MAPPER = csvMapperBuilder()
            .addModule(new JodaModule())
            .build();

    private final CsvSchema WRAPPER_SCHEMA_JODA = MAPPER.schemaFor(DateTimeWrapper.class);
    private final CsvSchema WRAPPER_SCHEMA_JDK_OLD = MAPPER.schemaFor(CalendarWrapper.class);

    /*
    /**********************************************************************
    /* Test methods, old JDK date/time
    /**********************************************************************
     */

    @Test
    public void testJDKCalendarTextual() throws Exception
    {
        CalendarWrapper input = new CalendarWrapper(TEST_CALENDAR);
        String doc = MAPPER.writer(WRAPPER_SCHEMA_JDK_OLD)
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        CalendarWrapper result = MAPPER.readerFor(CalendarWrapper.class)
                .with(WRAPPER_SCHEMA_JDK_OLD)
                .readValue(doc);
        assertEquals(input.cal.getTimeInMillis(), result.cal.getTimeInMillis());
    }

    @Test
    public void testJDKCalendarTimestamp() throws Exception
    {
        CalendarWrapper input = new CalendarWrapper(TEST_CALENDAR);
        String doc = MAPPER.writer(WRAPPER_SCHEMA_JDK_OLD)
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        CalendarWrapper result = null;
        try {
            result = MAPPER.readerFor(CalendarWrapper.class)
                .with(WRAPPER_SCHEMA_JDK_OLD)
                .readValue(doc);
        } catch (Exception e) {
            fail("Failed with input of:\n"+doc
                    +"\nproblem: `"+e.getClass().getName()+"`: "+e.getMessage());
        }
        assertEquals(input.cal.getTimeInMillis(), result.cal.getTimeInMillis());
    }

    /*
    /**********************************************************************
    /* Test methods, Joda
    /**********************************************************************
     */

    @Test
    public void testJodaDateTimeTextual() throws Exception
    {
        DateTimeWrapper input = new DateTimeWrapper(TEST_DATETIME);
        String doc = MAPPER.writer(WRAPPER_SCHEMA_JODA)
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        DateTimeWrapper result = MAPPER.readerFor(DateTimeWrapper.class)
                .with(WRAPPER_SCHEMA_JODA)
                .readValue(doc);
        assertEquals(input.dt, result.dt);
    }

    @Test
    public void testJodaDateTimeTimestamp() throws Exception
    {
        DateTimeWrapper input = new DateTimeWrapper(TEST_DATETIME);
        String doc = MAPPER.writer(WRAPPER_SCHEMA_JODA)
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        DateTimeWrapper result = null;
        try {
            result = MAPPER.readerFor(DateTimeWrapper.class)
                    .with(WRAPPER_SCHEMA_JODA)
                    .readValue(doc);
        } catch (Exception e) {
            fail("Failed with input of:\n"+doc
                    +"\nproblem: `"+e.getClass().getName()+"`: "+e.getMessage());
        }
        assertEquals(TEST_DATETIME, result.dt);
    }
}
