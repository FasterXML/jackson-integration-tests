package tools.jackson.integtest.dt.guava;

import java.util.Iterator;

import com.google.common.collect.ImmutableSortedSet;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.guava.GuavaModule;

import tools.jackson.integtest.BaseTest;

public class GuavaTest extends BaseTest
{
    final private ObjectMapper MAPPER = jsonMapperBuilder()
            .addModule(new GuavaModule())
            .build();

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
