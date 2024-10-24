package com.aymen.metastore.model.entity.Dto

import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto

data class PointsPaymentDto(
    var id: Long? = null,
    var amount: Long? = 0,
    var provider: CompanyDto? = null,
    var clientCompany: CompanyDto? = null,
    var clientUser: UserDto? = null,
    var createdDate : String = "",
    var lastModifiedDate : String = ""
)
