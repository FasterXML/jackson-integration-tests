package tools.jackson.failing;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.POJONode;

import tools.jackson.integtest.BaseTest;

import org.msgpack.jackson.dataformat.MessagePackExtensionType;

public class MessagePackExtensionTest extends BaseTest
{
    private final ObjectMapper MAPPER = jsonMapper();

    /**
     * Reproducing
     *
     * https://github.com/FasterXML/jackson-databind/issues/2824
     */
    public void testPOJONodeWithMessagePackExtension() throws Exception
    {
        final ArrayNode node = MAPPER.createArrayNode();
        node.add(new POJONode(new MessagePackExtensionType((byte) 0, new byte[]{2})));
        String json = node.toString();
        assertNotNull(json);
    }
}
