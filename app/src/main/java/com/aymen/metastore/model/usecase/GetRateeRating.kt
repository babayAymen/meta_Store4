package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Rating
import com.aymen.metastore.model.repository.remoteRepository.ratingRepository.RatingRepository
import com.aymen.store.model.Enum.AccountType
import kotlinx.coroutines.flow.Flow

class GetRateeRating( private val repository : RatingRepository) {

    operator fun invoke(rateeId : Long , type : AccountType) : Flow<PagingData<Rating>>{
        return repository.getAllMyRating(rateeId , type)
    }
}