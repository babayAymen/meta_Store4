package com.aymen.metastore.model.entity.model

data class PaginatedResponse<T>(
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val number: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean,
    val content: List<T>,
)
