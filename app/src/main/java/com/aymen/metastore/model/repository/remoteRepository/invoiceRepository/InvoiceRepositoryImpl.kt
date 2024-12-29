package com.aymen.metastore.model.repository.remoteRepository.invoiceRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.entity.dto.CommandLineDto
import com.aymen.metastore.model.entity.dto.InvoiceDto
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.model.Invoice
import com.aymen.metastore.model.entity.paging.remotemediator.AllInvoiceRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.CommandLineByInvoiceRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.InvoiceAsClientAndStatusRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.InvoiceRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.CommandLineWithInvoiceAndArticle
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
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = AllInvoiceRemoteMediator(
                api = api, room = room, id= companyId, status = status
            ),
            pagingSourceFactory = {
                when(status){
                    PaymentStatus.ALL -> invoiceDao.getAllMyInvoiceAsProvider(companyId = companyId)
                    else -> invoiceDao.getAllMyInvoiceAsProviderAndStatus(companyId, status)

                }
            }
        ).flow.map {
            it.map { article ->
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
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = InvoiceRemoteMediator(
                api = api, room = room, type = accountType, id= clientId, status = status
            ),
            pagingSourceFactory = {
                when(accountType){
                    AccountType.COMPANY -> {
                        if(status == PaymentStatus.ALL) invoiceDao.getAllMyInvoiceAsClient(clientId = clientId)
                        else invoiceDao.getAllMyInvoiceAsClientAndPaid(clientId = clientId, status = status)
                    }
                    AccountType.USER -> {
                        if(status == PaymentStatus.ALL) invoiceDao.getAllMyInvoiceAsPersonClient(clientId = clientId, isInvoice = true)
                        else invoiceDao.getAllMyInvoiceAsPersonClientAndPaid(clientId = clientId, status = status)

                    }
                    AccountType.META -> TODO()
                    AccountType.NULL -> TODO()
                    AccountType.SELLER -> TODO()
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
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = InvoiceAsClientAndStatusRemoteMediator(
                api = api, room = room, id= clientId,status = status
            ),
            pagingSourceFactory = { invoiceDao.getAllMyInvoiceAsClientAndStatus(clientId = clientId, status = status)
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
    ): Flow<PagingData<CommandLineWithInvoiceAndArticle>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = CommandLineByInvoiceRemoteMediator(
                api = api , room = room , companyId = companyId , invoiceId = invoiceId
            ),
            pagingSourceFactory = {
                commandLineDao.getAllCommandsLineByInvoiceId(invoiceId)
            }
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }


    //    override suspend fun getAllMyInvoicesAsClient(companyId : Long) = api.getAllMyInvoicesAsClient(companyId = companyId)
    override suspend fun getLastInvoiceCode() = api.getLastInvoiceCode()
    override suspend fun addInvoice(
        commandLineDtos: List<CommandLine>,
        clientId: Long, invoiceCode: Long,
        discount: Double, clientType: AccountType,
        invoiceMode: InvoiceMode,
        ): Response<List<CommandLineDto>> = api.addInvoice(commandLineDtos,clientId,invoiceCode,discount,
        clientType, invoiceMode,type = "pdf-save-client")

    override suspend fun getAllMyInvoicesAsClientAndStatus(
        id: Long,
        status: Status
    ): Response<List<InvoiceDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun accepteInvoice(invoiceId: Long, status: Status) = api.acceptInvoice(invoiceId , status)
    override suspend fun getAllMyPaymentNotAccepted(companyId: Long): Response<List<InvoiceDto>> {
        TODO("Not yet implemented")
    }
//    override suspend fun getAllMyInvoicesAsProviderAndStatus(companyId: Long, status: PaymentStatus) = api.getAllMyInvoicesAsProviderAndStatus(companyId, status)

}