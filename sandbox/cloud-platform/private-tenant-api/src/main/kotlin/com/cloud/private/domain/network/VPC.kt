package com.cloud.private.domain.network

import com.cloud.common.domain.audit.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "vpcs")
class VPC(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val tenantId: String,

    @Column(nullable = false, length = 200)
    val name: String,

    @Column(nullable = false, length = 50)
    val cidrBlock: String,

    @Column(nullable = false, length = 50)
    val region: String,

    @Column(nullable = false)
    var enableDnsSupport: Boolean = true,

    @Column(nullable = false)
    var enableDnsHostnames: Boolean = true
) : BaseEntity()
