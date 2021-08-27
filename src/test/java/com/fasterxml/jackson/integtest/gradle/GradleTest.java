package com.fasterxml.jackson.integtest.gradle;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

public class GradleTest
{
    static final String VERSION_QUALIFIER = "latest.release";

    static final String[] JACKSON_MODULES = {
            // Core
            "com.fasterxml.jackson.core:jackson-annotations",
            "com.fasterxml.jackson.core:jackson-core",
            "com.fasterxml.jackson.core:jackson-databind",

            // Data Formats
            "com.fasterxml.jackson.dataformat:jackson-dataformat-avro",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-cbor",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-csv",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-ion",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-properties",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-protobuf",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-smile",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-toml", // in 2.13
            "com.fasterxml.jackson.dataformat:jackson-dataformat-xml",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml",

            // Data Types
            "com.fasterxml.jackson.datatype:jackson-datatype-eclipse-collections",
            "com.fasterxml.jackson.datatype:jackson-datatype-guava",

            // 27-Aug-2021, tatu: Removed from 2.13:
//            "com.fasterxml.jackson.datatype:jackson-datatype-hibernate3",
            "com.fasterxml.jackson.datatype:jackson-datatype-hibernate4",
            "com.fasterxml.jackson.datatype:jackson-datatype-hibernate5",
            "com.fasterxml.jackson.datatype:jackson-datatype-hppc",
            "com.fasterxml.jackson.datatype:jackson-datatype-jakarta-jsonp", // since 2.13 (nee jsr-353)
            "com.fasterxml.jackson.datatype:jackson-datatype-jaxrs",
            "com.fasterxml.jackson.datatype:jackson-datatype-joda",
            "com.fasterxml.jackson.datatype:jackson-datatype-joda-money",
            "com.fasterxml.jackson.datatype:jackson-datatype-jdk8",
            "com.fasterxml.jackson.datatype:jackson-datatype-json-org",
            "com.fasterxml.jackson.datatype:jackson-datatype-jsr310",
            "com.fasterxml.jackson.datatype:jackson-datatype-jsr353", // old JSONP (javax.)
            "com.fasterxml.jackson.datatype:jackson-datatype-pcollections",

            // JAX-RS
            "com.fasterxml.jackson.jaxrs:jackson-jaxrs-base",
            "com.fasterxml.jackson.jaxrs:jackson-jaxrs-cbor-provider",
            "com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider",
            "com.fasterxml.jackson.jaxrs:jackson-jaxrs-smile-provider",
            "com.fasterxml.jackson.jaxrs:jackson-jaxrs-xml-provider",
            "com.fasterxml.jackson.jaxrs:jackson-jaxrs-yaml-provider",

            // Jakarta-RS (since 2.13)
            "com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-base",
            "com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-cbor-provider",
            "com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-json-provider",
            "com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-smile-provider",
            "com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-xml-provider",
            "com.fasterxml.jackson.jakarta.rs:jackson-jakarta-rs-yaml-provider",
            
            // Jackson Jr.
            // "com.fasterxml.jackson.jr:jackson-jr-all" does not have the bom dependency, see: https://github.com/FasterXML/jackson-databind/issues/2726#issuecomment-707327060
            "com.fasterxml.jackson.jr:jackson-jr-annotation-support",
            "com.fasterxml.jackson.jr:jackson-jr-objects",
            "com.fasterxml.jackson.jr:jackson-jr-retrofit2",
            "com.fasterxml.jackson.jr:jackson-jr-stree",

            // Modules, basic
            "com.fasterxml.jackson.module:jackson-module-afterburner",
            "com.fasterxml.jackson.module:jackson-module-blackbird",
            "com.fasterxml.jackson.module:jackson-module-guice",
            "com.fasterxml.jackson.module:jackson-module-jaxb-annotations",
            "com.fasterxml.jackson.module:jackson-module-jakarta-xmlbind-annotations",
            "com.fasterxml.jackson.module:jackson-module-jsonSchema",
            "com.fasterxml.jackson.module:jackson-module-kotlin",
            "com.fasterxml.jackson.module:jackson-module-mrbean",
            "com.fasterxml.jackson.module:jackson-module-no-ctor-deser",
            "com.fasterxml.jackson.module:jackson-module-osgi",
            "com.fasterxml.jackson.module:jackson-module-parameter-names",
            "com.fasterxml.jackson.module:jackson-module-paranamer",

            // 20-Nov-2020, tatu: as per: [module-scala#480] support for Scala 2.10
            //   dropped from Jackson 2.12
//            "com.fasterxml.jackson.module:jackson-module-scala_2.10",
            "com.fasterxml.jackson.module:jackson-module-scala_2.11",
            "com.fasterxml.jackson.module:jackson-module-scala_2.12",
            "com.fasterxml.jackson.module:jackson-module-scala_2.13"
    };

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    // 05-Mar-2021, tatu: Alas, failing for reason I do not fully understand.
    //   Commenting out around 2.12.2 to prevent bogus test failure, so we'll
    //   catch other regressions.

    /**
     * For each Jackson module, this test sets up a Gradle build that only defines the version for that one module.
     * It then depends on all other modules without defining a version. Resolving the classpath still succeeds,
     * with all modules having an aligned version, because the Jackson BOM is always present to provide the versions.
     */
    @Test
    public void testJacksonBomDependency() throws Exception
    {
        Version version = new ObjectMapper().version();
        String versionOfIntegrationTestProject = version.getMajorVersion() + "." + version.getMinorVersion();
        Set<String> failedModules = new TreeSet<>();

        File settingsFile = testFolder.newFile("settings.gradle.kts");
        File buildFile = testFolder.newFile("build.gradle.kts");

        Files.write("rootProject.name = \"test-project\"", settingsFile, Charsets.UTF_8);

        // run Gradle once to make sure everything is installed
        build("help");

        Files.write(
                "repositories {\n" +
                "  mavenCentral()\n" +
                "}\n" +
                "val jacksonCore by configurations.creating\n" +
                "dependencies {\n" +
                "  \"jacksonCore\"(\"com.fasterxml.jackson.core:jackson-core:" + VERSION_QUALIFIER + "\")\n" +
                "}\n" +
                "tasks.register(\"printJacksonVersion\") {\n" +
                "  doLast {\n" +
                "    val jar = jacksonCore.singleFile.name\n" +
                "    println(jar.substring(\"jackson-core\".length + 1, jar.indexOf(\".jar\")))\n" +
                "  }\n" +
                "}\n", buildFile, Charsets.UTF_8);
        String latestReleaseVersion = build("printJacksonVersion");
        String latestReleaseMinor = latestReleaseVersion.substring(0, latestReleaseVersion.lastIndexOf('.'));

        assumeTrue("Skipping test because latest release (" + latestReleaseMinor + ") " +
                "does not correspond to version of integration test project (" + versionOfIntegrationTestProject +").",
                versionOfIntegrationTestProject.equals(latestReleaseMinor));

        System.out.println("Testing latest Jackson release " + latestReleaseVersion);

        for(String module : JACKSON_MODULES) {
            System.out.println("Checking: " + module);

            Files.write(
                    "plugins {\n" +
                    " `java-library`\n" +
                    "}\n" +
                    "repositories {\n" +
                    "  mavenCentral()\n" +
                    "}\n" +
                    "dependencies {\n" +
                    "  constraints {\n" +
                    "    implementation(\"" + module + ":" + latestReleaseVersion + "\")\n" +
                    "  }\n" +
                    Arrays.stream(JACKSON_MODULES).map(ga -> "  implementation(\"" + ga + "\")\n").collect(Collectors.joining()) +
                    "}\n" +
                    "tasks.register(\"printJars\") {\n" +
                    "  doLast {\n" +
                    "    configurations.compileClasspath.get().files.filter { it.name.startsWith(\"jackson-\") }.forEach {\n" +
                    "      println(it.name)\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n", buildFile, Charsets.UTF_8);

            try {
                assertEquals(expectedClasspath(latestReleaseVersion), buildClasspath());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                failedModules.add(module);
            }
        }

        assertTrue(failedModules.toString(), failedModules.isEmpty());
    }

    private Set<String> expectedClasspath(String version) {
        Set<String> classpath = Arrays.stream(JACKSON_MODULES).map(ga -> ga.split(":")[1] + "-" + version + ".jar").collect(Collectors.toCollection(TreeSet::new));
        classpath.add("jackson-core-asl-1.9.13.jar");
        classpath.add("jackson-mapper-asl-1.9.13.jar");
        return classpath;
    }

    private Set<String> buildClasspath() throws Exception {
        return Arrays.stream(build("printJars").split("\n")).collect(Collectors.toCollection(TreeSet::new));
    }

    private String build(String task) throws Exception {
        String gradlew = getClass().getResource("gradlew").getFile();
        Runtime.getRuntime().exec("chmod a+x " + gradlew).waitFor();
        ProcessBuilder bp = new ProcessBuilder(gradlew, task, "-q", "-s", "--project-dir", testFolder.getRoot().getAbsolutePath());
        bp.redirectErrorStream(true);
        Process process = bp.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        reader.close();

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            System.out.println(output.toString());
            throw new RuntimeException("Gradle build failed");
        }

        return output.toString().trim();
    }
}
