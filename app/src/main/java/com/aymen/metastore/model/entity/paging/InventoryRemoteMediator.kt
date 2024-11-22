package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.entity.Category
import com.aymen.metastore.model.entity.room.entity.SubCategory
import com.aymen.metastore.model.entity.room.remoteKeys.InventoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.InvoiceRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.InventoryWithArticle
import com.aymen.metastore.model.entity.roomRelation.InvoiceWithClientPersonProvider
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.repository.globalRepository.ServiceApi

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
    private val categoryDao = room.categoryDao()
    private val subCategoryDao = room.subCategoryDao()

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
                    getNextPageClosestToCurrentPosition(state)?.minus(1) ?: 0
                }

                androidx.paging.LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem(state)
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    previousePage
                }

                androidx.paging.LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem(state)
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }
            val response = api.getInventory(id,currentPage, PAGE_SIZE)
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == androidx.paging.LoadType.REFRESH){
                        deleteCache()
                    }
                    inventoryDao.insertRemoteKeys(response.map { article ->
                        InventoryRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                        )
                    })

                    userDao.insertUser(response.map {user -> user.company?.user?.toUser()!!})
                    companyDao.insertCompany(response.map {company -> company.company?.toCompany()!!})
                    articleDao.insertArticle(response.map { article -> article.article?.article?.toArticle()!! })
                    categoryDao.insertCategory(response.map { category -> category.article?.category?.toCategory()?:Category() })
                    subCategoryDao.insertSubCategory(response.map { subCategory -> subCategory.article?.subCategory?.toSubCategory()?:SubCategory() })
                    articleCompanyDao.insertArticle(response.map { article -> article.article?.toArticleCompany(false)!! })
                    inventoryDao.insertInventory(response.map {inventory -> inventory.toInventory() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, InventoryWithArticle>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { inventoryDao.getInventoryRemoteKey(it.inventory.id!!).prevPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, InventoryWithArticle>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { inventoryDao.getInventoryRemoteKey(it.inventory.id!!).nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, InventoryWithArticle>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.inventory?.id?.let { inventoryDao.getInventoryRemoteKey(it).nextPage }
    }

    private suspend fun deleteCache(){
        inventoryDao.clearAllInventoryTables()
        inventoryDao.clearAllRemoteKeysTabels()
    }

}