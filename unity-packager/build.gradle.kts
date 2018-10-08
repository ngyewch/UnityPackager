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
    compile("commons-io:commons-io:2.6")
    compile("org.apache.commons:commons-compress:1.18")
    compile("org.yaml:snakeyaml:1.23")
}
