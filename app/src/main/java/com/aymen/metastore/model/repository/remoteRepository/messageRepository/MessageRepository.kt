package com.aymen.store.model.repository.remoteRepository.messageRepository

import com.aymen.metastore.model.Enum.MessageType
import com.aymen.store.model.Enum.AccountType
import com.aymen.store.model.entity.dto.ConversationDto
import com.aymen.store.model.entity.dto.MessageDto
import com.aymen.store.model.entity.realm.Conversation
import com.aymen.store.model.entity.realm.Message
import retrofit2.Response

interface MessageRepository {
    suspend fun getAllMyConversations():Response<List<ConversationDto>>

    suspend fun getAllMyMessageByConversationId(conversationId : Long):Response<List<MessageDto>>
    suspend fun getAllMyMessageByConversationIdd(conversationId : Long):Response<List<Message>>

    suspend fun sendMessage(conversation: ConversationDto):Response<Void>

    suspend fun getConversationByCaleeId(id : Long, messageType: MessageType) : Response<Conversation>

    suspend fun getAllMessageByCaleeId(id : Long, typeype: AccountType): Response<List<MessageDto>>
    suspend fun getAllMessageByCaleeIdd(id : Long, typeype: AccountType): Response<List<Message>>
}