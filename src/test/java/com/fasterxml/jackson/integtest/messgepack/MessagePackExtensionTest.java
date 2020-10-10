package com.fasterxml.jackson.integtest.messgepack;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.integtest.BaseTest;
import org.msgpack.jackson.dataformat.MessagePackExtensionType;

import java.util.List;

public class MessagePackExtensionTest extends BaseTest {
    public void testPOJONodeWithMessagePackExtensionShouldNotThrowExceptionWhenEvaluate() {
        try {
            final var node = new ArrayNode(new JsonNodeFactory(false), List.of(new POJONode(new MessagePackExtensionType((byte) 0, new byte[]{2}))));
            node.toString();
        } catch (Exception e) {
            fail("POJO Node with MessagePackExtensionType should not throw an exception when evaluate");
        }
    }
}
