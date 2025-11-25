package com.cloud.private.domain.vm

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/vms")
class VMController(
    private val vmService: VMService
) {

    @PostMapping
    fun createVM(@RequestBody request: CreateVMRequest): ResponseEntity<VirtualMachine> {
        val vm = vmService.createVM(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(vm)
    }

    @GetMapping
    fun listVMs(): ResponseEntity<List<VirtualMachine>> {
        val vms = vmService.listVMs()
        return ResponseEntity.ok(vms)
    }

    @GetMapping("/{id}")
    fun getVM(@PathVariable id: Long): ResponseEntity<VirtualMachine> {
        val vm = vmService.getVM(id)
        return ResponseEntity.ok(vm)
    }

    @PutMapping("/{id}")
    fun updateVM(
        @PathVariable id: Long,
        @RequestBody request: UpdateVMRequest
    ): ResponseEntity<VirtualMachine> {
        val vm = vmService.updateVM(id, request)
        return ResponseEntity.ok(vm)
    }

    @DeleteMapping("/{id}")
    fun deleteVM(@PathVariable id: Long): ResponseEntity<Void> {
        vmService.deleteVM(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/start")
    fun startVM(@PathVariable id: Long): ResponseEntity<VirtualMachine> {
        val vm = vmService.startVM(id)
        return ResponseEntity.ok(vm)
    }

    @PostMapping("/{id}/stop")
    fun stopVM(@PathVariable id: Long): ResponseEntity<VirtualMachine> {
        val vm = vmService.stopVM(id)
        return ResponseEntity.ok(vm)
    }
}
