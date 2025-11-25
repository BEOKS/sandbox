package com.cloud.domain.resource

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ResourceRepository : JpaRepository<Resource, Long> {
    fun findByResourceId(resourceId: String): Optional<Resource>
    fun findByOwnerId(ownerId: String): List<Resource>
    fun findByType(type: ResourceType): List<Resource>
    fun findByOwnerIdAndType(ownerId: String, type: ResourceType): List<Resource>

    @Query("SELECT r FROM Resource r JOIN r.tags t WHERE t.key = :key AND t.value = :value")
    fun findByTag(key: String, value: String): List<Resource>
}

@Repository
interface VirtualMachineRepository : JpaRepository<VirtualMachine, Long> {
    fun findByResourceId(resourceId: String): Optional<VirtualMachine>
    fun findByOwnerId(ownerId: String): List<VirtualMachine>
    fun findByVpcId(vpcId: String): List<VirtualMachine>
}

@Repository
interface S3BucketRepository : JpaRepository<S3Bucket, Long> {
    fun findByResourceId(resourceId: String): Optional<S3Bucket>
    fun findByOwnerId(ownerId: String): List<S3Bucket>
    fun findByRegion(region: String): List<S3Bucket>
}

@Repository
interface VPCRepository : JpaRepository<VPC, Long> {
    fun findByResourceId(resourceId: String): Optional<VPC>
    fun findByOwnerId(ownerId: String): List<VPC>
    fun findByRegion(region: String): List<VPC>
}
