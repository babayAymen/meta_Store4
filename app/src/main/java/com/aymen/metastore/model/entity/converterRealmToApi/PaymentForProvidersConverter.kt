package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.Dto.PaymentForProvidersDto
import com.aymen.metastore.model.entity.room.PaymentForProviders

fun mappaymentForProvidersToRoomPaymentForProviders(paymentForProvider : PaymentForProvidersDto) : PaymentForProviders{
    return PaymentForProviders(
         id = paymentForProvider.id,

     purchaseOrderLineId = paymentForProvider.purchaseOrderLine?.id,

     giveenespece = paymentForProvider.giveenespece,

     status = paymentForProvider.status,

     createdDate = paymentForProvider.createdDate,

     lastModifiedDate = paymentForProvider.lastModifiedDate,
    )
}