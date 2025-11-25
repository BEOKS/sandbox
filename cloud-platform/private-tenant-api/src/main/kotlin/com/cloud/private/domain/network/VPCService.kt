package com.cloud.private.domain.network

import com.cloud.common.multitenancy.TenantContext
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

data class CreateVPCRequest(
    val name: String,
    val cidrBlock: String,
    val region: String
)

@Service
@Transactional
class VPCService(
    private val vpcRepository: VPCRepository
) {

    fun createVPC(request: CreateVPCRequest): VPC {
        val tenantId = TenantContext.getTenantOrThrow().tenantId
        logger.info { "Creating VPC for tenant: $tenantId" }

        val vpc = VPC(
            tenantId = tenantId,
            name = request.name,
            cidrBlock = request.cidrBlock,
            region = request.region
        )

        return vpcRepository.save(vpc)
    }

    @Transactional(readOnly = true)
    fun getVPC(id: Long): VPC {
        val tenantId = TenantContext.getTenantOrThrow().tenantId
        return vpcRepository.findByTenantIdAndId(tenantId, id)
            ?: throw RuntimeException("VPC not found: $id")
    }

    @Transactional(readOnly = true)
    fun listVPCs(): List<VPC> {
        val tenantId = TenantContext.getTenantOrThrow().tenantId
        return vpcRepository.findByTenantId(tenantId)
    }

    fun deleteVPC(id: Long) {
        val vpc = getVPC(id)
        vpcRepository.delete(vpc)
    }
}
