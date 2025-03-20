// Module-info for integration tests, non-test
module tools.jackson.integtest.base
{
    // JDK deps
    requires transitive java.xml;

    // Core deps
    requires transitive com.fasterxml.jackson.annotation;
    requires transitive tools.jackson.core;
    requires transitive tools.jackson.databind;

    // Data formats
    requires transitive tools.jackson.dataformat.avro;
    requires transitive tools.jackson.dataformat.cbor;
    requires transitive tools.jackson.dataformat.csv;
    requires transitive tools.jackson.dataformat.ion;
    requires transitive tools.jackson.dataformat.protobuf;
    requires transitive tools.jackson.dataformat.properties;
    requires transitive tools.jackson.dataformat.smile;
    requires transitive tools.jackson.dataformat.xml;
    requires transitive tools.jackson.dataformat.yaml;
    requires transitive tools.jackson.dataformat.toml;

    // Base modules

    // Data types 
    requires transitive tools.jackson.datatype.eclipsecollections;
    requires transitive tools.jackson.datatype.guava;
    requires transitive tools.jackson.datatype.joda;
    requires transitive tools.jackson.datatype.javax.money;
    requires transitive tools.jackson.datatype.javatime;
    requires transitive tools.jackson.datatype.jsonp;
    // No more supported on 3.0
    //requires transitive tools.jackson.datatype.jsr353;
    requires transitive tools.jackson.datatype.moneta;
    
    // Other Jackson components
    requires transitive tools.jackson.jr.ob;
    requires transitive tools.jackson.jr.stree;
    requires transitive tools.jackson.module.afterburner;
    requires transitive tools.jackson.module.kotlin;

    // 3rd party
    requires transitive com.google.common;
    requires transitive org.eclipse.collections.api;
    requires transitive org.eclipse.collections.impl;
    requires transitive jakarta.json; // new JSONP
    requires transitive java.money; // java.money API
    requires transitive kotlin.stdlib;
    requires transitive org.immutables.value;
    requires transitive org.javamoney.moneta;
    requires transitive org.joda.time;

    // And then actual test(-only) dependencies
    requires transitive org.junit.jupiter.api;
    requires transitive org.junit.platform.commons;
    requires transitive org.assertj.core;
}
