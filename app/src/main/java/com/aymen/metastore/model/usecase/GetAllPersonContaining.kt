package com.aymen.metastore.model.usecase

import android.util.Log
import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.UserDto
import com.aymen.store.model.Enum.SearchType
import com.aymen.store.model.repository.remoteRepository.clientRepository.ClientRepository
import kotlinx.coroutines.flow.Flow

class GetAllPersonContaining(private val repository : ClientRepository) {

    operator fun invoke(id : Long,personName : String , searchType : SearchType) : Flow<PagingData<UserDto>>{
        Log.e("getAllPersonContaining","search call $id")
        return repository.getAllClientUserContaining(id , searchType, personName)
    }
}