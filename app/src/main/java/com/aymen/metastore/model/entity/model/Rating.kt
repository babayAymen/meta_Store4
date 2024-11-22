package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.Enum.RateType


data class Rating (

    val id : Long? = null,
    val raterUser: User? = null,
    val rateeUser: User? = null,
    var rateValue : Int? = 0,
    val raterCompany: Company? = null,
    val rateeCompany: Company? = null,
    var comment: String? = null,
    val photo: String? = null,
    var type : RateType? = RateType.COMPANY_RATE_USER,
    val createdDate : String? = null,
    val lastModifiedDate : String ? = null
)