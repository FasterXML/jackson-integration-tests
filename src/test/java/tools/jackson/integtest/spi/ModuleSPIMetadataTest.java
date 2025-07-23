package tools.jackson.integtest.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.eclipsecollections.EclipseCollectionsModule;
import tools.jackson.datatype.guava.GuavaModule;
import tools.jackson.datatype.javax.money.JavaxMoneyModule;
import tools.jackson.datatype.joda.JodaModule;
import tools.jackson.datatype.jsonp.JSONPModule;
import tools.jackson.integtest.BaseTest;
import tools.jackson.module.afterburner.AfterburnerModule;
import tools.jackson.module.blackbird.BlackbirdModule;

import static org.junit.jupiter.api.Assertions.fail;

public class ModuleSPIMetadataTest extends BaseTest
{
    private final List<Class<? extends JacksonModule>> modules = Arrays.asList(
            // General-purpose:
                    AfterburnerModule.class,
                    BlackbirdModule.class,

                    // Collection data types:
                    EclipseCollectionsModule.class,
                    GuavaModule.class,

                    // Other data types:
                    JavaxMoneyModule.class,
                    JodaModule.class,
                    JSONPModule.class
            );

    @Test
    public void testFindModules()
    {
        List<JacksonModule> modulesFound = JsonMapper.Builder.findModules();
        List<Class<?>> missing = new ArrayList<>();
        final int found = modulesFound.size();

        main_loop:
        for(Class<?> module : modules) {
            Iterator<JacksonModule> it = modulesFound.iterator();
            while (it.hasNext()) {
                JacksonModule nextFound = it.next();
                if (nextFound.getClass().equals(module)) {
                    it.remove();
                    continue main_loop;
                }
            }
            missing.add(module);
        }

        if(!missing.isEmpty()) {
            fail("Found "+found+" modules; unmatched ("+modulesFound+"), missing "+missing.size()+". Missing modules: " + missing);
        }
    }
}
