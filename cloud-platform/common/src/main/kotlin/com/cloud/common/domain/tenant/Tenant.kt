package com.cloud.common.domain.tenant

import com.cloud.common.domain.audit.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "tenants")
class Tenant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 100)
    val tenantId: String,

    @Column(nullable = false, length = 200)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val type: TenantType,

    @Column(nullable = false, length = 500)
    val databaseUrl: String,

    @Column(nullable = false, length = 100)
    val databaseUsername: String,

    @Column(nullable = false, length = 500)
    val databasePassword: String,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(length = 1000)
    var description: String? = null,

    @Column(length = 200)
    var contactEmail: String? = null,

    @Column(length = 50)
    var contactPhone: String? = null
) : BaseEntity() {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tenant) return false
        return tenantId == other.tenantId
    }

    override fun hashCode(): Int {
        return tenantId.hashCode()
    }

    override fun toString(): String {
        return "Tenant(id=$id, tenantId='$tenantId', name='$name', type=$type, active=$active)"
    }
}
