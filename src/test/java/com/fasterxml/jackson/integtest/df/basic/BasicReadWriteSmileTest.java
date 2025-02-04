package com.fasterxml.jackson.integtest.df.basic;

import java.util.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicReadWriteSmileTest extends BaseTest
{
    @Test
    public void testSimple() throws Exception
    {
        final ObjectMapper mapper = smileMapper();
        final Map<String, Object> input = Collections.singletonMap("key", Integer.valueOf(1972));
        byte[] smile = mapper.writeValueAsBytes(input);

        Map<?,?> output = mapper.readValue(smile, Map.class);
        assertEquals(input, output);
    }
}
