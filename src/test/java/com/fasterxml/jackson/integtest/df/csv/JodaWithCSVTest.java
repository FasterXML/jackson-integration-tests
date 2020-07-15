package com.fasterxml.jackson.integtest.df.csv;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import com.fasterxml.jackson.integtest.BaseTest;

public class JodaWithCSVTest extends BaseTest
{
    static class DateTimeWrapper {
        public DateTime dt;

        protected DateTimeWrapper() { }
        public DateTimeWrapper(DateTime v) { dt = v; }
    }

    private final static DateTime TEST_DATETIME = DateTime.parse("1972-12-28T12:00:01.000Z");

    private final CsvMapper MAPPER = csvMapperBuilder()
            .addModule(new JodaModule())
            .build();

    private final CsvSchema WRAPPER_SCHEMA = MAPPER.schemaFor(DateTimeWrapper.class);
    
    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    public void testJodaDateTimeTextual() throws Exception
    {
        DateTimeWrapper input = new DateTimeWrapper(TEST_DATETIME);
        String doc = MAPPER.writer(WRAPPER_SCHEMA)
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        DateTimeWrapper result = MAPPER.readerFor(DateTimeWrapper.class)
                .with(WRAPPER_SCHEMA)
                .readValue(doc);
        assertEquals(input.dt, result.dt);
    }

    // 06-Jul-2020, tatu: Does not yet work, need some TLC on Joda
    /*
    public void testJodaDateTimeNumeric() throws Exception
    {
        String doc = MAPPER.writer(WRAPPER_SCHEMA)
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(TEST_DATETIME);
        DateTime result = null;
        try {
            result = MAPPER.readValue(doc, DateTime.class);
        } catch (Exception e) {
            fail("Failed with `"+e.getClass().getName()+"`, with input of:\n"+doc);
        }
        assertEquals(TEST_DATETIME, result);
    }
    */
}
