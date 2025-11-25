plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("io.spring.dependency-management")
}

dependencies {
    // Spring Boot
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-validation")

    // Database
    api("org.postgresql:postgresql")
    api("com.zaxxer:HikariCP")

    // Flyway
    api("org.flywaydb:flyway-core")
    api("org.flywaydb:flyway-database-postgresql")
}
