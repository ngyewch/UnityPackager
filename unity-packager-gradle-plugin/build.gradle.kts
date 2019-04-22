plugins {
    `java-gradle-plugin`
    `maven-publish`
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
        create("unityPackager") {
            id = "com.github.ngyewch.unity.packager.gradle"
            implementationClass = "com.github.ngyewch.unity.packager.gradle.UnityPackagerPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String?
            artifactId = project.name
            version = project.version as String?
            from(components["java"])
        }
    }
}