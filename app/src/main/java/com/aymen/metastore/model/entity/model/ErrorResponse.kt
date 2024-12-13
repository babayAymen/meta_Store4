package com.aymen.metastore.model.entity.model

data class ErrorResponse(
    val success: Boolean,
    val message: String,
    val date: String,
    val details: List<String>
)
