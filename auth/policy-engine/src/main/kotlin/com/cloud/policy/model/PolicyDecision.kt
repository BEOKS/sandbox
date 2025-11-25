package com.cloud.policy.model

data class PolicyDecision(
    val allowed: Boolean,
    val reason: String? = null,
    val matchedPolicies: List<String> = emptyList()
) {
    companion object {
        fun allow(reason: String? = null, matchedPolicies: List<String> = emptyList()): PolicyDecision {
            return PolicyDecision(allowed = true, reason = reason, matchedPolicies = matchedPolicies)
        }

        fun deny(reason: String, matchedPolicies: List<String> = emptyList()): PolicyDecision {
            return PolicyDecision(allowed = false, reason = reason, matchedPolicies = matchedPolicies)
        }
    }
}
