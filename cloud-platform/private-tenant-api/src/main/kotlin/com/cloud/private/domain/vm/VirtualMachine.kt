package com.cloud.private.domain.vm

import com.cloud.common.domain.audit.BaseEntity
import jakarta.persistence.*

enum class VMStatus {
    PENDING,
    RUNNING,
    STOPPED,
    TERMINATED,
    ERROR
}

@Entity
@Table(name = "virtual_machines")
class VirtualMachine(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val tenantId: String,

    @Column(nullable = false, length = 200)
    val name: String,

    @Column(nullable = false, length = 50)
    val instanceType: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: VMStatus = VMStatus.PENDING,

    @Column(length = 50)
    var ipAddress: String? = null,

    @Column(length = 50)
    var publicIpAddress: String? = null,

    @Column
    var vpcId: Long? = null,

    @Column(length = 100)
    var imageId: String? = null,

    @Column
    var cpuCores: Int = 1,

    @Column
    var memoryGb: Int = 1,

    @Column
    var diskGb: Int = 10
) : BaseEntity() {

    override fun toString(): String {
        return "VirtualMachine(id=$id, name='$name', status=$status, instanceType='$instanceType')"
    }
}
