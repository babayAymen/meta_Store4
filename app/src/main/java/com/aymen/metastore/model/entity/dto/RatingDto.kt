package com.aymen.metastore.model.entity.dto

import com.aymen.metastore.model.Enum.RateType
import com.aymen.metastore.model.entity.room.entity.Rating

data class RatingDto (

    val id : Long? = null,
    var raterUser: UserDto? = null,
    val rateeUser: UserDto? = null,
    var rateValue : Int? = 0,
    var raterCompany: CompanyDto? = null,
    val rateeCompany: CompanyDto? = null,
    var comment: String? = null,
    val photo: String? = null,
    var type : RateType? = RateType.COMPANY_RATE_USER,
    val createdDate : String? = null,
    val lastModifiedDate : String ? = null
){
    fun toRating() : Rating {

        return Rating(
            id = id,
            raterUserId = raterUser?.id,
            rateeUserId = rateeUser?.id,
            rateValue = rateValue,
            raterCompanyId = raterCompany?.id,
            rateeCompanyId = rateeCompany?.id,
            comment = comment,
            photo = photo,
            type = type,
            createdDate = createdDate,
            lastModifiedDate = lastModifiedDate
        )
    }
}