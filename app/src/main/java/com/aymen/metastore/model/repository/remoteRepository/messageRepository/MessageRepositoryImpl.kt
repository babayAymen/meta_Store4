package com.aymen.store.model.repository.remoteRepository.messageRepository

import com.aymen.metastore.model.Enum.MessageType
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.dto.ConversationDto
import com.aymen.store.model.repository.globalRepository.ServiceApi
import retrofit2.Response
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val api : ServiceApi
): MessageRepository {
    override suspend fun getAllMyConversations() = api.getAllMyConversations()
    override suspend fun getAllMyMessageByConversationId(conversationId : Long) = api.getAllMyMessageByConversationId(conversationId)
    override suspend fun getAllMyMessageByConversationIdd(conversationId : Long) = api.getAllMyMessageByConversationIdd(conversationId)
    override suspend fun sendMessage(conversation: ConversationDto) = api.sendMessage(conversation)
    override suspend fun getConversationByCaleeId(id: Long, messageType: MessageType) = api.getConversationByCaleeId(id,messageType)
    override suspend fun getAllMessageByCaleeId(
        id: Long,
        type: AccountType
    ) = api.getAllMessageByCaleeId(id,type )
    override suspend fun getAllMessageByCaleeIdd(
        id: Long,
        type: AccountType
    ) = api.getAllMessageByCaleeIdd(id,type )


}