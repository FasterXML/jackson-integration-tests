package com.fasterxml.jackson.integtest.dt.javaxmoney;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.javax.money.JavaxMoneyModule;

import com.fasterxml.jackson.integtest.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.money.CurrencyUnit;

import org.javamoney.moneta.CurrencyUnitBuilder;

public class JavaxMoneyTest extends BaseTest
{
    final private ObjectMapper MAPPER = jsonMapperBuilder()
            .addModule(new JavaxMoneyModule())
            .build();

    @Test
    public void deserialization() throws Exception {
        final CurrencyUnit actual = MAPPER.readValue("\"EUR\"", CurrencyUnit.class);
        final CurrencyUnit expected = CurrencyUnitBuilder.of("EUR", "default").build();
        assertEquals(expected, actual);
    }

    @Test
    public void serialization() throws Exception {
        final CurrencyUnit currency = CurrencyUnitBuilder.of("EUR", "default").build();

        final String actual = MAPPER.writeValueAsString(currency);

        assertThat(actual).isEqualTo(q("EUR"));
    }
}
