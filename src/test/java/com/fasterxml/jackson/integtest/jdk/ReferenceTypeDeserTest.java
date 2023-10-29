package com.fasterxml.jackson.integtest.jdk;

import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.integtest.BaseTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for deserializing "reference types"; Java 8 {@link java.util.Optional},
 * {@code AtomicReference}, Guava {@code Option}, esp. regarding
 * handling via Creators.
 */
public class ReferenceTypeDeserTest extends BaseTest
{
    private final static String JSON_FROM_ABSENT = a2q("{ }");
    private final static String JSON_FROM_NULL = a2q("{'value':null}");

    private final static AtomicReference<?> ATOMIC_REFERENCE_EMPTY
        = new AtomicReference<>();
    private final static com.google.common.base.Optional<?> GUAVA_OPTIONAL_EMPTY
        = com.google.common.base.Optional.absent();
    private final static java.util.Optional<?> JDK_OPTIONAL_EMPTY
        = java.util.Optional.empty();

    static abstract class RefFieldBean<T> {
        public T value;

        protected RefFieldBean() { }

        public RefFieldBean<T> with(T v) {
            value = v;
            return this;
        }
    }

    static abstract class RefCreatorBean<T> {
        protected T value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public RefCreatorBean(T v) {
            this.value = v;
        }

        public T getValue() { return value; }
    }

    static class AtomicRefNodeFieldBean extends RefFieldBean<AtomicReference<JsonNode>> { }
    static class GuavaOptionalNodeFieldBean extends RefFieldBean<com.google.common.base.Optional<JsonNode>> { }
    static class JDKOptionalNodeFieldBean extends RefFieldBean<java.util.Optional<JsonNode>> { }

    private final ObjectMapper MAPPER = jsonMapperBuilder()
            .addModule(new GuavaModule())
            .addModule(new Jdk8Module())
            .build();

    private final JsonNode NULL_NODE = MAPPER.getNodeFactory().nullNode();

    /*
    /**********************************************************************
    /* Tests for incoming `null` into Reference type
    /**********************************************************************
     */

    public void testNullFieldForAtomicRef() throws Exception
    {
        AtomicRefNodeFieldBean bean = MAPPER.readValue(JSON_FROM_NULL,
                AtomicRefNodeFieldBean.class);
        _assertAtomicRefsEqual(atomicReference(NULL_NODE), bean.value);
    }

    public void testNullFieldForGuavaOptional() throws Exception
    {
        GuavaOptionalNodeFieldBean bean = MAPPER.readValue(JSON_FROM_NULL,
                GuavaOptionalNodeFieldBean.class);
        _assertGuavaOptionalsEqual(guavaOptional(NULL_NODE), bean.value);
    }

    public void testNullFieldForJDKOptional() throws Exception
    {
        JDKOptionalNodeFieldBean bean = MAPPER.readValue(JSON_FROM_NULL,
                JDKOptionalNodeFieldBean.class);

        _assertJDKOptionalsEqual(jdkOptional(NULL_NODE), bean.value);
    }

    /*
    /**********************************************************************
    /* Tests for incoming "absent" value into Reference type
    /**********************************************************************
     */

    public void testAbsentFieldForAtomicRef() throws Exception
    {
        AtomicRefNodeFieldBean bean = MAPPER.readValue(JSON_FROM_ABSENT,
                AtomicRefNodeFieldBean.class);
        // 28-Oct-2023, tatu: Seems wrong, but has to wait for 2.17
        //_assertAtomicRefsEqual(ATOMIC_REFERENCE_EMPTY, bean.value);
        _assertAtomicRefsEqual(null, bean.value);
    }

    public void testAbsentFieldForGuavaOptional() throws Exception
    {
        GuavaOptionalNodeFieldBean bean = MAPPER.readValue(JSON_FROM_ABSENT,
                GuavaOptionalNodeFieldBean.class);
        // 28-Oct-2023, tatu: Seems wrong, but has to wait for 2.17
        //_assertGuavaOptionalsEqual(GUAVA_OPTIONAL_EMPTY, bean.value);
        _assertGuavaOptionalsEqual(null, bean.value);
    }

    public void testAbsentFieldForJDKOptional() throws Exception
    {
        JDKOptionalNodeFieldBean bean = MAPPER.readValue(JSON_FROM_ABSENT,
                JDKOptionalNodeFieldBean.class);

        // 28-Oct-2023, tatu: Seems wrong, but has to wait for 2.17
        //_assertJDKOptionalsEqual(JDK_OPTIONAL_EMPTY, bean.value);
        _assertJDKOptionalsEqual(null, bean.value);
    }

    /*
    /**********************************************************************
    /* Helper methods, factories
    /**********************************************************************
     */

    protected <T> AtomicReference<T> atomicReference(T value) {
        return new AtomicReference<>(value);
    }

    protected <T> com.google.common.base.Optional<T> guavaOptional(T value) {
        return com.google.common.base.Optional.fromNullable(value);
    }

    protected <T> java.util.Optional<T> jdkOptional(T value) {
        return java.util.Optional.ofNullable(value);
    }

    /*
    /**********************************************************************
    /* Helper methods, assertions
    /**********************************************************************
     */

    private void _assertAtomicRefsEqual(AtomicReference<?> ref1,
            AtomicReference<?> ref2) {
        if (ref1 == ref2) { // works as "null == null" check as well as identity
            return;
        }
        assertThat(ref1).hasSameClassAs(ref2);
        Object value1 = ref1.get();
        Object value2 = ref2.get();

        assertThat(value1).hasSameClassAs(value2);
        assertThat(value1).isEqualTo(value2);
    }

    private void _assertGuavaOptionalsEqual(com.google.common.base.Optional<?> ref1,
            com.google.common.base.Optional<?> ref2) {
        if (ref1 == ref2) { // works as "null == null" check as well as identity
            return;
        }
        assertThat(ref1).hasSameClassAs(ref2);
        Object value1 = ref1.get();
        Object value2 = ref2.get();

        assertThat(value1).hasSameClassAs(value2);
        assertThat(value1).isEqualTo(value2);
    }

    private void _assertJDKOptionalsEqual(java.util.Optional<?> ref1,
            java.util.Optional<?> ref2) {
        if (ref1 == ref2) { // works as "null == null" check as well as identity
            return;
        }
        assertThat(ref1).isNotNull();
        assertThat(ref2).isNotNull();
        assertThat(ref1).hasSameClassAs(ref2);
        Object value1 = ref1.get();
        Object value2 = ref2.get();

        assertThat(value1).hasSameClassAs(value2);
        assertThat(value1).isEqualTo(value2);
    }
}


