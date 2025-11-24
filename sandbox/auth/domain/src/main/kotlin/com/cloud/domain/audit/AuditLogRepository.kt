package com.cloud.domain.audit

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface AuditLogRepository : JpaRepository<AuditLog, Long> {
    fun findByUserId(userId: String): List<AuditLog>

    fun findByResourceId(resourceId: String): List<AuditLog>

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :start AND :end ORDER BY a.timestamp DESC")
    fun findByTimestampBetween(
        @Param("start") start: Instant,
        @Param("end") end: Instant
    ): List<AuditLog>

    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.timestamp BETWEEN :start AND :end ORDER BY a.timestamp DESC")
    fun findByUserIdAndTimestampBetween(
        @Param("userId") userId: String,
        @Param("start") start: Instant,
        @Param("end") end: Instant
    ): List<AuditLog>
}
