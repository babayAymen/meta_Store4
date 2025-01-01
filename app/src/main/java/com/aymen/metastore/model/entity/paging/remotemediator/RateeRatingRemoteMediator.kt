package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.RatingRemoteKeys
import com.aymen.metastore.model.entity.roomRelation.RatingWithRater
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.Enum.AccountType

@OptIn(ExperimentalPagingApi::class)
class RateeRatingRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val rateeId : Long,
    private val type : AccountType
) : RemoteMediator<Int , RatingWithRater>() {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val ratingDao = room.ratingDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RatingWithRater>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    0
                }
                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem()
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    previousePage
                }
                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem()
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }
            val response = api.getRate(rateeId,type ,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    ratingDao.insertRatingRemoteKeys(response.content.map { article ->
                        RatingRemoteKeys(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.rateeUser?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.raterUser?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.rateeCompany?.user?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.raterCompany?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {user -> user.rateeCompany?.toCompany()})
                    companyDao.insertCompany(response.content.map {user -> user.raterCompany?.toCompany()})
                    ratingDao.insertRating(response.content.map { payment -> payment.toRating()})


                } catch (ex: Exception) {
                    Log.e("error", "articlecompany ${ex}")
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("error", "articlecompany ${ex.message}")
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return ratingDao.getFirstRatingRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return ratingDao.getLatestRatingRemoteKey()?.nextPage
    }


    private suspend fun deleteCache(){
        ratingDao.clearAllRatingRemoteKeysTable()
        ratingDao.clearAllRatingTable()
    }
}