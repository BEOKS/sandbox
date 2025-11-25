package com.cloud.resource.exception

import com.cloud.common.dto.ApiResponse
import com.cloud.common.dto.ErrorDetail
import com.cloud.common.exception.BusinessException
import com.cloud.common.exception.ForbiddenException
import com.cloud.common.exception.ResourceNotFoundException
import com.cloud.common.exception.UnauthorizedException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ApiResponse<Unit>> {
        logger.warn(ex) { "Business exception: ${ex.message}" }
        val errorDetail = ErrorDetail(
            code = ex.errorCode.code,
            message = ex.message ?: ex.errorCode.message,
            status = ex.errorCode.status.value()
        )
        return ResponseEntity
            .status(ex.errorCode.status)
            .body(ApiResponse.error(errorDetail))
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<ApiResponse<Unit>> {
        logger.warn(ex) { "Resource not found: ${ex.message}" }
        val errorDetail = ErrorDetail(
            code = ex.errorCode.code,
            message = ex.message ?: ex.errorCode.message,
            status = HttpStatus.NOT_FOUND.value()
        )
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(errorDetail))
    }

    @ExceptionHandler(UnauthorizedException::class, AuthenticationException::class)
    fun handleUnauthorizedException(ex: Exception): ResponseEntity<ApiResponse<Unit>> {
        logger.warn(ex) { "Unauthorized: ${ex.message}" }
        val errorDetail = ErrorDetail(
            code = "UNAUTHORIZED",
            message = ex.message ?: "인증이 필요합니다",
            status = HttpStatus.UNAUTHORIZED.value()
        )
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(errorDetail))
    }

    @ExceptionHandler(ForbiddenException::class, AccessDeniedException::class)
    fun handleForbiddenException(ex: Exception): ResponseEntity<ApiResponse<Unit>> {
        logger.warn(ex) { "Forbidden: ${ex.message}" }
        val errorDetail = ErrorDetail(
            code = "FORBIDDEN",
            message = ex.message ?: "접근 권한이 없습니다",
            status = HttpStatus.FORBIDDEN.value()
        )
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ApiResponse.error(errorDetail))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        val errorDetail = ErrorDetail(
            code = "VALIDATION_ERROR",
            message = "입력값 검증 실패",
            status = HttpStatus.BAD_REQUEST.value(),
            details = errors
        )
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorDetail))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiResponse<Unit>> {
        logger.error(ex) { "Unexpected error: ${ex.message}" }
        val errorDetail = ErrorDetail(
            code = "INTERNAL_SERVER_ERROR",
            message = "서버 내부 오류가 발생했습니다",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(errorDetail))
    }
}
