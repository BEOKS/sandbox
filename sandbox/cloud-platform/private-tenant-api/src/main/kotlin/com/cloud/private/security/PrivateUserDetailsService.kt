package com.cloud.private.security

import com.cloud.common.security.UserPrincipal
import com.cloud.private.domain.user.PrivateUserRepository
import mu.KotlinLogging
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class PrivateUserDetailsService(
    private val userRepository: PrivateUserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        logger.debug { "Loading user by username: $username" }

        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found: $username") }

        val authorities = user.roles.split(",")
            .map { SimpleGrantedAuthority("ROLE_$it") }

        return UserPrincipal(
            username = user.username,
            password = user.password,
            tenantId = user.tenantId,
            userId = user.id!!,
            authorities = authorities,
            enabled = user.active
        )
    }
}
