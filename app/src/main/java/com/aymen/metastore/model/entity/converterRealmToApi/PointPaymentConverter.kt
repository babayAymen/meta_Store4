package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.Dto.PointsPaymentDto
import com.aymen.metastore.model.entity.room.PointsPayment

fun mapPointsPaymentToRoomPointsPayment(pointPayment : PointsPaymentDto):PointsPayment{
    return PointsPayment(
         id = pointPayment.id  ,

     amount= pointPayment.amount,

     providerId = pointPayment.provider?.id,

     clientCompanyId = pointPayment.clientCompany?.id,

     clientUserId = pointPayment.clientUser?.id,

     createdDate = pointPayment.createdDate,

     lastModifiedDate = pointPayment.lastModifiedDate
    )
}