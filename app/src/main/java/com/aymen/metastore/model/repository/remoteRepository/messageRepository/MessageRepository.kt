package com.aymen.metastore.model.repository.remoteRepository.messageRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.Enum.MessageType
import com.aymen.store.model.Enum.AccountType
import com.aymen.metastore.model.entity.dto.ConversationDto
import com.aymen.metastore.model.entity.dto.MessageDto
import com.aymen.metastore.model.entity.roomRelation.ConversationWithUserOrCompany
import com.aymen.metastore.model.entity.roomRelation.MessageWithCompanyAndUserAndConversation
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface MessageRepository {

    suspend fun sendMessage(conversation: ConversationDto):Response<Void>
    suspend fun getConversationByCaleeId(id : Long, messageType: MessageType) : Response<ConversationDto>
//    suspend fun getAllMessageByCaleeId(id : Long, typeype: AccountType): Response<List<MessageDto>>

    fun getAllConversation(): Flow<PagingData<ConversationWithUserOrCompany>>
    fun getAllMessagesByConversationId(conversationId : Long, accountType: AccountType) : Flow<PagingData<MessageWithCompanyAndUserAndConversation>>
}