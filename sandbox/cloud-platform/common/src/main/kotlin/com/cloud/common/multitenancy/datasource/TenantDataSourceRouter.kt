package com.cloud.common.multitenancy.datasource

import com.cloud.common.multitenancy.TenantContext
import mu.KotlinLogging
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource

private val logger = KotlinLogging.logger {}

class TenantDataSourceRouter : AbstractRoutingDataSource() {

    override fun determineCurrentLookupKey(): Any? {
        val tenant = TenantContext.getTenant()
        logger.debug { "Determining DataSource for tenant: $tenant" }
        return tenant?.tenantId
    }

    override fun determineTargetDataSource() = try {
        super.determineTargetDataSource()
    } catch (e: IllegalStateException) {
        logger.error(e) { "Failed to determine target DataSource" }
        throw e
    }
}
