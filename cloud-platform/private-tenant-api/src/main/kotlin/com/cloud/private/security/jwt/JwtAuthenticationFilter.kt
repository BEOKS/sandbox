package com.cloud.private.security.jwt

import com.cloud.common.domain.tenant.TenantType
import com.cloud.common.multitenancy.TenantContext
import com.cloud.common.multitenancy.TenantIdentifier
import com.cloud.common.security.SecurityConstants
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

private val logger = KotlinLogging.logger {}

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = getJwtFromRequest(request)

            if (jwt != null && tokenProvider.validateToken(jwt)) {
                val username = tokenProvider.getUsernameFromToken(jwt)
                val tenantId = tokenProvider.getTenantIdFromToken(jwt)

                // Set tenant context
                TenantContext.setTenant(TenantIdentifier(tenantId, TenantType.PRIVATE))

                val userDetails = userDetailsService.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authentication
                logger.debug { "Set authentication for user: $username, tenant: $tenantId" }
            }
        } catch (ex: Exception) {
            logger.error(ex) { "Could not set user authentication in security context" }
        }

        filterChain.doFilter(request, response)
    }

    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(SecurityConstants.AUTH_HEADER)

        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            bearerToken.substring(SecurityConstants.TOKEN_PREFIX.length)
        } else {
            null
        }
    }
}
