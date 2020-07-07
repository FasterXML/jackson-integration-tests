package com.fasterxml.jackson.integtest.df;

import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.integtest.BaseTest;

public class CSVTest extends BaseTest
{
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = csvMapper();

        // !!! 23-Feb-2016, tatu: Trivial to avoid using any Schema
        assertNotNull(mapper);
    }
}
