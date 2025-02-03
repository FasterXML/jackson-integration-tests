package tools.jackson.integtest.jacksonjr;

import tools.jackson.core.TreeNode;
import tools.jackson.jr.ob.JSON;
import tools.jackson.jr.stree.JrSimpleTreeExtension;
import tools.jackson.jr.stree.JrsArray;
import tools.jackson.jr.stree.JrsObject;

import tools.jackson.integtest.BaseTest;

// Copied from `ReadViaJSONTest` of jackson-jr/jr-stree
public class JacksonJrTreeTest extends BaseTest
{
    // 23-Jul-2020, tatu: Use the new mechanism for registering
    private final JSON JSON_WITH_TREE = JSON.builder()
            .register(new JrSimpleTreeExtension())
        .build();

    public void testSimpleList() throws Exception
    {
        final String INPUT = "[true,\"abc\"]";
        // and then through jr-objects:
        TreeNode node = JSON_WITH_TREE.treeFrom(INPUT);

        assertTrue(node instanceof JrsArray);
        assertEquals(2, node.size());

        // actually, verify with write...
        assertEquals(INPUT, JSON_WITH_TREE.asString(node));
    }

    public void testSimpleMap() throws Exception
    {
        final String INPUT = "{\"a\":1,\"b\":true,\"c\":3}";
        TreeNode node = JSON_WITH_TREE.treeFrom(INPUT);
        assertTrue(node instanceof JrsObject);
        assertEquals(3, node.size());

        assertEquals(INPUT, JSON_WITH_TREE.asString(node));
    }

}
