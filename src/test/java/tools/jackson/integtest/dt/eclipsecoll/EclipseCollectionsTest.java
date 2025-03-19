package tools.jackson.integtest.dt.eclipsecoll;

import org.junit.jupiter.api.Test;
import org.eclipse.collections.api.list.primitive.*;
import org.eclipse.collections.impl.factory.primitive.*;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.eclipsecollections.EclipseCollectionsModule;

import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class EclipseCollectionsTest extends BaseTest
{
    final private ObjectMapper MAPPER = jsonMapperBuilder()
            .addModule(new EclipseCollectionsModule())
            .build();

    @Test
    public void immutableList() throws Exception {
        _testCollection(BooleanLists.immutable.of(true, false, true), "[true, false, true]", ImmutableBooleanList.class);
        _testCollection(ByteLists.immutable.of((byte) 1, (byte) 2, (byte) 3), "[1, 2, 3]", ImmutableByteList.class);
        _testCollection(ShortLists.immutable.of((short) 1, (short) 2, (short) 3), "[1, 2, 3]", ImmutableShortList.class);
        _testCollection(CharLists.immutable.of('a', 'b', 'c'), "\"abc\"", ImmutableCharList.class);
        _testCollection(IntLists.immutable.of(1, 2, 3), "[1, 2, 3]", ImmutableIntList.class);
        _testCollection(FloatLists.immutable.of(1.1F, 2.3F, 3.5F), "[1.1, 2.3, 3.5]", ImmutableFloatList.class);
        _testCollection(LongLists.immutable.of(1, 2, 3), "[1, 2, 3]", ImmutableLongList.class);
        _testCollection(DoubleLists.immutable.of(1.1, 2.3, 3.5), "[1.1, 2.3, 3.5]", ImmutableDoubleList.class);
    }

    protected final <T> void _testCollection(T expected, String json, Class<?>... types)
        throws Exception
    {
        for (Class<?> type : types) {
            Object value = MAPPER.readValue(json, type);
            assertEquals(expected, value);
            assertInstanceOf(type, value);
        }
    }

    @Test
    public void serializePrimitive() throws Exception {
        assertEquals("[true,false,true]", MAPPER.writeValueAsString(
                BooleanLists.immutable.of(true, false, true)));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(
                ShortLists.immutable.of((short) 1, (short) 2, (short) 3)));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(
                IntLists.immutable.of(1, 2, 3)));
        assertEquals("[1.1,2.3,3.5]", MAPPER.writeValueAsString(
                FloatLists.immutable.of(1.1F, 2.3F, 3.5F)));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(
                LongLists.immutable.of(1, 2, 3)));
        assertEquals("[1.1,2.3,3.5]", MAPPER.writeValueAsString(
                DoubleLists.immutable.of(1.1, 2.3, 3.5)));
    }
}
