package com.aymen.metastore.model.repository.remoteRepository.ratingRepository

import android.util.Log
import com.aymen.metastore.model.entity.api.RatingDto
import com.aymen.metastore.model.entity.realm.Rating
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.repository.globalRepository.ServiceApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
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


}