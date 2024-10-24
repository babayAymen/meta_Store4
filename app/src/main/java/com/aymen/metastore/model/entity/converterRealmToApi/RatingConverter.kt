package com.aymen.metastore.model.entity.converterRealmToApi

import com.aymen.metastore.model.Enum.RateType
import com.aymen.metastore.model.entity.Dto.RatingDto
import com.aymen.metastore.model.entity.room.Rating

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