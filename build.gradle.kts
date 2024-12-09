plugins {
    kotlin("jvm") version "1.9.21"
}

group = "trd"
version = "1.0"

val COROUTINES_CORE = "1.6.0"
val JSOUP = "1.15.4"
val JSON = "20231013"

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