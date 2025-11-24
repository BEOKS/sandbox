package com.cloud.policy.evaluator

import com.cloud.domain.policy.PolicyEffect
import com.cloud.domain.policy.PolicyRepository
import com.cloud.policy.condition.ConditionEvaluator
import com.cloud.policy.model.EvaluationContext
import com.cloud.policy.model.PolicyDecision
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class DefaultPolicyEvaluator(
    private val policyRepository: PolicyRepository,
    private val conditionEvaluator: ConditionEvaluator
) : PolicyEvaluator {

    @Cacheable(value = ["policyDecisions"], key = "#context.subject.userId + ':' + #context.action + ':' + #context.resource.resourceType")
    override fun evaluate(context: EvaluationContext): PolicyDecision {
        logger.debug { "Evaluating policy for user=${context.subject.userId}, action=${context.action}, resource=${context.resource.resourceType}" }

        val policies = policyRepository.findAllEnabled()
        val matchedPolicies = mutableListOf<String>()
        var explicitDeny = false
        var explicitAllow = false

        for (policy in policies) {
            for (rule in policy.rules) {
                // 액션 패턴 매칭
                if (!matchesPattern(rule.actionPattern, context.action)) {
                    continue
                }

                // 리소스 패턴 매칭
                if (!matchesResourcePattern(rule.resourcePattern, context.resource)) {
                    continue
                }

                // 조건 평가
                val condition = rule.condition
                if (condition != null && !conditionEvaluator.evaluate(condition, context)) {
                    continue
                }

                // 정책 매칭
                matchedPolicies.add(policy.name)
                logger.debug { "Matched policy: ${policy.name}, rule: ${rule.name}, effect: ${rule.effect}" }

                when (rule.effect) {
                    PolicyEffect.DENY -> {
                        explicitDeny = true
                        return PolicyDecision.deny(
                            reason = "Explicit deny by policy: ${policy.name}, rule: ${rule.name}",
                            matchedPolicies = matchedPolicies
                        )
                    }
                    PolicyEffect.ALLOW -> {
                        explicitAllow = true
                    }
                }
            }
        }

        return if (explicitAllow) {
            PolicyDecision.allow(
                reason = "Explicit allow by matched policies",
                matchedPolicies = matchedPolicies
            )
        } else {
            PolicyDecision.deny(
                reason = "No explicit allow found (implicit deny)",
                matchedPolicies = matchedPolicies
            )
        }
    }

    private fun matchesPattern(pattern: String, value: String): Boolean {
        // 와일드카드 패턴 매칭 (*를 지원)
        val regex = pattern
            .replace(".", "\\.")
            .replace("*", ".*")
            .toRegex()
        return regex.matches(value)
    }

    private fun matchesResourcePattern(pattern: String, resource: EvaluationContext.Resource): Boolean {
        // 리소스 패턴: type:id 또는 type:* 형식
        // 예: vm:*, s3:bucket-123, *:*
        val parts = pattern.split(":")
        if (parts.size != 2) {
            return false
        }

        val typePattern = parts[0]
        val idPattern = parts[1]

        // 타입 매칭
        if (!matchesPattern(typePattern, resource.resourceType)) {
            return false
        }

        // ID 매칭 (resourceId가 null이면 새로운 리소스 생성으로 간주)
        if (resource.resourceId != null && idPattern != "*") {
            if (!matchesPattern(idPattern, resource.resourceId)) {
                return false
            }
        }

        return true
    }
}
