package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "worker_remote_key")
data class WorkerRemoteKeys(
    @PrimaryKey val id : Long?,
    val prevPage : Int?,
    val nextPage : Int?
)
