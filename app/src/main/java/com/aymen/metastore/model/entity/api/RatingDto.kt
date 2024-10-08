package com.aymen.metastore.model.entity.api

import com.aymen.metastore.model.Enum.RateType
import com.aymen.store.model.entity.api.CompanyDto
import com.aymen.store.model.entity.api.UserDto

data class RatingDto (


    var id : Long? = null,

    var raterUser: UserDto? = null,


    var rateeUser: UserDto? = null,

    var rateValue : Int = 0,

    var raterCompany: CompanyDto? = null,


    var rateeCompany: CompanyDto? = null,

    var comment: String? = null,

    var photo: String? = null,

    var type : RateType? = RateType.COMPANY_RATE_USER
)