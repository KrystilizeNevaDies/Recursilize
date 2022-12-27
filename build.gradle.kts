plugins {
    java
    `java-library`
    `maven-publish`
}

// subprojects
subprojects {

    plugins.apply("java")
    plugins.apply("java-library")
    plugins.apply("maven-publish")

    group = "org.example"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    java {
        withJavadocJar()
        withSourcesJar()
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }
    }
}