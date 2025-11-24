package com.cloud.private.domain.storage

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface S3Repository : JpaRepository<S3Bucket, Long> {

    fun findByTenantId(tenantId: String): List<S3Bucket>

    fun findByTenantIdAndId(tenantId: String, id: Long): S3Bucket?

    fun findByBucketName(bucketName: String): Optional<S3Bucket>

    fun existsByBucketName(bucketName: String): Boolean
}
