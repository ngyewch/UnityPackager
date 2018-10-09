plugins {
    java
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
    compile("commons-cli:commons-cli:1.4")
    compile("org.apache.commons:commons-lang3:3.8.1")
}
