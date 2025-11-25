package com.cloud.resource

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.cloud"])
@EntityScan(basePackages = ["com.cloud.domain"])
@EnableJpaRepositories(basePackages = ["com.cloud.domain"])
class ResourceApiApplication

fun main(args: Array<String>) {
    runApplication<ResourceApiApplication>(*args)
}
