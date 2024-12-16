package com.aymen.metastore.model.entity.room.remoteKeys

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "comment_article_remote_keys")
data class CommentArticleRemoteKeys(
    @PrimaryKey val id : Long?,
    val prevPage : Int?,
    val nextPage : Int?,
    val articleId : Long?
)
