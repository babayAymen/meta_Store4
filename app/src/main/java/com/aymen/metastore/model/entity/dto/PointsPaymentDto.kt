package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.entity.room.entity.PointsPayment

data class PointsPaymentDto(
    var id: Long? = null,
    var amount: Long? = 0,
    var provider: CompanyDto? = null,
    var clientCompany: CompanyDto? = null,
    var clientUser: UserDto? = null,
    var createdDate : String = "",
    var lastModifiedDate : String = ""
){
    fun toPointsPayment() : PointsPayment {

        return PointsPayment(
            id = id,
            amount = amount,
            providerId = provider?.id,
            clientCompanyId = clientCompany?.id,
            clientUserId = clientUser?.id,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}
