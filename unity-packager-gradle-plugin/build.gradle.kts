plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.10.0"
    //id("com.github.ben-manes.versions")
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
    compile("org.apache.commons:commons-lang3:3.9")
}

gradlePlugin {
    plugins {
        create("unityPackagerPlugin") {
            id = "com.github.ngyewch.unity.packager.gradle"
            implementationClass = "com.github.ngyewch.unity.packager.gradle.UnityPackagerPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/ngyewch/UnityPackager"
    vcsUrl = "https://github.com/ngyewch/UnityPackager.git"
    tags = listOf("gradle", "plugin", "unity", "unitypackage")
}
