plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1" // Add the Shadow Plugin
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    // Specify the fully qualified name of your main class.
    mainClass.set("dbhq.Main")
}

group = "dbhq"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // Define your project dependencies here
    implementation("org.javacord:javacord:3.8.0")
    implementation("org.apache.logging.log4j:log4j-core:2.22.1")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.3")
    implementation("org.json:json:20231013")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    // Customize the Shadow JAR
    archiveClassifier.set("") // Remove the classifier to replace the default JAR
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}
