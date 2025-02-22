package com.fasterxml.jackson.integtest.dt.guava;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import com.fasterxml.jackson.integtest.BaseTest;

import com.google.common.collect.ImmutableSortedSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuavaTest extends BaseTest
{
    final private ObjectMapper MAPPER = jsonMapperBuilder()
            .addModule(new GuavaModule())
            .build();

    @Test
    public void testImmutableSortedSet() throws Exception
    {
        ImmutableSortedSet<Integer> set = MAPPER.readValue("[5,1,2]",
                new TypeReference<ImmutableSortedSet<Integer>>() { });
        assertEquals(3, set.size());
        Iterator<Integer> it = set.iterator();
        assertEquals(Integer.valueOf(1), it.next());
        assertEquals(Integer.valueOf(2), it.next());
        assertEquals(Integer.valueOf(5), it.next());

        String json = MAPPER.writeValueAsString(set);
        assertEquals("[1,2,5]", json);
    }
}
