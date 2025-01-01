package com.aymen.store.model.repository.remoteRepository.paymentRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.entity.dto.PaymentForProvidersDto
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.paging.remotemediator.BuyHistoryMediator
import com.aymen.metastore.model.entity.paging.pagingsource.GetInvoicesByStatusPagingSource
import com.aymen.metastore.model.entity.paging.pagingsource.PayedPagingSource
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
import com.aymen.store.model.Enum.PaymentStatus
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val api: ServiceApi,
    private val room : AppDatabase
) : PaymentRepository {

    private val invoiceDao = room.invoiceDao()
    override suspend fun getAllMyPaymentsEspeceByDate(
        date: String,
        findate: String
    ): Response<List<PaymentForProvidersDto>> {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyBuyHistory(id: Long): Flow<PagingData<InvoiceWithClientPersonProvider>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = BuyHistoryMediator(
                api = api, room = room, id = id
            ),
            pagingSourceFactory = {
                invoiceDao.getAllMyBuyHistory()
            }
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }


    override fun getNotAcceptedInvoice(
        id: Long,
        isProvider: Boolean,
        status: Status
    ): Flow<PagingData<Invoice>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
//            remoteMediator = NotAcceptedRemoteMediator(
//                api = api, room = room, id = id, isProvider = isProvider
//            ),
            pagingSourceFactory = {GetInvoicesByStatusPagingSource(api , id , isProvider , status) }
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }


}