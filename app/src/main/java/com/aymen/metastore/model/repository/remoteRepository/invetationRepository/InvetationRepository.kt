package com.aymen.store.model.repository.remoteRepository.invetationRepository

import androidx.paging.PagingData
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.entity.model.Invitation
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface InvetationRepository {

     fun getAllMyInvetations(companyId : Long):Flow<PagingData<Invitation>>
    suspend fun RequestResponse(status : Status,id:Long):Response<Void>
    suspend fun cancelInvitation(id:Long):Response<Void>
}