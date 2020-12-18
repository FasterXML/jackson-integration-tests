package com.fasterxml.jackson.integtest.df.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.integtest.BaseTest;
import com.fasterxml.jackson.integtest.testutil.FiveMinuteUser;

public class XMLWithAfterburnerTest extends BaseTest
{
    private final ObjectMapper AB_MAPPER = xmlMapperBuilder()
            .addModule(afterburnerModule())
            .build();

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    public void testReadWriteXMLWithAfterburner() throws Exception
    {
        // First with simple Point
        {
            PointXYZ input = new PointXYZ(13, -99, 7);
            String xml = AB_MAPPER.writeValueAsString(input);
            PointXYZ result = AB_MAPPER.readValue(xml, PointXYZ.class);

            assertEquals(input.toString(), result.toString());
        }

        // and then bit more complex one
        {
            FiveMinuteUser input = new FiveMinuteUser("Bob", "Burger", true,
                    FiveMinuteUser.Gender.MALE,
                    new byte[] { 1, 2, 3, 4 });
    
            String xml = AB_MAPPER.writeValueAsString(input);
            FiveMinuteUser result = AB_MAPPER.readValue(xml, FiveMinuteUser.class);
    
            assertEquals(input, result);
            assertEquals(input.toString(), result.toString());
        }
    }
}
