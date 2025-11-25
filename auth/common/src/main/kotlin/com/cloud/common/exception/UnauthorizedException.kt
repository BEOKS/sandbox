package com.cloud.common.exception

/**
 * 인증 실패 시 발생하는 예외
 */
class UnauthorizedException(
    message: String = "인증이 필요합니다",
    cause: Throwable? = null
) : BusinessException(CommonErrorCode.UNAUTHORIZED, message, cause)
