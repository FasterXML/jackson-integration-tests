package com.fasterxml.jackson.integtest.jacksonjr;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.fasterxml.jackson.jr.ob.JSON.Feature;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

public class JacksonJrBasicTest extends BaseTest
{
    static class TestBean {
        protected int x;
        protected NameBean name;
        
        public void setName(NameBean n) { name = n; }
        public void setX(int x) { this.x = x; }

        public int getX() { return x; }
        public NameBean getName() { return name; }
    }

    protected static class NameBean {
        protected String first, last;

        public NameBean() { }
        public NameBean(String f, String l) {
            first = f;
            last = l;
        }

        public String getFirst() { return first; }
        public String getLast() { return last; }

        public void setFirst(String n) { first = n; }
        public void setLast(String n) { last = n; }
    }

    /*
    /**********************************************************************
    /* Tests for arrays
    /**********************************************************************
     */

    @Test
    public void testByteArray() throws Exception {
        byte[] result = JSON.std.beanFrom(byte[].class, q("YWJj"));
        assertEquals("abc", new String(result, "UTF-8"));
    }

    @Test
    public void testCharArray() throws Exception {
        char[] result = JSON.std.beanFrom(char[].class, q("abc"));
        assertEquals("abc", new String(result));
    }

    @Test
    public void testSimpleArray() throws Exception
    {
        _testArray("[true,\"abc\",3]", 3);
    }

    @Test
    public void testEmptyArray() throws Exception
    {
        _testArray("[]", 0);
    }

    // separate tests since code path differs
    @Test
    public void testSingleElementArray() throws Exception {
        _testArray("[12]", 1);
    }

    @Test
    public void testSmallArray() throws Exception {
        _testArray("[true,42,\"maybe\"]", 3);
    }

    private void _testArray(String input, int expCount) throws Exception
    {
        Object ob;

        // first: can explicitly request an array:
        ob = JSON.std.arrayFrom(input);
        assertTrue(ob instanceof Object[]);
        assertEquals(expCount, ((Object[]) ob).length);
        assertEquals(input, JSON.std.asString(ob));

        // or, with "List of Any"
        ob = JSON.std
                .arrayOfFrom(Object.class, input);
        assertTrue(ob instanceof Object[]);
        assertEquals(expCount, ((Object[]) ob).length);
        assertEquals(input, JSON.std.asString(ob));

        ob = JSON.std
              .with(JSON.Feature.READ_JSON_ARRAYS_AS_JAVA_ARRAYS)
              .arrayOfFrom(Object.class, input);
        assertTrue(ob instanceof Object[]);
        assertEquals(expCount, ((Object[]) ob).length);
        assertEquals(input, JSON.std.asString(ob));
        
        // or by changing default mapping:
        ob = JSON.std.with(Feature.READ_JSON_ARRAYS_AS_JAVA_ARRAYS).anyFrom(input);
        assertTrue(ob instanceof Object[]);
        assertEquals(expCount, ((Object[]) ob).length);
        assertEquals(input, JSON.std.asString(ob));
    }

    /*
    /**********************************************************************
    /* Tests for POJOs
    /**********************************************************************
     */

    @Test
    public void testSimpleBean() throws Exception
    {
        final String INPUT = a2q("{'name':{'first':'Bob','last':'Burger'},'x':13}");
        TestBean bean = JSON.std.beanFrom(TestBean.class, INPUT);

        assertNotNull(bean);
        assertEquals(13, bean.x);
        assertNotNull(bean.name);
        assertEquals("Bob", bean.name.first);
        assertEquals("Burger", bean.name.last);
    }

    @Test
    public void testUnknownProps() throws Exception
    {
        final String INPUT = a2q("{'first':'Bob','middle':'Eugene', 'last':'Smith'}");

        // First: fine if marked as such
        NameBean name = JSON.std
                .without(JSON.Feature.FAIL_ON_UNKNOWN_BEAN_PROPERTY)
                .beanFrom(NameBean.class, INPUT);
        assertNotNull(name);
        assertEquals("Bob", name.first);
        assertEquals("Smith", name.last);

        // but not if check enabled
        try {
            name = JSON.std
                    .with(JSON.Feature.FAIL_ON_UNKNOWN_BEAN_PROPERTY)
                    .beanFrom(NameBean.class, INPUT);
            fail("Should have thrown exception");
        } catch (JSONObjectException e) {
            verifyException(e, "unrecognized JSON property \"middle\"");
            verifyException(e, "(known properties: [\"first\", \"last\"])");
        }
    }
}
