package com.aymen.store.model.repository.remoteRepository.workerRepository

import androidx.paging.PagingData
import com.aymen.metastore.model.entity.dto.WorkerDto
import com.aymen.metastore.model.entity.model.Worker
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WorkerRepository {

     fun getAllMyWorker(companyId : Long): Flow<PagingData<Worker>>
}