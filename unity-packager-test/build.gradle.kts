plugins {
    java
    //id("com.github.ben-manes.versions")
}

apply {
    plugin("ca.cutterslade.analyze")
}

repositories {
    jcenter()
    maven(url = "https://jitpack.io")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    //compile(project(":unity-packager"))
}
