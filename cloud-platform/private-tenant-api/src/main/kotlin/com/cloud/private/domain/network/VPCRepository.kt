package com.cloud.private.domain.network

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VPCRepository : JpaRepository<VPC, Long> {

    fun findByTenantId(tenantId: String): List<VPC>

    fun findByTenantIdAndId(tenantId: String, id: Long): VPC?
}
