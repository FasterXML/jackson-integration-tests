// Module-info for integration tests
module tools.jackson.integtest
{
    // JDK deps
    requires java.xml;

    // Core deps
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    // Data formats
    requires tools.jackson.dataformat.avro;
    requires tools.jackson.dataformat.cbor;
    requires tools.jackson.dataformat.csv;
    requires tools.jackson.dataformat.protobuf;
    requires tools.jackson.dataformat.properties;
    requires tools.jackson.dataformat.smile;
    requires tools.jackson.dataformat.xml;
    requires tools.jackson.dataformat.yaml;

    // Base modules

    // Data types 
    requires tools.jackson.datatype.joda;
    requires tools.jackson.datatype.javatime;
    requires tools.jackson.datatype.jsonp;
    requires tools.jackson.datatype.jsr353;
    requires tools.jackson.datatype.guava;
    
    // Other Jackson components
    requires tools.jackson.jr.ob;
    requires tools.jackson.module.kotlin;

    // 3rd party
    requires com.google.common;
    requires kotlin.stdlib;
    requires org.joda.time;

    // Test libraries
    requires junit;
}
