package com.aymen.metastore.model.repository.remoteRepository.ratingRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.RateType
import com.aymen.metastore.model.entity.dto.RatingDto
import com.aymen.metastore.model.entity.model.Rating
import com.aymen.metastore.model.entity.paging.remotemediator.RateeRatingRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class RatingRepositoryImpl @Inject constructor(
    private val api : ServiceApi,
    private val room : AppDatabase
) : RatingRepository {

    private val ratingDao = room.ratingDao()
        @OptIn(ExperimentalPagingApi::class)
        override fun getAllMyRating(rateeId: Long, type: RateType): Flow<PagingData<Rating>>{
            return Pager(
                config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
                remoteMediator = RateeRatingRemoteMediator(
                    api = api, room = room, rateeId = rateeId, type = type
                ),
                pagingSourceFactory = {
                    when(type){
                        RateType.USER_RATE_COMPANY,
                        RateType.COMPANY_RATE_COMPANY -> {ratingDao.getAllRateeRatingCompany(rateeId = rateeId)}
                        RateType.COMPANY_RATE_USER,
                        RateType.META_RATE_USER -> {ratingDao.getAllRateeRatingUser(rateeId = rateeId)}
                        RateType.COMPANY_RATE_ARTICLE,
                        RateType.USER_RATE_ARTICLE -> ratingDao.getArticleComments(articleId = rateeId)
                    }
                }
            ).flow.map {
                it.map { article ->
                    article.toRatingWithRater()
                }
            }
        }

    override suspend fun doRating(rating: String, image: File?): Response<RatingDto> = api.doRating(
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