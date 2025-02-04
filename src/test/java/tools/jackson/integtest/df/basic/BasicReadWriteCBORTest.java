package tools.jackson.integtest.df.basic;

import java.util.*;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicReadWriteCBORTest extends BaseTest
{
    @Test
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = cborMapper();
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("key", Integer.valueOf(1972));
        input.put("key2", true);
        input.put("key3", "abc");

        byte[] encoded = mapper.writeValueAsBytes(input);
        Map<?,?> output = mapper.readValue(encoded, Map.class);

        assertEquals(input, output);
    }
}
