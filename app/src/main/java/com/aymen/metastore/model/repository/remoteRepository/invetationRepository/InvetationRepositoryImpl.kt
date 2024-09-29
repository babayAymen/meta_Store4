package com.aymen.store.model.repository.remoteRepository.invetationRepository

import com.aymen.store.model.Enum.Status
import com.aymen.store.model.repository.globalRepository.ServiceApi
import retrofit2.Response
import javax.inject.Inject

class InvetationRepositoryImpl @Inject constructor(
    private val api: ServiceApi
)
    : InvetationRepository {
    override suspend fun getAllMyInvetations() = api.getAllMyInvetations()
    override suspend fun RequestResponse(status : Status ,id: Long) = api.RequestResponse(status,id)
    override suspend fun cancelInvitation(id: Long) = api.cancelInvitation(id)
}