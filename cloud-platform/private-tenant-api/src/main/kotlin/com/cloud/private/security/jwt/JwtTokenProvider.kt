package com.cloud.private.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

private val logger = KotlinLogging.logger {}

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
) {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray())
    }

    fun generateToken(username: String, tenantId: String, userId: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        logger.debug { "Generating JWT token for user: $username, tenant: $tenantId" }

        return Jwts.builder()
            .setSubject(username)
            .claim("tenantId", tenantId)
            .claim("userId", userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun generateToken(authentication: Authentication, tenantId: String, userId: Long): String {
        return generateToken(authentication.name, tenantId, userId)
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: Exception) {
            logger.error(ex) { "Invalid JWT token" }
            false
        }
    }

    fun getUsernameFromToken(token: String): String {
        val claims = getClaimsFromToken(token)
        return claims.subject
    }

    fun getTenantIdFromToken(token: String): String {
        val claims = getClaimsFromToken(token)
        return claims["tenantId"] as String
    }

    fun getUserIdFromToken(token: String): Long {
        val claims = getClaimsFromToken(token)
        return (claims["userId"] as Number).toLong()
    }

    private fun getClaimsFromToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}
