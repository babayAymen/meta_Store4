package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.ArticleCompanyRandomRKE
import com.aymen.metastore.model.entity.roomRelation.ArticleWithArticleCompany
import com.aymen.metastore.model.entity.roomRelation.RandomArticleChild
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.store.model.Enum.CompanyCategory

@OptIn(ExperimentalPagingApi::class)
class ArticleCompanyRandomMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val category : CompanyCategory,
    private val companyId : Long?
):RemoteMediator<Int, ArticleWithArticleCompany>() {


    private val articleCompanyDao = room.articleCompanyDao()
    private val companyDao = room.companyDao()
    private val userDao = room.userDao()
    private val articleDao = room.articleDao()
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleWithArticleCompany>
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
//                    Log.e("testRemotemediator","next page in append method : $nextPage")
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }
            val response = api.getRandomArticles(category,currentPage, state.config.pageSize)

            response.content.map { article -> Log.e("testarticle","$article") }
            val endOfPaginationReached = response.last
//            Log.e("testRemotemediator","endpaginationreached : $endOfPaginationReached response last : ${response.last}")
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    articleCompanyDao.insertArticleRandomKeys(response.content.map { article ->
                        ArticleCompanyRandomRKE(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    userDao.insertUser(response.content.map {user -> user.company?.user?.toUser()})
                    companyDao.insertCompany(response.content.map {company -> company.company?.toCompany()})
                    articleDao.insertArticle(response.content.map {article -> article.article?.toArticle(isMy = article.company?.id == companyId) })
                        articleCompanyDao.insertOrUpdate(response.content.map { article -> article.toArticleCompany(isRandom = true, isMy = false) })

                } catch (ex: Exception) {
                    Log.e("error", "articlecompany ${ex}")
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("error", "articlecompany ${ex.message}")
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
       return articleCompanyDao.getFirstRandomArticleCompanyRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
     return articleCompanyDao.getLatestRandomArticeCompanyRemoteKey()?.nextPage
    }


    private suspend fun deleteCache(){
        val ids = articleCompanyDao.getAllIdsByCategory(category)
        articleCompanyDao.clearAllRandomArticleCompanyTable(category)
        ids.forEach {
        articleCompanyDao.clearRandomRemoteKeysTableById(it)
        }
    }
}