package com.cloud.domain.policy

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PolicyRepository : JpaRepository<Policy, Long> {
    fun findByName(name: String): Optional<Policy>

    @Query("SELECT p FROM Policy p WHERE p.enabled = true ORDER BY p.priority DESC")
    fun findAllEnabled(): List<Policy>

    fun findByEnabledOrderByPriorityDesc(enabled: Boolean): List<Policy>
}

@Repository
interface PolicyRuleRepository : JpaRepository<PolicyRule, Long> {
    fun findByPolicyId(policyId: Long): List<PolicyRule>
}
