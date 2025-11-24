package com.cloud.common.security

data class AuthenticationResult(
    val accessToken: String,
    val refreshToken: String? = null,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val tenantId: String,
    val userId: Long
)
