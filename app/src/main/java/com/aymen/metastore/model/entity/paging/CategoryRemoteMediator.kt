package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.CategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.CategoryWithCompanyAndUser
import com.aymen.store.model.repository.globalRepository.ServiceApi

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
                    getNextPageClosestToCurrentPosition(state)?.minus(1)?:0
                }

                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem(state)
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    previousePage
                }

                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem(state)
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }
            val response = api.getAllCategoryByCompany(id!!,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    categoryDao.insertKeys(response.map { article ->
                        CategoryRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                            lastUpdated = null
                        )
                    })

                    userDao.insertUser(response.map {user -> user.company?.user?.toUser()})
                    companyDao.insertCompany(response.map {company -> company.company?.toCompany()})
                    categoryDao.insertCategory(response.map {category -> category.toCategory() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, CategoryWithCompanyAndUser>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { categoryDao.getCategoryRemoteKey(it.category.id!!).prevPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, CategoryWithCompanyAndUser>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { categoryDao.getCategoryRemoteKey(it.category.id!!).nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, CategoryWithCompanyAndUser>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.category?.id?.let { categoryDao.getCategoryRemoteKey(it).nextPage }
    }

    private suspend fun deleteCache(){
     //   categoryDao.clearAllCategoryTable()
        categoryDao.clearAllRemoteKeysTable()
    }
}