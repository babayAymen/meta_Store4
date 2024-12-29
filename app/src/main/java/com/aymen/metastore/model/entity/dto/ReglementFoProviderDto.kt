package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.model.ReglementForProviderModel
import com.aymen.metastore.model.entity.room.entity.ReglementForProvider


data class ReglementFoProviderDto(

     val id : Long? = null,

     val payer : CompanyDto? = null,

     val amount : Double? = null,

     val isAccepted : Boolean? = false,

     val meta : UserDto? = null,

     val paymentForProviderPerDay : PaymentForProviderPerDayDto? = null,

     val createdDate : String? = null,

     val lastModifiedDate : String? = null
){
    fun toReglementEntity() : ReglementForProvider{
        return ReglementForProvider(
            id,
            payerId = payer?.id,
            amount,
            isAccepted,
            metaId = meta?.id,
            paymentForProviderPerDayId = paymentForProviderPerDay?.id,
            createdDate,
            lastModifiedDate
        )
    }

    fun toReglementModel() : ReglementForProviderModel{
        return ReglementForProviderModel(
            id,
            payer = payer?.toCompanyModel(),
            amount,
            isAccepted,
            meta = meta?.toUserModel(),
            paymentForProviderPerDay = paymentForProviderPerDay?.toPaymentForProviderPerDayModel(),
            createdDate,
            lastModifiedDate
        )
    }
}
