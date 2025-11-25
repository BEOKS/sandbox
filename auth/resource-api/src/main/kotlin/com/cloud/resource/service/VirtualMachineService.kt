package com.cloud.resource.service

import com.cloud.common.exception.ResourceNotFoundException
import com.cloud.common.util.SecurityContextUtil
import com.cloud.domain.resource.ResourceStatus
import com.cloud.domain.resource.VirtualMachine
import com.cloud.domain.resource.VirtualMachineRepository
import com.cloud.resource.dto.CreateVMRequest
import com.cloud.resource.dto.UpdateVMRequest
import com.cloud.resource.dto.VMResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class VirtualMachineService(
    private val vmRepository: VirtualMachineRepository
) {

    @Transactional
    fun createVM(request: CreateVMRequest): VMResponse {
        val userId = SecurityContextUtil.getCurrentUserId() ?: throw IllegalStateException("User not authenticated")

        val vm = VirtualMachine(
            resourceId = "vm-${UUID.randomUUID()}",
            name = request.name,
            ownerId = userId,
            description = request.description,
            instanceType = request.instanceType,
            cpuCores = request.cpuCores,
            memoryGb = request.memoryGb,
            diskGb = request.diskGb,
            imageId = request.imageId,
            vpcId = request.vpcId,
            subnetId = request.subnetId
        )

        // 태그 추가
        request.tags.forEach { (key, value) ->
            vm.addTag(key, value)
        }

        val saved = vmRepository.save(vm)
        return toResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getVM(resourceId: String): VMResponse {
        val vm = vmRepository.findByResourceId(resourceId)
            .orElseThrow { ResourceNotFoundException("VirtualMachine", resourceId) }
        return toResponse(vm)
    }

    @Transactional(readOnly = true)
    fun listVMs(): List<VMResponse> {
        return vmRepository.findAll().map { toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun listMyVMs(): List<VMResponse> {
        val userId = SecurityContextUtil.getCurrentUserId() ?: throw IllegalStateException("User not authenticated")
        return vmRepository.findByOwnerId(userId).map { toResponse(it) }
    }

    @Transactional
    fun updateVM(resourceId: String, request: UpdateVMRequest): VMResponse {
        val vm = vmRepository.findByResourceId(resourceId)
            .orElseThrow { ResourceNotFoundException("VirtualMachine", resourceId) }

        request.name?.let { vm.name = it }
        request.description?.let { vm.description = it }
        request.tags?.forEach { (key, value) ->
            vm.removeTag(key)
            vm.addTag(key, value)
        }

        val updated = vmRepository.save(vm)
        return toResponse(updated)
    }

    @Transactional
    fun deleteVM(resourceId: String) {
        val vm = vmRepository.findByResourceId(resourceId)
            .orElseThrow { ResourceNotFoundException("VirtualMachine", resourceId) }

        vm.status = ResourceStatus.DELETING
        vmRepository.save(vm)
        // 실제로는 비동기로 삭제 처리
    }

    private fun toResponse(vm: VirtualMachine): VMResponse {
        return VMResponse(
            id = vm.id!!,
            resourceId = vm.resourceId,
            name = vm.name,
            status = vm.status,
            ownerId = vm.ownerId,
            instanceType = vm.instanceType,
            cpuCores = vm.cpuCores,
            memoryGb = vm.memoryGb,
            diskGb = vm.diskGb,
            imageId = vm.imageId,
            publicIpAddress = vm.publicIpAddress,
            privateIpAddress = vm.privateIpAddress,
            vpcId = vm.vpcId,
            subnetId = vm.subnetId,
            description = vm.description,
            tags = vm.tags.associate { it.key to it.value },
            createdAt = vm.createdAt.toString(),
            updatedAt = vm.updatedAt.toString()
        )
    }
}
