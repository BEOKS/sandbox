plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

dependencies {
    // Spring
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework:spring-context")
    api("org.springframework:spring-web")

    // Spring Security
    api("org.springframework.security:spring-security-core")

    // Validation
    api("org.springframework.boot:spring-boot-starter-validation")

    // Logging
    api("io.github.oshai:kotlin-logging-jvm:7.0.0")
}
