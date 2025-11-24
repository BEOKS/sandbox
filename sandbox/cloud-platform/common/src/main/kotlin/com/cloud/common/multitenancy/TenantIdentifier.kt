package com.cloud.common.multitenancy

import com.cloud.common.domain.tenant.TenantType

data class TenantIdentifier(
    val tenantId: String,
    val type: TenantType
) {
    override fun toString(): String = tenantId
}
