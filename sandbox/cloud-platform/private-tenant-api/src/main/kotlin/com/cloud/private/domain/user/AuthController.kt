package com.cloud.private.domain.user

import com.cloud.common.security.AuthenticationResult
import com.cloud.common.security.UserPrincipal
import com.cloud.private.security.jwt.JwtTokenProvider
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

data class LoginRequest(
    val username: String,
    val password: String,
    val tenantId: String
)

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val tenantId: String,
    val fullName: String? = null
)

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: PrivateUserRepository
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<AuthenticationResult> {
        logger.info { "Login attempt for user: ${loginRequest.username}" }

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.username,
                loginRequest.password
            )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val userPrincipal = authentication.principal as UserPrincipal
        val token = jwtTokenProvider.generateToken(
            username = userPrincipal.username,
            tenantId = userPrincipal.tenantId,
            userId = userPrincipal.userId
        )

        val result = AuthenticationResult(
            accessToken = token,
            expiresIn = 86400,
            tenantId = userPrincipal.tenantId,
            userId = userPrincipal.userId
        )

        return ResponseEntity.ok(result)
    }

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<PrivateUser> {
        val authentication = SecurityContextHolder.getContext().authentication
        val userPrincipal = authentication.principal as UserPrincipal

        val user = userRepository.findByUsername(userPrincipal.username)
            .orElseThrow { RuntimeException("User not found") }

        return ResponseEntity.ok(user)
    }
}
