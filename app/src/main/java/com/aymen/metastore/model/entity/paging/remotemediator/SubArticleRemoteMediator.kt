package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.SubArticleRemoteKeys
import com.aymen.metastore.model.entity.room.remoteKeys.SubCategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.SubArticleWithArticles
import com.aymen.metastore.model.entity.roomRelation.SubCategoryWithCategory
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class SubArticleRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val parentId : Long
): RemoteMediator<Int , SubArticleWithArticles>() {

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val articleDao = room.articleDao()
    private val articleCompanyDao = room.articleCompanyDao()
    private val subArticleDao = room.subArticleDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SubArticleWithArticles>
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

            val response = api.getArticlesChilds(parentId,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else response.number - 1
            val nextPage = if (endOfPaginationReached) null else response.number + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    subArticleDao.insertKeys(response.content.map { article ->
                        SubArticleRemoteKeys(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    articleDao.insertArticle(response.content.map { article -> article.childArticle?.article?.toArticle(true) })
                    userDao.insertUser(response.content.map { articleCompany -> articleCompany.childArticle?.provider?.user?.toUser() })
                    companyDao.insertCompany(response.content.map { articleCompany -> articleCompany.childArticle?.provider?.toCompany() })
                    articleCompanyDao.insertArticle(response.content.map { articleCompany -> articleCompany.childArticle?.toArticleCompany(false) })
                    subArticleDao.insertSubArticle(response.content.map { subArticle -> subArticle.toSubArticleEntity() })


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
        return subArticleDao.getFirstSubArticleRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return subArticleDao.getLatestSubArticleRemoteKey()?.nextPage
    }


    private suspend fun deleteCache(){
        subArticleDao.clearAllSubArticleTable()
        subArticleDao.clearAllRemoteKeysTable()
    }
}