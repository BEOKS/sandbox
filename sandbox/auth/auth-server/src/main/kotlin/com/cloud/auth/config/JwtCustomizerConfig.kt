package com.cloud.auth.config

import com.cloud.domain.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer

@Configuration
class JwtCustomizerConfig(
    private val userRepository: UserRepository
) {

    @Bean
    fun jwtCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer { context ->
            if (context.tokenType.value == "access_token") {
                val username = context.getPrincipal<Authentication>().name
                val user = userRepository.findByUsername(username).orElse(null)

                if (user != null) {
                    val claims = context.claims

                    // 사용자 기본 정보 추가
                    claims.claim("user_id", user.id)
                    claims.claim("email", user.email)
                    claims.claim("first_name", user.firstName)
                    claims.claim("last_name", user.lastName)

                    // 사용자 역할 추가
                    claims.claim("roles", user.roles)

                    // 사용자 속성 추가 (ABAC에 사용)
                    val attributes = user.attributes.associate { it.key to it.value }
                    claims.claim("user_attributes", attributes)
                }
            }
        }
    }
}
