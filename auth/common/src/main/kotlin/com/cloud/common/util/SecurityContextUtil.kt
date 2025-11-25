package com.cloud.common.util

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Spring Security 컨텍스트 유틸리티
 */
object SecurityContextUtil {

    /**
     * 현재 인증 정보 조회
     */
    fun getAuthentication(): Authentication? {
        return SecurityContextHolder.getContext().authentication
    }

    /**
     * 현재 사용자 ID 조회
     */
    fun getCurrentUserId(): String? {
        return getAuthentication()?.name
    }

    /**
     * 인증 여부 확인
     */
    fun isAuthenticated(): Boolean {
        val authentication = getAuthentication()
        return authentication != null && authentication.isAuthenticated
    }

    /**
     * 특정 권한 보유 여부 확인
     */
    fun hasAuthority(authority: String): Boolean {
        return getAuthentication()?.authorities?.any { it.authority == authority } ?: false
    }

    /**
     * 특정 역할 보유 여부 확인
     */
    fun hasRole(role: String): Boolean {
        val roleWithPrefix = if (role.startsWith("ROLE_")) role else "ROLE_$role"
        return hasAuthority(roleWithPrefix)
    }
}
