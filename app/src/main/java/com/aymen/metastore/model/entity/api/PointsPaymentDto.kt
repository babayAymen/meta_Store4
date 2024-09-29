package com.aymen.metastore.model.entity.api

import com.aymen.store.model.entity.api.CompanyDto
import com.aymen.store.model.entity.api.UserDto

data class PointsPaymentDto(
    var id: Long? = null,
    var amount: Long? = 0,
    var provider: CompanyDto? = null,
    var clientCompany: CompanyDto? = null,
    var clientUser: UserDto? = null
)
