plugins {
    id("java")
}

group = "dbhq"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.javacord:javacord:3.8.0")
    implementation("org.apache.logging.log4j:log4j-core:2.22.1")
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.3")
    implementation("org.json:json:20231013")
}

tasks.test {
    useJUnitPlatform()
}