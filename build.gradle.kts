import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    id("org.openjfx.javafxplugin") version "0.0.10"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "dev.kason"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "17-ea+11"
    modules = listOf("controls", "graphics", "media", "swing", "fxml").map { "javafx.$it" }
}

dependencies {
    implementation("com.google.guava:guava:31.1-jre")
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("org.slf4j:jul-to-slf4j:1.7.36")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}