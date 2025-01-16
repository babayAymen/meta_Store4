package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.CommandLineByInvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.CommandLineWithInvoiceAndArticle
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class CommandLineByInvoiceRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val companyId : Long,
    private val invoiceId : Long
):RemoteMediator<Int, CommandLineWithInvoiceAndArticle>() {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val invoiceDao = room.invoiceDao()
    private val articleCompanyDao = room.articleCompanyDao()
    private val articleDao = room.articleDao()
    private val commandLineDao = room.commandLineDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CommandLineWithInvoiceAndArticle>
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
            val response = api.getAllCommandLinesByInvoiceId(companyId , invoiceId , currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    commandLineDao.insertCommandLineByInvoiceKeys(response.content.map { article ->
                        CommandLineByInvoiceRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                        )
                    })


                    userDao.insertUser(response.content.map {user -> user.article?.company?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.article?.company?.toCompany()})
                    articleDao.insertArticle(response.content.map {article -> article.article?.article?.toArticle(isMy = true)!! })
                    articleCompanyDao.insertArticle(response.content.map { it.article?.toArticleCompany(false)})

                    userDao.insertUser(response.content.map {user -> user.invoice?.person?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.invoice?.client?.user?.toUser()})
                    userDao.insertUser(response.content.map {user -> user.invoice?.provider?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.invoice?.client?.toCompany()})
                    companyDao.insertCompany(response.content.map {company -> company.invoice?.provider?.toCompany()})
                    invoiceDao.insertInvoice(response.content.map {invoice -> invoice.invoice?.toInvoice(isInvoice = true) })

                    commandLineDao.insertCommandLine(response.content.map { line -> line.toCommandLine() })

                } catch (ex: Exception) {
                    Log.e("getAllOrdersLineByInvoiceId", ex.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("getAllOrdersLineByInvoiceId", ex.toString())
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
       return commandLineDao.getFirstInvoiceRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
       return commandLineDao.getLatestInvoiceRemoteKey()?.nextPage
    }

    private suspend fun deleteCache(){
        commandLineDao.clearAllCommandLineByInvoice()
        commandLineDao.clearInvoiceRemoteKeysTable()
    }
}