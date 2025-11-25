package com.cloud.common.multitenancy.resolver

import com.cloud.common.multitenancy.TenantIdentifier
import jakarta.servlet.http.HttpServletRequest

interface TenantResolver {
    fun resolve(request: HttpServletRequest): TenantIdentifier?
}
