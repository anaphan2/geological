plugins {
    id("multiloader-common")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.moddev)
}

neoForge {
    neoFormVersion = libs.versions.neoForm.get()
    // Automatically enable AccessTransformers if the file exists
    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

val commonResourcesDir = layout.buildDirectory.dir("generated/commonResources")

val syncCommonResources = tasks.register<Sync>("syncCommonResources") {
    from(sourceSets.main.get().resources)
    into(commonResourcesDir)
}

dependencies {
    // Fabric and NeoForge both bundle Fabric Mixin, so it is safe to use it in common
    // If you need to update, check what version they are using to see what is compatible
    // https://github.com/neoforged/NeoForge/blob/26.2.x/gradle.properties#L37
    // https://github.com/FabricMC/fabric-loader/blob/master/gradle.properties#L12
    compileOnly(libs.mixin)
    // Fabric and NeoForge both bundle MixinExtras, so it is safe to use it in common
    annotationProcessor(libs.mixinExtras.common)
    compileOnly(libs.mixinExtras.common)

}

configurations {
    create("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonKotlin") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonKotlin", sourceSets.main.get().kotlin.sourceDirectories.filter { !it.name.endsWith("java") }.singleFile)
    add(
        "commonResources",
        commonResourcesDir.map { it.asFile }
    ) {
        builtBy(syncCommonResources)
    }
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
            attribute(loaderAttribute, "common")
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
                attribute(loaderAttribute, "common")
            }
        }
    }
}