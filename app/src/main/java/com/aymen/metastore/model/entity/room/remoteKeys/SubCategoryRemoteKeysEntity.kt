package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sub_category_remote_keys_table")
data class SubCategoryRemoteKeysEntity (
    @PrimaryKey
    val id : Long,
    val nextPage : Int?,
    val previousPage : Int?
)