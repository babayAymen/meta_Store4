package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "art_remote_keys_entity")
data class ArtRemoteKeysEntity(
    @PrimaryKey val id : Long? = null,
    val prevPage : Int?,
    val nextPage : Int?
)
