package com.fasterxml.jackson.failing;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.POJONode;

import com.fasterxml.jackson.integtest.BaseTest;

import org.msgpack.jackson.dataformat.MessagePackExtensionType;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MessagePackExtensionTest extends BaseTest
{
    private final ObjectMapper MAPPER = jsonMapper();

    /**
     * Reproducing
     *
     * https://github.com/FasterXML/jackson-databind/issues/2824
     */
    @Test
    public void testPOJONodeWithMessagePackExtension() throws Exception
    {
        final ArrayNode node = MAPPER.createArrayNode();
        node.add(new POJONode(new MessagePackExtensionType((byte) 0, new byte[]{2})));
        String json = node.toString();
        assertNotNull(json);
    }
}
