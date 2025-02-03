package tools.jackson.integtest.df.basic;

import java.util.*;

import tools.jackson.databind.ObjectMapper;

import tools.jackson.integtest.BaseTest;

public class BasicReadWriteSmileTest extends BaseTest
{
    public void testSimple() throws Exception
    {
        final ObjectMapper mapper = smileMapper();
        final Map<String, Object> input = Collections.singletonMap("key", Integer.valueOf(1972));
        byte[] smile = mapper.writeValueAsBytes(input);

        Map<?,?> output = mapper.readValue(smile, Map.class);
        assertEquals(input, output);
    }
}
