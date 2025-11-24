package com.cloud.common.domain.tenant

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TenantRepository : JpaRepository<Tenant, Long> {

    fun findByTenantId(tenantId: String): Optional<Tenant>

    fun findByTenantIdAndActive(tenantId: String, active: Boolean): Optional<Tenant>

    fun findAllByType(type: TenantType): List<Tenant>

    fun findAllByActive(active: Boolean): List<Tenant>

    fun existsByTenantId(tenantId: String): Boolean
}
