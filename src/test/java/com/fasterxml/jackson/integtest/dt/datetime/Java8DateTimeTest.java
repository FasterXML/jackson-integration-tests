package com.fasterxml.jackson.integtest.dt.datetime;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.integtest.BaseTest;

/**
 * Simple tests to see that Joda module works too.
 */
public class Java8DateTimeTest extends BaseTest
{
    private static final ZoneId Z1 = ZoneId.of("America/Chicago");

    // from [databind#2983]
    static class DurationWrapper2983 {
        Duration d;

        DurationWrapper2983(@JsonProperty("duration") Duration duration) {
            d = duration;
        }
    }

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
        ZonedDateTime read = MAPPER.readValue(q("2000-01-01T12:00Z"), ZonedDateTime.class);
        assertEquals("The value is not correct.",
                ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                read);
    }

    public void testZonedDateTimeWrite() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        // with no explicit timezone specification, timezone we pass will be used so
        String value = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.",
                q("1969-12-31T18:00:00-06:00"), value);
    }

    // from [databind#2983]
    public void testDurationViaConstructor() throws Exception
    {
        DurationWrapper2983 result = MAPPER.readValue("{ \"duration\": \"PT5M\" }",
                DurationWrapper2983.class);
        assertNotNull(result);
        assertNotNull(result.d);
        assertEquals("PT5M", result.d.toString());
    }
}
