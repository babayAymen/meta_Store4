package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "sub_article_remote_key")
data class SubArticleRemoteKeys(
    @PrimaryKey val id : Long?,
    val prevPage : Int?,
    val nextPage : Int?
)
