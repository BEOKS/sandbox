package com.cloud.common.multitenancy.resolver

import com.cloud.common.domain.tenant.TenantRepository
import com.cloud.common.multitenancy.TenantIdentifier
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class HeaderTenantResolver(
    private val tenantRepository: TenantRepository
) : TenantResolver {

    companion object {
        const val TENANT_HEADER_NAME = "X-Tenant-ID"
    }

    override fun resolve(request: HttpServletRequest): TenantIdentifier? {
        val tenantId = request.getHeader(TENANT_HEADER_NAME)

        if (tenantId.isNullOrBlank()) {
            logger.debug { "No tenant ID found in header: $TENANT_HEADER_NAME" }
            return null
        }

        return tenantRepository.findByTenantIdAndActive(tenantId, true)
            .map { TenantIdentifier(it.tenantId, it.type) }
            .orElse(null)
            .also {
                if (it != null) {
                    logger.debug { "Resolved tenant from header: $it" }
                } else {
                    logger.warn { "Tenant not found or inactive: $tenantId" }
                }
            }
    }
}
