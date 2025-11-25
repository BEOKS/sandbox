package com.cloud.domain.resource

import com.cloud.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "resource_tags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["resource_id", "tag_key"])]
)
class ResourceTag(
    @Column(name = "tag_key", nullable = false, length = 100)
    var key: String,

    @Column(nullable = false, length = 1000)
    var value: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    var resource: Resource
) : BaseEntity()
