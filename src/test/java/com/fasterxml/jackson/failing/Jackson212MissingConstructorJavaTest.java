package com.fasterxml.jackson.failing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.integtest.BaseTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @see Jackson212MissingConstructorTest
 * @see <a href="https://github.com/FasterXML/jackson-module-kotlin/issues/396#issuecomment-906401187">jackson-module-kotlin issue 396</a>
 */
public class Jackson212MissingConstructorJavaTest extends BaseTest {

    interface Problem {
        String DEFAULT_TYPE = "about:blank";
        int DEFAULT_STATUS = 500;

        String getType();

        int getStatus();
    }

    static class DefaultProblem implements Problem {
        private final String type;
        private final int status;

        /**
         * This is required to workaround Jackson's missing support for static
         * {@link JsonCreator}s in mix-ins. That is, we need to define the
         * creator on a constructor in the mix-in that is matching with a
         * constructor here too.
         *
         * @see <a href="https://github.com/FasterXML/jackson-databind/issues/1820">jackson-databind issue 1820</a>
         */
        DefaultProblem(String type, Integer status) {
            this.type = type != null ? type : Problem.DEFAULT_TYPE;
            this.status = status != null ? status : Problem.DEFAULT_STATUS;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public int getStatus() {
            return status;
        }
    }

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "type",
            defaultImpl = DefaultProblem.class,
            visible = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonRootName("problem")
    interface ProblemMixIn extends Problem {

        @Override
        @JsonProperty("type")
        String getType();

        @Override
        @JsonProperty("status")
        int getStatus();
    }

    abstract static class DefaultProblemMixIn extends DefaultProblem {

        @JsonCreator
        DefaultProblemMixIn(@JsonProperty("type") String type, @JsonProperty("status") Integer status) {
            super(type, status);
            throw new IllegalStateException(
                    "mix-in constructor is there only for extracting the JSON mapping, " +
                            "it should not have been called");
        }
    }

    static class ProblemModule extends SimpleModule {

        @Override
        public void setupModule(SetupContext context) {
            super.setupModule(context);
            registerMixIns(context);
        }

        private static void registerMixIns(SetupContext context) {
            context.setMixInAnnotations(DefaultProblem.class, DefaultProblemMixIn.class);
            context.setMixInAnnotations(Problem.class, ProblemMixIn.class);
        }
    }

    private static final ProblemModule MODULE = new ProblemModule();

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper().registerModule(MODULE);

    private static final XmlMapper XML_MAPPER = (XmlMapper) new XmlMapper().registerModule(MODULE);

    public void testEmptyProblemJsonDeserialization() throws IOException {
        byte[] problemJsonBytes = "{}".getBytes(StandardCharsets.UTF_8);
        Problem problem = JSON_MAPPER.readValue(problemJsonBytes, Problem.class);
        assertEquals(Problem.DEFAULT_TYPE, problem.getType());
        assertEquals(Problem.DEFAULT_STATUS, problem.getStatus());
    }

    public void testEmptyProblemXmlDeserialization() throws IOException {
        byte[] problemXmlBytes = "<problem/>".getBytes(StandardCharsets.UTF_8);
        Problem problem = XML_MAPPER.readValue(problemXmlBytes, Problem.class);
        assertEquals(Problem.DEFAULT_TYPE, problem.getType());
        assertEquals(Problem.DEFAULT_STATUS, problem.getStatus());
    }
}