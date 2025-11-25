package com.cloud.domain.user

import com.cloud.domain.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "user_attributes",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "attribute_key"])]
)
class UserAttribute(
    @Column(name = "attribute_key", nullable = false, length = 100)
    var key: String,

    @Column(nullable = false, length = 1000)
    var value: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(length = 500)
    var description: String? = null
) : BaseEntity()
