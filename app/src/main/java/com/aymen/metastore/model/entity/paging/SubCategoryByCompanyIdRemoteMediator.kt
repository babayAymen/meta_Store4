package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.SubCategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class SubCategoryByCompanyIdRemoteMediator(

    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long?
): RemoteMediator<Int, SubCategoryWithCategory>() {

    private val subCategoryDao = room.subCategoryDao()
    private val categoryDao = room.categoryDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SubCategoryWithCategory>
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
            val response = api.getAllSubCategories(id!!,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    subCategoryDao.insertKeys(response.map { article ->
                        SubCategoryRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            previousPage = prevPage
                        )
                    })

                    userDao.insertUser(response.map {user -> user.company?.user?.toUser()!!})
                    companyDao.insertCompany(response.map {company -> company.company?.toCompany()!!})
                    categoryDao.insertCategory(response.map {category -> category.category?.toCategory()!! })
                    subCategoryDao.insertSubCategory(response.map {subCategory -> subCategory.toSubCategory() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, SubCategoryWithCategory>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { subCategoryDao.getSubCategoryRemoteKey(it.subCategory.id!!).previousPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, SubCategoryWithCategory>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { subCategoryDao.getSubCategoryRemoteKey(it.subCategory.id!!).nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, SubCategoryWithCategory>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.subCategory?.id?.let { subCategoryDao.getSubCategoryRemoteKey(it).nextPage }
    }

    private suspend fun deleteCache(){
        //   subCategoryDao.clearAllSubCategoryTable()
        subCategoryDao.clearAllRemoteKeysTable()
    }
}