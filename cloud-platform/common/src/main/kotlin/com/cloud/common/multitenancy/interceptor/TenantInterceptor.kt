package com.cloud.common.multitenancy.interceptor

import com.cloud.common.exception.TenantNotFoundException
import com.cloud.common.multitenancy.TenantContext
import com.cloud.common.multitenancy.resolver.TenantResolver
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

private val logger = KotlinLogging.logger {}

@Component
class TenantInterceptor(
    private val tenantResolver: TenantResolver
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val tenant = tenantResolver.resolve(request)
            ?: throw TenantNotFoundException("Unable to resolve tenant from request")

        TenantContext.setTenant(tenant)
        logger.debug { "Tenant context set for request: ${request.requestURI}" }

        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        logger.debug { "Request completed for tenant: ${TenantContext.getTenant()}" }
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        TenantContext.clear()
        logger.debug { "Tenant context cleared after request completion" }
    }
}
