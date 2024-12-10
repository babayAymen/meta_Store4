package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.entity.ArticleCompany
import com.aymen.metastore.model.entity.room.entity.Category
import com.aymen.metastore.model.entity.room.entity.Invoice
import com.aymen.metastore.model.entity.room.entity.SubCategory
import com.aymen.metastore.model.entity.room.remoteKeys.CommandLineByInvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.CommandLineWithInvoiceAndArticle
import com.aymen.store.model.repository.globalRepository.ServiceApi

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
    private val categoryDao = room.categoryDao()
    private val subCategoryDao = room.subCategoryDao()
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
                    getNextPageClosestToCurrentPosition(state)?.minus(1) ?: 0
                }

                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem(state)
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    previousePage
                }

                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem(state)
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    nextePage
                }
            }
            val response = api.getAllCommandLinesByInvoiceId(companyId , invoiceId , currentPage, state.config.pageSize)
            Log.e("getAllOrdersLineByInvoiceId", "begin in viewmodel response $response")
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    commandLineDao.insertCommandLineByInvoiceKeys(response.map { article ->
                        CommandLineByInvoiceRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                        )
                    })


                    userDao.insertUser(response.map {user -> user.article?.company?.user?.toUser()})
                    userDao.insertUser(response.map {user -> user.article?.provider?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.article?.company?.toCompany()})
                    companyDao.insertCompany(response.map { company -> company.article?.provider?.toCompany() })
                    categoryDao.insertCategory(response.map {category -> category.article?.category?.toCategory() })
                    categoryDao.insertCategory(response.map {category -> category.article?.subCategory?.category?.toCategory() })
                    subCategoryDao.insertSubCategory(response.map {subCategory -> subCategory.article?.subCategory?.toSubCategory() })
                    articleDao.insertArticle(response.map {article -> article.article?.article?.toArticle(isMy = true)!! })
                    articleCompanyDao.insertArticle(response.map { it.article?.toArticleCompany(false)})

                    userDao.insertUser(response.map {user -> user.invoice?.person?.toUser()})
                    userDao.insertUser(response.map {user -> user.invoice?.client?.user?.toUser()})
                    userDao.insertUser(response.map {user -> user.invoice?.provider?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.invoice?.client?.toCompany()})
                    companyDao.insertCompany(response.map {company -> company.invoice?.provider?.toCompany()})
                    invoiceDao.insertInvoice(response.map {invoice -> invoice.invoice?.toInvoice(isInvoice = false) })

                    commandLineDao.insertCommandLine(response.map { line -> line.toCommandLine() })

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

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, CommandLineWithInvoiceAndArticle>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val reomteKey = entity?.let { commandLineDao.getCommandLineByInvoiceRemoteKey(it.commandLine.id!!) }
        return reomteKey?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, CommandLineWithInvoiceAndArticle>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        val reomteKey = entity?.let { commandLineDao.getCommandLineByInvoiceRemoteKey(it.commandLine.id!!) }
        return reomteKey?.nextPage
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, CommandLineWithInvoiceAndArticle>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val reomteKey = entity?.commandLine?.id?.let { commandLineDao.getCommandLineByInvoiceRemoteKey(it) }
        return reomteKey?.nextPage
    }

    private suspend fun deleteCache(){
        commandLineDao.clearAllCommandLineByInvoice()
        commandLineDao.clearInvoiceRemoteKeysTable()
    }
}