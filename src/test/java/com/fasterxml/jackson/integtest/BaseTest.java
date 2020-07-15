package com.fasterxml.jackson.integtest;

import java.io.*;
import java.util.Arrays;
import java.util.Calendar;

import junit.framework.TestCase;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.avro.AvroFactory;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.ion.IonFactory;
import com.fasterxml.jackson.dataformat.ion.IonObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.protobuf.ProtobufFactory;
import com.fasterxml.jackson.dataformat.protobuf.ProtobufMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.databind.SmileMapper;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public abstract class BaseTest
    extends TestCase
{
    protected enum ABC { A, B, C; }

    @JsonPropertyOrder({ "x", "y" })
    protected static class PointXY {
        public int x, y;

        public PointXY() { }
        public PointXY(int a, int b)
        {
            x = a;
            y = b;
        }
    }

    @JsonPropertyOrder({ "x", "y", "z" })
    protected static class PointXYZ extends PointXY {
        public int z = -13;

        public PointXYZ() { super(); }
        public PointXYZ(int a, int b, int c) {
            super(a, b);
            z = c;
        }
    }

    protected static class CalendarWrapper {
        public Calendar cal;

        protected CalendarWrapper() { }
        public CalendarWrapper(Calendar v) { cal = v; }
    }

    
    /*
    /**********************************************************
    /* Additional assertion methods
    /**********************************************************
     */

    protected void assertToken(JsonToken expToken, JsonToken actToken)
    {
        if (actToken != expToken) {
            fail("Expected token "+expToken+", current token "+actToken);
        }
    }

    protected void assertToken(JsonToken expToken, JsonParser jp)
    {
        assertToken(expToken, jp.getCurrentToken());
    }

    protected void verifyException(Throwable e, String... matches)
    {
        String msg = e.getMessage();
        String lmsg = (msg == null) ? "" : msg.toLowerCase();
        for (String match : matches) {
            String lmatch = match.toLowerCase();
            if (lmsg.indexOf(lmatch) >= 0) {
                return;
            }
        }
        fail("Expected an exception with one of substrings ("+Arrays.asList(matches)+"): got one with message \""+msg+"\"");
    }

    /**
     * Method that gets textual contents of the current token using
     * available methods, and ensures results are consistent, before
     * returning them
     */
    protected String getAndVerifyText(JsonParser jp)
        throws IOException, JsonParseException
    {
        // Ok, let's verify other accessors
        int actLen = jp.getTextLength();
        char[] ch = jp.getTextCharacters();
        String str2 = new String(ch, jp.getTextOffset(), actLen);
        String str = jp.getText();

        if (str.length() !=  actLen) {
            fail("Internal problem (jp.token == "+jp.getCurrentToken()+"): jp.getText().length() ['"+str+"'] == "+str.length()+"; jp.getTextLength() == "+actLen);
        }
        assertEquals("String access via getText(), getTextXxx() must be the same", str, str2);

        return str;
    }

    /*
    /**********************************************************
    /* Mapper construction
    /**********************************************************
     */

    // // First the good old default JSON mapper
    
    protected static JsonMapper.Builder jsonMapperBuilder() {
        return JsonMapper.builder();
    }

    protected static JsonMapper.Builder jsonMapperBuilder(JsonFactory f) {
        return JsonMapper.builder(f);
    }

    protected static JsonMapper jsonMapper() {
        return jsonMapperBuilder().build();
    }

    // // // Then binary format mappers:

    protected static AvroMapper.Builder avroMapperBuilder() {
        return AvroMapper.builder();
    }

    protected static AvroMapper.Builder avroMapperBuilder(AvroFactory f) {
        return AvroMapper.builder(f);
    }

    protected static AvroMapper avroMapper() {
        return avroMapperBuilder().build();
    }

    protected static CBORMapper.Builder cborMapperBuilder() {
        return CBORMapper.builder();
    }

    protected static CBORMapper.Builder cborMapperBuilder(CBORFactory f) {
        return CBORMapper.builder(f);
    }

    protected static CBORMapper cborMapper() {
        return cborMapperBuilder().build();
    }

    protected static IonObjectMapper.Builder ionMapperBuilder() {
        return IonObjectMapper.builder();
    }

    protected static IonObjectMapper.Builder ionMapperBuilder(IonFactory f) {
        return IonObjectMapper.builder(f);
    }

    protected static IonObjectMapper ionMapper() {
        return ionMapperBuilder().build();
    }

    protected static ProtobufMapper.Builder protobufMapperBuilder() {
        return ProtobufMapper.builder();
    }

    protected static ProtobufMapper.Builder protobufMapperBuilder(ProtobufFactory f) {
        return ProtobufMapper.builder(f);
    }

    protected static ProtobufMapper protobufMapper() {
        return protobufMapperBuilder().build();
    }

    protected static SmileMapper.Builder smileMapperBuilder() {
        return SmileMapper.builder();
    }

    protected static SmileMapper.Builder smileMapperBuilder(SmileFactory f) {
        return SmileMapper.builder(f);
    }

    protected static SmileMapper smileMapper() {
        return smileMapperBuilder().build();
    }

    // // // And textual format mappers

    protected static CsvMapper.Builder csvMapperBuilder() {
        return CsvMapper.builder();
    }

    protected static CsvMapper.Builder csvMapperBuilder(CsvFactory f) {
        return CsvMapper.builder(f);
    }

    protected static CsvMapper csvMapper() {
        return csvMapperBuilder().build();
    }

    protected static JavaPropsMapper.Builder propsMapperBuilder() {
        return JavaPropsMapper.builder();
    }

    protected static JavaPropsMapper.Builder propsMapperBuilder(JavaPropsFactory f) {
        return JavaPropsMapper.builder(f);
    }

    protected static JavaPropsMapper propsMapper() {
        return propsMapperBuilder().build();
    }

    protected static XmlMapper.Builder xmlMapperBuilder() {
        return XmlMapper.builder();
    }

    protected static XmlMapper.Builder xmlMapperBuilder(XmlFactory f) {
        return XmlMapper.builder(f);
    }

    protected static XmlMapper xmlMapper() {
        return xmlMapperBuilder().build();
    }

    protected static YAMLMapper.Builder yamlMapperBuilder() {
        return YAMLMapper.builder();
    }

    protected static YAMLMapper.Builder yamlMapperBuilder(YAMLFactory f) {
        return YAMLMapper.builder(f);
    }

    protected static YAMLMapper yamlMapper() {
        return yamlMapperBuilder().build();
    }

    /*
    /**********************************************************
    /* And other helpers
    /**********************************************************
     */

    protected static String q(String str) {
        return '"'+str+'"';
    }

    protected static String a2q(String json) {
        return json.replace("'", "\"");
    }

    protected static String q2a(String json) {
        return json.replace("\"", "'");
    }
}
