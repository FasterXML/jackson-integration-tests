package com.fasterxml.jackson.integtest.df.basic;

import java.util.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicReadWriteYAMLTest extends BaseTest
{
    @SuppressWarnings("serial")
    @Test
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = yamlMapper();
        byte[] data = mapper.writeValueAsBytes(new HashMap<String,String>() { });

        Map<?,?> output = mapper.readValue(data, Map.class);
        assertEquals(0, output.size());
    }
}
