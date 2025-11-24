package com.cloud.domain.policy

import com.cloud.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "policies")
class Policy(
    @Column(nullable = false, unique = true, length = 100)
    var name: String,

    @Column(length = 1000)
    var description: String? = null,

    @Column(nullable = false)
    var enabled: Boolean = true,

    @Column(nullable = false)
    var priority: Int = 0,

    @OneToMany(mappedBy = "policy", cascade = [CascadeType.ALL], orphanRemoval = true)
    var rules: MutableList<PolicyRule> = mutableListOf()
) : BaseEntity() {

    fun addRule(rule: PolicyRule) {
        rules.add(rule)
        rule.policy = this
    }

    fun removeRule(rule: PolicyRule) {
        rules.remove(rule)
        rule.policy = null
    }
}
