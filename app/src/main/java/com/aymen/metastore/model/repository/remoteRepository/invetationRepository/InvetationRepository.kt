package com.aymen.store.model.repository.remoteRepository.invetationRepository

import com.aymen.store.model.Enum.Status
import com.aymen.store.model.entity.dto.InvitationDto
import com.aymen.store.model.entity.realm.Invetation
import retrofit2.Response

interface InvetationRepository {

    suspend fun getAllMyInvetations():Response<List<InvitationDto>>
    suspend fun getAllMyInvetationss():Response<List<Invetation>>

    suspend fun RequestResponse(status : Status,id:Long):Response<Void>

    suspend fun cancelInvitation(id:Long):Response<Void>
}