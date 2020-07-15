package com.fasterxml.jackson.integtest.df.basic;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.integtest.BaseTest;

public class PropertiesTest extends BaseTest
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
