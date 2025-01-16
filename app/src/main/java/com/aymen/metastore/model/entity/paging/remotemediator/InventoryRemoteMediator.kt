package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.entity.Category
import com.aymen.metastore.model.entity.room.entity.SubCategory
import com.aymen.metastore.model.entity.room.remoteKeys.InventoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InventoryWithArticle
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class InventoryRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long
): RemoteMediator<Int , InventoryWithArticle> (){


    private val inventoryDao = room.inventoryDao()
    private val articleCompanyDao = room.articleCompanyDao()
    private val articleDao = room.articleDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: androidx.paging.LoadType,
        state: PagingState<Int, InventoryWithArticle>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                androidx.paging.LoadType.REFRESH -> {
                     0
                }

                androidx.paging.LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem()
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    previousePage
                }

                androidx.paging.LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem()
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }
            val response = api.getInventory(id,currentPage, PAGE_SIZE)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == androidx.paging.LoadType.REFRESH){
                        deleteCache()
                    }
                    inventoryDao.insertRemoteKeys(response.content.map { article ->
                        InventoryRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.company?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.company?.toCompany()})
                    articleDao.insertArticle(response.content.map { article -> article.article?.article?.toArticle(isMy = true) })
                    articleCompanyDao.insertArticle(response.content.map { article -> article.article?.toArticleCompany(false)})
                    inventoryDao.insertInventory(response.content.map {inventory -> inventory.toInventory() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return inventoryDao.getFirstAllRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
       return inventoryDao.getLatestAllRemoteKey()?.nextPage
    }

    private suspend fun deleteCache(){
        inventoryDao.clearAllInventoryTables()
        inventoryDao.clearAllRemoteKeysTabels()
    }

}