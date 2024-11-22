package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.ConversationWithUserOrCompany
import com.aymen.metastore.model.repository.remoteRepository.messageRepository.MessageRepository
import kotlinx.coroutines.flow.Flow

class GetAllConversation(private val repository: MessageRepository) {

    operator fun invoke(): Flow<PagingData<ConversationWithUserOrCompany>> {
        return repository.getAllConversation()
    }

}