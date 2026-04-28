plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.1"
}

group = "at.cc.main"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation("org.jsoup:jsoup:1.22.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.mockito:mockito-core:5.23.0")
    mockitoAgent("org.mockito:mockito-core:5.23.0") { isTransitive = false }
    mockitoAgent("net.bytebuddy:byte-buddy-agent:1.15.11") { isTransitive = false }
}

tasks.test {
    useJUnitPlatform()

    jvmArgs(mockitoAgent.files.map { "-javaagent:${it.absolutePath}" })
}

tasks.shadowJar {
    archiveFileName.set("crawler.jar")
    archiveVersion.set("")
    archiveClassifier.set("")

    manifest {
        attributes(
            "Main-Class" to "at.cc.main.javawebcrawler.Main",
        )
    }
}