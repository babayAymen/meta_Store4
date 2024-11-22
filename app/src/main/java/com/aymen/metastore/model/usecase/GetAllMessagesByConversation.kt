package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.MessageWithCompanyAndUserAndConversation
import com.aymen.metastore.model.repository.remoteRepository.messageRepository.MessageRepository
import com.aymen.store.model.Enum.AccountType
import kotlinx.coroutines.flow.Flow

class GetAllMessagesByConversation(private val repository : MessageRepository) {

     operator fun invoke(id : Long, accountType: AccountType) : Flow<PagingData<MessageWithCompanyAndUserAndConversation>>{
        return repository.getAllMessagesByConversationId(id, accountType)
    }

}