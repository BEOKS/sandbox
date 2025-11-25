package com.cloud.private.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Component
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        logger.error(authException) { "Unauthorized error: ${authException.message}" }

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val body = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to HttpServletResponse.SC_UNAUTHORIZED,
            "error" to "Unauthorized",
            "message" to "Full authentication is required to access this resource",
            "path" to request.requestURI
        )

        objectMapper.writeValue(response.outputStream, body)
    }
}
