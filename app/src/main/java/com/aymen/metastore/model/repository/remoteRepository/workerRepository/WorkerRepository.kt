package com.aymen.store.model.repository.remoteRepository.workerRepository

import com.aymen.store.model.entity.dto.WorkerDto
import retrofit2.Response

interface WorkerRepository {

    suspend fun getAllMyWorker(companyId : Long): Response<List<WorkerDto>>
}