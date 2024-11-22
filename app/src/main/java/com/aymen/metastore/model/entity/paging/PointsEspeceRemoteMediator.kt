package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentForProviderRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.PointsPaymentRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.SubCategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PaymentForProvidersWithCommandLine
import com.aymen.metastore.model.entity.roomRelation.PointsWithProviderclientcompanyanduser
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class PointsEspeceRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val type : com.aymen.metastore.model.Enum.LoadType,
    private val id : Long,
    private val beginDate : String?,
    private val finalDate : String?,
    private val status : PaymentStatus?
):RemoteMediator<Int, PaymentForProvidersWithCommandLine>() {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val paymentForProvidersDao = room.paymentForProvidersDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PaymentForProvidersWithCommandLine>
    ): MediatorResult {
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
            val response = when(type){
                com.aymen.metastore.model.Enum.LoadType.RANDOM -> {
                    api.getAllMyPaymentsEspece(id,currentPage, PAGE_SIZE)
                }
                com.aymen.metastore.model.Enum.LoadType.ADMIN -> {
                    api.getAllMyPaymentsEspeceByDate(id,beginDate!!, finalDate!!,currentPage, PAGE_SIZE)
                }
                com.aymen.metastore.model.Enum.LoadType.CONTAINING -> {
                    api.getAllMyPaymentsEspece(id,currentPage, PAGE_SIZE)
                }
            }
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    paymentForProvidersDao.insertKeys(response.map { article ->
                        PointsPaymentForProviderRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.map {user -> user.purchaseOrderLine?.purchaseorder?.person?.toUser()!!})
                    userDao.insertUser(response.map {user -> user.purchaseOrderLine?.purchaseorder?.company?.user?.toUser()!!})
                    userDao.insertUser(response.map {user -> user.purchaseOrderLine?.purchaseorder?.client?.user?.toUser()!!})
                    companyDao.insertCompany(response.map {company -> company.purchaseOrderLine?.purchaseorder?.company?.toCompany()!!})
                    companyDao.insertCompany(response.map {company -> company.purchaseOrderLine?.purchaseorder?.client?.toCompany()!!})
                    paymentForProvidersDao.insertPaymentForProviders(response.map {payment -> payment.toPaymentForProviders() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, PaymentForProvidersWithCommandLine>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { paymentForProvidersDao.getRemoteKeys(it.paymentForProviders.id!!)?.prevPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, PaymentForProvidersWithCommandLine>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { paymentForProvidersDao.getRemoteKeys(it.paymentForProviders.id!!)?.nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, PaymentForProvidersWithCommandLine>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.paymentForProviders?.id?.let { paymentForProvidersDao.getRemoteKeys(it)?.nextPage }
    }

    private suspend fun deleteCache(){
        paymentForProvidersDao.clearRemoteKeys()
        paymentForProvidersDao.clearPointsPayment()
    }
}