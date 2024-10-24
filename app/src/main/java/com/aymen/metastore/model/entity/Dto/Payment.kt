package com.aymen.store.model.entity.dto

import com.aymen.store.model.Enum.PaymentMode
import com.aymen.store.model.Enum.Status
import java.util.Date

data class Payment(

    val id : Long? = null,

    var amount : Double,

    var delay : Date,

    var agency : String,

    var bankAccount : String,

    var number : String,

    var transactionId : String,

    var status : Status,

    var type : PaymentMode,

    var invoice: InvoiceDto
)
