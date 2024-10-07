package com.aymen.metastore.model.repository.remoteRepository.ratingRepository

import com.aymen.metastore.model.entity.api.RatingDto
import com.aymen.metastore.model.entity.realm.Rating
import com.aymen.store.model.Enum.AccountType
import retrofit2.Response
import java.io.File

interface RatingRepository {

    suspend fun getAllMyRating(id : Long , type :AccountType ):Response<List<Rating>>

    suspend fun doRating( rating : String, image : File?):Response<Void>?

    suspend fun enabledToCommentCompany(companyId : Long) : Response<Boolean>

    suspend fun enabledToCommentUser(userId : Long) : Response<Boolean>

    suspend fun enabledToCommentArticle(companyId : Long) : Response<Boolean>
}