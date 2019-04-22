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
    id("com.github.ben-manes.versions") version ("0.21.0")
}

apply {
    plugin("ca.cutterslade.analyze")
}

allprojects {
    group = "com.github.ngyewch.unitypackager"
    version = "1.0"

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
