package com.cloud.domain.resource

import jakarta.persistence.*

@Entity
@Table(name = "virtual_machines")
class VirtualMachine(
    resourceId: String,
    name: String,
    ownerId: String,
    description: String? = null,

    @Column(nullable = false, length = 50)
    var instanceType: String,

    @Column(nullable = false)
    var cpuCores: Int,

    @Column(nullable = false)
    var memoryGb: Int,

    @Column(nullable = false)
    var diskGb: Int,

    @Column(length = 100)
    var imageId: String? = null,

    @Column(length = 50)
    var publicIpAddress: String? = null,

    @Column(length = 50)
    var privateIpAddress: String? = null,

    @Column(length = 100)
    var vpcId: String? = null,

    @Column(length = 100)
    var subnetId: String? = null
) : Resource(
    resourceId = resourceId,
    name = name,
    type = ResourceType.VM,
    ownerId = ownerId,
    description = description
)
