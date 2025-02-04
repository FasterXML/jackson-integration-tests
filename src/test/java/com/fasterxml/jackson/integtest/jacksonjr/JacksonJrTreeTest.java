package com.fasterxml.jackson.integtest.jacksonjr;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.TreeNode;

import com.fasterxml.jackson.integtest.BaseTest;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.stree.JrSimpleTreeExtension;
import com.fasterxml.jackson.jr.stree.JrsArray;
import com.fasterxml.jackson.jr.stree.JrsObject;

import static org.junit.jupiter.api.Assertions.*;

// Copied from `ReadViaJSONTest` of jackson-jr/jr-stree
public class JacksonJrTreeTest extends BaseTest
{
    // 23-Jul-2020, tatu: Use the new mechanism for registering
    private final JSON JSON_WITH_TREE = JSON.builder()
            .register(new JrSimpleTreeExtension())
        .build();

    @Test
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

    @Test
    public void testSimpleMap() throws Exception
    {
        final String INPUT = "{\"a\":1,\"b\":true,\"c\":3}";
        TreeNode node = JSON_WITH_TREE.treeFrom(INPUT);
        assertTrue(node instanceof JrsObject);
        assertEquals(3, node.size());

        assertEquals(INPUT, JSON_WITH_TREE.asString(node));
    }

}
