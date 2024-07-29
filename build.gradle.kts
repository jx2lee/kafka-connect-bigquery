description = "Coinone BigQuery Sink Connector"

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

group = "com.coinone"
version = "1.0-SNAPSHOT"

val connectApiVersion by extra("3.6.1")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:connect-api:$connectApiVersion")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}