plugins {
    id("java-library")
}

val modulesWithoutGradleMetadata = listOf(
        "com.fasterxml.jackson.module:jackson-module-afterburner", // TODO remove after next release - fixed in https://github.com/FasterXML/jackson-modules-base/pull/198
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
        configurations.compileClasspath.get().resolve() // triggers the rule above

        // Create dependencies to all Modules references in the BOM
        val allModules = configurations.detachedConfiguration(*allJacksonModule.map { dependencies.create(it) }.toTypedArray())
        // Tell Gradle to do the dependency resolution and return the result with dependency information
        val allModulesResolved = allModules.incoming.resolutionResult.allComponents.filter {
            it.moduleVersion!!.group.startsWith("com.fasterxml.jackson") && !modulesWithoutGradleMetadata.contains(it.moduleVersion!!.module.toString())
        }

        val allModulesWithoutBomDependency = mutableListOf<String>()
        allModulesResolved.forEach { component ->
            if (component.dependencies.map { it as ResolvedDependencyResult }.none { it.selected.moduleVersion!!.name == "jackson-bom" }) {
                allModulesWithoutBomDependency.add(component.moduleVersion.toString())
            }
        }
        if (allModulesWithoutBomDependency.isNotEmpty()) {
            val message = "Missing dependency to 'jackson-bom'. Gradle Metadata publishing is most likely broken:\n  - ${allModulesWithoutBomDependency.joinToString("\n  - ")}"
            throw RuntimeException(message)
        }
    }
}