package tools.jackson.integtest.df.basic;

import java.util.*;

import tools.jackson.databind.ObjectMapper;

import tools.jackson.integtest.BaseTest;

public class BasicReadWriteYAMLTest extends BaseTest
{
    @SuppressWarnings("serial")
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = yamlMapper();
        byte[] data = mapper.writeValueAsBytes(new HashMap<String,String>() { });

        Map<?,?> output = mapper.readValue(data, Map.class);
        assertEquals(0, output.size());
    }
}
