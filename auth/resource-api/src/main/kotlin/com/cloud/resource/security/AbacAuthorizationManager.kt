package com.cloud.resource.security

import com.cloud.policy.evaluator.PolicyEvaluator
import com.cloud.policy.model.EvaluationContext
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.function.Supplier

private val logger = KotlinLogging.logger {}

@Component
class AbacAuthorizationManager(
    private val policyEvaluator: PolicyEvaluator
) : AuthorizationManager<RequestAuthorizationContext> {

    override fun check(
        authentication: Supplier<Authentication>,
        context: RequestAuthorizationContext
    ): AuthorizationDecision {
        val auth = authentication.get()

        if (!auth.isAuthenticated) {
            logger.debug { "User not authenticated" }
            return AuthorizationDecision(false)
        }

        val request = context.request
        val method = request.method
        val uri = request.requestURI

        // JWT에서 사용자 정보 추출
        val jwt = auth.principal as? Jwt
        val userId = jwt?.subject ?: "anonymous"
        val roles = jwt?.getClaimAsStringList("roles")?.toSet() ?: emptySet()
        val userAttributes = jwt?.getClaim<Map<String, Any>>("user_attributes") ?: emptyMap()

        // 요청으로부터 액션과 리소스 타입 추출
        val action = extractAction(method, uri)
        val resourceType = extractResourceType(uri)

        // 평가 컨텍스트 생성
        val evaluationContext = EvaluationContext(
            subject = EvaluationContext.Subject(
                userId = userId,
                roles = roles,
                attributes = userAttributes
            ),
            resource = EvaluationContext.Resource(
                resourceId = null, // 실제로는 URI에서 추출
                resourceType = resourceType,
                ownerId = null // 실제로는 DB에서 조회
            ),
            action = action,
            environment = EvaluationContext.Environment(
                ipAddress = request.remoteAddr,
                userAgent = request.getHeader("User-Agent")
            )
        )

        // 정책 평가
        val decision = policyEvaluator.evaluate(evaluationContext)

        logger.debug { "Authorization decision for user=$userId, action=$action, resource=$resourceType: ${decision.allowed}" }

        return AuthorizationDecision(decision.allowed)
    }

    private fun extractAction(method: String?, uri: String): String {
        return when (method) {
            "GET" -> if (uri.matches(Regex(".*/[0-9a-zA-Z-]+$"))) "read" else "list"
            "POST" -> "create"
            "PUT", "PATCH" -> "update"
            "DELETE" -> "delete"
            else -> "unknown"
        }
    }

    private fun extractResourceType(uri: String): String {
        return when {
            uri.contains("/vms") -> "vm"
            uri.contains("/s3-buckets") -> "s3"
            uri.contains("/vpcs") -> "vpc"
            else -> "unknown"
        }
    }
}
