package com.cloud.domain.resource

import jakarta.persistence.*

@Entity
@Table(name = "s3_buckets")
class S3Bucket(
    resourceId: String,
    name: String,
    ownerId: String,
    description: String? = null,

    @Column(nullable = false, length = 50)
    var region: String,

    @Column(nullable = false)
    var versioning: Boolean = false,

    @Column(nullable = false)
    var encryption: Boolean = false,

    @Column(length = 50)
    var encryptionType: String? = null,

    @Column(nullable = false)
    var publicAccess: Boolean = false,

    @Column
    var sizeBytes: Long = 0,

    @Column
    var objectCount: Long = 0
) : Resource(
    resourceId = resourceId,
    name = name,
    type = ResourceType.S3_BUCKET,
    ownerId = ownerId,
    description = description
)
