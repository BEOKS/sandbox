package com.cloud.private.domain.network

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/vpcs")
class VPCController(
    private val vpcService: VPCService
) {

    @PostMapping
    fun createVPC(@RequestBody request: CreateVPCRequest): ResponseEntity<VPC> {
        val vpc = vpcService.createVPC(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(vpc)
    }

    @GetMapping
    fun listVPCs(): ResponseEntity<List<VPC>> {
        val vpcs = vpcService.listVPCs()
        return ResponseEntity.ok(vpcs)
    }

    @GetMapping("/{id}")
    fun getVPC(@PathVariable id: Long): ResponseEntity<VPC> {
        val vpc = vpcService.getVPC(id)
        return ResponseEntity.ok(vpc)
    }

    @DeleteMapping("/{id}")
    fun deleteVPC(@PathVariable id: Long): ResponseEntity<Void> {
        vpcService.deleteVPC(id)
        return ResponseEntity.noContent().build()
    }
}
