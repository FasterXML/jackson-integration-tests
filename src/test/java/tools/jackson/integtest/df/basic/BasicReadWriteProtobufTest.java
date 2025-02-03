package tools.jackson.integtest.df.basic;

import tools.jackson.dataformat.protobuf.ProtobufMapper;
import tools.jackson.dataformat.protobuf.schema.ProtobufSchema;

import tools.jackson.integtest.BaseTest;

public class BasicReadWriteProtobufTest extends BaseTest
{
    private final ProtobufMapper MAPPER = protobufMapper();

    public void testSimpleRoundtrip() throws Exception
    {
        ProtobufSchema pointSchema = MAPPER.generateSchemaFor(PointXY.class);
        PointXY input = new PointXY(19, 72);
        byte[] bytes = MAPPER.writer(pointSchema)
                .writeValueAsBytes(input);
        PointXY result = MAPPER.reader(pointSchema)
                .forType(PointXY.class)
                .readValue(bytes);
        assertEquals(input, result);
    }
}
