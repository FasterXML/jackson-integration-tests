package tools.jackson.integtest.dt.datetime;

import java.time.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

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
            // Java Time module part of Jackson 3 databind
            .build();

    /*
    /**********************************************************************
    /* Basic usage
    /**********************************************************************
     */

    @Test
    public void testZonedDateTimeRead() throws Exception
    {
        ZonedDateTime read = MAPPER.readValue(q("2000-01-01T12:00Z"), ZonedDateTime.class);
        assertEquals(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                read);
    }

    @Test
    public void testZonedDateTimeWrite() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        // with no explicit timezone specification, timezone we pass will be used so
        String value = MAPPER.writer()
                .without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals(q("1969-12-31T18:00:00-06:00"), value);
    }

    // from [databind#2983]
    @Test
    public void testDurationViaConstructor() throws Exception
    {
        DurationWrapper2983 result = MAPPER.readValue("{ \"duration\": \"PT5M\" }",
                DurationWrapper2983.class);
        assertNotNull(result);
        assertNotNull(result.d);
        assertEquals("PT5M", result.d.toString());
    }
}
