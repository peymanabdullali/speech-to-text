plugins {
    id("java")
}

group = "az"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.assemblyai:assemblyai-java:1.0.10")
}

tasks.test {
    useJUnitPlatform()
}