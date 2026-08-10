plugins {
    id("multiloader-loader")
    alias(libs.plugins.loom)
    alias(libs.plugins.kotlin)
}

val modId: String by project

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabricLoader)
    implementation(libs.fabricApi)

    implementation(libs.flk)
}

loom {
    val aw = project(":common").file("src/main/resources/${modId}.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }
    runs {
        named("client") {
            client()
            setConfigName("Fabric Client")
            ideConfigGenerated(true)
            runDir("runs/client")
        }
        named("server") {
            server()
            setConfigName("Fabric Server")
            ideConfigGenerated(true)
            runDir("runs/server")
        }
    }
}

// Implement mcgradleconventions loader attribute
val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)

listOf(
    "apiElements",
    "runtimeElements",
    "sourcesElements",
    "javadocElements",
    "includeInternal",
    "modCompileClasspath"
).forEach { configurationName ->
    configurations.named(configurationName) {
        attributes {
            attribute(loaderAttribute, "fabric")
        }
    }
}

sourceSets.configureEach {
    listOf(
        compileClasspathConfigurationName,
        runtimeClasspathConfigurationName
    ).forEach { configurationName ->
        configurations.named(configurationName) {
            attributes {
                attribute(loaderAttribute, "fabric")
            }
        }
    }
}