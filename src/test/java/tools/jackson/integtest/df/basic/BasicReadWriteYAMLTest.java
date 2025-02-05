package tools.jackson.integtest.df.basic;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicReadWriteYAMLTest extends BaseTest
{
    @Test
    public void testSimple() throws Exception
    {
        ObjectMapper mapper = yamlMapper();
        final PointXYZ input = new PointXYZ(1, 2, 3);
        byte[] data = mapper.writeValueAsBytes(input);

        PointXYZ output = mapper.readValue(data, PointXYZ.class);
        assertEquals(input, output);
    }
}
