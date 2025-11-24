package com.cloud.common.dto

/**
 * 페이지네이션 응답
 */
data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
) {
    companion object {
        fun <T> of(content: List<T>, page: Int, size: Int, totalElements: Long): PageResponse<T> {
            val totalPages = if (size == 0) 1 else ((totalElements + size - 1) / size).toInt()
            return PageResponse(
                content = content,
                page = page,
                size = size,
                totalElements = totalElements,
                totalPages = totalPages,
                last = page >= totalPages - 1
            )
        }
    }
}
