plugins {
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.6"
    java
    application
}

group = "ck.apps"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    // Keep local first so the project can build in offline environments.
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.18.0")
    implementation("commons-io:commons-io:2.18.0")
    implementation("com.google.guava:guava:33.3.1-jre")
    implementation("org.jsoup:jsoup:1.10.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.2")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Audio codec SPI dependencies used by runtime playback path.
    runtimeOnly("com.googlecode.soundlibs:tritonus-share:0.3.7-2")
    runtimeOnly("com.googlecode.soundlibs:mp3spi:1.9.5-1")
    runtimeOnly("com.googlecode.soundlibs:vorbisspi:1.0.3-1")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.hamcrest:hamcrest:3.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("ck.apps.leabharcleachtadh.games.Practice")
}

springBoot {
    mainClass.set("ck.apps.leabharcleachtadh.api.PracticeApiApplication")
    buildInfo()
    buildImage {
        imageName.set("naturallang:latest")
        docker {
            publish.set(false)
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ck.apps.leabharcleachtadh.games.Practice"
    }
}

val fatJar by tasks.registering(Jar::class) {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = "ck.apps.leabharcleachtadh.games.Practice"
    }
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith(".jar") }
            .map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
