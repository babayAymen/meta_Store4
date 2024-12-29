package com.aymen.metastore.model.entity.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Worker
import com.aymen.metastore.model.entity.room.remoteKeys.WorkerRemoteKeys
import com.aymen.metastore.model.entity.roomRelation.WorkerWithUser

@Dao
interface WorkerDao {

    @Upsert
    suspend fun insertWorker(worker : List<Worker>)

    @Upsert
    suspend fun insertWorkerKeys(keys : List<WorkerRemoteKeys>)

    @Transaction
    @Query("SELECT * FROM worker")
     fun getAllWorkors(): PagingSource<Int,WorkerWithUser>

    @Query("DELETE FROM worker")
    suspend fun clearAllWorkersTable()

    @Query("DELETE FROM worker_remote_key")
    suspend fun clearAllWorkersRemoteKeysTable()

    @Query("SELECT * FROM worker_remote_key ORDER BY id ASC LIMIT 1")
    suspend fun getFirstWorkerRemoteKey() : WorkerRemoteKeys?
    @Query("SELECT * FROM worker_remote_key ORDER BY id DESC LIMIT 1")
    suspend fun getLatestWorkerRemoteKey() : WorkerRemoteKeys?
}