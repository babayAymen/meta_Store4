package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "rating_remote_keys")
data class RatingRemoteKeys(
    @PrimaryKey val id : Long?,
    val prevPage : Int?,
    val nextPage : Int?
)
