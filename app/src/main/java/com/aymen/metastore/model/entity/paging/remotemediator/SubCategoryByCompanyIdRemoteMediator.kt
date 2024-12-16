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
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

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
                    0
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
            val response = api.getAllSubCategories(id!!,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else response.number - 1
            val nextPage = if (endOfPaginationReached) null else response.number + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    subCategoryDao.insertKeys(response.content.map { article ->
                        SubCategoryRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            previousPage = prevPage
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.company?.user?.toUser()!!})
                    companyDao.insertCompany(response.content.map {company -> company.company?.toCompany()!!})
                    categoryDao.insertCategory(response.content.map {category -> category.category?.toCategory()!! })
                    subCategoryDao.insertSubCategory(response.content.map {subCategory -> subCategory.toSubCategory() })

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
      val remote = subCategoryDao.getLatestSubCategoryRemoteKey()
        return if(remote?.nextPage == null) null else remote.nextPage
    }


    private suspend fun deleteCache(){
        subCategoryDao.clearAllSubCategoryTable(id?:0)
        subCategoryDao.clearAllRemoteKeysTable()
    }
}