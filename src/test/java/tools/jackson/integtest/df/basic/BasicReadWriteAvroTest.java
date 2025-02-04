package tools.jackson.integtest.df.basic;

import org.junit.jupiter.api.Test;

import tools.jackson.dataformat.avro.AvroMapper;
import tools.jackson.dataformat.avro.AvroSchema;
import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicReadWriteAvroTest extends BaseTest
{
    @Test
    public void testBasicReadWrite() throws Exception
    {
        AvroMapper mapper = avroMapper();

        AvroSchema schema = mapper.schemaFor(PointXYZ.class);

        PointXYZ input = new PointXYZ(1, 2, 3);
        byte[] encoded = mapper.writer(schema).writeValueAsBytes(input);

        PointXYZ output = mapper.readerFor(PointXYZ.class)
                .with(schema).readValue(encoded);

        assertEquals(input, output);
    }
}
