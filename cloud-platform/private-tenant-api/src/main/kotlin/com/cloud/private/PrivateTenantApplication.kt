package com.cloud.private

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.cloud"])
@ConfigurationPropertiesScan("com.cloud")
class PrivateTenantApplication

fun main(args: Array<String>) {
    runApplication<PrivateTenantApplication>(*args)
}
