package com.aymen.store.model.repository.remoteRepository.messageRepository

import com.aymen.metastore.model.Enum.MessageType
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.dto.ConversationDto
import com.aymen.store.model.entity.dto.MessageDto
import retrofit2.Response

interface MessageRepository {
    suspend fun getAllMyConversations():Response<List<ConversationDto>>

    suspend fun getAllMyMessageByConversationId(conversationId : Long):Response<List<MessageDto>>
    suspend fun sendMessage(conversation: ConversationDto):Response<Void>
    suspend fun getConversationByCaleeId(id : Long, messageType: MessageType) : Response<ConversationDto>
    suspend fun getAllMessageByCaleeId(id : Long, typeype: AccountType): Response<List<MessageDto>>
}