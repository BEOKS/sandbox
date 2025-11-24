package com.cloud.admin

import de.codecentric.boot.admin.server.config.EnableAdminServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.cloud"])
@ConfigurationPropertiesScan("com.cloud")
@EnableAdminServer
class AdminApplication

fun main(args: Array<String>) {
    runApplication<AdminApplication>(*args)
}
