package tools.jackson.integtest.dt.joda;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;

import tools.jackson.datatype.joda.JodaModule;

import tools.jackson.integtest.BaseTest;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Simple tests to see that Joda module works too.
 */
public class JodaTest extends BaseTest
{
    static class TimeWrapper {
        public DateTime time;
    }

    final private ObjectMapper MAPPER = jsonMapperBuilder()
            .addModule(new JodaModule())
            .build();

    /*
    /**********************************************************************
    /* Failing tests: attempts to use without module should fail (2.12+)
    /**********************************************************************
     */

    @Test
    public void testFailWithoutJodaModule() throws Exception
    {
        final ObjectMapper vanilla = jsonMapper();

        try {
            vanilla.writeValueAsString(new DateTime());
            fail("Should fail to serialize without Joda module");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "Joda date/time type `org.joda.time.DateTime` not supported by default");
        }

        try {
            vanilla.readValue("{ }", DateTime.class);
            fail("Should fail to deserialize without Joda module");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "Joda date/time type `org.joda.time.DateTime` not supported by default");
        }
    }

    /*
    /**********************************************************************
    /* Basic usage
    /**********************************************************************
     */

    @Test
    public void testJodaBasicReadWrite() throws Exception
    {
        TimeWrapper input = new TimeWrapper();
        input.time = DateTime.now();
        String json = MAPPER.writeValueAsString(input);
        TimeWrapper out = MAPPER.readValue(json, TimeWrapper.class);
        // no guarantee timezone remains the same, but underlying timestamp should so:
        assertEquals(input.time.getMillis(), out.time.getMillis());
    }
}
