package com.aymen.metastore.model.entity.dto

import com.aymen.store.model.Enum.Status

data class CashDto(
    val id : Long? = null,
    val transaction: String? = "",
    val amount: Double? = null,
    val status: Status? = Status.INWAITING,
    val invoice: InvoiceDto? = null,
    val createdDate : String = "",
    val lastModifiedDate : String = "",
)
