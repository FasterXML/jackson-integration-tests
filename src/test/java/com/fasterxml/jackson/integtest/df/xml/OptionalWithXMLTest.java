package com.fasterxml.jackson.integtest.df.xml;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.integtest.BaseTest;

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
            .enable(ToXmlGenerator.Feature.WRITE_NULLS_AS_XSI_NIL)
            .addModule(new Jdk8Module())
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
