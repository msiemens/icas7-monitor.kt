import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val coroutinesVersion = "1.3.9-native-mt"
val klockVersion = "1.12.0"
val kotlinVersion = "1.4.0"
val ktorVersion = "1.4.0"
val jacksonVersion = "2.11.1"
val serializationVersion = "1.0.0-RC"

plugins {
    application
    kotlin("jvm") version "1.4.0"
    // kotlin("plugin.serialization") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "6.0.0"
    id("com.palantir.graal") version "0.7.1-15-g62b5090"
    id("com.github.ben-manes.versions") version "0.29.0"
}
group = "de.msiemens"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "MainKt"
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx")
}

graal {
    graalVersion("20.2.0")
    javaVersion("11")

    mainClass(application.mainClassName)
    outputName("icas7-monitor")
    option("-H:IncludeResources=logging.properties")
    option("--enable-https")
    option("-O3")

    if (System.getProperty("os.name") == "Linux") {
        option("--static")
        option("--libc=musl")
    }
}

dependencies {
    implementation(kotlin("reflect:$kotlinVersion"))
    implementation("com.soywiz.korlibs.klock:klock:$klockVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-gson:$ktorVersion")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3")
    implementation("io.github.microutils:kotlin-logging:1.8.3")
    implementation("org.slf4j:slf4j-jdk14:1.7.29")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    withType<ShadowJar> {
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }
    }

    withType<Jar> {
        manifest {
            attributes["Main-Class"] = application.mainClassName
        }
    }

    withType<Test> {
        useJUnitPlatform()

        testLogging {
            events("PASSED", "FAILED", "SKIPPED")
        }
    }

    build {
        dependsOn(shadowJar)
    }
}