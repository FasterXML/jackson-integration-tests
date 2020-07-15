package com.fasterxml.jackson.integtest.df.props;

import java.util.Calendar;
import java.util.Locale;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import com.fasterxml.jackson.datatype.joda.JodaModule;

import com.fasterxml.jackson.integtest.BaseTest;

public class DateTimeWithPropsTest extends BaseTest
{
    static class DateTimeWrapper {
        public DateTime dt;
        
        protected DateTimeWrapper() { }
        public DateTimeWrapper(DateTime v) { dt = v; }
    }

    private final static DateTime TEST_DATETIME = DateTime.parse("1972-12-28T12:00:01.000Z");
    private final static Calendar TEST_CALENDAR = TEST_DATETIME.toCalendar(Locale.getDefault());

    private final JavaPropsMapper MAPPER = propsMapperBuilder()
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

    public void testJodaDateTimeTextual() throws Exception
    {
        DateTimeWrapper input = new DateTimeWrapper(TEST_DATETIME);
        String doc = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        DateTimeWrapper result = MAPPER.readerFor(DateTimeWrapper.class)
                .readValue(doc);
        assertEquals(input.dt, result.dt);
    }

    // 06-Jul-2020, tatu: Does not yet work, need some TLC on Joda
    /*
    public void testJodaDateTimeNumeric() throws Exception
    {
        DateTimeWrapper input = new DateTimeWrapper(TEST_DATETIME);
        String doc = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        DateTimeWrapper result = null;
        try {
            result = MAPPER.readValue(doc, DateTimeWrapper.class);
        } catch (Exception e) {
            fail("Failed with input of:\n"+doc
                    +"\nproblem: `"+e.getClass().getName()+"`: "+e.getMessage());
        }
        assertEquals(TEST_DATETIME, result.dt);
    }
    */
}
