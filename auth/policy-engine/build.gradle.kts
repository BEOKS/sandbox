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
    api(project(":common"))
    api(project(":domain"))

    // Spring Security
    api("org.springframework.security:spring-security-core")
    api("org.springframework.security:spring-security-web")

    // JSON Processing
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

    // Expression Language (for policy evaluation)
    implementation("org.springframework:spring-expression")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
}
