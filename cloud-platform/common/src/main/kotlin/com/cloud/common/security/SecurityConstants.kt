package com.cloud.common.security

object SecurityConstants {
    const val AUTH_HEADER = "Authorization"
    const val TOKEN_PREFIX = "Bearer "
    const val TENANT_HEADER = "X-Tenant-ID"

    // Public endpoints (no authentication required)
    val PUBLIC_ENDPOINTS = arrayOf(
        "/api/auth/login",
        "/api/auth/register",
        "/api/health",
        "/actuator/**",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    )
}
