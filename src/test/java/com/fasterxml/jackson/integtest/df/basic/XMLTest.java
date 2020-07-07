package com.fasterxml.jackson.integtest.df.basic;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.fasterxml.jackson.integtest.BaseTest;

public class XMLTest extends BaseTest
{
    public void testSimple() throws Exception
    {
        // NOTE: with XML not possible to use generic mapper so:
        ObjectMapper mapper = new XmlMapper();
        byte[] data = mapper.writeValueAsBytes(new PointXYZ(1, 2, 3));

        PointXYZ output = mapper.readValue(data, PointXYZ.class);
        assertNotNull(output);
        assertEquals(1, output.x);
        assertEquals(2, output.y);
        assertEquals(3, output.z);
    }
}
