package com.cloud.resource.controller

import com.cloud.common.dto.ApiResponse
import com.cloud.resource.dto.CreateVMRequest
import com.cloud.resource.dto.UpdateVMRequest
import com.cloud.resource.dto.VMResponse
import com.cloud.resource.service.VirtualMachineService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/vms")
@Tag(name = "Virtual Machines", description = "VM 리소스 관리 API")
@SecurityRequirement(name = "bearer-jwt")
class VirtualMachineController(
    private val vmService: VirtualMachineService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "VM 생성", description = "새로운 가상 머신을 생성합니다")
    fun createVM(@Valid @RequestBody request: CreateVMRequest): ApiResponse<VMResponse> {
        val vm = vmService.createVM(request)
        return ApiResponse.success(vm)
    }

    @GetMapping("/{resourceId}")
    @Operation(summary = "VM 조회", description = "특정 가상 머신의 상세 정보를 조회합니다")
    fun getVM(@PathVariable resourceId: String): ApiResponse<VMResponse> {
        val vm = vmService.getVM(resourceId)
        return ApiResponse.success(vm)
    }

    @GetMapping
    @Operation(summary = "VM 목록 조회", description = "모든 가상 머신 목록을 조회합니다")
    fun listVMs(): ApiResponse<List<VMResponse>> {
        val vms = vmService.listVMs()
        return ApiResponse.success(vms)
    }

    @GetMapping("/my")
    @Operation(summary = "내 VM 목록 조회", description = "현재 사용자가 소유한 가상 머신 목록을 조회합니다")
    fun listMyVMs(): ApiResponse<List<VMResponse>> {
        val vms = vmService.listMyVMs()
        return ApiResponse.success(vms)
    }

    @PutMapping("/{resourceId}")
    @Operation(summary = "VM 수정", description = "가상 머신 정보를 수정합니다")
    fun updateVM(
        @PathVariable resourceId: String,
        @Valid @RequestBody request: UpdateVMRequest
    ): ApiResponse<VMResponse> {
        val vm = vmService.updateVM(resourceId, request)
        return ApiResponse.success(vm)
    }

    @DeleteMapping("/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "VM 삭제", description = "가상 머신을 삭제합니다")
    fun deleteVM(@PathVariable resourceId: String) {
        vmService.deleteVM(resourceId)
    }
}
