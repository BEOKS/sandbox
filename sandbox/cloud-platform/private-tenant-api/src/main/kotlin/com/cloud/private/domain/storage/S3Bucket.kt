package com.cloud.private.domain.storage

import com.cloud.common.domain.audit.BaseEntity
import jakarta.persistence.*

enum class StorageClass {
    STANDARD,
    GLACIER,
    DEEP_ARCHIVE
}

@Entity
@Table(name = "s3_buckets")
class S3Bucket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 100)
    val tenantId: String,

    @Column(nullable = false, unique = true, length = 200)
    val bucketName: String,

    @Column(nullable = false, length = 50)
    val region: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val storageClass: StorageClass = StorageClass.STANDARD,

    @Column(nullable = false)
    var totalSize: Long = 0,

    @Column(nullable = false)
    var objectCount: Long = 0,

    @Column(nullable = false)
    var versioning: Boolean = false,

    @Column(nullable = false)
    var encryption: Boolean = true
) : BaseEntity()
