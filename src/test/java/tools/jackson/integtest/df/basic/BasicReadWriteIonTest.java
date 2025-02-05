package tools.jackson.integtest.df.basic;

import org.junit.jupiter.api.Test;

import tools.jackson.dataformat.ion.IonFactory;
import tools.jackson.dataformat.ion.IonObjectMapper;
import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

public class BasicReadWriteIonTest extends BaseTest
{
    @Test
    public void testSimple() throws Exception
    {
        IonObjectMapper mapper = ionMapperBuilder(IonFactory.forBinaryWriters()).build();

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("key", Integer.valueOf(1972));
        input.put("key2", true);
        input.put("key3", "abc");
        byte[] encoded = mapper.writeValueAsBytes(input);

        Map<?,?> output = mapper.readValue(encoded, Map.class);
        assertEquals(input, output);
    }
}
