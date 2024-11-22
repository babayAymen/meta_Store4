package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_containing_remote_keys")
data class ArticleContainingRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id : Long,
    val previousPage : Int?,
    val nextPage : Int?
)
