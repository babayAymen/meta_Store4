package com.aymen.metastore.model.usecase

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.model.Worker
import com.aymen.store.model.repository.remoteRepository.workerRepository.WorkerRepository
import kotlinx.coroutines.flow.Flow

class GetAllWorkers(private val repository: WorkerRepository) {
    operator fun invoke(companyId : Long) : Flow<PagingData<Worker>> = repository.getAllMyWorker(companyId)
}