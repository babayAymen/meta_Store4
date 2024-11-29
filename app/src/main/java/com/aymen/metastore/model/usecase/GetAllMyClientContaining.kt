package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.SearchHistoryWithClientOrProviderOrUserOrArticle
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepository
import kotlinx.coroutines.flow.Flow

class GetAllMyClientContaining(private val repository : CompanyRepository) {

    operator fun invoke(id : Long , clientName : String) : Flow<PagingData<SearchHistoryWithClientOrProviderOrUserOrArticle>>{
        return repository.getAllMyClientContaining(id , clientName)
    }
}