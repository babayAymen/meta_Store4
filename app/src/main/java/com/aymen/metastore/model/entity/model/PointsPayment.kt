package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.entity.dto.PointsPaymentDto

data class PointsPayment(
    var id: Long? = null,
    var amount: Long? = 0,
    var provider: Company? = null,
    var clientCompany: Company? = null,
    var clientUser: User? = null,
    var createdDate : String = "",
    var lastModifiedDate : String = ""
){
    fun toPointPaymentDto():PointsPaymentDto{
        return PointsPaymentDto(
             id,
            amount,
            provider?.toCompanyDto(),
            clientCompany?.toCompanyDto(),
            clientUser?.toUserDto(),
            createdDate,
            lastModifiedDate

        )
    }
}