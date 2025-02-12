package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.Enum.RateType
import com.aymen.metastore.model.entity.model.Rating
import com.aymen.metastore.model.repository.remoteRepository.ratingRepository.RatingRepository
import kotlinx.coroutines.flow.Flow

class GetRateeRating( private val repository : RatingRepository) {

    operator fun invoke(rateeId : Long , type : RateType) : Flow<PagingData<Rating>>{
        return repository.getAllMyRating(rateeId , type)
    }
}