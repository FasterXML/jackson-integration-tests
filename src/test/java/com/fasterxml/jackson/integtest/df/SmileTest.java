package com.fasterxml.jackson.integtest.df;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.integtest.BaseTest;

public class SmileTest extends BaseTest
{
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = smileMapper();
        byte[] smile = mapper.writeValueAsBytes(new HashMap<String,String>());

        Map<?,?> output = mapper.readValue(smile, Map.class);
        assertEquals(0, output.size());
    }
}
