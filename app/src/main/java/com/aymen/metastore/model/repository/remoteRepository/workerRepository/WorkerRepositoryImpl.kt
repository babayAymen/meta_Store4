package com.aymen.store.model.repository.remoteRepository.workerRepository

import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import javax.inject.Inject

class WorkerRepositoryImpl @Inject constructor(
    private val api : ServiceApi
) :WorkerRepository {
    override suspend fun getAllMyWorker(companyId : Long) = api.getAllMyWorker(companyId = companyId)
}