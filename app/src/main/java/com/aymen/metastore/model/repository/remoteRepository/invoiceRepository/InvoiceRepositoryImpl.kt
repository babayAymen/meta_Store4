package com.aymen.metastore.model.repository.remoteRepository.invoiceRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.InvoiceMode
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.dto.InvoiceDto
import com.aymen.metastore.model.entity.model.CommandLine
import com.aymen.metastore.model.entity.paging.remotemediator.AllInvoiceRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.CommandLineByInvoiceRemoteMediator
import com.aymen.metastore.model.entity.paging.InvoiceAsClientAndStatusRemoteMediator
import com.aymen.metastore.model.entity.paging.InvoiceRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.CommandLineWithInvoiceAndArticle
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.globalRepository.ServiceApi
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

    override fun getAllMyInvoicesAsProvider(companyId : Long) :Flow<PagingData<InvoiceWithClientPersonProvider>>{
       return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = AllInvoiceRemoteMediator(
                api = api, room = room, id= companyId
            ),
            pagingSourceFactory = { invoiceDao.getAllMyInvoiceAsProvider(companyId = companyId)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    override fun getAllInvoicesAsClient(clientId: Long, accountType : AccountType): Flow<PagingData<InvoiceWithClientPersonProvider>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = InvoiceRemoteMediator(
                api = api, room = room, type = LoadType.ADMIN, id= clientId, status = null
            ),
            pagingSourceFactory = {
                if(accountType == AccountType.COMPANY){
                invoiceDao.getAllMyInvoiceAsClient(clientId = clientId)
                }else{
                    invoiceDao.getAllMyInvoiceAsPersonClient(clientId = clientId, isInvoice = true)
                }
            }
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    override fun getAllInvoicesAsClientAndStatus(
        clientId: Long,
        status: Status
    ): Flow<PagingData<InvoiceWithClientPersonProvider>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = InvoiceAsClientAndStatusRemoteMediator(
                api = api, room = room, id= clientId,status = status
            ),
            pagingSourceFactory = { invoiceDao.getAllMyInvoiceAsClientAndStatus(clientId = clientId, status = status)}
        ).flow.map {
            it.map { article ->
                article
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
        ) = api.addInvoice(commandLineDtos,clientId,invoiceCode,discount,
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