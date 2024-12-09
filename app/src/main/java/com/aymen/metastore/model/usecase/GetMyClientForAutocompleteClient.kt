package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.ClientProviderRelationDto
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepository
import kotlinx.coroutines.flow.Flow

class GetMyClientForAutocompleteClient(private val repository : ClientRepository) {

    operator fun invoke(companyId : Long , clientName : String) : Flow<PagingData<ClientProviderRelationDto>>{
        return repository.getMyClientForAutocompleteClient(companyId , clientName)
    }
}