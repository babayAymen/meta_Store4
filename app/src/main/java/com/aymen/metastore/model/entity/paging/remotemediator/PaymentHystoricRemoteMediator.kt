package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.model.Payment
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.PaymentRemoteKeys
import com.aymen.metastore.model.entity.room.remoteKeys.SubCategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.PaymentWithInvoice
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class PaymentHystoricRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val invoiceId : Long
): RemoteMediator<Int , PaymentWithInvoice> (){
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val invoiceDao = room.invoiceDao()
    private val paymentDao = room.paymentDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PaymentWithInvoice>
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
            val response = api.getPaymentHystoricByInvoiceId(invoiceId,currentPage, state.config.pageSize)

            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else response.number - 1
            val nextPage = if (endOfPaginationReached) null else response.number + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    paymentDao.insertKeys(response.content.map { article ->
                        PaymentRemoteKeys(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    paymentDao.insertPayment(response.content.map {payment -> payment.toPayment() })

                } catch (ex: Exception) {
                    Log.e("errorsubcategory", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("errorsubcategory", ex.message.toString())
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return paymentDao.getFirstPaymentRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
      return paymentDao.getLatestPaymentRemoteKey()?.nextPage
    }


    private suspend fun deleteCache(){
        paymentDao.clearAllPaymentTable()
        paymentDao.clearAllRemoteKeysTable()
    }

}