import kotlin.io.path.createParentDirectories

plugins {
    id("multiloader-loader")
    alias(libs.plugins.moddev)
    alias(libs.plugins.kotlin)
}

val modId: String by project

neoForge {
    version = libs.versions.neoforge.get()
    // Automatically enable neoforge AccessTransformers if the file exists
    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            ideName = "NeoForge ${name.capitalize()} (${project.path})" // Unify the run config names with fabric
        }
        register("client") {
            client()
            gameDirectory = project.mkdir(project.file("runs/client"))
        }
        register("data") {
            clientData()
            gameDirectory = project.mkdir(project.file("runs/data"))
            // DataGen can be run by - "./gradlew :neoforge:runData" in Terminal.
            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll("--mod", modId, "--all",
                "--output", rootProject.project(":common").file("src/generated/resources/").absolutePath,
                "--existing", rootProject.project(":common").file("src/main/resources/").absolutePath
            )
        }
        register("server") {
            server()
            project.file("runs/server").toPath().createParentDirectories()
            gameDirectory = project.mkdir(project.file("runs/server"))
        }
    }
    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

//sourceSets.main.get().resources { srcDir("src/generated/resources") }

dependencies {
    implementation(libs.kff)
}

// Implement mcgradleconventions loader attribute

val loaderAttribute = Attribute.of(
    "io.github.mcgradleconventions.loader",
    String::class.java
)

listOf(
    "apiElements",
    "runtimeElements",
    "sourcesElements",
    "javadocElements"
).forEach { configurationName ->
    configurations.named(configurationName) {
        attributes {
            attribute(loaderAttribute, "neoforge")
        }
    }
}

sourceSets.configureEach {
    listOf(
        compileClasspathConfigurationName,
        runtimeClasspathConfigurationName,
        getTaskName(null, "jarJar")
    ).forEach { configurationName ->
        configurations.named(configurationName) {
            attributes {
                attribute(loaderAttribute, "neoforge")
            }
        }
    }
}