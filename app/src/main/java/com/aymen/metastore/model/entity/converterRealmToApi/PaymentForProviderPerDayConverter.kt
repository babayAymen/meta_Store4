package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.Dto.PaymentForProviderPerDayDto
import com.aymen.metastore.model.entity.room.PaymentForProviderPerDay

fun mapPaymentForProviderPerDayToRoomPaymentForProviderPerDay(payment : PaymentForProviderPerDayDto?):PaymentForProviderPerDay{
    return PaymentForProviderPerDay(
         id = payment?.id ,

     providerId = payment?.provider?.id ,

     payed = payment?.payed,

     amount = payment?.amount ,

     createdDate = payment?.createdDate,

     lastModifiedDate = payment?.lastModifiedDate
    )
}