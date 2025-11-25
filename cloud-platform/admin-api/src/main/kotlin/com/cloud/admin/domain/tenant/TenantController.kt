package com.cloud.admin.domain.tenant

import com.cloud.common.domain.tenant.Tenant
import com.cloud.common.domain.tenant.TenantRepository
import com.cloud.common.domain.tenant.TenantType
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

data class CreateTenantRequest(
    val tenantId: String,
    val name: String,
    val type: TenantType,
    val databaseUrl: String,
    val databaseUsername: String,
    val databasePassword: String,
    val description: String? = null,
    val contactEmail: String? = null
)

data class UpdateTenantRequest(
    val name: String? = null,
    val active: Boolean? = null,
    val description: String? = null,
    val contactEmail: String? = null
)

@RestController
@RequestMapping("/api/admin/tenants")
class TenantController(
    private val tenantRepository: TenantRepository
) {

    @PostMapping
    fun createTenant(@RequestBody request: CreateTenantRequest): ResponseEntity<Tenant> {
        if (tenantRepository.existsByTenantId(request.tenantId)) {
            return ResponseEntity.badRequest().build()
        }

        val tenant = Tenant(
            tenantId = request.tenantId,
            name = request.name,
            type = request.type,
            databaseUrl = request.databaseUrl,
            databaseUsername = request.databaseUsername,
            databasePassword = request.databasePassword,
            description = request.description,
            contactEmail = request.contactEmail
        )

        val saved = tenantRepository.save(tenant)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }

    @GetMapping
    fun listTenants(): ResponseEntity<List<Tenant>> {
        val tenants = tenantRepository.findAll()
        return ResponseEntity.ok(tenants)
    }

    @GetMapping("/{id}")
    fun getTenant(@PathVariable id: Long): ResponseEntity<Tenant> {
        val tenant = tenantRepository.findById(id)
            .orElseThrow { RuntimeException("Tenant not found: $id") }
        return ResponseEntity.ok(tenant)
    }

    @PutMapping("/{id}")
    fun updateTenant(
        @PathVariable id: Long,
        @RequestBody request: UpdateTenantRequest
    ): ResponseEntity<Tenant> {
        val tenant = tenantRepository.findById(id)
            .orElseThrow { RuntimeException("Tenant not found: $id") }

        request.name?.let { tenant.name }
        request.active?.let { tenant.active = it }
        request.description?.let { tenant.description = it }
        request.contactEmail?.let { tenant.contactEmail = it }

        val updated = tenantRepository.save(tenant)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteTenant(@PathVariable id: Long): ResponseEntity<Void> {
        tenantRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
