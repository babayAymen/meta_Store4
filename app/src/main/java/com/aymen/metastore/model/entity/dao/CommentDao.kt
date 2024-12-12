package com.aymen.metastore.model.entity.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aymen.metastore.model.entity.room.entity.Comment

@Dao
interface CommentDao {

    @Upsert
    suspend fun insertComment(comment : Comment)

    @Query("SELECT * FROM comment WHERE articleId = :articleId")
    suspend fun getAllCommentByArticleId(articleId : Long):List<Comment>
}