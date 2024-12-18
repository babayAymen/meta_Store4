package com.aymen.store.model.repository.remoteRepository.paymentRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.entity.dto.PaymentForProvidersDto
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.paging.remotemediator.BuyHistoryMediator
import com.aymen.metastore.model.entity.paging.remotemediator.InCompleteRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.NotPaidremoteMediator
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

//    @OptIn(ExperimentalPagingApi::class)
    override fun getPaidInvoice(id: Long, isProvider: Boolean, paymentStatus: PaymentStatus): Flow<PagingData<Invoice>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
//            remoteMediator = PayedRemoteMediator(
//                api = api, room = room, id = id, isProvider = isProvider
//            ),
            pagingSourceFactory = {
                PayedPagingSource(api, id , isProvider, paymentStatus)
            }
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getNotPaidInvoice(id: Long, isProvider : Boolean): Flow<PagingData<InvoiceWithClientPersonProvider>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = NotPaidremoteMediator(
                api = api, room = room, id = id, isProvider = isProvider
            ),
            pagingSourceFactory = {
                if(isProvider) {
                    invoiceDao.getAllMyBuyHistoryFromPaidInvoiceAsProvider(id = id, paid = PaymentStatus.NOT_PAID)
                }else{
                    invoiceDao.getAllMyBuyHistoryFromPaidInvoiceAsClient(id = id, paid = PaymentStatus.NOT_PAID)
                }
            }
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getInCompleteInvoice(id: Long, isProvider: Boolean): Flow<PagingData<InvoiceWithClientPersonProvider>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = InCompleteRemoteMediator(
                api = api, room = room, id = id, isProvider = isProvider
            ),
            pagingSourceFactory = {
                if(isProvider) {
                    invoiceDao.getAllMyBuyHistoryFromIncompleteInvoice(id = id, paid = PaymentStatus.INCOMPLETE)
                }else{
                    invoiceDao.getAllMyBuyHistoryFromIncompleteInvoiceAsClient(id = id, paid = PaymentStatus.INCOMPLETE)
                }
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