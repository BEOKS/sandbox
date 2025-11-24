plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":domain"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Security OAuth2 Authorization Server
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server:1.3.2")

    // JWT
    implementation("org.springframework.security:spring-security-oauth2-jose")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")
}
