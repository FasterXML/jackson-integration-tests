package com.fasterxml.jackson.integtest.df.basic;

import com.fasterxml.jackson.dataformat.protobuf.ProtobufMapper;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchema;
import com.fasterxml.jackson.integtest.BaseTest;

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
