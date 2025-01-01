package com.aymen.metastore.model.entity.model

import com.aymen.metastore.model.Enum.RateType
import com.aymen.metastore.model.entity.room.entity.Rating


data class Rating (

    val id : Long? = null,
    var raterUser: User? = null,
    var rateeUser: User? = null,
    var rateValue : Int? = 0,
    var raterCompany: Company? = null,
    var rateeCompany: Company? = null,
    var comment: String? = null,
    val photo: String? = null,
    var type : RateType? = RateType.COMPANY_RATE_USER,
    val createdDate : String? = null,
    val lastModifiedDate : String ? = null
){
    fun toRatingEntity() : Rating{
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