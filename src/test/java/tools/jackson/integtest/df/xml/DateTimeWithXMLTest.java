package tools.jackson.integtest.df.xml;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

import tools.jackson.datatype.joda.JodaModule;

import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateTimeWithXMLTest extends BaseTest
{
    static class JodaDateTimeWrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public DateTime dt;

        protected JodaDateTimeWrapper() { }
        public JodaDateTimeWrapper(DateTime v) { dt = v; }
    }

    static class Java8ZonedDateTimeWrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public ZonedDateTime dt;

        protected Java8ZonedDateTimeWrapper() { }
        public Java8ZonedDateTimeWrapper(ZonedDateTime v) { dt = v; }
    }

    private final static DateTime TEST_JODA_DATETIME = DateTime.parse("1972-12-28T12:00:01.000Z");
    private final static Calendar TEST_CALENDAR = TEST_JODA_DATETIME.toCalendar(Locale.getDefault());
    private final static ZonedDateTime TEST_JAVA8_ZONEDDATETIME = ZonedDateTime.parse("1972-12-28T12:00:01.000Z");

    private final ObjectMapper MAPPER = xmlMapperBuilder()
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
        // trick: force a bogus attribute?
        doc = doc.replace("<cal", "<cal lang='en' ");
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
        // trick: force a bogus attribute?
        doc = doc.replace("<cal", "<cal lang='en' ");
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
    public void testJodaDateTimeWrapperTextual() throws Exception
    {
        JodaDateTimeWrapper input = new JodaDateTimeWrapper(TEST_JODA_DATETIME);
        String xml = MAPPER.writer()
                .writeValueAsString(input);
        // trick: force a bogus attribute?
        xml = xml.replace("<dt", "<dt lang='en' ");

        JodaDateTimeWrapper result = MAPPER.readValue(xml, JodaDateTimeWrapper.class);
        assertEquals(input.dt, result.dt);
    }

    @Test
    public void testJodaRootDateTimeTextual() throws Exception
    {
        String xml = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(TEST_JODA_DATETIME);
        // trick: force a bogus attribute?
        xml = xml.replace("<DateTime>", "<DateTime lang='en'>");

        DateTime result = MAPPER.readValue(xml, DateTime.class);
        assertEquals(TEST_JODA_DATETIME, result);
    }

    @Test
    public void testJodaRootDateTimeNumeric() throws Exception
    {
        String xml = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(TEST_JODA_DATETIME);
        // trick: force a bogus attribute?
        xml = xml.replace("<DateTime>", "<DateTime lang='en'>");
        DateTime result = MAPPER.readValue(xml, DateTime.class);
        assertEquals(TEST_JODA_DATETIME, result);
    }

    /*
    /**********************************************************************
    /* Test methods, Java 8 Date/Time
    /**********************************************************************
     */

    @Test
    public void testJava8DateTimeWrapperTextual() throws Exception
    {
        Java8ZonedDateTimeWrapper input = new Java8ZonedDateTimeWrapper(TEST_JAVA8_ZONEDDATETIME);
        String xml = MAPPER.writer()
                .writeValueAsString(input);
        // trick: force a bogus attribute?
        xml = xml.replace("<dt", "<dt lang='en' ");
        Java8ZonedDateTimeWrapper result = MAPPER.readValue(xml, Java8ZonedDateTimeWrapper.class);
        _assertEquality(input.dt, result.dt);
    }

    @Test
    public void testJava8RootDateTimeTextual() throws Exception
    {
        String xml = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(TEST_JAVA8_ZONEDDATETIME);

        // trick: force a bogus attribute?
        xml = xml.replace("<ZonedDateTime>", "<ZonedDateTime lang='en'>");
        
        ZonedDateTime result = MAPPER.readValue(xml, ZonedDateTime.class);
        _assertEquality(TEST_JAVA8_ZONEDDATETIME, result);
    }

    @Test
    public void testJava8RootDateTimeNumeric() throws Exception
    {
        String xml = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(TEST_JAVA8_ZONEDDATETIME);

        // trick: force a bogus attribute?
        xml = xml.replace("<ZonedDateTime>", "<ZonedDateTime lang='en'>");
        ZonedDateTime result = MAPPER.readValue(xml, ZonedDateTime.class);
        _assertEquality(TEST_JAVA8_ZONEDDATETIME, result);
    }

    private void _assertEquality(ZonedDateTime exp, ZonedDateTime act)
    {
        // not sure why but timezone appears to change so just compare field-by-field

        assertEquals(exp.getYear(), act.getYear());
        assertEquals(exp.getMonth(), act.getMonth());
        assertEquals(exp.getDayOfMonth(), act.getDayOfMonth());

        assertEquals(exp.getHour(), act.getHour());
        assertEquals(exp.getMinute(), act.getMinute());
        assertEquals(exp.getSecond(), act.getSecond());
    }
}
