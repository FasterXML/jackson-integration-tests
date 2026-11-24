plugins {
    id("java-library")
}

val modulesWithoutGradleMetadata = listOf(
        "tools.jackson.jr:jackson-jr-all", // TODO is there a reason not to add this?

        "tools.jackson:jackson-bom", // does not need it
        "tools.jackson.module:jackson-module-scala_2.12", // built with sbt
        "tools.jackson.module:jackson-module-scala_2.13", // built with sbt
        "tools.jackson.module:jackson-module-scala_3",     // built with sbt

        // 31-Oct-2025, tatu: does not depend on jackson-bom; 2.x group id
	//    and naming convention different
        "com.fasterxml.jackson.core:jackson-annotations",

        // 3rd-party managed dependencies, by XML module:
        "com.fasterxml.woodstox:woodstox-core",
        "org.codehaus.woodstox:stax2-api" // for Woodstox
)

dependencies {
    // 18-Jul-2025, tatu: Choose one or the other of these (regular vs SNAPSHOT)
    //                                                  |
    //                                                  V

    implementation(platform("tools.jackson:jackson-bom:3.2.+"))
    //implementation(platform("tools.jackson:jackson-bom:3.2.0-SNAPSHOT"))

    // Need just new Snapshot repo (everything)
    repositories.maven("https://central.sonatype.com/repository/maven-snapshots");
}

repositories.mavenCentral()

configurations.all {
    // 23-Mar-2025: [#27] Reduce default snapshot TTL from 24h to none, to avoid
    //    failing (or passing, for that matter) on stale snapshots
    resolutionStrategy.cacheChangingModulesFor(0, "seconds") // always refresh SNAPSHOTs
}

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
        val allModules = resolver(allJacksonModule)
        val modulesWithGradleMetadata = allJacksonModule.filter { m -> modulesWithoutGradleMetadata.none { m.startsWith(it) } }

        // Tell Gradle to do the dependency resolution and return the result with dependency information
        val allModulesResolved = resolveJacksonModules(allModules)

        // Fail fast (with a readable message) if anything could not be resolved: typically
        // means some module(s) referenced by the BOM have not been published (yet), for
        // example SNAPSHOT versions right after a release. Remaining checks need full resolution.
        // (modules listed in 'modulesWithoutGradleMetadata' are not verified, so may be missing)
        val unresolved = allModules.incoming.resolutionResult.allDependencies
            .filterIsInstance<UnresolvedDependencyResult>()
            .filter { dep -> modulesWithoutGradleMetadata.none { dep.attempted.displayName.startsWith("$it:") } }
            .groupBy { it.attempted.displayName }
            .toSortedMap()
            .map { (missing, deps) ->
                val reason = deps.first().failure.message?.lineSequence()?.first() ?: deps.first().failure.toString()
                val requiredBy = deps.map { it.from }
                    .filter { it.id !is ProjectComponentIdentifier } // skip this (test) project itself
                    .map { it.moduleVersion?.toString() ?: it.id.displayName }
                    .distinct().sorted()
                "$missing: $reason" + requiredBy.joinToString("") { "\n      required by $it" }
            }
        if (unresolved.isNotEmpty()) {
            throw RuntimeException("Could not resolve ${unresolved.size} module(s) referenced via the BOM" +
                    " (not published yet?):\n  - ${unresolved.joinToString("\n  - ")}\n")
        }

        val allModulesWithoutBomDependency = mutableListOf<String>()
        allModulesResolved.forEach { component ->
            if (component.dependencies.filterIsInstance<ResolvedDependencyResult>().none { it.selected.moduleVersion!!.name == "jackson-bom" }) {
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
        val pomAllModules = resolver(allJacksonModule)
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

        val pomMetadataFiles = resolver(modulesWithGradleMetadata.map { "$it@pom" })
        val gradleMetadataFiles = resolver(modulesWithGradleMetadata.map { "$it@module" })
        val checksumFiles = resolver(modulesWithGradleMetadata.map { "$it@jar.md5" })

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

fun resolver(deps: List<String>) = configurations.detachedConfiguration(
    *deps.map { dependencies.create(it) }.toTypedArray()).also {
        it.resolutionStrategy.cacheChangingModulesFor(0, "seconds") }

