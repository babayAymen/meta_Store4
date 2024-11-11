package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.entity.Dto.RatingDto
import com.aymen.metastore.model.entity.room.Rating
import com.aymen.store.model.entity.dto.CompanyDto
import com.aymen.store.model.entity.dto.UserDto

fun mapRatingToRoomRating(rating : RatingDto) : Rating{
    return Rating(
         id = rating.id,

     raterUserId = rating.raterUser?.id,

     rateeUserId = rating.rateeUser?.id,

     raterCompanyId = rating.raterCompany?.id,

     rateeCompanyId = rating.rateeCompany?.id,

     comment = rating.comment,

     photo = rating.photo,

     type = rating.type,

     rateValue = rating.rateValue,

     createdDate = rating.createdDate,

     lastModifiedDate =rating.lastModifiedDate
    )
}
fun mapRoomRatingToRatingDto(rating : Rating) : RatingDto{
    return RatingDto(
         id = rating.id,

     raterUser = rating.raterUserId?.let { UserDto(id = it) },

     rateeUser = rating.rateeUserId?.let { UserDto(id = it) },

     raterCompany = rating.raterCompanyId?.let { CompanyDto(id = it) },

     rateeCompany = rating.rateeCompanyId?.let { CompanyDto(id = it) },

     comment = rating.comment,

     photo = rating.photo,

     type = rating.type,

     rateValue = rating.rateValue,

     createdDate = rating.createdDate,

     lastModifiedDate =rating.lastModifiedDate
    )
}