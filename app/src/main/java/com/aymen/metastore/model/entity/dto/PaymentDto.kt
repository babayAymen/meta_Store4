package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.Payment
import com.aymen.store.model.Enum.PaymentMode
import com.aymen.store.model.Enum.Status
import java.util.Date

data class PaymentDto(

    val id : Long? = null,
    val amount : Double,
    val delay : String,
    val agency : String,
    val bankAccount : String,
    val number : String,
    val transactionId : String,
    val status : Status,
    val type : PaymentMode,
    val invoice: InvoiceDto
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
            invoiceId = invoice.id
        )
    }
}
