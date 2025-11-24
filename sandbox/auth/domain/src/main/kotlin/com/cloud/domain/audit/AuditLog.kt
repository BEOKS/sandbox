package com.cloud.domain.audit

import com.cloud.domain.common.BaseEntity
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "audit_logs", indexes = [
    Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    Index(name = "idx_audit_user", columnList = "userId"),
    Index(name = "idx_audit_resource", columnList = "resourceId")
])
class AuditLog(
    @Column(nullable = false, length = 100)
    var userId: String,

    @Column(nullable = false, length = 100)
    var action: String,

    @Column(length = 100)
    var resourceId: String? = null,

    @Column(length = 50)
    var resourceType: String? = null,

    @Column(nullable = false)
    var timestamp: Instant = Instant.now(),

    @Column(nullable = false, length = 50)
    var result: String,

    @Column(length = 100)
    var ipAddress: String? = null,

    @Column(length = 500)
    var userAgent: String? = null,

    @Column(columnDefinition = "TEXT")
    var requestPayload: String? = null,

    @Column(columnDefinition = "TEXT")
    var responsePayload: String? = null,

    @Column(length = 1000)
    var errorMessage: String? = null
) : BaseEntity()
