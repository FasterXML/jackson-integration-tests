plugins {
    id("java-library")
}

val modulesWithoutGradleMetadata = listOf(
        "com.fasterxml.jackson.jr:jackson-jr-all", // TODO is there a reason not to add this?

        "com.fasterxml.jackson:jackson-bom", // does not need it
        "com.fasterxml.jackson.module:jackson-module-scala_2.11", // built with sbt
        "com.fasterxml.jackson.module:jackson-module-scala_2.12", // built with sbt
        "com.fasterxml.jackson.module:jackson-module-scala_2.13", // built with sbt
        "com.fasterxml.jackson.module:jackson-module-scala_3"     // built with sbt
)

dependencies {
    implementation(platform("com.fasterxml.jackson:jackson-bom:+"))
}

repositories.mavenCentral()

// (miss-)use a component metadata rule to collect all entries from the BOM
val allJacksonModule = mutableListOf<String>()
dependencies.components.withModule("com.fasterxml.jackson:jackson-bom") {
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
            val gmmDependencies = gmmModule.dependencies.filter { !it.requested.displayName.startsWith("com.fasterxml.jackson:jackson-bom:") }.map { it.toString() }
            if (pomDependencies != gmmDependencies) {
                message += "Dependencies of ${pomModule.id} are wrong in Gradle Metadata:" +
                        "\n  POM:    ${pomDependencies.joinToString()}" +
                        "\n  Gradle: ${gmmDependencies.joinToString()}" +
                        "\n"
            }
        }
        if (message.isNotEmpty()) {
            throw RuntimeException(message)
        }
    }
}

fun resolveJacksonModules(allModules: Configuration) =
    allModules.incoming.resolutionResult.allComponents.filter {
        it.moduleVersion!!.group.startsWith("com.fasterxml.jackson") && !modulesWithoutGradleMetadata.contains(it.moduleVersion!!.module.toString())
    }