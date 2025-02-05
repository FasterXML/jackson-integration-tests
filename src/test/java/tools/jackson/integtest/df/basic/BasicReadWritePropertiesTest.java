package tools.jackson.integtest.df.basic;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import tools.jackson.integtest.BaseTest;
import tools.jackson.integtest.BaseTest.PointXYZ;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicReadWritePropertiesTest extends BaseTest
{
    private final ObjectMapper MAPPER = propsMapper();

    @Test
    public void testUntypedReadWrite() throws Exception
    {
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("key", Integer.valueOf(1972));
        input.put("key2", true);
        input.put("key3", "abc");
        String doc = MAPPER.writeValueAsString(input);

        Map<?,?> output = MAPPER.readValue(doc, Map.class);
        // NOTE: with Properties cannot retain type so it's all Strings now
        Map<String, Object> exp = input.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().toString()));
        assertEquals(exp, output);
    }

    @Test
    public void testTypedReadWrite() throws Exception
    {
        PointXYZ input = new PointXYZ(1, 2, 3);
        String doc = MAPPER.writeValueAsString(input);
        PointXYZ output = MAPPER.readerFor(PointXYZ.class).readValue(doc);
        assertEquals(input, output);
    }
}
