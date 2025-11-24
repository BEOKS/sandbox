package com.cloud.domain.resource

import jakarta.persistence.*

@Entity
@Table(name = "vpcs")
class VPC(
    resourceId: String,
    name: String,
    ownerId: String,
    description: String? = null,

    @Column(nullable = false, length = 50)
    var cidrBlock: String,

    @Column(nullable = false, length = 50)
    var region: String,

    @Column(nullable = false)
    var enableDnsHostnames: Boolean = true,

    @Column(nullable = false)
    var enableDnsSupport: Boolean = true,

    @ElementCollection
    @CollectionTable(name = "vpc_subnets", joinColumns = [JoinColumn(name = "vpc_id")])
    @Column(name = "subnet_cidr")
    var subnets: MutableSet<String> = mutableSetOf()
) : Resource(
    resourceId = resourceId,
    name = name,
    type = ResourceType.VPC,
    ownerId = ownerId,
    description = description
)
