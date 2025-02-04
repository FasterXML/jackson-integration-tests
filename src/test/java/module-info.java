// Module-info for integration tests, actual tests
module tools.jackson.integtest
{
    // Same deps as "non-test" side (lovely duplication)
    
    // // JDK deps
    requires java.xml;

    // // Core deps
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    // // Data formats
    requires tools.jackson.dataformat.avro;
    requires tools.jackson.dataformat.cbor;
    requires tools.jackson.dataformat.csv;
    requires tools.jackson.dataformat.ion;
    requires tools.jackson.dataformat.protobuf;
    requires tools.jackson.dataformat.properties;
    requires tools.jackson.dataformat.smile;
    requires tools.jackson.dataformat.xml;
    requires tools.jackson.dataformat.yaml;

    // // Base modules

    // // Data types 
    requires tools.jackson.datatype.joda;
    requires tools.jackson.datatype.javatime;
    requires tools.jackson.datatype.jsonp;
    requires tools.jackson.datatype.jsr353;
    requires tools.jackson.datatype.guava;
    
    // // Other Jackson components
    requires tools.jackson.jr.ob;
    requires tools.jackson.jr.stree;
    requires tools.jackson.module.kotlin;

    // // 3rd party
    requires com.google.common;
    requires jakarta.json; // new JSONP
    requires java.json; // old JSONP
    requires kotlin.stdlib;
    requires org.immutables.value;
    requires org.joda.time;

    // And then actual test(-only) dependencies
    requires junit;
    requires org.assertj.core;
}
