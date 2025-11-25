package com.cloud.public.config

import com.cloud.common.security.SecurityConstants
import com.cloud.public.security.certificate.CertificateAuthenticationFilter
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Component
class CertificateAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        logger.error(authException) { "Certificate authentication failed: ${authException.message}" }

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val body = mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "status" to HttpServletResponse.SC_UNAUTHORIZED,
            "error" to "Unauthorized",
            "message" to "Valid client certificate is required",
            "path" to request.requestURI
        )

        objectMapper.writeValue(response.outputStream, body)
    }
}

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class PublicSecurityConfig(
    private val certificateAuthenticationFilter: CertificateAuthenticationFilter,
    private val certificateAuthenticationEntryPoint: CertificateAuthenticationEntryPoint
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { }
            .exceptionHandling { it.authenticationEntryPoint(certificateAuthenticationEntryPoint) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(*SecurityConstants.PUBLIC_ENDPOINTS).permitAll()
                    .anyRequest().authenticated()
            }
            .x509 { it.subjectPrincipalRegex("CN=(.*?)(?:,|$)") }
            .addFilterBefore(certificateAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
