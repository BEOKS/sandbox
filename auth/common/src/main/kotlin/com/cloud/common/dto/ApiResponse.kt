package com.cloud.common.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import java.time.Instant

/**
 * 표준 API 응답 형식
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDetail? = null,
    val timestamp: Instant = Instant.now()
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(success = true, data = data)
        }

        fun <T> error(code: String, message: String, status: HttpStatus): ApiResponse<T> {
            return ApiResponse(
                success = false,
                error = ErrorDetail(code = code, message = message, status = status.value())
            )
        }

        fun <T> error(errorDetail: ErrorDetail): ApiResponse<T> {
            return ApiResponse(success = false, error = errorDetail)
        }
    }
}

/**
 * 에러 상세 정보
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorDetail(
    val code: String,
    val message: String,
    val status: Int,
    val details: Map<String, Any?>? = null
)
