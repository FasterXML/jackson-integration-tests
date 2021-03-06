package com.fasterxml.jackson.integtest.dt.jsonp;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import com.fasterxml.jackson.integtest.BaseTest;

public class JavaxOldJsonpTest extends BaseTest
{
    public void testSimpleDeser() throws Exception
    {
        final ObjectMapper mapper = jsonMapperBuilder()
                .addModule(new JSR353Module())
                .build();

        final String JSON = "[1,true,\"foo\"]";
        JsonValue v = mapper.readValue(JSON, JsonValue.class);
        assertTrue(v instanceof JsonArray);
        JsonArray a = (JsonArray) v;
        assertEquals(3, a.size());
        assertTrue(a.get(0) instanceof JsonNumber);
        assertSame(JsonValue.TRUE, a.get(1));
        assertTrue(a.get(2) instanceof JsonString);
    }
}
