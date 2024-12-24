
package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.CategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.CategoryWithCompanyAndUser
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class CategoryRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long?
)
    : RemoteMediator<Int, CategoryWithCompanyAndUser>() {

    private val categoryDao = room.categoryDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CategoryWithCompanyAndUser>
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
            val response = api.getAllCategoryByCompany(id!!,currentPage, state.config.pageSize)
            Log.e("categorylogger","response ${response.content.size} company id $id")
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else response.number - 1
            val nextPage = if (endOfPaginationReached) null else response.number + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    categoryDao.insertKeys(response.content.map { article ->
                        CategoryRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                            lastUpdated = null
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.company?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.company?.toCompany()})
                    categoryDao.insertCategoryCateg(response.content.map {category -> category.toCategory(isCategory = true) })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return categoryDao.getFirstCategoryRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem() :Int? {
        return categoryDao.getLatestCategoryRemoteKey()?.nextPage
    }

    private suspend fun deleteCache(){
           categoryDao.clearAllCategoryTable(id?:0)
        categoryDao.clearAllRemoteKeysTable()
    }
}
