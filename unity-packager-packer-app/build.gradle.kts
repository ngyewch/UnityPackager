plugins {
    java
    //id("com.github.ben-manes.versions")
    id("com.jonaslasauskas.capsule") version ("0.3.0")
}

apply {
    plugin("ca.cutterslade.analyze")
}

repositories {
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    compile(project(":unity-packager"))
    compile("commons-cli:commons-cli:1.4")
    compile("org.apache.commons:commons-lang3:3.9")
}

tasks {
    val capsule by getting(com.jonaslasauskas.gradle.plugin.capsule.Capsule::class) {
        capsuleManifest {
            applicationId = "${project.name}"
            applicationClass = "com.github.ngyewch.unity.packager.app.Packer"
        }
    }

    get("build").dependsOn("capsule")
}
