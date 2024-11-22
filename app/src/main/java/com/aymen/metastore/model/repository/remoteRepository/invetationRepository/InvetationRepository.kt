package com.aymen.store.model.repository.remoteRepository.invetationRepository

import androidx.paging.PagingData
import com.aymen.store.model.Enum.Status
import com.aymen.metastore.model.entity.dto.InvitationDto
import com.aymen.metastore.model.entity.roomRelation.InvitationWithClientOrWorkerOrCompany
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface InvetationRepository {

     fun getAllMyInvetations():Flow<PagingData<InvitationWithClientOrWorkerOrCompany>>
    suspend fun RequestResponse(status : Status,id:Long):Response<Void>
    suspend fun cancelInvitation(id:Long):Response<Void>
}