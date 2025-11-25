plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":common"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
}
