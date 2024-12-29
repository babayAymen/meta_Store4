package com.aymen.metastore.model.entity.model

import com.aymen.store.model.Enum.PaymentMode
import com.aymen.store.model.Enum.Status


data class Payment (

    val id : Long? = null,
    val amount : Double,
    val delay : String,
    val agency : String,
    val bankAccount : String,
    val number : String,
    val transactionId : String,
    val status : Status? = Status.INWAITING,
    val type : PaymentMode? = PaymentMode.CASH,
    val invoice: Invoice
)