package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.CategoryRemoteKeysEntity
import com.aymen.metastore.model.entity.room.remoteKeys.CommentArticleRemoteKeys
import com.aymen.metastore.model.entity.roomRelation.CategoryWithCompanyAndUser
import com.aymen.metastore.model.entity.roomRelation.CommentWithArticleAndUserOrCompany
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class CommentArticleRemoteMediator(
    private val api: ServiceApi,
    private val room : AppDatabase,
    private val articleId : Long
):RemoteMediator<Int, CommentWithArticleAndUserOrCompany>() {

    private val articleDao = room.articleDao()
    private val articleCompanyDao = room.articleCompanyDao()
    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val commentDao = room.commentDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CommentWithArticleAndUserOrCompany>
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
            val response = api.getArticleComments(articleId,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else response.number - 1
            val nextPage = if (endOfPaginationReached) null else response.number + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    commentDao.insertCommentRemoteKeys(response.content.map { comment ->
                        CommentArticleRemoteKeys(
                            id = comment.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                            articleId = comment.article?.id!!
                        )
                    })
                    userDao.insertUser(response.content.map { user -> user.user?.toUser() })
                    userDao.insertUser(response.content.map { user -> user.company?.user?.toUser() })
                    companyDao.insertCompany(response.content.map { company -> company.company?.toCompany() })
                    articleDao.insertArticle(response.content.map { article -> article.article?.article?.toArticle(false) })
                    articleCompanyDao.insertArticle(response.content.map { article -> article.article?.toArticleCompany(true) })
                    commentDao.insertComment(response.content.map { comment -> comment.toComment() })



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
        return commentDao.getFirstCommentArticleRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem() :Int? {
        return commentDao.getLatestCommentArticleRemoteKey()?.nextPage
    }

    private suspend fun deleteCache(){
        commentDao.clearAllCommentArticleTableByArticleId(articleId)
        commentDao.clearAllRemoteKeysTableByArticleId(articleId)
    }
}