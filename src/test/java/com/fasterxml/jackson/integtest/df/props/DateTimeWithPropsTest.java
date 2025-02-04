package com.fasterxml.jackson.integtest.df.props;

import java.util.Calendar;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import com.fasterxml.jackson.datatype.joda.JodaModule;

import com.fasterxml.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DateTimeWithPropsTest extends BaseTest
{
    static class DateTimeWrapper {
        public DateTime dt;
        
        protected DateTimeWrapper() { }
        public DateTimeWrapper(DateTime v) { dt = v; }
    }

    private final static DateTime TEST_DATETIME = DateTime.parse("1972-12-28T12:00:01.000Z");
    private final static long TEST_TIMESTAMP = TEST_DATETIME.getMillis();
    private final static Calendar TEST_CALENDAR = TEST_DATETIME.toCalendar(Locale.getDefault());

    static class XxxWrapper<X> {
        public X value;

        public XxxWrapper(X v) { value = v; }
        protected XxxWrapper() { value = null; }
    }

    private final JavaPropsMapper MAPPER = propsMapperBuilder()
            .addModule(new JodaModule())
            .build();

    /*
    /**********************************************************************
    /* Test methods, old JDK date/time
    /**********************************************************************
     */

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    // // // And then a wider set of things

    @SuppressWarnings("deprecation")
    @Test
    public void testJodaDateMidnightNumeric() throws Exception {
        _testJodaMiscNumeric(org.joda.time.DateMidnight.class,
                new org.joda.time.DateMidnight(TEST_TIMESTAMP));
    }

    @Test
    public void testJodaInstantNumeric() throws Exception {
        _testJodaMiscNumeric(Instant.class, new Instant(TEST_TIMESTAMP));
    }

    @Test
    public void testJodaLocalDateNumeric() throws Exception {
        _testJodaMiscNumeric(LocalDate.class, new LocalDate(TEST_TIMESTAMP));
    }

    @Test
    public void testJodaLocalDateTimeNumeric() throws Exception {
        // 15-Jul-2020, tatu: Need to force use of UTC, default constructor would
        //    use local TimeZone
        _testJodaMiscNumeric(LocalDateTime.class, new LocalDateTime(TEST_TIMESTAMP,  DateTimeZone.UTC));
    }

    @Test
    public void testJodaLocalTimeNumeric() throws Exception {
        _testJodaMiscNumeric(LocalTime.class, new LocalTime(TEST_TIMESTAMP));
    }

    private <T> void _testJodaMiscNumeric(Class<T> type, T instance) throws Exception
    {
        XxxWrapper<T> input = new XxxWrapper<>(instance);
        String doc = "value: "+TEST_TIMESTAMP;
        XxxWrapper<T> result = null;
        JavaType readType = MAPPER.getTypeFactory()
                .constructParametricType(XxxWrapper.class, type);
        try {
            result = MAPPER.readerFor(readType)
                    .readValue(doc);
        } catch (Exception e) {
            fail("Failed with input of:\n"+doc
                    +"\nproblem: `"+e.getClass().getName()+"`: "+e.getMessage());
        }
        assertEquals(input.value, result.value);
    }
}
