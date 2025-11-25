package com.cloud.common.multitenancy.datasource

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.springframework.stereotype.Component
import javax.sql.DataSource

private val logger = KotlinLogging.logger {}

@Component
class DataSourceManager {

    private val dataSources = mutableMapOf<String, DataSource>()

    fun createDataSource(config: DataSourceConfig): DataSource {
        logger.info { "Creating DataSource for tenant: ${config.tenantId}" }

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.url
            username = config.username
            password = config.password
            driverClassName = config.driverClassName
            maximumPoolSize = config.maxPoolSize
            poolName = "HikariPool-${config.tenantId}"
            isAutoCommit = true
            connectionTimeout = 30000
            idleTimeout = 600000
            maxLifetime = 1800000
        }

        return HikariDataSource(hikariConfig)
    }

    fun getDataSource(tenantId: String): DataSource? {
        return dataSources[tenantId]
    }

    fun addDataSource(tenantId: String, dataSource: DataSource) {
        dataSources[tenantId] = dataSource
        logger.info { "DataSource added for tenant: $tenantId" }
    }

    fun removeDataSource(tenantId: String) {
        dataSources.remove(tenantId)?.let {
            if (it is HikariDataSource) {
                it.close()
            }
            logger.info { "DataSource removed for tenant: $tenantId" }
        }
    }

    fun getAllDataSources(): Map<String, DataSource> {
        return dataSources.toMap()
    }
}
