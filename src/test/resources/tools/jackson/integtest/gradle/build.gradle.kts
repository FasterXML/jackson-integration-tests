plugins {
    id("java-library")
}

val modulesWithoutGradleMetadata = listOf(
        "tools.jackson.jr:jackson-jr-all", // TODO is there a reason not to add this?

        "tools.jackson:jackson-bom", // does not need it
        "tools.jackson.module:jackson-module-scala_2.11", // built with sbt
        "tools.jackson.module:jackson-module-scala_2.12", // built with sbt
        "tools.jackson.module:jackson-module-scala_2.13", // built with sbt
        "tools.jackson.module:jackson-module-scala_3",     // built with sbt

        // 3rd-party managed dependencies, by XML module:
        "com.fasterxml.woodstox:woodstox-core",
        "org.codehaus.woodstox:stax2-api" // for Woodstox
)

dependencies {
    // implementation(platform("tools.jackson:jackson-bom:+"))

    // 28-Apr-2023, tatu: Uncomment following (and comment ^^^) to test SNAPSHOT versions
    implementation(platform("tools.jackson:jackson-bom:3.0.0-rc2-SNAPSHOT"))
    // Need both new Snapshot repo (almost everything) and "old" (for jackson-annotations)
    repositories.maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    repositories.maven("https://oss.sonatype.org/content/repositories/snapshots")
}

repositories.mavenCentral()

// (miss-)use a component metadata rule to collect all entries from the BOM
val allJacksonModule = mutableListOf<String>()
dependencies.components.withModule("tools.jackson:jackson-bom") {
    allVariants {
        withDependencyConstraints {
            allJacksonModule.addAll(map { it.toString() })
        }
    }
}

tasks.register("checkMetadata") {
    doLast {
        var message = ""

        configurations.compileClasspath.get().resolve() // triggers the rule above

        // Create dependencies to all Modules references in the BOM
        val allModules = configurations.detachedConfiguration(*allJacksonModule.map { dependencies.create(it) }.toTypedArray())
        val modulesWithGradleMetadata = allJacksonModule.filter { m -> modulesWithoutGradleMetadata.none { m.startsWith(it) } }

        // Tell Gradle to do the dependency resolution and return the result with dependency information
        val allModulesResolved = resolveJacksonModules(allModules)

        val allModulesWithoutBomDependency = mutableListOf<String>()
        allModulesResolved.forEach { component ->
            if (component.dependencies.map { it as ResolvedDependencyResult }.none { it.selected.moduleVersion!!.name == "jackson-bom" }) {
                allModulesWithoutBomDependency.add(component.moduleVersion.toString())
            }
        }
        if (allModulesWithoutBomDependency.isNotEmpty()) {
            message += "Missing dependency to 'jackson-bom'. Gradle Metadata publishing is most likely broken:\n  - ${allModulesWithoutBomDependency.joinToString("\n  - ")}\n"
        }

        // fetch again in a separate context using only the POM metadata
        repositories.all {
            (this as MavenArtifactRepository).metadataSources {
                mavenPom()
                ignoreGradleMetadataRedirection()
            }
        }
        val pomAllModules = configurations.detachedConfiguration(*allJacksonModule.map { dependencies.create(it) }.toTypedArray())
        val pomAllModulesResolved = resolveJacksonModules(pomAllModules)
        allModulesResolved.forEachIndexed { index, gmmModule ->
            val pomModule = pomAllModulesResolved[index]

            val pomDependencies = pomModule.dependencies.map { it.toString() }
            val gmmDependencies = gmmModule.dependencies.filter { !it.requested.displayName.startsWith("tools.jackson:jackson-bom:") }.map { it.toString() }
            if (pomDependencies != gmmDependencies) {
                message += "Dependencies of ${pomModule.id} are wrong in Gradle Metadata:" +
                        "\n  POM:    ${pomDependencies.joinToString()}" +
                        "\n  Gradle: ${gmmDependencies.joinToString()}" +
                        "\n\n"
            }
        }

        val pomMetadataFiles = configurations.detachedConfiguration(*modulesWithGradleMetadata.map { dependencies.create("$it@pom") }.toTypedArray())
        val gradleMetadataFiles = configurations.detachedConfiguration(*modulesWithGradleMetadata.map { dependencies.create("$it@module") }.toTypedArray())
        val checksumFiles = configurations.detachedConfiguration(*modulesWithGradleMetadata.map { dependencies.create("$it@jar.md5") }.toTypedArray())

        val pomsWithoutMarker = pomMetadataFiles.files.filter { !it.readText().contains("<!-- do_not_remove: published-with-gradle-metadata -->") }.map { it.name }
        if (pomsWithoutMarker.isNotEmpty()) {
            message += "POMs without Gradle Metadata marker:\n  - ${pomsWithoutMarker.joinToString("\n  - ")}\n\n"
        }

        val checksumsFromFile = checksumFiles.associate { it.name.substringBeforeLast("-") to it.readText() }
        val checksumsFromMetadata = gradleMetadataFiles.associate { it.name.substringBeforeLast("-") to it.readText().lines().first {
            it.contains("\"md5\"") }.substringAfterLast(": \"").replace("\"", "").padStart(32, '0')
        }
        val checksumsDiff = checksumsFromFile.filter { (k,v) -> checksumsFromMetadata[k] != v }
        if (checksumsDiff.isNotEmpty()) {
            message += "Checksums in Gradle Metadata are wrong:\n  - ${checksumsDiff.keys.joinToString("\n  - ")}\n\n"
            throw RuntimeException(message)
        }

        if (message.isNotEmpty()) {
            throw RuntimeException(message)
        }
    }
}

fun resolveJacksonModules(allModules: Configuration) =
    allModules.incoming.resolutionResult.allComponents.filter {
        it.moduleVersion!!.group.startsWith("tools.jackson") && !modulesWithoutGradleMetadata.contains(it.moduleVersion!!.module.toString())
    }
