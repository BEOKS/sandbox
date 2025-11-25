package com.cloud.common.multitenancy

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

object TenantContext {
    private val currentTenant = ThreadLocal<TenantIdentifier>()

    fun setTenant(identifier: TenantIdentifier) {
        logger.debug { "Setting tenant context: $identifier" }
        currentTenant.set(identifier)
    }

    fun getTenant(): TenantIdentifier? {
        return currentTenant.get()
    }

    fun getTenantOrThrow(): TenantIdentifier {
        return getTenant() ?: throw IllegalStateException("No tenant context set")
    }

    fun clear() {
        logger.debug { "Clearing tenant context" }
        currentTenant.remove()
    }
}
