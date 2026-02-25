plugins {
    kotlin("jvm") version "2.1.0"
}

group = "trd"
version = "1.0"

val COROUTINES_CORE = "1.10.2"
val JSOUP = "1.22.1"
val JSON = "20251224"

repositories {
    mavenCentral()
}

dependencies {
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_CORE")

    // Jsoup
    implementation("org.jsoup:jsoup:$JSOUP")

    // Json
    implementation("org.json:json:$JSON")
}

kotlin {
    jvmToolchain(11)
}