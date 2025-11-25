package com.cloud.common.exception

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 */
class ResourceNotFoundException(
    message: String,
    cause: Throwable? = null
) : BusinessException(CommonErrorCode.NOT_FOUND, message, cause) {

    constructor(resourceType: String, id: Any) : this("$resourceType 를 찾을 수 없습니다: $id")
}
