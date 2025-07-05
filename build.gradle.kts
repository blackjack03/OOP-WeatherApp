import com.github.spotbugs.snom.SpotBugsTask

plugins {
    // Apply the java plugin to add support for Java
    java

    // Apply the application plugin to add support for building a CLI application
    // You can run your app via task "run": ./gradlew run
    application

    /*
     * Adds tasks to export a runnable jar.
     * In order to create it, launch the "shadowJar" task.
     * The runnable jar will be found in build/libs/projectname-all.jar
     */
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.danilopianini.gradle-java-qa") version "1.96.0"
    id("org.openjfx.javafxplugin") version "0.0.14"
}

tasks.withType<SpotBugsTask>().configureEach {
    reports.apply {
        create("html").required.set(true)
    }
}

repositories { // Where to search for dependencies
    mavenCentral()
}

val javaFxVersion = "22"
val javaFXModules = listOf("base", "controls", "fxml", "swing", "graphics")

/* 1a. Runtime / test ⇒ only the correct variant detected by the plugin */
javafx {
    version = javaFxVersion
    modules = javaFXModules.map { "javafx.$it" }
}

/* Extra Config: All JavaFX variants for shadowJar only */
val javafxAll = configurations.create("javafxAll")

val supportedPlatforms = listOf("linux", "mac", "win", "mac-aarch64") // All required for OOP

dependencies {
    /* ---- Universal JavaFX for shadowJar ---- */
    supportedPlatforms.forEach { platform ->
        javaFXModules.forEach { mod ->
            add("javafxAll", "org.openjfx:javafx-$mod:$javaFxVersion:$platform")
        }
    }

    // Import libraries
    // Maven dependencies are composed by a group name, a name and a version, separated by colons
    implementation("org.jooq:jool:0.9.15")
    implementation("org.jsoup:jsoup:1.18.1")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.opencsv:opencsv:5.11.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.15.2")

    /*
     * Simple Logging Facade for Java (SLF4J) with Apache Log4j
     * See: http://www.slf4j.org/
     */
    val slf4jVersion = "2.0.17"
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    // Logback backend for SLF4J
    runtimeOnly("ch.qos.logback:logback-classic:1.5.18")

    // JUnit API and testing engine
    val jUnitVersion = "5.11.3"
    // when dependencies share the same version, grouping in a val helps to keep them in sync
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")

    /* SpotBugs annotations (compile-only) */
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.6")
}

application {
    // Define the main class for the application.
    mainClass.set("org.app.Main")
}

sourceSets {
    main {
        resources.srcDir("src/main/resources")
    }
}

// Link shadowJar to build
tasks.named("build") {
    dependsOn("shadowJar")
}

/* Shadow-jar */
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("WeatherTravelApp")
    mergeServiceFiles()                  // Required for JavaFX ServiceLoader

    configurations = listOf(
        project.configurations.runtimeClasspath.get(),
        javafxAll
    )

    manifest {
        attributes["Main-Class"] = application.mainClass.get() // Jar executable via double-click
    }
}

// disable the “slim” jar
tasks.named<Jar>("jar") {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events(*org.gradle.api.tasks.testing.logging.TestLogEvent.values())
        showStandardStreams = true
    }
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
