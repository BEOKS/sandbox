plugins {
    java
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.liquibase.gradle") version "2.2.1"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.liquibase:liquibase-core")

    // H2 (개발/테스트용)
    runtimeOnly("com.h2database:h2")
    // Oracle
    runtimeOnly("com.oracle.database.jdbc:ojdbc11:23.3.0.23.09")

    // Liquibase Gradle 플러그인용
    liquibaseRuntime("org.liquibase:liquibase-core:4.25.1")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:3.0.3")
    liquibaseRuntime("info.picocli:picocli:4.7.5")
    liquibaseRuntime("com.h2database:h2")
    liquibaseRuntime("com.oracle.database.jdbc:ojdbc11:23.3.0.23.09")
    liquibaseRuntime("javax.xml.bind:jaxb-api:2.3.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// 환경변수에서 DB 설정 가져오기 (기본값: H2)
val dbUrl: String = System.getenv("DB_URL") ?: "jdbc:h2:mem:liquibasedb;INIT=CREATE SCHEMA IF NOT EXISTS CLIENT"
val dbUsername: String = System.getenv("DB_USERNAME") ?: "sa"
val dbPassword: String = System.getenv("DB_PASSWORD") ?: ""
val dbSchema: String = System.getenv("DB_SCHEMA") ?: "CLIENT"

// SQL 추출을 위한 Liquibase 설정
liquibase {
    activities.register("main") {
        this.arguments = mapOf(
            "changelogFile" to "db/changelog/db.changelog-master.yaml",
            "url" to dbUrl,
            "username" to dbUsername,
            "password" to dbPassword,
            "defaultSchemaName" to dbSchema,
            "searchPath" to "src/main/resources",
            "outputFile" to "build/liquibase/migration.sql"
        )
    }
    // Oracle용 오프라인 SQL 생성 (실제 DB 연결 없이)
    activities.register("oracle") {
        this.arguments = mapOf(
            "changelogFile" to "db/changelog/db.changelog-master.yaml",
            "url" to "offline:oracle",
            "defaultSchemaName" to dbSchema,
            "searchPath" to "src/main/resources",
            "outputFile" to "build/liquibase/migration-oracle.sql"
        )
    }
    runList = System.getenv("LIQUIBASE_ACTIVITY") ?: "main"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
