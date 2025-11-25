package com.cloud.domain.policy

import com.cloud.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "policy_rules")
class PolicyRule(
    @Column(nullable = false, length = 200)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    var effect: PolicyEffect = PolicyEffect.ALLOW,

    @Column(length = 1000)
    var description: String? = null,

    @Column(name = "action_pattern", nullable = false, length = 200)
    var actionPattern: String,

    @Column(name = "resource_pattern", nullable = false, length = 200)
    var resourcePattern: String,

    @Column(columnDefinition = "TEXT")
    var condition: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    var policy: Policy? = null
) : BaseEntity()
