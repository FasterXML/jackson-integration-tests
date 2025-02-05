package tools.jackson.integtest.df.basic;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

public class BasicReadWriteTomlTest extends BaseTest
{
    @Test
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = tomlMapper();

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("key", Integer.valueOf(1972));
        input.put("key2", true);
        input.put("key3", "abc");

        String doc = mapper.writeValueAsString(input);
        Map<?,?> output = mapper.readValue(doc, Map.class);
        assertEquals(input, output);
    }
}
