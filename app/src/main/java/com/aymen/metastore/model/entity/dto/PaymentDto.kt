package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.Payment
import com.aymen.store.model.Enum.PaymentMode
import com.aymen.store.model.Enum.Status
import java.util.Date

data class PaymentDto(

    val id : Long? = null,
    val amount : Double? = null,
    val delay : String? = null,
    val agency : String? = null,
    val bankAccount : String? = null,
    val number : String? = null,
    val transactionId : String? = null,
    val status : Status? = Status.INWAITING,
    val type : PaymentMode? = PaymentMode.CASH,
    val invoice: InvoiceDto? = null,
    val lastModifiedDate : String? = null
){
    fun toPayment() : Payment {

        return Payment(
            id = id,
            amount = amount,
            delay = delay,
            agency = agency,
            bankAccount = bankAccount,
            number = number,
            transactionId = transactionId,
            status = status,
            type = type,
            invoiceId = invoice?.id,
            lastModifiedDate = lastModifiedDate
        )
    }
}
