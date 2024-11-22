package com.aymen.store.model.repository.remoteRepository.messageRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.Enum.LoadType
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.entity.dto.ConversationDto
import com.aymen.metastore.model.entity.paging.ConversationRemoteMediator
import com.aymen.metastore.model.entity.paging.MessageRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.roomRelation.ConversationWithUserOrCompany
import com.aymen.metastore.model.entity.roomRelation.MessageWithCompanyAndUserAndConversation
import com.aymen.metastore.model.repository.remoteRepository.messageRepository.MessageRepository
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.store.model.repository.globalRepository.ServiceApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val api : ServiceApi,
    private val room : AppDatabase
): MessageRepository {

    private val messageDao = room.messageDao()
    private val conversationDao = room.conversationDao()

//    override suspend fun getAllMyMessageByConversationId(conversationId : Long) = api.getAllMyMessageByConversationId(conversationId)
    override suspend fun sendMessage(conversation: ConversationDto) = api.sendMessage(conversation)
    override suspend fun getConversationByCaleeId(id: Long, messageType: MessageType) = api.getConversationByCaleeId(id,messageType)
//    override suspend fun getAllMessageByCaleeId(
//        id: Long,
//        type: AccountType
//    ) = api.getAllMessageByCaleeId(id,type )

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllConversation(): Flow<PagingData<ConversationWithUserOrCompany>> {
        return  Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = ConversationRemoteMediator(
                api = api, room = room, type = LoadType.RANDOM
            ),
            pagingSourceFactory = { conversationDao.getAllConversation()}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMessagesByConversationId(conversationId: Long, accountType: AccountType): Flow<PagingData<MessageWithCompanyAndUserAndConversation>> {
        return  Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = 3),
            remoteMediator = MessageRemoteMediator(
                api = api, room = room, type = LoadType.RANDOM, id = conversationId, accountType = accountType
            ),
            pagingSourceFactory = { messageDao.getAllMessagesByConversationId(conversationId)}
        ).flow.map {
            it.map { article ->
                article
            }
        }
    }


}