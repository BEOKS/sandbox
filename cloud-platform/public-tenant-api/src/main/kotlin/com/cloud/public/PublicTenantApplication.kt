package com.cloud.public

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.cloud"])
@ConfigurationPropertiesScan("com.cloud")
class PublicTenantApplication

fun main(args: Array<String>) {
    runApplication<PublicTenantApplication>(*args)
}
