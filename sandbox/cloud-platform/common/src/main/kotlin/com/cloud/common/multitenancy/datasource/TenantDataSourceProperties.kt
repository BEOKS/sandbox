package com.cloud.common.multitenancy.datasource

import com.cloud.common.domain.tenant.TenantType
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tenant")
data class TenantDataSourceProperties(
    val type: TenantType,
    val datasources: List<DataSourceConfig> = emptyList()
)

data class DataSourceConfig(
    val tenantId: String,
    val url: String,
    val username: String,
    val password: String,
    val driverClassName: String = "org.postgresql.Driver",
    val maxPoolSize: Int = 10
)
