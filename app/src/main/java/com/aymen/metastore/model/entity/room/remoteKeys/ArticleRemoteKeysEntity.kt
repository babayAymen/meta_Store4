package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "article_remote_keys_table")
data class ArticleRemoteKeysEntity(
    @PrimaryKey val id : Long,
    val nextPage : Int?,
    val previousPage : Int?
)
