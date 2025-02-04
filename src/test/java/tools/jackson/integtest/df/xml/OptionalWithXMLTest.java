package tools.jackson.integtest.df.xml;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.xml.XmlWriteFeature;

import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptionalWithXMLTest extends BaseTest
{
    // For [modules-java8#280]
    static class IntWrapper {
        public int a;

        public IntWrapper() {
            a = 1;
        }
    }

    static class OptionalBean {
        public Optional<IntWrapper> opt = Optional.empty();
    }
    
    private final ObjectMapper MAPPER = xmlMapperBuilder()
            .enable(XmlWriteFeature.WRITE_NULLS_AS_XSI_NIL)
            .build();

    // For [modules-java8#280]: working case when enabling `xsi:nil` writes:
    @Test
    public void testOptionalWithXMLWriteAndRead() throws Exception
    {
        String doc = MAPPER.writeValueAsString(new OptionalBean());
                 
        OptionalBean result = MAPPER.readValue(doc, OptionalBean.class);
        assertEquals(Optional.empty(), result.opt);
    }

    // For [modules-java8#280]: working case with `xsi:nil` as input
    @Test
    public void testOptionalWithXMLReadWithNil() throws Exception
    {
        final String doc = "<OptionalBean><opt xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/></OptionalBean>";
                 
        OptionalBean result = MAPPER.readValue(doc, OptionalBean.class);
        assertEquals(Optional.empty(), result.opt);
    }
}
