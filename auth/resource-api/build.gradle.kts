plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":common"))
    implementation(project(":domain"))
    implementation(project(":policy-engine"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // OAuth2 Resource Server
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
}
