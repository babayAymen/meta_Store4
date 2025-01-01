package com.aymen.metastore.model.repository.remoteRepository.ratingRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.RatingDto
import com.aymen.metastore.model.entity.model.Rating
import com.aymen.store.model.Enum.AccountType
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.io.File

interface RatingRepository {

     fun getAllMyRating(id : Long , type :AccountType ):Flow<PagingData<Rating>>

    suspend fun doRating( rating : String, image : File?):Response<RatingDto>

    suspend fun enabledToCommentCompany(companyId : Long) : Response<Boolean>

    suspend fun enabledToCommentUser(userId : Long) : Response<Boolean>

    suspend fun enabledToCommentArticle(companyId : Long) : Response<Boolean>
}