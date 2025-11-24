package com.cloud.private.domain.vm

import com.cloud.common.multitenancy.TenantContext
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

data class CreateVMRequest(
    val name: String,
    val instanceType: String,
    val imageId: String,
    val vpcId: Long? = null
)

data class UpdateVMRequest(
    val name: String? = null,
    val status: VMStatus? = null
)

@Service
@Transactional
class VMService(
    private val vmRepository: VMRepository
) {

    fun createVM(request: CreateVMRequest): VirtualMachine {
        val tenantId = TenantContext.getTenantOrThrow().tenantId
        logger.info { "Creating VM for tenant: $tenantId" }

        val vm = VirtualMachine(
            tenantId = tenantId,
            name = request.name,
            instanceType = request.instanceType,
            imageId = request.imageId,
            vpcId = request.vpcId,
            status = VMStatus.PENDING
        )

        return vmRepository.save(vm)
    }

    @Transactional(readOnly = true)
    fun getVM(id: Long): VirtualMachine {
        val tenantId = TenantContext.getTenantOrThrow().tenantId
        return vmRepository.findByTenantIdAndId(tenantId, id)
            ?: throw RuntimeException("VM not found: $id")
    }

    @Transactional(readOnly = true)
    fun listVMs(): List<VirtualMachine> {
        val tenantId = TenantContext.getTenantOrThrow().tenantId
        return vmRepository.findByTenantId(tenantId)
    }

    fun updateVM(id: Long, request: UpdateVMRequest): VirtualMachine {
        val vm = getVM(id)

        request.name?.let { vm.name }
        request.status?.let { vm.status = it }

        return vmRepository.save(vm)
    }

    fun deleteVM(id: Long) {
        val vm = getVM(id)
        vm.status = VMStatus.TERMINATED
        vmRepository.save(vm)
    }

    fun startVM(id: Long): VirtualMachine {
        val vm = getVM(id)
        vm.status = VMStatus.RUNNING
        return vmRepository.save(vm)
    }

    fun stopVM(id: Long): VirtualMachine {
        val vm = getVM(id)
        vm.status = VMStatus.STOPPED
        return vmRepository.save(vm)
    }
}
