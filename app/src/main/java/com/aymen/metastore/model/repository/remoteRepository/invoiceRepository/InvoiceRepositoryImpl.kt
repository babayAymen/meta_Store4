package com.aymen.metastore.model.repository.remoteRepository.invoiceRepository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.SearchPaymentEnum
import com.aymen.metastore.model.entity.dto.CommandLineDto
import com.aymen.metastore.model.entity.dto.InvoiceDto
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.paging.pagingsource.SearchInvoicePagingSource
import com.aymen.metastore.model.entity.paging.remotemediator.AllInvoiceRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.CommandLineByInvoiceRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.InvoiceAsClientAndStatusRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.InvoiceRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.Enum.PaymentStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class InvoiceRepositoryImpl @Inject constructor(
    private val api: ServiceApi,
    private val room : AppDatabase
) : InvoiceRepository {

    private val invoiceDao = room.invoiceDao()
    private val commandLineDao = room.commandLineDao()

    override fun getAllMyInvoicesAsProvider(
        companyId: Long,
        isProvider: Boolean,
        status: PaymentStatus
    ): Flow<PagingData<Invoice>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = AllInvoiceRemoteMediator(
                api = api, room = room, id = companyId, status = status
            ),
            pagingSourceFactory = {
                Log.e("itemcountinvoice","status : $status companyid $companyId isprovider : $isProvider")
                when (status) {
                    PaymentStatus.ALL -> {
                       val r = invoiceDao.getAllMyInvoiceAsProvider(companyId = companyId)
                        Log.e("itemcountinvoice","without r")
                        Log.e("itemcountinvoice","r : $r")
                        r
                    }
                    else -> invoiceDao.getAllMyInvoiceAsProviderAndStatus(companyId, status)

                }
            }
        ).flow.map {
            it.map { article ->
                Log.e("itemcountinvoice","article $article")
                article.toInvoiceWithClientPersonProvider()
            }
        }
    }

    override fun getAllInvoicesAsClient(
        clientId: Long,
        accountType: AccountType,
        status: PaymentStatus
    ): Flow<PagingData<Invoice>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = InvoiceRemoteMediator(
                api = api, room = room, type = accountType, id = clientId, status = status
            ),
            pagingSourceFactory = {
                when (accountType) {
                    AccountType.COMPANY -> {
                        if (status == PaymentStatus.ALL) invoiceDao.getAllMyInvoiceAsClient(clientId = clientId)
                        else invoiceDao.getAllMyInvoiceAsClientAndPaid(
                            clientId = clientId,
                            status = status
                        )
                    }

                    AccountType.USER -> {
                        if (status == PaymentStatus.ALL) invoiceDao.getAllMyInvoiceAsPersonClient(
                            clientId = clientId,
                            isInvoice = true
                        )
                        else invoiceDao.getAllMyInvoiceAsPersonClientAndPaid(
                            clientId = clientId,
                            status = status
                        )

                    }

                    AccountType.META -> TODO()
                    AccountType.NULL -> TODO()
                    AccountType.SELLER -> TODO()
                    AccountType.DELIVERY -> TODO()
                }
            }
        ).flow.map {
            it.map { article ->
                article.toInvoiceWithClientPersonProvider()
            }
        }
    }

    override fun getAllInvoicesAsClientAndStatus(
        clientId: Long,
        status: Status
    ): Flow<PagingData<Invoice>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = InvoiceAsClientAndStatusRemoteMediator(
                api = api, room = room, id = clientId, status = status
            ),
            pagingSourceFactory = {
                invoiceDao.getAllMyInvoiceAsClientAndStatus(clientId = clientId, status = status)
            }
        ).flow.map {
            it.map { article ->
                article.toInvoiceWithClientPersonProvider()
            }
        }
    }

    override fun getAllCommandLineByInvoiceId(
        companyId: Long,
        invoiceId: Long
    ): Flow<PagingData<CommandLine>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = CommandLineByInvoiceRemoteMediator(
                api = api, room = room, companyId = companyId, invoiceId = invoiceId
            ),
            pagingSourceFactory = {
                commandLineDao.getAllCommandsLineByInvoiceId(invoiceId)
            }
        ).flow.map {
            it.map { article ->
                article.toCommandLineModel()
            }
        }
    }

    override suspend fun getLastInvoiceCode(asProvider: Boolean) = api.getLastInvoiceCode(asProvider)
    override suspend fun addInvoice(
        commandLineDtos: List<CommandLine>,
        clientId: Long,
        invoiceCode: Long,
        discount: Double,
        clientType: AccountType,
        invoiceMode: InvoiceMode,
        asProvider: Boolean
    ): Response<List<CommandLineDto>> = api.addInvoice(
        commandLineDtos, clientId, invoiceCode, discount,
        clientType, invoiceMode, type = "pdf-save-client", asProvider
    )

    override suspend fun getAllMyInvoicesAsClientAndStatus(
        id: Long,
        status: Status
    ): Response<List<InvoiceDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun accepteInvoice(invoiceId: Long, status: Status) =
        api.acceptInvoice(invoiceId, status)

    override suspend fun getAllMyPaymentNotAccepted(companyId: Long): Response<List<InvoiceDto>> {
        TODO("Not yet implemented")
    }

    override fun searchInvoice(type: SearchPaymentEnum, text: String, companyId: Long): Flow<PagingData<Invoice>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            pagingSourceFactory = {
                SearchInvoicePagingSource(
                    api = api,
                    type = type,
                    text = text,
                    companyId = companyId
                )
            }
        ).flow
    }

    override suspend fun deleteInvoiceById(invoiceId: Long) = api.deleteInvoiceById(invoiceId)
    override suspend fun acceptInvoiceAsDelivery(orderId: Long): Response<Boolean> = api.acceptInvoiceAsDelivery(orderId)
    override suspend fun submitOrderDelivered(orderId: Long, code: String): Response<Boolean> = api.submitOrderDelivered(orderId, code)
    override suspend fun userRejectOrder(orderId: Long): Response<Void> = api.userRejectOrder(orderId)

}




















