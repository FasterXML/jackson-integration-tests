package tools.jackson.integtest.df.basic;

import tools.jackson.databind.ObjectMapper;

import tools.jackson.integtest.BaseTest;

public class BasicReadWriteAvroTest extends BaseTest
{
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = avroMapper();

        // !!! 23-Feb-2016, tatu: Trivial to avoid using any Schema
        assertNotNull(mapper);
    }
}
