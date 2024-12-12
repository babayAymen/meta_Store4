package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.SubCategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class SubCategoryRemoteMediator(

    private val api : ServiceApi,
    private val room : AppDatabase,
    private val id : Long
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
        Log.e("subcategoryviewModel","call getAllSubCategoriesByCompanyId mediator1")
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
            val response = api.getAllSubCategories(id,currentPage, state.config.pageSize)
            Log.e("subcategoryviewModel","call getAllSubCategoriesByCompanyId mediator")
            Log.e("subcategoryviewModel","call getAllSubCategoriesByCompanyId ${response.size}")
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
                    Log.e("errorsubcategory", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("errorsubcategory", ex.message.toString())
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, SubCategoryWithCategory>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        val remoteKey = entity?.let { subCategoryDao.getSubCategoryRemoteKey(it.subCategory.id!!) }
        return remoteKey?.previousPage
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, SubCategoryWithCategory>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        val remoteKey =  entity?.let { subCategoryDao.getSubCategoryRemoteKey(it.subCategory.id!!) }
        return remoteKey?.nextPage
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, SubCategoryWithCategory>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        val remoteKey =  entity?.subCategory?.id?.let { subCategoryDao.getSubCategoryRemoteKey(it) }
        return remoteKey?.nextPage
    }

    private suspend fun deleteCache(){
//        subCategoryDao.clearAllSubCategoryTable(id)
        subCategoryDao.clearAllRemoteKeysTable()
    }
}