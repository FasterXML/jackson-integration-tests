package tools.jackson.integtest.mrbean;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import tools.jackson.module.mrbean.MrBeanModule;

import tools.jackson.integtest.BaseTest;

// Copied from mrbean module tests ("RoundTripTest")
public class MrBeanTest extends BaseTest
{
    public interface Bean {
        String getField();
        void setField(String field);
    }

    public interface ReadOnlyBean {
        String getField();
    }

    private final ObjectMapper MAPPER = jsonMapperBuilder()
            .addModule(new MrBeanModule())
            .build();

    @Test
    public void testSimple() throws Exception
    {
        final String input = "{\"field\":\"testing\"}";
        final Bean bean = MAPPER.readValue(input, Bean.class);
        assertEquals("testing", bean.getField());
        final String output = MAPPER.writeValueAsString(bean);
        assertEquals(input, output);
    }

    @Test
    public void testSimpleWithoutSetter() throws Exception
    {
        final String input = "{\"field\":\"testing\"}";
        final ReadOnlyBean bean = MAPPER.readValue(input, ReadOnlyBean.class);
        assertEquals("testing", bean.getField());
        final String output = MAPPER.writeValueAsString(bean);
        assertEquals(input, output);
    }
}
