package com.fasterxml.jackson.integtest.df.basic;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BasicReadWriteAvroTest extends BaseTest
{
    @Test
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = avroMapper();

        // !!! 23-Feb-2016, tatu: Trivial to avoid using any Schema
        assertNotNull(mapper);
    }
}
