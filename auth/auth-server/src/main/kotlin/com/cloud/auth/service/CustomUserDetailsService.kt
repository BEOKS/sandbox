package com.cloud.auth.service

import com.cloud.domain.user.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("User not found: $username") }

        val authorities = user.roles.map { SimpleGrantedAuthority(it) }

        return User.builder()
            .username(user.username)
            .password(user.password)
            .authorities(authorities)
            .accountLocked(!user.accountNonLocked)
            .disabled(!user.enabled)
            .build()
    }
}
