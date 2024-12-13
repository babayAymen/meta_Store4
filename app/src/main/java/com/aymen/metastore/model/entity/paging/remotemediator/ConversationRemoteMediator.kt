package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.ConversationRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.ConversationWithUserOrCompany
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class ConversationRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val type  : com.aymen.metastore.model.Enum.LoadType
)  : RemoteMediator<Int, ConversationWithUserOrCompany>(){

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val messageDao = room.messageDao()
    private val conversationDao = room.conversationDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ConversationWithUserOrCompany>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    getNextPageClosestToCurrentPosition(state)?.minus(1) ?: 1
                }

                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem(state)
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = previousPage != null
                    )
                    previousePage
                }

                LoadType.APPEND -> {
                    val nextPage = getNextPageClosestToCurrentPosition(state)
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = nextPage != null
                    )
                    nextePage
                }
            }
            val response = when(type){
                com.aymen.metastore.model.Enum.LoadType.RANDOM -> {
                    api.getAllMyConversations(currentPage, PAGE_SIZE)
                }
                com.aymen.metastore.model.Enum.LoadType.ADMIN -> {
                    api.getAllMyConversations(currentPage, PAGE_SIZE)
                }
                com.aymen.metastore.model.Enum.LoadType.CONTAINING -> {
                    api.getAllMyConversations(currentPage, PAGE_SIZE)
                }
            }
            val endOfPaginationReached = response.isEmpty()
            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    conversationDao.insertKeys(response.map { article ->
                        ConversationRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                        )
                    })

                    userDao.insertUser(response.map {user -> user.user1?.toUser()!!})
                    userDao.insertUser(response.map {user -> user.user2?.toUser()!!})
                    userDao.insertUser(response.map {user -> user.company1?.user?.toUser()!!})
                    userDao.insertUser(response.map {user -> user.company2?.user?.toUser()!!})
                    companyDao.insertCompany(response.map {company -> company.company1?.toCompany()!!})
                    companyDao.insertCompany(response.map {company -> company.company2?.toCompany()!!})
                    conversationDao.insertConversation(response.map {conversation -> conversation.toConversation() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, ConversationWithUserOrCompany>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { conversationDao.getConversationRemoteKey(it.conversation.id!!).prevPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, ConversationWithUserOrCompany>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { conversationDao.getConversationRemoteKey(it.conversation.id!!).nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, ConversationWithUserOrCompany>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.conversation?.id?.let { conversationDao.getConversationRemoteKey(it).nextPage }
    }

    private suspend fun deleteCache(){
        conversationDao.clearAllConversationTable()
        conversationDao.clearAllConversationRemoteKeysTable()
    }
}