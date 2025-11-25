package com.cloud.private.domain.storage

import com.cloud.common.multitenancy.TenantContext
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

data class CreateBucketRequest(
    val bucketName: String,
    val region: String,
    val storageClass: StorageClass = StorageClass.STANDARD
)

@Service
@Transactional
class S3Service(
    private val s3Repository: S3Repository
) {

    fun createBucket(request: CreateBucketRequest): S3Bucket {
        val tenantId = TenantContext.getTenantOrThrow().tenantId
        logger.info { "Creating S3 bucket for tenant: $tenantId" }

        if (s3Repository.existsByBucketName(request.bucketName)) {
            throw RuntimeException("Bucket name already exists: ${request.bucketName}")
        }

        val bucket = S3Bucket(
            tenantId = tenantId,
            bucketName = request.bucketName,
            region = request.region,
            storageClass = request.storageClass
        )

        return s3Repository.save(bucket)
    }

    @Transactional(readOnly = true)
    fun getBucket(id: Long): S3Bucket {
        val tenantId = TenantContext.getTenantOrThrow().tenantId
        return s3Repository.findByTenantIdAndId(tenantId, id)
            ?: throw RuntimeException("Bucket not found: $id")
    }

    @Transactional(readOnly = true)
    fun listBuckets(): List<S3Bucket> {
        val tenantId = TenantContext.getTenantOrThrow().tenantId
        return s3Repository.findByTenantId(tenantId)
    }

    fun deleteBucket(id: Long) {
        val bucket = getBucket(id)
        s3Repository.delete(bucket)
    }
}
