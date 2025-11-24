package com.cloud.common.exception

/**
 * 권한 부족 시 발생하는 예외
 */
class ForbiddenException(
    message: String = "접근 권한이 없습니다",
    cause: Throwable? = null
) : BusinessException(CommonErrorCode.FORBIDDEN, message, cause)
