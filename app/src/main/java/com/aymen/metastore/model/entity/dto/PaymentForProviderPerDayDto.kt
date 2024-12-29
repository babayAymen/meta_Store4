package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.PaymentForProviderPerDay

data class PaymentForProviderPerDayDto (

    val id : Long ? = null,
    val amount : Double ?= null,
    val receiver : CompanyDto? = null,
    val isPayed : Boolean ? = false,
    val createdDate : String? = null,
    val lastModifiedDate : String? = null,
    val rest : Double? = null

){
    fun toPaymentForProviderPerDay() : PaymentForProviderPerDay {

        return PaymentForProviderPerDay(
            id = id,
            amount = amount,
            receiverId = receiver?.id,
            isPayed = isPayed,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            rest = rest
        )
    }

    fun toPaymentForProviderPerDayModel() : com.aymen.metastore.model.entity.model.PaymentForProviderPerDay{
        return com.aymen.metastore.model.entity.model.PaymentForProviderPerDay(
            id = id,
            amount = amount,
            receiver = receiver?.toCompanyModel(),
            isPayed = isPayed,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate,
            rest = rest
        )
    }
}
