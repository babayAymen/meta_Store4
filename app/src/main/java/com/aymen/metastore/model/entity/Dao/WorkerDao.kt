package com.aymen.metastore.model.entity.Dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Worker

@Dao
interface WorkerDao {

    @Upsert
    suspend fun insertWorker(worker : Worker)

    @Query("SELECT * FROM worker")
    suspend fun getAllWorkors(): List<Worker>
}