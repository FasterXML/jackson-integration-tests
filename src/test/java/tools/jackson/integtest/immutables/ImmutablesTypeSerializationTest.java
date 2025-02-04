package tools.jackson.integtest.immutables;

import org.junit.jupiter.api.Test;

import org.immutables.value.Value;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import tools.jackson.integtest.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

public class ImmutablesTypeSerializationTest
    extends BaseTest
{
    @Value.Immutable
    @JsonDeserialize(as = ImmutableAccount.class)
    @JsonSerialize(as = ImmutableAccount.class)
    public interface Account {
        Long getId();
        String getName();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableKey.class)
    @JsonSerialize(as = ImmutableKey.class)
    public interface Key<T> {
        T getId();
    }

    @Value.Immutable
    @JsonDeserialize(as = ImmutableEntry.class)
    @JsonSerialize(as = ImmutableEntry.class)
    public interface Entry<K, V> {
        K getKey();
        V getValue();
    }

    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = jsonMapper();

    @Test
    public void testImmutablesSimpleDeserialization() throws Exception {
        Account expected = ImmutableAccount.builder()
                .id(1L)
                .name("foo")
                .build();
        Account actual = MAPPER.readValue("{\"id\": 1,\"name\":\"foo\"}", Account.class);
        assertEquals(expected, actual);
    }

    @Test
    public void testImmutablesSimpleRoundTrip() throws Exception {
        Account original = ImmutableAccount.builder()
                .id(1L)
                .name("foo")
                .build();
        String json = MAPPER.writeValueAsString(original);
        Account deserialized = MAPPER.readValue(json, Account.class);
        assertEquals(original, deserialized);
    }

    @Test
    public void testImmutablesSimpleGenericDeserialization() throws Exception {
        Key<Account> expected = ImmutableKey.<Account>builder()
                .id(ImmutableAccount.builder()
                        .id(1L)
                        .name("foo")
                        .build())
                .build();
        Key<Account> actual = MAPPER.readValue(
                "{\"id\":{\"id\": 1,\"name\":\"foo\"}}",
                new TypeReference<Key<Account>>() {});
        assertEquals(expected, actual);
    }

    @Test
    public void testImmutablesSimpleGenericRoundTrip() throws Exception {
        Key<Account> original = ImmutableKey.<Account>builder()
                .id(ImmutableAccount.builder()
                        .id(1L)
                        .name("foo")
                        .build())
                .build();
        String json = MAPPER.writeValueAsString(original);
        Key<Account> deserialized = MAPPER.readValue(json, new TypeReference<Key<Account>>() {});
        assertEquals(original, deserialized);
    }

    @Test
    public void testImmutablesMultipleTypeParametersDeserialization() throws Exception {
        Entry<Key<Account>, Account> expected = ImmutableEntry.<Key<Account>, Account>builder()
                .key(ImmutableKey.<Account>builder()
                        .id(ImmutableAccount.builder()
                                .id(1L)
                                .name("foo")
                                .build())
                        .build())
                .value(ImmutableAccount.builder()
                        .id(2L)
                        .name("bar")
                        .build())
                .build();
        Entry<Key<Account>, Account> actual = MAPPER.readValue(
                "{\"key\":{\"id\":{\"id\": 1,\"name\":\"foo\"}},\"value\":{\"id\":2,\"name\":\"bar\"}}",
                new TypeReference<Entry<Key<Account>, Account>>() {});
        assertEquals(expected, actual);
    }

    @Test
    public void testImmutablesMultipleTypeParametersRoundTrip() throws Exception {
        Entry<Key<Account>, Account> original = ImmutableEntry.<Key<Account>, Account>builder()
                .key(ImmutableKey.<Account>builder()
                        .id(ImmutableAccount.builder()
                                .id(1L)
                                .name("foo")
                                .build())
                        .build())
                .value(ImmutableAccount.builder()
                        .id(2L)
                        .name("bar")
                        .build())
                .build();
        String json = MAPPER.writeValueAsString(original);
        Entry<Key<Account>, Account> deserialized = MAPPER.readValue(
                json, new TypeReference<Entry<Key<Account>, Account>>() {});
        assertEquals(original, deserialized);
    }
}
