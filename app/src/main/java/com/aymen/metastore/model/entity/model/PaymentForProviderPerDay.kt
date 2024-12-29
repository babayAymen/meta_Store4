package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.PaymentForProviderPerDayDto


data class PaymentForProviderPerDay (

    val id : Long ? = null,
    val amount : Double ?= null,
    val receiver : Company? = null,
    var isPayed : Boolean ? = false,
    val createdDate : String? = null,
    val lastModifiedDate : String? =null,
    var rest : Double? = null
){
   fun  toPaymentForProviderDto() : PaymentForProviderPerDayDto{
        return PaymentForProviderPerDayDto(
             id,
             amount,
             receiver = receiver?.toCompanyDto(),
             isPayed,
             createdDate,
             lastModifiedDate,
             rest = rest
        )
   }
}