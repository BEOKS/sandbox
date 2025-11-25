package com.cloud.private.config

import com.cloud.common.domain.tenant.TenantRepository
import com.cloud.common.domain.tenant.TenantType
import com.cloud.common.multitenancy.datasource.DataSourceManager
import com.cloud.common.multitenancy.datasource.TenantDataSourceProperties
import com.cloud.common.multitenancy.datasource.TenantDataSourceRouter
import mu.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

private val logger = KotlinLogging.logger {}

@Configuration
@EnableConfigurationProperties(TenantDataSourceProperties::class)
class PrivateDataSourceConfig(
    private val tenantProperties: TenantDataSourceProperties,
    private val dataSourceManager: DataSourceManager,
    private val tenantRepository: TenantRepository
) {

    @Bean
    @Primary
    fun tenantDataSource(): DataSource {
        logger.info { "Initializing tenant DataSource router for PRIVATE tenants" }

        val router = TenantDataSourceRouter()
        val targetDataSources = mutableMapOf<Any, Any>()

        // Load datasources for PRIVATE tenants from configuration
        tenantProperties.datasources
            .filter { config ->
                // Filter only PRIVATE tenants
                tenantRepository.findByTenantId(config.tenantId)
                    .map { it.type == TenantType.PRIVATE }
                    .orElse(true) // Allow if tenant not yet in DB (initial setup)
            }
            .forEach { config ->
                val dataSource = dataSourceManager.createDataSource(config)
                targetDataSources[config.tenantId] = dataSource
                dataSourceManager.addDataSource(config.tenantId, dataSource)
                logger.info { "Registered DataSource for tenant: ${config.tenantId}" }
            }

        router.setTargetDataSources(targetDataSources)
        router.afterPropertiesSet()

        logger.info { "Tenant DataSource router initialized with ${targetDataSources.size} data sources" }
        return router
    }
}
