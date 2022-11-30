plugins {
    java
    `java-library`
}

// subprojects
subprojects {

    plugins.apply("java")
    plugins.apply("java-library")

    group = "org.example"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}