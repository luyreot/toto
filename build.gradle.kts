plugins {
    kotlin("jvm") version "1.9.21"
}

group = "trd"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    implementation("org.jsoup:jsoup:1.15.4")

    implementation("org.json:json:20231013")
}

kotlin {
    jvmToolchain(11)
}