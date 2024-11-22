package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_remote_keys_table")
data class CategoryRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val prevPage: Int?,
    val nextPage: Int?,
    val lastUpdated: Long?
)
