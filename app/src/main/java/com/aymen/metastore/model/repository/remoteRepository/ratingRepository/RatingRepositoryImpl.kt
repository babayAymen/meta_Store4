package com.aymen.metastore.model.repository.remoteRepository.ratingRepository

import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.repository.globalRepository.ServiceApi
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class RatingRepositoryImpl @Inject constructor(
    private val api : ServiceApi
)
    : RatingRepository {
        override suspend fun getAllMyRating(id : Long , type : AccountType) = api.getRate(id , type)
    override suspend fun doRating( rating: String, image : File?)
    = api.doRating(
        rating,  image = image?.asRequestBody()
            .let {
                MultipartBody.Part
                    .createFormData(
                        if(image != null)"image" else "file",
                        image?.name,
                        it?: "".toRequestBody()
                    )
            }
    )

    override suspend fun enabledToCommentCompany(companyId : Long) = api.enabledToCommentCompany(companyId = companyId)
    override suspend fun enabledToCommentUser(userId: Long) = api.enabledToCommentUser(userId)
    override suspend fun enabledToCommentArticle(companyId: Long) = api.enabledToCommentArticle(companyId)


}