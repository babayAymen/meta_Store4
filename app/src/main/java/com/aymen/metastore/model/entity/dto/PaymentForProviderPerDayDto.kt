package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.PaymentForProviderPerDay

data class PaymentForProviderPerDayDto (

    val id : Long ? = null,
    val amount : Double ?= null,
    val provider : CompanyDto? = null,
    val payed : Boolean ? = false,
    val createdDate : String? = "",
    val lastModifiedDate : String? = ""

){
    fun toPaymentForProviderPerDay() : PaymentForProviderPerDay {

        return PaymentForProviderPerDay(
            id = id,
            amount = amount,
            providerId = provider?.id,
            payed = payed,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
