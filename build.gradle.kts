plugins {
    kotlin("jvm") version "2.1.0"
}

group = "trd"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jsoup:jsoup:1.22.1")
    implementation("org.json:json:20251224")
    implementation("org.seleniumhq.selenium:selenium-java:4.48.0")
}

kotlin {
    jvmToolchain(11)
}