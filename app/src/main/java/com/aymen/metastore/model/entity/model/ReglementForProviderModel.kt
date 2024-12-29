package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.ReglementFoProviderDto


data class ReglementForProviderModel(

     val id : Long? = null,

     val payer : Company? = null,

     val amount : Double? = null,

     val isAccepted : Boolean? = false,

     val meta : User? = null,

     val paymentForProviderPerDay : PaymentForProviderPerDay? = null,

     val createdDate : String? = null,

     val lastModifiedDate : String? = null
){
    fun toReglementDto() : ReglementFoProviderDto{
        return ReglementFoProviderDto(
            id,
            payer = payer?.toCompanyDto(),
            amount,
            isAccepted,
            meta = meta?.toUserDto(),
            paymentForProviderPerDay = paymentForProviderPerDay?.toPaymentForProviderDto(),
            createdDate,
            lastModifiedDate
        )
    }
}
