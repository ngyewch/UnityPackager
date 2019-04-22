plugins {
    java
    `maven-publish`
    id("com.jfrog.bintray") version ("1.8.4")
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
    compile("org.springframework:spring-core:5.1.6.RELEASE")
    compile("org.yaml:snakeyaml:1.24")
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

bintray {
    user = project.findProperty("bintray.user") as String?
    key = project.findProperty("bintray.key") as String?
    setPublications("maven")
    pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "unity-packager"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/ngyewch/UnityPackager.git"
    })
}
