package com.cloud.common.multitenancy.resolver

import com.cloud.common.domain.tenant.TenantRepository
import com.cloud.common.multitenancy.TenantIdentifier
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class DomainTenantResolver(
    private val tenantRepository: TenantRepository
) : TenantResolver {

    override fun resolve(request: HttpServletRequest): TenantIdentifier? {
        val serverName = request.serverName
        logger.debug { "Resolving tenant from domain: $serverName" }

        // 도메인 기반 테넌트 식별 로직
        // 예: tenant1.cloud.com -> tenant1
        val tenantId = extractTenantIdFromDomain(serverName)

        if (tenantId.isNullOrBlank()) {
            logger.debug { "Could not extract tenant ID from domain: $serverName" }
            return null
        }

        return tenantRepository.findByTenantIdAndActive(tenantId, true)
            .map { TenantIdentifier(it.tenantId, it.type) }
            .orElse(null)
            .also {
                if (it != null) {
                    logger.debug { "Resolved tenant from domain: $it" }
                } else {
                    logger.warn { "Tenant not found or inactive: $tenantId" }
                }
            }
    }

    private fun extractTenantIdFromDomain(serverName: String): String? {
        // 예: tenant1.cloud.com -> tenant1
        // 예: tenant1-api.cloud.com -> tenant1
        val parts = serverName.split(".")
        return if (parts.size > 2) {
            parts[0].split("-")[0]
        } else {
            null
        }
    }
}
