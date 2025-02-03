package tools.jackson.integtest.df.basic;

import java.util.*;

import tools.jackson.databind.ObjectMapper;

import tools.jackson.integtest.BaseTest;

public class BasicReadWritePropertiesTest extends BaseTest
{
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = propsMapper();
        final Map<String, Object> input = Collections.singletonMap("key", "value");
        String doc = mapper.writeValueAsString(input);

        Map<?,?> output = mapper.readValue(doc, Map.class);
        assertEquals(input, output);
    }
}
