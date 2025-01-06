package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.CashDto
import com.aymen.store.model.Enum.Status
data class CashModel (

    var id : Long? = null,

    var transactionId: String = "",

    var amount: Double? = null,

    var status: Status? = Status.INWAITING,

    var invoice: Invoice? = null,

    var createdDate : String = "",

    var lastModifiedDate : String = "",
){
    fun toCashDto() : CashDto{
        return CashDto(
            id,
            transactionId,
            amount,
            status,
            invoice = invoice?.toInvoiceDto(),
            createdDate,
            lastModifiedDate
        )
    }
}