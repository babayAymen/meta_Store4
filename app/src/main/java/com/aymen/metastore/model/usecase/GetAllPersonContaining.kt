package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyClient
import com.aymen.metastore.model.entity.roomRelation.CompanyWithCompanyOrUser
import com.aymen.store.model.Enum.SearchCategory
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepository
import kotlinx.coroutines.flow.Flow

class GetAllPersonContaining(private val repository : ClientRepository) {

    operator fun invoke(personName : String , searchType : SearchType, searchCategory: SearchCategory) : Flow<PagingData<CompanyWithCompanyClient>>{
        return repository.getAllClientUserContaining(personName , searchType, searchCategory)
    }
}