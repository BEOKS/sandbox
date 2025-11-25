package com.cloud.public.security.certificate

import com.cloud.common.domain.tenant.TenantType
import com.cloud.common.multitenancy.TenantContext
import com.cloud.common.multitenancy.TenantIdentifier
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private val logger = KotlinLogging.logger {}

@Component
class CertificateAuthenticationFilter(
    private val certificateExtractor: X509CertificateExtractor,
    private val certificateValidator: CertificateValidator
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val certificate = certificateExtractor.extractCertificate(request)

            if (certificate != null && certificateValidator.validate(certificate)) {
                val tenantId = certificateValidator.extractTenantId(certificate)
                val username = certificateValidator.extractUsername(certificate)

                // Set tenant context
                TenantContext.setTenant(TenantIdentifier(tenantId, TenantType.PUBLIC))

                // Create authentication
                val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
                val authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authentication
                logger.debug { "Set authentication for user: $username, tenant: $tenantId" }
            } else {
                logger.debug { "No valid certificate found, proceeding without authentication" }
            }
        } catch (ex: Exception) {
            logger.error(ex) { "Could not set user authentication from certificate" }
        }

        filterChain.doFilter(request, response)
    }
}
