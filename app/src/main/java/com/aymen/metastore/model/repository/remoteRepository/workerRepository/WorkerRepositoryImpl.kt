package com.aymen.store.model.repository.remoteRepository.workerRepository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.aymen.metastore.model.entity.model.Worker
import com.aymen.metastore.model.entity.paging.remotemediator.ArticleRemoteMediator
import com.aymen.metastore.model.entity.paging.remotemediator.WorkerRemoteMediator
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.repository.globalRepository.ServiceApi
import com.aymen.metastore.util.PAGE_SIZE
import com.aymen.metastore.util.PRE_FETCH_DISTANCE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WorkerRepositoryImpl @Inject constructor(
    private val api : ServiceApi,
    private val room : AppDatabase
) :WorkerRepository {

    private val workerDao = room.workerDao()


    @OptIn(ExperimentalPagingApi::class)
    override fun getAllMyWorker(companyId: Long): Flow<PagingData<Worker>> {
        return Pager(
            config = PagingConfig(pageSize= PAGE_SIZE, prefetchDistance = PRE_FETCH_DISTANCE),
            remoteMediator = WorkerRemoteMediator(
                api = api, room = room,  companyId = companyId
            ),
            pagingSourceFactory = { workerDao.getAllWorkors()}
        ).flow.map {
            it.map { article ->
                article.toWorkerWithUser()
            }
        }
    }
}