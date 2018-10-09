plugins {
    `java-gradle-plugin`
    `maven-publish`
    //id("com.github.ben-manes.versions")
}

apply {
    plugin("ca.cutterslade.analyze")
}

repositories {
    mavenLocal()
    jcenter()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    compile("com.github.ngyewch.unitypackager:unity-packager:0.0.1")
    compile(gradleApi())
}

gradlePlugin {
    (plugins) {
        "unitypackager" {
            id = "com.github.ngyewch.unitypackager"
            implementationClass = "com.github.ngyewch.unity.packager.gradle.UnityPackagerPlugin"
        }
    }
}

publishing {
    publications.create("mavenJava", MavenPublication::class.java) {
        from(components.getByName("java"))
        groupId = "com.github.ngyewch.gradle"
        artifactId = "unitypackager"
        version = "0.0.1"
    }
}

tasks {
    "build" {
        dependsOn(":unity-packager:build", "publishToMavenLocal")
    }
}
