package com.aymen.metastore.model.entity.model

import com.aymen.store.model.Enum.PaymentMode
import com.aymen.store.model.Enum.Status


data class Payment (

    val id : Long? = null,
    val amount : Double? = null,
    val delay : String? = null,
    val agency : String? = null,
    val bankAccount : String? = null,
    val number : String? = null,
    val transactionId : String? = null,
    val status : Status? = Status.INWAITING,
    val type : PaymentMode? = PaymentMode.CASH,
    val invoice: Invoice? = null,
    val lastModifiedDate : String? = null
)