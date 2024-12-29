package com.aymen.metastore.model.entity.paging.remotemediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aymen.metastore.model.entity.model.Worker
import com.aymen.metastore.model.entity.room.AppDatabase
import com.aymen.metastore.model.entity.room.remoteKeys.WorkerRemoteKeys
import com.aymen.metastore.model.entity.roomRelation.WorkerWithUser
import com.aymen.metastore.model.repository.globalRepository.ServiceApi

@OptIn(ExperimentalPagingApi::class)
class WorkerRemoteMediator(
    private val api : ServiceApi,
    private val room : AppDatabase,
    private val companyId : Long
): RemoteMediator<Int , WorkerWithUser> (){

    private val workerDao = room.workerDao()
    private val userDao = room.userDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, WorkerWithUser>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    0
                }
                LoadType.PREPEND -> {
                    val previousPage = getPreviousPageForTheFirstItem()
                    val previousePage = previousPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    previousePage
                }
                LoadType.APPEND -> {
                    val nextPage = getNextPageForTheLasttItem()
                    val nextePage = nextPage ?: return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    nextePage
                }
            }
            val response = api.getAllMyWorker(companyId,currentPage, state.config.pageSize)
            val endOfPaginationReached = response.last
            val prevPage = if (response.first) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1

            room.withTransaction {
                try {
                    if(loadType == LoadType.REFRESH){
                        deleteCache()
                    }
                    workerDao.insertWorkerKeys(response.content.map { article ->
                        WorkerRemoteKeys(
                            id = article.id!!,
                            nextPage = nextPage,
                            prevPage = prevPage
                        )
                    })

                    response.content.map { article -> Log.e("logartilce","article company : ${article.company.id} and my company is : $companyId") }
                    userDao.insertUser(response.content.map {user -> user.user.toUser()})
                    workerDao.insertWorker(response.content.map { worker -> worker.toWorker() })


                } catch (ex: Exception) {
                    Log.e("error", "articlecompany ${ex}")
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (ex: Exception) {
            Log.e("error", "articlecompany ${ex.message}")
            MediatorResult.Error(ex)
        }
    }

    private suspend fun getPreviousPageForTheFirstItem(): Int? {
        return workerDao.getFirstWorkerRemoteKey()?.prevPage
    }

    private suspend fun getNextPageForTheLasttItem(): Int? {
        return workerDao.getLatestWorkerRemoteKey()?.nextPage
    }


    private suspend fun deleteCache(){
        workerDao.clearAllWorkersRemoteKeysTable()
        workerDao.clearAllWorkersTable()
    }
}