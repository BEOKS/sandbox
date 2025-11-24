package com.cloud.domain.user

import com.cloud.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false, unique = true, length = 100)
    var username: String,

    @Column(nullable = false, length = 255)
    var password: String,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(nullable = false, length = 50)
    var firstName: String,

    @Column(nullable = false, length = 50)
    var lastName: String,

    @Column(nullable = false)
    var enabled: Boolean = true,

    @Column(nullable = false)
    var accountNonLocked: Boolean = true,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var attributes: MutableSet<UserAttribute> = mutableSetOf(),

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role")
    var roles: MutableSet<String> = mutableSetOf()
) : BaseEntity() {

    fun addAttribute(key: String, value: String) {
        attributes.add(UserAttribute(key = key, value = value, user = this))
    }

    fun removeAttribute(key: String) {
        attributes.removeIf { it.key == key }
    }

    fun getAttribute(key: String): String? {
        return attributes.find { it.key == key }?.value
    }
}
