package com.cloud.private.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PrivateUserRepository : JpaRepository<PrivateUser, Long> {

    fun findByUsername(username: String): Optional<PrivateUser>

    fun findByEmail(email: String): Optional<PrivateUser>

    fun findByTenantId(tenantId: String): List<PrivateUser>

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean
}
