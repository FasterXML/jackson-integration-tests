package com.fasterxml.jackson.integtest.dt.datetime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.integtest.BaseTest;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple tests to see that Joda module works too.
 */
public class Java8DateTimeTest extends BaseTest
{
    private static final ZoneId Z1 = ZoneId.of("America/Chicago");

//    static class TimeWrapper {
//        public ZonedDateTime time;
//    }

    final private ObjectMapper MAPPER = jsonMapperBuilder()
            .addModule(new JavaTimeModule())
            .build();

    /*
    /**********************************************************************
    /* Failing tests: attempts to use without module should fail (2.12+)
    /**********************************************************************
     */

    public void testFailWithoutDateTimeModule() throws Exception
    {
        final ObjectMapper vanilla = jsonMapper();

        try {
            vanilla.writeValueAsString(ZonedDateTime.now());
            fail("Should fail to serialize without Joda module");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "Java 8 date/time type `java.time.ZonedDateTime` not supported by default");
        }

        try {
            vanilla.readValue("{ }", ZonedDateTime.class);
            fail("Should fail to deserialize without Joda module");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "Java 8 date/time type `java.time.ZonedDateTime` not supported by default");
        }
    }

    /*
    /**********************************************************************
    /* Basic usage
    /**********************************************************************
     */

    public void testZonedDateTimeRead() throws Exception
    {
        assertEquals("The value is not correct.",
                ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")),
                MAPPER.readValue(q("2000-01-01T12:00Z"), ZonedDateTime.class));
    }

    public void testZonedDateTimeWrite() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        String value = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.",
                q("1970-01-01T00:00:00Z"), value);
    }
}
