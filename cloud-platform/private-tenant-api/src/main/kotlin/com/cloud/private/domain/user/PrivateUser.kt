package com.cloud.private.domain.user

import com.cloud.common.domain.audit.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
class PrivateUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val tenantId: String,

    @Column(nullable = false, unique = true, length = 100)
    val username: String,

    @Column(nullable = false, length = 500)
    var password: String,

    @Column(nullable = false, length = 200)
    var email: String,

    @Column(length = 100)
    var fullName: String? = null,

    @Column(length = 50)
    var phone: String? = null,

    @Column(nullable = false, length = 200)
    var roles: String = "USER",

    @Column(nullable = false)
    var active: Boolean = true
) : BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrivateUser) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "PrivateUser(id=$id, username='$username', email='$email', tenantId='$tenantId')"
    }
}
