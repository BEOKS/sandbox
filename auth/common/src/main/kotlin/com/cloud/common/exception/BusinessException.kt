package com.cloud.common.exception

/**
 * 비즈니스 로직 예외의 기본 클래스
 */
open class BusinessException(
    val errorCode: ErrorCode,
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message ?: errorCode.message, cause) {

    constructor(errorCode: ErrorCode, cause: Throwable) : this(errorCode, null, cause)
}
