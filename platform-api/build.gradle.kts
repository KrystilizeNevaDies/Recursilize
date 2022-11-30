dependencies {
    implementation(project(":core"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}