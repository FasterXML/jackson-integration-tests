package tools.jackson.integtest.gradle;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static java.util.Objects.requireNonNull;

import static org.junit.jupiter.api.Assertions.*;

public class GradleTest
{
    /**
     * This test calls the Gradle build in 'src/test/resources/com/fasterxml/jackson/integtest/gradle' which:
     * - Collects all entries from the latest Jackson BOM
     * - Checks the metadata of all the entries if they point back to the BOM
     * - If not, it fails which can mean one of the following:
     *   - .module file is missing completely (not configured in corresponding pom.xml?)
     *   - 'do_not_remove: published-with-gradle-metadata' comment missing in published POM
     *   - .module file is there but something is misconfigured
     * Gradle Metadata is published for most Jackson components for reasons described here:
     * <a href="https://blog.gradle.org/alignment-with-gradle-module-metadata">blog.gradle.org/alignment-with-gradle-module-metadata</a>
     */
    @Test
    public void testJacksonBomDependency(@TempDir File tempDir) throws Exception {
        copyToTestFolder(tempDir, "settings.gradle.kts");
        copyToTestFolder(tempDir, "build.gradle.kts");
        build(tempDir, ":checkMetadata");
    }

    private void copyToTestFolder(File testFolder, String fileName) throws IOException {
        URL resource = getClass().getResource(fileName);
        assertNotNull(resource, "Null resource '"+fileName+"', from `"+getClass()+"`");
        Files.copy(new File(resource.getFile()).toPath(),
                new File(testFolder, fileName).toPath());
    }

    private void build(File testFolder, String task) throws Exception {
        String fileName = "gradlew";
        URL resource = getClass().getResource(fileName);
        assertNotNull(resource, "Null resource '"+fileName+"', from `"+getClass()+"`");
        String gradlew = resource.getFile();
        Runtime.getRuntime().exec("chmod a+x " + gradlew).waitFor();
        ProcessBuilder bp = new ProcessBuilder(gradlew, task, "-q",
                "--project-dir", testFolder.getAbsolutePath());
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
            fail(output.toString().trim());
        }
    }
}
