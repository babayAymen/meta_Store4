package com.aymen.metastore.model.repository.remoteRepository.DeliveryRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.entity.model.PurchaseOrder
import com.aymen.metastore.model.entity.paging.remotemediator.InvoiceDeliveredRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
import com.aymen.store.model.Enum.AccountType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor(
    private val api : ServiceApi,
    private val room : AppDatabase
): DeliveryRepository {
    private val invoiceDao = room.invoiceDao()
    private val purchaseOrderDao = room.purchaseOrderDao()
    override suspend fun addAsDelivery(userId: Long): Response<AccountType> = api.addAsDelivery(userId)
    @OptIn(ExperimentalPagingApi::class)
    override fun getInvoicesIdelevered(): Flow<PagingData<PurchaseOrder>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = InvoiceDeliveredRemoteMediator(api , room),
            pagingSourceFactory = {
                purchaseOrderDao.getInvoicesNotDelivered(isTaken = true) // from deliverd
            }
        ).flow.map {
            it.map { invoice ->
                invoice.toPurchaseOrderWithCompanyAndUserOrClient()
            }
        }
    }

}