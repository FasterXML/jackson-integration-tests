package com.fasterxml.jackson.integtest.df.basic;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.integtest.BaseTest;

public class AvroTest extends BaseTest
{
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = avroMapper();

        // !!! 23-Feb-2016, tatu: Trivial to avoid using any Schema
        assertNotNull(mapper);
    }
}
