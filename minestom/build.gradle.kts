dependencies {
    implementation("com.github.Minestom:Minestom:-SNAPSHOT")
    implementation(project(":core"))
    implementation(project(":platform-api"))
}

tasks.withType(Test::class.java) {
    useJUnitPlatform()
}