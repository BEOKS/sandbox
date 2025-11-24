package com.cloud.common.exception

import org.springframework.http.HttpStatus

/**
 * 에러 코드 인터페이스
 */
interface ErrorCode {
    val code: String
    val message: String
    val status: HttpStatus
}

/**
 * 공통 에러 코드
 */
enum class CommonErrorCode(
    override val code: String,
    override val message: String,
    override val status: HttpStatus
) : ErrorCode {
    // 4xx Client Errors
    BAD_REQUEST("COMMON-400", "잘못된 요청입니다", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("COMMON-401", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("COMMON-403", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    NOT_FOUND("COMMON-404", "리소스를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("COMMON-405", "허용되지 않은 HTTP 메서드입니다", HttpStatus.METHOD_NOT_ALLOWED),
    CONFLICT("COMMON-409", "리소스 충돌이 발생했습니다", HttpStatus.CONFLICT),

    // 5xx Server Errors
    INTERNAL_SERVER_ERROR("COMMON-500", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
}
