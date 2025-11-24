package com.cloud.resource.dto

import com.cloud.domain.resource.ResourceStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateVMRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 200)
    val name: String,

    @field:NotBlank
    val instanceType: String,

    @field:Min(1)
    val cpuCores: Int,

    @field:Min(1)
    val memoryGb: Int,

    @field:Min(1)
    val diskGb: Int,

    val imageId: String? = null,
    val vpcId: String? = null,
    val subnetId: String? = null,
    val description: String? = null,
    val tags: Map<String, String> = emptyMap()
)

data class UpdateVMRequest(
    val name: String? = null,
    val description: String? = null,
    val tags: Map<String, String>? = null
)

data class VMResponse(
    val id: Long,
    val resourceId: String,
    val name: String,
    val status: ResourceStatus,
    val ownerId: String,
    val instanceType: String,
    val cpuCores: Int,
    val memoryGb: Int,
    val diskGb: Int,
    val imageId: String?,
    val publicIpAddress: String?,
    val privateIpAddress: String?,
    val vpcId: String?,
    val subnetId: String?,
    val description: String?,
    val tags: Map<String, String>,
    val createdAt: String,
    val updatedAt: String
)
