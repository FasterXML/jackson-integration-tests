package tools.jackson.integtest.dt.datetime;

import java.time.Duration;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;

import tools.jackson.integtest.BaseTest;

// Related to [databind#2795], but also [databind#2683] -- basically
// mix-ins for java.time.* should still work with Jackson 2.12.
public class MixinForJava8DateTimesTest extends BaseTest
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    static class UnmodifiableCollectionMixin {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public UnmodifiableCollectionMixin(final Collection<?> collection) { }
    }

    @JsonAutoDetect(
            fieldVisibility = JsonAutoDetect.Visibility.NONE,
            getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE,
            setterVisibility = JsonAutoDetect.Visibility.NONE,
            creatorVisibility = JsonAutoDetect.Visibility.NONE
    )
    static abstract class DurationMixin {
        @JsonCreator
        public static void ofSeconds(@JsonProperty("seconds") long seconds,
                @JsonProperty("nano") long nanoAdjustment) {
        }

        @JsonGetter("seconds")
        public abstract long getSeconds();

        @JsonGetter("nano")
        public abstract int getNano();
    }

    public void testMixinWithJava8DateTimeSer() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .addMixIn(Duration.class, DurationMixin.class)
                .build();
        Duration input = Duration.ofSeconds(123L);
        String json = mapper.writeValueAsString(input);

        Map<?,?> map = mapper.readValue(json, Map.class);
        if (map.size() != 2) {
            fail("Incorrect serialization: "+json);
        }
    }

    public void testMixinWithJava8DateTimeDeser() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .addMixIn(Duration.class, DurationMixin.class)
                .build();
        Duration result = mapper.readValue(a2q("{'seconds':200, 'nano' : 13}"),
                Duration.class);
        assertNotNull(result);
    }
}
