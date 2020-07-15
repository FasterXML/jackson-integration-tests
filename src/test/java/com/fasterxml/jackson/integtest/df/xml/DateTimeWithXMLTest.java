package com.fasterxml.jackson.integtest.df.xml;

import java.util.Calendar;
import java.util.Locale;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.datatype.joda.JodaModule;

import com.fasterxml.jackson.integtest.BaseTest;

public class DateTimeWithXMLTest extends BaseTest
{
    static class DateTimeWrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public DateTime dt;

        protected DateTimeWrapper() { }
        public DateTimeWrapper(DateTime v) { dt = v; }
    }

    private final static DateTime TEST_DATETIME = DateTime.parse("1972-12-28T12:00:01.000Z");
    private final static Calendar TEST_CALENDAR = TEST_DATETIME.toCalendar(Locale.getDefault());

    private final ObjectMapper MAPPER = xmlMapperBuilder()
            .addModule(new JodaModule())
            .build();

    /*
    /**********************************************************************
    /* Test methods, old JDK date/time
    /**********************************************************************
     */

    public void testJDKCalendarTextual() throws Exception
    {
        CalendarWrapper input = new CalendarWrapper(TEST_CALENDAR);
        String doc = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        CalendarWrapper result = MAPPER.readerFor(CalendarWrapper.class)
                .readValue(doc);
        assertEquals(input.cal.getTimeInMillis(), result.cal.getTimeInMillis());
    }

    public void testJDKCalendarTimestamp() throws Exception
    {
        CalendarWrapper input = new CalendarWrapper(TEST_CALENDAR);
        String doc = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        CalendarWrapper result = MAPPER.readerFor(CalendarWrapper.class)
                .readValue(doc);
        assertEquals(input.cal.getTimeInMillis(), result.cal.getTimeInMillis());
    }

    /*
    /**********************************************************************
    /* Test methods, Joda
    /**********************************************************************
     */

    public void testJodaDateTimeWrapperTextual() throws Exception
    {
        DateTimeWrapper input = new DateTimeWrapper(TEST_DATETIME);
        String xml = MAPPER.writer()
                .writeValueAsString(input);
        DateTimeWrapper result = MAPPER.readValue(xml, DateTimeWrapper.class);
        assertEquals(input.dt, result.dt);
    }

    public void testJodaRootDateTimeTextual() throws Exception
    {
        String xml = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(TEST_DATETIME);
        DateTime result = MAPPER.readValue(xml, DateTime.class);
        assertEquals(TEST_DATETIME, result);
    }

    // 06-Jul-2020, tatu: Does not yet work, need some TLC on Joda
    /*
    public void testJodaRootDateTimeNumeric() throws Exception
    {
        String xml = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(TEST_DATETIME);
        DateTime result = MAPPER.readValue(xml, DateTime.class);
        assertEquals(TEST_DATETIME, result);
    }
    */
}
