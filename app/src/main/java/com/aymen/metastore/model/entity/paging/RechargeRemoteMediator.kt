package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.RechargeRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PointsWithProviderclientcompanyanduser
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class RechargeRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long
):RemoteMediator<Int, PointsWithProviderclientcompanyanduser>() {
    private val companyDao = room.companyDao()
    private val userDao = room.userDao()
    private val pointsPaymentDao = room.pointsPaymentDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PointsWithProviderclientcompanyanduser>
    ):  MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    getNextPageClosestToCurrentPosition(state)?.minus(1) ?: 0
                }

                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem(state)
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    previousePage
                }

                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem(state)
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }

            val response = api.getRechargeHistory(id, currentPage , state.config.pageSize)
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    pointsPaymentDao.insertRechargeKeys(response.map { article ->
                        RechargeRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.map {user -> user.clientUser?.toUser()})
                    userDao.insertUser(response.map {user -> user.clientCompany?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.clientCompany?.toCompany()})
                    userDao.insertUser(response.map {user -> user.provider?.user?.toUser()})
                    companyDao.insertCompany(response.map { company -> company.provider?.toCompany()})
                    pointsPaymentDao.insertPointsPayment(response.map { point -> point.toPointsPayment() })

                } catch (ex: Exception) {
                    Log.e("error", "$ex")
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }
    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, PointsWithProviderclientcompanyanduser>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { pointsPaymentDao.getPointPaymentRemoteKey(it.pointsPayment.id!!).prevPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, PointsWithProviderclientcompanyanduser>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { pointsPaymentDao.getPointPaymentRemoteKey(it.pointsPayment.id!!).nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, PointsWithProviderclientcompanyanduser>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.pointsPayment?.id?.let { pointsPaymentDao.getPointPaymentRemoteKey(it).nextPage }
    }

    private suspend fun deleteCache(){
        pointsPaymentDao.clearPointsPayment()
        pointsPaymentDao.clearPointsPaymentRemoteKeysTable()
    }
}