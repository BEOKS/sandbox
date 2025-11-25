package com.cloud.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

@Configuration
@EnableJpaAuditing
class JpaConfig {

    @Bean
    fun auditorAware(): AuditorAware<String> {
        return AuditorAware {
            val authentication = SecurityContextHolder.getContext().authentication
            Optional.ofNullable(authentication?.name ?: "system")
        }
    }
}
