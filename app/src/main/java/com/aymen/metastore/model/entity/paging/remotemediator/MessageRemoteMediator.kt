package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.MessageRemoteKeysEntity
import com.aymen.metastore.model.entity.roomRelation.MessageWithCompanyAndUserAndConversation
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class MessageRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val type  : com.aymen.metastore.model.Enum.LoadType,
    private val id : Long,
    private val accountType: AccountType?
)  : RemoteMediator<Int, MessageWithCompanyAndUserAndConversation>(){

    private val userDao = room.userDao()
    private val companyDao = room.companyDao()
    private val messageDao = room.messageDao()
    private val conversationDao = room.conversationDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageWithCompanyAndUserAndConversation>
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
            val response = when(type){
                com.aymen.metastore.model.Enum.LoadType.RANDOM -> {
                    api.getAllMessageByCaleeId(id,accountType!!,currentPage, PAGE_SIZE)
                }
                com.aymen.metastore.model.Enum.LoadType.ADMIN -> {
                    api.getAllMyMessageByConversationId(id,currentPage, PAGE_SIZE)
                }
                com.aymen.metastore.model.Enum.LoadType.CONTAINING -> {
                    api.getAllMessageByCaleeId(id,accountType!!,currentPage, PAGE_SIZE)
                }
            }
            val endOfPaginationReached = response.isEmpty() || response.size < state.config.pageSize
            val prevPage = if (currentPage == 0) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    messageDao.insertRemoteKeys(response.map { article ->
                        MessageRemoteKeysEntity(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage,
                        )
                    })

                    userDao.insertUser(response.map {user -> user.conversation?.user1?.toUser()!!})
                    userDao.insertUser(response.map {user -> user.conversation?.user2?.toUser()!!})
                    userDao.insertUser(response.map {user -> user.conversation?.company1?.user?.toUser()!!})
                    userDao.insertUser(response.map {user -> user.conversation?.company2?.user?.toUser()!!})
                    companyDao.insertCompany(response.map {company -> company.conversation?.company1?.toCompany()!!})
                    companyDao.insertCompany(response.map {company -> company.conversation?.company2?.toCompany()!!})
                    conversationDao.insertConversation(response.map {conversation -> conversation.conversation?.toConversation()!! })
                    messageDao.insertMessage(response.map { message -> message.toMessage() })

                } catch (ex: Exception) {
                    Log.e("error", ex.message.toString())
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(state: PagingState<Int, MessageWithCompanyAndUserAndConversation>): Int? {
        val loadResult = state.pages.firstOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.firstOrNull()
        return entity?.let { messageDao.getMessageRemoteKey(it.message.id!!).prevPage }
    }

    private suspend fun getNextPageForTheLasttItem(state: PagingState<Int, MessageWithCompanyAndUserAndConversation>): Int? {
        val loadResult = state.pages.lastOrNull { it.data.isNotEmpty() }
        val entity = loadResult?.data?.lastOrNull()
        return entity?.let { messageDao.getMessageRemoteKey(it.message.id!!).nextPage }
    }

    private suspend fun getNextPageClosestToCurrentPosition(state: PagingState<Int, MessageWithCompanyAndUserAndConversation>): Int? {
        val position = state.anchorPosition
        val entity = position?.let { state.closestItemToPosition(it) }
        return entity?.message?.id?.let { messageDao.getMessageRemoteKey(it).nextPage }
    }

    private suspend fun deleteCache(){
        messageDao.clearAllMessages()
        messageDao.clearAllMessageRemoteKeys()
    }
}