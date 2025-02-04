// Module-info for integration tests, actual tests
module tools.jackson.integtest
{
    // Same deps as "non-test" side (lovely duplication)
    requires tools.jackson.integtest.base;

    // But! Need to open a few test packages

    opens tools.jackson.failing;
    opens tools.jackson.integtest;
    opens tools.jackson.integtest.df.basic;
    opens tools.jackson.integtest.df.csv;
    opens tools.jackson.integtest.df.props;
    opens tools.jackson.integtest.df.xml;
    opens tools.jackson.integtest.dt.datetime;
    opens tools.jackson.integtest.dt.guava;
    opens tools.jackson.integtest.dt.joda;
    opens tools.jackson.integtest.dt.jsonp;
    opens tools.jackson.integtest.gradle;
    opens tools.jackson.integtest.immutables;
    opens tools.jackson.integtest.jacksonjr;
    opens tools.jackson.integtest.jdk;
    opens tools.jackson.integtest.testutil;
}
