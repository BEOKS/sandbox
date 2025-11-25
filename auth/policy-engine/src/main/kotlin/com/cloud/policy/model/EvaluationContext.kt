package com.cloud.policy.model

data class EvaluationContext(
    val subject: Subject,
    val resource: Resource,
    val action: String,
    val environment: Environment
) {
    data class Subject(
        val userId: String,
        val roles: Set<String> = emptySet(),
        val attributes: Map<String, Any> = emptyMap()
    )

    data class Resource(
        val resourceId: String?,
        val resourceType: String,
        val ownerId: String?,
        val tags: Map<String, String> = emptyMap(),
        val attributes: Map<String, Any> = emptyMap()
    )

    data class Environment(
        val timestamp: Long = System.currentTimeMillis(),
        val ipAddress: String? = null,
        val userAgent: String? = null,
        val region: String? = null,
        val attributes: Map<String, Any> = emptyMap()
    )
}
