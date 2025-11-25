package com.cloud.domain.common

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()

    @CreatedBy
    @Column(updatable = false)
    var createdBy: String? = null

    @LastModifiedBy
    var updatedBy: String? = null

    @Version
    var version: Long = 0
}
