package com.cloud.private.domain.vm

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VMRepository : JpaRepository<VirtualMachine, Long> {

    fun findByTenantId(tenantId: String): List<VirtualMachine>

    fun findByTenantIdAndId(tenantId: String, id: Long): VirtualMachine?

    fun findByTenantIdAndStatus(tenantId: String, status: VMStatus): List<VirtualMachine>
}
