package tools.jackson.integtest.dt.guava;

import java.util.Arrays;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import tools.jackson.databind.*;

import tools.jackson.dataformat.avro.AvroMapper;
import tools.jackson.dataformat.avro.AvroSchema;
import tools.jackson.datatype.guava.GuavaModule;

import tools.jackson.integtest.BaseTest;

public class GuavaWithAvroTest extends BaseTest
{
    static class MultimapHolder {
        public Multimap<String, String> map = ArrayListMultimap.create();
    }

    public void testGenerateAvroForMultimap() throws Exception
    {
        final AvroMapper avroMapper = avroMapperBuilder()
                .addModule(new GuavaModule())
                .build();
        final AvroSchema schema = avroMapper.schemaFor(MultimapHolder.class);
        
        ObjectWriter avroWriter = avroMapper.writer(schema);
        MultimapHolder input = new MultimapHolder();
        input.map.put("a", "b");
        input.map.put("a", "x");
        input.map.put("b", "y");
        final byte[] avro = avroWriter.writeValueAsBytes(input);        

        MultimapHolder output = avroMapper.readerFor(MultimapHolder.class)
                .with(schema)
                .readValue(avro);
        assertEquals(3, output.map.size());
        assertEquals(Arrays.asList("b", "x"), output.map.get("a"));
        assertEquals(Arrays.asList("y"), output.map.get("b"));
    }
}
