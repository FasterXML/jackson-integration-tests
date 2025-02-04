package tools.jackson.integtest.dt.jsonp;

import org.junit.jupiter.api.Test;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

import tools.jackson.integtest.BaseTest;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.jsonp.JSONPModule;

import static org.junit.jupiter.api.Assertions.*;

// 05-Mar-2021, tatu: Bloody fudging spit. Javax/Jakarta transition is
//  a completely and utter mess -- if BOTH implementations in Classpath,
//  NEW implementation FAILS to create provider via SPI. Clown Car At
//  Full Capacity!!!
//
//  So, No Can Haz Test as of now :-( :-( :-(
public class JakartaNewJsonpTest extends BaseTest
{
    @SuppressWarnings("unused")
    @Test
    public void testSimpleDeser() throws Exception
    {
        ObjectMapper mapper = jsonMapperBuilder()
                .addModule(new JSONPModule())
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
