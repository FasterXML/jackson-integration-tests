package tools.jackson.integtest.df.basic;

import java.util.*;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicReadWriteCBORTest extends BaseTest
{
    @SuppressWarnings("serial")
    @Test
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = cborMapper();
        byte[] data = mapper.writeValueAsBytes(new HashMap<String,String>() { });

        Map<?,?> output = mapper.readValue(data, Map.class);
        assertEquals(0, output.size());
    }
}
