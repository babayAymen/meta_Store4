package com.aymen.metastore.model.repository.remoteRepository.aymenRepository

import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import javax.inject.Inject

class AymenRepositoryImpl @Inject constructor(
    private val api : ServiceApi
): AymenRepository {
    override suspend fun makeAsPointSeller(status: Boolean, id: Long) = api.makeAsPointSeller(id,status)
    override suspend fun makeAsMetaSeller(status: Boolean, id: Long) = api.makeAsMetaSeller(id, status)


}