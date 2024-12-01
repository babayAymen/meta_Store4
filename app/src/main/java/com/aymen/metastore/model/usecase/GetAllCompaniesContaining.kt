package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.model.entity.roomRelation.SearchHistoryWithClientOrProviderOrUserOrArticle
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.remoteRepository.companyRepository.CompanyRepository
import kotlinx.coroutines.flow.Flow

class GetAllCompaniesContaining(private val repository: CompanyRepository) {

    operator fun invoke(search : String, searchType: SearchType, myId : Long) : Flow<PagingData<SearchHistoryWithClientOrProviderOrUserOrArticle>>{
        return repository.getAllCompaniesContaining(search, searchType, myId)
    }
}