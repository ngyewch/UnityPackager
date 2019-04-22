buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("ca.cutterslade.gradle:gradle-dependency-analyze:1.3.1")
    }
}

plugins {
    java
    id("com.jfrog.bintray") version ("1.8.4")
    id("com.github.ben-manes.versions") version ("0.21.0")
}

apply {
    plugin("ca.cutterslade.analyze")
}

allprojects {
    repositories {
        jcenter()
    }

    tasks {
        "dependencyUpdates"(com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class) {
            resolutionStrategy {
                componentSelection {
                    all {
                        val rejected = listOf("alpha", "beta", "rc", "cr", "m")
                                .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                                .any { it.matches(candidate.version) }
                        if (rejected) {
                            reject("Release candidate")
                        }
                    }
                }
            }
        }
    }
}

bintray {
    user = project.findProperty("bintray.user") as String?
    key = project.findProperty("bintray.api") as String?
    pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {
        repo = "generic"
        name = "unity-packager"
        setLicenses("Apache-2.0")
        vcsUrl = "https://github.com/ngyewch/UnityPackager.git"
    })
}
