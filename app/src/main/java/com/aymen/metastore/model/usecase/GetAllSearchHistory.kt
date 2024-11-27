package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.SearchHistoryWithClientOrProviderOrUserOrArticle
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepository
import kotlinx.coroutines.flow.Flow

class GetAllSearchHistory(private val repository : ClientRepository) {

    operator fun invoke(id : Long): Flow<PagingData<SearchHistoryWithClientOrProviderOrUserOrArticle>>{
        return repository.getAllHistory(id = id)
    }
}