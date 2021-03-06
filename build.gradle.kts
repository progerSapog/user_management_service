import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "me.vladsapozhnikov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.3")
    testImplementation(platform("io.strikt:strikt-bom:0.28.2"))
    testImplementation("io.strikt:strikt-core:0.34.1")
    testImplementation("io.strikt:strikt-java-time:0.28.2")
    testImplementation("io.dropwizard:dropwizard-testing:2.0.28")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")


    implementation("com.h2database:h2:2.1.210")
    implementation("io.dropwizard:dropwizard-core:2.0.28")
    implementation("io.dropwizard:dropwizard-jdbi:1.3.29")
    implementation("io.dropwizard:dropwizard-migrations:2.0.28")
    implementation("io.dropwizard:dropwizard-jdbi3:2.0.28")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.7")
    implementation("com.smoketurner:dropwizard-swagger:2.0.12-1")
    implementation("org.jdbi:jdbi3-kotlin:3.28.0")
    implementation("org.jdbi:jdbi3-kotlin-sqlobject:3.28.0")
    implementation("org.kodein.di:kodein-di-generic-jvm:6.1.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("com.sapozhnikov.ManagementServiceApp")
}