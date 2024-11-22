package com.aymen.metastore.model.entity.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.entity.Article
import com.aymen.metastore.model.entity.room.remoteKeys.ArtRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val companyId : Long
): RemoteMediator<Int, Article>() {


    private val articleDao = room.articleDao()
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    getNextPageClosestToCurrentPosition(state)?.minus(1) ?: 0
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
            val response = api.getAllArticlesByCategor(companyId,currentPage, PAGE_SIZE)

            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    articleDao.insertKeys(response.map { article ->
                        ArtRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    articleDao.insertArticle(response.map {article -> article.toArticle() })

                } catch (ex: Exception) {
                    Log.e("error", "article ${ex.message}")
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, Article>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { articleDao.getArticleRemoteKey(it.id!!).prevPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, Article>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { articleDao.getArticleRemoteKey(it.id!!).nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, Article>): Int? {
        val position = state.anchorPosition
        Log.e("getNextPageClosestToCurrentPosition", "position $position")
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.id?.let { articleDao.getArticleRemoteKey(it).nextPage }
    }

    private suspend fun deleteCache(){
        articleDao.clearAllArticle()
        articleDao.clearAllArticleRemoteKeys()
    }
}